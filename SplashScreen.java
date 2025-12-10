package BankSystem;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    private static final Font WELCOME_FONT = new Font("Arial", Font.BOLD, 30);
    private static final int SPLASH_DURATION = 3000; // 3 ثواني
    
    // **المسار الصحيح المباشر للشعار**
    // تم إصلاح علامات القسمة العكسية باستخدام العلامات المائلة الأمامية
    private static final String LOGO_PATH = "C:/Windows/Screenshot 2025-12-10 214702.png"; 

    public SplashScreen() {
        
        JPanel panel = new JPanel(new BorderLayout(10, 10)); 
        panel.setBackground(Color.WHITE);
        
        // =========================================================
        // 1. إضافة الشعار (الصورة)
        // =========================================================
        try {
            ImageIcon logoIcon = new ImageIcon(LOGO_PATH);
            
            // التحقق من أن الصورة تم تحميلها فعلاً
            if (logoIcon.getIconWidth() > 0) { 
                
                // اختياري: تصغير حجم الصورة إلى حجم ثابت (مثلاً 150x150)
                Image img = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(img);
                
                JLabel logoLabel = new JLabel(logoIcon);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(logoLabel, BorderLayout.NORTH); 
                
            } else {
                // رسالة بديلة في حال عدم العثور على الملف
                JLabel placeholder = new JLabel("SBTU Logo Not Found. Check Path: " + LOGO_PATH, SwingConstants.CENTER);
                placeholder.setFont(new Font("Arial", Font.ITALIC, 14));
                panel.add(placeholder, BorderLayout.NORTH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // =========================================================
        // 2. إضافة نص الترحيب
        // =========================================================
        JLabel welcomeLabel = new JLabel("Welcome To SBTU Bank", SwingConstants.CENTER);
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(new Color(0, 102, 204));

        panel.add(welcomeLabel, BorderLayout.CENTER);
        
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        
        getContentPane().add(panel);
        
        setSize(500, 300);
        setLocationRelativeTo(null); 
    }

    public void showSplash() {
        setVisible(true);

        try {
            Thread.sleep(SPLASH_DURATION); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        setVisible(false);
        dispose(); 
    }
}