package com.moonlight.hotelService.service;

import com.moonlight.hotelService.dto.HotelRequest;
import com.moonlight.hotelService.dto.HotelResponse;

import java.util.List;

public interface HotelService {
    HotelResponse saveHotelDetails(HotelRequest hotelRequest);

    HotelResponse getHotelInfo(Integer hotelId);

    List<HotelResponse> getAllHotelsDetails();

    String deleteHotelInfo(Integer hotelId);

    HotelResponse updateHotelDetails(Integer hotelId, HotelRequest hotelRequest);

}
