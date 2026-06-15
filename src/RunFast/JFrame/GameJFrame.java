package RunFast.JFrame;

import RunFast.Core.Card;
import RunFast.Core.CardPattern;
import RunFast.Core.Judge;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameJFrame extends JFrame { //游戏主窗口，负责跑得快发牌、出牌和界面显示
    private final Container container;
    private final ArrayList<ArrayList<Card>> playerList = new ArrayList<>();    //三个玩家的手牌
    private final ArrayList<ArrayList<Card>> discardList = new ArrayList<>();   //三个玩家各自的弃牌堆
    private final ArrayList<Card> lastCards = new ArrayList<>();                //当前需要跟的牌
    private final JLabel[] playerInfo = new JLabel[3];                         //三个玩家的提示文本
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);  //中间状态提示
    private final JButton playButton = new JButton("出牌");
    private final JButton passButton = new JButton("不要");
    private final JButton restartButton = new JButton("重新开始");
    private final Random random = new Random();

    private int turn = 1;                 //0为左侧电脑，1为玩家，2为右侧电脑
    private int lastPlayer = -1;          //最后成功出牌的玩家
    private int passCount = 0;            //连续不要次数
    private boolean gameOver = false;

    //此构造方法的目的是初始化游戏窗口并开始一局跑得快
    public GameJFrame() {
        container = getContentPane();
        initJFrame();
        initView();
        startNewGame();
        setVisible(true);
    }

    //此方法的目的是设置游戏窗口基础信息
    private void initJFrame() {
        setTitle("RunFast");
        setSize(930, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        container.setLayout(null);
        container.setBackground(new Color(46, 125, 86));
    }

    //此方法的目的是添加按钮和文本提示组件
    private void initView() {
        statusLabel.setBounds(250, 250, 430, 36);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);
        container.add(statusLabel);

        for (int i = 0; i < playerInfo.length; i++) {
            playerInfo[i] = new JLabel("", SwingConstants.CENTER);
            playerInfo[i].setOpaque(true);
            playerInfo[i].setBackground(new Color(33, 87, 61));
            playerInfo[i].setForeground(Color.WHITE);
            playerInfo[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            container.add(playerInfo[i]);
        }
        playerInfo[0].setBounds(30, 70, 140, 28);
        playerInfo[1].setBounds(395, 560, 140, 28);
        playerInfo[2].setBounds(760, 70, 140, 28);

        playButton.setBounds(350, 475, 90, 30);
        passButton.setBounds(490, 475, 90, 30);
        restartButton.setBounds(790, 475, 110, 32);

        playButton.addActionListener(e -> playSelectedCards());
        passButton.addActionListener(e -> passTurn());
        restartButton.addActionListener(e -> startNewGame());

        container.add(playButton);
        container.add(passButton);
        container.add(restartButton);
    }

    //此方法的目的是重新开始一局游戏
    private void startNewGame() {
        removeCardsFromContainer();
        playerList.clear();
        discardList.clear();
        lastCards.clear();
        lastPlayer = -1;
        passCount = 0;
        turn = 1;
        gameOver = false;

        for (int i = 0; i < 3; i++) {
            playerList.add(new ArrayList<>());
            discardList.add(new ArrayList<>());
        }

        ArrayList<Card> deck = createRunFastDeck();
        Collections.shuffle(deck);
        for (int i = 0; i < deck.size(); i++) {
            playerList.get(i % 3).add(deck.get(i));
        }

        for (ArrayList<Card> cards : playerList) {
            orderCards(cards);
        }

        statusLabel.setText("轮到你出牌");
        renderTable();
        updateControls();
    }

    //此方法的目的是创建跑得快牌组，不加入大小王，并按现有规则保留三张A和一张2
    private ArrayList<Card> createRunFastDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        String[] colors = {"FangKuai", "MeiHua", "HongTao", "HeiTao"};
        String[] normalNumbers = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String color : colors) {
            for (String number : normalNumbers) {
                deck.add(new Card(number, color));
            }
        }

        deck.add(new Card("2", colors[random.nextInt(colors.length)]));

        ArrayList<String> aColors = new ArrayList<>();
        Collections.addAll(aColors, colors);
        Collections.shuffle(aColors);
        for (int i = 0; i < 3; i++) {
            deck.add(new Card("A", aColors.get(i)));
        }

        return deck;
    }

    //此方法的目的是按牌值从大到小整理手牌
    private void orderCards(ArrayList<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getm_Value).reversed());
    }

    //此方法的目的是刷新整张牌桌的牌和提示
    private void renderTable() {
        removeCardsFromContainer();

        renderComputerCards(playerList.get(0), 55, 110);
        renderPlayerCards();
        renderComputerCards(playerList.get(2), 805, 110);
        renderDiscardPiles();
        updatePlayerInfo();

        container.repaint();
    }

    //此方法的目的是从窗口中移除旧牌组件，避免重复显示
    private void removeCardsFromContainer() {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof Card) {
                container.remove(component);
            }
        }
    }

    //此方法的目的是显示电脑玩家的背面手牌
    private void renderComputerCards(ArrayList<Card> cards, int x, int startY) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            card.turnRear();
            card.setSelectable(false);
            card.setClicked(false);
            card.setLocation(x, startY + i * 18);
            container.add(card);
            container.setComponentZOrder(card, 0);
        }
    }

    //此方法的目的是显示玩家自己的正面手牌
    private void renderPlayerCards() {
        ArrayList<Card> playerCards = playerList.get(1);
        int startX = Math.max(35, 465 - playerCards.size() * 22);
        for (int i = 0; i < playerCards.size(); i++) {
            Card card = playerCards.get(i);
            card.turnFront();
            card.setSelectable(turn == 1 && !gameOver);
            card.setClicked(false);
            card.setLocation(startX + i * 44, 590);
            container.add(card);
            container.setComponentZOrder(card, 0);
        }
    }

    //此方法的目的是分别显示每个玩家面前的弃牌堆
    private void renderDiscardPiles() {
        renderDiscardPile(discardList.get(0), 210, 170);
        renderDiscardPile(discardList.get(1), 465 - discardList.get(1).size() * 24, 395);
        renderDiscardPile(discardList.get(2), 620, 170);
    }

    //此方法的目的是显示单个玩家刚出的牌
    private void renderDiscardPile(ArrayList<Card> cards, int startX, int y) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            card.turnFront();
            card.setSelectable(false);
            card.setClicked(false);
            card.setLocation(startX + i * 48, y);
            container.add(card);
            container.setComponentZOrder(card, 0);
        }
    }

    //此方法的目的是刷新三个玩家的剩余牌数
    private void updatePlayerInfo() {
        playerInfo[0].setText("电脑A：" + playerList.get(0).size() + "张");
        playerInfo[1].setText("玩家：" + playerList.get(1).size() + "张");
        playerInfo[2].setText("电脑B：" + playerList.get(2).size() + "张");
    }

    //此方法的目的是处理玩家点击出牌按钮后的逻辑
    private void playSelectedCards() {
        if (turn != 1 || gameOver) {
            return;
        }

        ArrayList<Card> selectedCards = getSelectedCards();
        if (selectedCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择要出的牌。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CardPattern pattern = Judge.judge(selectedCards);
        if (!pattern.isValid()) {
            JOptionPane.showMessageDialog(this, "当前牌型不合法。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!canBeatLastCards(selectedCards)) {
            JOptionPane.showMessageDialog(this, "所选牌不能压过上一手牌。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        playCards(1, selectedCards);
        continueAfterPlay();
    }

    //此方法的目的是获取玩家当前选中的手牌
    private ArrayList<Card> getSelectedCards() {
        ArrayList<Card> selectedCards = new ArrayList<>();
        for (Card card : playerList.get(1)) {
            if (card.isClicked()) {
                selectedCards.add(card);
            }
        }
        orderCards(selectedCards);
        return selectedCards;
    }

    //此方法的目的是判断当前出牌能否压过上一手牌
    private boolean canBeatLastCards(List<Card> cards) {
        if (lastCards.isEmpty() || lastPlayer == turn || passCount >= 2) {
            return Judge.judge(cards).isValid();
        }
        return Judge.canBeat(cards, lastCards);
    }

    //此方法的目的是把一名玩家出的牌放到桌面中央
    private void playCards(int playerIndex, ArrayList<Card> cards) {
        playerList.get(playerIndex).removeAll(cards);
        discardList.get(playerIndex).clear();
        discardList.get(playerIndex).addAll(cards);
        lastCards.clear();
        lastCards.addAll(cards);
        lastPlayer = playerIndex;
        passCount = 0;
        statusLabel.setText(playerName(playerIndex) + " 出牌：" + describeCards(cards));
        renderTable();
        checkWinner(playerIndex);
    }

    //此方法的目的是处理玩家不要
    private void passTurn() {
        if (turn != 1 || gameOver) {
            return;
        }
        if (lastCards.isEmpty() || lastPlayer == 1) {
            JOptionPane.showMessageDialog(this, "当前你有牌权，不能不要。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Judge.hasPlayableCards(playerList.get(1), lastCards)) {
            JOptionPane.showMessageDialog(this, "你有可以压过上一手的牌，跑得快规则下不能不要。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        passCount++;
        statusLabel.setText("玩家 不要");
        nextTurn();
        renderTable();
        computerTurnLater();
    }

    //此方法的目的是在玩家出牌后推动下一位玩家行动
    private void continueAfterPlay() {
        if (gameOver) {
            return;
        }
        nextTurn();
        updateControls();
        computerTurnLater();
    }

    //此方法的目的是切换到下一名玩家
    private void nextTurn() {
        turn = (turn + 1) % 3;
        if (passCount >= 2) {
            lastCards.clear();
            clearDiscardPiles();
            passCount = 0;
            statusLabel.setText(playerName(turn) + " 获得牌权");
        }
        updateControls();
    }

    //此方法的目的是在一轮结束后清空桌面弃牌堆
    private void clearDiscardPiles() {
        for (ArrayList<Card> cards : discardList) {
            cards.clear();
        }
    }

    //此方法的目的是延迟执行电脑回合，让界面变化更容易观察
    private void computerTurnLater() {
        if (gameOver || turn == 1) {
            return;
        }

        Timer timer = new Timer(700, e -> {
            ((Timer) e.getSource()).stop();
            computerTurn();
        });
        timer.setRepeats(false);
        timer.start();
    }

    //此方法的目的是执行电脑玩家的出牌或不要
    private void computerTurn() {
        if (gameOver || turn == 1) {
            return;
        }

        ArrayList<Card> cards = Judge.findSmallestBeat(playerList.get(turn), lastCards,
                lastCards.isEmpty() || lastPlayer == turn || passCount >= 2);
        if (cards.isEmpty()) {
            passCount++;
            statusLabel.setText(playerName(turn) + " 不要");
            nextTurn();
            renderTable();
            computerTurnLater();
            return;
        }

        playCards(turn, cards);
        if (!gameOver) {
            nextTurn();
            renderTable();
            computerTurnLater();
        }
    }

    //此方法的目的是检查是否已经有玩家出完手牌
    private void checkWinner(int playerIndex) {
        if (!playerList.get(playerIndex).isEmpty()) {
            return;
        }
        gameOver = true;
        statusLabel.setText(playerName(playerIndex) + " 获胜，点击重新开始再来一局");
        updateControls();
        JOptionPane.showMessageDialog(this, playerName(playerIndex) + " 获胜！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
    }

    //此方法的目的是根据玩家索引获取显示名称
    private String playerName(int index) {
        switch (index) {
            case 0:
                return "电脑A";
            case 1:
                return "玩家";
            case 2:
                return "电脑B";
            default:
                return "";
        }
    }

    //此方法的目的是把出的牌转为文本提示
    private String describeCards(List<Card> cards) {
        StringBuilder builder = new StringBuilder();
        for (Card card : cards) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(card.getm_Number());
        }
        return builder.toString();
    }

    //此方法的目的是根据当前回合启用或禁用玩家按钮
    private void updateControls() {
        boolean playerTurn = turn == 1 && !gameOver;
        playButton.setEnabled(playerTurn);
        passButton.setEnabled(playerTurn && !lastCards.isEmpty() && lastPlayer != 1);
    }
}
