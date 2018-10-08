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
public abstract class AbstractAddFieldStrategy {
	
	// 用来加载HR本地化相关的自定义档案
	protected Map<String, String> defdocMap;
	
	/**
	 * HR本地化：添加字段方法，选定了一些必输的属性<br>
	 * Created on 2018-10-02 18:40:09pm 
	 * @author Ethan Wu
	 */
	protected static InfoItemVO addField(Object[] newField, int showOrder, String refmodel) {
		InfoItemVO ret = new InfoItemVO();
		ret.setItem_code(newField[1].toString());
		ret.setItem_name(newField[2].toString());
		ret.setItem_name2(newField[3] == null ? null : newField[3].toString());
		ret.setItem_name3(newField[4] == null ? null : newField[4].toString());
		ret.setItem_name4(newField[5] == null ? null : newField[5].toString());
		ret.setItem_name5(newField[6] == null ? null : newField[6].toString());
		ret.setItem_name6(newField[7] == null ? null : newField[7].toString());
		ret.setData_type((int) newField[8]);
		ret.setMax_length((int) newField[10]);
		ret.setResid(newField[12].toString());
		ret.setRespath(newField[13].toString());
		ret.setUnique_flag(new UFBoolean(newField[15].toString()));
		ret.setNullable(new UFBoolean(newField[14].toString()));
		ret.setShoworder(showOrder);
		ret.setPrecise((int) newField[11]);
		ret.setRef_model_name(refmodel);
		// 默认全局属性和自定义项 供后续修改删除
		ret.setCustom_attr(UFBoolean.TRUE);
		ret.setPk_org("GLOBLE00000000000000");
		ret.setPk_group("GLOBLE00000000000000");
		// 默认非隐藏 不只读
		ret.setHided(UFBoolean.FALSE);
		ret.setRead_only(UFBoolean.FALSE);
		ret.setStatus(VOStatus.NEW);
		return ret;
	}
	
	/**
	 * HR本地化：找到所有HR本地化相关的自定义档案，用于字段生成<br>
	 * Created on 2018-10-02 23:25:10pm
	 * @author Ethan Wu
	 * @return Map<String, String>
	 * @throws BusinessException 
	 */
	protected static Map<String, String> getDefdocList() throws BusinessException {
		IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(" select reserv3, pk_refinfo from bd_refinfo where reserv3 like 'SEALOCAL%' ");
		ArrayList<Object> obj = null;
		try {
			obj = (ArrayList<Object>) queryBS.executeQuery(sb.toString(), new ArrayListProcessor());
		} catch (BusinessException e) {
			Logger.error(e);
			throw new BusinessException("User define file loading failure! Please check database connectivity.");
		}
		Map<String, String> ret = new HashMap<String, String>();
		for (Object defdoc : obj) {
			ret.put(((Object[])defdoc)[0].toString(), ((Object[])defdoc)[1].toString());
		}
		return ret;
	}
	
	// TODO: 这个方法有两个需要refactor的地方：
	// 1. 实现带参与查询
	// 2. 将临时表定义成一个VO对象然后用BeanListProcessor查询
	protected static ArrayList<Object[]> getTemplateTable(String countryCode) throws BusinessException {
		IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(" select * from hr_infoset_item_sealocal where country in ('GLOBAL','" + countryCode + "') ");
		ArrayList<Object> obj = null;
		try {
			obj = (ArrayList<Object>) queryBS.executeQuery(sb.toString(), new ArrayListProcessor());
		} catch (BusinessException e) {
			Logger.error(e);
			throw new BusinessException("Localization pre-set template table loading failure! Please check database connectivity.\n " + e.getMessage());
		}
		ArrayList<Object[]> ret = new ArrayList<Object[]>();
		for (Object o : obj) {
			ret.add((Object[]) o);
		}
		return ret;
	}
}
