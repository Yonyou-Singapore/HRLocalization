package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
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
		String option = arguments[0];
		String monthlySalary = arguments[1];
		String bonus = arguments[2];
		String epfGroup = arguments[3];
		String sql = null;
		
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
		sb.append(" case when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then wa_data." + monthlySalary + " * 0.11 ");
		// Group B, when monthly wage is over 20000, the employee contribution is wage * 11%
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then wa_data." + monthlySalary + " * 0.11 ");
		// Group C, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then wa_data." + monthlySalary + " * 0.055 ");
		// Group D, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then wa_data." + monthlySalary + " * 0.055 ");
		// Else, if wage is not over 20000, strictly follow the EPF table
		sb.append(" else employee_cont end employee_cont ");
		sb.append(" from sealocal_epf_rates where wa_data." + monthlySalary + " >= lower and wa_data." + monthlySalary + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " ");
		return sb.toString();
	}
	
	// Calculating the Employer EPF 
	private String getEmployerContribution(String monthlySalary, String bonus, String epfGroup) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		// Group A, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 13%
		sb.append(" case when wa_data." + monthlySalary + " <= 5000 and wa_data." + monthlySalary + " + wa_data." + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP A' then wa_data." + monthlySalary + " * 0.13 ");
		// Group C, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 6.5%
		sb.append(" when wa_data." + monthlySalary + " <= 5000 and wa_data." + monthlySalary + " + wa_data." + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP C' then wa_data." + monthlySalary + " * 0.065 ");
		// Group A, when monthly wage is over 20000, the employer contribution is wage * 12%
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then wa_data." + monthlySalary + " * 0.12 ");
		// Group B, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then 5.00 ");
		// Group C, when monthly wage is over 20000, the employer contribution is wage * 6%
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then wa_data." + monthlySalary + " * 0.06 ");
		// Group D, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when wa_data." + monthlySalary + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then 5.00 ");
		// Else, strictly follow the EPF table
		sb.append(" else employer_cont end employer_cont ");
		sb.append(" from sealocal_epf_rates where wa_data." + monthlySalary + " >= lower and wa_data." + monthlySalary + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " ");
		return sb.toString();
	}

}
