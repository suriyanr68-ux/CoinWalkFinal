package com.example.coinwalk2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private GameEngine gameEngine;
    private GameRenderer gameRenderer;
    private GameUpdateListener updateListener;

    private float touchX, touchY;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGameUpdateListener(GameUpdateListener listener) {
        this.updateListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gameEngine = new GameEngine(w, h, updateListener);

        // โค้ดโหลดแบบพิเศษ: รองรับทั้งไฟล์ภาพปกติ และไฟล์ XML ลายเส้น
        gameRenderer = new GameRenderer(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.game_logo));
        /*android.graphics.drawable.Drawable drawable = getContext().getDrawable(R.mipmap.ic_launcher);
        android.graphics.Bitmap safeLogo = null;

        if (drawable != null) {
            if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
                safeLogo = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
            } else {
                // จัดการวาดไฟล์ XML ลงบนแผ่นภาพจำลองเพื่อแปลงเป็น Bitmap
                int width = Math.max(1, drawable.getIntrinsicWidth());
                int height = Math.max(1, drawable.getIntrinsicHeight());
                safeLogo = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(safeLogo);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
        }

        // ส่งแผ่นภาพที่แปลงสำเร็จไปให้ Renderer วาดต่อ
        gameRenderer = new GameRenderer(getContext(), safeLogo);*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameEngine == null || gameRenderer == null) return;

        // 1. คำนวณ Logic ลำดับตัวแปร
        gameEngine.update();

        // 2. ส่งต่อข้อมูลไปให้ Renderer วาดผลลัพธ์ลงจอ
        gameRenderer.render(canvas, gameEngine);

        // สั่ง Loop วาดซ้ำเรื่อยๆ
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameEngine == null) return false;

        float centerX = gameEngine.screenWidth / 2f;
        float centerY = gameEngine.screenHeight / 2f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();

                // กดปุ่ม START GAME ในหน้าเมนู
                if (gameEngine.isMainMenu) {
                    if (touchX >= centerX - 230f && touchX <= centerX + 230f &&
                            touchY >= centerY + 150f && touchY <= centerY + 280f) {
                        gameEngine.startNewGame();
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
                float diffX = event.getX() - touchX;
                float diffY = event.getY() - touchY;

                if (!gameEngine.isMainMenu && !gameEngine.isPaused) {
                    // ตรวจจับการ Swipe (เหลือเฉพาะ ปัดซ้าย-ปัดขวา)
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (diffX > 100f && gameEngine.player.currentLane < 2) gameEngine.player.currentLane++; // ปัดขวา
                        else if (diffX < -100f && gameEngine.player.currentLane > 0) gameEngine.player.currentLane--; // ปัดซ้าย
                    }
                    // ส่วนของ diffY (ขึ้น-ลง) ถูกตัดออกไปแล้ว
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    // ฟังก์ชันเสริมสำหรับใช้ควบคุมข้ามมาจาก MainActivity
    public void returnToMainMenu() {
        if (gameEngine != null) gameEngine.isMainMenu = true;
    }

    public void revivePlayer() {
        if (gameEngine != null) {
            gameEngine.isPaused = false;
            gameEngine.obstacles.clear();
        }
    }

    public void resetGameData() {
        if (gameEngine != null) gameEngine.startNewGame();
    }
}