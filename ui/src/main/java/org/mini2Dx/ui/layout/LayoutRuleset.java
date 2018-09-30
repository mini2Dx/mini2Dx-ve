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
package org.mini2Dx.ui.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.controller.ControllerType;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.ui.InputSource;
import org.mini2Dx.ui.element.UiElement;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.render.RenderNode;

/**
 * The size and offset ruleset of a {@link UiElement} for different
 * {@link ScreenSize}s
 */
public abstract class LayoutRuleset {
	public static final String DEFAULT_RULESET = "flex-column:xs-12c,xs-auto";

	public static LayoutRuleset parse(String layout) {
		final String [] typeAndValue = layout.toLowerCase().split(":");
		switch(typeAndValue[0]) {
		case "p":
		case "pix":
		case "pixel":
			return new PixelLayoutRuleset(layout, typeAndValue[1]);
		case "flex-col":
		case "flex-column":
			return new FlexLayoutRuleset(FlexDirection.COLUMN, layout, typeAndValue[1]);
		case "flex-r":
		case "flex-row":
			return new FlexLayoutRuleset(FlexDirection.ROW, layout, typeAndValue[1]);
		case "flex-col-r":
		case "flex-column-r":
		case "flex-column-reverse":
			return new FlexLayoutRuleset(FlexDirection.COLUMN_REVERSE, layout, typeAndValue[1]);
		case "flex-r-r":
		case "flex-row-r":
		case "flex-row-reverse":
			return new FlexLayoutRuleset(FlexDirection.ROW_REVERSE, layout, typeAndValue[1]);
		case "flex-cen":
		case "flex-centre":
		case "flex-center":
			return new FlexLayoutRuleset(FlexDirection.CENTER, layout, typeAndValue[1]);
		}
		throw new MdxException("Invalid layout type '" + typeAndValue[0] + "'");
	}

	private final String rules;

	public LayoutRuleset(String rules) {
		super();
		this.rules = rules;
	}

	public abstract void layout(LayoutState layoutState, ParentRenderNode<?, ?> parentNode, Array<RenderNode<?, ?>> children);

	public abstract float getPreferredElementRelativeX(LayoutState layoutState);

	public abstract float getPreferredElementRelativeY(LayoutState layoutState);

	public abstract float getPreferredElementWidth(LayoutState layoutState);

	public abstract float getPreferredElementHeight(LayoutState layoutState);

	public abstract boolean isHiddenByInputSource(LayoutState layoutState);

	public abstract SizeRule getCurrentWidthRule();

	public abstract SizeRule getCurrentHeightRule();

	public abstract OffsetRule getCurrentOffsetXRule();

	public abstract OffsetRule getCurrentOffsetYRule();

	public boolean equals(String rules) {
		return this.rules.equals(rules);
	}
}
