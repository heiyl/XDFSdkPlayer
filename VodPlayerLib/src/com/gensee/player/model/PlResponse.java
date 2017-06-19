package com.gensee.player.model;

/**
 * com.gensee.player.model
 * 2017-06-2017/6/19.
 * wuxu@xdf.cn
 */

public class PlResponse {

    private int status;
    private String result;
    private String info;

    public boolean getStatus() {
        return status > 0;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
