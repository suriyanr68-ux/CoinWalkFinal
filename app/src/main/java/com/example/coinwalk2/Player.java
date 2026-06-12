package com.example.coinwalk2;

public class Player {
    public float x = 0f;
    public float y = 0f;
    public float jumpY = 0f;      // พิกัด Y เสริมตอนกระโดด
    public int currentLane = 1;   // เลนปัจจุบัน (0, 1, 2)
    public int jumpActionTime = 0;// เวลาในการกระโดด[cite: 3]
    public boolean isDucking = false; // กำลังก้มอยู่หรือไม่[cite: 3]
    public int duckActionTime = 0; // เวลาในการก้ม[cite: 3]

    public void update(float targetX, float speed) {
        // แอนิเมชันสไลด์เปลี่ยนเลน ซ้าย-ขวา[cite: 3]
        if (this.x < targetX) {
            this.x += speed;
            if (this.x > targetX) this.x = targetX;
        } else if (this.x > targetX) {
            this.x -= speed;
            if (this.x < targetX) this.x = targetX;
        }

        // คำนวณความสูงตอนกระโดด (ลดเวลาลงเรื่อยๆ จนจบลูป)[cite: 3]
        if (jumpActionTime > 0) {
            jumpY = -180f * (1f - (Math.abs(10 - jumpActionTime) / 10f));
            jumpActionTime--;
        } else {
            jumpY = 0f;
        }

        // คำนวณเวลาก้มหลบ[cite: 3]
        if (isDucking) {
            duckActionTime--;
            if (duckActionTime <= 0) isDucking = false;
        }
    }

    public void jump() {
        if (jumpActionTime <= 0 && !isDucking) { jumpActionTime = 30; } // เริ่มกระโดด[cite: 3]
    }

    public void duck() {
        if (jumpActionTime <= 0 && !isDucking) { isDucking = true; duckActionTime = 12; } // เริ่มก้ม[cite: 3]
    }
}