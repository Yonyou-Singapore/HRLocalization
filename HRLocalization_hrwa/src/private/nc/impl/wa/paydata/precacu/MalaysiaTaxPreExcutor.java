package nc.impl.wa.paydata.precacu;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.impl.wa.paydata.tax.FixTaxRateRemitting;
import nc.impl.wa.paydata.tax.FixTaxRateWithholding;
import nc.impl.wa.paydata.tax.IMalaysiaPCBTaxInfPreProcess;
import nc.impl.wa.paydata.tax.ITaxInfPreProcess;
import nc.impl.wa.paydata.tax.ITaxRateProcess;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.impl.wa.paydata.tax.TaxTableTaxRateRemitting;
import nc.impl.wa.paydata.tax.TaxTableTaxRateWithHolding;
import nc.impl.wa.paydata.tax.WorkTaxRateRemitting;
import nc.impl.wa.paydata.tax.WorkTaxRateWithholding;
import nc.impl.wa.taxrate.TaxQueryServiceImpl;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.util.DBConsts;
import nc.pubitf.uapbd.CurrencyRateUtil;
import nc.vo.bd.currinfo.CurrinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.PsnTaxTypeVO;
import nc.vo.wa.payfile.Taxtype;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.taxrate.TaxTableTypeEnum;

/**
 * PCB函数处理
 *
 * @author: zhangg
 * @date: 2010-4-21 下午01:12:26
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class MalaysiaTaxPreExcutor extends AbstractFormulaExecutor {
	
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof MalaysiaTaxFormulaVO) {
			// 传递扣税信息到中间表
			MalaysiaTaxFormulaVO taxFormulaVO = (MalaysiaTaxFormulaVO) inTaxFormulaVO;
			IMalaysiaPCBTaxInfPreProcess  taxInfPreProcess = this.createMYTaxInfPreProcess(taxFormulaVO.getClass_wagetype(), context);
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}


	private  String getParentPkClass(WaLoginContext context,TaxFormulaVO  taxFormulaVO ){
		//补发与 普通薪资计算不一样。补发进行计算时，需要使用父方案的PK
		String pk_wa_class = "";
		if(taxFormulaVO.getClass_type().equals(TaxFormulaVO.CLASS_TYPE_REDATA)){
			pk_wa_class  = WaLoginVOHelper.getParentClassPK(context.getWaLoginVO());
		}else{
			pk_wa_class = context.getPk_wa_class();
		}
		return pk_wa_class;

	}
	
	/**
	 * 普通发放或者其他类型发放
	 * @param context 
	 */
	public IMalaysiaPCBTaxInfPreProcess createMYTaxInfPreProcess(String type, WaLoginContext context) {
		//支持多次方法 需要判断是否多次方法 如果是 走多次发放的逻辑,PCB计算中用到的薪资项目取和值
		Boolean ismutiple = false;
		if(context.getWaLoginVO().getBatch() != null && context.getWaLoginVO().getBatch() > 1) {
			ismutiple = true;
		}
		if(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL.equals(type) && !ismutiple){
			//普通计税
			return new MY_NormalPCBTaxInfPreProcess();
		}else if (MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR.equals(type)){
			//年终奖
			return new MY_AwardPCBTaxInfPreProcess();
		}else if(ismutiple){
			//多次发放
			return new MY_MutiPCBTaxInfPreProcess();
		}
		return null;
	}
}