package com.fastchar.http.core;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 11:23
 */
public enum FastHttpToolType {
    AUTO("自动判断使用OKHTTP还是HTTPCLIENT！"),
    OKHTTP("使用OKHTTP网络请求！"),
    HTTPCLIENT("使用httpclient进行网格请求！"),
    ;
    public final String details;

    FastHttpToolType(String details) {
        this.details = details;
    }
}
