package com.onlinePharmacy.order.dto;


import java.time.LocalDateTime;

public record DeleteResponse(
    String message,
    String status,
    String timestamp
) {
   
    public DeleteResponse(String message) {
        this(message, "SUCCESS", LocalDateTime.now().toString());
    }
}