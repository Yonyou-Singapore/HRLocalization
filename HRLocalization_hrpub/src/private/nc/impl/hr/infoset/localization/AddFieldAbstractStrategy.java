package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/***************************************************************************
 * HR本地化添加字段抽象类，主要写了一些公共方法<br>
 * Created on 2018-10-02 18:38:41pm
 * @author Ethan Wu
 ***************************************************************************/
public abstract class AddFieldAbstractStrategy {
	
	// 用来加载HR本地化相关的自定义档案
	protected Map<String, String> defdocMap;
	
	/**
	 * HR本地化：添加字段方法，选定了一些必输的属性<br>
	 * Created on 2018-10-02 18:40:09pm 
	 * @author Ethan Wu
	 * @param code
	 * @param name
	 * @param name2
	 * @param dataType
	 * @param maxLength
	 * @param resid
	 * @param respath
	 * @param unique
	 * @param nullable
	 * @param showOrder
	 * @param precision
	 * @param refmodel
	 * @return
	 */
	protected static InfoItemVO addField(String code, String name, String name2, int dataType, 
			int maxLength, String resid, String respath, UFBoolean unique, UFBoolean nullable,
			int showOrder, int precision, String refmodel) {
		InfoItemVO ret = new InfoItemVO();
		ret.setItem_code(code);
		ret.setItem_name(name);
		ret.setItem_name2(name2);
		ret.setData_type(dataType);
		ret.setMax_length(maxLength);
		ret.setResid(resid);
		ret.setRespath(respath);
		ret.setUnique_flag(unique);
		ret.setNullable(nullable);
		ret.setShoworder(showOrder);
		ret.setPrecise(precision);
		ret.setRef_model_name(refmodel);
		ret.setStatus(VOStatus.NEW);
		return ret;
	}
	
	/**
	 * HR本地化：找到所有HR本地化相关的自定义档案，用于字段生成<br>
	 * Created on 2018-10-02 23:25:10pm
	 * @author Ethan Wu
	 * @return Map<String, String>
	 */
	protected static Map<String, String> getDefdocList() {
		IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(" select reserv3, pk_refinfo from bd_refinfo where reserv3 like 'SEALOCAL%' ");
		ArrayList<Object> obj = null;
		try {
			obj = (ArrayList<Object>) queryBS.executeQuery(sb.toString(), new ArrayListProcessor());
		} catch (BusinessException e) {
			Logger.error(e);
			ExceptionUtils.wrappBusinessException("Localization user-define files query fails");
		}
		Map<String, String> ret = new HashMap<String, String>();
		for (Object defdoc : obj) {
			ret.put(((Object[])defdoc)[0].toString(), ((Object[])defdoc)[1].toString());
		}
		return ret;
	}
	
	protected static Map<String, String> getAddedFields(InfoItemVO[] bodyVOs) {
		Map<String, String> ret = new HashMap<String, String>();
		
		return ret;
	}
}
