package RunFast.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;

public class PockerGame {
    static ArrayList<Card> list=new ArrayList<>();          //牌数组
    static TreeMap<Integer, Card> player1=new TreeMap<>(Collections.reverseOrder());     //玩家数组
    static TreeMap<Integer, Card> player2=new TreeMap<>(Collections.reverseOrder());
    static TreeMap<Integer, Card> player3=new TreeMap<>(Collections.reverseOrder());

    //此静态代码块的目的是创建扑克牌对象供后续游戏使用
    static{
        final String[] color ={"♦","♣","♥","♠"};
        final String[] number ={"3","4","5","6","7","8","9","10","J","Q","K","A","2"};

        //随机数对象，为随机生成2与A花色
        Random random2 = new Random();
        Random randomA = new Random();

        //扑克牌创建并填入
        for (String s : color) {
            for (String string : number) {

                //普通牌生成
                if (!string.equals("2") && !string.equals("A")) {
                    list.add(new Card(string, s));
                }
            }
        }

        //跑得快中只有一张2与三张A，特殊牌只需要生成一次
        list.add(new Card("2", color[random2.nextInt(color.length)]));

        int skipAColor = randomA.nextInt(color.length);
        for (int k = 0; k < color.length; k++) {
            if (k != skipAColor) {
                list.add(new Card("A", color[k]));
            }
        }
    }

    public String viewCard(String name,TreeMap<Integer, Card> player){
        return name+player.values().toString();
    }

    public String viewCard(TreeMap<Integer, Card> player){
        return player.values().toString();
    }

    private void addCard(TreeMap<Integer, Card> player, Card card) {
        player.put(card.getm_Value(), card);
    }

    public PockerGame() {
        Collections.shuffle(list);  //洗牌

        //发牌
        for (int i = 0; i < list.size(); i++) {
            if(i%3==0){
                addCard(player1, list.get(i));
            }
            else if(i%3==1){
                addCard(player2, list.get(i));
            }
            else {
                addCard(player3, list.get(i));
            }
        }

        System.out.println(viewCard("test",player1));
        System.out.println(viewCard(player2));
        System.out.println(viewCard(player3));
    };

}
