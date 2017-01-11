package com.supernova.core;

import com.supernova.beans.HealthData;
import com.supernova.gateway.HealthInfoGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asakhare on 1/10/17.
 */
@Component
public class HealthDataAggregator {

    @Autowired
    HealthInfoGateway healthInfoGateway;

    public Map<String, HealthData> getHealthData(String userId, String accessToken, int weekDuration) throws IOException, GeneralSecurityException {
        Map<String, HealthData> response = new HashMap<>();
        HealthData healthData = healthInfoGateway.getHealthData(userId, accessToken, weekDuration);
        response.put("fit",healthData);

        return response;
    }
}

