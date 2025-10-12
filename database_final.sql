-- Xóa CSDL nếu tồn tại
DROP DATABASE IF EXISTS ev_warranty01;

-- Tạo mới database và chọn charset UTF8MB4 để hỗ trợ Unicode
CREATE DATABASE ev_warranty01;

-- Sử dụng CSDL vừa tạo
USE ev_warranty01;

/* =========================================================
   1) USERS / SERVICE CENTERS / WAREHOUSES
   ========================================================= */

-- =========================================================
-- USERS: Quản lý tài khoản người dùng hệ thống
-- =========================================================
CREATE TABLE users(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính tự tăng
  username VARCHAR(120) NOT NULL UNIQUE,                 -- Tên đăng nhập duy nhất
  email VARCHAR(150) NOT NULL UNIQUE,                    -- Email duy nhất trong hệ thống
  phone VARCHAR(20),                                     -- Số điện thoại liên hệ
  password VARCHAR(255) NOT NULL,                        -- Mật khẩu (hash BCrypt)
  role VARCHAR(50) NOT NULL,                             -- Vai trò: EVM_ADMIN / SC_MANAGER / SC_STAFF / SC_TECHNICIAN
  is_active TINYINT(1) DEFAULT 1,                        -- 1: hoạt động, 0: bị khóa
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP          -- Ngày tạo tài khoản
);

-- =========================================================
-- SERVICE_CENTERS: Danh sách trung tâm dịch vụ (SC)
-- =========================================================
CREATE TABLE service_centers(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính tự tăng
  code VARCHAR(50) NOT NULL UNIQUE,                      -- Mã trung tâm duy nhất
  name VARCHAR(150) NOT NULL,                            -- Tên trung tâm dịch vụ
  address VARCHAR(200),                                  -- Địa chỉ trung tâm
  region VARCHAR(100),                                   -- Khu vực hoạt động (Bắc / Trung / Nam)
  manager_user_id BIGINT,                                -- ID người quản lý trung tâm
  FOREIGN KEY (manager_user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE  -- Ràng buộc đến bảng users
);

-- =========================================================
-- WAREHOUSES: Kho vật tư (của hãng hoặc trung tâm SC)
-- =========================================================
CREATE TABLE warehouses(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  code VARCHAR(50) NOT NULL UNIQUE,                      -- Mã kho duy nhất
  name VARCHAR(150) NOT NULL,                            -- Tên kho
  type VARCHAR(20) NOT NULL,                             -- Loại kho: 'EVM' hoặc 'SC'
  service_center_id BIGINT,                              -- FK đến trung tâm SC nếu là kho của SC
  address VARCHAR(200),                                  -- Địa chỉ kho
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id) ON DELETE SET NULL ON UPDATE CASCADE
);

/* =========================================================
   2) CUSTOMERS / VEHICLES / PARTS
   ========================================================= */

-- =========================================================
-- CUSTOMERS: Thông tin khách hàng mua xe
-- =========================================================
CREATE TABLE customers(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  full_name VARCHAR(150) NOT NULL,                       -- Tên khách hàng
  phone VARCHAR(20),                                     -- Số điện thoại liên hệ
  email VARCHAR(150),                                    -- Email khách hàng
  address VARCHAR(200),                                  -- Địa chỉ
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP          -- Ngày tạo hồ sơ
);

-- =========================================================
-- VEHICLES: Thông tin xe điện
-- =========================================================
CREATE TABLE vehicles(
  vin VARCHAR(32) PRIMARY KEY,                           -- Mã VIN duy nhất của xe
  model VARCHAR(80) NOT NULL,                            -- Model xe (VD: VF3, VF8)
  customer_id BIGINT,                                    -- FK khách hàng sở hữu xe
  purchase_date DATE,                                    -- Ngày mua xe
  coverage_to DATE,                                      -- Ngày hết hạn bảo hành
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- =========================================================
-- PARTS: Danh mục phụ tùng xe điện
-- =========================================================
CREATE TABLE parts(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  part_no VARCHAR(64) NOT NULL UNIQUE,                   -- Mã phụ tùng duy nhất
  name VARCHAR(150) NOT NULL,                            -- Tên phụ tùng
  track_serial TINYINT(1) DEFAULT 0,                     -- Theo dõi serial (1: Có, 0: Không)
  track_lot TINYINT(1) DEFAULT 0,                        -- Theo dõi lô hàng (1: Có, 0: Không)
  uom VARCHAR(20) DEFAULT 'EA'                           -- Đơn vị tính (EA = Each)
);

-- =========================================================
-- PART_POLICIES: Chính sách bảo hành cho phụ tùng
-- =========================================================
CREATE TABLE part_policies(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  part_id BIGINT NOT NULL,                               -- FK đến phụ tùng
  warranty_months INT,                                   -- Thời gian bảo hành (tháng)
  limit_km INT,                                          -- Giới hạn km bảo hành
  notes VARCHAR(200),                                    -- Ghi chú
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =========================================================
-- PART_SUBSTITUTIONS: Phụ tùng thay thế tương đương
-- =========================================================
CREATE TABLE part_substitutions(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  part_id BIGINT NOT NULL,                               -- Phụ tùng gốc
  substitute_part_id BIGINT NOT NULL,                    -- Phụ tùng thay thế
  UNIQUE(part_id, substitute_part_id),                   -- Không trùng cặp
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (substitute_part_id) REFERENCES parts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

/* =========================================================
   3) CLAIMS - YÊU CẦU BẢO HÀNH
   ========================================================= */

-- =========================================================
-- CLAIMS: Yêu cầu bảo hành do SC mở
-- =========================================================
CREATE TABLE claims(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  vin VARCHAR(32) NOT NULL,                              -- Xe bị lỗi
  opened_by BIGINT NOT NULL,                             -- Người mở yêu cầu (technician)
  service_center_id BIGINT NOT NULL,                     -- Trung tâm dịch vụ thực hiện
  status VARCHAR(40) NOT NULL,                           -- Trạng thái: SUBMITTED / APPROVED / REJECTED / CLOSED
  failure_desc TEXT,                                     -- Mô tả lỗi khách hàng
  approval_level VARCHAR(40),                            -- Cấp duyệt hiện tại: MANAGER / EVM
  resolution_type VARCHAR(30),                           -- Cách xử lý: REPAIR / REPLACE / INSPECT / NFF
  resolution_note VARCHAR(300),                          -- Ghi chú xử lý
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Ngày tạo claim
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Ngày cập nhật
  FOREIGN KEY (vin) REFERENCES vehicles(vin) ON DELETE RESTRICT,
  FOREIGN KEY (opened_by) REFERENCES users(id) ON DELETE RESTRICT,
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id) ON DELETE RESTRICT
);

-- =========================================================
-- CLAIM_STATUS_HISTORY: Lưu lịch sử thay đổi trạng thái của claim
-- =========================================================
CREATE TABLE claim_status_history(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim
  status VARCHAR(40) NOT NULL,                           -- Trạng thái mới
  changed_by BIGINT NOT NULL,                            -- Người thay đổi
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Thời điểm thay đổi
  note VARCHAR(200),                                     -- Ghi chú thêm
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
  FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- CLAIM_APPROVALS: Quá trình duyệt yêu cầu bảo hành
-- =========================================================
CREATE TABLE claim_approvals(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim
  approver_id BIGINT NOT NULL,                           -- Người duyệt
  level VARCHAR(40) NOT NULL,                            -- MANAGER / EVM
  decision VARCHAR(20) NOT NULL,                         -- APPROVED / REJECTED
  decision_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- Thời gian duyệt
  remark VARCHAR(200),                                   -- Ghi chú duyệt
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
  FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- CLAIM_PARTS: Các phụ tùng nằm trong claim
-- =========================================================
CREATE TABLE claim_parts(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim
  part_id BIGINT NOT NULL,                               -- FK đến parts
  qty DECIMAL(12,2) NOT NULL,                            -- Số lượng yêu cầu
  planned TINYINT(1) DEFAULT 1,                          -- 1 = có trong kế hoạch, 0 = phát sinh
  serial_no VARCHAR(100),                                -- Serial phụ tùng nếu có
  lot_no VARCHAR(50),                                    -- Số lô nếu có
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE RESTRICT
);

-- =========================================================
-- CLAIM_LABOUR: Giờ công sửa chữa trong claim
-- =========================================================
CREATE TABLE claim_labour(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim
  technician_id BIGINT,                                  -- Người thực hiện
  hours DECIMAL(6,2) NOT NULL,                           -- Số giờ công
  rate DECIMAL(10,2) NOT NULL,                           -- Đơn giá giờ công
  note VARCHAR(200),                                     -- Ghi chú công việc
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
  FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- CLAIM_ASSIGNMENTS: Giao claim cho kỹ thuật viên
-- =========================================================
CREATE TABLE claim_assignments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim
  technician_id BIGINT NOT NULL,                         -- Người được giao
  assigned_by BIGINT NOT NULL,                           -- Người giao việc
  status VARCHAR(40) NOT NULL,                           -- ASSIGNED / IN_PROGRESS / COMPLETED / CANCELLED
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- Ngày giao
  accepted_at TIMESTAMP NULL,                            -- Ngày nhận
  started_at TIMESTAMP NULL,                             -- Ngày bắt đầu
  completed_at TIMESTAMP NULL,                           -- Ngày hoàn thành
  note VARCHAR(200),                                     -- Ghi chú
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
  FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE SET NULL,
  FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- CAMPAIGNS: Chiến dịch Recall hoặc Service
-- =========================================================
CREATE TABLE campaigns(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  type VARCHAR(30) NOT NULL,                             -- Loại chiến dịch (Recall / Service)
  name VARCHAR(150) NOT NULL,                            -- Tên chiến dịch
  description TEXT,                                      -- Mô tả chi tiết
  start_date DATE,                                       -- Ngày bắt đầu
  end_date DATE,                                         -- Ngày kết thúc
  created_by BIGINT,                                     -- Người tạo
  status VARCHAR(30) NOT NULL,                           -- Trạng thái: Planned / Active / Closed
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- CAMPAIGN_VINS: Danh sách VIN thuộc chiến dịch
-- =========================================================
CREATE TABLE campaign_vins(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  campaign_id BIGINT NOT NULL,                           -- FK đến chiến dịch
  vin VARCHAR(32) NOT NULL,                              -- VIN xe thuộc chiến dịch
  status VARCHAR(30) DEFAULT 'Planned',                  -- Trạng thái xử lý xe trong campaign
  UNIQUE(campaign_id, vin),                              -- Mỗi VIN chỉ thuộc chiến dịch 1 lần
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
  FOREIGN KEY (vin) REFERENCES vehicles(vin) ON DELETE CASCADE
);

/* =========================================================
   5) WORK_ORDERS - Lệnh công việc
   ========================================================= */
CREATE TABLE work_orders(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  type VARCHAR(30) NOT NULL,                             -- Loại WO: Claim / Campaign
  campaign_id BIGINT NULL,                               -- FK nếu từ chiến dịch
  vin VARCHAR(32) NOT NULL,                              -- Xe được xử lý
  service_center_id BIGINT NOT NULL,                     -- Trung tâm dịch vụ
  created_by BIGINT NOT NULL,                            -- Người tạo
  assigned_tech_id BIGINT NULL,                          -- Kỹ thuật viên phụ trách
  status VARCHAR(30) NOT NULL,                           -- OPEN / IN_PROGRESS / COMPLETED
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Ngày tạo
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Ngày cập nhật
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE SET NULL,
  FOREIGN KEY (vin) REFERENCES vehicles(vin) ON DELETE RESTRICT,
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id) ON DELETE RESTRICT,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
  FOREIGN KEY (assigned_tech_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- WORK_ORDER_ITEMS: Chi tiết công việc hoặc vật tư trong WO
-- =========================================================
CREATE TABLE work_order_items(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  work_order_id BIGINT NOT NULL,                         -- FK đến WO
  item_type VARCHAR(30) NOT NULL,                        -- Loại item: PART / LABOUR
  description VARCHAR(200),                              -- Mô tả công việc
  part_id BIGINT NULL,                                   -- Nếu là phụ tùng
  qty DECIMAL(10,2) DEFAULT 1,                           -- Số lượng
  unit_price DECIMAL(12,2) DEFAULT 0,                    -- Đơn giá
  is_approved TINYINT(1) DEFAULT 0,                      -- 0 = chưa duyệt, 1 = duyệt
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE,
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE SET NULL
);

-- =========================================================
-- CUSTOMER_PAYMENTS: Ghi nhận thanh toán của khách hàng
-- =========================================================
CREATE TABLE customer_payments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  work_order_id BIGINT NOT NULL,                         -- FK đến WO
  method VARCHAR(30) NOT NULL,                           -- Hình thức thanh toán: CASH / CARD
  status VARCHAR(30) NOT NULL,                           -- PENDING / PAID
  amount DECIMAL(12,2) NOT NULL,                         -- Số tiền
  paid_at TIMESTAMP NULL,                                -- Ngày thanh toán
  note VARCHAR(200),                                     -- Ghi chú
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE
);

/* =========================================================
   6) INVENTORY - Kho, xuất nhập vật tư
   ========================================================= */

-- =========================================================
-- STOCK: Tồn kho hiện tại của từng phụ tùng
-- =========================================================
CREATE TABLE stock(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  warehouse_id BIGINT NOT NULL,                          -- FK đến kho
  part_id BIGINT NOT NULL,                               -- FK đến phụ tùng
  qty_on_hand DECIMAL(12,2) DEFAULT 0,                   -- Số lượng hiện có
  qty_reserved DECIMAL(12,2) DEFAULT 0,                  -- Số lượng đã giữ
  UNIQUE(warehouse_id, part_id),                         -- Mỗi kho - phụ tùng chỉ có 1 bản ghi
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
);

-- =========================================================
-- SHIPMENTS: Phiếu xuất hàng (delivery order)
-- =========================================================
CREATE TABLE shipments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  do_no VARCHAR(50) NOT NULL UNIQUE,                     -- Mã phiếu giao hàng duy nhất
  source_wh_id BIGINT NOT NULL,                          -- Kho nguồn
  dest_wh_id BIGINT NOT NULL,                            -- Kho đích
  carrier VARCHAR(100),                                  -- Đơn vị vận chuyển
  tracking_no VARCHAR(100),                              -- Mã theo dõi vận chuyển
  status VARCHAR(40) NOT NULL,                           -- CREATED / IN_TRANSIT / RECEIVED
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Ngày tạo phiếu
  FOREIGN KEY (source_wh_id) REFERENCES warehouses(id) ON DELETE RESTRICT,
  FOREIGN KEY (dest_wh_id) REFERENCES warehouses(id) ON DELETE RESTRICT
);

-- =========================================================
-- SHIPMENT_LINES: Chi tiết phiếu giao hàng
-- =========================================================
CREATE TABLE shipment_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  shipment_id BIGINT NOT NULL,                           -- FK đến shipment
  part_id BIGINT NOT NULL,                               -- FK đến phụ tùng
  qty DECIMAL(12,2) NOT NULL,                            -- Số lượng giao
    lot_no VARCHAR(50),                                   -- Mã lô hàng (nếu có)
  FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,  -- Khi phiếu xuất bị xóa → chi tiết xóa theo
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE RESTRICT          -- Không cho xóa phụ tùng nếu đang được tham chiếu
);

-- =========================================================
-- GRN (Goods Receipt Note): Phiếu nhập hàng tại kho nhận
-- =========================================================
CREATE TABLE grn(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  shipment_id BIGINT NOT NULL,                           -- FK đến phiếu giao hàng gốc
  warehouse_id BIGINT NOT NULL,                          -- Kho nhận hàng
  received_by BIGINT NOT NULL,                           -- Người thực hiện nhận hàng
  status VARCHAR(30) NOT NULL,                           -- Trạng thái: NEW / CONFIRMED / CANCELLED
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Ngày tạo phiếu nhập
  FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,  -- Xóa shipment sẽ xóa GRN
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT, -- Không cho xóa kho nếu có GRN
  FOREIGN KEY (received_by) REFERENCES users(id) ON DELETE SET NULL       -- Khi user bị xóa → gán NULL
);

-- =========================================================
-- GRN_LINES: Chi tiết từng phụ tùng trong phiếu nhập hàng
-- =========================================================
CREATE TABLE grn_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  grn_id BIGINT NOT NULL,                                -- FK đến phiếu GRN
  part_id BIGINT NOT NULL,                               -- FK đến phụ tùng
  qty_ok DECIMAL(12,2) DEFAULT 0,                        -- Số lượng đạt yêu cầu
  qty_damaged DECIMAL(12,2) DEFAULT 0,                   -- Số lượng hỏng / lỗi
  FOREIGN KEY (grn_id) REFERENCES grn(id) ON DELETE CASCADE, -- Xóa phiếu GRN sẽ xóa chi tiết
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE RESTRICT
);

-- =========================================================
-- ISSUES: Phiếu xuất kho cho yêu cầu bảo hành (claim)
-- =========================================================
CREATE TABLE issues(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL,                              -- FK đến claim cần xử lý
  warehouse_id BIGINT NOT NULL,                          -- Kho xuất phụ tùng
  requested_by BIGINT NOT NULL,                          -- Người yêu cầu (thường là SC)
  issued_by BIGINT,                                      -- Người xuất kho (EVM Staff)
  status VARCHAR(30) NOT NULL,                           -- CREATED / APPROVED / COMPLETED
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Ngày tạo phiếu
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,         -- Xóa claim → xóa issue
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT, -- Không cho xóa kho
  FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE SET NULL,      -- Nếu user bị xóa → NULL
  FOREIGN KEY (issued_by) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
-- ISSUE_LINES: Chi tiết vật tư xuất cho phiếu issue
-- =========================================================
CREATE TABLE issue_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  issue_id BIGINT NOT NULL,                              -- FK đến phiếu issue
  part_id BIGINT NOT NULL,                               -- Phụ tùng xuất kho
  qty DECIMAL(12,2) NOT NULL,                            -- Số lượng
  serial_no VARCHAR(100),                                -- Số serial nếu có
  lot_no VARCHAR(50),                                    -- Mã lô nếu có
  FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE CASCADE,  -- Xóa issue → xóa dòng con
  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE RESTRICT
);

-- =========================================================
-- SETTLEMENTS: Quyết toán chi phí bảo hành (tổng hợp parts + labour)
-- =========================================================
CREATE TABLE settlements(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  claim_id BIGINT NOT NULL UNIQUE,                       -- FK đến claim, 1 claim = 1 settlement
  status VARCHAR(30) NOT NULL,                           -- SUBMITTED / APPROVED / PAID
  submitted_by BIGINT,                                   -- Người gửi yêu cầu thanh toán
  submitted_at TIMESTAMP NULL,                           -- Thời gian gửi
  approved_by BIGINT,                                    -- Người duyệt thanh toán
  approved_at TIMESTAMP NULL,                            -- Thời gian duyệt
  total_parts DECIMAL(12,2) DEFAULT 0,                   -- Tổng chi phí phụ tùng
  total_labour DECIMAL(12,2) DEFAULT 0,                  -- Tổng chi phí nhân công
  total_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_parts + total_labour) STORED, -- Tổng cộng
  FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE  -- Khi claim bị xóa → settlement xóa theo
);

-- =========================================================
-- AUDIT_LOGS: Nhật ký hệ thống (ghi lại hành động người dùng)
-- =========================================================
CREATE TABLE audit_logs(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,                  -- Khóa chính
  actor_id BIGINT,                                       -- Người thực hiện hành động
  action VARCHAR(60) NOT NULL,                           -- Hành động: CREATE / UPDATE / DELETE
  entity VARCHAR(60) NOT NULL,                           -- Tên bảng / thực thể
  entity_id BIGINT,                                      -- ID của bản ghi bị ảnh hưởng
  before_json JSON,                                      -- Dữ liệu trước khi thay đổi
  after_json JSON,                                       -- Dữ liệu sau khi thay đổi
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Thời điểm log
  FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL  -- Khi user bị xóa → NULL
);

