package com.example.final_bitm.PosoClass;

import java.io.Serializable;

/**
 * Created by Nizam Uddin Shamrat on 2/13/2018.
 */

public class PictureClass implements Serializable {
    private String userId;
    private String eventId;
    private String pictureId;
    private String photoUrl;

    public PictureClass(String userId, String eventId, String photoUrl) {
        this.userId = userId;
        this.eventId = eventId;
        this.photoUrl = photoUrl;
    }

    public PictureClass(String userId, String eventId, String pictureId, String photoUrl) {
        this.userId = userId;
        this.eventId = eventId;
        this.pictureId = pictureId;
        this.photoUrl = photoUrl;
    }

    public PictureClass() {
    }

    public PictureClass(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }
}
