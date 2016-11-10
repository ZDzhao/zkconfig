package com.zk.client;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface ChildListener {
	public void onEvent(PathChildrenCache pc,PathChildrenCacheEvent event);

}
