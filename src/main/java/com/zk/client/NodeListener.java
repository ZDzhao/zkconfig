package com.zk.client;

import org.apache.curator.framework.recipes.cache.NodeCache;

public interface NodeListener {
	public void nodeChanged(NodeCache nc);

}
