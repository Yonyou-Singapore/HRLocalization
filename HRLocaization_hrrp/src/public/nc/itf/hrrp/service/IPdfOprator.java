package nc.itf.hrrp.service;

import nc.vo.hrrp.report.AggReport;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

/**
 * <p>Title: IPdfOprator</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 */
public interface IPdfOprator {
	/**
	 * <p>�������ƣ�PdfBuild</p>
	 * <p>��������������PDF</p>
	 * @author 
	 * @throws Exception 
	 */
	public byte[] pdfBuild() throws Exception;
	/**
	 * @return the strWhr
	 */
	public String getStrWhr();

	/**
	 * @param strWhr the strWhr to set
	 */
	public void setStrWhr(String strWhr);
	/**
	 * @return the aggvo
	 */
	public AbstractBill getAggvo();

	/**
	 * @param aggvo the aggvo to set
	 */
	public void setAggvo(AggReport aggvo);
}
