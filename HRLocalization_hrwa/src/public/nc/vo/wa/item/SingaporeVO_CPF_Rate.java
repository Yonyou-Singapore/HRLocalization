package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
/**
 * 
 * @author weiningc
 *
 */
public class SingaporeVO_CPF_Rate extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String orgtype;
	private String contribution_type;
	private Integer age_lower;
	private Integer age_upper;
	private UFDouble ow_ceilling;
	private UFDouble aw_fixvalue;
	private UFDouble totalwage_lower;
	private UFDouble totalwage_upper;
	private String totalcpf_formula;
	private String employeecpf_formula;
	private String cpfcode;
	private String cyear;
	private UFDate effective_date;
	private UFDate expiration_date;
	private String owee_formla;
	private String owtotal_formula;
	private String awee_formula;
	private String awtotal_formula;
	
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	public String getContribution_type() {
		return contribution_type;
	}
	public void setContribution_type(String contribution_type) {
		this.contribution_type = contribution_type;
	}
	public Integer getAge_lower() {
		return age_lower;
	}
	public void setAge_lower(Integer age_lower) {
		this.age_lower = age_lower;
	}
	public Integer getAge_upper() {
		return age_upper;
	}
	public void setAge_upper(Integer age_upper) {
		this.age_upper = age_upper;
	}
	
	public UFDouble getTotalwage_lower() {
		return totalwage_lower;
	}
	public void setTotalwage_lower(UFDouble totalwage_lower) {
		this.totalwage_lower = totalwage_lower;
	}
	public UFDouble getTotalwage_upper() {
		return totalwage_upper;
	}
	public void setTotalwage_upper(UFDouble totalwage_upper) {
		this.totalwage_upper = totalwage_upper;
	}
	public String getTotalcpf_formula() {
		return totalcpf_formula;
	}
	public void setTotalcpf_formula(String totalcpf_formula) {
		this.totalcpf_formula = totalcpf_formula;
	}
	public String getCpfcode() {
		return cpfcode;
	}
	public void setCpfcode(String cpfcode) {
		this.cpfcode = cpfcode;
	}
	public String getCyear() {
		return cyear;
	}
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}
	public UFDate getEffective_date() {
		return effective_date;
	}
	public void setEffective_date(UFDate effective_date) {
		this.effective_date = effective_date;
	}
	public UFDate getExpiration_date() {
		return expiration_date;
	}
	public void setExpiration_date(UFDate expiration_date) {
		this.expiration_date = expiration_date;
	}
	
	public String getOwee_formla() {
		return owee_formla;
	}
	public void setOwee_formla(String owee_formla) {
		this.owee_formla = owee_formla;
	}
	public String getOwtotal_formula() {
		return owtotal_formula;
	}
	public void setOwtotal_formula(String owtotal_formula) {
		this.owtotal_formula = owtotal_formula;
	}
	public String getAwee_formula() {
		return awee_formula;
	}
	public void setAwee_formula(String awee_formula) {
		this.awee_formula = awee_formula;
	}
	public String getAwtotal_formula() {
		return awtotal_formula;
	}
	public void setAwtotal_formula(String awtotal_formula) {
		this.awtotal_formula = awtotal_formula;
	}
	public UFDouble getOw_ceilling() {
		return ow_ceilling;
	}
	public void setOw_ceilling(UFDouble ow_ceilling) {
		this.ow_ceilling = ow_ceilling;
	}
	public UFDouble getAw_fixvalue() {
		return aw_fixvalue;
	}
	public void setAw_fixvalue(UFDouble aw_fixvalue) {
		this.aw_fixvalue = aw_fixvalue;
	}
	public String getEmployeecpf_formula() {
		return employeecpf_formula;
	}
	public void setEmployeecpf_formula(String employeecpf_formula) {
		this.employeecpf_formula = employeecpf_formula;
	}
	@Override
	public String getTableName() {
		return "wa_sgcpf_rate";
	}
	
}
