package io.github.shuzhuoi.synology.spring.boot2;

import io.github.shuzhuoi.synology.auth.store.InMemorySynologySessionStore;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyHttpClient;
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyHttpClient;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring Boot 2 Starter 自动装配测试，使用 ApplicationContextRunner 验证装配与条件回退行为。
 */
class SynologyDsmAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SynologyDsmAutoConfiguration.class));

    /**
     * 默认装配：Hutool HTTP + Jackson Codec + SynologyDsmClient，且配置属性正确绑定。
     */
    @Test
    void shouldAssembleDefaultClientWithHutoolAndJackson() {
        runner.withPropertyValues(
                        "synology.dsm.base-url=http://127.0.0.1:5000",
                        "synology.dsm.account=demo",
                        "synology.dsm.password=secret")
                .run(context -> {
                    assertThat(context).hasSingleBean(SynologyDsmClient.class);
                    assertThat(context).getBean(SynologyHttpClient.class).isInstanceOf(HutoolSynologyHttpClient.class);
                    assertThat(context).getBean(SynologyJsonCodec.class).isInstanceOf(JacksonSynologyJsonCodec.class);
                    SynologyDsmConfig config = context.getBean(SynologyDsmConfig.class);
                    assertEquals("http://127.0.0.1:5000", config.getBaseUrl());
                    assertEquals("demo", config.getAccount());
                    assertEquals("secret", config.getPassword());
                    assertEquals("FileStation", config.getSessionName());
                });
    }

    /**
     * synology.dsm.enabled=false 时应完全关闭自动装配。
     */
    @Test
    void shouldNotCreateBeansWhenDisabled() {
        runner.withPropertyValues("synology.dsm.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(SynologyDsmClient.class));
    }

    /**
     * http-adapter=okhttp3 时应切换为 OkHttp3 实现。
     */
    @Test
    void shouldUseOkHttp3AdapterWhenConfigured() {
        runner.withPropertyValues(
                        "synology.dsm.base-url=http://127.0.0.1:5000",
                        "synology.dsm.http-adapter=okhttp3")
                .run(context -> {
                    assertThat(context).hasSingleBean(SynologyDsmClient.class);
                    assertThat(context).getBean(SynologyHttpClient.class).isInstanceOf(OkHttp3SynologyHttpClient.class);
                });
    }

    /**
     * 用户自定义 Bean 存在时默认装配应回退，客户端使用用户提供的组件。
     */
    @Test
    void shouldBackOffWhenUserDefinesOwnBeans() {
        SynologyJsonCodec customCodec = new JacksonSynologyJsonCodec();
        runner.withPropertyValues("synology.dsm.base-url=http://127.0.0.1:5000")
                .withBean(SynologyHttpClient.class, () -> request -> null)
                .withBean(SynologyJsonCodec.class, () -> customCodec)
                .withBean(InMemorySynologySessionStore.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(SynologyHttpClient.class);
                    assertThat(context).hasSingleBean(SynologyJsonCodec.class);
                    assertThat(context.getBean(SynologyJsonCodec.class)).isSameAs(customCodec);
                    assertThat(context).hasSingleBean(SynologyDsmClient.class);
                });
    }

    /**
     * 类路径缺少 JSON 实现时应快速失败，并给出明确的错误信息。
     */
    @Test
    void shouldFailFastWhenJsonCodecMissing() {
        runner.withClassLoader(new FilteredClassLoader(JacksonSynologyJsonCodec.class))
                .withPropertyValues("synology.dsm.base-url=http://127.0.0.1:5000")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalStateException.class);
                });
    }

    /**
     * spring.factories 必须注册自动配置类，保证真实应用引入 Starter 即生效。
     */
    @Test
    void shouldRegisterAutoConfigurationInSpringFactories() throws IOException {
        assertTrue(classpathResourceContains("META-INF/spring.factories",
                "io.github.shuzhuoi.synology.spring.boot2.SynologyDsmAutoConfiguration"));
    }

    /**
     * 遍历类路径上同名资源的所有来源，判断任一内容包含期望文本。
     */
    private boolean classpathResourceContains(String path, String expected) throws IOException {
        Enumeration<URL> resources = getClass().getClassLoader().getResources(path);
        while (resources.hasMoreElements()) {
            InputStream stream = resources.nextElement().openStream();
            try {
                String content = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
                if (content.contains(expected)) {
                    return true;
                }
            } finally {
                stream.close();
            }
        }
        return false;
    }
}
