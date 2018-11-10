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
package org.mini2Dx.core.serialization.map.deserialize;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.Map;

/**
 * Utility class used during JSON/XML deserialization
 */
public abstract  class DeserializedMap<T> {
	protected final Field field;
	protected final Class<?> fieldClass;

	protected final T map;

	public DeserializedMap(Field field, Class<?> fieldClass, Object object) throws ReflectionException {
		super();
		this.field = field;
		this.fieldClass = fieldClass;

		if(field.isFinal()) {
			map = (T) field.get(object);
		} else {
			map = (T) (fieldClass.isInterface() ? ClassReflection.newInstance(getFallbackImplementation())
					: ClassReflection.newInstance(fieldClass));
			field.set(object, map);
		}
	}

	public abstract Class<? extends T> getFallbackImplementation();

	public abstract Class<?> getKeyClass();

	public abstract Class<?> getValueClass();

	public abstract void put(Object key, Object value);

	public static DeserializedMap getImplementation(Field field, Class<?> fieldClass, Object object) throws ReflectionException {
		if(Map.class.isAssignableFrom(fieldClass)) {
			return new MapDeserializedMap(field, fieldClass, object);
		} else if(ArrayMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<ArrayMap>(field, ArrayMap.class, field.getElementType(0), field.getElementType(1), object);
		} else if(IntMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<IntMap>(field, IntMap.class, Integer.class, field.getElementType(0), object);
		} else if(IntFloatMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<IntFloatMap>(field, IntFloatMap.class, Integer.class, Float.class, object);
		} else if(IntIntMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<IntIntMap>(field, IntIntMap.class, Integer.class, Integer.class, object);
		} else if(LongMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<LongMap>(field, LongMap.class, Long.class, field.getElementType(0), object);
		} else if(ObjectFloatMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<ObjectFloatMap>(field, ObjectFloatMap.class, field.getElementType(0), Float.class, object);
		} else if(ObjectIntMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<ObjectIntMap>(field, ObjectIntMap.class, field.getElementType(0), Integer.class, object);
		} else if(ObjectMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<ObjectMap>(field, ObjectMap.class, field.getElementType(0), field.getElementType(1), object);
		} else if(OrderedMap.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedMap<OrderedMap>(field, OrderedMap.class, field.getElementType(0), field.getElementType(1), object);
		}
		return null;
	}
}
