package com.moonlight.hotelService.external.mappers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRatingResponse {
    private Integer ratingId;
    private Integer userId;
    private Integer hotelId;
    private float rating;
    private String feedback;
}
