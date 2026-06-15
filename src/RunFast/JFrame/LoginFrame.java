package RunFast.JFrame;

import RunFast.Core.PockerGame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.SecureRandom;

public class LoginFrame extends JFrame {
    private static final String PASSWORD = "123456";
    private static final String CAPTCHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JTextField accountField;
    private final JPasswordField passwordField;
    private final JTextField captchaField;
    private final JLabel captchaLabel;
    private String captchaCode;

    public LoginFrame() {
        accountField = new JTextField();
        passwordField = new JPasswordField();
        captchaField = new JTextField();
        captchaLabel = new JLabel("", SwingConstants.CENTER);

        setTitle("RunFast Login");
        setSize(420, 310);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(245, 247, 250));
        rootPanel.setBorder(new EmptyBorder(24, 32, 28, 32));

        JLabel titleLabel = new JLabel("RunFast", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(42, 55, 75));

        JLabel subtitleLabel = new JLabel("Sign in to start the game", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(107, 119, 140));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(22, 0, 18, 0));

        configureInput(accountField);
        configureInput(passwordField);
        configureInput(captchaField);
        configureCaptchaLabel();

        addFormRow(formPanel, 0, "Account", accountField);
        addFormRow(formPanel, 1, "Password", passwordField);
        addCaptchaRow(formPanel);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> login());

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(loginButton, BorderLayout.SOUTH);
        add(rootPanel);

        refreshCaptcha();
    }

    private void addFormRow(JPanel panel, int row, String labelText, JTextField field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(0, 0, 12, 14);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(55, 65, 81));
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.gridwidth = 2;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(0, 0, 12, 0);
        panel.add(field, fieldConstraints);
    }

    private void addCaptchaRow(JPanel panel) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 2;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(0, 0, 0, 14);

        JLabel label = new JLabel("Captcha");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(55, 65, 81));
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 2;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(0, 0, 0, 10);
        panel.add(captchaField, fieldConstraints);

        GridBagConstraints captchaConstraints = new GridBagConstraints();
        captchaConstraints.gridx = 2;
        captchaConstraints.gridy = 2;
        captchaConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(captchaLabel, captchaConstraints);
    }

    private void configureInput(JTextField field) {
        field.setPreferredSize(new Dimension(210, 36));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(new Color(31, 41, 55));
        field.setCaretColor(new Color(66, 133, 244));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 216, 226)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    private void configureCaptchaLabel() {
        captchaLabel.setPreferredSize(new Dimension(82, 36));
        captchaLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        captchaLabel.setForeground(new Color(31, 78, 121));
        captchaLabel.setOpaque(true);
        captchaLabel.setBackground(new Color(232, 240, 254));
        captchaLabel.setBorder(BorderFactory.createLineBorder(new Color(157, 181, 219)));
        captchaLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        captchaLabel.setToolTipText("Click to refresh");
        captchaLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                refreshCaptcha();
            }
        });
    }

    private void refreshCaptcha() {
        StringBuilder builder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            builder.append(CAPTCHA_CHARS.charAt(RANDOM.nextInt(CAPTCHA_CHARS.length())));
        }
        captchaCode = builder.toString();
        captchaLabel.setText(captchaCode);
    }

    private void login() {
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captcha = captchaField.getText().trim();

        if (account.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            accountField.requestFocus();
            return;
        }

        if (!PASSWORD.equals(password)) {
            JOptionPane.showMessageDialog(this, "Password error.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
            refreshCaptcha();
            return;
        }

        if (!captchaCode.equals(captcha)) {
            JOptionPane.showMessageDialog(this, "Captcha error.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            captchaField.setText("");
            captchaField.requestFocus();
            refreshCaptcha();
            return;
        }

        dispose();
        new PockerGame();   //flag
    }
}
