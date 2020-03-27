package nc.impl.wa.paydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.hr.wa.IPsndocWa;
import nc.util.iufo.hr.normal.HRSqlUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.item.CustomerProrateVOs;
import nc.vo.wa.localenum.ProrateEnum;
import nc.vo.wa.paydata.PsncomputeVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * PsndocWa的Impl类
 * 
 * @author: liangxr
 * @date: 2010-5-26 上午10:27:42
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PsndocWaImpl implements IPsndocWa {

	private final String DOC_NAME = "psndocwa";
	private SimpleDocServiceTemplate serviceTemplate;

	public PsndocWaImpl() {
		super();
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}

	public PsndocWaVO insert(PsndocWaVO psndocWa) throws BusinessException {
		return getServiceTemplate().insert(psndocWa);
	}

	public PsndocWaVO update(PsndocWaVO psndocWa) throws BusinessException {
		return getServiceTemplate().update(psndocWa,true);
	}

	/**
	 * 时点薪资保存
	 * @author liangxr on 2010-5-26
	 * @see nc.itf.hr.wa.IPsndocWa#updatePsndocWas(nc.vo.wa.paydata.PsndocWaVO[])
	 */
	@Override
	public void updatePsndocWas(PsndocWaVO[] psndocWas) throws BusinessException {

		List<PsndocWaVO> insertList = new ArrayList<PsndocWaVO>();
		PsndocWaDAO dmo = new PsndocWaDAO();
		dmo.deletePsndocWa(psndocWas);
		for (PsndocWaVO psndocWaVO : psndocWas) {
			insertList.add(psndocWaVO);
		}

		dmo.getBaseDao().insertVOList(insertList);

	}

	/**
	 * 时点薪资查询
	 * @author liangxr on 2010-5-26
	 * @see nc.itf.hr.wa.IPsndocWa#queryAllShowResult(nc.vo.wa.pub.WaLoginVO, java.lang.String, java.lang.String)
	 */
	@Override
	public PsncomputeVO[] queryAllShowResult(WaLoginVO vo, String deptpower, String psnclspower) throws BusinessException {

		PsncomputeVO[] tempvos = null;

		try {
			PsndocWaDAO dmo = new PsndocWaDAO();
			PsndocWaVO[] psndovVos= dmo.queryAllShowResult(vo, deptpower, psnclspower);
			if(ArrayUtils.isEmpty(psndovVos)){
				return null;
			}
			tempvos = toWaComputeVO(psndovVos, vo.getPeriodVO().getCstartdate(),vo.getPeriodVO().getCenddate()) ;
			WaWorkDay waWorkDay = new WaWorkDay();
			waWorkDay.setWorkDayAndWage(tempvos, vo);
			//时点薪资天数计算应按照各个薪资项目设置的prorate计算，或者根据人员信息设置的工作人天计算 add by weiningc 20191014 start
			if(tempvos != null && tempvos.length > 0) {
				new PsndocRecalUtil().reCalPsncomputeVOs(tempvos, vo);
			}
			//end
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e);
		}

		return tempvos; 
	}

	
	/**
	 * 将时点薪资变动记录VO转换成时点薪资VO
	 * @author liangxr on 2010-5-26
	 * @param rsVOs
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	private PsncomputeVO[] toWaComputeVO(PsndocWaVO[] rsVOs, UFLiteralDate begindate, UFLiteralDate enddate) {
		int count = 0;
		String pk = "", pk_item = "";
		Vector<PsncomputeVO> v = new Vector<PsncomputeVO>();
		PsncomputeVO[] computevos = null;

		for (PsndocWaVO rsVO : rsVOs) {
			PsncomputeVO tempvo = new PsncomputeVO();
			PsndocWaVO psndocWa = new PsndocWaVO();

			tempvo.setPk_psndoc_sub(rsVO.getPk_psndoc_sub());
			tempvo.setPk_psndoc(rsVO.getPk_psndoc());
			tempvo.setClerkcode(rsVO.getClerkcode());
			tempvo.setPsncode(rsVO.getPsncode());
			tempvo.setPsnname(rsVO.getPsnname());
			tempvo.setDeptname(rsVO.getDeptname());
			tempvo.setPk_wa_item(rsVO.getPk_wa_item());
			tempvo.setAssgid(rsVO.getAssgid());
			//			tempvo.setTs(rsVO.getTs());

			if (pk.equals(rsVO.getPk_psndoc())) {
				if (!pk_item.equals(rsVO.getPk_wa_item())) {
					count = 0;
				}
			} else {
				count = 0;
			}
			pk = rsVO.getPk_psndoc();
			pk_item = rsVO.getPk_wa_item();

			tempvo.setItemvname(rsVO.getItemname());
			tempvo.setBegindate(rsVO.getBegindate());
			tempvo.setEnddate(rsVO.getEnddate());
			tempvo.setDangbiename(rsVO.getDbname());
			tempvo.setJibiename(rsVO.getJbname());
			tempvo.setWadocnmoney(rsVO.getNowmoney());
			tempvo.setBasedays(rsVO.getBasedays());
			String nowjidangname = "";
			if (tempvo.getJibiename() != null) {
				nowjidangname = tempvo.getJibiename();
			}
			if (tempvo.getDangbiename() != null && nowjidangname.length() > 0) {
				nowjidangname = nowjidangname + "/" + tempvo.getDangbiename();
			}
			tempvo.setNowJiDangName(nowjidangname);
			psndocWa.setPk_psndoc_wa(rsVO.getPk_psndoc_wa());
			tempvo.setPk_psncompute(psndocWa.getPk_psndoc_wa());
			if (rsVO.isCheckflag()||!StringUtil.isEmpty(rsVO.getPk_psndoc_wa())) {
				tempvo.setIscompute(new Boolean(true));
			} else {
				tempvo.setIscompute(new Boolean(false));
			}
			tempvo.setWanmoney(rsVO.getNmoney());
			tempvo.setWanbeforemoney(rsVO.getNbeforemoney());
			tempvo.setWanceforedays(rsVO.getNceforedays());
			tempvo.setWanaftermoney(rsVO.getNaftermoney());
			tempvo.setWanafterdays(rsVO.getNafterdays());
			tempvo.setDaywage(rsVO.getDaywage());
			tempvo.setPsndocwavo(rsVO);
			tempvo.setPsnbasdocPK(rsVO.getPk_psndoc());
			tempvo.setSub_ts(rsVO.getWadocts());
			if (count <= 1) {
				if (count == 0) {
					UFLiteralDate tempBegindate = new UFLiteralDate(tempvo.getBegindate().getMillis());
					if ( !tempBegindate.after(enddate)) {
						v.addElement(tempvo);
						count++;
					}

				} else {
					((v.get(v.size() - 1))).setOldJiDangName(tempvo.getNowJiDangName());
					((v.get(v.size() - 1))).setOldwadocnmoney(tempvo.getWadocnmoney());
					((v.get(v.size() - 1))).setOldbegindate(tempvo.getBegindate());
					((v.get(v.size() - 1))).setOldenddate(tempvo.getEnddate()==null?v.get(v.size() - 1).getBegindate().getDateBefore(1):tempvo.getEnddate());
					((v.get(v.size() - 1))).setPre_sub_id(tempvo.getPk_psndoc_sub());
					((v.get(v.size() - 1))).setPre_sub_ts(tempvo.getSub_ts());
					((v.get(v.size() - 1))).setIscompute(((v.get(v.size() - 1))).getIscompute()&&tempvo.getIscompute());
					count++;
				}
			}
		}
		computevos = new PsncomputeVO[v.size()];
		if (v.size() > 0) {
			v.copyInto(computevos);
		}
		for(PsncomputeVO psncomputeVO:computevos){
			if(psncomputeVO.getEnddate()!=null&&psncomputeVO.getEnddate().before(enddate)){
				psncomputeVO.setOldJiDangName(psncomputeVO.getNowJiDangName());
				psncomputeVO.setOldwadocnmoney(psncomputeVO.getWadocnmoney());
				psncomputeVO.setOldbegindate(psncomputeVO.getBegindate());
				psncomputeVO.setOldenddate(psncomputeVO.getEnddate());
				psncomputeVO.setNowJiDangName(null);
				psncomputeVO.setWadocnmoney(UFDouble.ZERO_DBL);
				psncomputeVO.setBegindate(psncomputeVO.getEnddate().getDateAfter(1));
				psncomputeVO.setEnddate(null);
				psncomputeVO.setPre_sub_id(psncomputeVO.getPk_psndoc_sub());
				psncomputeVO.setPre_sub_ts(psncomputeVO.getSub_ts());
				psncomputeVO.setPk_psndoc_sub(null);
				psncomputeVO.setSub_ts(null);
			}
			//20151116 shenliangc 时点薪资计算保存后修改定调资数据的发放标志，“调薪前日期”没有更新。
				//20151117 shenliangc NCdp205543964 时点薪资调薪天数参数取“薪资期间计薪日天数”，时点薪资的基准天数取不到值
//			}else if(psncomputeVO.getEnddate()==null && psncomputeVO.getOldbegindate()==null){
//				psncomputeVO.setWanceforedays(UFDouble.ZERO_DBL);
//			}
		}
		return computevos;
	}


	@Override
	public boolean isCheckPsndocWaHave(WaLoginVO waLoginVO, String pk_psndoc) throws nc.vo.pub.BusinessException {

		boolean bool = false;
		try {
			PsndocWaDAO dmo = new PsndocWaDAO();
			bool = dmo.ischeck(waLoginVO, pk_psndoc);
		} catch (Exception e) {
			Logger.error(e);
		}
		return bool;
	}

	@Override
	public boolean isExistUnCaculatePsn(WaLoginVO waLoginVO, String deptpower, String powclpower)
			throws BusinessException {
		PsndocWaDAO dmo = new PsndocWaDAO();
		try{
			PsndocWaVO[] psndovVos= dmo.queryAllShowResult(waLoginVO, deptpower, powclpower);
			dmo.deletePsndocWaWithoutSD(waLoginVO, psndovVos);
			if(ArrayUtils.isEmpty(psndovVos)){
				return false;
			}
			for(PsndocWaVO psndovVo:psndovVos){
				if(StringUtils.isEmpty(psndovVo.getPk_psndoc_wa())){
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e);
		}

	}

}