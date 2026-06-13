package com.example.coinwalk2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class GameRenderer {
    private Paint paint = new Paint();
    private Bitmap logoBitmap;
    private Context context;

    public GameRenderer(Context context, Bitmap logoBitmap) {
        this.context = context;
        this.logoBitmap = logoBitmap;
        paint.setAntiAlias(true);
    }

    public void render(Canvas canvas, GameEngine engine) {
        if (engine.isMainMenu) {
            drawMainMenu(canvas, engine);
        } else {
            drawGameplay(canvas, engine);
        }
    }

    private void drawMainMenu(Canvas canvas, GameEngine engine) {
        // วาดพื้นหลังลายโค้ดไซไฟ bg_cyber_code
        Drawable bgDrawable = context.getDrawable(R.drawable.bg_cyber_code);
        if (bgDrawable != null) {
            bgDrawable.setBounds(0, 0, (int)engine.screenWidth, (int)engine.screenHeight);
            bgDrawable.draw(canvas);
        } else {
            paint.setColor(Color.parseColor("#0B132B"));
            canvas.drawRect(0f, 0f, engine.screenWidth, engine.screenHeight, paint);
        }

        float centerX = engine.screenWidth / 2f;
        float centerY = engine.screenHeight / 2f;

        // วาดโลโก้
        if (logoBitmap != null) {
            int logoSize = 380;
            android.graphics.Rect destRect = new android.graphics.Rect(
                    (int)(centerX - logoSize/2f), (int)(centerY - 450f),
                    (int)(centerX + logoSize/2f), (int)(centerY - 70f)
            );
            canvas.drawBitmap(logoBitmap, null, destRect, paint);
        }

        // ข้อความ COIN WALK
        paint.setColor(Color.parseColor("#F59E0B")); paint.setTextSize(95f); paint.setFakeBoldText(true); paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("COIN WALK", centerX, centerY + 50f, paint);

        // ปุ่ม START GAME
        paint.setColor(Color.parseColor("#10B981")); paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(centerX - 230f, centerY + 150f, centerX + 230f, centerY + 280f, 25f, 25f, paint);
        paint.setColor(Color.WHITE); paint.setTextSize(42f);
        canvas.drawText("START GAME", centerX, centerY + 230f, paint);
    }

    private void drawGameplay(Canvas canvas, GameEngine engine) {
        // 1. วาดพื้นหลังเหล็กสีเงินขัดเงา
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#C0C8D0"));
        canvas.drawRect(0f, 0f, engine.screenWidth, engine.playableBottom, paint);

        // 2. วาดโลโก้ลางๆ ด้านหลังฉาก (Alpha 35)
        if (logoBitmap != null) {
            int oldAlpha = paint.getAlpha();
            paint.setAlpha(35);
            int bgLogoSize = 550;
            android.graphics.Rect destRect = new android.graphics.Rect(
                    (int)(engine.screenWidth/2f - bgLogoSize/2f), (int)(engine.playableBottom/2f - bgLogoSize/2f),
                    (int)(engine.screenWidth/2f + bgLogoSize/2f), (int)(engine.playableBottom/2f + bgLogoSize/2f)
            );
            canvas.drawBitmap(logoBitmap, null, destRect, paint);
            paint.setAlpha(oldAlpha);
        }

        // 3. วาดเส้นแบ่งเลนถนนเรืองแสงนีออน
        for (int i = 1; i <= 2; i++) {
            float lx = i * engine.laneWidth;
            paint.setColor(Color.parseColor("#4099CCFF")); paint.setStrokeWidth(16f);
            canvas.drawLine(lx, 0f, lx, engine.playableBottom, paint);
            paint.setColor(Color.WHITE); paint.setStrokeWidth(6f);
            canvas.drawLine(lx, 0f, lx, engine.playableBottom, paint);
        }

        // 4. สั่งให้ Entity แต่ละตัววาดตัวเอง
        if (engine.coin.y <= engine.playableBottom) {
            engine.coin.draw(canvas, paint);
        }
        for (Obstacle obs : engine.obstacles) {
            obs.draw(canvas, paint);
        }
        engine.player.draw(canvas, paint);

        // 5. วาดหน้าต่างสลัวตอน Pause
        if (engine.isPaused) {
            paint.setColor(Color.argb(180, 15, 23, 42));
            canvas.drawRect(0f, 0f, engine.screenWidth, engine.screenHeight, paint);
            paint.setColor(Color.parseColor("#F59E0B")); paint.setTextSize(75f); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME PAUSED", engine.screenWidth / 2f, engine.screenHeight / 2f - 30f, paint);
        }
    }
}