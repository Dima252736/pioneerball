// Player.java
package com.example.pioneerball;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Player {
    public float x, y;
    public int color;
    public float radius = 50;
    private float speed = 10;

    public Player(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void move(boolean left, boolean right, boolean up, boolean down) {
        if (left) x -= speed;
        if (right) x += speed;
        if (up) y -= speed;
        if (down) y += speed;
    }

    public void update() {}

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }
}