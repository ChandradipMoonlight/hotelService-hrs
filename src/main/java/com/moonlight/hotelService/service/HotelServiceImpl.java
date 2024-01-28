package com.moonlight.hotelService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonlight.commonutility.config.HttpConfig;
import com.moonlight.commonutility.constants.AppConstants;
import com.moonlight.commonutility.exception.ResourceNotFoundException;
import com.moonlight.commonutility.service.ReactiveWebClientService;
import com.moonlight.commonutility.utils.JsonUtils;
import com.moonlight.hotelService.builder.HotelBuildFactory;
import com.moonlight.hotelService.dto.*;
import com.moonlight.hotelService.entity.Hotel;
import com.moonlight.hotelService.external.mappers.UserRatingResponse;
import com.moonlight.hotelService.repository.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotelServiceImpl implements HotelService{

    @Autowired
    private HotelBuildFactory buildFactory;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HttpConfig httpConfig;

    @Autowired
    private ReactiveWebClientService reactiveWebClientService;

    @Value("${url.userService.getUser}")
    private String getUserUrl;

    @Value("${url.ratingService.getRatingByHotel}")
    private String getRatingByHotel;

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
        HotelResponse response = buildFactory.buildHotelResponse(hotel);
        Flux<UserRatingDTO> flux = getAllRatingsOfHotel(hotelId);
        List<UserRatingDTO> allRatingsOfHotel = flux.collectList().block();
        response.setRatings(allRatingsOfHotel);
        response.setOverAllRating(getOverAllRating(allRatingsOfHotel));
        log.info("allRatingsOfHotel : {}", JsonUtils.javaToJson(allRatingsOfHotel));
        return response;
    }
    private OverAllRating getOverAllRating(List<UserRatingDTO> userRatings) {
        OverAllRating overAllRating = new OverAllRating();
        DoubleSummaryStatistics statistics = userRatings.stream()
                .mapToDouble(UserRatingDTO::getRating)
                .summaryStatistics();
        DecimalFormat df = new DecimalFormat("#.#");
        String avg = df.format(statistics.getAverage());
        overAllRating.setAvgRating(Double.parseDouble(avg));
        overAllRating.setTotalRating(statistics.getCount());
        return overAllRating;
    }

    @Override
    public List<HotelResponse> getAllHotelsDetails() {
        return hotelRepository.findAll()
                .stream()
                .map(hotel -> {
                    HotelResponse hotelResponse = buildFactory.buildHotelResponse(hotel);
                    List<UserRatingDTO> ratings = getAllRatingsOfHotel(hotelResponse.getHotelId())
                            .collectList().block();
                    hotelResponse.setRatings(ratings);
                    hotelResponse.setOverAllRating(getOverAllRating(ratings));
                    return hotelResponse;
                }).collect(Collectors.toList());
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

    private Mono<JsonNode> callRatingServiceByHotelId(Integer hotelId) {
//        String url = "http://localhost:8083/api/v1/ratings/getByHotel/"+hotelId;
//        String url = "http://RATING-SERVICE/api/v1/ratings/getByHotel/"+hotelId;
        return reactiveWebClientService.getMono(getRatingByHotel+hotelId, JsonNode.class, httpConfig.getDefaultHeaders())
                .doOnError(e -> log.info("Error Occurred While Fetching Ratings : {}", e.getMessage()))
                .doOnSuccess(e-> log.info("RatingService Response : {}", e));
    }

    private Mono<JsonNode> callUserServiceByUserId(Integer userId) {
//        String url = "http://localhost:8081/api/v1/user/get/"+userId;
//        String url = "http://USER-SERVICE/api/v1/user/get/"+userId;
        return reactiveWebClientService.getMono(getUserUrl+userId, JsonNode.class, httpConfig.getDefaultHeaders())
                .doOnError(e -> log.error("Error Occurred while fetching User Details : {}", e.getMessage()))
                .doOnSuccess(jsonNode -> log.info("UserServiceResponse : {}", jsonNode));
    }
    private Flux<UserRatingDTO> getAllRatingsOfHotel(Integer hotelId) {
        return callRatingServiceByHotelId(hotelId)
                .filter(jsonNode -> jsonNode.get("status").asText().equals(AppConstants.SUCCESS))
                .flatMapMany(jsonNode -> Flux.fromIterable(jsonNode.get("data")))
                .flatMap(jsonNode -> buildUserRatingDTO(jsonNode));
    }
    private Mono<UserRatingDTO> buildUserRatingDTO(JsonNode jsonNode) {
        UserRatingResponse userRatingResponse = JsonUtils.jsonToJava(jsonNode, UserRatingResponse.class);
        return callUserServiceByUserId(userRatingResponse.getUserId())
                .filter(userJsonNode -> userJsonNode.get("status").asText().equals(AppConstants.SUCCESS))
                .map(userJsonNode -> {
                    UserDTO userDTO = JsonUtils.jsonToJava(userJsonNode.get("data"), UserDTO.class);
                    UserRatingDTO ratingDTO = new UserRatingDTO();
                    ratingDTO.setUser(userDTO);
                    ratingDTO.setRatingId(userRatingResponse.getRatingId());
                    ratingDTO.setFeedback(userRatingResponse.getFeedback());
                    ratingDTO.setRating(userRatingResponse.getRating());
                    return ratingDTO;
                });
    }
}
