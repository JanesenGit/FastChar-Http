package com.fastchar.http;

import com.fastchar.core.FastChar;
import com.fastchar.http.core.FastHttpHeader;
import com.fastchar.http.core.FastHttpToolType;
import com.fastchar.http.httpclient.FastHttpClientConfig;
import com.fastchar.http.okhttp.FastOKHttpConfig;
import com.fastchar.interfaces.IFastConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 11:20
 */
public class FastHttpConfig implements IFastConfig {

    private FastHttpToolType httpType = FastHttpToolType.AUTO;
    private FastOKHttpConfig okHttpConfig;
    private FastHttpClientConfig httpClientConfig;
    private boolean debug;

    private final List<FastHttpHeader> headers = new ArrayList<>();

    public FastHttpConfig() {
        if (FastChar.getFindClass().test("org.apache.http.client.HttpClient")
                && FastChar.getFindClass().test("org.apache.http.entity.mime.MIME")
                && FastChar.getFindClass().test("org.apache.http.HttpEntity")) {
            httpClientConfig = FastChar.getConfig(FastHttpClientConfig.class);
        }

        if (FastChar.getFindClass().test("okhttp3.OkHttpClient")) {
            okHttpConfig = FastChar.getConfig(FastOKHttpConfig.class);
        }
    }

    public FastOKHttpConfig getOkHttpConfig() {
        return okHttpConfig;
    }

    public FastHttpToolType getHttpType() {
        return httpType;
    }

    public FastHttpConfig setHttpType(FastHttpToolType httpType) {
        this.httpType = httpType;
        return this;
    }

    public FastHttpClientConfig getHttpClientConfig() {
        return httpClientConfig;
    }


    public FastHttpConfig addHeader(String name, String value) {
        headers.add(new FastHttpHeader(name, value));
        return this;
    }

    public FastHttpConfig addHeader(FastHttpHeader header) {
        headers.add(header);
        return this;
    }

    public List<FastHttpHeader> getHeaders() {
        return headers;
    }

    public boolean isDebug() {
        return debug;
    }

    public FastHttpConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
