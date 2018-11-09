package org.mini2Dx.core.serialization.collection;

import java.util.Collection;

public class CollectionSerializedCollection extends SerializedCollection<Collection> {
	private Object [] values;

	public CollectionSerializedCollection(Collection collection) {
		super(collection);
		values = collection.toArray();
	}

	@Override
	public Object get(int index) {
		return values[index];
	}

	@Override
	public int getLength() {
		return collection.size();
	}

	@Override
	public void dispose() {
		values = null;
	}
}
