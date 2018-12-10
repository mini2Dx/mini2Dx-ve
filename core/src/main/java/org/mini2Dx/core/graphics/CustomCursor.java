/**
 * Copyright (c) 2018 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import org.mini2Dx.core.Mdx;

/**
 * Implements an {@link InputProcessor} that sets a custom mouse cursor image based on the mouse state.
 */
public class CustomCursor implements InputProcessor {
	private final Cursor upCursor, downCursor;

	/**
	 * Constructor
	 * @param upPixmap The image to use in the mouse button up state
	 * @param downPixmap The image to use in the mouse button down state
	 * @param xHotspot The x location of the hotspot pixel within the cursor image (origin top-left corner)
	 * @param yHotspot The y location of the hotspot pixel within the cursor image (origin top-left corner)
	 */
	public CustomCursor(Pixmap upPixmap, Pixmap downPixmap, int xHotspot, int yHotspot) {
		super();

		switch(Mdx.os) {
		case WINDOWS:
		case MAC:
		case UNIX:
			upCursor = Gdx.graphics.newCursor(upPixmap, xHotspot, yHotspot);
			downCursor = Gdx.graphics.newCursor(downPixmap, xHotspot, yHotspot);
			Gdx.graphics.setCursor(upCursor);
			break;
		default:
		case ANDROID:
		case IOS:
		case UNKNOWN:
			upCursor = null;
			downCursor = null;
			break;
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(downCursor != null) {
			Gdx.graphics.setCursor(downCursor);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(upCursor != null) {
			Gdx.graphics.setCursor(upCursor);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
