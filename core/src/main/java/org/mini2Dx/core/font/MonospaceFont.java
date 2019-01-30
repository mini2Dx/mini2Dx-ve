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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.TextureRegion;

/**
 * Similar to {@link BitmapFont} except every character is a fixed-width which simplifies rendering calculations
 */
public class MonospaceFont implements GameFont {
	private final FontParameters fontParameters;
	private TextureRegion [][] characterTextures;
	private int charactersPerRow, charactersPerColumn;

	public MonospaceFont(FontParameters fontParameters) {
		super();
		this.fontParameters = fontParameters;

		if(fontParameters.characterWidth < 0) {
			fontParameters.characterWidth = fontParameters.frameWidth;
		}
		if(fontParameters.lineHeight < 0) {
			fontParameters.lineHeight = fontParameters.frameHeight;
		}
	}

	public boolean load(AssetManager assetManager) {
		if(characterTextures != null) {
			return true;
		}
		final TextureAtlas textureAtlas = assetManager.get(fontParameters.textureAtlasPath, TextureAtlas.class);
		if(textureAtlas == null) {
			throw new MdxException("No such texture atlas '" + fontParameters.textureAtlasPath + "'");
		}
		final TextureRegion textureRegion = new TextureRegion(textureAtlas.findRegion(fontParameters.texturePath));
		charactersPerRow = textureRegion.getRegionWidth() / fontParameters.frameWidth;
		charactersPerColumn = textureRegion.getRegionHeight() / fontParameters.frameHeight;

		characterTextures = new TextureRegion[charactersPerRow][charactersPerColumn];
		for(int x = 0; x < charactersPerRow; x++) {
			for(int y = 0; y < charactersPerColumn; y++) {
				characterTextures[x][y] = new TextureRegion(textureRegion, x * fontParameters.frameWidth, y *
						fontParameters.frameHeight, fontParameters.frameWidth, fontParameters.frameHeight);
			}
		}
		return true;
	}

	@Override
	public void draw(Graphics g, String str, float x, float y) {
		draw(g, str, x, y, -1f);
	}

	@Override
	public void draw(Graphics g, String str, float x, float y, float renderWidth) {
		draw(g, str, x, y, renderWidth, Align.left);
	}

	@Override
	public void draw(Graphics g, String str, float x, float y, float renderWidth, int horizontalAlignment) {
		draw(g, str, x, y, renderWidth, horizontalAlignment, null);
	}

	@Override
	public FontGlyphLayout newGlyphLayout() {
		return new MonospaceFontGlyphLayout(this);
	}

	@Override
	public GameFontCache newCache() {
		return new MonospaceFontCache(this);
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public void setColor(Color color) {

	}

	public void draw(Graphics g, String str, float x, float y, float renderWidth, FontRenderListener listener) {
		draw(g, str, x, y, renderWidth, Align.left, listener);
	}

	public void draw(Graphics g, String str, float x, float y, float renderWidth, int horizontalAlignment, FontRenderListener listener) {
		final float strFullRenderLength = (str.length() * fontParameters.characterWidth) + (str.length() * fontParameters.spacing);
		final float lineWidth;

		if(renderWidth < 0f) {
			horizontalAlignment = Align.left;
			lineWidth = 1000f;
		} else {
			lineWidth = renderWidth;
		}

		switch(horizontalAlignment) {
		case Align.left:
			renderStringLeftAligned(g, str, x, y, lineWidth, listener);
			break;
			case Align.right:

				break;
				case Align.center:
					break;
		}
	}

	private void renderStringRightAligned(Graphics g, String str, float x, float y, float lineWidth, FontRenderListener listener) {

	}

	private void renderStringLeftAligned(Graphics g, String str, float x, float y, float lineWidth, FontRenderListener listener) {
		float yOffset = 0f;
		float xOffset = 0f;

		for(int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);

			if(c == '\n' || c == '\r') {
				xOffset = 0f;
				yOffset += fontParameters.lineHeight;
				continue;
			}

			if(listener == null) {
				renderChar(g, c, x + xOffset, y + yOffset);
			} else if(listener.preRenderChar(g, c, x + xOffset, y + yOffset, fontParameters.frameWidth, fontParameters.frameHeight)) {
				renderChar(g, c, x + xOffset, y + yOffset);
			}
			if(listener != null) {
				listener.postRenderChar(g, c, x + xOffset, y + yOffset, fontParameters.frameWidth, fontParameters.frameHeight);
			}

			xOffset += fontParameters.characterWidth + fontParameters.spacing;

			if(xOffset >= lineWidth) {
				xOffset = 0f;
				yOffset += fontParameters.lineHeight;
			}
		}
	}

	private void renderChar(Graphics g, char c, float renderX, float renderY) {


	}

	private int getXIndex(int index, int charactersPerRow) {
		return index % charactersPerRow;
	}

	private int getYIndex(int index, int charactersPerRow) {
		return index / charactersPerRow;
	}

	public TextureRegion getTextureRegion(char c) {
		if(characterTextures == null) {
			return null;
		}
		final int xIndex, yIndex;

		if(fontParameters.overrideCharacterIndices != null) {
			final int index = fontParameters.overrideCharacterIndices.get(c, -1);
			if(index < 0) {
				return null;
			}
			xIndex = getXIndex(index, charactersPerRow);
			yIndex = getYIndex(index, charactersPerRow);
		} else {
			xIndex = getXIndex(c, charactersPerRow);
			yIndex = getYIndex(c, charactersPerRow);
		}

		return characterTextures[xIndex][yIndex];
	}

	@Override
	public boolean useIntegerPositions() {
		return true;
	}

	public FontParameters getFontParameters() {
		return fontParameters;
	}

	public static class FontParameters {
		public String textureAtlasPath;
		public String texturePath;
		public int frameWidth;
		public int frameHeight;
		public int characterWidth = -1;
		public int lineHeight = -1;
		public int spacing = 1;
		public IntIntMap overrideCharacterIndices;
	}

	public static interface FontRenderListener {

		/**
		 * Called before rendering a character to screen
		 * @param g The {@link Graphics} context
		 * @param c The text character to be rendered
		 * @param charRenderX The x coordinate the character will render at
		 * @param charRenderY The y coordinate the character will render at
		 * @param charRenderWidth The frame width of the character
		 * @param charRenderHeight The frame height of the character
		 * @return True if the character should be rendered, otherwise false
		 */
		public boolean preRenderChar(Graphics g, char c, float charRenderX, float charRenderY, float charRenderWidth, float charRenderHeight);

		/**
		 * Called after rendering a character to screen. <br><br>Note: If {@link #preRenderChar(Graphics, char, float, float, float, float)} returns false, this method will still be called.
		 * @param g The {@link Graphics} context
		 * @param c The text character rendered
		 * @param charRenderX The x coordinate the character was rendered at
		 * @param charRenderY The y coordinate the character was rendered at
		 * @param charRenderWidth The frame width of the character
		 * @param charRenderHeight The frame height of the character
		 */
		public void postRenderChar(Graphics g, char c, float charRenderX, float charRenderY, float charRenderWidth, float charRenderHeight);
	}
}
