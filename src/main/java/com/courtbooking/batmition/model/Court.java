package com.courtbooking.batmition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courts")
public class Court {
    @Id
    private String id;
    private String name;
    private String location;
    private boolean isAvailable;
    private double pricePerHour;
}
