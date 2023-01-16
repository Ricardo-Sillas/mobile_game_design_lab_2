package edu.utep.cs.cs4381.tappydefender.model;

import android.graphics.Rect;

public class Ship {

    private Rect hitbox;
    private int speed;
    private int x;
    private int y;
    private int z;
    private int a;

    public Ship() {
        hitbox = new Rect(x, y, x + z, y + a);
    }

    public Rect setHitbox(int x, int y, int z, int a) {
        hitbox.set(x, y, x + z, y + a);
        return hitbox;
    }

    public Rect getHitbox() {
        return hitbox;
    }
}
