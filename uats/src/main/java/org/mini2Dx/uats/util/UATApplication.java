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
package org.mini2Dx.uats.util;

import org.mini2Dx.core.assets.FallbackFileHandleResolver;
import org.mini2Dx.core.game.ScreenBasedGame;
import org.mini2Dx.tiled.TiledMap;
import org.mini2Dx.tiled.TiledMapLoader;
import org.mini2Dx.uats.*;
import org.mini2Dx.ui.NavigationMode;
import org.mini2Dx.ui.UiThemeLoader;
import org.mini2Dx.ui.style.UiTheme;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Logger;

/**
 *
 * @author Thomas Cashman
 */
public class UATApplication extends ScreenBasedGame {
	public static final NavigationMode NAVIGATION_MODE = NavigationMode.BUTTON_OR_POINTER;

	private AssetManager assetManager;

	@Override
	public void initialise() {
		FallbackFileHandleResolver fallbackFileHandleResolver = new FallbackFileHandleResolver(
				new ClasspathFileHandleResolver(), new InternalFileHandleResolver());
		assetManager = new AssetManager(fallbackFileHandleResolver);
		assetManager.setLogger(new Logger("AssetManager", Application.LOG_ERROR));

		assetManager.setLoader(UiTheme.class, new UiThemeLoader(fallbackFileHandleResolver));
		assetManager.setLoader(TiledMap.class, new TiledMapLoader(fallbackFileHandleResolver));

		assetManager.load(UiTheme.DEFAULT_THEME_FILENAME, UiTheme.class);
		assetManager.finishLoading();

		addScreen(new LoadingScreen(assetManager));
		addScreen(new UATSelectionScreen(assetManager));
		addScreen(new BlendingUAT(assetManager));
		addScreen(new ClippingUAT(assetManager));
		addScreen(new GeometryUAT());
		addScreen(new GraphicsUAT(assetManager));
		addScreen(new TextureRegionUAT());
		addScreen(new SpriteUAT());
		addScreen(new AudioUAT());
		addScreen(new OrthogonalTiledMapNoCachingUAT(assetManager));
		addScreen(new OrthogonalTiledMapWithCachingUAT(assetManager));
		addScreen(new IsometricTiledMapUAT(assetManager));
		addScreen(new HexagonalTiledMapUAT(assetManager));
		addScreen(new ParticleEffectsUAT());
		addScreen(new ControllerUAT());
		addScreen(new ControllerMapping());
		addScreen(new FlexUiUAT(assetManager));
		addScreen(new PixelUiUAT(assetManager));
		addScreen(new UiSerializationUAT(assetManager));
	}

	@Override
	public int getInitialScreenId() {
		return 0;
	}
}
