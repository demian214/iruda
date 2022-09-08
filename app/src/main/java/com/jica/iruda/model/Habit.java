package com.jica.iruda.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Habit implements Serializable {
    private String id;
    private String userId;
    private String title;        // 제목
    private String content;      // 내용
    private LocalDateTime timestamp;           // 생성 시간
    private LocalTime alarmTime;    // 알람 시간

    public Habit() {}

    public Habit(String id, String title, String content, LocalDateTime timestamp, LocalTime alarmTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.alarmTime = alarmTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }
}
