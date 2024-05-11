package com.fastchar.http.okhttp;

import com.fastchar.core.FastChar;
import com.fastchar.interfaces.IFastConfig;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/10 18:01
 */
public class FastOKHttpConfig implements IFastConfig {

    private volatile OkHttpClient.Builder clientBuilder;

    public OkHttpClient.Builder clientBuilder() {
        if (clientBuilder == null) {
            synchronized (this) {
                if (clientBuilder == null) {
                    SSLSocketFactory ssfFactory = null;
                    X509TrustManager x509TrustManager = new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    };
                    try {
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
                        ssfFactory = sc.getSocketFactory();
                    } catch (Exception e) {
                        FastChar.getLogger().error(this.getClass(), e);
                    }

                    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    };

                    clientBuilder = new OkHttpClient.Builder()
                            .cookieJar(new FastOKHttpCookieJar())
                            .hostnameVerifier(hostnameVerifier)
                            .connectTimeout(1000 * 60 * 5, TimeUnit.MILLISECONDS)
                            .readTimeout(1000 * 60 * 5, TimeUnit.MILLISECONDS)
                            .callTimeout(1000 * 60 * 5, TimeUnit.MILLISECONDS)
                            .writeTimeout(1000 * 60 * 5, TimeUnit.MILLISECONDS)
                            .pingInterval(3, TimeUnit.SECONDS);

                    if (ssfFactory != null) {
                        clientBuilder.sslSocketFactory(ssfFactory, x509TrustManager);
                    }
                }
            }
        }
        return clientBuilder;
    }
}
