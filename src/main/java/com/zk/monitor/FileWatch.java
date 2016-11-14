package com.zk.monitor;

import java.io.File;
import com.zk.zkconfig.ZkConfigInit;

public class FileWatch {
	
	private boolean isRunning = false;
	private long lastModified;
	private int period = 1000*15;	
	
	public long getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public void watch(String filePath, Monitor client, int period) throws Exception{
		if (filePath == null) {
			throw new Exception("filepath is null!");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("file doesn't exisst!");
		}
		if (!isRunning) {
			setLastModified(file.lastModified());
			while (file.exists()) {
				isRunning = true;
				long lm = file.lastModified();
				if (lm > lastModified) {
					publishConfig(client);
					setLastModified(lm);
				}
				Thread.sleep(period);
			}
			if (!file.exists() && isRunning) {
				isRunning = false;
			}
		}
	}
	
	public void publishConfig( Monitor client) {
		client.updateFile();
	}
	public void runWatch(final String filePath, final Monitor client, final ZkConfigInit configInit){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String dwp = configInit.getPeriod();
					if(dwp !=null && !"".equals(dwp.trim())){
						period = Integer.parseInt(dwp);
					}
					
					watch(filePath, client, period);
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
