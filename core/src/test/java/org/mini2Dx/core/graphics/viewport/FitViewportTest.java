/**
 * Copyright (c) 2019 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.graphics.viewport;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import junit.framework.Assert;
import org.junit.Test;

public class FitViewportTest {
	private static final int VIEWPORT_WIDTH = 320;
	private static final int VIEWPORT_HEIGHT = 240;

	private final ScalingViewport viewport = new FitViewport(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

	@Test
	public void testOnResize() {
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1.5f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1.5f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(2f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(2f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.5f), viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.5f), viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 3, MathUtils.round(VIEWPORT_HEIGHT * 2.5f));
		Assert.assertEquals(32, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(2.5f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(2.5f, viewport.getScaleY(), 0.01f);
	}

	@Test
	public void testOnResizePowerOfTwo() {
		viewport.setPowerOfTwo(true);
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.25f), viewport.getX());
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.25f), viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(2f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(2f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.5f), viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		Assert.assertEquals(0, viewport.getX());
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.5f), viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(1f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(1f, viewport.getScaleY(), 0.01f);

		viewport.onResize(VIEWPORT_WIDTH * 6, VIEWPORT_HEIGHT * 4);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.25f), viewport.getX());
		Assert.assertEquals(0, viewport.getY());
		Assert.assertEquals(VIEWPORT_WIDTH, viewport.getWidth());
		Assert.assertEquals(VIEWPORT_HEIGHT, viewport.getHeight());
		Assert.assertEquals(4f, viewport.getScaleX(), 0.01f);
		Assert.assertEquals(4f, viewport.getScaleY(), 0.01f);
	}

	@Test
	public void testToScreenCoordinates() {
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		final Vector2 coordinates = new Vector2();
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		coordinates.set(1f, 1f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(1.5f, coordinates.x, 0.001f);
		Assert.assertEquals(1.5f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.5f), coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.5f), coordinates.y, 0.001f);
	}

	@Test
	public void testToScreenCoordinatesPowerOfTwo() {
		viewport.setPowerOfTwo(true);
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		final Vector2 coordinates = new Vector2();
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.25f), coordinates.x, 0.001f);
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.25f), coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(MathUtils.round(VIEWPORT_WIDTH * 0.5f), coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, 0f);
		viewport.toScreenCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(MathUtils.round(VIEWPORT_HEIGHT * 0.5f), coordinates.y, 0.001f);
	}

	@Test
	public void testToWorldCoordinates() {
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		final Vector2 coordinates = new Vector2();
		coordinates.set(0f, 0f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		coordinates.set(1.5f, 1.5f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(1f, coordinates.x, 0.001f);
		Assert.assertEquals(1f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		coordinates.set(2f, 2f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(1f, coordinates.x, 0.001f);
		Assert.assertEquals(1f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		coordinates.set(MathUtils.round(VIEWPORT_WIDTH * 0.5f), 0f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, MathUtils.round(VIEWPORT_HEIGHT * 0.5f));
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);
	}

	@Test
	public void testToWorldCoordinatesPowerOfTwo() {
		viewport.setPowerOfTwo(true);
		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		final Vector2 coordinates = new Vector2();
		coordinates.set(0f, 0f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(MathUtils.round(VIEWPORT_WIDTH * 1.5f), MathUtils.round(VIEWPORT_HEIGHT * 1.5f));
		coordinates.set(MathUtils.round(VIEWPORT_WIDTH * 0.25f), MathUtils.round(VIEWPORT_HEIGHT * 0.25f));
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT * 2);
		coordinates.set(2f, 2f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(1f, coordinates.x, 0.001f);
		Assert.assertEquals(1f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH * 2, VIEWPORT_HEIGHT);
		coordinates.set(MathUtils.round(VIEWPORT_WIDTH * 0.5f), 0f);
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);

		viewport.onResize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * 2);
		coordinates.set(0f, MathUtils.round(VIEWPORT_HEIGHT * 0.5f));
		viewport.toWorldCoordinates(coordinates);
		Assert.assertEquals(0f, coordinates.x, 0.001f);
		Assert.assertEquals(0f, coordinates.y, 0.001f);
	}
}
