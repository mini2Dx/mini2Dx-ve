package org.mini2Dx.uats;

import com.badlogic.gdx.assets.AssetManager;
import org.mini2Dx.core.font.MonospaceFont;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.BasicGameScreen;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.uats.util.ScreenIds;

public class MonospaceFontUAT extends BasicGameScreen implements MonospaceFont.FontRenderListener {
	private final AssetManager assetManager;
	private final MonospaceFont font;

	public MonospaceFontUAT(AssetManager assetManager) {
		super();
		this.assetManager = assetManager;

		final MonospaceFont.FontParameters parameters = new MonospaceFont.FontParameters();


		font = new MonospaceFont(parameters);
	}

	@Override
	public void initialise(GameContainer gc) {
		assetManager.
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> screenManager, float delta) {
		if(!font.load(assetManager)) {
			return;
		}
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {

	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		if(!font.load(assetManager)) {
			return;
		}

		font.render();
	}

	@Override
	public int getId() {
		return ScreenIds.getScreenId(MonospaceFontUAT.class);
	}

	@Override
	public boolean preRenderChar(Graphics g, char c, float charRenderX, float charRenderY, float charRenderWidth, float charRenderHeight) {

		return true;
	}

	@Override
	public void postRenderChar(Graphics g, char c, float charRenderX, float charRenderY, float charRenderWidth, float charRenderHeight) {
	}
}
