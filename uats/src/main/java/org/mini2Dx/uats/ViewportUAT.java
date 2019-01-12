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
package org.mini2Dx.uats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.viewport.*;
import org.mini2Dx.core.screen.BasicGameScreen;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.uats.util.ScreenIds;
import org.mini2Dx.uats.util.UATSelectionScreen;

/**
 * UAT for testing {@link Viewport} implementations
 */
public class ViewportUAT extends BasicGameScreen {
	public static final String TEXTURE_PACK = "texture-region-uat";
	private static final int VIEWPORT_WIDTH = 320;
	private static final int VIEWPORT_HEIGHT = 240;

	private final Viewport[] viewports = new Viewport[5];

	private Texture texture;
	private int viewportIndex;

	@Override
	public void initialise(GameContainer gc) {
		texture = new Texture(Gdx.files.classpath(TEXTURE_PACK + ".png"));

		viewports[0] = new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewports[1] = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewports[2] = new StretchViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewports[3] = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewports[4] = new ScreenViewport();
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> screenManager, float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			viewportIndex = viewportIndex == viewports.length - 1 ? 0 : viewportIndex + 1;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			for(int i = 0; i < viewports.length; i++) {
				if(viewports[i] instanceof ScalingViewport) {
					final ScalingViewport scalingViewport = ((ScalingViewport) viewports[i]);
					scalingViewport.setPowerOfTwo(!scalingViewport.isPowerOfTwo());
				}
			}
		} else if (Gdx.input.justTouched()) {
			screenManager.enterGameScreen(UATSelectionScreen.SCREEN_ID, new FadeOutTransition(),
					new FadeInTransition());
		}
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {

	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		g.setBackgroundColor(Color.BLACK);

		final Viewport viewport = viewports[viewportIndex];
		viewport.apply(g);
		g.setColor(Color.BLUE);
		g.fillRect(-VIEWPORT_WIDTH, -VIEWPORT_HEIGHT, VIEWPORT_WIDTH * 3f, VIEWPORT_HEIGHT * 3f);
		g.drawTexture(texture, 0f, 8f);

		g.setColor(Color.RED);
		g.drawRect(0f, 0f, viewport.getWidth(), viewport.getHeight());

		switch(viewportIndex) {
		case 1:
			g.drawString("Fit Viewport (" +
					viewport.getX() + "," + viewport.getY() + "," +
					viewport.getWidth() + "," + viewport.getHeight() + ") vs Window (" +
					g.getWindowWidth() + "," + g.getWindowHeight() + ")", 4f, 4f);
			break;
		case 2:
			g.drawString("Stretch Viewport (" +
					viewport.getX() + "," + viewport.getY() + "," +
					viewport.getWidth() + "," + viewport.getHeight() + ") vs Window (" +
					g.getWindowWidth() + "," + g.getWindowHeight() + ")", 4f, 4f);
			break;
		case 3:
			g.drawString("Extend Viewport (" +
					viewport.getX() + "," + viewport.getY() + "," +
					viewport.getWidth() + "," + viewport.getHeight() + ") vs Window (" +
					g.getWindowWidth() + "," + g.getWindowHeight() + ")", 4f, 4f);
			break;
		case 4:
			g.drawString("Screen Viewport (" +
					viewport.getX() + "," + viewport.getY() + "," +
					viewport.getWidth() + "," + viewport.getHeight() + ") vs Window (" +
					g.getWindowWidth() + "," + g.getWindowHeight() + ")", 4f, 4f);
			break;
		default:
		case 0:
			g.drawString("Fill Viewport (" +
					viewport.getX() + "," + viewport.getY() + "," +
					viewport.getWidth() + "," + viewport.getHeight() + ") vs Window (" +
					g.getWindowWidth() + "," + g.getWindowHeight() + ")", 4f, 4f);
			break;
		}
		viewport.unapply(g);
	}

	@Override
	public int getId() {
		return ScreenIds.getScreenId(ViewportUAT.class);
	}
}
