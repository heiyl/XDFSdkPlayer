package com.gensee.utils;

/**
 * com.gensee.utils
 * 2017-06-2017/6/20.
 * wuxu@xdf.cn
 */

public class API {

    public static final Boolean isDebug = Boolean.TRUE;

    //第三方
    //微信
    public static final String WX_APP_ID = "wx8a15cbb7c58a948d";
    public static final String WX_APP_KEY = "01adaecf88eedc9cce75bb03015aa61c";

    //微课堂
    public static final String ESTUDY_APP_KEY = "CE804942A6D34511BBF4A935E0F7BF11";
    public static final String ESTUDY_CHANNEL_ID = "1006";

    public static final String ESTUDY_BASE_URL = "http://estudy.staff.xdf.cn/api.php";
    //线下咨询提交手机号  CourseNote/index
    public static final String COMMIT_PHONE_NUMBER_URL = ESTUDY_BASE_URL + "/CourseNote/index";
    //获取群信息
    public static final String GROUP_INTO_URL = ESTUDY_BASE_URL + "/CourseGroup/index";

    //课程下的课时列表
    public static final String COURSE_LIST_URL = ESTUDY_BASE_URL + "/CourseLessonList/index";
}
