package com.example.coinwalk2;

public class Obstacle {
    public float x;
    public float y;
    public float speedY; // ความเร็วในการเคลื่อนที่ลงล่าง[cite: 1]
    public int type;     // 1 = อุปสรรคต่ำ (ต้องโดดหลบ), 2 = อุปสรรคสูง (ต้องก้มหลบ)[cite: 3]

    public Obstacle(float x, float y, float speedY, int type) {
        this.x = x;
        this.y = y;
        this.speedY = speedY;
        this.type = type;
    }

    public void update() {
        this.y += this.speedY; // ขยับอุปสรรคลงมาด้านล่าง[cite: 3]
    }
}