# Synology DSM Java SDK

[![License: Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java: 8 / 17](https://img.shields.io/badge/java-8%20%2F%2017-orange.svg)](https://www.java.com/)
[![Version: 0.4.0](https://img.shields.io/badge/version-0.4.0-green.svg)](CHANGELOG.md)

Synology DSM Java SDK 提供对 [Synology DSM WebAPI](https://global.download.synology.com/download/Document/Software/DeveloperGuide/OS/Dynamicsite/All/enu/Synology_DiskStation_Administration_Web_API_Guide.pdf) 的 Java 调用能力，完整覆盖 **File Station** 文件操作、任务型接口、分享、收藏、缩略图、虚拟目录、压缩和解压等能力。

SDK 将业务 API、HTTP 实现和 Spring Boot 集成拆分为独立模块。普通 Java、Spring Boot 2 和 Spring Boot 3 项目可以按需选择，不需要把 Spring、Hutool 或 OkHttp3 引入 core。

## 选择模块

| 使用场景 | 引入模块 | Java 要求 | HTTP 实现 |
| --- | --- | --- | --- |
| 普通 Java，默认方案 | `http-hutool` + `json-jackson` | Java 8+ | Hutool |
| 普通 Java，使用 OkHttp3 | `http-okhttp3` + `json-jackson` | Java 8+ | OkHttp 3.x |
| JSON 默认实现 | `synology-dsm-java-sdk-json-jackson` | Java 8+ | Jackson |
| 自定义 HTTP 实现 | `synology-dsm-java-sdk-core` | Java 8+ | 用户实现 `SynologyHttpClient` |
| Spring Boot 2 | `synology-dsm-java-sdk-spring-boot2-starter` | Java 8+ | 默认 Hutool，可选 OkHttp3 |
| Spring Boot 3 | `synology-dsm-java-sdk-spring-boot3-starter` | Java 17+ | 默认 Hutool，可选 OkHttp3 |

> [!NOTE]
> core、两个 HTTP adapter 和 Boot 2 Starter 以 Java 8 为目标；Boot 3 Starter 以 Java 17 为目标。构建整个源码仓库请使用 JDK 17。

## 安装

### 普通 Java：Hutool

Hutool adapter 已传递依赖 core，但 JSON 实现需要单独引入：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-hutool</artifactId>
    <version>0.4.0</version>
</dependency>
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-jackson</artifactId>
    <version>0.4.0</version>
</dependency>
```

### 普通 Java：OkHttp3

OkHttp3 adapter 同样已传递依赖 core，不需要同时引入 Hutool；同时引入 JSON 实现：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-okhttp3</artifactId>
    <version>0.4.0</version>
</dependency>
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-jackson</artifactId>
    <version>0.4.0</version>
</dependency>
```

### 自定义 HTTP 实现

只使用业务模型和 API 执行能力时，可以仅引入 core，并实现 `SynologyHttpClient`：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-core</artifactId>
    <version>0.4.0</version>
</dependency>
```

只使用 core 时，还必须自行提供一个 `SynologyJsonCodec` 实现。SDK 不通过反射或 `ServiceLoader` 静默选择 JSON 库。

### JSON 实现选择

默认实现是独立的 `synology-dsm-java-sdk-json-jackson` 模块。它不会修改 Spring Boot 全局 `ObjectMapper`，也不会让 core 编译依赖 Jackson。普通 Java 项目需要显式创建 Codec：

```java
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;

JacksonSynologyJsonCodec jsonCodec = new JacksonSynologyJsonCodec();
```

未来可以增加与 Jackson 平级的 `synology-dsm-java-sdk-json-fastjson2`。使用其他实现时，保持同一个 `SynologyJsonCodec` 接口，并在 Builder 中明确传入唯一实现。

## 环境要求

- 普通 Java、core、HTTP adapter、Spring Boot 2 Starter：Java 8 及以上
- Spring Boot 3 Starter：Java 17 及以上
- Spring Boot 2：面向 Spring Boot 2.7.x，当前基线为 2.7.18
- Spring Boot 3：面向 Spring Boot 3.x，当前基线为 3.3.13
- Maven 3.6.3 及以上
- 一台可访问的 Synology NAS（已开启 WebAPI，DSM 6.x / 7.x 均可）

## 普通 Java 快速开始

```java
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;

SynologyDsmConfig config = SynologyDsmConfig.builder()
        .baseUrl("https://nas.example.com:5001")
        .account("your-account")
        .password("your-password")
        .build();

// JSON Codec 由调用方明确选择，避免 core 与具体 JSON 库耦合
SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(
        config,
        new JacksonSynologyJsonCodec()
);

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

使用 OkHttp3 时，core 的公开调用方式不变，只需要替换客户端工厂：

```java
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;

SynologyDsmClient client = OkHttp3SynologyDsmClientFactory.create(
        config,
        new JacksonSynologyJsonCodec()
);
```

需要配置代理、TLS 或连接池时，可以自行创建 `OkHttpClient` 并注入 `OkHttp3SynologyHttpClient`：

```java
SynologyDsmClient client = SynologyDsmClient.builder()
        .config(config)
        .httpClient(new OkHttp3SynologyHttpClient(customOkHttpClient))
        .jsonCodec(new JacksonSynologyJsonCodec())
        .build();
```

更多操作示例见 [`synology-dsm-java-sdk-java-example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example)，覆盖信息查询、列表、创建目录、上传、下载、重命名、删除、搜索、目录大小、后台任务、分享、收藏、缩略图、虚拟目录、压缩和解压等完整链路。

稳定参数可以使用类型安全枚举，例如 `SortDirection.ASC`、`FileTypeFilter.FILE`、`ThumbSize.SMALL` 和 `CompressFormat.ZIP`。原 String Builder 方法继续保留，用于兼容 DSM 后续增加的扩展值。

`BackgroundTask.getParams()` 返回 `Map<String, Object>`，不再暴露 Jackson `JsonNode`；嵌套对象、列表、数字、布尔值和 null 会按 JDK 集合类型保留。

## Spring Boot Starter

项目提供 Boot 2 和 Boot 3 两个独立 Starter。二者使用同一个 `synology.dsm` 配置协议和同一个 `SynologyDsmClient` 公共 API，但自动配置注册方式、Spring Boot 依赖和 Java 目标版本彼此隔离。

> [!TIP]
> Starter 默认已经包含 core、Hutool adapter 和 Jackson JSON Codec。首次接入只需要引入对应 Starter，不需要重复声明这些依赖。

> [!CAUTION]
> 一个应用只引入与自身 Spring Boot 主版本对应的 Starter，不要同时引入 Boot 2 和 Boot 3 Starter。

### Spring Boot 2 完整教程

适用于 Spring Boot 2.7.x，最低使用 Java 8。项目当前以 Spring Boot 2.7.18 作为构建和验证基线。

#### 第一步：引入 Starter

在 Spring Boot 2 项目的 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-spring-boot2-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

默认场景不需要额外引入 `synology-dsm-java-sdk-core` 或 `synology-dsm-java-sdk-http-hutool`。

#### 第二步：配置 DSM 连接

在 `src/main/resources/application.yaml` 中加入：

```yaml
synology:
  dsm:
    base-url: ${SYNOLOGY_DSM_BASE_URL}
    account: ${SYNOLOGY_DSM_ACCOUNT}
    password: ${SYNOLOGY_DSM_PASSWORD}
    session-name: FileStation
    http-adapter: hutool
    auto-login: true
    auto-refresh-session: true
```

`base-url` 只填写 DSM 地址和端口，不要附加 `/webapi`。建议通过环境变量提供账号和密码，不要把真实凭据提交到仓库。

#### 第三步：设置环境变量

PowerShell：

```powershell
$env:SYNOLOGY_DSM_BASE_URL = "https://nas.example.com:5001"
$env:SYNOLOGY_DSM_ACCOUNT = "your-account"
$env:SYNOLOGY_DSM_PASSWORD = "your-password"
```

Linux 或 macOS：

```bash
export SYNOLOGY_DSM_BASE_URL="https://nas.example.com:5001"
export SYNOLOGY_DSM_ACCOUNT="your-account"
export SYNOLOGY_DSM_PASSWORD="your-password"
```

#### 第四步：注入并调用客户端

Starter 会创建单例 `SynologyDsmClient` Bean。业务类通过构造器注入后即可调用 File Station API：

```java
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class FileStationRunner implements ApplicationRunner {

    private final SynologyDsmClient client;

    public FileStationRunner(SynologyDsmClient client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        ListFilesResponse response = client.fileStation().list().files(
                ListFilesRequest.builder("/home")
                        .limit(50)
                        .build()
        );
        for (SynologyFile file : response.getFiles()) {
            System.out.println(file.getPath());
        }
    }
}
```

第一次调用需要认证的 API 时，客户端自动登录；后续调用复用缓存的 SID。

#### 第五步：启动应用

在你的 Spring Boot 应用根目录执行：

```bash
mvn spring-boot:run
```

应用启动后会执行 `FileStationRunner`。如果 `/home` 不是当前 DSM 账号可访问的目录，请替换为该账号具有读取权限的共享目录。

### Spring Boot 3 完整教程

适用于 Spring Boot 3.x，必须使用 Java 17 或更高版本。项目当前以 Spring Boot 3.3.13 作为构建和验证基线。

#### 第一步：引入 Starter

在 Spring Boot 3 项目的 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-spring-boot3-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

默认场景不需要额外引入 `synology-dsm-java-sdk-core` 或 `synology-dsm-java-sdk-http-hutool`。

#### 第二步：配置 DSM 连接

在 `src/main/resources/application.yaml` 中加入：

```yaml
synology:
  dsm:
    base-url: ${SYNOLOGY_DSM_BASE_URL}
    account: ${SYNOLOGY_DSM_ACCOUNT}
    password: ${SYNOLOGY_DSM_PASSWORD}
    session-name: FileStation
    http-adapter: hutool
    auto-login: true
    auto-refresh-session: true
```

`base-url` 只填写 DSM 地址和端口，不要附加 `/webapi`。建议通过环境变量提供账号和密码，不要把真实凭据提交到仓库。

#### 第三步：设置环境变量

PowerShell：

```powershell
$env:SYNOLOGY_DSM_BASE_URL = "https://nas.example.com:5001"
$env:SYNOLOGY_DSM_ACCOUNT = "your-account"
$env:SYNOLOGY_DSM_PASSWORD = "your-password"
```

Linux 或 macOS：

```bash
export SYNOLOGY_DSM_BASE_URL="https://nas.example.com:5001"
export SYNOLOGY_DSM_ACCOUNT="your-account"
export SYNOLOGY_DSM_PASSWORD="your-password"
```

#### 第四步：注入并调用客户端

Boot 3 Starter 暴露的业务客户端仍然是 core 的 `SynologyDsmClient`，使用方式与 Boot 2 一致：

```java
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class FileStationRunner implements ApplicationRunner {

    private final SynologyDsmClient client;

    public FileStationRunner(SynologyDsmClient client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        ListFilesResponse response = client.fileStation().list().files(
                ListFilesRequest.builder("/home")
                        .limit(50)
                        .build()
        );
        for (SynologyFile file : response.getFiles()) {
            System.out.println(file.getPath());
        }
    }
}
```

第一次调用需要认证的 API 时，客户端自动登录；后续调用复用缓存的 SID。

#### 第五步：启动应用

确认当前终端使用 JDK 17 或更高版本，然后在应用根目录执行：

```bash
java -version
mvn spring-boot:run
```

应用启动后会执行 `FileStationRunner`。如果 `/home` 不可访问，请替换为当前 DSM 账号具有读取权限的共享目录。

### Starter 配置参考

Boot 2 和 Boot 3 使用完全相同的配置项：

| 配置项 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `synology.dsm.enabled` | 否 | `true` | 是否启用 Starter 自动配置 |
| `synology.dsm.base-url` | 是 | 无 | DSM 地址，例如 `https://nas.example.com:5001`，不要包含 `/webapi` |
| `synology.dsm.account` | 是 | 无 | DSM 登录账号，建议使用最小权限账号 |
| `synology.dsm.password` | 是 | 无 | DSM 登录密码，不会由 SDK 输出到日志 |
| `synology.dsm.session-name` | 否 | `FileStation` | DSM WebAPI 会话名称 |
| `synology.dsm.connect-timeout-millis` | 否 | `10000` | HTTP 连接超时，单位毫秒 |
| `synology.dsm.read-timeout-millis` | 否 | `60000` | HTTP 读取超时，上传或下载大文件时可适当调大 |
| `synology.dsm.auto-login` | 否 | `true` | 没有可用 SID 时是否自动登录 |
| `synology.dsm.auto-refresh-session` | 否 | `true` | SID 失效时是否重新登录并重试一次 |
| `synology.dsm.http-adapter` | 否 | `hutool` | HTTP 实现，可选 `hutool` 或 `okhttp3` |

表中的必填项针对 Starter 启用且由 Starter 创建默认客户端的场景。设置 `enabled=false` 或完全提供自定义客户端时，不再由该配置协议创建默认客户端。

完整配置示例：

```yaml
synology:
  dsm:
    enabled: true
    base-url: ${SYNOLOGY_DSM_BASE_URL}
    account: ${SYNOLOGY_DSM_ACCOUNT}
    password: ${SYNOLOGY_DSM_PASSWORD}
    session-name: FileStation
    connect-timeout-millis: 10000
    read-timeout-millis: 60000
    auto-login: true
    auto-refresh-session: true
    http-adapter: hutool
```

### 切换到 OkHttp3

Starter 中的 OkHttp3 adapter 是可选依赖。使用时先增加：

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-okhttp3</artifactId>
    <version>0.4.0</version>
</dependency>
```

然后修改配置：

```yaml
synology:
  dsm:
    http-adapter: okhttp3
```

需要配置代理、TLS、拦截器或连接池时，可以提供自己的 `SynologyHttpClient` Bean：

```java
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyHttpClient;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynologyHttpConfiguration {

    @Bean
    public SynologyHttpClient synologyHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 在这里配置代理、TLS、拦截器或连接池。
                .build();
        return new OkHttp3SynologyHttpClient(okHttpClient);
    }
}
```

检测到用户提供的 `SynologyHttpClient` 后，Starter 不会再创建默认 Hutool 或 OkHttp3 adapter。

### 替换 JSON Codec

Starter 默认创建 `JacksonSynologyJsonCodec`。如果业务项目实现了其他 Codec，例如未来的 Fastjson2 Codec，只需要注册唯一的 `SynologyJsonCodec` Bean：

```java
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynologyJsonConfiguration {

    @Bean
    public SynologyJsonCodec synologyJsonCodec() {
        // 替换为业务项目引入的 JSON 实现。
        return new YourSynologyJsonCodec();
    }
}
```

Starter 会优先使用用户提供的 Codec，不会同时静默选择多个实现，也不会注册或修改 Spring Boot 的全局 `ObjectMapper`。若业务需要自定义 Jackson 行为，可以创建 `JacksonSynologyJsonCodec(objectMapper)`；Codec 内部会复制该对象后再增加 SDK 映射规则。

仓库中的 Boot 2/3 example 都提供了 `CustomJsonCodecConfiguration`。默认 profile 使用 Starter 自动创建的 Codec；激活 `custom-json-codec` profile 可以验证用户 Bean 覆盖流程。

### 自定义 SessionStore

默认 `InMemorySynologySessionStore` 使用当前 JVM 内的 `ConcurrentHashMap` 缓存 SID，适合单实例应用。多实例共享、跨进程复用或持久化会话时，在业务项目中实现 [`SynologySessionStore`](synology-dsm-java-sdk-core/src/main/java/io/github/shuzhuoi/synology/auth/store/SynologySessionStore.java)，并注册为 Bean：

```java
import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynologySessionStoreConfiguration {

    @Bean
    public SynologySessionStore synologySessionStore() {
        // YourSynologySessionStore 由业务项目实现，可接入 Redis、数据库或其他缓存。
        return new YourSynologySessionStore();
    }
}
```

实现类需要处理 `get`、`put`、`remove` 和 `clear`。仓库中的 [`ExampleSessionStore`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-example-common/src/main/java/io/github/shuzhuoi/synology/example/common/ExampleSessionStore.java) 展示了最小实现方式。SDK 不提供或强制引入 Redis 模块。

### 覆盖或禁用自动配置

Starter 遵循 Spring Boot 的用户配置优先原则：

| 用户提供的 Bean | Starter 行为 |
| --- | --- |
| `SynologyDsmConfig` | 不再根据 `synology.dsm` 创建默认配置对象 |
| `SynologyHttpClient` | 不再创建默认 HTTP adapter |
| `SynologySessionStore` | 将用户实现注入默认客户端 |
| `SynologyJsonCodec` | 使用用户实现，不创建默认 Jackson Codec |
| `SynologyDsmClient` | 不再创建默认客户端 |

完全不需要自动配置时：

```yaml
synology:
  dsm:
    enabled: false
```

### Session 生命周期

默认客户端的 Session 行为如下：

1. 第一次调用需要认证的 API 时自动登录。
2. 登录成功后将 SID 写入 `SynologySessionStore`。
3. 后续请求复用同一个 SID，不会每次重新登录。
4. DSM 返回会话失效错误码 106、107 或 119 时，自动重新登录并重试当前请求一次。
5. 需要主动结束 DSM 会话时调用 `client.session().logout()`，该操作同时移除缓存 SID。

将 `auto-login` 设为 `false` 后，如果 SessionStore 中没有 SID，调用认证接口会抛出认证异常。将 `auto-refresh-session` 设为 `false` 后，会话失效时不会自动重试。

### 运行仓库中的完整示例

两个示例都会执行完整基础工作流：查询信息、列出共享目录和文件、创建目录、上传、下载、重命名、删除，并在结束时登出。

- [Spring Boot 2 示例](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot2-example)
- [Spring Boot 3 示例](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot3-example)

以 Boot 2 为例，先进入资源目录并复制模板：

```powershell
Copy-Item application.example.yaml application.yaml
```

Boot 3 使用相同操作。`application.yaml` 已被根 `.gitignore` 忽略，不会提交真实凭据。

除 DSM 连接变量外，完整工作流还需要：

| 环境变量 | 说明 |
| --- | --- |
| `SYNOLOGY_SAMPLE_FILE` | 本地待上传文件的绝对路径，文件必须存在 |
| `SYNOLOGY_SAMPLE_FOLDER` | DSM 中用于测试的远端目录，建议使用专用测试目录 |
| `SYNOLOGY_DOWNLOAD_FOLDER` | 下载文件保存到本地的目录 |

在 IDE 中分别运行：

- Boot 2：[`SpringBoot2StarterExampleApplication`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot2-example/src/main/java/io/github/shuzhuoi/synology/spring/boot2/example/SpringBoot2StarterExampleApplication.java)
- Boot 3：[`SpringBoot3StarterExampleApplication`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot3-example/src/main/java/io/github/shuzhuoi/synology/spring/boot3/example/SpringBoot3StarterExampleApplication.java)

### 常见问题

#### 启动时报环境变量占位符无法解析

确认 `SYNOLOGY_DSM_BASE_URL`、`SYNOLOGY_DSM_ACCOUNT` 和 `SYNOLOGY_DSM_PASSWORD` 已设置在启动应用的同一个终端或 IDE Run Configuration 中。

#### 提示没有可用的 SynologyHttpClient

检查 `synology.dsm.http-adapter`。使用 `okhttp3` 时必须额外引入 OkHttp3 adapter；填写其他值时 Starter 不会创建默认 HTTP 客户端。

#### Boot 3 出现 class file version 错误

Boot 3 Starter 和示例使用 Java 17。检查 IDE Project SDK、Maven Runner JDK 和终端中的 `java -version` 是否一致。

#### DSM 地址可以访问，但 SDK 请求失败

`base-url` 应为 `http://host:port` 或 `https://host:port`，不要包含 `/webapi`。使用 HTTPS 自签名证书时，需要在选定的 HTTP 客户端中正确配置证书信任，不建议在生产环境关闭证书校验。

#### 不希望应用启动后立即访问 DSM

Starter 创建客户端时不会登录。只有业务代码第一次调用需要认证的 API 时才会登录；不要注册启动即执行的 `ApplicationRunner`，改为在实际业务请求中注入并使用 `SynologyDsmClient`。

#### Starter 会覆盖 Spring Boot 的 ObjectMapper 吗

不会。两个 Starter 都不注册 `ObjectMapper` Bean，也不修改 Spring Boot 的 Jackson 配置。若业务项目统一管理 Jackson 版本，可以通过 `mvn dependency:tree` 检查最终生效的依赖版本。

## 普通 Java 客户端配置

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

## 普通 Java 完整示例

普通 Java 示例位于 [`synology-dsm-java-sdk-java-example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example)。每个示例从 classpath 读取对应的本地 YAML；首次运行时，先复制同目录的 `*.example.yaml` 模板为不带 `.example` 的文件，再填写测试环境信息。

| 示例 | 覆盖能力 |
| --- | --- |
| [`FileStationBasicExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationBasicExample.java) | 查询、列表、创建目录、上传、下载、重命名、删除 |
| [`FileStationOkHttp3Example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationOkHttp3Example.java) | 使用 OkHttp3 执行基础工作流 |
| [`FileStationAdvancedExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationAdvancedExample.java) | 搜索、目录大小、后台任务 |
| [`FileStationResourceExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationResourceExample.java) | 分享、收藏、缩略图、虚拟目录 |
| [`FileStationArchiveExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationArchiveExample.java) | 压缩和解压 |
| [`FileStationOfficialCoverageExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationOfficialCoverageExample.java) | 官方契约补齐接口 |

本地配置文件已由根 `.gitignore` 忽略。不要在模板或 Java 源码中写入真实 DSM 密码。

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

## 许可证

[Apache License 2.0](LICENSE)
