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
*��ȡ�ַ�
*/
public static final String CUTNUM="cutnum";
/**
*��Ӧ�ֶ�
*/
public static final String DBFIELD="dbfield";
/**
*��Ӧ��
*/
public static final String DBROW="dbrow";
/**
*����
*/
public static final String DESCRIPTION="description";
/**
*С��λ
*/
public static final String DICNUM="dicnum";
/**
*���
*/
public static final String FIELDID="fieldid";
/**
*���ݸ�ʽ
*/
public static final String FORMAT="format";
/**
*�����ֶ���
*/
public static final String NAME="name";
/**
*�Ƿ�ֻ��
*/
public static final String READONLY="readonly";
/**
*�ϲ㵥������
*/
public static final String REPID="repid";
/**
*�ָ��ʽ
*/
public static final String SPLFMT="splfmt";
/**
*ʱ���
*/
public static final String TS="ts";
/** 
* ��ȡ��ȡ�ַ�
*
* @return ��ȡ�ַ�
*/
public Integer getCutnum () {
return (Integer) this.getAttributeValue( Field.CUTNUM);
 } 

/** 
* ���ý�ȡ�ַ�
*
* @param cutnum ��ȡ�ַ�
*/
public void setCutnum ( Integer cutnum) {
this.setAttributeValue( Field.CUTNUM,cutnum);
 } 

/** 
* ��ȡ��Ӧ�ֶ�
*
* @return ��Ӧ�ֶ�
*/
public String getDbfield () {
return (String) this.getAttributeValue( Field.DBFIELD);
 } 

/** 
* ���ö�Ӧ�ֶ�
*
* @param dbfield ��Ӧ�ֶ�
*/
public void setDbfield ( String dbfield) {
this.setAttributeValue( Field.DBFIELD,dbfield);
 } 

/** 
* ��ȡ��Ӧ��
*
* @return ��Ӧ��
*/
public Integer getDbrow () {
return (Integer) this.getAttributeValue( Field.DBROW);
 } 

/** 
* ���ö�Ӧ��
*
* @param dbrow ��Ӧ��
*/
public void setDbrow ( Integer dbrow) {
this.setAttributeValue( Field.DBROW,dbrow);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getDescription () {
return (String) this.getAttributeValue( Field.DESCRIPTION);
 } 

/** 
* ��������
*
* @param description ����
*/
public void setDescription ( String description) {
this.setAttributeValue( Field.DESCRIPTION,description);
 } 

/** 
* ��ȡС��λ
*
* @return С��λ
*/
public Integer getDicnum () {
return (Integer) this.getAttributeValue( Field.DICNUM);
 } 

/** 
* ����С��λ
*
* @param dicnum С��λ
*/
public void setDicnum ( Integer dicnum) {
this.setAttributeValue( Field.DICNUM,dicnum);
 } 

/** 
* ��ȡ���
*
* @return ���
*/
public String getFieldid () {
return (String) this.getAttributeValue( Field.FIELDID);
 } 

/** 
* ���ñ��
*
* @param fieldid ���
*/
public void setFieldid ( String fieldid) {
this.setAttributeValue( Field.FIELDID,fieldid);
 } 

/** 
* ��ȡ���ݸ�ʽ
*
* @return ���ݸ�ʽ
*/
public String getFormat () {
return (String) this.getAttributeValue( Field.FORMAT);
 } 

/** 
* �������ݸ�ʽ
*
* @param format ���ݸ�ʽ
*/
public void setFormat ( String format) {
this.setAttributeValue( Field.FORMAT,format);
 } 

/** 
* ��ȡ�����ֶ���
*
* @return �����ֶ���
*/
public String getName () {
return (String) this.getAttributeValue( Field.NAME);
 } 

/** 
* ���ñ����ֶ���
*
* @param name �����ֶ���
*/
public void setName ( String name) {
this.setAttributeValue( Field.NAME,name);
 } 

/** 
* ��ȡ�Ƿ�ֻ��
*
* @return �Ƿ�ֻ��
*/
public String getReadonly () {
return (String) this.getAttributeValue( Field.READONLY);
 } 

/** 
* �����Ƿ�ֻ��
*
* @param readonly �Ƿ�ֻ��
*/
public void setReadonly ( String readonly) {
this.setAttributeValue( Field.READONLY,readonly);
 } 

/** 
* ��ȡ�ϲ㵥������
*
* @return �ϲ㵥������
*/
public String getRepid () {
return (String) this.getAttributeValue( Field.REPID);
 } 

/** 
* �����ϲ㵥������
*
* @param repid �ϲ㵥������
*/
public void setRepid ( String repid) {
this.setAttributeValue( Field.REPID,repid);
 } 

/** 
* ��ȡ�ָ��ʽ
*
* @return �ָ��ʽ
*/
public String getSplfmt () {
return (String) this.getAttributeValue( Field.SPLFMT);
 } 

/** 
* ���÷ָ��ʽ
*
* @param splfmt �ָ��ʽ
*/
public void setSplfmt ( String splfmt) {
this.setAttributeValue( Field.SPLFMT,splfmt);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Field.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Field.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.Field");
  }
}