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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.graphics.Graphics;

public class MonospaceFontCache implements GameFontCache {
	private final Array<MonospaceGlyph> glyphs = new Array<MonospaceGlyph>();
	private final MonospaceFont monospaceFont;
	private final MonospaceFontGlyphLayout glyphLayout;

	private final Color color = new Color(Color.BLACK);
	private float x, y;

	public MonospaceFontCache(MonospaceFont monospaceFont) {
		super();
		this.monospaceFont = monospaceFont;
		glyphLayout = new MonospaceFontGlyphLayout(monospaceFont);
	}

	@Override
	public void addText(CharSequence str, float x, float y) {
		glyphLayout.setText(str, color, -1f, Align.left, true);
		glyphLayout.transferGlyphsTo(glyphs);
	}

	@Override
	public void addText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		glyphLayout.setText(str, color, targetWidth, halign, wrap);
		glyphLayout.transferGlyphsTo(glyphs);
	}

	@Override
	public void clear() {
		while(glyphs.size > 0) {
			final MonospaceGlyph glyph = glyphs.removeIndex(0);
			glyph.release();
		}
	}

	@Override
	public void draw(Graphics g) {
		monospaceFont.draw(g, glyphs, x, y, null);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color.set(color);
	}

	@Override
	public void setAllColors(Color color) {
		for(int i = 0; i < glyphs.size; i++) {
			glyphs.get(i).color.set(color);
		}
	}

	@Override
	public void setText(CharSequence str, float x, float y) {
		clear();
		glyphLayout.setText(str, color, -1f, Align.left, true);
		glyphLayout.transferGlyphsTo(glyphs);
	}

	@Override
	public void setText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		clear();
		glyphLayout.setText(str, color, targetWidth, halign, wrap);
		glyphLayout.transferGlyphsTo(glyphs);
	}

	@Override
	public void translate(float x, float y) {
		this.x += x;
		this.y += y;
	}

	@Override
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public GameFont getFont() {
		return monospaceFont;
	}
}
