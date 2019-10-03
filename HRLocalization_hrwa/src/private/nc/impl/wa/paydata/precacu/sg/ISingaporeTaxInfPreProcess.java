package nc.impl.wa.paydata.precacu.sg;

import java.util.concurrent.ConcurrentHashMap;

import nc.bs.dao.DAOException;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public interface ISingaporeTaxInfPreProcess {
	public abstract void transferTaxCacuData(SingaporeFormulaVO singaporeformulaVO,
			WaLoginContext context) throws DAOException, BusinessException;	
}
