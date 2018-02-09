package de.schlichtherle.model;

/**
 * @author wucy
 */
public class LicenseCommonContent {

    /**
     * 颁发时间
     */
    private  String issuedTime;
    /**
     * license生效时间
     */
    private  String notBefore;
    /**
     * license失效时间
     */
    private  String notAfter;
    /**
     * 授权客户类型 默认user
      */
    private  String consumerType;
    /**
     * 授权客户数量
     */
    private  int consumerAmount;
    /**
     * 信息
     */
    private  String info;
    /**
     * 其他受检查的相关属性，例如：mac ip
     */
    private  LicenseCheckModel licenseCheckModel;

    public String getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        this.issuedTime = issuedTime;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public int getConsumerAmount() {
        return consumerAmount;
    }

    public void setConsumerAmount(int consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LicenseCheckModel getLicenseCheckModel() {
        return licenseCheckModel;
    }

    public void setLicenseCheckModel(LicenseCheckModel licenseCheckModel) {
        this.licenseCheckModel = licenseCheckModel;
    }
}
