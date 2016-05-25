import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AprioriAlgorithm {
	
	private static AprioriAlgorithm aprioriAlgorithm;
	private double minSupport;
	private Gui gui;
	
	private Map<String, String[]> attributes;
	private List<Map<String, String>> data;
	
	private AprioriAlgorithm() {}
	
	public static AprioriAlgorithm get() {
		aprioriAlgorithm = aprioriAlgorithm == null ? new AprioriAlgorithm() : aprioriAlgorithm;
		return aprioriAlgorithm;
	}
	
	public void setAttributes(Map<String, String[]> attributes) {
		this.attributes = attributes;
	}
	
	public void setDatas(List<Map<String, String>> data) {
		this.data = data;
	}
	
	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}
	
	public void start(Gui gui) {
		this.gui = gui;
		Map<Map<String, String>, Double> result = newCandidate(1, null);
		mineRules(result);
		gui.setResultData(new AssociationRule[] {new AssociationRule("A -> B", 0.6, 0.8)});
	}
	
	private Map<Map<String, String>, Double> newCandidate(int round, 
			Map<Map<String, String>, Double> preCandidate) {
		
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
			return preCandidate;
		}
		
		gui.setResultData(candidate);
		return newCandidate(round+1, candidate);
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
	
	private AssociationRule[] mineRules(Map<Map<String, String>, Double> result) {
		return null;
	}
}