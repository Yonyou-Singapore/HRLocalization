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

	
	int convMode = 0;
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof MalaysiaTaxFormulaVO) {
			// 传递扣税信息到中间表
			MalaysiaTaxFormulaVO taxFormulaVO = (MalaysiaTaxFormulaVO) inTaxFormulaVO;
			IMalaysiaPCBTaxInfPreProcess  taxInfPreProcess = this.createMYTaxInfPreProcess(taxFormulaVO.getClass_wagetype());
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}

	
	/**
	 * 折算模式：0 元币种 × 汇率 ＝ 目的币种
	 *          1 元币种 / 汇率 ＝ 目的币种
	 * @param src_currency_pk
	 * @param dest_currency_pk
	 * @return
	 * @throws BusinessException 
	 */
	public  void setCurrenyConvmode(WaLoginContext context) throws BusinessException{
		
		String src_currency_pk = context.getWaLoginVO().getCurrid();
		String dest_currency_pk = context.getWaLoginVO().getTaxcurrid();		
		if(src_currency_pk.equals(dest_currency_pk)){
			convMode = 0;
			return ;
		}
		CurrencyRateUtil currencyRateUtil = CurrencyRateUtil.getInstanceByOrg(context.getPk_org());
		CurrinfoVO currinfoVO  = currencyRateUtil.getCurrinfoVO(src_currency_pk, dest_currency_pk);		
		convMode =  currinfoVO.getConvmode();
	}
	
	public int getConvMode() {
		return convMode;
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

	private void currencyrateBeforeCaculate(String pk_wa_class,String userid ) throws BusinessException{
		
		
        //注意转换模式。
		int convmode = getConvMode();
		String convSign =null;
		if(convmode==1){
			convSign = "/";
		}else{
			convSign = "*";
		}
		
		StringBuilder sbd = new StringBuilder();
		//20150917 shenliangc 解决四川烟草DB2字段除法结果小数位丢失问题。NCdp205497893，参与除法的字段要强转数据类型cast(tax_base as double)
		if(DBConsts.DB2 == DataSourceCenter.getInstance().getDatabaseType()){
			sbd.append("  update  wa_cacu_data set  tax_base = cast(tax_base as double)"+ convSign+"currencyrate, ");
			sbd.append(" taxed = cast(taxed as double)"+ convSign+"currencyrate, ");
			sbd.append("  taxedBase = cast(taxedBase as double)"+ convSign+"currencyrate, ");
			sbd.append(" retaxed = cast(retaxed as double)"+ convSign+"currencyrate, ");
			sbd.append(" redata = cast(redata as double)"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = cast(redataLasttaxBase as double)"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttax = cast(redataLasttax as double)"+ convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}else{
			sbd.append("  update  wa_cacu_data set  tax_base = tax_base"+ convSign+"currencyrate, ");
			sbd.append(" taxed = taxed"+ convSign+"currencyrate, ");
			sbd.append("  taxedBase = taxedBase"+ convSign+"currencyrate, ");
			sbd.append(" retaxed = retaxed"+ convSign+"currencyrate, ");
			sbd.append(" redata = redata"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = redataLasttaxBase"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttax = redataLasttax"+ convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}

		executeSQLs(sbd.toString());
	}


     private void currencyrateAfterCaculate(String pk_wa_class,String userid ) throws BusinessException{

    	 
    	//注意转换模式。
 		int convmode = getConvMode();
 		String convSign =null;
 		if(convmode==1){
 			convSign = "*";
 		}else{
 			convSign = "/";
 		}
 		
 		
		StringBuilder sbd = new StringBuilder();
		//20150917 shenliangc 解决四川烟草DB2字段除法结果小数位丢失问题。NCdp205497893，参与除法的字段要强转数据类型cast(tax_base as double)
		if(DBConsts.DB2 == DataSourceCenter.getInstance().getDatabaseType()){
			sbd.append("  update  wa_cacu_data set  tax_base = cast(tax_base as double)"+convSign+"currencyrate, ");
			sbd.append(" taxed = cast(taxed as double)"+convSign+"currencyrate, ");
			sbd.append("  taxedBase = cast(taxedBase as double)"+convSign+"currencyrate, ");
			sbd.append(" retaxed = cast(retaxed as double)"+convSign+"currencyrate, ");
			sbd.append(" redata = cast(redata as double)"+convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = cast(redataLasttaxBase as double)"+convSign+"currencyrate, ");
			sbd.append(" redataLasttax =cast(redataLasttax as double)"+convSign+"currencyrate, ");
			//扣税要折算回来
			sbd.append(" cacu_value = cast(cacu_value as double)"+convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}else{
			sbd.append("  update  wa_cacu_data set  tax_base = tax_base"+convSign+"currencyrate, ");
			sbd.append(" taxed = taxed"+convSign+"currencyrate, ");
			sbd.append("  taxedBase = taxedBase"+convSign+"currencyrate, ");
			sbd.append(" retaxed = retaxed"+convSign+"currencyrate, ");
			sbd.append(" redata = redata"+convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = redataLasttaxBase"+convSign+"currencyrate, ");
			sbd.append(" redataLasttax = redataLasttax"+convSign+"currencyrate, ");
			//扣税要折算回来
			sbd.append(" cacu_value = cacu_value"+convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}

		executeSQLs(sbd.toString());
	}


	/**
	 * 根据根据税率表的不同和扣税方式的不同，创建不同的扣税解析器
	 * 应扣税={((应纳税所得额×税率-速算扣除数) * ( 1- 减税比例))-已扣税}/汇率
	 *
	 *
	 * 计算减免税和汇率
	 *
	 * 计算cacu_value 时 注意精度与舍为方式。
	 *
	 * @author zhangg on 2010-6-4
	 * @param context
	 * @throws BusinessException
	 */
		private StringBuffer getUpdateCurrencyRate(String pk_wa_class,String userid,WaClassItemVO taxItemvo) throws BusinessException{
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data ");                  //   1

			if(taxItemvo!=null){
				sqlBuffer.append("   set cacu_value = " +getRoundTaxSQL(taxItemvo) );  //   2
			}else{
				sqlBuffer.append("   set cacu_value = " +getTaxSQL() );  //   2
			}

			sqlBuffer.append("  where wa_cacu_data.pk_wa_class = '" + pk_wa_class + "' ");
			sqlBuffer.append("         and wa_cacu_data.creator = '" +userid + "'");
			return sqlBuffer;
		}


	    private String 	getTaxSQL(){
			return "( "+ getThisTimeTaxSQL() +"-taxed ) ";
		}

	  private String   getThisTimeTaxSQL(){
		return " (( taxable_income  * taxrate * 0.01  - nquickdebuct) * ((100 - derateptg) / 100))";
	    }
		private String getRoundTaxSQL(WaClassItemVO itemVO) throws BusinessException {
			   String sql = "";

				int digits = itemVO.getIflddecimal();
				/**
				 * 四舍五入， 进位， 舍位 薪资提供的进行方式同普通意义上的进位不同， 需求如下：
				 *
				 * 在小数位数后增加进位方式属性,下拉选择,系统提供进位 舍位和四舍五入三种舍位方式
				 * 进位，根据用户设定的小数位数,当计算结果中小数位数的的后一位不等于0时小数位数的最后一位加1
				 * 舍位，根据用户设定的小数位数,不论计算结果中小数位数的后一位是否等于0,均直接舍弃
				 * 四舍五入,根据用户设定的小数位数,计算结果中小数位数的后一位按照四舍五入的规则进行进位或舍位计算
				 */

				if (itemVO.getRound_type() == null || itemVO.getRound_type().intValue() == 0) {// 四舍五入
					 sql = "  ( round("+ getThisTimeTaxSQL() + ", " + digits + " ) - taxed)  " ;
				} else {
					//本次扣税肯定会>=0
					UFDouble f = UFDouble.ZERO_DBL;
					if (itemVO.getRound_type().intValue() == 1) {// 进位
						f = new UFDouble(0.4f);
					} else if (itemVO.getRound_type().intValue() == 2) {// 舍位操作
						f = new UFDouble(-0.5f);
					} else {// 默认四舍五入
						f = UFDouble.ZERO_DBL;
					}

					f = f.multiply(Math.pow(0.1, digits));

					 sql = "  ( round("+ getThisTimeTaxSQL() +"+("+ f + "), " + digits + " ) - taxed)   " ;
				}

				return sql;

		}

	/**
	 *
	 * @author zhangg on 2010-4-21
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	private PsnTaxTypeVO[] getPsnTaxTypeVOs(String pk_wa_class,String userid) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct wa_cacu_data.taxtype, "); // 1
		sqlBuffer.append("                wa_cacu_data.taxtableid, "); // 2
		sqlBuffer.append("                wa_cacu_data.isndebuct ,wa_cacu_data.isderate ,wa_cacu_data.derateptg "); // 3
		sqlBuffer.append("  from wa_cacu_data ");
		sqlBuffer.append("  where wa_cacu_data.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("         and wa_cacu_data.creator = '" +userid + "'  and  wa_cacu_data.taxtableid <> '~' ");

		PsnTaxTypeVO[] psnTaxTypeVOs = executeQueryVOs(sqlBuffer.toString(), PsnTaxTypeVO.class);
		TaxQueryServiceImpl taxQueryServiceImpl = new TaxQueryServiceImpl();
		for (PsnTaxTypeVO psnTaxTypeVO : psnTaxTypeVOs) {
			psnTaxTypeVO.setTaxBaseVO(taxQueryServiceImpl.queryByPk(psnTaxTypeVO.getTaxtableid()));
		}
		return psnTaxTypeVOs;
	}


	/**
	 * 根据税率表的不同和扣税方式的不同，创建不同的扣税解析器
	 *
	 * @author zhangg on 2010-4-9
	 * @param psnTaxTypeVO
	 * @return ITaxRateProcess
	 */
	private ITaxRateProcess getRateProcess(PsnTaxTypeVO psnTaxTypeVO) {
		ITaxRateProcess taxRateProcess = null;

		if (psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.FIXTAX.value())) {// 固定税率表

			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// 代扣税
				taxRateProcess = new FixTaxRateWithholding();

			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// 代付税
				taxRateProcess = new FixTaxRateRemitting();
			}
		} else if (psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.TAXTABLE.value())) {// 变动税率表
			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// 代扣税
				taxRateProcess = new TaxTableTaxRateWithHolding();
			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// 代付税
				taxRateProcess = new TaxTableTaxRateRemitting();
			}
		}else if(psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.WORKTAX.value())){
			//劳务税率表
			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// 代扣税
				taxRateProcess = new WorkTaxRateWithholding();
			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// 代付税
				taxRateProcess = new WorkTaxRateRemitting();
			}
		}
		
		return taxRateProcess;
	}
	
	/**
	 * 普通发放或者其他类型发放
	 */
	public IMalaysiaPCBTaxInfPreProcess createMYTaxInfPreProcess(String type) {
		if(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL.equals(type)){
			//普通计税
			return new MY_NormalPCBTaxInfPreProcess();
		}else if (MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR.equals(type)){
			//年终奖
			return new MY_AwardPCBTaxInfPreProcess();
		}else{
			//补发计税
			return new MY_MutiPCBTaxInfPreProcess();
		}
	}
}