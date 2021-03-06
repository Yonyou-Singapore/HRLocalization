/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.leave;

import nc.vo.pub.IVOMeta;
import nc.vo.ta.annotation.FKColumn;
import nc.vo.ta.annotation.HeadVOClassName;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.timebill.annotation.BeginTimeFieldName;
import nc.vo.ta.timebill.annotation.BillBeginDateFieldName;
import nc.vo.ta.timebill.annotation.BillCodeFieldName;
import nc.vo.ta.timebill.annotation.BillEndDateFieldName;
import nc.vo.ta.timebill.annotation.EndTimeFieldName;
import nc.vo.ta.timebill.annotation.PkJobOrgFieldName;
import nc.vo.ta.timebill.annotation.PkPsndocFieldName;
import nc.vo.ta.timebill.annotation.PkPsnjobFieldName;
import nc.vo.ta.timebill.annotation.PkTimeItemFieldName;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:${vmObject.createdDate}
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@Table(tableName="tbm_leaveb")
@IDColumn(idColumn="pk_leaveb")
@BeginTimeFieldName(fieldName="leavebegintime")
@EndTimeFieldName(fieldName="leaveendtime")
@HeadVOClassName(className="nc.vo.ta.leave.LeavehVO")
@FKColumn(fkColumn="pk_leaveh")
@PkPsndocFieldName(fieldName="pk_psndoc")
@PkPsnjobFieldName(fieldName="pk_psnjob")
@PkJobOrgFieldName(fieldName="pk_joborg")
@PkTimeItemFieldName(fieldName="pk_leavetype")
@BillCodeFieldName(fieldName="bill_code")
@BillBeginDateFieldName(fieldName="leavebegindate")
@BillEndDateFieldName(fieldName="leaveenddate")
public class LeavebVO extends LeaveCommonVO{
	
	private java.lang.String pk_leaveb;
	private java.lang.String pk_leaveh;
	
//	private UFBoolean islactation;
	

	public static final String PK_LEAVEB = "pk_leaveb";
	public static final String PK_LEAVEH = "pk_leaveh";
	private java.lang.String transtype;		//流程类型    冗余字段
	private java.lang.String transtypeid;	//流程类型    冗余字段
	
	private nc.vo.pub.lang.UFDateTime leavebegintimeother;//普通假休假开始时间移动模板用
	private nc.vo.pub.lang.UFDouble leavehourother;//普通假休假时长移动模板用
	private java.lang.Integer lactationholidaytypeother;;//哺乳时段， 移动模板用
	private nc.vo.pub.lang.UFLiteralDate leavebegindateother;//哺乳假开始日期，移动模板用
	
	public static final String LEAVEBEGINTIMEOTHER="leavebegintimeother";//普通假休假开始时间移动模板用
	public static final String LEAVEHOUROTHER="leavehourother";//普通假休假时长移动模板用
	public static final String LEAVEBEGINDATEOTHER="leavebegindateother";//哺乳假开始日期，移动模板用
	public static final String LACTATIONHOLIDAYTYPEOTHER="lactationholidaytypeother";//哺乳时段， 移动模板用
	
	// 东南亚本地化 添加了20个自定义项 start
	private java.lang.String def1;
	private java.lang.String def2;
	private java.lang.String def3;
	private java.lang.String def4;
	private java.lang.String def5;
	private java.lang.String def6;
	private java.lang.String def7;
	private java.lang.String def8;
	private java.lang.String def9;
	private java.lang.String def10;
	private java.lang.String def11;
	private java.lang.String def12;
	private java.lang.String def13;
	private java.lang.String def14;
	private java.lang.String def15;
	private java.lang.String def16;
	private java.lang.String def17;
	private java.lang.String def18;
	private java.lang.String def19;
	private java.lang.String def20;
	// 东南亚本地化 添加了20个自定义项 end
	
	public java.lang.Integer getLactationholidaytypeother() {
		return lactationholidaytypeother;
	}
	public void setLactationholidaytypeother(
			java.lang.Integer lactationholidaytypeother) {
		this.lactationholidaytypeother = lactationholidaytypeother;
	}
	public nc.vo.pub.lang.UFLiteralDate getLeavebegindateother() {
		return leavebegindateother;
	}
	public void setLeavebegindateother(
			nc.vo.pub.lang.UFLiteralDate leavebegindateother) {
		this.leavebegindateother = leavebegindateother;
	}
	public nc.vo.pub.lang.UFDateTime getLeavebegintimeother() {
		return leavebegintimeother;
	}
	public void setLeavebegintimeother(nc.vo.pub.lang.UFDateTime leavebegintimeother) {
		this.leavebegintimeother = leavebegintimeother;
	}
	public nc.vo.pub.lang.UFDouble getLeavehourother() {
		return leavehourother;
	}
	public void setLeavehourother(nc.vo.pub.lang.UFDouble leavehourother) {
		this.leavehourother = leavehourother;
	}
			
	/**
	 * 属性pk_leaveb的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_leaveb () {
		return pk_leaveb;
	}   
	/**
	 * 属性pk_leaveb的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_leaveb java.lang.String
	 */
	public void setPk_leaveb (java.lang.String newPk_leaveb ) {
	 	this.pk_leaveb = newPk_leaveb;
	} 	  
	/**
	 * 属性pk_leaveh的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_leaveh () {
		return pk_leaveh;
	}   
	/**
	 * 属性pk_leaveh的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_leaveh java.lang.String
	 */
	public void setPk_leaveh (java.lang.String newPk_leaveh ) {
	 	this.pk_leaveh = newPk_leaveh;
	} 	  
 
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_leaveb";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_leaveb";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_leaveb";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:${vmObject.createdDate}
	  */
   public LeavebVO() {
		super();	
	}
	
	@Override
	public boolean isAppBill() {
		return true;
	}
	@Override
	public void setAppBill(boolean isAppBill) {
		
	}
//	public UFBoolean getIslactation() {
//		return islactation;
//	}
//	public void setIslactation(UFBoolean islactation) {
//		this.islactation = islactation;
//	}
    @Override
    public IVOMeta getMetaData()
    {
//        return VOMetaFactory.getInstance().getVOMeta("hrta.tbm_leaveh");
        return null;
    }
    
    public java.lang.String getTranstype() {
		return transtype;
	}
	public void setTranstype(java.lang.String transtype) {
		this.transtype = transtype;
	}
	public java.lang.String getTranstypeid() {
		return transtypeid;
	}
	public void setTranstypeid(java.lang.String transtypeid) {
		this.transtypeid = transtypeid;
	}
	
	
	// 东南亚本地化 添加了20个自定义项 start
	public java.lang.String getDef1() {
		return def1;
	}
	public void setDef1(java.lang.String def1) {
		this.def1 = def1;
	}
	public java.lang.String getDef2() {
		return def2;
	}
	public void setDef2(java.lang.String def2) {
		this.def2 = def2;
	}
	public java.lang.String getDef3() {
		return def3;
	}
	public void setDef3(java.lang.String def3) {
		this.def3 = def3;
	}
	public java.lang.String getDef4() {
		return def4;
	}
	public void setDef4(java.lang.String def4) {
		this.def4 = def4;
	}
	public java.lang.String getDef5() {
		return def5;
	}
	public void setDef5(java.lang.String def5) {
		this.def5 = def5;
	}
	public java.lang.String getDef6() {
		return def6;
	}
	public void setDef6(java.lang.String def6) {
		this.def6 = def6;
	}
	public java.lang.String getDef7() {
		return def7;
	}
	public void setDef7(java.lang.String def7) {
		this.def7 = def7;
	}
	public java.lang.String getDef8() {
		return def8;
	}
	public void setDef8(java.lang.String def8) {
		this.def8 = def8;
	}
	public java.lang.String getDef9() {
		return def9;
	}
	public void setDef9(java.lang.String def9) {
		this.def9 = def9;
	}
	public java.lang.String getDef10() {
		return def10;
	}
	public void setDef10(java.lang.String def10) {
		this.def10 = def10;
	}
	public java.lang.String getDef11() {
		return def11;
	}
	public void setDef11(java.lang.String def11) {
		this.def11 = def11;
	}
	public java.lang.String getDef12() {
		return def12;
	}
	public void setDef12(java.lang.String def12) {
		this.def12 = def12;
	}
	public java.lang.String getDef13() {
		return def13;
	}
	public void setDef13(java.lang.String def13) {
		this.def13 = def13;
	}
	public java.lang.String getDef14() {
		return def14;
	}
	public void setDef14(java.lang.String def14) {
		this.def14 = def14;
	}
	public java.lang.String getDef15() {
		return def15;
	}
	public void setDef15(java.lang.String def15) {
		this.def15 = def15;
	}
	public java.lang.String getDef16() {
		return def16;
	}
	public void setDef16(java.lang.String def16) {
		this.def16 = def16;
	}
	public java.lang.String getDef17() {
		return def17;
	}
	public void setDef17(java.lang.String def17) {
		this.def17 = def17;
	}
	public java.lang.String getDef18() {
		return def18;
	}
	public void setDef18(java.lang.String def18) {
		this.def18 = def18;
	}
	public java.lang.String getDef19() {
		return def19;
	}
	public void setDef19(java.lang.String def19) {
		this.def19 = def19;
	}
	public java.lang.String getDef20() {
		return def20;
	}
	public void setDef20(java.lang.String def20) {
		this.def20 = def20;
	}
	// 东南亚本地化 添加了20个自定义项 end
} 


