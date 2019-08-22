package nc.impl.hrrp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.constant.hrrp.report.Constant;
import nc.constant.hrrp.report.Constant.FORMAT;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.hrrp.service.IBaseService;
import nc.itf.hrrp.service.IPdfOprator;
import nc.vo.hrrp.pdfrep.HrrpFormat;
import nc.vo.hrrp.pdfrep.ReadonlyEnum;
import nc.vo.hrrp.pdfrep.TotalRangeEnum;
import nc.vo.hrrp.report.AggReport;
import nc.vo.hrrp.report.Field;
import nc.vo.hrrp.report.Report;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import org.apache.commons.lang.StringUtils;
import org.granite.lang.util.Strings;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * <p>
 * Title: PdfOprator
 * </P>
 * <p>
 * Description:
 * </p>
 * 
 * @author
 * @version 1.0
 * @since 2014-10-11
 */
public class PdfOprator implements IPdfOprator {

	private Map<String, Field> frMap;
	private AggReport aggvo;
	private List<Map> lstData = null;
	private int pageTotal = 0;
	private String strWhr = "";

	/**
	 * @return the strWhr
	 */
	public String getStrWhr() {
		if (strWhr == null)
			strWhr = "";
		else if (strWhr.trim().length() < 1)
			strWhr = "";
		else if (!strWhr.toLowerCase().startsWith(" where"))
			strWhr = " where " + strWhr;// .replace("'", "''");
		return strWhr;
	}

	/**
	 * @param strWhr
	 *            the strWhr to set
	 */
	public void setStrWhr(String strWhr) {
		if (strWhr == null)
			strWhr = "";
		else if (!strWhr.toLowerCase().startsWith(" where"))
			strWhr = " where " + strWhr;// .replace("'", "''");
		;
		this.strWhr = strWhr;
	}

	/**
	 * @return the frMap
	 */
	public Map<String, Field> getFrMap() {
		return frMap;
	}

	/**
	 * @param frMap
	 *            the frMap to set
	 */
	public void setFrMap(Map<String, Field> frMap) {
		this.frMap = frMap;
	}

	/**
	 * @return the aggvo
	 */
	public AbstractBill getAggvo() {
		return aggvo;
	}

	/**
	 * @param aggvo
	 *            the aggvo to set
	 */
	public void setAggvo(AggReport aggvo) {
		this.aggvo = aggvo;
	}

	public PdfOprator(AggReport aggvo) {
		this.aggvo = aggvo;
	}

	public PdfOprator() {
	}

	/**
	 * <p>
	 * pdf build
	 * </p>
	 * 
	 * @author
	 * @throws IOException
	 * @throws DocumentException
	 * @throws Exception
	 */
	public byte[] pdfBuild() throws BusinessException, IOException,
			DocumentException {

		if (aggvo == null)
			throw new BusinessException("Config Error");// 
		String tarPath = aggvo.getParentVO().getOutput();
		String srcPath = aggvo.getParentVO().getInput();
		try {
			FileInputStream fi = new FileInputStream(new File(srcPath));
			fi.close();
		} catch (FileNotFoundException ex) {
			throw new BusinessException("Read Input File Error:" + srcPath);// 
		}
		// try {
		// FileOutputStream fo = new FileOutputStream(new File(tarPath));
		// fo.close();
		// } catch (FileNotFoundException ex) {
		// throw new BusinessException("Create Temp File Error:" + tarPath +
		// ".pdf");// 
		// }
		Report rp = aggvo.getParentVO();
		Field[] fields = (Field[]) aggvo.getChildren(Field.class);
		BaseFont bfThailand = null;
		try {
			// bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
			// BaseFont.NOT_EMBEDDED);
			bfThailand = BaseFont.createFont(
					getClass().getResource(Constant.REPCONSTANT.FONT_NAME)
							.toString(), BaseFont.IDENTITY_H,
					BaseFont.NOT_EMBEDDED);
		} catch (Exception ex) {
			try {
				bfThailand = BaseFont.createFont(
						Constant.REPCONSTANT.FONT_SYS_PATH,
						BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			} catch (Exception ex1) {
			}
		}
		frMap = new HashMap<String, Field>();
		for (Field field : fields) {
			frMap.put(field.getName(), field);
		}
		List<String> lstTemp = new ArrayList<String>();// 
		if (rp.getProcname() != null && rp.getProcname().length() > 0)
			lstData = getDatas(rp.getProcname() + " '" + getStrWhr() + "'");//
		// lstData=getDatasByProc(rp.getProcname(),strWhr);
		else
			lstData = getDatas(rp.getSql() + " " + getStrWhr());// 
		if (lstData == null) {
			throw new BusinessException("Data Has ERROR:" + rp.getSql() + " "
					+ getStrWhr());//
		}
		// if("1".equals("1"))
		// throw new BusinessException("debug");
		if (lstData.size() < 1)
			throw new BusinessException("Data Is Null");
		rp.setPagenum(rp.getPagenum() == null ? 1 : rp.getPagenum());
		pageTotal = (lstData.size() + rp.getPagenum() - 1) / rp.getPagenum();// 
		int count = pageTotal;
		// 瀛椾綋澶у皬 fontsize add by weiningc start
		float textSize = 0.0f;
		// end
		if (Constant.BOOL.NO.equals(rp.getIspage()))// 
			count = 1;
		for (int i = 0; i < count; i++) {// 
			FileInputStream in = new FileInputStream(new File(srcPath));
			String strPath = tarPath + "_" + i + ".pdf";
			FileOutputStream outTemp = new FileOutputStream(new File(strPath));
			lstTemp.add(strPath);
			PdfReader pdfr = new PdfReader(in);
			PdfStamper pdfs = new PdfStamper(pdfr, outTemp);
			AcroFields form = pdfs.getAcroFields();
			Map<String, Item> map = form.getFields();
			List<String> keys = new ArrayList<String>();
			//
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				keys.add(it.next());
			}
			for (String key : keys) {
				if (frMap.get(key) != null) {
//					textSize = 8;
					try {
						textSize = Float.parseFloat(frMap.get(key).getFontsize().toString());// PdfUtils.getDefaultTextSize(item);
					} catch (Exception exp) {
						ExceptionUtils.wrappBusinessException(exp.getMessage());
					}
					if (bfThailand != null)
						form.setFieldProperty(key, "textfont", bfThailand, null);
					form.setFieldProperty(key, "textsize", textSize, null);
					// TODO

					// form.getFieldItem(key).getMerged(0).getAsNumber(PdfName.Q).intValue();
					// form.getFieldItem(key).getMerged(0).getAsNumber(PdfName.Q)
					// form.setFieldProperty(field, name, value, inst)
					// -------------------------------------------------------
					//
					int prepage = this.getPrepageno(i);

					String val = fillData(frMap.get(key), i, prepage,
							rp.getPagenum());// FILLDATA

//					if (Constant.BOOL.TEXT.equals(frMap.get(key).getReadonly())) {
//						List<FieldPosition> pos = form.getFieldPositions(key);
//						FieldPosition p = pos.get(0);
//						PdfContentByte content = pdfs.getOverContent(p.page);
//						Item item = form.getFieldItem(key);
//						ColumnText ct = new ColumnText(content);
//						Rectangle rect = p.position;
//						Font thaiFont = new Font(bfThailand, textSize);
//						Chunk ck = new Chunk(val, thaiFont);
//						ct.setSimpleColumn(new Phrase(ck), rect.getLeft(),
//								rect.getBottom(), rect.getRight(),
//								rect.getTop(), 10,// 
//								PdfUtils.getItemAlignment(item));
//						ct.go();
//						form.removeField(key);
//					} else {
					form.setField(key, val);
					if (ReadonlyEnum.ONLYREAD.value().equals(frMap.get(key)
							.getReadonly()))// 
						form.setFieldProperty(key, "setfflags",
								PdfFormField.FF_READ_ONLY, null);
					form.renameField(key, key + "_" + i);
					form.removeField(key);
//					}
				}
				// form.renameField(key, key + "_" + i);
				// form.removeField(key);
			}
			pdfs.close();
			pdfr.close();
			outTemp.close();
			in.close();
		}
		// 闂佸憡鑹鹃悧鍡涙嚐閻戠F
		Document doc = new Document();
		FileOutputStream out = new FileOutputStream(new File(tarPath + ".pdf"));
		PdfCopy pdfc = new PdfCopy(doc, new FileOutputStream(tarPath + ".pdf"));
		int i = 1;
		doc.open();

		for (String str : lstTemp) {
			File fTemp = new File(str);
			FileInputStream inTemp = new FileInputStream(fTemp);
			PdfReader pdfr = new PdfReader(inTemp);
			// 婵＄偑鍊濆褔鏁撻敓锟�?
			rp.setCopypageno(rp.getCopypageno() == null ? 1 : rp
					.getCopypageno());
			if (i == 1) {
				for (int j = 1, cnt = rp.getCopypageno(); j < cnt; j++) {
					pdfc.addPage(pdfc.getImportedPage(pdfr, j));
				}
			}
			// 闂佸憡甯掑Λ婵嬪Υ婢舵劕绀冮柛娑卞弾閸燂拷
			PdfImportedPage ppage = pdfc.getImportedPage(pdfr,
					rp.getCopypageno());
			pdfc.addPage(ppage);
			// 婵＄偑鍊濆褔鏁撻敓锟�?
			if (i == lstTemp.size()) {
				for (int j = rp.getCopypageno() + 1, cnt = pdfr
						.getNumberOfPages() + 1; j < cnt; j++) {
					pdfc.addPage(pdfc.getImportedPage(pdfr, j));
				}
			}
			pdfr.close();
			inTemp.close();
			i++;
			fTemp.delete();
		}
		// in.close();
		out.close();
		doc.close();
		File fret = new File(tarPath + ".pdf");
		FileInputStream is = new FileInputStream(fret);
		byte[] bts = new byte[is.available()];
		is.read(bts);
		is.close();
		fret.delete();
		return bts;
	}

	/**
	 * 鑾峰彇鍓嶄竴椤�
	 * 
	 * @param i
	 * @return
	 */
	private int getPrepageno(int i) {
		if (i == 0) {
			return -1;
		}
		return i - 1;
	}

	/**
	 * 榛樿瀛椾綋澶у皬 font size
	 * 
	 * @return
	 */
	private float qryDefaultFontSize() {
		StringBuilder sb = new StringBuilder();
		sb.append("select def.memo from bd_defdoc def, bd_defdoclist defl");
		sb.append(" where def.pk_defdoclist = defl.pk_defdoclist and defl.code = 'pdfformat'");
		sb.append(" and def.code = '14' and def.enablestate = 2");
		DataAccessUtils util = new DataAccessUtils();
		IRowSet rowset = util.query(sb.toString());
		String frontsize = null;
		while (rowset.next()) {
			frontsize = rowset.getString(0);
		}
		if (frontsize != null) {
			return Float.valueOf(frontsize);
		}
		return 8L;// 榛樿瀛椾綋澶у皬涓�8
	}

	/**
	 * <p>
	 * 闂佸搫鍊介～澶屾兜閸洖瑙︾�广儱娉﹂悙鐑樻櫖婵繄鍨妉lData
	 * </p>
	 * 
	 * @param field
	 * @param nowPage
	 *            褰撳墠椤�
	 * @param prepage
	 *            鍓嶄竴椤�
	 * @param pageCnt
	 *            鎬婚〉鏁�
	 * @return
	 * @author
	 */
	private String fillData(Field field, int nowPage, int prepage, int pageCnt) {
		if (lstData == null || lstData.size() < 1) {
			return "";
		}
		// 鍘熷��
		if (HrrpFormat.SRCVALUE.value().equals(field.getFormat())) {
			return field.getDbfield();
		}
//		if (FORMAT.PAGE_NOW.equals(field.getFormat()))// 褰撳墠椤�
//			return (nowPage + 1) + "";
//		if (FORMAT.TOTAL.equals(field.getFormat()))// 鎬婚〉鏁�
//			return pageTotal + "";
		field.setDbrow(field.getDbrow() == null ? 1 : field.getDbrow());
		
		// -1琛ㄧず褰撳墠椤电疮璁�
		if (TotalRangeEnum.CURRENTPAGE.value().equals(field.getTotaltype())) {
			double sum = 0;
			for (int i = nowPage * pageCnt; i < Math.min(lstData.size(),
					(nowPage + 1) * pageCnt); i++) {
				try {
					sum += Double.parseDouble(lstData.get(i)
							.get(field.getDbfield()).toString());
				} catch (Exception e) {
					return "0";
				}
			}
			String currentvalue = getNumberFormat(field, sum + "");
			//绌烘牸鍒嗗壊
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		// -2琛ㄧず鍓嶄竴椤电疮璁″悎璁�
		if (TotalRangeEnum.BEFOREPAGE.value().equals(field.getTotaltype())) {
			if (prepage == -1) {// 琛ㄧず绗竴椤�
				return "0";
			}
			double sum = 0;
			for (int i = 0; i < Math.min(lstData.size(), (prepage + 1)
					* pageCnt); i++) {
				try {
					sum += Double.parseDouble(lstData.get(i)
							.get(field.getDbfield()).toString());
				} catch (Exception e) {
					return "0";
				}
			}
			String currentvalue = getNumberFormat(field, sum + "");
			//绌烘牸鍒嗗壊
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		// -3琛ㄧず鎬荤疮璁�
		if (TotalRangeEnum.TOTALPAGE.value().equals(field.getTotaltype())) {
			double currsum = 0;
			for (int i = 0; i < Math.min(lstData.size(), (nowPage + 1)
					* pageCnt); i++) {
				try {
					currsum += Double.parseDouble(lstData.get(i)
							.get(field.getDbfield()).toString());
				} catch (Exception e) {
					return "0";
				}
			}
			String currentvalue = getNumberFormat(field, currsum + "");
			//绌烘牸鍒嗗壊
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		//-4总累计合计
		if (TotalRangeEnum.TOTALNUMBERPAGE.value().equals(field.getTotaltype())) {
			double totalsum = 0;
			for (int i = 0; i < Math.min(lstData.size(), (pageTotal + 1)
					* pageCnt); i++) {
				try {
					totalsum += Double.parseDouble(lstData.get(i)
							.get(field.getDbfield()).toString());
				} catch (Exception e) {
					return "0";
				}
			}
			String currentvalue = getNumberFormat(field, totalsum + "");
			//绌烘牸鍒嗗壊
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		
		int row = 0;
		row = field.getDbrow() + nowPage * pageCnt - 1;
		row = Math.max(0, row);
		Object val;
		if (row > lstData.size() - 1)
			return "";
		if (field.getDbfield() == null) {
			//褰撳墠椤�
			if(HrrpFormat.SPLIT_CURRENTPAGE.value().equals(field.getFormat())) {
				return String.valueOf(nowPage+1);
			}
			//鎬婚〉鏁�
			if(HrrpFormat.SPLIT_TOTALPAGE.value().equals(field.getFormat())) {
				return String.valueOf(pageTotal);
			}
			return "";
		}
		val = lstData.get(row).get(field.getDbfield().toLowerCase());
		if (val == null)
			return "";
		String valStr = val.toString();
		if (valStr.trim().length() < 1)
			return "";
		if (field.getFormat() == null || "~".equals(field.getFormat()))
			return valStr;
		boolean issplitblank = StringUtils.isBlank(field.getSplfmt()) ? true : false;
		if(issplitblank) {
			return valStr;
		}
		//鏍煎紡鍒嗗壊
		if (HrrpFormat.SPLITSEGMENT.value().equals(field.getFormat()))
		{
			return this.getSplitSegment(valStr, field);
			
		}
		//绌烘牸鍒嗗壊
		else if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
			return this.getSplitSpace(valStr, field);
		}
		//褰撳墠椤�
		else if(HrrpFormat.SPLIT_CURRENTPAGE.value().equals(field.getFormat())) {
			return String.valueOf(nowPage);
		}
		//鎬婚〉鏁�
		else if(HrrpFormat.SPLIT_TOTALPAGE.value().equals(field.getFormat())) {
			return String.valueOf(pageTotal);
		}
		return getNumberFormat(field, valStr);

	}
	

	private String getSplitSpace(String valStr, Field field) {
		if (valStr == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		// 绌烘牸鍒嗗壊
		char[] charArraySpace = valStr.toCharArray();
		
		for (char arr : charArraySpace) {
			sb.append(arr + this.getSpaces(field.getSplfmt()));
		}
		// String[] vals = valStr.split(" ");
		// int cut = field.getCutnum();
		// if (cut == -1)
		// return vals[Math.max(vals.length - 1, 0)];
		return sb.toString();
	}

	private String getSplitSegment(String valStr, Field field) {
		if (valStr == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		// 绌烘牸鍒嗗壊
		char[] charArraySpace = valStr.toCharArray();
		for (char arr : charArraySpace) {
			sb.append(arr + field.getSplfmt());
		}
		// String[] vals = valStr.split(" ");
		// int cut = field.getCutnum();
		// if (cut == -1)
		// return vals[Math.max(vals.length - 1, 0)];
		return sb.toString();
	}

	/**
	 * 绌烘牸闂磋窛
	 * @param spaces
	 * @return
	 */
	public static String getSpaces(String spaces) {
		String text = "";
		if(StringUtils.isBlank(spaces)) {
			return text;
		}
		int spacenum = 0;
		try{
			spacenum = Integer.parseInt(spaces);
		} catch (NumberFormatException e) {
			ExceptionUtils.wrappBusinessException("褰撲负绌烘牸鍒嗗壊鐨勬椂鍊欙紝鍒嗗壊鏍煎紡蹇呴』鏄暣鏁�!");
		}
		for (int x = 0; x < spacenum; x++) {
			text += " ";
		}
		return text;
	}

	/**
	 * <p>
	 * number format
	 * </p>
	 * 
	 * @param field
	 * @param val
	 * @return
	 * @author
	 */
	private String getNumberFormat(Field field, String val) {
		double dbval = 0;
		try {
			dbval = Double.parseDouble(val);
		} catch (Exception e) {
			return val;
		}
		int xs = 2;
		field.setDicnum(field.getDicnum() == null ? -1 : field.getDicnum());
		if (field.getDicnum() > -1)
			xs = field.getDicnum();
		StringBuffer sbDic = new StringBuffer("#0.");
		while (xs > 0) {
			sbDic.append("0");
			xs--;
		}
		String fmt = sbDic.toString();
		if (fmt.endsWith("."))
			fmt = fmt.substring(0, fmt.length() - 1);
		DecimalFormat df = new DecimalFormat(sbDic.toString());
		if (field.getDicnum() != null && field.getDicnum() == 2) {// 涓や綅灏忔暟
			return getNumFormat(df.format(dbval));
		} else if (HrrpFormat.SPLIT_INTEGER.value().equals(field.getFormat())) {// 鏁存暟
			return getNumFormat(new DecimalFormat("#0").format(dbval)) + "";
		} /*else if (FORMAT.DECIMAL.equals(field.getFormat())) {// 灏忔暟
			return df.format(dbval).split("\\.")[1];
		}*/ else if (FORMAT.TAIWEN.equals(field.getFormat())) {// 娉版枃
			return getTaiwen(df.format(dbval));// getTaiwen("12345678.91")
		} else
			return dbval + "";

	}

	/**
	 * <p>
	 * 闁哄倽顫夌涵鍫曞触瀹ュ泦鐐烘晬濮濐摣tNumFormat
	 * </p>
	 * <p>
	 * 闁哄倽顫夌涵鍫曞箵韫囨艾鐗氶柨娑欑啲閹凤拷?闁告瑩鏀遍悧绋款嚕韫囨挸鐎婚柛鎾冲级閺嗙喖鏁撻敓锟�?/p>
	 * 
	 * @param val
	 * @return
	 */
	private String getNumFormat(String val) {
		String[] nums = val.split("\\.");
		char[] chs = nums[0].toCharArray();
		String rst = "";
		int k = 0;
		for (int i = chs.length - 1; i >= 0; i--) {
			rst = chs[i] + rst;
			if ((rst.length() - k) % 3 == 0 && i != 0) {
				rst = "," + rst;
				k++;
			}
		}
		if (nums.length > 1)
			rst = rst + "." + nums[1];
		return rst;
	}

	/**
	 * <p>
	 * Taiwen
	 * </p>
	 * 
	 * @param val
	 * @return
	 * @author
	 */
	private String getTaiwen(String val) {
		// TODO
		String[] vals = val.split("\\.");
		int len = vals[0].length();
		boolean isInt = true;
		if (vals.length > 1) {
			if (!"00".equals(val.split("\\.")[1]))
				isInt = false;
		}
		StringBuffer strRet = new StringBuffer();
		char[] chs = vals[0].toCharArray();
		int i = len;
		char temp = 'a';
		for (char ch : chs) {
			if (vals[0] == "0")
				strRet.append(getTC(ch, i, len, temp == '0', false));
			else
				strRet.append(getTC(ch, i, len, temp == '0', true));
			temp = ch;
			i--;
		}
		if (!isInt) {
			int lend = vals[1].length();
			char[] chsd = vals[1].toCharArray();
			int j = lend;
			char tempd = 'a';
			for (char ch : chsd) {
				strRet.append(getTC(ch, j, lend, tempd == '0', false));
				tempd = ch;
				j--;
			}
		}
		if (isInt)
			strRet.append("娉版枃鏁存暟鏍煎紡");
		else
			strRet.append("娉版枃灏忔暟鏍煎紡");
		return strRet.toString();
	}

	private String getTC(char c, int wei, int len, boolean isPre0,
			boolean large0) {
		String strRet = "";
		wei = wei % 10;
		if (len > 1 && wei == 1)
			strRet = getTC(c, 1, false);
		else if (len > 1 && wei == 2)
			strRet = getTC(c, 2, false);
		else if (wei == 2 || wei == 8)
			strRet = getTC(c, 2, false);
		else
			strRet = getTC(c);
		if (c == '0' && wei == 7 && !isPre0)
			strRet += getBit(wei, large0);
		else if (c == '0' && wei == 1)
			strRet += getBit(wei, large0);
		else if (c == '0')
			strRet += "";
		else if (wei == 8)
			strRet += getBit(2, true);
		else
			strRet += getBit(wei, large0);
		return strRet;
	}

	private String getBit(int len, boolean large0) {
		String strRet = "";
		switch (len) {
		case 1:
			if (large0)
				strRet = "閸犳梹鐩�鐟曞棗鏋栭敓锟�";
			else
				strRet = "";
			break;
		case 2:
			strRet = "閸犳棏锟藉灝顦查崰鏃撴嫹";
			break;
		case 3:
			strRet = "閸犳棑绲哥粻鐔锋灃椤撴瑦涓�";
			break;
		case 4:
			strRet = "閸犳梻鍋嬮崜顖氭灃閿燂拷";
			break;
		case 5:
			strRet = "閸犳棏鍊藉ù顔兼灃缁亞鐤勯崰鏃撴嫹";
			break;
		case 6:
			strRet = "閸犳瑤鐤嗛柌婊冩灃閿燂拷";
			break;
		case 7:
			strRet = "閸犳柣鍎粻鐔锋灃娓氀呯懙";
			break;
		case 8:
			strRet = "閸犳棏锟藉灝顦查崰鏃�鐩�瀵鏋℃径鍐洬閸犳棑鎷�";
			break;
		case 9:
			strRet = "閸犳棑绲哥粻鐔锋灃椤撴瑦涓㈤崰鏂诲劕缁犵喎鏋栨笟褏鐟�";
			break;
		default:
			strRet = "";
			break;
		}
		return strRet;
	}

	private String getTC(char c) {
		return getTC(c, 3, false);
	}

	private String getTC(char c, int last, boolean zero) {
		String strRet = "";
		switch (c) {
		case '1':
			if (last == 1)
				strRet = "閸犳瑱鎷烽崰鏃戝幍缁犳稑鏋栭敓锟�";
			else if (last == 2)
				strRet = "";
			else
				strRet = "閸犳棏鍊界粭婵嗘灃婢舵氨鐤勯崰鏃撴嫹";
			break;
		case '2':
			if (last == 2)
				strRet = "閸犳柡鎳呴崒锟�";
			else
				strRet = "閸犳棏锟借儻鍘崰鏃撴嫹";
			break;
		case '3':
			strRet = "閸犳棏锟借儻顩崰鏃撴嫹";
			break;
		case '4':
			strRet = "閸犳棏锟藉灝鍊曢崰娆欐嫹";
			break;
		case '5':
			strRet = "閸犳棏鍊界粻鐔锋灃閿燂拷";
			break;
		case '6':
			strRet = "閸犳棏鍊界粩锟�";
			break;
		case '7':
			strRet = "閸犳瑱鎷烽崰鏂跨墍缁犳稑鏋栭敓锟�";
			break;
		case '8':
			strRet = "閸犳瑤鐤嗙粭鈥虫灃閿燂拷";
			break;
		case '9':
			strRet = "閸犳瑱鎷烽崰鏂剧枂缁犵喎鏋栭敓锟�";
			break;
		case '0':
			if (zero)
				strRet = "閸犳柣鍔嗛懙鐟版灃濞嗗﹥涓㈤崰娆欐嫹";
			else
				strRet = "";
			break;
		case '.':
			strRet = "";
			// strRet = "閸犳柨鐗�閻栬泛鏋栭敓锟�";
			break;
		default:
			strRet = c + "";
		}
		return strRet;
	}

	/**
	 * <p>
	 * 闂佸搫鍊介～澶屾兜閸洖瑙︾�广儱娉﹂悙鐑樻櫖婵繍鎽Datas
	 * </p>
	 * 
	 * @param sqlStr
	 * @return
	 * @author
	 */
	private List<Map> getDatas(String sqlStr) {
		IBaseService ibs = NCLocator.getInstance().lookup(IBaseService.class);
		try {
			return ibs.QueryBySql(sqlStr);
		} catch (DAOException e) {
			return null;
		}
	}

	private List<Map> getDatasByProc(String procName, String strWhr) {
		IBaseService ibs = NCLocator.getInstance().lookup(IBaseService.class);
		try {
			return ibs.QueryByProc(procName, getStrWhr());
		} catch (Exception e) {
			return null;
		}
	}
}
