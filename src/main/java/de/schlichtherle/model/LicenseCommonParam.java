package de.schlichtherle.model;

/**
 * @author wucy
 */
public class LicenseCommonParam {

    /**
     * 密钥库存储路径
     */
    private  String resource ;
    /**
     * 密钥库的别名
     */
    private  String alias;
    /**
     * 密钥库存储密码
     */
    private  String storePwd ;
    /**
     * 密钥库密码
     */
    private  String keyPwd ;
    /**
     * 主题
     */
    private  String subject ;
    /**
     * lic文件保存路径
     */
    private  String licPath ;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStorePwd() {
        return storePwd;
    }

    public void setStorePwd(String storePwd) {
        this.storePwd = storePwd;
    }

    public String getKeyPwd() {
        return keyPwd;
    }

    public void setKeyPwd(String keyPwd) {
        this.keyPwd = keyPwd;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLicPath() {
        return licPath;
    }

    public void setLicPath(String licPath) {
        this.licPath = licPath;
    }
}
