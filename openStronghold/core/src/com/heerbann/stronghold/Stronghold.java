package com.heerbann.stronghold;

import java.util.Comparator;

import com.heerbann.stronghold.assets.StrongholdAssetManager;
import com.heerbann.stronghold.utils.ZIndexProperty;

public class Stronghold {
	
	public static final ZSort comperator = new ZSort();

	private static class ZSort implements Comparator<ZIndexProperty> {

		@Override
		public int compare (ZIndexProperty n1, ZIndexProperty n2) {
			return (n1.getZ() < n2.getZ()) ? -1 : (n1.getZ() > n2.getZ()) ? 1 : 0;
		}

	}
		
	public static final StrongholdAssetManager manager = new StrongholdAssetManager();
	
	public static EntryPoint app;
	public static InputManager inputs;
}
