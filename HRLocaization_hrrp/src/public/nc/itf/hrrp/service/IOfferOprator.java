package nc.itf.hrrp.service;

/**
 * <p>Title: IOfferBuild</P>
 * <p>Description: </p>
 * @version 1.0
 * @since 2015-6-16
 */
public interface IOfferOprator {
	/**
	 * <p>�������ƣ�PdfBuild</p>
	 * <p>�����������������б���</p>
	 * @author 
	 * @throws Exception 
	 */
	public byte[] OfferBuild() throws Exception;
	/**
	 * @return the strWhr
	 */
	public String getStrWhr();

	/**
	 * @param strWhr the strWhr to set
	 */
	public void setStrWhr(String strWhr);
	
	/**
	 * @return the procName
	 */
	public String getProcName();

	/**
	 * @param procName the procName to set
	 */
	public void setProcName(String procName);
	/**
	 * @return the procName
	 */
	public String getSqlStr();

	/**
	 * @param procName the procName to set
	 */
	public void setSqlStr(String sqlStr);

}
