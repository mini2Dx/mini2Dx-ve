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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.graphics.GlyphLayout;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.LibGdxGraphics;

/**
 * BMFont implementation of {@link GameFont}. See <a href="http://www.angelcode.com/products/bmfont/">here</a>
 */
public class BitmapFont extends com.badlogic.gdx.graphics.g2d.BitmapFont implements GameFont {
	private final BitmapFontGlyphLayout sharedGlyphLayout;

	public BitmapFont() {
		super(true);
		sharedGlyphLayout = (BitmapFontGlyphLayout) newGlyphLayout();
	}

	public BitmapFont(FileHandle fileHandle) {
		super(fileHandle, true);
		sharedGlyphLayout = (BitmapFontGlyphLayout) newGlyphLayout();
	}

	public BitmapFont (BitmapFontData data, TextureRegion region, boolean integer) {
		this(data, region != null ? Array.with(region) : null, integer);
	}

	public BitmapFont (BitmapFontData data, Array<TextureRegion> pageRegions, boolean integer) {
		super(data, pageRegions, integer);
		sharedGlyphLayout = (BitmapFontGlyphLayout) newGlyphLayout();
	}

	@Override
	public void draw(Graphics g, String str, float x, float y) {
		draw(((LibGdxGraphics) g).getSpriteBatch(), str, x, y);
	}

	@Override
	public void draw(Graphics g, String str, float x, float y, float targetWidth) {
		draw(g, str, x, y, targetWidth, Align.left, true);
	}

	@Override
	public void draw(Graphics g, String str, float x, float y, float targetWidth, int horizontalAlignment, boolean wrap) {
		draw(((LibGdxGraphics) g).getSpriteBatch(), str, x, y, targetWidth, horizontalAlignment, wrap);
	}

	@Override
	public FontGlyphLayout newGlyphLayout() {
		return new BitmapFontGlyphLayout(this);
	}

	@Override
	public FontGlyphLayout getSharedGlyphLayout() {
		return sharedGlyphLayout;
	}

	@Override
	public GameFontCache newCache() {
		return new BitmapFontCache(this, useIntegerPositions());
	}

	@Override
	public boolean useIntegerPositions() {
		return super.usesIntegerPositions();
	}
}
