package com.zk.zkconfig;

import java.util.List;

import com.zk.client.CuratorClient;
import com.zk.monitor.FileMonitor;
import com.zk.monitor.FileWatch;
import com.zk.monitor.Monitor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String zkhost = "192.168.6.73";
    	String path = "D:\\a.properties";
    	CuratorClient client = new CuratorClient(zkhost, "config");
    	ZkConfigInit configInit = new ZkConfigInit(path);
    	   	
    	List<String> filePaths = configInit.getApps();
    	if (filePaths != null && filePaths.size() > 0) {
			for (String filePath : filePaths) {
				Monitor monitor = new FileMonitor();
				monitor.monitor(client, filePath);
				
				FileWatch watch = new FileWatch();
				watch.runWatch(filePath, monitor, configInit);
			}
		}
    	
        System.out.println( "Hello World!" );
    }
}
