package de.schlichtherle.demo;

import de.schlichtherle.client.VerifyLicense;
import de.schlichtherle.model.LicenseCommonParam;

/**
 * 验证licenseDemo
 * @author wucy
 */
public class LicenseVerifyDemo {

    public void run(){
		//验证证书
        LicenseCommonParam licenseCommonParam = new LicenseCommonParam();
        licenseCommonParam.setAlias("publiccert");
        licenseCommonParam.setStorePwd("kedacom123");
        licenseCommonParam.setSubject("license");
        licenseCommonParam.setLicPath("license.lic");
        licenseCommonParam.setResource("/publicCerts.store");
        Boolean succ = new VerifyLicense().verify(licenseCommonParam);

        System.out.println("verify result is :" + succ);
	}
}
