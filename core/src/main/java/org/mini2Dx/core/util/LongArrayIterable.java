package org.mini2Dx.core.util;

import com.badlogic.gdx.utils.LongArray;

import java.util.Iterator;

public class LongArrayIterable implements Iterable {
	private final LongArray longArray;

	public LongArrayIterable(LongArray longArray) {
		super();
		this.longArray = longArray;
	}

	@Override
	public Iterator iterator() {
		return new Iterator() {
			int index;

			@Override
			public boolean hasNext() {
				return index < longArray.size;
			}

			@Override
			public Object next() {
				final long result = longArray.get(index);
				index++;
				return result;
			}

			@Override
			public void remove() {
				longArray.removeIndex(index);
				index--;
			}
		};
	}
}
