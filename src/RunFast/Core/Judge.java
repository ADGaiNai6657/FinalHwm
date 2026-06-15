package RunFast.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Judge { //牌型判断工具类，负责识别牌型、比较大小和寻找可出的牌
    private Judge() {
    }

    //此方法的目的是判断一组牌属于哪种牌型
    public static CardPattern judge(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return CardPattern.invalid(0);
        }

        ArrayList<Card> sortedCards = copyAndSortByRank(cards);
        int size = sortedCards.size();
        Map<Integer, Integer> counts = countRanks(sortedCards);

        //单张、对子、三张、炸弹都属于所有牌点相同的基础牌型
        if (counts.size() == 1) {
            int rank = sortedCards.get(0).getRankValue();
            if (size == 1) {
                return new CardPattern(CardPattern.Type.SINGLE, rank, 1, size);
            }
            if (size == 2) {
                return new CardPattern(CardPattern.Type.PAIR, rank, 1, size);
            }
            if (size == 3) {
                return new CardPattern(CardPattern.Type.TRIPLE, rank, 1, size);
            }
            if (size == 4) {
                return new CardPattern(CardPattern.Type.BOMB, rank, 1, size);
            }
        }

        //三带一：三张相同牌点再带一张单牌
        if (size == 4 && hasCount(counts, 3)) {
            return new CardPattern(CardPattern.Type.TRIPLE_WITH_ONE, rankWithCount(counts, 3), 1, size);
        }

        //三带二：跑得快中允许三张相同牌点带任意两张单牌
        if (size == 5 && hasCount(counts, 3)) {
            return new CardPattern(CardPattern.Type.TRIPLE_WITH_PAIR, rankWithCount(counts, 3), 1, size);
        }

        //四带二：四张相同牌点带任意两张单牌
        if (size == 6 && hasCount(counts, 4)) {
            return new CardPattern(CardPattern.Type.FOUR_WITH_TWO, rankWithCount(counts, 4), 1, size);
        }

        //飞机和飞机带翅膀需要优先于顺子、连对判断
        CardPattern planePattern = judgePlane(counts, size);
        if (planePattern.isValid()) {
            return planePattern;
        }

        //顺子至少五张，且不能包含2
        if (isStraight(counts, size)) {
            return new CardPattern(CardPattern.Type.STRAIGHT, maxRank(counts), size, size);
        }

        //连对至少两对，且不能包含2
        if (isPairStraight(counts, size)) {
            return new CardPattern(CardPattern.Type.PAIR_STRAIGHT, maxRank(counts), size / 2, size);
        }

        return CardPattern.invalid(size);
    }

    //此方法的目的是判断当前牌能否压过上一手牌
    public static boolean canBeat(List<Card> currentCards, List<Card> lastCards) {
        CardPattern current = judge(currentCards);
        if (!current.isValid()) {
            return false;
        }

        if (lastCards == null || lastCards.isEmpty()) {
            return true;
        }

        CardPattern last = judge(lastCards);
        if (!last.isValid()) {
            return true;
        }

        //炸弹可以压任意非炸弹牌型
        if (current.isBomb() && !last.isBomb()) {
            return true;
        }
        if (current.isBomb() != last.isBomb()) {
            return false;
        }
        //非同类牌型不能互相压，炸弹例外已在上方处理
        if (current.getType() != last.getType()) {
            return false;
        }
        if (current.getCardCount() != last.getCardCount()) {
            return false;
        }
        //顺子、连对、飞机等连续牌型必须长度相同才能比较
        if (current.getLength() != last.getLength()) {
            return false;
        }
        return current.getMainRank() > last.getMainRank();
    }

    //此方法的目的是判断手牌中是否存在可以压过上一手的牌
    public static boolean hasPlayableCards(List<Card> handCards, List<Card> lastCards) {
        return !findSmallestBeat(handCards, lastCards, false).isEmpty();
    }

    //此方法的目的是给电脑寻找一组最小可出的牌
    public static ArrayList<Card> findSmallestBeat(List<Card> handCards, List<Card> lastCards, boolean hasControl) {
        ArrayList<Card> hand = copyAndSortByRank(handCards);
        if (hand.isEmpty()) {
            return new ArrayList<>();
        }

        if (hasControl || lastCards == null || lastCards.isEmpty()) {
            return findSmallestLead(hand);
        }

        //没有牌权时，先尝试找同牌型能压过上一手的牌
        CardPattern lastPattern = judge(lastCards);
        if (!lastPattern.isValid()) {
            return findSmallestLead(hand);
        }

        ArrayList<Card> sameTypeCards = findSameTypeBeat(hand, lastPattern);
        if (!sameTypeCards.isEmpty()) {
            return sameTypeCards;
        }

        //同牌型找不到时，非炸弹可以用炸弹压
        if (!lastPattern.isBomb()) {
            return findBombAbove(hand, 0);
        }
        return findBombAbove(hand, lastPattern.getMainRank());
    }

    //此方法的目的是复制并按点数从小到大排序
    public static ArrayList<Card> copyAndSortByRank(List<Card> cards) {
        ArrayList<Card> sortedCards = new ArrayList<>(cards);
        sortedCards.sort(Comparator.comparingInt(Card::getRankValue).thenComparingInt(Card::getm_Value));
        return sortedCards;
    }

    //此方法的目的是在有牌权时尽量出最小单张
    private static ArrayList<Card> findSmallestLead(ArrayList<Card> hand) {
        ArrayList<Card> result = new ArrayList<>();
        result.add(hand.get(0));
        return result;
    }

    //此方法的目的是按上一手牌型寻找同牌型可压的牌
    private static ArrayList<Card> findSameTypeBeat(ArrayList<Card> hand, CardPattern lastPattern) {
        //根据上一手牌型分发到对应的寻找方法
        switch (lastPattern.getType()) {
            case SINGLE:
                return findSameRankGroup(hand, 1, lastPattern.getMainRank());
            case PAIR:
                return findSameRankGroup(hand, 2, lastPattern.getMainRank());
            case TRIPLE:
                return findSameRankGroup(hand, 3, lastPattern.getMainRank());
            case TRIPLE_WITH_ONE:
                return findTripleWithOne(hand, lastPattern.getMainRank());
            case TRIPLE_WITH_PAIR:
                return findTripleWithPair(hand, lastPattern.getMainRank());
            case STRAIGHT:
                return findStraight(hand, lastPattern.getLength(), lastPattern.getMainRank());
            case PAIR_STRAIGHT:
                return findPairStraight(hand, lastPattern.getLength(), lastPattern.getMainRank());
            case PLANE:
                return findPlane(hand, lastPattern.getLength(), lastPattern.getMainRank());
            case PLANE_WITH_SINGLE:
                return findPlaneWithSingle(hand, lastPattern.getLength(), lastPattern.getMainRank());
            case PLANE_WITH_PAIR:
                return findPlaneWithPair(hand, lastPattern.getLength(), lastPattern.getMainRank());
            case FOUR_WITH_TWO:
                return findFourWithTwo(hand, lastPattern.getMainRank());
            case BOMB:
                return findBombAbove(hand, lastPattern.getMainRank());
            default:
                return new ArrayList<>();
        }
    }

    //此方法的目的是寻找指定张数的同点数牌
    private static ArrayList<Card> findSameRankGroup(ArrayList<Card> hand, int count, int minRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        ArrayList<Integer> ranks = sortedRanks(byRank);
        for (int rank : ranks) {
            ArrayList<Card> cards = byRank.get(rank);
            if (rank > minRank && cards.size() >= count) {
                return new ArrayList<>(cards.subList(0, count));
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找三带一
    private static ArrayList<Card> findTripleWithOne(ArrayList<Card> hand, int minRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        for (int rank : sortedRanks(byRank)) {
            if (rank <= minRank || byRank.get(rank).size() < 3) {
                continue;
            }
            ArrayList<Card> result = new ArrayList<>(byRank.get(rank).subList(0, 3));
            Card kicker = findKicker(hand, new HashSet<>(Collections.singletonList(rank)), 1).stream().findFirst().orElse(null);
            if (kicker != null) {
                result.add(kicker);
                return result;
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找三带二，跑得快中可以带任意两张单牌
    private static ArrayList<Card> findTripleWithPair(ArrayList<Card> hand, int minRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        for (int rank : sortedRanks(byRank)) {
            if (rank <= minRank || byRank.get(rank).size() < 3) {
                continue;
            }
            ArrayList<Card> kickers = findKicker(hand, new HashSet<>(Collections.singletonList(rank)), 2);
            if (kickers.size() == 2) {
                ArrayList<Card> result = new ArrayList<>(byRank.get(rank).subList(0, 3));
                result.addAll(kickers);
                return result;
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找顺子
    private static ArrayList<Card> findStraight(ArrayList<Card> hand, int length, int minMainRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        //顺子从3开始查找，最大不能到2
        for (int start = 3; start <= 14 - length + 1; start++) {
            int end = start + length - 1;
            if (end <= minMainRank || end >= 15) {
                continue;
            }
            ArrayList<Card> result = new ArrayList<>();
            for (int rank = start; rank <= end; rank++) {
                ArrayList<Card> cards = byRank.get(rank);
                if (cards == null || cards.isEmpty()) {
                    result.clear();
                    break;
                }
                result.add(cards.get(0));
            }
            if (result.size() == length) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找连对
    private static ArrayList<Card> findPairStraight(ArrayList<Card> hand, int pairLength, int minMainRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        //连对从两对开始支持，比较时要求对数相同
        for (int start = 3; start <= 14 - pairLength + 1; start++) {
            int end = start + pairLength - 1;
            if (end <= minMainRank || end >= 15) {
                continue;
            }
            ArrayList<Card> result = new ArrayList<>();
            for (int rank = start; rank <= end; rank++) {
                ArrayList<Card> cards = byRank.get(rank);
                if (cards == null || cards.size() < 2) {
                    result.clear();
                    break;
                }
                result.addAll(cards.subList(0, 2));
            }
            if (result.size() == pairLength * 2) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找飞机
    private static ArrayList<Card> findPlane(ArrayList<Card> hand, int length, int minMainRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        //飞机只取连续的三张组合，不允许2参与
        ArrayList<Integer> tripleRanks = ranksWithAtLeast(byRank, 3);
        ArrayList<Integer> sequence = findRankSequence(tripleRanks, length, minMainRank);
        if (sequence.isEmpty()) {
            return new ArrayList<>();
        }
        return collectSameRankCards(byRank, sequence, 3);
    }

    //此方法的目的是寻找飞机带单牌
    private static ArrayList<Card> findPlaneWithSingle(ArrayList<Card> hand, int length, int minMainRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        ArrayList<Integer> tripleRanks = ranksWithAtLeast(byRank, 3);
        ArrayList<Integer> sequence = findRankSequence(tripleRanks, length, minMainRank);
        if (sequence.isEmpty()) {
            return new ArrayList<>();
        }

        HashSet<Integer> excludedRanks = new HashSet<>(sequence);
        //翅膀不能从飞机本身的三张里拆出来
        ArrayList<Card> kickers = findKicker(hand, excludedRanks, length);
        if (kickers.size() != length) {
            return new ArrayList<>();
        }

        ArrayList<Card> result = collectSameRankCards(byRank, sequence, 3);
        result.addAll(kickers);
        return result;
    }

    //此方法的目的是寻找飞机带对子
    private static ArrayList<Card> findPlaneWithPair(ArrayList<Card> hand, int length, int minMainRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        ArrayList<Integer> tripleRanks = ranksWithAtLeast(byRank, 3);
        ArrayList<Integer> sequence = findRankSequence(tripleRanks, length, minMainRank);
        if (sequence.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Integer> pairRanks = new ArrayList<>();
        //飞机带对子时，翅膀对子不能使用飞机本身的牌点
        for (int rank : sortedRanks(byRank)) {
            if (!sequence.contains(rank) && byRank.get(rank).size() >= 2) {
                pairRanks.add(rank);
            }
        }
        if (pairRanks.size() < length) {
            return new ArrayList<>();
        }

        ArrayList<Card> result = collectSameRankCards(byRank, sequence, 3);
        for (int i = 0; i < length; i++) {
            result.addAll(byRank.get(pairRanks.get(i)).subList(0, 2));
        }
        return result;
    }

    //此方法的目的是寻找炸弹
    private static ArrayList<Card> findBombAbove(ArrayList<Card> hand, int minRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        for (int rank : sortedRanks(byRank)) {
            ArrayList<Card> cards = byRank.get(rank);
            if (rank > minRank && cards.size() == 4) {
                return new ArrayList<>(cards);
            }
        }
        return new ArrayList<>();
    }

    //此方法的目的是寻找四带二
    private static ArrayList<Card> findFourWithTwo(ArrayList<Card> hand, int minRank) {
        Map<Integer, ArrayList<Card>> byRank = groupByRank(hand);
        for (int rank : sortedRanks(byRank)) {
            ArrayList<Card> cards = byRank.get(rank);
            if (rank <= minRank || cards.size() < 4) {
                continue;
            }
            ArrayList<Card> kickers = findKicker(hand, new HashSet<>(Collections.singletonList(rank)), 2);
            if (kickers.size() == 2) {
                ArrayList<Card> result = new ArrayList<>(cards.subList(0, 4));
                result.addAll(kickers);
                return result;
            }
        }
        return new ArrayList<>();
    }

    private static Map<Integer, Integer> countRanks(List<Card> cards) {
        //统计每个点数出现的次数，供牌型判断使用
        Map<Integer, Integer> counts = new HashMap<>();
        for (Card card : cards) {
            counts.put(card.getRankValue(), counts.getOrDefault(card.getRankValue(), 0) + 1);
        }
        return counts;
    }

    private static Map<Integer, ArrayList<Card>> groupByRank(List<Card> cards) {
        //按点数分组，供电脑寻找可出牌使用
        Map<Integer, ArrayList<Card>> byRank = new HashMap<>();
        for (Card card : cards) {
            byRank.computeIfAbsent(card.getRankValue(), key -> new ArrayList<>()).add(card);
        }
        return byRank;
    }

    private static ArrayList<Integer> sortedRanks(Map<Integer, ArrayList<Card>> byRank) {
        ArrayList<Integer> ranks = new ArrayList<>(byRank.keySet());
        Collections.sort(ranks);
        return ranks;
    }

    private static boolean hasCount(Map<Integer, Integer> counts, int targetCount) {
        return counts.containsValue(targetCount);
    }

    private static int rankWithCount(Map<Integer, Integer> counts, int targetCount) {
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == targetCount) {
                return entry.getKey();
            }
        }
        return 0;
    }

    private static int maxRank(Map<Integer, Integer> counts) {
        int maxRank = 0;
        for (int rank : counts.keySet()) {
            maxRank = Math.max(maxRank, rank);
        }
        return maxRank;
    }

    private static boolean isStraight(Map<Integer, Integer> counts, int size) {
        if (size < 5 || counts.size() != size || counts.containsKey(15)) {
            return false;
        }
        return isContinuous(counts.keySet());
    }

    private static boolean isPairStraight(Map<Integer, Integer> counts, int size) {
        if (size < 4 || size % 2 != 0 || counts.containsKey(15)) {
            return false;
        }
        for (int count : counts.values()) {
            if (count != 2) {
                return false;
            }
        }
        return isContinuous(counts.keySet());
    }

    private static CardPattern judgePlane(Map<Integer, Integer> counts, int size) {
        ArrayList<Integer> tripleRanks = new ArrayList<>();
        //飞机的主体必须是连续的三张，2不能参与飞机
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getKey() < 15 && entry.getValue() >= 3) {
                tripleRanks.add(entry.getKey());
            }
        }
        Collections.sort(tripleRanks);

        //优先尝试最长的飞机主体，避免多组三张时误取短飞机
        for (int length = tripleRanks.size(); length >= 2; length--) {
            for (int startIndex = 0; startIndex <= tripleRanks.size() - length; startIndex++) {
                ArrayList<Integer> sequence = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    sequence.add(tripleRanks.get(startIndex + i));
                }
                if (!isContinuous(sequence)) {
                    continue;
                }

                int mainRank = sequence.get(sequence.size() - 1);
                //纯飞机：333444
                if (size == length * 3) {
                    return new CardPattern(CardPattern.Type.PLANE, mainRank, length, size);
                }
                //飞机带单牌：33344456
                if (size == length * 4 && hasEnoughSingleWings(counts, sequence, length)) {
                    return new CardPattern(CardPattern.Type.PLANE_WITH_SINGLE, mainRank, length, size);
                }
                //飞机带对子：3334445566
                if (size == length * 5 && hasEnoughPairWings(counts, sequence, length)) {
                    return new CardPattern(CardPattern.Type.PLANE_WITH_PAIR, mainRank, length, size);
                }
            }
        }

        return CardPattern.invalid(size);
    }

    private static boolean isContinuous(Iterable<Integer> ranks) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int rank : ranks) {
            list.add(rank);
        }
        Collections.sort(list);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) - list.get(i - 1) != 1) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<Card> findKicker(ArrayList<Card> hand, HashSet<Integer> excludedRanks, int count) {
        //从小牌开始找附带牌，尽量保留大牌
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : hand) {
            if (!excludedRanks.contains(card.getRankValue())) {
                result.add(card);
                if (result.size() == count) {
                    return result;
                }
            }
        }
        return result;
    }

    private static boolean hasEnoughSingleWings(Map<Integer, Integer> counts, ArrayList<Integer> tripleRanks, int neededCount) {
        int count = 0;
        //扣除飞机主体后，剩余牌数量足够即可带单牌
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int rank = entry.getKey();
            int cardCount = entry.getValue();
            if (tripleRanks.contains(rank)) {
                cardCount -= 3;
            }
            count += Math.max(0, cardCount);
        }
        return count >= neededCount;
    }

    private static boolean hasEnoughPairWings(Map<Integer, Integer> counts, ArrayList<Integer> tripleRanks, int neededPairCount) {
        int count = 0;
        //扣除飞机主体后，剩余牌按对子数量计算
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int rank = entry.getKey();
            int cardCount = entry.getValue();
            if (tripleRanks.contains(rank)) {
                cardCount -= 3;
            }
            count += Math.max(0, cardCount) / 2;
        }
        return count >= neededPairCount;
    }

    private static ArrayList<Integer> ranksWithAtLeast(Map<Integer, ArrayList<Card>> byRank, int count) {
        ArrayList<Integer> ranks = new ArrayList<>();
        for (int rank : sortedRanks(byRank)) {
            if (rank < 15 && byRank.get(rank).size() >= count) {
                ranks.add(rank);
            }
        }
        return ranks;
    }

    private static ArrayList<Integer> findRankSequence(ArrayList<Integer> ranks, int length, int minMainRank) {
        //寻找一段长度相同且最大点数更大的连续点数组
        for (int startIndex = 0; startIndex <= ranks.size() - length; startIndex++) {
            ArrayList<Integer> sequence = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                sequence.add(ranks.get(startIndex + i));
            }
            if (isContinuous(sequence) && sequence.get(sequence.size() - 1) > minMainRank) {
                return sequence;
            }
        }
        return new ArrayList<>();
    }

    private static ArrayList<Card> collectSameRankCards(Map<Integer, ArrayList<Card>> byRank, ArrayList<Integer> ranks, int count) {
        ArrayList<Card> result = new ArrayList<>();
        for (int rank : ranks) {
            result.addAll(byRank.get(rank).subList(0, count));
        }
        return result;
    }

}
