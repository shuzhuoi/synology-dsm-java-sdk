package io.github.shuzhuoi.synology.http.okhttp3.internal.response;

import okhttp3.Response;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 在调用方关闭下载流时同步关闭 OkHttp 响应，确保连接能够及时回收到连接池。
 */
public final class OkHttpResponseInputStream extends FilterInputStream {

    private final Response response;

    public OkHttpResponseInputStream(InputStream inputStream, Response response) {
        super(inputStream);
        this.response = response;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            response.close();
        }
    }
}
