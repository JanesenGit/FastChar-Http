package com.fastchar.http;

import com.fastchar.core.FastChar;
import com.fastchar.core.FastHandler;
import com.fastchar.exception.FastFindException;
import com.fastchar.http.core.*;
import com.fastchar.http.httpclient.FastHttpClientRequest;
import com.fastchar.http.interfaces.IFastHttpListener;
import com.fastchar.http.interfaces.IFastHttpRequest;
import com.fastchar.http.okhttp.FastOKHttpRequest;
import com.fastchar.utils.FastStringUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 11:05
 */
public class FastHttp {
    private static boolean INIT = false;

    synchronized static void initConfig() {
        if (INIT) {
            return;
        }
        try {
            INIT = true;
            FastHttpToolType httpType = FastChar.getConfig(FastHttpConfig.class).getHttpType();
            if (httpType == FastHttpToolType.AUTO) {
                FastChar.getOverrides()
                        .add(FastHttpClientRequest.class)
                        .add(FastOKHttpRequest.class);
            }

            if (httpType == FastHttpToolType.OKHTTP) {
                FastChar.getFindClass()
                        .find("okhttp3.OkHttpClient",
                                "https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp");

                FastChar.getOverrides()
                        .add(FastOKHttpRequest.class);
            }

            if (httpType == FastHttpToolType.HTTPCLIENT) {
                FastChar.getFindClass()
                        .find("org.apache.http.client.HttpClient",
                                "https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient");

                FastChar.getOverrides()
                        .add(FastHttpClientRequest.class);
            }
        } catch (FastFindException e) {
            e.printStackTrace();
        }
    }

    public static Request newRequest() {
        return new Request().reset();
    }

    public static Request newRequest(String url) {
        return new Request().reset().setUrl(url);
    }

    public static Request request() {
        return FastChar.getOverrides().singleInstance(Request.class).reset();
    }

    public static Request request(String url) {
        return FastChar.getOverrides().singleInstance(Request.class).reset().setUrl(url);
    }

    public static class Request {
        private final IFastHttpRequest request;
        private FastHttpRequest httpRequest;

        public Request() {
            if (!INIT) {
                initConfig();
            }
            request = FastChar.getOverrides().newInstance(IFastHttpRequest.class);
        }

        private Request reset() {
            httpRequest = new FastHttpRequest();
            return this;
        }

        public Request setLog(boolean log) {
            httpRequest.setLog(log);
            return this;
        }


        public Request setUrl(String url) {
            httpRequest.setUrl(url);
            return this;
        }

        public String getUrl() {
            return httpRequest.getUrl();
        }

        public Request addParam(String name, Object value) {
            httpRequest.addParam(name, value);
            return this;
        }

        public Request addParam(Map<?, ?> params) {
            for (Object key : params.keySet()) {
                httpRequest.addParam(String.valueOf(key), params.get(key));
            }
            return this;
        }

        public Request addParam(List<FastHttpParam> params) {
            for (FastHttpParam param : params) {
                httpRequest.addParam(param);
            }
            return this;
        }

        public Request addParam(FastHttpParam param) {
            httpRequest.addParam(param);
            return this;
        }

        public Request addParam(String content) {
            httpRequest.addParam(new FastHttpParam().setContent(content));
            return this;
        }

        public Request addHeader(String name, String value) {
            httpRequest.addHeader(name, value);
            return this;
        }

        public Request addHeader(FastHttpHeader header) {
            httpRequest.addHeader(header);
            return this;
        }

        public Request addCookie(String name, String value) {
            httpRequest.addCookie(name,value);
            return this;
        }

        public Request clearEmptyHeader() {
            List<FastHttpHeader> headers = httpRequest.getHeaders();
            List<FastHttpHeader> waitRemove = new ArrayList<>();
            for (FastHttpHeader header : headers) {
                if (header == null) {
                    continue;
                }
                if (FastStringUtils.isEmpty(header.getName()) || FastStringUtils.isEmpty(header.getValue())) {
                    waitRemove.add(header);
                }
            }
            headers.removeAll(waitRemove);
            return this;
        }

        public Map<String,String> getHeadersMap() {
            Map<String, String> headers = new LinkedHashMap<>();
            List<FastHttpHeader> httpHeaders = httpRequest.getHeaders();
            for (FastHttpHeader header : httpHeaders) {
                if (header == null) {
                    continue;
                }
                headers.put(header.getName(), header.getValue());
            }
            return headers;
        }

        public List<FastHttpHeader> getHeadersList() {
            return httpRequest.getHeaders();
        }


        public List<FastHttpParam> getParams() {
            return httpRequest.getParams();
        }


        public Request setRequestType(FastHttpRequestType paramType) {
            httpRequest.setRequestType(paramType);
            return this;
        }

        public Request setParentLayerCode(String layerCode) {
            httpRequest.setParentLayerCode(layerCode);
            return this;
        }

        public Request setMethod(String method) {
            httpRequest.setMethod(FastHttpMethod.valueOf(method.toUpperCase()));
            return this;
        }

        public Request clearParams() {
            httpRequest.getParams().clear();
            return this;
        }

        public FastHttpResponse post() {
            httpRequest.setMethod(FastHttpMethod.POST);
            return doRequest();
        }

        public FastHttpResponse put() {
            httpRequest.setMethod(FastHttpMethod.PUT);
            return doRequest();
        }

        public FastHttpResponse delete() {
            httpRequest.setMethod(FastHttpMethod.DELETE);
            return doRequest();
        }

        public FastHttpResponse get() {
            httpRequest.setMethod(FastHttpMethod.GET);
            return doRequest();
        }

        public FastHttpResponse patch() {
            httpRequest.setMethod(FastHttpMethod.PATCH);
            return doRequest();
        }

        public FastHttpResponse head() {
            httpRequest.setMethod(FastHttpMethod.HEAD);
            return doRequest();
        }

        public FastHttpResponse options() {
            httpRequest.setMethod(FastHttpMethod.OPTIONS);
            return doRequest();
        }


        public FastHttpMethod getMethod() {
            return httpRequest.getMethod();
        }


        public boolean writeTo(OutputStream outputStream) {
            httpRequest.setOutputStream(outputStream);
            FastHttpResponse httpResponse = this.request.request(httpRequest);
            httpRequest.setOutputStream(null);
            return httpResponse.getStatusCode() == 0;
        }

        public FastHttpResponse request() {
            if (httpRequest.getMethod() == null) {
                httpRequest.setMethod(FastHttpMethod.GET);
            }
            return doRequest();
        }

        public FastHttpResponse request(String methodName) {
            if (FastStringUtils.isEmpty(methodName)) {
                methodName = FastHttpMethod.GET.name();
            }
            httpRequest.setMethod(FastHttpMethod.valueOf(methodName.toUpperCase()));
            return doRequest();
        }


        private FastHttpResponse doRequest() {
            FastHttpConfig httpConfig = FastChar.getConfig(FastHttpConfig.class);
            for (FastHttpHeader header : httpConfig.getHeaders()) {
                httpRequest.addHeader(header);
            }
            httpRequest.setBeginRequestTime(System.currentTimeMillis());
            List<IFastHttpListener> iFastHttpListeners = FastChar.getOverrides().newInstances(false, IFastHttpListener.class);
            for (IFastHttpListener iFastHttpListener : iFastHttpListeners) {
                if (iFastHttpListener != null) {
                    FastHandler handler = new FastHandler();
                    iFastHttpListener.onBeforeRequest(httpRequest, handler);
                    if (handler.getCode() != 0) {
                        FastHttpResponse httpResponse = new FastHttpResponse();
                        httpResponse.setSuccess(false);
                        httpResponse.setStatusCode(handler.getCode());
                        httpResponse.setContent(handler.getError());
                        return httpResponse;
                    }
                }
            }
            FastHttpResponse httpResponse = this.request.request(httpRequest);
            httpRequest.setEndRequestTime(System.currentTimeMillis());
            for (IFastHttpListener iFastHttpListener : iFastHttpListeners) {
                if (iFastHttpListener != null) {
                    iFastHttpListener.onAfterRequest(httpRequest, httpResponse);
                }
            }
            return httpResponse;
        }
    }

}
