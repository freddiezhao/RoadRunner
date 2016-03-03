package com.github.glomadrian.loadingpath.painter.indeterminate;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.github.glomadrian.loadingpath.painter.IndeterminatePathPainter;
import com.github.glomadrian.loadingpath.painter.PointPathPainter;
import com.github.glomadrian.loadingpath.painter.configuration.Direction;
import com.github.glomadrian.loadingpath.painter.configuration.TwoWayConfiguration;
import com.github.glomadrian.loadingpath.path.PathContainer;

/**
 * @author Adrián García Lomas
 */
public class TwoWayPainter extends PointPathPainter implements IndeterminatePathPainter {

  private ValueAnimator movementAnimator;
  private ValueAnimator leftLineAnimator;
  private ValueAnimator rightLineAnimator;
  private int offsetRight = 0;
  private int offsetLeft = 0;
  private Paint paint;
  private int movementLinePoints = 50;

  //Movement line configuration
  private String movementDirection = Direction.LEFT;
  private int color = Color.RED;
  private float strokeWidth = 10;
  private float zone = 0f;
  private int movementLoopTime = 5000;
  private float movementLineSize = 0.005f;

  //Right line configuration
  private int rightLineLoopTime = 5000;
  private int rightLineStartDelayTime = 2000;
  private float rightLineMaxSize = 0.5f;
  //Left line configuration
  private int leftLineLoopTime = 2000;
  private int leftLineStartDelayTime = 5000;
  private float leftLineMaxSize = 0.5f;

  public TwoWayPainter(View view, PathContainer pathContainer,
      TwoWayConfiguration twoWayConfiguration) {
    super(pathContainer, view);

    movementDirection = twoWayConfiguration.getMovementDirection();
    color = twoWayConfiguration.getColor();
    strokeWidth = twoWayConfiguration.getStrokeWidth();
    movementLoopTime = twoWayConfiguration.getMovementLoopTime();
    rightLineLoopTime = twoWayConfiguration.getRightLineLoopTime();
    movementLineSize = twoWayConfiguration.getMovementLineSize();
    rightLineStartDelayTime = twoWayConfiguration.getRightLineStartDelayTime();
    rightLineMaxSize = twoWayConfiguration.getRightLineMaxSize();
    leftLineLoopTime = twoWayConfiguration.getLeftLineLoopTime();
    leftLineStartDelayTime = twoWayConfiguration.getLeftLineStartDelayTime();
    leftLineMaxSize = twoWayConfiguration.getLeftLineMaxSize();
    init();
  }

  private void init() {
    initPaint();
    initLineMovement();
    initMovementAnimator();
    initRightLineAnimator();
    initLeftLineAnimator();
  }

  private void initLineMovement() {
    movementLinePoints = getNumberOfLinePointsInRange(movementLineSize);
  }

  private void initMovementAnimator() {
    if (movementDirection.equals(Direction.RIGHT)) {
      movementAnimator = ValueAnimator.ofFloat(0, 1);
    } else {
      movementAnimator = ValueAnimator.ofFloat(1, 0);
    }
    movementAnimator.setDuration(movementLoopTime);
    movementAnimator.setInterpolator(new LinearInterpolator());
    movementAnimator.setRepeatCount(ValueAnimator.INFINITE);
    movementAnimator.addUpdateListener(new MovementLineAnimatorUpdateListener());
  }

  private void initRightLineAnimator() {
    int points = getPositionForZone(rightLineMaxSize);
    rightLineAnimator = ValueAnimator.ofInt(0, points);
    rightLineAnimator.setDuration(rightLineLoopTime);
    rightLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
    rightLineAnimator.setRepeatMode(ValueAnimator.REVERSE);
    rightLineAnimator.setStartDelay(rightLineStartDelayTime);
    rightLineAnimator.addUpdateListener(new RightLineAnimatorUpdateListener());
  }

  private void initLeftLineAnimator() {
    int points = getPositionForZone(leftLineMaxSize);
    leftLineAnimator = ValueAnimator.ofInt(0, points);
    leftLineAnimator.setDuration(leftLineLoopTime);
    leftLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
    leftLineAnimator.setRepeatMode(ValueAnimator.REVERSE);
    leftLineAnimator.setStartDelay(leftLineStartDelayTime);
    leftLineAnimator.addUpdateListener(new LeftLineAnimatorUpdateListener());
  }

  public void initPaint() {
    paint = new Paint();
    paint.setColor(color);
    paint.setStrokeWidth(strokeWidth);
  }

  @Override
  public void paintPath(Canvas canvas) {
    drawWithOffset(zone, offsetRight, offsetLeft, movementLinePoints, canvas, paint);
  }

  @Override
  public void start() {
    movementAnimator.start();
    leftLineAnimator.start();
    rightLineAnimator.start();
  }

  @Override
  public void stop() {
    movementAnimator.cancel();
    leftLineAnimator.cancel();
    rightLineAnimator.cancel();
  }

  @Override
  public void restart() {
    stop();
    start();
  }

  private class MovementLineAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      zone = (float) animation.getAnimatedValue();
      view.invalidate();
    }
  }

  private class RightLineAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      offsetRight = (int) animation.getAnimatedValue();
    }
  }

  private class LeftLineAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      offsetLeft = (int) animation.getAnimatedValue();
    }
  }
}
