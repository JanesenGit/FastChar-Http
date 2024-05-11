package com.fastchar.http.core;

import com.fastchar.core.FastJsonWrap;
import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastIOUtils;
import com.fastchar.utils.FastStringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * user-agent随机生成工具
 * @author 沈建（Janesen）
 * @date 2021/8/23 14:44
 */
public class FastHttpUserAgent {

    private static String USER_AGENTS_DESK_TOP_JSON = null;

    private static String USER_AGENTS_MOBILE_TOP_JSON = null;

    /**
     * 随机获取桌面浏览器的标识
     * @return 标识
     */
    public static String getDesktopRandom() {
        try {
            if (FastStringUtils.isEmpty(USER_AGENTS_DESK_TOP_JSON)) {
                URL resource = FastHttpUserAgent.class.getResource("/user-agents-desktop.json");
                if (resource != null) {
                    InputStream input = null;
                    try {
                        input = resource.openStream();
                        USER_AGENTS_DESK_TOP_JSON = FastStringUtils.join(FastFileUtils.readLines(input, StandardCharsets.UTF_8), "");
                    } finally {
                        FastIOUtils.closeQuietly(input);
                    }
                }
            }
            FastJsonWrap jsonWrap = FastJsonWrap.newInstance(USER_AGENTS_DESK_TOP_JSON);
            int length = jsonWrap.getInt("length");
            Random random = new Random();
            int index = random.nextInt(length);
            return jsonWrap.getString("[" + index + "].ug");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 随机获取手机浏览器的标识
     * @return 标识
     */
    public static String getMobileRandom() {
        try {
            if (FastStringUtils.isEmpty(USER_AGENTS_MOBILE_TOP_JSON)) {
                URL resource = FastHttpUserAgent.class.getResource("/user-agents-mobile.json");
                if (resource != null) {
                    InputStream input = null;
                    try {
                        input = resource.openStream();
                        USER_AGENTS_MOBILE_TOP_JSON = FastStringUtils.join(FastFileUtils.readLines(input, StandardCharsets.UTF_8), "");
                    } finally {
                        FastIOUtils.closeQuietly(input);
                    }
                }
            }
            FastJsonWrap jsonWrap = FastJsonWrap.newInstance(USER_AGENTS_MOBILE_TOP_JSON);
            int length = jsonWrap.getInt("length");
            Random random = new Random();
            int index = random.nextInt(length);
            return jsonWrap.getString("[" + index + "].ug");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
