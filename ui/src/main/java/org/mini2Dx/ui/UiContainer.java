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
package org.mini2Dx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectSet;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.controller.ControllerType;
import org.mini2Dx.core.controller.button.ControllerButton;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.viewport.Viewport;
import org.mini2Dx.ui.controller.ControllerUiInput;
import org.mini2Dx.ui.element.*;
import org.mini2Dx.ui.event.EventTrigger;
import org.mini2Dx.ui.event.params.ControllerEventTriggerParams;
import org.mini2Dx.ui.event.params.EventTriggerParamsPool;
import org.mini2Dx.ui.event.params.KeyboardEventTriggerParams;
import org.mini2Dx.ui.event.params.MouseEventTriggerParams;
import org.mini2Dx.ui.layout.PixelLayoutUtils;
import org.mini2Dx.ui.layout.ScreenSize;
import org.mini2Dx.ui.listener.ScreenSizeListener;
import org.mini2Dx.ui.listener.UiContainerListener;
import org.mini2Dx.ui.navigation.UiNavigation;
import org.mini2Dx.ui.render.*;
import org.mini2Dx.ui.style.StyleRule;
import org.mini2Dx.ui.style.UiTheme;
import org.mini2Dx.ui.util.IdAllocator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The container for all UI elements. {@link #update(float)},
 * {@link #interpolate(float)} and {@link #render(Graphics)} must be called by
 * your {@link GameContainer}
 */
public class UiContainer extends ParentUiElement implements InputProcessor {
	private static final String LOGGING_TAG = UiContainer.class.getSimpleName();
    private static final Vector2 SHARED_VECTOR = new Vector2();
	private static final Array<UiContainer> uiContainerInstances = new Array<UiContainer>(true, 2, UiContainer.class);
	private static Visibility defaultVisibility = Visibility.HIDDEN;
	private static UiTheme UI_THEME;
	private static UiContainerState STATE = UiContainerState.NOOP;

	private final Array<ControllerUiInput<?>> controllerInputs = new Array<ControllerUiInput<?>>(true,1, ControllerUiInput.class);
	private final Array<UiContainerListener> listeners = new Array<UiContainerListener>(true,1, UiContainerListener.class);

	private final IntSet receivedKeyDowns = new IntSet();
	private final ObjectSet<String> receivedButtonDowns = new ObjectSet<String>();

	private final AtomicBoolean forceRenderTreeLayout = new AtomicBoolean(false);
	private final UiContainerRenderTree renderTree;

	private InputSource lastInputSource, nextInputSource;
	private ControllerType lastControllerType = ControllerType.UNKNOWN, nextControllerType = ControllerType.UNKNOWN;
	private int lastMouseX, lastMouseY;
	private float scaleX = 1f;
	private float scaleY = 1f;
	private String lastThemeId;
	private boolean themeWarningIssued, initialThemeLayoutComplete;

	private final IntArray actionKeys = new IntArray();
	private Navigatable activeNavigation;
	private ActionableRenderNode activeAction;
	private TextInputableRenderNode activeTextInput;

	private NavigationMode navigationMode = NavigationMode.BUTTON_OR_POINTER;
	private boolean textInputIgnoredFirstEnter = false;
	private ScreenSizeScaleMode screenSizeScaleMode = ScreenSizeScaleMode.NO_SCALING;

	private boolean passThroughMouseMovement = false;

	private Viewport viewport;

	/**
	 * Constructor
	 * 
	 * @param gc
	 *            Your game's {@link GameContainer}
	 * @param assetManager
	 *            The {@link AssetManager} for the game
	 */
	public UiContainer(GameContainer gc, AssetManager assetManager) {
		this(gc.getWidth(), gc.getHeight(), assetManager);
	}

	public UiContainer(int width, int height, AssetManager assetManager) {
		super(IdAllocator.getNextId("ui-container-root"));
		this.width = width;
		this.height = height;

		actionKeys.add(Keys.ENTER);

		switch (Mdx.os) {
		case ANDROID:
		case IOS:
			lastInputSource = InputSource.TOUCHSCREEN;
			break;
		case MAC:
		case UNIX:
		case WINDOWS:
			lastInputSource = InputSource.KEYBOARD_MOUSE;
			break;
		default:
			break;
		}

		renderTree = new UiContainerRenderTree(this, assetManager);
		super.renderNode = renderTree;

		setVisibility(Visibility.VISIBLE);
		uiContainerInstances.add(this);
	}
	
	public static void relayoutAllUiContainers() {
		Gdx.app.log(LOGGING_TAG, "Triggering re-layout for all UiContainer instances");
		for(int i = uiContainerInstances.size - 1; i >= 0; i--) {
			uiContainerInstances.get(i).forceRenderTreeLayout();
		}
	}
	
	private void forceRenderTreeLayout() {
		forceRenderTreeLayout.set(true);
	}
	
	public void dispose() {
		uiContainerInstances.removeValue(this, false);
	}

	@Override
	protected ParentRenderNode<?, ?> createRenderNode(ParentRenderNode<?, ?> parent) {
		return renderTree;
	}

	/**
	 * Updates all {@link UiElement}s
	 * 
	 * @param delta
	 *            The time since the last frame (in seconds)
	 */
	public void update(float delta) {
		updateLastInputSource();
		updateLastControllerType();
		if (!isThemeApplied()) {
			if (!themeWarningIssued) {
				if (Gdx.app != null) {
					Gdx.app.error(LOGGING_TAG, "No theme applied to UI - cannot update or render UI.");
				}
				themeWarningIssued = true;
			}
			return;
		}
		if(lastThemeId == null || (lastThemeId != null && !lastThemeId.equals(UI_THEME.getId()))) {
			renderTree.setDirty();
			initialThemeLayoutComplete = false;
			Gdx.app.log(LOGGING_TAG, "Applied theme - " + UI_THEME.getId());
		}
		lastThemeId = UI_THEME.getId();

		if(forceRenderTreeLayout.get()) {
			renderTree.onResize(width, height);
			forceRenderTreeLayout.set(false);
		}
		
		notifyPreUpdate(delta);
		for (int i = controllerInputs.size - 1; i >= 0; i--) {
			controllerInputs.get(i).update(delta);
		}
		if (renderTree.isDirty()) {
			STATE = UiContainerState.LAYOUT;
			renderTree.layout();
			STATE = UiContainerState.NOOP;
			renderTree.processLayoutDeferred();
			initialThemeLayoutComplete = true;
		}
		STATE = UiContainerState.UPDATE;
		renderTree.update(delta);
		notifyPostUpdate(delta);
		STATE = UiContainerState.NOOP;
		renderTree.processUpdateDeferred();

		PixelLayoutUtils.update(delta);
	}

	/**
	 * Interpolates all {@link UiElement}s
	 * 
	 * @param alpha
	 *            The interpolation alpha
	 */
	public void interpolate(float alpha) {
		if (!isThemeApplied()) {
			return;
		}
		STATE = UiContainerState.INTERPOLATE;
		notifyPreInterpolate(alpha);
		renderTree.interpolate(alpha);
		notifyPostInterpolate(alpha);
		STATE = UiContainerState.NOOP;
	}

	/**
	 * Renders all visible {@link UiElement}s
	 * 
	 * @param g
	 *            The {@link Graphics} context
	 */
	public void render(Graphics g) {
		if (!isThemeApplied()) {
			return;
		}
		if (!initialThemeLayoutComplete) {
			return;
		}
		STATE = UiContainerState.RENDER;
		notifyPreRender(g);
		switch (visibility) {
		case HIDDEN:
			return;
		case NO_RENDER:
			return;
		default:
			float previousScaleX = g.getScaleX();
			float previousScaleY = g.getScaleY();

			if (scaleX != 1f || scaleY != 1f) {
				g.setScale(scaleX, scaleY);
			}
			renderTree.render(g);
			if (scaleX != 1f || scaleY != 1f) {
				g.setScale(previousScaleX, previousScaleY);
			}
			break;
		}
		notifyPostRender(g);
		STATE = UiContainerState.NOOP;
		renderTree.processRenderDeferred();
	}

	@Override
	public void attach(ParentRenderNode<?, ?> parentRenderNode) {
	}

	@Override
	public void detach(ParentRenderNode<?, ?> parentRenderNode) {
	}

	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	/**
	 * Adds a {@link ScreenSizeListener} to listen for {@link ScreenSize} change
	 * 
	 * @param listener
	 *            The {@link ScreenSizeListener} to add
	 */
	public void addScreenSizeListener(ScreenSizeListener listener) {
		renderTree.addScreenSizeListener(listener);
	}

	/**
	 * Removes a {@link ScreenSizeListener} from this {@link UiContainer}
	 * 
	 * @param listener
	 *            The {@link ScreenSizeListener} to remove
	 */
	public void removeScreenSizeListener(ScreenSizeListener listener) {
		renderTree.removeScreenSizeListener(listener);
	}

	/**
	 * Returns if a {@link UiTheme} has been applied to thi {@link UiContainer}
	 * 
	 * @return True if the {@link UiTheme} has been applied
	 */
	public static boolean isThemeApplied() {
		return UI_THEME != null;
	}

	/**
	 * Returns the {@link UiTheme} currently applied to this {@link UiContainer}
	 * 
	 * @return Null if no {@link UiTheme} has been applied
	 */
	public static UiTheme getTheme() {
		return UI_THEME;
	}

	/**
	 * Sets the current {@link UiTheme} for this {@link UiContainer}
	 * 
	 * @param theme
	 *            The {@link UiTheme} to apply
	 */
	public static void setTheme(UiTheme theme) {
		if (theme == null) {
			return;
		}
		if (UI_THEME != null && UI_THEME.getId().equals(theme.getId())) {
			return;
		}
		UI_THEME = theme;
	}

	/**
	 * Returns the current {@link UiContainerState}
	 * @return
	 */
	public static UiContainerState getState() {
		return STATE;
	}

	@Override
	public void setStyleId(String styleId) {
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!pointerNavigationAllowed()) {
			return false;
		}

		if (viewport != null){
			SHARED_VECTOR.x = screenX;
			SHARED_VECTOR.y = screenY;
			viewport.toWorldCoordinates(SHARED_VECTOR);
			screenX = MathUtils.round(SHARED_VECTOR.x);
			screenY = MathUtils.round(SHARED_VECTOR.y);
		}

		updateLastInputSource(screenX, screenY);

		lastMouseX = screenX;
		lastMouseY = screenY;

		if (activeTextInput != null && activeTextInput.mouseDown(screenX, screenY, pointer, button) == null) {
			// Release textbox control
			activeTextInput = null;
			activeAction = null;

			switch (Mdx.os) {
			case ANDROID:
			case IOS:
				Gdx.input.setOnscreenKeyboardVisible(false);
				break;
			default:
				break;
			}
		}

		ActionableRenderNode result = renderTree.mouseDown(screenX, screenY, pointer, button);
		if (result != null) {
			MouseEventTriggerParams params = EventTriggerParamsPool.allocateMouseParams();
			params.setMouseX(screenX);
			params.setMouseY(screenY);
			result.beginAction(EventTrigger.getTriggerForMouseClick(button), params);
			EventTriggerParamsPool.release(params);

			setActiveAction(result);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!pointerNavigationAllowed()) {
			return false;
		}

		if (viewport != null){
			SHARED_VECTOR.x = screenX;
			SHARED_VECTOR.y = screenY;
			viewport.toWorldCoordinates(SHARED_VECTOR);
            screenX = MathUtils.round(SHARED_VECTOR.x);
            screenY = MathUtils.round(SHARED_VECTOR.y);
		}

		updateLastInputSource(screenX, screenY);

		lastMouseX = screenX;
		lastMouseY = screenY;

		if (activeAction == null) {
			return false;
		}
		activeAction.mouseUp(screenX, screenY, pointer, button);
		activeAction = null;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!pointerNavigationAllowed()) {
			return false;
		}

		if (viewport != null){
			SHARED_VECTOR.x = screenX;
			SHARED_VECTOR.y = screenY;
			viewport.toWorldCoordinates(SHARED_VECTOR);
            screenX = MathUtils.round(SHARED_VECTOR.x);
            screenY = MathUtils.round(SHARED_VECTOR.y);
		}

		updateLastInputSource(screenX, screenY);

		lastMouseX = screenX;
		lastMouseY = screenY;

		if(passThroughMouseMovement) {
			renderTree.mouseMoved(screenX, screenY);
			return false;
		}
		return renderTree.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (viewport != null){
			SHARED_VECTOR.x = screenX;
			SHARED_VECTOR.y = screenY;
			viewport.toWorldCoordinates(SHARED_VECTOR);
            screenX = MathUtils.round(SHARED_VECTOR.x);
            screenY = MathUtils.round(SHARED_VECTOR.y);
		}

		updateLastInputSource(screenX, screenY);

		lastMouseX = screenX;
		lastMouseY = screenY;

		if (!pointerNavigationAllowed()) {
			return false;
		}
		if(passThroughMouseMovement) {
			renderTree.mouseMoved(screenX, screenY);
			return false;
		}
		return renderTree.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		if (!pointerNavigationAllowed()) {
			return false;
		}
		return renderTree.mouseScrolled(lastMouseX, lastMouseY, amount);
	}

	@Override
	public boolean keyTyped(char character) {
		if (activeTextInput == null) {
			return false;
		}
		if (activeTextInput.isReceivingInput()) {
			activeTextInput.characterReceived(character);
		}
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		receivedKeyDowns.add(keycode);
		if (activeTextInput != null && activeTextInput.isReceivingInput()) {
			return true;
		}
		if (actionKeys.contains(keycode) && activeAction != null) {
			KeyboardEventTriggerParams params = EventTriggerParamsPool.allocateKeyboardParams();
			params.setKey(keycode);
			activeAction.setState(NodeState.ACTION);
			activeAction.beginAction(EventTrigger.KEYBOARD, params);
			EventTriggerParamsPool.release(params);

			if (activeTextInput != null) {
				textInputIgnoredFirstEnter = false;
			}
			return true;
		}
		if (handleModalKeyDown(keycode)) {
			return true;
		}
		receivedKeyDowns.remove(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Key down was sent before this UI Container accepted input
		if (!receivedKeyDowns.remove(keycode)) {
			return false;
		}
		if (handleTextInputKeyUp(keycode)) {
			return true;
		}
		if (actionKeys.contains(keycode) && activeAction != null) {
			KeyboardEventTriggerParams params = EventTriggerParamsPool.allocateKeyboardParams();
			params.setKey(keycode);
			activeAction.setState(NodeState.NORMAL);
			activeAction.endAction(EventTrigger.KEYBOARD, params);
			EventTriggerParamsPool.release(params);

			switch (Mdx.os) {
			case ANDROID:
			case IOS:
				Gdx.input.setOnscreenKeyboardVisible(false);
				break;
			default:
				break;
			}
			return true;
		}
		if (handleModalKeyUp(keycode)) {
			return true;
		}
		return false;
	}

	public boolean buttonDown(ControllerUiInput<?> controllerUiInput, ControllerButton button) {
		if (activeNavigation == null) {
			return false;
		}
		receivedButtonDowns.add(button.getAbsoluteValue());
		ActionableRenderNode hotkeyAction = activeNavigation.hotkey(button);
		if (hotkeyAction != null) {
			if(!hotkeyAction.isEnabled()) {
				return true;
			}
			ControllerEventTriggerParams params = EventTriggerParamsPool.allocateControllerParams();
			params.setControllerButton(button);
			hotkeyAction.setState(NodeState.ACTION);
			hotkeyAction.beginAction(EventTrigger.CONTROLLER, params);
			EventTriggerParamsPool.release(params);
		} else if (activeAction != null) {
			if (button.equals(controllerUiInput.getActionButton())) {
				if (activeTextInput != null) {
					if (!textInputIgnoredFirstEnter) {
						ControllerEventTriggerParams params = EventTriggerParamsPool.allocateControllerParams();
						params.setControllerButton(button);
						activeAction.setState(NodeState.ACTION);
						activeAction.beginAction(EventTrigger.CONTROLLER, params);
						EventTriggerParamsPool.release(params);
					}
				} else {
					ControllerEventTriggerParams params = EventTriggerParamsPool.allocateControllerParams();
					params.setControllerButton(button);
					activeAction.setState(NodeState.ACTION);
					activeAction.beginAction(EventTrigger.CONTROLLER, params);
					EventTriggerParamsPool.release(params);
				}
			}
		}
		return true;
	}

	public boolean buttonUp(ControllerUiInput<?> controllerUiInput, ControllerButton button) {
		// Button down was sent before this UI Container accepted input
		if (!receivedButtonDowns.remove(button.getAbsoluteValue())) {
			return false;
		}
		if (activeNavigation == null) {
			return false;
		}
		ActionableRenderNode hotkeyAction = activeNavigation.hotkey(button);
		if (hotkeyAction != null) {
			if(!hotkeyAction.isEnabled()) {
				return true;
			}
			ControllerEventTriggerParams params = EventTriggerParamsPool.allocateControllerParams();
			params.setControllerButton(button);
			hotkeyAction.setState(NodeState.ACTION);
			hotkeyAction.endAction(EventTrigger.CONTROLLER, params);
			EventTriggerParamsPool.release(params);
		} else if (activeAction != null) {
			if (activeTextInput != null && !textInputIgnoredFirstEnter) {
				textInputIgnoredFirstEnter = true;
				return true;
			}
			if (button.equals(controllerUiInput.getActionButton())) {
				ControllerEventTriggerParams params = EventTriggerParamsPool.allocateControllerParams();
				params.setControllerButton(button);
				activeAction.setState(NodeState.NORMAL);
				activeAction.endAction(EventTrigger.CONTROLLER, params);
				EventTriggerParamsPool.release(params);
				textInputIgnoredFirstEnter = false;
			}
		}
		return true;
	}

	private boolean handleModalKeyDown(int keycode) {
		if (activeNavigation == null) {
			return false;
		}
		ActionableRenderNode hotkeyAction = activeNavigation.hotkey(keycode);
		if (hotkeyAction == null) {
			if (keyNavigationAllowed()) {
				if (activeAction != null) {
					activeAction.setState(NodeState.NORMAL);
				}
				ActionableRenderNode result = activeNavigation.navigate(keycode);
				if (result != null) {
					result.setState(NodeState.HOVER);
					setActiveAction(result);
				}
			}
		} else {
			if(!hotkeyAction.isEnabled()) {
				return true;
			}
			KeyboardEventTriggerParams params = EventTriggerParamsPool.allocateKeyboardParams();
			params.setKey(keycode);
			hotkeyAction.setState(NodeState.ACTION);
			hotkeyAction.beginAction(EventTrigger.KEYBOARD, params);
			EventTriggerParamsPool.release(params);
		}
		return true;
	}

	private boolean handleModalKeyUp(int keycode) {
		if (activeNavigation == null) {
			return false;
		}
		ActionableRenderNode hotkeyAction = activeNavigation.hotkey(keycode);
		if (hotkeyAction != null) {
			if(!hotkeyAction.isEnabled()) {
				return true;
			}
			KeyboardEventTriggerParams params = EventTriggerParamsPool.allocateKeyboardParams();
			params.setKey(keycode);
			hotkeyAction.setState(NodeState.NORMAL);
			hotkeyAction.endAction(EventTrigger.KEYBOARD, params);
			EventTriggerParamsPool.release(params);
		}
		return true;
	}

	private boolean handleTextInputKeyUp(int keycode) {
		if (activeTextInput == null) {
			return false;
		}
		if (!activeTextInput.isReceivingInput()) {
			return false;
		}
		switch (keycode) {
		case Keys.BACKSPACE:
			activeTextInput.backspace();
			break;
		case Keys.ENTER:
			if (!textInputIgnoredFirstEnter) {
				textInputIgnoredFirstEnter = true;
				return true;
			}
			if (activeTextInput.enter()) {
				activeTextInput = null;
				activeAction = null;
				switch (Mdx.os) {
				case ANDROID:
				case IOS:
					Gdx.input.setOnscreenKeyboardVisible(false);
					break;
				default:
					break;
				}
			}
			break;
		case Keys.RIGHT:
			activeTextInput.moveCursorRight();
			break;
		case Keys.LEFT:
			activeTextInput.moveCursorLeft();
			break;
		}
		return true;
	}

	public void setActiveAction(ActionableRenderNode actionable) {
		if (activeAction != null && !activeAction.getId().equals(actionable.getId())) {
			activeAction.setState(NodeState.NORMAL);
		}
		if (actionable instanceof TextInputableRenderNode) {
			activeTextInput = (TextInputableRenderNode) actionable;
			switch (Mdx.os) {
			case ANDROID:
			case IOS:
				Gdx.input.setOnscreenKeyboardVisible(true);
				break;
			default:
				break;
			}
		}
		activeAction = actionable;
		notifyElementActivated(actionable);
	}

	/**
	 * Sets the current {@link Navigatable} for UI navigation
	 * 
	 * @param activeNavigation
	 *            The current {@link Navigatable} being navigated
	 */
	public void setActiveNavigation(Navigatable activeNavigation) {
		if (this.activeNavigation != null && activeNavigation != null
				&& this.activeNavigation.getId().equals(activeNavigation.getId())) {
			return;
		}
		unsetExistingNavigationHover();
		this.activeNavigation = activeNavigation;

		if (renderTree == null) {
			return;
		}
		if (!keyNavigationAllowed()) {
			return;
		}
		if (activeAction != null) {
			activeAction.setState(NodeState.NORMAL);
		}
		UiNavigation navigation = activeNavigation.getNavigation();
		if (navigation == null) {
			return;
		}
		Actionable firstActionable = navigation.resetCursor();
		if (firstActionable == null) {
			return;
		}
		RenderNode<?, ?> renderNode = renderTree.getElementById(firstActionable.getId());
		if (renderNode == null) {
			return;
		}
		setActiveAction(((ActionableRenderNode) renderNode));
		((ActionableRenderNode) renderNode).setState(NodeState.HOVER);
	}

	private void unsetExistingNavigationHover() {
		if (activeNavigation == null) {
			return;
		}
		if (renderTree == null) {
			return;
		}
		UiNavigation navigation = activeNavigation.getNavigation();
		if (navigation == null) {
			return;
		}
		Actionable actionable = navigation.getCursor();
		if (actionable == null) {
			return;
		}
		ActionableRenderNode actionableRenderNode = (ActionableRenderNode) renderNode
				.getElementById(actionable.getId());
		if (actionableRenderNode == null) {
			return;
		}
		if (actionableRenderNode.getState() != NodeState.HOVER) {
			return;
		}
		actionableRenderNode.setState(NodeState.NORMAL);
	}

	public void clearActiveAction() {
		unsetExistingNavigationHover();
		this.activeAction = null;
	}

	/**
	 * Clears the current {@link Navigatable} being navigated
	 */
	public void clearActiveNavigation() {
		unsetExistingNavigationHover();
		this.activeTextInput = null;
		this.activeAction = null;
		this.activeNavigation = null;
	}

	/**
	 * Returns the currently active {@link Navigatable}
	 * 
	 * @return null if there is nothing active
	 */
	public Navigatable getActiveNavigation() {
		return activeNavigation;
	}

	/**
	 * Returns the currently hovered {@link ActionableRenderNode}
	 * @return Null if nothing is hovered
	 */
	public ActionableRenderNode getActiveAction() {
		return activeAction;
	}

	/**
	 * Returns the width of the {@link UiContainer}
	 * 
	 * @return The width in pixels
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Returns the height of the {@link UiContainer}
	 * 
	 * @return The height in pixels
	 */
	public float getHeight() {
		return height;
	}

	@Override
	public StyleRule getStyleRule() {
		return StyleRule.NOOP;
	}

	/**
	 * Sets the width and height of the {@link UiContainer}
	 * 
	 * @param width
	 *            The width in pixels
	 * @param height
	 *            The height in pixels
	 */
	public void set(int width, int height) {
		this.width = width;
		this.height = height;
		renderTree.onResize(width, height);
	}

	/**
	 * Returns the configured {@link Graphics} scaling during rendering
	 * 
	 * @return 1f by default
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * Returns the configured {@link Graphics} scaling during rendering
	 * 
	 * @return 1f by default
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Sets the {@link Graphics} scaling during rendering. Mouse/touch
	 * coordinates will be scaled accordingly.
	 * 
	 * @param scaleX
	 *            Scaling along the X axis
	 * @param scaleY
	 *            Scaling along the Y axis
	 */
	public void setScale(float scaleX, float scaleY) {
		if(scaleX == this.scaleX && scaleY == this.scaleY) {
			return;
		}
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		renderTree.onResize(width, height);
	}

	/**
	 * Returns the last {@link InputSource} used on the {@link UiContainer}
	 * 
	 * @return
	 */
	public InputSource getLastInputSource() {
		return lastInputSource;
	}

	private void updateLastInputSource(int screenX, int screenY) {
		if(Math.abs(screenX - lastMouseX) > 2 || Math.abs(screenY - lastMouseY) > 2) {
			switch(Mdx.os) {
			case WINDOWS:
			case MAC:
			case UNIX:
			case UNKNOWN:
			default:
				setLastInputSource(InputSource.KEYBOARD_MOUSE);
				break;
			case ANDROID:
			case IOS:
				setLastInputSource(InputSource.TOUCHSCREEN);
				break;
			}
		}
	}

	private void updateLastInputSource() {
		if (nextInputSource == null) {
			return;
		}
		if (this.lastInputSource.equals(nextInputSource)) {
			return;
		}
		InputSource oldInputSource = this.lastInputSource;
		this.lastInputSource = nextInputSource;
		notifyInputSourceChange(oldInputSource, lastInputSource);
	}

	/**
	 * Sets the last {@link InputSource} used on the {@link UiContainer}
	 * 
	 * @param lastInputSource
	 *            The {@link InputSource} last used
	 */
	public void setLastInputSource(InputSource lastInputSource) {
		this.nextInputSource = lastInputSource;
	}

	/**
	 * Returns the last {@link ControllerType} used on the {@link UiContainer}
	 * 
	 * @return
	 */
	public ControllerType getLastControllerType() {
		return lastControllerType;
	}

	private void updateLastControllerType() {
		if (nextControllerType == null) {
			return;
		}
		if (this.lastControllerType.equals(nextControllerType)) {
			return;
		}
		ControllerType oldControllerType = this.lastControllerType;
		this.lastControllerType = nextControllerType;
		notifyControllerTypeChange(oldControllerType, lastControllerType);
	}

	/**
	 * Sets the last {@link ControllerType} used on the {@link UiContainer}
	 * 
	 * @param lastControllerType
	 *            The {@link ControllerType} last used
	 */
	public void setLastControllerType(ControllerType lastControllerType) {
		this.nextControllerType = lastControllerType;
	}

	/**
	 * Adds a {@link ControllerUiInput} instance to this {@link UiContainer}
	 * 
	 * @param input
	 *            The instance to add
	 */
	public void addControllerInput(ControllerUiInput<?> input) {
		controllerInputs.add(input);
	}

	/**
	 * Removes a {@link ControllerUiInput} instance from this
	 * {@link UiContainer}
	 * 
	 * @param input
	 *            The instance to remove
	 */
	public void removeControllerInput(ControllerUiInput<?> input) {
		controllerInputs.removeValue(input, false);
	}

	@Override
	public void setZIndex(int zIndex) {
	}

	/**
	 * Add the key used for triggering actions (i.e. selecting a menu option)
	 * 
	 * @param keycode
	 *            The {@link Keys} value
	 */
	public void addActionKey(int keycode) {
		actionKeys.add(keycode);
	}

	/**
	 * Removes a key used for triggering actions (i.e. selecting a menu option)
	 *
	 * @param keycode
	 *            The {@link Keys} value
	 */
	public void removeActionKey(int keycode) {
		actionKeys.removeValue(keycode);
	}

	/**
	 * Clears the keys used for triggering actions (i.e. selecting a menu option)
	 */
	public void clearActionKeys() {
		actionKeys.clear();
	}

	/**
	 * Set to true if mouseMoved() events to should pass through this input handler regardless
	 * @param passThroughMouseMovement
	 */
	public void setPassThroughMouseMovement(boolean passThroughMouseMovement) {
		this.passThroughMouseMovement = passThroughMouseMovement;
	}

	/**
	 * Returns if this {@link UiContainer} can be navigated by keyboard/controller
	 * @return True by default
	 */
	public boolean keyNavigationAllowed() {
		switch (Mdx.os) {
		case ANDROID:
		case IOS:
			return false;
		case MAC:
		case UNIX:
		case UNKNOWN:
		case WINDOWS:
		default:
			switch(navigationMode) {
			case BUTTON_ONLY:
			case BUTTON_OR_POINTER:
				return true;
			default:
			case POINTER_ONLY:
				return false;
			}
		}
	}

	/**
	 * Returns if this {@link UiContainer} can be navigated by touch/mouse
	 * @return True by default
	 */
	public boolean pointerNavigationAllowed() {
		switch (Mdx.os) {
		case ANDROID:
		case IOS:
			return true;
		case MAC:
		case UNIX:
		case UNKNOWN:
		case WINDOWS:
		default:
			switch(navigationMode) {
			default:
			case BUTTON_ONLY:
				return false;
			case BUTTON_OR_POINTER:
			case POINTER_ONLY:
				return true;
			}
		}
	}

	/**
	 * Sets the {@link NavigationMode} on this {@link UiContainer}
	 * @param navigationMode The {@link NavigationMode}
	 */
	public void setNavigationMode(NavigationMode navigationMode) {
		if(navigationMode == null) {
			return;
		}
		this.navigationMode = navigationMode;
	}

	/**
	 * Returns the scaling mode used for {@link ScreenSize} values
	 * @return {@link ScreenSizeScaleMode#NO_SCALING} by default
	 */
	public ScreenSizeScaleMode getScreenSizeScaleMode() {
		return screenSizeScaleMode;
	}

	/**
	 * Sets the scaling mode used for {@link ScreenSize} values
	 * @param screenSizeScaleMode The {@link ScreenSizeScaleMode} to set
	 */
	public void setScreenSizeScaleMode(ScreenSizeScaleMode screenSizeScaleMode) {
		if(screenSizeScaleMode == null) {
			return;
		}
		if(this.screenSizeScaleMode == screenSizeScaleMode) {
			return;
		}
		this.screenSizeScaleMode = screenSizeScaleMode;
		renderTree.onResize(width, height);
	}

	/**
	 * Adds a {@link UiContainerListener} to this {@link UiContainer}
	 * 
	 * @param listener
	 *            The {@link UiContainerListener} to be notified of events
	 */
	public void addListener(UiContainerListener listener) {
		listeners.add(listener);
		addScreenSizeListener(listener);
	}

	/**
	 * Removes a {@link UiContainerListener} from this {@link UiContainer}
	 * 
	 * @param listener
	 *            The {@link UiContainerListener} to stop receiving events
	 */
	public void removeListener(UiContainerListener listener) {
		listeners.removeValue(listener, false);
		removeScreenSizeListener(listener);
	}

	private void notifyPreUpdate(float delta) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).preUpdate(this, delta);
		}
	}

	private void notifyPostUpdate(float delta) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).postUpdate(this, delta);
		}
	}

	private void notifyPreInterpolate(float alpha) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).preInterpolate(this, alpha);
		}
	}

	private void notifyPostInterpolate(float alpha) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).postInterpolate(this, alpha);
		}
	}

	private void notifyPreRender(Graphics g) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).preRender(this, g);
		}
	}

	private void notifyPostRender(Graphics g) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).postRender(this, g);
		}
	}

	private void notifyInputSourceChange(InputSource oldSource, InputSource newSource) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).inputSourceChanged(this, oldSource, newSource);
		}
	}

	private void notifyControllerTypeChange(ControllerType oldControllerType, ControllerType newControllerType) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).controllerTypeChanged(this, oldControllerType, newControllerType);
		}
	}

	private void notifyElementActivated(ActionableRenderNode actionable) {
		for (int i = listeners.size - 1; i >= 0; i--) {
			listeners.get(i).onElementAction(this, actionable.getElement());
		}
	}

	/**
	 * Returns the default {@link Visibility} for newly created
	 * {@link UiElement} objects
	 * 
	 * @return A non-null {@link Visibility} value. {@link Visibility#HIDDEN} by
	 *         default
	 */
	public static Visibility getDefaultVisibility() {
		return defaultVisibility;
	}

	/**
	 * Sets the default {@link Visibility} for newly created {@link UiElement}
	 * objects
	 * 
	 * @param defaultVisibility
	 *            The {@link Visibility} to set as default
	 */
	public static void setDefaultVisibility(Visibility defaultVisibility) {
		if (defaultVisibility == null) {
			return;
		}
		UiContainer.defaultVisibility = defaultVisibility;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
