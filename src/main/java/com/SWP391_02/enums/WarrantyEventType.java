package com.SWP391_02.enums;

public enum WarrantyEventType {
    ACTIVATION,            // kích hoạt bảo hành
    CLAIM_OPENED,          // mở yêu cầu bảo hành
    CLAIM_APPROVED,        // duyệt
    CLAIM_REJECTED,        // từ chối
    REPAIR_COVERED,        // sửa chữa được bảo hành
    REPAIR_DENIED,         // sửa chữa không được bảo hành
    TRANSFERRED,           // chuyển chủ/đơn vị
    EXPIRED                 // hết hạn
}

