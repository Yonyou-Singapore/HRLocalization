package nc.impl.wa.paydata.tax;

import nc.bs.dao.DAOException;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 将算税的相关信息（ cacu_value, taxtableid, tax_base, taxtype, isndebuct, isderate, derateptg） 传送到中间表
 * Malaysia PCB函数--扣税函数
 * @author xuanlt
 *
 */
public interface IMalaysiaPCBTaxInfPreProcess {
	
	public abstract void transferTaxCacuData(MalaysiaTaxFormulaVO malaysiaFormulaVO,
			WaLoginContext context) throws DAOException, BusinessException;	

}
