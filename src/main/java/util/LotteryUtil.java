package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author 张虎
 * @createtime 2019年4月17日 上午11:39:41
 */
public class LotteryUtil {
	/**
	 * 抽奖
	 *
	 * @param orignalRates
	 *            原始的概率列表，保证顺序和实际物品对应
	 * @return 物品的索引
	 */
	public static int lottery(List<Integer> orignalRates) {
		if (orignalRates == null || orignalRates.isEmpty()) {
			return -1;
		}

		int size = orignalRates.size();

		// 计算总概率，这样可以保证不一定总概率是1
		int sumRate = 0;
		for (int rate : orignalRates) {
			sumRate += rate;
		}

		// 计算每个物品在总概率的基础下的概率情况
		List<Double> sortOrignalRates = new ArrayList<Double>(size);
		Double tempSumRate = 0d;
		for (double rate : orignalRates) {
			tempSumRate += rate;
			sortOrignalRates.add(tempSumRate / sumRate);
		}

		// 根据区块值来获取抽取到的物品索引
		double nextInteger = Math.random();
		sortOrignalRates.add(nextInteger);
		Collections.sort(sortOrignalRates);
		return sortOrignalRates.indexOf(nextInteger);
	}
}
