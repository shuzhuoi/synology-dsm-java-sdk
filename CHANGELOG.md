# Changelog

## Unreleased

- 暂无已发布变更。

## 1.0.0 (2026-07-13)

这是 SDK 的首个稳定版本。公共 API、HTTP/JSON 扩展边界和 Spring Boot 集成方式从本版本开始按语义化版本规则维护。

### 新增

- 新增独立 `synology-dsm-java-sdk-http-okhttp3` 模块，提供 OkHttp3 HTTP 实现和对应 Java 示例。
- 新增 `synology-dsm-java-sdk-spring-boot2-starter` 和 `synology-dsm-java-sdk-spring-boot3-starter`，分别适配 Spring Boot 2.x 和 3.x，并提供可运行示例。
- 新增独立 `synology-dsm-java-sdk-json-jackson` 模块，提供默认 Jackson JSON Codec。
- 新增可选 `synology-dsm-java-sdk-json-fastjson2` 模块，基于 Fastjson2 2.0.61 实现相同 JSON 契约。
- 新增 `SynologyJsonCodec` SPI 和中立模型映射注解，支持 Jackson、Fastjson2 以及用户自定义 JSON 实现。
- 新增可替换的 `SynologySessionStore`，默认提供本地内存 Map 实现；Redis 等外部缓存由使用者自行实现和接入。
- 新增 `SortDirection`、`FileTypeFilter`、`ThumbSize`、`CompressFormat`、`CompressMode`、`CompressLevel` enum 重载，原 String 方法继续保留。

### 架构调整

- core 只保留业务 API、会话管理、HTTP/JSON SPI 和通用模型，不再依赖具体 JSON、HTTP 或 Spring 实现。
- HTTP 实现拆分为 Hutool 和 OkHttp3 平级 adapter；JSON 实现拆分为 Jackson 和 Fastjson2 平级模块。
- Boot 2/3 Starter 默认装配 Jackson Codec；用户提供 `SynologyJsonCodec` Bean 时优先使用用户实现。
- 默认 Session/SID 缓存使用本地 Map，不引入 Redis、Caffeine 或 Spring Cache 依赖。
- 所有 SDK 示例统一收拢到 `synology-dsm-java-sdk-example` 聚合模块，并区分普通 Java、Spring Boot 2 和 Spring Boot 3 示例。
- Maven 编译改用 `release`：Java 8 模块限制为 Java 8 JDK API，Boot 3 模块限制为 Java 17；完整 Reactor 统一使用 JDK 17 构建。
- 新增 `local-build` Maven Profile，本地验证和 IDEA 构建时可跳过 GPG 签名，正式发布时不启用。

### 破坏性变更

- **Breaking:** 普通 Java `SynologyDsmClient.Builder` 必须显式设置 `jsonCodec`。
- **Breaking:** Hutool/OkHttp3 便捷工厂创建方法增加 `SynologyJsonCodec` 参数。
- **Breaking:** `BackgroundTask.params` 从 Jackson `JsonNode` 改为 `Map<String, Object>`，core 公共 API 不再暴露 Jackson 类型。

### 从 0.4.0 升级

1. 普通 Java 项目在 HTTP adapter 之外增加一个 JSON 实现依赖：默认使用 `synology-dsm-java-sdk-json-jackson`，或者改用 `synology-dsm-java-sdk-json-fastjson2`。
2. 为 `SynologyDsmClient.builder()` 增加 `.jsonCodec(new JacksonSynologyJsonCodec())` 或其他 `SynologyJsonCodec` 实现。
3. 为 `HutoolSynologyDsmClientFactory.create(...)` 或 `OkHttp3SynologyDsmClientFactory.create(...)` 增加 JSON Codec 参数。
4. 将 `BackgroundTask.getParams()` 的读取逻辑从 Jackson `JsonNode` 调整为 JDK `Map<String, Object>`。
5. Spring Boot 项目只引入与自身版本对应的 Starter。Starter 默认提供 Jackson Codec；切换 Fastjson2 时排除默认 Jackson 模块并显式注册 `Fastjson2SynologyJsonCodec`。

`fastjson2-extension-spring5` 和 `fastjson2-extension-spring6` 不属于 SDK 的必需依赖。只有业务应用希望把 Fastjson2 设置为 Spring MVC 全局 JSON 消息转换器时，才需要根据 Spring Boot 主版本自行引入对应扩展。

## 0.4.0 (2026-07-10)

- 新增 `SYNO.FileStation.Extract`：支持 start / status / stop / list / wait
- 新增 `SYNO.FileStation.Compress`：支持 start / status / stop / wait
- `FileStationClient` 新增 `extract()` / `compress()` 入口
- 新增 `ArchiveItem`、`ExtractListResponse`、`ExtractStatusResponse`、`CompressStatusResponse` 等强类型响应实体
- example 模块新增归档能力示例 `FileStationArchiveExample` 与 `filestation-archive.example.yaml`
- Extract / Compress 和归档示例已在真实 DSM 环境验证通过

## 0.3.0 (2026-07-10)

- 新增 `SYNO.FileStation.Sharing`：支持分享链接 getinfo / list / create / delete / clear_invalid / edit
- 新增 `SYNO.FileStation.Favorite`：支持收藏夹 list / add / delete / clear_broken / edit / replace_all
- 新增 `SYNO.FileStation.Thumb`：支持获取文件缩略图二进制流
- 新增 `SYNO.FileStation.VirtualFolder`：支持列出 CIFS / NFS / ISO 等虚拟目录挂载点
- `FileStationClient` 新增 `sharing()` / `favorite()` / `thumb()` / `virtualFolder()` 入口
- example 模块新增资源能力示例 `FileStationResourceExample` 与 `filestation-resource.example.yaml`
- 修复 Hutool HTTP 适配层未将 `SYNO.FileStation.Thumb` 识别为二进制响应，导致缩略图响应流为空的问题

## 0.2.0 (2026-07-08)

- 新增 `SYNO.FileStation.Search`：按文件名/大小/时间/类型/归属等条件搜索文件，支持 start / list / stop / clean 和轮询等待
- 新增 `SYNO.FileStation.DirSize`：统计目录累计大小，支持 start / status / stop 和轮询等待
- 新增 `SYNO.FileStation.BackgroundTask`：列出与清理 copy / move / delete / compress / extract 等后台任务
- 新增通用任务轮询抽象 `SynologyTaskPoller` 与 `TaskPollingOptions`，统一 delete / search / dirSize 等任务型接口的等待逻辑
- `autoRefreshSession` 正式生效：认证请求遇到会话失效错误码（106 / 107 / 119）时自动重新登录并重试一次
- 重构 `FileStationFileClient.delete` 复用 `SynologyTaskPoller`，移除硬编码轮询常量

## 0.1.0 (2026-07-08)

- 首个版本，提供 Synology DSM File Station 高频文件操作的 Java SDK
- Maven 多模块结构：core / http-hutool / example
- 支持 `SYNO.API.Info`、`SYNO.API.Auth`
- 支持 `SYNO.FileStation.Info`、`List`、`Upload`、`Download`、`CreateFolder`、`Rename`、`Delete`、`CopyMove`
- 支持 `SYNO.FileStation.MD5` 任务（start / status / stop）
- 会话自动管理：自动登录、SID 复用、登出
- HTTP 抽象与 Hutool 默认实现
- 统一异常体系：`SynologyDsmException` / `SynologyApiException` / `SynologyAuthException` / `SynologyHttpException`
- 强类型 Request / Response，所有方法返回实体类
- 提供 `FileStationBasicExample` 示例
