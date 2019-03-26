package nc.vo.hrrp.dr;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class Rpderatevo extends SuperVO {
/**
	 * 
	 */
	private static final long serialVersionUID = 4644825225999753728L;
/**
*Bonus
*/
public static final String AR_1="ar_1";
/**
*Additional remuneration’s EPF
*/
public static final String AR_10="ar_10";
/**
*Total net additional remuneration
*/
public static final String AR_11="ar_11";
/**
*Arrears
*/
public static final String AR_2="ar_2";
/**
*Commissions
*/
public static final String AR_3="ar_3";
/**
*Gratuity
*/
public static final String AR_4="ar_4";
/**
*Compensation
*/
public static final String AR_5="ar_5";
/**
*Director's fee
*/
public static final String AR_6="ar_6";
/**
*Income tax paid by employers on behalf of employees
*/
public static final String AR_7="ar_7";
/**
*Others
*/
public static final String AR_8="ar_8";
/**
*Total additional remuneration
*/
public static final String AR_9="ar_9";
/**
*Benefit-In-Kind
*/
public static final String BIK="bik";
/**
*单据号
*/
public static final String BILLNO="billno";
/**
*单据类型
*/
public static final String BILLTYPE="billtype";
/**
*创建时间
*/
public static final String CREATIONTIME="creationtime";
/**
*创建人
*/
public static final String CREATOR="creator";
/**
*Medical expenses for own parents, special need and parent care
*/
public static final String DE_1="de_1";
/**
*Purchase of personal computer for individual
*/
public static final String DE_10="de_10";
/**
*Net deposit in Skim SimpananPendidikan Nasional
*/
public static final String DE_11="de_11";
/**
*Purchase of sports equipment for any sport activity as defined
*/
public static final String DE_12="de_12";
/**
*Payment of alimony to former wife
*/
public static final String DE_13="de_13";
/**
*Life insurance premium
*/
public static final String DE_14="de_14";
/**
*Education and medical insurance premium
*/
public static final String DE_15="de_15";
/**
*Deferred annuity premium or contribution to Private Retirement Scheme
*/
public static final String DE_16="de_16";
/**
*Interest on Housing Loan
*/
public static final String DE_17="de_17";
/**
*SOCSO Contribution
*/
public static final String DE_18="de_18";
/**
*Father Relief
*/
public static final String DE_2="de_2";
/**
*Mother Relief
*/
public static final String DE_3="de_3";
/**
*Basic supporting equipment for disabled self, spouse, child or parent
*/
public static final String DE_4="de_4";
/**
*Education fees
*/
public static final String DE_5="de_5";
/**
*Medical expenses on serious diseases for self, spouse or child
*/
public static final String DE_6="de_6";
/**
*Complete medical examination for self, spouse or child
*/
public static final String DE_7="de_7";
/**
*Total(a+b)
*/
public static final String DE_8="de_8";
/**
*Purchase of books/magazines/journals/similar
*/
public static final String DE_9="de_9";
/**
*启用状态
*/
public static final String DRSTATUS="drstatus";
/**
*当月EPF
*/
public static final String EPF="epf";
/**
*当月净收入
*/
public static final String INCOME="income";
/**
*修改时间
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*修改人
*/
public static final String MODIFIER="modifier";
/**
*期间
*/
public static final String PERIOD="period";
/**
*员工
*/
public static final String PK_DEFDOC="pk_defdoc";
/**
*减免编号
*/
public static final String PK_DERATE="pk_derate";
/**
*集团
*/
public static final String PK_GROUP="pk_group";
/**
*组织
*/
public static final String PK_ORG="pk_org";
/**
*Travelling allowance
*/
public static final String TRVALLOW="trvallow";
/**
*时间戳
*/
public static final String TS="ts";
/**
*Value Of Living Accomodation
*/
public static final String VOLA="vola";
/**
*当月工资
*/
public static final String WAGE="wage";
/**
*Zakat or Fitrah
*/
public static final String ZOF="zof";
/** 
* 获取Bonus
*
* @return Bonus
*/
public UFDouble getAr_1 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_1);
 } 

/** 
* 设置Bonus
*
* @param ar_1 Bonus
*/
public void setAr_1 ( UFDouble ar_1) {
this.setAttributeValue( Rpderatevo.AR_1,ar_1);
 } 

/** 
* 获取Additional remuneration’s EPF
*
* @return Additional remuneration’s EPF
*/
public UFDouble getAr_10 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_10);
 } 

/** 
* 设置Additional remuneration’s EPF
*
* @param ar_10 Additional remuneration’s EPF
*/
public void setAr_10 ( UFDouble ar_10) {
this.setAttributeValue( Rpderatevo.AR_10,ar_10);
 } 

/** 
* 获取Total net additional remuneration
*
* @return Total net additional remuneration
*/
public UFDouble getAr_11 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_11);
 } 

/** 
* 设置Total net additional remuneration
*
* @param ar_11 Total net additional remuneration
*/
public void setAr_11 ( UFDouble ar_11) {
this.setAttributeValue( Rpderatevo.AR_11,ar_11);
 } 

/** 
* 获取Arrears
*
* @return Arrears
*/
public UFDouble getAr_2 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_2);
 } 

/** 
* 设置Arrears
*
* @param ar_2 Arrears
*/
public void setAr_2 ( UFDouble ar_2) {
this.setAttributeValue( Rpderatevo.AR_2,ar_2);
 } 

/** 
* 获取Commissions
*
* @return Commissions
*/
public UFDouble getAr_3 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_3);
 } 

/** 
* 设置Commissions
*
* @param ar_3 Commissions
*/
public void setAr_3 ( UFDouble ar_3) {
this.setAttributeValue( Rpderatevo.AR_3,ar_3);
 } 

/** 
* 获取Gratuity
*
* @return Gratuity
*/
public UFDouble getAr_4 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_4);
 } 

/** 
* 设置Gratuity
*
* @param ar_4 Gratuity
*/
public void setAr_4 ( UFDouble ar_4) {
this.setAttributeValue( Rpderatevo.AR_4,ar_4);
 } 

/** 
* 获取Compensation
*
* @return Compensation
*/
public UFDouble getAr_5 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_5);
 } 

/** 
* 设置Compensation
*
* @param ar_5 Compensation
*/
public void setAr_5 ( UFDouble ar_5) {
this.setAttributeValue( Rpderatevo.AR_5,ar_5);
 } 

/** 
* 获取Director's fee
*
* @return Director's fee
*/
public UFDouble getAr_6 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_6);
 } 

/** 
* 设置Director's fee
*
* @param ar_6 Director's fee
*/
public void setAr_6 ( UFDouble ar_6) {
this.setAttributeValue( Rpderatevo.AR_6,ar_6);
 } 

/** 
* 获取Income tax paid by employers on behalf of employees
*
* @return Income tax paid by employers on behalf of employees
*/
public UFDouble getAr_7 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_7);
 } 

/** 
* 设置Income tax paid by employers on behalf of employees
*
* @param ar_7 Income tax paid by employers on behalf of employees
*/
public void setAr_7 ( UFDouble ar_7) {
this.setAttributeValue( Rpderatevo.AR_7,ar_7);
 } 

/** 
* 获取Others
*
* @return Others
*/
public UFDouble getAr_8 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_8);
 } 

/** 
* 设置Others
*
* @param ar_8 Others
*/
public void setAr_8 ( UFDouble ar_8) {
this.setAttributeValue( Rpderatevo.AR_8,ar_8);
 } 

/** 
* 获取Total additional remuneration
*
* @return Total additional remuneration
*/
public UFDouble getAr_9 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.AR_9);
 } 

/** 
* 设置Total additional remuneration
*
* @param ar_9 Total additional remuneration
*/
public void setAr_9 ( UFDouble ar_9) {
this.setAttributeValue( Rpderatevo.AR_9,ar_9);
 } 

/** 
* 获取Benefit-In-Kind
*
* @return Benefit-In-Kind
*/
public UFDouble getBik () {
return (UFDouble) this.getAttributeValue( Rpderatevo.BIK);
 } 

/** 
* 设置Benefit-In-Kind
*
* @param bik Benefit-In-Kind
*/
public void setBik ( UFDouble bik) {
this.setAttributeValue( Rpderatevo.BIK,bik);
 } 

/** 
* 获取单据号
*
* @return 单据号
*/
public String getBillno () {
return (String) this.getAttributeValue( Rpderatevo.BILLNO);
 } 

/** 
* 设置单据号
*
* @param billno 单据号
*/
public void setBillno ( String billno) {
this.setAttributeValue( Rpderatevo.BILLNO,billno);
 } 

/** 
* 获取单据类型
*
* @return 单据类型
*/
public String getBilltype () {
return (String) this.getAttributeValue( Rpderatevo.BILLTYPE);
 } 

/** 
* 设置单据类型
*
* @param billtype 单据类型
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( Rpderatevo.BILLTYPE,billtype);
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDate getCreationtime () {
return (UFDate) this.getAttributeValue( Rpderatevo.CREATIONTIME);
 } 

/** 
* 设置创建时间
*
* @param creationtime 创建时间
*/
public void setCreationtime ( UFDate creationtime) {
this.setAttributeValue( Rpderatevo.CREATIONTIME,creationtime);
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getCreator () {
return (String) this.getAttributeValue( Rpderatevo.CREATOR);
 } 

/** 
* 设置创建人
*
* @param creator 创建人
*/
public void setCreator ( String creator) {
this.setAttributeValue( Rpderatevo.CREATOR,creator);
 } 

/** 
* 获取Medical expenses for own parents, special need and parent care
*
* @return Medical expenses for own parents, special need and parent care
*/
public UFDouble getDe_1 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_1);
 } 

/** 
* 设置Medical expenses for own parents, special need and parent care
*
* @param de_1 Medical expenses for own parents, special need and parent care
*/
public void setDe_1 ( UFDouble de_1) {
this.setAttributeValue( Rpderatevo.DE_1,de_1);
 } 

/** 
* 获取Purchase of personal computer for individual
*
* @return Purchase of personal computer for individual
*/
public UFDouble getDe_10 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_10);
 } 

/** 
* 设置Purchase of personal computer for individual
*
* @param de_10 Purchase of personal computer for individual
*/
public void setDe_10 ( UFDouble de_10) {
this.setAttributeValue( Rpderatevo.DE_10,de_10);
 } 

/** 
* 获取Net deposit in Skim SimpananPendidikan Nasional
*
* @return Net deposit in Skim SimpananPendidikan Nasional
*/
public UFDouble getDe_11 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_11);
 } 

/** 
* 设置Net deposit in Skim SimpananPendidikan Nasional
*
* @param de_11 Net deposit in Skim SimpananPendidikan Nasional
*/
public void setDe_11 ( UFDouble de_11) {
this.setAttributeValue( Rpderatevo.DE_11,de_11);
 } 

/** 
* 获取Purchase of sports equipment for any sport activity as defined
*
* @return Purchase of sports equipment for any sport activity as defined
*/
public UFDouble getDe_12 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_12);
 } 

/** 
* 设置Purchase of sports equipment for any sport activity as defined
*
* @param de_12 Purchase of sports equipment for any sport activity as defined
*/
public void setDe_12 ( UFDouble de_12) {
this.setAttributeValue( Rpderatevo.DE_12,de_12);
 } 

/** 
* 获取Payment of alimony to former wife
*
* @return Payment of alimony to former wife
*/
public UFDouble getDe_13 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_13);
 } 

/** 
* 设置Payment of alimony to former wife
*
* @param de_13 Payment of alimony to former wife
*/
public void setDe_13 ( UFDouble de_13) {
this.setAttributeValue( Rpderatevo.DE_13,de_13);
 } 

/** 
* 获取Life insurance premium
*
* @return Life insurance premium
*/
public UFDouble getDe_14 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_14);
 } 

/** 
* 设置Life insurance premium
*
* @param de_14 Life insurance premium
*/
public void setDe_14 ( UFDouble de_14) {
this.setAttributeValue( Rpderatevo.DE_14,de_14);
 } 

/** 
* 获取Education and medical insurance premium
*
* @return Education and medical insurance premium
*/
public UFDouble getDe_15 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_15);
 } 

/** 
* 设置Education and medical insurance premium
*
* @param de_15 Education and medical insurance premium
*/
public void setDe_15 ( UFDouble de_15) {
this.setAttributeValue( Rpderatevo.DE_15,de_15);
 } 

/** 
* 获取Deferred annuity premium or contribution to Private Retirement Scheme
*
* @return Deferred annuity premium or contribution to Private Retirement Scheme
*/
public UFDouble getDe_16 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_16);
 } 

/** 
* 设置Deferred annuity premium or contribution to Private Retirement Scheme
*
* @param de_16 Deferred annuity premium or contribution to Private Retirement Scheme
*/
public void setDe_16 ( UFDouble de_16) {
this.setAttributeValue( Rpderatevo.DE_16,de_16);
 } 

/** 
* 获取Interest on Housing Loan
*
* @return Interest on Housing Loan
*/
public UFDouble getDe_17 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_17);
 } 

/** 
* 设置Interest on Housing Loan
*
* @param de_17 Interest on Housing Loan
*/
public void setDe_17 ( UFDouble de_17) {
this.setAttributeValue( Rpderatevo.DE_17,de_17);
 } 

/** 
* 获取SOCSO Contribution
*
* @return SOCSO Contribution
*/
public UFDouble getDe_18 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_18);
 } 

/** 
* 设置SOCSO Contribution
*
* @param de_18 SOCSO Contribution
*/
public void setDe_18 ( UFDouble de_18) {
this.setAttributeValue( Rpderatevo.DE_18,de_18);
 } 

/** 
* 获取Father Relief
*
* @return Father Relief
*/
public UFDouble getDe_2 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_2);
 } 

/** 
* 设置Father Relief
*
* @param de_2 Father Relief
*/
public void setDe_2 ( UFDouble de_2) {
this.setAttributeValue( Rpderatevo.DE_2,de_2);
 } 

/** 
* 获取Mother Relief
*
* @return Mother Relief
*/
public UFDouble getDe_3 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_3);
 } 

/** 
* 设置Mother Relief
*
* @param de_3 Mother Relief
*/
public void setDe_3 ( UFDouble de_3) {
this.setAttributeValue( Rpderatevo.DE_3,de_3);
 } 

/** 
* 获取Basic supporting equipment for disabled self, spouse, child or parent
*
* @return Basic supporting equipment for disabled self, spouse, child or parent
*/
public UFDouble getDe_4 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_4);
 } 

/** 
* 设置Basic supporting equipment for disabled self, spouse, child or parent
*
* @param de_4 Basic supporting equipment for disabled self, spouse, child or parent
*/
public void setDe_4 ( UFDouble de_4) {
this.setAttributeValue( Rpderatevo.DE_4,de_4);
 } 

/** 
* 获取Education fees
*
* @return Education fees
*/
public UFDouble getDe_5 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_5);
 } 

/** 
* 设置Education fees
*
* @param de_5 Education fees
*/
public void setDe_5 ( UFDouble de_5) {
this.setAttributeValue( Rpderatevo.DE_5,de_5);
 } 

/** 
* 获取Medical expenses on serious diseases for self, spouse or child
*
* @return Medical expenses on serious diseases for self, spouse or child
*/
public UFDouble getDe_6 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_6);
 } 

/** 
* 设置Medical expenses on serious diseases for self, spouse or child
*
* @param de_6 Medical expenses on serious diseases for self, spouse or child
*/
public void setDe_6 ( UFDouble de_6) {
this.setAttributeValue( Rpderatevo.DE_6,de_6);
 } 

/** 
* 获取Complete medical examination for self, spouse or child
*
* @return Complete medical examination for self, spouse or child
*/
public UFDouble getDe_7 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_7);
 } 

/** 
* 设置Complete medical examination for self, spouse or child
*
* @param de_7 Complete medical examination for self, spouse or child
*/
public void setDe_7 ( UFDouble de_7) {
this.setAttributeValue( Rpderatevo.DE_7,de_7);
 } 

/** 
* 获取Total(a+b)
*
* @return Total(a+b)
*/
public UFDouble getDe_8 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_8);
 } 

/** 
* 设置Total(a+b)
*
* @param de_8 Total(a+b)
*/
public void setDe_8 ( UFDouble de_8) {
this.setAttributeValue( Rpderatevo.DE_8,de_8);
 } 

/** 
* 获取Purchase of books/magazines/journals/similar
*
* @return Purchase of books/magazines/journals/similar
*/
public UFDouble getDe_9 () {
return (UFDouble) this.getAttributeValue( Rpderatevo.DE_9);
 } 

/** 
* 设置Purchase of books/magazines/journals/similar
*
* @param de_9 Purchase of books/magazines/journals/similar
*/
public void setDe_9 ( UFDouble de_9) {
this.setAttributeValue( Rpderatevo.DE_9,de_9);
 } 

/** 
* 获取启用状态
*
* @return 启用状态
*/
public UFBoolean getDrstatus () {
return (UFBoolean) this.getAttributeValue( Rpderatevo.DRSTATUS);
 } 

/** 
* 设置启用状态
*
* @param drstatus 启用状态
*/
public void setDrstatus ( UFBoolean drstatus) {
this.setAttributeValue( Rpderatevo.DRSTATUS,drstatus);
 } 

/** 
* 获取当月EPF
*
* @return 当月EPF
*/
public UFDouble getEpf () {
return (UFDouble) this.getAttributeValue( Rpderatevo.EPF);
 } 

/** 
* 设置当月EPF
*
* @param epf 当月EPF
*/
public void setEpf ( UFDouble epf) {
this.setAttributeValue( Rpderatevo.EPF,epf);
 } 

/** 
* 获取当月净收入
*
* @return 当月净收入
*/
public UFDouble getIncome () {
return (UFDouble) this.getAttributeValue( Rpderatevo.INCOME);
 } 

/** 
* 设置当月净收入
*
* @param income 当月净收入
*/
public void setIncome ( UFDouble income) {
this.setAttributeValue( Rpderatevo.INCOME,income);
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDate getModifiedtime () {
return (UFDate) this.getAttributeValue( Rpderatevo.MODIFIEDTIME);
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDate modifiedtime) {
this.setAttributeValue( Rpderatevo.MODIFIEDTIME,modifiedtime);
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return (String) this.getAttributeValue( Rpderatevo.MODIFIER);
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.setAttributeValue( Rpderatevo.MODIFIER,modifier);
 } 

/** 
* 获取期间
*
* @return 期间
*/
public String getPeriod () {
return (String) this.getAttributeValue( Rpderatevo.PERIOD);
 } 

/** 
* 设置期间
*
* @param period 期间
*/
public void setPeriod ( String period) {
this.setAttributeValue( Rpderatevo.PERIOD,period);
 } 

/** 
* 获取员工
*
* @return 员工
*/
public String getPk_defdoc () {
return (String) this.getAttributeValue( Rpderatevo.PK_DEFDOC);
 } 

/** 
* 设置员工
*
* @param pk_defdoc 员工
*/
public void setPk_defdoc ( String pk_defdoc) {
this.setAttributeValue( Rpderatevo.PK_DEFDOC,pk_defdoc);
 } 

/** 
* 获取减免编号
*
* @return 减免编号
*/
public String getPk_derate () {
return (String) this.getAttributeValue( Rpderatevo.PK_DERATE);
 } 

/** 
* 设置减免编号
*
* @param pk_derate 减免编号
*/
public void setPk_derate ( String pk_derate) {
this.setAttributeValue( Rpderatevo.PK_DERATE,pk_derate);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( Rpderatevo.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( Rpderatevo.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( Rpderatevo.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( Rpderatevo.PK_ORG,pk_org);
 } 

/** 
* 获取Travelling allowance
*
* @return Travelling allowance
*/
public UFDouble getTrvallow () {
return (UFDouble) this.getAttributeValue( Rpderatevo.TRVALLOW);
 } 

/** 
* 设置Travelling allowance
*
* @param trvallow Travelling allowance
*/
public void setTrvallow ( UFDouble trvallow) {
this.setAttributeValue( Rpderatevo.TRVALLOW,trvallow);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Rpderatevo.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Rpderatevo.TS,ts);
 } 

/** 
* 获取Value Of Living Accomodation
*
* @return Value Of Living Accomodation
*/
public UFDouble getVola () {
return (UFDouble) this.getAttributeValue( Rpderatevo.VOLA);
 } 

/** 
* 设置Value Of Living Accomodation
*
* @param vola Value Of Living Accomodation
*/
public void setVola ( UFDouble vola) {
this.setAttributeValue( Rpderatevo.VOLA,vola);
 } 

/** 
* 获取当月工资
*
* @return 当月工资
*/
public UFDouble getWage () {
return (UFDouble) this.getAttributeValue( Rpderatevo.WAGE);
 } 

/** 
* 设置当月工资
*
* @param wage 当月工资
*/
public void setWage ( UFDouble wage) {
this.setAttributeValue( Rpderatevo.WAGE,wage);
 } 

/** 
* 获取Zakat or Fitrah
*
* @return Zakat or Fitrah
*/
public UFDouble getZof () {
return (UFDouble) this.getAttributeValue( Rpderatevo.ZOF);
 } 

/** 
* 设置Zakat or Fitrah
*
* @param zof Zakat or Fitrah
*/
public void setZof ( UFDouble zof) {
this.setAttributeValue( Rpderatevo.ZOF,zof);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.rpderate");
  }
}