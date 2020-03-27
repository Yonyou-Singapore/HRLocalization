package nc.vo.wa.localenum;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class ProrateEnum extends MDEnum {
	public ProrateEnum(IEnumValue enumValue) {
		super(enumValue); 
	} 
	
	public static final ProrateEnum PRORATE_BY_WORKINGDAYS = MDEnum.valueOf(ProrateEnum.class, 0);
	public static final ProrateEnum PRORATE_BY_CALENDARDAY = MDEnum.valueOf(ProrateEnum.class, 1);
	public static final ProrateEnum PRORATE_BY_CALENDAR_DIV_FIXEDDAYS = MDEnum.valueOf(ProrateEnum.class, 2);
	public static final ProrateEnum NOPRORATE = MDEnum.valueOf(ProrateEnum.class, 3);
	public static final ProrateEnum PRORATE_BY_WORK_DIV_FIXEDDAYS = MDEnum.valueOf(ProrateEnum.class, 4);


}

