package com.zk.zkconfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigPaser {

	private String propPath;
	private Properties config = new Properties();
	
	public ConfigPaser(String path) {
		this.propPath = path;
		try {
			FileInputStream inStream = new FileInputStream(propPath);
			config.load(inStream);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key){
		return config.getProperty(key);
	}	
	
	public List<String> getAllConfigValues(String key){
		String[] values = config.getProperty(key).split(",");
		return Arrays.asList(values);
	}

	public static void main(String[] args) {
		ConfigPaser paser = new ConfigPaser("D:\\a.properties");
		System.out.println("get appinstance: " + paser.getProperty("app_config_paths"));
		System.out.println("get appinstance: " + paser.getAllConfigValues("app_config_paths"));
	}

}
