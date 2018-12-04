package nc.impl.wa.paydata.precacu;

/**
 * PCB Group 计税
 */
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.impl.wa.paydata.tax.IMalaysiaPCBTaxInfPreProcess;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.am.common.util.UFDoubleUtils;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.defdoc.DefdoclistVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.item.MalaysiaVO_PCB;
import nc.vo.wa.item.MalaysiaVO_PCB_Rate;
import nc.vo.wa.item.MalaysiaVO_PCB_para;
import nc.vo.wa.item.UFDoubleScaleUtils;
import nc.vo.wa.pub.WaLoginContext;

public class MY_NormalPCBTaxInfPreProcess extends AbstractFormulaExecutor implements
		IMalaysiaPCBTaxInfPreProcess {
	
	private BaseDAO dao = null;
	//如resident，Non-Resident (未满182天），C: Returning Exper Programe(REP)，D: Knowledge worker(IRDA)
	//税率表中如category1，3，2.。。
	private ConcurrentHashMap<String, String> my_pcbcategoryandgroup;
	
	public MY_NormalPCBTaxInfPreProcess() {
		if(dao == null) {
			dao = new BaseDAO();
			initData();
		}
	}
	
	public void initData() {
		//先查bd_defdoclist拿到pk_defdoclist
		my_pcbcategoryandgroup = new ConcurrentHashMap<String, String>();
		SqlBuilder sb = new SqlBuilder();
		sb.append("code", new String[] {"SEALOCAL003", "SEALOCAL006"});
		sb.append(" and dr=0");
		try{
			NCObject[] deflistvos = MDPersistenceService.lookupPersistenceQueryService().
					queryBillOfNCObjectByCond(DefdoclistVO.class, sb.toString(), false);
			List<String> deflistpks = new ArrayList<String>();
			if(deflistvos != null && deflistvos.length >0 
					&& deflistvos[0].getContainmentObject() instanceof DefdoclistVO) {
				for(NCObject deflist : deflistvos) {
					deflistpks.add(((DefdoclistVO)deflist.getContainmentObject()).getPk_defdoclist());
				}
			}
			sb = new SqlBuilder();
			sb.append("pk_defdoclist", deflistpks.toArray(new String[0]));
			sb.append(" and dr = 0");
			NCObject[] defvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(DefdocVO.class, sb.toString(), false);
			if(defvos != null && defvos.length >0 
					&& defvos[0].getContainmentObject() instanceof DefdocVO) {
			for(NCObject defvo : defvos) {
				my_pcbcategoryandgroup.put(((DefdocVO)defvo.getContainmentObject()).getPk_defdoc(), ((DefdocVO)defvo.getContainmentObject()).getCode());
				}
			}
			} catch(Exception e) {
				ExceptionUtils.wrapException(e.getMessage(), e);
		}

	}
	@Override
	public void transferTaxCacuData(MalaysiaTaxFormulaVO malaysiaFormulaVO,
			WaLoginContext context) throws BusinessException {
		//1.查询已知的薪资项目
		List<MalaysiaVO_PCB> results = this.queryPCBKnownItems(malaysiaFormulaVO, context);
		
		//2.逻辑计算未查询的薪资项目，计算P
		ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem = this.cacuOtherPCBItem(results.toArray(new MalaysiaVO_PCB[0]), context);
		
		//3.更新wa_cacu_data, cacu_calue，考虑精度
		this.updateCacudata(cacuOtherPCBItem);

	}
	
	/**这里需要创建临时表,批量更新
	 * 更新 wa_cacu_data
	 * @param cacuOtherPCBItem
	 * @throws BusinessException 
	 */
	private void updateCacudata(ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) throws BusinessException {
		//创建临时表
		this.creatTempTable(cacuOtherPCBItem);
		
		 String sql = " update wa_cacu_data w"
			+ " set (w.cacu_value) ="
			+ " (select p.pcb from tmp_wa_pcb p where w.pk_cacu_data = p.pk_cacu_data)"
			+ " where exists (select 1 from tmp_wa_pcb)";
		 super.executeSQLs(sql);
	}

	private void creatTempTable(ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) {
		Connection con = null;
		String tempTable = null;
		// 创建临时表
		try {
			con = ConnectionFactory.getConnection();
			String tableName = "tmp_wa_pcb";
			String columns = "pk_cacu_data varchar2(20), pcb number(28,8)";
			tempTable = new TempTable().createTempTable(con, tableName, columns, "pk_cacu_data");
//			((CrossDBConnection) con).setAddTimeStamp(false);
			prepareTempTable(tempTable, cacuOtherPCBItem);
		} catch (SQLException e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
	}

	private void prepareTempTable(String tempTable,
			ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem) {
		String sql = "insert into " + tempTable + " (pk_cacu_data,pcb) values (?,?)";
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

	/**
	 * 计算P = ([(P – M) R + B] – (Z + X))/n + 1
	 * @param results
	 */
	private void cacuPara_P(List<MalaysiaVO_PCB> results) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 计算部分薪资项目,如"P = [∑(Y – K*) + (Y1 – K1*) + [(Y2 – K2*) n] + (Yt – Kt*)**] – 
	 * [D + S + DU + SU +QC + (∑LP + LP1)]"
	 * @param results
	 * @param context 
	 * @throws DAOException 
	 */
	private ConcurrentHashMap<String, UFDouble> cacuOtherPCBItem(MalaysiaVO_PCB[] results, WaLoginContext context) throws DAOException {
		//计算n, y2, k2, D, S, DU, SU, QC, LP1
		String period = context.getCperiod();
		String year = context.getCyear();
		ConcurrentHashMap<String, UFDouble> wacacumap = new ConcurrentHashMap<String, UFDouble>();
		//固定参数
		Map<String, UFDouble> stableParas = this.queryStablePara();
		//税率对照表
		MalaysiaVO_PCB_Rate[] raterange = this.queryPCBRateRanage();
			
		//剩余月份
		Integer n = 12 - Integer.valueOf(period);
		for(MalaysiaVO_PCB vo : results) {
			if(MalaysiaVO_PCB.MY_RESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory()))) {
				this.calResident(vo, wacacumap, stableParas, raterange, period, year, n);
			} else if(MalaysiaVO_PCB.MY_NONRESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory()))) {
				this.calNonResident(vo, wacacumap, stableParas, raterange);
			}
			
			
		}
		return wacacumap;
		
	}
	
	/**
	 * Non-Resident
	 * @param vo
	 * @param wacacumap
	 * @param stableParas
	 * @param raterange
	 */
	private void calNonResident(MalaysiaVO_PCB vo,
			ConcurrentHashMap<String, UFDouble> wacacumap,
			Map<String, UFDouble> stableParas, MalaysiaVO_PCB_Rate[] raterange) {
		UFDouble basicslary = vo.getY1();//f_198应税收入
		//税率方案
		MalaysiaVO_PCB_Rate rangevo = this.matchPcbRateAndRange(null, raterange, vo);
		UFDouble p = UFDoubleUtils.multiply(basicslary, UFDoubleUtils.div(rangevo.getPcb_rate(), new UFDouble(100)));
		wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(p));
		
	}
	/**
	 * 
	 * @param vo
	 * @param raterange 
	 * @param stableParas 
	 * @param wacacumap 
	 * @param year 
	 * @param period 
	 * @param n 
	 */
	private void calResident(MalaysiaVO_PCB vo, ConcurrentHashMap<String, UFDouble> wacacumap, 
			Map<String, UFDouble> stableParas, MalaysiaVO_PCB_Rate[] raterange, String period, String year, Integer n) {
		//Y2: 以Y1为后续月份的预估收入
		UFDouble y1 = vo.getY1();//f_198应税收入
		UFDouble y2 = y1;
		//计算 D+S+DU+SU+QC+(LP_LP1)
		UFDouble d = stableParas.get("D");
		UFDouble kt = vo.getKt();
		UFDouble s = UFDouble.ZERO_DBL;
		UFDouble du = UFDouble.ZERO_DBL;
		UFDouble su = UFDouble.ZERO_DBL;
		UFDouble qc = UFDouble.ZERO_DBL;
		UFDouble y = UFDouble.ZERO_DBL;
		UFDouble k = UFDouble.ZERO_DBL;
		UFDouble k1 = vo.getK1();
		UFDouble k2 = UFDouble.ZERO_DBL;
		UFDouble lp = UFDouble.ZERO_DBL;
		//S
		if(!vo.getIsspouseworking().booleanValue()) {
			s = stableParas.get("S");
		}
		
		//DU
		if(vo.getIsdisable().booleanValue()) {
			du = stableParas.get("DU");
		}
		//SU
		if(vo.getIssponsedisable() != null && vo.getIssponsedisable().booleanValue()) {
			su = stableParas.get("SU");
		}
		//QC
		qc = vo.getChildren() == null ? UFDouble.ZERO_DBL : UFDoubleUtils.multiply(vo.getChildren(), stableParas.get("QC"));
		
		//Y,K,LP
		Boolean iscurrenroll = compareBeginDate(vo.getBegindate(), period, year);
		if(iscurrenroll) {
			y = UFDoubleUtils.add(vo.getY(), vo.getTotalpayable());
			k = UFDoubleUtils.add(vo.getK(), vo.getTotalepf());
			lp = UFDoubleUtils.add(vo.getLp(), vo.getTotaltax());
		} else {
			y = vo.getY();
			k = vo.getK();
			lp = vo.getLp();
		}
		//K2
		k2 = this.getK2(stableParas.get("TQ"),k, k1, kt, n);
		//计算P
		UFDouble P1 = UFDoubleUtils.add(UFDoubleUtils.sub(y, k), 
				UFDoubleUtils.sub(y1, k1), 
				UFDoubleUtils.multiply(UFDoubleUtils.sub(y2, k2), new UFDouble(n)),
				UFDoubleUtils.sub(vo.getYt(), kt));
		UFDouble P2 = UFDoubleUtils.add(d, s, du, su, qc, lp, vo.getLp1());
		UFDouble P = UFDoubleUtils.sub(P1, P2);
		
		//根据P 确定税率， 速算扣除数， 速算种类， ZAKAT得到最终税额
		MalaysiaVO_PCB_Rate pabrange = this.matchPcbRateAndRange(P, raterange, vo);
		//MTD= ([(P – M) R + B] – (Z + X))/n + 1
		//计算Z,X
		UFDouble z = UFDouble.ZERO_DBL;
		UFDouble x = UFDouble.ZERO_DBL;
		if(iscurrenroll) {
			z = UFDoubleUtils.add(vo.getZ(), vo.getTotalpcb());
			x = UFDoubleUtils.add(vo.getX(), vo.getTotalpcb());
		} else {
			z = vo.getZ();
			x = vo.getX();
		}
		UFDouble tempmtd = UFDoubleUtils.sub(UFDoubleUtils.add(UFDoubleUtils.multiply(
				UFDoubleUtils.sub(P,  pabrange.getPcb_m()), UFDoubleUtils.div(pabrange.getPcb_rate(),new UFDouble(100))), pabrange.getPcb_category1_3()), 
				UFDoubleUtils.add(z, x));
		UFDouble mtd = UFDoubleUtils.div(tempmtd, new UFDouble(n + 1));
		//计算Net MTD: MTD - zakat
		UFDouble netmtd = UFDoubleUtils.sub(mtd, z);
		if(UFDoubleUtils.isLessThan(netmtd, UFDouble.ZERO_DBL) && 
				(UFDouble.ZERO_DBL.equals(vo.getYt()) || vo.getYt() == null)) {
			wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(UFDouble.ZERO_DBL));
			return;
		} else if(UFDoubleUtils.isLessThan(netmtd, UFDouble.ZERO_DBL) &&
				UFDoubleUtils.isGreaterThan(vo.getYt(), UFDouble.ZERO_DBL)) {
			//TODO
			netmtd = mtd;
		} else if((UFDoubleUtils.isGreaterThan(netmtd, UFDouble.ZERO_DBL) && UFDouble.ZERO_DBL.equals(vo.getYt())
				|| UFDoubleUtils.isEqual(netmtd, UFDouble.ZERO_DBL) && UFDouble.ZERO_DBL.equals(vo.getYt()))) {
			
			wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(netmtd));
			return;
		} else if(UFDoubleUtils.isGreaterThan(netmtd, UFDouble.ZERO_DBL) && UFDoubleUtils.isGreaterThan(vo.getYt(), UFDouble.ZERO_DBL)) {
			//TODO
		}
		
		//计算additional remuneration : X + [Step [2.13] x (n + 1)]
		UFDouble totalmonth_decu = UFDoubleUtils.add(x, tempmtd);
		UFDouble totalyear_decu = UFDoubleUtils.add(UFDoubleUtils.multiply(UFDoubleUtils.sub(P, pabrange.getPcb_m()), pabrange.getPcb_rate()),
				pabrange.getPcb_category1_3());
		UFDouble currtax = this.getCurrTax(totalyear_decu, totalmonth_decu, z);
		
		//last
		UFDouble pcb = UFDoubleUtils.sub(UFDoubleUtils.add(netmtd, currtax), z);
		//TODO 精度未处理
		
		
		wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(pcb));
		
	}
	/**
	 * Malaysia PCB进舍位方式比较奇葩 
	 * 1,2,3,4分进位为5    6,7,8,9进位为10
	 * 如2.22 为2.25,  2.27为2.30
	 * @param context
	 * @return
	 */
	public UFDouble getPrecisson(UFDouble value) {
		//不考虑薪资发放项目的舍位方式，固定未此方法
		return UFDoubleScaleUtils.setScale(value, new UFDouble(0.05), RoundingMode.HALF_UP);
	}

	/**
	 * 
	 * @param totalyear_decu
	 * @param totalmonth_decu
	 * @param z
	 * @return
	 */
	private UFDouble getCurrTax(UFDouble totalyear_decu,
			UFDouble totalmonth_decu, UFDouble z) {
		UFDouble currtax = UFDoubleUtils.add(UFDoubleUtils.add(totalyear_decu, totalmonth_decu), z);
		if(UFDoubleUtils.isLessThan(currtax, new UFDouble(10))) {
			return new UFDouble(10);
		} else {
			return currtax;
		}
	}

	/**
	 * 根据P 确定rate等等
	 * @param p
	 * @param vo 
	 * @param rate range
	 */
	private MalaysiaVO_PCB_Rate matchPcbRateAndRange(UFDouble p, MalaysiaVO_PCB_Rate[] raterange, MalaysiaVO_PCB vo) {
		for(MalaysiaVO_PCB_Rate range : raterange) {
			if(range.getLower_limit() != null && 
					MalaysiaVO_PCB.MY_RESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory())) && //Resident
					UFDoubleUtils.isGreaterThan(p, range.getLower_limit()) &&
					UFDoubleUtils.isLessThan(p, range.getUpper_limit())) {
				return range;
			} else if(p == null && MalaysiaVO_PCB.MY_NONRESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory()))) {
				return range;
			}
		}
		return new MalaysiaVO_PCB_Rate();
	}

	/**
	 * PCB Rate range 税率对照表
	 * @throws DAOException 
	 */
	private MalaysiaVO_PCB_Rate[] queryPCBRateRanage() throws DAOException {
		List<MalaysiaVO_PCB_Rate> results = (List<MalaysiaVO_PCB_Rate>) dao.retrieveAll(MalaysiaVO_PCB_Rate.class);
		return results.toArray(new MalaysiaVO_PCB_Rate[0]);
	}

	/**
	 * 判断K1和下面公式算出的值哪个小，哪个小取哪个  
	 * @param ufDouble
	 * @param k
	 * @param k1
	 * @param kt
	 * @param n
	 */
	private UFDouble getK2(UFDouble qt, UFDouble k, UFDouble k1, UFDouble kt,
			Integer n) {
		UFDouble uf1 = UFDoubleUtils.sub(qt, UFDoubleUtils.div(UFDoubleUtils.add(k, k1, kt), new UFDouble(n)));
		UFDouble uf2 = k1;
		return UFDoubleUtils.isGreaterThan(uf1, uf2) ? uf2 : uf1;
		
	}

	/**
	 * 判断入职日期是否大于本年的一月一日
	 * @param begindate
	 * @param period
	 * @param year
	 * @return
	 */
	private boolean compareBeginDate(UFDate begindate, String period,
			String year) {
		if(begindate != null) {
			int enrollyear = begindate.getYear();
			if(enrollyear == Integer.valueOf(year)) {
				int enrollmonth = begindate.getMonth();
				if(enrollmonth > 1) {
					return true;
				} else {
					int enrollday = begindate.getDay();
					if(enrollday > 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查询固定参数，如Individual Deductions，Spouse Deductions等等
	 * @throws DAOException 
	 */
	private Map<String, UFDouble> queryStablePara() throws DAOException {
		Map<String, UFDouble> paramap = new HashMap<String, UFDouble>();
		List<MalaysiaVO_PCB_para> paras = (List<MalaysiaVO_PCB_para>) dao.retrieveAll(MalaysiaVO_PCB_para.class);
		for(MalaysiaVO_PCB_para para : paras) {
			paramap.put(para.getCode(), para.getValue());
		}
		return paramap;
	}

	private List<MalaysiaVO_PCB> queryPCBKnownItems(
			MalaysiaTaxFormulaVO malaysiaFormulaVO, WaLoginContext context) {
		List<MalaysiaVO_PCB> results = null;
		String condition = " pk_wa_class = ? and creator = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_wa_class());
		parameter.addParam(context.getWaLoginVO().getCreator());
		try {
			results = (List<MalaysiaVO_PCB>) dao.retrieveByClause(MalaysiaVO_PCB.class, condition, parameter);
		} catch (Exception e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
		return results;
	}

	private List<MalaysiaVO_PCB> constructVO(
			List<Map<String, Object>> resultslist) {
		List<MalaysiaVO_PCB> pcbs = new ArrayList<MalaysiaVO_PCB>();
		for(Map<String, Object> map : resultslist) {
			if(map != null) {
				for(Map.Entry<String, Object> entry : map.entrySet()) {
					MalaysiaVO_PCB pcb = new MalaysiaVO_PCB();
					pcb.setY1(new UFDouble(entry.getKey()));
				}
			}
		}
		return null;
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
