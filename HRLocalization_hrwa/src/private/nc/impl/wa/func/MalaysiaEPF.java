package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.MalaysiaEPFPreExecutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
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
		
		this.excute(formula, this.getContext());
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		String option = arguments[0];
		String monthlySalary = arguments[1];
		String bonus = arguments[2];
		String epfGroup = arguments[3];
		String bonusOver5K = arguments[4];
		String sql = null;
		
		if (option.equals("0")) {	// Handle employee contribution
			sql = getEmployeeContribution(monthlySalary, bonus,epfGroup);
		} else if (option.equals("1")) { // Handle employer contribution
			sql = getEmployerContribution(monthlySalary, bonus, epfGroup, bonusOver5K);
		}
		
		fvo.setReplaceStr(sql);
		return fvo;
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		IFormula executor = new MalaysiaEPFPreExecutor();
		executor.excute(arguments, context);
	}
	
	// Calculating the Employee EPF Contribution
	private String getEmployeeContribution(String monthlySalary, String bonus, String epfGroup) {
		StringBuffer sb = new StringBuffer();
		sb.append(" (select case ");
		// Take into consideration of the voluntary contribution scenario
		// 1. Total salary is within 20000
		sb.append(" when wa_cacu_data.cacu_value <> -1 and (wa_data."+ monthlySalary + " + wa_data." + bonus + ") <= 20000 then wa_cacu_data.cacu_value / 100 * upper ");
		// 2. Total salary exceeds 20000
		sb.append(" when wa_cacu_data.cacu_value <> -1 then wa_cacu_data.cacu_value / 100 * (wa_data."+ monthlySalary + " + wa_data." + bonus + ") ");
		// Group A, when monthly wage is over 20000, the employee contribution is wage * 11%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.11 ");
		// Group B, when monthly wage is over 20000, the employee contribution is wage * 11%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.11 ");
		// Group C, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.055 ");
		// Group D, when monthly wage is over 20000, the employee contribution is wage * 5.5%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.055 ");
		// Group E, when monthly wage is over 20000, the employee contribution is wage * 0.0%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP E' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.0 ");
		// Else, if wage is not over 20000, strictly follow the EPF table
		sb.append(" else employee_cont end employee_cont ");
		sb.append(" from sealocal_epf_rates where wa_data." + monthlySalary + " + wa_data." + bonus + " >= lower and wa_data." 
				+ monthlySalary + " + wa_data." + bonus + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " )");
		return sb.toString();
	}
	
	// Calculating the Employer EPF 
	private String getEmployerContribution(String monthlySalary, String bonus, String epfGroup, String bonusOver5K) {
		StringBuffer sb = new StringBuffer();
		sb.append(" (select case ");
		if (bonusOver5K.equals("1")) {
		// 2019-05-17 Elken项目暂时取消5000校验 by Ethan Wu start
		//Group A, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 13%
		sb.append(" when wa_data." + monthlySalary + " <= 5000 and wa_data." + monthlySalary + " + wa_data." + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP A' then (wa_data." + monthlySalary + " + wa_data." + bonus + ") * 0.13 ");
		// Group C, when monthly wage is no more than 5000 and bonus + monthly wage is greater than 5000, contribution is wage * 6.5%
		sb.append(" when wa_data." + monthlySalary + " <= 5000 and " + monthlySalary + " + wa_data." + bonus 
				+ " > 5000 and sealocal_epf_rates.group_name = 'GROUP C' then (wa_data." + monthlySalary + " + wa_data." + bonus + ") * 0.065 ");
		// 2019-05-17 Elken项目暂时取消5000校验 by Ethan Wu end
		}
		// Group A, when monthly wage is over 20000, the employer contribution is wage * 12%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP A' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.12 ");
		// Group B, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP B' then 5.00 ");
		// Group C, when monthly wage is over 20000, the employer contribution is wage * 6%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP C' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.06 ");
		// Group D, when monthly wage is over 20000, the employer contribution is RM5.00
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP D' then 5.00 ");
		// Group E, when monthly wage is over 20000, the employer contribution is wage * 4%
		sb.append(" when wa_data." + monthlySalary + " + wa_data." + bonus + " > 20000 and sealocal_epf_rates.group_name = 'GROUP E' then (wa_data." 
				+ monthlySalary + " + wa_data." + bonus + ") * 0.04 ");
		// Else, strictly follow the EPF table
		sb.append(" else employer_cont end employer_cont ");
		sb.append(" from sealocal_epf_rates where wa_data." + monthlySalary + " + wa_data." + bonus + " >= lower and wa_data." 
				+ monthlySalary + " + wa_data." + bonus + " <= upper ");
		sb.append(" and group_name = " + epfGroup + " )");
		return sb.toString();
	}
	
}
