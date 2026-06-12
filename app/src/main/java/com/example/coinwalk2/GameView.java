package com.example.coinwalk2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; // นำเข้าสำหรับโหลดรูปภาพ
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable; // นำเข้าสำหรับดึง Gradient background
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private Bitmap logoBitmap; // ตัวแปรเก็บรูปโลโก้

    public void pauseGame() {
        this.isPaused = true;
        invalidate(); // สั่งให้วาดหน้าจอใหม่เพื่อแสดงคำว่า GAME PAUSED[cite: 13]
    }
    private boolean isMainMenu = true;
    private GameUpdateListener updateListener;

    // ดึงออบเจกต์จากคลาสต่างๆ ที่แยกไว้มาใช้งาน[cite: 13]
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

    private void init() {
        paint.setAntiAlias(true);
        // 1. โหลดรูปภาพโลโก้ Computer Engineering เข้ามาเตรียมไว้ตั้งแต่เริ่มระบบ
        logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_logo);
    }

    public void setGameUpdateListener(GameUpdateListener listener) { this.updateListener = listener; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInitialized && getWidth() > 0 && getHeight() > 0) {
            initGameSetup();
            isInitialized = true;
        }
        drawGamePlay(canvas);
        if (!isPaused) { invalidate(); } // ลูปเกมวนวาดใหม่เรื่อยๆ[cite: 13]
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
        // ================== [ ส่วนของหน้าเมนูหลัก ] ==================
        if (isMainMenu) {
            // 1. วาดพื้นหลัง Gradient จาก XML
            Drawable bgDrawable = getContext().getDrawable(R.drawable.bg_gradient_menu);
            if (bgDrawable != null) {
                bgDrawable.setBounds(0, 0, getWidth(), getHeight());
                bgDrawable.draw(canvas);
            } else {
                paint.setColor(Color.parseColor("#1E293B")); canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);
            }

            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;

            // 2. จัดวางโลโก้ (ย่อขนาดลงเล็กน้อยให้สมส่วน และดันขึ้นไปด้านบนสุด)
            int logoSize = 380;
            int logoLeft = (int) (centerX - (logoSize / 2f));
            int logoTop = (int) (centerY - 450f); // ขยับขึ้นไปด้านบน

            if (logoBitmap != null) {
                android.graphics.Rect destRect = new android.graphics.Rect(logoLeft, logoTop, logoLeft + logoSize, logoTop + logoSize);
                canvas.drawBitmap(logoBitmap, null, destRect, paint);
            }

            // 3. วาดชื่อเกม COIN WALK (ขยับลงมาอยู่ใต้โลโก้พอดี ไม่ซ้อนทับกัน)
            paint.setColor(Color.parseColor("#F59E0B"));
            paint.setTextSize(95f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            float titleY = centerY + 50f; // พิกัดใต้โลโก้
            canvas.drawText("COIN WALK", centerX, titleY, paint);

            // 4. วาดปุ่ม START GAME (ขยับลงมาด้านล่างถัดจากชื่อเกม)
            float btnWidth = 460f;
            float btnHeight = 130f;
            float btnLeft = centerX - (btnWidth / 2f);
            float btnTop = titleY + 100f; // ห่างจากชื่อเกมลงมา 100 พิกเซลเพื่อความโปร่ง

            paint.setColor(Color.parseColor("#10B981"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(btnLeft, btnTop, btnLeft + btnWidth, btnTop + btnHeight, 25f, 25f, paint);

            // 5. วาดตัวหนังสือบนปุ่ม START GAME
            paint.setColor(Color.WHITE);
            paint.setTextSize(42f);
            float textY = btnTop + (btnHeight / 2f) - ((paint.descent() + paint.ascent()) / 2f);
            canvas.drawText("START GAME", centerX, textY, paint);
            return;
        }
        // ==========================================================

        int currentLevel = (score / 20) + 1;
        float laneWidth = getWidth() / 3f;

        // วาดเลนถนน[cite: 13]
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

            // อัปเดตคลาสโมเดลต่างๆ[cite: 13]
            player.update(targetX, playerSpeed);
            coin.update(9f + (currentLevel * 2.0f), getPlayableBottom(), laneWidth, random);

            // สุ่มสร้างอุปสรรค[cite: 13]
            int spawnRate = Math.max(14, 60 - (currentLevel * 6));
            if (gameTick % spawnRate == 0) {
                int obstacleLane = random.nextInt(3);
                float obsX = (obstacleLane * laneWidth) + (laneWidth / 2f);
                float obsSpeed = 10f + (currentLevel * 3.5f);
                int obsType = random.nextBoolean() ? 1 : 2;
                obstacles.add(new Obstacle(obsX, 0f, obsSpeed, obsType));
            }

            // ขยับและตรวจการชนอุปสรรค[cite: 13]
            Iterator<Obstacle> iterator = obstacles.iterator();
            while (iterator.hasNext()) {
                Obstacle obs = iterator.next();
                obs.update();

                if (Math.abs(obs.x - player.x) < 60f && obs.y > player.y - 100f && obs.y < getPlayableBottom() - 30f) {
                    if (obs.type == 1 && player.jumpY < -60f) { /* กระโดดหลบพ้น */ }
                    else if (obs.type == 2 && player.isDucking) { /* ก้มหลบพ้น */ }
                    else {
                        this.isPaused = true;
                        if (updateListener != null) { updateListener.onGameOver(); } // ชน! ส่งสัญญาณไปหน้าจอหลัก[cite: 13]
                        return;
                    }
                }
                if (obs.y > getPlayableBottom() + 100f) { iterator.remove(); }
            }

            // ตรวจการกินเหรียญ[cite: 13]
            if (checkCircleCollision(player.x, player.y + player.jumpY, coin.x, coin.y)) {
                score += 5;
                if (updateListener != null) { updateListener.onScoreUpdated(score, (score / 20) + 1); }
                coin.y = getPlayableBottom() + 100f;
            }
        }

        // วาดเหรียญทอง[cite: 13]
        if (coin.y <= getPlayableBottom()) {
            paint.setColor(Color.parseColor("#F59E0B")); canvas.drawCircle(coin.x, coin.y, coin.radius, paint);
        }

        // วาดสิ่งกีดขวาง[cite: 13]
        for (Obstacle obs : obstacles) {
            paint.setColor(obs.type == 1 ? Color.parseColor("#EF4444") : Color.parseColor("#F97316"));
            canvas.drawRoundRect(obs.x - 55f, obs.y - 25f, obs.x + 55f, obs.y + 15f, 8f, 8f, paint);
        }

        // วาดตัวละคร[cite: 13]
        float drawY = player.y + player.jumpY;
        paint.setColor(player.isDucking ? Color.parseColor("#1D4ED8") : Color.parseColor("#3B82F6"));
        canvas.drawCircle(player.x, drawY, player.isDucking ? 45f : 55f, paint);

        if (isPaused) {
            // ฉากหยุดเกม[cite: 13]
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
        int action = event.getActionMasked();
        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTouchX = touchX;
                startTouchY = touchY;

                if (isMainMenu) {
                    float titleY = (getHeight() / 2f) + 50f;
                    float btnTop = titleY + 100f;
                    float btnBottom = btnTop + 130f;
                    float btnLeft = (getWidth() / 2f) - 230f;
                    float btnRight = (getWidth() / 2f) + 230f;

                    if (touchX >= btnLeft && touchX <= btnRight &&
                            touchY >= btnTop && touchY <= btnBottom) {

                        isMainMenu = false;
                        isPaused = false;

                        if (updateListener != null) {
                            updateListener.onGameStarted();
                        }

                        invalidate();
                    }
                    return true;
                }

                if (isPaused) {
                    isPaused = false;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isPaused && !isMainMenu) {
                    float diffX = touchX - startTouchX;
                    float diffY = touchY - startTouchY;

                    if (Math.abs(diffX) > minSwipeDistance || Math.abs(diffY) > minSwipeDistance) {
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (diffX > 0) {
                                if (player.currentLane < 2) player.currentLane++;
                            } else {
                                if (player.currentLane > 0) player.currentLane--;
                            }
                        } else {
                            if (diffY > 0) {
                                player.duck();
                            } else {
                                player.jump();
                            }
                        }
                    } else {
                        isPaused = true;
                        invalidate();
                    }
                }
                performClick();
                break;
        }
        return true;
    }

    public void returnToMainMenu() {
        isMainMenu = true;
        resetGameData(); // รีเซ็ตแต้มและอุปสรรคทั้งหมด[cite: 13]
        invalidate();    // สั่งวาดหน้าจอใหม่เพื่อกลับสู่หน้าจอเมนูหลักที่มีตัวหนังสือสีส้ม[cite: 13]
    }

    @Override
    public boolean performClick() { return super.performClick(); }
}