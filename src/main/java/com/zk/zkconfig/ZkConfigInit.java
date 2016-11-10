package com.zk.zkconfig;

import java.util.List;

public class ZkConfigInit {
	
	private static List<String> appPaths = null;
	private static String period;
	private ConfigPaser paser;
	
	public ZkConfigInit(String path) {
		paser = new ConfigPaser(path);
		appPaths = paser.getAllConfigValues("app_config_paths");
		period = paser.getProperty("dir_watch_period");
	}	

	public static List<String> getApps() {
		return appPaths;
	}

	public static String getPeriod() {
		return period;
	}

}
