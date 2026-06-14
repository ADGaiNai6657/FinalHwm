package RunFast.Core;

public class Card { //基础牌单位，包含花色与数字
    private String m_Number;
    private String m_Color;
    private int m_Value;

    public Card(String m_Number, String m_Color) {
        this.m_Number = m_Number;
        this.m_Color = m_Color;
        this.m_Value = calculateValue(m_Number, m_Color);
    }
    public Card() {};

    private int calculateValue(String number, String color) {
        return calculateNumberValue(number) * 10 + calculateColorValue(color);
    }

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

    private int calculateColorValue(String color) {
        switch (color) {
            case "♦":
                return 1;
            case "♣":
                return 2;
            case "♥":
                return 3;
            case "♠":
                return 4;
            default:
                return 0;
        }
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
    public int getm_Value(){
        return m_Value;
    }
    public void setm_Value(int m_Value){
        this.m_Value = m_Value;
    }

    @Override
    public String toString() {
        return m_Number + m_Color;
    }


}
