package nc.vo.wa.localenum;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class CpfWagCategoryEnum extends MDEnum{

	public CpfWagCategoryEnum(IEnumValue enumValue) {
		super(enumValue);
	}
	public static final CpfWagCategoryEnum CONTRIBUTE_TO_ORDINARY_WAGE = MDEnum.valueOf(CpfWagCategoryEnum.class, "ow");
	public static final CpfWagCategoryEnum CONTRIBUTE_TO_ADDITIONAL_WAGE = MDEnum.valueOf(CpfWagCategoryEnum.class, "aw");


}
