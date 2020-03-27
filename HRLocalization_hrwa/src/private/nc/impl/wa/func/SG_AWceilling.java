package nc.impl.wa.func;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author weiningc
 *
 */
public class SG_AWceilling extends AbstractPreExcutorFormulaParse {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6681306834481349471L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		//OW 字段
		String ow_item = arguments[0];
		//AW字段
		String aw_item = arguments[1];
		//YTD OW字段  今年已发放累计OW
		String ytd_ow_item = arguments[2];
		//YTD AW字段  今年已发放累计AW
		String ytd_aw_item = arguments[3];
		//用于计算ow ceilling和aw的固定值，因为aw的ceilling是浮动的
		UFDouble[] owandaw_ceilling = this.getOwAndAwCeilling();
		String sql = this.getFormulaForAwCeillingSql(ow_item, aw_item, owandaw_ceilling, ytd_ow_item, ytd_aw_item);
		fvo.setReplaceStr(sql);
		return fvo;  
	}
	
	private UFDouble[] getOwAndAwCeilling() {
		
		DataAccessUtils util = new DataAccessUtils();
		UFDouble[] owandaw_ceilling = new UFDouble[2];
		StringBuffer sb = new StringBuffer(); 
		sb.append(" select s.ow_ceilling, s.aw_fixvalue from wa_sgcpf_rate s where rownum = 1");
		IRowSet result = util.query(sb.toString());
		if(result.next()) {
			owandaw_ceilling[0] = result.getUFDouble(0);
			owandaw_ceilling[1] = result.getUFDouble(1);
		}
		return owandaw_ceilling;
	}

	/**
	 * =IF(B14<6000,102000-((CFOW+OW) + OW*(12-CURRENTMONTH))- CFAW,102000-((CFOW+6000) + 6000*(12-CURRENTMONTH)))
	 * @param ow_item
	 * @param aw_item
	 * @param owandaw_ceilling 
	 * @param cfaw_item 
	 * @param cfow_item 
	 * @return
	 */ 
	private String getFormulaForAwCeillingSql(String ow_item, String aw_item, UFDouble[] owandaw_ceilling, String ytd_ow_item, String ytd_aw_item) {
		//TODO  这里如果员工离职  需要计算AWCeilling， 但是不需要加上未来预计月份的收入和
		String cperiod = this.context.getCperiod();
		Integer n = 12 - Integer.valueOf(cperiod);
		StringBuffer sb = new StringBuffer();
		sb.append(" case when wa_data."  +  ow_item + "<" + owandaw_ceilling[0] );
		sb.append(" then " + owandaw_ceilling[1] + " -((wa_data." + ytd_ow_item + " +wa_data." + ow_item + ")");
		sb.append(" + wa_data." + ow_item + " *" + n + ")");
		sb.append(" - wa_data." + ytd_aw_item);
		sb.append(" else " + owandaw_ceilling[1] + " -((wa_data." + ytd_ow_item + "+" + owandaw_ceilling[0] + ") + ");
		sb.append(owandaw_ceilling[0] + " *" + n + ")");
		sb.append(" - wa_data." + ytd_aw_item + " end");
		return sb.toString();
	}


	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
