package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.impl.wa.paydata.precacu.MalaysiaTaxPreExcutor;
import nc.impl.wa.paydata.tax.MalaysiaPCBFormulaUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

public class MalaysiaPCB extends AbstractPreExcutorFormulaParse {
	
	BaseDAO dao = null;
	
	public MalaysiaPCB() {
		if(dao == null) {
			dao = new BaseDAO();
		}
	}
	
	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		MalaysiaTaxFormulaVO malaysiataxVO = MalaysiaPCBFormulaUtil.translate2FormulaVO(getFunctionVO(),formula.toString());
		IFormula excutor = new MalaysiaTaxPreExcutor();
		excutor.excute(malaysiataxVO, getContext());
	}
}

