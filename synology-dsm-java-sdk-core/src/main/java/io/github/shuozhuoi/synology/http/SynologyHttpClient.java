package io.github.shuzhuoi.synology.http;

/**
 * SDK 的 HTTP 抽象。
 * <p>
 * core 模块只依赖该接口，具体实现可以是 Hutool、OkHttp、Apache HttpClient 等。
 */
public interface SynologyHttpClient {

    /**
     * 执行一次 HTTP 请求并返回统一响应。
     */
    SynologyHttpResponse execute(SynologyHttpRequest request);
}
