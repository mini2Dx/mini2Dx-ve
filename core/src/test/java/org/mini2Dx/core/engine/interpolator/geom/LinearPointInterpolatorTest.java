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
package org.mini2Dx.core.engine.interpolator.geom;

import junit.framework.Assert;
import org.junit.Test;
import org.mini2Dx.core.engine.interpolator.value.InterpolatedValue;
import org.mini2Dx.core.engine.interpolator.Interpolators;
import org.mini2Dx.core.geom.Point;

/**
 * Tests the {@link LinearPointInterpolator}
 */
public class LinearPointInterpolatorTest {
  private final Point initialValue = new Point(22f, 12f);
  private final Point currentValue = new Point(10f, 45f);
  private final InterpolatedValue<Point> interpolatedValue = new InterpolatedValue<>(Interpolators.linearPoint(), new Point(), new Point());

  @Test
  public void testInterpolateValues() {
    interpolatedValue.getInitialValue().set(initialValue);
    interpolatedValue.getCurrentValue().set(currentValue);

    Point flyweight = new Point();
    for (float alpha = 0.0f; alpha <= 1.0f; alpha += 0.05f) {
      float x = initialValue.getX() + alpha * (currentValue.getX() - initialValue.getX());
      float y = initialValue.getY() + alpha * (currentValue.getY() - initialValue.getY());

      interpolatedValue.getInterpolatedValue(flyweight, alpha);
      Assert.assertEquals("x at "+ alpha, x, flyweight.getX(), 0.05f);
      Assert.assertEquals("y at "+ alpha, y, flyweight.getY(), 0.05f);
    }
  }
}
