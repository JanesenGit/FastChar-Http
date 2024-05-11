package com.fastchar.http.core;

import com.fastchar.utils.FastMD5Utils;
import com.fastchar.utils.FastStringUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.*;


/**
 * @author 沈建（Janesen）
 * @date 2021/8/10 18:03
 */
public class FastHttpRequest {

    private final String id;
    private String url;
    private String parentLayerCode;
    private List<FastHttpParam> params = new ArrayList<>();
    private FastHttpMethod method;
    private final List<FastHttpHeader> headers = new ArrayList<>();
    private final List<FastHttpCookie> cookies = new ArrayList<>();
    private FastHttpRequestType requestType = FastHttpRequestType.FORM;
    private long beginRequestTime;
    private long endRequestTime;

    private String charset = "utf-8";

    private boolean log = true;

    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public FastHttpRequest setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public FastHttpRequest() {
        this.id = FastMD5Utils.MD5To16(FastStringUtils.buildUUID());
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public FastHttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<FastHttpParam> getParams() {
        return params;
    }

    public String getParamsContent() {
        StringBuilder stringBuilder = new StringBuilder();
        for (FastHttpParam param : params) {
            stringBuilder.append(param.getContent());
        }
        return stringBuilder.toString();
    }

    public FastHttpRequest setParams(List<FastHttpParam> params) {
        this.params = params;
        return this;
    }

    public FastHttpMethod getMethod() {
        return method;
    }

    public FastHttpRequest setMethod(FastHttpMethod method) {
        this.method = method;
        return this;
    }

    public List<FastHttpHeader> getHeaders() {
        return headers;
    }

    public List<FastHttpCookie> getCookies() {
        return cookies;
    }

    public FastHttpRequest addParam(String name, Object value) {
        this.params.add(new FastHttpParam().setName(name).setValue(value));
        return this;
    }

    public FastHttpRequest addParam(FastHttpParam param) {
        this.params.add(param);
        return this;
    }


    public FastHttpRequest addHeader(String name, String value) {
        this.headers.add(new FastHttpHeader().setName(name).setValue(value));
        return this;
    }
    public FastHttpRequest addHeader(FastHttpHeader header) {
        this.headers.add(header);
        return this;
    }

    public FastHttpRequest addCookie(String name, String value) {
        this.removeCookie(name);
        this.cookies.add(new FastHttpCookie().setName(name).setValue(value));
        return this;
    }

    public FastHttpRequest removeCookie(String name) {
        List<FastHttpCookie> waitRemove = new ArrayList<>();
        for (FastHttpCookie cookie : this.cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                waitRemove.add(cookie);
            }
        }
        this.cookies.removeAll(waitRemove);
        return this;
    }

    public long getBeginRequestTime() {
        return beginRequestTime;
    }

    public FastHttpRequest setBeginRequestTime(long beginRequestTime) {
        this.beginRequestTime = beginRequestTime;
        return this;
    }

    public long getEndRequestTime() {
        return endRequestTime;
    }

    public FastHttpRequest setEndRequestTime(long endRequestTime) {
        this.endRequestTime = endRequestTime;
        return this;
    }

    public String getUrlAndParam() {
        if (FastStringUtils.isNotEmpty(url)) {
            if (url.contains("?")) {
                return url + "&" + FastStringUtils.join(toUrlParam(), "&");
            }
            return url + "?" + FastStringUtils.join(toUrlParam(), "&");
        }
        return null;
    }

    public List<String> toUrlParam() {
        List<String> urlParams = new ArrayList<>();
        for (FastHttpParam param : params) {
            if (param.getValue() instanceof File) {
                continue;
            }
            urlParams.add(param.getName() + "=" + param.getValue());
        }
        return urlParams;
    }

    public Map<String, Object> toMapParam() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (FastHttpParam param : params) {
            map.put(param.getName(), param.getValue());
        }
        return map;
    }

    public FastHttpRequestType getRequestType() {
        return requestType;
    }

    public FastHttpRequest setRequestType(FastHttpRequestType paramType) {
        this.requestType = paramType;
        return this;
    }

    public String getParentLayerCode() {
        return parentLayerCode;
    }

    public FastHttpRequest setParentLayerCode(String parentLayerCode) {
        this.parentLayerCode = parentLayerCode;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public FastHttpRequest setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public boolean isLog() {
        return log;
    }

    public FastHttpRequest setLog(boolean log) {
        this.log = log;
        return this;
    }

    @Override
    public String toString() {
        return "FastHttpRequest{" +
                "url='" + url + '\'' +
                ", params=" + Arrays.toString(params.toArray()) +
                ", method=" + method +
                ", headers=" + Arrays.toString(headers.toArray()) +
                ", paramType=" + requestType +
                '}';
    }
}
