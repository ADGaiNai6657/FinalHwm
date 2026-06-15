package RunFast.Core;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //程序入口，启动登录界面
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}