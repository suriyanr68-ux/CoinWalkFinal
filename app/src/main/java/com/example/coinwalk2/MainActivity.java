package com.example.coinwalk2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GameUpdateListener {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        gameView.setGameUpdateListener(this); // เชื่อมโยงตัวรับเหตุการณ์[cite: 4]
        setContentView(gameView);
    }

    @Override
    public void onGameOver() {
        runOnUiThread(this::showReviveDialog); // เมื่อชนปุ๊บ เปิดหน้าต่างคำถามช่วยชีวิตทันที[cite: 4]
    }

    @Override
    public void onScoreUpdated(int currentScore, int currentLevel) {
        // สามารถนำไปประยุกต์แสดงผลลง UI เพิ่มเติมได้[cite: 4]
    }

    private void showReviveDialog() {
        // 1. เรียกรับโมเดลคำถามที่สุ่มมาจากคลาส ComputerQuestion
        ComputerQuestion selectedQuestion = ComputerQuestion.getRandomQuestion();

        // 2. สุ่มจัดวางปุ่มคำตอบซ้าย-ขวาไม่ให้ซ้ำเดิม[cite: 4]
        Random random = new Random();
        boolean isCorrectLeft = random.nextBoolean();

        // 3. แสดงหน้าต่างคำถามบนจอภาพ[cite: 4]
        new AlertDialog.Builder(this)
                .setTitle("โอกาสแก้ตัวด้วยวิศวคอมฯ!")
                .setMessage(selectedQuestion.getQuestion())
                .setCancelable(false)
                .setPositiveButton(isCorrectLeft ? selectedQuestion.getCorrectAnswer() : selectedQuestion.getWrongAnswer(), (dialog, which) -> {
                    if (isCorrectLeft) { gameView.revivePlayer(); } // ตอบถูก -> คืนชีพวิ่งต่อ[cite: 3, 4]
                    else { handleWrongAnswer(); } // ตอบผิด -> เริ่มใหม่
                })
                .setNegativeButton(!isCorrectLeft ? selectedQuestion.getCorrectAnswer() : selectedQuestion.getWrongAnswer(), (dialog, which) -> {
                    if (!isCorrectLeft) { gameView.revivePlayer(); } // ตอบถูก -> คืนชีพวิ่งต่อ[cite: 3, 4]
                    else { handleWrongAnswer(); } // ตอบผิด -> เริ่มใหม่
                })
                .show();
    }

    private void handleWrongAnswer() {
        gameView.resetGameData(); // รีเซ็ตข้อมูลเกมเก่า[cite: 3, 4]
        showStartMenu(); // กลับไปหน้าเริ่มเกมใหม่[cite: 4]
    }

    private void showStartMenu() {
        runOnUiThread(() -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}