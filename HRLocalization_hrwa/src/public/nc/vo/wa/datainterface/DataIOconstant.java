/**
 * 
 */
package nc.vo.wa.datainterface;

import java.util.HashMap;
import java.util.Map;

import nc.vo.hr.datainterface.ItemSeprtorEnum;

/**
 * @author xuanlt 利用模板生成的表单模板，不能设置列表中某一列的可见性。所以将所有的BillItem统一管理，
 *         不同的节点，根据自己的需要动态组合自己的billItems数组
 */
public final class DataIOconstant
{

	public static final String strReg = "[\\?\\+\\*\\(\\)\\.\\|\\^\\\\\\[\\]\\{\\}\\*\\&\\-\\=\\!\\@\\#\\$\\%\\~\\`\\:\\;\\,\\<\\> ]";

	/**
	 * 接口所用到的界面名字
	 * 
	 */
	// public static final String INITPANEL = "initPanel";
	// public static final String BASEINFPANEL = "BaseInfPanel";
	// public static final String PARAINFPANEL = "ParaInfPanel";
	// public static final String IOITEMSPANEL = "IOItemsPanel";
	// public static final String DATAIOPANEL = "dataIOPanel";
	/**
	 * 接口的基本信息所对应的列
	 */
	public static final String HR_DATAINTFACE_B = "hr_dataintface_b";
	public static final String HR_IFSETTOP = "hr_ifsettop";

	public static final String PK_DATAIO_INTFACE = "pk_dataio_intface";
	public static final String VIFNAME = "vifname";
	public static final String IIFTYPE = "iiftype";
	public static final String VFILENAME = "vfilename";
	public static final String IFILETYPE = "ifiletype";
	public static final String IIFDOT = "iifdot";
	public static final String IIFKILOBIT = "iifkilobit";
	public static final String IIFSEPARATOR = "iifseparator";
	public static final String ISEPARATOR = "iseparator";
	public static final String IIFTOP = "iiftop";
	public static final String PK_CORP = "pk_corp";
	public static final String VMEMO = "vmemo";
	public static final String OPERATORID = "operatorid";
	public static final String VCOL = "vcol";
	public static final String VTABLE = "vtable";
	//
	public static final String IIFCARET = "iifcaret";
	public static final String CLASSID = "classid";
	public static final String IIFLNSRA = "IiflnSra";
	public static final String LNLENGTH = "Lnlength";
	public static final String LNCARET = "Lncaret";
	public static final String PK1 = "pk1";
	public static final String PK2 = "pk2";
	public static final String EXT1 = "ext1";
	public static final String EXT2 = "ext2";
	public static final String DATE1 = "date1";
	public static final String DATE2 = "date2";
	/** 是否默认接口 时间管理专用 */
	public static final String IDEFAULT = "IDefault";

	public static final String BANK_FIX_COL_MONEY = "bank_fix_col_money";

	/**
	 * 导入导出所对应的列
	 */

	public static final String PK_DATAIO_B = "pk_dataio_b";
	public static final String IFID = "ifid";
	public static final String VFIELDNAME = "vfieldname";
	public static final String IFIELDTYPE = "ifieldtype";
	public static final String ISOURCETYPE = "isourcetype";
	public static final String VCONTENT = "vcontent";
	public static final String IIFORDER = "iiforder";
	public static final String VINCLUDEBEFORE = "vincludebefore";
	public static final String VINCLUDEAFTER = "vincludeafter";
	public static final String VFORMULASTR = "vformulastr";
	public static final String IFLDDECIMAL = "iflddecimal";
	public static final String IFLDWIDTH = "ifldwidth";
	public static final String ISEQ = "iseq";
	public static final String ICARETPOS = "icaretpos";
	public static final String VCARET = "vcaret";
	public static final String VSEPARATOR = "vseparator";

	/**
	 * 行号设置所对应的列
	 * 
	 */
	public static final String DATEFORMAT = "dateformat";
	public static final String ICARETPOSTOP = "icaretpos";
	public static final String IFLDWIDTHTOP = "ifldwidth";
	public static final String IFLDDECIMALTOP = "iflddecimal";
	public static final String IIFSUMTOP = "iifsum";

	// public static final int TXTFILE = 1;
	// public static final int XLSFILE = 2;

	// /*数据来源——单一型*/
	// public static final int SINGLETYPE = 0;
	// /*数据来源——公式型*/
	// public static final int FORMULARTYPE = 1;

	public static final String PREFIX_SINGLESOURCETYPE = "colum";

	public static final Map<Integer, String> ITEMSEPERATOR = new HashMap<Integer, String>();
	static
	{
		// ITEMSEPERATOR.put((Integer) ItemSeprtorEnum.BLANK.value(), " ");
		ITEMSEPERATOR.put((Integer) ItemSeprtorEnum.COMMA.value(), ",");
		ITEMSEPERATOR.put((Integer) ItemSeprtorEnum.SEM.value(), ";");
		ITEMSEPERATOR.put((Integer) ItemSeprtorEnum.ERECT.value(), "|");
		ITEMSEPERATOR.put((Integer) ItemSeprtorEnum.NULL.value(), "");
	}
	public static final String BLANK = " ";

	// public static final int D_DEFAULT = 0; /*YYYY-MM-DD*/
	// public static final int D_FORMAT1 = 1; /*YYYYMMDD*/
	// public static final int D_FORMAT2 = 2; /*YYYY/MM/DD*/
	// public static final int D_FORMAT3 = 3; /*DD/MM/YYYY*/
	// public static final int D_FORMAT4 = 4; /*MM/DD/YYYY*/

	// public static final Map<Integer,String> DATEFORMATS = new
	// HashMap<Integer, String>();
	// static{
	// DATEFORMATS.put((Integer)DateFormatEnum.Y_M_D.value(), "YYYY-MM-DD");
	// DATEFORMATS.put((Integer)DateFormatEnum.YMD.value(), "YYYYMMDD");
	// DATEFORMATS.put((Integer)DateFormatEnum.Y_M_D1.value(), "YYYY/MM/DD");
	// DATEFORMATS.put((Integer)DateFormatEnum.DMY.value(), "DD/MM/YYYY");
	// DATEFORMATS.put((Integer)DateFormatEnum.MDY.value(), "MM/DD/YYYY");
	// }

	public static final String BD_PSNDOC = "bd_psndoc";
	public static final String PSNCODE = "psncode";

	public static final String BD_PSNBASDOC = "bd_psnbasdoc";
	public static final String ID = "id";

	// public static final ConstEnumFactory<String> RELATEDITEM_FCTRY = new
	// ConstEnumFactory(new String[]{"人员编码",
	// "身份证号码"
	// },new String[]{BD_PSNDOC+PSNCODE,BD_PSNDOC+ID} );

	/* 数据类型 */
	// public static final int STRINGTYPE = 1;
	// public static final int DECIMALTYPE = 0;
	// public static final int BOOLEANTYPE = 3;
	// public static final int DATETYPE = 2;
	// 接口类型
	// public static final int WA_DATAIO = 0;////薪资数据接口
	// public static final int WA_BANK = 1; //银行报盘
	// public static final int BM_DATAIO = 2; //福利数据接口
	// public static final int BM_BANK = 3;//福利报送数据
	// public static final int WA_BANK_CORP = 4;//多公司银行报盘
	// public static final int KAOQIN = 6; ///其他 考勤数据接口
	/* 补位位置 */
	// public static final int NOCARET = 0;
	// public static final int BEFORECARET = 1;
	// public static final int AFTERCARET = 2;
	// public static final IConstEnum[] CARETPOS =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(NOCARET,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000102")/*@res
	// "不补"*/),
	// new DefaultConstEnum(BEFORECARET,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000103")/*@res
	// "补前"*/),
	// new DefaultConstEnum(AFTERCARET,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000104")/*@res
	// "补后"*/)
	// };
	/* 数据类型 */

	// public static final int TIMETYPE = 4;
	// public static final IConstEnum[] FIELDTYPE =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(STRINGTYPE,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000098")/*@res 字符型
	// */),
	// new DefaultConstEnum(DECIMALTYPE,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000099")/*@res
	// 数字型*/),
	// new DefaultConstEnum(BOOLEANTYPE,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000100")/*@res 布尔型
	// */),
	// new DefaultConstEnum(DATETYPE,
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000101")/*@res
	// 日期型*/)
	// //,
	// // new DefaultConstEnum(TIMETYPE,
	// NCLangRes4VoTransl.getNCLangRes().getStrByID("public",
	// "UPPpublic-000647")/*"时间型"*/),
	// };
	// public static final Map<String, Integer> FIELDTYPEMMAP = new HashMap<
	// String,Integer>();
	// static{
	// FIELDTYPEMMAP.put(
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000098"),STRINGTYPE/*@res
	// 字符型 */);
	// FIELDTYPEMMAP.put(
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000099"),DECIMALTYPE/*@res
	// 数字型*/);
	// FIELDTYPEMMAP.put(
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000100"),BOOLEANTYPE/*@res
	// 布尔型 */);
	// FIELDTYPEMMAP.put(
	// ResHelper.getString("nc_hr_wa_pub4","UPPnc_hr_wa_pub4-000101")/*@res
	// 日期型*/,DATETYPE);
	// //FIELDTYPEMMAP.put(
	// NCLangRes4VoTransl.getNCLangRes().getStrByID("public",
	// "UPPpublic-000647")/*"时间型"*/,TIMETYPE);
	//			
	// };
	public static String UNITCODE = "0";// 单位代号
	public static String DATE = "1";// 日期
	public static String PSNCOUNT = "2";// 人数
	public static String ITEMSUM = "3";// 项目合计
	public static String FIRSTLINECONTENT = "4";// 首行内容

	public static final String DATAOUT = "dataout";
	public static final String DATAIN = "datain";
	public static final String UNKNOWN = "unknown";

	public static final String NODE_BANK = "60130bankitf";// 银行报盘
	public static final String NODE_DATAIO = "60130dataitf";// 数据接口

}
