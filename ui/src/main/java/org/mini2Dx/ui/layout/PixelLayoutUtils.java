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
		System.out.println(parentUiElement.getId() + " " + recursive);
		if(recursive) {
			boolean matchedParent = false;
			for(int i = 0; i < parentUiElement.getTotalChildren(); i++) {
				final UiElement child = parentUiElement.getChild(i);
				if(!(child instanceof ParentUiElement)) {
					System.out.println(child.getId() + " not a parent " + child.getX() + "," + child.getY() + " " + child.getWidth() + "," + child.getHeight());
					continue;
				}
				final ParentUiElement nestedTree = (ParentUiElement) child;
				System.out.println(parentUiElement.getId() + " recursive " + nestedTree.getId());
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
			System.out.println(parentUiElement.getId() + " deferred by initial layout");
			deferShrinkToContentsUntilLayout(parentUiElement, recursive, callback);
			return;
		}
		if(!parentUiElement.isInitialUpdateOccurred()) {
			System.out.println(parentUiElement.getId() + " deferred by initial update");
			deferShrinkToContentsUntilUpdate(parentUiElement, recursive, callback);
			return;
		}
		if(parentUiElement.isRenderNodeDirty()) {
			System.out.println(parentUiElement.getId() + " deferred by dirty node");
			deferShrinkToContentsUntilLayout(parentUiElement, recursive, callback);
			return;
		}
		float maxX = 0f;
		float maxY = 0f;
		for(int i = 0; i < parentUiElement.getTotalChildren(); i++) {
			final UiElement child = parentUiElement.getChild(i);
			if(!child.isInitialLayoutOccurred()) {
				System.out.println(parentUiElement.getId() + " deferred by " + child.getId());
				child.deferUntilLayout(new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, recursive, callback);
					}
				});
				return;
			}
			if(!child.isInitialUpdateOccurred()) {
				System.out.println(parentUiElement.getId() + " deferred by " + child.getId());
				child.deferUntilUpdate(new Runnable() {
					@Override
					public void run() {
						shrinkToContents(parentUiElement, recursive, callback);
					}
				});
				return;
			}
			if(child.isRenderNodeDirty()) {
				System.out.println(parentUiElement.getId() + " deferred by " + child.getId());
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
		System.out.println("MXMY: " + maxX + "," + maxY);
		parentUiElement.setContentWidth(maxX - parentUiElement.getPaddingLeft());
		parentUiElement.setContentHeight(maxY - parentUiElement.getPaddingTop());
		System.out.println("SET TO: " + parentUiElement.getId() + " " + parentUiElement.getWidth() + "," + parentUiElement.getHeight());

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
	public static void alignEdgeToEdge(UiElement element, UiElement alignToElement, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		if (!element.isInitialLayoutOccurred()) {
			deferAlignEdgeToEdgeUntilLayout(element, alignToElement, horizontalAlignment, verticalAlignment);
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			deferAlignEdgeToEdgeUntilUpdate(element, alignToElement, horizontalAlignment, verticalAlignment);
			return;
		}
		if (alignToElement.isRenderNodeDirty() || alignToElement.getWidth() <= 0f) {
			deferAlignEdgeToEdgeUntilLayout(element, alignToElement, horizontalAlignment, verticalAlignment);
			return;
		}

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
	}

	protected static void deferAlignEdgeToEdgeUntilUpdate(final UiElement element, final UiElement alignToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		element.deferUntilUpdate(new Runnable() {
			@Override
			public void run() {
				alignEdgeToEdge(element, alignToElement, horizontalAlignment, verticalAlignment);
			}
		});
	}

	protected static void deferAlignEdgeToEdgeUntilLayout(final UiElement element, final UiElement alignToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		element.deferUntilLayout(new Runnable() {
			@Override
			public void run() {
				alignEdgeToEdge(element, alignToElement, horizontalAlignment, verticalAlignment);
			}
		});
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
		if (!element.isInitialLayoutOccurred()) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (alignToElement.isRenderNodeDirty() || alignToElement.getWidth() <= 0f) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignLeftOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
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
		if (!element.isInitialLayoutOccurred()) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}
		if (alignToElement.isRenderNodeDirty() || alignToElement.getWidth() <= 0f) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignRightOf(element, alignToElement, verticalAlignment);
				}
			});
			return;
		}

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
		if (!element.isInitialLayoutOccurred()) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (alignToElement.isRenderNodeDirty() || alignToElement.getWidth() <= 0f) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignBelow(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}

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
		if (!element.isInitialLayoutOccurred()) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			element.deferUntilUpdate(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}
		if (alignToElement.isRenderNodeDirty() || alignToElement.getWidth() <= 0f) {
			element.deferUntilLayout(new Runnable() {
				@Override
				public void run() {
					alignAbove(element, alignToElement, horizontalAlignment);
				}
			});
			return;
		}

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
	public static void snapTo(final UiElement element, UiElement snapToElement, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		if (!element.isInitialLayoutOccurred()) {
			deferSnapToUntilLayout(element, snapToElement, horizontalAlignment, verticalAlignment);
			return;
		}
		if (!element.isInitialUpdateOccurred()) {
			deferSnapToUntilUpdate(element, snapToElement, horizontalAlignment, verticalAlignment);
			return;
		}
		if (snapToElement.isRenderNodeDirty() || snapToElement.getWidth() <= 0f) {
			deferSnapToUntilLayout(element, snapToElement, horizontalAlignment, verticalAlignment);
			return;
		}

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
	}

	protected static void deferSnapToUntilUpdate(final UiElement element, final UiElement snapToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		element.deferUntilUpdate(new Runnable() {
			@Override
			public void run() {
				snapTo(element, snapToElement, horizontalAlignment, verticalAlignment);
			}
		});
	}

	protected static void deferSnapToUntilLayout(final UiElement element, final UiElement snapToElement, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		element.deferUntilLayout(new Runnable() {
			@Override
			public void run() {
				snapTo(element, snapToElement, horizontalAlignment, verticalAlignment);
			}
		});
	}
}
