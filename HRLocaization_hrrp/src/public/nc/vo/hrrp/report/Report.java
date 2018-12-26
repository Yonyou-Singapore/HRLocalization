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
*���ݺ�
*/
public static final String BILLNO="billno";
/**
*��������
*/
public static final String BILLTYPE="billtype";
/**
*����ҳ��
*/
public static final String COPYPAGENO="copypageno";
/**
*����ʱ��
*/
public static final String CREATIONTIME="creationtime";
/**
*������
*/
public static final String CREATOR="creator";
/**
*����
*/
public static final String DESCRIPTION="description";
/**
*����·��
*/
public static final String INPUT="input";
/**
*�Ƿ��ҳ
*/
public static final String ISPAGE="ispage";
/**
*�޸�ʱ��
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*�޸���
*/
public static final String MODIFIER="modifier";
/**
*���·��
*/
public static final String OUTPUT="output";
/**
*ÿҳ��¼��
*/
public static final String PAGENUM="pagenum";
/**
*����
*/
public static final String PK_GROUP="pk_group";
/**
*��֯
*/
public static final String PK_ORG="pk_org";
/**
*�洢����
*/
public static final String PROCNAME="procname";
/**
*�洢���̲���
*/
public static final String PROCPARA="procpara";
/**
*������
*/
public static final String REPID="repid";
/**
*������
*/
public static final String REPNAME="repname";
/**
*SQL���
*/
public static final String SQL="sql";
/**
*ʱ���
*/
public static final String TS="ts";
/** 
* ��ȡ���ݺ�
*
* @return ���ݺ�
*/
public String getBillno () {
return (String) this.getAttributeValue( Report.BILLNO);
 } 

/** 
* ���õ��ݺ�
*
* @param billno ���ݺ�
*/
public void setBillno ( String billno) {
this.setAttributeValue( Report.BILLNO,billno);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getBilltype () {
return (String) this.getAttributeValue( Report.BILLTYPE);
 } 

/** 
* ���õ�������
*
* @param billtype ��������
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( Report.BILLTYPE,billtype);
 } 

/** 
* ��ȡ����ҳ��
*
* @return ����ҳ��
*/
public Integer getCopypageno () {
return (Integer) this.getAttributeValue( Report.COPYPAGENO);
 } 

/** 
* ���ø���ҳ��
*
* @param copypageno ����ҳ��
*/
public void setCopypageno ( Integer copypageno) {
this.setAttributeValue( Report.COPYPAGENO,copypageno);
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDate getCreationtime () {
return (UFDate) this.getAttributeValue( Report.CREATIONTIME);
 } 

/** 
* ���ô���ʱ��
*
* @param creationtime ����ʱ��
*/
public void setCreationtime ( UFDate creationtime) {
this.setAttributeValue( Report.CREATIONTIME,creationtime);
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getCreator () {
return (String) this.getAttributeValue( Report.CREATOR);
 } 

/** 
* ���ô�����
*
* @param creator ������
*/
public void setCreator ( String creator) {
this.setAttributeValue( Report.CREATOR,creator);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getDescription () {
return (String) this.getAttributeValue( Report.DESCRIPTION);
 } 

/** 
* ��������
*
* @param description ����
*/
public void setDescription ( String description) {
this.setAttributeValue( Report.DESCRIPTION,description);
 } 

/** 
* ��ȡ����·��
*
* @return ����·��
*/
public String getInput () {
return (String) this.getAttributeValue( Report.INPUT);
 } 

/** 
* ��������·��
*
* @param input ����·��
*/
public void setInput ( String input) {
this.setAttributeValue( Report.INPUT,input);
 } 

/** 
* ��ȡ�Ƿ��ҳ
*
* @return �Ƿ��ҳ
*/
public String getIspage () {
return (String) this.getAttributeValue( Report.ISPAGE);
 } 

/** 
* �����Ƿ��ҳ
*
* @param ispage �Ƿ��ҳ
*/
public void setIspage ( String ispage) {
this.setAttributeValue( Report.ISPAGE,ispage);
 } 

/** 
* ��ȡ�޸�ʱ��
*
* @return �޸�ʱ��
*/
public UFDate getModifiedtime () {
return (UFDate) this.getAttributeValue( Report.MODIFIEDTIME);
 } 

/** 
* �����޸�ʱ��
*
* @param modifiedtime �޸�ʱ��
*/
public void setModifiedtime ( UFDate modifiedtime) {
this.setAttributeValue( Report.MODIFIEDTIME,modifiedtime);
 } 

/** 
* ��ȡ�޸���
*
* @return �޸���
*/
public String getModifier () {
return (String) this.getAttributeValue( Report.MODIFIER);
 } 

/** 
* �����޸���
*
* @param modifier �޸���
*/
public void setModifier ( String modifier) {
this.setAttributeValue( Report.MODIFIER,modifier);
 } 

/** 
* ��ȡ���·��
*
* @return ���·��
*/
public String getOutput () {
return (String) this.getAttributeValue( Report.OUTPUT);
 } 

/** 
* �������·��
*
* @param output ���·��
*/
public void setOutput ( String output) {
this.setAttributeValue( Report.OUTPUT,output);
 } 

/** 
* ��ȡÿҳ��¼��
*
* @return ÿҳ��¼��
*/
public Integer getPagenum () {
return (Integer) this.getAttributeValue( Report.PAGENUM);
 } 

/** 
* ����ÿҳ��¼��
*
* @param pagenum ÿҳ��¼��
*/
public void setPagenum ( Integer pagenum) {
this.setAttributeValue( Report.PAGENUM,pagenum);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return (String) this.getAttributeValue( Report.PK_GROUP);
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( Report.PK_GROUP,pk_group);
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return (String) this.getAttributeValue( Report.PK_ORG);
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( Report.PK_ORG,pk_org);
 } 

/** 
* ��ȡ�洢����
*
* @return �洢����
*/
public String getProcname () {
return (String) this.getAttributeValue( Report.PROCNAME);
 } 

/** 
* ���ô洢����
*
* @param procname �洢����
*/
public void setProcname ( String procname) {
this.setAttributeValue( Report.PROCNAME,procname);
 } 

/** 
* ��ȡ�洢���̲���
*
* @return �洢���̲���
*/
public String getProcpara () {
return (String) this.getAttributeValue( Report.PROCPARA);
 } 

/** 
* ���ô洢���̲���
*
* @param procpara �洢���̲���
*/
public void setProcpara ( String procpara) {
this.setAttributeValue( Report.PROCPARA,procpara);
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getRepid () {
return (String) this.getAttributeValue( Report.REPID);
 } 

/** 
* ���ñ�����
*
* @param repid ������
*/
public void setRepid ( String repid) {
this.setAttributeValue( Report.REPID,repid);
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getRepname () {
return (String) this.getAttributeValue( Report.REPNAME);
 } 

/** 
* ���ñ�����
*
* @param repname ������
*/
public void setRepname ( String repname) {
this.setAttributeValue( Report.REPNAME,repname);
 } 

/** 
* ��ȡSQL���
*
* @return SQL���
*/
public String getSql () {
return (String) this.getAttributeValue( Report.SQL);
 } 

/** 
* ����SQL���
*
* @param sql SQL���
*/
public void setSql ( String sql) {
this.setAttributeValue( Report.SQL,sql);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( Report.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( Report.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrrp.Report");
  }
}