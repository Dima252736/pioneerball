package com.example.pioneerball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Rect;
import java.util.HashMap;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private long startTime;
    private long timeLimit = 60000; // 60 секунд в миллисекундах
    private long remainingTime;
    private Player player1, player2;
    private Ball ball;
    private Paint paint;
    private HashMap<String, Rect> buttons;
    private boolean moveUp1, moveDown1, moveLeft1, moveRight1;
    private boolean moveUp2, moveDown2, moveLeft2, moveRight2;
    private int score1 = 0, score2 = 0;
    private boolean gameOver = false;
    private String winnerText = "";
    private Rect resetButton;
    static int height;
    static int width;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);

        paint = new Paint();
        resetGame();

        buttons = new HashMap<>();


        setFocusable(true);
    }

    private void setupButtons() {
        buttons.put("W", new Rect(100, height-248, 200, height-148));
        buttons.put("A", new Rect(0, height-148, 100, height-48));
        buttons.put("S", new Rect(100, height-148, 200, height-48));
        buttons.put("D", new Rect(200, height-148, 300, height-48));

        buttons.put("UP", new Rect(width - 200, height-248, width - 100, height-148));
        buttons.put("LEFT", new Rect(width - 300, height-148, width - 200, height-48));
        buttons.put("DOWN", new Rect(width - 200, height-148, width - 100, height-48));
        buttons.put("RIGHT", new Rect(width - 100, height-148, width, height-48));

        resetButton = new Rect(width/2-100, height-248, width/2+100, height-148);

    }



    private void resetGame() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ball1);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.player111);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.player222);
        player1 = new Player(500, 600, Color.BLUE, image1);
        player2 = new Player(1720, 600, Color.RED, image2);
        ball = new Ball(1110, 500, image);
        gameOver = false;
        winnerText = "";
        startTime = System.currentTimeMillis();
        remainingTime = timeLimit;
        



    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        height = getHeight();
        width = getWidth();
        setupButtons();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            for (String key : buttons.keySet()) {
                Rect rect = buttons.get(key);
                if (rect.contains((int) x, (int) y)) {
                    switch (key) {
                        case "W": moveUp1 = true; break;
                        case "A": moveLeft1 = true; break;
                        case "S": moveDown1 = true; break;
                        case "D": moveRight1 = true; break;
                        case "UP": moveUp2 = true; break;
                        case "LEFT": moveLeft2 = true; break;
                        case "DOWN": moveDown2 = true; break;
                        case "RIGHT": moveRight2 = true; break;
                    }
                }
            }
            if (resetButton.contains((int) x, (int) y)) {
                score1 = 0;
                score2 = 0;
                resetGame();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            moveUp1 = moveDown1 = moveLeft1 = moveRight1 = false;
            moveUp2 = moveDown2 = moveLeft2 = moveRight2 = false;
        }

        return true;
    }

    public void update() {
        remainingTime = timeLimit - (System.currentTimeMillis() - startTime);
        if (remainingTime <= 0) {
            gameOver = true;
            if (score1 > score2) {
                winnerText = "Player 1 wins!";
            } else if (score2 > score1) {
                winnerText = "Player 2 wins!";
            } else {
                winnerText = "Draw!";
            }
            return;
        }

        if (gameOver) return;

        player1.move(moveLeft1, moveRight1, moveUp1, moveDown1);
        player2.move(moveLeft2, moveRight2, moveUp2, moveDown2);
        ball.update();

        if (ball.x - ball.radius <= 0) {
            score2++;
            checkWin();
            return;
        } else if (ball.x + ball.radius >= getWidth()) {
            score1++;
            checkWin();
            return;
        }


        ball.checkCollision(player1);
        ball.checkCollision(player2);
    }

    private void checkWin() {
        if (score1 >= 5) {
            gameOver = true;
            winnerText = "Player 1 wins!";
        } else if (score2 >= 5) {
            gameOver = true;
            winnerText = "Player 2 wins!";
        } else {
            resetGame();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(1);
            canvas.drawRect(0, 0, width, height, paint);
            player1.draw(canvas, paint);
            player2.draw(canvas, paint);
            ball.draw(canvas, paint);

            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(10);
            canvas.drawLine(width / 2, 0, width / 2, height - 200, paint);
            canvas.drawLine(0, height-200, width, height-200, paint);

            paint.setColor(Color.LTGRAY);
            for (String key : buttons.keySet()) {
                Rect rect = buttons.get(key);
                canvas.drawRect(rect, paint);
                paint.setColor(Color.BLACK);
                paint.setTextSize(40);
                canvas.drawText(key, rect.left + 20, rect.bottom - 20, paint);
                paint.setColor(Color.LTGRAY);
            }

            paint.setColor(Color.YELLOW);
            canvas.drawRect(resetButton, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText("Reset", resetButton.left + 30, resetButton.bottom - 20, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("Player 1: " + score1, 50, 100, paint);
            canvas.drawText("Player 2: " + score2, width - 300, 100, paint);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            long seconds = remainingTime / 1000;
            canvas.drawText("Time: " + seconds, width / 2 - 100, 100, paint);


            if (gameOver) {
                paint.setTextSize(80);
                paint.setColor(Color.MAGENTA);
                canvas.drawText(winnerText, width / 2 - 250, height / 2, paint);
            }
        }
    }
}
