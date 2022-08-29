package com.jica.iruda.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Habit implements Serializable {
    private String title;        // 제목
    private String content;      // 내용
    private String createTime;   // 생성 시간
    private String alramTime;    // 알람 시간

    public Habit() {
    }

    public Habit(String title, String content, String createTime, String alramTime) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.alramTime = alramTime;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAlramTime() {
        return alramTime;
    }

    public void setAlramTime(String alramTime) {
        this.alramTime = alramTime;
    }
    
    // 정렬을 위한 메서드
    public int compareTo(Habit compareHabit) {
        LocalDateTime compareTime = LocalDateTime.parse(((Habit)compareHabit).getCreateTime());
        LocalDateTime thisLocalDateTime = LocalDateTime.parse(this.createTime);

        return thisLocalDateTime.compareTo(compareTime);

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }
}
