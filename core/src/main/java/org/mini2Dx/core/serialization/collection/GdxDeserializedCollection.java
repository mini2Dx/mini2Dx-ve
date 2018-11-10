/**
 * Copyright (c) 2018 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.serialization.collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class used during JSON/XML deserialization
 */
public class GdxDeserializedCollection<T, N> extends DeserializedCollection<T> {
	private static final String LOGGING_TAG = GdxDeserializedCollection.class.getSimpleName();

	private final Class<T> fallbackClass;
	private final Class<N> valueClass;
	private Method addMethod;

	public GdxDeserializedCollection(Class<T> fallbackClass, Class<N> valueClass, Field field, Class<?> fieldClass, Object object) throws ReflectionException, NoSuchMethodException {
		super(field, fieldClass, object);
		this.fallbackClass = fallbackClass;
		this.valueClass = valueClass;

		for(Method method : fieldClass.getMethods()) {
			method.setAccessible(true);
			if(method.getName().equals("add") && method.getParameterTypes().length == 1) {
				addMethod = method;
				break;
			}
		}

		if(addMethod == null) {
			throw new NoSuchMethodException("No such method add on " + fieldClass.getName());
		}
	}

	@Override
	public Class<? extends T> getFallbackImplementation() {
		return fallbackClass;
	}

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}

	@Override
	public void add(Object element) {
		try {
			addMethod.invoke(collection, (N) element);
		} catch (IllegalAccessException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Gdx.app.error(LOGGING_TAG, e.getMessage(), e);
		}
	}
}
