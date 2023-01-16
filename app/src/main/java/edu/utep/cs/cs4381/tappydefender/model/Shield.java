package edu.utep.cs.cs4381.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

import edu.utep.cs.cs4381.tappydefender.R;
import edu.utep.cs.cs4381.tappydefender.TDView;

public class Shield {

    private int x, y;
    private int minX, minY;
    private int maxX, maxY;
    int speed;
    private Bitmap bitmap;
    private Rect hitbox;
    private static final Random random = new Random();
    private Context context;

    public Shield(Context context, int width, int height) {
        this.context = context;
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.shield);
        maxX = width;
        maxY = height;
        minX = 0;
        minY = 0;
        speed = random.nextInt(6) + 10;
        x = -100;
        y = random.nextInt(maxY) - bitmap.getHeight();
        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

// Updates shield speed, and when it occurs
    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;
        if (TDView.getDistanceTraveled() % 3000 < 100 && TDView.getDistanceTraveled() > 100 && TDView.getDistanceTraveled() < 9000) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shield);
            speed = random.nextInt(10)+10;
            x = maxX;
            y = random.nextInt(maxY) - bitmap.getHeight();
        }
        hitbox.set(x, y,bitmap.getWidth() + x,bitmap.getHeight() + y);
    }

// Setters and getters

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitbox() {
        return hitbox;
    }

}
