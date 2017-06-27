package com.gensee.player.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.gensee.player.model
 * 2017-06-2017/6/20.
 * wuxu@xdf.cn
 */

public class PlGroup extends PlResponse implements Serializable{

    private ArrayList<Group> result = new ArrayList<>();

    public ArrayList<Group> getResult() {
        return result;
    }

    public void setResult(ArrayList<Group> result) {
        this.result = result;
    }

    public class Group implements Serializable{
        private String group_number;
        private String group_desc;
        private String group_img_url;

        public String getGroup_number() {
            return group_number;
        }

        public void setGroup_number(String group_number) {
            this.group_number = group_number;
        }

        public String getGroup_desc() {
            return group_desc;
        }

        public void setGroup_desc(String group_desc) {
            this.group_desc = group_desc;
        }

        public String getGroup_img_url() {
            return group_img_url;
        }

        public void setGroup_img_url(String group_img_url) {
            this.group_img_url = group_img_url;
        }
    }
}
