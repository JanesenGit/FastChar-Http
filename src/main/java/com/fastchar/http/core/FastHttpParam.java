package com.fastchar.http.core;

import com.fastchar.utils.FastStringUtils;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/10 18:04
 */
public class FastHttpParam {

    private String name;
    private Object value;

    private String content;

    public String getName() {
        return name;
    }

    public FastHttpParam setName(String name) {
        this.name = name;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public FastHttpParam setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getContent() {
        if (FastStringUtils.isEmpty(content)) {
            return name + "=" + value + ";";
        }
        return content;
    }

    public FastHttpParam setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "FastHttpParam{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
