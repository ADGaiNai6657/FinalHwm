package RunFast.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PockerGame {
    static ArrayList<Card> list=new ArrayList<>();          //牌数组
    static ArrayList<String> player1=new ArrayList<>();     //玩家数组
    static ArrayList<String> player2=new ArrayList<>();
    static ArrayList<String> player3=new ArrayList<>();

    //此静态代码块的目的是创建扑克牌对象供后续游戏使用
    static{
        final String[] color ={"♦","♣","♥","♠"};
        final String[] number ={"3","4","5","6","7","8","9","10","J","Q","K","A","2"};

        //随机数对象，为随机生成2与A花色
        Random random2 = new Random();
        Random randomA=new Random();

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

    public String viewCard(String name,ArrayList<?> list){
        return name+list.toString();
    }

    public PockerGame() {
        Collections.shuffle(list);  //洗牌
//        System.out.println(list);

        //发牌
        for (int i = 0; i < list.size(); i++) {
            if(i%3==0){
                player1.add(list.get(i).toString());
            }
            else if(i%3==1){
                player2.add(list.get(i).toString());
            }
            else {
                player3.add(list.get(i).toString());
            }
        }

        System.out.println(viewCard("test",player1));
//        System.out.println(viewCard(player2));
//        System.out.println(viewCard(player3));
    };

}
