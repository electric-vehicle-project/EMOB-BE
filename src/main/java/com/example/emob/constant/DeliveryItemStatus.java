package com.example.emob.constant;

public enum DeliveryItemStatus {
    PENDING, // chờ xử lý
    IN_TRANSIT, // đang vận chyển
    DELIVERED, // đã vận chyển thành công
    RETURNED, // hàng trả về
    CANCELED // hàng bị hủy
}
