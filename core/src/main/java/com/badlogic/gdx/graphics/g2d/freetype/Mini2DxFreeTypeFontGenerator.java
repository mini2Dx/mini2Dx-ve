package com.badlogic.gdx.graphics.g2d.freetype;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Mini2DxFreeTypeFontGenerator extends FreeTypeFontGenerator {

	public Mini2DxFreeTypeFontGenerator(FileHandle fontFile) {
		super(fontFile);
	}

	public Mini2DxFreeTypeBitmapFontData generateFontData(FreeTypeFontParameter parameter) {
		return (Mini2DxFreeTypeBitmapFontData) generateData(parameter, new Mini2DxFreeTypeBitmapFontData());
	}

	public static class Mini2DxFreeTypeBitmapFontData extends FreeTypeBitmapFontData {

		public Array<TextureRegion> getRegions() {
			return regions;
		}
	}
}
