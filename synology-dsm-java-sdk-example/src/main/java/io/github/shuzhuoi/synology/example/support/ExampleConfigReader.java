package io.github.shuzhuoi.synology.example.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 示例模块共用的 YAML 配置读取工具，避免不同 HTTP adapter 示例重复配置加载逻辑。
 */
public final class ExampleConfigReader {

    private ExampleConfigReader() {
    }

    /**
     * 从 classpath 读取示例配置。
     */
    public static <T> T readYaml(Class<?> anchorClass, String configFile,
                                 String configExampleFile, Class<T> configType) throws IOException {
        InputStream inputStream = anchorClass.getClassLoader().getResourceAsStream(configFile);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + configFile
                    + "，请先复制 " + configExampleFile + " 为 " + configFile + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, configType);
        }
    }

    /**
     * 校验示例配置中的必填字符串。
     */
    public static String requiredValue(String value, String configFile, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("示例配置文件 " + configFile + " 缺少必填配置：" + fieldName);
        }
        return value;
    }
}
