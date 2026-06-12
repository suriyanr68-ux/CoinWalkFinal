package com.example.coinwalk2;

public interface GameUpdateListener {
    void onScoreUpdated(int currentScore, int currentLevel); // อัปเดตคะแนน[cite: 2, 3]
    void onGameOver(); // แจ้งเตือนเกมโอเวอร์[cite: 2, 3]
}