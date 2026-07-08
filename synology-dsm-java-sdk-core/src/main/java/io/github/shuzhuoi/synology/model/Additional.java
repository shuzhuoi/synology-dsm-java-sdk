package io.github.shuzhuoi.synology.model;

public enum Additional {
    REAL_PATH("real_path"),
    SIZE("size"),
    OWNER("owner"),
    TIME("time"),
    PERM("perm"),
    TYPE("type"),
    MOUNT_POINT_TYPE("mount_point_type"),
    VOLUME_STATUS("volume_status");

    private final String value;

    Additional(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
