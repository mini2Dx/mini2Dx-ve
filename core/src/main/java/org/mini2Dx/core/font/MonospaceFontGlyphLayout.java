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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.core.graphics.TextureRegion;

public class MonospaceFontGlyphLayout implements FontGlyphLayout {
	private final Array<MonospaceGlyph> glyphs = new Array<MonospaceGlyph>();
	private final MonospaceFont monospaceFont;
	private final MonospaceFont.FontParameters fontParameters;

	private float maxX, maxY;

	public MonospaceFontGlyphLayout(MonospaceFont monospaceFont) {
		super();
		this.monospaceFont = monospaceFont;
		fontParameters = monospaceFont.getFontParameters();
	}

	@Override
	public void setText(CharSequence str) {
		setText(str, Color.BLACK, -1f, Align.left, true);
	}

	@Override
	public void setText(CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
		maxX = -1f;
		maxY = -1f;

		switch(halign) {
		default:
		case Align.left:
			setTextLeftAlign(str, color, targetWidth, wrap);
			break;
		case Align.right:
			setTextRightAlign(str, color, targetWidth, wrap);
			break;
		case Align.center:
			setTextCenterAlign(str, color, targetWidth, wrap);
			break;
		}

		if(glyphs.size > str.length()) {
			for(int i = glyphs.size - 1; i >= str.length(); i--) {
				final MonospaceGlyph glyph = glyphs.removeIndex(i);
				glyph.release();
			}
		}

		for(int i = 0; i < glyphs.size; i++) {
			final MonospaceGlyph glyph = getGlyph(i);
			if(glyph.glyphChar == '\n' || glyph.glyphChar == '\r') {
				continue;
			}
			maxX = Math.max(maxX, glyph.x + fontParameters.characterWidth);
			maxY = Math.max(maxY, glyph.y + fontParameters.lineHeight);
		}

		if(halign == Align.center && targetWidth >= 0f) {
			maxX = targetWidth;
		}
	}

	private void setTextLeftAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		final int estimateMaxCharsPerLine;
		if(targetWidth < 0f) {
			targetWidth = Float.MAX_VALUE;
			estimateMaxCharsPerLine = Integer.MAX_VALUE;
		} else {
			estimateMaxCharsPerLine = MathUtils.round(targetWidth / (fontParameters.characterWidth + fontParameters.spacing));
		}

		float yOffset = 0f;
		float xOffset = 0f;

		for(int i = 0; i < str.length();) {
			final char startChar = str.charAt(i);
			if(startChar == '\n' || startChar == '\r' || Character.isWhitespace(startChar)) {
				final MonospaceGlyph glyph = getGlyph(i);
				glyph.color.set(color);
				glyph.glyphChar = startChar;
				glyph.x = -1f;
				glyph.y = -1f;
				glyph.textureRegion = null;
				i++;
				continue;
			}

			final int totalChars = Math.max(1, calculateMaxCharactersBeforeWrap(str, i, estimateMaxCharsPerLine, targetWidth));

			for(int j = i; j < i + totalChars && j < str.length(); j++) {
				final char c = str.charAt(j);

				final MonospaceGlyph glyph = getGlyph(j);
				glyph.color.set(color);
				glyph.glyphChar = c;
				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.textureRegion = monospaceFont.getTextureRegion(c);

				xOffset += fontParameters.characterWidth + fontParameters.spacing;
			}

			if(!wrap) {
				return;
			}
			xOffset = 0f;
			yOffset += fontParameters.lineHeight;
			i += totalChars;
		}
	}

	private void setTextRightAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		final int charactersPerLine;

		if(targetWidth < 0f) {
			int maxCharsPerLine = 0;

			for(int i = 0; i < str.length();) {
				if(str.charAt(i) == '\n' || str.charAt(i) == '\r' || Character.isWhitespace(str.charAt(i))) {
					i++;
					continue;
				}
				final int totalChars = calculateMaxCharactersBeforeWrap(str, i, Integer.MAX_VALUE, Float.MAX_VALUE);
				maxCharsPerLine = Math.max(totalChars, maxCharsPerLine);
				i += totalChars;
			}

			if(maxCharsPerLine == 0) {
				return;
			} else {
				charactersPerLine = maxCharsPerLine;
			}

			targetWidth = (charactersPerLine * fontParameters.characterWidth) + (charactersPerLine * fontParameters.spacing) - fontParameters.spacing;
		} else {
			charactersPerLine = MathUtils.round(targetWidth / (fontParameters.characterWidth + fontParameters.spacing));
		}

		float yOffset = 0f;
		float xOffset = targetWidth - fontParameters.characterWidth;

		for(int i = 0; i < str.length();) {
			final char startChar = str.charAt(i);
			if(startChar == '\n' || startChar == '\r' || Character.isWhitespace(startChar)) {
				final MonospaceGlyph glyph = getGlyph(i);
				glyph.color.set(color);
				glyph.glyphChar = startChar;
				glyph.x = -1f;
				glyph.y = -1f;
				glyph.textureRegion = null;
				i++;
				continue;
			}

			final int totalChars = Math.max(1, calculateMaxCharactersBeforeWrap(str, i, charactersPerLine, targetWidth));

			for(int j = i + totalChars - 1; j >= i; j--) {
				final char c = str.charAt(j);
				final MonospaceGlyph glyph = getGlyph(j);
				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.glyphChar = c;
				glyph.color.set(color);
				glyph.textureRegion = monospaceFont.getTextureRegion(c);

				xOffset -= fontParameters.characterWidth + fontParameters.spacing;
			}
			if(!wrap) {
				return;
			}
			xOffset = targetWidth - fontParameters.characterWidth;
			yOffset += fontParameters.lineHeight;
			i += totalChars;
		}
	}

	private void setTextCenterAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		final int charactersPerLine;

		if(targetWidth < 0f) {
			int maxCharsPerLine = 0;

			for(int i = 0; i < str.length();) {
				if(str.charAt(i) == '\n' || str.charAt(i) == '\r' || Character.isWhitespace(str.charAt(i))) {
					i++;
					continue;
				}
				final int totalChars = calculateMaxCharactersBeforeWrap(str, i, Integer.MAX_VALUE, Float.MAX_VALUE);
				maxCharsPerLine = Math.max(totalChars, maxCharsPerLine);
				i += totalChars;
			}

			if(maxCharsPerLine == 0) {
				return;
			} else {
				charactersPerLine = maxCharsPerLine;
			}

			targetWidth = (charactersPerLine * fontParameters.characterWidth) + (charactersPerLine * fontParameters.spacing) - fontParameters.spacing;
		} else {
			charactersPerLine = MathUtils.round(targetWidth / (fontParameters.characterWidth + fontParameters.spacing));
		}

		float yOffset = 0f;

		for(int i = 0; i < str.length();) {
			final char startChar = str.charAt(i);
			if(startChar == '\n' || startChar == '\r' || Character.isWhitespace(startChar)) {
				final MonospaceGlyph glyph = getGlyph(i);
				glyph.color.set(color);
				glyph.glyphChar = startChar;
				glyph.x = -1f;
				glyph.y = -1f;
				glyph.textureRegion = null;
				i++;
				continue;
			}

			final int totalChars = Math.max(1, calculateMaxCharactersBeforeWrap(str, i, charactersPerLine, targetWidth));
			final float lineWidth = (totalChars * fontParameters.characterWidth) + (totalChars * fontParameters.spacing) - fontParameters.spacing;

			float xOffset = MathUtils.round((targetWidth * 0.5f) - (lineWidth * 0.5f));

			for(int j = i; j < i + totalChars; j++) {
				final char c = str.charAt(j);
				final MonospaceGlyph glyph = getGlyph(j);
				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.glyphChar = c;
				glyph.color.set(color);
				glyph.textureRegion = monospaceFont.getTextureRegion(c);

				xOffset += fontParameters.characterWidth + fontParameters.spacing;
			}

			if(!wrap) {
				return;
			}
			yOffset += fontParameters.lineHeight;
			i += totalChars;
		}
	}

	/**
	 *
	 * @param str
	 * @param from
	 * @return The amount of characters to render before the wrap
	 */
	public int calculateMaxCharactersBeforeWrap(CharSequence str, int from, int estimate, float targetWidth) {
		float x = 0f;

		for(int i = from; i < str.length(); i++) {
			final char c = str.charAt(i);
			switch(c) {
			case '\r':
			case '\n':
				return i - from;
			}

			if(x + fontParameters.characterWidth > targetWidth) {
				if(Character.isWhitespace(c)) {
					return i - from;
				}
				if(c == '\n' || c == '\r') {
					return i - from;
				}

				//Scan backwards for first space
				for(int j = i - 1; j >= from; j--) {
					final char previousChar = str.charAt(j);
					if(Character.isWhitespace(previousChar)) {
						return j - from;
					}
				}
				return i - from;
			}

			x += fontParameters.characterWidth + fontParameters.spacing;
		}
		return Math.min(estimate, str.length() - from);
	}

	@Override
	public void reset() {
		while(glyphs.size > 0) {
			final MonospaceGlyph run = glyphs.removeIndex(0);
			run.release();
		}

		maxX = -1f;
		maxY = -1f;
	}

	@Override
	public void dispose() {
		reset();
	}

	@Override
	public float getWidth() {
		return maxX;
	}

	@Override
	public float getHeight() {
		return maxY;
	}

	@Override
	public GameFont getFont() {
		return monospaceFont;
	}

	public void transferGlyphsTo(Array<MonospaceGlyph> result, float x, float y) {
		while(glyphs.size > 0) {
			final MonospaceGlyph glyph = glyphs.removeIndex(0);
			glyph.x += x;
			glyph.y += y;
			result.add(glyph);
		}
	}

	public Array<MonospaceGlyph> getGlyphs() {
		return glyphs;
	}

	private MonospaceGlyph getGlyph(int index) {
		while(index >= glyphs.size) {
			final MonospaceGlyph result = MonospaceGlyph.allocate();
			glyphs.add(result);
		}
		return glyphs.get(index);
	}
}
