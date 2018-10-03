package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/***************************************************************************
 * HR本地化：插入马来西亚HR项目需要的人员基本信息字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/

public class MalaysiaFieldStrategy extends AddFieldAbstractStrategy implements IAddLocalizationFieldStrategy {

	public MalaysiaFieldStrategy() throws BusinessException {
		defdocMap = getDefdocList();
	}
	
	@Override
	public InfoSetVO[] addLocalField(InfoSetVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		for (InfoSetVO infoSet : vos) {
			if (infoSet.getInfoset_code()
					.equals(IAddLocalizationFieldStrategy.PERSONAL_INFO_TABLE)) {
				InfoItemVO[] bodyVOs = infoSet.getInfo_item();
				
				ArrayList<Object[]> templateList = getTemplateTable("MY");
				
				// 这个查重实现应该可以放进抽象类里
				for (int i = templateList.size() - 1; i >= 0; i--) {
					for (InfoItemVO item : bodyVOs) {
						if (templateList.get(i)[1].toString().equals(item.getItem_code())) {
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
					for (Object[] newField : templateList) {
						newBodyVOsList.add(addField(newField, newBodyVOsList.size(), newField[9] != null ? defdocMap.get(newField[9].toString()) : ""));
					}
				}
				
				infoSet.setInfo_item(newBodyVOsList.toArray(new InfoItemVO[0]));
			}
		}
		return vos;
	}
	
}
