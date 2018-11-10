package org.mini2Dx.core.serialization.map.deserialize;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class used during JSON/XML deserialization
 */
public class GdxDeserializedMap<T> extends DeserializedMap<T> {
	private static final String LOGGING_TAG = GdxDeserializedMap.class.getSimpleName();

	private final Class<T> fallbackClass;
	private final Class<?> keyClass, valueClass;

	private Method putMethod;

	public GdxDeserializedMap(Field field, Class<T> fieldClass, Class<?> keyClass, Class<?> valueClass, Object object) throws ReflectionException {
		super(field, fieldClass, object);
		this.fallbackClass = fieldClass;
		this.keyClass = keyClass;
		this.valueClass = valueClass;

		for(Method method : fieldClass.getMethods()) {
			if(method.getName().equals("put")) {
				putMethod = method;
				break;
			}
		}
	}

	@Override
	public Class<? extends T> getFallbackImplementation() {
		return fallbackClass;
	}

	@Override
	public Class<?> getKeyClass() {
		return keyClass;
	}

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}

	@Override
	public void put(Object key, Object value) {
		try {
			putMethod.invoke(map, key, value);
		} catch (IllegalAccessException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		}
	}
}
