package io.github.shuzhuoi.synology.internal;

/**
 * 执行器测试用响应实体，用于验证 Jackson 能把 data 节点转换成 SDK 返回对象。
 */
public class ExecutorData {

    private String name;
    private Integer count;

    public ExecutorData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
