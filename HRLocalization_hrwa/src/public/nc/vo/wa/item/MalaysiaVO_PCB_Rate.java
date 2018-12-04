package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class MalaysiaVO_PCB_Rate extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pcb_group;
	private String pcb_formula;
	private String pcb_formulaname;
	private UFDouble lower_limit;
	private UFDouble upper_limit;
	private UFDouble pcb_m;//速算基数
	private UFDouble pcb_rate;//税率
	private UFDouble pcb_category1_3;
	private UFDouble pcb_category2;
	public String getPcb_group() {
		return pcb_group;
	}
	public void setPcb_group(String pcb_group) {
		this.pcb_group = pcb_group;
	}
	public String getPcb_formula() {
		return pcb_formula;
	}
	public void setPcb_formula(String pcb_formula) {
		this.pcb_formula = pcb_formula;
	}
	public String getPcb_formulaname() {
		return pcb_formulaname;
	}
	public void setPcb_formulaname(String pcb_formulaname) {
		this.pcb_formulaname = pcb_formulaname;
	}
	public UFDouble getLower_limit() {
		return lower_limit;
	}
	public void setLower_limit(UFDouble lower_limit) {
		this.lower_limit = lower_limit;
	}
	public UFDouble getUpper_limit() {
		return upper_limit;
	}
	public void setUpper_limit(UFDouble upper_limit) {
		this.upper_limit = upper_limit;
	}
	public UFDouble getPcb_m() {
		return pcb_m;
	}
	public void setPcb_m(UFDouble pcb_m) {
		this.pcb_m = pcb_m;
	}
	public UFDouble getPcb_rate() {
		return pcb_rate;
	}
	public void setPcb_rate(UFDouble pcb_rate) {
		this.pcb_rate = pcb_rate;
	}
	public UFDouble getPcb_category1_3() {
		return pcb_category1_3;
	}
	public void setPcb_category1_3(UFDouble pcb_category1_3) {
		this.pcb_category1_3 = pcb_category1_3;
	}
	public UFDouble getPcb_category2() {
		return pcb_category2;
	}
	public void setPcb_category2(UFDouble pcb_category2) {
		this.pcb_category2 = pcb_category2;
	}
	
	@Override
	public String getTableName() {
		return "wa_mypcb_rate";
	}
	
	
}
