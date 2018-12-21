package nc.impl.wa.func;

import com.ibm.db2.jcc.t4.sb;

import nc.impl.wa.classitem.ClassitemDAO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * HR本地化：EPF后台实现
 * @author Ethan Wu
 *
 */
public class MalaysiaEPF extends AbstractPreExcutorFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		// TODO: reduce the number of arguments
		String option = arguments[0];
		String epfGroup = arguments[1];
		String sql = null;
		ClassitemDAO dao = new ClassitemDAO();
		WaClassItemVO[] normalRenumItemVOs = dao.queryItemInfoVO(this.getContext().getPk_org(),  this.getContext().getPk_wa_class()
				, this.getContext().getCyear(), this.getContext().getCperiod(), " wa_classitem.my_isepf_n = 'Y' ");
		WaClassItemVO[] addRenumItemVOs = dao.queryItemInfoVO(this.getContext().getPk_org(),  this.getContext().getPk_wa_class()
				, this.getContext().getCyear(), this.getContext().getCperiod(), " wa_classitem.my_isepf_a = 'Y' ");
		
		String monthlySalary = SeaLocalFormulaUtil.getConcatString(normalRenumItemVOs);
		String bonus = SeaLocalFormulaUtil.getConcatString(addRenumItemVOs);
		
		if (option.equals("0")) {	// Handle employee contribution
			sql = getEmployeeContribution(monthlySalary, epfGroup);
		} else if (option.equals("1")) { // Handle employer contribution
			sql = getEmployerContribution(monthlySalary, bonus, epfGroup);
		}
		
		fvo.setReplaceStr(sql);
		return fvo;
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	// Calculating the Employee EPF Contribution
	private String getEmployeeContribution(String monthlySalary, String epfGroup) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		// Group A, when monthly wage is over 20000, the employee contribution is wage * 11%
		sb.append(" case when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then (" + monthlySalary + ") * 0.11 ");
		// Group B, when monthly wage is over 20000, the employee contribution is wage * 11%
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then (" + monthlySalary + ") * 0.11 ");
		// Group C, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then (" + monthlySalary + ") * 0.055 ");
		// Group D, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then (" + monthlySalary + ") * 0.055 ");
		// Else, if wage is not over 20000, strictly follow the EPF table
		sb.append(" else employee_cont end employee_cont ");
		sb.append(" from sealocal_epf_rates where " + monthlySalary + " >= lower and " + monthlySalary + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " ");
		return sb.toString();
	}
	
	// Calculating the Employer EPF 
	private String getEmployerContribution(String monthlySalary, String bonus, String epfGroup) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		// Group A, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 13%
		sb.append(" case when " + monthlySalary + " <= 5000 and " + monthlySalary + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP A' then (" + monthlySalary + ") * 0.13 ");
		// Group C, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 6.5%
		sb.append(" when " + monthlySalary + " <= 5000 and " + monthlySalary + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP C' then (" + monthlySalary + ") * 0.065 ");
		// Group A, when monthly wage is over 20000, the employer contribution is wage * 12%
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then (" + monthlySalary + ") * 0.12 ");
		// Group B, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then 5.00 ");
		// Group C, when monthly wage is over 20000, the employer contribution is wage * 6%
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then (" + monthlySalary + ") * 0.06 ");
		// Group D, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when " + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then 5.00 ");
		// Else, strictly follow the EPF table
		sb.append(" else employer_cont end employer_cont ");
		sb.append(" from sealocal_epf_rates where " + monthlySalary + " >= lower and " + monthlySalary + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " ");
		return sb.toString();
	}
	


}
