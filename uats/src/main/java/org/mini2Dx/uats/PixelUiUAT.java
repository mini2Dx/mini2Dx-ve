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
package org.mini2Dx.uats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import org.mini2Dx.core.exception.ControllerPlatformException;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.game.GameResizeListener;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.BasicGameScreen;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.uats.util.ScreenIds;
import org.mini2Dx.uats.util.UATApplication;
import org.mini2Dx.uats.util.UATSelectionScreen;
import org.mini2Dx.uats.util.UiUtils;
import org.mini2Dx.ui.UiContainer;
import org.mini2Dx.ui.controller.ControllerUiInput;
import org.mini2Dx.ui.element.*;
import org.mini2Dx.ui.event.ActionEvent;
import org.mini2Dx.ui.layout.FlexDirection;
import org.mini2Dx.ui.layout.HorizontalAlignment;
import org.mini2Dx.ui.layout.VerticalAlignment;
import org.mini2Dx.ui.listener.ActionListener;
import org.mini2Dx.ui.navigation.VerticalUiNavigation;
import org.mini2Dx.ui.style.UiTheme;

/**
 * User acceptance test for pixel UI layout
 */
public class PixelUiUAT extends BasicGameScreen implements GameResizeListener {
	private final AssetManager assetManager;

	private UiContainer uiContainer;
	private ControllerUiInput<?> controllerInput;

	private Container topLeftContainer, centerContainer, bottomRightContainer;

	private TabView tabView;
	private Select<String> select;
	private TextBox textBox;
	private Checkbox checkbox;
	private RadioButton radioButton;
	private Slider slider;
	private Label textBoxResult, checkboxResult, radioButtonResult, sliderResult;

	private int nextScreenId = -1;

	public PixelUiUAT(AssetManager assetManager) {
		super();
		this.assetManager = assetManager;
	}

	@Override
	public void initialise(GameContainer gc) {
		uiContainer = new UiContainer(gc, assetManager);
		uiContainer.setKeyboardNavigationEnabled(UATApplication.USE_KEYBOARD_NAVIGATION);
		gc.addResizeListener(this);
		initialiseUi();

		if(Controllers.getControllers().size > 0) {
			try {
				System.out.println(uiContainer.getId());
				controllerInput = UiUtils.setUpControllerInput(Controllers.getControllers().get(0), uiContainer);
				if(controllerInput != null) {
					controllerInput.disable();
				}
			} catch (ControllerPlatformException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> screenManager, float delta) {
		//System.out.println("Row " + uiContainer.getElementById("row-select").isRenderNodeDirty());
		//System.out.println("Select " + select.isRenderNodeDirty());

		uiContainer.update(delta);
		if (nextScreenId > -1) {
			screenManager.enterGameScreen(nextScreenId, new FadeOutTransition(), new FadeInTransition());
			nextScreenId = -1;
		}
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {
		uiContainer.interpolate(alpha);
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		g.setBackgroundColor(Color.WHITE);
		g.setColor(Color.BLACK);
		uiContainer.render(g);
	}

	@Override
	public int getId() {
		return ScreenIds.getScreenId(PixelUiUAT.class);
	}

	@Override
	public void onResize(int width, int height) {
		uiContainer.set(width, height);
	}

	@Override
	public void preTransitionIn(Transition transitionIn) {
		nextScreenId = -1;
		if (!UiContainer.isThemeApplied()) {
			UiContainer.setTheme(assetManager.get(UiTheme.DEFAULT_THEME_FILENAME, UiTheme.class));
		}
		Gdx.input.setInputProcessor(uiContainer);
	}

	private void initialiseUi() {
		topLeftContainer = new Container("top-left-frame");
		topLeftContainer.setStyleId("no-background");

		final Label header = UiUtils.createHeader("UI UAT");

		Div topLeftHeaderDiv = Div.withElements("top-left-header", header);
		topLeftContainer.add(topLeftHeaderDiv);

		Button backRowButton = UiUtils.createButton(null, "", new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onActionBegin(ActionEvent event) {
				// TODO Auto-generated method stub

			}
		});
		backRowButton.set(0f, 0f, 50f, 20f);

		Div backDiv = Div.withElements("behind-header", backRowButton);
		backDiv.setZIndex(-1);
		topLeftContainer.add(backDiv);

		topLeftContainer.shrinkToContents(true);
		topLeftContainer.snapTo(uiContainer, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		topLeftContainer.setVisibility(Visibility.VISIBLE);
		uiContainer.add(topLeftContainer);

		VerticalUiNavigation tab1Navigation = new VerticalUiNavigation();
		textBox = UiUtils.createTextBox(tab1Navigation, "textbox", new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				textBoxResult.setText(textBox.getValue());
			}

			@Override
			public void onActionBegin(ActionEvent event) {
			}
		});
		textBox.setFlexLayout(null);
		textBox.set(0f, 0f, uiContainer.getWidth() * 0.75f, 25f);

		select = UiUtils.createSelect(tab1Navigation, "select", new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				System.out.println("Selected value: " + select.getSelectedOption().getValue());
			}

			@Override
			public void onActionBegin(ActionEvent event) {
			}
		});
		select.setFlexLayout(null);
		select.set(0f, 0f, uiContainer.getWidth() * 0.75f, 25f);

		textBoxResult = UiUtils.createLabel("");
		checkboxResult = UiUtils.createLabel("Radio Button::responsive = false");
		checkbox = UiUtils.createCheckbox(tab1Navigation, "checkbox", new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				if(checkbox.isChecked()) {
					radioButton.setResponsive(true);
				} else {
					radioButton.setResponsive(false);
				}
				checkboxResult.setText("Radio Button::responsive = " + checkbox.isChecked());
			}

			@Override
			public void onActionBegin(ActionEvent event) {}
		});

		radioButtonResult = UiUtils.createLabel("");
		radioButton = UiUtils.createRadioButton(tab1Navigation, "radioButton", new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				switch(radioButton.getSelectedOption()) {
				case "FlexRow Layout":
					radioButton.setFlexDirection(FlexDirection.ROW);
					break;
				case "FlexRow-Reverse Layout":
					radioButton.setFlexDirection(FlexDirection.ROW_REVERSE);
					break;
				case "Div Layout":
					radioButton.setFlexDirection(FlexDirection.COLUMN);
					break;
				case "Div-Reverse Layout":
					radioButton.setFlexDirection(FlexDirection.COLUMN_REVERSE);
					break;
				}
				radioButtonResult.setText(radioButton.getSelectedOption());
			}

			@Override
			public void onActionBegin(ActionEvent event) {}
		});
		radioButton.addOption("FlexRow Layout");
		radioButton.addOption("FlexRow-Reverse Layout");
		radioButton.addOption("Column Layout");
		radioButton.addOption("Column-Reverse Layout");

		slider = new Slider();
		slider.setVisibility(Visibility.VISIBLE);
		slider.addActionListener(new ActionListener() {

			@Override
			public void onActionEnd(ActionEvent event) {
				sliderResult.setText(String.valueOf(slider.getValue()));
			}

			@Override
			public void onActionBegin(ActionEvent event) {}
		});
		sliderResult = UiUtils.createLabel("0.0");

		TextButton returnButton = UiUtils.createButton(tab1Navigation, "Return to UAT Selection Screen", new ActionListener() {

			@Override
			public void onActionBegin(ActionEvent event) {}

			@Override
			public void onActionEnd(ActionEvent event) {
				nextScreenId = UATSelectionScreen.SCREEN_ID;
			}
		});
		returnButton.setFlexLayout(null);
		returnButton.set(0f, 0f, uiContainer.getWidth() * 0.75f, 20f);

		select.addOption("Item 1", "1");
		select.addOption("Item 2", "2");
		select.addOption("Item 3", "3");

		centerContainer = new Container("main-centerContainer");
		centerContainer.set(0f, 0f, uiContainer.getWidth(), uiContainer.getHeight());
		//centerContainer.snapTo(uiContainer, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);

		tabView = new TabView("tabView");
		tabView.set(0f, 0f, centerContainer.getWidth(), centerContainer.getHeight());
		tabView.setVisibility(Visibility.VISIBLE);

		Tab tab1 = new Tab("tab1", "Tab 1");
		final FlexRow textBoxRow = FlexRow.withElements("row-textbox", textBox, textBoxResult);
		final FlexRow selectRow = FlexRow.withElements("row-select", select);
		final FlexRow checkboxRow = FlexRow.withElements("row-checkbox", checkbox, checkboxResult);
		final FlexRow radioButtonRow = FlexRow.withElements("row-radioButton", radioButton, radioButtonResult);
		final FlexRow sliderRow = FlexRow.withElements("row-slider", slider, sliderResult);
		final FlexRow returnRow = FlexRow.withElements("row-return-button", returnButton);

		textBoxResult.alignBelow(textBox, HorizontalAlignment.LEFT);

		textBoxRow.snapTo(tab1);
		selectRow.alignBelow(textBoxRow, HorizontalAlignment.LEFT);
		checkboxRow.alignBelow(selectRow, HorizontalAlignment.LEFT);
		radioButtonRow.alignBelow(checkboxRow, HorizontalAlignment.LEFT);
		sliderRow.alignBelow(radioButtonRow, HorizontalAlignment.LEFT);
		returnRow.alignBelow(sliderRow, HorizontalAlignment.LEFT);

		tab1.add(textBoxRow);
		tab1.add(selectRow);
		tab1.add(checkboxRow);
		tab1.add(radioButtonRow);
		tab1.add(sliderRow);
		tab1.add(returnRow);
		tab1.setNavigation(tab1Navigation);
		tabView.add(tab1);

//		Tab tab2 = new Tab("tab2", "Tab 2");
//
//		ProgressBar progressBar = new ProgressBar();
//		progressBar.set(0f, 0f, 50f, 20f);
//		progressBar.setValue(0.4f);
//		progressBar.setVisibility(Visibility.VISIBLE);
//		tab2.add(FlexRow.withElements(progressBar));
//
//		Button reAddElementsButton =  UiUtils.createButton(null, "Re-add tabview", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {
//				centerContainer.remove(tabView);
//				centerContainer.add(tabView);
//			}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		tab2.add(FlexRow.withElements(reAddElementsButton));
//
//		tabView.add(tab2);
//
//		Tab tab3 = new Tab("tab3", "Tab 3");
//		Button hiddenButton = UiUtils.createButton(null, "Hidden", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		hiddenButton.setVisibility(Visibility.HIDDEN);
//		tab3.add(FlexRow.withElements(hiddenButton));
//		final ScrollBox scrollBox = new ScrollBox("scrollBox");
//		scrollBox.set(0f, 0f, uiContainer.getWidth() * 0.75f, 300f);
//		scrollBox.setVisibility(Visibility.VISIBLE);
//		scrollBox.setMaxHeight(300f);
//		for(int i = 0; i < 30; i++) {
//			if(i % 2 == 0) {
//				Label label = UiUtils.createLabel("Label " + i);
//				FlexRow row = FlexRow.withElements(label);
//				row.setVisibility(Visibility.VISIBLE);
//				scrollBox.add(row);
//			} else {
//				Button button = UiUtils.createButton(null, "Test", null);
//				FlexRow row = FlexRow.withElements(button);
//				row.setVisibility(Visibility.VISIBLE);
//				scrollBox.add(row);
//			}
//		}
//		tab3.add(FlexRow.withElements(scrollBox));
//
//		Button scrollToTopButton = UiUtils.createButton(null, "Scroll to top (immediate)", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {
//				scrollBox.scrollToTop(true);
//			}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		tab3.add(FlexRow.withElements(scrollToTopButton));
//
//		Button scrollToBottomButton = UiUtils.createButton(null, "Scroll to bottom (immediate)", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {
//				scrollBox.scrollToBottom(true);
//			}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		tab3.add(FlexRow.withElements(scrollToBottomButton));
//
//		scrollToTopButton = UiUtils.createButton(null, "Scroll to top (smooth)", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {
//				scrollBox.scrollToTop(false);
//			}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		tab3.add(FlexRow.withElements(scrollToTopButton));
//
//		scrollToBottomButton = UiUtils.createButton(null, "Scroll to bottom (smooth)", new ActionListener() {
//
//			@Override
//			public void onActionEnd(ActionEvent event) {
//				scrollBox.scrollToBottom(false);
//			}
//
//			@Override
//			public void onActionBegin(ActionEvent event) {}
//		});
//		tab3.add(FlexRow.withElements(scrollToBottomButton));
//
//		tabView.add(tab3);

		tabView.setNextTabHotkey(Input.Keys.E);
		tabView.setPreviousTabHotkey(Input.Keys.Q);

		centerContainer.add(tabView);
		centerContainer.setVisibility(Visibility.VISIBLE);
		centerContainer.setNavigation(tabView.getNavigation());
		//centerContainer.shrinkToContents(true);
		uiContainer.add(centerContainer);
//
//		bottomRightContainer = new Container("bottom-right-frame");
//		bottomRightContainer.set(0f, uiContainer.getHeight() - 25f, uiContainer.getWidth() * 0.33f, 50f);
//		bottomRightContainer.setVisibility(Visibility.VISIBLE);
//		FlexRow bottomFrameFlexRow = FlexRow.withElements("row-os", UiUtils.createHeader("OVERFLOW CLIPPED"));
//		bottomFrameFlexRow.setHeight(12f);
//		bottomFrameFlexRow.setOverflowClipped(true);
//		bottomRightContainer.add(bottomFrameFlexRow);
//		uiContainer.add(bottomRightContainer);
//		bottomRightContainer.snapTo(uiContainer, HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM);
	}
}
