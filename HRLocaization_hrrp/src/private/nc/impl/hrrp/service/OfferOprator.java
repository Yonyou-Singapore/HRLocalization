package nc.impl.hrrp.service;

import java.util.List;
import java.util.Map;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.hrrp.service.IBaseService;
import nc.itf.hrrp.service.IOfferOprator;
import nc.vo.pub.BusinessException;

/**
 * <p>Title: OfferOprator</P> <p>Description: </p>
 * @version 1.0
 * @since 2015-6-16
 */
public class OfferOprator implements IOfferOprator {

	private String strWhr = "";
	private String ProcName = "vpcb";
	private String SqlStr="SELECT * FROM vpcb";
	/**
	 * @return the procName
	 */
	@Override
	public String getProcName() {
		return ProcName;
	}

	/**
	 * @param procName the procName to set
	 */
	@Override
	public void setSqlStr(String sqlStr) {
		SqlStr = sqlStr;
	}
	
	@Override
	public String getSqlStr() {
		return SqlStr;
	}

	/**
	 * @param procName the procName to set
	 */
	@Override
	public void setProcName(String procName) {
		ProcName = procName;
	}

	private List<Map> lstData = null;
	private final String key="offer";

	/**
	 * <p>∑Ω∑®√˚≥∆£∫BankOfferBuild</p> <p>∑Ω∑®√Ë ˆ£∫</p>
	 * @return
	 * @throws Exception
	 * @since 2015-6-16
	 */
	public byte[] OfferBuild() throws Exception {
//		lstData = getDatas(getProcName() + " '" + getStrWhr() + "'");
		lstData=getDatas(getSqlStr()+" "+getStrWhr());
		StringBuffer rst=new StringBuffer();
//				rst="H0000010022753000xx0ZZZZ                     231104                                                                             \r\n"+
//				"D0000020022750000XX0C0021000000029                                 0007528380000             π“¬ ß«? ‘∑∏‘‚æ∏‘Ï                 \r\n"+
//				"D0000030022750006xx0C0001600000029                                 0007823920000             π“? ÿ¿“?ªŸ√≥§ÿªµ?               \r\n"+
//				"D0000040022750358xx0C0010000000029                                 0007767750000             π“¬ª√–¡≥∑—»π?µ—≥±Ï‰æ‚√®πÏ         \r\n"+
//				"D0000050022750001xx0C0001500000029                                 0007084880000             π“ß ¡„?ª√–µ‘‡?                   \r\n"+
//				"T0000060022753000xx00000000000000000000000000040000034100000                                                                    \r\n";
		//TODO …˙≥…◊÷∑˚¥Æ
		if (lstData == null) 
			throw new BusinessException("Data Has ERROR");
		if (lstData.size() < 1)
			throw new BusinessException("Data Is Null");
		for(Map mp:lstData)
		{
			Object obj=mp.get(key);
			if(obj!=null)
				rst.append(obj.toString()+"\r\n");
		}
		byte[] bts = rst.toString().getBytes("UTF-8");
		return bts;
	}

	/**
	 * <p>∑Ω∑®√˚≥∆£∫getStrWhr</p> <p>∑Ω∑®√Ë ˆ£∫</p>
	 * @return
	 * @since 2015-6-16
	 */
	@Override
	public String getStrWhr() {
		if (strWhr == null)
			strWhr = "";
		else if (strWhr.trim().length() < 1)
			strWhr = "";
		else if (!strWhr.toLowerCase().startsWith(" where"))
			strWhr = " where " + strWhr;//.replace("'", "''");
		return strWhr;
	}

	/**
	 * <p>∑Ω∑®√˚≥∆£∫setStrWhr</p> <p>∑Ω∑®√Ë ˆ£∫</p>
	 * @param strWhr
	 * @since 2015-6-16
	 */
	@Override
	public void setStrWhr(String strWhr) {
		if (strWhr == null)
			strWhr = "";
		else if (!strWhr.toLowerCase().startsWith(" where"))
			strWhr = " where " + strWhr;//.replace("'", "''");
		;
		this.strWhr = strWhr;
	}

	private List<Map> getDatas(String sqlStr) {
		IBaseService ibs = NCLocator.getInstance().lookup(IBaseService.class);
		try {
			return ibs.QueryBySql(sqlStr);
		} catch (DAOException e) {
			return null;
		}
	}
}
