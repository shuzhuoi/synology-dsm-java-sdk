package io.github.shuzhuoi.synology.util;

public final class SynologyPath {

    private SynologyPath() {
    }

    public static String normalize(String path) {
        // File Station API 要求路径从共享目录开始，并以 / 开头。
        if (path == null || path.trim().length() == 0) {
            throw new IllegalArgumentException("path must not be blank");
        }
        String normalized = path.trim().replace("\\", "/");
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
