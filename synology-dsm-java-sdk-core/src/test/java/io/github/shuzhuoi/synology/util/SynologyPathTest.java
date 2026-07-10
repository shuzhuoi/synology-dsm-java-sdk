package io.github.shuzhuoi.synology.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 固定当前 File Station 路径归一化规则。
 */
class SynologyPathTest {

    @Test
    void normalizesPathSeparatorsAndLeadingSlash() {
        assertEquals("/photo/2026", SynologyPath.normalize("photo\\2026"));
        assertEquals("/photo/2026", SynologyPath.normalize("/photo//2026/"));
        assertEquals("/photo/2026", SynologyPath.normalize("  photo/2026  "));
    }

    @Test
    void keepsRootPath() {
        assertEquals("/", SynologyPath.normalize("/"));
        assertEquals("/", SynologyPath.normalize("\\"));
    }

    @Test
    void rejectsBlankPath() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                SynologyPath.normalize(null);
            }
        });
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                SynologyPath.normalize("   ");
            }
        });
    }
}
