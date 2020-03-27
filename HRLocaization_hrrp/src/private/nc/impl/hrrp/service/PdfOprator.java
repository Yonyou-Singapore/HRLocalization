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
import nc.bs.logging.Logger;
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
		//fontsize add by weiningc start
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
		// copy page处理
		Document doc = new Document();
		FileOutputStream out = new FileOutputStream(new File(tarPath + ".pdf"));
		PdfCopy pdfc = new PdfCopy(doc, new FileOutputStream(tarPath + ".pdf"));
		int i = 1;
		doc.open();
		//add by weiningc 20191021 支持copypage 多张配置,比如允许2，3张分别重复 start
		List<Integer> copypagenos = new ArrayList<Integer>();
		if(rp.getCopypageno() == null) {
			copypagenos.add(Integer.valueOf(1));
		} else {
			//转字符,再按照char分割,转数字
			String copypageno = String.valueOf(rp.getCopypageno());
			for(int k=0; k<copypageno.length(); k++) {
				copypagenos.add(Integer.valueOf(String.valueOf(copypageno.charAt(k))));
			}
		}
		
		for(int k=0; k<copypagenos.size(); k++) {
			Integer copypage = copypagenos.get(k);
			for (String str : lstTemp) {
				File fTemp = new File(str);
				FileInputStream inTemp = new FileInputStream(fTemp);
				PdfReader pdfr = new PdfReader(inTemp);
				rp.setCopypageno(copypage == null ? 1 : copypage);
				//merge 重复前的几页
				if (i == 1 && k == 0 && str.equalsIgnoreCase(lstTemp.get(k))) {//第一次的时候才加
					for (int j = 1, cnt = copypage; j < cnt; j++) {
						pdfc.addPage(pdfc.getImportedPage(pdfr, j));
					}
				}
				//merge copypage no
				PdfImportedPage ppage = pdfc.getImportedPage(pdfr,
						copypage);
				pdfc.addPage(ppage);
				// merge最后页
				if (i == lstTemp.size()) {
					for (int j = copypage + 1, cnt = pdfr
							.getNumberOfPages() + 1; j < cnt; j++) {
						pdfc.addPage(pdfc.getImportedPage(pdfr, j));
					}
				}
				//最后次的时候才close和计数
				if(k == copypagenos.size() - 1) {
					pdfr.close();
					inTemp.close();
					i++;
					fTemp.delete();
				}
			}
		}
		// in.close();
		if(out != null) {
			out.close();
		}
		if(doc != null) {
			doc.close();
		}
		File fret = new File(tarPath + ".pdf");
		FileInputStream is = new FileInputStream(fret);
		byte[] bts = new byte[is.available()];
		is.read(bts);
		is.close();
		fret.delete();
		return bts;
	}

	/**
	 * 前一页
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
	 * <p>
	 * 填充pdf field
	 * </p>
	 * 
	 * @param field
	 * @param nowPage
	 *            当前页
	 * @param prepage
	 *            前一页
	 * @param pageCnt
	 *            总页数
	 * @return
	 * @author
	 */
	private String fillData(Field field, int nowPage, int prepage, int pageCnt) {
		if (lstData == null || lstData.size() < 1) {
			return "";
		}
		// 原值
		if (HrrpFormat.SRCVALUE.value().equals(field.getFormat())) {
			return field.getDbfield();
		}
//		if (FORMAT.PAGE_NOW.equals(field.getFormat()))// 瑜版挸澧犳い锟�
//			return (nowPage + 1) + "";
//		if (FORMAT.TOTAL.equals(field.getFormat()))// 閹銆夐弫锟�
//			return pageTotal + "";
		field.setDbrow(field.getDbrow() == null ? 1 : field.getDbrow());
		
		//当前页
		Logger.info("===TotalType===" + field.getTotaltype());
		if (TotalRangeEnum.CURRENTPAGE.value().equals(field.getTotaltype())) {
			Logger.info("===当前页===Field: " + field.getName() + " ,DBField: " + field.getDbfield());
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
			Logger.info("===褰撳墠椤电疮璁�=== SUM: " + sum + " ,Size: " + lstData.size() + " NowPage: " + nowPage);
			String currentvalue = getNumberFormat(field, sum + "");
			//空格分割
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		// -2前一页
		if (TotalRangeEnum.BEFOREPAGE.value().equals(field.getTotaltype())) {
			if (prepage == -1) {// 第一页
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
			//空格分割
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		// -3总合计
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
			//空格分割
			if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
				return this.getSplitSpace(currentvalue, field);
			}
			return currentvalue;
		}
		//-4鎬荤疮璁″悎璁�
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
			//缁岀儤鐗搁崚鍡楀
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
			//瑜版挸澧犳い锟�
			if(HrrpFormat.SPLIT_CURRENTPAGE.value().equals(field.getFormat())) {
				return String.valueOf(nowPage+1);
			}
			//閹銆夐弫锟�
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
		//閺嶇厧绱￠崚鍡楀
		if (HrrpFormat.SPLITSEGMENT.value().equals(field.getFormat()))
		{
			return this.getSplitSegment(valStr, field);
			
		}
		//缁岀儤鐗搁崚鍡楀
		else if (HrrpFormat.SPLITSPACE.value().equals(field.getFormat())) {
			return this.getSplitSpace(valStr, field);
		}
		//瑜版挸澧犳い锟�
		else if(HrrpFormat.SPLIT_CURRENTPAGE.value().equals(field.getFormat())) {
			return String.valueOf(nowPage);
		}
		//閹銆夐弫锟�
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
		// 缁岀儤鐗搁崚鍡楀
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
		// 缁岀儤鐗搁崚鍡楀
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
	 * 缁岀儤鐗搁梻纾嬬獩
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
			ExceptionUtils.wrappBusinessException("瑜版挷璐熺粚鐑樼壐閸掑棗澹婇惃鍕閸婃瑱绱濋崚鍡楀閺嶇厧绱¤箛鍛淬�忛弰顖涙殻閺侊拷!");
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
		if (field.getDicnum() != null && field.getDicnum() == 2) {// 娑撱倓缍呯亸蹇旀殶
			return getNumFormat(df.format(dbval));
		} else if (HrrpFormat.SPLIT_INTEGER.value().equals(field.getFormat())) {// 閺佸瓨鏆�
			return getNumFormat(new DecimalFormat("#0").format(dbval)) + "";
		} /*else if (FORMAT.DECIMAL.equals(field.getFormat())) {// 鐏忓繑鏆�
			return df.format(dbval).split("\\.")[1];
		}*/ else if (FORMAT.TAIWEN.equals(field.getFormat())) {// 濞夌増鏋�
			return getTaiwen(df.format(dbval));// getTaiwen("12345678.91")
		} else
			return dbval + "";

	}

	/**
	 * <p>
	 * 闂佸搫鍊介～澶屾兜閸洖瑙︾�广儱娉﹂悙鐑樻櫖婵繍鎽NumFormat
	 * </p>
	 * <p>
	 * 闂佸搫鍊介～澶屾兜閸洖绠甸煫鍥ㄨ壘閻楁岸鏌ㄥ☉娆戝暡闁瑰嚖鎷�?闂佸憡鐟╅弨閬嶆偋缁嬫鍤曢煫鍥ㄦ尭閻庡鏌涢幘鍐茬骇闁哄棛鍠栭弫鎾绘晸閿燂拷?/p>
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
			strRet.append("濞夌増鏋冮弫瀛樻殶閺嶇厧绱�");
		else
			strRet.append("濞夌増鏋冪亸蹇旀殶閺嶇厧绱�");
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
				strRet = "闁哥姵姊归惄锟介悷鏇炴閺嬫牠鏁撻敓锟�";
			else
				strRet = "";
			break;
		case 2:
			strRet = "闁哥姵妫忛敓钘夌仢椤︽煡宕伴弮鎾村";
			break;
		case 3:
			strRet = "闁哥姵妫戠徊鍝ョ不閻旈攱鐏冩い鎾寸懄娑擄拷";
			break;
		case 4:
			strRet = "闁哥姵姊婚崑瀣礈椤栨碍鐏冮柨鐕傛嫹";
			break;
		case 5:
			strRet = "闁哥姵妫忛崐钘壝归鍏肩亙缂侇噮浜為悿鍕窗閺冩挻瀚�";
			break;
		case 6:
			strRet = "闁哥姵鐟ら悿鍡涙煂濠婂啯鐏冮柨鐕傛嫹";
			break;
		case 7:
			strRet = "闁哥姵鏌ｉ崕顒傜不閻旈攱鐏冨〒姘�鍛嚈";
			break;
		case 8:
			strRet = "闁哥姵妫忛敓钘夌仢椤︽煡宕伴弮锟介惄锟界�殿喗顨呴弸鈩冨緞閸愵収娲柛鐘虫閹凤拷";
			break;
		case 9:
			strRet = "闁哥姵妫戠徊鍝ョ不閻旈攱鐏冩い鎾寸懄娑撱垽宕伴弬璇插姇缂佺姷鍠庨弸鏍ㄧ瑹瑜忛悷锟�";
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
				strRet = "闁哥姵鐟遍幏鐑藉窗閺冩垵骞嶇紒鐘崇☉閺嬫牠鏁撻敓锟�";
			else if (last == 2)
				strRet = "";
			else
				strRet = "闁哥姵妫忛崐鐣岀箔濠靛棙鐏冨鑸垫皑閻ゅ嫰宕伴弮鎾村";
			break;
		case '2':
			if (last == 2)
				strRet = "闁哥姵鏌￠幊鍛村磼閿燂拷";
			else
				strRet = "闁哥姵妫忛敓鍊熷劵閸橆剟宕伴弮鎾村";
			break;
		case '3':
			strRet = "闁哥姵妫忛敓鍊熷劵椤╊偊宕伴弮鎾村";
			break;
		case '4':
			strRet = "闁哥姵妫忛敓钘夌仢閸婃洟宕板▎娆愬";
			break;
		case '5':
			strRet = "闁哥姵妫忛崐鐣岀不閻旈攱鐏冮柨鐕傛嫹";
			break;
		case '6':
			strRet = "闁哥姵妫忛崐鐣岀博閿燂拷";
			break;
		case '7':
			strRet = "闁哥姵鐟遍幏鐑藉窗閺傝法澧嶇紒鐘崇☉閺嬫牠鏁撻敓锟�";
			break;
		case '8':
			strRet = "闁哥姵鐟ら悿鍡欑箔閳ヨ櫕鐏冮柨鐕傛嫹";
			break;
		case '9':
			strRet = "闁哥姵鐟遍幏鐑藉窗閺傚墽鏋傜紒鐘靛枎閺嬫牠鏁撻敓锟�";
			break;
		case '0':
			if (zero)
				strRet = "闁哥姵鏌ｉ崝鍡涙嚈閻熺増鐏冩繛鍡楋攻娑撱垽宕板▎娆愬";
			else
				strRet = "";
			break;
		case '.':
			strRet = "";
			// strRet = "闁哥姵鏌ㄩ悧锟介柣鏍硾閺嬫牠鏁撻敓锟�";
			break;
		default:
			strRet = c + "";
		}
		return strRet;
	}

	/**
	 * <p>
	 * 闂備礁鎼崐浠嬶綖婢跺本鍏滈柛顐ｆ礀鐟欙妇锟藉箍鍎卞▔锕傛倷閻戞ɑ娅栧┑顔界箥閹筋柡Datas
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
