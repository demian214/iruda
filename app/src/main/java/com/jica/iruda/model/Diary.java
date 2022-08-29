package com.jica.iruda.model;

import java.time.LocalDateTime;

public class Diary {
    private String emogi;               // 이모지
    private String content;             // 내용
    private String contentImg;          // 사진
    private boolean achievementState;   // 달성 여부
    private LocalDateTime createTime;   // 생성 시간

    public Diary() {
    }

    public Diary(String emogi, String content, String contentImg, boolean achievementState, LocalDateTime createTime) {
        this.emogi = emogi;
        this.content = content;
        this.contentImg = contentImg;
        this.achievementState = achievementState;
        this.createTime = createTime;
    }

    public String getEmogi() {
        return emogi;
    }

    public void setEmogi(String emogi) {
        this.emogi = emogi;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public boolean isAchievementState() {
        return achievementState;
    }

    public void setAchievementState(boolean achievementState) {
        this.achievementState = achievementState;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
