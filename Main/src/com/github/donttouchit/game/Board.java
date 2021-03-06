package com.github.donttouchit.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.github.donttouchit.geom.GridPoint;
import com.github.donttouchit.geom.LevelChecker;

public class Board extends Actor {
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private static final Texture wallTexture = new Texture("resources/wall.png");
	private static final Texture floorTexture = new Texture("resources/floor.png");

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	private Level level;

	public Board(Level level) {
		setLevel(level);
		setWidth(getLevel().getColumns() * Level.CELL_SIZE);
		setHeight(getLevel().getRows() * Level.CELL_SIZE);

		addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				System.err.println("Level listener");
				int cx = (int) (x / Level.CELL_SIZE);
				int cy = (int) (y / Level.CELL_SIZE);
				getLevel().setPassable(cx, cy, !getLevel().isPassable(cx, cy));
			}
		});
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for (int column = 0; column < getLevel().getColumns(); ++column) {
			for (int row = 0; row < getLevel().getRows(); ++row) {
				float x = getX() + column * Level.CELL_SIZE;
				float y = getY() + row * Level.CELL_SIZE;
				if (getLevel().isPassable(column, row)) {
					batch.draw(floorTexture, x, y, Level.CELL_SIZE, Level.CELL_SIZE);
				} else {
					batch.draw(wallTexture, x, y, Level.CELL_SIZE, Level.CELL_SIZE);
				}
			}
		}
		batch.end();

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.translate(getX(), getY(), 0);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(0.0f, 0.0f, 1.0f, parentAlpha);
		for (int column = 0; column <= getLevel().getColumns(); ++column) {
//			shapeRenderer.line(column * Level.CELL_SIZE, 0, column * Level.CELL_SIZE, getLevel().getRows() * Level.CELL_SIZE);
		}
		for (int row = 0; row <= getLevel().getRows(); ++row) {
//			shapeRenderer.line(0, row * Level.CELL_SIZE, getLevel().getColumns() * Level.CELL_SIZE, row * Level.CELL_SIZE);
		}
		shapeRenderer.end();

		GridPoint enter = getLevel().getEnterPoint(), exit = getLevel().getExitPoint();

		Gdx.gl.glLineWidth(3.0f);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		shapeRenderer.setColor(Color.MAGENTA);
		shapeRenderer.rect(enter.x * Level.CELL_SIZE, enter.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);

		shapeRenderer.setColor(Color.PINK);
		shapeRenderer.rect(exit.x * Level.CELL_SIZE, exit.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);

		LevelChecker levelChecker = new LevelChecker();
		if (levelChecker.checkExitWay(getLevel())) {
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(0, 0, getWidth(), getHeight());
		}

		shapeRenderer.end();
		Gdx.gl.glLineWidth(1.0f);

		batch.begin();
		super.draw(batch, parentAlpha);
	}
}
