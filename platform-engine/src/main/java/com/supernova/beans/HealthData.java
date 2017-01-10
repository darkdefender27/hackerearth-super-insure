package com.supernova.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asakhare on 1/10/17.
 */

public class HealthData {

    private String userId ;
    private int weekDuration;
    private List<Double> weeklyWorkoutDataMeters = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getWeekDuration() {
        return weekDuration;
    }

    public void setWeekDuration(int weekDuration) {
        this.weekDuration = weekDuration;
    }

    public List<Double> getWeeklyWorkoutDataMeters() {
        return weeklyWorkoutDataMeters;
    }

    public void addIntoWeeklyWorkoutDataMeters(Double weeklyWorkoutDataInMeters) {
        this.weeklyWorkoutDataMeters.add(weeklyWorkoutDataInMeters);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HealthData{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", weekDuration='").append(weekDuration).append('\'');
        sb.append(", weeklyWorkoutDataMeters=").append(weeklyWorkoutDataMeters);
        sb.append('}');
        return sb.toString();
    }
}
