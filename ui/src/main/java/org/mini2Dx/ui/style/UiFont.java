/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ui.style;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.Mini2DxFreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.font.BitmapFont;
import org.mini2Dx.core.font.GameFont;
import org.mini2Dx.core.font.MonospaceFont;
import org.mini2Dx.core.serialization.SerializationException;
import org.mini2Dx.core.serialization.annotation.Field;
import org.mini2Dx.core.util.ColorUtils;

/**
 * A font for user interfaces
 */
public class UiFont {

	@Field
	private String path;
	@Field(optional=true)
	private String borderColor;
	@Field(optional=true)
	private int borderWidth;
	@Field(optional=true)
	private String shadowColor;
	@Field(optional=true)
	private int shadowOffsetX;
	@Field(optional=true)
	private int shadowOffsetY;
	@Field(optional=true)
	private int spaceX;
	@Field(optional=true)
	private int spaceY;
	@Field(optional=true)
	private boolean flip;
	@Field(optional=true)
	private boolean kerning = true;
	@Field
	private int fontSize;
	
	private Color fontBorderColor, fontShadowColor;
	private GameFont gameFont;
	
	public void prepareAssets(UiTheme theme, FileHandleResolver fileHandleResolver, AssetManager assetManager) {
		if(theme.isHeadless()) {
			return;
		}

		if(path.endsWith(".ttf")) {
			final Mini2DxFreeTypeFontGenerator fontGenerator = new Mini2DxFreeTypeFontGenerator(fileHandleResolver.resolve(path));
			if(borderColor != null) {
				fontBorderColor = ColorUtils.rgbToColor(borderColor);
			}
			if(shadowColor != null) {
				fontShadowColor = ColorUtils.rgbToColor(shadowColor);
			}
			FreeTypeFontParameter fontParameter = new  FreeTypeFontParameter();
			fontParameter.size = fontSize;
			fontParameter.flip = !flip;
			fontParameter.kerning = kerning;
			if(borderWidth > 0 && borderColor != null) {
				fontParameter.borderWidth = borderWidth;
				fontParameter.borderColor = fontBorderColor;
			}
			if((shadowOffsetX != 0 || shadowOffsetY != 0) && shadowColor != null) {
				fontParameter.shadowColor = fontShadowColor;
				fontParameter.shadowOffsetX = shadowOffsetX;
				fontParameter.shadowOffsetY = shadowOffsetY;
			}
			if(spaceX != 0 || spaceY != 0) {
				fontParameter.spaceX = spaceX;
				fontParameter.spaceY = spaceY;
			}
			final Mini2DxFreeTypeFontGenerator.Mini2DxFreeTypeBitmapFontData freeTypeBitmapFontData = fontGenerator.generateFontData(fontParameter);
			gameFont = new BitmapFont(freeTypeBitmapFontData, freeTypeBitmapFontData.getRegions(), true);
		} else if(path.endsWith(".fnt")) {
			gameFont = new BitmapFont(fileHandleResolver.resolve(path));
		} else if(path.endsWith(".xml")) {
			try {
				final MonospaceFont.FontParameters fontParameters = Mdx.xml.fromXml(fileHandleResolver.resolve(path).reader(), MonospaceFont.FontParameters.class);
				gameFont = new MonospaceFont(fontParameters);
				((MonospaceFont) gameFont).load(assetManager);
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void dispose() {
		gameFont.dispose();
	}

	public GameFont getGameFont() {
		return gameFont;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public Color getFontBorderColor() {
		return fontBorderColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(String shadowColor) {
		this.shadowColor = shadowColor;
	}

	public int getShadowOffsetX() {
		return shadowOffsetX;
	}

	public void setShadowOffsetX(int shadowOffsetX) {
		this.shadowOffsetX = shadowOffsetX;
	}

	public int getShadowOffsetY() {
		return shadowOffsetY;
	}

	public void setShadowOffsetY(int shadowOffsetY) {
		this.shadowOffsetY = shadowOffsetY;
	}

	public int getSpaceX() {
		return spaceX;
	}

	public void setSpaceX(int spaceX) {
		this.spaceX = spaceX;
	}

	public int getSpaceY() {
		return spaceY;
	}

	public void setSpaceY(int spaceY) {
		this.spaceY = spaceY;
	}

	public boolean isFlip() {
		return flip;
	}

	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	public boolean isKerning() {
		return kerning;
	}

	public void setKerning(boolean kerning) {
		this.kerning = kerning;
	}

	private String getFontParameterKey(FreeTypeFontParameter parameter) {
		StringBuilder result = new StringBuilder();
		result.append(parameter.characters);
		result.append(parameter.borderGamma);
		result.append(parameter.borderStraight);
		result.append(parameter.borderWidth);
		result.append(parameter.flip);
		result.append(parameter.gamma);
		result.append(parameter.genMipMaps);
		result.append(parameter.incremental);
		result.append(parameter.kerning);
		result.append(parameter.mono);
		result.append(parameter.renderCount);
		result.append(parameter.shadowOffsetX);
		result.append(parameter.shadowOffsetY);
		result.append(parameter.size);
		result.append(parameter.spaceX);
		result.append(parameter.spaceY);
		result.append(parameter.borderColor == null ? "null" : parameter.borderColor.toFloatBits());
		result.append(parameter.color == null ? "null" : parameter.color.toFloatBits());
		result.append(parameter.magFilter == null ? "null" : parameter.magFilter.getGLEnum());
		result.append(parameter.minFilter == null ? "null" : parameter.minFilter.getGLEnum());
		result.append(parameter.packer == null ? "null" : parameter.packer.hashCode());
		result.append(parameter.shadowColor == null ? "null" : parameter.shadowColor.toFloatBits());
		return result.toString();
	}
}
