package com.fastchar.http.core;

import java.io.Serializable;

public class FastHttpCookie implements Serializable {

    private String name;

    private String value;

    private long expiresAt;

    private String domain;

    private String path = "/";

    private boolean secure;

    private boolean httpOnly;

    private boolean hostOnly;

    private boolean persistent;

    public String getName() {
        return name;
    }

    public FastHttpCookie setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FastHttpCookie setValue(String value) {
        this.value = value;
        return this;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public FastHttpCookie setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public FastHttpCookie setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FastHttpCookie setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public FastHttpCookie setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public FastHttpCookie setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public boolean isHostOnly() {
        return hostOnly;
    }

    public FastHttpCookie setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
        return this;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public FastHttpCookie setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }
}
