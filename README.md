# Synology DSM Java SDK

English | [中文](README_zh-CN.md)

[![License: Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java: 8 / 17](https://img.shields.io/badge/java-8%20%2F%2017-orange.svg)](https://www.java.com/)
[![CI](https://github.com/shuzhuoi/synology-dsm-java-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/shuzhuoi/synology-dsm-java-sdk/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.shuzhuoi/synology-dsm-java-sdk-core.svg)](https://central.sonatype.com/artifact/io.github.shuzhuoi/synology-dsm-java-sdk-core)

Synology DSM Java SDK provides Java bindings for the [Synology DSM WebAPI](https://global.download.synology.com/download/Document/Software/DeveloperGuide/OS/Dynamicsite/All/enu/Synology_DiskStation_Administration_Web_API_Guide.pdf), with full coverage of **File Station** file operations, task-based APIs, sharing, favorites, thumbnails, virtual folders, compression and extraction.

The SDK splits business APIs, HTTP implementations and Spring Boot integration into independent modules. Plain Java, Spring Boot 2 and Spring Boot 3 projects can pick only what they need, without pulling Spring, Hutool or OkHttp3 into core.

## Version and Compatibility

This documentation matches SDK `1.0.1`. The public API has been maintained under semantic versioning since `1.0.0`: compatibility is preserved within the same major version, and breaking changes move to the next major version.

When upgrading from `0.4.0`, focus on the changes to `SynologyDsmClient.Builder`, the HTTP factory methods and `BackgroundTask.getParams()`. See the `1.0.0` section in [`CHANGELOG.md`](CHANGELOG.md) for migration details.

The default Starter uses Jackson, but Jackson lives only in a separate JSON module and never enters core. Fastjson2 is an equal alternative; `fastjson2-extension-spring5/6` is only needed when the application wants to replace the global Spring MVC JSON message converters, and is never required by the SDK Codec or the Starter.

## Choosing Modules

| Scenario | Modules | Java | HTTP implementation |
| --- | --- | --- | --- |
| Plain Java, default | `http-hutool` + `json-jackson` | Java 8+ | Hutool |
| Plain Java with OkHttp3 | `http-okhttp3` + `json-jackson` | Java 8+ | OkHttp 3.x |
| Default JSON implementation | `synology-dsm-java-sdk-json-jackson` | Java 8+ | Jackson |
| Alternative JSON implementation | `synology-dsm-java-sdk-json-fastjson2` | Java 8+ | Fastjson2 |
| Custom HTTP implementation | `synology-dsm-java-sdk-core` | Java 8+ | Implement `SynologyHttpClient` yourself |
| Spring Boot 2 | `synology-dsm-java-sdk-spring-boot2-starter` | Java 8+ | Hutool by default, OkHttp3 optional |
| Spring Boot 3 | `synology-dsm-java-sdk-spring-boot3-starter` | Java 17+ | Hutool by default, OkHttp3 optional |

> [!NOTE]
> core, both JSON implementations, both HTTP adapters and the Boot 2 Starter target Java 8; the Boot 3 Starter targets Java 17. The whole reactor is built with JDK 17, and `--release 8/17` constrains the JDK APIs each module may use as well as the produced bytecode.

## Installation

### Plain Java: Hutool

The Hutool adapter brings core in transitively, but the JSON implementation must be added separately:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-hutool</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-jackson</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Plain Java: OkHttp3

The OkHttp3 adapter also brings core in transitively; do not add Hutool at the same time. Add the JSON implementation as well:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-okhttp3</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-jackson</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Custom HTTP Implementation

If you only need the business models and the API execution machinery, depend on core alone and implement `SynologyHttpClient`:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

With core only, you must also supply a `SynologyJsonCodec` implementation. The SDK never picks a JSON library silently through reflection or `ServiceLoader`.

### Choosing the JSON Implementation

The SDK ships Jackson and Fastjson2 as equal alternatives. The documentation and the Starters use `synology-dsm-java-sdk-json-jackson` by default; plain Java projects that want to avoid Jackson can use `synology-dsm-java-sdk-json-fastjson2`. Neither module makes core compile against a concrete JSON library.

With Jackson:

```java
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;

JacksonSynologyJsonCodec jsonCodec = new JacksonSynologyJsonCodec();
```

For Fastjson2, add the dependency instead:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-fastjson2</artifactId>
    <version>1.0.1</version>
</dependency>
```

Then create the codec explicitly:

```java
import io.github.shuzhuoi.synology.json.fastjson2.Fastjson2SynologyJsonCodec;

Fastjson2SynologyJsonCodec jsonCodec = new Fastjson2SynologyJsonCodec();
```

HTTP adapters and JSON implementations are independent. Hutool and OkHttp3 both work with either codec, but a single `SynologyDsmClient` takes exactly one explicit implementation.

## Requirements

- Plain Java, core, HTTP adapters and the Spring Boot 2 Starter: Java 8 or later
- Spring Boot 3 Starter: Java 17 or later
- Spring Boot 2: targets Spring Boot 2.7.x, currently baseline 2.7.18
- Spring Boot 3: targets Spring Boot 3.x, currently baseline 3.3.13
- Maven 3.6.3 or later
- A reachable Synology NAS with the WebAPI enabled (DSM 6.x / 7.x both work)

## Building from Source

The repository contains both Java 8 and Java 17 target modules, so always run Maven with JDK 17. Verify the JDK Maven actually uses, not just the IDE project SDK:

```bash
mvn -version
```

The `Java version` in the output should be `17`. The parent POM sets `maven.compiler.release=8` by default, while the Boot 3 Starter and the Boot 3 example override it to `17`; this prevents Java 8 modules compiled on JDK 17 from accidentally using Java 9+ APIs such as `List.of`.

For a full local verification, use the `local-build` profile to skip GPG signing:

```bash
mvn clean verify -Plocal-build
```

`clean` removes previous `target` output; `verify` runs compilation, tests, packaging and the extra validations configured in the project. `local-build` only sets `gpg.skip=true` — it does not skip tests, JARs, sources or Javadoc.

After reloading the Maven project in IDEA, tick `local-build` under **Profiles** in the Maven tool window and make sure the Maven Runner JRE is JDK 17. Do not enable this profile for a real Maven Central release, or the artifacts will not be GPG-signed.

To verify only the Boot 3 Starter and its in-project dependencies:

```bash
mvn -pl synology-dsm-java-sdk-spring-boot3-starter -am clean verify -Plocal-build
```

To verify only the Boot 3 example and its in-project dependencies:

```bash
mvn -pl synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot3-example -am clean verify -Plocal-build
```

## Plain Java Quick Start

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

// The JSON codec is chosen explicitly by the caller, keeping core decoupled from any concrete JSON library
SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(
        config,
        new JacksonSynologyJsonCodec()
);

// The first call logs in automatically; later calls reuse the same SID
ListFilesResponse response = client.fileStation().list().files(
        ListFilesRequest.builder("/home")
                .limit(50)
                .build()
);

for (SynologyFile file : response.getFiles()) {
    System.out.println(file.getPath());
}

// Log out explicitly when finished to release the DSM session
client.session().logout();
```

With OkHttp3, the core API stays the same — only the client factory changes:

```java
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;

SynologyDsmClient client = OkHttp3SynologyDsmClientFactory.create(
        config,
        new JacksonSynologyJsonCodec()
);
```

To configure a proxy, TLS or the connection pool, build your own `OkHttpClient` and inject it into `OkHttp3SynologyHttpClient`:

```java
SynologyDsmClient client = SynologyDsmClient.builder()
        .config(config)
        .httpClient(new OkHttp3SynologyHttpClient(customOkHttpClient))
        .jsonCodec(new JacksonSynologyJsonCodec())
        .build();
```

More samples live in [`synology-dsm-java-sdk-java-example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example), covering info, listing, folder creation, upload, download, rename, delete, search, folder size, background tasks, sharing, favorites, thumbnails, virtual folders, compression and extraction.

Stable parameters have type-safe enums such as `SortDirection.ASC`, `FileTypeFilter.FILE`, `ThumbSize.SMALL` and `CompressFormat.ZIP`. The original String builder methods are kept for forward compatibility with values DSM may add later.

`BackgroundTask.getParams()` returns `Map<String, Object>` instead of exposing Jackson `JsonNode`; nested objects, lists, numbers, booleans and nulls are preserved as JDK collection types.

## Spring Boot Starter

The project ships two independent Starters for Boot 2 and Boot 3. Both share the same `synology.dsm` configuration protocol and the same `SynologyDsmClient` public API, but their auto-configuration registration, Spring Boot dependencies and Java targets are fully isolated.

> [!TIP]
> Each Starter already includes core, the Hutool adapter and the Jackson JSON codec. To get started you only need the Starter itself — no need to redeclare those dependencies.

> [!CAUTION]
> Add only the Starter matching your Spring Boot major version; never add the Boot 2 and Boot 3 Starters to the same application.

### Spring Boot 2 Tutorial

For Spring Boot 2.7.x on Java 8 or later. The project currently builds and verifies against Spring Boot 2.7.18.

#### Step 1: Add the Starter

Add to the `pom.xml` of your Spring Boot 2 project:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-spring-boot2-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

In the default setup you do not need `synology-dsm-java-sdk-core` or `synology-dsm-java-sdk-http-hutool` on top.

#### Step 2: Configure the DSM Connection

Add to `src/main/resources/application.yaml`:

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

`base-url` takes only the DSM host and port — do not append `/webapi`. Provide the account and password through environment variables; never commit real credentials to the repository.

#### Step 3: Set Environment Variables

PowerShell:

```powershell
$env:SYNOLOGY_DSM_BASE_URL = "https://nas.example.com:5001"
$env:SYNOLOGY_DSM_ACCOUNT = "your-account"
$env:SYNOLOGY_DSM_PASSWORD = "your-password"
```

Linux or macOS:

```bash
export SYNOLOGY_DSM_BASE_URL="https://nas.example.com:5001"
export SYNOLOGY_DSM_ACCOUNT="your-account"
export SYNOLOGY_DSM_PASSWORD="your-password"
```

#### Step 4: Inject and Use the Client

The Starter registers a singleton `SynologyDsmClient` bean. Constructor-inject it into your business classes and call the File Station API:

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

The first authenticated call logs in automatically; later calls reuse the cached SID.

#### Step 5: Start the Application

From the root of your Spring Boot application:

```bash
mvn spring-boot:run
```

Once the application starts, `FileStationRunner` runs. If `/home` is not accessible to the DSM account, replace it with a shared folder the account can read.

### Spring Boot 3 Tutorial

For Spring Boot 3.x on Java 17 or later. The project currently builds and verifies against Spring Boot 3.3.13.

#### Step 1: Add the Starter

Add to the `pom.xml` of your Spring Boot 3 project:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-spring-boot3-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

In the default setup you do not need `synology-dsm-java-sdk-core` or `synology-dsm-java-sdk-http-hutool` on top.

#### Step 2: Configure the DSM Connection

Add to `src/main/resources/application.yaml`:

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

`base-url` takes only the DSM host and port — do not append `/webapi`. Provide the account and password through environment variables; never commit real credentials to the repository.

#### Step 3: Set Environment Variables

PowerShell:

```powershell
$env:SYNOLOGY_DSM_BASE_URL = "https://nas.example.com:5001"
$env:SYNOLOGY_DSM_ACCOUNT = "your-account"
$env:SYNOLOGY_DSM_PASSWORD = "your-password"
```

Linux or macOS:

```bash
export SYNOLOGY_DSM_BASE_URL="https://nas.example.com:5001"
export SYNOLOGY_DSM_ACCOUNT="your-account"
export SYNOLOGY_DSM_PASSWORD="your-password"
```

#### Step 4: Inject and Use the Client

The Boot 3 Starter exposes the same core `SynologyDsmClient`, so usage is identical to Boot 2:

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

The first authenticated call logs in automatically; later calls reuse the cached SID.

#### Step 5: Start the Application

Make sure the current terminal uses JDK 17 or later, then run from the application root:

```bash
java -version
mvn spring-boot:run
```

Once the application starts, `FileStationRunner` runs. If `/home` is not accessible, replace it with a shared folder the DSM account can read.

### Starter Configuration Reference

Boot 2 and Boot 3 use exactly the same properties:

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `synology.dsm.enabled` | No | `true` | Enables the Starter auto-configuration |
| `synology.dsm.base-url` | Yes | — | DSM address, e.g. `https://nas.example.com:5001`, without `/webapi` |
| `synology.dsm.account` | Yes | — | DSM account; a least-privilege account is recommended |
| `synology.dsm.password` | Yes | — | DSM password; never printed to logs by the SDK |
| `synology.dsm.session-name` | No | `FileStation` | DSM WebAPI session name |
| `synology.dsm.connect-timeout-millis` | No | `10000` | HTTP connect timeout in milliseconds |
| `synology.dsm.read-timeout-millis` | No | `60000` | HTTP read timeout; increase for large uploads/downloads |
| `synology.dsm.auto-login` | No | `true` | Log in automatically when no SID is available |
| `synology.dsm.auto-refresh-session` | No | `true` | Re-login and retry once when the SID expires |
| `synology.dsm.http-adapter` | No | `hutool` | HTTP implementation: `hutool` or `okhttp3` |

"Required" applies when the Starter is enabled and creates the default client. Setting `enabled=false` or supplying a fully custom client removes the default client creation.

Full configuration example:

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

### Switching to OkHttp3

The OkHttp3 adapter is an optional dependency of the Starters. First add:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-http-okhttp3</artifactId>
    <version>1.0.1</version>
</dependency>
```

Then change the configuration:

```yaml
synology:
  dsm:
    http-adapter: okhttp3
```

To configure a proxy, TLS, interceptors or the connection pool, provide your own `SynologyHttpClient` bean:

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
                // Configure proxy, TLS, interceptors or connection pool here.
                .build();
        return new OkHttp3SynologyHttpClient(okHttpClient);
    }
}
```

When a user-provided `SynologyHttpClient` is present, the Starter no longer creates the default Hutool or OkHttp3 adapter.

### Replacing the JSON Codec

The Starter creates `JacksonSynologyJsonCodec` by default. To switch to Fastjson2, exclude the default Jackson module from the Starter and add the Fastjson2 module. The example below uses the Boot 2 Starter; for Boot 3 just change the Starter artifactId to `synology-dsm-java-sdk-spring-boot3-starter`:

```xml
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-spring-boot2-starter</artifactId>
    <version>1.0.1</version>
    <exclusions>
        <exclusion>
            <groupId>io.github.shuzhuoi</groupId>
            <artifactId>synology-dsm-java-sdk-json-jackson</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>io.github.shuzhuoi</groupId>
    <artifactId>synology-dsm-java-sdk-json-fastjson2</artifactId>
    <version>1.0.1</version>
</dependency>
```

Then register the single `SynologyJsonCodec` bean:

```java
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.fastjson2.Fastjson2SynologyJsonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynologyJsonConfiguration {

    @Bean
    public SynologyJsonCodec synologyJsonCodec() {
        return new Fastjson2SynologyJsonCodec();
    }
}
```

The Starter prefers the user-provided codec, never picks multiple implementations silently, and never registers or modifies Spring Boot's global `ObjectMapper`. Registering only the Fastjson2 bean without excluding the Jackson module also runs, but Jackson stays on the classpath; exclude it via the Maven configuration above if you want it fully gone. For custom Jackson behavior, create `JacksonSynologyJsonCodec(objectMapper)`; the codec copies the mapper before adding the SDK mapping rules.

The Boot 2/3 examples in the repository both ship a `CustomJsonCodecConfiguration`. The default profile uses the codec created by the Starter; activating the `custom-json-codec` profile exercises the user-bean override path.

### Custom SessionStore

The default `InMemorySynologySessionStore` caches SIDs in a JVM-local `ConcurrentHashMap`, which suits single-instance applications. To share sessions across instances, processes or restarts, implement [`SynologySessionStore`](synology-dsm-java-sdk-core/src/main/java/io/github/shuzhuoi/synology/auth/store/SynologySessionStore.java) in your project and register it as a bean:

```java
import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynologySessionStoreConfiguration {

    @Bean
    public SynologySessionStore synologySessionStore() {
        // YourSynologySessionStore is implemented by your project and can be backed by Redis, a database or any other cache.
        return new YourSynologySessionStore();
    }
}
```

The implementation must handle `get`, `put`, `remove` and `clear`. The [`ExampleSessionStore`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-example-common/src/main/java/io/github/shuzhuoi/synology/example/common/ExampleSessionStore.java) in the repository shows the minimal implementation. The SDK does not ship or force a Redis module.

### Overriding or Disabling Auto-Configuration

The Starters follow the Spring Boot user-configuration-first principle:

| User-provided bean | Starter behavior |
| --- | --- |
| `SynologyDsmConfig` | No default config object is created from `synology.dsm` |
| `SynologyHttpClient` | No default HTTP adapter is created |
| `SynologySessionStore` | The user implementation is injected into the default client |
| `SynologyJsonCodec` | The user implementation is used; no default Jackson codec is created |
| `SynologyDsmClient` | No default client is created |

To disable auto-configuration entirely:

```yaml
synology:
  dsm:
    enabled: false
```

### Session Lifecycle

The default client behaves as follows:

1. The first authenticated API call logs in automatically.
2. After a successful login the SID is stored in `SynologySessionStore`.
3. Subsequent requests reuse the same SID instead of logging in again.
4. When DSM returns session-expired error codes 106, 107 or 119, the client re-logs in and retries the current request once.
5. Call `client.session().logout()` to end the DSM session explicitly; this also removes the cached SID.

With `auto-login=false`, calling an authenticated API without a SID in the SessionStore throws an authentication exception. With `auto-refresh-session=false`, expired sessions are not retried automatically.

### Running the Bundled Examples

Both examples run the full basic workflow: query info, list shares and files, create a folder, upload, download, rename, delete, then log out.

- [Spring Boot 2 example](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot2-example)
- [Spring Boot 3 example](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot3-example)

Taking Boot 2 as an example, enter the resources directory and copy the template:

```powershell
Copy-Item application.example.yaml application.yaml
```

Boot 3 works the same way. `application.yaml` is ignored by the root `.gitignore`, so real credentials are never committed.

Besides the DSM connection variables, the full workflow needs:

| Environment variable | Description |
| --- | --- |
| `SYNOLOGY_SAMPLE_FILE` | Absolute path of a local file to upload; the file must exist |
| `SYNOLOGY_SAMPLE_FOLDER` | Remote folder on DSM used for testing; a dedicated test folder is recommended |
| `SYNOLOGY_DOWNLOAD_FOLDER` | Local folder where downloaded files are saved |

Run in the IDE:

- Boot 2: [`SpringBoot2StarterExampleApplication`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot2-example/src/main/java/io/github/shuzhuoi/synology/spring/boot2/example/SpringBoot2StarterExampleApplication.java)
- Boot 3: [`SpringBoot3StarterExampleApplication`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-spring-boot3-example/src/main/java/io/github/shuzhuoi/synology/spring/boot3/example/SpringBoot3StarterExampleApplication.java)

### FAQ

#### The application fails to start because an environment variable placeholder cannot be resolved

Make sure `SYNOLOGY_DSM_BASE_URL`, `SYNOLOGY_DSM_ACCOUNT` and `SYNOLOGY_DSM_PASSWORD` are set in the same terminal or IDE run configuration that starts the application.

#### The Starter reports no SynologyHttpClient available

Check `synology.dsm.http-adapter`. Using `okhttp3` requires the OkHttp3 adapter dependency; with any other value the Starter will not create a default HTTP client.

#### Boot 3 fails with a class file version error

The Boot 3 Starter and example use Java 17. Align the IDE project SDK, the Maven Runner JDK and the terminal `java -version`.

#### The DSM address is reachable but SDK requests fail

`base-url` should be `http://host:port` or `https://host:port` without `/webapi`. With an HTTPS self-signed certificate, configure trust correctly in the chosen HTTP client; disabling certificate verification in production is not recommended.

#### I do not want the application to touch DSM at startup

Creating the client does not log in. Login happens on the first authenticated API call from business code; do not register an `ApplicationRunner` that runs at startup — inject and use `SynologyDsmClient` in real business requests instead.

#### Does the Starter override Spring Boot's ObjectMapper?

No. Neither Starter registers an `ObjectMapper` bean or touches Spring Boot's Jackson configuration. If your project manages the Jackson version centrally, inspect the effective version with `mvn dependency:tree`; to fully avoid Jackson version arbitration, exclude the default Jackson module as described in "Replacing the JSON Codec" and switch to the Fastjson2 implementation.

#### Does using Fastjson2 require `fastjson2-extension-spring5/6`?

No. `synology-dsm-java-sdk-json-fastjson2` only provides the JSON codec used inside the SDK — depending on `fastjson2` directly is enough. Only introduce `fastjson2-extension-spring5` or `fastjson2-extension-spring6` (matching your Spring Boot version) if the application itself wants Fastjson2 as the global Spring MVC message converter; the SDK Starter never modifies the application's global `ObjectMapper` or converters.

## Plain Java Client Configuration

Configure via `SynologyDsmConfig.builder()`:

```java
SynologyDsmConfig config = SynologyDsmConfig.builder()
        .baseUrl("https://nas.example.com:5001")
        .account("your-account")
        .password("your-password")
        .connectTimeoutMillis(10000)
        .readTimeoutMillis(60000)
        .build();
```

| Option | Default | Description |
| --- | --- | --- |
| `baseUrl` | Required | DSM base address, e.g. `https://nas.example.com:5001`, without `/webapi` |
| `account` | Required | DSM account |
| `password` | Required | DSM password; never printed to logs |
| `sessionName` | `FileStation` | WebAPI session name; fixed to `FileStation` for File Station |
| `connectTimeoutMillis` | `10000` | HTTP connect timeout (ms) |
| `readTimeoutMillis` | `60000` | HTTP read timeout (ms); increase for large uploads/downloads |
| `autoLogin` | `true` | Log in automatically when no SID is available |
| `autoRefreshSession` | `true` | On session expiry (error codes 106/107/119), re-login and retry once |

Trailing `/` in `baseUrl` is removed automatically. Internally the SDK builds request URLs as `baseUrl + /webapi/entry.cgi`. Sensitive information such as passwords, SIDs and cookies never appears in logs.

## Plain Java Examples

The plain Java examples live in [`synology-dsm-java-sdk-java-example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example). Each example reads a local YAML file from the classpath; on first run, copy the matching `*.example.yaml` template to a file without `.example` and fill in your test environment details.

| Example | Coverage |
| --- | --- |
| [`FileStationBasicExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationBasicExample.java) | Info, listing, folder creation, upload, download, rename, delete |
| [`FileStationOkHttp3Example`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationOkHttp3Example.java) | Basic workflow over OkHttp3 |
| [`FileStationAdvancedExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationAdvancedExample.java) | Search, folder size, background tasks |
| [`FileStationResourceExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationResourceExample.java) | Sharing, favorites, thumbnails, virtual folders |
| [`FileStationArchiveExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationArchiveExample.java) | Compression and extraction |
| [`FileStationOfficialCoverageExample`](synology-dsm-java-sdk-example/synology-dsm-java-sdk-java-example/src/main/java/io/github/shuzhuoi/synology/example/FileStationOfficialCoverageExample.java) | Remaining official API contracts |

The local configuration files are ignored by the root `.gitignore`. Never put real DSM passwords in templates or Java sources.

## Download and Thumbnail Streams

`download().file(...)` and `thumb().get(...)` return the raw stream of the underlying HTTP connection; the SDK never loads file content into memory. Always close the stream when finished, otherwise connections cannot return to the pool and high-frequency calls will exhaust it. Try-with-resources is recommended:

```java
try (InputStream in = client.fileStation().download().file("/video/backup.zip").getInputStream()) {
    Files.copy(in, Paths.get("/local/backup.zip"), StandardCopyOption.REPLACE_EXISTING);
}
```

Notes:

- The stream is read lazily; closing it releases the connection. Do not hold an unconsumed stream reference for a long time.
- When the response has no body (for example HTTP 204), `getInputStream()` returns `null` — callers must null-check.
- Uploads use `multipart` forms and have no stream to close; increase `readTimeoutMillis` for large downloads.

## Security Recommendations

- **Always use HTTPS in production.** The DSM login endpoint submits the account and password as plain form parameters, which is sniffable over HTTP; trust self-signed certificates properly in the HTTP client instead of disabling certificate verification.
- Use a **dedicated least-privilege DSM account** for the SDK and inject the password through environment variables rather than code or configuration files in the repository.
- **Treat the SID as a logged-in session**: never log it or attach it to monitoring tags. The SDK never prints the account, password or SID.
- Review logged-in devices and Auto Block settings in the DSM console regularly, and enable two-factor authentication for administrator accounts.

## API Entry Points

The SDK exposes three layers of entry points:

```
SynologyDsmClient
├── apiInfo()                  // SYNO.API.Info
├── auth()                     // SYNO.API.Auth (explicit login/logout)
├── session()                  // Session management (auto login, SID reuse, logout)
└── fileStation()              // File Station aggregate entry
    ├── info()                 // SYNO.FileStation.Info
    ├── list()                 // SYNO.FileStation.List
    ├── upload()               // SYNO.FileStation.Upload
    ├── download()             // SYNO.FileStation.Download
    ├── file()                 // create/rename/delete/copy/move
    ├── task()                 // Task-based APIs (MD5 etc.)
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
