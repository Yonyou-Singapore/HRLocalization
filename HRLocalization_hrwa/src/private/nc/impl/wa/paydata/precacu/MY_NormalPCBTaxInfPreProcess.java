package nc.impl.wa.paydata.precacu;

/**
 * PCB Group 计税
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.impl.wa.classitem.ClassitemDAO;
import nc.impl.wa.func.SeaLocalFormulaUtil;
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
import nc.vo.wa.classitem.WaClassItemVO;
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
//			NCObject[] defvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(DefdocVO.class, sb.toString(), false);
			//使用BaseDAO查询自定义项
			Collection<DefdocVO> defvos = new BaseDAO().retrieveByClause(DefdocVO.class, sb.toString()	);
			if(defvos != null && defvos.size() >0) {
				for(DefdocVO defvo : defvos) {
					my_pcbcategoryandgroup.put(defvo.getPk_defdoc(), defvo.getCode());
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
	
	/**
	 * 这里需要创建临时表,批量更新
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
			if(vo.getPcbcategory() != null && MalaysiaVO_PCB.MY_RESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory()))) {
				this.calResident(vo, wacacumap, stableParas, raterange, period, year, n);
			} else if(vo.getPcbcategory() != null && MalaysiaVO_PCB.MY_NONRESIDENT.equals(my_pcbcategoryandgroup.get(vo.getPcbcategory()))) {
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
		UFDouble currentzakat = vo.getCurrentzakat() == null ? UFDouble.ZERO_DBL : vo.getCurrentzakat();
		//S
		if(vo.getPcbgroup() != null) {
			s = this.getSvalue(vo, stableParas);
//			s = stableParas.get("S");
		}
		
		//DU
		if(vo.getIsdisable() != null && vo.getIsdisable().booleanValue()) {
			du = stableParas.get("DU");
		}
		//SU
		if(vo.getIssponsedisable() != null && vo.getIssponsedisable().booleanValue()) {
			su = stableParas.get("SU");
		}
		//QC
		qc = vo.getChildren() == null ? UFDouble.ZERO_DBL : UFDoubleUtils.multiply(vo.getChildren(), stableParas.get("QC"));
		
		//LOG
		StringBuilder sb = new StringBuilder();
		Logger.error("=================PCB开始计算=================");
		sb.append("PCB=====" + "period:").append(period).append(" ,Basic Salary").append(y1).append("/n");
		sb.append(" ,S:" + s).append(" ,D:" + d).append(" ,DU:" + du).append(" ,SU:"+ su).append(" ,QC:" + qc).append("/n");
		Logger.error(sb.toString());
		
		//Y,K,LP   入职日期是否大于本年的1月1日
		Boolean iscurrenroll = compareBeginDate(vo.getBegindate(), period, year);
		//场景:系统上线时，将本年以前的抵扣或收入录入相应的人员信息集中，增加起初字段判断是否期初
		Boolean isopenningdata = compareOpenningDate(vo.getOpeningdate(), period, year);
		
		if(iscurrenroll || isopenningdata) {
			y = UFDoubleUtils.add(vo.getY(), vo.getTotalpayable());
			k = UFDoubleUtils.add(vo.getK(), vo.getTotalepf());
			lp = UFDoubleUtils.add(vo.getLp(), vo.getTotaltax());
		} else {
			y = vo.getY();
			k = vo.getK();
			lp = vo.getLp();
		}
		//K2 第一次计算时kt为0
		k2 = this.getK2(stableParas.get("TQ"),k, k1, UFDouble.ZERO_DBL, n);
		//如果 K+k1>6000  K=6000, k1=0
		//如果K+K1+Kt>6000, K=6000, K, Kt=0, modify by weiningc 20190129 
		if(UFDoubleUtils.isGreaterThan(UFDoubleUtils.add(k, k1, UFDouble.ZERO_DBL), stableParas.get("TQ"))) {
			Logger.error("===PCB===如果 K+k1+kt>" + stableParas.get("TQ") + ",K=," + stableParas.get("TQ") + ",k1=0,kt=0");
			k = stableParas.get("TQ");
			k1 = UFDouble.ZERO_DBL;
			kt = UFDouble.ZERO_DBL;
		}
		
		//计算P
		UFDouble P1 = UFDoubleUtils.add(UFDoubleUtils.sub(y, k), 
				UFDoubleUtils.sub(y1, k1), 
				UFDoubleUtils.multiply(UFDoubleUtils.sub(y2, k2), new UFDouble(n)),
				UFDoubleUtils.sub(UFDouble.ZERO_DBL, UFDouble.ZERO_DBL));//第一次的时候yt和kt为0
		UFDouble P2 = UFDoubleUtils.add(d, s, du, su, qc, lp, vo.getLp1());
		UFDouble P = UFDoubleUtils.sub(P1, P2).setScale(2, UFDouble.ROUND_FLOOR);
		
		Logger.error("======PCB=====P = [∑(Y – K) + (Y1 – K1) + [(Y2 – K2) n] + (Yt – Kt)] – [D + S + DU + SU + QC + (∑LP + LP1)]");
		Logger.error("======PCB=====[∑(" + y + "-" + k + ") + (" + y1 + "-" + k1 + ") + [(" + y2 + "-" + k2 + ")*" + n + "] + (" +
				0 + "-" + 0 + ")] - [" + d + "+" + s + "+" + du + "+" + su + "+" + qc + "+" + lp + "+" + vo.getLp1() + ")]");
		
		//根据P 确定税率， 速算扣除数， 速算种类， ZAKAT得到最终税额
		MalaysiaVO_PCB_Rate pabrange = this.matchPcbRateAndRange(P, raterange, vo);
		//MTD= ([(P – M) R + B] – (Z + X))/n + 1
		//计算Z,X
		UFDouble z = UFDouble.ZERO_DBL;
		UFDouble x = UFDouble.ZERO_DBL;
		if(iscurrenroll || isopenningdata) {
			z = vo.getZ();
			x = UFDoubleUtils.add(vo.getX(), vo.getTotalpcb());
		} else {
			z = vo.getZ();
			x = vo.getX();
		}
		//扣税类别
		UFDouble decucationclass = this.getDeductionClass(vo, pabrange);
		
		UFDouble tempmtd = UFDoubleUtils.sub(
				UFDoubleUtils.add(UFDoubleUtils.multiply(
				UFDoubleUtils.sub(P,  pabrange.getPcb_m()), 
				UFDoubleUtils.div(pabrange.getPcb_rate(),new UFDouble(100))), decucationclass), 
				UFDoubleUtils.add(z, x)).setScale(2, UFDouble.ROUND_FLOOR);
		UFDouble mtd = UFDoubleUtils.div(tempmtd, new UFDouble(n + 1));
		Logger.error("====PCB==== ([(P – M) R + B] – (Z + X))/n + 1");
		Logger.error("====PCB====([(" + P + "-" + pabrange.getPcb_m() + ")*" + UFDoubleUtils.div(pabrange.getPcb_rate(),new UFDouble(100)) + "+" + decucationclass + "] - ("
				+ z + "+" + x + ")) / " + (n+1));
		Logger.error("====PCB====MTD: " + mtd);
		//如果mtd<10, 则为0 add by weiningc 20190516 start
		if(UFDoubleUtils.isLessThan(mtd, new UFDouble(10))) {
			mtd = UFDouble.ZERO_DBL;
			Logger.error("====PCB====MTD少于10,置位零, MTD:" + UFDouble.ZERO_DBL);
		}
		//end
		//计算Net MTD: MTD - zakat
		UFDouble netmtd = UFDoubleUtils.sub(mtd, currentzakat);
		if(UFDoubleUtils.isLessThan(netmtd, UFDouble.ZERO_DBL) && 
				(UFDouble.ZERO_DBL.equals(vo.getYt()) || vo.getYt() == null)) {
			wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(UFDouble.ZERO_DBL));
			return;
		} else if(UFDoubleUtils.isLessThan(netmtd, UFDouble.ZERO_DBL) &&
				UFDoubleUtils.isGreaterThan(vo.getYt(), UFDouble.ZERO_DBL)) {
			netmtd = mtd;
		} else if(!UFDoubleUtils.isLessThan(netmtd, UFDouble.ZERO_DBL) && !UFDoubleUtils.isGreaterThan(vo.getYt(), UFDouble.ZERO_DBL)) {
			wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(netmtd));
			Logger.error("PCB==========" + " ,最终的netMTD: " + this.getPrecisson(netmtd) + " ,Z:" + z);
			return;
		} else if(UFDoubleUtils.isGreaterThan(netmtd, UFDouble.ZERO_DBL) && UFDoubleUtils.isGreaterThan(vo.getYt(), UFDouble.ZERO_DBL)) {
		}
		//在这里进行向下进位
		mtd = mtd.setScale(2, UFDouble.ROUND_FLOOR);
		netmtd = netmtd.setScale(2, UFDouble.ROUND_FLOOR);
		//计算additional remuneration : X + [Step [2.13] x (n + 1)]
		UFDouble totalmonth_decu = UFDoubleUtils.add(x, UFDoubleUtils.multiply(mtd, new UFDouble(n+1)));
		//K2 第二次计算额外收入时，Kt不为0
		k2 = this.getK2(stableParas.get("TQ"),k, k1, kt, n); 
		//如果 K+k1>6000  K=6000, k1=0 
		//如果K+K1+Kt>6000, K=6000, K, Kt=0, modify by weiningc 20190129 
		if(UFDoubleUtils.isGreaterThan(UFDoubleUtils.add(k, k1, UFDouble.ZERO_DBL), stableParas.get("TQ"))) {
			Logger.error("===PCB===如果 K+k1+kt>" + stableParas.get("TQ") + ",K=," + stableParas.get("TQ") + ",k1=0,kt=0");
			k = stableParas.get("TQ");
			k1 = UFDouble.ZERO_DBL;
			kt = UFDouble.ZERO_DBL;
		}
		
		UFDouble totalyear_decu1 = UFDoubleUtils.add(UFDoubleUtils.sub(y, k), 
				UFDoubleUtils.sub(y1, k1), 
				UFDoubleUtils.multiply(UFDoubleUtils.sub(y2, k2), new UFDouble(n)),
				UFDoubleUtils.sub(vo.getYt(), kt));//第一次的时候yt和kt为0
		
		UFDouble totoalyearaddP = UFDoubleUtils.sub(totalyear_decu1, P2).setScale(2, UFDouble.ROUND_FLOOR);;//额外收入重新算出的P
		//第二次计算P(有additional 收入)
		Logger.error("======PCB caclute additional P=====[∑(Y – K) + (Y1 – K1) + [(Y2 – K2) n] + (Yt – Kt)] – [D + S + DU + SU + QC + (∑LP + LP1)]");
		Logger.error("======PCB=====[∑(" + y + "-" + k + ") + (" + y1 + "-" + k1 + ") + [(" + y2 + "-" + k2 + ")*" + n + "] + (" +
				vo.getYt() + "-" + kt + ")] - [" + d + "+" + s + "+" + du + "+" + su + "+" + qc + "+" + lp + "+" + vo.getLp1() + ")]");
		Logger.error("======PCB=====" + totoalyearaddP);
		
		//重新找费率表
		MalaysiaVO_PCB_Rate range2 = this.matchPcbRateAndRange(totoalyearaddP, raterange, vo);
		//扣税类别
		decucationclass = this.getDeductionClass(vo, range2);
		
		UFDouble tempmtd2 = UFDoubleUtils.add(UFDoubleUtils.multiply(
				UFDoubleUtils.sub(totoalyearaddP,  range2.getPcb_m()), 
				UFDoubleUtils.div(range2.getPcb_rate(),new UFDouble(100))), decucationclass).setScale(2, UFDouble.ROUND_FLOOR);
		UFDouble additonmtd = UFDoubleUtils.add(UFDoubleUtils.sub(tempmtd2, totalmonth_decu), currentzakat).setScale(2, UFDouble.ROUND_FLOOR);
		Logger.error("====PCB====currentzakat:" + currentzakat);
		if(UFDoubleUtils.isLessThan(additonmtd, new UFDouble(10))) {
			Logger.error("====PCB====Additionl MTD:" + additonmtd);
			//add by weininc 20190516 start
			additonmtd = UFDouble.ZERO_DBL;
			Logger.error("====PCB====Additionl MTD少于10,置位零, MTD:" + UFDouble.ZERO_DBL);
		}
		Logger.error("======PCB==additional mtd===Total tax for a year = (P – M) x R + B");
		Logger.error("====PCB==additional mtd====([(" + totoalyearaddP + "-" + range2.getPcb_m() + ")*" + UFDoubleUtils.div(range2.getPcb_rate(),new UFDouble(100)) + "+" + decucationclass + "]");
		Logger.error("Total tax for a year: " + tempmtd2);
		Logger.error("Additional MTD=Step [3.3] - Step [3.1] + CURRENTZAKAT");
		Logger.error(tempmtd2 + "-" + totalmonth_decu + " + " + currentzakat);
		Logger.error(additonmtd);
		
		//last
		UFDouble pcb = UFDoubleUtils.add(netmtd, additonmtd);
		//扣减当前currentzakat
		pcb = UFDoubleUtils.sub(pcb, currentzakat);
		if(UFDoubleUtils.isLessThan(pcb, new UFDouble(UFDouble.ZERO_DBL))) {
			pcb = UFDouble.ZERO_DBL;
			Logger.error("===========PCB小于0，置位0==========" + " ,PCB: " + pcb);
		} 
		
		Logger.error("===========最终PCB==========" + " ,PCB: " + pcb);
		
		wacacumap.put(vo.getPk_cacu_data(), this.getPrecisson(pcb));
		
	}
	
	/**
	 * 根据薪资项目属性重新取Y1
	 * @param vo
	 * @param y1
	 * @return
	 */
	private UFDouble reFetchY1(MalaysiaVO_PCB vo, UFDouble y1) {
		
		return null;
	}

	private UFDouble getSvalue(MalaysiaVO_PCB vo, Map<String, UFDouble> stableParas) {
		if("02".equals(my_pcbcategoryandgroup.get(vo.getPcbgroup()))) {
			return stableParas.get("S");
		}
		return UFDouble.ZERO_DBL;
	}

	private UFDouble getDeductionClass(MalaysiaVO_PCB vo,MalaysiaVO_PCB_Rate pabrange) {
		if(vo.getPcbgroup() == null) {
			return UFDouble.ZERO_DBL;
		}
		if(MalaysiaVO_PCB.MY_SINGLEOR.equals(my_pcbcategoryandgroup.get(vo.getPcbgroup())) ||
				MalaysiaVO_PCB.MY_MARRIEDAND_SPONSEWORKING.equals(my_pcbcategoryandgroup.get(vo.getPcbgroup())) ||
				MalaysiaVO_PCB.MY_DIVORCEORWINDOW.equals(my_pcbcategoryandgroup.get(vo.getPcbgroup()))) {
			return pabrange.getPcb_category1_3();
		} else if(MalaysiaVO_PCB.MY_MARRIEDAND_SPONSENOTWORKING.equals(my_pcbcategoryandgroup.get(vo.getPcbgroup()))) {
			return pabrange.getPcb_category2();
		} else {
			return UFDouble.ZERO_DBL;
		}
	}

	/**
	 * 当前月 - 期初月 = 1表示 上线所在月份发的薪水
	 * @param openingdate
	 * @param period
	 * @param year
	 * @return
	 */
	private Boolean compareOpenningDate(UFDate openingdate, String period,
			String year) {
		if(openingdate != null) {
			int openingyear = openingdate.getYear();
			if(openingyear == Integer.valueOf(year)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Malaysia PCB进舍位方式比较奇怪
	 * 1,2,3,4分进位为5    6,7,8,9进位为10
	 * 如2.22 为2.25,  2.27为2.30
	 * @param context
	 * @return
	 */
	public UFDouble getPrecisson(UFDouble value) {
		return UFDoubleScaleUtils.setScaleForSpecial2(value);
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
					MalaysiaVO_PCB.MY_RESIDENT.equals(range.getPcb_group()) && //Resident
					UFDoubleUtils.isGreaterThan(p, range.getLower_limit()) &&
					UFDoubleUtils.isLessThan(p, range.getUpper_limit())) {
				return range;
			} else if(p == null && MalaysiaVO_PCB.MY_NONRESIDENT.equals(range.getPcb_group())) {
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
		UFDouble uf1 = UFDoubleUtils.div(UFDoubleUtils.sub(qt, UFDoubleUtils.add(k, k1, kt)), new UFDouble(n));
		//如果uf1小于零， 则取零， 其它的取本身,这个值不能小于零
		uf1 = UFDoubleUtils.isLessThan(uf1, UFDouble.ZERO_DBL) ? UFDouble.ZERO_DBL : uf1;
		UFDouble uf2 = k1;
		UFDouble k2 =  UFDoubleUtils.isGreaterThan(uf1, uf2) ? uf2 : uf1.setScale(2, UFDouble.ROUND_FLOOR);
		//输出公式
//		Logger.error("K2:[[6000 – (∑K + K1 + Kt)] / n]");
		Logger.error("K2:[[" + qt + "– (∑K + K1 + Kt)] / n]");
		Logger.error("[" + qt + "-" + "(" + k + "+" + k1 + "+" + kt + ") ]" + "/" + n);
		Logger.error("K2: " + k2);
		
		return k2;
		
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
				return true;
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

	@SuppressWarnings("unchecked")
	private List<MalaysiaVO_PCB> queryPCBKnownItems(
			MalaysiaTaxFormulaVO malaysiaFormulaVO, WaLoginContext context) {
		List<MalaysiaVO_PCB> results = null;
		String condition = " pk_wa_class = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_wa_class());
		try {
			//y1 场景：薪资项目增加属性，如用于normal pcb计算和additional pcb计算，并且有增减属性,需要重新计算y1
			this.setPcbTableNameForY1AndYt(context);
			
			results = (List<MalaysiaVO_PCB>) dao.retrieveByClause(MalaysiaVO_PCB.class, condition, parameter);
		} catch (Exception e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
		return results;
	}

	private void setPcbTableNameForY1AndYt(WaLoginContext context) {
		ClassitemDAO dao = new ClassitemDAO();
		try {
			WaClassItemVO[] normalpcb = dao.queryItemInfoVO(context.getPk_org(),  context.getPk_wa_class()
					, context.getCyear(), context.getCperiod(), " wa_classitem.my_ispcb_n = 'Y' ");
			WaClassItemVO[] additionalpcb = dao.queryItemInfoVO(context.getPk_org(),  context.getPk_wa_class()
					, context.getCyear(), context.getCperiod(), " wa_classitem.my_ispcb_a = 'Y' ");
			String y1 = SeaLocalFormulaUtil.getConcatString(normalpcb);
			String yt = SeaLocalFormulaUtil.getConcatString(additionalpcb);
			//处理+号
			y1 = this.removeOtherSymbol(y1, "y1");
			yt = this.removeOtherSymbol(yt, "yt");
			MalaysiaVO_PCB.setSumY1(y1);
			MalaysiaVO_PCB.setSumYt(yt);
		} catch (BusinessException e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
		
	}

	private String removeOtherSymbol(String y1, String alias) {
		if(y1 == null || "".equals(y1)) {
			return null;
		}
		if(y1.trim().startsWith("+")) {
			return "sum("+ y1.substring(y1.indexOf("+") + 1) + ")" + alias + ",";
		}
		
		return "sum(" + y1 + ")" + alias + ",";
		
	}

	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
