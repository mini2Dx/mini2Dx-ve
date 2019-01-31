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

public interface GameFont {

	/**
	 * Draws text to the {@link Graphics} context using this font
	 * @param g The {@link Graphics} context
	 * @param str The text to render
	 * @param x The x coordinate to draw at
	 * @param y The y coordinate to draw at
	 */
	public void draw(Graphics g, String str, float x, float y);

	/**
	 * Draws text to the {@link Graphics} context using this font
	 * @param g The {@link Graphics} context
	 * @param str The text to render
	 * @param x The x coordinate to draw at
	 * @param y The y coordinate to draw at
	 * @param targetWidth The target width to render with (Note: text will wrap if exceeding this width)
	 */
	public void draw(Graphics g, String str, float x, float y, float targetWidth);

	/**
	 * Draws text to the {@link Graphics} context using this font
	 * @param g The {@link Graphics} context
	 * @param str The text to render
	 * @param x The x coordinate to draw at
	 * @param y The y coordinate to draw at
	 * @param targetWidth The target width to render with
	 * @param horizontalAlignment The horizontal alignment within the targetWidth. See {@link com.badlogic.gdx.utils.Align}
	 * @param wrap True if text should wrap if exceeding targetWidth, false if it should clip
	 */
	public void draw(Graphics g, String str, float x, float y, float targetWidth, int horizontalAlignment, boolean wrap);

	/**
	 * Creates a {@link FontGlyphLayout} for this font
	 * @return A new {@link FontGlyphLayout} instance
	 */
	public FontGlyphLayout newGlyphLayout();

	/**
	 * Returns a {@link FontGlyphLayout} instance associated with this {@link GameFont} instance
	 * @return A {@link FontGlyphLayout} instance attached to this font
	 */
	public FontGlyphLayout getSharedGlyphLayout();

	/**
	 * Creates a {@link GameFontCache} for this font
	 * @return A new {@link GameFontCache} instance
	 */
	public GameFontCache newCache();

	/**
	 * Returns the {@link Color} the font will be rendered with
	 * @return {@link Color#BLACK} by default
	 */
	public Color getColor();

	/**
	 * Sets the {@link Color} to render the font with
	 * @param color
	 */
	public void setColor(Color color);

	/**
	 * Returns the line height of the font
	 * @return
	 */
	public float getLineHeight();

	/**
	 * Returns the height of a capital letter above the baseline
	 * @return
	 */
	public float getCapHeight();

	/**
	 * Returns if characters use integer positions
	 * @return False if characters can be placed on half-pixels
	 */
	public boolean useIntegerPositions();

	/**
	 * Disposes of this font instance and its resources
	 */
	public void dispose();
}
