package com.example.coinwalk2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

public class Coin {
    public float x, y;
    public float radius = 35f;

    // === เพิ่มโค้ดส่วนนี้เข้าไปเพื่อแก้ Error ของคลาส Coin ครับ ===
    public Coin(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }
    // =======================================================

    // อัปเดตการเลื่อนลงมา ถ้าเลยหน้าจอให้สุ่มเกิดใหม่ด้านบน
    public void update(float speed, float playableBottom, float laneWidth, Random random) {
        y += speed;
        if (y > playableBottom + 50f) {
            respawn(playableBottom, laneWidth, random);
        }
    }

    public void respawn(float playableBottom, float laneWidth, Random random) {
        int coinLane = random.nextInt(3);
        x = (coinLane * laneWidth) + (laneWidth / 2f);
        y = -random.nextInt(400) - 100f; // สุ่มระยะหน่วงด้านบน
    }

    // วาดเหรียญทองแบบมีมิติขอบเข้ม
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.parseColor("#EAB308")); // สีทองหลัก
        canvas.drawCircle(x, y, radius, paint);

        paint.setColor(Color.parseColor("#CA8A04")); // ขอบในสีทองเข้ม
        canvas.drawCircle(x, y, radius - 8f, paint);
    }
}