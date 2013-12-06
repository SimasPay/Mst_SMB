package com.mfino.ccpayment.util;

import com.mfino.util.ConfigurationUtil;

public class IPFilterting {

	public static boolean validip(String ip) {
		Boolean b = new Boolean(ConfigurationUtil.getNSIARemoteIPCheck());
		if (b.equals(new Boolean("true"))) {
			String serverip = ConfigurationUtil.getNSIARemoteAddress();
			if (ip.equals(serverip)) {
				return true;
			}
			return false;
		}
		return true;
	}
}
