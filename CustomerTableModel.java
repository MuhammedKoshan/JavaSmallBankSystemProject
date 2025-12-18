package BankSystem;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CustomerTableModel extends AbstractTableModel {
    
    // 1. تعريف أسماء الأعمدة بما فيها العمود الجديد Total Balance
    private final String[] columnNames = {
        "ID", 
        "Name",  
        "Email", 
        "Phone", 
        "Saving Acc. No", 
        "Checking Acc. No",
        "Saving Balance",  
        "Checking Balance",
        "Total Balance" // العمود التاسع الجديد
    };
    
    private List<Customer> customerList;
    private final BankSystem bankSystem;

    // تنسيق العملة بالدولار الأمريكي (Locale.US)
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    public CustomerTableModel(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        loadData(); 
    }
    
    /**
     * دالة تحميل وترتيب البيانات من النظام
     */
    private void loadData() {
        this.customerList = new ArrayList<>(bankSystem.getCustomers().values());
        // ترتيب العملاء حسب الـ ID لضمان ثبات العرض
        Collections.sort(this.customerList, (c1, c2) -> c1.getCustomerId().compareTo(c2.getCustomerId()));
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return customerList.size();
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        Customer customer = customerList.get(row);

        switch (col) {
            case 0: return customer.getCustomerId();
            case 1: return customer.getName();
            case 2: return customer.getEmail();
            case 3: return customer.getPhone();
            
            case 4: return customer.getSavingsAccNum() != null ? customer.getSavingsAccNum() : "N/A";
            case 5: return customer.getCheckingAccNum() != null ? customer.getCheckingAccNum() : "N/A";
            
            case 6: // Saving Balance
                return getBalanceFormatted(customer.getSavingsAccNum());
                
            case 7: // Checking Balance
                return getBalanceFormatted(customer.getCheckingAccNum());
                
            case 8: // ** Total Balance (Saving + Checking) **
                BigDecimal total = BigDecimal.ZERO;
                
                // إضافة رصيد التوفير إذا كان موجوداً
                BankAccount savings = bankSystem.getAccount(customer.getSavingsAccNum());
                if (savings != null) {
                    total = total.add(savings.getBalance());
                }
                
                // إضافة رصيد الجاري إذا كان موجوداً
                BankAccount checking = bankSystem.getAccount(customer.getCheckingAccNum());
                if (checking != null) {
                    total = total.add(checking.getBalance());
                }
                
                return CURRENCY_FORMAT.format(total);
                
            default: return null;
        }
    }

    /**
     * دالة مساعدة لجلب الرصيد وتنسيقه كعملة نصية
     */
    private String getBalanceFormatted(String accNum) {
        if (accNum != null) {
            BankAccount acc = bankSystem.getAccount(accNum);
            if (acc != null) {
                return CURRENCY_FORMAT.format(acc.getBalance());
            }
        }
        return "N/A";
    }
    
    /**
     * دالة تحديث الجدول عند حدوث أي تغيير في البيانات
     */
    @Override
    public void fireTableDataChanged() {
        loadData(); // إعادة تحميل القائمة المرتبة
        super.fireTableDataChanged();
    }
}
