package RunFast.Core;

public class Card { //基础牌单位，包含花色与数字
    private String m_Number;
    private String m_Color;
    private int m_Value;

    public Card(String m_Number, String m_Color) {
        this.m_Number = m_Number;
        this.m_Color = m_Color;
        this.m_Value = 0;
    }
    public Card() {};

    public String getm_Number() {
        return m_Number;
    }
    public void setm_Number(String m_Number) {
        this.m_Number = m_Number;
    }
    public String getm_Color() {
        return m_Color;
    }
    public void setm_Color(String m_Color) {
        this.m_Color = m_Color;
    }

    @Override
    public String toString() {
        return m_Number + m_Color;
    }

}
