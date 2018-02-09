package de.schlichtherle.demo;


import de.schlichtherle.client.CreateLicense;
import de.schlichtherle.model.LicenseCheckModel;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建LicenseDemo
 * @author wucy
 */
public class LicenseCreateDemo {

	public void run(){

		LicenseCommonParam licenseCommonParam = new LicenseCommonParam();
		licenseCommonParam.setAlias("privatekey");
		licenseCommonParam.setResource("/privateKeys.store");
		licenseCommonParam.setStorePwd("kedacom123");
		licenseCommonParam.setKeyPwd("kedacom123");
		licenseCommonParam.setSubject("license");
		licenseCommonParam.setLicPath("license.lic");

		LicenseCommonContent licenseCommonContent = new LicenseCommonContent();
		licenseCommonContent.setIssuedTime("2018-01-01");
		licenseCommonContent.setNotBefore("2018-02-01");
		licenseCommonContent.setNotAfter("2020-12-31");
		licenseCommonContent.setConsumerType("user");
		licenseCommonContent.setConsumerAmount(1);
		licenseCommonContent.setInfo("this is a license");

		LicenseCheckModel licenseCheckModel = new LicenseCheckModel();
		List<String> macAddressList = new ArrayList<String>();
		macAddressList.add("E4-70-B8-DF-DC-1A");
		licenseCheckModel.setMacAddressList(macAddressList);
		licenseCommonContent.setLicenseCheckModel(licenseCheckModel);

		Boolean succ = new CreateLicense().create(licenseCommonParam,licenseCommonContent);

		System.out.println("create result is :" + succ);

	}
}
