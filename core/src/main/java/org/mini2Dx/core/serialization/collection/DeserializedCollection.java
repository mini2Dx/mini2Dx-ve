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

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Utility class used during JSON/XML deserialization
 */
public abstract class DeserializedCollection<T> {
	protected final Field field;
	protected final Class<?> fieldClass;

	protected final T collection;

	public DeserializedCollection(Field field, Class<?> fieldClass, Object object) throws ReflectionException {
		super();
		this.field = field;
		this.fieldClass = fieldClass;

		if(field.isFinal()) {
			collection = (T) field.get(object);
		} else {
			collection = (T) (fieldClass.isInterface() ? ClassReflection.newInstance(getFallbackImplementation())
					: ClassReflection.newInstance(fieldClass));
			field.set(object, collection);
		}
	}

	public abstract Class<? extends T> getFallbackImplementation();

	public abstract Class<?> getValueClass();

	public abstract void add(Object element);

	public static DeserializedCollection getImplementation(Field field, Class<?> fieldClass, Object object) throws ReflectionException, NoSuchMethodException {
		if(List.class.isAssignableFrom(fieldClass)) {
			return new ListDeserializedCollection(field, fieldClass, object);
		} else if(Set.class.isAssignableFrom(fieldClass)) {
			return new SetDeserializedCollection(field, fieldClass, object);
		} else if(Array.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<Array, Object>(Array.class, field.getElementType(0), field, fieldClass, object);
		} else if(BooleanArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<BooleanArray, Boolean>(BooleanArray.class, Boolean.class, field, fieldClass, object);
		} else if(ByteArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<ByteArray, Byte>(ByteArray.class, Byte.class, field, fieldClass, object);
		} else if(CharArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<CharArray, Character>(CharArray.class, Character.class, field, fieldClass, object);
		} else if(FloatArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<FloatArray, Float>(FloatArray.class, Float.class, field, fieldClass, object);
		} else if(IntArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<IntArray, Integer>(IntArray.class, Integer.class, field, fieldClass, object);
		} else if(IntSet.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<IntSet, Integer>(IntSet.class, Integer.class, field, fieldClass, object);
		} else if(LongArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<LongArray, Long>(LongArray.class, Long.class, field, fieldClass, object);
		} else if(OrderedSet.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<OrderedSet, Object>(OrderedSet.class, field.getElementType(0), field, fieldClass, object);
		} else if(ObjectSet.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<ObjectSet, Object>(ObjectSet.class, field.getElementType(0), field, fieldClass, object);
		} else if(ShortArray.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<ShortArray, Short>(ShortArray.class, Short.class, field, fieldClass, object);
		} else if(SortedIntList.class.isAssignableFrom(fieldClass)) {
			return new GdxDeserializedCollection<SortedIntList, Integer>(SortedIntList.class, Integer.class, field, fieldClass, object);
		} else if(Collection.class.isAssignableFrom(fieldClass)) {
			return new ListDeserializedCollection(field, fieldClass, object);
		} else {
			return null;
		}
	}
}
