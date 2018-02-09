package de.schlichtherle.model;

import java.util.List;

/**
 * 扩展额外需校验参数
 * @author wucy
 */
public class LicenseCheckModel {

	/**
	 * 授权mac list集合
	 */
	private List<String> macAddressList;

	public LicenseCheckModel() {

	}

	public List<String> getMacAddressList() {
		return macAddressList;
	}

	public void setMacAddressList(List<String> macAddressList) {
		this.macAddressList = macAddressList;
	}
}