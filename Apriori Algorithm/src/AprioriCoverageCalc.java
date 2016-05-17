import java.util.HashSet;
import java.util.Set;

/**
 * 用來比對多個屬性間的涵蓋率
 */
public class AprioriCoverageCalc {
	
	// 記錄所有的資料
	private AttributeData [] attributeDatas;
	
	// 紀錄有幾筆資料
	private int dataCount = 0;
	
	/**
	 * 儲存過濾器格式
	 */
	public static class Filter {
		private String attributeName;
		private boolean type;
		public Filter(String attributeName, boolean type) {
			this.attributeName = attributeName;
			this.type = type;
		}
		
		public String getAttributeName() {
			return this.attributeName;
		}
		
		public boolean getType() {
			return this.type;
		}
	}
	
	/**
	 * 建構子
	 * @param attributeDatas 所有資料
	 */
	public AprioriCoverageCalc(AttributeData [] attributeDatas) {
		this.attributeDatas = attributeDatas;
		this.dataCount = this.attributeDatas[0].getDataCount();
	}
	
	public AttributeData getAttributeDataByName(String attributeName) {
		
		// 若未正確初始，擋下
		if (attributeDatas == null || attributeDatas.length == 0) return null;
		
		// 比較每個項目，符合就返回
		for (AttributeData ad : attributeDatas) 
			if (ad.getAttributeName().equals(attributeName)) return ad;
		
		return null;
	}
	
	/**
	 * 取得涵蓋率
	 * @param filters 過濾格式 "attA:True", "attC:False"...
	 * @return 涵蓋率
	 */
	public double getCoverage(String ... filters) {
		
		// 若未正確初始，擋下
		if (attributeDatas == null || attributeDatas.length == 0) return 0.0;
		
		// 沒有傳入資料 (空陣列)
		if (filters.length == 0) return 0.0;
		
		// 暫存轉換後物件陣列
		Filter parsedFilters [] = new Filter[filters.length];
		
		// 轉換 Filter 格式
		for (int i = 0; i < filters.length; i++) {
			String [] spl = filters[i].trim().split(":", 2);
			parsedFilters[i] = new Filter(spl[0], Boolean.parseBoolean(spl[1]));
		}

		return getCoverage(parsedFilters);
	}
	
	/**
	 * 取得涵蓋率
	 * @param filters 過濾格式 
	 * @return 涵蓋率
	 */
	public double getCoverage(Filter ... filters) {
		
		// 若未正確初始，擋下
		if (attributeDatas == null || attributeDatas.length == 0) return 0.0;
		
		// 沒有傳入資料 (空陣列)
		if (filters.length == 0) return 0.0;
				
		// 若只有一項直接使用自身的方法
		if (filters.length == 1) {
			Filter filter = filters[0];
			AttributeData ad = getAttributeDataByName(filter.attributeName);
			if (ad == null) return 0.0;
			else return ad.getCoverage(filter.getType());
		}
		
		// 若有多項，每項開始比對
		// 使用到的屬性數量
		int useAttributeCount = filters.length;
		
		// 取得有用到的 AttributeData
		AttributeData [] useAttributeDatas = new AttributeData [useAttributeCount];
		for (int i = 0; i < useAttributeCount; i++)
			useAttributeDatas[i] = getAttributeDataByName(filters[i].getAttributeName());
		
		// 紀錄目前符合的 id
		Set<Integer> remainderIds = new HashSet<>();
		
		// 使用第一個 filter 來初始 remainderIds
		AttributeData firstAD = useAttributeDatas[0];
		Filter firstFilter = filters[0];
		for (int i = 0; i < dataCount; i++)
			if (firstAD.getData(i) == firstFilter.getType())
				remainderIds.add(i);
		
		// 使用第二第三..個來過濾符合的 ID
		for (int filterIdx = 1; filterIdx < useAttributeCount; filterIdx++) 
			// 每個 id 都測一次
			for (int id : remainderIds.stream().mapToInt(i->i).toArray()) 
				// 此屬性不符合，拿掉該筆資料 id
				if (useAttributeDatas[filterIdx].getData(id) != filters[filterIdx].getType()) 
					remainderIds.remove(id);

		return remainderIds.size() / dataCount;
	}
	
	
}
