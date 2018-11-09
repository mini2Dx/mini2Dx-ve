package org.mini2Dx.core.serialization.collection;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GdxSerializedCollection<T> extends SerializedCollection<T> {
	private static final String LOGGING_TAG = GdxSerializedCollection.class.getSimpleName();

	private Method getMethod;
	private Field sizeField;

	public GdxSerializedCollection(Class<T> clazz, T collection) throws NoSuchFieldException {
		super(collection);

		for(Method method : clazz.getMethods()) {
			method.setAccessible(true);
			if(method.getName().equals("get")) {
				getMethod = method;
				break;
			}
		}
		sizeField = clazz.getField("size");
	}

	@Override
	public Object get(int index) {
		try {
			return getMethod.invoke(collection, index);
		} catch (IllegalAccessException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		}
		return null;
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
	}
}
