package nc.vo.wa.localenum;


public enum CountryItemEnum{

	MALAYSIAITEM("MY"),
	SINGAPOREITEM("SG"),
	INDONESIAITEM("IND"),
	PHILIPPINESITEM("PH");

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
