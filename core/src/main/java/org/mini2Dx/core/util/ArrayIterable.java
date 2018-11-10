package org.mini2Dx.core.util;

import java.util.Iterator;

public class ArrayIterable<T> implements Iterable<T> {
	private final T [] array;

	public ArrayIterable(T[] array) {
		super();
		this.array = array;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int index;

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

			@Override
			public T next() {
				final T result = array[index];
				index++;
				return result;
			}

			@Override
			public void remove() {
				array[index] = null;
			}
		};
	}
}
