package io.github.shuzhuoi.synology.samples.config;

/**
 * File Station 基础示例的本地配置。
 * <p>
 * 该类只服务于 samples 模块，用于承载从 filestation-basic.yaml 读取到的运行参数。
 */
public class FileStationBasicSampleConfig {

    /**
     * DSM 服务地址，例如：https://nas.example.com:5001。
     */
    private String dsmUrl;

    /**
     * DSM 登录账号。
     */
    private String account;

    /**
     * DSM 登录密码。
     */
    private String password;

    /**
     * 用于上传演示的本地文件路径。
     */
    private String sampleFile;

    /**
     * File Station 中用于演示操作的远端目录。
     */
    private String sampleFolder;

    /**
     * 下载文件保存到本地的目录。
     */
    private String downloadFolder;

    public String getDsmUrl() {
        return dsmUrl;
    }

    public void setDsmUrl(String dsmUrl) {
        this.dsmUrl = dsmUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSampleFile() {
        return sampleFile;
    }

    public void setSampleFile(String sampleFile) {
        this.sampleFile = sampleFile;
    }

    public String getSampleFolder() {
        return sampleFolder;
    }

    public void setSampleFolder(String sampleFolder) {
        this.sampleFolder = sampleFolder;
    }

    public String getDownloadFolder() {
        return downloadFolder;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder = downloadFolder;
    }
}
