package nc.itf.hrrp.service;

import java.io.IOException;

import nc.vo.hrrp.report.AggReport;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import com.itextpdf.text.DocumentException;

/**
 * <p>Title: IPdfOprator</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 */
public interface IPdfOprator {
	/**
	 * <p>方法名称：PdfBuild</p>
	 * <p>方法描述：构建PDF</p>
	 * @author 
	 * @throws Exception 
	 */
	public byte[] pdfBuild() throws BusinessException, IOException, DocumentException;
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
