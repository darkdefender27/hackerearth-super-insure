package com.supernova.gateway;

import com.supernova.beans.HealthData;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asakhare on 1/10/17.
 */
@Component
public class FitGateway implements HealthInfoGateway {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FitGateway.class);
    private static final String APPLICATION_NAME = "healthdatacollector";
    private static final int TIMEOUT = 5000;
    private static final long A_WEEK_IN_MILLISECONDS =  7 * 24 * 3600 * 1000;

    private Fitness fitnessInstance = null;

    private String accessToken = "ya29.CjDPA6UqqzwNLr-2Z3DQZ1xv8OErd5H19E0N5n_BwY_Yy_Yjp_om-7cyeeEOJ9lxaaM";

    public FitGateway() throws GeneralSecurityException, IOException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Build service account credential.

       /* GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("appconfig.json"))
                .createScoped(Collections.singleton(FitnessScopes.FITNESS_ACTIVITY_READ));*/

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        // Set up Fitness instance.
        fitnessInstance = new Fitness.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME).build();

      /*  restTemplate = new RestTemplate(getClientHttpRequestFactory());*/
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        //Set time out for connection to Google APIs
        clientHttpRequestFactory.setConnectTimeout(TIMEOUT);
        return clientHttpRequestFactory;
    }

    @Override
    public HealthData getHealthData(String userId, String bearerToken, int weekDuration) throws IOException, GeneralSecurityException {
        /*userId = "{\n" +
                "  \"aggregateBy\": [{\n" +
                "    \"dataTypeName\": \"com.google.distance.delta\",\n" +
                "    \"dataSourceId\": \"derived:com.google.distance.delta:com.google.android.gms:merge_distance_delta\"\n" +
                "  }],\n" +
                "  \"bucketByTime\": { \"durationMillis\": 604800000 },\n" +
                "  \"startTimeMillis\": 1480035695396,\n" +
                "  \"endTimeMillis\": 1484039695396 \n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<String>(userId, headers);

        ResponseEntity<String> responseString = restTemplate.exchange(
                "https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate",
                HttpMethod.POST,
                entity,
                String.class);
        LOG.info("Response:" + responseString.getBody());
        return responseString.getBody();*/

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential().setAccessToken(bearerToken);

        // Set up Fitness instance.
        fitnessInstance = new Fitness.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME).build();

        AggregateRequest request = new AggregateRequest();

        //Set the Aggregate By Filter
        List<AggregateBy> aggregateByList =  new ArrayList<>();
        AggregateBy aggregateBy = new AggregateBy();
        aggregateBy.setDataSourceId("derived:com.google.distance.delta:com.google.android.gms:merge_distance_delta");
        aggregateBy.setDataTypeName("com.google.distance.delta");
        aggregateByList.add(aggregateBy);
        request.setAggregateBy(aggregateByList);

        //Set Time duration, here it is Per Week of workout
        BucketByTime bucketByTime = new BucketByTime();
        bucketByTime.setDurationMillis(A_WEEK_IN_MILLISECONDS);
        request.setBucketByTime(bucketByTime);
        long currentTimeMillis = System.currentTimeMillis();

        //Set Start time to Time to number of weeks from Today
        request.setStartTimeMillis(currentTimeMillis-(weekDuration*A_WEEK_IN_MILLISECONDS));

        //Set End time to current time
        request.setEndTimeMillis(currentTimeMillis);

        //Fire request to get Data from Google Apis
        AggregateResponse aggregateResponse = fitnessInstance.users().dataset().aggregate("me", request).execute();
        LOG.info("Aggregated Response:" + aggregateResponse);

        return translateAggregateData(userId,weekDuration, aggregateResponse);
    }

    private HealthData translateAggregateData(String userId, int weekDuration,AggregateResponse aggregateResponse) {
        HealthData healthData = new HealthData();

        healthData.setUserId(userId);
        healthData.setWeekDuration(weekDuration);

        for (AggregateBucket bucket : aggregateResponse.getBucket()) {
            if (!bucket.getDataset().isEmpty() && !bucket.getDataset().get(0).getPoint().isEmpty()
                    && !bucket.getDataset().get(0).getPoint().get(0).getValue().isEmpty()) {

                healthData.getWeeklyWorkoutDataMeters().add(bucket.getDataset().get(0).getPoint().get(0).getValue().get(0).getFpVal());
            }
        }
        LOG.info("Health Data " + healthData);

        return healthData;
    }

}
