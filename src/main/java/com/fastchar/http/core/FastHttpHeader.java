package com.fastchar.http.core;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/10 18:04
 */
public class FastHttpHeader {

    private String name;
    private String value;

    public FastHttpHeader() {
    }

    public FastHttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public FastHttpHeader setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FastHttpHeader setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "FastHttpHeader{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
