/**
 * 關聯法則
 */
public class AssociationRule {
	private String rule;	        	// 法則字串
	private double coverage;    // 法則涵蓋率
	private double accuracy;    // 法則正確率
	
	/*
	 * 建構子
	 * @param rule 法則字串
	 * @param coverage 涵蓋率
	 * @param accuracy 正確率
	 */
	public AssociationRule(String rule, double coverage, double accuracy) {
		this.rule = rule;
		this.coverage = coverage;
		this.accuracy = accuracy;
	}
	
	/*
	 * 取得法則字串
	 * @return 法則字串
	 */
	public String getRule() {
		return rule;
	}
	
	/*
	 * 取得涵蓋率
	 * @return 涵蓋率
	 */
	public double getCoverage() {
		return coverage;
	}
	
	/*
	 * 取得正確率
	 * @return 正確率
	 */
	public double getAccuracy() {
		return accuracy;
	}
}
