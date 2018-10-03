package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * HR本地化：插入新加坡HR项目需要的人员基本信息字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/
public class SingaporeFieldStrategy extends AddFieldAbstractStrategy implements IAddLocalizationFieldStrategy {

	private static final String COUNTRY_CODE = "SG";
	
	public SingaporeFieldStrategy() throws BusinessException {
		defdocMap = getDefdocList();
	}
	
	@Override
	public InfoSetVO[] addLocalField(InfoSetVO[] vos) {
		// TODO To add Singapore implementation for the fields
		return vos;
	}

}
