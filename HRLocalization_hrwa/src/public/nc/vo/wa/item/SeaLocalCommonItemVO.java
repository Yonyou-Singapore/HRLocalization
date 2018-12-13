package nc.vo.wa.item;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class SeaLocalCommonItemVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String SEALOCAL_FLAG = "sealocal%";

	public static final String SEALOCAL_BS = "sealocal_bs";// Basic Salary
	public static final String SEALOCAL_TA = "sealocal_ta";// Total Allowance
	public static final String SEALOCAL_TB = "sealocal_tb";// Total bonus
	public static final String SEALOCAL_NPL = "sealocal_npl";// NPL
	public static final String SEALOCAL_OT = "sealocal_ot";// Total OT
	public static final String SEALOCAL_EPF_ER = "sealocal_epf_er";// EPF(employer)
	public static final String SEALOCAL_EPF_EE = "sealocal_epf_ee";// EPF(employee)
	public static final String SEALOCAL_EIS_ER = "sealocal_eis_er";
	public static final String SEALOCAL_EIS_EE = "sealocal_eis_ee";
	public static final String SEALOCAL_SOCSO_EYER = "sealocal_socso_eyer";
	public static final String SEALOCAL_SOCSO_EYEE = "sealocal_socso_eyee";
	public static final String SEALOCAL_PCB = "sealocal_pcb";
	public static final String SEALOCAL_ZAKAT = "sealocal_zakat";
	public static final String SEALOCAL_PAY = "sealocal_pay";// NET PAY
	public static final String SEALOCAL_Y1 = "sealocal_Y1";// Taxable
															// Income(Normal)(Y1)
	public static final String SEALOCAL_YT = "sealocal_Yt";// Taxable
															// Income(Additional)(Yt)
	public static final String SEALOCAL_TI = "sealocal_ti";// Taxable Income
	public static final String SEALOCAL_EPF_NR = "sealocal_epf_nr";// EPF(Normal
																	// remuneration)
	public static final String SEALOCAL_EPF_AR = "sealocal_epf_ar";// EPF(Addtional
																	// remuneration)
	public static final String SEALOCAL_K1 = "sealocal_K1";// Normal
															// remuneration’s
															// EPF and Other
															// Approved
															// Funds(K1)
	public static final String SEALOCAL_KT = "sealocal_Kt";// Additional
															// remuneration’s
															// EPF (Kt)
	public static final String SEALOCAL_K1KT = "sealocal_K1Kt";// Taxable
																// Deduction(K1+Kt)
	public static final String SEALOCAL_LP1 = "sealocal_LP1";// Taxable Option
																// Deduction(LP1)
	public static final String SEALOCAL_CF_EPF_ER = "sealocal_cf_epf_er";// CF
																			// EPF(employer)
	public static final String SEALOCAL_CF_EPF_EE = "sealocal_cf_epf_ee";// CF
																			// EPF(employee)
	public static final String SEALOCAL_CF_EIS_ER = "sealocal_cf_eis_er";
	public static final String SEALOCAL_CF_EIS_EE = "sealocal_cf_eis_ee";
	public static final String sealocal_cf_socso_er = "sealocal_cf_socso_er";
	public static final String SEALOCAL_CF_SOCSO_EE = "sealocal_cf_socso_ee";
	public static final String SEALOCAL_X = "sealocal_X";// CF PCB(X)
	public static final String SEALOCAL_Y = "sealocal_Y";// CF Taxable Income(Y)
	public static final String SEALOCAL_K = "sealocal_K";// CF Taxable
															// Deduction(K)
	public static final String SEALOCAL_LP = "sealocal_LP";// CF Taxable Option
															// Deduction(LP)
	public static final String SEALOCAL_Z = "sealocal_Z";// CF ZAKAT(Z)
	public static final String SEALOCAL_YTD_EPF_ER = "sealocal_ytd_epf_er";// YTD
																			// EPF(employer)
	public static final String SEALOCAL_YTD_EPF_EE = "sealocal_ytd_epf_ee";// YTD
																			// EPF(employee)
	public static final String SEALOCAL_YTD_EIS_ER = "sealocal_ytd_eis_er";
	public static final String SEALOCAL_YTD_EIS_EE = "sealocal_ytd_eis_ee";
	public static final String SEALOCAL_YTD_SOCSO_ER = "sealocal_ytd_socso_er";
	public static final String sealocal_ytd_socso_er = "sealocal_ytd_epf_ee";
	public static final String SEALOCAL_YTD_PCB = "sealocal_ytd_pcb";// YTD PCB

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
