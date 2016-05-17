import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		// 這是使用的DEMO
		FileParser fileParser = new FileParser("demo1.txt");
		AttributeData [] attributeDatas = fileParser.getAllData();
		AprioriCoverageCalc aprioriCoverageCalc = new AprioriCoverageCalc(attributeDatas);
		
		// 定義涵蓋率
		double minCoverage = 0.4;
		
		// 取得 Single-Set
		Map<String, Double> singleSetItems = new HashMap<>();
		
		// 初始化 Single-Set
		for (AttributeData ad : attributeDatas) {
			singleSetItems.put(ad.getAttributeName() + ":True", 0.0);
			singleSetItems.put(ad.getAttributeName() + ":False", 0.0);
		}
		
		// 檢查若有低於涵蓋率的，剃除
		for (String filter : singleSetItems.keySet().toArray(new String[singleSetItems.size()])) {
			double coverage = aprioriCoverageCalc.getCoverage(filter);
			if (coverage < minCoverage) singleSetItems.remove(filter);
			else singleSetItems.put(filter, coverage);
		}
		
		// 印出符合的 Single-Set Items
		System.out.println("Single-Set Items:");
		for (String item : singleSetItems.keySet()) {
			String [] splItem = item.split(":", 2);
			System.out.format("%s = %s  Coverage: %.01f\r\n", splItem[0], splItem[1], singleSetItems.get(item));
		}
		System.out.println("\r\n");
		
		// 取得 Double-Set
		Map<String, Double> doubleSetItems = new HashMap<>();
		
		// 初始化 Double-Set
		String [] singleAttributes = singleSetItems.keySet().toArray(new String[singleSetItems.size()]);
		for (int i = 0; i < singleAttributes.length; i++) 
			for (int j = i + 1; j < singleAttributes.length; j++) 
				if (!singleAttributes[i].equals(singleAttributes[j]))
					doubleSetItems.put(singleAttributes[i] + "&" + singleAttributes[j], 0.0);

		// 檢查若有低於涵蓋率的，剃除
		for (String filter : doubleSetItems.keySet().toArray(new String[doubleSetItems.size()])) {
			double coverage = aprioriCoverageCalc.getCoverage(filter);
			if (coverage < minCoverage) doubleSetItems.remove(filter);
			else doubleSetItems.put(filter, coverage);
		}		
		
		// 印出符合的 Double-Set Items
		System.out.println("Double-Set Items:");
		for (String item : doubleSetItems.keySet()) {
			String [] splItem = item.split("&", 2);
			String [] splItem1 = splItem[0].split(":", 2);
			String [] splItem2 = splItem[1].split(":", 2);
			System.out.format("%s = %s & %s = %s Coverage: %.01f\r\n", splItem1[0], splItem1[1], splItem2[0], splItem2[1], doubleSetItems.get(item));
		}
		System.out.println("\r\n");
	}

}
