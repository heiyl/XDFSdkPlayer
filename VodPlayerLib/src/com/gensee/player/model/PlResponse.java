package com.gensee.player.model;

import java.io.Serializable;

/**
 * com.gensee.player.model
 * 2017-06-2017/6/19.
 * wuxu@xdf.cn
 */

public class PlResponse implements Serializable {

    private int status;
    private String info;

    public boolean getStatus() {
        return status > 0;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
