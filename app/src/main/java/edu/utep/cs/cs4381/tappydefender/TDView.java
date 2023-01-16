package edu.utep.cs.cs4381.tappydefender;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.tappydefender.model.EnemyShip;
import edu.utep.cs.cs4381.tappydefender.model.PlayerShip;
import edu.utep.cs.cs4381.tappydefender.model.Shield;
import edu.utep.cs.cs4381.tappydefender.model.SpaceDust;

public class TDView extends SurfaceView
        implements Runnable {

    private static float distanceRemaining;
    private static float totDistance;
    private Pause pause;

    private List<EnemyShip> enemyShips = new CopyOnWriteArrayList<>();

    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private PlayerShip player;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    private Thread gameThread;
    private boolean playing;

    private int width;
    private int height;

    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    public Context context;

    private boolean gameEnded;
    private boolean pauseGame;

    private SoundEffect soundEffect;


    private Shield shield;

    public TDView(Context context, int width, int height) {
        super(context);
        this.context = context;

        soundEffect = new SoundEffect(context);

        fastestTime = HighScoreRecorder.instance(context).retrieve();

        this.width = width;
        this.height = height;
        totDistance = 10000;

        startGame();

        pause = new Pause(context);
        player = new PlayerShip(context, width, height);
        holder = getHolder();
        paint = new Paint();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {
        boolean hitDetected = false;
        for (EnemyShip enemy: enemyShips) {
            if (Rect.intersects(player.getHitbox(), enemy.getHitbox())) {
                hitDetected = true;
                enemy.setX(-enemy.getBitmap().getWidth());
            }
        }
        if (Rect.intersects(player.getHitbox(), shield.getHitbox())) {
            player.addShieldStrength();
            shield.setX(-shield.getBitmap().getWidth());
        }
        if (hitDetected && !gameEnded) {
            soundEffect.play(SoundEffect.Sound.BUMP);
            if (player.reduceShieldStrength() <= 0) {
                soundEffect.play(SoundEffect.Sound.DESTROYED);
                gameEnded = true;
            }
        }
        if(!pauseGame) {
            player.update();
            shield.update(player.getSpeed());
            for (EnemyShip enemy : enemyShips) {
                enemy.update(player.getSpeed());
            }
            for (SpaceDust sd : dustList) {
                sd.update(player.getSpeed());
            }
            if (!gameEnded) {
                distanceRemaining -= player.getSpeed();
                timeTaken = System.currentTimeMillis() - timeStarted;
            }
        }
        if (distanceRemaining < 0) {
            soundEffect.play(SoundEffect.Sound.WIN);
            if (timeTaken < fastestTime) {
                HighScoreRecorder.instance(null).store(timeTaken);
                fastestTime = timeTaken;
            }
            distanceRemaining = 0;
            gameEnded = true;
        }
    }

    private void draw() {
        // White specs of dust
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setStrokeWidth(4);
        paint.setTextSize(48);
        int yy = 50;
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Draw the dust from our arrayList
            for(SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),  player.getY(), paint);
            if(!gameEnded && !pauseGame) {
                canvas.drawBitmap(shield.getBitmap(), shield.getX(), shield.getY(), paint);
                for (EnemyShip enemy: enemyShips) {
                    canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
                }
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(HighScoreRecorder.formatTime("Fastest", fastestTime), 10, yy, paint);
                canvas.drawText("Shield: " + player.getShieldStrength(), 10, height - yy, paint);

                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(HighScoreRecorder.formatTime("Time", timeTaken), width / 2, yy, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", width / 2, height - yy, paint);

                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", width - 10, height - yy, paint);
                canvas.drawBitmap(pause.getBitmap(), width-80, 10, paint);
            }
            else if(pauseGame) {
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Tap to continue!", width/2, height/2, paint);
            }
            else {
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", width/2, 120, paint);
                paint.setTextSize(50);
                canvas.drawText(HighScoreRecorder.formatTime("Fastest", fastestTime), width/2, 180, paint);
                canvas.drawText(HighScoreRecorder.formatTime("Time", timeTaken), width / 2, 250, paint);
                canvas.drawText("Distance remaining: " + distanceRemaining/1000 + " KM", width/2, 310, paint);
                paint.setTextSize(100);
                canvas.drawText("Tap to replay!", width/2, 420, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }


    private void control() {
        try {
            gameThread.sleep(17); // in milliseconds
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                player.setBoosting(true);
                if(pauseGame) {
                    pauseGame = false;
                }
                if (Pause.buttons(motionEvent.getX(), motionEvent.getY(), width) == 1) {
                    pauseGame = true;
                }
                if (gameEnded) {
                    startGame();
                }
                break;
            case MotionEvent.ACTION_UP:
                player.setBoosting(false);
                break;
        }
        return true;
    }

    public static float getDistanceTraveled() {
        return totDistance - distanceRemaining;
    }

    private void startGame() {
        player = new PlayerShip(context, width, height);
        enemyShips.clear();
        for(int i = 0; i < 3; i++) {
            enemyShips.add(new EnemyShip(context, width, height));
        }
        dustList.clear();
        for (int i = 0; i < 40; i++) {
            dustList.add(new SpaceDust(width, height));
        }
        distanceRemaining = totDistance; // 10 km
        shield = new Shield(context, width, height);
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
        pauseGame = false;
    }
}

