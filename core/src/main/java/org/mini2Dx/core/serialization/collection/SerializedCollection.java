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

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class SerializedCollection<T> {
	protected final T collection;

	public SerializedCollection(T collection) {
		super();
		this.collection = collection;
	}

	public abstract Object get(int index);

	public abstract int getLength();

	public abstract void dispose();

	public static SerializedCollection getImplementation(Class<?> clazz, Object collection) throws NoSuchFieldException {
		if(List.class.isAssignableFrom(clazz)) {
			return new ListSerializedCollection((List) collection);
		} else if(Set.class.isAssignableFrom(clazz)) {
			return new CollectionSerializedCollection((Set) collection);
		} else if(Collection.class.isAssignableFrom(clazz)) {
			return new CollectionSerializedCollection((Collection) collection);
		}  else if(Array.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<Array>(Array.class, (Array) collection);
		} else if(BooleanArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<BooleanArray>(BooleanArray.class, (BooleanArray) collection);
		} else if(ByteArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<ByteArray>(ByteArray.class, (ByteArray) collection);
		} else if(CharArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<CharArray>(CharArray.class, (CharArray) collection);
		} else if(FloatArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<FloatArray>(FloatArray.class, (FloatArray) collection);
		} else if(IntArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<IntArray>(IntArray.class, (IntArray) collection);
		} else if(IntSet.class.isAssignableFrom(clazz)) {
			return new IntSetSerializedCollection((IntSet) collection);
		} else if(OrderedSet.class.isAssignableFrom(clazz)) {
			return new IterableGdxSerializedCollection<OrderedSet>(OrderedSet.class, (OrderedSet) collection);
		} else if(ObjectSet.class.isAssignableFrom(clazz)) {
			return new IterableGdxSerializedCollection<ObjectSet>(ObjectSet.class, (ObjectSet) collection);
		} else if(LongArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<LongArray>(LongArray.class, (LongArray) collection);
		} else if(ShortArray.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<ShortArray>(ShortArray.class, (ShortArray) collection);
		} else if(SortedIntList.class.isAssignableFrom(clazz)) {
			return new GdxSerializedCollection<SortedIntList>(SortedIntList.class, (SortedIntList) collection);
		}
		return null;
	}
}
