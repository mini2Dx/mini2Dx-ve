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
