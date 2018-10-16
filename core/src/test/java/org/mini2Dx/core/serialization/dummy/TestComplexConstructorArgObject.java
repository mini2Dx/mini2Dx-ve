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
package org.mini2Dx.core.serialization.dummy;

import org.mini2Dx.core.serialization.annotation.ConstructorArg;
import org.mini2Dx.core.serialization.annotation.Field;

import java.util.Objects;

public class TestComplexConstructorArgObject {
	private final int id;
	private float x, y, width, height;

	public TestComplexConstructorArgObject() {
		this(9);
	}

	public TestComplexConstructorArgObject(@ConstructorArg(clazz = Integer.class, name = "id") int id) {
		this(id, 0f, 0f, 0f, 0f);
	}

	public TestComplexConstructorArgObject(@ConstructorArg(clazz = Integer.class, name = "id") int id,
										   @ConstructorArg(clazz = Float.class, name = "x") float x,
										   @ConstructorArg(clazz = Float.class, name = "y") float y,
										   @ConstructorArg(clazz = Float.class, name = "width") float width,
										   @ConstructorArg(clazz = Float.class, name = "height") float height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@ConstructorArg(clazz = Integer.class, name = "id")
	public int getId() {
		return id;
	}

	@ConstructorArg(clazz = Float.class, name = "x")
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	@ConstructorArg(clazz = Float.class, name = "y")
	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	@ConstructorArg(clazz = Float.class, name = "width")
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	@ConstructorArg(clazz = Float.class, name = "height")
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestComplexConstructorArgObject that = (TestComplexConstructorArgObject) o;
		if(id != that.id) {
			System.err.println(id + " (id) !=" + that.id);
			return false;
		}
		if(Float.compare(that.x, x) != 0) {
			System.err.println(x + " (x) !=" + that.x);
			return false;
		}
		if(Float.compare(that.y, y) != 0) {
			System.err.println(y + " (y) !=" + that.y);
			return false;
		}
		if(Float.compare(that.width, width) != 0) {
			System.err.println(width + " (width) !=" + that.width);
			return false;
		}
		if(Float.compare(that.height, height) != 0) {
			System.err.println(height + " (height) !=" + that.height);
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, x, y, width, height);
	}
}
