package nc.ui.wa.datainterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.CaretposEnum;
import nc.vo.hr.datainterface.DateFormatEnum;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItemSeprtorEnum;
import nc.vo.hr.datainterface.LineTopEnum;
import nc.vo.hr.datainterface.LineTopPositionEnum;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;


public class TxtExporterForBank extends DefaultExporter
{

	public static final String crlf = System.getProperties().getProperty("line.separator"); // 换行符
	java.io.Writer raf = null;
	java.io.FileOutputStream fileOut = null;

	@Override
	protected void openFile() throws Exception
	{
		try
		{
			getAppModel().setBlnIsCancel(false);
			if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
			{
				if (getIntfaceInfs()[getReadIndex()] != null)
				{
					HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
					String fileName = getParas().getFileLocation() + "\\" + itfVO.getVifname() + ".txt";
					java.io.File file = new java.io.File(fileName);
					if (file.exists())
					{
						int i = MessageDialog.showOkCancelDlg(null, null, ResHelper.getString("6013datainterface","06013datainterface0107")/*@res "要导出的文件["*/ + file.getName() + ResHelper.getString("6013datainterface","06013datainterface0108")/*@res "]已存在，继续执行将替换原有文件，要继续吗?"*/);
						if (i != 1)
						{
							fileOut = null;
							raf = null;
							getAppModel().setBlnIsCancel(true);
							return;
						}
						// file.delete();
					}
					fileOut = new java.io.FileOutputStream(fileName);
					raf = new java.io.OutputStreamWriter(fileOut);
				}
			}

		}
		catch (Exception e)
		{
			// 错误处理代码
			closeFile();
			throw new Exception(ResHelper.getString("6013datainterface","06013datainterface0109")/*@res "文件不存在或文件路径名称错误"*/);
		}
	}

	@Override
	protected void closeFile() throws Exception
	{
		if (raf != null)
		{
			raf.close();
		}

		if (fileOut != null)
		{

			fileOut.close();
		}
	}

	/**
	 * 数字型项目的字符串标示
	 *
	 * @param aDigit
	 * @param maxFieldLen
	 * @param ifDot
	 *            //是否许需要小数点
	 * @param ifQwfg
	 *            //是否需要千位分割符
	 * @param caretPos
	 * @param caret
	 * @param decimalNum
	 * @param iIfcaret
	 * @return
	 * @throws Exception
	 */
	protected String getStringDigit(String aDigit, FormatItemVO vo, boolean ifDot, boolean ifQwfg)
	{
		// 表体每一列的期望宽度 = 列定义的宽度
		int maxFieldLen = vo.getIfldwidth();
		int decimalNum = vo.getIflddecimal(); // 小数位数
//		String includeAfter = vo.getVincludeafter();// 后位分隔符
//		String includeBefore = vo.getVincludebefore();// 前位分隔符
		nc.vo.pub.lang.UFDouble d = new nc.vo.pub.lang.UFDouble(aDigit);
		d = d.setScale(-1 * decimalNum, UFDouble.ROUND_CEILING);
		aDigit = d.toString();

		/* 增加千位分隔符 */
		if (ifQwfg)
		{
			aDigit = getAddQWFG(aDigit, ifQwfg);
		}

		/* 增加小数点 */
		int dotPos = aDigit.indexOf(".");
		if (dotPos < 0)
		{
			aDigit = aDigit + ".";
		}
		dotPos = aDigit.indexOf(".");

		if (!ifDot)
		{// 不需要
			aDigit = aDigit.substring(0, dotPos) + aDigit.substring(dotPos + 1);
		}

		int len = getStrlength(aDigit);

		if (len > maxFieldLen)
		{// 超出期望跨度，不进行截取
			/* 已经在界面中显示的长度，是用户所期望的长度。 */
			// return getStringCutByByte(aDigit, maxFieldLen);
			return aDigit;
			// return getStringCutByByte(aDigit, len);
		}
		else if (len == maxFieldLen)
		{
			return aDigit;
		}

//		if (!StringUtils.isBlank(includeBefore))
		String temp = getStringStr(aDigit, vo);
//		{
//			temp = includeBefore + temp;
//		}
//		if (!StringUtils.isBlank(includeAfter))
//		{
//			temp = temp + includeAfter;
//		}
		return temp;

	}

	/**
	 * 增加千位分割符
	 *
	 * @param num
	 * @param txtQWFG
	 * @return
	 * @throws Exception
	 */
	protected String getAddQWFG(String num, boolean txtQWFG)
	{
		if (!txtQWFG)
		{// 不需要
			return num;
		}
		else
		{
			String retNum = num;
			int posDec = num.indexOf(".");
			if (posDec < 0)
			{
				posDec = num.length();
			}
			posDec -= 3;
			while (posDec > 0)
			{
				retNum = num.substring(0, posDec) + "," + num.substring(posDec);
				posDec -= 3;
			}
			return retNum;
		}
	}

	/**
	 * 功能描述: 以BYTE为单位截取字串长度 输入参数说明:s 字串, l是BYTE数
	 */
	public String getStringCutByByte(String s, int l)
	{
		if (s == null)
		{
			return null;
		}
		int len = s.length();
		for (int i = len; i > 0; i--)
		{
			if (getStrlength(s) <= l)
			{
				return s;
			}
			s = s.substring(0, i);
		}
		return s;
	}

	protected String getStringStr(String aStr, FormatItemVO vo)
	{
		int maxFieldLen = vo.getIfldwidth();
		int caretPos = vo.getIcaretpos() == null ? (Integer) CaretposEnum.NO.value() : vo.getIcaretpos();
		String caret = vo.getVcaret();
		String includeAfter = vo.getVincludeafter();// 后位分隔符
		String includeBefore = vo.getVincludebefore();// 前位分隔符

		int i, len;

		len = getStrlength(aStr);

		if (len > maxFieldLen)
		{
			aStr = getStringCutByByte(aStr, maxFieldLen);
		}

		len = getStrlength(aStr);

		/* 增加补位符 */
		/**
		 * Modified by Young 2005-06-28 Start
		 */
		if (caretPos != 0)
		{
			i = 0;

			if (len < maxFieldLen)
			{
				if (caret == null || caret.trim().equals(""))
				{
					caret = BLANK;
				}

				while (i < maxFieldLen - len)
				{
					if (caretPos == 1) // 补前
					{
						aStr = caret + aStr;
					}
					else
					{
						aStr = aStr + caret;
					}
					i++;
				}
			}

		}
		if (!StringUtils.isBlank(includeBefore))
		{
			aStr = includeBefore + aStr;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			aStr = aStr + includeAfter;
		}
		return aStr;
	}

	/**
	 * 是否需要小数点
	 */
	protected boolean isNeedDot()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifdot();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * 是否需要千位分割符
	 */
	protected boolean isNeedKilobit()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifkilobit();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * 是否需要统一补位符
	 *
	 * @return
	 */
	protected boolean isNeedCaret()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifcaret();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * 是否使用统一项目分割符
	 *
	 * @return
	 */
	protected boolean isNeedSeperator()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifseparator();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	protected String getUnifySeperator()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIseparator();
				return DataIOconstant.ITEMSEPERATOR.get(value);
			}
		}
		return DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
	}

	@Override
	protected void beforeOutputHead()
	{
		if (raf == null)
		{
			return;
		}
		// 接口信息中是否输出行号 并且是首行
		if (isUseTopLine())
		{
			// 是，则得到所有的行号信息
			String signline = getFlagLine(LineTopPositionEnum.HEAD.toIntValue());
			// 循环处理行号信息
			signline = signline + (crlf);
			try
			{
				raf.write(signline.toString());
				raf.flush();
			}
			catch (IOException e)
			{}
		}
	}

	@Override
	protected void afterOutputBody()
	{
		// 薪资福利可能需要标志行
		// 接口信息中是否输出行号 并且是末行
		if (raf == null)
		{
			return;
		}
		if (isUseBottomLine())
		{
			// 是，则得到所有的行号信息
			String signline = getFlagLine(LineTopPositionEnum.TAIL.toIntValue());
			// 循环处理行号信息
			signline = signline + (crlf);
			try
			{
				raf.write(signline.toString());
				raf.flush();
			}
			catch (IOException e)
			{

			}
		}
	}

	private boolean isUseTopLine()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIiftop();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}
	
	// HR本地化：判断标志行2是否启用
	private boolean isUseBottomLine()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIiftop2();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	private boolean isTheFirst()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getToplineposition();
				return (value == (Integer) LineTopPositionEnum.HEAD.value()) ? true : false;
			}
		}
		return false;
	}

	private boolean isSline()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getToplinenum();
				return (value == (Integer) LineTopEnum.MLINE.value()) ? false : true;
			}
		}
		return false;
	}

	
	// HR本地化改动：根据参数决定取首行项还是尾行项
	private IfsettopVO[] getSignlineItems(int toplinePosition)
	{
		IfsettopVO[] ret = null;
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				ret = (IfsettopVO[]) getIntfaceInfs()[getReadIndex()].getTableVO(DataIOconstant.HR_IFSETTOP);
				ArrayList<IfsettopVO> temp = new ArrayList<IfsettopVO>(Arrays.asList(ret));
				for (int i = temp.size() - 1; i >= 0; i--) {
					if (temp.get(i).getItoplineposition().equals(toplinePosition)) {
						continue;
					} else {
						temp.remove(i);
					}
				}
				ret = temp.toArray(new IfsettopVO[0]);
			}
		}

		return ret;
	}

	protected String getStringDigit4TopLine(String aDigit, IfsettopVO vo, boolean ifDot, boolean ifQwfg)
	{
		// 表体每一列的期望宽度 = 列定义的宽度
		int maxFieldLen = vo.getIfldwidth();
		int decimalNum = 0;
		if (vo.getIflddecimal() != null)
		{
			decimalNum = vo.getIflddecimal(); // 小数位数
		}

		String includeAfter = "";// 后位分隔符
		String includeBefore = "";// 前位分隔符
		nc.vo.pub.lang.UFDouble d = new nc.vo.pub.lang.UFDouble(aDigit);
		d = d.setScale(-1 * decimalNum, UFDouble.ROUND_CEILING);
		aDigit = d.toString();

		/* 增加千位分隔符 */
		if (ifQwfg)
		{
			aDigit = getAddQWFG(aDigit, ifQwfg);
		}

		/* 增加小数点 */
		int dotPos = aDigit.indexOf(".");
		if (dotPos < 0)
		{
			aDigit = aDigit + ".";
		}
		dotPos = aDigit.indexOf(".");

		if (!ifDot)
		{// 不需要
			aDigit = aDigit.substring(0, dotPos) + aDigit.substring(dotPos + 1);
		}

		int len = getStrlength(aDigit);

		if (len > maxFieldLen)
		{// 超出期望跨度，不进行截取
			/* 已经在界面中显示的长度，是用户所期望的长度。 */
			// return getStringCutByByte(aDigit, maxFieldLen);
			return aDigit;
			// return getStringCutByByte(aDigit, len);
		}
		else if (len == maxFieldLen)
		{
			return aDigit;
		}

		String temp = getStringStr4TopLine(aDigit, vo);
		if (!StringUtils.isBlank(includeBefore))
		{
			temp = includeBefore + temp;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			temp = temp + includeAfter;
		}
		return temp;
	}

	protected String getStringStr4TopLine(String aStr, IfsettopVO vo)
	{
		int maxFieldLen = vo.getIfldwidth();
		int caretPos = vo.getIcaretpos() == null ? (Integer) CaretposEnum.BEFORE.value() : vo.getIcaretpos();
		String caret = vo.getVcaret();
		String includeAfter = "";// 后扩符
		String includeBefore = "";// 前扩符

		int i, len;

		len = getStrlength(aStr);

		if (len > maxFieldLen)
		{
			aStr = getStringCutByByte(aStr, maxFieldLen);
		}

		/* 增加补位符 */
		/**
		 * Modified by Young 2005-06-28 Start
		 */
		if (caretPos != 0)
		{
			i = 0;

			if (len < maxFieldLen)
			{
				if (caret == null || caret.trim().equals(""))
				{
					caret = BLANK;
				}

				while (i < maxFieldLen - len)
				{
					if (caretPos == 1) // 补前
					{
						aStr = caret + aStr;
					}
					else
					{
						aStr = aStr + caret;
					}
					i++;
				}
			}

		}
		if (!StringUtils.isBlank(includeBefore))
		{
			aStr = includeBefore + aStr;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			aStr = aStr + includeAfter;
		}
		return aStr;
	}

	public String[] getTblAndCol(String tblAndCol)
	{
		int index = tblAndCol.indexOf(".");

		String tbl = tblAndCol.substring(0, index);

		String col = tblAndCol.substring(index + 1);

		String[] newTblAndCol = new String[2];

		newTblAndCol[0] = tbl;
		newTblAndCol[1] = col;

		return newTblAndCol;
	}

	// HR本地化：本来这边源码是全部注掉的 现在重新实现了一下
	protected String getItemSum(String[] tabAndCol)
	{
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());
		
		String key = tabAndCol[0] + tabAndCol[1];
		UFDouble sum = new UFDouble(UFDouble.ZERO_DBL);
		if (datas != null && datas.size() > 0) {
			for (HashMap<String, Object> entry : datas) {
				if (entry.containsKey(key)) {
					if (entry.get(key) != null) {
						sum = sum.add(new UFDouble(entry.get(key).toString()));
					}
				} else {
					ExceptionUtils.wrappBusinessException("The total sum item key does not exist!");
				}
			}
		}

		return sum.toString();
	}
	
	// HR本地化：添加一个可以取一些公共字符串的东西
	protected String getFirstLineContent(String[] tabAndCol) {
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());
		
		String key = tabAndCol[0] + tabAndCol[1];
		String result = null;
		if (datas != null && datas.size() > 0) {
			HashMap<String, Object> entry = datas.get(0);
			if (entry.containsKey(key)) {
				if (entry.get(key) != null) {
					result = entry.get(key) == null ? null : entry.get(key).toString();
				}
			} else {
				ExceptionUtils.wrappBusinessException("The first line content item key does not exist!");
			}
		}
		return result;
	}

	// 根据格式来格式化日期
	private String formatDate(UFDate date, String dateFormat)
	{
		// 取代 dateformat中的 YYYY MM DD
		String year = String.valueOf(date.getYear());
		String yearTwoDigit = year.substring(2);
		String month = String.valueOf(date.getMonth());
		if (month.trim().length() == 1)
		{
			month = "0" + month;
		}

		String day = String.valueOf(date.getDay());
		if (day.trim().length() == 1)
		{
			day = "0" + day;
		}
		return dateFormat.replaceAll("YYYY", year).replaceAll("YY", yearTwoDigit).replaceAll("MM", month).replaceAll("DD", day);

	}

	public String getFlagLine(int lineTopPosition)
	{
		IfsettopVO[] data_top = getSignlineItems(lineTopPosition);
		if (data_top == null)
		{
			return "";
		}
		boolean line = isSline();
		StringBuilder topLine = new StringBuilder();
		

		try
		{

			for (int i = 0; i < data_top.length; i++)
			{
				// 新行逻辑重新写了一遍 之前尾行处理不符合新加的设置
				if (data_top[i].getInextline().equals(BooleanEnum.YES.toIntValue()) && line) {
					topLine.append(crlf);
				} else if (i < data_top.length - 1 && !line && i != 0) {
					topLine.append(crlf);
				}
				Object objitemSumTableAndCol = data_top[i].getVfieldname();
				String itemSumTableAndCol = "";
				if (objitemSumTableAndCol != null)
				{
					itemSumTableAndCol = objitemSumTableAndCol.toString();
				}
				// String topSeperater = null;
				// 得到项目分割符
				// if(!StringHelper.isEmpty(data_top[i].getVseparator())){
				// topSeperater = data_top[i].getVseparator()
				// }
				String topSeperater = DataIOconstant.ITEMSEPERATOR.get(data_top[i].getVseparator());
				if (topSeperater == null)
				{
					topSeperater = DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
				}

				// 处理发放人数
				if (data_top[i].getVcontent().equals(DataIOconstant.PSNCOUNT))
				{// 人数
					// 对于发放人数，考虑 "项目长度" “补位位置” 与 “补位符”
					// int length = getFormatItemVOs().length;
					HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
					ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());

					topLine.append(getStringDigit4TopLine(String.valueOf(datas.size()), data_top[i], isNeedDot(), false));
				}
				else if (data_top[i].getVcontent().equals(DataIOconstant.ITEMSUM))
				{// 项目合计
					// 处理项目合计
					// 得到表明与字段
					if (!StringUtils.isBlank(itemSumTableAndCol))
					{
						String[] stArrayTabAndCol = getTblAndCol(itemSumTableAndCol);
						String itemSum = getItemSum(stArrayTabAndCol);
						itemSum = getStringDigit4TopLine(itemSum, data_top[i], isNeedDot(), false);
						topLine.append(itemSum);
					}
				}
				else if (data_top[i].getVcontent().equals(DataIOconstant.FIRSTLINECONTENT))
				{// 首行内容
					if (!StringUtils.isBlank(itemSumTableAndCol))
					{
						String[] stArrayTabAndCol = getTblAndCol(itemSumTableAndCol);
						String firstLineContent = getFirstLineContent(stArrayTabAndCol);
						firstLineContent = getStringStr4TopLine(firstLineContent, data_top[i]);
						topLine.append(firstLineContent);
					}
				}
				// 处理单位代号
				else if (data_top[i].getVcontent().equals(DataIOconstant.UNITCODE))
				{
					topLine.append(getStringStr4TopLine(itemSumTableAndCol, data_top[i]));
				}
				// 处理日期，注意日期格式
				else if (data_top[i].getVcontent().equals(DataIOconstant.DATE))
				{
					String content = itemSumTableAndCol;
					String datef = data_top[i].getDateformat();
					if (datef == null)
					{
						datef = (String) DateFormatEnum.Y_M_D.value();
					}
					if (StringUtils.isBlank(content))
					{
						topLine.append(getStringStr4TopLine("", data_top[i]));
					}
					else
					{
						topLine.append(getStringStr4TopLine(formatDate(new UFDate(content), datef), data_top[i]));
					}

				}
				// 添加分割符
				if (topSeperater != null && i < data_top.length - 1)
				{
					topLine.append(topSeperater);
				}
				// 是否输出多行
				// 这边的逻辑针对本地化需求先注掉
//				if (i < data_top.length - 1 && !line )
//				{
//					topLine.append(crlf);
//				}
			}

		}
		catch (Exception ex)
		{
			// throw ex;
		}
		return topLine.toString();
	}

	private FormatItemVO[] getFormatItemVOs()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				return (FormatItemVO[]) getIntfaceInfs()[getReadIndex()].getTableVO(DataIOconstant.HR_DATAINTFACE_B);
			}
		}

		return null;
	}

	@Override
	protected void outPutHead() throws IOException
	{
		if (raf == null)
		{
			return;
		}
		if (getIntfaceInfs() == null || (getReadIndex() + 1) > getIntfaceInfs().length || getIntfaceInfs()[getReadIndex()] == null)
		{
			return;
		}
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		if (itfVO.getIouthead() == null || itfVO.getIouthead().equals(BooleanEnum.NO.value()))
		{
			return;
		}

		// 输出表头
		FormatItemVO[] vos = getFormatItemVOs();
		StringBuilder headline = new StringBuilder();

		// 循环处理项目设置
		// 项目分割符
		// 补位符
		boolean isAdjust = (itfVO.getIheadadjustbody() != null && (itfVO.getIheadadjustbody().equals(BooleanEnum.YES.value())));
		// 处理行号
		if (isSetLnsraLength() && isAdjust)
		{
			// 如果设置行号长度。并且表头与表体格式一致 则表头进行补位
			for (int index = 0; index < getLnsraLength(); index++)
			{
				headline.append(BLANK);
			}
			if (isNeedSeperator())
			{// 统一使用项目分割符
				// 默认的项目分割符长度都是一位
				headline.append(BLANK);
			}

		}

		for (int index = 0; index < vos.length; index++)
		{
			FormatItemVO formatItemVO = vos[index];
			String name = formatItemVO.getVfieldname().trim();

			// 取项目分隔符
			String topSeperater = DataIOconstant.ITEMSEPERATOR.get(formatItemVO.getVseparator());
			if (topSeperater == null)
			{
				topSeperater = "";
			};

			// 值的实际宽度
			int nameLen = getStrlength(name);
			// 确定项目分割符
			if (!isAdjust)
			{// 表头与表体格式一致,如果不一致 ，核查是否统一使用分割符

				if (isNeedSeperator())
				{// 如果不一致 ，核查是否统一使用分割符
					topSeperater = getUnifySeperator();// 统一项目分割符
				}
				else
				{
					topSeperater = DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
				}
			}

			// 在首行中，本列的期望宽度= 列定义宽度+前括符宽度+后括符宽度
			int fieldLen = formatItemVO.getIfldwidth().intValue(); // 宽度
			String includeBefore = formatItemVO.getVincludebefore();
			String includeAfter = formatItemVO.getVincludeafter();

			if (includeBefore != null)
			{
				fieldLen += getStrlength(includeBefore);
			}
			if (includeAfter != null)
			{
				fieldLen += getStrlength(includeAfter);
			}

			// 实际宽度超大， 不进行截取
			if (fieldLen < nameLen)
			{
				// headline.append(getStringCutByByte(name, fieldLen));
				headline.append(name);

			}
			else
			{// 实际宽度不足
				if (isAdjust)
				{// 是否表头与表体格式一致
					// 表头是不能补充数字的,所以将所有的单个数字都替换为空格
					String caret = formatItemVO.getVcaret();
					if (caret != null)
					{
						caret = caret.replaceAll("\\d", " ");
					}

					name = complementString(name, fieldLen, formatItemVO.getIcaretpos() == null ? (Integer) CaretposEnum.NO.value() : formatItemVO.getIcaretpos(), caret);
				}
				else
				{
					if (isNeedCaret())
					{// 统一使用补位符，数字型前补0，字符型前补空格
						name = complementString(name, fieldLen, (Integer) CaretposEnum.BEFORE.value(), DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value()));//
					}
				}// 否则不进行补位
				headline.append(name);
			}
			// 添加分割符
			if (topSeperater != null && index < vos.length - 1)
			{
				headline.append(topSeperater);
			}
		}
		headline.append(crlf);
		raf.write(headline.toString());
		raf.flush();
	}

	@Override
	protected void outPutBody() throws IOException
	{
		if (raf == null)
		{
			return;
		}
		boolean dot = isNeedDot();
		boolean kilobit = isNeedKilobit();
		// boolean needLineNo = getParas().isOutPutLineNo();

		if (getIntfaceInfs() == null || (getReadIndex() + 1) > getIntfaceInfs().length || getIntfaceInfs()[getReadIndex()] == null)
		{
			return;
		}
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());

		AppendableVO[] appendVOs = (AppendableVO[]) (getAppModel()).getBillModelMap().get(itfVO.getPk_dataio_intface()).getBodyValueVOs(AppendableVO.class.getName());

		for (int index = 0; appendVOs != null && index < appendVOs.length; index++)
		{
			StringBuilder sbd = new StringBuilder();
			// HashMap<String, Object> map = datas.get(index);
			// CircularlyAccessibleValueObject rowVO = getBillmodel()
			// .getBodyValueRowVO(index, getBodyVOName());
			// 循环fomatVOS，处理每一个单元格
			FormatItemVO[] vos = getFormatItemVOs();
			// if (needLineNo) {// 是否需要行号
			// sbd.append(getLineNo(index + 1));
			// if (isNeedSeperator()) {
			// sbd.append(getUnifySeperator());
			// } else {
			// sbd.append(BLANK);// 不统一使用分割符，则添加一个空格
			// }
			// }
			for (int temp = 0; temp < vos.length; temp++)
			{
				FormatItemVO formatItemVO = vos[temp];
				// 换行处理
				if (formatItemVO.getInextline().equals(BooleanEnum.YES.toIntValue())) {
					sbd.append(crlf);
				}
				// 获取单元值
				Object value = appendVOs[index].getAttributeValue(formatItemVO.getVcontent().replace(".", ""));

				if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.DEC.value()))
				{// 数字型

					Object b = null;
					if (value == null)
					{
						b = new BigDecimal(0.00);
					}
					else
					{
						if (value instanceof BigDecimal)
						{
							b = value;
						}
						else
						{
							b = value;
						}
					}

					value = getStringDigit(b != null ? b.toString() : "", formatItemVO, dot, kilobit);

				}
				else if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.DATE.value()))
				{
					if (value == null) {
						value = "";
					} else {
						value = value.toString().substring(0, 10);
					}
					formatItemVO.setVcaret(null);
					// HR本地化改动，让行也能输出日期，简直有病 ，导个银行报盘还要放生日么
					value = formatDate(new UFDate(value.toString()), formatItemVO.getDateformat());
					value = getStringStr((String)value, formatItemVO);
				}
				else if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.BOO.value()))
				{

				}
				else
				{// //字符型
					if (value == null)
					{
						value = "";
					}
					value = getStringStr((String) value, formatItemVO);
				}

				// 添加单元值
				sbd.append(value);

				if (temp < vos.length - 1)
				{
					String topSeperater = DataIOconstant.ITEMSEPERATOR.get(formatItemVO.getVseparator());
					if (topSeperater == null)
					{
						topSeperater = "";
					}

					// 添加项目分割符
					sbd.append(topSeperater);
				}

			}
			// 输出sbd
			sbd.append(crlf);
			raf.write(sbd.toString());
			raf.flush();
		}

		// for (int index = 0; index < getBillmodel().getRowCount(); index++) {
		// StringBuilder sbd = new StringBuilder();
		// CircularlyAccessibleValueObject rowVO = getBillmodel()
		// .getBodyValueRowVO(index, getBodyVOName());
		// // 循环fomatVOS，处理每一个单元格
		// FormatItemVO[] vos = getFormatItemVOs();
		// if(needLineNo){//是否需要行号
		// sbd.append(getLineNo(index+1));
		// if(isNeedSeperator()){
		// sbd.append(getUnifySeperator());
		// }else{
		// sbd.append(BLANK);//不统一使用分割符，则添加一个空格
		// }
		// }
		// for (int temp = 0; temp < vos.length; temp++) {
		// FormatItemVO formatItemVO = vos[temp];
		//
		// // 获取单元值
		// Object obj = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent()));
		// String value = "";
		// if(obj!=null){
		// value = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent())).toString();
		// }
		//
		// if (formatItemVO.getIfieldtype() == 1) {//数字型
		// value = getStringDigit(value, formatItemVO, dot, kilobit);
		// }else {////字符型
		// value = getStringStr(value, formatItemVO);
		// }
		// //添加单元值
		// sbd.append(value);
		//
		// if(temp < vos.length-1){
		// //添加项目分割符
		// sbd.append(formatItemVO.getVseparator());
		// }
		//
		//
		// }
		// //输出sbd
		// sbd.append(crlf);
		// raf.write(sbd.toString());
		// raf.flush();
		// }

		// for (int index = 0; index < getBillmodel().getRowCount(); index++) {
		// StringBuilder sbd = new StringBuilder();
		// CircularlyAccessibleValueObject rowVO = getBillmodel()
		// .getBodyValueRowVO(index, getBodyVOName());
		// // 循环fomatVOS，处理每一个单元格
		// FormatItemVO[] vos = getFormatItemVOs();
		// if(needLineNo){//是否需要行号
		// sbd.append(getLineNo(index+1));
		// if(isNeedSeperator()){
		// sbd.append(getUnifySeperator());
		// }else{
		// sbd.append(BLANK);//不统一使用分割符，则添加一个空格
		// }
		// }
		// for (int temp = 0; temp < vos.length; temp++) {
		// FormatItemVO formatItemVO = vos[temp];
		//
		// // 获取单元值
		// Object obj = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent()));
		// String value = "";
		// if(obj!=null){
		// value = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent())).toString();
		// }
		//
		// if (formatItemVO.getIfieldtype() == 1) {//数字型
		// value = getStringDigit(value, formatItemVO, dot, kilobit);
		// }else {////字符型
		// value = getStringStr(value, formatItemVO);
		// }
		// //添加单元值
		// sbd.append(value);
		//
		// if(temp < vos.length-1){
		// //添加项目分割符
		// sbd.append(formatItemVO.getVseparator());
		// }
		//
		//
		// }
		// //输出sbd
		// sbd.append(crlf);
		// raf.write(sbd.toString());
		// raf.flush();
		// }
	}

	/**
	 * 得到某一行的行号
	 */
	// protected String getLineNo(int lineNo){
	// String str= String.valueOf(lineNo);
	// if(isSetLnsraLength()){
	// str=complementString(String.valueOf(lineNo), getLnsraLength(),
	// DataIOconstant.BEFORECARET, getLnsraCaret()) ;
	// }
	// return str;
	// }
	/**
	 * 是否设置标志行长度
	 *
	 * @return
	 */
	protected boolean isSetLnsraLength()
	{
		// int dot = ((HrIntfaceVO)getIntfaceInf().getParentVO()).getIiflnsra();
		int dot = 0;
		return (dot == 1) ? true : false;
	}

	/**
	 * 得到标志行长度
	 *
	 * @return
	 */
	protected int getLnsraLength()
	{
		// return ((HrIntfaceVO)getIntfaceInf().getParentVO()).getLnlength();
		return 20;

	}

	/**
	 * 得到标志行补位符
	 *
	 * @return
	 */
	protected String getLnsraCaret()
	{
		// return ((HrIntfaceVO)getIntfaceInf().getParentVO()).getLncaret();
		return "";

	}
}