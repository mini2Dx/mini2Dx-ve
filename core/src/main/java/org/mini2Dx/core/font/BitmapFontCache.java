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
package org.mini2Dx.core.font;

import com.badlogic.gdx.graphics.Color;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.LibGdxGraphics;

public class BitmapFontCache implements GameFontCache {
	private final BitmapFont bitmapFont;
	private final com.badlogic.gdx.graphics.g2d.BitmapFontCache bitmapFontCache;

	public BitmapFontCache(BitmapFont font) {
		this(font, font.useIntegerPositions());
	}

	public BitmapFontCache(BitmapFont font, boolean integer) {
		super();
		bitmapFont = font;
		bitmapFontCache = new com.badlogic.gdx.graphics.g2d.BitmapFontCache(font, integer);
	}

	@Override
	public void addText(CharSequence str, float x, float y) {
		bitmapFontCache.addText(str, x , y);
	}

	@Override
	public void addText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		bitmapFontCache.addText(str, x , y,  targetWidth, halign, wrap);
	}

	@Override
	public void clear() {
		bitmapFontCache.clear();
	}

	@Override
	public void draw(Graphics g) {
		bitmapFontCache.draw(((LibGdxGraphics) g).getSpriteBatch());
	}

	@Override
	public Color getColor() {
		return bitmapFontCache.getColor();
	}

	@Override
	public void setColor(Color color) {
		bitmapFontCache.setColor(color);
	}

	@Override
	public void setAllColors(Color colors) {
		bitmapFontCache.setColors(colors);
	}

	@Override
	public void setAllAlphas(float alpha) {
		bitmapFontCache.setAlphas(alpha);
	}

	@Override
	public void setText(CharSequence str, float x, float y) {
		bitmapFontCache.setText(str, x, y);
	}

	@Override
	public void setText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		bitmapFontCache.setText(str, x, y, targetWidth, halign, wrap);
	}

	@Override
	public void translate(float x, float y) {
		bitmapFontCache.translate(x, y);
	}

	@Override
	public void setPosition(float x, float y) {
		bitmapFontCache.setPosition(x, y);
	}

	@Override
	public GameFont getFont() {
		return bitmapFont;
	}
}
