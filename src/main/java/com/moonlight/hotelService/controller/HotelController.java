package com.moonlight.hotelService.controller;

import com.moonlight.commonutility.mappers.AppResponse;
import com.moonlight.hotelService.dto.HotelRequest;
import com.moonlight.hotelService.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping(value = "/save")
    public ResponseEntity<AppResponse> saveHotelDetails(@RequestBody HotelRequest hotelRequest) {
        return ResponseEntity.ok()
                .body(new AppResponse(hotelService.saveHotelDetails(hotelRequest)));
    }

    @GetMapping(value = "/get/{userId}")
    public ResponseEntity<AppResponse> getHotelDetail(Integer hotelId) {
        return ResponseEntity.ok()
                .body(new AppResponse(hotelService.getHotelInfo(hotelId)));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<AppResponse> getAllHotelDetails() {
        return ResponseEntity.ok()
                .body(new AppResponse(hotelService.getAllHotelsDetails()));
    }

    @DeleteMapping(value = "/delete/{hotelId}")
    public ResponseEntity<AppResponse> deleteHotelDetails(Integer hotelId) {
        return ResponseEntity.ok()
                .body(new AppResponse(hotelService.deleteHotelInfo(hotelId)));
    }

    @PutMapping(value = "/update/{hotelId}")
    public ResponseEntity<AppResponse> updateHotelDetails(@PathVariable Integer hotelId, @RequestBody HotelRequest hotelRequest) {
        return ResponseEntity.ok()
                .body(new AppResponse(hotelService.updateHotelDetails(hotelId, hotelRequest)));
    }
}
