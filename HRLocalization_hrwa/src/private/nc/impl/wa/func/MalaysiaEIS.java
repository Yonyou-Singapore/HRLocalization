package nc.impl.wa.func;

import nc.impl.wa.classitem.ClassitemDAO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

public class MalaysiaEIS extends AbstractPreExcutorFormulaParse {
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		String option = arguments[0];
		String sql = null;
		ClassitemDAO dao = new ClassitemDAO();
		WaClassItemVO[] monthlySalaryItemVOs = dao.queryItemInfoVO(this.getContext().getPk_org(),  this.getContext().getPk_wa_class()
				, this.getContext().getCyear(), this.getContext().getCperiod(), " wa_classitem.my_iseis = 'Y' ");
		String monthlySalary = SeaLocalFormulaUtil.getConcatString(monthlySalaryItemVOs);
		
		if (option.equals("0")) {
			sql = getEmployeeContribution(monthlySalary);
		} else if (option.equals("1")) {
			sql = getEmployerContribution(monthlySalary);
		} else {
			fvo.setReplaceStr("null");
		}
		fvo.setReplaceStr(sql);
		return fvo;
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	private String getEmployeeContribution(String monthlySalary) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select employee_cont from my_eis_rates ");
		sb.append(" where " + monthlySalary + " > lower and ");
		sb.append(monthlySalary + " <= upper ");
		return sb.toString();
	}
	
	private String getEmployerContribution(String monthlySalary) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select employer_cont from my_eis_rates ");
		sb.append(" where " + monthlySalary + " > lower and ");
		sb.append(monthlySalary + " <= upper ");
		return sb.toString();
	}
}
