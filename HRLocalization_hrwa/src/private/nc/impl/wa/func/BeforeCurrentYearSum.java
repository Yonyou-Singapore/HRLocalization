package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 本年度本期以前函数实现
 * @author weiningc
 *
 */
public class BeforeCurrentYearSum extends  AbstractPreExcutorFormulaParse {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6681306834481349471L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException { 
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		String item = arguments[0];
		String[] startandend_period = this.getBeforCurrentYearPeriod();
		String sql = this.getFormulaSql(item, startandend_period);
		fvo.setReplaceStr(sql);
		return fvo;
	}
	
	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param item
	 * @param startandend_period
	 * @return
	 */
	private String getFormulaSql(String itemid, String[] startandend_period) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select sum(data_source." + itemid + " )"); // 1
		sqlBuffer.append("  from wa_data data_source ");
		sqlBuffer.append(" where data_source.pk_wa_class = '" + this.getContext().getPk_wa_class() + "' ");
		sqlBuffer.append("   and (data_source.cyear || data_source.cperiod) >= '" + startandend_period[0] + "' ");
		sqlBuffer.append("   and (data_source.cyear || data_source.cperiod) <= '" + startandend_period[1] + "' ");
		sqlBuffer.append("   and data_source.pk_psndoc = wa_data.pk_psndoc and  data_source.stopflag = 'N' ");
		return sqlBuffer.toString();
	}

	
	/**
	 * 取本年度本期以前的 period
	 * @param type 
	 * @param endperiod 
	 * @param begingperiod 
	 */
	private String[] getBeforCurrentYearPeriod() {
		
		String endperiod = this.getContext().getCperiod();
		//若是一月份， 则直接返回
		if("01".equals(endperiod)) {
			return null;
		}
		String[] arr_period = new String[2];
		String year = this.getContext().getCyear();
		arr_period[0] = year + "01";
		int end = Integer.valueOf(endperiod) - 1;
		if(end <= 9) {
			endperiod = 0 + String.valueOf(end);
		} else {
			endperiod = String.valueOf(end);
		}
		arr_period[1] = year + endperiod;
		return arr_period;
		
	}

}