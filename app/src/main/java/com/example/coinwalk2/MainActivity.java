package com.example.coinwalk2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GameUpdateListener {

    private GameView gameView;
    private TextView tvScore;
    private TextView tvLevel;
    private View btnBack; // เปลี่ยนเป็น View เพื่อรองรับ LinearLayout
    private View containerScore;
    private View containerLevel;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        gameView = findViewById(R.id.gameView);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        btnBack = findViewById(R.id.btnBack);
        containerScore = findViewById(R.id.containerScore);
        containerLevel = findViewById(R.id.containerLevel);

        gameView.setGameUpdateListener(this);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                gameView.returnToMainMenu();
                if (containerScore != null) containerScore.setVisibility(View.GONE);
                if (containerLevel != null) containerLevel.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                currentLevel = 1;
            });
        }
    }

    @Override
    public void onGameStarted() {
        runOnUiThread(() -> {
            if (containerScore != null) containerScore.setVisibility(View.VISIBLE);
            if (containerLevel != null) containerLevel.setVisibility(View.VISIBLE);
            if (btnBack != null) btnBack.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onGameOver() {
        runOnUiThread(this::showReviveDialog);
    }

    @Override
    public void onScoreUpdated(int currentScore, int currentLevel) {
        this.currentLevel = currentLevel;
        runOnUiThread(() -> {
            if (tvScore != null) tvScore.setText(String.valueOf(currentScore)); // เปลี่ยนให้แสดงแค่ตัวเลข
            if (tvLevel != null) tvLevel.setText(String.valueOf(currentLevel)); // เปลี่ยนให้แสดงแค่ตัวเลข
        });
    }

    private void showReviveDialog() {
        ComputerQuestion selectedQuestion = ComputerQuestion.getRandomQuestionByLevel(currentLevel);
        Random random = new Random();
        boolean isCorrectLeft = random.nextBoolean();

        new AlertDialog.Builder(this)
                .setTitle("โอกาสแก้ตัวระดับ LV." + currentLevel + "!")
                .setMessage(selectedQuestion.getQuestion())
                .setCancelable(false)
                .setPositiveButton(isCorrectLeft ? selectedQuestion.getCorrectAnswer() : selectedQuestion.getWrongAnswer(), (dialog, which) -> {
                    if (isCorrectLeft) { gameView.revivePlayer(); }
                    else { handleWrongAnswer(); }
                })
                .setNegativeButton(!isCorrectLeft ? selectedQuestion.getCorrectAnswer() : selectedQuestion.getWrongAnswer(), (dialog, which) -> {
                    if (!isCorrectLeft) { gameView.revivePlayer(); }
                    else { handleWrongAnswer(); }
                })
                .show();
    }

    private void handleWrongAnswer() {
        gameView.resetGameData();
        currentLevel = 1;
        showStartMenu();
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