package nc.impl.wa.paydata.tax;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;

/**
 *
 * @author: zhangg
 * @date: 2010-1-14 上午10:43:32
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class MalaysiaPCBFormulaUtil {
	/**
	 *
	 * @author zhangg on 2010-1-12
	 * @param taxFormula
	 * @return
	 * @throws BusinessException
	 * @throws ParseException
	 */
	public static MalaysiaTaxFormulaVO translate2FormulaVO(FunctionVO taxFunctionVO, String taxFormula) throws BusinessException {

		MessageFormat format = new MessageFormat(taxFunctionVO.getArguments());
		try {
			Object[] parts = format.parse(taxFormula);
			if (parts.length != 2) {
				throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0271")/*@res "中的参数个数或者格式不正确!"*/);
			}
			//普通发放 0，多次发放 2，奖金发放 1
			String class_type = parts[0].toString().trim();
			if (class_type != null) {
				if (class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL) || class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR) ||
						class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_REDATA)) {
					Logger.info(taxFormula + "Class_type 参数取值正确!");
				} else {
					throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0272")/*@res "Class_type 参数取值不正确!, 应为0  或者 1"*/);
				}
			}
			MalaysiaTaxFormulaVO taxvo = new MalaysiaTaxFormulaVO();
			taxvo.setClass_wagetype(class_type);
			return taxvo;
		} catch (ParseException e) {
			throw new BusinessException("Pares PCB formula error, please check.");
		}
	}



	public static String getCheckTaxFormula(FunctionVO taxFunctionVO,WaClassItemVO itemVO) throws BusinessException {
		String taxFormula = TaxFormulaUtil.getTaxFormula(taxFunctionVO, itemVO.getVformula());

		return null;
	}

	/**
	 * 将公式替换成相应的可以用执行的语句
	 *
	 * @author zhangg on 2010-1-12
	 * @see nc.vo.wa.paydata.IFormula#checkReplace(java.lang.String)
	 */

	public static String getTaxFormula(FunctionVO taxFunctionVO, String itemFormula) throws BusinessException {

		// 第1步：检查是否存在tax（）函数
		// tax函数.x表示括号间有0个以上字符， 真正表达式为最大匹配
		// \\s*表示tax和（间有0个以上空格
		Pattern pattern = Pattern.compile(taxFunctionVO.getPattern());
		Matcher matcher = pattern.matcher(itemFormula);
		if (!matcher.matches()) {
			return null;
		}

		Vector<String> formulaVector = new Vector<String>();

		// 含有tax函数
		while (matcher.find()) {
			String formula = matcher.group();
			formulaVector.add(formula);
		}

		if (formulaVector.size() > 1) {
			throw new BusinessException(itemFormula + ResHelper.getString("6013salarypmt","06013salarypmt0274")/*@res "含有1个以上tax函数, 不支持!"*/);
		}

		String taxFormula = formulaVector.get(0);

		return taxFormula;
	}
}