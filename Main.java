package BankSystem;

import javax.swing.UIManager;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        
        // 1. تطبيق حجم الخط الافتراضي (كما اتفقنا عليه سابقاً)
        try {
            // ... (كود UIManager لتغيير حجم الخط) ...
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // =========================================================
        // 2. تشغيل شاشة الترحيب أولاً
        // =========================================================
        SplashScreen splash = new SplashScreen();
        splash.showSplash(); // عرض الشاشة لمدة 3 ثوانٍ

        // 3. بدء تشغيل النظام البنكي والواجهة الرئيسية بعد إغلاق شاشة الترحيب
        BankSystem bank = new BankSystem();
        javax.swing.SwingUtilities.invokeLater(() -> new MenuGUI(bank));
    }
}