package nc.vo.hrrp.report;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class Field extends SuperVO {
/**
	 * 
	 */
	private static final long serialVersionUID = 5329297668313541084L;
/**
*截取字符
*/
public static final String CUTNUM="cutnum";
/**
*对应字段
*/
public static final String DBFIELD="dbfield";
/**
*对应行
*/
public static final String DBROW="dbrow";
/**
*描述
*/
public static final String DESCRIPTION="description";
/**
*小数位
*/
public static final String DICNUM="dicnum";
/**
*编号
*/
public static final String FIELDID="fieldid";
/**
*数据格式
*/
public static final String FORMAT="format";
/**
*报表字段名
*/
public static final String NAME="name";
/**
*是否只读
*/
public static final String READONLY="readonly";
/**
*上层单据主键
*/
public static final String REPID="repid";
/**
*分割格式
*/
public static final String SPLFMT="splfmt";
/**
*时间戳
*/
public static final String TS="ts";

public static final String TOTALTYPE = "totaltype";

public static final String FONTSIZE = "fontsize";

/** 
* 获取截取字符
*
* @return 截取字符
*/
public Integer getCutnum () {
return (Integer) this.getAttributeValue( Field.CUTNUM);
 } 

/** 
* 设置截取字符
*
* @param cutnum 截取字符
*/
public void setCutnum ( Integer cutnum) {
this.setAttributeValue( Field.CUTNUM,cutnum);
 } 

/** 
* 获取对应字段
*
* @return 对应字段
*/
public String getDbfield () {
return (String) this.getAttributeValue( Field.DBFIELD);
 } 

/** 
* 设置对应字段
*
* @param dbfield 对应字段
*/
public void setDbfield ( String dbfield) {
this.setAttributeValue( Field.DBFIELD,dbfield);
 } 

/** 
* 获取对应行
*
* @return 对应行
*/
public Integer getDbrow () {
return (Integer) this.getAttributeValue( Field.DBROW);
 } 

/** 
* 设置对应行
*
* @param dbrow 对应行
*/
public void setDbrow ( Integer dbrow) {
this.setAttributeValue( Field.DBROW,dbrow);
 } 

/** 
* 获取描述
*
* @return 描述
*/
public String getDescription () {
return (String) this.getAttributeValue( Field.DESCRIPTION);
 } 

/** 
* 设置描述
*
* @param description 描述
*/
public void setDescription ( String description) {
this.setAttributeValue( Field.DESCRIPTION,description);
 } 

/** 
* 获取小数位
*
* @return 小数位
*/
public Integer getDicnum () {
return (Integer) this.getAttributeValue( Field.DICNUM);
 } 

/** 
* 设置小数位
*
* @param dicnum 小数位
*/
public void setDicnum ( Integer dicnum) {
this.setAttributeValue( Field.DICNUM,dicnum);
 } 

/** 
* 获取编号
*
* @return 编号
*/
public String getFieldid () {
return (String) this.getAttributeValue( Field.FIELDID);
 } 

/** 
* 设置编号
*
* @param fieldid 编号
*/
public void setFieldid ( String fieldid) {
this.setAttributeValue( Field.FIELDID,fieldid);
 } 

/** 
* 获取数据格式
*
* @return 数据格式
*/
public String getFormat () {
return (String) this.getAttributeValue( Field.FORMAT);
 } 

/** 
* 设置数据格式
*
* @param format 数据格式
*/
public void setFormat ( String format) {
this.setAttributeValue( Field.FORMAT,format);
 } 

/** 
* 获取报表字段名
*
* @return 报表字段名
*/
public String getName () {
return (String) this.getAttributeValue( Field.NAME);
 } 

/** 
* 设置报表字段名
*
* @param name 报表字段名
*/
public void setName ( String name) {
this.setAttributeValue( Field.NAME,name);
 } 

/** 
* 获取是否只读
*
* @return 是否只读
*/
public String getReadonly () {
return (String) this.getAttributeValue( Field.READONLY);
 } 

/** 
* 设置是否只读
*
* @param readonly 是否只读
*/
public void setReadonly ( String readonly) {
this.setAttributeValue( Field.READONLY,readonly);
 } 

/** 
* 获取上层单据主键
*
* @return 上层单据主键
*/
public String getRepid () {
return (String) this.getAttributeValue( Field.REPID);
 } 

/** 
* 设置上层单据主键
*
* @param repid 上层单据主键
*/
public void setRepid ( String repid) {
this.setAttributeValue( Field.REPID,repid);
 } 

/** 
* 获取分割格式
*
* @return 分割格式
*/
public String getSplfmt () {
return (String) this.getAttributeValue( Field.SPLFMT);
 } 

/** 
* 设置分割格式
*
* @param splfmt 分割格式
*/
public void setSplfmt ( String splfmt) {
this.setAttributeValue( Field.SPLFMT,splfmt);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Field.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Field.TS,ts);
 } 

public void setTotaltype(String totaltype) {
	this.setAttributeValue(Field.TOTALTYPE, totaltype);
}

public void setFontsize(Integer fontsize) {
	this.setAttributeValue(Field.FONTSIZE, fontsize);
}

public String getTotaltype() {
	return (String)this.getAttributeValue( Field.TOTALTYPE);
}

public Integer getFontsize() {
	return (Integer)this.getAttributeValue( Field.FONTSIZE);
}

@Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.Field");
  }
}