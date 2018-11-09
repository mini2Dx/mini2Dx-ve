package org.mini2Dx.core.serialization.collection;

import java.util.List;

public class ListSerializedCollection extends SerializedCollection<List> {

	public ListSerializedCollection(List collection) {
		super(collection);
	}

	@Override
	public Object get(int index) {
		return collection.get(index);
	}

	@Override
	public int getLength() {
		return collection.size();
	}

	@Override
	public void dispose() {
	}
}
