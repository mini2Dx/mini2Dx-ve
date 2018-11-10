package org.mini2Dx.core.serialization.map.serialize;

import com.badlogic.gdx.utils.OrderedMap;

/**
 * Utility class used during JSON/XML serialization
 */
public class OrderedMapSerializedMap extends SerializedMap<OrderedMap> {

	public OrderedMapSerializedMap(OrderedMap map) {
		super(map);
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public int getSize() {
		return map.size;
	}

	@Override
	public Iterable keys() {
		return map.keys();
	}
}
