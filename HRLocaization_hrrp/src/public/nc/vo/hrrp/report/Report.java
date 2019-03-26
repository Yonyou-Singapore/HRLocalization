package nc.vo.hrrp.report;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class Report extends SuperVO {
/**
	 * 
	 */
	private static final long serialVersionUID = -8174155480139180449L;
/**
*单据号
*/
public static final String BILLNO="billno";
/**
*单据类型
*/
public static final String BILLTYPE="billtype";
/**
*复制页码
*/
public static final String COPYPAGENO="copypageno";
/**
*创建时间
*/
public static final String CREATIONTIME="creationtime";
/**
*创建人
*/
public static final String CREATOR="creator";
/**
*描述
*/
public static final String DESCRIPTION="description";
/**
*输入路径
*/
public static final String INPUT="input";
/**
*是否分页
*/
public static final String ISPAGE="ispage";
/**
*修改时间
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*修改人
*/
public static final String MODIFIER="modifier";
/**
*输出路径
*/
public static final String OUTPUT="output";
/**
*每页记录数
*/
public static final String PAGENUM="pagenum";
/**
*集团
*/
public static final String PK_GROUP="pk_group";
/**
*组织
*/
public static final String PK_ORG="pk_org";
/**
*存储过程
*/
public static final String PROCNAME="procname";
/**
*存储过程参数
*/
public static final String PROCPARA="procpara";
/**
*报表编号
*/
public static final String REPID="repid";
/**
*报表名
*/
public static final String REPNAME="repname";
/**
*SQL语句
*/
public static final String SQL="sql";
/**
*时间戳
*/
public static final String TS="ts";
/** 
* 获取单据号
*
* @return 单据号
*/
public String getBillno () {
return (String) this.getAttributeValue( Report.BILLNO);
 } 

/** 
* 设置单据号
*
* @param billno 单据号
*/
public void setBillno ( String billno) {
this.setAttributeValue( Report.BILLNO,billno);
 } 

/** 
* 获取单据类型
*
* @return 单据类型
*/
public String getBilltype () {
return (String) this.getAttributeValue( Report.BILLTYPE);
 } 

/** 
* 设置单据类型
*
* @param billtype 单据类型
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( Report.BILLTYPE,billtype);
 } 

/** 
* 获取复制页码
*
* @return 复制页码
*/
public Integer getCopypageno () {
return (Integer) this.getAttributeValue( Report.COPYPAGENO);
 } 

/** 
* 设置复制页码
*
* @param copypageno 复制页码
*/
public void setCopypageno ( Integer copypageno) {
this.setAttributeValue( Report.COPYPAGENO,copypageno);
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDate getCreationtime () {
return (UFDate) this.getAttributeValue( Report.CREATIONTIME);
 } 

/** 
* 设置创建时间
*
* @param creationtime 创建时间
*/
public void setCreationtime ( UFDate creationtime) {
this.setAttributeValue( Report.CREATIONTIME,creationtime);
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getCreator () {
return (String) this.getAttributeValue( Report.CREATOR);
 } 

/** 
* 设置创建人
*
* @param creator 创建人
*/
public void setCreator ( String creator) {
this.setAttributeValue( Report.CREATOR,creator);
 } 

/** 
* 获取描述
*
* @return 描述
*/
public String getDescription () {
return (String) this.getAttributeValue( Report.DESCRIPTION);
 } 

/** 
* 设置描述
*
* @param description 描述
*/
public void setDescription ( String description) {
this.setAttributeValue( Report.DESCRIPTION,description);
 } 

/** 
* 获取输入路径
*
* @return 输入路径
*/
public String getInput () {
return (String) this.getAttributeValue( Report.INPUT);
 } 

/** 
* 设置输入路径
*
* @param input 输入路径
*/
public void setInput ( String input) {
this.setAttributeValue( Report.INPUT,input);
 } 

/** 
* 获取是否分页
*
* @return 是否分页
*/
public String getIspage () {
return (String) this.getAttributeValue( Report.ISPAGE);
 } 

/** 
* 设置是否分页
*
* @param ispage 是否分页
*/
public void setIspage ( String ispage) {
this.setAttributeValue( Report.ISPAGE,ispage);
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDate getModifiedtime () {
return (UFDate) this.getAttributeValue( Report.MODIFIEDTIME);
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDate modifiedtime) {
this.setAttributeValue( Report.MODIFIEDTIME,modifiedtime);
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return (String) this.getAttributeValue( Report.MODIFIER);
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.setAttributeValue( Report.MODIFIER,modifier);
 } 

/** 
* 获取输出路径
*
* @return 输出路径
*/
public String getOutput () {
return (String) this.getAttributeValue( Report.OUTPUT);
 } 

/** 
* 设置输出路径
*
* @param output 输出路径
*/
public void setOutput ( String output) {
this.setAttributeValue( Report.OUTPUT,output);
 } 

/** 
* 获取每页记录数
*
* @return 每页记录数
*/
public Integer getPagenum () {
return (Integer) this.getAttributeValue( Report.PAGENUM);
 } 

/** 
* 设置每页记录数
*
* @param pagenum 每页记录数
*/
public void setPagenum ( Integer pagenum) {
this.setAttributeValue( Report.PAGENUM,pagenum);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( Report.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( Report.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( Report.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( Report.PK_ORG,pk_org);
 } 

/** 
* 获取存储过程
*
* @return 存储过程
*/
public String getProcname () {
return (String) this.getAttributeValue( Report.PROCNAME);
 } 

/** 
* 设置存储过程
*
* @param procname 存储过程
*/
public void setProcname ( String procname) {
this.setAttributeValue( Report.PROCNAME,procname);
 } 

/** 
* 获取存储过程参数
*
* @return 存储过程参数
*/
public String getProcpara () {
return (String) this.getAttributeValue( Report.PROCPARA);
 } 

/** 
* 设置存储过程参数
*
* @param procpara 存储过程参数
*/
public void setProcpara ( String procpara) {
this.setAttributeValue( Report.PROCPARA,procpara);
 } 

/** 
* 获取报表编号
*
* @return 报表编号
*/
public String getRepid () {
return (String) this.getAttributeValue( Report.REPID);
 } 

/** 
* 设置报表编号
*
* @param repid 报表编号
*/
public void setRepid ( String repid) {
this.setAttributeValue( Report.REPID,repid);
 } 

/** 
* 获取报表名
*
* @return 报表名
*/
public String getRepname () {
return (String) this.getAttributeValue( Report.REPNAME);
 } 

/** 
* 设置报表名
*
* @param repname 报表名
*/
public void setRepname ( String repname) {
this.setAttributeValue( Report.REPNAME,repname);
 } 

/** 
* 获取SQL语句
*
* @return SQL语句
*/
public String getSql () {
return (String) this.getAttributeValue( Report.SQL);
 } 

/** 
* 设置SQL语句
*
* @param sql SQL语句
*/
public void setSql ( String sql) {
this.setAttributeValue( Report.SQL,sql);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Report.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Report.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.Report");
  }
}