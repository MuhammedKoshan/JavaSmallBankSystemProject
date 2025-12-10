package BankSystem;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CustomerTableModel extends AbstractTableModel {
    
    // تم تحديث قائمة الأعمدة لتشمل أرصدة الحسابات
    private final String[] columnNames = {
        "ID", 
        "Name", 
        "Email", 
        "Phone", 
        "Saving Acc. No", 
        "Checking Acc. No",
        "Saving Balance",  // العمود الجديد 6
        "Checking Balance" // العمود الجديد 7
    };
    private List<Customer> customerList;
    private final BankSystem bankSystem; // للاستخدام في جلب الرصيد

    // تنسيق العملة
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));

    public CustomerTableModel(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        loadData(); 
    }
    
    private void loadData() {
        this.customerList = new ArrayList<>(bankSystem.getCustomers().values());
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
            
            // ====================================================
            // الأعمدة الجديدة لأرصدة الحسابات
            // ====================================================
            case 6: // Saving Balance
                String savingsAccNum = customer.getSavingsAccNum();
                if (savingsAccNum != null) {
                    BankAccount acc = bankSystem.getAccount(savingsAccNum);
                    if (acc != null) {
                        return CURRENCY_FORMAT.format(acc.getBalance());
                    }
                }
                return "N/A";
                
            case 7: // Checking Balance
                String checkingAccNum = customer.getCheckingAccNum();
                if (checkingAccNum != null) {
                    BankAccount acc = bankSystem.getAccount(checkingAccNum);
                    if (acc != null) {
                        return CURRENCY_FORMAT.format(acc.getBalance());
                    }
                }
                return "N/A";
                
            default: return null;
        }
    }
    
    @Override
    public void fireTableDataChanged() {
        loadData();
        super.fireTableDataChanged();
    }
}