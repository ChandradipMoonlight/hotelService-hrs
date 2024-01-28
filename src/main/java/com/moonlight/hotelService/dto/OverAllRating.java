package com.moonlight.hotelService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverAllRating {
    private long totalRating;
    private double avgRating;
}
