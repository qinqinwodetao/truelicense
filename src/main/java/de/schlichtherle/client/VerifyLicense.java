package de.schlichtherle.client;

import de.schlichtherle.license.*;
import de.schlichtherle.model.LicenseCommonParam;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * VerifyLicense
 * @author wucy
 */
public class VerifyLicense {


	public boolean verify(LicenseCommonParam licenseCommonParam) {
		/************** 证书使用者端执行 ******************/
		LicenseManager licenseManager = LicenseManagerHolder
				.getLicenseManager(initLicenseParams(licenseCommonParam));
		// 安装证书
		try {
			licenseManager.install(new File(licenseCommonParam.getLicPath()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// 验证证书
		try {
			licenseManager.verify();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


    /**
     * 返回验证证书需要的参数
     * @return
     */
	private static LicenseParam initLicenseParams(LicenseCommonParam licenseCommonParam) {
		Preferences preference = Preferences
				.userNodeForPackage(VerifyLicense.class);
		CipherParam cipherParam = new DefaultCipherParam(licenseCommonParam.getStorePwd());

		KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(
				VerifyLicense.class, licenseCommonParam.getResource(), licenseCommonParam.getAlias(), licenseCommonParam.getStorePwd(), null);
		LicenseParam licenseParams = new DefaultLicenseParam(licenseCommonParam.getSubject(),
				preference, privateStoreParam, cipherParam);
		return licenseParams;
	}
}