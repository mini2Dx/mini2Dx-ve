package org.mini2Dx.core.util;

import com.badlogic.gdx.utils.IntArray;

import java.util.Iterator;

public class IntArrayIterable implements Iterable {
	private final IntArray intArray;

	public IntArrayIterable(IntArray intArray) {
		super();
		this.intArray = intArray;
	}

	@Override
	public Iterator iterator() {
		return new Iterator() {
			int index;

			@Override
			public boolean hasNext() {
				return index < intArray.size;
			}

			@Override
			public Object next() {
				final int result = intArray.get(index);
				index++;
				return result;
			}

			@Override
			public void remove() {
				intArray.removeIndex(index);
				index--;
			}
		};
	}
}
