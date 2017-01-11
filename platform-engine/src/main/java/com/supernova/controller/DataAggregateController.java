package com.supernova.controller;

import com.supernova.beans.HealthData;
import com.supernova.core.HealthDataAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Created by asakhare on 1/10/17.
 */
@RestController
public class DataAggregateController {

    @Autowired
    private HealthDataAggregator healthDataAggregator;

    private static final Logger log = LoggerFactory.getLogger(DataAggregateController.class);

    @RequestMapping(value= "/healthInfo/users/{userId}", method = RequestMethod.GET)
    public Map<String, HealthData> healthData(@PathVariable("userId") String userId, @RequestParam(value="accessToken") String accessToken
    , @RequestParam(value="weekDuration") int weekDuration) throws IOException, GeneralSecurityException {
        log.info("Received Request for userId:" + userId + " " + weekDuration + " " + accessToken);
        return healthDataAggregator.getHealthData(userId,accessToken,weekDuration);
    }

    /*@RequestMapping(value= "/healthInfo/user/{userId}", method = RequestMethod.GET)
    public Map<String, HealthData> healthData(@PathVariable("userId") String userId) throws IOException, GeneralSecurityException {
        String accessToken = "token";
        int weekDuration = 5;
        log.info("Received Request for userId:" + userId + " " + weekDuration + " " + accessToken);
        return healthDataAggregator.getHealthData(userId,accessToken,weekDuration);
    }*/

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String greet() throws IOException, GeneralSecurityException {
        return "Hello";
    }
}
