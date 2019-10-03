package nc.impl.wa.paydata.precacu.sg;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public class SingaporeTaxPreExcutor extends AbstractFormulaExecutor {
	
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof SingaporeFormulaVO) {
			// 传递扣税信息到中间表
			SingaporeFormulaVO taxFormulaVO = (SingaporeFormulaVO) inTaxFormulaVO;
			ISingaporeTaxInfPreProcess  taxInfPreProcess = this.createSingaporeTaxInfPreProcess(taxFormulaVO);
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}

	
	/**
	 * 普通发放或者其他类型发放
	 */
	public ISingaporeTaxInfPreProcess createSingaporeTaxInfPreProcess(SingaporeFormulaVO taxFormulaVO) {
		
		return new SG_TaxInfPreProcess();
	}
}
