package com.gensee.player.model;

/**
 * com.gensee.player.model
 * 2017-06-2017/6/19.
 * wuxu@xdf.cn
 */

public class Response {

    private boolean status;
    private String result;
    private String info;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
