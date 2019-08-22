package nc.vo.wa.localenum;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public enum CountryItemEnum{

	MALAYSIAITEM("my"),
	SINGAPOREITEM("sg"),
	INDONESIAITEM("in"),
	PHILIPPINESITEM("ph");

	private String code;
	
	private CountryItemEnum(String code) {
		this.code = code;
	}
	
	public static String getCode(String code) {
		for (CountryItemEnum statu : CountryItemEnum.values()) {
			if (statu.getStatusCode().equals(code)) {
				return statu.code;
			}

		}
		return null;
	}
	
	public String getStatusCode() {
		return code;
	}
}
