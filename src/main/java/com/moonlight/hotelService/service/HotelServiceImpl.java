package com.moonlight.hotelService.service;

import com.moonlight.commonutility.exception.ResourceNotFoundException;
import com.moonlight.hotelService.builder.HotelBuildFactory;
import com.moonlight.hotelService.dto.HotelRequest;
import com.moonlight.hotelService.dto.HotelResponse;
import com.moonlight.hotelService.entity.Hotel;
import com.moonlight.hotelService.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService{

    @Autowired
    private HotelBuildFactory buildFactory;

    @Autowired
    private HotelRepository hotelRepository;
    @Override
    public HotelResponse saveHotelDetails(HotelRequest hotelRequest) {
        Hotel hotel = buildFactory.buildHotelEntity(hotelRequest);
        hotel =  hotelRepository.save(hotel);
        return buildFactory.buildHotelResponse(hotel);
    }

    @Override
    public HotelResponse getHotelInfo(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "HotelId", String.valueOf(hotelId)));
        return buildFactory.buildHotelResponse(hotel);
    }

    @Override
    public List<HotelResponse> getAllHotelsDetails() {
        return hotelRepository.findAll()
                .stream()
                .map(hotel -> buildFactory.buildHotelResponse(hotel))
                .collect(Collectors.toList());
    }

    @Override
    public String deleteHotelInfo(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "HotelId", String.valueOf(hotelId)));
        hotelRepository.delete(hotel);
        return "Hotel Details Deleted for given hotelId : "+hotelId;
    }

    @Override
    public HotelResponse updateHotelDetails(Integer hotelId, HotelRequest hotelRequest) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "HotelId", String.valueOf(hotelId)));
        buildFactory.updateHotelDetails(hotel, hotelRequest);
        hotel = hotelRepository.save(hotel);
        return buildFactory.buildHotelResponse(hotel);
    }
}
