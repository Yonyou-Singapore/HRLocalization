/**
 * 
 */
package nc.impl.wa.datainterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.itf.hr.datainterface.IDataIOQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItfTypeEnum;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.HRAggVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.util.SqlWrapper;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.util.ExceptionUtil;

/**
 * 批量维护 查询Service实现
 * 
 * @author xuanlt
 * 
 */
public class DataIOQueryServiceImpl implements IDataIOQueryService
{

	private BaseDAO baseDAO;



	private WaifsetDao dao = null;
	private static String Prefix_waitem = "wa_data.";
	private static String Prefix_bmitem = "bm_data.";

	private BaseDAO getBaseDAO()
	{
		if (null == baseDAO)
		{
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}



	// /**
	// * (1)添加排序条件
	// * (2)添加项目权限限制
	// * (3)globalVO必须有classpk cyear，cperiod
	// * @param headVO
	// * @param vo
	// * @param whrAppendStr
	// * @return
	// */
	// public HRAggVO queryDataByHeadVO(SuperVO headVO, String whrAppendStr)
	// {
	// if(StringHelper.isEmpty(whrAppendStr)){
	// whrAppendStr = "(1=1) order by iseq";
	// }
	//
	// if (headVO.getPrimaryKey() == null ||
	// headVO.getPrimaryKey().trim().length() == 0)
	// {
	// HRAggVO aggVO = new HRAggVO();
	// aggVO.setParentVO(headVO);
	// aggVO.setTableCodes(getBodyTableCodeArray());
	// aggVO.setBodyTableFKs(getBodyFkArray());
	// aggVO.setTableNames(getBodyTableCodeArray());
	//
	// return aggVO;
	// }
	//
	// try
	// {
	//
	// // WaGlobalVO vo = new WaGlobalVO();
	// // vo.setWaClassPK(getFrameUI().getCurrClassID());
	// // String yearAndPeriod = getFrameUI().getCurrPeriod();
	// // vo.setWaYear(yearAndPeriod.substring(0,4));
	// // vo.setWaPeriod(yearAndPeriod.substring(4));
	// // return
	// WADelegator.getWaifset().queryByHeadVO(headVO,getBodyVOClassArray(),
	// getBodyTableCodeArray(), getBodyFkArray(), whrAppendStr,
	// Global.getUserID(), new WaGlobalVO[]{vo});
	// //
	// }
	// catch (Exception e)
	// {
	// Logger.error(e.getMessage(), e);
	// }
	//
	// return null;
	// }

	private WaifsetDao getWaifsetDao()
	{
		try
		{
			if (dao == null)
			{
				dao = new WaifsetDao();
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
		}
		return dao;
	}

	/**
	 * 得到有权限的项目的集合
	 */
	@Override
	public HRAggVO queryByHeadVO(SuperVO headVO, Class[] bodyVOClasses, String[] bodyTableCodes, String[] bodyTableFks, String whrAppendStr, String userid, WaLoginContext waGlobalVOs) throws BusinessException
	{
		if (headVO != null && !StringUtils.isBlank(headVO.getPrimaryKey()))
		{
			Integer ifftype = ((HrIntfaceVO) headVO).getIiftype();

			if (ifftype.equals(ItfTypeEnum.WA_BANK.value()) || ifftype.equals(ItfTypeEnum.WA_BANK.value()))
			{
				return queryByHeadVOForWa(headVO, bodyVOClasses, bodyTableCodes, bodyTableFks, whrAppendStr, userid, waGlobalVOs);
			}
			else
			{
				return queryByHeadVOForBm(headVO, bodyVOClasses, bodyTableCodes, bodyTableFks, whrAppendStr, userid, waGlobalVOs);
			}
		}
		return null;
	}

	/**
	 * 得到有权限的项目的集合
	 */
	public HRAggVO queryByHeadVOForWa(SuperVO headVO, Class[] bodyVOClasses, String[] bodyTableCodes, String[] bodyTableFks, String whrAppendStr, String userid, WaLoginContext waGlobalVOs) throws BusinessException
	{
		if (headVO != null && !StringUtils.isBlank(headVO.getPrimaryKey()))
		{
			IPersistenceRetrieve service = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
			HRAggVO aggvo = service.queryByHeadVO(null, headVO, bodyVOClasses, bodyTableCodes, bodyTableFks, whrAppendStr);

			// 对于没有权限的项目进行过滤
			List<String> itemids = getWaifsetDao().getItemIDWithPower(userid, waGlobalVOs);

			FormatItemVO[] cavos = (FormatItemVO[]) aggvo.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
			Vector<FormatItemVO> vec = new Vector<FormatItemVO>();
			for (int index = 0; index < cavos.length; index++)
			{
				FormatItemVO itemvo = cavos[index];
				if (itemvo.getVcontent() != null && itemvo.getVcontent().startsWith(Prefix_waitem))
				{
					String itemid = itemvo.getVcontent().substring(Prefix_waitem.length());
					if (itemids.contains(itemid))
					{
						vec.add(itemvo);
					}
				}
				else
				{
					// 预置的项目都要添加
					vec.add(itemvo);
				}
			}
			FormatItemVO[] newarray = new FormatItemVO[vec.size()];
			aggvo.setTableVO(DataIOconstant.HR_DATAINTFACE_B, vec.toArray(newarray));
			return aggvo;
		}

		return null;
	}

	public HRAggVO queryByHeadVOForBm(SuperVO headVO, Class[] bodyVOClasses, String[] bodyTableCodes, String[] bodyTableFks, String whrAppendStr, String userid, WaLoginContext waGlobalVOs) throws BusinessException
	{
		if (headVO != null && !StringUtils.isBlank(headVO.getPrimaryKey()))
		{
			IPersistenceRetrieve service = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
			HRAggVO aggvo = service.queryByHeadVO(null, headVO, bodyVOClasses, bodyTableCodes, bodyTableFks, whrAppendStr);

			// 对于没有权限的项目进行过滤
			List<String> itemids = getWaifsetDao().getBMItemIDWithPower(userid, waGlobalVOs);

			FormatItemVO[] cavos = (FormatItemVO[]) aggvo.getTableVO(DataIOconstant.HR_DATAINTFACE_B);

			Vector<FormatItemVO> vec = new Vector<FormatItemVO>();

			for (int index = 0; index < cavos.length; index++)
			{
				FormatItemVO itemvo = cavos[index];
				if (itemvo.getVcontent() != null && itemvo.getVcontent().startsWith(Prefix_bmitem))
				{
					// 单独处理职工账号
					// bm_data.vpsnacccode.因为职工账号不属于可以分配的福利项目。但是却以bm_data 开始
					String itemid = itemvo.getVcontent().substring(Prefix_bmitem.length());
					if (itemid.equals("vpsnacccode"))
					{
						vec.add(itemvo);
					}
					else
					{
						if (itemids.contains(itemid))
						{
							vec.add(itemvo);
						}
					}
				}
				else
				{
					// 预置的项目都要添加
					vec.add(itemvo);
				}
			}
			FormatItemVO[] newarray = new FormatItemVO[vec.size()];
			aggvo.setTableVO(DataIOconstant.HR_DATAINTFACE_B, vec.toArray(newarray));
			return aggvo;
		}
		return null;
	}

	@Override
	public WaItemVO[] queryWaItemByUser(WaLoginContext waContext) throws nc.vo.pub.BusinessException
	{
		// undo
		return getWaifsetDao().queryWaItemByUser(waContext);
	}


	/**
	 * @author xuanlt on 2010-1-12
	 * @see nc.itf.hr.datainterface.IDataIOQueryService#queryByPk(java.lang.String)
	 */
	@Override
	public HrIntfaceVO queryByPk(String pk) throws BusinessException
	{
		return null;
	}

	@Override
	public GeneralVO[] queryBmExptData(FormatItemVO[] items, String condition) throws BusinessException
	{
		return getWaifsetDao().queryBmExptData(items, condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.hr.wa.IWaifset#queryWaExptData(nc.vo.hr.dataIO.FormatItemVO[],
	 * java.lang.String)
	 */
	@Override
	public GeneralVO[] queryWaExptData(FormatItemVO[] items, String condition) throws BusinessException
	{
		return getWaifsetDao().queryWaExptData(items, condition);

	}




	@Override
	public HashMap<String, ArrayList<HashMap<String, Object>>> queryWaDataByCondAndAggVOs(WaLoginContext context, AggHrIntfaceVO[] aggVOs, String condition) throws BusinessException
	{
		if (aggVOs == null)
		{
			return null;
		}
		StringBuffer sqlBuffer = new StringBuffer();

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,IHRWADataResCode.WADEFAULT,"wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(powerSql);
		}
		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(StringUtil.isEmptyWithTrim(powerSql) ? condition
					: " and " + condition);
		}

		HashMap<String, ArrayList<HashMap<String, Object>>> map = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		for (int i = 0; i < aggVOs.length; i++)
		{
			ArrayList<HashMap<String, Object>> datas = null;

			String cond = "";
			if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
			{
				// Commented on 2018-11-27: Remove the restriction where employee bank must be the same as company's
				HrIntfaceVO vo = (HrIntfaceVO) aggVOs[i].getParentVO();
//				cond = " t_bank.pk_banktype like '%%'";
				datas = queryWaDataByCondBank(context, sqlBuffer.toString(), cond);
				// Added on 2018-11-27 by Ethan, to avoid null pointer.
				if (datas != null || datas.size() > 0) {
					datas = mergeLocalTableWithWaData(datas, context);
				}
			}else{

				datas = queryWaDataByCond(context, sqlBuffer.toString(), cond);
			}

			map.put(aggVOs[i].getParentVO().getPrimaryKey(), datas);
		}



		return map;
	}
	
	// HR本地化需求：向已经输出的map里面继续添加薪资项目和人员基本信息
	private ArrayList<HashMap<String, Object>> mergeLocalTableWithWaData(ArrayList<HashMap<String, Object>> data, WaLoginContext context) {
		if (data == null || data.size() == 0) {
			return data;
		}
		IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		StringBuffer psndocCodes = new StringBuffer();
		
		// 查找人员信息集的所有可用字段
		StringBuffer infoSetQuery = new StringBuffer();
		infoSetQuery.append(" select * from hr_infoset_item where pk_infoset in (select pk_infoset from hr_infoset where table_code = 'bd_psndoc') ");
		ArrayList<InfoItemVO> infoItems = null;
		try {
			infoItems = (ArrayList<InfoItemVO>) queryBS.executeQuery(infoSetQuery.toString(), new BeanListProcessor(InfoItemVO.class));
		} catch (BusinessException e1) {
			Logger.error(e1);
			ExceptionUtils.wrapException("Query Infoset Item error:\n" + e1.getMessage(), e1);
		}
		
		// 查找当前组织期间的薪资发放项目（可用字段）
		StringBuffer classItemQuery = new StringBuffer();
		classItemQuery.append(" select * from wa_classitem where cperiod = '" + context.getCperiod() + "' and cyear = '" + context.getCyear());
		classItemQuery.append("' and pk_org = '" + context.getPk_org() + "' and pk_wa_class = '" + context.getClassPK() + "' and dr=0 ");
		ArrayList<WaClassItemVO> classItems = null;
		try {
			classItems = (ArrayList<WaClassItemVO>) queryBS.executeQuery(classItemQuery.toString(), new BeanListProcessor(WaClassItemVO.class));
		} catch (BusinessException e1) {
			Logger.error(e1);
			ExceptionUtils.wrapException("Query WaClass Item error:\n" + e1.getMessage(), e1);
		}
		
		// 添加人员基本信息
		for (HashMap<String, Object> entry : data) {
			if (entry.containsKey("bd_psndoccode") && entry.get("bd_psndoccode") != null) {
				String code = (String) entry.get("bd_psndoccode");
				psndocCodes.append("'" + code + "',");
			}
		}
		psndocCodes.deleteCharAt(psndocCodes.length() - 1);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from bd_psndoc where code in (" + psndocCodes.toString() + ") and dr = 0 ");
		ArrayList<PsndocVO> psndocVOs = null;
		try {
			psndocVOs = (ArrayList<PsndocVO>) queryBS.executeQuery(sb.toString(), new BeanListProcessor(PsndocVO.class));
		} catch (BusinessException e) {
			Logger.error(e);
			ExceptionUtils.wrapException("Query Personal Info error:\n" + e.getMessage(), e);
		}
		
		// 添加薪资发放信息
		StringBuffer psndocPks = new StringBuffer();
		if (psndocVOs != null && psndocVOs.size() > 0) {
			for (PsndocVO psndoc : psndocVOs) {
				psndocPks.append("'" + psndoc.getPk_psndoc() + "',");
			}
			psndocPks.deleteCharAt(psndocPks.length() - 1);
		} 
		
		StringBuffer sb1 = new StringBuffer();
		sb1.append(" select * from wa_data where checkflag = 'Y' and pk_psndoc in (" + psndocPks.toString() + ") and ");
		sb1.append(" cperiod = '" + context.getCperiod() + "' and cyear = '" + context.getCyear());
		sb1.append("' and pk_org = '" + context.getPk_org() + "' and pk_wa_class = '" + context.getClassPK() + "' ");
		sb1.append(" and dr = 0");
		ArrayList<DataVO> wadataVOs = null;
		try {
			wadataVOs = (ArrayList<DataVO>) queryBS.executeQuery(sb1.toString(), new BeanListProcessor(DataVO.class));
		} catch (BusinessException e) {
			Logger.error(e);
			ExceptionUtils.wrapException("Query Salary Data error:\n" + e.getMessage(), e);
		}
		
		// 添加两个map以便添加字段
		HashMap<String, PsndocVO> codeToPsnMap = new HashMap<String, PsndocVO>();
		HashMap<String, DataVO> psnpkToWaMap = new HashMap<String, DataVO>();
		if (psndocVOs != null && psndocVOs.size() > 0) {
			for (PsndocVO vo : psndocVOs) {
				codeToPsnMap.put(vo.getCode(), vo);
			}
		}
		if (wadataVOs != null && wadataVOs.size() > 0) {
			for (DataVO vo : wadataVOs) {
				psnpkToWaMap.put(vo.getPk_psndoc(), vo);
			}
		}
		
		// 为返回data添加字段
		for (HashMap<String, Object> obj : data) {
			// 添加人员基本信息字段
			String psnCode = obj.get("bd_psndoccode").toString();
			PsndocVO psndocVO = codeToPsnMap.get(psnCode);
			if (infoItems != null && infoItems.size() > 0) {
				for (InfoItemVO item : infoItems) {
					obj.put("bd_psndoc" + item.getItem_code(), psndocVO.getAttributeValue(item.getItem_code()));
				}
			}
			
			// 添加薪资发放项目字段
			String psnPk = psndocVO.getPk_psndoc();
			if (classItems != null && classItems.size() > 0) {
				for (WaClassItemVO item : classItems) {
					obj.put("wa_data" + item.getItemkey(), psnpkToWaMap.get(psnPk).getAttributeValue(item.getItemkey()));
				}
			}
		}
		
		
		return data;
	}

	@Override
	public ArrayList<HashMap<String, Object>> queryWaDataByCond(WaLoginContext context, String condition, String innerCondition) throws BusinessException
	{
		// IItemQueryService service =
		// NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService service = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// 拼接薪资项目,有可能有动态扩展
		WaClassItemVO[] itemVOs = null;
		if (context.getNodeCode().equals(DataIOconstant.NODE_BANK)) {
			itemVOs = service.queryItemWithAccount(context.getPk_org(),
					context.getPk_wa_class(), context.getCyear(),
					context.getCperiod());
		} else {
			itemVOs = service.queryAllClassItemInfos(context);
		}

		// WaItemVO[] itemVOs = service.queryByOrg(context.getPk_group(),
		// context.getPk_org(), null);
		String sql_wa_cols = "0";
		for (int i = 0; itemVOs != null && i < itemVOs.length; i++)
		{

			if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
			{
				// if (itemVOs[i].getItemkey().equals("f_3"))
				{
					String tab_col = " wa_data." + itemVOs[i].getItemkey() + " ";
					Integer bankaccount = itemVOs[i].getBankaccount();
					if (bankaccount == null)
					{
						// bankaccount = 1;
						continue;
					}
					// if(itemVOs[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value()))
					// {
					// tab_col =
					// "(case when wa_data.payacc=t_bank.payacc then "+tab_col+" else 0.00 end) ";
					tab_col = "(case when " + bankaccount + "=t_bank.payacc then " + tab_col + " else 0.00 end) ";

					String strAddOrSubFlag = " + ";
					// 目前，只有本次扣税，扣款合计，补发扣税是当成减项处理
					if (itemVOs[i].getItemkey().equals("f_2") || itemVOs[i].getItemkey().equals("f_5") || itemVOs[i].getItemkey().equals("f_9"))
					{
						strAddOrSubFlag = " - ";
					}
					sql_wa_cols += sql_wa_cols.equals("") ? strAddOrSubFlag + tab_col : strAddOrSubFlag + tab_col;
					// }
				}
			}
			else
			{
				// if(itemVOs[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value()))
				// {
				String tab_col = " wa_data." + itemVOs[i].getItemkey() + " as wa_data" + itemVOs[i].getItemkey();
				sql_wa_cols += sql_wa_cols.equals("") ? tab_col : "," + tab_col;
				// }
			}
		}
		String havingSql = "sum(" + sql_wa_cols + ")";
		if (!sql_wa_cols.equals(""))
		{
			if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
			{
				sql_wa_cols = "sum(" + sql_wa_cols + ") as wa_data" + DataIOconstant.BANK_FIX_COL_MONEY + " ";
			}
		}
		String bank_view = " ( select distinct bd_psnbankacc.pk_psndoc,bd_psnbankacc.payacc,bd_bankaccbas.accnum ,bd_bankdoc.pk_bankdoc,bd_banktype.pk_banktype,bd_banktype.code,bd_banktype.name,bd_banktype.name2,bd_banktype.name3,bd_banktype.name4,bd_banktype.name5,bd_banktype.name6" 
		+ " from bd_psnbankacc " + " left outer join bd_bankaccbas on bd_psnbankacc.pk_bankaccbas=bd_bankaccbas.pk_bankaccbas  and bd_bankaccbas.enablestate=2 " 
		//shenliangc 20140702 银行报盘增加户名
		+ " left outer join bd_defdoc on bd_defdoc.enablestate = 2 and bd_defdoc.pk_defdoclist = '1001ZZ10000000009031' and bd_defdoc.pk_defdoc=bd_bankaccbas.areacode "
		+ " left outer join bd_banktype on bd_banktype.pk_banktype=bd_bankaccbas.pk_banktype " + " left outer join bd_bankdoc on bd_bankdoc.pk_bankdoc=bd_bankaccbas.pk_bankdoc ) t_bank ";

		String bank_viewDataIo1 = " ( select  "+ SQLHelper.getMultiLangNameColumn("bd_banktype.name")+ " name,bd_banktype.pk_banktype from bd_banktype) bd_banktype1 ";
		String bank_viewDataIo2 = " ( select  "+ SQLHelper.getMultiLangNameColumn("bd_banktype.name")+ " name,bd_banktype.pk_banktype from bd_banktype) bd_banktype2 ";
		String bank_viewDataIo3 = " ( select  "+ SQLHelper.getMultiLangNameColumn("bd_banktype.name")+ " name,bd_banktype.pk_banktype from bd_banktype) bd_banktype3 ";
		//		String bank_viewDataIo2 = " ( select bd_bankaccbas.accname,bd_bankaccbas.accnum ,bd_psnbankacc.pk_psndoc from bd_bankaccbas join bd_psnbankacc on bd_bankaccbas.pk_bankaccbas = bd_psnbankacc.pk_bankaccbas and bd_psnbankacc.payacc = '2') bankaccbas2 ";
		//		String bank_viewDataIo3 = " ( select bd_bankaccbas.accname,bd_bankaccbas.accnum ,bd_psnbankacc.pk_psndoc from bd_bankaccbas join bd_psnbankacc on bd_bankaccbas.pk_bankaccbas = bd_psnbankacc.pk_bankaccbas and bd_psnbankacc.payacc = '3') bankaccbas3 ";

		// 暂时先将所有字段查询出来，优化时可以仅将薪资条中用户选择的项目列拼接出来即可
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select distinct " + (sql_wa_cols.equals("") ? "" : sql_wa_cols + ","));
		sbSql.append(" bd_psndoc.id as bd_psndocid,bd_psndoc.code as bd_psndoccode," + SQLHelper.getMultiLangNameColumn("bd_psnidtype.name") +
				" as bd_psndocidtype , "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  as bd_psndocname ");
		//shenliangc 20140702 银行报盘增加户名
//		sbSql.append(" , case when bd_psndoc.idtype = 0 then '身份证' when bd_psndoc.idtype = 1 then '军官证' when bd_psndoc.idtype = 2 then '护照' when bd_psndoc.idtype = 3 then '香港身份证' when bd_psndoc.idtype = 4 then '回乡证' when bd_psndoc.idtype = 5 then '台湾身份证' when bd_psndoc.idtype = 6 then '澳门身份证' when bd_psndoc.idtype = 7 then '台胞证' when bd_psndoc.idtype = 8 then '外国人永久居留证' end as bd_psndocidtype , "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  as bd_psndocname ");
		sbSql.append(" , "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ "  as bd_psnclname ,om_post.postname as om_postpostname,om_job.jobname as om_jobjobname,om_postseries.postseriesname as om_postseriespostseriesname ");
		sbSql.append(" ,org_dept_v.code as org_deptcode , "+ SQLHelper.getMultiLangNameColumn("org_dept_v.name")+ "  as org_deptname ");


		if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			sbSql.append(" ,t_bank.accnum as bd_bankaccbasaccnum,t_bank.pk_banktype as bd_banktypepk_banktype , t_bank.code as bd_banktypecode,  "+ SQLHelper.getMultiLangNameColumn("t_bank.name")+ "  as bd_banktypename ");
		}
		else
		{
			sbSql.append(",  bd_banktype1.name   as bd_banktype1name, wa_data.pk_bankaccbas1 as  wa_datapk_bankaccbas1");
			sbSql.append(",  bd_banktype2.name   as bd_banktype2name, wa_data.pk_bankaccbas2 as wa_datapk_bankaccbas2");
			sbSql.append(",  bd_banktype3.name   as bd_banktype3name, wa_data.pk_bankaccbas3 as wa_datapk_bankaccbas3");
		}
		//		else
		//		{
		//			sbSql.append(", bankaccbas1.accname as bd_bankaccbasaccname1, bankaccbas1.accnum as  bd_bankaccbasaccnum1");
		//			sbSql.append(", bankaccbas2.accname  as bd_bankaccbasaccname2, bankaccbas2.accnum as bd_bankaccbasaccnum2");
		//			sbSql.append(", bankaccbas3.accname  as bd_bankaccbasaccname3, bankaccbas3.accnum as bd_bankaccbasaccnum3");
		//		}
		//guoqt数据接口增加导出员工号
		sbSql.append(" ,hi_psnjob.CLERKCODE  as hi_psnjobclerkcode ");
		// 20150122 shenliangc 碧桂园数据接口“待选薪资项目”添加“任职组织”字段 begin
		sbSql.append(" , org_orgs.name  as  org_orgsname");
		// end
		sbSql.append(" from wa_data ");
		// 20150122 shenliangc 碧桂园数据接口“待选薪资项目”添加“任职组织”字段 begin
		sbSql.append(" inner join org_orgs on org_orgs.pk_org = wa_data.workorg ");
		// end
		sbSql.append(" inner join wa_waclass on wa_data.pk_wa_class=wa_waclass.pk_wa_class ");
		sbSql.append(" inner join bd_psndoc on wa_data.pk_psndoc=bd_psndoc.pk_psndoc ");
		sbSql.append(" inner join hi_psnjob on wa_data.pk_psnjob=hi_psnjob.pk_psnjob ");
		sbSql.append(" inner join bd_psncl on hi_psnjob.pk_psncl=bd_psncl.pk_psncl ");
		sbSql.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sbSql.append(" left join om_post on om_post.pk_post=hi_psnjob.pk_post ");
		sbSql.append(" left join om_job on om_job.pk_job=hi_psnjob.pk_job ");
		sbSql.append(" left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
		sbSql.append(" left join om_postseries on om_postseries.pk_postseries=hi_psnjob.pk_postseries ");

		// " inner join "+period_view+" on (t.pk_wa_class=wa_data.pk_wa_class and t.cyear=wa_data.cyear and t.cperiod=wa_data.cperiod) ";
		if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			sbSql.append(" left outer join " + bank_view + " on (t_bank.pk_psndoc=wa_data.pk_psndoc )");
		}
		else
		{
			sbSql.append(" left outer join " + bank_viewDataIo1 + " on (bd_banktype1.pk_banktype=wa_data.pk_banktype1 )");
			sbSql.append(" left outer join " + bank_viewDataIo2 + " on (bd_banktype2.pk_banktype=wa_data.pk_banktype2 )");
			sbSql.append(" left outer join " + bank_viewDataIo3 + " on (bd_banktype3.pk_banktype=wa_data.pk_banktype3 )");
		}

		if (context.getWaLoginVO().getBatch() != null
				&& context.getWaLoginVO().getBatch() > 100) {
			sbSql.append(" where wa_data.pk_org='" + context.getPk_org()
					+ "' and wa_data.pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
					+ context.getPk_prnt_class() + "' and cyear = '" + context.getWaYear()
					+ "' and cperiod = '" + context.getWaPeriod() + "' and batch > 100 )"
					+ " and wa_data.cyear='" + context.getWaYear()
					+ "' and wa_data.cperiod='" + context.getWaPeriod()
					+ "' and wa_data.stopflag='N' ");
		} else {
			sbSql.append(" where wa_data.pk_org='" + context.getPk_org()
					+ "' and wa_data.pk_wa_class = '"
					+ context.getPk_wa_class() + "' and wa_data.cyear='"
					+ context.getWaYear() + "' and wa_data.cperiod='"
					+ context.getWaPeriod() + "' and wa_data.stopflag='N' ");
		}

		if (condition != null && !condition.trim().equals(""))
		{
			sbSql.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where " + condition + ") ");
		}
		// 测试暂时注释掉
		if (innerCondition != null && !innerCondition.trim().equals(""))
		{
			sbSql.append(" and " + innerCondition);
		}
		//数据权限
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sbSql.append(" and " + powerSql);
		}

		if (context.getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			sbSql.append(" group by bd_psndoc.id ,bd_psndoc.code, "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  " + " ,org_dept_v.code,  "+ SQLHelper.getMultiLangNameColumn("org_dept_v.name")+ "  " + " ,t_bank.accnum,t_bank.pk_banktype,t_bank.code, "+ SQLHelper.getMultiLangNameColumn("t_bank.name")+ " , "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ " ,om_post.postname,om_job.jobname,om_postseries.postseriesname ");
			sbSql.append(" having ("+havingSql+") <> 0");
			sbSql.append(" order by t_bank.code,org_dept_v.code,bd_psndoc.code ");
		}
		else
		{
			sbSql.append(" order by org_dept_v.code,bd_psndoc.code ");
		}

		BaseDAO dao = getBaseDAO();
		WaBusilogUtil.writePaydataQueryBusiLog(context);

		return (ArrayList<HashMap<String, Object>>) dao.executeQuery(sbSql.toString(), new MapListProcessor());
	}

	public ArrayList<HashMap<String, Object>> queryWaDataByCondBank(WaLoginContext context, String condition, String innerCondition) throws BusinessException
	{
		// IItemQueryService service =
		// NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService service = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// 拼接薪资项目,有可能有动态扩展
		WaClassItemVO[] itemVOs = null;
		itemVOs = service.queryItemWithAccount(context.getPk_org(),
				context.getPk_wa_class(), context.getCyear(),
				context.getCperiod());

		// WaItemVO[] itemVOs = service.queryByOrg(context.getPk_group(),
		// context.getPk_org(), null);
		// 暂时先将所有字段查询出来，优化时可以仅将薪资条中用户选择的项目列拼接出来即可
		StringBuffer sbSql = new StringBuffer("");

		for (int i = 0; itemVOs != null && i < itemVOs.length; i++)
		{

			String tab_col = " wa_data." + itemVOs[i].getItemkey() + " ";
			Integer bankaccount = itemVOs[i].getBankaccount();
			if (bankaccount == null)
			{
				// bankaccount = 1;
				continue;
			}
			// if(itemVOs[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value()))
			// {
			// tab_col =
			// "(case when wa_data.payacc=t_bank.payacc then "+tab_col+" else 0.00 end) ";
			tab_col = "(case when " + bankaccount + "=t_bank.payacc then " + tab_col + " else 0.00 end) ";

			String sql_wa_cols = tab_col+" as wa_data" + DataIOconstant.BANK_FIX_COL_MONEY + " ";

			String bank_view = " ( select distinct bd_psnbankacc.pk_psndoc,bd_psnbankacc.payacc,bd_bankaccbas.accnum ,bd_bankdoc.pk_bankdoc," +
					//shenliangc 20141218 银行报盘增加户名最终合并
					" bd_bankaccbas.accname ,bd_bankaccbas.province,bd_bankaccbas.city,bd_bankaccbas.tel,bd_defdoc.code as dcode, " 
					+/*20141216 shenliangc 银行报盘增加“开户银行”*/" bd_bankdoc.name as bankname," + 
					"bd_banktype.pk_banktype,bd_banktype.code,bd_banktype.name,bd_banktype.name2,bd_banktype.name3,bd_banktype.name4," +
					"bd_banktype.name5,bd_banktype.name6" + " from bd_psnbankacc "
					+ " left outer join bd_bankaccbas on bd_psnbankacc.pk_bankaccbas=bd_bankaccbas.pk_bankaccbas   and bd_bankaccbas.enablestate=2 "
					//shenliangc 20140702 银行报盘增加户名
					+ " left outer join bd_defdoc on bd_defdoc.enablestate = 2 and bd_defdoc.pk_defdoclist = '1001ZZ10000000009031' and bd_defdoc.pk_defdoc=bd_bankaccbas.areacode "
					+ " left outer join bd_banktype on bd_banktype.pk_banktype=bd_bankaccbas.pk_banktype "
					+ " left outer join bd_bankdoc on bd_bankdoc.pk_bankdoc=bd_bankaccbas.pk_bankdoc ) t_bank ";

			if(sbSql.length()>10){
				sbSql.append(" union ");
			}

			sbSql.append(" select distinct " + (sql_wa_cols.equals("") ? "" : sql_wa_cols + ","));
			// 20151007 by guopeng NCdp205487082 添加任职组织、员工号、证件类型
			sbSql.append(" bd_psndoc.id as bd_psndocid,bd_psndoc.code as bd_psndoccode," + SQLHelper.getMultiLangNameColumn("bd_psnidtype.name") +
				" as bd_psndocidtype, "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  as bd_psndocname ");
			sbSql.append(" , "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ "  as bd_psnclname ,om_post.postname as om_postpostname,om_job.jobname as om_jobjobname,om_postseries.postseriesname as om_postseriespostseriesname ");
			sbSql.append(" ,org_dept_v.code as org_deptcode , "+ SQLHelper.getMultiLangNameColumn("org_dept_v.name")+ "  as org_deptname ");


			
			//shenliangc 20140702 银行报盘增加户名
			sbSql.append(" ,t_bank.accnum as bd_bankaccbasaccnum,t_bank.accname as bd_bankaccbasaccname," +
					"t_bank.province as bd_bankaccbasprovince,t_bank.city as bd_bankaccbascity,t_bank.tel as bd_bankaccbastel,t_bank.dcode as bd_defdoccode," +
					"t_bank.pk_banktype as bd_banktypepk_banktype , t_bank.code as bd_banktypecode,  "
					+ SQLHelper.getMultiLangNameColumn("t_bank.name")+ "  as bd_banktypename ");


			sbSql.append("   ,    '"+itemVOs[i].getItemkey()+"' as itemkey ," + /*20141216 shenliangc 银行报盘增加“开户银行”*/" t_bank.bankname as bankname");

			sbSql.append(" ,hi_psnjob.CLERKCODE  as hi_psnjobclerkcode ");
			sbSql.append(" , org_orgs.name  as  org_orgsname");
			sbSql.append(" from wa_data ");
			// 20151007 by guopeng NCdp205487082 添加任职组织、员工号、证件类型
			sbSql.append(" inner join org_orgs on org_orgs.pk_org = wa_data.workorg ");
			sbSql.append(" inner join wa_waclass on wa_data.pk_wa_class=wa_waclass.pk_wa_class ");
			sbSql.append(" inner join bd_psndoc on wa_data.pk_psndoc=bd_psndoc.pk_psndoc ");
			sbSql.append(" inner join hi_psnjob on wa_data.pk_psnjob=hi_psnjob.pk_psnjob ");
			sbSql.append(" inner join bd_psncl on hi_psnjob.pk_psncl=bd_psncl.pk_psncl ");
			sbSql.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
			sbSql.append(" left join om_post on om_post.pk_post=hi_psnjob.pk_post ");
			sbSql.append(" left join om_job on om_job.pk_job=hi_psnjob.pk_job ");
			// 20151007 by guopeng NCdp205487082 添加任职组织、员工号、证件类型
			sbSql.append(" left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
			sbSql.append(" left join om_postseries on om_postseries.pk_postseries=hi_psnjob.pk_postseries ");


			sbSql.append(" left outer join " + bank_view + " on (t_bank.pk_psndoc=wa_data.pk_psndoc " +
					"and  t_bank.accnum = wa_data.pk_bankaccbas"+bankaccount+" and t_bank.payacc ="+bankaccount+")");



			if (context.getWaLoginVO().getBatch() != null
					&& context.getWaLoginVO().getBatch() > 100) {
				sbSql.append(" where wa_data.pk_org='" + context.getPk_org()
						+ "' and wa_data.pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ context.getPk_prnt_class() + "' and cyear = '" + context.getWaYear()
						+ "' and cperiod = '" + context.getWaPeriod() + "' and batch > 100 )"
						+ " and wa_data.cyear='" + context.getWaYear()
						+ "' and wa_data.cperiod='" + context.getWaPeriod()
						+ "' and wa_data.stopflag='N' ");
			} else {
				sbSql.append(" where wa_data.pk_org='" + context.getPk_org()
						+ "' and wa_data.pk_wa_class = '"
						+ context.getPk_wa_class() + "' and wa_data.cyear='"
						+ context.getWaYear() + "' and wa_data.cperiod='"
						+ context.getWaPeriod() + "' and wa_data.stopflag='N' ");
			}

			if (condition != null && !condition.trim().equals(""))
			{
				sbSql.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where " + condition + ") ");
			}
			// 测试暂时注释掉
			if (innerCondition != null && !innerCondition.trim().equals(""))
			{
				sbSql.append(" and " + innerCondition);
			}
			//guoqt  NCdp205018761 银行报盘不导出负数及0
			sbSql.append(" and wa_data." + itemVOs[i].getItemkey() + "  > 0");
			
			//数据权限
			String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
					HICommonValue.RESOUCECODE_6007PSNJOB,
					IHRWADataResCode.WADEFAULT, "wa_data");
			if (!StringUtil.isEmptyWithTrim(powerSql)) {
				sbSql.append(" and " + powerSql);
			}

		}

		//20151112 shenliangc 客户验证版发现问题：java.sql.SQLException: 要执行的 SQL 语句不得为空白或空值
		if(sbSql.length() == 0){
			return null;
		}
		
		if(sbSql.length()>50){
			sbSql.insert(0, "select * from (");
			sbSql.append(" ) as pp order by bd_psndoccode  ");
		}


		BaseDAO dao = getBaseDAO();
		WaBusilogUtil.writePaydataQueryBusiLog(context);

		return (ArrayList<HashMap<String, Object>>) dao.executeQuery(sbSql.toString(), new MapListProcessor());
	}

	@Override
	public void syncAggHrIntfaceVO(AggHrIntfaceVO[] aggVOs, String lastCYear,
			String lastCPeriod) throws BusinessException {
		if (ArrayUtils.isEmpty(aggVOs)) {
			return;
		}
		AggHrIntfaceVO aggVO = aggVOs[0];
		AggHrIntfaceVO nextVO = queryNextAggHrIntfaceVO(aggVO);
		if(nextVO != null){
			lastCYear = ((HrIntfaceVO)nextVO.getParentVO()).getCyear();
			lastCPeriod = ((HrIntfaceVO)nextVO.getParentVO()).getCperiod();
		}
		PeriodVO[] periodVOs = getWaifsetDao().queryPeriodVOs(aggVO, lastCYear, lastCPeriod);
		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
		if(!ArrayUtils.isEmpty(periodVOs)){
			for(int index = 0; index < periodVOs.length; index++){
				if (nextVO != null && (periodVOs[index].getCyear().equals(lastCYear)&&periodVOs[index].getCperiod().equals(lastCPeriod))) {
					continue;
				}
				String pk = "";
				//新建对象复制属性那个，不能直接引用
				HrIntfaceVO intfaceVO = new HrIntfaceVO();
				intfaceVO = (HrIntfaceVO) aggVO.getParentVO().clone();
				intfaceVO.setPk_dataio_intface(null);
				intfaceVO.setStatus(VOStatus.NEW);
				intfaceVO.setCyear(periodVOs[index].getCyear());
				intfaceVO.setCperiod(periodVOs[index].getCperiod());
				pk = persistence.insertVO(null,intfaceVO , null);

				FormatItemVO[] fitemVOs = (FormatItemVO[])aggVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
				if (!ArrayUtils.isEmpty(fitemVOs)) {
					FormatItemVO[] fcopyItemVOs = new FormatItemVO[fitemVOs.length];
					if (fitemVOs != null)
					{
						for (int i = 0; i < fitemVOs.length; i++){
							//新建对象复制属性那个，不能直接引用
							FormatItemVO itemVO = new FormatItemVO();
							//								BeanUtils.copyProperties(fitemVOs[i],itemVO);
							itemVO = (FormatItemVO) fitemVOs[i].clone();
							itemVO.setIfid(pk);
							itemVO.setPrimaryKey(null);
							fcopyItemVOs[i] = itemVO;
						}
						persistence.insertVOArray(null, fcopyItemVOs, null);
					}
				}
				IfsettopVO[] iitemVOs = (IfsettopVO[])aggVO.getTableVO(DataIOconstant.HR_IFSETTOP);
				if (!ArrayUtils.isEmpty(iitemVOs)) {
					IfsettopVO[] icopyItemVOs = new IfsettopVO[iitemVOs.length];
					if (iitemVOs != null)
					{
						for (int i = 0; i < iitemVOs.length; i++){
							//新建对象复制属性那个，不能直接引用
							IfsettopVO itemVO = new IfsettopVO();
							//								BeanUtils.copyProperties(iitemVOs[i],itemVO);
							itemVO = (IfsettopVO) iitemVOs[i].clone();
							itemVO.setIfid(pk);
							itemVO.setPrimaryKey(null);
							icopyItemVOs[i] = itemVO;
						}
						persistence.insertVOArray(null, icopyItemVOs, null);
					}
				}

			}
		}
	}

	private AggHrIntfaceVO queryNextAggHrIntfaceVO(AggHrIntfaceVO aggVO){
		HrIntfaceVO parentVO = (HrIntfaceVO) aggVO.getParentVO();

		StringBuffer sbSlipWhere = new StringBuffer("classid='").append(
				parentVO.getClassid()).append("' and vifname = '"+parentVO.getVifname()+"' and (cyear>'").append(
						parentVO.getCyear()).append("' or (cyear='").append(
								parentVO.getCyear()).append("' and cperiod>'").append(
										parentVO.getCperiod()).append("')) ");

		try {

			List<AggHrIntfaceVO> aggVOList = (List<AggHrIntfaceVO>) MDPersistenceService
					.lookupPersistenceQueryService().queryBillOfVOByCondWithOrder(
							AggHrIntfaceVO.class, sbSlipWhere.toString(), true,
							false, new String[] { "cyear", "cperiod" });
			if (aggVOList != null && !aggVOList.isEmpty() && aggVOList.size() > 0) {
				// 当前工资条的期间，后续期间中最近的薪资条
				return aggVOList.get(aggVOList.size() - 1);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	//patch to v636 wangqim  NCdp205217332
	@Override
	public List<String[]> queryPsndocVOBycodes(String[] codes)
			throws BusinessException {
		//20150925 shenliang 为王琦同学填坑。拼装SQL之前需要人员编码数组判空。 begin
		Object result = null;
		InSQLCreator inSQLCreator=new InSQLCreator();
		try {
			if (!ArrayUtils.isEmpty(codes)) {
//				StringBuffer psncodes = new StringBuffer();
//				for (String s : codes) {
//					psncodes.append(",").append("'").append(s).append("'");
//				}
				String sql = "select bd_psndoc.CODE,"
						// 2015-11-14 zhousze 获取name时需要处理多语，现在处理多语 begin
						+ SQLHelper.getMultiLangNameColumn("bd_psndoc.NAME")
						// +
						// "as name from bd_psndoc where bd_psndoc.code in("+psncodes.substring(1)+");";
//		20160119  xiejie3  NCdp205575016  薪资接口导入，选择excel后点显示（excel中有9000人），控制台报：ORA-01795: 列表中的最大表达式数为 1000				
						//20160119 shenliangc NCdp205575004 薪资接口--数据导入，控制台报错，SQL写的少了空格sql:select bd_psndoc.CODE,bd_psndoc.NAMEas name……
						+ " as name from bd_psndoc where bd_psndoc.code in("
						+ inSQLCreator.getInSQL(codes) + ");";
//		end  	NCdp205575016	
				// end

				result = new BaseDAO().executeQuery(sql,
						new ResultSetProcessor() {
							@Override
							public Object handleResultSet(ResultSet rs)
									throws SQLException {
								List<String[]> list = new ArrayList<String[]>();
								while (rs.next()) {
									String[] strs = new String[] {
											rs.getString("CODE"),
											rs.getString("NAME") };
									list.add(strs);
								}
								return list;
							}
						});
			}
		} finally {
			inSQLCreator.clear();
		}
		//20150925 shenliang 为王琦同学填坑。拼装SQL之前需要人员编码数组判空。 end
		return (List<String[]>)result;
	}
}
