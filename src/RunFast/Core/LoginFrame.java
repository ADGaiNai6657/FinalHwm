package RunFast.Core;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LoginFrame extends JFrame { //登录窗口，负责玩家账号验证
    private static final String PASSWORD = "123456";      //默认登录密码

    private final JComboBox<String> accountBox;           //玩家账号选择框
    private final JPasswordField passwordField;           //密码输入框

    //此构造方法的目的是创建登录界面并绑定登录按钮事件
    public LoginFrame() {
        accountBox = new JComboBox<>(new String[]{"player 1", "player 2", "player 3"});
        passwordField = new JPasswordField();

        //登录窗口基础设置
        setTitle("Login");
        setSize(320, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        //账号与密码输入区域
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Account:"));
        inputPanel.add(accountBox);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        //登录按钮，点击后进行密码验证
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        add(inputPanel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
    }

    //此方法的目的是验证密码，验证成功后进入游戏
    private void login() {
        String password = new String(passwordField.getPassword());

        if (PASSWORD.equals(password)) {
            dispose();
            new PockerGame();
        } else {
            JOptionPane.showMessageDialog(this, "Password error", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
