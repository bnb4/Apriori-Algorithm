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
		newCandidate(1, null);
	}
	
	private void newCandidate(int round,	 Map<Map<String, String>, Double> preCandidate) {
		
		List<Map<String, String>> pairedList;
		if (round == 1) {
			pairedList = firstPair();
		} else {
			pairedList = pair(round, preCandidate.keySet());
		}
		 
		Map<Map<String, String>, Double> candidate = new HashMap<Map<String, String>, Double>();
		for (Map<String, String> paired : pairedList) {
			double support = calculateSupport(paired);
			if (support >= minSupport) {
				candidate.put(paired, support);
			}
		}
		
		if (candidate.size() == 0) {
			return;
		}
		
		gui.setResultData(candidate);
		if (round != 1) {
			gui.setResultData(mineRules(candidate));
		}
		newCandidate(round+1, candidate);
	}
	
	private List<Map<String, String>> firstPair() {
		List<Map<String, String>> pairedList = new ArrayList<Map<String,String>>();
		
		for (String attribute : attributes.keySet()) {
			for (String value : attributes.get(attribute)) {
				Map<String,String> temp = new HashMap<String,String>();
				temp.put(attribute, value);
				pairedList.add(temp);
			}
		}
		return pairedList;
	}
	
	private List<Map<String, String>> pair(int targetNum, Set<Map<String,String>> prePair) {
		Map<String, String>[] single = mapSetToArray(prePair);
		List<Map<String, String>> pairedList = new ArrayList<Map<String,String>>();
		
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
	
	private Map<String, String>[] mapSetToArray(Set<Map<String, String>> set) {
		Map<String, String>[] array = set.toArray(new Map[set.size()]);
		return array;
	}
	
	private double calculateSupport(Map<String, String> target) {
		int matchData = 0;
		for (Map<String, String> d : data) {
			int matchAtt = 0;
			for (String key : target.keySet()) {
				if (d.get(key).equals(target.get(key))) {
					matchAtt++;
				}
			}
			if (matchAtt == target.size()) {
				matchData++;
			}
		}
		return matchData * 1.0 / data.size();
	}
	
	private AssociationRule[] mineRules(Map<Map<String, String>, Double> association) {
		List<AssociationRule> rules = new ArrayList<AssociationRule>();
		
		for (Map<String, String> associationItems : association.keySet()) {
			double support = association.get(associationItems);
			for (String attribute : associationItems.keySet()) {
				Map<String, String> targetA = new HashMap<String, String>(associationItems);
				targetA.remove(attribute);
				Map<String, String> targetB = new HashMap<String, String>();
				targetB.put(attribute, associationItems.get(attribute));
				
				String ruleString = toRuleString(targetA, targetB);
				double confidence = calculateConfidence(targetA, targetB);
				
				rules.add(new AssociationRule(ruleString, support, confidence));
			}	
		}
		
		return rules.toArray(new AssociationRule[rules.size()]);
	}
	
	private String toRuleString(Map<String, String> targetA, Map<String, String> targetB) {
		String rule = "{ ";
		for (String attribute : targetA.keySet()) {
			rule += attribute + " = " + targetA.get(attribute) + " , ";
		}
		rule = rule.substring(0, rule.length() - 2);
		rule += "} -> { ";
		for (String attribute : targetB.keySet()) {
			rule += attribute + " = " + targetB.get(attribute) + " , ";
		}
		rule = rule.substring(0, rule.length() - 2);
		rule += "}";
		
		return rule;
	}
	
	private double calculateConfidence(Map<String, String> targetA, Map<String, String> targetB) {
		int supportData = 0;
		int matchData = 0;
		for (Map<String, String> d : data) {
			int matchAtt = 0;
			for (String key : targetA.keySet()) {
				if (d.get(key).equals(targetA.get(key))) {
					matchAtt++;
				}
			}
			if (matchAtt != targetA.size()) {
				continue;
			}
			supportData++;
			
			matchAtt = 0;
			for (String key : targetB.keySet()) {
				if (d.get(key).equals(targetB.get(key))) {
					matchAtt++;
				}
			}
			if (matchAtt == targetB.size()) {
				matchData++;
			}
		}
		return Math.rint(matchData * 100.0 / supportData)/100;
	}
}