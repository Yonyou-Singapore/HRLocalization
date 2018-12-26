package nc.vo.hrrp.ds;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class Rpdrstandvo extends SuperVO {
/**
	 * 
	 */
	private static final long serialVersionUID = -2971306451646153833L;
/**
*废除时间
*/
public static final String ABOLISHDATE="abolishdate";
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
*减免项目名
*/
public static final String DERNAME="dername";
/**
*启用时间
*/
public static final String ENABLEDATE="enabledate";
/**
*匹配公式
*/
public static final String FORMULA="formula";
/**
*匹配字段
*/
public static final String MATCHFILED="matchfiled";
/**
*修改时间
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*修改人
*/
public static final String MODIFIER="modifier";
/**
*减免标准主键
*/
public static final String PK_DRSTAND="pk_drstand";
/**
*集团
*/
public static final String PK_GROUP="pk_group";
/**
*组织
*/
public static final String PK_ORG="pk_org";
/**
*优先级
*/
public static final String PRIORITY="priority";
/**
*单独限量
*/
public static final String SINGLELIMIT="singlelimit";
/**
*启用状态
*/
public static final String STATE="state";
/**
*总额限量
*/
public static final String TOTALLIMIT="totallimit";
/**
*时间戳
*/
public static final String TS="ts";
/**
*计量单位
*/
public static final String UNIT="unit";
/** 
* 获取废除时间
*
* @return 废除时间
*/
public UFDate getAbolishdate () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.ABOLISHDATE);
 } 

/** 
* 设置废除时间
*
* @param abolishdate 废除时间
*/
public void setAbolishdate ( UFDate abolishdate) {
this.setAttributeValue( Rpdrstandvo.ABOLISHDATE,abolishdate);
 } 

/** 
* 获取单据号
*
* @return 单据号
*/
public String getBillno () {
return (String) this.getAttributeValue( Rpdrstandvo.BILLNO);
 } 

/** 
* 设置单据号
*
* @param billno 单据号
*/
public void setBillno ( String billno) {
this.setAttributeValue( Rpdrstandvo.BILLNO,billno);
 } 

/** 
* 获取单据类型
*
* @return 单据类型
*/
public String getBilltype () {
return (String) this.getAttributeValue( Rpdrstandvo.BILLTYPE);
 } 

/** 
* 设置单据类型
*
* @param billtype 单据类型
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( Rpdrstandvo.BILLTYPE,billtype);
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDate getCreationtime () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.CREATIONTIME);
 } 

/** 
* 设置创建时间
*
* @param creationtime 创建时间
*/
public void setCreationtime ( UFDate creationtime) {
this.setAttributeValue( Rpdrstandvo.CREATIONTIME,creationtime);
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getCreator () {
return (String) this.getAttributeValue( Rpdrstandvo.CREATOR);
 } 

/** 
* 设置创建人
*
* @param creator 创建人
*/
public void setCreator ( String creator) {
this.setAttributeValue( Rpdrstandvo.CREATOR,creator);
 } 

/** 
* 获取减免项目名
*
* @return 减免项目名
*/
public String getDername () {
return (String) this.getAttributeValue( Rpdrstandvo.DERNAME);
 } 

/** 
* 设置减免项目名
*
* @param dername 减免项目名
*/
public void setDername ( String dername) {
this.setAttributeValue( Rpdrstandvo.DERNAME,dername);
 } 

/** 
* 获取启用时间
*
* @return 启用时间
*/
public UFDate getEnabledate () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.ENABLEDATE);
 } 

/** 
* 设置启用时间
*
* @param enabledate 启用时间
*/
public void setEnabledate ( UFDate enabledate) {
this.setAttributeValue( Rpdrstandvo.ENABLEDATE,enabledate);
 } 

/** 
* 获取匹配公式
*
* @return 匹配公式
*/
public String getFormula () {
return (String) this.getAttributeValue( Rpdrstandvo.FORMULA);
 } 

/** 
* 设置匹配公式
*
* @param formula 匹配公式
*/
public void setFormula ( String formula) {
this.setAttributeValue( Rpdrstandvo.FORMULA,formula);
 } 

/** 
* 获取匹配字段
*
* @return 匹配字段
*/
public String getMatchfiled () {
return (String) this.getAttributeValue( Rpdrstandvo.MATCHFILED);
 } 

/** 
* 设置匹配字段
*
* @param matchfiled 匹配字段
*/
public void setMatchfiled ( String matchfiled) {
this.setAttributeValue( Rpdrstandvo.MATCHFILED,matchfiled);
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDate getModifiedtime () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.MODIFIEDTIME);
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDate modifiedtime) {
this.setAttributeValue( Rpdrstandvo.MODIFIEDTIME,modifiedtime);
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return (String) this.getAttributeValue( Rpdrstandvo.MODIFIER);
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.setAttributeValue( Rpdrstandvo.MODIFIER,modifier);
 } 

/** 
* 获取减免标准主键
*
* @return 减免标准主键
*/
public String getPk_drstand () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_DRSTAND);
 } 

/** 
* 设置减免标准主键
*
* @param pk_drstand 减免标准主键
*/
public void setPk_drstand ( String pk_drstand) {
this.setAttributeValue( Rpdrstandvo.PK_DRSTAND,pk_drstand);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( Rpdrstandvo.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( Rpdrstandvo.PK_ORG,pk_org);
 } 

/** 
* 获取优先级
*
* @return 优先级
*/
public Integer getPriority () {
return (Integer) this.getAttributeValue( Rpdrstandvo.PRIORITY);
 } 

/** 
* 设置优先级
*
* @param priority 优先级
*/
public void setPriority ( Integer priority) {
this.setAttributeValue( Rpdrstandvo.PRIORITY,priority);
 } 

/** 
* 获取单独限量
*
* @return 单独限量
*/
public UFDouble getSinglelimit () {
return (UFDouble) this.getAttributeValue( Rpdrstandvo.SINGLELIMIT);
 } 

/** 
* 设置单独限量
*
* @param singlelimit 单独限量
*/
public void setSinglelimit ( UFDouble singlelimit) {
this.setAttributeValue( Rpdrstandvo.SINGLELIMIT,singlelimit);
 } 

/** 
* 获取启用状态
*
* @return 启用状态
*/
public UFBoolean getState () {
return (UFBoolean) this.getAttributeValue( Rpdrstandvo.STATE);
 } 

/** 
* 设置启用状态
*
* @param state 启用状态
*/
public void setState ( UFBoolean state) {
this.setAttributeValue( Rpdrstandvo.STATE,state);
 } 

/** 
* 获取总额限量
*
* @return 总额限量
*/
public UFDouble getTotallimit () {
return (UFDouble) this.getAttributeValue( Rpdrstandvo.TOTALLIMIT);
 } 

/** 
* 设置总额限量
*
* @param totallimit 总额限量
*/
public void setTotallimit ( UFDouble totallimit) {
this.setAttributeValue( Rpdrstandvo.TOTALLIMIT,totallimit);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Rpdrstandvo.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Rpdrstandvo.TS,ts);
 } 

/** 
* 获取计量单位
*
* @return 计量单位
*/
public String getUnit () {
return (String) this.getAttributeValue( Rpdrstandvo.UNIT);
 } 

/** 
* 设置计量单位
*
* @param unit 计量单位
*/
public void setUnit ( String unit) {
this.setAttributeValue( Rpdrstandvo.UNIT,unit);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.rpdrstand");
  }
}