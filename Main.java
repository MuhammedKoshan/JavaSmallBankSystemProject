package BankSystem;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // استخدام SwingUtilities لضمان تشغيل الواجهة بشكل مستقر
        SwingUtilities.invokeLater(() -> {
            BankSystem bankSystem = new BankSystem();
            // نبدأ بواجهة تسجيل الدخول أولاً وليس MenuGUI مباشرة
            new LoginGUI(bankSystem); 
        });
    }
}
