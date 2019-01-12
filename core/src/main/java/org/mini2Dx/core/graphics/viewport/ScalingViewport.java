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
import org.mini2Dx.core.util.Scaling;

/**
 * Base class for implementing {@link Viewport} instances that use {@link Scaling}
 */
public class ScalingViewport extends Viewport {
	private final Scaling scaling;
	private final float worldWidth, worldHeight;
	private final Vector2 size = new Vector2();
	private final Vector2 scale = new Vector2();

	private boolean powerOfTwo;

	public ScalingViewport(Scaling scaling, boolean powerOfTwo, float worldWidth, float worldHeight) {
		super();
		this.scaling = scaling;
		this.powerOfTwo = powerOfTwo;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	@Override
	public void onResize(int width, int height) {
		scaling.apply(size, scale, powerOfTwo, worldWidth, worldHeight, width, height);

		final int viewWidth = MathUtils.round(size.x);
		final int viewHeight = MathUtils.round(size.y);

		setBounds((width - viewWidth) / MathUtils.round(2 * scale.x), (height - viewHeight) / MathUtils.round(2 * scale.y),
				MathUtils.round(worldWidth), MathUtils.round(worldHeight), scale.x, scale.y);
	}

	public boolean isPowerOfTwo() {
		return powerOfTwo;
	}

	public void setPowerOfTwo(boolean powerOfTwo) {
		this.powerOfTwo = powerOfTwo;
	}
}
