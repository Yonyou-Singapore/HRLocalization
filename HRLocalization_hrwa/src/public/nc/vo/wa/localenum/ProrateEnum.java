package nc.vo.wa.localenum;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class ProrateEnum extends MDEnum {

	public ProrateEnum(IEnumValue enumValue) {
		super(enumValue);
	}
	
	public static final ProrateEnum WORKINGDAY = MDEnum.valueOf(ProrateEnum.class, 0);
	public static final ProrateEnum CALENDARDAY = MDEnum.valueOf(ProrateEnum.class, 1);
	public static final ProrateEnum REMOVE_NPL_TO_WORKINGDAY = MDEnum.valueOf(ProrateEnum.class, 2);
}
