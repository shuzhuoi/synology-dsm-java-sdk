# Synology DSM Java SDK

[![License: Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java: 8+](https://img.shields.io/badge/java-8%2B-orange.svg)](https://www.java.com/)
[![Version: 0.4.0](https://img.shields.io/badge/version-0.4.0-green.svg)](CHANGELOG.md)

Synology DSM Java SDK 提供对 [Synology DSM WebAPI](https://global.download.synology.com/download/Document/Software/DeveloperGuide/OS/Dynamicsite/All/enu/Synology_DiskStation_Administration_Web_API_Guide.pdf) 的 Java 调用能力，当前版本聚焦 **File Station** 文件操作、任务型接口和分享/收藏等资源能力。

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-core</artifactId>
    <version>0.4.0</version>
</dependency>

<!-- 默认 HTTP 实现，基于 Hutool -->
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-hutool</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Requirements

- Java 8 及以上
- Maven 3.5+
- 一台可访问的 Synology NAS（已开启 WebAPI，DSM 6.x / 7.x 均可）

## Usage

```java
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;

SynologyDsmConfig config = SynologyDsmConfig.builder()
        .baseUrl("https://nas.example.com:5001")
        .account("your-account")
        .password("your-password")
        .build();

// HutoolSynologyDsmClientFactory 内部已组装 core + hutool http 实现
SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config);

// 首次调用时自动登录，后续复用同一个 SID
ListFilesResponse response = client.fileStation().list().files(
        ListFilesRequest.builder("/home")
                .limit(50)
                .build()
);

for (SynologyFile file : response.getFiles()) {
    System.out.println(file.getPath());
}

// 结束时主动登出，释放 DSM 会话
client.session().logout();
```

更多操作示例见 `synology-dsm-java-sdk-example` 模块下代码，覆盖信息查询、列表、创建目录、上传、下载、重命名、删除、搜索、目录大小、后台任务、分享、收藏、缩略图、虚拟目录、压缩和解压等完整链路。

运行示例前，根据要验证的能力复制对应配置文件并填写真实 DSM 信息：

- 基础文件操作： `FileStationBasicExample#main`。
- 任务型接口： `FileStationAdvancedExample#main`。
- 分享、收藏、缩略图、虚拟目录： `FileStationResourceExample#main`。
- 压缩和解压：`FileStationArchiveExample#main`。

## Client configuration

通过 `SynologyDsmConfig.builder()` 配置：

```java
SynologyDsmConfig config = SynologyDsmConfig.builder()
        .baseUrl("https://nas.example.com:5001")
        .account("your-account")
        .password("your-password")
        .connectTimeoutMillis(10000)
        .readTimeoutMillis(60000)
        .build();
```

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `baseUrl` | 必填 | DSM 基础地址，例如 `https://nas.example.com:5001`，不要包含 `/webapi` |
| `account` | 必填 | DSM 登录账号 |
| `password` | 必填 | DSM 登录密码，不会输出到日志 |
| `sessionName` | `FileStation` | WebAPI 会话名，File Station 固定为 `FileStation` |
| `connectTimeoutMillis` | `10000` | HTTP 连接超时（毫秒） |
| `readTimeoutMillis` | `60000` | HTTP 读取超时（毫秒），大文件上传/下载可调大 |
| `autoLogin` | `true` | 没有 SID 时是否自动登录 |
| `autoRefreshSession` | `true` | 会话失效（错误码 106/107/119）时自动重新登录并重试一次 |

`baseUrl` 末尾的 `/` 会被自动去除。SDK 内部通过 `baseUrl + /webapi/entry.cgi` 拼接请求地址。密码、SID、Cookie 等敏感信息不会出现在日志中。

## API 入口

SDK 采用三层入口：

```
SynologyDsmClient
├── apiInfo()                  // SYNO.API.Info
├── auth()                     // SYNO.API.Auth（显式登录/登出）
├── session()                  // 会话管理（自动登录、复用 SID、登出）
└── fileStation()              // File Station 聚合入口
    ├── info()                 // SYNO.FileStation.Info
    ├── list()                 // SYNO.FileStation.List
    ├── upload()               // SYNO.FileStation.Upload
    ├── download()             // SYNO.FileStation.Download
    ├── file()                 // 创建/重命名/删除/复制/移动
    ├── task()                 // 任务型接口（MD5 等）
    ├── search()               // SYNO.FileStation.Search
    ├── dirSize()              // SYNO.FileStation.DirSize
    ├── backgroundTask()       // SYNO.FileStation.BackgroundTask
    ├── sharing()              // SYNO.FileStation.Sharing
    ├── favorite()             // SYNO.FileStation.Favorite
    ├── thumb()                // SYNO.FileStation.Thumb
    ├── virtualFolder()        // SYNO.FileStation.VirtualFolder
    ├── compress()             // SYNO.FileStation.Compress
    └── extract()              // SYNO.FileStation.Extract
```

## License

[Apache License 2.0](LICENSE)
