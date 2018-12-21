package nc.impl.wa.paydata.precacu;

import nc.bs.dao.DAOException;
import nc.impl.wa.paydata.tax.IMalaysiaPCBTaxInfPreProcess;
import nc.impl.wa.paydata.tax.ITaxInfPreProcess;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public class MY_MutiPCBTaxInfPreProcess implements IMalaysiaPCBTaxInfPreProcess, ITaxInfPreProcess {

	@Override
	public void transferTaxCacuData(MalaysiaTaxFormulaVO malaysiaFormulaVO,
			WaLoginContext context) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferTaxCacuData(TaxFormulaVO taxFormulaVO,
			WaLoginContext context) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
