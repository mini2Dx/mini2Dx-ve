/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.ui.render;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import org.mini2Dx.core.controller.button.ControllerButton;
import org.mini2Dx.ui.element.Actionable;
import org.mini2Dx.ui.element.Tab;
import org.mini2Dx.ui.layout.LayoutState;
import org.mini2Dx.ui.navigation.ControllerHotKeyOperation;
import org.mini2Dx.ui.navigation.KeyboardHotKeyOperation;

/**
 * {@link RenderNode} implementation for {@link Tab}
 */
public class TabRenderNode extends DivRenderNode implements NavigatableRenderNode {
	private IntMap<String> keyboardHotkeys = new IntMap<String>();
	private ObjectMap<String, String> controllerHotkeys = new ObjectMap<String, String>();

	public TabRenderNode(ParentRenderNode<?, ?> parent, Tab tab) {
		super(parent, tab);
	}

	@Override
	public ActionableRenderNode hotkey(int keycode) {
		String id = keyboardHotkeys.get(keycode);
		if (id == null) {
			return null;
		}
		RenderNode<?, ?> renderNode = searchTreeForElementById(id);
		if (renderNode == null) {
			return null;
		}
		return (ActionableRenderNode) renderNode;
	}

	@Override
	public ActionableRenderNode hotkey(ControllerButton controllerButton) {
		String id = controllerHotkeys.get(controllerButton.getAbsoluteValue());
		if (id == null) {
			return null;
		}
		RenderNode<?, ?> renderNode = searchTreeForElementById(id);
		if (renderNode == null) {
			return null;
		}
		return (ActionableRenderNode) renderNode;
	}

	@Override
	public void syncHotkeys(Queue<ControllerHotKeyOperation> controllerHotKeyOperations,
							Queue<KeyboardHotKeyOperation> keyboardHotKeyOperations) {
		while (controllerHotKeyOperations.size > 0) {
			ControllerHotKeyOperation hotKeyOperation = controllerHotKeyOperations.removeFirst();
			if (hotKeyOperation.isMapOperation()) {
				controllerHotkeys.put(hotKeyOperation.getControllerButton().getAbsoluteValue(),
						hotKeyOperation.getActionable().getId());
			} else {
				if (hotKeyOperation.getControllerButton() == null) {
					controllerHotkeys.clear();
				} else {
					controllerHotkeys.remove(hotKeyOperation.getControllerButton().getAbsoluteValue());
				}
			}
		}
		while (keyboardHotKeyOperations.size > 0) {
			KeyboardHotKeyOperation hotKeyOperation = keyboardHotKeyOperations.removeFirst();
			if (hotKeyOperation.isMapOperation()) {
				keyboardHotkeys.put(hotKeyOperation.getKeycode(), hotKeyOperation.getActionable().getId());
			} else {
				if (hotKeyOperation.getKeycode() == Integer.MAX_VALUE) {
					keyboardHotkeys.clear();
				} else {
					keyboardHotkeys.remove(hotKeyOperation.getKeycode());
				}
			}
		}
	}

	@Override
	public ActionableRenderNode navigate(int keycode) {
		Actionable actionable = ((Tab) element).getNavigation().navigate(keycode);
		if (actionable == null) {
			return null;
		}
		return (ActionableRenderNode) searchTreeForElementById(actionable.getId());
	}
}
