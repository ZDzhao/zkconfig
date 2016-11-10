package com.zk.monitor;

import com.zk.client.CuratorClient;


public interface Monitor {
	
	/**
	 * 通过配置文件路径，获取zk服务器上的配置路径
	 * @param path
	 * @return
	 */
	public String getZkPathByConfigPath(String path);
	/**
	 * 执行monitor业务方法
	 */
	public void monitor(CuratorClient store, String Path);
	/**
	 * 初始化，可根据需要具体实现
	 * @param store
	 * @param filePath
	 */
	public void init(CuratorClient store, String filePath);
	/**
	 * 初始化,将配置文件信息读入zk节点，可根据需要具体实现
	 * @throws Exception 
	 */
	public void initDataToZk() throws Exception;
	/**
	 * 监听磁盘配置文件变动
	 */
	public void monitorFile();
	/**
	 * 更新文件
	 */
	public void updateFile();
}
