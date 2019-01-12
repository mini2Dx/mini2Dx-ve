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

import com.badlogic.gdx.math.Vector2;
import org.mini2Dx.core.game.GameResizeListener;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

public abstract class Viewport implements GameResizeListener {
	private int boundX, boundY, boundWidth, boundHeight;
	private float scaleX, invScaleX;
	private float scaleY, invScaleY;

	private boolean initialised = false;

	private float previousScaleX, previousScaleY, previousTranslateX, previousTranslateY;
	private final Rectangle previousClip = new Rectangle();

	public void apply(Graphics g) {
		if(!initialised) {
			onResize(g.getWindowWidth(), g.getWindowHeight());
			initialised = true;
		}

		previousScaleX = g.getScaleX();
		previousScaleY = g.getScaleY();
		previousTranslateX = g.getTranslationX();
		previousTranslateY = g.getTranslationY();
		g.peekClip(previousClip);

		g.setScale(scaleX, scaleY);
		g.setTranslation(-boundX, -boundY);
		g.setClip(0, 0, boundWidth, boundHeight);
	}

	public void unapply(Graphics g) {
		g.setClip(previousClip);
		g.setTranslation(previousTranslateX, previousTranslateY);
		g.setScale(previousScaleX, previousScaleY);
	}

	public void toScreenCoordinates(Vector2 result, float worldX, float worldY) {
		result.x = (worldX * scaleX) + boundX;
		result.y = (worldY * scaleY) + boundY;
	}

	public void toWorldCoordinates(Vector2 result, float screenX, float screenY) {
		result.x = (screenX - boundX) * invScaleX;
		result.y = (screenY - boundY) * invScaleY;
	}

	public void toScreenCoordinates(Vector2 worldCoordinates) {
		toScreenCoordinates(worldCoordinates, worldCoordinates.x, worldCoordinates.y);
	}

	public void toWorldCoordinates(Vector2 screenCoordinates) {
		toWorldCoordinates(screenCoordinates, screenCoordinates.x, screenCoordinates.y);
	}

	protected void setBounds(int boundX, int boundY, int boundWidth, int boundHeight, float scaleX, float scaleY) {
		this.boundX = boundX;
		this.boundY = boundY;
		this.boundWidth = boundWidth;
		this.boundHeight = boundHeight;
		this.scaleX = scaleX;
		this.invScaleX = 1f / scaleX;
		this.scaleY = scaleY;
		this.invScaleY = 1f / scaleY;
	}

	public int getBoundX() {
		return boundX;
	}

	public int getBoundY() {
		return boundY;
	}

	public int getBoundWidth() {
		return boundWidth;
	}

	public int getBoundHeight() {
		return boundHeight;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getInvScaleX() {
		return invScaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getInvScaleY() {
		return invScaleY;
	}
}