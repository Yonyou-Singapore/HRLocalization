package nc.impl.hrrp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.granite.lang.util.Strings;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.constant.hrrp.report.Constant;
import nc.constant.hrrp.report.Constant.FORMAT;
import nc.itf.hrrp.service.IBaseService;
import nc.itf.hrrp.service.IPdfOprator;
import nc.vo.hrrp.report.AggReport;
import nc.vo.hrrp.report.Field;
import nc.vo.hrrp.report.Report;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
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
 * <p>Title: PdfOprator</P> <p>Description: </p>
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
			strWhr = " where " + strWhr;//.replace("'", "''");
		return strWhr;
	}

	/**
	 * @param strWhr the strWhr to set
	 */
	public void setStrWhr(String strWhr) {
		if (strWhr == null)
			strWhr = "";
		else if (!strWhr.toLowerCase().startsWith(" where"))
			strWhr = " where " + strWhr;//.replace("'", "''");
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
	 * @param frMap the frMap to set
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
	 * @param aggvo the aggvo to set
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
	 * <p>閺傝纭堕崥宥囆為敍姝卍fBuild</p>
	 * @author
	 * @throws Exception
	 */
	public byte[] pdfBuild() throws Exception {

		if (aggvo == null)
			throw new BusinessException("Config Error");// 闁板秶鐤嗗┃鎰弓鐠佸墽锟�?
		String tarPath = aggvo.getParentVO().getOutput();
		String srcPath = aggvo.getParentVO().getInput();
		try {
			FileInputStream fi = new FileInputStream(new File(srcPath));
			fi.close();
		} catch (FileNotFoundException ex) {
			throw new BusinessException("Read Input File Error:" + srcPath);// 濡剝婢橀弬鍥︽闁挎瑨锟�?
		}
//		try {
//			FileOutputStream fo = new FileOutputStream(new File(tarPath));
//			fo.close();
//		} catch (FileNotFoundException ex) {
//			throw new BusinessException("Create Temp File Error:" + tarPath + ".pdf");// 娑撳瓨妞傛潏鎾冲毉閺傚洣娆㈤柨娆掝嚖
//		}
		Report rp = aggvo.getParentVO();
		Field[] fields = (Field[]) aggvo.getChildren(Field.class);
		BaseFont bfThailand = null;
		try {
			// bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			bfThailand = BaseFont.createFont(getClass().getResource(Constant.REPCONSTANT.FONT_NAME).toString(),
					BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (Exception ex) {
			try {
				bfThailand = BaseFont.createFont(Constant.REPCONSTANT.FONT_SYS_PATH, BaseFont.IDENTITY_H,
						BaseFont.NOT_EMBEDDED);
			} catch (Exception ex1) {
			}
		}
		frMap = new HashMap<String, Field>();
		for (Field field : fields) {
			frMap.put(field.getName(), field);
		}
		List<String> lstTemp = new ArrayList<String>();// 娑撳瓨妞傞弬鍥︽閻╊喖锟�?
		if (rp.getProcname() != null && rp.getProcname().length() > 0)
			lstData = getDatas(rp.getProcname() + " '" + getStrWhr() + "'");// 閺佺増宓佸┃?
		// lstData=getDatasByProc(rp.getProcname(),strWhr);
		else
			lstData = getDatas(rp.getSql()+" "+getStrWhr());// 閺佺増宓佸┃?
		if (lstData == null) {
			throw new BusinessException("Data Has ERROR:"+rp.getSql()+" "+getStrWhr());// 閺佺増宓佹稉铏光敄
		}
//		if("1".equals("1"))
//		throw new BusinessException("debug");
		if (lstData.size() < 1)
			throw new BusinessException("Data Is Null");
		rp.setPagenum(rp.getPagenum() == null ? 1 : rp.getPagenum());
		pageTotal = (lstData.size() + rp.getPagenum() - 1) / rp.getPagenum();// 閹銆夐弫?
		int count = pageTotal;
		if (Constant.BOOL.NO.equals(rp.getIspage()))// 娑撳秴鍨庢い?
			count = 1;
		for (int i = 0; i < count; i++) {// 閺佺増宓佹い?
			FileInputStream in = new FileInputStream(new File(srcPath));
			String strPath = tarPath + "_" + i + ".pdf";
			FileOutputStream outTemp = new FileOutputStream(new File(strPath));
			lstTemp.add(strPath);
			PdfReader pdfr = new PdfReader(in);
			PdfStamper pdfs = new PdfStamper(pdfr, outTemp);
			AcroFields form = pdfs.getAcroFields();
			Map<String, Item> map = form.getFields();
			List<String> keys = new ArrayList<String>();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				keys.add(it.next());
			}
			for (String key : keys) {
				if (frMap.get(key) != null) {
					float textSize = 14;
					try {
						textSize = Float.parseFloat(frMap.get(key).getSplfmt());// PdfUtils.getDefaultTextSize(item);
					} catch (Exception exp) {
					}
					if (bfThailand != null)
						form.setFieldProperty(key, "textfont", bfThailand, null);
					form.setFieldProperty(key, "textsize", textSize, null);
					// TODO

					// form.getFieldItem(key).getMerged(0).getAsNumber(PdfName.Q).intValue();
					// form.getFieldItem(key).getMerged(0).getAsNumber(PdfName.Q)
					// form.setFieldProperty(field, name, value, inst)
					// -------------------------------------------------------
					String val = fillData(frMap.get(key), i, rp.getPagenum());// FILLDATA

					if (Constant.BOOL.TEXT.equals(frMap.get(key).getReadonly())) {
						List<FieldPosition> pos = form.getFieldPositions(key);
						FieldPosition p = pos.get(0);
						PdfContentByte content = pdfs.getOverContent(p.page);
						Item item = form.getFieldItem(key);
						ColumnText ct = new ColumnText(content);
						Rectangle rect = p.position;
						Font thaiFont = new Font(bfThailand, textSize);
						Chunk ck = new Chunk(val, thaiFont);
						ct.setSimpleColumn(new Phrase(ck), rect.getLeft(), rect.getBottom(), rect.getRight(),
								rect.getTop(), 10,// 闂磋窛
								PdfUtils.getItemAlignment(item));
						ct.go();
						form.removeField(key);
					} else {
						form.setField(key, val);
						if (Constant.BOOL.YES.equals(frMap.get(key).getReadonly()))// 鍙
							form.setFieldProperty(key, "setfflags", PdfFormField.FF_READ_ONLY, null);
						form.renameField(key, key + "_" + i);
						form.removeField(key);
					}
				}
				// form.renameField(key, key + "_" + i);
				// form.removeField(key);
			}
			pdfs.close();
			pdfr.close();
			outTemp.close();
			in.close();
		}
		// 閸氬牆鑻烶DF
		Document doc = new Document();
		FileOutputStream out = new FileOutputStream(new File(tarPath + ".pdf"));
		PdfCopy pdfc = new PdfCopy(doc, new FileOutputStream(tarPath + ".pdf"));
		int i = 1;
		doc.open();

		for (String str : lstTemp) {
			File fTemp = new File(str);
			FileInputStream inTemp = new FileInputStream(fTemp);
			PdfReader pdfr = new PdfReader(inTemp);
			// 妞ら潧锟�?
			rp.setCopypageno(rp.getCopypageno() == null ? 1 : rp.getCopypageno());
			if (i == 1) {
				for (int j = 1, cnt = rp.getCopypageno(); j < cnt; j++) {
					pdfc.addPage(pdfc.getImportedPage(pdfr, j));
				}
			}
			// 閸掑棝銆夐崘鍛啇
			PdfImportedPage ppage = pdfc.getImportedPage(pdfr, rp.getCopypageno());
			pdfc.addPage(ppage);
			// 妞ら潧锟�?
			if (i == lstTemp.size()) {
				for (int j = rp.getCopypageno() + 1, cnt = pdfr.getNumberOfPages() + 1; j < cnt; j++) {
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
	 * <p>閺傝纭堕崥宥囆為敍姝爄llData</p>
	 * @param field
	 * @param nowPage 锟�?锟�?锟�?
	 * @param pageCnt
	 * @return
	 * @author
	 */
	private String fillData(Field field, int nowPage, int pageCnt) {
		if (lstData == null || lstData.size() < 1)
			return "";
		if (FORMAT.NONE.equals(field.getFormat()))// 閸樼喎?
			return field.getDbfield();
		if (FORMAT.PAGE_NOW.equals(field.getFormat()))// 瑜版挸澧犳い鐢电垳
			return (nowPage + 1) + "";
		if (FORMAT.TOTAL.equals(field.getFormat()))// 閹銆夐弫?
			return pageTotal + "";
		field.setDbrow(field.getDbrow() == null ? 1 : field.getDbrow());
		if (field.getDbrow() == -1) {// 瑜版挸澧犳い鍨湽锟�?
			double sum = 0;
			for (int i = nowPage * pageCnt; i < Math.min(lstData.size(), (nowPage + 1) * pageCnt); i++) {
				try {
					sum += Double.parseDouble(lstData.get(i).get(field.getDbfield()).toString());
				} catch (Exception e) {
					return "0";
				}
			}
			return getNumberFormat(field, sum + "");
		}
		int row = 0;
		row = field.getDbrow() + nowPage * pageCnt - 1;
		row = Math.max(0, row);
		Object val;
		if (row > lstData.size() - 1)
			return "";
		if (field.getDbfield() == null)
			return "";
		val = lstData.get(row).get(field.getDbfield().toLowerCase());
		if (val == null)
			return "";
		String valStr = val.toString();
		if (valStr.trim().length() < 1)
			return "";
		if (field.getFormat() == null || "~".equals(field.getFormat()))
			return valStr;
		if (FORMAT.DATA.equals(field.getFormat()))// 閸樼喐鏆熼幑?
		{
			if (field.getDicnum() == null)
				return valStr;
			for (int i = 0, cnt = (field.getDicnum() + valStr.length()); i > cnt; i--) {
				valStr = "0" + valStr;
			}
			return valStr;
		}

		if (FORMAT.BOOL.equals(field.getFormat()))// 鐢啫锟�?閸欘垶鍘ら崥鍫濆礋鐎涙顑侀幋顏勫絿)
		{
			if (field.getCutnum() != null) {
				int cut = field.getCutnum();
				valStr = valStr.substring(cut - 1, cut);
			}
			if ("yes".equals(valStr.toLowerCase()))
				return "Yes";
			else if ("no".equals(valStr.toLowerCase()))
				return "No";
			else
				return "1".equals(valStr) ? "Yes" : "No";
		}
		if (FORMAT.DATE.equals(field.getFormat()))// 閺冦儲锟�?
		{
			UFDate ufd;
			try {
				ufd = new UFDate(valStr);
			} catch (Exception ex) {
				return "";
			}
			int year = ufd.getYear();
			// if(isDateAdd)
			// year+=543;//浣涘巻
			return ufd.getDay() + "-" + ufd.getMonth() + "-" + year;
		}
		if (FORMAT.SINGLE.equals(field.getFormat()))// 閸楁洖鐡х粭?
		{
			int cut = field.getCutnum();
			if (valStr.length() < cut)
				return "";
			try {
				return valStr.substring(cut - 1, cut);
			} catch (Exception ex) {
				return "";
			}
		}
		if (FORMAT.SPLITE.equals(field.getFormat()))// 閸掑棗锟�?
		{
			String[] sps = field.getSplfmt().split(",");
			char[] rts = valStr.toCharArray();
			List<String> lst = new ArrayList<String>();
			for (char c : rts) {
				lst.add(c + "");
			}
			int i = 0;
			for (String sp : sps) {
				int ins = Integer.parseInt(sp) + i;
				if (ins > lst.size())
					break;
				lst.add(ins, " ");
				i++;

			}
			valStr = Strings.toString(lst).replaceAll(",", "");
			return valStr;
		}
		if (FORMAT.SPACE.equals(field.getFormat())) {
			if (valStr == null)
				return "";
			String[] vals = valStr.split(" ");
			int cut = field.getCutnum();
			if (cut == -1)
				return vals[Math.max(vals.length - 1, 0)];
			return vals[Math.max(cut - 1, 0)];
		}
		return getNumberFormat(field, valStr);

	}

	/**
	 * <p>閺傝纭堕崥宥囆為敍姝tNumberFormat</p>
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
		if (FORMAT.NUMBER.equals(field.getFormat())) {// 娑撱倓缍呯亸蹇旀殶
			return getNumFormat(df.format(dbval));
		} else if (FORMAT.INT.equals(field.getFormat())) {// 閺佸瓨锟�?
			return getNumFormat(new DecimalFormat("#0").format(dbval)) + "";
		} else if (FORMAT.DECIMAL.equals(field.getFormat())) {// 鐏忓繑鏆熸担?
			return df.format(dbval).split("\\.")[1];
		} else if (FORMAT.TAIWEN.equals(field.getFormat())) {// 濞夌増锟�?
			return getTaiwen(df.format(dbval));// getTaiwen("12345678.91")
		} else
			return dbval + "";

	}

	/**
	 * <p>鏂规硶鍚嶇О锛歡etNumFormat</p>
	 * <p>鏂规硶鎻忚堪锛氾拷?鍙锋牸寮忓垎鍓叉暟锟�?/p>
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
	 * <p>Taiwen</p>
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
			strRet.append("喔栢箟喔о笝");
		else
			strRet.append("喔笗喔侧竾喔勦箤");
		return strRet.toString();
	}

	private String getTC(char c, int wei, int len, boolean isPre0, boolean large0) {
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
				strRet = "喔氞覆喔�";
			else
				strRet = "";
			break;
		case 2:
			strRet = "喔复喔�";
			break;
		case 3:
			strRet = "喔｀箟喔涪";
			break;
		case 4:
			strRet = "喔炧副喔�";
			break;
		case 5:
			strRet = "喔浮喔粪箞喔�";
			break;
		case 6:
			strRet = "喙佮釜喔�";
			break;
		case 7:
			strRet = "喔ム箟喔侧笝";
			break;
		case 8:
			strRet = "喔复喔氞弗喙夃覆喔�";
			break;
		case 9:
			strRet = "喔｀箟喔涪喔ム箟喔侧笝";
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
				strRet = "喙�喔箛喔�";
			else if (last == 2)
				strRet = "";
			else
				strRet = "喔笝喔多箞喔�";
			break;
		case '2':
			if (last == 2)
				strRet = "喔⑧傅";
			else
				strRet = "喔腑喔�";
			break;
		case '3':
			strRet = "喔覆喔�";
			break;
		case '4':
			strRet = "喔傅喙�";
			break;
		case '5':
			strRet = "喔箟喔�";
			break;
		case '6':
			strRet = "喔竵";
			break;
		case '7':
			strRet = "喙�喔堗箛喔�";
			break;
		case '8':
			strRet = "喙佮笡喔�";
			break;
		case '9':
			strRet = "喙�喔佮箟喔�";
			break;
		case '0':
			if (zero)
				strRet = "喔ㄠ腹喔權涪喙�";
			else
				strRet = "";
			break;
		case '.':
			strRet = "";
			// strRet = "喔堗父喔�";
			break;
		default:
			strRet = c + "";
		}
		return strRet;
	}

	/**
	 * <p>閺傝纭堕崥宥囆為敍姝tDatas</p>
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
