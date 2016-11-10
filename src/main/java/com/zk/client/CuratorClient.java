package com.zk.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

public class CuratorClient {

	private CuratorFramework client;
	private String namespace;
	private ConcurrentHashMap<String, PathChildrenCache> pathCahcheMap = new ConcurrentHashMap<String, PathChildrenCache>();
	private ConcurrentHashMap<String, NodeCache> nodeCahcheMap = new ConcurrentHashMap<String, NodeCache>();

	public CuratorClient(String connectString, String namespace) {
		this.namespace = namespace;
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		client = CuratorFrameworkFactory.builder().connectString(connectString).sessionTimeoutMs(5000)
				.retryPolicy(retryPolicy).namespace(namespace).build();
		client.start();
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void createPersistent(String path) {
		try {
			client.create().creatingParentsIfNeeded().forPath(path);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void createEphemeral(String path) {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void delete(String path) {
		try {
			client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void delete(String path, int version) throws Exception {
		client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version).forPath(path);
	}

	public boolean checkExists(String path) throws Exception {
		Stat stat = null;
		stat = client.checkExists().forPath(path);
		if (stat == null) {
			return false;
		}
		return true;
	}

	public void set(String path, String value) throws Exception {
		client.setData().forPath(path, value.getBytes());
	}
	
	public void set(String path, byte[] value) throws Exception {
		client.setData().forPath(path, value);
	}

	public String get(String path) throws Exception {
		byte[] data;
		data = client.getData().forPath(path);
		if (data == null) {
			return "";
		}
		return new String(data);
	}

	public List<String> getChildren(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (NoNodeException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public boolean isConnected() {
		return client.getZookeeperClient().isConnected();
	}

	public void doClose() {
		client.close();
	}

	public void setPathChildrenCache(String path, final ChildListener listener) throws Exception {
		if (pathCahcheMap.contains(path)) {
			throw new Exception("The PathChildrenCache is already exsits!");
		}
		final PathChildrenCache pcc = new PathChildrenCache(client, path, true);
		pcc.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework clientTrue, PathChildrenCacheEvent event) throws Exception {
				listener.onEvent(pcc, event);
			}
		});
		pcc.start(StartMode.POST_INITIALIZED_EVENT);
		pathCahcheMap.put(path, pcc);
	}

	public List<String> getPathChildrenCacheData(String path) {
		if (!pathCahcheMap.contains(path)) {
			return null;
		}
		PathChildrenCache pcc = pathCahcheMap.get(path);
		List<String> list = new ArrayList<String>();
		for (ChildData data : pcc.getCurrentData()) {
			list.add(data.toString());
		}
		return list;
	}

	public Map<String, String> getChildrenData(String path) throws Exception {
		Map<String, String> children = new HashMap<String, String>();
		List<String> list = client.getChildren().forPath(path);
		for (String cpath : list) {
			String data = new String(client.getData().forPath(path + "/" + cpath));
			children.put(cpath, data);
		}
		return children;
	}

	public void close() throws IOException {
		PathChildrenCache pcc;
		for (String path : pathCahcheMap.keySet()) {
			pcc = pathCahcheMap.get(path);
			pcc.close();
		}
		NodeCache nc;
		for (String path : nodeCahcheMap.keySet()) {
			nc = nodeCahcheMap.get(path);
			nc.close();
		}
		client.close();
	}

	public void setNodeCache(String path, final NodeListener nodeListener) throws Exception {
		if (nodeCahcheMap.contains(path)) {
			throw new Exception("The NodeCache is already exsits!");
		}
		final NodeCache nc = new NodeCache(client, path);
		nc.start(true);
		nc.getListenable().addListener(new NodeCacheListener() {
			public void nodeChanged() throws Exception {
				nodeListener.nodeChanged(nc);
			}
		});
		nodeCahcheMap.put(path, nc);

	}

	public void masterSelect(String leaderPath) {
		LeaderSelector selector = new LeaderSelector(client, leaderPath, new LeaderSelectorListenerAdapter() {

			public void takeLeadership(CuratorFramework client) throws Exception {
				// TODO 自动生成的方法存根
				Thread.sleep(3000);
			}
		});
		selector.autoRequeue();
		selector.start();
	}

	public InterProcessMutex processMutex(String path) {
		return new InterProcessMutex(client, path);
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO 自动生成的方法存根
		CuratorClient zkclient = new CuratorClient("192.168.6.241", null);
		zkclient.createPersistent("/namenode");
		Thread.sleep(500);
		zkclient.delete("/namendoe");
	}

}
