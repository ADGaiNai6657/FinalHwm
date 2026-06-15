package RunFast.Core;

public class CardPattern { //牌型对象，保存一手牌的类型、主牌大小和长度信息
    //此枚举的目的是统一管理跑得快中支持的所有牌型
    public enum Type {
        INVALID,
        SINGLE,
        PAIR,
        TRIPLE,
        TRIPLE_WITH_ONE,
        TRIPLE_WITH_PAIR,
        STRAIGHT,
        PAIR_STRAIGHT,
        PLANE,
        PLANE_WITH_SINGLE,
        PLANE_WITH_PAIR,
        BOMB,
        FOUR_WITH_TWO
    }

    private final Type type;        //牌型类型
    private final int mainRank;     //主牌点数，用于比较大小
    private final int length;       //连续牌型长度，例如顺子长度、连对长度、飞机长度
    private final int cardCount;    //当前牌型总张数

    //此构造方法的目的是创建不可变牌型结果
    public CardPattern(Type type, int mainRank, int length, int cardCount) {
        this.type = type;
        this.mainRank = mainRank;
        this.length = length;
        this.cardCount = cardCount;
    }

    //此方法的目的是创建无效牌型对象
    public static CardPattern invalid(int cardCount) {
        return new CardPattern(Type.INVALID, 0, 0, cardCount);
    }

    //此方法的目的是判断当前牌型是否有效
    public boolean isValid() {
        return type != Type.INVALID;
    }

    //此方法的目的是判断当前牌型是否为炸弹
    public boolean isBomb() {
        return type == Type.BOMB;
    }

    public Type getType() {
        return type;
    }

    public int getMainRank() {
        return mainRank;
    }

    public int getLength() {
        return length;
    }

    public int getCardCount() {
        return cardCount;
    }

    //此方法的目的是获取牌型中文显示名称
    public String getDisplayName() {
        switch (type) {
            case SINGLE:
                return "单张";
            case PAIR:
                return "对子";
            case TRIPLE:
                return "三张";
            case TRIPLE_WITH_ONE:
                return "三带一";
            case TRIPLE_WITH_PAIR:
                return "三带二";
            case STRAIGHT:
                return "顺子";
            case PAIR_STRAIGHT:
                return "连对";
            case PLANE:
                return "飞机";
            case PLANE_WITH_SINGLE:
                return "飞机带单牌";
            case PLANE_WITH_PAIR:
                return "飞机带对子";
            case BOMB:
                return "炸弹";
            case FOUR_WITH_TWO:
                return "四带二";
            default:
                return "无效牌型";
        }
    }
}
