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
	}

	private void setTextLeftAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		if(targetWidth < 0f) {
			targetWidth = Float.MAX_VALUE;
		}

		float yOffset = 0f;
		float xOffset = 0f;

		for(int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			final MonospaceGlyph glyph = getGlyph(i);
			glyph.color.set(color);
			glyph.glyphChar = c;

			if(c == '\n' || c == '\r') {
				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.textureRegion = null;

				xOffset = 0f;
				yOffset += fontParameters.lineHeight;
				continue;
			}

			if(xOffset + fontParameters.characterWidth >= targetWidth) {
				if(!wrap) {
					return;
				}
				xOffset = 0f;
				yOffset += fontParameters.lineHeight;
			}

			glyph.x = xOffset;
			glyph.y = yOffset;
			glyph.textureRegion = monospaceFont.getTextureRegion(c);

			xOffset += fontParameters.characterWidth + fontParameters.spacing;

			if(xOffset >= targetWidth) {
				if(!wrap) {
					return;
				}
				xOffset = 0f;
				yOffset += fontParameters.lineHeight;
			}
		}
	}

	private void setTextRightAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		final int charactersPerLine;
		final boolean ignoreLineBreaks = targetWidth < 0f;

		if(targetWidth < 0f) {
			int maxCharsPerLine = 0;
			int charCount = 0;

			for(int i = 0; i < str.length(); i++) {
				final char c = str.charAt(i);
				if(c == '\r' || c == '\n') {
					maxCharsPerLine = Math.max(charCount, maxCharsPerLine);
					charCount = 0;
					continue;
				}
				charCount++;
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
			if(ignoreLineBreaks && str.charAt(i) == '\n') {
				i += 1;
				continue;
			}

			int totalChars = 0;
			int start = Math.min(str.length() - 1, i + charactersPerLine - 1);

			for(int j = i; j <= start && j < str.length(); j++) {
				final char c = str.charAt(j);
				if(c == '\n' || c == '\r') {
					start = j - 1;
					totalChars += 1;
					break;
				}
			}

			for(int j = start; j >= i; j--) {
				totalChars++;

				final char c = str.charAt(j);
				final MonospaceGlyph glyph = getGlyph(j);
				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.glyphChar = c;
				glyph.color.set(color);

				if(c == '\n' || c == '\r') {
					glyph.textureRegion = null;

					xOffset = targetWidth - fontParameters.characterWidth;
					yOffset += fontParameters.lineHeight;
					break;
				}

				glyph.x = xOffset;
				glyph.y = yOffset;
				glyph.textureRegion = monospaceFont.getTextureRegion(c);

				xOffset -= fontParameters.characterWidth + fontParameters.spacing;

				if(xOffset < 0f) {
					if(!wrap) {
						return;
					}
					xOffset = targetWidth - fontParameters.characterWidth;
					yOffset += fontParameters.lineHeight;
				}
			}
			i += totalChars;
		}
	}

	private void setTextCenterAlign(CharSequence str, Color color, float targetWidth, boolean wrap) {
		final int charactersPerLine;

		if(targetWidth < 0f) {
			int maxCharsPerLine = 0;
			int charCount = 0;

			for(int i = 0; i < str.length(); i++) {
				final char c = str.charAt(i);
				if(c == '\r' || c == '\n') {
					maxCharsPerLine = Math.max(charCount, maxCharsPerLine);
					charCount = 0;
					continue;
				}
				charCount++;
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
			if(str.charAt(i) == '\n') {
				final MonospaceGlyph glyph = getGlyph(i);
				glyph.x = -1f;
				glyph.y = yOffset;
				glyph.glyphChar = str.charAt(i);
				glyph.color.set(color);
				glyph.textureRegion = null;

				i += 1;
				continue;
			}

			int totalChars = Math.min(str.length() - i, charactersPerLine);

			for(int j = i + 1; j <= i + charactersPerLine && j < str.length(); j++) {
				final char c = str.charAt(j);
				if(c == '\n' || c == '\r') {
					totalChars = j - i;
					break;
				}
			}

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

	public void transferGlyphsTo(Array<MonospaceGlyph> result) {
		while(glyphs.size > 0) {
			result.add(glyphs.removeIndex(0));
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
