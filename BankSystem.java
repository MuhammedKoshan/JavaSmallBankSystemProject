package BankSystem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BankSystem {
    
    // لتمثيل جميع العملاء والحسابات
    private final Map<String, Customer> customers;
    private final Map<String, BankAccount> accounts;
    private final Random random;
    
    // متغير لتوليد ID العميل (تسلسلي)
    private int nextCustomerId=1; 
    
    // =========================================================
    // **التعديل الجديد: متغير لتوليد رقم الحساب التسلسلي**
    // يبدأ من 10 أرقام (يمكنك تغيير هذا الرقم البادئ)
   
    // =========================================================


    public BankSystem() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.random = new Random();
        
        // أمثلة مبدئية لضمان عدم ظهور قوائم فارغة عند التشغيل الأول
       
        
        // يجب إضافة حسابات اختبار هنا إذا لزم الأمر
    }
    
    // =========================================================
    // CUSTOMER MANAGEMENT FUNCTIONS
    // =========================================================

    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    public boolean customerExists(String customerId) {
        return customers.containsKey(customerId);
    }
    
    // دالة مساعدة مطلوبة لعرض العملاء في الجدول والكومبوبوكس
    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public String generateUniqueCustomerId() {
        String newId;
        do {
            // توليد تسلسلي لـ ID العميل
            newId = String.valueOf(nextCustomerId++);
        } while (customers.containsKey(newId));
        return newId;
    }
    
    // =========================================================
    // ACCOUNT GENERATION AND MANAGEMENT FUNCTIONS
    // =========================================================
    
    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public BankAccount getAccount(String accNum) {
        return accounts.get(accNum);
    }

    public boolean accountExists(String accNum) {
        return accounts.containsKey(accNum);
    }
    
    /**
     * توليد رقم حساب فريد (تسلسلي) مع التحقق من عدم تكراره.
     */
   
    
    // =========================================================
    // TRANSACTION AND SERVICE FUNCTIONS
    // =========================================================

    public boolean transfer(String fromAccNum, String toAccNum, BigDecimal amount) {
        BankAccount fromAcc = accounts.get(fromAccNum);
        BankAccount toAcc = accounts.get(toAccNum);

        if (fromAcc == null || toAcc == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // منطق التحويل: السحب أولاً، ثم الإيداع
        if (fromAcc.withdraw(amount)) {
            toAcc.deposit(amount);
            return true;
        }
        return false;
    }
    
    // دالة وهمية لتمثيل إضافة طلب خدمة (للتوافق مع MenuGUI)
    public void addServiceRequest(ServiceRequest request) {
        System.out.println("Service Request Added: " + request.getIssue());
    }
    
    // دالة وهمية لتمثيل خدمة الطلب التالي
    public ServiceRequest serveNextRequest() {
        return new ServiceRequest("0000", "Simulated Service", 1); // إرجاع طلب وهمي
    }
    
    // دالة وهمية للتحقق من وجود طلبات
    public boolean hasRequests() {
        return true; 
    }

	public String generateUniqueAccountNumber() {
		// TODO Auto-generated method stub
		return null;
	}
}