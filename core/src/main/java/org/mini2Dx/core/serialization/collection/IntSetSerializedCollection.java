package org.mini2Dx.core.serialization.collection;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;

public class IntSetSerializedCollection extends SerializedCollection<IntSet> {
	private final IntArray intArray = new IntArray();

	public IntSetSerializedCollection(IntSet collection) {
		super(collection);
		final IntSet.IntSetIterator intSetIterator = collection.iterator();
		while(intSetIterator.hasNext) {
			intArray.add(intSetIterator.next());
		}
	}

	@Override
	public Object get(int index) {
		return intArray.get(index);
	}

	@Override
	public int getLength() {
		return collection.size;
	}

	@Override
	public void dispose() {
		intArray.clear();
	}
}
