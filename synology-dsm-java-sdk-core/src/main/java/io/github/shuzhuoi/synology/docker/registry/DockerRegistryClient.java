package io.github.shuzhuoi.synology.docker.registry;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerRegistryListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerRegistrySearchResponse;
import io.github.shuzhuoi.synology.docker.model.DockerRegistryTagListResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Docker.Registry 客户端。
 * <p>
 * 提供注册表配置查询与维护（对应 DSM 容器管理器「注册表 → 设置」页面：
 * Docker Hub / 自定义 registry、认证账号、信任自签名证书、切换当前使用的注册表）
 * 以及镜像搜索（search）和镜像标签查询（tags，注意使用 v2 版本）。
 * <p>
 * 注意：get / search / create / set / using / delete 使用 v1 版本，
 * tags 是 v2 的分页增强版，两者版本常量不同。
 * 参数编码约定（契约声明 requestFormat=JSON）：
 * 字符串参数以 JSON 字符串形式传输（带引号）；
 * offset / limit 数值参数与 enable_trust_SSC 布尔参数传普通字符串。
 * 常见错误码：105 权限不足、400 无效参数。
 */
public class DockerRegistryClient {

    private final SynologyApiExecutor executor;

    public DockerRegistryClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询注册表配置列表（含当前使用的注册表名）。
     *
     * @return 注册表配置列表
     */
    public DockerRegistryListResponse get() {
        // get 无业务参数。
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_VERSION,
                "get",
                new LinkedHashMap<String, String>(),
                DockerRegistryListResponse.class
        );
    }

    /**
     * 在当前使用的注册表中搜索镜像（默认前 50 条）。
     *
     * @param keyword 搜索关键词，例如 caddy
     * @return 搜索结果（含总数与分页信息）
     */
    public DockerRegistrySearchResponse search(String keyword) {
        return search(keyword, null, null);
    }

    /**
     * 在当前使用的注册表中分页搜索镜像。
     *
     * @param keyword 搜索关键词
     * @param offset  起始序号，null 表示由 DSM 决定
     * @param limit   返回数量上限，null 表示由 DSM 决定
     * @return 搜索结果（含总数与分页信息）
     */
    public DockerRegistrySearchResponse search(String keyword, Integer offset, Integer limit) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // q 为搜索关键词（字符串，带引号）；offset / limit 为普通整数字符串。
        parameters.put("q", SynologyParameterEncoder.quoted(keyword));
        parameters.put("offset", SynologyParameterEncoder.integerValue(offset));
        parameters.put("limit", SynologyParameterEncoder.integerValue(limit));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_VERSION,
                "search",
                parameters,
                DockerRegistrySearchResponse.class
        );
    }

    /**
     * 查询镜像的标签列表（默认前 50 个）。
     * <p>
     * 注意 tags 使用 Registry v2 版本（分页增强版）。
     *
     * @param repository 仓库名称，例如 postgres
     * @return 标签列表
     */
    public DockerRegistryTagListResponse tags(String repository) {
        return tags(repository, null, null);
    }

    /**
     * 分页查询镜像的标签列表。
     * <p>
     * 注意 tags 使用 Registry v2 版本（分页增强版）。
     *
     * @param repository 仓库名称，例如 postgres
     * @param offset     起始序号，null 表示由 DSM 决定
     * @param limit      返回数量上限，null 表示由 DSM 决定
     * @return 标签列表
     */
    public DockerRegistryTagListResponse tags(String repository, Integer offset, Integer limit) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // repository 为字符串（带引号）；offset / limit 为普通整数字符串。
        parameters.put("repository", SynologyParameterEncoder.quoted(repository));
        parameters.put("offset", SynologyParameterEncoder.integerValue(offset));
        parameters.put("limit", SynologyParameterEncoder.integerValue(limit));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_TAGS_VERSION,
                "tags",
                parameters,
                DockerRegistryTagListResponse.class
        );
    }

    /**
     * 新增注册表配置。
     *
     * @param request 新增请求（name / url 必填）
     * @return 操作结果
     */
    public SynologyOperationResponse create(DockerRegistryUpsertRequest request) {
        return upsert("create", request);
    }

    /**
     * 修改注册表配置（按 name 定位）。
     *
     * @param request 修改请求（name / url 必填，name 定位目标条目）
     * @return 操作结果
     */
    public SynologyOperationResponse set(DockerRegistryUpsertRequest request) {
        return upsert("set", request);
    }

    /**
     * 切换当前使用的注册表（影响后续镜像拉取与搜索的来源）。
     *
     * @param name 注册表显示名
     * @return 操作结果
     */
    public SynologyOperationResponse using(String name) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // name 以 JSON 字符串形式传输（带引号）。
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_VERSION,
                "using",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 删除注册表配置。
     * <p>
     * DSM 内置条目（如 Docker Hub，syno=true）不可删除。
     *
     * @param name 注册表显示名
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String name) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // name 以 JSON 字符串形式传输（带引号）。
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_VERSION,
                "delete",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 执行 create / set 共用的参数组装与请求发送。
     */
    private SynologyOperationResponse upsert(String method, DockerRegistryUpsertRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // 字符串参数以 JSON 字符串形式传输（带引号），enable_trust_SSC 为普通小写布尔字符串。
        parameters.put("name", SynologyParameterEncoder.quoted(request.getName()));
        parameters.put("url", SynologyParameterEncoder.quoted(request.getUrl()));
        parameters.put("enable_trust_SSC", SynologyParameterEncoder.booleanValue(request.getEnableTrustSsc()));
        parameters.put("username", SynologyParameterEncoder.quoted(request.getUsername()));
        parameters.put("password", SynologyParameterEncoder.quoted(request.getPassword()));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.REGISTRY_API,
                DockerApi.REGISTRY_VERSION,
                method,
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
