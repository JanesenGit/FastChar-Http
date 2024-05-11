package com.fastchar.http.httpclient;

import com.fastchar.annotation.AFastClassFind;
import com.fastchar.core.FastChar;
import com.fastchar.http.FastHttpConfig;
import com.fastchar.http.core.*;
import com.fastchar.http.interfaces.IFastHttpRequest;
import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastMD5Utils;
import com.fastchar.utils.FastStringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 11:29
 */
@AFastClassFind({"org.apache.http.client.HttpClient", "org.apache.http.entity.mime.MIME", "org.apache.http.HttpEntity"})
public class FastHttpClientRequest implements IFastHttpRequest {

    private final CloseableHttpClient client;

    public FastHttpClientRequest() {
        FastHttpClientConfig config = FastChar.getConfig(FastHttpClientConfig.class);
        client = config.clientBuilder()
                .build();
        if (FastChar.getConfig(FastHttpConfig.class).isDebug()) {
            FastChar.getLogger().info(this.getClass(), "启用ApacheHttpClient网络组件！");
        }
    }

    @Override
    public FastHttpResponse request(FastHttpRequest request) {
        FastHttpResponse fastHttpResponse = new FastHttpClientResponse().setRequest(request);
        try {
            RequestBuilder builder = RequestBuilder
                    .create(request.getMethod().name());
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
                builder.setUri(new URI(request.getUrlAndParam()));
            } else {
                builder.setUri(new URI(request.getUrl())).setEntity(builderHttpEntity(request));
            }


            if (request.getOutputStream() != null) {
                HttpEntity entity = builder.getEntity();
                if (entity != null) {
                    entity.writeTo(request.getOutputStream());
                    fastHttpResponse.setStatusCode(0);
                    return fastHttpResponse;
                }
                fastHttpResponse.setStatusCode(-1);
                return fastHttpResponse;
            }

            HttpUriRequest build = builder.build();

            HttpClientContext httpClientContext = HttpClientContext.create();
            BasicCookieStore cookieStore = new BasicCookieStore();
            for (FastHttpCookie cookie : request.getCookies()) {
                if (FastStringUtils.isEmpty(cookie.getDomain())) {
                    cookie.setDomain(new URI(request.getUrl()).getHost());
                }
                BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
                basicClientCookie.setPath(cookie.getPath());
                basicClientCookie.setDomain(cookie.getDomain());
                basicClientCookie.setSecure(cookie.isSecure());
                basicClientCookie.setExpiryDate(new Date(cookie.getExpiresAt()));
                cookieStore.addCookie(basicClientCookie);
            }
            httpClientContext.setCookieStore(cookieStore);


            HttpResponse execute = client.execute(build, httpClientContext);
            fastHttpResponse.setSuccess(true);
            fastHttpResponse.setStatusCode(execute.getStatusLine().getStatusCode());
            Header[] allHeaders = execute.getAllHeaders();
            for (Header header : allHeaders) {
                if (header == null) {
                    continue;
                }
                if (FastStringUtils.isEmpty(header.getName())) {
                    continue;
                }
                if (header.getValue() == null) {
                    continue;
                }
                fastHttpResponse.getHeaders().add(new FastHttpHeader().setName(header.getName()).setValue(header.getValue()));
            }
            String contentType = null;
            String contentDisposition = null;
            Header contentTypeHeader = execute.getLastHeader("Content-Type");
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
            }
            Header contentDispositionHeader = execute.getLastHeader("Content-Disposition");
            if (contentDispositionHeader != null) {
                contentDisposition = contentDispositionHeader.getValue();
            }

            String fileExtension = FastHttpHelper.getFileExtension(contentType);
            HttpEntity entity = execute.getEntity();
            if (FastHttpHelper.isTextContent(contentType)) {
                fastHttpResponse.setContent(EntityUtils.toString(entity, request.getCharset()));
                EntityUtils.consumeQuietly(entity);
            } else if (FastStringUtils.isNotEmpty(fileExtension)) {
                String fileName = FastHttpHelper.getFileName(contentDisposition);
                if (FastStringUtils.isEmpty(fileName)) {
                    fileName = FastMD5Utils.MD5To16(request.getUrl()) + "." + fileExtension;
                }

                File file = new File(FastChar.getConstant().getAttachDirectory(), fileName);
                FastFileUtils.copyInputStreamToFile(entity.getContent(), file);
                fastHttpResponse.setContentFile(file);
                EntityUtils.consumeQuietly(entity);
            } else {
                fastHttpResponse.setContentStream(entity.getContent());
            }
        } catch (Exception e) {
            FastChar.getLogger().error(this.getClass(), e);
            fastHttpResponse.setException(true);
            fastHttpResponse.setSuccess(false);
            fastHttpResponse.setContent(e.toString());
        }
        fastHttpResponse.setHttpToolType(FastHttpToolType.HTTPCLIENT);

        return fastHttpResponse;
    }


    private HttpEntity builderHttpEntity(FastHttpRequest request) {
        try {

            if (request.getRequestType() == FastHttpRequestType.JSON_MAP) {
                return new StringEntity(FastChar.getJson().toJson(request.toMapParam()), ContentType.APPLICATION_JSON);
            } else if (request.getRequestType() == FastHttpRequestType.JSON_ARRAY) {
                return new StringEntity(FastChar.getJson().toJson(request.getParams()), ContentType.APPLICATION_JSON);
            } else if (request.getRequestType() == FastHttpRequestType.JSON_PLAIN) {
                return new StringEntity(request.getParamsContent(), ContentType.APPLICATION_JSON);
            } else if (request.getRequestType() == FastHttpRequestType.TEXT_PLAIN) {
                return new StringEntity(request.getParamsContent(), ContentType.TEXT_PLAIN);
            }

            boolean isMultipart = false;
            List<NameValuePair> formParam = new ArrayList<>();
            MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
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
                            multipartBuilder.addPart(param.getName(), new FileBody(file));
                        } else {
                            FastChar.getLogger().error(FastHttpRequest.class, "文件：" + file.getAbsolutePath() + " 无法确定mediaType！");
                        }
                        isMultipart = true;
                    } else {
                        FastChar.getLogger().error(FastHttpRequest.class, "文件：" + file.getAbsolutePath() + " 不存在！");
                    }
                } else {
                    formParam.add(new BasicNameValuePair(param.getName(), String.valueOf(value)));
                    multipartBuilder.addTextBody(param.getName(), String.valueOf(value), ContentType.TEXT_PLAIN.withCharset("utf-8"));
                }
            }
            if (isMultipart) {
                return multipartBuilder.build();
            } else {
                return new UrlEncodedFormEntity(formParam, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
