package org.mini2Dx.core.serialization.collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IterableGdxSerializedCollection <T extends Iterable> extends SerializedCollection<T> {
	private static final String LOGGING_TAG = IterableGdxSerializedCollection.class.getSimpleName();

	private final Array elements = new Array();
	private Field sizeField;

	public IterableGdxSerializedCollection(Class<T> clazz, T collection) throws NoSuchFieldException {
		super(collection);

		sizeField = clazz.getField("size");
		for(Object obj : collection) {
			elements.add(obj);
		}
	}

	@Override
	public Object get(int index) {
		return elements.get(index);
	}

	@Override
	public int getLength() {
		try {
			return (int) sizeField.get(collection);
		} catch (IllegalAccessException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		}
		return -1;
	}

	@Override
	public void dispose() {
		elements.clear();
	}
}
