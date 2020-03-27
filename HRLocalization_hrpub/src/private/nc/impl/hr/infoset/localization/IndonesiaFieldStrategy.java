package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.pub.BusinessException;

public class IndonesiaFieldStrategy extends AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	public IndonesiaFieldStrategy() throws BusinessException {
		countryCode = "IND";
	}
}

