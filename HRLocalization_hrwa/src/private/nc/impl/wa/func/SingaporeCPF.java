package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.sg.SingaporeFormulaVO;
import nc.impl.wa.paydata.precacu.sg.SingaporeTaxPreExcutor;
import nc.impl.wa.paydata.tax.SingaporePayRollFormulaUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author weiningc
 *
 */
public class SingaporeCPF extends AbstractPreExcutorFormulaParse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		SingaporeFormulaVO singaporetaxVO = SingaporePayRollFormulaUtil.translate2FormulaVO(getFunctionVO(),formula.toString());
		IFormula excutor = new SingaporeTaxPreExcutor();
		excutor.excute(singaporetaxVO, getContext());
	}


}
