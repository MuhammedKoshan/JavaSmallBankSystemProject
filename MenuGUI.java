package BankSystem;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import java.time.format.DateTimeFormatter;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;

public class MenuGUI extends JFrame {

    private final BankSystem bankSystem;
    private JTabbedPane tabbedPane;

    private static final String LOGO_PATH = "C:/Windows/Screenshot 2025-12-10 214702.png"; 

    private CustomerTableModel customerTableModel; 
    private JTable customerTable;
    
    private JComboBox<String> savingsCustomerBox;
    private JComboBox<String> checkingCustomerBox;
    
    private final Map<String, Integer> ISSUE_PRIORITIES = Map.of(
        "Lost Card / Stolen Card", 1,
        "Withdrawal/Deposit Error", 2,
        "Account Information Update", 3,
        "General Inquiry", 3
    );
    private String showAccountSearchDialog() {
        JDialog dialog = new JDialog(this, "Select Bank Account", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // شريط البحث
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField searchField = new JTextField();
        searchPanel.add(new JLabel("Search (Name/Acc No): "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // الجدول
        String[] columns = {"Account No", "Owner", "Type", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // دالة التحديث
        Runnable refreshTable = () -> {
            model.setRowCount(0);
            String txt = searchField.getText().toLowerCase();
            // نفترض وجود دالة getAllAccounts في bankSystem أو الوصول عبر الـ customers
            bankSystem.getCustomers().values().forEach(customer -> {
                // فحص حساب التوفير
                if (customer.getSavingsAccNum() != null) {
                    if (customer.getName().toLowerCase().contains(txt) || customer.getSavingsAccNum().contains(txt)) {
                        BankAccount acc = bankSystem.getAccount(customer.getSavingsAccNum());
                        model.addRow(new Object[]{customer.getSavingsAccNum(), customer.getName(), "Savings", acc.getBalance() + " $"});
                    }
                }
                // فحص الحساب الجاري
                if (customer.getCheckingAccNum() != null) {
                    if (customer.getName().toLowerCase().contains(txt) || customer.getCheckingAccNum().contains(txt)) {
                        BankAccount acc = bankSystem.getAccount(customer.getCheckingAccNum());
                        model.addRow(new Object[]{customer.getCheckingAccNum(), customer.getName(), "Checking", acc.getBalance() + " $"});
                    }
                }
            });
        };

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { refreshTable.run(); }
        });
        refreshTable.run();

        JButton selectBtn = new JButton("Select This Account");
        final String[] selectedAcc = {null};
        selectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedAcc[0] = (String) model.getValueAt(row, 0);
                dialog.dispose();
            }
        });

        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(selectBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
        return selectedAcc[0];
    }
    public MenuGUI(BankSystem bankSystem) {
        this.bankSystem = bankSystem;

        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH); 
            if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.err.println("Failed to load window icon from path: " + LOGO_PATH);
        }

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

    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45); 

        Font borderFont = new Font("Arial", Font.BOLD, 20); 

        JPanel formsPanel = new JPanel(); 
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS)); 
        formsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                "🆕 Customer & Account Creation", 
                TitledBorder.LEADING, 
                TitledBorder.TOP, 
                borderFont));

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
        
        JPanel accountsContainer = new JPanel(new GridLayout(1, 2, 15, 15));
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

        try {
            customerTableModel = new CustomerTableModel(bankSystem);
            customerTable = new JTable(customerTableModel);
            customerTable.setAutoCreateRowSorter(true); 
            customerTable.setRowHeight(30); 
        } catch (Exception e) {
             customerTable = new JTable(new String[][]{{"Error", "Missing Model"}}, new String[]{"Status", "Detail"});
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
 // دالة لتحديث الجداول والقوائم في كل التبويبات بعد أي عملية إضافة أو تغيير
    private void updateCustomerTablesAndBoxes() {
        // 1. تحديث بيانات الجدول الرئيسي للعملاء
        if (customerTableModel != null) {
            customerTableModel.fireTableDataChanged();
        }
        
        // 2. تجهيز قائمة الخيارات الجديدة (الأسماء والأرقام المحدثة)
        String[] updatedOptions = getCustomerOptions();
        DefaultComboBoxModel<String> savingsModel = new DefaultComboBoxModel<>(updatedOptions);
        DefaultComboBoxModel<String> checkingModel = new DefaultComboBoxModel<>(updatedOptions);

        // 3. تحديث القوائم المنسدلة في تبويبات إنشاء الحسابات
        if (savingsCustomerBox != null) {
            savingsCustomerBox.setModel(savingsModel);
        }
        if (checkingCustomerBox != null) {
            checkingCustomerBox.setModel(checkingModel);
        }
    }	
 private JPanel createCustomerForm() {
    // 1. إنشاء لوحة خارجية (Outer Panel) وظيفتها فقط توسيط ما بداخلها
    JPanel outerPanel = new JPanel(new GridBagLayout());
    outerPanel.setOpaque(false); // للحفاظ على خلفية البرنامج الأصلية

    // 2. إنشاء اللوحة الأساسية للنموذج (Form Panel)
    JPanel form = new JPanel(new GridBagLayout()); 
    form.setBorder(BorderFactory.createTitledBorder("Customer Details"));
    GridBagConstraints gbc = new GridBagConstraints();
    
    // إعدادات المسافات الداخلية والمحاذاة
    gbc.insets = new Insets(10, 15, 10, 15); 
    gbc.anchor = GridBagConstraints.WEST; 
    gbc.fill = GridBagConstraints.HORIZONTAL; 

    // --- تعريف الحقول ---
    JTextField idField = new JTextField(bankSystem.generateAutomaticID(), 30); 
    idField.setEditable(false); 
    idField.setBackground(new Color(235, 240, 250)); 
    idField.setFont(new Font("Monospaced", Font.BOLD, 14));

    JTextField nameField = new JTextField(25); 
    JTextField emailField = new JTextField(25);
    
    JComboBox<String> codeBox = new JComboBox<>(new String[]{"+963", "+90", "+962", "+20", "+971"});
    JTextField phoneField = new JTextField(15);
    JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
    phonePanel.setOpaque(false);
    phonePanel.add(codeBox, BorderLayout.WEST);
    phonePanel.add(phoneField, BorderLayout.CENTER);
    
    JButton createBtn = new JButton("Create Customer");
    createBtn.setPreferredSize(new Dimension(180, 40));
    createBtn.setBackground(new Color(100, 160, 240));
    createBtn.setForeground(Color.WHITE);
    createBtn.setFont(new Font("Arial", Font.BOLD, 14));

    // --- توزيع العناصر داخل لوحة الـ form ---
    gbc.gridx = 0; gbc.gridy = 0;
    form.add(new JLabel("Generated Customer ID (TR):"), gbc);
    gbc.gridx = 1;
    form.add(idField, gbc);

    gbc.gridx = 0; gbc.gridy = 1;
    form.add(new JLabel("Name:"), gbc);
    gbc.gridx = 1;
    form.add(nameField, gbc);

    gbc.gridx = 0; gbc.gridy = 2;
    form.add(new JLabel("Email:"), gbc);
    gbc.gridx = 1;
    form.add(emailField, gbc);

    gbc.gridx = 0; gbc.gridy = 3;
    form.add(new JLabel("Phone:"), gbc);
    gbc.gridx = 1;
    form.add(phonePanel, gbc);
    
    // توسيط زر الإنشاء في الأسفل
    gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER; 
    gbc.insets = new Insets(25, 15, 15, 15);
    form.add(createBtn, gbc);

    // =========================================================
    // 🛑 منطق التحقق (Smart Validation) وتغيير الألوان 🛑
    // =========================================================
    createBtn.addActionListener(e -> {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim().toLowerCase(); 
            String phoneRaw = phoneField.getText().trim();
            String fullPhone = codeBox.getSelectedItem() + phoneRaw;

            StringBuilder errorMsg = new StringBuilder("Invalid Input!\n");
            boolean hasError = false;

            // التحقق من الاسم
            if (name.isEmpty()) {
                flashField(nameField, new Color(255, 200, 200));
                errorMsg.append("- Name cannot be empty.\n");
                hasError = true;
            }

            // التحقق من الإيميل
            if (!email.endsWith("@gmail.com") || email.length() <= 10) {
                flashField(emailField, new Color(255, 200, 200));
                errorMsg.append("- Email must be @gmail.com\n");
                hasError = true;
            }

            // التحقق من الهاتف (10 أرقام)
            if (phoneRaw.length() != 10 || !phoneRaw.matches("\\d+")) {
                flashField(phoneField, new Color(255, 200, 200));
                errorMsg.append("- Phone must be 10 digits\n");
                hasError = true;
            }

            if (hasError) {
                showError(errorMsg.toString());
                return;
            }
            
            // في حال النجاح
            bankSystem.addCustomer(new Customer(idField.getText(), name, email, fullPhone));
            idField.setBackground(new Color(200, 255, 200)); // لون أخضر للنجاح
            
            showSuccess("Customer successfully registered!");
            
            // إعادة ضبط الحقول
            Timer resetTimer = new Timer(1500, ev -> {
                idField.setBackground(new Color(235, 240, 250));
                idField.setText(bankSystem.generateAutomaticID());
                nameField.setText(""); emailField.setText(""); phoneField.setText("");
            });
            resetTimer.setRepeats(false); resetTimer.start();
            updateCustomerTablesAndBoxes();
            
        } catch (Exception ex) {
            showError("System Error: " + ex.getMessage());
        }
    });

    // 3. وضع لوحة الـ form داخل الـ outerPanel (التوسيط السحري)
    outerPanel.add(form, new GridBagConstraints());
    
    return outerPanel;
}
// دالة مساعدة لعمل وميض (Flash) للألوان عند الخطأ
private void flashField(JTextField field, Color flashColor) {
    Color originalColor = field.getBackground();
    field.setBackground(flashColor);
    Timer timer = new Timer(1000, e -> field.setBackground(originalColor));
    timer.setRepeats(false);
    timer.start();
}
    private String showCustomerSearchDialog() {
        // 1. إنشاء نافذة منبثقة (Dialog)
        JDialog dialog = new JDialog(this, "Search & Select Customer", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // 2. شريط البحث العلوي
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField searchField = new JTextField();
        searchPanel.add(new JLabel("Search Name or ID: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // 3. جدول لعرض النتائج
        String[] columns = {"ID", "Name", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // 4. دالة لتحديث الجدول بناءً على البحث
        Runnable refreshTable = () -> {
            model.setRowCount(0);
            String searchText = searchField.getText().toLowerCase();
            for (Customer c : bankSystem.getCustomers().values()) {
                if (c.getName().toLowerCase().contains(searchText) || c.getCustomerId().contains(searchText)) {
                    model.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone()});
                }
            }
        };

        // تحديث البحث عند الكتابة
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { refreshTable.run(); }
        });
        refreshTable.run(); // تحميل البيانات لأول مرة

        // 5. زر التأكيد (Select)
        final String[] selectedId = {null};
        JButton selectBtn = new JButton("Select Customer");
        selectBtn.setBackground(new Color(40, 140, 40));
        selectBtn.setForeground(Color.WHITE);
        selectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId[0] = (String) model.getValueAt(row, 0); // جلب الـ ID
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a customer from the table.");
            }
        });

        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(selectBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
        return selectedId[0]; // تعيد الـ ID المختار أو null إذا أغلق النافذة
    }
private JPanel createAccountForm(String type, Color bgColor) {
    JPanel form = new JPanel(new BorderLayout()); 
    form.setBorder(BorderFactory.createTitledBorder(type + " Account"));
    form.setBackground(bgColor.brighter());

    JPanel detailsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // --- 1. إعداد نظام اختيار العميل (Browse) ---
    final String[] currentSelectedID = {null}; 
    JTextField selectedCustomerDisplay = new JTextField("Click Browse to select...", 15);
    selectedCustomerDisplay.setEditable(false);
    selectedCustomerDisplay.setBackground(new Color(245, 245, 245));
    
    JButton browseBtn = new JButton("🔍 Browse");
    browseBtn.addActionListener(e -> {
        String id = showCustomerSearchDialog(); //
        if (id != null) {
            currentSelectedID[0] = id;
            Customer c = bankSystem.getCustomers().get(id); //
            String shortId = id.length() > 4 ? id.substring(id.length() - 4) : id;
            selectedCustomerDisplay.setText(c.getName() + " (ID: ..." + shortId + ")");
            selectedCustomerDisplay.setBackground(new Color(220, 255, 220)); 
        }
    });

    JPanel selectionPanel = new JPanel(new BorderLayout(5, 0));
    selectionPanel.setOpaque(false);
    selectionPanel.add(selectedCustomerDisplay, BorderLayout.CENTER);
    selectionPanel.add(browseBtn, BorderLayout.EAST);

    // --- 2. إعداد حقل رقم الحساب (توليد تلقائي يبدأ بـ 25) ---
    JTextField accField = new JTextField(bankSystem.generateRandomAccountNumber(), 15); //
    accField.setEditable(false); 
    accField.setBackground(new Color(235, 235, 235));
    accField.setFont(new Font("Monospaced", Font.BOLD, 13));

    // --- 3. حقل الرصيد ---
    JTextField balField = new JTextField("0.00", 15);
    
    // --- 4. توزيع العناصر في الشبكة ---
    int row = 0;
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("Customer:"), gbc); 
    gbc.gridx = 1; detailsPanel.add(selectionPanel, gbc);

    gbc.gridx = 0; gbc.gridy = row++; 
    detailsPanel.add(new JLabel("Account Number:"), gbc);
    gbc.gridx = 1; detailsPanel.add(accField, gbc);

    gbc.gridx = 0; gbc.gridy = row++; 
    detailsPanel.add(new JLabel("Initial Balance ($):"), gbc);
    gbc.gridx = 1; detailsPanel.add(balField, gbc);
    
    detailsPanel.setVisible(false); 

    // --- 5. زر التبديل (Toggle) ---
    JToggleButton toggleBtn = new JToggleButton("Create " + type + " Account ▼");
    toggleBtn.setBackground(bgColor.darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 15)); 

    toggleBtn.addActionListener(e -> {
        if (toggleBtn.isSelected()) {
            accField.setText(bankSystem.generateRandomAccountNumber());
        }
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Create " + type + " Account ▼"); 
        form.revalidate();
    });

    // --- 6. زر التأكيد (Confirm) مع منطق منع تكرار الحساب ---
    JButton createBtn = new JButton("Confirm & Save Account"); 
    createBtn.setBackground(new Color(40, 140, 40));
    createBtn.setForeground(Color.WHITE);
    
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
    detailsPanel.add(createBtn, gbc); 

    createBtn.addActionListener(e -> {
        try {
            String customerId = currentSelectedID[0]; 
            if (customerId == null) {
                showError("Please select a customer first!");
                return;
            }

            Customer owner = bankSystem.getCustomer(customerId); //

            // ========================================================
            // 🛑 التحقق من ملكية حساب مسبق من نفس النوع 🛑
            // ========================================================
            if (type.equals("Savings") && owner.getSavingsAccNum() != null) { //
                showError("please! This customer already has a Savings Account.");
                flashField(selectedCustomerDisplay, new Color(255, 200, 200)); 
                return;
            }

            if (type.equals("Checking") && owner.getCheckingAccNum() != null) { //
                showError("please! This customer already has a Checking Account.");
                flashField(selectedCustomerDisplay, new Color(255, 200, 200)); 
                return;
            }
            // ========================================================

            String accNum = accField.getText().trim();
            BigDecimal amount = new BigDecimal(balField.getText().trim());

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                showError("Initial balance cannot be negative.");
                return;
            }

            // تنفيذ الإضافة وتحديث رقم الحساب في كائن العميل
            if (type.equals("Savings")) {
                bankSystem.addAccount(new SavingsAccount(accNum, owner, amount)); //
                owner.setAccountNumber("Savings", accNum); //
            } else {
                bankSystem.addAccount(new CheckingAccount(accNum, owner, amount)); //
                owner.setAccountNumber("Checking", accNum); //
            }
            
            showSuccess(type + " account created successfully!");
            updateCustomerTablesAndBoxes(); //
            
            // تنظيف وإغلاق
            detailsPanel.setVisible(false);
            toggleBtn.setSelected(false);
            toggleBtn.setText("Create " + type + " Account ▼");
            currentSelectedID[0] = null;
            selectedCustomerDisplay.setText("Click Browse to select...");
            selectedCustomerDisplay.setBackground(new Color(245, 245, 245));
            form.revalidate();
            
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    });

    form.add(toggleBtn, BorderLayout.NORTH); 
    form.add(detailsPanel, BorderLayout.CENTER); 

    return form;
}
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(createDepositForm());
        panel.add(createWithdrawForm());
        panel.add(createTransferForm()); 
        return panel;
    }
   private JPanel createDepositForm() {
    JPanel form = new JPanel(new BorderLayout()); 
    form.setBorder(BorderFactory.createTitledBorder("📥 Deposit Money"));
    form.setBackground(new Color(153, 204, 255).brighter());

    JPanel detailsPanel = new JPanel(new GridBagLayout()); 
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // --- التعديل هنا: استخدام متغير نهائي لتخزين الرقم المختار ---
    final String[] selectedAccNum = {null};
    JTextField accField = new JTextField(15);
    accField.setEditable(false); // منع الكتابة اليدوية لضمان الدقة
    accField.setBackground(new Color(245, 245, 245));
    
    JButton browseBtn = new JButton("🔍 Browse");
    // تخصيص شكل زر Browse
    browseBtn.setBackground(new Color(200, 200, 200));
    browseBtn.setFocusPainted(false);

    JTextField amtField = new JTextField("0.00", 15);

    // إضافة منطق زر البحث
    browseBtn.addActionListener(e -> {
        String result = showAccountSearchDialog(); // استدعاء نافذة البحث
        if (result != null) {
            selectedAccNum[0] = result;
            accField.setText(result);
            accField.setBackground(new Color(230, 255, 230)); // تغيير اللون للنجاح
        }
    });

    int row = 0;
    // السطر الأول: رقم الحساب مع زر Browse
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("Account:"), gbc);
    
    // وضع الحقل والزر في لوحة واحدة (Panel)
    JPanel accBrowsePanel = new JPanel(new BorderLayout(5, 0));
    accBrowsePanel.setOpaque(false);
    accBrowsePanel.add(accField, BorderLayout.CENTER);
    accBrowsePanel.add(browseBtn, BorderLayout.EAST);
    
    gbc.gridx = 1; gbc.weightx = 0.7;
    detailsPanel.add(accBrowsePanel, gbc);

    // السطر الثاني: المبلغ
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("Amount to Deposit:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.7;
    detailsPanel.add(amtField, gbc);

    // السطر الثالث: زر الإيداع
    JButton depositBtn = new JButton("Confirm Deposit");
    depositBtn.setBackground(new Color(50, 180, 50)); 
    depositBtn.setForeground(Color.WHITE);
    depositBtn.setFont(new Font("Arial", Font.BOLD, 14));
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(15, 5, 5, 5); 
    detailsPanel.add(depositBtn, gbc);
    
    // مصد للفراغ السفلي
    gbc.gridy = row++; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; 
    detailsPanel.add(new JLabel(""), gbc); 

    detailsPanel.setVisible(false); 
    JToggleButton toggleBtn = new JToggleButton("Deposit Money ▼");
    toggleBtn.setBackground(new Color(50, 180, 50).darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

    form.add(toggleBtn, BorderLayout.NORTH); 
    form.add(detailsPanel, BorderLayout.CENTER); 

    toggleBtn.addActionListener(e -> {
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Deposit Money ▼"); 
        form.revalidate(); form.repaint();
    });
    
    depositBtn.addActionListener(e -> {
        try {
            String accNum = accField.getText().trim();
            if (accNum.isEmpty() || accNum.equals("Click Browse...")) {
                showError("Please select an account first!");
                return;
            }
            
            BankAccount account = bankSystem.getAccount(accNum);
            if (account == null) { showError("Account not found!"); return; }
            
            BigDecimal amount = new BigDecimal(amtField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            
            account.deposit(amount);
            showSuccess("Deposit successful!\nNew balance: " + account.getBalance());
            
            // تحديث الجدول وإعادة ضبط الحقول
            if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 
            accField.setText("");
            accField.setBackground(new Color(245, 245, 245));
            amtField.setText("0.00");
            detailsPanel.setVisible(false); 
            toggleBtn.setSelected(false);
            toggleBtn.setText("Deposit Money ▼");
        } catch (NumberFormatException ex) { 
            showError("Please enter a valid positive amount."); 
        } catch (Exception ex) { 
            showError("Error: " + ex.getMessage()); 
        }
    });
    
    return form;
}

   private JPanel createWithdrawForm() {
    JPanel form = new JPanel(new BorderLayout()); 
    form.setBorder(BorderFactory.createTitledBorder("📤 Withdraw Money"));
    form.setBackground(new Color(255, 204, 153).brighter());

    JPanel detailsPanel = new JPanel(new GridBagLayout()); 
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // استخدام Browse بدلاً من الكتابة اليدوية
    JTextField accField = new JTextField(15);
    accField.setEditable(false);
    accField.setBackground(new Color(245, 245, 245));
    JButton browseBtn = new JButton("🔍 Browse");

    JTextField amtField = new JTextField("0.00", 15);

    browseBtn.addActionListener(e -> {
        String result = showAccountSearchDialog(); // دالة البحث التي صممناها
        if (result != null) {
            accField.setText(result);
            accField.setBackground(new Color(255, 245, 230)); // لون خفيف للتمييز
        }
    });

    int row = 0;
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
    detailsPanel.add(new JLabel("Account Number:"), gbc);
    
    // وضع الحقل والزر في لوحة واحدة
    JPanel accBrowsePanel = new JPanel(new BorderLayout(5, 0));
    accBrowsePanel.setOpaque(false);
    accBrowsePanel.add(accField, BorderLayout.CENTER);
    accBrowsePanel.add(browseBtn, BorderLayout.EAST);
    
    gbc.gridx = 1; gbc.weightx = 0.6;
    detailsPanel.add(accBrowsePanel, gbc);

    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.4;
    detailsPanel.add(new JLabel("Amount to Withdraw:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.6;
    detailsPanel.add(amtField, gbc);

    JButton withdrawBtn = new JButton("Withdraw");
    withdrawBtn.setBackground(new Color(255, 153, 0)); 
    withdrawBtn.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
    detailsPanel.add(withdrawBtn, gbc);
    
    gbc.gridy = row++; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; 
    detailsPanel.add(new JLabel(""), gbc); 

    detailsPanel.setVisible(false); 
    JToggleButton toggleBtn = new JToggleButton("Withdraw Money ▼");
    toggleBtn.setBackground(new Color(255, 153, 0).darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

    form.add(toggleBtn, BorderLayout.NORTH); 
    form.add(detailsPanel, BorderLayout.CENTER); 

    toggleBtn.addActionListener(e -> {
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Withdraw Money ▼"); 
        form.revalidate(); form.repaint();
    });

    withdrawBtn.addActionListener(e -> {
        try {
            String accNum = accField.getText().trim();
            if (accNum.isEmpty()) { showError("Please select an account!"); return; }

            BankAccount account = bankSystem.getAccount(accNum);
            if (account == null) { showError("Account not found!"); return; }
            
            BigDecimal amount = new BigDecimal(amtField.getText().trim());
            if (account.withdraw(amount)) {
                showSuccess("Withdraw successful!\nNew balance: " + account.getBalance());
                if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 
                accField.setText(""); amtField.setText("0.00");
                detailsPanel.setVisible(false); toggleBtn.setSelected(false);
            } else {
                showError("Withdraw failed! Check balance or limits.");
            }
        } catch (Exception ex) { showError("Invalid amount format."); }
    });
    return form;
}
    
    private JPanel createTransferForm() {
    JPanel form = new JPanel(new BorderLayout()); 
    form.setBorder(BorderFactory.createTitledBorder("🔄 Account Transfer"));
    form.setBackground(new Color(153, 204, 255).brighter());

    JPanel detailsPanel = new JPanel(new GridBagLayout()); 
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // إعداد حقول الاختيار
    JTextField fromAccField = new JTextField(15); fromAccField.setEditable(false);
    JButton browseFrom = new JButton("🔍 From");
    
    JTextField toAccField = new JTextField(15); toAccField.setEditable(false);
    JButton browseTo = new JButton("🔍 To");

    JTextField amtField = new JTextField("0.00", 15);

    // برمجة أزرار البحث
    browseFrom.addActionListener(e -> {
        String res = showAccountSearchDialog();
        if (res != null) fromAccField.setText(res);
    });

    browseTo.addActionListener(e -> {
        String res = showAccountSearchDialog();
        if (res != null) {
            if (res.equals(fromAccField.getText())) {
                showError("Cannot transfer to the same account!");
                return;
            }
            toAccField.setText(res);
        }
    });

    int row = 0;
    // سطر حساب المرسل
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("From Account:"), gbc);
    JPanel p1 = new JPanel(new BorderLayout(5,0)); p1.setOpaque(false);
    p1.add(fromAccField, BorderLayout.CENTER); p1.add(browseFrom, BorderLayout.EAST);
    gbc.gridx = 1; gbc.weightx = 0.7; detailsPanel.add(p1, gbc);

    // سطر حساب المستقبل
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("To Account:"), gbc);
    JPanel p2 = new JPanel(new BorderLayout(5,0)); p2.setOpaque(false);
    p2.add(toAccField, BorderLayout.CENTER); p2.add(browseTo, BorderLayout.EAST);
    gbc.gridx = 1; detailsPanel.add(p2, gbc);

    // سطر المبلغ
    gbc.gridx = 0; gbc.gridy = row++; detailsPanel.add(new JLabel("Amount:"), gbc);
    gbc.gridx = 1; detailsPanel.add(amtField, gbc);

    JButton transferBtn = new JButton("Transfer Money");
    transferBtn.setBackground(new Color(0, 153, 204)); transferBtn.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; detailsPanel.add(transferBtn, gbc);
    
    gbc.gridy = row++; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; detailsPanel.add(new JLabel(""), gbc); 

    detailsPanel.setVisible(false); 
    JToggleButton toggleBtn = new JToggleButton("Account Transfer ▼");
    toggleBtn.setBackground(new Color(0, 153, 204).darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

    form.add(toggleBtn, BorderLayout.NORTH); form.add(detailsPanel, BorderLayout.CENTER); 
    
    toggleBtn.addActionListener(e -> {
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Account Transfer ▼"); 
        form.revalidate();
    });
    
    transferBtn.addActionListener(e -> {
        try {
            String from = fromAccField.getText();
            String to = toAccField.getText();
            BigDecimal amount = new BigDecimal(amtField.getText().trim());

            if (from.isEmpty() || to.isEmpty()) { showError("Select both accounts!"); return; }

            if (bankSystem.transfer(from, to, amount)) {
                showSuccess("Transfer successful!");
                if (customerTableModel != null) customerTableModel.fireTableDataChanged(); 
                fromAccField.setText(""); toAccField.setText(""); amtField.setText("0.00");
                detailsPanel.setVisible(false); toggleBtn.setSelected(false);
            } else {
                showError("Transfer failed! Check funds in sender account.");
            }
        } catch (Exception ex) { showError("Invalid amount."); }
    });
    return form;
}
    
    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(createServiceRequestForm());
        panel.add(createHistoryViewPanel());
        return panel;
    }
private JPanel createServiceRequestForm() {
    JPanel form = new JPanel(new BorderLayout());
    form.setBorder(BorderFactory.createTitledBorder("🔔 Manage Service Requests"));
    form.setBackground(new Color(230, 230, 255));
    
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 5, 8, 5); 
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // --- 1. إعداد نظام اختيار العميل (Browse) ---
    final String[] selectedCustomerID = {null};
    JTextField cidField = new JTextField(15);
    cidField.setEditable(false); // المستخدم يجب أن يستخدم زر Browse
    cidField.setBackground(new Color(245, 245, 245));
    JButton browseCustBtn = new JButton("🔍 Browse");

    browseCustBtn.addActionListener(e -> {
        String res = showCustomerSearchDialog(); // استدعاء نافذة البحث الخاصة بك
        if (res != null) {
            selectedCustomerID[0] = res;
            Customer c = bankSystem.getCustomers().get(res);
            // تنسيق العرض ليظهر: الاسم (ID: ...الرقم)
            String shortId = res.length() > 4 ? res.substring(res.length() - 4) : res;
            cidField.setText(c.getName() + " (ID: ..." + shortId + ")");
            cidField.setBackground(new Color(230, 255, 230)); // تغيير اللون للأخضر عند الاختيار
        }
    });

    String[] issueKeys = ISSUE_PRIORITIES.keySet().toArray(new String[0]);
    JComboBox<String> issueSelector = new JComboBox<>(issueKeys);

    int row = 0;
    
    // الصف الأول: اختيار العميل
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3; gbc.weighty = 0;
    detailsPanel.add(new JLabel("Customer:"), gbc);
    
    JPanel browsePanel = new JPanel(new BorderLayout(5, 0));
    browsePanel.setOpaque(false);
    browsePanel.add(cidField, BorderLayout.CENTER);
    browsePanel.add(browseCustBtn, BorderLayout.EAST);
    
    gbc.gridx = 1; gbc.weightx = 0.7;
    detailsPanel.add(browsePanel, gbc);

    // الصف الثاني: اختيار المشكلة
    gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.3;
    detailsPanel.add(new JLabel("Select Issue:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.7;
    detailsPanel.add(issueSelector, gbc);
    
    // الصف الثالث: زر إضافة طلب جديد
    JButton addReqBtn = new JButton("Add New Request");
    addReqBtn.setBackground(new Color(153, 51, 255)); 
    addReqBtn.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.weighty = 0;
    detailsPanel.add(addReqBtn, gbc);
    
    // الصف الرابع: زر خدمة الطلب التالي
    JButton serveReqBtn = new JButton("Serve Next Request");
    serveReqBtn.setBackground(new Color(100, 100, 100)); 
    serveReqBtn.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
    detailsPanel.add(serveReqBtn, gbc);

    // 🔥 مصد الفراغ (Vertical Glue) لرفع كل العناصر للأعلى
    gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
    gbc.weighty = 1.0; 
    gbc.fill = GridBagConstraints.BOTH;
    detailsPanel.add(new JLabel(""), gbc); 

    detailsPanel.setVisible(false); 
    JToggleButton toggleBtn = new JToggleButton("Manage Service Requests ▼");
    toggleBtn.setBackground(new Color(153, 51, 255).darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

    form.add(toggleBtn, BorderLayout.NORTH); 
    form.add(detailsPanel, BorderLayout.CENTER); 
    
    toggleBtn.addActionListener(e -> {
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Form ▲" : "Manage Service Requests ▼"); 
        form.revalidate();
    });

    // منطق إضافة الطلب مع عرض رقم الأولوية
    addReqBtn.addActionListener(e -> {
        if (selectedCustomerID[0] == null) {
            showError("Please select a customer using the Browse button!");
            return;
        }
        String selectedIssue = (String) issueSelector.getSelectedItem();
        int priorityNum = ISSUE_PRIORITIES.get(selectedIssue); // جلب الرقم من الـ Map
        
        bankSystem.addServiceRequest(new ServiceRequest(selectedCustomerID[0], selectedIssue, priorityNum));
        
        // إظهار رقم الأولوية في رسالة النجاح
        showSuccess(String.format("Request added for: %s\nIssue: %s\nPriority Level: %d", 
                    cidField.getText(), selectedIssue, priorityNum));
        
        // إعادة ضبط الحقول بعد النجاح
        cidField.setText("");
        cidField.setBackground(new Color(245, 245, 245));
        selectedCustomerID[0] = null;
    });

    // منطق خدمة الطلب مع عرض رقم الأولوية للعميل المخدوم
    serveReqBtn.addActionListener(e -> {
        if (!bankSystem.hasRequests()) { showError("No pending requests."); return; }
        ServiceRequest req = bankSystem.serveNextRequest();
        
        showSuccess(String.format("Serving Next Customer\nCustomer ID: %s\nIssue: %s\nPriority Level: %d", 
                    req.getCustomerId(), req.getIssue(), req.getPriority()));
    });

    return form;
}
  
  
  
  
  
  
   private JPanel createHistoryViewPanel() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createTitledBorder("📊 Transaction History Viewer"));
    panel.setBackground(new Color(255, 255, 230));

    JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
    
    // --- إعداد نظام الـ Browse لـ Account Number ---
    JTextField accField = new JTextField();
    accField.setEditable(false);
    accField.setBackground(new Color(245, 245, 245));
    JButton browseAccBtn = new JButton("🔍 Browse");
    
    browseAccBtn.addActionListener(e -> {
        String res = showAccountSearchDialog(); // استخدام نافذة بحث الحسابات
        if (res != null) {
            accField.setText(res);
        }
    });

    JTextArea historyArea = new JTextArea();
    historyArea.setEditable(false);
    historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
    
    JPanel topPanel = new JPanel(new BorderLayout(5, 5));
    JButton viewBtn = new JButton("View History");
    viewBtn.setBackground(new Color(0, 153, 76)); 
    viewBtn.setForeground(Color.WHITE);
    
    // تجميع العناصر في شريط البحث العلوي
    JPanel inputWrapper = new JPanel(new BorderLayout(5, 0));
    inputWrapper.setOpaque(false);
    inputWrapper.add(new JLabel("Account No: "), BorderLayout.WEST);
    inputWrapper.add(accField, BorderLayout.CENTER);
    inputWrapper.add(browseAccBtn, BorderLayout.EAST);
    
    topPanel.add(inputWrapper, BorderLayout.CENTER);
    topPanel.add(viewBtn, BorderLayout.EAST);
    
    detailsPanel.add(topPanel, BorderLayout.NORTH);
    detailsPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
    detailsPanel.setVisible(false); 

    JToggleButton toggleBtn = new JToggleButton("View Transaction History ▼");
    toggleBtn.setBackground(new Color(0, 153, 76).darker());
    toggleBtn.setForeground(Color.WHITE);
    toggleBtn.setFont(new Font("Arial", Font.BOLD, 16)); 

    panel.add(toggleBtn, BorderLayout.NORTH); 
    panel.add(detailsPanel, BorderLayout.CENTER); 
    
    toggleBtn.addActionListener(e -> {
        detailsPanel.setVisible(toggleBtn.isSelected());
        toggleBtn.setText(toggleBtn.isSelected() ? "Close Viewer ▲" : "View Transaction History ▼"); 
        panel.revalidate(); panel.repaint();
    });

    viewBtn.addActionListener(e -> {
        String accNum = accField.getText().trim();
        if (accNum.isEmpty()) { showError("Please select an account first!"); return; }
        
        BankAccount account = bankSystem.getAccount(accNum);
        if (account == null) { showError("Account not found"); historyArea.setText(""); return; }
        
        StringBuilder sb = new StringBuilder("TRANSACTION HISTORY for " + accNum + "\n\n");
        List<Transaction> list = account.getTransactions();
        if (list.isEmpty()) sb.append("No transactions found.");
        else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Transaction t : list) sb.append(String.format("%-18s | %-15s | %s%n", t.getType(), t.getAmount(), t.getDate().format(fmt)));
        }
        historyArea.setText(sb.toString());
    });
    return panel;
}
}	
