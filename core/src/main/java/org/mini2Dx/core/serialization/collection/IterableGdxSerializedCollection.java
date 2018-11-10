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
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Field;

/**
 * Utility class used during JSON/XML serialization
 */
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
