package com.trainme.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentEx {
    @SerializedName("exercise")
    @Expose
    private Exercise exercise;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("repetitions")
    @Expose
    private Integer repetitions;
    @SerializedName("metadata")
    @Expose
    private Object metadata;

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}
