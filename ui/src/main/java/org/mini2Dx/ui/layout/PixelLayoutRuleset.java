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
package org.mini2Dx.ui.layout;

import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.render.RenderNode;

public class PixelLayoutRuleset extends LayoutRuleset {
	private final OffsetRule xRule, yRule;
	private final SizeRule widthRule, heightRule;

	public PixelLayoutRuleset(String rules, String ruleValue) {
		super(rules);
		final String [] values = ruleValue.split(",");
		if(values.length != 4) {
			throw new MdxException("Invalid pixel layout '" + rules + "'. Must be pixel:x,y,width,height");
		}
		xRule = new AbsoluteOffsetRule(Float.parseFloat(values[0]));
		yRule = new AbsoluteOffsetRule(Float.parseFloat(values[1]));
		widthRule = new AbsoluteSizeRule(Float.parseFloat(values[2]));
		heightRule = new AbsoluteSizeRule(Float.parseFloat(values[3]));
	}

	@Override
	public void layout(LayoutState layoutState, ParentRenderNode<?, ?> parentNode, Array<RenderNode<?, ?>> children) {
		float startX = parentNode.getStyle().getPaddingLeft();
		float startY = parentNode.getStyle().getPaddingTop();

		for (int i = 0; i < children.size; i++) {
			RenderNode<?, ?> node = children.get(i);
			node.layout(layoutState);
			if (!node.isIncludedInLayout()) {
				continue;
			}
			node.setRelativeX(startX + node.getXOffset());
			node.setRelativeY(startY + node.getYOffset());
		}
	}

	@Override
	public float getPreferredElementRelativeX(LayoutState layoutState) {
		return xRule.getOffset(layoutState);
	}

	@Override
	public float getPreferredElementRelativeY(LayoutState layoutState) {
		return yRule.getOffset(layoutState);
	}

	@Override
	public float getPreferredElementWidth(LayoutState layoutState) {
		return widthRule.getSize(layoutState);
	}

	@Override
	public float getPreferredElementHeight(LayoutState layoutState) {
		return heightRule.getSize(layoutState);
	}

	@Override
	public boolean isHiddenByInputSource(LayoutState layoutState) {
		return false;
	}

	@Override
	public SizeRule getCurrentWidthRule() {
		return widthRule;
	}

	@Override
	public SizeRule getCurrentHeightRule() {
		return heightRule;
	}

	@Override
	public OffsetRule getCurrentOffsetXRule() {
		return xRule;
	}

	@Override
	public OffsetRule getCurrentOffsetYRule() {
		return yRule;
	}


}
