package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class SeaLocalCommonItemVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	private String item_code;
	private String item_name;
	private Integer item_class;// 数据类型
	private Integer item_type; // 增减属性
	private Integer item_length;
	private UFBoolean item_isclearnextmonth;// 下月是否清零
	private String itemkey;
	private String country;
	private String item_name2;//多语名称 第二语言名称
	//是否公式
	private UFBoolean isformula;
	//公式显示
	private String vformula;
	//公式value
	private String vformulastr;

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public Integer getItem_class() {
		return item_class;
	}

	public void setItem_class(Integer item_class) {
		this.item_class = item_class;
	}

	public Integer getItem_type() {
		return item_type;
	}

	public void setItem_type(Integer item_type) {
		this.item_type = item_type;
	}

	public Integer getItem_length() {
		return item_length;
	}

	public void setItem_length(Integer item_length) {
		this.item_length = item_length;
	}

	public UFBoolean getItem_isclearnextmonth() {
		return item_isclearnextmonth;
	}

	public void setItem_isclearnextmonth(UFBoolean item_isclearnextmonth) {
		this.item_isclearnextmonth = item_isclearnextmonth;
	}
	
	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}
	

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getItem_name2() {
		return item_name2;
	}

	public void setItem_name2(String item_name2) {
		this.item_name2 = item_name2;
	}
	

	public UFBoolean getIsformula() {
		return isformula;
	}

	public void setIsformula(UFBoolean isformula) {
		this.isformula = isformula;
	}

	public String getVformula() {
		return vformula;
	}

	public void setVformula(String vformula) {
		this.vformula = vformula;
	}

	public String getVformulastr() {
		return vformulastr;
	}

	public void setVformulastr(String vformulastr) {
		this.vformulastr = vformulastr;
	}

	@Override
	public String getTableName() {
		return "wa_sealocalcommon_item";
	}

}
