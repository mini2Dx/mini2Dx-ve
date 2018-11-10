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
package org.mini2Dx.core.serialization.map.serialize;

import com.badlogic.gdx.utils.*;

import java.util.Map;

/**
 * Utility class used during JSON/XML serialization
 */
public abstract class SerializedMap<T> {
	protected final T map;

	public SerializedMap(T map) {
		super();
		this.map = map;
	}

	public abstract Object get(Object key);

	public abstract int getSize();

	public abstract Iterable keys();

	public static SerializedMap getImplementation(Class<?> clazz, Object map) throws NoSuchFieldException {
		if(ArrayMap.class.isAssignableFrom(clazz)) {
			return new ArrayMapSerializedMap((ArrayMap) map);
		} else if(IntFloatMap.class.isAssignableFrom(clazz)) {
			return new IntFloatMapSerializedMap((IntFloatMap) map);
		} else if(IntIntMap.class.isAssignableFrom(clazz)) {
			return new IntIntMapSerializedMap((IntIntMap) map);
		} else if(IntMap.class.isAssignableFrom(clazz)) {
			return new IntMapSerializedMap((IntMap) map);
		} else if(LongMap.class.isAssignableFrom(clazz)) {
			return new LongMapSerializedMap((LongMap) map);
		} else if(ObjectFloatMap.class.isAssignableFrom(clazz)) {
			return new ObjectFloatMapSerializedMap((ObjectFloatMap) map);
		} else if(ObjectIntMap.class.isAssignableFrom(clazz)) {
			return new ObjectIntMapSerializedMap((ObjectIntMap) map);
		} else if(ObjectMap.class.isAssignableFrom(clazz)) {
			return new ObjectMapSerializedMap((ObjectMap) map);
		} else if(OrderedMap.class.isAssignableFrom(clazz)) {
			return new OrderedMapSerializedMap((OrderedMap) map);
		} else if(Map.class.isAssignableFrom(clazz)) {
			return new MapSerializedMap((Map) map);
		}
		return null;
	}
}
