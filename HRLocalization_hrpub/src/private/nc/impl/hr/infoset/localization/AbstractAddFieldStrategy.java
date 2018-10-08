package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.infoset.sealocal.PresetPsndocFieldVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.util.SqlWrapper;

/***************************************************************************
 * HR本地化添加字段抽象类，主要写了一些公共方法<br>
 * Created on 2018-10-02 18:38:41pm
 * @author Ethan Wu
 ***************************************************************************/
public abstract class AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	
	// 用来加载HR本地化相关的自定义档案
	protected Map<String, String> defdocMap;
	protected String countryCode;
	
	/**
	 * HR本地化：添加字段方法，选定了一些必输的属性<br>
	 * Created on 2018-10-02 18:40:09pm 
	 * @author Ethan Wu
	 */
	protected static InfoItemVO addField(PresetPsndocFieldVO newField, int showOrder, String refmodel) {
		InfoItemVO ret = new InfoItemVO();
		ret.setItem_code(newField.getItem_code());
		ret.setItem_name(newField.getItem_name());
		ret.setItem_name2(newField.getItem_name2());
		ret.setItem_name3(newField.getItem_name3());
		ret.setItem_name4(newField.getItem_name4());
		ret.setItem_name5(newField.getItem_name5());
		ret.setItem_name6(newField.getItem_name6());
		ret.setData_type(newField.getData_type());
		ret.setMax_length(newField.getMax_length());
		ret.setResid(newField.getResid());
		ret.setRespath(newField.getRespath());
		ret.setUnique_flag(newField.getUnique_flag());
		ret.setNullable(newField.getNullable());
		ret.setShoworder(showOrder);
		ret.setPrecise(newField.getPrecise());
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
			throw new BusinessException("User define file loading failure! Please check database connectivity.\n" + e.getMessage());
		}
		Map<String, String> ret = new HashMap<String, String>();
		for (Object defdoc : obj) {
			ret.put(((Object[])defdoc)[0].toString(), ((Object[])defdoc)[1].toString());
		}
		return ret;
	}
	
	protected static ArrayList<PresetPsndocFieldVO> getTemplateTable(String countryCode) throws BusinessException {
		IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(" select * from hr_infoset_item_sealocal where country in ('GLOBAL',{country_code?}) ");
		String sql = sb.toString();
		SqlWrapper sw = new SqlWrapper(sql);
		sw.bind("country_code", countryCode);
		ArrayList<PresetPsndocFieldVO> obj = null;
		try {
			obj = (ArrayList<PresetPsndocFieldVO>) queryBS.executeQuery(sw.getSql(), sw.getSqlParameter(), new BeanListProcessor(PresetPsndocFieldVO.class));
		} catch (BusinessException e) {
			Logger.error(e);
			throw new BusinessException("Localization pre-set template table loading failure! Please check database connectivity.\n " + e.getMessage());
		}
		return obj;
	}

	@Override
	public InfoSetVO[] addLocalField(InfoSetVO[] vos) throws BusinessException {
		defdocMap = getDefdocList();
		if (vos == null || vos.length == 0) {
			return null;
		}
		for (InfoSetVO infoSet : vos) {
			if (infoSet.getInfoset_code()
					.equals(IAddLocalizationFieldStrategy.PERSONAL_INFO_TABLE)) {
				InfoItemVO[] bodyVOs = infoSet.getInfo_item();
				
				ArrayList<PresetPsndocFieldVO> templateList = getTemplateTable(countryCode);
				
				for (int i = templateList.size() - 1; i >= 0; i--) {
					for (InfoItemVO item : bodyVOs) {
						if (templateList.get(i).getItem_code().equals(item.getItem_code())) {
							templateList.remove(i);
							break;
						}
					}
				}
				
				ArrayList<InfoItemVO> newBodyVOsList = 
						new ArrayList<InfoItemVO>(Arrays.asList(bodyVOs));
				
				if (templateList.size() == 0) {
					return vos;
				} else {
					for (PresetPsndocFieldVO newField : templateList) {
						newBodyVOsList.add(addField(newField, newBodyVOsList.size(), newField.getRef_model_name() != null 
								&& defdocMap.containsKey(newField.getRef_model_name())
								? defdocMap.get(newField.getRef_model_name()) : null));
					}
				}
				
				infoSet.setInfo_item(newBodyVOsList.toArray(new InfoItemVO[0]));
			}
		}
		return vos;
	}

}
