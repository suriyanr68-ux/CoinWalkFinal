package com.example.coinwalk2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    public void pauseGame() {
        this.isPaused = true;
        invalidate(); // สั่งให้วาดหน้าจอใหม่เพื่อแสดงคำว่า GAME PAUSED[cite: 3]
    }
    private boolean isMainMenu = true;
    private GameUpdateListener updateListener;

    // ดึงออบเจกต์จากคลาสต่างๆ ที่แยกไว้มาใช้งาน[cite: 3]
    private final Player player = new Player();
    private final Coin coin = new Coin();
    private final List<Obstacle> obstacles = new ArrayList<>();

    private int score = 0;
    private final Paint paint = new Paint();
    private boolean isInitialized = false;
    private int gameTick = 0;

    private float startTouchX = 0f;
    private float startTouchY = 0f;
    private final float minSwipeDistance = 80f;
    private final Random random = new Random();
    private boolean isPaused = false;

    public GameView(Context context) { super(context); init(); }
    public GameView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    private void init() { paint.setAntiAlias(true); }

    public void setGameUpdateListener(GameUpdateListener listener) { this.updateListener = listener; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInitialized && getWidth() > 0 && getHeight() > 0) {
            initGameSetup();
            isInitialized = true;
        }
        drawGamePlay(canvas);
        if (!isPaused) { invalidate(); } // ลูปเกมวนวาดใหม่เรื่อยๆ[cite: 3]
    }

    private void initGameSetup() {
        player.y = getHeight() - 650f;
        player.x = (getWidth() / 3f) * 1.5f;
        resetGameData();
        isMainMenu = true;
    }

    public void resetGameData() {
        score = 0;
        player.currentLane = 1;
        gameTick = 0;
        obstacles.clear();
        coin.y = -100f;
        isPaused = true;
    }

    public void revivePlayer() {
        obstacles.clear();
        isPaused = false;
        invalidate();
    }

    private void drawGamePlay(Canvas canvas) {
        if (isMainMenu) {
            // วาดหน้าเมนูหลัก[cite: 3]
            paint.setColor(Color.parseColor("#1E293B")); canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);
            paint.setColor(Color.parseColor("#F59E0B")); paint.setTextSize(90f); paint.setFakeBoldText(true); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("COIN WALK", getWidth() / 2f, getHeight() / 2f - 100f, paint);
            paint.setColor(Color.parseColor("#10B981")); canvas.drawRoundRect(getWidth() / 2f - 200f, getHeight() / 2f, getWidth() / 2f + 200f, getHeight() / 2f + 120f, 20f, 20f, paint);
            paint.setColor(Color.WHITE); paint.setTextSize(45f); canvas.drawText("START GAME", getWidth() / 2f, getHeight() / 2f + 75f, paint);
            return;
        }

        int currentLevel = (score / 20) + 1;
        float laneWidth = getWidth() / 3f;

        // วาด UI คะแนน[cite: 3]
        paint.setColor(Color.parseColor("#1E293B")); paint.setFakeBoldText(true); paint.setTextAlign(Paint.Align.LEFT);

        // วาดเลนถนน[cite: 3]
        paint.setColor(Color.parseColor("#CBD5E1"));
        paint.setStrokeWidth(6f);
        for (int i = 1; i <= 2; i++) {
            float lx = i * laneWidth;
            canvas.drawLine(lx, 0f, lx, getPlayableBottom(), paint);
        }
        if (!isPaused) {
            gameTick++;
            float playerSpeed = 22f + (currentLevel * 2.0f);
            float targetX = (player.currentLane * laneWidth) + (laneWidth / 2f);

            // อัปเดตคลาสโมเดลต่างๆ[cite: 3]
            player.update(targetX, playerSpeed);
            coin.update(9f + (currentLevel * 2.0f), getPlayableBottom(), laneWidth, random);

            // สุ่มสร้างอุปสรรค[cite: 3]
            int spawnRate = Math.max(14, 60 - (currentLevel * 6));
            if (gameTick % spawnRate == 0) {
                int obstacleLane = random.nextInt(3);
                float obsX = (obstacleLane * laneWidth) + (laneWidth / 2f);
                float obsSpeed = 10f + (currentLevel * 3.5f);
                int obsType = random.nextBoolean() ? 1 : 2;
                obstacles.add(new Obstacle(obsX, 0f, obsSpeed, obsType));
            }

            // ขยับและตรวจการชนอุปสรรค[cite: 3]
            Iterator<Obstacle> iterator = obstacles.iterator();
            while (iterator.hasNext()) {
                Obstacle obs = iterator.next();
                obs.update();

                if (Math.abs(obs.x - player.x) < 60f && obs.y > player.y - 100f && obs.y < getPlayableBottom() - 30f) {
                    if (obs.type == 1 && player.jumpY < -60f) { /* กระโดดหลบพ้น */ }
                    else if (obs.type == 2 && player.isDucking) { /* ก้มหลบพ้น */ }
                    else {
                        this.isPaused = true;
                        if (updateListener != null) { updateListener.onGameOver(); } // ชน! ส่งสัญญาณไปหน้าจอหลัก[cite: 3]
                        return;
                    }
                }
                if (obs.y > getPlayableBottom() + 100f) { iterator.remove(); }
            }

            // ตรวจการกินเหรียญ[cite: 3]
            if (checkCircleCollision(player.x, player.y + player.jumpY, coin.x, coin.y)) {
                score += 5;
                if (updateListener != null) { updateListener.onScoreUpdated(score, (score / 20) + 1); }
                coin.y = getPlayableBottom() + 100f;
            }
        }

        // วาดเหรียญทอง[cite: 3]
        if (coin.y <= getPlayableBottom()) {
            paint.setColor(Color.parseColor("#F59E0B")); canvas.drawCircle(coin.x, coin.y, coin.radius, paint);
        }

        // วาดสิ่งกีดขวาง[cite: 3]
        for (Obstacle obs : obstacles) {
            paint.setColor(obs.type == 1 ? Color.parseColor("#EF4444") : Color.parseColor("#F97316"));
            canvas.drawRoundRect(obs.x - 55f, obs.y - 25f, obs.x + 55f, obs.y + 15f, 8f, 8f, paint);
        }

        // วาดตัวละคร[cite: 3]
        float drawY = player.y + player.jumpY;
        paint.setColor(player.isDucking ? Color.parseColor("#1D4ED8") : Color.parseColor("#3B82F6"));
        canvas.drawCircle(player.x, drawY, player.isDucking ? 45f : 55f, paint);

        if (isPaused) {
            // ฉากหยุดเกม[cite: 3]
            paint.setColor(Color.argb(180, 15, 23, 42)); canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);
            paint.setColor(Color.parseColor("#F59E0B")); paint.setTextSize(75f); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME PAUSED", getWidth() / 2f, getHeight() / 2f - 30f, paint);
        }
    }

    public float getPlayableBottom() { return getHeight() - 400f; }
    public boolean checkCircleCollision(float x1, float y1, float cx, float cy) {
        return Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy)) < (55f + coin.radius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); //[cite: 3]
        float touchX = event.getX(); //[cite: 3]
        float touchY = event.getY(); //[cite: 3]

        switch (action) { //[cite: 3]
            case MotionEvent.ACTION_DOWN: //[cite: 3]
                startTouchX = touchX; //[cite: 3]
                startTouchY = touchY; //[cite: 3]

                // 1. ตรวจสอบว่าอยู่หน้าเมนูหลัก และกดปุ่ม START หรือไม่
                if (isMainMenu) { //[cite: 3]
                    if (touchX >= getWidth() / 2f - 200f && touchX <= getWidth() / 2f + 200f &&
                            touchY >= getHeight() / 2f && touchY <= getHeight() / 2f + 120f) { //[cite: 3]

                        isMainMenu = false;
                        isPaused = false; //[cite: 3]

                        // ส่งสัญญาณบอก MainActivity ให้เปิดการแสดงผลปุ่มต่างๆ บนหน้าจอ
                        if (updateListener != null) {
                            updateListener.onGameStarted();
                        }

                        invalidate(); //[cite: 3]
                    }
                    return true; //[cite: 3]
                }

                // 2. ถ้าเกม Pause อยู่ ให้แตะเพื่อ Resume เล่นต่อ
                if (isPaused) { //[cite: 3]
                    isPaused = false; //[cite: 3]
                    invalidate(); //[cite: 3]
                    return true; //[cite: 3]
                }
                break; //[cite: 3]

            case MotionEvent.ACTION_UP: //[cite: 3]
                if (!isPaused && !isMainMenu) { //[cite: 3]
                    float diffX = touchX - startTouchX; //[cite: 3]
                    float diffY = touchY - startTouchY; //[cite: 3]

                    if (Math.abs(diffX) > minSwipeDistance || Math.abs(diffY) > minSwipeDistance) { //[cite: 3]
                        if (Math.abs(diffX) > Math.abs(diffY)) { //[cite: 3]
                            if (diffX > 0) {
                                if (player.currentLane < 2) player.currentLane++; //[cite: 3]
                            } else {
                                if (player.currentLane > 0) player.currentLane--; //[cite: 3]
                            }
                        } else {
                            if (diffY > 0) {
                                player.duck(); // เรียกคำสั่งก้มจากคลาส Player ที่แยกไว้[cite: 3]
                            } else {
                                player.jump(); // ้เรียกคำสั่งกระโดดจากคลาส Player ที่แยกไว้[cite: 3]
                            }
                        }
                    } else {
                        isPaused = true; //[cite: 3]
                        invalidate(); //[cite: 3]
                    }
                }
                performClick(); //[cite: 3]
                break; //[cite: 3]
        } // 👈 ปีกกาปิดอันนี้คือตัวปิดคำสั่ง switch (action) ที่สมบูรณ์
        return true; //[cite: 3]
    }
        public void returnToMainMenu() {
            isMainMenu = true;
            resetGameData(); // รีเซ็ตแต้มและอุปสรรคทั้งหมด[cite: 3]
            invalidate();    // สั่งวาดหน้าจอใหม่เพื่อกลับสู่หน้าจอเมนูหลักที่มีตัวหนังสือสีส้ม[cite: 3]
        }

    @Override
    public boolean performClick() { return super.performClick(); }
}