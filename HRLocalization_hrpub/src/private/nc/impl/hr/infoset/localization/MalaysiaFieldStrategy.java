package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.infoset.sealocal.PresetPsndocFieldVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/***************************************************************************
 * HR本地化：插入马来西亚HR项目需要的人员基本信息字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/

public class MalaysiaFieldStrategy extends AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	
	public MalaysiaFieldStrategy() throws BusinessException {
		defdocMap = getDefdocList();
		countryCode = "MY";
	}
}
