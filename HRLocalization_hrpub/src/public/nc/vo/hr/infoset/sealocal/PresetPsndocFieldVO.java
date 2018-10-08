package nc.vo.hr.infoset.sealocal;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class PresetPsndocFieldVO extends SuperVO {
	
	/**
	 * Auto gerenated serial UID
	 */
	private static final long serialVersionUID = 5858186491401261284L;
	
	// Attributes
	private String item_code;
	private String item_name;
	private String item_name2;
	private String item_name3;
	private String item_name4;
	private String item_name5;
	private String item_name6;
	private Integer data_type;
	private String ref_model_name;
	private Integer max_length;
	private Integer precise;
	private String resid;
	private String respath;
	private UFBoolean nullable;
	private UFBoolean unique_flag;
	
	// Database fields
	public static final String ITEM_CODE = "item_code";
	public static final String ITEM_NAME = "item_name";
	public static final String ITEM_NAME2 = "item_name2";
	public static final String ITEM_NAME3 = "item_name3";
	public static final String ITEM_NAME4 = "item_name4";
	public static final String ITEM_NAME5 = "item_name5";
	public static final String ITEM_NAME6 = "item_name6";
	public static final String DATA_TYPE = "data_type";
	public static final String REF_MODEL_NAME = "ref_model_name";
	public static final String MAX_LENGTH = "max_length";
	public static final String PRECISE = "precise";
	public static final String RESID = "resid";
	public static final String RESPATH = "respath";
	public static final String NULLABLE = "nullable";
	public static final String UNIQUE_FLAG = "unique_flag";
	
	
	// Default table name
	public static String getDefaultTableName() {
		return "hr_infoset_item_sealocal";
	}
	
	// Getter and setters
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
	public String getItem_name2() {
		return item_name2;
	}
	public void setItem_name2(String item_name2) {
		this.item_name2 = item_name2;
	}
	public String getItem_name3() {
		return item_name3;
	}
	public void setItem_name3(String item_name3) {
		this.item_name3 = item_name3;
	}
	public String getItem_name4() {
		return item_name4;
	}
	public void setItem_name4(String item_name4) {
		this.item_name4 = item_name4;
	}
	public String getItem_name5() {
		return item_name5;
	}
	public void setItem_name5(String item_name5) {
		this.item_name5 = item_name5;
	}
	public String getItem_name6() {
		return item_name6;
	}
	public void setItem_name6(String item_name6) {
		this.item_name6 = item_name6;
	}
	public Integer getData_type() {
		return data_type;
	}
	public void setData_type(Integer data_type) {
		this.data_type = data_type;
	}
	public String getRef_model_name() {
		return ref_model_name;
	}
	public void setRef_model_name(String ref_model_name) {
		this.ref_model_name = ref_model_name;
	}
	public Integer getMax_length() {
		return max_length;
	}
	public void setMax_length(Integer max_length) {
		this.max_length = max_length;
	}
	public Integer getPrecise() {
		return precise;
	}
	public void setPrecise(Integer precise) {
		this.precise = precise;
	}
	public String getResid() {
		return resid;
	}
	public void setResid(String resid) {
		this.resid = resid;
	}
	public String getRespath() {
		return respath;
	}
	public void setRespath(String respath) {
		this.respath = respath;
	}
	public UFBoolean getNullable() {
		return nullable;
	}
	public void setNullable(UFBoolean nullable) {
		this.nullable = nullable;
	}
	public UFBoolean getUnique_flag() {
		return unique_flag;
	}
	public void setUnique_flag(UFBoolean unique_flag) {
		this.unique_flag = unique_flag;
	}
	
	
}
