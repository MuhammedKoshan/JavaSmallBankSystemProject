package BankSystem;

// يجب أن تضمن أن كلاس BankAccount متاح (على الأقل لوحدة المعاملات)

public class Customer {
    private final String customerId;
    private String name;
    private String email;
    private String phone;
    
    // =========================================================
    // **الإضافات الجديدة لتخزين أرقام الحسابات**
    // =========================================================
    private String savingsAccNum;
    private String checkingAccNum;

    // المُنشئ (Constructor)
    public Customer(String customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        // يتم تهيئة أرقام الحسابات إلى null عند إنشاء العميل
        this.savingsAccNum = null;
        this.checkingAccNum = null;
    }

    // =========================================================
    // Getters (التي تستخدمها الجداول والنماذج)
    // =========================================================

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
    
    // **Getters المطلوبة لـ CustomerTableModel لعرض أرقام الحسابات**
    public String getSavingsAccNum() {
        return savingsAccNum;
    }

    public String getCheckingAccNum() {
        return checkingAccNum;
    }

    // =========================================================
    // Setters (التي تستخدمها نماذج التعديل)
    // =========================================================

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * دالة لتعيين رقم الحساب بعد إنشائه.
     * تستخدم في MenuGUI ضمن زر Confirm Creation.
     */
    public void setAccountNumber(String type, String accNum) {
        if (type.equals("Savings")) {
            this.savingsAccNum = accNum;
        } else if (type.equals("Checking")) {
            this.checkingAccNum = accNum;
        }
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", savingsAcc=" + (savingsAccNum != null ? savingsAccNum : "N/A") +
                ", checkingAcc=" + (checkingAccNum != null ? checkingAccNum : "N/A") +
                '}';
    }
}
