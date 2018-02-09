
package de.schlichtherle.util;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * validate nets properties
 * @author wucy
 */
public class ListNets {


	/**
	 * validate all network's macs
	 * @param macAddress
	 * @return
	 * @throws SocketException
	 */
	public static boolean validateMacAddress(String macAddress)
			throws SocketException {
		boolean returnFlag = false;
		Enumeration<NetworkInterface> nets = NetworkInterface
				.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			byte[] mac = netint.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			if (mac != null) {
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i],
							(i < mac.length - 1) ? "-" : ""));
				}
			}
			if (sb.toString().equals(macAddress)) {
				returnFlag = true;
			}
		}
		return returnFlag;

	}

	/**
	 * validate all network's ip & macs
	 * @param ipAddress
	 * @param macAddress
	 * @return
	 * @throws SocketException
	 */
	public static boolean validatoIpAndMacAddress(String ipAddress,
			String macAddress) throws SocketException {
		boolean returnFlag = false;
		Enumeration<NetworkInterface> nets = NetworkInterface
				.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			byte[] mac = netint.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			if (mac != null) {
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i],
							(i < mac.length - 1) ? "-" : ""));
				}
			}
			if (sb.toString().equals(macAddress)) {
				Enumeration<InetAddress> inetAddresses = netint
						.getInetAddresses();
				String ip = "";
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					ip = inetAddress.getHostAddress();
					if (ipAddress.toString().equals(ip)) {
						returnFlag = true;
					}
				}
			}
		}
		return returnFlag;

	}
}