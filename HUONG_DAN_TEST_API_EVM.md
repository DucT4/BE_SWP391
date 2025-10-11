# HƯỚNG DẪN TEST API EVM DUYỆT CLAIM

## Bước 1: Import data test vào database

Chạy file SQL đã tạo:
```bash
mysql -u root -p ev_warranty01 < /Users/letu/Downloads/SWP391_02/test_data_claim_workflow.sql
```

Hoặc copy nội dung file `test_data_claim_workflow.sql` và chạy trong MySQL Workbench.

---

## Bước 2: Restart server Spring Boot

```bash
cd /Users/letu/Downloads/SWP391_02
./mvnw spring-boot:run
```

---

## Bước 3: Test API theo thứ tự workflow

### 3.1. Login để lấy token

**Login SC_TECHNICIAN:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "tech01",
  "password": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "username": "tech01",
  "role": "ROLE_SC_TECHNICIAN"
}
```

Lưu token này để dùng cho bước tiếp theo.

---

**Login SC_MANAGER:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "manager01",
  "password": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 2,
  "username": "manager01",
  "role": "ROLE_SC_MANAGER"
}
```

---

**Login EVM_STAFF:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "evm01",
  "password": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 3,
  "username": "evm01",
  "role": "ROLE_EVM_STAFF"
}
```

Lưu token EVM này để test API duyệt!

---

### 3.2. SC_MANAGER gửi claim lên hãng (chuyển từ DRAFT → SENT_TO_EVM)

```http
PUT http://localhost:8080/api/claims/1/submit?remark=Cần hỗ trợ kiểm tra pin
Authorization: Bearer {TOKEN_CỦA_MANAGER}
```

**Response thành công:**
```json
{
  "message": "Đã gửi claim lên hãng!"
}
```

---

### 3.3. EVM_STAFF duyệt claim ✅ (Đây là API bạn cần test)

**Trường hợp APPROVE:**
```http
PUT http://localhost:8080/api/claims/approve
Authorization: Bearer {TOKEN_CỦA_EVM}
Content-Type: application/json

{
  "claimId": 1,
  "decision": "APPROVED",
  "remark": "Hãng xác nhận lỗi pin trong bảo hành, sẽ gửi phụ tùng"
}
```

**Response thành công:**
```json
{
  "message": "Hãng đã cập nhật quyết định!"
}
```

---

**Trường hợp REJECT:**
```http
PUT http://localhost:8080/api/claims/approve
Authorization: Bearer {TOKEN_CỦA_EVM}
Content-Type: application/json

{
  "claimId": 1,
  "decision": "REJECTED",
  "remark": "Lỗi do người dùng sử dụng sai, không thuộc bảo hành"
}
```

---

## Bước 4: Kiểm tra kết quả trong database

```sql
-- Xem claim hiện tại
SELECT id, vin, status, approval_level, failure_desc 
FROM claims 
WHERE id = 1;

-- Xem lịch sử duyệt
SELECT ca.*, u.username as approver_name
FROM claim_approvals ca
LEFT JOIN users u ON ca.approver_id = u.id
WHERE ca.claim_id = 1
ORDER BY ca.decision_at DESC;

-- Xem lịch sử thay đổi trạng thái
SELECT csh.*, u.username as changed_by_name
FROM claim_status_history csh
LEFT JOIN users u ON csh.changed_by = u.id
WHERE csh.claim_id = 1
ORDER BY csh.changed_at DESC;
```

---

## Bước 5: Xem log trong console

Khi bạn call API, console sẽ hiển thị:

```
INFO - === APPROVE CLAIM ===
INFO - Nhận request approve claim: claimId=1, decision=APPROVED
INFO - User ID: 3, Role: ROLE_EVM_STAFF
INFO - Approve claim thành công: claimId=1, decision=APPROVED
```

---

## Các trường hợp lỗi thường gặp

### ❌ Lỗi 401 - Unauthorized
```json
{
  "message": "Token không hợp lệ"
}
```
**Nguyên nhân:** Không có token hoặc token đã hết hạn.
**Giải pháp:** Login lại để lấy token mới.

---

### ❌ Lỗi 403 - Forbidden
```json
{
  "message": "Chỉ có EVM Staff mới có quyền duyệt claim"
}
```
**Nguyên nhân:** Đang dùng token của SC_TECH hoặc SC_MANAGER.
**Giải pháp:** Dùng token của EVM_STAFF (evm01).

---

### ❌ Lỗi 400 - Bad Request
```json
{
  "message": "Lỗi: Claim không tồn tại"
}
```
**Nguyên nhân:** ClaimId không đúng.
**Giải pháp:** Kiểm tra lại ID claim trong database.

---

```json
{
  "message": "Lỗi: Chỉ có thể duyệt claim ở trạng thái SENT_TO_EVM"
}
```
**Nguyên nhân:** Claim chưa được SC_MANAGER gửi lên hãng.
**Giải pháp:** Gọi API submit trước (bước 3.2).

---

## Test với Postman

### Import Collection

1. Mở Postman
2. File → Import
3. Paste nội dung bên dưới:

```json
{
  "info": {
    "name": "EV Warranty - Claim Workflow Test",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "tech_token",
      "value": ""
    },
    {
      "key": "manager_token",
      "value": ""
    },
    {
      "key": "evm_token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "1. Login SC_TECHNICIAN",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.collectionVariables.set(\"tech_token\", pm.response.json().token);"
            ]
          }
        }
      ],
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "{{baseUrl}}/api/auth/login",
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"tech01\",\"password\":\"123456\"}"
        }
      }
    },
    {
      "name": "2. Login SC_MANAGER",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.collectionVariables.set(\"manager_token\", pm.response.json().token);"
            ]
          }
        }
      ],
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "{{baseUrl}}/api/auth/login",
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"manager01\",\"password\":\"123456\"}"
        }
      }
    },
    {
      "name": "3. Login EVM_STAFF",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.collectionVariables.set(\"evm_token\", pm.response.json().token);"
            ]
          }
        }
      ],
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "{{baseUrl}}/api/auth/login",
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"evm01\",\"password\":\"123456\"}"
        }
      }
    },
    {
      "name": "4. Manager Submit Claim",
      "request": {
        "method": "PUT",
        "header": [{"key": "Authorization", "value": "Bearer {{manager_token}}"}],
        "url": "{{baseUrl}}/api/claims/1/submit?remark=Cần hỗ trợ"
      }
    },
    {
      "name": "5. EVM Approve Claim",
      "request": {
        "method": "PUT",
        "header": [
          {"key": "Authorization", "value": "Bearer {{evm_token}}"},
          {"key": "Content-Type", "value": "application/json"}
        ],
        "url": "{{baseUrl}}/api/claims/approve",
        "body": {
          "mode": "raw",
          "raw": "{\"claimId\":1,\"decision\":\"APPROVED\",\"remark\":\"OK\"}"
        }
      }
    }
  ]
}
```

### Cách dùng Postman Collection:

1. Run lần lượt các request 1, 2, 3 để lấy token (token sẽ tự động lưu vào variables)
2. Run request 4 để manager submit claim
3. Run request 5 để EVM duyệt claim
4. Kiểm tra Response và database

---

## Test với curl (Terminal)

```bash
# 1. Login EVM
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"evm01","password":"123456"}' | jq -r '.token')

echo "EVM Token: $TOKEN"

# 2. Approve claim
curl -X PUT http://localhost:8080/api/claims/approve \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": 1,
    "decision": "APPROVED",
    "remark": "Hãng xác nhận OK"
  }'
```

---

## Kết quả mong đợi

Sau khi approve thành công:

1. **Claims table:**
   - status: `APPROVED` (hoặc `REJECTED`)
   - approval_level: `EVM`
   - updated_at: timestamp hiện tại

2. **Claim_approvals table:**
   - Có 2 records:
     - Record 1: level=MANAGER, decision=FORWARDED
     - Record 2: level=EVM, decision=APPROVED

3. **Claim_status_history table:**
   - Có 3 records:
     - DRAFT → SENT_TO_EVM → APPROVED

---

Chúc bạn test thành công! 🚀

