package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class MalaysiaVO_PCB_para extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;
	private UFDouble value;
	private String desc;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UFDouble getValue() {
		return value;
	}
	public void setValue(UFDouble value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String getTableName() {
		return "wa_mypcb_parameter";
	}
	
	
}
