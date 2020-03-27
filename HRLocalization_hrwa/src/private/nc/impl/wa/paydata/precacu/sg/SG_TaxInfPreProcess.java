package nc.impl.wa.paydata.precacu.sg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.vo.am.common.util.SqlBuilder;
import nc.vo.am.common.util.UFDoubleUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaset.VarryVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.item.SingaporeVO_CPF;
import nc.vo.wa.item.SingaporeVO_CPF_Rate;
import nc.vo.wa.pub.WaLoginContext;

public class SG_TaxInfPreProcess extends AbstractFormulaExecutor
		implements ISingaporeTaxInfPreProcess {

	private BaseDAO dao = null;
//	private FormulaParse formulaparse = null;

	public SG_TaxInfPreProcess() {
		if (dao == null) {
			dao = new BaseDAO();
//			formulaparse = new FormulaParse();
		}
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub

	}  

	@Override
	public void transferTaxCacuData(SingaporeFormulaVO singaporeFormulaVO,
			WaLoginContext context) throws DAOException, BusinessException {
		// 1.查询已知的薪资项目
		List<SingaporeVO_CPF> results = this.queryCPFKnownItems(
				singaporeFormulaVO, context);

		// 2.逻辑计算Singapore CPF, 包括OW Employee rate等等
		ConcurrentHashMap<String, UFDouble> cacuOtherCPFItems = this
				.cacuOtherCPFItems(results.toArray(new SingaporeVO_CPF[0]),
						context, singaporeFormulaVO);

		// 3.更新wa_cacu_data, cacu_calue，考虑精度
		this.updateCacudata(cacuOtherCPFItems);

	}

	private void updateCacudata(
			ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) throws BusinessException {//创建临时表
		this.creatTempTable(cacuOtherPCBItem);
		
		 String sql = " update wa_cacu_data w"
			+ " set (w.cacu_value) ="
			+ " (select p.cpf from tmp_wa_sgcpf p where w.pk_cacu_data = p.pk_cacu_data)"
			+ " where exists (select 1 from tmp_wa_sgcpf)";
		 super.executeSQLs(sql);
	}
	
	private void creatTempTable(ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) {
		Connection con = null;
		String tempTable = null;
		// 创建临时表
		try {
			con = ConnectionFactory.getConnection();
			String tableName = "tmp_wa_sgcpf";
			String columns = "pk_cacu_data varchar2(20), cpf number(28,8)";
			tempTable = new TempTable().createTempTable(con, tableName, columns, "pk_cacu_data");
//			((CrossDBConnection) con).setAddTimeStamp(false);
			prepareTempTable(tempTable, cacuOtherPCBItem);
		} catch (SQLException e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
	}
	
	private void prepareTempTable(String tempTable,
			ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) {
		String sql = "insert into " + tempTable + " (pk_cacu_data,cpf) values (?,?)";
		PreparedStatement stm = null;
		Connection conn = null;
		try {
			conn = ConnectionFactory.getConnection();
			((CrossDBConnection) conn).setAddTimeStamp(false);
			stm = conn.prepareStatement(sql);
			for (Map.Entry<String, UFDouble> entry : cacuOtherPCBItem.entrySet()) {
				stm.setString(1, entry.getKey());
				stm.setDouble(2, entry.getValue().doubleValue());
				stm.addBatch();
			}
			stm.executeBatch();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}
	}

	private ConcurrentHashMap<String, UFDouble> cacuOtherCPFItems(
			SingaporeVO_CPF[] results, WaLoginContext context, SingaporeFormulaVO singaporeFormulaVO) throws BusinessException {
		
		String period = context.getCperiod();
		String year = context.getCyear();
		FormulaParse formulaparse = new FormulaParse();
		ConcurrentHashMap<String, UFDouble> wacacumap = new ConcurrentHashMap<String, UFDouble>();
		//税率对照表
		SingaporeVO_CPF_Rate[] raterange = this.queryCPFRateRanage(year, singaporeFormulaVO.getOrgmode());
		Logger.error("===Start cal Singapore CPF===");	
		for(SingaporeVO_CPF vo : results) {
			//1 员工年龄
			if(vo.getBirthdate() == null) {
				throw new BusinessException(vo.getPsncode() + " :Birthdate is blank.");
			}
			int age = this.getAge(vo.getBirthdate(), year, period);
			Logger.error("===Singapore CPF===" + vo.getPsncode() + " ,Birthdate:" + vo.getBirthdate() + " ,year:" + year + " ,period:" + period);	
			//prcode
			String prcode = this.getPRCode(vo, year, period);
			if(StringUtils.isBlank(prcode)) {
				continue;
			}
			this.calPrivateEmployeecpf(vo, wacacumap, raterange, age, prcode, singaporeFormulaVO.getPayer(), formulaparse);
			
		}
		return wacacumap;
	}


	private String getPRCode(SingaporeVO_CPF vo, String year, String period) {
		//PR第几年  若是公民, 直接是第三年 公民只有G/G  并且直接是第三年 PR03
		//公民和PR使用的统一标识 有颜色区分 Citizen 为粉红色 PR为蓝色
		if(SingaporeFormulaVO.SINGAPORE_CITIZEN.equals(vo.getIdtype())/*Singapore Citizen*/) {
			Logger.error("===Singapore CPF=== NRIC-PINK, PR03");
			return SingaporeFormulaVO.THIRDPR;
		}
		if(vo.getSgpr() == null || !vo.getSgpr().booleanValue() || vo.getPr_approvaldate() == null
				|| vo.getIdtype() == null) {
			Logger.error("===Singapore CPF=== 非PR, 不计算CPF. ");
			return null;
		}
		int prage = this.getPRAge(vo.getPr_approvaldate(), year, period);
		String prcode = "";
		if(prage > 2 && SingaporeFormulaVO.FG_ContributionMode.equals(vo.getFullemployercpf())) {
			prcode = SingaporeFormulaVO.SENCONDPR;
		} else if(prage > 3 && SingaporeFormulaVO.GG_ContributionMode.equals(vo.getFullemployercpf())) {
			prcode = SingaporeFormulaVO.THIRDPR;
		} else {
			prcode = SingaporeFormulaVO.PRCODE + prage;
		}
		return prcode;
	}
	
	/**
	 * 年龄
	 * @param birthdate
	 * @param year
	 * @param period
	 * @return
	 */
	private int getAge(UFDate birthdate, String year, String period) {
		int agemouth = Integer.valueOf(period) - birthdate.getMonth();
		int ageyear = Integer.valueOf(year) - birthdate.getYear();
		if(agemouth > 0) {
			ageyear = ageyear + 1;
		} 
		return ageyear;
	}
	
	/**
	 * PR Age
	 * @param birthdate
	 * @param year
	 * @param period
	 * @return
	 */
	private int getPRAge(UFDate approvedate, String year, String period) {
		int agemouth = Integer.valueOf(period) - approvedate.getMonth();
		int ageyear = Integer.valueOf(year) - approvedate.getYear();
		if(agemouth > 0) {
			ageyear = ageyear + 1;
		} 
		return ageyear;
	}
	
	/**
	 * 
	 * @param vo
	 * @param wacacumap
	 * @param raterange
	 * @param age
	 * @param prcode
	 * @param payer  总缴纳部分 or 雇员缴纳部分 0--总缴纳  1--雇员缴纳
	 * @param formulaparse 
	 */
	private void calPrivateEmployeecpf(SingaporeVO_CPF vo,
			ConcurrentHashMap<String, UFDouble> wacacumap,
			SingaporeVO_CPF_Rate[] raterange, Integer age, String prcode, Integer payer, FormulaParse formulaparse) {
		//根据PR年份和年龄匹配相应的区间
		String formula = this.matchCPFEmployee_Raterange(raterange, age, prcode, vo, payer);
		//解析公式
		if(StringUtils.isBlank(formula)) {
			Logger.error("===Singapore CPF===Psn: " + vo.getPsncode() +  " ,TW:" + vo.getTw() +" ,Psn age: " + age + " ,PRCODE: " + prcode + " ,matched formula: " + formula);
			return;
		}
		Logger.error("===Singapore CPF===Psn: " + vo.getPsncode() +  " ,TW:" + vo.getTw() +" ,Psn age: " + age + " ,PRCODE: " + prcode + " ,matched formula: " + formula);
		UFDouble ow_ceilling = raterange[0].getOw_ceilling();
		//若OW 超过 cpfceilling时， 取cpfceilling
		UFDouble ow = vo.getOw();
		if(UFDoubleUtils.isGreaterThan(ow, ow_ceilling)) {
			ow = ow_ceilling;
			Logger.info("===Singapore CPF===OW great than cpfceilling, take ceilling: " + ow_ceilling);
		}
		UFDouble aw = vo.getAw();
		UFDouble aw_ceilling = vo.getAwceilling() == null ? UFDouble.ZERO_DBL : vo.getAwceilling();
		if(UFDoubleUtils.isGreaterThan(aw, aw_ceilling)) {
			aw = aw_ceilling;
		}
		//替换公式中的OW, AW, TW, NPE
		formula = this.replaceFormula(vo, formula, ow, aw);
		if(SingaporeFormulaVO.NONECPF.equals(formula)) {
			wacacumap.put(vo.getPk_cacu_data(), UFDouble.ZERO_DBL);
			return;
		}
		formulaparse.setExpress(vo.getPk_cacu_data() + "->" + formula);
		String value = formulaparse.getValue(); //单个公式的数学公式的结果
		//Employee是舍位 如Employer的cpf是进位,如5.11 ==>6.00， Total和Employer是进位如5.88 ==>5.00
		UFDouble cpfvalue = this.getCpfValueWithRounding(new UFDouble(value), payer);
		wacacumap.put(vo.getPk_cacu_data(), cpfvalue);
	}
	
	/**
	 * * @param payer 总缴纳部分 or 雇员缴纳部分 0--总缴纳  1--雇员缴纳
	 * 2--OW Employee CPF Rate 3--OW Total CPF Rate   
	 * 4--AW Employee CPF Rate   5--AW Total Employee CPF Rate
	 * Employee是舍位 如Employer的cpf是进位,如5.11 ==>6.00， Total和Employer是进位如5.88 ==>5.00
	 * @param ufDouble
	 * @param payer
	 * @return
	 */
	private UFDouble getCpfValueWithRounding(UFDouble ufDouble, Integer payer) {
		UFDouble cpfvalue = UFDouble.ZERO_DBL;
		if(payer == 0 || payer == 3 || payer == 5) {
			cpfvalue = ufDouble.setScale(0, UFDouble.ROUND_UP);
		} if(payer == 1 || payer ==2 || payer == 4) {
			cpfvalue = ufDouble.setScale(0, UFDouble.ROUND_DOWN);
		}
		return cpfvalue;
	}

	/**
	 * 
	 * @param vo
	 * @param formula
	 * @param ow 
	 * @return
	 */
	private String replaceFormula(SingaporeVO_CPF vo, String formula, UFDouble ow, UFDouble aw) {
		formula = StringUtils.replace(formula, SingaporeFormulaVO.OW, ow == null ? UFDouble.ZERO_DBL.toString() : ow.toString());
		formula = StringUtils.replace(formula, SingaporeFormulaVO.AW, vo.getAw() == null ? UFDouble.ZERO_DBL.toString() : vo.getAw().toString());
		formula = StringUtils.replace(formula, SingaporeFormulaVO.TW, vo.getTw() == null ? UFDouble.ZERO_DBL.toString() : vo.getTw().toString());
		formula = StringUtils.replace(formula, SingaporeFormulaVO.NPE, vo.getNpe() == null ? UFDouble.ZERO_DBL.toString() : vo.getNpe().toString());
		return formula;
	}

	/**
	 * 获取CPF 计算公式
	 * @param raterange
	 * @param age
	 * @param prcode
	 * @param payer 总缴纳部分 or 雇员缴纳部分 0--总缴纳  1--雇员缴纳
	 * 2--OW Employee CPF Rate 3--OW Total CPF Rate   
	 * 4--AW Employee CPF Rate   5--AW Total Employee CPF Rate
	 * @param fullemployercpf
	 * @param totalwage 
	 * @return
	 */
	private String matchCPFEmployee_Raterange(SingaporeVO_CPF_Rate[] raterange,
			Integer age, String prcode, SingaporeVO_CPF vo, Integer payer) {
		for(SingaporeVO_CPF_Rate rate : raterange) {
			if(payer == 0 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getTotalcpf_formula();
			}
			else if(payer == 1 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getEmployeecpf_formula();
			}
			else if(payer == 2 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getOwee_formla();
			}
			else if(payer == 3 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getOwtotal_formula();
			}
			else if(payer == 4 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getAwee_formula();
			}
			else if(payer == 5 && rate.getContribution_type().equals(vo.getFullemployercpf()) && prcode.equals(rate.getCpfcode()) && age >= rate.getAge_lower() && age <= rate.getAge_upper()
					&& (UFDoubleUtils.isGreaterThan(vo.getTw(), rate.getTotalwage_lower()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_lower())) 
					&& (UFDoubleUtils.isLessThan(vo.getTw(), rate.getTotalwage_upper()) || UFDoubleUtils.isEqual(vo.getTw(), rate.getTotalwage_upper()))) {
				return rate.getAwtotal_formula();
			}
		}
		return null;
	}


	private SingaporeVO_CPF_Rate[] queryCPFRateRanage(String year, Integer orgmode) throws DAOException {
		SqlBuilder sb = new SqlBuilder();
		//私人公司还是政府单位
		sb.append(" orgtype", orgmode == 0 ? SingaporeFormulaVO.PRIVATE_COMPANY : SingaporeFormulaVO.PUBLIC_COMPANY);
		sb.append(" and cyear", year);
		//有效时间应在CPF rate有效期内
		UFDate currentdate = new UFDate();
		sb.append(" and '" + currentdate.asEnd().toString() + "' between ");
		sb.append(" effective_date and expiration_date");
		//test
//		sb.append(" and contribution_type = 'GG' and 750 between totalwage_lower and totalwage_upper and cpfcode = 'PR03'");
		//end
		List<SingaporeVO_CPF_Rate> results = (List<SingaporeVO_CPF_Rate>) dao.retrieveByClause(SingaporeVO_CPF_Rate.class, 
				sb.toString());
		if(results == null || results.size() == 0) {
			Logger.error("CPF rate is blank, condition is " + sb );
		}
		return results.toArray(new SingaporeVO_CPF_Rate[0]);
	}

	private List<SingaporeVO_CPF> queryCPFKnownItems(
			SingaporeFormulaVO singaporeFormulaVO, WaLoginContext context) {
			List<SingaporeVO_CPF> results = null;
			String condition = " pk_wa_class = ?";
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(context.getPk_wa_class());
			try {
				results = (List<SingaporeVO_CPF>) dao.retrieveByClause(SingaporeVO_CPF.class, condition, parameter);
			} catch (Exception e) {
				ExceptionUtils.wrapException(e.getMessage(), e);
			}
			return results;}

}
