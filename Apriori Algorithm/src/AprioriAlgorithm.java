import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 	Apriori演算法
 */
public class AprioriAlgorithm {
	
	private static AprioriAlgorithm aprioriAlgorithm;  // 獨體參考
	private double minSupport;                         // 最低接受涵蓋率
	private Gui gui;                                   // 介面參考
	
	private Map<String, String[]> attributes;          // 所有屬性與其可能值
	private List<Map<String, String>> data;            // 資料集
	
	/*
	 * 建構子
	 */
	private AprioriAlgorithm() {}
	
	/*
	 * 取得演算法物件
	 * @return 演算法獨體物件參考
	 */
	public static AprioriAlgorithm get() {
		aprioriAlgorithm = aprioriAlgorithm == null ? new AprioriAlgorithm() : aprioriAlgorithm;
		return aprioriAlgorithm;
	}
	
	/*
	 * 傳入所有屬性與其可能值
	 * @param attributes 所有屬性與其可能值
	 */
	public void setAttributes(Map<String, String[]> attributes) {
		this.attributes = attributes;
	}
	
	/*
	 * 傳入資料集
	 * @param data 資料集
	 */
	public void setDatas(List<Map<String, String>> data) {
		this.data = data;
	}
	
	/*
	 * 傳入最低接受涵蓋率
	 * @param minSupport 最低接受涵蓋率
	 */
	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}
	
	/*
	 * 執行Apriori演算法
	 * @param gui 介面參考
	 */
	public void start(Gui gui) {
		this.gui = gui;
		newCandidate(1, null);  // 遞迴做Apriori演算法
	}
	
	/*
	 * N維Apriori演算法
	 * @param round 維度
	 * @param preCandidate 前一維的候選集
	 */
	private void newCandidate(int round,	 Map<Map<String, String>, Double> preCandidate) {
		
		// 本次候選集清單
		List<Map<String, String>> pairedList;
		if (round == 1) {
			// 處理原始資料成為本次候選集清單
			pairedList = firstPair();
		} else {
			// 配對前一維候選集成為本次候選集清單
			pairedList = pair(round, preCandidate.keySet());
		}
		 
		// 利用涵蓋率篩選候選集清單成為正式候選集
		Map<Map<String, String>, Double> candidate = new HashMap<Map<String, String>, Double>();
		for (Map<String, String> paired : pairedList) {
			// 涵蓋率大於等於minSupport則將本候選項目加入正式候選集
			double support = calculateSupport(paired);
			if (support >= minSupport) {
				candidate.put(paired, support);
			}
		}
		
		// 如果已無候選項目則Apriori演算法結束
		if (candidate.size() == 0) {
			return;
		}
		
		// 將本次各候選集涵蓋率傳給介面
		gui.setResultData(candidate);
		
		// 將本次得到之關聯法則傳給介面
		if (round != 1) {
			gui.setResultData(mineRules(candidate));
		}
		
		// 進行下一維度Apriori演算法
		newCandidate(round+1, candidate);
	}
	
	/*
	 * 處理原始資料成為一維候選集清單
	 * @return 一維候選集清單
	 */
	private List<Map<String, String>> firstPair() {
		List<Map<String, String>> pairedList = new ArrayList<Map<String,String>>();
		
		// 將所有屬性與其可能值配對加入候選集清單
		for (String attribute : attributes.keySet()) {
			for (String value : attributes.get(attribute)) {
				Map<String,String> temp = new HashMap<String,String>();
				temp.put(attribute, value);
				pairedList.add(temp);
			}
		}
		return pairedList;
	}
	
	/*
	 * 配對前一維候選集成為本次候選集清單
	 * @param targetNum 維度
	 * @param prePair 上一維候選集
	 * @return 候選集清單
	 */
	private List<Map<String, String>> pair(int targetNum, Set<Map<String,String>> prePair) {
		Map<String, String>[] single = mapSetToArray(prePair);
		List<Map<String, String>> pairedList = new ArrayList<Map<String,String>>();
		
		// 將上一維候選集配對後符合維度則加入新候選集清單
		for (int i = 0; i < prePair.size(); i++) {
			for (int j = i+1; j < prePair.size(); j++) {	
				Map<String,String> temp = new HashMap<String,String>();
				temp.putAll(single[i]);
				temp.putAll(single[j]);
				
				if (temp.size() == targetNum) {
					pairedList.add(temp);
				}
			}
		}
		return pairedList;
	}
	
	/*
	 * Map集合轉陣列
	 * @param set 集合
	 * @return 陣列
	 */
	private Map<String, String>[] mapSetToArray(Set<Map<String, String>> set) {
		Map<String, String>[] array = set.toArray(new Map[set.size()]);
		return array;
	}
	
	/*
	 * 計算涵蓋率
	 * @param target 目標項目
	 * @return 涵蓋率
	 */
	private double calculateSupport(Map<String, String> target) {
		int matchData = 0;
		for (Map<String, String> d : data) {
			if (isMatch(d, target)) {
				matchData++;
			}
		}
		return matchData * 1.0 / data.size();
	}
	
	/*
	 * 目標項目是否完全存在於資料中
	 * @param data 資料
	 * @param target 目標項目
	 * @return true:完全存在 false:不完全存在
	 */
	private boolean isMatch(Map<String, String> data, Map<String, String> target) {
		int matchNum = 0;
		for (String key : target.keySet()) {
			if (data.get(key).equals(target.get(key))) {
				matchNum++;
			}
		}	
		return matchNum == target.size() ? true : false;
	}
	
	/*
	 * 挖掘關聯法則
	 * @param association 候選集
	 * @return 關聯法則陣列
	 */
	private AssociationRule[] mineRules(Map<Map<String, String>, Double> association) {
		List<AssociationRule> rules = new ArrayList<AssociationRule>();
		
		for (Map<String, String> associationItems : association.keySet()) {
			// 本組合支持率
			double support = association.get(associationItems);
			// 本組合各種排列
			for (String attribute : associationItems.keySet()) {
				// 將候選集分為法則前項與後項
				Map<String, String> antecedent = new HashMap<String, String>(associationItems);
				antecedent.remove(attribute);
				Map<String, String> consequent = new HashMap<String, String>();
				consequent.put(attribute, associationItems.get(attribute));
				
				// 法則內容與其正確率
				String ruleString = toRuleString(antecedent, consequent);
				double confidence = calculateConfidence(antecedent, consequent);
				
				rules.add(new AssociationRule(ruleString, support, confidence));
			}	
		}
		return rules.toArray(new AssociationRule[rules.size()]);
	}
	
	/*
	 * 取得關聯法則字串
	 * @param antecedent 法則前項
	 * @param consequent 法則後項
	 * @return 法則內容字串
	 */
	private String toRuleString(Map<String, String> antecedent, Map<String, String> consequent) {
		String rule = "{ ";
		for (String attribute : antecedent.keySet()) {
			rule += attribute + " = " + antecedent.get(attribute) + " , ";
		}
		rule = rule.substring(0, rule.length() - 2);
		rule += "} -> { ";
		for (String attribute : consequent.keySet()) {
			rule += attribute + " = " + consequent.get(attribute) + " , ";
		}
		rule = rule.substring(0, rule.length() - 2);
		rule += "}";
		
		return rule;
	}
	
	/*
	 * 計算關聯法則正確率
	 * @param antecedent 法則前項
	 * @param consequent 法則後項
	 * @return 正確率
	 */
	private double calculateConfidence(Map<String, String> antecedent, Map<String, String> consequent) {
		int supportData = 0;
		int matchData = 0;
		
		for (Map<String, String> d : data) {
			if (!isMatch(d, antecedent)) {
				continue;
			}
			supportData++;
			if (isMatch(d, consequent)) {
				matchData++;
			}
		}
		return Math.rint(matchData * 100.0 / supportData)/100; // 取兩位小數
	}
}