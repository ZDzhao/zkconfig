package com.zk.monitor;

import java.io.File;
import java.io.IOException;
import org.apache.curator.framework.recipes.cache.NodeCache;

import com.zk.client.CuratorClient;
import com.zk.client.NodeListener;
import com.zk.zkconfig.FileUtils;

public class FileMonitor implements Monitor {
	
	private static CuratorClient client;
	private static String filePath;
	private static String zkPath;

	@Override
	public void monitor(CuratorClient store, String Path) {
		this.init(store, Path);
		try {
			this.initDataToZk();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		this.monitorFile();

	}

	@Override
	public void init(CuratorClient store, String Path) {
		client = store;
		filePath = Path;
		zkPath = getZkPathByConfigPath(filePath);
	}

	@Override
	public void initDataToZk() throws Exception {
		File curFile = new File(filePath);
		byte[] value = FileUtils.readFile(curFile);
		
		if (client.checkExists(zkPath)) {
			client.set(zkPath, value);
		}else {
			client.createPersistent(zkPath);
			client.set(zkPath, value);
		}		
	}

	@Override
	public String getZkPathByConfigPath(String filePath) {
		if (filePath == null) {
			return null;
		}
		//example: /zkconfig/***.xml
		String zkPath =getLastName(filePath);
		return zkPath;
	}

	/**
	 * 1.得到配置文件的文件名作为zk的节点名称
	 * @param filePath(Linux文件路径格式，绝对路径)
	 * @return 配置文件的文件名
	 */
	public String getLastName(String filePath){
		if (filePath == null) {
			return null;
		}
		int beginIndex = filePath.lastIndexOf("\\");
		String path = filePath.substring(beginIndex, filePath.length());
		return path;
	}
	
	@Override
	public void monitorFile() {
		try {
			client.setNodeCache(zkPath, new NodeListener() {
				
				@Override
				public void nodeChanged(NodeCache nc) {
					updateFileFromzk();
				}
			});
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void updateFileFromzk() {
		try {
			byte[] data = client.get(zkPath).getBytes();
			FileUtils.writeFile(data, filePath);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace(); 
		}		
	}
	
	public static void updateFileFromWatch() {
		File curFile = new File(filePath);
		byte[] value = null;
		try {
			value = FileUtils.readFile(curFile);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}	
		try {
			client.set(zkPath, value);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	@Override
	public void updateFile() {
		updateFileFromWatch();
		
	}

}
