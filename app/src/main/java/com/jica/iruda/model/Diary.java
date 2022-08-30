package com.jica.iruda.model;

public class Diary {
    private int emogiIndex;             // 이모지
    private String content;             // 내용
    private String contentImg;          // 사진
    private boolean achievementState;   // 달성 여부
    private String createTime;   // 생성 시간

    public Diary() {
    }

    public Diary(int emogiIndex, String content, String contentImg, boolean achievementState, String createTime) {
        this.emogiIndex = emogiIndex;
        this.content = content;
        this.contentImg = contentImg;
        this.achievementState = achievementState;
        this.createTime = createTime;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
