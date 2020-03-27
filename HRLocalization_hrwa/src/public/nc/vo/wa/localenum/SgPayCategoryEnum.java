package nc.vo.wa.localenum;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class SgPayCategoryEnum extends MDEnum {

	public SgPayCategoryEnum(IEnumValue enumValue) {
		super(enumValue);
	}
	public static final SgPayCategoryEnum SG_BRP = MDEnum.valueOf(SgPayCategoryEnum.class, "brp");
	public static final SgPayCategoryEnum SG_GRP = MDEnum.valueOf(SgPayCategoryEnum.class, "grp");
}
