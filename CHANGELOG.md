# Changelog

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
- 提供 `FileStationBasicSample` 示例
