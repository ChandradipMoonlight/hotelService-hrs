package com.moonlight.hotelService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRatingDTO {
    private Integer ratingId;
    private float rating;
    private String feedback;
    private UserDTO user;
}
