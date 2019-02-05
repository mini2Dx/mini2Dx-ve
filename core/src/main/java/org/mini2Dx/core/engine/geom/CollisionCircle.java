/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.engine.geom;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.engine.PositionChangeListener;
import org.mini2Dx.core.engine.Positionable;
import org.mini2Dx.core.engine.SizeChangeListener;
import org.mini2Dx.core.engine.Sizeable;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Shape;
import org.mini2Dx.core.graphics.Graphics;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An implementation of {@link Circle} that allows for interpolation. Game
 * objects can use this class to move around the game world and retrieve the
 * appropriate rendering coordinates after interpolating between the previous
 * and current position.
 */
public class CollisionCircle extends Circle implements CollisionShape {

	private static final Vector2 TMP_SOURCE_VECTOR = new Vector2();
	private static final Vector2 TMP_TARGET_VECTOR = new Vector2();
	
	private final int id;
	private final ReadWriteLock positionChangeListenerLock;
	private final ReadWriteLock sizeChangeListenerLock;
	
	private Array<PositionChangeListener> positionChangeListeners;
	private Array<SizeChangeListener> sizeChangeListeners;
	
	private Circle previousCircle;
	private Circle renderCircle;
	private int renderX, renderY;
	private boolean interpolateRequired = false;
	
	public CollisionCircle(float radius) {
		this(CollisionIdSequence.nextId(), radius);
	}
	
	public CollisionCircle(int id, float radius) {
		this(id, 0f, 0f, radius);
	}
	
	public CollisionCircle(float centerX, float centerY, float radius) {
		this(CollisionIdSequence.nextId(), centerX, centerY, radius);
	}
	
	public CollisionCircle(int id, float centerX, float centerY, float radius) {
		super(centerX, centerY, radius);
		this.id = id;
		
		positionChangeListenerLock = new ReentrantReadWriteLock();
		sizeChangeListenerLock = new ReentrantReadWriteLock();
		previousCircle = new Circle(centerX, centerY, radius);
		renderCircle = new Circle(centerX, centerY, radius);
		storeRenderCoordinates();
	}
	
	private void storeRenderCoordinates() {
		renderX = MathUtils.round(renderCircle.getX());
		renderY = MathUtils.round(renderCircle.getY());
	}
	
	@Override
	public void preUpdate() {
		previousCircle.set(this);
	}
	
	@Override
	public void update(GameContainer gc, float delta) {
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {
		if(!interpolateRequired) {
			return;
		}
		renderCircle.set(previousCircle.lerp(this, alpha));
		storeRenderCoordinates();
		if(renderX != MathUtils.round(getX())) {
			return;
		}
		if(renderY != MathUtils.round(getY())) {
			return;
		}
		interpolateRequired = false;
	}
	
	@Override
	public void draw(Graphics g) {
		renderCircle.draw(g);
	}
	
	@Override
	public void fill(Graphics g) {
		renderCircle.fill(g);
	}
	
	@Override
	public float getDistanceTo(Positionable positionable) {
		return getDistanceTo(positionable.getX(), positionable.getY());
	}

	@Override
	public <T extends Positionable> void addPostionChangeListener(
			PositionChangeListener<T> listener) {
		positionChangeListenerLock.writeLock().lock();
		if (positionChangeListeners == null) {
			positionChangeListeners = new Array<PositionChangeListener>(true, 2);
		}
		positionChangeListeners.add(listener);
		positionChangeListenerLock.writeLock().unlock();
	}

	@Override
	public <T extends Positionable> void removePositionChangeListener(
			PositionChangeListener<T> listener) {
		positionChangeListenerLock.readLock().lock();
		if (positionChangeListeners == null) {
			positionChangeListenerLock.readLock().unlock();
			return;
		}
		positionChangeListenerLock.readLock().unlock();
		
		positionChangeListenerLock.writeLock().lock();
		positionChangeListeners.removeValue(listener, false);
		positionChangeListenerLock.writeLock().unlock();
	}
	
	private void notifyPositionChangeListeners() {
		positionChangeListenerLock.readLock().lock();
		if (positionChangeListeners == null) {
			positionChangeListenerLock.readLock().unlock();
			return;
		}
		for (int i = positionChangeListeners.size - 1; i >= 0; i--) {
			if(i >= positionChangeListeners.size) {
				i = positionChangeListeners.size - 1;
			}
			PositionChangeListener listener = positionChangeListeners.get(i);
			positionChangeListenerLock.readLock().unlock();
			listener.positionChanged(this);
			positionChangeListenerLock.readLock().lock();
		}
		positionChangeListenerLock.readLock().unlock();
	}
	
	@Override
	public <T extends Sizeable> void addSizeChangeListener(SizeChangeListener<T> listener) {
		sizeChangeListenerLock.writeLock().lock();
		if (sizeChangeListeners == null) {
			sizeChangeListeners = new Array<SizeChangeListener>(true, 2);
		}
		sizeChangeListeners.add(listener);
		sizeChangeListenerLock.writeLock().unlock();
	}

	@Override
	public <T extends Sizeable> void removeSizeChangeListener(SizeChangeListener<T> listener) {
		sizeChangeListenerLock.readLock().lock();
		if (sizeChangeListeners == null) {
			sizeChangeListenerLock.readLock().unlock();
			return;
		}
		sizeChangeListenerLock.readLock().unlock();
		
		sizeChangeListenerLock.writeLock().lock();
		sizeChangeListeners.removeValue(listener, false);
		sizeChangeListenerLock.writeLock().unlock();
	}
	
	private void notifySizeChangeListeners() {
		sizeChangeListenerLock.readLock().lock();
		if (sizeChangeListeners == null) {
			sizeChangeListenerLock.readLock().unlock();
			return;
		}
		for (int i = sizeChangeListeners.size - 1; i >= 0; i--) {
			if(i >= sizeChangeListeners.size) {
				i = sizeChangeListeners.size - 1;
			}
			SizeChangeListener listener = sizeChangeListeners.get(i);
			sizeChangeListenerLock.readLock().unlock();
			listener.sizeChanged(this);
			sizeChangeListenerLock.readLock().lock();
		}
		sizeChangeListenerLock.readLock().unlock();
	}
	
	@Override
	public void forceTo(float x, float y) {
		boolean notifyPositionListeners = x != getX() || y != getY();
		
		super.set(x, y);
		previousCircle.set(x, y);
		renderCircle.set(x, y);
		storeRenderCoordinates();
		
		if(!notifyPositionListeners) {
			return;
		}
		notifyPositionChangeListeners();
	}
	
	@Override
	public void moveTowards(float x, float y, float speed) {
		TMP_SOURCE_VECTOR.set(getX(), getY());
		TMP_TARGET_VECTOR.set(x, y);
		Vector2 direction = TMP_TARGET_VECTOR.sub(TMP_SOURCE_VECTOR).nor();
		
		float xComponent = speed * MathUtils.cosDeg(direction.angle());
		float yComponent = speed * MathUtils.sinDeg(direction.angle());
		TMP_SOURCE_VECTOR.add(xComponent, yComponent);
		
		set(TMP_SOURCE_VECTOR.x, TMP_SOURCE_VECTOR.y);
	}

	@Override
	public void moveTowards(Positionable positionable, float speed) {
		moveTowards(positionable.getX(), positionable.getY(), speed);
	}
	
	@Override
	public void add(float x, float y) {
		super.add(x, y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}
	
	@Override
	public void subtract(float x, float y) {
		super.subtract(x, y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}

	public void setX(float x) {
		if(x == getX()) {
			return;
		}
		super.setX(x);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}
	
	public void setY(float y) {
		if(y == getY()) {
			return;
		}
		super.setY(y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}
	
	public void set(float x, float y) {
		if(x == getX() && y == getY()) {
			return;
		}
		super.set(x, y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}
	
	@Override
	public void setRadius(float radius) {
		if(radius == getRadius()) {
			return;
		}
		super.setRadius(radius);
		interpolateRequired = true;
		notifySizeChangeListeners();
	}
	
	@Override
	public void setCenter(float x, float y) {
		if(x == getCenterX() && y == getCenterY()) {
			return;
		}
		super.setCenter(x, y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}

	@Override
	public void setCenterX(float x) {
		if(x == getCenterX()) {
			return;
		}
		super.setCenterX(x);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}

	@Override
	public void setCenterY(float y) {
		if(y == getCenterY()) {
			return;
		}
		super.setCenterY(y);
		interpolateRequired = true;
		notifyPositionChangeListeners();
	}
	
	@Override
	public void scale(float scale) {
		super.scale(scale);
		interpolateRequired = true;
		notifySizeChangeListeners();
	}
	
	public int getRenderX() {
		return renderX;
	}
	
	public int getRenderY() {
		return renderY;
	}
	
	public float getRenderRadius() {
		return renderCircle.getRadius();
	}
	
	public float getPreviousX() {
		return previousCircle.getX();
	}
	
	public float getPreviousY() {
		return previousCircle.getY();
	}

	public float getPreviousRadius() {
		return previousCircle.getRadius();
	}

	public int getId() {
		return id;
	}

	public boolean isInterpolateRequired() {
		return interpolateRequired;
	}

	@Override
	public float getWidth() {
		return getRadius() * 2f;
	}

	@Override
	public float getHeight() {
		return getRadius() * 2f;
	}
	
	@Override
	public Shape getShape() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CollisionCircle that = (CollisionCircle) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id);
	}
}
