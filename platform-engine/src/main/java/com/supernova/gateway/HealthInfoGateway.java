package com.supernova.gateway;

import com.supernova.beans.HealthData;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by asakhare on 1/10/17.
 */
public interface HealthInfoGateway {

    HealthData getHealthData(String userId, String bearerToken, int weekDuration) throws IOException, GeneralSecurityException;
}
