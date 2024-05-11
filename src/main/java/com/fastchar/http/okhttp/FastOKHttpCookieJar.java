package com.fastchar.http.okhttp;

import com.fastchar.core.FastChar;
import com.fastchar.http.core.FastHttpCookie;
import com.fastchar.interfaces.IFastCache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Cookie处理类
 *
 * @author 沈建（Janesen）
 * @date 2021/8/13 10:12
 */
public class FastOKHttpCookieJar implements CookieJar {

    private final List<FastHttpCookie> cookies = new ArrayList<>();

    public void addCookie(String name, String value) {
        this.removeCookie(name);
        this.cookies.add(new FastHttpCookie().setName(name).setValue(value));
    }

    public void addCookies(List<FastHttpCookie> cookies) {
        for (FastHttpCookie cookie : cookies) {
            this.addCookie(cookie);
        }
    }

    public void addCookie(FastHttpCookie cookie) {
        this.removeCookie(cookie.getName());
        this.cookies.add(cookie);
    }

    public void removeCookie(String name) {
        List<FastHttpCookie> waitRemove = new ArrayList<>();
        for (FastHttpCookie cookie : this.cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                waitRemove.add(cookie);
            }
        }
        this.cookies.removeAll(waitRemove);
    }


    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<FastHttpCookie> cookieList = null;
        try {
            IFastCache iFastCache = FastChar.safeGetCache();
            if (iFastCache != null) {
                cookieList = iFastCache.get(FastOKHttpCookieJar.class.getSimpleName(), httpUrl.host());
            } else {
                cookieList = FastChar.getMemoryCache().get(httpUrl.host());
            }
        } catch (Exception e) {
            FastChar.getLogger().error(this.getClass(), e);
        }
        if (cookieList != null) {
            cookieList.addAll(this.cookies);
            List<Cookie> list = new ArrayList<>();
            for (FastHttpCookie httpCookie : cookieList) {
                list.add(toCookie(httpCookie));
            }
            return list;
        }
        return new ArrayList<>();
    }


    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        try {
            List<FastHttpCookie> cookieList = new ArrayList<>();
            for (Cookie cookie : list) {
                cookieList.add(new FastHttpCookie()
                        .setName(cookie.name())
                        .setValue(cookie.value())
                        .setHttpOnly(cookie.httpOnly())
                        .setHostOnly(cookie.hostOnly())
                        .setSecure(cookie.secure())
                        .setDomain(cookie.domain())
                        .setExpiresAt(cookie.expiresAt())
                        .setPersistent(cookie.persistent())
                        .setPath(cookie.path()));
            }
            IFastCache iFastCache = FastChar.safeGetCache();
            if (iFastCache != null) {
                iFastCache.set(FastOKHttpCookieJar.class.getSimpleName(), httpUrl.host(), cookieList);
            } else {
                FastChar.getMemoryCache().put(httpUrl.host(), cookieList);
            }
        } catch (Exception e) {
            FastChar.getLogger().error(this.getClass(), e);
        }
    }


    private Cookie toCookie(FastHttpCookie httpCookie) {
        Cookie.Builder builder = new Cookie.Builder()
                .domain(httpCookie.getDomain())
                .expiresAt(httpCookie.getExpiresAt())
                .name(httpCookie.getName())
                .path(httpCookie.getPath())
                .value(httpCookie.getValue());

        if (httpCookie.isHttpOnly()) {
            builder.httpOnly();
        }
        if (httpCookie.isSecure()) {
            builder.secure();
        }
        if (httpCookie.isHostOnly()) {
            builder.hostOnlyDomain(httpCookie.getDomain());
        }
        return builder.build();
    }


}

