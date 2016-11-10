package com.zk.monitor;

import java.io.File;
import com.zk.zkconfig.ZkConfigInit;

public class FileWatch {
	
	private boolean isRunning = false;
	private static long lastModified;
	private static int period;
	
	static {
		period = 1000*15;//默认十五秒
		String dwp = ZkConfigInit.getPeriod();
		if(dwp !=null && !"".equals(dwp.trim())){
			period = Integer.parseInt(dwp);
		}
	}
	
	public long getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(long lastModified) {
		FileWatch.lastModified = lastModified;
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
	public void runWatch(final String filePath, final Monitor client){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
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
