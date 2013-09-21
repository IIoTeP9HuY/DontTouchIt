package com.github.donttouchit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.donttouchit.DontTouchIt;

import java.util.ArrayList;
import java.util.List;

public class ChooseLevelScreen extends BasicScreen {
	private Stage stage = new Stage();
	private VerticalGroup buttonGroup = new VerticalGroup();
	private List<Button> levelButtons = new ArrayList<Button>();

	public ChooseLevelScreen(DontTouchIt game) {
		super(game);
		/**
		 * Add buttons to the list
		 */
		for (Button button : levelButtons) {
			button.pad(100);
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		float x = (width - buttonGroup.getWidth()) / 2;
		float y = (height - buttonGroup.getHeight()) / 2;
		buttonGroup.setPosition(x, y);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void show() {
		System.err.println("Showing MenuScreen");
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	protected void draw() {
		super.draw();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	protected void update(float delta) {
		super.update(delta);
		stage.act(delta);
	}
}

