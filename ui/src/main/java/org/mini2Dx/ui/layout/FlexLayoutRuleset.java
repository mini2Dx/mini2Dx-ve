package org.mini2Dx.ui.layout;

import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.controller.ControllerType;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.ui.InputSource;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.render.RenderNode;

import java.util.*;

public class FlexLayoutRuleset extends LayoutRuleset {

	protected static final String AUTO = "auto";
	protected static final String PIXEL_SUFFIX = "px";
	protected static final String COLUMN_SUFFIX = "c";
	protected static final String EMPTY_STRING = "";

	protected final Map<ScreenSize, SizeRule> widthRules = new HashMap<ScreenSize, SizeRule>();
	protected final Map<ScreenSize, SizeRule> heightRules = new HashMap<ScreenSize, SizeRule>();
	protected final Set<InputSource> hiddenByInput = new HashSet<InputSource>();
	protected final Set<ControllerType> hiddenByControllerType = new HashSet<ControllerType>();
	protected final Map<ScreenSize, OffsetRule> offsetXRules = new HashMap<ScreenSize, OffsetRule>();
	protected final Map<ScreenSize, OffsetRule> offsetYRules = new HashMap<ScreenSize, OffsetRule>();

	private final FlexDirection flexDirection;
	private boolean hiddenByInputSource = false;

	private SizeRule currentWidthRule = null;
	private SizeRule currentHeightRule = null;
	private OffsetRule currentOffsetXRule = null;
	private OffsetRule currentOffsetYRule = null;

	/**
	 * Constructor
	 * @param rules The ruleset, e.g. xs-12c xs-offset-4c sm-500px sm-offset-20px
	 */
	public FlexLayoutRuleset(FlexDirection flexDirection, String rules, String ruleValue) {
		super(rules);
		this.flexDirection = flexDirection;

		String [] components = ruleValue.split(",");
		switch(components.length) {
		case 1: {
			//Horizontal only
			String[] rule = components[0].split(" ");
			for (int i = 0; i < rule.length; i++) {
				String[] ruleDetails = rule[i].split("-");
				switch (ruleDetails.length) {
				case 1:
					throw new MdxException("Invalid flex value '" + rule[i] + "'. Perhaps you forgot a size prefix, e.g. xs-");
				case 2:
					// e.g. xs-12, hidden-controller, visible-touchscreen,
					// hidden-keyboardmouse
					storeSizeRule(true, widthRules, ruleDetails);
					break;
				case 3:
					// e.g. xs-offset-12, hidden-controller-ps4
					storeOffsetRule(true, offsetXRules, ruleDetails);
					break;
				}
			}
			storeSizeRule(false, heightRules, "xs-auto".split("-"));
			break;
		}
		case 2: {
			{
				//Horizontal
				String[] rule = components[0].split(" ");
				for (int i = 0; i < rule.length; i++) {
					String[] ruleDetails = rule[i].split("-");
					switch (ruleDetails.length) {
					case 1:
						throw new MdxException("Invalid flex value '" + rule[i] + "'. Perhaps you forgot a size prefix, e.g. xs-");
					case 2:
						// e.g. xs-12, hidden-controller, visible-touchscreen,
						// hidden-keyboardmouse
						storeSizeRule(true, widthRules, ruleDetails);
						break;
					case 3:
						// e.g. xs-offset-12, hidden-controller-ps4
						storeOffsetRule(true, offsetXRules, ruleDetails);
						break;
					}
				}
			}
			{
				//Vertical
				String[] rule = components[1].split(" ");
				for (int i = 0; i < rule.length; i++) {
					String[] ruleDetails = rule[i].split("-");
					switch (ruleDetails.length) {
					case 1:
						throw new MdxException("Invalid flex value '" + rule[i] + "'. Perhaps you forgot a size prefix, e.g. xs-");
					case 2:
						// e.g. xs-12, hidden-controller, visible-touchscreen,
						// hidden-keyboardmouse
						storeSizeRule(false, heightRules, ruleDetails);
						break;
					case 3:
						// e.g. xs-offset-12, hidden-controller-ps4
						storeOffsetRule(false, offsetYRules, ruleDetails);
						break;
					}
				}
			}
		}
		default:
			break;
		}

		finaliseRuleset(widthRules, offsetXRules);
		finaliseRuleset(heightRules, offsetYRules);
	}

	private void storeSizeRule(boolean horizontalRuleset, Map<ScreenSize, SizeRule> sizeRules, String[] ruleDetails) {
		switch (ruleDetails[0].toLowerCase()) {
		case "hidden": {
			if(!horizontalRuleset) {
				throw new MdxException("hidden-* rules can only be applied to horizontal rulesets");
			}
			switch (InputSource.fromFriendlyString(ruleDetails[1])) {
			case CONTROLLER:
				hiddenByInput.add(InputSource.CONTROLLER);
				break;
			case KEYBOARD_MOUSE:
				hiddenByInput.add(InputSource.KEYBOARD_MOUSE);
				break;
			case TOUCHSCREEN:
				hiddenByInput.add(InputSource.TOUCHSCREEN);
				break;
			}
			break;
		}
		default:
			ScreenSize screenSize = ScreenSize.fromString(ruleDetails[0]);
			if (ruleDetails[1].equalsIgnoreCase(AUTO)) {
				if(horizontalRuleset) {
					throw new MdxException("Invalid size - cannot use auto size for horizontal size rules. Must end be columns (c) or pixels (px)");
				}
				sizeRules.put(screenSize, new AutoSizeRule());
			} else if (ruleDetails[1].endsWith(PIXEL_SUFFIX)) {
				sizeRules.put(screenSize,
						new AbsoluteSizeRule(Integer.parseInt(ruleDetails[1].replace(PIXEL_SUFFIX, EMPTY_STRING))));
			} else if (ruleDetails[1].endsWith(COLUMN_SUFFIX)) {
				if(!horizontalRuleset) {
					throw new MdxException("Invalid size - cannot use column size for vertical size rules. Must be pixel (px) or auto");
				}
				sizeRules.put(screenSize,
						new ResponsiveSizeRule(Integer.parseInt(ruleDetails[1].replace(COLUMN_SUFFIX, EMPTY_STRING))));
			} else {
				throw new MdxException("Invalid size - must end with c (columns) or px (pixels");
			}
			break;
		}
	}

	private void storeOffsetRule(boolean horizontalRuleset, Map<ScreenSize, OffsetRule> offsetRules, String[] ruleDetails) {
		switch (ruleDetails[0].toLowerCase()) {
		case "hidden": {
			if(!horizontalRuleset) {
				throw new MdxException("hidden-* rules can only be applied to horizontal rulesets");
			}
			switch (InputSource.fromFriendlyString(ruleDetails[1])) {
			case CONTROLLER:
				ControllerType controllerType = ControllerType.fromFriendlyString(ruleDetails[2]);
				switch(controllerType) {
				case UNKNOWN:
					break;
				default:
					hiddenByControllerType.add(controllerType);
					break;
				}
				break;
			default:
				throw new MdxException("Invalid rule " + ruleDetails[0] + "-" + ruleDetails[1] + "-" + ruleDetails[2]);
			}
		}
		default: {
			ScreenSize screenSize = ScreenSize.fromString(ruleDetails[0]);
			if (ruleDetails[2].endsWith(PIXEL_SUFFIX)) {
				offsetRules.put(screenSize,
						new AbsoluteOffsetRule(Integer.parseInt(ruleDetails[2].replace(PIXEL_SUFFIX, EMPTY_STRING))));
			} else if (ruleDetails[2].endsWith(COLUMN_SUFFIX)) {
				if(!horizontalRuleset) {
					throw new MdxException("Invalid offset - cannot use column offset for vertical size rules. Must be pixel (px)");
				}
				offsetRules.put(screenSize,
						new ResponsiveOffsetRule(Integer.parseInt(ruleDetails[2].replace(COLUMN_SUFFIX, EMPTY_STRING))));
			} else {
				throw new MdxException("Invalid offset - must end with c (columns) or px (pixels");
			}
		}
		}
	}

	private void finaliseRuleset(Map<ScreenSize, SizeRule> sizeRules, Map<ScreenSize, OffsetRule> offsetRules) {
		Iterator<ScreenSize> screenSizes = ScreenSize.smallestToLargest();
		SizeRule lastSizeRule = new ResponsiveSizeRule(12);
		OffsetRule lastOffsetRule = new AbsoluteOffsetRule(0);

		while (screenSizes.hasNext()) {
			ScreenSize nextSize = screenSizes.next();

			if (!sizeRules.containsKey(nextSize)) {
				sizeRules.put(nextSize, lastSizeRule);
			} else {
				lastSizeRule = sizeRules.get(nextSize);
			}

			if (!offsetRules.containsKey(nextSize)) {
				offsetRules.put(nextSize, lastOffsetRule);
			} else {
				lastOffsetRule = offsetRules.get(nextSize);
			}
		}
	}

	@Override
	public void layout(LayoutState layoutState, ParentRenderNode<?, ?> parentNode, Array<RenderNode<?, ?>> children) {
		flexDirection.layout(layoutState, parentNode, children);
	}

	@Override
	public float getPreferredElementRelativeX(LayoutState layoutState) {
		currentOffsetXRule = offsetXRules.get(layoutState.getScreenSize());
		return currentOffsetXRule.getOffset(layoutState);
	}

	@Override
	public float getPreferredElementRelativeY(LayoutState layoutState) {
		currentOffsetYRule = offsetYRules.get(layoutState.getScreenSize());
		return currentOffsetYRule.getOffset(layoutState);
	}

	@Override
	public float getPreferredElementWidth(LayoutState layoutState) {
		currentWidthRule = widthRules.get(layoutState.getScreenSize());
		return currentWidthRule.getSize(layoutState);
	}

	@Override
	public float getPreferredElementHeight(LayoutState layoutState) {
		currentHeightRule = heightRules.get(layoutState.getScreenSize());
		return currentHeightRule.getSize(layoutState);
	}

	public boolean isHiddenByInputSource(LayoutState layoutState) {
		switch(layoutState.getLastInputSource()) {
		case CONTROLLER:
			if(hiddenByControllerType.isEmpty()) {
				hiddenByInputSource = hiddenByInput.contains(layoutState.getLastInputSource());
			} else {
				hiddenByInputSource = hiddenByControllerType.contains(layoutState.getLastControllerType());
			}
			break;
		case KEYBOARD_MOUSE:
			hiddenByInputSource = hiddenByInput.contains(layoutState.getLastInputSource());
			break;
		case TOUCHSCREEN:
			hiddenByInputSource = hiddenByInput.contains(layoutState.getLastInputSource());
			break;
		default:
			break;
		}
		return hiddenByInputSource;
	}

	public boolean isHiddenByInputSource() {
		return hiddenByInputSource;
	}

	public SizeRule getCurrentWidthRule() {
		return currentWidthRule;
	}

	public SizeRule getCurrentHeightRule() {
		return currentHeightRule;
	}

	public OffsetRule getCurrentOffsetXRule() {
		return currentOffsetXRule;
	}

	public OffsetRule getCurrentOffsetYRule() {
		return currentOffsetYRule;
	}
}
