package com.fastchar.http.interfaces;

import com.fastchar.core.FastHandler;
import com.fastchar.http.core.FastHttpRequest;
import com.fastchar.http.core.FastHttpResponse;

/**
 * 网络请求监听
 * @author 沈建（Janesen）
 * @date 2021/8/23 09:41
 */
public interface IFastHttpListener {

    /**
     * 请求前监听
     * @param request 请求对象
     * @param handler 请求句柄，当code!=0时，终止请求！
     */
    void onBeforeRequest(FastHttpRequest request, FastHandler handler);


    /**
     * 请求后监听
     * @param request 请求对象
     * @param response 请求响应
     */
    void onAfterRequest(FastHttpRequest request, FastHttpResponse response);

}
