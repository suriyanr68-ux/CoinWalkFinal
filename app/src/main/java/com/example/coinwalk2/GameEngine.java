package com.example.coinwalk2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameEngine {
    public boolean isMainMenu = true;
    public boolean isPaused = false;
    public int score = 0;
    public int gameTick = 0;
    public int currentLevel = 1;

    public float screenWidth;
    public float screenHeight;
    public float playableBottom;
    public float laneWidth;

    public Player player;
    public Coin coin;
    public ArrayList<Obstacle> obstacles = new ArrayList<>();

    private Random random = new Random();
    private GameUpdateListener updateListener;

    public GameEngine(float width, float height, GameUpdateListener listener) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.playableBottom = height * 0.82f;
        this.laneWidth = width / 3f;
        this.updateListener = listener;

        // สร้างออบเจกต์เริ่มต้น
        player = new Player(width / 2f, height * 0.7f);
        coin = new Coin(width / 2f, -200f);
    }

    // อัปเดตสถานะของเกมในทุกๆ เฟรม
    public void update() {
        if (isMainMenu || isPaused) return;

        gameTick++;
        currentLevel = (score / 50) + 1;

        float playerSpeed = 22f + (currentLevel * 2.0f);
        float targetX = (player.currentLane * laneWidth) + (laneWidth / 2f);

        // 1. อัปเดตผู้เล่นและเหรียญ
        player.update(targetX, playerSpeed);
        coin.update(9f + (currentLevel * 2.0f), playableBottom, laneWidth, random);

        // 2. ระบบสุ่มเกิดสิ่งกีดขวาง
        int spawnRate = Math.max(14, 60 - (currentLevel * 6));
        if (gameTick % spawnRate == 0) {
            int obsLane = random.nextInt(3);
            float obsX = (obsLane * laneWidth) + (laneWidth / 2f);
            float obsSpeed = 10f + (currentLevel * 3.5f);
            int obsType = random.nextBoolean() ? 1 : 2;
            obstacles.add(new Obstacle(obsX, 0f, obsSpeed, obsType));
        }

        // 3. อัปเดตและตรวจจับการชนสิ่งกีดขวาง
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obs = iterator.next();
            obs.update();

            if (Math.abs(obs.x - player.x) < 60f && obs.y > player.y - 100f && obs.y < playableBottom - 30f) {
                if (obs.type == 1 && player.jumpY < -60f) { /* หลบพ้น */ }
                else if (obs.type == 2 && player.isDucking) { /* หลบพ้น */ }
                else {
                    this.isPaused = true;
                    if (updateListener != null) updateListener.onGameOver();
                    return;
                }
            }
            if (obs.y > playableBottom + 100f) {
                iterator.remove();
            }
        }

        // 4. ตรวจจับการเก็บเหรียญ
        float dx = player.x - coin.x;
        float dy = (player.y + player.jumpY) - coin.y;
        float distanceSq = (dx * dx) + (dy * dy);
        float radiusSum = 50f + coin.radius;

        if (distanceSq < (radiusSum * radiusSum)) {
            score += 5;
            if (updateListener != null) updateListener.onScoreUpdated(score, currentLevel);
            coin.respawn(playableBottom, laneWidth, random);
        }
    }

    public void startNewGame() {
        isMainMenu = false;
        isPaused = false;
        score = 0;
        gameTick = 0;
        currentLevel = 1;
        obstacles.clear();
        player.currentLane = 1;
        coin.y = -200f;
        if (updateListener != null) updateListener.onGameStarted();
    }
}