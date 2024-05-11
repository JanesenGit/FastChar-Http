package com.fastchar.http.core;

import com.fastchar.core.FastJsonWrap;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 10:04
 */
public class FastHttpResponse {

    private String content;
    private File contentFile;

    private int statusCode;
    private boolean success;
    private boolean exception;
    private List<FastHttpHeader> headers = new ArrayList<>();

    private transient InputStream contentStream;
    private transient FastHttpRequest request;

    private FastHttpToolType httpToolType;

    /**
     * 获取Http请求返回的文本内容
     *
     * @return String
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置Http请求返回的文本内容
     *
     * @param content 文本内容
     * @return 当前对象
     */
    public FastHttpResponse setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 判断请求是否执行成功，注意：并不代表请求返回的code==200
     *
     * @return Boolean
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置请求是否执行成功
     *
     * @param success Boolean
     * @return 当前对象
     */
    public FastHttpResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public List<FastHttpHeader> getHeaders() {
        return headers;
    }

    public FastHttpResponse setHeaders(List<FastHttpHeader> headers) {
        this.headers = headers;
        return this;
    }

    public boolean isException() {
        return exception;
    }

    public FastHttpResponse setException(boolean exception) {
        this.exception = exception;
        return this;
    }

    public FastHttpRequest getRequest() {
        return request;
    }

    public FastHttpResponse setRequest(FastHttpRequest request) {
        this.request = request;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public FastHttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getHeadValue(String name) {
        for (FastHttpHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public List<String> getHeadValues(String name) {
        List<String> values = new ArrayList<>();
        for (FastHttpHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                values.add(header.getValue());
            }
        }
        return values;
    }

    public File getContentFile() {
        return contentFile;
    }

    public FastHttpResponse setContentFile(File contentFile) {
        this.contentFile = contentFile;
        return this;
    }

    /**
     * 获取json值的处理对象
     *
     * @return FastJsonWrap
     */
    public FastJsonWrap getJsonWrap() {
        return new FastJsonWrap(content);
    }


    public InputStream getContentStream() {
        return contentStream;
    }

    public FastHttpResponse setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    public FastHttpToolType getHttpToolType() {
        return httpToolType;
    }

    public FastHttpResponse setHttpToolType(FastHttpToolType httpToolType) {
        this.httpToolType = httpToolType;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "code=" + statusCode +
                ", content='" + content + '\'' +
                ", contentFile='" + contentFile + '\'' +
                ", contentStream='" + contentStream + '\'' +
                ", success=" + success +
                ", exception=" + exception +
                ",headers=" + Arrays.toString(headers.toArray()) +
                '}';
    }

}
