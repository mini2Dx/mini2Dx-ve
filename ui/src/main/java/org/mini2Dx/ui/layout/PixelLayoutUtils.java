/**
 * Copyright (c) 2018 See AUTHORS file
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ui.layout;

import com.badlogic.gdx.math.MathUtils;
import javafx.scene.Parent;
import org.mini2Dx.ui.UiContainer;
import org.mini2Dx.ui.element.ParentUiElement;
import org.mini2Dx.ui.element.UiElement;

/**
 * Utility class for layout of elements based on pixel coordinates
 */
public class PixelLayoutUtils {

	/**
	 * Shrinks the width and height for a {@link ParentUiElement} based on its children
	 * @param parentUiElement The {@link ParentUiElement} to set
	 */
	public static void shrinkToContents(final ParentUiElement parentUiElement, final boolean recursive, final Runnable callback) {
		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			deferShrinkToContentsUntilUpdate(parentUiElement, recursive, callback);
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			break;
		}

		if(recursive) {
			boolean matchedParent = false;
			for(int i = 0; i < parentUiElement.getTotalChildren(); i++) {
				final UiElement child = parentUiElement.getChild(i);
				if(!(child instanceof ParentUiElement)) {
					continue;
				}
				final ParentUiElement nestedTree = (ParentUiElement) child;
				nestedTree.shrinkToContents(true, new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, false, callback);
					}
				});
				matchedParent = true;
			}

			if(matchedParent) {
				return;
			}
		}

		if(!parentUiElement.isInitialLayoutOccurred()) {
			deferShrinkToContentsUntilLayout(parentUiElement, recursive, callback);
			return;
		}
		if(!parentUiElement.isInitialUpdateOccurred()) {
			deferShrinkToContentsUntilUpdate(parentUiElement, recursive, callback);
			return;
		}
		float maxX = 0f;
		float maxY = 0f;
		for(int i = 0; i < parentUiElement.getTotalChildren(); i++) {
			final UiElement child = parentUiElement.getChild(i);
			if(!child.isInitialLayoutOccurred()) {
				child.deferUntilLayout(new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, recursive, callback);
					}
				});
				return;
			}
			if(!child.isInitialUpdateOccurred()) {
				child.deferUntilUpdate(new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, recursive, callback);
					}
				});
				return;
			}
			if(child.isRenderNodeDirty()) {
				child.deferUntilLayout(new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, recursive, callback);
					}
				});
				return;
			}
			maxX = Math.max(maxX, child.getX() + child.getWidth());
			maxY = Math.max(maxY, child.getY() + child.getHeight());
		}
		parentUiElement.setContentWidth(maxX);
		parentUiElement.setContentHeight(maxY);

		if(parentUiElement.getId().contains("Selection Screen")) {
			System.out.println("Selection Screen " + maxY);
		}

		if(callback != null) {
			parentUiElement.deferUntilLayout(callback);
		}
	}

	private static void deferShrinkToContentsUntilLayout(final ParentUiElement parentUiElement, final boolean recursive, final Runnable callback) {
		if(parentUiElement.getTotalChildren() > 0) {
			parentUiElement.setRenderNodeDirty();
			final UiElement lastChild = parentUiElement.get(parentUiElement.getTotalChildren() - 1);
			lastChild.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					shrinkToContents(parentUiElement, recursive, callback);
				}
			});
			return;
		}
		parentUiElement.deferUntilLayout(new Runnable() {
			@Override
			public void run() {
				shrinkToContents(parentUiElement, recursive, callback);
			}
		});
	}

	private static void deferShrinkToContentsUntilUpdate(final ParentUiElement parentUiElement, final boolean recursive, final Runnable callback) {
		if(parentUiElement.getTotalChildren() > 0) {
			parentUiElement.get(parentUiElement.getTotalChildren() - 1).deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					shrinkToContents(parentUiElement, recursive, callback);
				}
			});
			return;
		}
		parentUiElement.deferUntilUpdate(new Runnable() {
			@Override
			public void run() {
				shrinkToContents(parentUiElement, recursive, callback);
			}
		});
	}

	/**
	 * Aligns the edges of a {@link UiElement} to the edges of another element.
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment {@link HorizontalAlignment#LEFT} aligns the right-side of this element to the left side of the align element.
	 * 	{@link HorizontalAlignment#CENTER} aligns the center of this element to the center of the align element.
	 * 	{@link HorizontalAlignment#RIGHT} aligns the left-side of this element to the right-side of the align element.
	 * @param verticalAlignment {@link VerticalAlignment#TOP} aligns the bottom-side of this element to the top-side of the align element.
	 * 	{@link VerticalAlignment#MIDDLE} aligns the middle of this element to the middle of the align element.
	 * 	{@link VerticalAlignment#BOTTOM} aligns the top-side of this element to the bottom-side of the align element.
	 */
	public static void alignEdgeToEdge(final UiElement element, final UiElement alignToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignEdgeToEdge(element, alignToElement, horizontalAlignment, verticalAlignment);
				}
			});
			return;
		}
		if (!alignToElement.isInitialised()) {
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignEdgeToEdge(element, alignToElement, horizontalAlignment, verticalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignEdgeToEdge(element, alignToElement, horizontalAlignment, verticalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x,y;
			switch (horizontalAlignment) {
			default:
			case LEFT:
				x = MathUtils.round(alignToElement.getX() - element.getWidth());
				break;
			case CENTER:
				x = MathUtils.round(alignToElement.getX() + (alignToElement.getWidth() * 0.5f) - (element.getWidth() * 0.5f));
				break;
			case RIGHT:
				x = MathUtils.round(alignToElement.getX() + alignToElement.getWidth());
				break;
			}
			switch (verticalAlignment) {
			default:
			case TOP:
				y = MathUtils.round(alignToElement.getY() - element.getHeight());
				break;
			case MIDDLE:
				y = MathUtils.round(alignToElement.getY() + (alignToElement.getHeight() * 0.5f) - (element.getHeight() * 0.5f));
				break;
			case BOTTOM:
				y = MathUtils.round(alignToElement.getY() + alignToElement.getHeight());
				break;
			}

			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Aligns the right edge of a {@link UiElement} to the left edge of another element
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param verticalAlignment {@link VerticalAlignment#TOP} aligns the top-side of this element to the top-side of the align element.
	 * 	 * 	{@link VerticalAlignment#MIDDLE} aligns the middle of this element to the middle of the align element.
	 * 	 * 	{@link VerticalAlignment#BOTTOM} aligns the bottom-side of this element to the bottom-side of the align element.
	 */
	public static void alignLeftOf(final UiElement element, final UiElement alignToElement, final VerticalAlignment verticalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (!alignToElement.isInitialised()) {
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x,y;
			switch (verticalAlignment) {
			default:
			case TOP:
				y = MathUtils.round(alignToElement.getY());
				break;
			case MIDDLE:
				y = MathUtils.round(alignToElement.getY() + (alignToElement.getHeight() * 0.5f) - (element.getHeight() * 0.5f));
				break;
			case BOTTOM:
				y = MathUtils.round(alignToElement.getY() + alignToElement.getHeight() - element.getHeight());
				break;
			}

			x = MathUtils.round(alignToElement.getX() - element.getWidth());
			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Aligns the left edge of this element to the right edge of another element
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param verticalAlignment {@link VerticalAlignment#TOP} aligns the top-side of this element to the top-side of the align element.
	 * 	 * 	{@link VerticalAlignment#MIDDLE} aligns the middle of this element to the middle of the align element.
	 * 	 * 	{@link VerticalAlignment#BOTTOM} aligns the bottom-side of this element to the bottom-side of the align element.
	 */
	public static void alignRightOf(final UiElement element, final UiElement alignToElement, final VerticalAlignment verticalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (!alignToElement.isInitialised()) {
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x, y;
			switch (verticalAlignment) {
			default:
			case TOP:
				y = MathUtils.round(alignToElement.getY());
				break;
			case MIDDLE:
				y = MathUtils.round(alignToElement.getY() + (alignToElement.getHeight() * 0.5f) - (element.getHeight() * 0.5f));
				break;
			case BOTTOM:
				y = MathUtils.round(alignToElement.getY() + alignToElement.getHeight() - element.getHeight());
				break;
			}
			x = MathUtils.round(alignToElement.getX() + alignToElement.getWidth());

			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Aligns the top edge of a {@link UiElement} to the bottom of another element
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment {@link HorizontalAlignment#LEFT} aligns the left-side of this element to the left side of the align element.
	 * 	{@link HorizontalAlignment#CENTER} aligns the center of this element to the center of the align element.
	 * 	{@link HorizontalAlignment#RIGHT} aligns the right-side of this element to the right-side of the align element.
	 */
	public static void alignBelow(final UiElement element, final UiElement alignToElement, final HorizontalAlignment horizontalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (!alignToElement.isInitialised()) {
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x,y;
			switch (horizontalAlignment) {
			default:
			case LEFT:
				x = MathUtils.round(alignToElement.getX());
				break;
			case CENTER:
				x = MathUtils.round(alignToElement.getX() + (alignToElement.getWidth() * 0.5f) - (element.getWidth() * 0.5f));
				break;
			case RIGHT:
				x = MathUtils.round(alignToElement.getX() + alignToElement.getWidth() - element.getWidth());
				break;
			}
			y = MathUtils.round(alignToElement.getY() + alignToElement.getHeight());

			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Aligns the bottom edge of a {@link UiElement} to the top of another element
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param alignToElement The {@link UiElement} to align with. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment {@link HorizontalAlignment#LEFT} aligns the left-side of this element to the left side of the align element.
	 * 	 * 	{@link HorizontalAlignment#CENTER} aligns the center of this element to the center of the align element.
	 * 	 * 	{@link HorizontalAlignment#RIGHT} aligns the right-side of this element to the right-side of the align element.
	 */
	public static void alignAbove(final UiElement element, final UiElement alignToElement, final HorizontalAlignment horizontalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (!alignToElement.isInitialised()) {
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			alignToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x,y;
			switch (horizontalAlignment) {
			default:
			case LEFT:
				x = MathUtils.round(alignToElement.getX());
				break;
			case CENTER:
				x = MathUtils.round(alignToElement.getX() + (alignToElement.getWidth() * 0.5f) - (element.getWidth() * 0.5f));
				break;
			case RIGHT:
				x = MathUtils.round(alignToElement.getX() + alignToElement.getWidth() - element.getWidth());
				break;
			}
			y = MathUtils.round(alignToElement.getY() - element.getHeight());

			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Snaps the top-left of a {@link UiElement} to the top-left corner of another {@link UiElement}
	 * @param element The {@link UiElement} to perform alignment on
	 * @param snapToElement The {@link UiElement} to snap to. Note: This can also be the {@link UiContainer}
	 */
	public static void snapTo(final UiElement element, UiElement snapToElement) {
		snapTo(element, snapToElement, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
	}

	/**
	 * Snaps the edge of a {@link UiElement} to the edge of another element.
	 *
	 * @param element The {@link UiElement} to perform alignment on
	 * @param snapToElement The {@link UiElement} to snap to. Note: This can also be the {@link UiContainer}
	 * @param horizontalAlignment The {@link HorizontalAlignment} of this element within the area of the align element
	 * @param verticalAlignment The {@link VerticalAlignment} of this element within the area of the align element
	 */
	public static void snapTo(final UiElement element, final UiElement snapToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					snapTo(element, snapToElement, horizontalAlignment, verticalAlignment);
				}
			});
			return;
		}
		if (!snapToElement.isInitialised()) {
			snapToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					snapTo(element, snapToElement, horizontalAlignment, verticalAlignment);
				}
			});
			return;
		}

		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			snapToElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					snapTo(element, snapToElement, horizontalAlignment, verticalAlignment);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			final float x, y;
			switch (horizontalAlignment) {
			default:
			case LEFT:
				x = MathUtils.round(snapToElement.getX());
				break;
			case CENTER:
				x = MathUtils.round(snapToElement.getX() + (snapToElement.getWidth() * 0.5f) - (element.getWidth() * 0.5f));
				break;
			case RIGHT:
				x = MathUtils.round(snapToElement.getX() + snapToElement.getWidth() - element.getWidth());
				break;
			}
			switch (verticalAlignment) {
			default:
			case TOP:
				y = MathUtils.round(snapToElement.getY());
				break;
			case MIDDLE:
				y = MathUtils.round(snapToElement.getY() + (snapToElement.getHeight() * 0.5f) - (element.getHeight() * 0.5f));
				break;
			case BOTTOM:
				y = MathUtils.round(snapToElement.getY() + snapToElement.getHeight() - element.getHeight());
				break;
			}
			element.setXY(x, y);
			break;
		}
	}

	/**
	 * Sets the width of one {@link UiElement} to match the width of another element
	 * @param element The {@link UiElement} to set the width on
	 * @param matchElement The {@link UiElement} to get the width of
	 */
	public static void setWidthToWidth(final UiElement element, final UiElement matchElement) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToWidth(element, matchElement);
				}
			});
			return;
		}
		if (!matchElement.isInitialised()) {
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToWidth(element, matchElement);
				}
			});
			return;
		}
		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToWidth(element, matchElement);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			element.setWidth(matchElement.getWidth());
			break;
		}
	}

	/**
	 * Sets the width of one {@link UiElement} to match the content width of another element. Note: content width = (width - margin - padding)
	 * @param element The {@link UiElement} to set the width on
	 * @param matchElement The {@link UiElement} to get the content width of
	 */
	public static void setWidthToContentWidth(final UiElement element, final UiElement matchElement) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToContentWidth(element, matchElement);
				}
			});
			return;
		}
		if (!matchElement.isInitialised()) {
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToContentWidth(element, matchElement);
				}
			});
			return;
		}
		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setWidthToContentWidth(element, matchElement);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			element.setWidth(matchElement.getContentWidth());
			break;
		}
	}

	/**
	 * Sets the height of one {@link UiElement} to match the height of another element
	 * @param element The {@link UiElement} to set the height on
	 * @param matchElement The {@link UiElement} to get the height of
	 */
	public static void setHeightToHeight(final UiElement element, final UiElement matchElement) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToHeight(element, matchElement);
				}
			});
			return;
		}
		if (!matchElement.isInitialised()) {
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToHeight(element, matchElement);
				}
			});
			return;
		}
		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToHeight(element, matchElement);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			element.setHeight(matchElement.getHeight());
			break;
		}
	}

	/**
	 * Sets the height of one {@link UiElement} to match the content height of another element. Note: content height = (height - margin - padding)
	 * @param element The {@link UiElement} to set the height on
	 * @param matchElement The {@link UiElement} to get the content height of
	 */
	public static void setHeightToContentHeight(final UiElement element, final UiElement matchElement) {
		if (!element.isInitialised()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToContentHeight(element, matchElement);
				}
			});
			return;
		}
		if (!matchElement.isInitialised()) {
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToContentHeight(element, matchElement);
				}
			});
			return;
		}
		switch(UiContainer.getState()) {
		case LAYOUT:
		case UPDATE:
			matchElement.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					setHeightToContentHeight(element, matchElement);
				}
			});
			break;
		case NOOP:
		case INTERPOLATE:
		case RENDER:
			element.setHeight(matchElement.getContentHeight());
			break;
		}
	}
}
