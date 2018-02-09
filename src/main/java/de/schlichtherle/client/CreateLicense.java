package de.schlichtherle.client;

import de.schlichtherle.license.*;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

/**
 * Create License
 * @author wucy
 */
public class CreateLicense {

	/**
	 * 为了方便直接用的API里的例子
	 * X500Princal是一个证书文件的固有格式，详见API
	 */
	private final static X500Principal DEFAULTHOLDERANDISSUER = new X500Principal(
			"CN=Duke、OU=JavaSoft、O=Sun Microsystems、C=US");

	/**
	 * 日期格式
	 */
	private static final String DATEFORMAT = "yyyy-MM-dd";



	public boolean create(LicenseCommonParam licenseCommonParam, LicenseCommonContent licenseCommonContent) {
		try {
			/************** 证书发布者端执行 ******************/
			LicenseManager licenseManager = LicenseManagerHolder
					.getLicenseManager(initLicenseParams(licenseCommonParam));
			licenseManager.store((createLicenseContent(licenseCommonParam,licenseCommonContent)), new File(licenseCommonParam.getLicPath()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * 返回生成证书时需要的参数
	 * @return
	 */
	private static LicenseParam initLicenseParams(LicenseCommonParam licenseCommonParam) {
		Preferences preference = Preferences
				.userNodeForPackage(CreateLicense.class);
		// 设置对证书内容加密的对称密码
		CipherParam cipherParam = new DefaultCipherParam(licenseCommonParam.getStorePwd());
		// 参数1,2从哪个Class.getResource()获得密钥库;参数3密钥库的别名;参数4密钥库存储密码;参数5密钥库密码
		KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(
				CreateLicense.class, licenseCommonParam.getResource(), licenseCommonParam.getAlias(), licenseCommonParam.getStorePwd(), licenseCommonParam.getKeyPwd());
		LicenseParam licenseParams = new DefaultLicenseParam(licenseCommonParam.getSubject(),
				preference, privateStoreParam, cipherParam);
		return licenseParams;
	}

	/**
	 * 从外部表单拿到证书的内容
	 * @return
	 */
	public final static LicenseContent createLicenseContent(LicenseCommonParam licenseCommonParam , LicenseCommonContent licenseCommonContent) throws ParseException {
		DateFormat format = new SimpleDateFormat(DATEFORMAT);
		LicenseContent content = new LicenseContent();
		content.setSubject(licenseCommonParam.getSubject());
		content.setHolder(DEFAULTHOLDERANDISSUER);
		content.setIssuer(DEFAULTHOLDERANDISSUER);

		content.setIssued(format.parse(licenseCommonContent.getIssuedTime()));
		content.setNotBefore(format.parse(licenseCommonContent.getNotBefore()));
		content.setNotAfter(format.parse(licenseCommonContent.getNotAfter()));

		content.setConsumerType(licenseCommonContent.getConsumerType());
		content.setConsumerAmount(licenseCommonContent.getConsumerAmount());
		content.setInfo(licenseCommonContent.getInfo());

		// 扩展mac校验
		content.setExtra(licenseCommonContent.getLicenseCheckModel());
		return content;
	}
}