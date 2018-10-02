/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ui.element;

import com.badlogic.gdx.math.MathUtils;
import org.mini2Dx.core.controller.button.ControllerButton;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.core.serialization.annotation.ConstructorArg;
import org.mini2Dx.ui.UiContainer;
import org.mini2Dx.ui.layout.HorizontalAlignment;
import org.mini2Dx.ui.layout.VerticalAlignment;
import org.mini2Dx.ui.navigation.ControllerHotKeyOperation;
import org.mini2Dx.ui.navigation.KeyboardHotKeyOperation;
import org.mini2Dx.ui.navigation.UiNavigation;
import org.mini2Dx.ui.navigation.VerticalUiNavigation;
import org.mini2Dx.ui.render.ActionableRenderNode;
import org.mini2Dx.ui.render.ContainerRenderNode;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.style.UiTheme;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Element for containing {@link UiElement}s. {@link Container} can act as a window through
 * {@link UiTheme} styles.
 */
public class Container extends Column implements Navigatable {
	private final Queue<ControllerHotKeyOperation> controllerHotKeyOperations = new LinkedList<ControllerHotKeyOperation>();
	private final Queue<KeyboardHotKeyOperation> keyboardHotKeyOperations = new LinkedList<KeyboardHotKeyOperation>();

	private UiNavigation navigation = new VerticalUiNavigation();

	/**
	 * Constructor. Generates a unique ID for the {@link Container}
	 */
	public Container() {
		this(null);
	}

	/**
	 * Constructor
	 * @param id The unique ID for the {@link Container}
	 */
	public Container(@ConstructorArg(clazz = String.class, name = "id") String id) {
		super(id);
	}

	/**
	 * Aligns this container to the same area of another element.
	 * Note: This element and the align element must already be added to the render tree before this method can be called.
	 *
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment The {@link HorizontalAlignment} of this element
	 * @param verticalAlignment The {@link VerticalAlignment} of this element
	 */
	public void alignTo(ParentUiElement alignToElement, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		if (renderNode == null) {
			throw new MdxException("Container must be added to a UiContainer before alignment can be set");
		}
		if (alignToElement.getWidth() < 0f) {
			throw new MdxException("alignToElement must be added to a UiContainer before alignment can be set");
		}
		final int x, y;

		switch (horizontalAlignment) {
		default:
		case LEFT:
			x = MathUtils.round(alignToElement.getX());
			break;
		case CENTER:
			x = MathUtils.round(alignToElement.getX() + (alignToElement.getWidth() * 0.5f) - (getWidth() * 0.5f));
			break;
		case RIGHT:
			x = MathUtils.round(alignToElement.getX() + alignToElement.getWidth() - getWidth());
			break;
		}
		switch (verticalAlignment) {
		default:
		case TOP:
			y = MathUtils.round(alignToElement.getY());
			break;
		case MIDDLE:
			y = MathUtils.round(alignToElement.getY() + (alignToElement.getHeight() * 0.5f) - (getHeight() * 0.5f));
			break;
		case BOTTOM:
			y = MathUtils.round(alignToElement.getY() + alignToElement.getHeight() - getHeight());
			break;
		}

		if (getLayout().startsWith("pixel")) {
			final String [] components = getLayout().split(",");
			setLayout("pixel:" + x + "," + y + "," + components[2] + "," + components[3]);
		} else {
			final String originalLayout = getLayout();
			final String [] originalLayoutComponents = originalLayout.split(":");
			final String flexComponent = originalLayoutComponents[0];
			final String [] xyComponents = originalLayoutComponents[1].split(",");
			final String [] xComponent = xyComponents[0].split(" ");

			final StringBuilder result = new StringBuilder(flexComponent);
			result.append(':');
			result.append("xs-offset-" + x + "px");

			for(int i = 0; i < xComponent.length; i++) {
				if(xComponent[i].contains("offset")) {
					continue;
				}
				result.append(' ');
				result.append(xComponent[i]);
			}

			result.append(',');
			result.append("xs-offset-" + y + "px");
			if(xyComponents.length == 1) {
				result.append(' ');
				result.append("xs-auto");
			} else {
				final String [] yComponent = xyComponents[1].split(" ");
				for(int i = 0; i < yComponent.length; i++) {
					if(yComponent[i].contains("offset")) {
						continue;
					}
					result.append(' ');
					result.append(yComponent[i]);
				}
			}
			setLayout(result.toString());
		}
	}

	/**
	 * Calls {@link #alignTo(ParentUiElement, HorizontalAlignment, VerticalAlignment)} on the next update
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment The {@link HorizontalAlignment} of this element
	 * @param verticalAlignment The {@link VerticalAlignment} of this element
	 */
	public void deferAlignTo(final ParentUiElement alignToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		defer(new Runnable() {
			@Override
			public void run() {
				alignTo(alignToElement, horizontalAlignment, verticalAlignment);
			}
		});
	}

	@Override
	public void syncWithRenderNode() {
		super.syncWithRenderNode();
		((ContainerRenderNode) renderNode).syncHotkeys(controllerHotKeyOperations, keyboardHotKeyOperations);
	}

	@Override
	protected ParentRenderNode<?, ?> createRenderNode(ParentRenderNode<?, ?> parent) {
		return new ContainerRenderNode(parent, this);
	}

	@Override
	public ActionableRenderNode navigate(int keycode) {
		if (renderNode == null) {
			return null;
		}
		return ((ContainerRenderNode) renderNode).navigate(keycode);
	}

	@Override
	public ActionableRenderNode hotkey(int keycode) {
		if (renderNode == null) {
			return null;
		}
		return ((ContainerRenderNode) renderNode).hotkey(keycode);
	}

	@Override
	public ActionableRenderNode hotkey(ControllerButton button) {
		if (renderNode == null) {
			return null;
		}
		return ((ContainerRenderNode) renderNode).hotkey(button);
	}

	@Override
	public void setHotkey(ControllerButton button, Actionable actionable) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, actionable, true));
	}

	@Override
	public void setHotkey(int keycode, Actionable actionable) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, actionable, true));
	}

	@Override
	public void unsetHotkey(ControllerButton button) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, null, false));
	}

	@Override
	public void unsetHotkey(int keycode) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, null, false));
	}

	@Override
	public void clearControllerHotkeys() {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(null, null, false));
	}

	@Override
	public void clearKeyboardHotkeys() {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(Integer.MAX_VALUE, null, false));
	}

	@Override
	public void clearHotkeys() {
		clearControllerHotkeys();
		clearKeyboardHotkeys();
	}

	@Override
	public UiNavigation getNavigation() {
		return navigation;
	}

	public void setNavigation(UiNavigation navigation) {
		if (navigation == null) {
			return;
		}
		this.navigation = navigation;
	}
}
