# Changelog

## Unreleased

- 新增独立 `synology-dsm-java-sdk-json-jackson` 模块，core 不再依赖或公开 Jackson 类型
- 新增 `SynologyJsonCodec` SPI 和中立模型映射注解，为后续 Fastjson2 等实现保留扩展边界
- Boot 2/3 Starter 默认装配 Jackson Codec，用户提供 `SynologyJsonCodec` Bean 时优先使用用户实现
- 新增 `SortDirection`、`FileTypeFilter`、`ThumbSize`、`CompressFormat`、`CompressMode`、`CompressLevel` enum 重载，保留原 String 方法
- **Breaking:** 普通 Java `SynologyDsmClient.Builder` 必须显式设置 `jsonCodec`
- **Breaking:** Hutool/OkHttp3 便捷工厂创建方法增加 `SynologyJsonCodec` 参数
- **Breaking:** `BackgroundTask.params` 从 Jackson `JsonNode` 改为 `Map<String, Object>`

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
