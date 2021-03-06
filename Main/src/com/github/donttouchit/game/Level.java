package com.github.donttouchit.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.donttouchit.game.properties.Dye;
import com.github.donttouchit.geom.GridPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Level implements ActionListener {
	private final List<LevelObject> levelObjects = new ArrayList<LevelObject>();
	private final Group group = new Group();
	private final boolean[][] passable;
	private final int columns, rows;
	private boolean inAction = false;
	private final GridPoint enterPoint;
	private final GridPoint exitPoint;
	public static final float CELL_SIZE = 64;

	public static class Specification {
		private ArrayList<LevelObject.Specification> levelObjectsSpecifications = new ArrayList<LevelObject.Specification>();
		private boolean[][] passable;
		private int columns, rows;
		private GridPoint enterPoint;
		private GridPoint exitPoint;
	}

	public Specification getSpecification() {
		Specification specification = new Specification();

		for (LevelObject levelObject : levelObjects) {
			specification.levelObjectsSpecifications.add(levelObject.getSpecification());
		}

		specification.columns = columns;
		specification.rows = rows;
		specification.enterPoint = enterPoint;
		specification.exitPoint = exitPoint;
		specification.passable = new boolean[columns][rows];
		for (int index = 0; index < columns; ++index) {
			System.arraycopy(passable[index], 0, specification.passable[index], 0, rows);
		}

		return specification;
	}

	public Level(int columns, int rows, GridPoint enterPoint, GridPoint exitPoint) {
		this.columns = columns;
		this.rows = rows;
		this.enterPoint = enterPoint;
		this.exitPoint = exitPoint;
		group.setWidth(getColumns() * CELL_SIZE);
		group.setHeight(getRows() * CELL_SIZE);
//		group.setOrigin(group.getWidth() / 2, group.getHeight() / 2);

		if (enterPoint.equals(exitPoint)) {
			throw new IllegalArgumentException("The enter point can not be equal to the exit one");
		}

		float w = Gdx.graphics.getWidth() - 2 * CELL_SIZE;
		float h = Gdx.graphics.getHeight() - CELL_SIZE;
		setBounds(CELL_SIZE, CELL_SIZE / 2, w, h);

		passable = new boolean[columns][rows];
		for (boolean[] array : passable) {
			Arrays.fill(array, true);
		}

		setPassable(enterPoint.x, enterPoint.y, false);
		setPassable(exitPoint.x, exitPoint.y, false);
		Board board = new Board(this);
		group.addActor(board);
	}

	public Level(Specification specification) {
		this(specification.columns, specification.rows, specification.enterPoint, specification.exitPoint);
		for (int index = 0; index < columns; ++index) {
			System.arraycopy(specification.passable[index], 0, passable[index], 0, rows);
		}

		List<LevelObject> levelObjectList = new ArrayList<LevelObject>();
		for (LevelObject.Specification objectSpecification : specification.levelObjectsSpecifications) {
			levelObjectList.add(objectSpecification.createLevelObject());
		}

		Collections.sort(levelObjectList);

		for (LevelObject levelObject : levelObjectList) {
			addLevelObject(levelObject);
		}
	}

	public void addLevelObject(LevelObject levelObject) {
		if (levelObject == null) {
			return;
		}
		group.addActor(levelObject);
		levelObjects.add(levelObject);
		levelObject.setLevel(this);
	}

	public void removeLevelObject(LevelObject levelObject) {
		if (levelObject == null) {
			return;
		}
		group.removeActor(levelObject);
		levelObjects.remove(levelObject);
	}

	public void removeLevelObject(int column, int row) {
		removeLevelObject(getLevelObject(column, row));
	}

	@Override
	public void ballEntered(Ball ball, GridPoint cell) {
		for (LevelObject levelObject : levelObjects) {
			if (levelObject instanceof ActionListener) {
				((ActionListener)levelObject).ballEntered(ball, cell);
			}
		}
	}

	@Override
	public void ballLeft(Ball ball, GridPoint cell) {
		for (LevelObject levelObject : levelObjects) {
			if (levelObject instanceof ActionListener) {
				((ActionListener)levelObject).ballLeft(ball, cell);
			}
		}
	}

	public boolean startAction(LevelObject levelObject) {
		if (inAction) return false;
		beforeAction(levelObject);
		inAction = true;
		return true;
	}

	public void stopAction(LevelObject levelObject) {
		if (inAction) {
			inAction = false;
			afterAction(levelObject);
		}
	}

	public void change(Dye dye, Object object) {
		for (LevelObject levelObject : levelObjects) {
			if (levelObject instanceof ChangeListener) {
				ChangeListener changeListener = (ChangeListener)levelObject;
				if (changeListener.accept(dye, object)) {
					changeListener.changed(object);
				}
			}
		}
	}

	private void beforeAction(LevelObject levelObject) {
	}

	private void afterAction(LevelObject levelObject) {
	}

	public boolean isOnBoard(int column, int row) {
		if (column < 0 || row < 0) {
			return false;
		}
		if (column >= columns || row >= rows) {
			return false;
		}
		return true;
	}

	public boolean isPassable(int column, int row) {
		if (!isOnBoard(column, row)) {
			return false;
		}
		return passable[column][row];
	}

	public void setPassable(int column, int row, boolean passable) {
		if (!isOnBoard(column, row)) {
			throw new IllegalArgumentException("This cell is out of board, so it's always impassable");
		}
		this.passable[column][row] = passable;
	}

	public boolean isEmpty(int column, int row) {
		if (!isPassable(column, row)) {
			return false;
		}

		for (LevelObject levelObject : levelObjects) {
			if (!levelObject.isPassable(column, row)) {
				return false;
			}
		}
		return true;
	}

	public Dye getDye(int column, int row) {
		for (LevelObject levelObject : levelObjects) {
			if (levelObject.getBoardPosition().equals(new GridPoint(column, row))) {
				if (levelObject instanceof Pedestal) {
					return ((Pedestal) levelObject).getDye();
				}
			}
		}
		return null;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public Group getGroup() {
		return group;
	}

	public GridPoint getEnterPoint() {
		return enterPoint;
	}

	public GridPoint getExitPoint() {
		return exitPoint;
	}

	public void setBounds(float x, float y, float w, float h) {
		if (true) { // if (Gdx.graphics.getWidth() < group.getWidth() || Gdx.graphics.getHeight() < group.getHeight()) {
			float xAspect = w / getGroup().getWidth();
			float yAspect = h / getGroup().getHeight();
			float aspect = Math.min(xAspect, yAspect);

			float dx = (w - (getGroup().getWidth() * aspect)) / 2;
			float dy = (h - (getGroup().getHeight() * aspect)) / 2;
			getGroup().setPosition(x + dx, y + dy);
			getGroup().setScale(aspect);
		}
	}

	public LevelObject getLevelObject(int column, int row) {
		GridPoint p = new GridPoint(column, row);
		for (LevelObject levelObject : levelObjects) {
			if (levelObject.getBoardPosition().equals(p)) {
				return levelObject;
			}
		}
		return null;
	}
}
