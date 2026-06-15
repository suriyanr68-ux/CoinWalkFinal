package com.example.coinwalk2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    public float x, y;
    public int currentLane = 1; // 0=ซ้าย, 1=กลาง, 2=ขวา

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }

    // อัปเดตตำแหน่งการสไลด์เปลี่ยนเลนอย่างเดียว
    public void update(float targetX, float speed) {
        if (x < targetX) {
            x = Math.min(x + speed, targetX);
        } else if (x > targetX) {
            x = Math.max(x - speed, targetX);
        }
    }

    // วาดตัวละครปกติ (ไม่มีการย่อหรือลอยตัว)
    public void draw(Canvas canvas, Paint paint) {
        // ร่างกายโทนสีน้ำเงินเหล็กเมทัลลิก
        paint.setColor(Color.parseColor("#2563EB"));
        canvas.drawCircle(x, y, 55f, paint);

        // แกนพลังงานเรืองแสงตรงกลางตัว
        paint.setColor(Color.parseColor("#93C5FD"));
        canvas.drawCircle(x, y, 15f, paint);
    }
}