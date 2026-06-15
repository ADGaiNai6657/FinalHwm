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

public class LoginFrame extends JFrame { //登录窗口，负责玩家账号、密码与验证码验证
    private static final String PASSWORD = "123456";      //默认登录密码
    private static final String CAPTCHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; //验证码字符范围
    private static final SecureRandom RANDOM = new SecureRandom();      //随机数对象，用于生成验证码

    private final JTextField accountField;                //账号输入框
    private final JPasswordField passwordField;           //密码输入框
    private final JTextField captchaField;                //验证码输入框
    private final JLabel captchaLabel;                    //验证码显示区域
    private String captchaCode;                           //当前验证码

    //此构造方法的目的是创建登录界面并绑定登录按钮与回车事件
    public LoginFrame() {
        accountField = new JTextField();
        passwordField = new JPasswordField();
        captchaField = new JTextField();
        captchaLabel = new JLabel("", SwingConstants.CENTER);

        //登录窗口基础设置
        setTitle("RunFast Login");
        setSize(420, 310);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //主面板样式设置
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(245, 247, 250));
        rootPanel.setBorder(new EmptyBorder(24, 32, 28, 32));

        //标题区域
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

        //输入框与验证码样式设置
        configureInput(accountField);
        configureInput(passwordField);
        configureInput(captchaField);
        configureCaptchaLabel();

        //账号、密码、验证码输入区域
        addFormRow(formPanel, 0, "Account", accountField);
        addFormRow(formPanel, 1, "Password", passwordField);
        addCaptchaRow(formPanel);

        //登录按钮，点击或回车后进行登录验证
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(loginButton, BorderLayout.SOUTH);
        add(rootPanel);

        refreshCaptcha();
    }

    //此方法的目的是向表单中添加普通输入行
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

    //此方法的目的是向表单中添加验证码输入行与验证码显示区域
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

    //此方法的目的是统一设置输入框样式
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

    //此方法的目的是设置验证码显示区域样式并绑定点击刷新事件
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

    //此方法的目的是随机生成四位验证码并显示在界面右侧
    private void refreshCaptcha() {
        StringBuilder builder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            builder.append(CAPTCHA_CHARS.charAt(RANDOM.nextInt(CAPTCHA_CHARS.length())));
        }
        captchaCode = builder.toString();
        captchaLabel.setText(captchaCode);
    }

    //此方法的目的是验证账号、密码和验证码，验证成功后进入游戏
    private void login() {
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captcha = captchaField.getText().trim();

        //账号不能为空
        if (account.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            accountField.requestFocus();
            return;
        }

        //密码错误时清空密码并刷新验证码
        if (!PASSWORD.equals(password)) {
            JOptionPane.showMessageDialog(this, "Password error.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
            refreshCaptcha();
            return;
        }

        //验证码错误时清空验证码并刷新验证码
        if (!captchaCode.equals(captcha)) {
            JOptionPane.showMessageDialog(this, "Captcha error.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            captchaField.setText("");
            captchaField.requestFocus();
            refreshCaptcha();
            return;
        }

        dispose();
        new PockerGame();   //进入游戏
    }
}
