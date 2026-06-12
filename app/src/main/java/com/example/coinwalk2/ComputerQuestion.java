package com.example.coinwalk2;

import java.util.Random;

public class ComputerQuestion {
    private String question;
    private String correctAnswer;
    private String wrongAnswer;

    // Constructor สำหรับสร้างวัตถุคำถาม
    public ComputerQuestion(String question, String correctAnswer, String wrongAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer = wrongAnswer;
    }

    // Getter สำหรับดึงข้อมูลไปแสดงผลบน UI
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getWrongAnswer() { return wrongAnswer; }

    /**
     * ฟังก์ชัน Static สำหรับสุ่มคำถามวิศวกรรมคอมพิวเตอร์เบื้องต้น
     */
    public static ComputerQuestion getRandomQuestion() {
        ComputerQuestion[] questionBank = {
                new ComputerQuestion("หน่วยประมวลผลกลางของคอมพิวเตอร์คืออะไร?", "CPU", "RAM"),
                new ComputerQuestion("หน่วยความจำใดที่ข้อมูลจะหายไปเมื่อปิดเครื่อง?", "RAM", "ROM"),
                new ComputerQuestion("1 Byte มีค่าเท่ากับกี่ Bit?", "8 Bit", "16 Bit"),
                new ComputerQuestion("ข้อใดคือระบบปฏิบัติการ (OS)?", "Linux", "Python"),
                new ComputerQuestion("พอร์ตใดนิยมใช้เชื่อมต่อเมาส์และคีย์บอร์ดในปัจจุบัน?", "USB", "VGA"),
                new ComputerQuestion("เลขฐานสอง (Binary Code) ประกอบด้วยเลขใดบ้าง?", "0 และ 1", "1 และ 2"),
                new ComputerQuestion("Gate ชนิดใดที่จะให้ผลลัพธ์เป็น 1 เมื่อ Input ทุกตัวเป็น 1?", "AND Gate", "OR Gate"),
                new ComputerQuestion("อุปกรณ์ใดทำหน้าที่แปลงสัญญาณดิจิทัลเป็นอนาล็อกเพื่อต่ออินเทอร์เน็ต?", "Modem", "CPU")
        };

        Random random = new Random();
        int randomIndex = random.nextInt(questionBank.length);
        return questionBank[randomIndex];
    }
}