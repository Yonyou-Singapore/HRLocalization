package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class SeaLocalCommonItemVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String MY_BS = "my_bs";// Basic Salary
	public static final String MY_TA = "my_ta";// Total Allowance
	public static final String MY_TB = "my_tb";// Total bonus
	public static final String MY_NPL = "my_npl";// NPL
	public static final String MY_OT = "my_ot";// Total OT
	public static final String MY_EPF_ER = "my_epf_er";// EPF(employer)
	public static final String MY_EPF_EE = "my_epf_ee";// EPF(employee)
	public static final String MY_EIS_ER = "my_eis_er";
	public static final String MY_EIS_EE = "my_eis_ee";
	public static final String MY_SOCSO_EYER = "my_socso_eyer";
	public static final String MY_SOCSO_EYEE = "my_socso_eyee";
	public static final String MY_PCB = "my_pcb";
	public static final String MY_ZAKAT = "my_zakat";
	public static final String MY_PAY = "my_pay";// NET PAY
	public static final String MY_Y1 = "my_Y1";// Taxable
															// Income(Normal)(Y1)
	public static final String MY_YT = "my_Yt";// Taxable
															// Income(Additional)(Yt)
	public static final String MY_TI = "my_ti";// Taxable Income
	public static final String MY_EPF_NR = "my_epf_nr";// EPF(Normal
																	// remuneration)
	public static final String MY_EPF_AR = "my_epf_ar";// EPF(Addtional
																	// remuneration)
	public static final String MY_K1 = "my_K1";// Normal
															// remuneration’s
															// EPF and Other
															// Approved
															// Funds(K1)
	public static final String MY_KT = "my_Kt";// Additional
															// remuneration’s
															// EPF (Kt)
	public static final String MY_K1KT = "my_K1Kt";// Taxable
																// Deduction(K1+Kt)
	public static final String MY_LP1 = "my_LP1";// Taxable Option
																// Deduction(LP1)
	public static final String MY_CF_EPF_ER = "my_cf_epf_er";// CF
																			// EPF(employer)
	public static final String MY_CF_EPF_EE = "my_cf_epf_ee";// CF
																			// EPF(employee)
	public static final String MY_CF_EIS_ER = "my_cf_eis_er";
	public static final String MY_CF_EIS_EE = "my_cf_eis_ee";
	public static final String my_cf_socso_er = "my_cf_socso_er";
	public static final String MY_CF_SOCSO_EE = "my_cf_socso_ee";
	public static final String MY_X = "my_X";// CF PCB(X)
	public static final String MY_Y = "my_Y";// CF Taxable Income(Y)
	public static final String MY_K = "my_K";// CF Taxable
															// Deduction(K)
	public static final String MY_LP = "my_LP";// CF Taxable Option
															// Deduction(LP)
	public static final String MY_Z = "my_Z";// CF ZAKAT(Z)
	public static final String MY_YTD_EPF_ER = "my_ytd_epf_er";// YTD
																			// EPF(employer)
	public static final String MY_YTD_EPF_EE = "my_ytd_epf_ee";// YTD
																			// EPF(employee)
	public static final String MY_YTD_EIS_ER = "my_ytd_eis_er";
	public static final String MY_YTD_EIS_EE = "my_ytd_eis_ee";
	public static final String MY_YTD_SOCSO_ER = "my_ytd_socso_er";
	public static final String my_ytd_socso_er = "my_ytd_epf_ee";
	public static final String MY_YTD_PCB = "my_ytd_pcb";// YTD PCB

	private String item_code;
	private String item_name;
	private Integer item_class;// 数据类型
	private Integer item_type; // 增减属性
	private Integer item_length;
	private UFBoolean item_isclearnextmonth;// 下月是否清零
	private String itemkey;

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

	@Override
	public String getTableName() {
		return "wa_sealocalcommon_item";
	}

}
