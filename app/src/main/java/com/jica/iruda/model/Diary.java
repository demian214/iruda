package com.jica.iruda.model;

import java.time.LocalDateTime;

public class Diary {
    private int day;
    private int emogiIndex;             // 이모지
    private String content;             // 내용
    private String imageUrl;            // 사진
    private Boolean achievement;        // 달성 여부
    private LocalDateTime timestamp;    // 생성 시간

    public Diary() {
    }

    public Diary(int day, int emogiIndex, String content, String imageUrl, Boolean achievement, LocalDateTime timestamp) {
        this.day = day;
        this.emogiIndex = emogiIndex;
        this.content = content;
        this.imageUrl = imageUrl;
        this.achievement = achievement;
        this.timestamp = timestamp;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getEmogiIndex() {
        return emogiIndex;
    }

    public void setEmogiIndex(int emogiIndex) {
        this.emogiIndex = emogiIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAchievement() {
        return achievement;
    }

    public void setAchievement(Boolean achievement) {
        this.achievement = achievement;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
