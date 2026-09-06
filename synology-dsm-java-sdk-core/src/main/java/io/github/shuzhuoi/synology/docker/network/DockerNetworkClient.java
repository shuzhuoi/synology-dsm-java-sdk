package io.github.shuzhuoi.synology.docker.network;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerNetwork;
import io.github.shuzhuoi.synology.docker.model.DockerNetworkListResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SYNO.Docker.Network 客户端。
 * <p>
 * 提供 Docker 网络的列表、创建与删除。
 * 注意：DSM 未提供按名称查询网络的可用契约，这里与 Project 一致采用
 * list 后本地筛选的方式；connect / disconnect / list_container / set
 * 四个方法暂无公开第三方实现佐证，待真机抓包确认契约后再补充。
 * <p>
 * 参数编码约定（契约声明 requestFormat=JSON）：
 * 字符串参数以 JSON 字符串形式传输（带引号）；enable_ipv6 布尔参数
 * 传普通小写字符串；remove 的 networks 参数为 JSON 数组字符串
 * （元素是 list 返回的完整网络对象）。
 * 常见错误码：105 权限不足、400 无效参数。
 */
public class DockerNetworkClient {

    private final SynologyApiExecutor executor;

    public DockerNetworkClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询全部 Docker 网络。
     *
     * @return 网络列表（注意响应字段名为 network 单数）
     */
    public DockerNetworkListResponse list() {
        // list 无业务参数。
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.NETWORK_API,
                DockerApi.NETWORK_VERSION,
                "list",
                new LinkedHashMap<String, String>(),
                DockerNetworkListResponse.class
        );
    }

    /**
     * 按名称查找网络。
     * <p>
     * DSM 未提供按名称查询的接口，这里基于 {@link #list()} 的结果本地筛选。
     *
     * @param name 网络名称
     * @return 匹配的网络，未找到时返回 null
     */
    public DockerNetwork findByName(String name) {
        DockerNetworkListResponse response = list();
        if (response == null || response.getNetwork() == null || name == null) {
            return null;
        }
        for (DockerNetwork network : response.getNetwork()) {
            if (network != null && name.equals(network.getName())) {
                return network;
            }
        }
        return null;
    }

    /**
     * 创建 Docker 网络。
     *
     * @param request 创建请求（name 必填）
     * @return 操作结果
     */
    public SynologyOperationResponse create(DockerNetworkCreateRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // 字符串参数以 JSON 字符串形式传输（带引号），enable_ipv6 为普通小写布尔字符串。
        parameters.put("name", SynologyParameterEncoder.quoted(request.getName()));
        parameters.put("driver", SynologyParameterEncoder.quoted(request.getDriver()));
        parameters.put("enable_ipv6", SynologyParameterEncoder.booleanValue(request.getEnableIpv6()));
        parameters.put("subnet", SynologyParameterEncoder.quoted(request.getSubnet()));
        parameters.put("gateway", SynologyParameterEncoder.quoted(request.getGateway()));
        // 注意参数名 iprange 无下划线，与响应字段名一致。
        parameters.put("iprange", SynologyParameterEncoder.quoted(request.getIprange()));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.NETWORK_API,
                DockerApi.NETWORK_VERSION,
                "create",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 按名称删除网络。
     * <p>
     * remove 接口要求传回完整网络对象（networks JSON 数组参数），
     * 这里先 list 定位再删除；网络仍被容器使用时 DSM 会拒绝删除。
     *
     * @param name 网络名称
     * @return 操作结果；未找到同名网络时抛出 IllegalArgumentException
     */
    public SynologyOperationResponse remove(String name) {
        DockerNetwork network = findByName(name);
        if (network == null) {
            throw new IllegalArgumentException("未找到名称为 " + name + " 的 Docker 网络");
        }
        return remove(network);
    }

    /**
     * 删除网络（传 list 返回的完整网络对象）。
     *
     * @param network list 返回的网络对象
     * @return 操作结果
     */
    public SynologyOperationResponse remove(DockerNetwork network) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // networks 参数为 JSON 数组字符串，元素是完整网络对象（与 list 响应结构一致）。
        parameters.put("networks", toNetworksJson(network));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.NETWORK_API,
                DockerApi.NETWORK_VERSION,
                "remove",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 把网络对象序列化为 remove 接口需要的 networks JSON 数组字符串。
     * <p>
     * core 模块不依赖具体 JSON 库，且网络对象结构固定，这里手工构建 JSON
     * （null 值输出 JSON null），字段顺序与 list 响应保持一致。
     */
    private static String toNetworksJson(DockerNetwork network) {
        StringBuilder builder = new StringBuilder("[");
        builder.append(toNetworkJson(network));
        builder.append("]");
        return builder.toString();
    }

    private static String toNetworkJson(DockerNetwork network) {
        StringBuilder builder = new StringBuilder("{");
        builder.append("\"containers\":").append(jsonStringList(network.getContainers()));
        builder.append(",\"driver\":").append(jsonString(network.getDriver()));
        builder.append(",\"enable_ipv6\":").append(jsonBoolean(network.getEnableIpv6()));
        builder.append(",\"gateway\":").append(jsonString(network.getGateway()));
        builder.append(",\"id\":").append(jsonString(network.getId()));
        builder.append(",\"iprange\":").append(jsonString(network.getIprange()));
        builder.append(",\"name\":").append(jsonString(network.getName()));
        builder.append(",\"subnet\":").append(jsonString(network.getSubnet()));
        builder.append("}");
        return builder.toString();
    }

    private static String jsonString(String value) {
        return value == null ? "null" : SynologyParameterEncoder.quoted(value);
    }

    private static String jsonBoolean(Boolean value) {
        return value == null ? "null" : String.valueOf(value);
    }

    private static String jsonStringList(List<String> values) {
        if (values == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(jsonString(values.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }
}
