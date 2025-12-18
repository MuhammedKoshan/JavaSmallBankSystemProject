package BankSystem;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * كلاس مخصص لتلوين خلية الجدول بناءً على قيمة الرصيد.
 */
public class BalanceStatusRenderer extends DefaultTableCellRenderer {
    
    // تنسيق العملة
    // (يجب أن يتطابق مع التنسيق المستخدم في CustomerTableModel)
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));

    public BalanceStatusRenderer() {
        // توسيط النص في الخلية
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null && !value.toString().equals("N/A")) {
            try {
                // تحويل قيمة النص المعروض (مثل 500.00 ر.س) إلى رقم للمقارنة
                double balance = CURRENCY_FORMAT.parse(value.toString()).doubleValue();
                
                // تطبيق قواعد التلوين
                if (balance > 0) {
                    // رصيد إيجابي
                    c.setForeground(new Color(34, 139, 34)); // Dark Green
                } else if (balance <= 0) {
                    // رصيد صفري أو سلبي
                    c.setForeground(new Color(255, 69, 0)); // Red-Orange
                } else {
                    c.setForeground(Color.BLACK); 
                }
            } catch (Exception e) {
                // إذا كان النص غير قابل للتحويل (مثلاً إذا كان "N/A" بدون فلتر)
                c.setForeground(Color.BLACK);
            }
        } else {
            // "N/A"
            c.setForeground(Color.GRAY);
        }
        
        // عند تحديد الخلية (Selected)
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            // نستخدم لون التحديد الافتراضي بدلاً من اللون المخصص
            c.setForeground(table.getSelectionForeground()); 
        } else {
             c.setBackground(table.getBackground());
        }

        return c;
    }
}
