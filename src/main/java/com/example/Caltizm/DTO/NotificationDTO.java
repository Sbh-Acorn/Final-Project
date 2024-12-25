package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class NotificationDTO {

    private String notificationId;
    private String email;
    private String productId;
    private String productName;
    private double previousPrice;
    private double currentPrice;
    private String createdAt;
    private boolean isRead;

}
