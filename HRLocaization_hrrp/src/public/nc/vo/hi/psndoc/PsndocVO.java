package nc.vo.hi.psndoc;
/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/


import java.util.ArrayList;
import java.util.List;
import nc.hr.utils.MultiLangHelper;
import nc.vo.hi.pub.IUnbundleRule;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public class PsndocVO extends PsnSuperVO implements IUnbundleRule {
	private static final long serialVersionUID = 6611261511587654044L;
	private static final String _TABLE_NAME = "bd_psndoc";
	public static final String ADDR = "addr";
	public static final String BIRTHDATE = "birthdate";
	public static final String BLOODTYPE = "bloodtype";
	public static final String CENSUSADDR = "censusaddr";
	public static final String CHARACTERRPR = "characterrpr";
	public static final String CODE = "code";
	public static final String COUNTRY = "country";
	public static final String DATAORIGINFLAG = "dataoriginflag";
	public static final String DIE_DATE = "die_date";
	public static final String DIE_REMARK = "die_remark";
	public static final String EDU = "edu";
	public static final String EMAIL = "email";
	public static final String SECRET_EMAIL = "secret_email";
	public static final String ENABLESTATE = "enablestate";
	public static final String FAX = "fax";
	public static final String FILEADDRESS = "fileaddress";
	public static final String HEALTH = "health";
	public static final String HOMEPHONE = "homephone";
	public static final String ID = "id";
	public static final String IDTYPE = "idtype";
	public static final String JOINPOLITYDATE = "joinpolitydate";
	public static final String JOINWORKDATE = "joinworkdate";
	public static final String MARITAL = "marital";
	public static final String MARRIAGEDATE = "marriagedate";
	public static final String MNECODE = "mnecode";
	public static final String MOBILE = "mobile";
	public static final String NAME = "name";
	public static final String NAME2 = "name2";
	public static final String NAME3 = "name3";
	public static final String NAME4 = "name4";
	public static final String NAME5 = "name5";
	public static final String NAME6 = "name6";
	public static final String NATIONALITY = "nationality";
	public static final String NATIVEPLACE = "nativeplace";
	public static final String OFFICEPHONE = "officephone";
	public static final String PENELAUTH = "penelauth";
	public static final String PERMANRESIDE = "permanreside";
	public static final String PHOTO = "photo";
	public static final String PK_DEGREE = "pk_degree";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_HRORG = "pk_hrorg";
	public static final String PK_ORG = "pk_org";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String POLITY = "polity";
	public static final String POSTALCODE = "postalcode";
	public static final String PREVIEWPHOTO = "previewphoto";
	public static final String PROF = "prof";
	public static final String RETIREDATE = "retiredate";
	public static final String SEX = "sex";
	public static final String SHORTNAME = "shortname";
	public static final String TITLETECHPOST = "titletechpost";
	public static final String USEDNAME = "usedname";
	public static final String ISHISKEYPSN = "ishiskeypsn";
	public static final String ISSHOPASSIST = "isshopassist";
	public static final String G_1="g_1";
	public static final String G_2="g_2";
	public static final String G_3="g_3";
	public static final String G_4="g_4";
	public static final String G_5="g_5";
	public static final String G_6="g_6";
	public static final String G_7="g_7";
	public static final String TE_1="te_1";
	public static final String TE_2="te_2";
	public static final String TE_3="te_3";
	public static final String TE_4="te_4";
	public static final String TE_5="te_5";
	public static final String TE_6="te_6";
	public static final String TE_7="te_7";
	public static final String TE_8="te_8";
public static final String TR_1="tr_1";
public static final String TR_2="tr_2";
public static final String TR_3="tr_3";
public static final String TR_4="tr_4";
public static final String TR_5="tr_5";
public static final String TR_6="tr_6";
public static final String TR_7="tr_7";
public static final String TR_8="tr_8";
public static final String TR_9="tr_9";
public static final String TR_10="tr_10";
public static final String TR_11="tr_11";
public static final String TR_12="tr_12";
public static final String TR_13="tr_13";
public static final String TR_14="tr_14";
public static final String TR_15="tr_15";
public static final String TR_16="tr_16";
public static final String TR_17="tr_17";
	
//	public static final String
	private String addr;
	private UFLiteralDate birthdate;
	private String bloodtype;
	private String censusaddr;
	private String characterrpr;
	private String code;
	private String country;
	private Integer dataoriginflag;
	private UFLiteralDate die_date;
	private String die_remark;
	private String edu;
	private String email;
	private String secret_email;
	private Integer enablestate = Integer.valueOf(2);
	private String fax;
	private String fileaddress;
	private String health;
	private String homephone;
	private String id;
	private String idtype;
	private UFLiteralDate joinpolitydate;
	private UFLiteralDate joinworkdate;
	private String marital;
	private UFLiteralDate marriagedate;
	private String mnecode;
	private String mobile;
	private String name;
	private String name2;
	private String name3;
	private String name4;
	private String name5;
	private String name6;
	private String nationality;
	private String nativeplace;
	private String officephone;
	private String penelauth;
	private String permanreside;
	private Object photo;
	private String pk_degree;
	private String pk_group;
	private String pk_hrorg;
	private String pk_org;
	private String pk_psndoc;
	private String polity;
	private String postalcode;
	private Object previewphoto;
	private String prof;
	private PsnJobVO psnJobVO = new PsnJobVO();
	private PsnOrgVO psnOrgVO = new PsnOrgVO();
	private UFLiteralDate retiredate;
	private Integer sex;
	private String shortname;
	private String titletechpost;
	private String usedname;
	private UFBoolean ishiskeypsn;
	private UFBoolean isshopassist;
	private UFBoolean isuapmanage;
	private UFDouble g_1;
	private UFDouble g_2;
	private UFDouble g_3;
	private UFDouble g_4;
	private UFDouble g_5;
	private UFDouble g_6;
	private UFDouble g_7;
	private UFDouble te_1;
	private UFDouble te_2;
	private UFDouble te_3;
	private UFDouble te_4;
	private UFDouble te_5;
	private UFDouble te_6;
	private UFDouble te_7;
	private UFDouble te_8;
	private UFDouble tr_1;
	private UFDouble tr_2;
	private UFDouble tr_3;
	private UFDouble tr_4;
	private UFDouble tr_5;
	private UFDouble tr_6;
	private UFDouble tr_7;
	private UFDouble tr_8;
	private UFDouble tr_9;
	private UFDouble tr_10;
	private UFDouble tr_11;
	private UFDouble tr_12;
	private UFDouble tr_13;
	private UFDouble tr_14;
	private UFDouble tr_15;
	private UFDouble tr_16;
	private UFDouble tr_17;


	public static String getDefaultTableName() {
		return "bd_psndoc";
	}

	public String getAddr() {
		return this.addr;
	}

	public SuperVO[] getAllVO() {
		return new SuperVO[] { this, this.psnJobVO, this.psnOrgVO };
	}

	public String[] getAttributeNames() {
		String[] strAttrNames = super.getAttributeNames();

		if (strAttrNames == null) {
			return null;
		}

		List listAttrNames = new ArrayList();

		for (String strAttrName : strAttrNames) {
			strAttrName = strAttrName.toLowerCase();

			if ((strAttrName.startsWith("hi_psndoc_"))
					|| (strAttrName.startsWith("hi_psn"))
					|| (strAttrName.equals("psnjobvo")))
				continue;
			if (strAttrName.equals("psnorgvo")) {
				continue;
			}

			listAttrNames.add(strAttrName);
		}

		return ((String[]) listAttrNames.toArray(new String[listAttrNames
				.size()]));
	}

	public Object getAttributeValue(String strAttribute) {
		SuperVO superVO = getVOByRule(strAttribute);
		String strFieldName = getFieldNameByRule(strAttribute);

		if (superVO == this) {
			return super.getAttributeValue(strFieldName);
		}

		return superVO.getAttributeValue(strFieldName);
	}

	public UFLiteralDate getBirthdate() {
		return this.birthdate;
	}

	public String getBloodtype() {
		return this.bloodtype;
	}

	public String getCensusaddr() {
		return this.censusaddr;
	}

	public String getCharacterrpr() {
		return this.characterrpr;
	}

	public String getCode() {
		return this.code;
	}

	public String getCountry() {
		return this.country;
	}

	public Integer getDataoriginflag() {
		return this.dataoriginflag;
	}

	public UFLiteralDate getDie_date() {
		return this.die_date;
	}

	public String getDie_remark() {
		return this.die_remark;
	}

	public String getEdu() {
		return this.edu;
	}

	public String getEmail() {
		return this.email;
	}

	public Integer getEnablestate() {
		return this.enablestate;
	}

	public String getFax() {
		return this.fax;
	}

	public String getFieldNameByRule(String strFieldName) {
		String strLowerCaseFieldName = strFieldName.toLowerCase();

		if ((strLowerCaseFieldName.startsWith("hi_psnorg_"))
				|| (strLowerCaseFieldName.startsWith("hi_psnjob_"))
				|| (strLowerCaseFieldName.startsWith("bd_psndoc_"))) {
			return strFieldName.substring(10);
		}

		return strFieldName;
	}

	public String getFileaddress() {
		return this.fileaddress;
	}

	public String getHealth() {
		return this.health;
	}

	public String getHomephone() {
		return this.homephone;
	}

	public String getId() {
		return this.id;
	}

	public String getIdtype() {
		return this.idtype;
	}

	public UFLiteralDate getJoinpolitydate() {
		return this.joinpolitydate;
	}

	public UFLiteralDate getJoinworkdate() {
		return this.joinworkdate;
	}

	public String getMarital() {
		return this.marital;
	}

	public UFLiteralDate getMarriagedate() {
		return this.marriagedate;
	}

	public String getMnecode() {
		return this.mnecode;
	}

	public String getMobile() {
		return this.mobile;
	}

	public String getMultiLangName() {
		return MultiLangHelper.getName(this);
	}

	public String getName() {
		return this.name;
	}

	public String getName2() {
		return this.name2;
	}

	public String getName3() {
		return this.name3;
	}

	public String getName4() {
		return this.name4;
	}

	public String getName5() {
		return this.name5;
	}

	public String getName6() {
		return this.name6;
	}

	public String getNationality() {
		return this.nationality;
	}

	public String getNativeplace() {
		return this.nativeplace;
	}

	public String getOfficephone() {
		return this.officephone;
	}

	public GeneralVO getOtherVO() {
		return null;
	}

	public String getPenelauth() {
		return this.penelauth;
	}

	public String getPermanreside() {
		return this.permanreside;
	}

	public Object getPhoto() {
		return this.photo;
	}

	public String getPk_degree() {
		return this.pk_degree;
	}

	public String getPk_group() {
		return this.pk_group;
	}

	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	public String getPk_org() {
		return this.pk_org;
	}

	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	public String getPKFieldName() {
		return "pk_psndoc";
	}

	public String getPolity() {
		return this.polity;
	}

	public String getPostalcode() {
		return this.postalcode;
	}

	public Object getPreviewphoto() {
		return this.previewphoto;
	}

	public String getProf() {
		return this.prof;
	}

	public PsnJobVO getPsnJobVO() {
		return this.psnJobVO;
	}

	public PsnOrgVO getPsnOrgVO() {
		return this.psnOrgVO;
	}

	public UFLiteralDate getRetiredate() {
		return this.retiredate;
	}

	public Integer getSex() {
		return this.sex;
	}

	public String getShortname() {
		return this.shortname;
	}

	public String getTableName() {
		return "bd_psndoc";
	}

	public String getTitletechpost() {
		return this.titletechpost;
	}

	public String getUsedname() {
		return this.usedname;
	}

	public SuperVO getVOByRule(String strFieldName) {
		if ((strFieldName.startsWith("hi_psnorg."))
				|| (strFieldName.startsWith("hi_psnorg_"))) {
			return this.psnOrgVO;
		}
		if ((strFieldName.startsWith("hi_psnjob."))
				|| (strFieldName.startsWith("hi_psnjob_"))) {
			return this.psnJobVO;
		}

		return this;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public void setAttributeValue(String strAttribute, Object objValue) {
		SuperVO superVO = getVOByRule(strAttribute);
		String strFieldName = getFieldNameByRule(strAttribute);

		if (superVO == this) {
			super.setAttributeValue(strFieldName, objValue);
		} else {
			superVO.setAttributeValue(strFieldName, objValue);
		}
	}

	public void setBirthdate(UFLiteralDate birthdate) {
		this.birthdate = birthdate;
	}

	public void setBloodtype(String bloodtype) {
		this.bloodtype = bloodtype;
	}

	public void setCensusaddr(String censusaddr) {
		this.censusaddr = censusaddr;
	}

	public void setCharacterrpr(String characterrpr) {
		this.characterrpr = characterrpr;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setDataoriginflag(Integer dataoriginflag) {
		this.dataoriginflag = dataoriginflag;
	}

	public void setDie_date(UFLiteralDate dieDate) {
		this.die_date = dieDate;
	}

	public void setDie_remark(String dieRemark) {
		this.die_remark = dieRemark;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEnablestate(Integer enablestate) {
		this.enablestate = enablestate;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setFileaddress(String fileaddress) {
		this.fileaddress = fileaddress;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public void setJoinpolitydate(UFLiteralDate joinpolitydate) {
		this.joinpolitydate = joinpolitydate;
	}

	public void setJoinworkdate(UFLiteralDate joinworkdate) {
		this.joinworkdate = joinworkdate;
	}

	public void setMarital(String marital) {
		this.marital = marital;
	}

	public void setMarriagedate(UFLiteralDate marriagedate) {
		this.marriagedate = marriagedate;
	}

	public void setMnecode(String mnecode) {
		this.mnecode = mnecode;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public void setName4(String name4) {
		this.name4 = name4;
	}

	public void setName5(String name5) {
		this.name5 = name5;
	}

	public void setName6(String name6) {
		this.name6 = name6;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public void setNativeplace(String nativeplace) {
		this.nativeplace = nativeplace;
	}

	public void setOfficephone(String officephone) {
		this.officephone = officephone;
	}

	public void setPenelauth(String penelauth) {
		this.penelauth = penelauth;
	}

	public void setPermanreside(String permanreside) {
		this.permanreside = permanreside;
	}

	public void setPhoto(Object photo) {
		this.photo = photo;
	}

	public void setPk_degree(String pk_degree) {
		this.pk_degree = pk_degree;
	}

	public void setPk_group(String pkGroup) {
		this.pk_group = pkGroup;
	}

	public void setPk_hrorg(String pkHrorg) {
		this.pk_hrorg = pkHrorg;
	}

	public void setPk_org(String pkOrg) {
		this.pk_org = pkOrg;
	}

	public void setPk_psndoc(String pkPsndoc) {
		this.pk_psndoc = pkPsndoc;
	}

	public void setPolity(String polity) {
		this.polity = polity;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public void setPreviewphoto(Object previewphoto) {
		this.previewphoto = previewphoto;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public void setPsnJobVO(PsnJobVO psnJobVO) {
		this.psnJobVO = psnJobVO;
	}

	public void setPsnOrgVO(PsnOrgVO psnOrgVO) {
		this.psnOrgVO = psnOrgVO;
	}

	public void setRetiredate(UFLiteralDate retiredate) {
		this.retiredate = retiredate;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public void setTitletechpost(String titletechpost) {
		this.titletechpost = titletechpost;
	}

	public void setUsedname(String usedname) {
		this.usedname = usedname;
	}

	public String getUnionPk() {
		if ((getPsnOrgVO() != null)
				&& (getPsnOrgVO().getPsntype().intValue() == 1)) {
			return getPk_psndoc() + getPsnOrgVO().getPk_psnorg();
		}
		return getPk_psndoc() + getPsnOrgVO().getPk_psnorg()
				+ getPsnJobVO().getPk_psnjob();
	}

	public void setSecret_email(String secret_email) {
		this.secret_email = secret_email;
	}

	public String getSecret_email() {
		return this.secret_email;
	}

	public void setIshiskeypsn(UFBoolean ishiskeypsn) {
		this.ishiskeypsn = ishiskeypsn;
	}

	public UFBoolean getIshiskeypsn() {
		return this.ishiskeypsn;
	}

	public void setIsshopassist(UFBoolean isshopassist) {
		this.isshopassist = isshopassist;
	}

	public UFBoolean getIsshopassist() {
		return this.isshopassist;
	}

	public UFBoolean getIsuapmanage() {
		return this.isuapmanage;
	}

	public void setIsuapmanage(UFBoolean isuapmanage) {
		this.isuapmanage = isuapmanage;
	}

	public UFDouble getG_1() {
		return g_1;
	}

	public void setG_1(UFDouble g_1) {
		this.g_1 = g_1;
	}

	public UFDouble getG_2() {
		return g_2;
	}

	public void setG_2(UFDouble g_2) {
		this.g_2 = g_2;
	}

	public UFDouble getG_3() {
		return g_3;
	}

	public void setG_3(UFDouble g_3) {
		this.g_3 = g_3;
	}

	public UFDouble getG_4() {
		return g_4;
	}

	public void setG_4(UFDouble g_4) {
		this.g_4 = g_4;
	}

	public UFDouble getG_5() {
		return g_5;
	}

	public void setG_5(UFDouble g_5) {
		this.g_5 = g_5;
	}

	public UFDouble getG_6() {
		return g_6;
	}

	public void setG_6(UFDouble g_6) {
		this.g_6 = g_6;
	}

	public UFDouble getG_7() {
		return g_7;
	}

	public void setG_7(UFDouble g_7) {
		this.g_7 = g_7;
	}

	public UFDouble getTe_1() {
		return te_1;
	}

	public void setTe_1(UFDouble te_1) {
		this.te_1 = te_1;
	}

	public UFDouble getTe_2() {
		return te_2;
	}

	public void setTe_2(UFDouble te_2) {
		this.te_2 = te_2;
	}

	public UFDouble getTe_3() {
		return te_3;
	}

	public void setTe_3(UFDouble te_3) {
		this.te_3 = te_3;
	}

	public UFDouble getTe_4() {
		return te_4;
	}

	public void setTe_4(UFDouble te_4) {
		this.te_4 = te_4;
	}

	public UFDouble getTe_5() {
		return te_5;
	}

	public void setTe_5(UFDouble te_5) {
		this.te_5 = te_5;
	}

	public UFDouble getTe_6() {
		return te_6;
	}

	public void setTe_6(UFDouble te_6) {
		this.te_6 = te_6;
	}

	public UFDouble getTe_7() {
		return te_7;
	}

	public void setTe_7(UFDouble te_7) {
		this.te_7 = te_7;
	}

	public UFDouble getTe_8() {
		return te_8;
	}

	public void setTe_8(UFDouble te_8) {
		this.te_8 = te_8;
	}

	public UFDouble getTr_1() {
		return tr_1;
	}

	public void setTr_1(UFDouble tr_1) {
		this.tr_1 = tr_1;
	}

	public UFDouble getTr_3() {
		return tr_3;
	}

	public void setTr_3(UFDouble tr_3) {
		this.tr_3 = tr_3;
	}

	public UFDouble getTr_2() {
		return tr_2;
	}

	public void setTr_2(UFDouble tr_2) {
		this.tr_2 = tr_2;
	}

	public UFDouble getTr_4() {
		return tr_4;
	}

	public void setTr_4(UFDouble tr_4) {
		this.tr_4 = tr_4;
	}

	public UFDouble getTr_5() {
		return tr_5;
	}

	public void setTr_5(UFDouble tr_5) {
		this.tr_5 = tr_5;
	}

	public UFDouble getTr_6() {
		return tr_6;
	}

	public void setTr_6(UFDouble tr_6) {
		this.tr_6 = tr_6;
	}

	public UFDouble getTr_7() {
		return tr_7;
	}

	public void setTr_7(UFDouble tr_7) {
		this.tr_7 = tr_7;
	}

	public UFDouble getTr_8() {
		return tr_8;
	}

	public void setTr_8(UFDouble tr_8) {
		this.tr_8 = tr_8;
	}

	public UFDouble getTr_9() {
		return tr_9;
	}

	public void setTr_9(UFDouble tr_9) {
		this.tr_9 = tr_9;
	}

	public UFDouble getTr_10() {
		return tr_10;
	}

	public void setTr_10(UFDouble tr_10) {
		this.tr_10 = tr_10;
	}

	public UFDouble getTr_11() {
		return tr_11;
	}

	public void setTr_11(UFDouble tr_11) {
		this.tr_11 = tr_11;
	}

	public UFDouble getTr_12() {
		return tr_12;
	}

	public void setTr_12(UFDouble tr_12) {
		this.tr_12 = tr_12;
	}

	public UFDouble getTr_13() {
		return tr_13;
	}

	public void setTr_13(UFDouble tr_13) {
		this.tr_13 = tr_13;
	}

	public UFDouble getTr_14() {
		return tr_14;
	}

	public void setTr_14(UFDouble tr_14) {
		this.tr_14 = tr_14;
	}

	public UFDouble getTr_15() {
		return tr_15;
	}

	public void setTr_15(UFDouble tr_15) {
		this.tr_15 = tr_15;
	}

	public UFDouble getTr_16() {
		return tr_16;
	}

	public void setTr_16(UFDouble tr_16) {
		this.tr_16 = tr_16;
	}

	public UFDouble getTr_17() {
		return tr_17;
	}

	public void setTr_17(UFDouble tr_17) {
		this.tr_17 = tr_17;
	}
}