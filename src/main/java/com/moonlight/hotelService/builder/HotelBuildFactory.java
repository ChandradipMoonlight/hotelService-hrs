package com.moonlight.hotelService.builder;

import com.moonlight.hotelService.dto.HotelRequest;
import com.moonlight.hotelService.dto.HotelResponse;
import com.moonlight.hotelService.entity.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelBuildFactory {

    public Hotel buildHotelEntity(HotelRequest request) {
        return Hotel.builder()
                .hotelName(request.getHotelName())
                .location(request.getLocation())
                .about(request.getAbout())
                .build();
    }

    public HotelResponse buildHotelResponse(Hotel hotel) {
        return HotelResponse.builder()
                .hotelId(hotel.getHotelId())
                .hotelName(hotel.getHotelName())
                .about(hotel.getAbout())
                .location(hotel.getLocation())
                .build();
    }

    public void updateHotelDetails(Hotel hotel, HotelRequest request) {
        if (request.getHotelName()!=null) {
            hotel.setHotelName(request.getHotelName());
        }
        if (request.getAbout()!=null) {
            hotel.setAbout(request.getAbout());
        }
        if (request.getLocation()!=null) {
            hotel.setLocation(request.getLocation());
        }
    }
}
