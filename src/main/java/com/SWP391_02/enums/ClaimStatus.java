package com.SWP391_02.enums;

public enum ClaimStatus {
    DRAFT,              // Mới tạo bởi SC_TECH
    SENT_TO_EVM, SUBMITTED,// SC_MANAGER đã gửi lên hãng
    APPROVED,           // Hãng đã duyệt
    REJECTED,           // Bị từ chối
    ASSIGNED,           // Đã phân công kỹ thuật viên
    DONE,               // Hoàn thành sửa chữa
    CLOSED              // Đã quyết toán & đóng
}
