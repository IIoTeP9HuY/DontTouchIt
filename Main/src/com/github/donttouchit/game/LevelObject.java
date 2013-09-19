package com.github.donttouchit.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.awt.*;

public class LevelObject extends Actor {
	private Level level;
	private int column = 0, row = 0;

	public LevelObject(Level level, int column, int row) {
		this.level = level;
		setWidth(Level.CELL_SIZE);
		setHeight(Level.CELL_SIZE);
		setColumn(column);
		setRow(row);
	}

	public void setBoardPosition(int column, int row) {
		setColumn(column);
		setRow(row);
	}

	public Point getBoardPosition() {
		return new Point(column, row);
	}

	public boolean isPassable(int column, int row) {
		return this.column != column || this.row != row;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
		setX(this.column * Level.CELL_SIZE);
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
		setY(this.row * Level.CELL_SIZE);
	}

	public Vector2 getCenter() {
		return new Vector2(0.5f * Level.CELL_SIZE, 0.5f * Level.CELL_SIZE);
	}
}
