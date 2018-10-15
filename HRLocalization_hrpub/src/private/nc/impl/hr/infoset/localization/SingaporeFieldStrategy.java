package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * HR本地化：插入新加坡HR项目需要的人员基本信息字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/
public class SingaporeFieldStrategy extends AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	
	public SingaporeFieldStrategy() throws BusinessException {
		countryCode = "SG";
	}
}
