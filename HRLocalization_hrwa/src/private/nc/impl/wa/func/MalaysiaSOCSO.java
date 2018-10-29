package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

public class MalaysiaSOCSO extends AbstractWAFormulaParse {
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setReplaceStr("Test Malaysia EPF String");
		return fvo;
	}
}
