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
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.graphics.TextureRegion;
import org.mini2Dx.core.screen.BasicGameScreen;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.uats.util.ScreenIds;
import org.mini2Dx.uats.util.UATSelectionScreen;

/**
 * User acceptance testing of {@link org.mini2Dx.core.graphics.Sprite} APIs
 */
public class SpriteUAT extends BasicGameScreen {
	private static final float MARGIN = 4f;
	public static final String TEXTURE_PACK = "texture-region-uat";

	private final FileHandleResolver fileHandleResolver;

	private Texture texture;
	private TextureRegion textureRegion, flipHRegion, flipVRegion, flipHVRegion;

	private Sprite spriteFullTexture;
	private Sprite spriteTextureRegion, spriteFlipHRegion, spriteFlipVRegion, spriteFlipHVRegion;

	public SpriteUAT(FileHandleResolver fileHandleResolver) {
		this.fileHandleResolver = fileHandleResolver;
	}

	@Override
	public void initialise(GameContainer gc) {
		texture = new Texture(fileHandleResolver.resolve(TEXTURE_PACK + ".png"));
		textureRegion = new TextureRegion(texture, 2, 2, 34, 48);
		flipHRegion = new TextureRegion(texture, 2, 2, 34, 48);
		flipHRegion.setFlipX(true);
		flipVRegion = new TextureRegion(texture, 2, 2, 34, 48);
		flipVRegion.setFlipY(true);
		flipHVRegion = new TextureRegion(texture, 2, 2, 34, 48);
		flipHVRegion.setFlip(true, true);

		spriteFullTexture = new Sprite(texture);
		spriteTextureRegion = new Sprite(textureRegion);
		spriteFlipHRegion = new Sprite(flipHRegion);
		spriteFlipVRegion = new Sprite(flipVRegion);
		spriteFlipHVRegion = new Sprite(flipHVRegion);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> screenManager, float delta) {
		if (Gdx.input.justTouched()) {
			screenManager.enterGameScreen(UATSelectionScreen.SCREEN_ID, new FadeOutTransition(),
					new FadeInTransition());
		}
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		float xPosition = MARGIN;
		float yPosition = MARGIN;
		g.drawString("(1) Sprite from Texture - " +
				spriteFullTexture.isFlipX() + "-" + spriteFullTexture.isFlipY(), xPosition, yPosition);
		yPosition += g.getFont().getLineHeight() + MARGIN;
		g.drawSprite(spriteFullTexture, xPosition, yPosition);
		yPosition += spriteFullTexture.getHeight() + MARGIN;

		g.drawString("(2) Sprite from TextureRegion - " +
				spriteTextureRegion.isFlipX() + "-" + spriteTextureRegion.isFlipY(), xPosition, yPosition);
		yPosition += g.getFont().getLineHeight() + MARGIN;
		g.drawSprite(spriteTextureRegion, xPosition, yPosition);
		yPosition += spriteTextureRegion.getRegionHeight() + MARGIN;

		g.drawString("(3) Sprite from TextureRegion (H)" +
				spriteFlipHRegion.isFlipX() + "-" + spriteFlipHRegion.isFlipY(), xPosition, yPosition);
		yPosition += g.getFont().getLineHeight() + MARGIN;
		g.drawSprite(spriteFlipHRegion, xPosition, yPosition);
		yPosition += spriteFlipHRegion.getRegionHeight();

		g.drawString("(4) Sprite from TextureRegion (V)" +
				spriteFlipVRegion.isFlipX() + "-" + spriteFlipVRegion.isFlipY(), xPosition, yPosition);
		yPosition += g.getFont().getLineHeight() + MARGIN;
		g.drawSprite(spriteFlipVRegion, xPosition, yPosition);
		yPosition += spriteFlipVRegion.getRegionHeight();

		g.drawString("(5) Sprite from TextureRegion (H + V)" +
				spriteFlipHVRegion.isFlipX() + "-" + spriteFlipHVRegion.isFlipY(), xPosition, yPosition);
		yPosition += g.getFont().getLineHeight() + MARGIN;
		g.drawSprite(spriteFlipHVRegion, xPosition, yPosition);

		xPosition += gc.getWidth() / 2;
		yPosition = MARGIN;
	}

	@Override
	public int getId() {
		return ScreenIds.getScreenId(SpriteUAT.class);
	}
}
