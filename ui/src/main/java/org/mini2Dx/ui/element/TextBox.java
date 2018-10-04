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
package org.mini2Dx.ui.element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.math.MathUtils;
import org.mini2Dx.core.serialization.annotation.ConstructorArg;
import org.mini2Dx.core.serialization.annotation.Field;
import org.mini2Dx.ui.UiContainer;
import org.mini2Dx.ui.event.ActionEvent;
import org.mini2Dx.ui.event.ActionEventPool;
import org.mini2Dx.ui.event.EventTrigger;
import org.mini2Dx.ui.event.params.EventTriggerParams;
import org.mini2Dx.ui.layout.HorizontalAlignment;
import org.mini2Dx.ui.layout.LayoutRuleset;
import org.mini2Dx.ui.layout.ScreenSize;
import org.mini2Dx.ui.layout.VerticalAlignment;
import org.mini2Dx.ui.listener.ActionListener;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.render.TextBoxRenderNode;
import org.mini2Dx.ui.style.StyleRule;

/**
 * A text box {@link UiElement}. Can optionally function as a password field.
 */
public class TextBox extends UiElement implements Actionable {
	private final Queue<Runnable> deferredQueue = new LinkedList<Runnable>();
	
	private List<ActionListener> actionListeners;
	private String value = "";

	@Field(optional=true)
	private String flexLayout = null;
	@Field(optional = true)
	private float x;
	@Field(optional = true)
	private float y;
	@Field(optional = true)
	private float width;
	@Field(optional = true)
	private float height;
	@Field(optional = true)
	private boolean enabled = true;
	@Field(optional = true)
	private boolean passwordField = false;
	
	protected TextBoxRenderNode renderNode;

	/**
	 * Constructor. Generates a unique ID for this {@link TextBox}
	 */
	public TextBox() {
		this(null);
	}

	/**
	 * Constructor
	 * @param id The unique ID for this {@link TextBox}
	 */
	public TextBox(@ConstructorArg(clazz = String.class, name = "id") String id) {
		super(id);
	}

	@Override
	public void attach(ParentRenderNode<?, ?> parentRenderNode) {
		if (renderNode != null) {
			return;
		}
		renderNode = new TextBoxRenderNode(parentRenderNode, this);
		parentRenderNode.addChild(renderNode);
	}

	@Override
	public void detach(ParentRenderNode<?, ?> parentRenderNode) {
		if (renderNode == null) {
			return;
		}
		parentRenderNode.removeChild(renderNode);
		renderNode = null;
	}

	@Override
	public void setVisibility(Visibility visibility) {
		if (this.visibility == visibility) {
			return;
		}
		this.visibility = visibility;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public void setStyleId(String styleId) {
		if (styleId == null) {
			return;
		}
		if (this.styleId.equals(styleId)) {
			return;
		}
		this.styleId = styleId;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}
	
	@Override
	public void syncWithUpdate() {
		while (!effects.isEmpty()) {
			renderNode.applyEffect(effects.poll());
		}

		x = renderNode.getRelativeX();
		y = renderNode.getRelativeY();
		width = renderNode.getOuterWidth();
		height = renderNode.getOuterHeight();

		processUpdateDeferred();
	}

	/**
	 * Returns the text value entered into this {@link TextBox}
	 * @return An empty {@link String} by default
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the text value entered into this {@link TextBox}
	 * @param value A non-null {@link String}
	 */
	public void setValue(String value) {
		if (value == null) {
			return;
		}
		if (value.equals(this.value)) {
			return;
		}
		this.value = value;
		
		if (renderNode == null) {
			return;
		}
		renderNode.updateBitmapFontCache();
	}

	/**
	 * Returns if this {@link TextBox} is a password field
	 * @return True if characters are hidden by * characters
	 */
	public boolean isPasswordField() {
		return passwordField;
	}

	/**
	 * Sets if this {@link TextBox} is a password field
	 * @param passwordField True if characters should be hidden by * characters
	 */
	public void setPasswordField(boolean passwordField) {
		this.passwordField = passwordField;
	}

	@Override
	public void notifyActionListenersOfBeginEvent(EventTrigger eventTrigger, EventTriggerParams eventTriggerParams) {
		if (actionListeners == null) {
			return;
		}
		ActionEvent event = ActionEventPool.allocate();
		event.set(this, eventTrigger, eventTriggerParams);
		for (int i = actionListeners.size() - 1; i >= 0; i--) {
			actionListeners.get(i).onActionBegin(event);
		}
		ActionEventPool.release(event);
	}

	@Override
	public void notifyActionListenersOfEndEvent(EventTrigger eventTrigger, EventTriggerParams eventTriggerParams) {
		if (actionListeners == null) {
			return;
		}
		ActionEvent event = ActionEventPool.allocate();
		event.set(this, eventTrigger, eventTriggerParams);
		for (int i = actionListeners.size() - 1; i >= 0; i--) {
			actionListeners.get(i).onActionEnd(event);
		}
		ActionEventPool.release(event);
	}

	@Override
	public void addActionListener(ActionListener listener) {
		if (actionListeners == null) {
			actionListeners = new ArrayList<ActionListener>(1);
		}
		actionListeners.add(listener);
	}

	@Override
	public void removeActionListener(ActionListener listener) {
		if (actionListeners == null) {
			return;
		}
		actionListeners.remove(listener);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public StyleRule getStyleRule() {
		if(!UiContainer.isThemeApplied()) {
			return null;
		}
		return UiContainer.getTheme().getTextBoxStyleRule(styleId, ScreenSize.XS);
	}

	public String getFlexLayout() {
		return flexLayout;
	}

	public void setFlexLayout(String flexLayout) {
		if(flexLayout == null) {
			return;
		}
		if(this.flexLayout != null && this.flexLayout.equals(flexLayout)) {
			return;
		}
		this.flexLayout = flexLayout;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public boolean isRenderNodeDirty() {
		if (renderNode == null) {
			return true;
		}
		return renderNode.isDirty();
	}

	@Override
	public void setRenderNodeDirty() {
		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public boolean isInitialLayoutOccurred() {
		if (renderNode == null) {
			return false;
		}
		return renderNode.isInitialLayoutOccurred();
	}

	@Override
	public boolean isInitialUpdateOccurred() {
		if(renderNode == null) {
			return false;
		}
		return renderNode.isInitialUpdateOccurred();
	}
}
