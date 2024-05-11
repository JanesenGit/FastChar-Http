package com.fastchar.http.okhttp;

import com.fastchar.annotation.AFastClassFind;
import com.fastchar.core.FastChar;
import com.fastchar.http.FastHttpConfig;
import com.fastchar.http.core.*;
import com.fastchar.http.interfaces.IFastHttpRequest;
import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastMD5Utils;
import com.fastchar.utils.FastStringUtils;
import okhttp3.*;
import okio.Buffer;

import java.io.File;
import java.net.URI;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 10:09
 */
@AFastClassFind("okhttp3.OkHttpClient")
public class FastOKHttpRequest implements IFastHttpRequest {
    private final OkHttpClient client;

    public FastOKHttpRequest() {
        FastOKHttpConfig config = FastChar.getConfig(FastOKHttpConfig.class);
        client = config.clientBuilder()
                .build();
        if (FastChar.getConfig(FastHttpConfig.class).isDebug()) {
            FastChar.getLogger().info(this.getClass(), "启用okhttp3网络组件！");
        }
    }

    @Override
    public FastHttpResponse request(FastHttpRequest request) {
        FastHttpResponse fastHttpResponse = new FastOKHttpResponse().setRequest(request);
        try {
            if (request == null) {
                return fastHttpResponse;
            }
            Request.Builder builder = new Request.Builder()
                    .url(request.getUrl());

            for (FastHttpHeader header : request.getHeaders()) {
                if (header == null) {
                    continue;
                }
                if (FastStringUtils.isEmpty(header.getName())) {
                    continue;
                }
                if (header.getValue() == null) {
                    continue;
                }
                builder.addHeader(header.getName(), header.getValue());
            }
            if (request.getMethod() == FastHttpMethod.GET
                    || request.getMethod() == FastHttpMethod.HEAD) {
                builder.url(request.getUrlAndParam()).method(request.getMethod().name(), null);
            } else {
                builder.method(request.getMethod().name(), builderRequestBody(request));
            }


            Request build = builder.build();

            if (request.getOutputStream() != null) {
                RequestBody body = build.body();
                if (body != null) {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    request.getOutputStream().write(buffer.readByteArray());
                    fastHttpResponse.setStatusCode(0);
                    return fastHttpResponse;
                }
                fastHttpResponse.setStatusCode(-9);
                return fastHttpResponse;
            }

            CookieJar cookieJar = client.cookieJar();
            if (cookieJar instanceof FastOKHttpCookieJar) {
                for (FastHttpCookie cookie : request.getCookies()) {
                    if (FastStringUtils.isEmpty(cookie.getDomain())) {
                        cookie.setDomain(new URI(request.getUrl()).getHost());
                    }
                    ((FastOKHttpCookieJar) cookieJar).addCookie(cookie);
                }
            }

            Call call = client.newCall(build);
            Response response = call.execute();
            fastHttpResponse.setSuccess(true);
            fastHttpResponse.setStatusCode(response.code());
            Headers headers = response.headers();
            for (String name : headers.names()) {
                fastHttpResponse.getHeaders().add(new FastHttpHeader().setName(name).setValue(headers.get(name)));
            }
            String contentType = response.header("Content-Type");
            String contentDisposition = response.header("Content-Disposition");
            ResponseBody body = response.body();
            if (body != null) {
                String fileExtension = FastHttpHelper.getFileExtension(contentType);
                if (FastHttpHelper.isTextContent(contentType)) {
                    fastHttpResponse.setContent(body.string());
                    response.close();
                } else if (FastStringUtils.isNotEmpty(fileExtension)) {

                    String fileName = FastHttpHelper.getFileName(contentDisposition);
                    if (FastStringUtils.isEmpty(fileName)) {
                        fileName = FastMD5Utils.MD5To16(request.getUrl()) + "." + fileExtension;
                    }

                    File file = new File(FastChar.getConstant().getAttachDirectory(), fileName);
                    FastFileUtils.copyInputStreamToFile(body.byteStream(), file);
                    fastHttpResponse.setContentFile(file);
                    response.close();
                } else {
                    fastHttpResponse.setContentStream(body.byteStream());
                }
            }
        } catch (Exception e) {
            FastChar.getLogger().error(this.getClass(), e);
            fastHttpResponse.setException(true);
            fastHttpResponse.setSuccess(false);
            fastHttpResponse.setContent(e.toString());
        }
        fastHttpResponse.setHttpToolType(FastHttpToolType.OKHTTP);
        return fastHttpResponse;
    }

    private RequestBody builderRequestBody(FastHttpRequest request) {
        if (request.getRequestType() == FastHttpRequestType.JSON_MAP) {
            return RequestBody.create(FastChar.getJson().toJson(request.toMapParam()), MediaType.parse("application/json;charset=utf-8"));
        } else if (request.getRequestType() == FastHttpRequestType.JSON_ARRAY) {
            return RequestBody.create(FastChar.getJson().toJson(request.getParams()), MediaType.parse("application/json;charset=utf-8"));
        }else if (request.getRequestType() == FastHttpRequestType.JSON_PLAIN) {
            return RequestBody.create(request.getParamsContent(), MediaType.parse("application/json;charset=utf-8"));
        }  else if (request.getRequestType() == FastHttpRequestType.TEXT_PLAIN) {
            return RequestBody.create(request.getParamsContent(), MediaType.parse("text/plain;charset=utf-8"));
        }


        boolean isMultipart = false;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (FastHttpParam param : request.getParams()) {
            if (param == null) {
                continue;
            }
            Object value = param.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof File) {
                File file = (File) value;
                if (file.exists()) {
                    String mediaTypeOrNull = FastFileUtils.guessMimeType(file.getAbsolutePath());
                    if (FastStringUtils.isNotEmpty(mediaTypeOrNull)) {
                        multipartBuilder.addFormDataPart(param.getName(), file.getName(),
                                RequestBody.create(file, MediaType.parse(mediaTypeOrNull)));
                    } else {
                        FastChar.getLogger().error(FastHttpRequest.class, "文件：" + file.getAbsolutePath() + " 无法确定mediaType！");
                    }
                    isMultipart = true;
                } else {
                    FastChar.getLogger().error(FastHttpRequest.class, "文件：" + file.getAbsolutePath() + " 不存在！");
                }
            } else {
                formBodyBuilder.add(param.getName(), String.valueOf(value));
                multipartBuilder.addFormDataPart(param.getName(), String.valueOf(value));
            }
        }
        if (isMultipart) {
            return multipartBuilder.build();
        } else {
            return formBodyBuilder.build();
        }
    }


}
