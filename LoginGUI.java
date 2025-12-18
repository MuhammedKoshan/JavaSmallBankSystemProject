package BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginGUI extends JFrame {
    private final BankSystem bankSystem;

    public LoginGUI(BankSystem bankSystem) {
        this.bankSystem = bankSystem;

        // إعدادات النافذة الأساسية
        setTitle("SBTU Bank | Portal");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // إنشاء اللوحة الرئيسية مع خلفية متدرجة (Gradient)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // تدرج لوني فخم من الأزرق الملكي الداكن إلى الأسود تقريباً
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 48), 0, getHeight(), new Color(36, 59, 85));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(50, 60, 50, 60));

        // 1. الشعار النصي (Logo Section)
        JLabel logoIcon = new JLabel("🏦"); // رمز تعبيري كبديل للشعار
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoIcon.setForeground(new Color(255, 215, 0)); // لون ذهبي
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("SBTU BANK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Secure Banking Gateway");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(new Color(180, 180, 180));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitle.setBorder(new EmptyBorder(5, 0, 40, 0));

        // 2. أزرار الدخول بتصميم عصري (Modern Buttons)
        JButton employeeBtn = createStyledButton("EMPLOYEE LOGIN", new Color(52, 152, 219));
        JButton customerBtn = createStyledButton("CUSTOMER ACCESS", new Color(127, 140, 141));

        // إضافة العناصر للوحة
        mainPanel.add(logoIcon);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(titleLabel);
        mainPanel.add(subTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(employeeBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(customerBtn);

        // 3. تذييل الصفحة (Footer)
        JLabel footer = new JLabel("© 2025 SBTU Financial Services");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(100, 100, 100));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(footer);

        // --- منطق الأزرار ---
        
        customerBtn.addActionListener(e -> {
            showCustomMessage("Access Denied", "This option is not available now.", JOptionPane.WARNING_MESSAGE);
        });

        employeeBtn.addActionListener(e -> showEmployeeLoginDialog());

        add(mainPanel);
        setVisible(true);
    }

    // دالة لإنشاء زر بتصميم عصري وحواف دائرية وتأثير Hover
    private JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(380, 55));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // تأثيرات الحركة (Hover Effect)
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(baseColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
        });
        return btn;
    }

    private void showEmployeeLoginDialog() {
        // تصميم لوحة تسجيل الدخول داخل الـ Dialog لتكون متناسقة
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        JTextField userField = new JTextField();
        userField.setBorder(BorderFactory.createTitledBorder("Admin Username"));
        
        JPasswordField passField = new JPasswordField();
        passField.setBorder(BorderFactory.createTitledBorder("Secure Password"));

        panel.add(new JLabel("Staff Authentication Required:"));
        panel.add(userField);
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Employee Login", 
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.equals("admin") && pass.equals("admin")) {
                new MenuGUI(bankSystem); // فتح الواجهة الأساسية
                this.dispose();
            } else {
                showCustomMessage("Error", "Invalid credentials. Try again.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCustomMessage(String title, String msg, int type) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + msg + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, label, title, type);
    }
}