import java.util.ArrayList;
import java.util.List;

/**
 * 用來儲存某個屬性的所有資料
 *
 */
public class AttributeData {

	// 屬性名稱
	private String attributeName = "N/A";
	
	// 儲存結構
	private List<Boolean> list = new ArrayList<>();
	
	/**
	 * 建構子
	 * @param attributeName 該屬性的名稱
	 */
	public AttributeData(String attributeName) {
		this.attributeName = attributeName;
	}
	
	/**
	 * 取得屬性名稱
	 * @return 屬性名稱
	 */
	public String getAttributeName() {
		return attributeName;
	}
	
	/**
	 * 新增一筆資料
	 * @param data 資料內容
	 * @return 該資料的 id ( 從 0 開始 )
	 */
	public int addData(boolean data) {
		list.add(data);
		return list.size() - 1;
	}
	
	/**
	 * 取得一筆資料
	 * @param id 該資料的 id
	 * @return 該資料的內容
	 */
	public boolean getData(int id) {
		if (id >= list.size()) return false;
		return list.get(id);
	}

	/**
	 * 取得有幾筆資料
	 * @return 資料數量
	 */
	public int getDataCount() {
		return list.size();
	}
	
	/**
	 * 取得自身涵蓋率
	 * @param type 要取得 true 還是 false 的涵蓋率
	 * @return 涵蓋率
	 */
	public double getCoverage(boolean type) {
		// 防止除 0
		if (list.size() == 0) return 0.0;
		
		int counter = 0;
		for (boolean b : list) if (b == type) counter++;
		return counter / list.size();
	}
	
}
