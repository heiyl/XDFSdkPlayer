package com.gensee.vod.model;


import com.gensee.player.model.PlResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.gensee.player.model
 * 2017/6/26.
 * wuxu@xdf.cn
 */

public class PlLive extends PlResponse implements Serializable {

    private PlLive result;
    private String liveTime;//直播时间
    private int lessonCount;//课时数量
    private ArrayList<Lesson> lessonList = new ArrayList<>();

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public PlLive getResult() {
        return result;
    }

    public void setResult(PlLive result) {
        this.result = result;
    }

    public ArrayList<Lesson> getLessonList() {
        return lessonList;
    }

    public void setLessonList(ArrayList<Lesson> lessonList) {
        this.lessonList = lessonList;
    }

    public class Lesson implements Serializable {
        private String lessonId;
        private String lessonName;
        private String beginTime;
        private String endTime;
        private String genseeId;
        private String genseeNum;//直播房间ID
        private String studentClientToken;//直播密码
        private String genseeVideoToken;//点播密码
        private String genseeVideoNum;//点播房间ID

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getLessonName() {
            return lessonName;
        }

        public void setLessonName(String lessonName) {
            this.lessonName = lessonName;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getGenseeId() {
            return genseeId;
        }

        public void setGenseeId(String genseeId) {
            this.genseeId = genseeId;
        }

        public String getGenseeNum() {
            return genseeNum;
        }

        public void setGenseeNum(String genseeNum) {
            this.genseeNum = genseeNum;
        }

        public String getStudentClientToken() {
            return studentClientToken;
        }

        public void setStudentClientToken(String studentClientToken) {
            this.studentClientToken = studentClientToken;
        }

        public String getGenseeVideoToken() {
            return genseeVideoToken;
        }

        public void setGenseeVideoToken(String genseeVideoToken) {
            this.genseeVideoToken = genseeVideoToken;
        }

        public String getGenseeVideoNum() {
            return genseeVideoNum;
        }

        public void setGenseeVideoNum(String genseeVideoNum) {
            this.genseeVideoNum = genseeVideoNum;
        }
    }
}
