package com.example.coinwalk2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    public float x, y;
    public float jumpY = 0;
    public int currentLane = 1; // 0=ซ้าย, 1=กลาง, 2=ขวา
    public boolean isDucking = false;

    // === เพิ่มโค้ดส่วนนี้เข้าไปเพื่อแก้ Error ตัวเมื่อกี้ครับ ===
    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }
    // ===================================================

    // อัปเดตตำแหน่งการสไลด์เปลี่ยนเลน
    public void update(float targetX, float speed) {
        if (x < targetX) {
            x = Math.min(x + speed, targetX);
        } else if (x > targetX) {
            x = Math.max(x - speed, targetX);
        }
    }

    // วาดตัวละครธีมไฮเทค
    public void draw(Canvas canvas, Paint paint) {
        float drawY = y + jumpY;

        // ร่างกายโทนสีน้ำเงินเหล็กเมทัลลิก
        paint.setColor(isDucking ? Color.parseColor("#1E3A8A") : Color.parseColor("#2563EB"));
        canvas.drawCircle(x, drawY, isDucking ? 45f : 55f, paint);

        // แกนพลังงานเรืองแสงตรงกลางตัว
        paint.setColor(Color.parseColor("#93C5FD"));
        canvas.drawCircle(x, drawY, 15f, paint);
    }
}