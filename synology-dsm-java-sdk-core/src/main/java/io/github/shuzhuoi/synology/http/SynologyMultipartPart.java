package io.github.shuzhuoi.synology.http;

import java.io.File;

/**
 * multipart/form-data 的单个表单 part。
 */
public class SynologyMultipartPart {

    /**
     * 表单字段名，例如 path、overwrite 或 file。
     */
    private final String name;
    /**
     * 表单字段值。普通字段是字符串，文件字段是 File。
     */
    private final Object value;

    private SynologyMultipartPart(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static SynologyMultipartPart text(String name, String value) {
        return new SynologyMultipartPart(name, value);
    }

    public static SynologyMultipartPart file(String name, File file) {
        return new SynologyMultipartPart(name, file);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
