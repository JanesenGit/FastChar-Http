package com.fastchar.http.core;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 10:54
 */
public enum FastHttpRequestType {
    /**
     * 使用表单 [application/x-www-form-urlencoded] 或 [multipart/form-data] 形式提交，将根据参数类型选择！遇到get方法时，将自动拼接url参数！
     */
    FORM("使用表单 [application/x-www-form-urlencoded] 或 [multipart/form-data] 形式提交！遇到get方法时，将自动拼接url参数！"),

    /**
     * 将params对象转为JSON字符串，以JSON格式提交，只支持post、delete、put、patch方法！
     */
    JSON_ARRAY("将params对象转为JSON字符串，以JSON格式提交，只支持post、delete、put、patch方法！"),

    /**
     * 将params对象转为Map对象后再转为JSON字符串，以JSON格式提交，只支持post、delete、put、patch方法！
     */
    JSON_MAP("将params对象转为Map对象后再转为JSON字符串，以JSON格式提交，只支持post、delete、put、patch方法！"),

    /**
     * 将params对象转string字符串后，以JSON格式提交，只支持post、delete、put、patch方法！
     */
    JSON_PLAIN("将params参数值转string字符串【不包含参数名】拼接后，以JSON格式提交，只支持post、delete、put、patch方法！"),


    /**
     * 将params对象转为string字符串后，以字符串内容提交，只支持post、delete、put、patch方法！
     */
    TEXT_PLAIN("将params对象转为string字符串后，以字符串内容提交，只支持post、delete、put、patch方法！"),
    ;
    public final String details;

    FastHttpRequestType(String details) {
        this.details = details;
    }
}
