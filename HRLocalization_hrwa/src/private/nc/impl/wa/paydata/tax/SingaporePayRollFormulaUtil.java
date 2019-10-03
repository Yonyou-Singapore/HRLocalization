package nc.impl.wa.paydata.tax;

import java.text.MessageFormat;
import java.text.ParseException;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.impl.wa.paydata.precacu.sg.SingaporeFormulaVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;

public class SingaporePayRollFormulaUtil {
	
	/**
	 * 校验参数合法性
	 * @param taxFunctionVO
	 * @param taxFormula
	 * @return
	 * @throws BusinessException
	 */
	public static SingaporeFormulaVO translate2FormulaVO(FunctionVO taxFunctionVO, String singaporeFormula) throws BusinessException {

		MessageFormat format = new MessageFormat(taxFunctionVO.getArguments());
		try {
			Object[] parts = format.parse(singaporeFormula);
			if (parts.length != 7) {
				throw new BusinessException(singaporeFormula + ResHelper.getString("6013salarypmt","06013salarypmt0271")/*@res "中的参数个数或者格式不正确!"*/);
			}
			//总缴纳部分 or 雇员缴纳部分 0--总缴纳  1--雇员缴纳
			Integer payer = Integer.valueOf((String) parts[0]);
			//公司性质  0--private  1--public
			Integer orgmode = Integer.valueOf((String) parts[1]);
			if (payer != null) {
				if (payer.compareTo(0) < 0 || payer.compareTo(SingaporeFormulaVO.AWTOTAL_CPFRATE) > 0) {
					throw new BusinessException(singaporeFormula + " ,First para not correct");
				}
			}
			if(orgmode != null) {
				if (orgmode.equals(SingaporeFormulaVO.PRIVATEORGMODE) || orgmode.equals(SingaporeFormulaVO.PUBLICORGMODE)) {
					Logger.info(singaporeFormula + " ,公司性质值正确");
				} else {
					throw new BusinessException(singaporeFormula + " ,Second para can only be 1 or 0");
				}
			}
			SingaporeFormulaVO singaporeformulaVO = new SingaporeFormulaVO();
			singaporeformulaVO.setPayer(payer);
			singaporeformulaVO.setOrgmode(orgmode);
			return singaporeformulaVO;
		} catch (ParseException e) {
			throw new BusinessException("Parse SingaporeCPF formula error, please check.");
		}
	}

}
