package com.fastchar.http.interfaces;

import com.fastchar.http.core.FastHttpRequest;
import com.fastchar.http.core.FastHttpResponse;

/**
 * Http网络请求接口
 * @author 沈建（Janesen）
 * @date 2021/8/13 10:03
 */
public interface IFastHttpRequest {

    FastHttpResponse request(FastHttpRequest request);

}
