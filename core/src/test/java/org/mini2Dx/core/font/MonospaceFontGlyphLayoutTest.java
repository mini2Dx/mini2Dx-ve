package org.mini2Dx.core.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MonospaceFontGlyphLayoutTest {
	private static final int FONT_FRAME_WIDTH = 32;
	private static final int FONT_FRAME_HEIGHT = 32;
	private static final int FONT_CHARACTER_WIDTH = 16;
	private static final int FONT_LINE_HEIGHT = 24;
	private static final int FONT_SPACING = 1;

	private final Mockery mockery = new Mockery();

	private final MonospaceFont.FontParameters fontParameters = new MonospaceFont.FontParameters();
	private MonospaceFont monospaceFont;
	private MonospaceFontGlyphLayout glyphLayout;

	@Before
	public void setUp() {
		fontParameters.frameWidth = FONT_FRAME_WIDTH;
		fontParameters.frameHeight = FONT_FRAME_HEIGHT;
		fontParameters.characterWidth = FONT_CHARACTER_WIDTH;
		fontParameters.lineHeight = FONT_LINE_HEIGHT;

		monospaceFont = new MonospaceFont(fontParameters);
		glyphLayout = (MonospaceFontGlyphLayout) monospaceFont.newGlyphLayout();
	}

	@After
	public void teardown() {
		mockery.assertIsSatisfied();
	}

	@Test
	public void testLayoutLeftAlignLineBreak() {
		final String str = "abc\ndef";

		glyphLayout.setText(str);

		float expectedX = 0f;
		float expectedY = 0f;
		for(int i = 0; i < str.length(); i++) {
			Assert.assertEquals(expectedX, glyphLayout.getGlyphs().get(i).x, 0.01f);
			Assert.assertEquals(expectedY, glyphLayout.getGlyphs().get(i).y, 0.01f);

			expectedX += FONT_CHARACTER_WIDTH + FONT_SPACING;
			if(str.charAt(i) == '\n') {
				expectedX = 0f;
				expectedY += FONT_LINE_HEIGHT;
			}
		}
	}

	@Test
	public void testLayoutLeftAlignWrap() {
		final String str = "abcdef";

		glyphLayout.setText(str, Color.BLUE, (FONT_CHARACTER_WIDTH + FONT_SPACING) * 3f, Align.left, true);

		float expectedX = 0f;
		float expectedY = 0f;
		for(int i = 0; i < str.length(); i++) {
			Assert.assertEquals(expectedX, glyphLayout.getGlyphs().get(i).x, 0.01f);
			Assert.assertEquals(expectedY, glyphLayout.getGlyphs().get(i).y, 0.01f);

			expectedX += FONT_CHARACTER_WIDTH + FONT_SPACING;
			if(i == 2) {
				expectedX = 0f;
				expectedY += FONT_LINE_HEIGHT;
			}
		}
	}

	@Test
	public void testLayoutRightAlignLineBreak() {
		final String str = "abc\ndef";

		glyphLayout.setText(str, Color.BLUE, -1f, Align.right, true);

		final float lineWidth = (3 * FONT_CHARACTER_WIDTH) + (FONT_SPACING * 3);
		float expectedX = 0f;
		float expectedY = 0f;
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '\n') {
				expectedX = 0f;
				expectedY += FONT_LINE_HEIGHT;
				continue;
			}

			Assert.assertEquals(expectedX, glyphLayout.getGlyphs().get(i).x, 0.01f);
			Assert.assertEquals(expectedY, glyphLayout.getGlyphs().get(i).y, 0.01f);

			expectedX += FONT_CHARACTER_WIDTH + FONT_SPACING;
			if(str.charAt(i) == '\n') {
				expectedX = 0f;
				expectedY += FONT_LINE_HEIGHT;
			}
		}
	}

	@Test
	public void testLayoutRightAlignWrap() {
		final String str = "abcdef";
		final float offset = 4f;
		final float targetWidth = (FONT_CHARACTER_WIDTH * 3f) + (FONT_SPACING * 2f) + offset;

		glyphLayout.setText(str, Color.BLUE, targetWidth, Align.right, true);

		float expectedX = offset;
		float expectedY = 0f;
		for(int i = 0; i < str.length(); i++) {
			Assert.assertEquals(expectedX, glyphLayout.getGlyphs().get(i).x, 0.01f);
			Assert.assertEquals(expectedY, glyphLayout.getGlyphs().get(i).y, 0.01f);

			expectedX += FONT_CHARACTER_WIDTH + FONT_SPACING;
			if(i == 2) {
				expectedX = offset;
				expectedY += FONT_LINE_HEIGHT;
			}
		}
	}

	@Test
	public void testLayoutCenterAlign() {
		final String str = "abc\ndef";
	}
}
