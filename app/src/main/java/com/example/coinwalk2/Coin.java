package com.example.coinwalk2;

import java.util.Random;

public class Coin {
    public float x = 0f;
    public float y = -100f;
    public final float radius = 40f; // รัศมีเหรียญ[cite: 3]

    public void update(float speed, float playableBottom, float laneWidth, Random random) {
        // ถ้าเหรียญตกเลยขอบจอ ให้สุ่มตำแหน่งเกิดใหม่ด้านบน[cite: 3]
        if (y > playableBottom || y < 0f) {
            int coinLane = random.nextInt(3);
            x = (coinLane * laneWidth) + (laneWidth / 2f);
            y = 0f;
        } else {
            y += speed; // ร่วงลงมาตามความเร็วเกม[cite: 3]
        }
    }
}