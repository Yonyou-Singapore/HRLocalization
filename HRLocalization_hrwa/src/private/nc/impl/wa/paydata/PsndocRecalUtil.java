package nc.impl.wa.paydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.util.iufo.hr.normal.HRSqlUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.item.CustomerProrateVOs;
import nc.vo.wa.paydata.PsncomputeVO;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.StringUtils;

public class PsndocRecalUtil {
	
	public static final String SEPERATE = "#";
	/**
	 * 时点薪资天数计算应按照各个薪资项目设置的prorate计算，或者根据人员信息设置的工作人天计算
	 * 薪资发放项目中有working day, calendar day, working day/fixed days, calendar/fixed days
	 * @param tempvos
	 * @param vo
	 * @throws BusinessException 
	 */
	void reCalPsncomputeVOs(PsncomputeVO[] tempvos, WaLoginVO vo) throws BusinessException {
		//1.按照开始日期和结束日期以及prorate的类型, 匹配基准天数和前后天数
		this.convertDayByProrate(tempvos, vo);
	}
	
	/**
	 * 转换基准天数等等信息
	 * @param tempvos
	 * @param vo
	 * @throws BusinessException 
	 */
	private void convertDayByProrate(PsncomputeVO[] tempvos, WaLoginVO vo) throws BusinessException {
		//Working day的map
		Map<String, String> eepaygroup = this.qryPsnPayGroup(tempvos, vo);
		//Prorate的map
		Map<String, String> sgproratemap = this.qryItemsProrate(tempvos, vo);
		//当前月的天数 
		UFLiteralDate periodstart = vo.getPeriodVO().getCstartdate();
		UFLiteralDate periodend = vo.getPeriodVO().getCenddate();
		
		for(PsncomputeVO calvo : tempvos) {
			String key_item = vo.getPk_wa_class() + calvo.getPk_wa_item();
			//没有prorate或者No Prorate  走标准产品
			String sgprorate = StringUtils.substringBefore(sgproratemap.get(key_item), SEPERATE);
			if(StringUtils.isBlank(sgproratemap.get(key_item)) || 
					"3".equals(sgprorate)) {//NO Prorate
				continue;
			}
			//By Working days,则取人员信息集上的working day
			//Fixed Days
			UFDouble fixeddays = UFDouble.ZERO_DBL;
			if(StringUtils.isBlank(StringUtils.substringAfter(sgproratemap.get(key_item), SEPERATE)) || "null".equals(StringUtils.substringAfter(sgproratemap.get(key_item), SEPERATE))) {
				fixeddays = UFDouble.ZERO_DBL;
			} else {
				fixeddays = new UFDouble(StringUtils.substringAfter(sgproratemap.get(key_item), SEPERATE));
			}
			switch(sgprorate) {
				case "0":
					//PRORATE_BY_WORKINGDAYS
					this.calPsncomputeVOByWorkingday(calvo, periodstart, periodend, eepaygroup.get(calvo.getPk_psndoc()));
					break;
				case "1":
					//PRORATE_BY_CALENDARDAY
					this.calPsncomputeVOByCalendar(calvo, periodstart, periodend);
					break;
				case "2":
					//PRORATE_BY_CALENDAR_DIV_FIXEDDAYS
					this.calPsncomputeVOByCalendarDivFixedDay(calvo, periodstart, periodend, fixeddays);
					break;
				case "4":
					//PRORATE_BY_WORK_DIV_FIXEDDAYS
					this.calPsncomputeVOByWorkingdayDivFixedDay(calvo, periodstart, periodend, eepaygroup.get(calvo.getPk_psndoc()), fixeddays);
					break;
				default:
					break;
			}
		}
	}
	
	

	private void calPsncomputeVOByWorkingdayDivFixedDay(PsncomputeVO calvo,
			UFLiteralDate periodstart, UFLiteralDate periodend, String eepaygroup,
			UFDouble fixeddays) throws BusinessException {
		//前薪资期间对应考勤期间的开始日期到调薪日期人员的应出勤天数
		UFDouble sn=UFDouble.ZERO_DBL;
		//调薪后天数为调薪开始日期到当前薪资期间对应考勤期间的结束日期人员的应出勤天数
		UFDouble en=UFDouble.ZERO_DBL;
		if(calvo.getBegindate().afterDate(periodstart)){
			if(calvo.getOldbegindate()!=null && !calvo.getOldenddate().beforeDate(periodstart)){
				//离职或调薪，先计算Days After Salary Adj(en), Days before Salary Adj(sn)= 26-Days After Salary Adj(en)
				en = getWorkingDays(calvo.getBegindate(), periodend, eepaygroup);
				sn = fixeddays.sub(en);
			} else {
				//新入职，先计算入职前计薪天数
				sn = getWorkingDays(periodstart, calvo.getBegindate().getDateBefore(1), eepaygroup);
				en = fixeddays.sub(sn);
			}
		}else{
			en = getWorkingDays(periodstart,calvo.getEnddate()!=null && calvo.getEnddate().before(periodend) ? calvo.getEnddate(): periodend, eepaygroup);
		}
		
		calvo.setWanceforedays((new UFDouble(sn)).setScale(0, UFDouble.ROUND_UP));
		calvo.setWanafterdays((new UFDouble(en)).setScale(0, UFDouble.ROUND_UP));
		calvo.setBasedays(fixeddays.setScale(0, UFDouble.ROUND_UP));
		
		UFDouble allDay = calvo.getWanceforedays().add(calvo.getWanafterdays());//调薪前天数+调薪后天数
		
		if(calvo.getBasedays() == null || calvo.getBasedays().doubleValue() == 0){
			calvo.setBasedays(allDay);
		}else{
			allDay = calvo.getBasedays();
		}
		
		UFDouble olddaypay = new UFDouble(0.0);
		UFDouble nowdaypay = new UFDouble(0.0);
		UFDouble oneday = new UFDouble(1.0);
		if(allDay.doubleValue() >= oneday.doubleValue()){//如果合起来还小于1天则日薪都为0
			olddaypay = calvo.getOldwadocnmoney().div(new UFDouble(allDay),6);
			nowdaypay = calvo.getWadocnmoney().div(new UFDouble(allDay),6);
		}
		calvo.setWanbeforemoney(olddaypay);
		calvo.setWanaftermoney(nowdaypay);
	}

	private void calPsncomputeVOByCalendarDivFixedDay(PsncomputeVO calvo,
			UFLiteralDate periodstart, UFLiteralDate periodend, UFDouble fixeddays) {
		//前薪资期间对应考勤期间的开始日期到调薪日期人员的应出勤天数
		UFDouble sn = UFDouble.ZERO_DBL;
		//调薪后天数为调薪开始日期到当前薪资期间对应考勤期间的结束日期人员的应出勤天数
		UFDouble en = UFDouble.ZERO_DBL;
		if(calvo.getBegindate().afterDate(periodstart)){
			if(calvo.getOldbegindate()!=null && ! calvo.getOldenddate().beforeDate(periodstart)){
				//GB 离职或调薪，先计算Days After Salary Adj(en), Days before Salary Adj(sn)= 26-Days After Salary Adj(en)
				en = calvo.getEnddate()!=null && calvo.getEnddate().beforeDate(periodend) ? new UFDouble(UFLiteralDate.getDaysBetween(calvo.getOldbegindate(), calvo.getEnddate())+1) : 
					new UFDouble(UFLiteralDate.getDaysBetween(calvo.getBegindate(), periodend) + 1);
				sn = fixeddays.sub(en);
			}else{
				//GB 新入职，先计算入职前计薪天数
				sn = calvo.getOldbegindate().afterDate(periodstart) ? new UFDouble(UFLiteralDate.getDaysBetween(calvo.getOldbegindate(), calvo.getOldenddate())+1) :
					new UFDouble(UFLiteralDate.getDaysBetween(periodstart, calvo.getOldenddate())+1);
				en = fixeddays.sub(sn);
			}
		}else{
			en = new UFDouble(UFLiteralDate.getDaysBetween(periodstart, calvo.getEnddate() != null && calvo.getEnddate().before(periodend) ? calvo.getEnddate() : periodend) + 1);
		}
		
		calvo.setWanceforedays((new UFDouble(sn)).setScale(0, UFDouble.ROUND_UP));
		calvo.setWanafterdays((new UFDouble(en)).setScale(0, UFDouble.ROUND_UP));
		calvo.setBasedays(fixeddays.setScale(0, UFDouble.ROUND_UP));
		
		UFDouble allDay = calvo.getWanceforedays().add(calvo.getWanafterdays());//调薪前天数+调薪后天数
		
		if(calvo.getBasedays() == null || calvo.getBasedays().doubleValue() == 0){
			calvo.setBasedays(allDay);
		}else{
			allDay = calvo.getBasedays();
		} 
		
		UFDouble olddaypay = new UFDouble(0.0);
		UFDouble nowdaypay = new UFDouble(0.0);
		UFDouble oneday = new UFDouble(1.0);
		if(allDay.doubleValue() >= oneday.doubleValue()){//如果合起来还小于1天则日薪都为0
			olddaypay = calvo.getOldwadocnmoney().div(new UFDouble(allDay),6);
			nowdaypay = calvo.getWadocnmoney().div(new UFDouble(allDay),6);
		}
		calvo.setWanbeforemoney(olddaypay);
		calvo.setWanaftermoney(nowdaypay);
	}

	/**
	 * 按照人员信息设置的工作日计算
	 * @param calvo
	 * @param periodstart
	 * @param periodend
	 * @param workdayType 
	 * @throws BusinessException 
	 */
	private void calPsncomputeVOByWorkingday(PsncomputeVO calvo,
			UFLiteralDate periodstart, UFLiteralDate periodend, String workdayType) throws BusinessException {
		if(StringUtils.isBlank(workdayType)) {
			return;
		}
		//前薪资期间对应考勤期间的开始日期到调薪日期人员的应出勤天数
		UFDouble sn = UFDouble.ZERO_DBL;
		//调薪后天数为调薪开始日期到当前薪资期间对应考勤期间的结束日期人员的应出勤天数
		UFDouble en = UFDouble.ZERO_DBL;
		UFDouble basedays = this.getWorkingDays(periodstart, periodend, workdayType);
		if(calvo.getBegindate().afterDate(periodstart)){
			if(calvo.getOldbegindate()!=null && !calvo.getOldenddate().beforeDate(periodstart)){
				sn = calvo.getOldbegindate().afterDate(periodstart)?this.getWorkingDays(calvo.getOldbegindate(), calvo.getOldenddate(), workdayType) : this.getWorkingDays(periodstart, calvo.getOldenddate(), workdayType);
			}
			en = calvo.getEnddate()!=null && calvo.getEnddate().beforeDate(periodend) ? this.getWorkingDays(calvo.getOldbegindate(), calvo.getEnddate(), workdayType) : this.getWorkingDays(calvo.getBegindate(), periodend, workdayType);
		}else{
			en = this.getWorkingDays(periodstart, calvo.getEnddate() != null && calvo.getEnddate().before(periodend) ? calvo.getEnddate() : periodend, workdayType);
		}
		
		calvo.setWanceforedays((new UFDouble(sn)).setScale(0, UFDouble.ROUND_UP));
		calvo.setWanafterdays((new UFDouble(en)).setScale(0, UFDouble.ROUND_UP));
		calvo.setBasedays((new UFDouble(basedays)).setScale(0, UFDouble.ROUND_UP));
		
		UFDouble allDay = calvo.getWanceforedays().add(calvo.getWanafterdays());//调薪前天数+调薪后天数
		
		if(calvo.getBasedays() == null || calvo.getBasedays().doubleValue() == 0){
			calvo.setBasedays(allDay);
		}else{
			allDay = calvo.getBasedays();
		}
		
		UFDouble olddaypay = new UFDouble(0.0);
		UFDouble nowdaypay = new UFDouble(0.0);
		UFDouble oneday = new UFDouble(1.0);
		if(allDay.doubleValue() >= oneday.doubleValue()){//如果合起来还小于1天则日薪都为0
			olddaypay = calvo.getOldwadocnmoney().div(new UFDouble(allDay),6);
			nowdaypay = calvo.getWadocnmoney().div(new UFDouble(allDay),6);
		}
		calvo.setWanbeforemoney(olddaypay);
		calvo.setWanaftermoney(nowdaypay);
	}

	/**
	 * 按照日历天数计算
	 * @param calvo
	 * @param periodstart
	 * @param periodend
	 */
	private void calPsncomputeVOByCalendar(PsncomputeVO calvo,
			UFLiteralDate periodstart, UFLiteralDate periodend) {
		//前薪资期间对应考勤期间的开始日期到调薪日期人员的应出勤天数
		UFDouble sn = UFDouble.ZERO_DBL;
		//调薪后天数为调薪开始日期到当前薪资期间对应考勤期间的结束日期人员的应出勤天数
		UFDouble en = UFDouble.ZERO_DBL;
		int basedays =UFLiteralDate.getDaysBetween(periodstart,periodend)+1;
		if(calvo.getBegindate().afterDate(periodstart)){
			if(calvo.getOldbegindate()!=null && !calvo.getOldenddate().beforeDate(periodstart)){
				sn = calvo.getOldbegindate().afterDate(periodstart) ? new UFDouble(UFLiteralDate.getDaysBetween(calvo.getOldbegindate(), calvo.getOldenddate())+1) :
						new UFDouble(UFLiteralDate.getDaysBetween(periodstart, calvo.getOldenddate())+1);
			}
			en = calvo.getEnddate()!=null && calvo.getEnddate().beforeDate(periodend) ? new UFDouble(UFLiteralDate.getDaysBetween(calvo.getOldbegindate(), calvo.getEnddate())+1) : 
						new UFDouble(UFLiteralDate.getDaysBetween(calvo.getBegindate(), periodend) + 1);
		}else{
			en = new UFDouble(UFLiteralDate.getDaysBetween(periodstart, calvo.getEnddate() != null && calvo.getEnddate().before(periodend) ? calvo.getEnddate() : periodend) + 1);
		}
		
		calvo.setWanceforedays((new UFDouble(sn)).setScale(0, UFDouble.ROUND_UP));
		calvo.setWanafterdays((new UFDouble(en)).setScale(0, UFDouble.ROUND_UP));
		calvo.setBasedays((new UFDouble(basedays)).setScale(0, UFDouble.ROUND_UP));
		
		UFDouble allDay = calvo.getWanceforedays().add(calvo.getWanafterdays());//调薪前天数+调薪后天数
		
		if(calvo.getBasedays() == null || calvo.getBasedays().doubleValue() == 0){
			calvo.setBasedays(allDay);
		}else{
			allDay = calvo.getBasedays();
		}
		
		UFDouble olddaypay = new UFDouble(0.0);
		UFDouble nowdaypay = new UFDouble(0.0);
		UFDouble oneday = new UFDouble(1.0);
		if(allDay.doubleValue() >= oneday.doubleValue()){//如果合起来还小于1天则日薪都为0
			olddaypay = calvo.getOldwadocnmoney().div(new UFDouble(allDay),6);
			nowdaypay = calvo.getWadocnmoney().div(new UFDouble(allDay),6);
		}
		calvo.setWanbeforemoney(olddaypay);
		calvo.setWanaftermoney(nowdaypay);
	}

	/**
	 * add chenth 20181108 根据人员信息上定义的一周工作天数，来计算工作时长
	 * @param begindate
	 * @param enddate
	 * @param pk_psndoc
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getWorkingDays(UFLiteralDate begindate,
			UFLiteralDate enddate, String workdayType) throws BusinessException {
		UFDouble days = UFDouble.ZERO_DBL;
		UFDouble incremental = UFDouble.ZERO_DBL;
		UFDouble weekendincremental = UFDouble.ZERO_DBL;
		//默认5.5天
		if(workdayType == null){
			workdayType = "5.5";
		}
		switch(workdayType){
			case "5":
			case "5.5":
			case "6":
				incremental = UFDouble.ONE_DBL;
				break;
			case "5 PT":
			case "5.5 PT":
				incremental = new UFDouble(0.5);
				break;
			default:
				incremental = UFDouble.ZERO_DBL;
		}
		switch(workdayType){
			case "6":
				weekendincremental = UFDouble.ONE_DBL;
				break;
			case "5.5":
			case "5.5 PT":
				weekendincremental = new UFDouble(0.5);
				break;
			default:
				weekendincremental = UFDouble.ZERO_DBL;
		}
		while(begindate.compareTo(enddate)<1){
			int week = begindate.getWeek() == 0 ? 7 : begindate
					.getWeek();
			if(week < 6){
				days = days.add(incremental);
			}else if(week == 6){
				days = days.add(weekendincremental);
			}
			begindate = begindate.getDateAfter(1);
		}
		
		return days;
	}

	private Map<String, String> convertMapForEeGroup(
			List<CustomerProrateVOs> prorateVOs) {
		Map<String, String> map = new HashMap<String, String>();
		for(CustomerProrateVOs vo : prorateVOs) {
			map.put(vo.getPk_wa_item() + vo.getPk_psndoc(), vo.getG_eepaygroup());
		}
		return map;
	}
	
	private Map<String, String> convertMapForProrate(
			List<CustomerProrateVOs> prorateVOs) {
		Map<String, String> map = new HashMap<String, String>();
		for(CustomerProrateVOs vo : prorateVOs) {
			map.put(vo.getPk_wa_item() + vo.getPk_psndoc(), vo.getSg_prorate());
		}
		return map;
	}

	private Map<String, String> qryItemsProrate(PsncomputeVO[] tempvos, WaLoginVO vo) {
		List<String> itemslist = new ArrayList<String>();
		for(PsncomputeVO psncomputevo : tempvos) {
			itemslist.add(psncomputevo.getPk_wa_item());
		}
		SqlBuilder sb = new SqlBuilder();
		sb.append("select pk_wa_class, pk_wa_item, sg_prorate, g_fixeddays from wa_classitem where 1=1");
		sb.append(" and pk_wa_class", vo.getPk_wa_class());
		sb.append(" and cperiod", vo.getCperiod());
		sb.append(" and cyear", vo.getCyear());
		try {
			sb.append(HRSqlUtil.buildInSql(" and pk_wa_item", itemslist));
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		DataAccessUtils util = new DataAccessUtils();
		IRowSet rowset = util.query(sb.toString());
		//map
		Map<String, String> itemmap = new HashMap<String, String>();
		while(rowset.next()) {
			itemmap.put(rowset.getString(0) + rowset.getString(1) , rowset.getString(2) + SEPERATE + rowset.getString(3));
		}
		return itemmap;
	}
	
	private Map<String, String> qryPsnPayGroup(PsncomputeVO[] tempvos, WaLoginVO vo) {
		List<String> psnlist = new ArrayList<String>();
		for(PsncomputeVO psncomputevo : tempvos) {
			psnlist.add(psncomputevo.getPk_psndoc());
		}
		SqlBuilder sb = new SqlBuilder();
		sb.append("select p.pk_psndoc, def.code from bd_psndoc p left join bd_defdoc def on p.g_eepaygroup = def.pk_defdoc where 1=1");
		try {
			sb.append(HRSqlUtil.buildInSql(" and pk_psndoc", psnlist));
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		DataAccessUtils util = new DataAccessUtils();
		IRowSet rowset = util.query(sb.toString());
		//map
		Map<String, String> psnpaygroupmap = new HashMap<String, String>();
		while(rowset.next()) {
			psnpaygroupmap.put(rowset.getString(0), rowset.getString(1));
		}
		return psnpaygroupmap;
	}
	
}
