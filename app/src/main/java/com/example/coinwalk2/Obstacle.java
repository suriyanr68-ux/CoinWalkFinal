package com.example.coinwalk2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Obstacle {
    public float x, y;
    public float speed;
    public int type; // 1 = สิ่งกีดขวางต่ำ (ต้องกระโดด), 2 = สิ่งกีดขวางสูง (ต้องก้ม)

    public Obstacle(float x, float y, float speed, int type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
    }

    // ขยับเลื่อนลงมาตามความเร็ว
    public void update() {
        y += speed;
    }

    // === เพิ่มฟังก์ชันนี้เข้าไปเพื่อแก้ Error บรรทัดที่ 97 ครับ ===
    public void draw(Canvas canvas, Paint paint) {
        // บล็อกหลัก สีเมทัลลิกแดง-ส้ม
        paint.setColor(type == 1 ? Color.parseColor("#B91C1C") : Color.parseColor("#C2410C"));
        canvas.drawRoundRect(x - 55f, y - 25f, x + 55f, y + 15f, 12f, 12f, paint);

        // ลวดลายร่องเหล็กเพิ่มมิติเครื่องจักรตรงกลาง
        paint.setColor(Color.parseColor("#7F1D1D"));
        canvas.drawRect(x - 45f, y - 5f, x + 45f, y + 5f, paint);
    }
}