package com.courtbooking.batmition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String courtId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private BookingStatus status;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
}
