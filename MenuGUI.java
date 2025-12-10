package BankSystem;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
// تم حذف استدعاء BalanceStatusRenderer


// ملاحظة: هذا الكلاس يتطلب وجود CustomerTableModel وكلاس BankSystem مُعدل (generateUniqueCustomerId و getCustomers و transfer)

public class MenuGUI extends JFrame {

    private final BankSystem bankSystem;
    private JTabbedPane tabbedPane;

    // **المسار المطلق للشعار** (لاستخدامه كأيقونة للنافذة)
    private static final String LOGO_PATH = "C:/Windows/Screenshot 2025-12-10 214702.png"; 

    // الخصائص العامة لتحديث العناصر
    private CustomerTableModel customerTableModel; 
    private JTable customerTable;
    
    // متغيرات الـ JComboBox العامة (لحل مشكلة التحديث)
    private JComboBox<String> savingsCustomerBox;
    private JComboBox<String> checkingCustomerBox;
    
    // تعريف المشاكل والأولويات (الخريطة الثابتة)
    private final Map<String, Integer> ISSUE_PRIORITIES = Map.of(
        "Lost Card / Stolen Card", 1,
        "Withdrawal/Deposit Error", 2,
        "Account Information Update", 3,
        "General Inquiry", 3
    );


    public MenuGUI(BankSystem bankSystem) {
        this.bankSystem = bankSystem;

        // =========================================================
        // إضافة أيقونة النافذة (Window Icon)
        // =========================================================
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH); 
            
            if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.err.println("Failed to load window icon from path: " + LOGO_PATH);
        }
        // =========================================================

        setTitle("SBTU Bank Management System");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("👤 Customers & Accounts", createAccountsPanel());
        tabbedPane.addTab("💸 Transactions & Transfer", createTransactionPanel());
        tabbedPane.addTab("📜 Service Queue", createServicePanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JButton exitBtn = new JButton("Exit System");
        exitBtn.setBackground(new Color(220, 50, 50));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> System.exit(0));
        
        mainPanel.add(exitBtn, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
    
    // =========================================================
    // HELPER FUNCTIONS
    // =========================================================

    /**
     * تجهيز قائمة الخيارات لعرضها في JComboBox (الشكل: ID - Name).
     */
    private String[] getCustomerOptions() {
        return bankSystem.getCustomers().values().stream()
                .map(c -> c.getCustomerId() + " - " + c.getName())
                .sorted() 
                .toArray(String[]::new);
    }
    
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // 1. Customers & Accounts Panel 
    // =========================================================

    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45); 

        Font borderFont = new Font("Arial", Font.BOLD, 20); 

        // 1. لوحة النماذج (Forms Panel) - ترتيب عمودي جديد
        JPanel formsPanel = new JPanel(); 
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS)); 
        formsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                "🆕 Customer & Account Creation", 
                TitledBorder.LEADING, 
                TitledBorder.TOP, 
                borderFont));

        // A. النموذج الأول: Customer Details (إطار رمادي بسيط)
        JPanel customerWrapper = new JPanel(new BorderLayout());
        customerWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                "Customer Details", 
                TitledBorder.LEADING, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.BLACK 
        )); 
        customerWrapper.add(createCustomerForm(), BorderLayout.CENTER);
        
        formsPanel.add(customerWrapper);
        
        // B. النماذج الثانية: Saving & Checking (جنباً إلى جنب)
        JPanel accountsContainer = new JPanel(new GridLayout(1, 2, 15, 15));
        
        // إطار رمادي بسيط
        accountsContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                "Account Creation Forms",
                TitledBorder.LEADING, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.BLACK 
        )); 

        accountsContainer.add(createAccountForm("Savings", new Color(153, 204, 255))); 
        accountsContainer.add(createAccountForm("Checking", new Color(255, 204, 153)));
        
        formsPanel.add(accountsContainer);
        
        splitPane.setTopComponent(formsPanel);

        // 2. لوحة الجدول (Table Panel)
        try {
            // ملاحظة: يجب تعديل CustomerTableModel.java ليعكس الأعمدة الأصلية فقط
            customerTableModel = new CustomerTableModel(bankSystem);
            customerTable = new JTable(customerTableModel);
            customerTable.setAutoCreateRowSorter(true); 
            customerTable.setRowHeight(30); 
            
            // **تم حذف منطق تطبيق BalanceStatusRenderer**

        } catch (Exception e) {
             customerTable = new JTable(new String[][]{{"Error", "Missing Model or BankSystem Method"}}, new String[]{"Status", "Detail"});
        }
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                "📋 Registered Customers List",
                TitledBorder.LEADING, 
                TitledBorder.TOP, 
                borderFont));
            
        tablePanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        splitPane.setBottomComponent(tablePanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerForm() {
        JPanel form = new JPanel(new GridBagLayout()); 
        form.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID يتم توليده تلقائياً (غير قابل للتعديل)
        JTextField idField = new JTextField("GENERATED_ID", 15);
        idField.setEditable(false); 
        idField.setBackground(new Color(240, 240, 240)); 
        
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        
        // دمج رمز البلد مع حقل الهاتف
        String[] countryCodes = {"+966", "+970", "+962", "+90", "+20", "+971"};
        JComboBox<String> codeBox = new JComboBox<>(countryCodes);
        codeBox.setSelectedItem("+966");
        JTextField phoneField = new JTextField(15);
        
        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.add(codeBox, BorderLayout.WEST);
        phonePanel.add(phoneField, BorderLayout.CENTER);
        
        
        JButton createBtn = new JButton("Create Customer");
        createBtn.setBackground(new Color(102, 178, 255));
        createBtn.setForeground(Color.WHITE);

        // بناء الواجهة
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; form.add(new JLabel("Customer ID (Auto):"), gbc); 
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; form.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3; form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7; form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3; form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7; form.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3; form.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7; form.add(phonePanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weighty = 1.0; 
        gbc.anchor = GridBagConstraints.SOUTH; 

        form.add(createBtn, gbc);

        // منطق الزر (مع التحقق)
        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim(); 
                String code = (String) codeBox.getSelectedItem();
                String localPhone = phoneField.getText().trim();
                String fullPhone = code + localPhone; 

                // 1. التحقق من إلزامية جميع الحقول
                if (name.isEmpty() || email.isEmpty() || localPhone.isEmpty()) {
                    showError("All fields (Name, Email, Phone) are required.");
                    return;
                }
                
                // 2. التحقق من رقم الهاتف: يجب أن يكون الرقم المحلي 10 أرقام بالضبط
                if (!localPhone.matches("\\d{10}")) {
                    showError("Invalid Phone Number. Local number must contain exactly 10 digits.");
                    return;
                }
                
                // 3. التحقق من الإيميل: @gmail.com فقط
                if (!email.toLowerCase().matches(".*@gmail\\.com")) {
                    showError("Invalid Email Domain. Only @gmail.com is allowed.");
                    return;
                }
                
                // توليد ID فريد
                String newAutoId = bankSystem.generateUniqueCustomerId(); 
                
                // إضافة العميل
                bankSystem.addCustomer(new Customer(newAutoId, name, email, fullPhone));
                showSuccess("Customer " + name + " created successfully! ID: " + newAutoId + " | Phone: " + fullPhone);
                
                // تحديث الواجهة بعد الإضافة
                idField.setText(newAutoId); 
                if (customerTableModel != null) customerTableModel.fireTableDataChanged();
                
                // تحديث الـ ComboBox في نماذج الحسابات
                String[] updatedOptions = getCustomerOptions();
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(updatedOptions);

                if (savingsCustomerBox != null) {
                    savingsCustomerBox.setModel(model);
                }
                if (checkingCustomerBox != null) {
                    checkingCustomerBox.setModel(model);
                }
                
                nameField.setText(""); emailField.setText(""); phoneField.setText("");
                
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        
        return form;
    }

    private JPanel createAccountForm(String type, Color bgColor) {
        // نستخدم BorderLayout هنا لضمان أن الزر يبقى في الأعلى بشكل منفصل
        JPanel form = new JPanel(new BorderLayout()); 
        form.setBorder(BorderFactory.createTitledBorder(type + " Account"));
        form.setBackground(bgColor.brighter());

        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // استخدام JComboBox وعرض قائمة العملاء
        JComboBox<String> customerBox = new JComboBox<>(getCustomerOptions()); 
        if (customerBox.getItemCount() == 0) {
            customerBox.addItem("No Customers Registered");
        }
        
        // تعيين الـ ComboBox للخاصية العامة لتحديثه لاحقاً
        if (type.equals("Savings")) {
            this.savingsCustomerBox = customerBox;
        } else if (type.equals("Checking")) {
            this.checkingCustomerBox = customerBox;
        }
        
        JTextField accField = new JTextField(15);
        JTextField balField = new JTextField("0.00", 15);
        
        int row = 0;
        
        // الصف 1: Customer ID (ComboBox)
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
        detailsPanel.add(new JLabel("Select Customer ID:"), gbc); 
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.7;
        detailsPanel.add(customerBox, gbc);

        // الصف 2: Account Number (إدخال يدوي)
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
        detailsPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.7;
        detailsPanel.add(accField, gbc);

        // الصف 3: Initial Balance
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
        detailsPanel.add(new JLabel("Initial Balance:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.7;
        detailsPanel.add(balField, gbc);
        
        // فاصل لضمان أن اللوحة تشغل الارتفاع
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; 
        detailsPanel.add(new JLabel(""), gbc); 

        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل والإنشاء (Toggle and Create Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("Create " + type + " Account ▼");
        toggleBtn.setBackground(bgColor.darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        // إضافة زر التبديل واللوحة إلى الفورم الرئيسي
        form.add(toggleBtn, BorderLayout.NORTH); 
        form.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Create " + type + " Account ▼"); 
            form.revalidate();
            form.repaint();
        });

        // =========================================================
        // 3. زر تأكيد الإنشاء (Confirm Creation Button inside detailsPanel)
        // =========================================================
        JButton createBtn = new JButton("Confirm Creation"); 
        createBtn.setBackground(new Color(50, 150, 50));
        createBtn.setForeground(Color.WHITE);
        
        // وضع زر الإنشاء أسفل الحقول
        GridBagConstraints createGbc = new GridBagConstraints();
        createGbc.insets = new Insets(10, 5, 5, 5);
        createGbc.gridx = 0; createGbc.gridy = row++; createGbc.gridwidth = 2;
        createGbc.fill = GridBagConstraints.HORIZONTAL;
        createGbc.weighty = 0.0;
        detailsPanel.add(createBtn, createGbc); 

        // منطق زر الإنشاء (Confirmation Logic)
        createBtn.addActionListener(e -> {
            try {
                String selectedCustomer = (String) customerBox.getSelectedItem();
                if (selectedCustomer == null || selectedCustomer.equals("No Customers Registered")) {
                    showError("Please select a customer.");
                    return;
                }
                String customerId = selectedCustomer.split(" - ")[0]; 
                
                String accNum = accField.getText().trim();
                String balText = balField.getText().trim();

                if (accNum.isEmpty()) {
                    showError("Account Number is required.");
                    return;
                }
                if (bankSystem.accountExists(accNum)) {
                    showError("Account already exists");
                    return;
                }
                
                BigDecimal amount = new BigDecimal(balText);
                if (amount.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();

                Customer owner = bankSystem.getCustomer(customerId);

                if (type.equals("Savings")) {
                    bankSystem.addAccount(new SavingsAccount(accNum, owner, amount));
                } else if (type.equals("Checking")) {
                    bankSystem.addAccount(new CheckingAccount(accNum, owner, amount));
                }
                
                // ملاحظة: بما أننا عدنا إلى الإدخال اليدوي، يجب أن يتم تحديث Customer.java يدوياً
                // إذا أردت لاحقاً عرض أرقام الحسابات في الجدول.
                owner.setAccountNumber(type, accNum); 

                showSuccess(type + " account " + accNum + " created!");
                
                // تحديث محتويات الجدول بالكامل
                if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 

                accField.setText(""); 
                balField.setText("0.00");
                
                // إخفاء اللوحة بعد النجاح
                detailsPanel.setVisible(false);
                toggleBtn.setSelected(false);
                toggleBtn.setText("Create " + type + " Account ▼");
                form.revalidate();
                
            } catch (NumberFormatException ex) {
                showError("Invalid initial balance or format.");
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        return form;
    }
    
    // =========================================================
    // 2. Transactions Panel (العمليات المالية)
    // =========================================================

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(createDepositForm());
        panel.add(createWithdrawForm());
        panel.add(createTransferForm()); 

        return panel;
    }
    
    private JPanel createDepositForm() {
        // نستخدم BorderLayout للزر في الأعلى واللوحة في الوسط
        JPanel form = new JPanel(new BorderLayout()); 
        form.setBorder(BorderFactory.createTitledBorder("📥 Deposit Money"));
        form.setBackground(new Color(153, 204, 255).brighter()); // لون خلفية لتوضيح الحدود

        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField accField = new JTextField(15);
        JTextField amtField = new JTextField("0.00", 15);

        int row = 0;

        // الصف 1: Account Number
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(accField, gbc);

        // الصف 2: Amount
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("Amount to Deposit:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(amtField, gbc);

        // زر الإيداع
        JButton depositBtn = new JButton("Deposit");
        depositBtn.setBackground(new Color(50, 180, 50)); 
        depositBtn.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5); 
        gbc.weighty = 0.0; 
        detailsPanel.add(depositBtn, gbc);
        
        // إضافة فاصل فارغ لملء المساحة المتبقية ودفعها للأسفل
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; 
        detailsPanel.add(new JLabel(""), gbc); 

        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل (Toggle Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("Deposit Money ▼");
        toggleBtn.setBackground(new Color(50, 180, 50).darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        // إضافة زر التبديل واللوحة إلى الفورم الرئيسي
        form.add(toggleBtn, BorderLayout.NORTH); 
        form.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Deposit Money ▼"); 
            form.revalidate();
            form.repaint();
        });
        
        // منطق زر الإيداع
        depositBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            BankAccount account = bankSystem.getAccount(accNum);
            if (account == null) {
                showError("Account not found!");
                return;
            }
            try {
                BigDecimal amount = new BigDecimal(amtField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
                account.deposit(amount);
                showSuccess("Deposit successful! New balance: " + account.getBalance());
                
                // **الإصلاح: تحديث محتويات الجدول بالكامل**
                if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 

                // إخفاء اللوحة بعد النجاح
                detailsPanel.setVisible(false);
                toggleBtn.setSelected(false);
                toggleBtn.setText("Deposit Money ▼");
                form.revalidate();
                
            } catch (NumberFormatException ex) {
                showError("Invalid or zero amount.");
            }
        });
        return form;
    }

    private JPanel createWithdrawForm() {
        // نستخدم BorderLayout للزر في الأعلى واللوحة في الوسط
        JPanel form = new JPanel(new BorderLayout()); 
        form.setBorder(BorderFactory.createTitledBorder("📤 Withdraw Money"));
        form.setBackground(new Color(255, 204, 153).brighter());

        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField accField = new JTextField(15);
        JTextField amtField = new JTextField("0.00", 15);

        int row = 0;

        // الصف 1: Account Number
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(accField, gbc);

        // الصف 2: Amount
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("Amount to Withdraw:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(amtField, gbc);

        // زر السحب
        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBackground(new Color(255, 153, 0)); 
        withdrawBtn.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5); 
        gbc.weighty = 0.0; 
        detailsPanel.add(withdrawBtn, gbc);
        
        // إضافة فاصل فارغ لملء المساحة المتبقية ودفعها للأسفل
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; 
        detailsPanel.add(new JLabel(""), gbc); 

        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل (Toggle Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("Withdraw Money ▼");
        toggleBtn.setBackground(new Color(255, 153, 0).darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        // إضافة زر التبديل واللوحة إلى الفورم الرئيسي
        form.add(toggleBtn, BorderLayout.NORTH); 
        form.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Withdraw Money ▼"); 
            form.revalidate();
            form.repaint();
        });

        // منطق زر السحب
        withdrawBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            BankAccount account = bankSystem.getAccount(accNum);
            if (account == null) {
                showError("Account not found!");
                return;
            }
            try {
                BigDecimal amount = new BigDecimal(amtField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
                
                boolean ok = account.withdraw(amount);
                if (ok) {
                    showSuccess("Withdraw successful! New balance: " + account.getBalance());
                    // **الإصلاح: تحديث محتويات الجدول بالكامل**
                    if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 
                }
                else showError("Withdraw failed (insufficient balance or overdraft limit).");
                
            } catch (NumberFormatException ex) {
                showError("Invalid or zero amount.");
            }
        });
        return form;
    }
    
    private JPanel createTransferForm() {
        // نستخدم BorderLayout للزر في الأعلى واللوحة في الوسط
        JPanel form = new JPanel(new BorderLayout()); 
        form.setBorder(BorderFactory.createTitledBorder("🔄 Account Transfer"));
        form.setBackground(new Color(153, 204, 255).brighter());

        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField fromAccField = new JTextField(15);
        JTextField toAccField = new JTextField(15);
        JTextField amtField = new JTextField("0.00", 15);

        int row = 0;
        
        // الصف 1: From Account
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("From Account:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(fromAccField, gbc);
        
        // الصف 2: To Account
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("To Account:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(toAccField, gbc);
        
        // الصف 3: Amount
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
        detailsPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.6;
        detailsPanel.add(amtField, gbc);

        // زر التحويل
        JButton transferBtn = new JButton("Transfer");
        transferBtn.setBackground(new Color(0, 153, 204)); 
        transferBtn.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5); 
        gbc.weighty = 0.0; 
        detailsPanel.add(transferBtn, gbc);
        
        // إضافة فاصل فارغ لملء المساحة المتبقية ودفعها للأسفل
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; 
        detailsPanel.add(new JLabel(""), gbc); 

        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل (Toggle Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("Account Transfer ▼");
        toggleBtn.setBackground(new Color(0, 153, 204).darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        // إضافة زر التبديل واللوحة إلى الفورم الرئيسي
        form.add(toggleBtn, BorderLayout.NORTH); 
        form.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Account Transfer ▼"); 
            form.revalidate();
            form.repaint();
        });
        
        // منطق زر التحويل
        transferBtn.addActionListener(e -> {
            String fromAcc = fromAccField.getText().trim();
            String toAcc = toAccField.getText().trim();
            
            if (fromAcc.isEmpty() || toAcc.isEmpty() || fromAcc.equals(toAcc)) {
                showError("Please enter valid source and destination accounts.");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amtField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showError("Invalid transfer amount.");
                return;
            }
            
            boolean success = bankSystem.transfer(fromAcc, toAcc, amount);
            
            if (success) {
                showSuccess(String.format("Transfer of %.2f successful!", amount));
                fromAccField.setText("");
                toAccField.setText("");
                amtField.setText("0.00");
                
                // **الإصلاح: تحديث محتويات الجدول بالكامل**
                if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 

                // إخفاء اللوحة بعد النجاح
                detailsPanel.setVisible(false);
                toggleBtn.setSelected(false);
                toggleBtn.setText("Account Transfer ▼");
                form.revalidate();
                
            } else {
                showError("Transfer failed. Check account numbers, balance, and limits.");
            }
        });
        return form;
    }
    
    // =========================================================
    // 3. Service Queue Panel 
    // =========================================================

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createServiceRequestForm());

        panel.add(createHistoryViewPanel());
        
        return panel;
    }

    private JPanel createServiceRequestForm() {
        // نستخدم BorderLayout للزر في الأعلى واللوحة في الوسط
        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createTitledBorder("🔔 Manage Service Requests"));
        form.setBackground(new Color(230, 230, 255)); // لون خلفية لتوضيح الحدود
        
        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField cidField = new JTextField(15);
        
        String[] issueKeys = ISSUE_PRIORITIES.keySet().toArray(new String[0]);
        JComboBox<String> issueSelector = new JComboBox<>(issueKeys);
        if (issueKeys.length > 0) issueSelector.setSelectedItem(issueKeys[0]);

        int row = 0;
        
        // الصف 1: Customer ID
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3; detailsPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.7; detailsPanel.add(cidField, gbc);
        
        // الصف 2: Issue Selector
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3; detailsPanel.add(new JLabel("Select Issue:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1; gbc.weightx = 0.7; detailsPanel.add(issueSelector, gbc);
        
        // الأزرار: Add New Request / Serve Next Request
        JButton addReqBtn = new JButton("Add New Request");
        addReqBtn.setBackground(new Color(153, 51, 255));
        addReqBtn.setForeground(Color.WHITE);
        
        JButton serveReqBtn = new JButton("Serve Next Request");
        serveReqBtn.setBackground(new Color(100, 100, 100));
        serveReqBtn.setForeground(Color.WHITE);
        
        // وضع الأزرار في الأسفل
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(10, 5, 5, 5); gbc.weighty = 0.0; detailsPanel.add(addReqBtn, gbc);

        gbc.gridy = row++; detailsPanel.add(serveReqBtn, gbc);
        
        // فاصل لملء المساحة ودفع المحتوى للأسفل
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; 
        detailsPanel.add(new JLabel(""), gbc); 
        
        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل (Toggle Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("Manage Service Requests ▼");
        toggleBtn.setBackground(new Color(153, 51, 255).darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        form.add(toggleBtn, BorderLayout.NORTH); 
        form.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Manage Service Requests ▼"); 
            form.revalidate();
            form.repaint();
        });
        
        // منطق زر الإضافة
        addReqBtn.addActionListener(e -> {
            try {
                String cidText = cidField.getText().trim();
                if (!bankSystem.customerExists(cidText)) {
                    showError("Customer not found");
                    return;
                }
                
                String selectedIssue = (String) issueSelector.getSelectedItem();
                int priority = ISSUE_PRIORITIES.getOrDefault(selectedIssue, 3); 
                
                bankSystem.addServiceRequest(new ServiceRequest(cidText, selectedIssue, priority));
                showSuccess("Request added for: " + selectedIssue + " (Priority: " + priority + ")");
                
            } catch (Exception ex) {
                showError("Error adding request: " + ex.getMessage());
            }
        });

        // منطق زر الخدمة
        serveReqBtn.addActionListener(e -> {
            if (!bankSystem.hasRequests()) {
                showError("No pending requests.");
                return;
            }
            ServiceRequest req = bankSystem.serveNextRequest();
            String msg = String.format("Serving Request%n%nCustomer ID: %s%nIssue: %s%nPriority: %d",
                    req.getCustomerId(), req.getIssue(), req.getPriority());
            showSuccess(msg);
        });
        
        return form;
    }

    private JPanel createHistoryViewPanel() {
        // نستخدم BorderLayout للزر في الأعلى واللوحة في الوسط
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📊 Transaction History Viewer"));
        panel.setBackground(new Color(255, 255, 230)); // لون خلفية لتوضيح الحدود

        // =========================================================
        // 1. لوحة تفاصيل الإدخال (التي سيتم طيها/توسيعها)
        // =========================================================
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));

        JTextField accField = new JTextField();
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18)); // الخط الكبير
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JButton viewBtn = new JButton("View History");
        viewBtn.setBackground(new Color(0, 153, 76));
        viewBtn.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Account No:"), BorderLayout.WEST);
        topPanel.add(accField, BorderLayout.CENTER);
        topPanel.add(viewBtn, BorderLayout.EAST);
        
        detailsPanel.add(topPanel, BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        
        // إخفاء لوحة التفاصيل مبدئياً
        detailsPanel.setVisible(false); 
        
        // =========================================================
        // 2. زر التبديل (Toggle Button)
        // =========================================================
        JToggleButton toggleBtn = new JToggleButton("View Transaction History ▼");
        toggleBtn.setBackground(new Color(0, 153, 76).darker());
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

        panel.add(toggleBtn, BorderLayout.NORTH); 
        panel.add(detailsPanel, BorderLayout.CENTER); 

        // منطق تبديل الرؤية
        toggleBtn.addActionListener(e -> {
            detailsPanel.setVisible(toggleBtn.isSelected());
            toggleBtn.setText(toggleBtn.isSelected() ? "Close Viewer ▲" : "View Transaction History ▼"); 
            panel.revalidate();
            panel.repaint();
        });

        // منطق زر عرض السجل
        viewBtn.addActionListener(e -> {
            String acc = accField.getText().trim();
            BankAccount account = bankSystem.getAccount(acc);
            if (account == null) {
                showError("Account not found");
                historyArea.setText("");
                return;
            }

            StringBuilder sb = new StringBuilder("TRANSACTION HISTORY for " + acc + "\n\n");
            List<Transaction> list = account.getTransactions();
            if (list.isEmpty()) {
                sb.append("No transactions found.");
            } else {
                sb.append(String.format("%-18s | %-15s | %s%n", "Type", "Amount", "Date/Time"));
                sb.append("------------------------------------------------------------\n");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (Transaction t : list) {
                    sb.append(String.format("%-18s | %-15s | %s%n",
                              t.getType(), t.getAmount().toPlainString(), t.getDate().format(fmt)));
                }
            }
            historyArea.setText(sb.toString());
        });

        return panel;
    }
}