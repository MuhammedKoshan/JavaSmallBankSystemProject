package BankSystem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.Comparator;

public class BankSystem {
    
    // تخزين العملاء والحسابات باستخدام الخرائط (Maps) لسرعة الوصول
    private final Map<String, Customer> customers;
    private final Map<String, BankAccount> accounts;
    
    // قائمة انتظار الخدمة ذات الأولوية
    private final PriorityQueue<ServiceRequest> serviceQueue;

    public BankSystem() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        
        // تهيئة قائمة الخدمة لترتيب الطلبات حسب الأولوية (الرقم الأصغر أولاً)
        this.serviceQueue = new PriorityQueue<>(
            Comparator.comparingInt(ServiceRequest::getPriority)
        );
    }

    // =========================================================
    // إدارة العملاء (Customer Management)
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
    
    public Map<String, Customer> getCustomers() {
        return customers;
    }

    /**
     * توليد ID تلقائي للعميل يبدأ بـ TR متبوعاً بـ 24 رقماً منسقاً
     */
    public String generateAutomaticID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("TR");
        
        for (int i = 0; i < 24; i++) {
            sb.append(random.nextInt(10));
            // إضافة مسافة كل 4 أرقام لتسهيل القراءة (مثل IBAN)
            if ((i + 1) % 4 == 0 && i < 23) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // =========================================================
    // إدارة الحسابات (Account Management)
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
     * إرجاع كافة الحسابات (مهم لنافذة البحث والـ Browse)
     */
    public Map<String, BankAccount> getAllAccounts() {
        return accounts;
    }

    /**
     * توليد رقم حساب عشوائي يبدأ بـ 25 مع 4 أرقام إضافية
     */
    public String generateRandomAccountNumber() {
        Random random = new Random();
        int suffix = random.nextInt(10000); 
        return "25" + String.format("%04d", suffix);
    }

    // =========================================================
    // العمليات المالية (Transactions)
    // =========================================================

    public boolean transfer(String fromAccNum, String toAccNum, BigDecimal amount) {
        BankAccount fromAcc = accounts.get(fromAccNum);
        BankAccount toAcc = accounts.get(toAccNum);

        if (fromAcc == null || toAcc == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // تنفيذ عملية السحب من المرسل ثم الإيداع للمستقبل
        if (fromAcc.withdraw(amount)) {
            toAcc.deposit(amount);
            return true;
        }
        return false;
    }

    // =========================================================
    // قائمة انتظار الخدمة (Service Queue)
    // =========================================================

    public void addServiceRequest(ServiceRequest request) {
        serviceQueue.add(request);
    }

    public ServiceRequest serveNextRequest() {
        return serviceQueue.poll();
    }

    public boolean hasRequests() {
        return !serviceQueue.isEmpty();
    }
}
