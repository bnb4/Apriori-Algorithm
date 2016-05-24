public class AssociationRule {
	private String rule;
	private double coverage;
	private double accuracy;
	
	public AssociationRule(String rule, double coverage, double accuracy) {
		this.rule = rule;
		this.coverage = coverage;
		this.accuracy = accuracy;
	}
	
	public String getRule() {
		return rule;
	}
	
	public double getCoverage() {
		return coverage;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
}
