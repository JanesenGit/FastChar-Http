package com.fastchar.http.core;

import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastStringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastHttpHelper {


    public static String getFileExtension(String contentType) {
        return FastFileUtils.getExtensionFromContentType(contentType);
    }

    public static String getFileName(String contentDisposition) {
        if (FastStringUtils.isNotEmpty(contentDisposition)) {
            String regStr = "filename=\"(.*)\"";
            Matcher matcher = Pattern.compile(regStr).matcher(contentDisposition);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public static boolean isTextContent(String contentType) {
        if (FastStringUtils.isEmpty(contentType)) {
            return true;
        }
        contentType = contentType.toLowerCase();
        if (contentType.startsWith("text/")) {
            return true;
        }
        if (contentType.contains("application/json")) {
            return true;
        }
        return false;
    }


}
