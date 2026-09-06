package io.github.shuzhuoi.synology.docker.log;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerContainerLogListResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Docker.Container.Log 客户端。
 * <p>
 * 提供容器日志的分页查询，支持时间范围、关键字、级别过滤。
 * 注意与 SYNO.Docker.Log（Docker 守护进程全局日志）是不同的 API。
 */
public class DockerContainerLogClient {

    private final SynologyApiExecutor executor;

    public DockerContainerLogClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询指定容器的日志。
     *
     * @param request 查询请求（name 必填）
     * @return 日志列表与总数
     */
    public DockerContainerLogListResponse get(DockerContainerLogRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // Container Manager 契约声明 requestFormat=JSON：
        // 字符串类过滤参数（name/from/to/keyword/level/sort_dir）以 JSON 字符串形式传输（带引号），
        // 数值类分页参数（offset/limit）传普通字符串。
        parameters.put("name", SynologyParameterEncoder.quoted(request.getName()));
        parameters.put("from", SynologyParameterEncoder.quoted(request.getFrom()));
        parameters.put("to", SynologyParameterEncoder.quoted(request.getTo()));
        parameters.put("keyword", SynologyParameterEncoder.quoted(request.getKeyword()));
        parameters.put("level", SynologyParameterEncoder.quoted(request.getLevel()));
        parameters.put("sort_dir", SynologyParameterEncoder.quoted(request.getSortDir()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_LOG_API,
                DockerApi.CONTAINER_LOG_VERSION,
                "get",
                parameters,
                DockerContainerLogListResponse.class
        );
    }
}
