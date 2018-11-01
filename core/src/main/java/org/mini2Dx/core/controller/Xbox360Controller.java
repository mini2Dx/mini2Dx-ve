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
package org.mini2Dx.core.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.Array;
import org.mini2Dx.core.controller.button.Xbox360Button;
import org.mini2Dx.core.controller.deadzone.DeadZone;
import org.mini2Dx.core.controller.deadzone.NoopDeadZone;
import org.mini2Dx.core.controller.deadzone.RadialDeadZone;
import org.mini2Dx.core.controller.xbox360.Xbox360ControllerListener;

/**
 * Base class for Xbox 360 controller mapping implementations
 */
public abstract class Xbox360Controller implements MdxController<Xbox360ControllerListener> {
	public static final String ID = "360";
	
	private final Controller controller;
    private final Array<Xbox360ControllerListener> listeners = new Array<>(true,2);
    
    private DeadZone leftStickDeadZone, rightStickDeadZone;
    private DeadZone leftTriggerDeadZone, rightTriggerDeadZone;
    private boolean leftTrigger, rightTrigger;
    
	public Xbox360Controller(Controller controller) {
		this(controller, new NoopDeadZone(), new NoopDeadZone());
	}
	
	public Xbox360Controller(Controller controller, DeadZone leftStickDeadZone, DeadZone rightStickDeadZone) {
		this.controller = controller;
		this.leftStickDeadZone = leftStickDeadZone;
		this.rightStickDeadZone = rightStickDeadZone;
		this.leftTriggerDeadZone = new RadialDeadZone();
		this.rightTriggerDeadZone = new RadialDeadZone();
		controller.addListener(this);
	}

	protected boolean notifyDisconnected() {
		for(Xbox360ControllerListener listener : listeners) {
			listener.disconnected(this);
		}
		return false;
	}
	
	protected boolean notifyButtonDown(Xbox360Button button) {
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.buttonDown(this, button)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyButtonUp(Xbox360Button button) {
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.buttonUp(this, button)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyLeftTriggerMoved(float value) {
		leftTriggerDeadZone.updateY(value);
		if(leftTriggerDeadZone.getY() >= 0.5f && !leftTrigger) {
			notifyButtonDown(Xbox360Button.LEFT_TRIGGER);
			leftTrigger = true;
		} else if(leftTriggerDeadZone.getY() < 0.5f && leftTrigger) {
			notifyButtonUp(Xbox360Button.LEFT_TRIGGER);
			leftTrigger = false;
		}
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.leftTriggerMoved(this, value)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyRightTriggerMoved(float value) {
		rightTriggerDeadZone.updateY(value);
		if(rightTriggerDeadZone.getY() >= 0.5f && !rightTrigger) {
			notifyButtonDown(Xbox360Button.RIGHT_TRIGGER);
			rightTrigger = true;
		} else if(rightTriggerDeadZone.getY() < 0.5f && rightTrigger) {
			notifyButtonUp(Xbox360Button.RIGHT_TRIGGER);
			rightTrigger = false;
		}
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.rightTriggerMoved(this, value)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyLeftStickXMoved(float value) {
		leftStickDeadZone.updateX(value);
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.leftStickXMoved(this, leftStickDeadZone.getX())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyLeftStickYMoved(float value) {
		leftStickDeadZone.updateY(value);
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.leftStickYMoved(this, leftStickDeadZone.getY())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyRightStickXMoved(float value) {
		rightStickDeadZone.updateX(value);
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.rightStickXMoved(this, rightStickDeadZone.getX())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notifyRightStickYMoved(float value) {
		rightStickDeadZone.updateY(value);
		for(Xbox360ControllerListener listener : listeners) {
			if(listener.rightStickYMoved(this, rightStickDeadZone.getY())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
    public void addListener(int index, Xbox360ControllerListener listener) {
    	listeners.insert(index, listener);
    }
	
	@Override
    public void addListener(Xbox360ControllerListener listener) {
    	listeners.add(listener);
    }
	
	@Override
    public void removeListener(int index) {
    	listeners.removeIndex(index);
    }
    
    @Override
    public void removeListener(Xbox360ControllerListener listener) {
    	listeners.removeValue(listener, false);
    }

	@Override
	public void clearListeners() {
		listeners.clear();
	}
	
    @Override
    public Xbox360ControllerListener getListener(int index) {
    	return listeners.get(index);
    }
	
    @Override
	public int getTotalListeners() {
		return listeners.size;
	}
    
    public ControllerType getControllerType() {
    	return ControllerType.XBOX_360;
    }

	public DeadZone getLeftStickDeadZone() {
		return leftStickDeadZone;
	}

	public void setLeftStickDeadZone(DeadZone leftStickDeadZone) {
		this.leftStickDeadZone = leftStickDeadZone;
	}

	public DeadZone getRightStickDeadZone() {
		return rightStickDeadZone;
	}

	public void setRightStickDeadZone(DeadZone rightStickDeadZone) {
		this.rightStickDeadZone = rightStickDeadZone;
	}

	public DeadZone getLeftTriggerDeadZone() {
		return leftTriggerDeadZone;
	}

	public void setLeftTriggerDeadZone(DeadZone leftTriggerDeadZone) {
		if(leftTriggerDeadZone == null) {
			leftTriggerDeadZone = new NoopDeadZone();
		}
		this.leftTriggerDeadZone = leftTriggerDeadZone;
	}

	public DeadZone getRightTriggerDeadZone() {
		return rightTriggerDeadZone;
	}

	public void setRightTriggerDeadZone(DeadZone rightTriggerDeadZone) {
		if(rightTriggerDeadZone == null) {
			rightTriggerDeadZone = new NoopDeadZone();
		}
		this.rightTriggerDeadZone = rightTriggerDeadZone;
	}
}
