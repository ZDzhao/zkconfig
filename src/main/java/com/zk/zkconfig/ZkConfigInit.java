package com.zk.zkconfig;

import java.util.List;

public class ZkConfigInit {
	
	private  List<String> appPaths = null;
	private  String period;
	private ConfigPaser paser;
	
	public ZkConfigInit(String path) {
		paser = new ConfigPaser(path);
		appPaths = paser.getAllConfigValues("app_config_paths");
		period = paser.getProperty("dir_watch_period");
	}	

	public  List<String> getApps() {
		return appPaths;
	}

	public  String getPeriod() {
		return period;
	}

}
