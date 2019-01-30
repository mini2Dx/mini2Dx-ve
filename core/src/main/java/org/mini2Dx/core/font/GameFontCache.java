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

/**
 * Caches glyph geometry for fast rendering of static text
 */
public interface GameFontCache {

	/**
	 * Adds text to the cache
	 * @param str The text to be added
	 * @param x The x coordinate to add the text to
	 * @param y The y coordinate to add the text to
	 */
	public void addText(java.lang.CharSequence str, float x, float y);

	/**
	 * Adds text to the cache
	 * @param str The text to be added
	 * @param x The x coordinate to add the text to
	 * @param y The y coordinate to add the text to
	 * @param targetWidth The target width of the text (text will wrap if exceeded)
	 * @param halign The horizontal alignment of the text based on {@link com.badlogic.gdx.utils.Align}
	 */
	public void addText(java.lang.CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap);

	/**
	 * Clears all text from the cache
	 */
	public void clear();

	/**
	 * Draws the cache to the {@link Graphics} context
	 * @param g The {@link Graphics} context to draw to
	 */
	public void draw(Graphics g);

	/**
	 * Returns the {@link Color} used for subsequent text operations
	 * @return The {@link Color} to draw with
	 */
	public Color getColor();

	/**
	 * Sets the {@link Color} for subsequent text operations
	 * @param color The {@link Color} to draw with
	 */
	public void setColor(Color color);

	/**
	 * Sets the {@link Color} for previous text operations
	 * @param color The {@link Color} to draw with
	 */
	public void setAllColors(Color color);

	/**
	 * Clears all text from the cache and adds a new text sequence
	 * @param str The text to be added
	 * @param x The x coordinate to add the text to
	 * @param y The y coordinate to add the text to
	 */
	public void setText(java.lang.CharSequence str, float x, float y);

	/**
	 * Clears all text from the cache and adds a new text sequence
	 * @param str The text to be added
	 * @param x The x coordinate to add the text to
	 * @param y The y coordinate to add the text to
	 * @param targetWidth The target width of the text (text will wrap if exceeded)
	 * @param halign The horizontal alignment of the text based on {@link com.badlogic.gdx.utils.Align}
	 * @param wrap True if the text should wrap when exceeding the targetWidth, false if the text should clip
	 */
	public void setText(java.lang.CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap);

	/**
	 * Sets the position of the text relative to its current position
	 * @param x The amount to move by on the x axis
	 * @param y The amount to move by on the y axis
	 */
	public void translate(float x, float y);

	/**
	 * Sets the position of the cache
	 * @param x The amount to move by on the x axis
	 * @param y The amount to move by on the y axis
	 */
	public void setPosition(float x, float y);

	/**
	 * Returns the underlying {@link GameFont} for this cache
	 * @return The {@link GameFont} this cache was created for
	 */
	public GameFont getFont();
}
