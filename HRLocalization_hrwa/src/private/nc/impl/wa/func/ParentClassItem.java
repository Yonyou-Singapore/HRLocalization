package nc.impl.wa.func;

import org.apache.commons.lang.StringUtils;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.pub.WaLoginContext;


public class ParentClassItem extends AbstractPreExcutorFormulaParse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		String item = arguments[0];
		String sql = this.getFormulaSql(item);
		fvo.setReplaceStr(sql);
//		return super.getReplaceStr(formula);
		return fvo;
		
	}
	
	private String getFormulaSql(String item) {
		//这里如果上级方案和当前方案一致，表示没有多次发放  则取数应该为0
		String pk_parentwaclass = "BLANK";
		if(this.context.getPk_wa_class().equals(this.context.getPk_prnt_class())) {
			pk_parentwaclass = "BLANK";
		} else {
			pk_parentwaclass = this.context.getPk_prnt_class();
		}
		
		SqlBuilder sb = new SqlBuilder();
		sb.append(" (select sum(data_source." + item);
		sb.append(" ) " + item);
		sb.append(" from wa_data data_source inner join wa_cacu_data c ");
		sb.append(" on data_source.pk_psndoc = c.pk_psndoc where data_source.pk_wa_class", pk_parentwaclass);
		sb.append(" and data_source.cyear", this.context.getCyear());
		sb.append(" and data_source.cperiod", this.context.getCperiod());
		sb.append(" and data_source.pk_psndoc = wa_data.pk_psndoc");
		sb.append(" )");
		//condition 该框架在接下来的解析中自动拼接条件
		return sb.toString();
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		
		
	}
	

}
