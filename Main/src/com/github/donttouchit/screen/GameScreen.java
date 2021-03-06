package com.github.donttouchit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.github.donttouchit.DontTouchIt;
import com.github.donttouchit.game.Level;
import com.github.donttouchit.utils.FontUtils;

public class GameScreen extends BasicScreen {
	private Stage stage;
	private Level level;
	private Level.Specification levelSpecification;
	private ImageButton restart;
	private TextButton back = new TextButton("M", FontUtils.style);

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		restart.setPosition(10, Gdx.graphics.getHeight() - 10 - restart.getHeight());
		back.setPosition(10, Gdx.graphics.getHeight() - 10 - restart.getHeight() - 64);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}

	public GameScreen(DontTouchIt game) {
		super(game);
		stage = new Stage();

		SpriteDrawable sprite = new SpriteDrawable(new Sprite(new Texture("resources/restart.png")));
		restart = new ImageButton(sprite);
		restart.pack();
		restart.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Gdx.app.log("Restart button", "Clicked");
				try {
					setLevel(new Level(levelSpecification));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().setScreen(getGame().getChooseLevelScreen());
			}
		});
	}

	public void setLevel(Level level) {
		stage.clear();
		this.level = level;
		levelSpecification = level.getSpecification();
		stage.addActor(level.getGroup());
		stage.addActor(restart);
		stage.addActor(back);
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

//		float x = (Gdx.graphics.getWidth() - level.getGroup().getWidth()) / 2;
//		float y = (Gdx.graphics.getHeight() - level.getGroup().getHeight()) / 2;
////		level.getGroup().setPosition(x, y);
//		level.getGroup().setPosition(0, 0);
	}
}
