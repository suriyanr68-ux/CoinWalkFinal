package com.example.coinwalk2;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ComputerQuestion {
    private int level; // 👈 เพิ่มตัวแปรเก็บระดับความยาก (1, 2, 3)
    private String question;
    private String correctAnswer;
    private String wrongAnswer;

    // ปรับปรุง Constructor ให้รับค่า level ด้วย
    public ComputerQuestion(int level, String question, String correctAnswer, String wrongAnswer) {
        this.level = level;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer = wrongAnswer;
    }

    public int getLevel() { return level; }
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getWrongAnswer() { return wrongAnswer; }

    /**
     * ฟังก์ชันสุ่มคำถามโดยกรองจากเลเวลของผู้เล่น
     */
    public static ComputerQuestion getRandomQuestionByLevel(int playerLevel) {
        ComputerQuestion[] questionBank = {
                // ⭐ LV 1: ฮาร์ดแวร์ทั่วไป และความรู้พื้นฐานสุดๆ
                new ComputerQuestion(1, "หน่วยประมวลผลกลางของคอมพิวเตอร์คืออะไร?", "CPU", "RAM"),
                new ComputerQuestion(1, "หน่วยความจำใดที่ข้อมูลจะหายไปเมื่อปิดเครื่อง?", "RAM", "ROM"),
                new ComputerQuestion(1, "1 Byte มีค่าเท่ากับกี่ Bit?", "8 Bit", "16 Bit"),
                new ComputerQuestion(1, "พอร์ตใดนิยมใช้เชื่อมต่อเมาส์และคีย์บอร์ดในปัจจุบัน?", "USB", "VGA"),
                new ComputerQuestion(1, "เลขฐานสอง (Binary Code) ประกอบด้วยเลขใดบ้าง?", "0 และ 1", "1 และ 2"),
                new ComputerQuestion(1, "ความผิดพลาดหรือข้อบกพร่องในโปรแกรมคอมพิวเตอร์เรียกว่าอะไร?", "Bug", "Virus"),

                // ⭐ LV 2: ซอฟต์แวร์, ระบบปฏิบัติการ และตรรกศาสตร์ (Logic Gate)
                new ComputerQuestion(2, "ข้อใดคือระบบปฏิบัติการ (OS)?", "Linux", "Python"),
                new ComputerQuestion(2, "หน่วยความจำความเร็วสูงที่อยู่ใกล้ชิดกับ CPU คืออะไร?", "Cache", "Harddisk"),
                new ComputerQuestion(2, "Gate ชนิดใดที่จะให้ผลลัพธ์เป็น 1 เมื่อ Input ทุกตัวเป็น 1?", "AND Gate", "OR Gate"),
                new ComputerQuestion(2, "Gate ชนิดใดที่ทำหน้าที่กลับค่าสัญญาณจาก 0 เป็น 1 หรือ 1 เป็น 0?", "NOT Gate", "AND Gate"),
                new ComputerQuestion(2, "สัญลักษณ์ใดมักใช้แทนเงื่อนไขและการตัดสินใจในผังงาน (Flowchart)?", "สี่เหลี่ยมข้าวหลามตัด", "วงกลม"),

                // ⭐ LV 3+: เครือข่าย (Network), ความปลอดภัย (Cybersecurity) และสถาปัตยกรรมระดับสูง
                new ComputerQuestion(3, "โปรแกรมที่แปลภาษาโปรแกรมระดับสูงให้เป็นภาษาเครื่องพร้อมกันทั้งโปรแกรมคืออะไร?", "Compiler", "Interpreter"),
                new ComputerQuestion(3, "ในระบบเลขฐานสิบหก (Hexadecimal) ตัวอักษร 'A' มีค่าเท่ากับข้อใด?", "10", "12"),
                new ComputerQuestion(3, "อุปกรณ์ใดทำหน้าที่แปลงสัญญาณดิจิทัลเป็นอนาล็อกเพื่อต่ออินเทอร์เน็ต?", "Modem", "CPU"),
                new ComputerQuestion(3, "ข้อใดคือหมายเลขที่ใช้ระบุตัวตนของอุปกรณ์ในระบบเครือข่าย?", "IP Address", "MAC URL"),
                new ComputerQuestion(3, "โปรโตคอล (Protocol) ใดใช้สำหรับรับส่งหน้าเว็บไซต์แบบปลอดภัย?", "HTTPS", "FTP"),
                new ComputerQuestion(3, "อุปกรณ์เครือข่ายใดทำหน้าที่จัดเส้นทางในการส่งข้อมูลไปยังปลายทาง?", "Router", "Switch"),
                new ComputerQuestion(3, "กระบวนการแปลงข้อมูลให้เป็นรหัสลับเพื่อความปลอดภัยเรียกว่าอะไร?", "Encryption", "Decryption"),
                new ComputerQuestion(3, "มัลแวร์ประเภทใดที่แอบแฝงมากับโปรแกรมดีเพื่อหลอกให้ผู้ใช้ติดตั้ง?", "Trojan", "Worm")
        };

        // ตรวจสอบเลเวลสูงสุดที่มีคำถามรองรับ (ในที่นี้คือเลเวล 3)
        int targetLevel = playerLevel;
        if (targetLevel > 3) targetLevel = 3;

        // คัดกรองคำถามเฉพาะเลเวลที่ต้องการ
        List<ComputerQuestion> filteredList = new ArrayList<>();
        for (ComputerQuestion q : questionBank) {
            if (q.getLevel() == targetLevel) {
                filteredList.add(q);
            }
        }

        // สุ่มคำถามจากรายการที่กรองแล้ว
        Random random = new Random();
        return filteredList.get(random.nextInt(filteredList.size()));
    }
}