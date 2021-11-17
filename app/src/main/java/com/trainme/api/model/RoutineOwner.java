package com.trainme.api.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoutineOwner {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("avatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("date")
    @Expose
    private Long date;
    @SerializedName("lastActivity")
    @Expose
    private Long lastActivity;

    /**
     * No args constructor for use in serialization
     *
     */
    public RoutineOwner() {
    }

    /**
     *
     * @param date
     * @param gender
     * @param avatarUrl
     * @param lastActivity
     * @param id
     * @param username
     */
    public RoutineOwner(Integer id, String username, String gender, String avatarUrl, Long date, Long lastActivity) {
        super();
        this.id = id;
        this.username = username;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.date = date;
        this.lastActivity = lastActivity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Long lastActivity) {
        this.lastActivity = lastActivity;
    }

}