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
package org.mini2Dx.core.graphics;

import com.badlogic.gdx.graphics.Texture;

/**
 * Splits a texture into multiple {@link Sprite} instances
 */
public class SpriteSheet {
	private final int totalColumns, totalRows, totalFrames;
	private final int frameWidth, frameHeight;
	private final Sprite [] frames;

	public SpriteSheet(Texture sheet, int frameWidth, int frameHeight) {
		this(new TextureRegion(sheet), frameWidth, frameHeight);
	}

	public SpriteSheet(TextureRegion sheet, int frameWidth, int frameHeight) {
		super();
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;

		totalColumns = sheet.getRegionWidth() / frameWidth;
		totalRows = sheet.getRegionHeight() / frameHeight;
		totalFrames = totalColumns * totalRows;

		frames = new Sprite[totalFrames];

		for (int i = 0; i < totalFrames; i++) {
			final int x = getFrameX(i) * frameWidth;
			final int y = getFrameY(i) * frameHeight;
			if (sheet == null) {
				continue;
			}
			frames[i] = new Sprite(new TextureRegion(sheet, x, y, frameWidth, frameHeight));
		}
	}

	public Sprite getSprite(int index) {
		return frames[index];
	}

	public Sprite getSprite(int x, int y) {
		final int index = (y * totalColumns) + x;
		return frames[index];
	}

	public int getFrameX(int index) {
		return index % totalColumns;
	}

	public int getFrameY(int index) {
		return index / totalColumns;
	}

	public int getTotalColumns() {
		return totalColumns;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public int getTotalFrames() {
		return totalFrames;
	}
}
