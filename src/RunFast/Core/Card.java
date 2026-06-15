package RunFast.Core;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

public class Card extends JLabel { //基础牌单位，包含花色、数字、牌值和图片显示
    private String m_Number;
    private String m_Color;
    private int m_Value;
    private boolean front = true;
    private boolean selectable = false;
    private boolean clicked = false;

    public Card(String m_Number, String m_Color) {
        this.m_Number = m_Number;
        this.m_Color = m_Color;
        this.m_Value = calculateValue(m_Number, m_Color);
        setSize(71, 96);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVisible(true);
        turnFront();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelected();
            }
        });
    }

    public Card() {
    }

    //此方法的目的是计算牌的完整价值，十位以上表示数字，个位表示花色
    private int calculateValue(String number, String color) {
        return calculateNumberValue(number) * 10 + calculateColorValue(color);
    }

    //此方法的目的是计算数字价值，用于确定跑得快中的牌大小
    private int calculateNumberValue(String number) {
        switch (number) {
            case "J":
                return 11;
            case "Q":
                return 12;
            case "K":
                return 13;
            case "A":
                return 14;
            case "2":
                return 15;
            default:
                return Integer.parseInt(number);
        }
    }

    //此方法的目的是计算花色价值，同数字时用于排序
    private int calculateColorValue(String color) {
        switch (color) {
            case "FangKuai":
            case "方块":
            case "♦":
                return 1;
            case "MeiHua":
            case "梅花":
            case "♣":
                return 2;
            case "HongTao":
            case "红桃":
            case "♥":
                return 3;
            case "HeiTao":
            case "黑桃":
            case "♠":
                return 4;
            default:
                return 0;
        }
    }

    //此方法的目的是显示牌的正面图片
    public void turnFront() {
        setIcon(loadCardIcon(m_Color + "-" + m_Number + "-" + m_Value + ".png"));
        front = true;
    }

    //此方法的目的是显示牌的背面图片
    public void turnRear() {
        setIcon(loadCardIcon("rear.png"));
        front = false;
    }

    //此方法的目的是在玩家点击手牌时切换选中状态
    private void toggleSelected() {
        if (!selectable) {
            return;
        }

        Point point = getLocation();
        if (clicked) {
            setLocation(point.x, point.y + 20);
        } else {
            setLocation(point.x, point.y - 20);
        }
        clicked = !clicked;
    }

    //此方法的目的是获取当前牌对应的图片路径
    public String getFrontImagePath() {
        return "src" + File.separator + "image" + File.separator + "poker" + File.separator
                + m_Color + "-" + m_Number + "-" + m_Value + ".png";
    }

    //此方法的目的是从jar内部或项目目录中加载扑克牌图片
    private ImageIcon loadCardIcon(String fileName) {
        URL resource = Card.class.getResource("/image/poker/" + fileName);
        if (resource != null) {
            return new ImageIcon(resource);
        }
        return new ImageIcon("src" + File.separator + "image" + File.separator + "poker" + File.separator + fileName);
    }

    public String getm_Number() {
        return m_Number;
    }

    public void setm_Number(String m_Number) {
        this.m_Number = m_Number;
        this.m_Value = calculateValue(this.m_Number, this.m_Color);
    }

    public String getm_Color() {
        return m_Color;
    }

    public void setm_Color(String m_Color) {
        this.m_Color = m_Color;
        this.m_Value = calculateValue(this.m_Number, this.m_Color);
    }

    public int getm_Value() {
        return m_Value;
    }

    public void setm_Value(int m_Value) {
        this.m_Value = m_Value;
    }

    public int getRankValue() {
        return calculateNumberValue(m_Number);
    }

    public boolean isFront() {
        return front;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        setCursor(selectable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    @Override
    public String toString() {
        return m_Color + m_Number;
    }
}
