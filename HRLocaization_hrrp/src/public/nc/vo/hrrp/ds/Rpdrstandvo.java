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
*�ϳ�ʱ��
*/
public static final String ABOLISHDATE="abolishdate";
/**
*���ݺ�
*/
public static final String BILLNO="billno";
/**
*��������
*/
public static final String BILLTYPE="billtype";
/**
*����ʱ��
*/
public static final String CREATIONTIME="creationtime";
/**
*������
*/
public static final String CREATOR="creator";
/**
*������Ŀ��
*/
public static final String DERNAME="dername";
/**
*����ʱ��
*/
public static final String ENABLEDATE="enabledate";
/**
*ƥ�乫ʽ
*/
public static final String FORMULA="formula";
/**
*ƥ���ֶ�
*/
public static final String MATCHFILED="matchfiled";
/**
*�޸�ʱ��
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*�޸���
*/
public static final String MODIFIER="modifier";
/**
*�����׼����
*/
public static final String PK_DRSTAND="pk_drstand";
/**
*����
*/
public static final String PK_GROUP="pk_group";
/**
*��֯
*/
public static final String PK_ORG="pk_org";
/**
*���ȼ�
*/
public static final String PRIORITY="priority";
/**
*��������
*/
public static final String SINGLELIMIT="singlelimit";
/**
*����״̬
*/
public static final String STATE="state";
/**
*�ܶ�����
*/
public static final String TOTALLIMIT="totallimit";
/**
*ʱ���
*/
public static final String TS="ts";
/**
*������λ
*/
public static final String UNIT="unit";
/** 
* ��ȡ�ϳ�ʱ��
*
* @return �ϳ�ʱ��
*/
public UFDate getAbolishdate () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.ABOLISHDATE);
 } 

/** 
* ���÷ϳ�ʱ��
*
* @param abolishdate �ϳ�ʱ��
*/
public void setAbolishdate ( UFDate abolishdate) {
this.setAttributeValue( Rpdrstandvo.ABOLISHDATE,abolishdate);
 } 

/** 
* ��ȡ���ݺ�
*
* @return ���ݺ�
*/
public String getBillno () {
return (String) this.getAttributeValue( Rpdrstandvo.BILLNO);
 } 

/** 
* ���õ��ݺ�
*
* @param billno ���ݺ�
*/
public void setBillno ( String billno) {
this.setAttributeValue( Rpdrstandvo.BILLNO,billno);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getBilltype () {
return (String) this.getAttributeValue( Rpdrstandvo.BILLTYPE);
 } 

/** 
* ���õ�������
*
* @param billtype ��������
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( Rpdrstandvo.BILLTYPE,billtype);
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDate getCreationtime () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.CREATIONTIME);
 } 

/** 
* ���ô���ʱ��
*
* @param creationtime ����ʱ��
*/
public void setCreationtime ( UFDate creationtime) {
this.setAttributeValue( Rpdrstandvo.CREATIONTIME,creationtime);
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getCreator () {
return (String) this.getAttributeValue( Rpdrstandvo.CREATOR);
 } 

/** 
* ���ô�����
*
* @param creator ������
*/
public void setCreator ( String creator) {
this.setAttributeValue( Rpdrstandvo.CREATOR,creator);
 } 

/** 
* ��ȡ������Ŀ��
*
* @return ������Ŀ��
*/
public String getDername () {
return (String) this.getAttributeValue( Rpdrstandvo.DERNAME);
 } 

/** 
* ���ü�����Ŀ��
*
* @param dername ������Ŀ��
*/
public void setDername ( String dername) {
this.setAttributeValue( Rpdrstandvo.DERNAME,dername);
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDate getEnabledate () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.ENABLEDATE);
 } 

/** 
* ��������ʱ��
*
* @param enabledate ����ʱ��
*/
public void setEnabledate ( UFDate enabledate) {
this.setAttributeValue( Rpdrstandvo.ENABLEDATE,enabledate);
 } 

/** 
* ��ȡƥ�乫ʽ
*
* @return ƥ�乫ʽ
*/
public String getFormula () {
return (String) this.getAttributeValue( Rpdrstandvo.FORMULA);
 } 

/** 
* ����ƥ�乫ʽ
*
* @param formula ƥ�乫ʽ
*/
public void setFormula ( String formula) {
this.setAttributeValue( Rpdrstandvo.FORMULA,formula);
 } 

/** 
* ��ȡƥ���ֶ�
*
* @return ƥ���ֶ�
*/
public String getMatchfiled () {
return (String) this.getAttributeValue( Rpdrstandvo.MATCHFILED);
 } 

/** 
* ����ƥ���ֶ�
*
* @param matchfiled ƥ���ֶ�
*/
public void setMatchfiled ( String matchfiled) {
this.setAttributeValue( Rpdrstandvo.MATCHFILED,matchfiled);
 } 

/** 
* ��ȡ�޸�ʱ��
*
* @return �޸�ʱ��
*/
public UFDate getModifiedtime () {
return (UFDate) this.getAttributeValue( Rpdrstandvo.MODIFIEDTIME);
 } 

/** 
* �����޸�ʱ��
*
* @param modifiedtime �޸�ʱ��
*/
public void setModifiedtime ( UFDate modifiedtime) {
this.setAttributeValue( Rpdrstandvo.MODIFIEDTIME,modifiedtime);
 } 

/** 
* ��ȡ�޸���
*
* @return �޸���
*/
public String getModifier () {
return (String) this.getAttributeValue( Rpdrstandvo.MODIFIER);
 } 

/** 
* �����޸���
*
* @param modifier �޸���
*/
public void setModifier ( String modifier) {
this.setAttributeValue( Rpdrstandvo.MODIFIER,modifier);
 } 

/** 
* ��ȡ�����׼����
*
* @return �����׼����
*/
public String getPk_drstand () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_DRSTAND);
 } 

/** 
* ���ü����׼����
*
* @param pk_drstand �����׼����
*/
public void setPk_drstand ( String pk_drstand) {
this.setAttributeValue( Rpdrstandvo.PK_DRSTAND,pk_drstand);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_GROUP);
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( Rpdrstandvo.PK_GROUP,pk_group);
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return (String) this.getAttributeValue( Rpdrstandvo.PK_ORG);
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( Rpdrstandvo.PK_ORG,pk_org);
 } 

/** 
* ��ȡ���ȼ�
*
* @return ���ȼ�
*/
public Integer getPriority () {
return (Integer) this.getAttributeValue( Rpdrstandvo.PRIORITY);
 } 

/** 
* �������ȼ�
*
* @param priority ���ȼ�
*/
public void setPriority ( Integer priority) {
this.setAttributeValue( Rpdrstandvo.PRIORITY,priority);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public UFDouble getSinglelimit () {
return (UFDouble) this.getAttributeValue( Rpdrstandvo.SINGLELIMIT);
 } 

/** 
* ���õ�������
*
* @param singlelimit ��������
*/
public void setSinglelimit ( UFDouble singlelimit) {
this.setAttributeValue( Rpdrstandvo.SINGLELIMIT,singlelimit);
 } 

/** 
* ��ȡ����״̬
*
* @return ����״̬
*/
public UFBoolean getState () {
return (UFBoolean) this.getAttributeValue( Rpdrstandvo.STATE);
 } 

/** 
* ��������״̬
*
* @param state ����״̬
*/
public void setState ( UFBoolean state) {
this.setAttributeValue( Rpdrstandvo.STATE,state);
 } 

/** 
* ��ȡ�ܶ�����
*
* @return �ܶ�����
*/
public UFDouble getTotallimit () {
return (UFDouble) this.getAttributeValue( Rpdrstandvo.TOTALLIMIT);
 } 

/** 
* �����ܶ�����
*
* @param totallimit �ܶ�����
*/
public void setTotallimit ( UFDouble totallimit) {
this.setAttributeValue( Rpdrstandvo.TOTALLIMIT,totallimit);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Rpdrstandvo.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Rpdrstandvo.TS,ts);
 } 

/** 
* ��ȡ������λ
*
* @return ������λ
*/
public String getUnit () {
return (String) this.getAttributeValue( Rpdrstandvo.UNIT);
 } 

/** 
* ���ü�����λ
*
* @param unit ������λ
*/
public void setUnit ( String unit) {
this.setAttributeValue( Rpdrstandvo.UNIT,unit);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.rpdrstand");
  }
}