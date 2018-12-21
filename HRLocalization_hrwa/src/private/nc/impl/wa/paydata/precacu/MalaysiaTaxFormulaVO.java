package nc.impl.wa.paydata.precacu;

import java.io.Serializable;

/**
 * 
 * @author weiningc
 * @version 1.0
 * @data:2018.11.16 14:33
 */
public class MalaysiaTaxFormulaVO  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//PCB_GROUPA:Non-Resident (未满182天)
	public final static String PCB_GROUPA = "PCB_GROUPA";
	
	//PCB_GROUPB:Resident
	public final static String PCB_GROUPB = "PCB_GROUPB";
	
	//PCB_GROUPC:Returning Expert Programe(REP)
	public final static String PCB_GROUPC= "PCB_GROUPC";
	
	//PCB_GROUPD:Knowledge worker(IRDA)
	public final static String PCB_GROUPD= "PCB_GROUPD";
	
	// Class_type: 取值范围（0, 1） 0为普通类别， 1为年度奖金类别    2 为薪资补发计算  
	public final static String CLASS_TYPE_NORMAL = "0";
	public final static String CLASS_TYPE_YEAR = "1";
	public final static String CLASS_TYPE_REDATA= "2";

	//PCB Group
	private String pcbgroup;
	
	//薪资发放类别
	private String class_wagetype;
	
	//月应发工资
	private String month_grosspay;
	
	//月实发工资
	private String month_netpay;
	
	//本年度本期以前的累计毛收入
	private String beforcurrent_totalwage;
	
	//本年度本期以前的累计扣款
	private String beforcurrent_totaldeduction;
	
	//宗教税
	private String zaket;

	public String getPcbgroup() {
		return pcbgroup;
	}

	public void setPcbgroup(String pcbgroup) {
		this.pcbgroup = pcbgroup;
	}

	public String getMonth_grosspay() {
		return month_grosspay;
	}

	public void setMonth_grosspay(String month_grosspay) {
		this.month_grosspay = month_grosspay;
	}

	public String getMonth_netpay() {
		return month_netpay;
	}

	public void setMonth_netpay(String month_netpay) {
		this.month_netpay = month_netpay;
	}

	public String getBeforcurrent_totalwage() {
		return beforcurrent_totalwage;
	}

	public void setBeforcurrent_totalwage(String beforcurrent_totalwage) {
		this.beforcurrent_totalwage = beforcurrent_totalwage;
	}

	public String getBeforcurrent_totaldeduction() {
		return beforcurrent_totaldeduction;
	}

	public void setBeforcurrent_totaldeduction(String beforcurrent_totaldeduction) {
		this.beforcurrent_totaldeduction = beforcurrent_totaldeduction;
	}

	public String getZaket() {
		return zaket;
	}

	public void setZaket(String zaket) {
		this.zaket = zaket;
	}

	public String getClass_wagetype() {
		return class_wagetype;
	}

	public void setClass_wagetype(String class_wagetype) {
		this.class_wagetype = class_wagetype;
	}
	
	

}
