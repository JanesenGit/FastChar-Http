package com.fastchar.http.httpclient;

import com.fastchar.interfaces.IFastConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/10 18:01
 */
public class FastHttpClientConfig implements IFastConfig {

    private volatile HttpClientBuilder clientBuilder;

    public HttpClientBuilder clientBuilder() {
        if (clientBuilder == null) {
            synchronized (this) {
                if (clientBuilder == null) {
                    SSLConnectionSocketFactory sslConnectionSocketFactory = null;
                    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
                    try {
                        SSLContextBuilder builder = new SSLContextBuilder();
                        // 全部信任 不做身份鉴定
                        builder.loadTrustMaterial(null, new TrustStrategy() {
                            @Override
                            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                return true;
                            }
                        });
                        sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                                new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"},
                                null, NoopHostnameVerifier.INSTANCE);

                        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                                .register("http", new PlainConnectionSocketFactory())
                                .register("https", sslConnectionSocketFactory)
                                .build();
                        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
                        poolingHttpClientConnectionManager.setMaxTotal(1000);
                        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(poolingHttpClientConnectionManager.getMaxTotal());

                    } catch (Exception ignored) {
                    }

                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setSocketTimeout(1000 * 60 * 3)
                            .setConnectTimeout(1000 * 60 * 3)
                            .setConnectionRequestTimeout(1000 * 60 * 3)
                            .build();

                    clientBuilder = HttpClientBuilder.create()
                            .setSSLSocketFactory(sslConnectionSocketFactory)
                            .setConnectionManager(poolingHttpClientConnectionManager)
                            .setConnectionManagerShared(true)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .setSSLHostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String s, SSLSession sslSession) {
                                    return true;
                                }
                            });

                }
            }
        }
        return clientBuilder;
    }


}
