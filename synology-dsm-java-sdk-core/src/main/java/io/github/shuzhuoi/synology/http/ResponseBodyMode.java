package io.github.shuzhuoi.synology.http;

/**
 * HTTP 响应体读取模式。
 * <p>
 * core 通过该枚举明确告诉具体 HTTP 适配器：响应体应该按 JSON 文本读取，还是保留为原始流。
 */
public enum ResponseBodyMode {

    /**
     * 普通 DSM JSON 响应，适配器应读取为字符串。
     */
    TEXT,

    /**
     * 下载、缩略图等二进制响应，适配器应返回原始 InputStream。
     */
    STREAM
}
