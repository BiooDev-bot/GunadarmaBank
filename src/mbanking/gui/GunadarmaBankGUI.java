package mbanking.gui;

import mbanking.dao.DatabaseException;
import mbanking.enums.TransactionType;
import mbanking.model.Transaction;
import mbanking.model.User;
import mbanking.service.Bank;
import mbanking.util.Formatter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GunadarmaBankGUI extends JFrame {
    private static final String CARD_AUTH = "AUTH";
    private static final String CARD_APP = "APP";
    private static final String LOGO_PATH = "assets/logo.png";

    private static final Color BACKGROUND = new Color(248, 247, 251);
    private static final Color SURFACE = Color.WHITE;
    private static final Color SURFACE_ALT = new Color(251, 249, 253);
    private static final Color SIDEBAR = new Color(42, 27, 53);
    private static final Color SIDEBAR_HOVER = new Color(57, 38, 73);
    private static final Color SIDEBAR_TEXT = new Color(243, 240, 247);
    private static final Color SIDEBAR_ACTIVE = new Color(218, 177, 63);
    private static final Color SIDEBAR_ACTIVE_TEXT = new Color(41, 28, 50);
    private static final Color PRIMARY = new Color(88, 31, 118);
    private static final Color ACCENT_DARK = new Color(137, 96, 17);
    private static final Color SUCCESS = new Color(20, 129, 74);
    private static final Color DANGER = new Color(181, 42, 69);
    private static final Color WARNING = new Color(176, 116, 12);
    private static final Color TEXT = new Color(32, 28, 39);
    private static final Color MUTED = new Color(102, 94, 116);
    private static final Color BORDER = new Color(224, 217, 232);
    private static final Color FIELD_BORDER = new Color(211, 202, 222);
    private static final Color SECONDARY_BUTTON = new Color(244, 241, 247);
    private static final Color TABLE_HEADER_BG = new Color(236, 226, 244);

    private static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MEDIUM = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 20);

    private static final int FORM_MAX_WIDTH = 480;

    private final Bank bank;
    private final CardLayout rootLayout;
    private final JPanel root;
    private final JPanel appPanel;
    private final JPanel contentPanel;
    private final Image logoImage;

    private User currentUser;
    private JLabel headerName;
    private JLabel headerAccount;
    private JLabel headerBalance;
    private JButton activeNavButton;

    public GunadarmaBankGUI(Bank bank) {
        super("GUNADARMA BANK");
        this.bank = bank;
        this.logoImage = loadLogoImage();
        configureLookAndFeel();
        if (logoImage != null) {
            setIconImage(logoImage);
        }

        rootLayout = new CardLayout();
        root = new JPanel(rootLayout);
        root.add(createAuthPanel(), CARD_AUTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);
        appPanel = createAppShell();
        root.add(appPanel, CARD_APP);

        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setSize(1120, 740);
        setLocationRelativeTo(null);
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing falls back to the default look and feel.
        }
        UIManager.put("Label.font", FONT_BASE);
        UIManager.put("Button.font", FONT_MEDIUM);
        UIManager.put("TextField.font", FONT_BASE);
        UIManager.put("PasswordField.font", FONT_BASE);
        UIManager.put("TabbedPane.font", FONT_MEDIUM);
        UIManager.put("Table.font", FONT_BASE);
        UIManager.put("TableHeader.font", FONT_MEDIUM);
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
    }

    private JPanel createAuthPanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel shell = new JPanel(new BorderLayout(34, 0));
        shell.setOpaque(false);
        shell.setPreferredSize(new Dimension(920, 560));

        JPanel brand = new JPanel(new GridBagLayout());
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 12, 0, 18));
        GridBagConstraints brandGbc = new GridBagConstraints();
        brandGbc.gridx = 0;
        brandGbc.gridy = 0;
        brandGbc.anchor = GridBagConstraints.WEST;
        brandGbc.insets = new Insets(0, 0, 22, 0);

        brand.add(createLogoView(310, 206), brandGbc);

        brandGbc.gridy++;
        brandGbc.insets = new Insets(0, 0, 8, 0);
        JLabel title = new JLabel(bank.getBankName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(TEXT);
        brand.add(title, brandGbc);

        brandGbc.gridy++;
        brandGbc.insets = new Insets(0, 0, 0, 0);
        JLabel subtitle = new JLabel("Desktop Banking");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(MUTED);
        brand.add(subtitle, brandGbc);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFocusable(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Daftar Akun", createRegisterPanel());

        JPanel formWrap = createSurfacePanel(new BorderLayout());
        formWrap.setBorder(new EmptyBorder(24, 24, 24, 24));
        formWrap.setPreferredSize(new Dimension(400, 520));
        formWrap.add(tabs, BorderLayout.CENTER);

        shell.add(brand, BorderLayout.CENTER);
        shell.add(formWrap, BorderLayout.EAST);
        page.add(shell);
        return page;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 4, 4, 4));
        GridBagConstraints gbc = formGbc();

        addWide(panel, sectionLabel("Login"), gbc, 0);
        JTextField username = textField();
        JPasswordField password = passwordField();
        JLabel message = messageLabel();

        addFormRow(panel, gbc, 1, "Username", username);
        addFormRow(panel, gbc, 2, "Password", password);
        addWide(panel, message, gbc, 3);

        JButton login = button("Login", PRIMARY, Color.WHITE);
        login.addActionListener(e -> runSafely(() -> {
            User user = bank.login(username.getText().trim(), new String(password.getPassword()).trim());
            if (user == null) {
                setMessage(message, "Username atau password salah.", false);
                return;
            }
            currentUser = user;
            username.setText("");
            password.setText("");
            setMessage(message, "", true);
            showApplication();
        }));
        addWide(panel, login, gbc, 4);

        JButton exit = button("Keluar", SECONDARY_BUTTON, TEXT);
        exit.addActionListener(e -> dispose());
        addWide(panel, exit, gbc, 5);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 4, 4, 4));
        GridBagConstraints gbc = formGbc();

        addWide(panel, sectionLabel("Daftar Akun Baru"), gbc, 0);
        JTextField fullName = textField();
        JTextField username = textField();
        JPasswordField password = passwordField();
        JPasswordField pin = passwordField();
        JLabel message = messageLabel();

        addFormRow(panel, gbc, 1, "Nama Lengkap", fullName);
        addFormRow(panel, gbc, 2, "Username", username);
        addFormRow(panel, gbc, 3, "Password", password);
        addFormRow(panel, gbc, 4, "PIN (6 digit)", pin);
        addWide(panel, message, gbc, 5);

        JButton register = button("Daftar", SUCCESS, Color.WHITE);
        register.addActionListener(e -> runSafely(() -> {
            String newPin = new String(pin.getPassword()).trim();
            if (newPin.length() != 6 || !newPin.matches("\\d+")) {
                setMessage(message, "PIN harus 6 digit angka. Pendaftaran dibatalkan.", false);
                return;
            }
            String accountNumber = bank.registerUser(fullName.getText().trim(), username.getText().trim(),
                    new String(password.getPassword()).trim(), newPin, 0);
            if (accountNumber == null) {
                setMessage(message, "Username '" + username.getText().trim() + "' sudah dipakai.", false);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Akun berhasil dibuat!\nNama: " + fullName.getText().trim()
                            + "\nUsername: " + username.getText().trim()
                            + "\nNo. Rek: " + accountNumber,
                    "Registrasi Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
            fullName.setText("");
            username.setText("");
            password.setText("");
            pin.setText("");
            setMessage(message, "Akun berhasil dibuat.", true);
        }));
        addWide(panel, register, gbc, 6);
        return panel;
    }

    private JPanel createAppShell() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);
        page.add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);
        main.add(createHeader(), BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        page.add(main, BorderLayout.CENTER);
        return page;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(242, 0));
        sidebar.setBorder(new EmptyBorder(24, 18, 24, 18));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.add(createLogoView(66, 44), BorderLayout.WEST);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 0));
        brandText.setOpaque(false);
        JLabel brandName = new JLabel("GUNADARMA");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandName.setForeground(Color.WHITE);
        JLabel brandSub = new JLabel("BANK");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        brandSub.setForeground(new Color(226, 218, 235));
        brandText.add(brandName);
        brandText.add(brandSub);
        brand.add(brandText, BorderLayout.CENTER);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 10));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(32, 0, 0, 0));
        nav.add(navButton("Cek Saldo", this::showBalanceView));
        nav.add(navButton("Setor Tunai", this::showDepositView));
        nav.add(navButton("Tarik Tunai", this::showWithdrawView));
        nav.add(navButton("Transfer", this::showTransferView));
        nav.add(navButton("Riwayat Transaksi", this::showHistoryView));
        nav.add(navButton("Ganti PIN", this::showPinView));
        sidebar.add(nav, BorderLayout.CENTER);

        JButton logout = button("Logout", SIDEBAR_HOVER, Color.WHITE);
        logout.addActionListener(e -> logout());
        sidebar.add(logout, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(24, 28, 18, 28));

        JPanel identity = new JPanel(new GridLayout(2, 1, 0, 2));
        identity.setOpaque(false);
        headerName = new JLabel("-");
        headerName.setFont(FONT_SECTION);
        headerName.setForeground(TEXT);
        headerAccount = new JLabel("-");
        headerAccount.setFont(FONT_BASE);
        headerAccount.setForeground(MUTED);
        identity.add(headerName);
        identity.add(headerAccount);

        JPanel balance = createSurfacePanel(new GridLayout(2, 1, 0, 2));
        balance.setBorder(new EmptyBorder(12, 18, 12, 18));
        JLabel balanceTitle = new JLabel("Saldo Aktif", SwingConstants.RIGHT);
        balanceTitle.setFont(FONT_BASE);
        balanceTitle.setForeground(MUTED);
        headerBalance = new JLabel("-", SwingConstants.RIGHT);
        headerBalance.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerBalance.setForeground(PRIMARY);
        balance.add(balanceTitle);
        balance.add(headerBalance);

        header.add(identity, BorderLayout.CENTER);
        header.add(balance, BorderLayout.EAST);
        return header;
    }

    private void showApplication() {
        updateHeader();
        rootLayout.show(root, CARD_APP);
        showBalanceView();
    }

    private void showBalanceView() {
        selectNavigation("Cek Saldo");
        JPanel grid = new JPanel(new GridLayout(1, 3, 18, 18));
        grid.setOpaque(false);
        grid.add(summaryCard("Nama", currentUser.getFullName(), PRIMARY));
        grid.add(summaryCard("No. Rek", currentUser.getAccount().getAccountNumber(), ACCENT_DARK));
        grid.add(summaryCard("Saldo", Formatter.formatCurrency(currentUser.getAccount().getBalance()), SUCCESS));

        JPanel cardsWrap = new JPanel(new BorderLayout());
        cardsWrap.setOpaque(false);
        cardsWrap.add(grid, BorderLayout.NORTH);
        setContent(viewShell("Cek Saldo", cardsWrap));
    }

    private void showDepositView() {
        selectNavigation("Setor Tunai");
        JPanel form = createFormCard();
        GridBagConstraints gbc = formGbc();
        JTextField amount = textField();
        JPasswordField pin = passwordField();
        JLabel message = messageLabel();
        addFormRow(form, gbc, 0, "Nominal Setor", amount);
        addFormRow(form, gbc, 1, "PIN", pin);
        addWide(form, message, gbc, 2);

        JButton submit = button("Setor Tunai", SUCCESS, Color.WHITE);
        submit.addActionListener(e -> runSafely(() -> {
            double value = parseAmount(amount.getText());
            if (value <= 0) {
                setMessage(message, "Nominal tidak valid.", false);
                return;
            }
            boolean success = bank.deposit(currentUser, value, new String(pin.getPassword()).trim());
            if (success) {
                setMessage(message, "Setor berhasil! " + Formatter.formatCurrency(value), true);
                amount.setText("");
                pin.setText("");
                updateHeader();
            } else {
                setMessage(message, "Setor gagal. Cek PIN atau nominal.", false);
            }
        }));
        addWide(form, submit, gbc, 3);
        setContent(viewShell("Setor Tunai", centerHorizontally(form)));
    }

    private void showWithdrawView() {
        selectNavigation("Tarik Tunai");
        JPanel form = createFormCard();
        GridBagConstraints gbc = formGbc();
        JLabel balance = infoLabel("Saldo saat ini: " + Formatter.formatCurrency(currentUser.getAccount().getBalance()));
        JTextField amount = textField();
        JPasswordField pin = passwordField();
        JLabel message = messageLabel();
        addWide(form, balance, gbc, 0);
        addFormRow(form, gbc, 1, "Nominal Tarik", amount);
        addFormRow(form, gbc, 2, "PIN", pin);
        addWide(form, message, gbc, 3);

        JButton submit = button("Tarik Tunai", WARNING, Color.WHITE);
        submit.addActionListener(e -> runSafely(() -> {
            double value = parseAmount(amount.getText());
            String result = bank.withdraw(currentUser, value, new String(pin.getPassword()).trim());
            if ("OK".equals(result)) {
                setMessage(message, "Tarik tunai berhasil! " + Formatter.formatCurrency(value), true);
                amount.setText("");
                pin.setText("");
                balance.setText("Saldo saat ini: " + Formatter.formatCurrency(currentUser.getAccount().getBalance()));
                updateHeader();
            } else {
                setMessage(message, result, false);
            }
        }));
        addWide(form, submit, gbc, 4);
        setContent(viewShell("Tarik Tunai", centerHorizontally(form)));
    }

    private void showTransferView() {
        selectNavigation("Transfer");
        JPanel form = createFormCard();
        GridBagConstraints gbc = formGbc();
        JLabel balance = infoLabel("Saldo saat ini: " + Formatter.formatCurrency(currentUser.getAccount().getBalance()));
        JTextField targetAccount = textField();
        JLabel receiver = infoLabel("Penerima: -");
        JTextField amount = textField();
        JPasswordField pin = passwordField();
        JLabel message = messageLabel();

        addWide(form, balance, gbc, 0);
        addFormRow(form, gbc, 1, "No. Rek Tujuan", targetAccount);
        addWide(form, receiver, gbc, 2);
        JButton check = button("Cek Penerima", SECONDARY_BUTTON, TEXT);
        check.addActionListener(e -> runSafely(() -> {
            User target = bank.findUserByAccount(targetAccount.getText().trim());
            if (target == null) {
                receiver.setText("Penerima: -");
                setMessage(message, "Nomor rekening tidak ditemukan.", false);
                return;
            }
            receiver.setText("Penerima: " + target.getFullName());
            setMessage(message, "Penerima ditemukan.", true);
        }));
        addWide(form, check, gbc, 3);
        addFormRow(form, gbc, 4, "Nominal", amount);
        addFormRow(form, gbc, 5, "PIN", pin);
        addWide(form, message, gbc, 6);

        JButton submit = button("Transfer", PRIMARY, Color.WHITE);
        submit.addActionListener(e -> runSafely(() -> {
            String targetNumber = targetAccount.getText().trim();
            User target = bank.findUserByAccount(targetNumber);
            if (target == null) {
                setMessage(message, "Nomor rekening tidak ditemukan.", false);
                return;
            }
            double value = parseAmount(amount.getText());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Konfirmasi transfer " + Formatter.formatCurrency(value) + " ke " + target.getFullName() + "?",
                    "Konfirmasi Transfer",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                setMessage(message, "Transfer dibatalkan.", true);
                return;
            }
            String result = bank.transfer(currentUser, targetNumber, value, new String(pin.getPassword()).trim());
            if (result.startsWith("OK:")) {
                String receiverName = result.substring(3);
                setMessage(message, "Transfer berhasil ke " + receiverName + ".", true);
                targetAccount.setText("");
                receiver.setText("Penerima: -");
                amount.setText("");
                pin.setText("");
                balance.setText("Saldo saat ini: " + Formatter.formatCurrency(currentUser.getAccount().getBalance()));
                updateHeader();
            } else {
                setMessage(message, result, false);
            }
        }));
        addWide(form, submit, gbc, 7);
        setContent(viewShell("Transfer", centerHorizontally(form)));
    }

    private void showHistoryView() {
        selectNavigation("Riwayat Transaksi");
        List<Transaction> transactions = currentUser.getTransactions();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Waktu", "Jenis", "Nominal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int start = Math.max(0, transactions.size() - 10);
        for (int i = transactions.size() - 1; i >= start; i--) {
            Transaction tx = transactions.get(i);
            model.addRow(new Object[]{tx.getFormattedTimestamp(), tx.getType().getLabel(), signedAmount(tx)});
        }

        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(236, 226, 244));
        table.setSelectionForeground(TEXT);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(TABLE_HEADER_BG);
        tableHeader.setForeground(PRIMARY);
        tableHeader.setPreferredSize(new Dimension(0, 42));
        tableHeader.setDefaultRenderer(new FlatTableHeaderRenderer(SwingConstants.LEFT));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        table.setDefaultRenderer(Object.class, renderer);
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer();
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        amountRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        table.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        table.getColumnModel().getColumn(2).setHeaderRenderer(new FlatTableHeaderRenderer(SwingConstants.RIGHT));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(SURFACE);

        JPanel tableCard = createSurfacePanel(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        if (transactions.isEmpty()) {
            tableCard.add(infoLabel("Belum ada transaksi."), BorderLayout.NORTH);
        }
        tableCard.add(scroll, BorderLayout.CENTER);
        setContent(viewShell("Riwayat Transaksi", tableCard));
    }

    private void showPinView() {
        selectNavigation("Ganti PIN");
        JPanel form = createFormCard();
        GridBagConstraints gbc = formGbc();
        JPasswordField oldPin = passwordField();
        JPasswordField newPin = passwordField();
        JPasswordField confirmPin = passwordField();
        JLabel message = messageLabel();

        addFormRow(form, gbc, 0, "PIN Lama", oldPin);
        addFormRow(form, gbc, 1, "PIN Baru", newPin);
        addFormRow(form, gbc, 2, "Konfirmasi PIN Baru", confirmPin);
        addWide(form, message, gbc, 3);

        JButton submit = button("Ganti PIN", PRIMARY, Color.WHITE);
        submit.addActionListener(e -> runSafely(() -> {
            String newValue = new String(newPin.getPassword()).trim();
            String confirmValue = new String(confirmPin.getPassword()).trim();
            if (!newValue.equals(confirmValue)) {
                setMessage(message, "Konfirmasi PIN tidak cocok.", false);
                return;
            }
            String result = bank.changePin(currentUser, new String(oldPin.getPassword()).trim(), newValue);
            if ("OK".equals(result)) {
                setMessage(message, "PIN berhasil diubah!", true);
                oldPin.setText("");
                newPin.setText("");
                confirmPin.setText("");
            } else {
                setMessage(message, result, false);
            }
        }));
        addWide(form, submit, gbc, 4);
        setContent(viewShell("Ganti PIN", centerHorizontally(form)));
    }

    private void logout() {
        resetActiveNavigation();
        currentUser = null;
        rootLayout.show(root, CARD_AUTH);
    }

    private void updateHeader() {
        if (currentUser == null) {
            return;
        }
        headerName.setText(currentUser.getFullName());
        headerAccount.setText("No. Rekening: " + currentUser.getAccount().getAccountNumber());
        headerBalance.setText(Formatter.formatCurrency(currentUser.getAccount().getBalance()));
    }

    private void setContent(JComponent view) {
        contentPanel.removeAll();
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND);
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel viewShell(String title, JComponent content) {
        JPanel shell = new JPanel(new BorderLayout(0, 18));
        shell.setBackground(BACKGROUND);
        shell.setBorder(new EmptyBorder(8, 28, 28, 28));
        shell.add(sectionLabel(title), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private JPanel summaryCard(String label, String value, Color accent) {
        JPanel card = createSurfacePanel(new GridBagLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel top = new JLabel(label, SwingConstants.CENTER);
        top.setForeground(MUTED);
        top.setFont(FONT_BASE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(top, gbc);

        JLabel main = new JLabel(value, SwingConstants.CENTER);
        main.setForeground(accent);
        main.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(main, gbc);

        return card;
    }

    private JPanel createFormCard() {
        RoundedPanel form = createSurfacePanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(24, 24, 24, 24));
        form.setMaxWidth(FORM_MAX_WIDTH);
        return form;
    }

    private JPanel centerHorizontally(JComponent component) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        wrap.add(component, gbc);
        return wrap;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SECTION);
        label.setForeground(TEXT);
        return label;
    }

    private JLabel infoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_MEDIUM);
        label.setForeground(TEXT);
        return label;
    }

    private JLabel messageLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(FONT_MEDIUM);
        return label;
    }

    private JTextField textField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    private JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setPreferredSize(new Dimension(274, 42));
        field.setBackground(SURFACE);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setSelectionColor(new Color(232, 219, 242));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(FIELD_BORDER, 10),
                new EmptyBorder(9, 12, 9, 12)
        ));
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton button = new RoundedButton(text);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton navButton(String text, Runnable action) {
        JButton button = button(text, SIDEBAR, SIDEBAR_TEXT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void selectNavigation(String text) {
        if (activeNavButton != null) {
            setInactiveNavigation(activeNavButton);
        }
        activeNavButton = findButtonByText(appPanel, text);
        if (activeNavButton != null) {
            activeNavButton.setBackground(SIDEBAR_ACTIVE);
            activeNavButton.setForeground(SIDEBAR_ACTIVE_TEXT);
            activeNavButton.repaint();
        }
    }

    private void resetActiveNavigation() {
        if (activeNavButton != null) {
            setInactiveNavigation(activeNavButton);
            activeNavButton = null;
        }
    }

    private void setInactiveNavigation(JButton button) {
        button.setBackground(SIDEBAR);
        button.setForeground(SIDEBAR_TEXT);
        button.repaint();
    }

    private JButton findButtonByText(JComponent component, String text) {
        for (Component child : component.getComponents()) {
            if (child instanceof JButton button && text.equals(button.getText())) {
                return button;
            }
            if (child instanceof JComponent nested) {
                JButton found = findButtonByText(nested, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private RoundedPanel createSurfacePanel(LayoutManager layout) {
        return new RoundedPanel(layout, SURFACE, BORDER, 14);
    }

    private Image loadLogoImage() {
        try {
            File logoFile = new File(LOGO_PATH);
            if (logoFile.isFile()) {
                return ImageIO.read(logoFile);
            }

            URL resource = getClass().getResource("/assets/logo.png");
            return resource == null ? null : ImageIO.read(resource);
        } catch (IOException e) {
            return null;
        }
    }

    private JComponent createLogoView(int width, int height) {
        LogoView logoView = new LogoView(logoImage);
        Dimension size = new Dimension(width, height);
        logoView.setPreferredSize(size);
        logoView.setMinimumSize(size);
        return logoView;
    }

    private GridBagConstraints formGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        return gbc;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel formLabel = new JLabel(label);
        formLabel.setFont(FONT_MEDIUM);
        formLabel.setForeground(TEXT);
        panel.add(formLabel, gbc);

        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 14, 12, 0);
        panel.add(field, gbc);
    }

    private void addWide(JPanel panel, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(component, gbc);
        gbc.gridwidth = 1;
    }

    private void setMessage(JLabel label, String text, boolean success) {
        label.setText(text == null || text.isEmpty() ? " " : text);
        label.setForeground(success ? SUCCESS : DANGER);
    }

    private String signedAmount(Transaction tx) {
        boolean credit = tx.getType() == TransactionType.DEPOSIT || tx.getType() == TransactionType.TRANSFER_IN;
        return (credit ? "+ " : "- ") + Formatter.formatCurrency(tx.getAmount());
    }

    private double parseAmount(String text) {
        try {
            return Double.parseDouble(text.trim().replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage() + (e.getCause() == null ? "" : "\nDetail: " + e.getCause().getMessage()),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Color buttonFill(Color base, ButtonModel model, boolean enabled) {
        if (!enabled) {
            return blend(base, Color.LIGHT_GRAY, 0.42);
        }
        if (model.isPressed()) {
            return blend(base, Color.BLACK, 0.14);
        }
        if (model.isRollover()) {
            return blend(base, isDark(base) ? Color.WHITE : Color.BLACK, 0.08);
        }
        return base;
    }

    private static boolean isDark(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance < 0.45;
    }

    private static Color blend(Color base, Color overlay, double ratio) {
        double keep = 1 - ratio;
        int red = (int) Math.round(base.getRed() * keep + overlay.getRed() * ratio);
        int green = (int) Math.round(base.getGreen() * keep + overlay.getGreen() * ratio);
        int blue = (int) Math.round(base.getBlue() * keep + overlay.getBlue() * ratio);
        return new Color(red, green, blue);
    }

    private static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(buttonFill(getBackground(), getModel(), isEnabled()));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            if (!isFocusOwner()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(120, 76, 150));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;
        private int maxWidth = -1;

        RoundedPanel(LayoutManager layout, Color fill, Color stroke, int radius) {
            super(layout);
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
            setOpaque(false);
        }

        void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            if (maxWidth > 0 && size.width > maxWidth) {
                return new Dimension(maxWidth, size.height);
            }
            return size;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(stroke);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }

    private static class FlatTableHeaderRenderer extends DefaultTableCellRenderer {
        FlatTableHeaderRenderer(int alignment) {
            setOpaque(true);
            setFont(FONT_MEDIUM);
            setHorizontalAlignment(alignment);
            setForeground(PRIMARY);
            setBackground(TABLE_HEADER_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(0, 12, 0, 12)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(TABLE_HEADER_BG);
            setForeground(PRIMARY);
            return this;
        }
    }

    private static class LogoView extends JComponent {
        private final Image logo;

        LogoView(Image logo) {
            this.logo = logo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            if (logo == null) {
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(SIDEBAR_ACTIVE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(18, getHeight() / 3)));
                g2.drawString("GB", Math.max(10, getWidth() / 3), Math.max(24, getHeight() / 2 + 8));
                g2.dispose();
                return;
            }

            int imageWidth = logo.getWidth(this);
            int imageHeight = logo.getHeight(this);
            if (imageWidth <= 0 || imageHeight <= 0) {
                g2.dispose();
                return;
            }

            double scale = Math.min(getWidth() / (double) imageWidth, getHeight() / (double) imageHeight);
            int drawWidth = (int) Math.round(imageWidth * scale);
            int drawHeight = (int) Math.round(imageHeight * scale);
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;
            g2.drawImage(logo, x, y, drawWidth, drawHeight, this);
            g2.dispose();
        }
    }
}
