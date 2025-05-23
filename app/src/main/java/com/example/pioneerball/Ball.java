package com.example.pioneerball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
    public float x, y;
    public float vx = 5, vy = 5;
    public float radius = 30;

    public Ball(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x += vx;
        y += vy;

        if (x < radius || x > GameView.width - radius) vx *= -1;
        if (y < radius || y > GameView.height - radius - 200) vy *= -1;
    }

    public void checkCollision(Player player) {
        float dx = x - player.x;
        float dy = y - player.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < radius + player.radius) {
            float angle = (float) Math.atan2(dy, dx);
            float speed = (float) Math.sqrt(vx * vx + vy * vy);
            vx = (float) Math.cos(angle) * speed;
            vy = (float) Math.sin(angle) * speed;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, radius, paint);
    }
}