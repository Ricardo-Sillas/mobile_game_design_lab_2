package edu.utep.cs.cs4381.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import edu.utep.cs.cs4381.tappydefender.R;

public class PlayerShip extends Ship{

    private int x;
    private int y;
    private int speed;
    private final Bitmap bitmap;

    private static final int GRAVITY = -12;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 20;

    private Rect hitbox;

    private boolean boosting;

    private int maxY;
    private int minY;

    private int shieldStrength;

    public PlayerShip(Context context, int width, int height) {
        shieldStrength = 3;
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ship);
        maxY = height - bitmap.getHeight();
        minY = 0;
        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

// Updates player ship
    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        y -= speed + GRAVITY;
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
        hitbox.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

// Changes shield strength accordingly

    public int reduceShieldStrength() {
        return --shieldStrength;
    }

    public int addShieldStrength() { return ++shieldStrength; }

// Setters and getters

    public int getShieldStrength() {
        return shieldStrength;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setBoosting(boolean flag) {
        boosting = flag;
    }

    public int getSpeed() {
        return speed;
    }

    public Rect getHitbox() { return hitbox;}
}
