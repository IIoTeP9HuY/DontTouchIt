package com.github.donttouchit.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.github.donttouchit.game.properties.Dye;
import com.github.donttouchit.geom.Direction;
import com.github.donttouchit.geom.GridPoint;

public abstract class Ball extends LevelObject {
	protected ShapeRenderer shapeRenderer = new ShapeRenderer();
	protected float R = 30;

	private float dx = 0, dy = 0;
	private float speedInCells = 3.0f;
	private Direction moveDirection = Direction.NONE;

	public static class Specification extends LevelObject.Specification {
	}

	@Override
	public Specification getSpecification() {
		Specification specification = new Specification();
		specification.dye = getDye();
		specification.column = getColumn();
		specification.row = getRow();
		return specification;
	}

	@Override
	public Integer getDepth() {
		return 2;
	}

	public Ball(Dye dye, int column, int row) {
		super(column, row, dye);

		addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				System.err.println("Tapped!");
			}

			@Override
			public void fling(InputEvent event, float velocityX, float velocityY, int button) {
				System.err.println("Flinged! " + velocityX + " " + velocityY);
				Direction direction;
				if (Math.abs(velocityX) > Math.abs(velocityY)) {
					direction = velocityX > 0 ? Direction.RIGHT : Direction.LEFT;
				} else {
					direction = velocityY > 0 ? Direction.TOP : Direction.BOTTOM;
				}
				move(direction);
			}
		});
	}

	public Ball(Specification specification) {
		this(specification.dye, specification.column, specification.row);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (moveDirection != Direction.NONE) {
			Vector2 dir = moveDirection.getVector2();
			dir.scl(speedInCells * delta);

			dx += dir.x;
			dy += dir.y;

			if (Math.abs(dx) >= 1 || Math.abs(dy) >= 1) {
				GridPoint previousCell = new GridPoint(getColumn(), getRow());

				if (Math.abs(dx) >= 1) {
					setColumn(getColumn() + (int)Math.signum(dx));
					dx -= Math.signum(dx);
				}
				if (Math.abs(dy) >= 1) {
					setRow(getRow() + (int)Math.signum(dy));
					dy -= Math.signum(dy);
				}

				getLevel().ballLeft(this, new GridPoint(previousCell));
				getLevel().ballEntered(this, new GridPoint(getColumn(), getRow()));

				if (!isEmpty(getMoveDirection())) {
					if (isWall(getMoveDirection())) {
						hitWall();
					}
					stop();
				}
			}
		}

	}

	protected boolean isInHole() {
		if (getLevel() == null) {
			return false;
		} else {
			return getDye().
					equals(getLevel()
							.getDye(getColumn(),
									getRow()));
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.translate(getX(), getY(), 0);

		Vector2 center = getCenter();

		// Border
		float innerR = R;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		if (isInHole()) {
			shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
			innerR = R * 4 / 5;
		} else {
			shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		}
		shapeRenderer.circle(center.x, center.y, R);
		shapeRenderer.end();

		// Inner
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(getDye().getColor());
		shapeRenderer.circle(center.x, center.y, innerR);
		shapeRenderer.end();

		batch.begin();
	}

	private boolean isWall(Direction direction) {
		if (direction == Direction.NONE) {
			return false;
		}
		GridPoint p = direction.getPoint();
		p.add(getColumn(), getRow());
		return !getLevel().isPassable(p.x, p.y);
	}

	private boolean isEmpty(Direction direction) {
		if (direction == Direction.NONE) {
			return true;
		}
		GridPoint p = direction.getPoint();
		p.add(getColumn(), getRow());
		return getLevel().isEmpty(p.x, p.y);
	}

	@Override
	public Vector2 getCenter() {
		return new Vector2((dx + 0.5f) * Level.CELL_SIZE, (dy + 0.5f) * Level.CELL_SIZE);
	}

	public float getSpeedInCells() {
		return speedInCells;
	}

	public void setSpeedInCells(float speedInCells) {
		this.speedInCells = speedInCells;
	}

	public Direction getMoveDirection() {
		return moveDirection;
	}

	public void stop() {
		moveDirection = Direction.NONE;
		dx = 0;
		dy = 0;
		getLevel().stopAction(this);
	}

	public void move(Direction moveDirection) {
		if (this.moveDirection == Direction.NONE && isEmpty(moveDirection) && getLevel().startAction(this)) {
			this.moveDirection = moveDirection;
		}
	}

	public void changeDirection(Direction direction) {
		if (getMoveDirection() == Direction.NONE) {
			throw new IllegalStateException("Ball is not moving now");
		}
		if (direction == Direction.NONE) {
			throw new IllegalArgumentException("Direction can not be NONE");
		}
		moveDirection = direction;
	}

	protected void hitWall() {
		Gdx.app.log("Ball at " + getColumn() + " " + getRow(), " hits the wall");
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public float getR() {
		return R;
	}

	public void setR(float r) {
		R = r;
	}
}
