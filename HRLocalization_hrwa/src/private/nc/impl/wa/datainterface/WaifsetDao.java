package nc.impl.wa.datainterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.DataFromEnum;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItfTypeEnum;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wabm.util.REUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author xuanlt
 */
public class WaifsetDao extends BaseDAOManager {
	//主卡银行账号、副卡1银行账号、副卡2银行账号、代发工资
	private final static String[] vcontents = new String[]{"wa_data.pk_bankaccbas1","wa_data.pk_bankaccbas2","wa_data.pk_bankaccbas3","wa_data.bank_fix_col_money"};

	private String getJoinCause(Vector<String> vector){
		StringBuilder sbd = new StringBuilder();
		if (vector.contains("bd_psndoc"))
		{
			sbd.append( " inner join bd_psndoc on wa_data.psnid=bd_psndoc.pk_psndoc and bd_psndoc.dr=0 ");
		}
		if (vector.contains("bd_psncl"))
		{
			sbd.append( " inner join  bd_psncl on wa_data.psnclid=bd_psncl.pk_psncl and bd_psncl.dr=0 ");
		}
		if(vector.contains("bd_psnbasdoc")){
			sbd.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc and bd_psnbasdoc.dr=0 ");
		}
		if (vector.contains("bd_deptdoc"))
		{
			sbd.append(" inner join  bd_deptdoc on wa_data.deptid=bd_deptdoc.pk_deptdoc and bd_deptdoc.dr=0 ");
		}
		if (vector.contains("wa_bank"))
		{//左外连接
			sbd.append(" left outer join wa_bank on wa_psn.bankid=wa_bank.pk_wa_bank and wa_bank.dr=0 ");
		}
		if (vector.contains("bd_corp"))
		{//左外连接
			sbd.append(" inner join bd_corp on bd_psndoc.pk_corp= bd_corp.pk_corp and  bd_corp.dr=0");
		}
		return sbd.toString();
	}

	private String getJoinCauseForBm(Vector<String> vector){
		/**
		 * 查找的顺序不能改变
		 */
		StringBuilder sbd = new StringBuilder();
		if (vector.contains("bd_psndoc"))
		{
			sbd.append( " inner join bd_psndoc on bm_data.psnid=bd_psndoc.pk_psndoc and bd_psndoc.dr=0 ");
		}
		if (vector.contains("bd_psncl"))
		{
			sbd.append( " inner join  bd_psncl on wa_data.psnclid=bd_psncl.pk_psncl and bd_psncl.dr=0 ");
		}
		if(vector.contains("bd_psnbasdoc")){
			sbd.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc and bd_psnbasdoc.dr=0 ");
		}
		if (vector.contains("bd_deptdoc"))
		{
			sbd.append(" inner join  bd_deptdoc on bm_data.deptid=bd_deptdoc.pk_deptdoc and bd_deptdoc.dr=0 ");
		}


		if (vector.contains("bd_corp"))
		{//左外连接
			sbd.append(" inner join bd_corp on bd_psndoc.pk_corp= bd_corp.pk_corp and  bd_corp.dr=0");
		}
		return sbd.toString();
	}

	public GeneralVO[] queryBmExptData(FormatItemVO[] items, String condition)
			throws DAOException {
		ArrayList<String> tableAndFld = new ArrayList<String>();

		StringBuilder sbd = new StringBuilder();
		sbd.append("select ");
		for (int index = 0; index < items.length; index++) {
			if(items[index].getIsourcetype() == DataFromEnum.SINGLE.value() ){
				//根据数据类型决定是否添加 单引号
				if(items[index].getIfieldtype()==(Integer)FieldTypeEnum.DEC.value()){
					sbd.append( items[index].getVcontent()+ " as " + DataIOconstant.PREFIX_SINGLESOURCETYPE+REUtil.REReplace(DataIOconstant.strReg, items[index].getVfieldname().toLowerCase(), "") );
				}else{
					sbd.append( "'" + items[index].getVcontent()+ "' as " + DataIOconstant.PREFIX_SINGLESOURCETYPE+REUtil.REReplace(DataIOconstant.strReg, items[index].getVfieldname().toLowerCase(), "") );
				}

			}else if(items[index].getIsourcetype() == DataFromEnum.FORMULAR.value()){
				sbd.append( items[index].getVcontent());
				tableAndFld.add(items[index].getVcontent());
			}
			if (index < items.length-1) {
				sbd.append(" , ");
			}
		}

		//增加人员编码，从而按照人员编码排序
		sbd.append(",bd_psndoc.psncode ");
		tableAndFld.add("bd_psndoc.psncode");

		//0-新增,1-正常 2-封存 3-已转出，4-已销户,5 已支取,6-已启封(2 3 4 属于"非正常"状态)
		sbd.append(" from bm_data inner join  bm_psndata on bm_data.psnid=bm_psndata.psnid  " +
				" and bm_data.bmclassid = bm_psndata.bmclassid and bm_data.cfundyear = bm_psndata.cfundyear and bm_data.cfundmonth = bm_psndata.cfundmonth  and bm_data.istateflag in (0,1,5,6)");

		Vector<String> vector = REUtil.getUsedTable(tableAndFld,condition);
		sbd.append(getJoinCauseForBm(vector));
		if(!StringUtils.isBlank(condition)){
			if(condition.trim().startsWith("and")){
				sbd.append(" where " + condition.trim().substring(3));
			}else{
				sbd.append(" where " + condition.trim());
			}
		}

		sbd.append(" order by bd_psndoc.psncode ");

		return (GeneralVO[]) getBaseDao().executeQuery(sbd.toString(), new GeneralVOProcessor(GeneralVO.class));
	}

	public GeneralVO[] queryWaExptData(FormatItemVO[] items, String condition)
			throws DAOException {
		ArrayList<String> tableAndFld = new ArrayList<String>();
		StringBuilder sbd = new StringBuilder();
		sbd.append("select ");
		for (int index = 0; index < items.length; index++) {
			if(items[index].getIsourcetype() == DataFromEnum.SINGLE.value() ){
				//根据数据类型决定是否添加 单引号
				if(items[index].getIfieldtype()==(Integer)FieldTypeEnum.DEC.value()){
					sbd.append( items[index].getVcontent()+ " as " + DataIOconstant.PREFIX_SINGLESOURCETYPE+REUtil.REReplace(DataIOconstant.strReg, items[index].getVfieldname().toLowerCase(), "") );
				}else{
					sbd.append( "'" + items[index].getVcontent()+ "' as " + DataIOconstant.PREFIX_SINGLESOURCETYPE+REUtil.REReplace(DataIOconstant.strReg, items[index].getVfieldname().toLowerCase(), "") );
				}
			}else if(items[index].getIsourcetype() == DataFromEnum.FORMULAR.value()){
				sbd.append( items[index].getVcontent());
				tableAndFld.add(items[index].getVcontent());
			}
			if (index < items.length-1) {
				sbd.append(" , ");
			}
		}
		//增加人员编码，从而按照人员编码排序
		sbd.append(",bd_psndoc.psncode ");
		tableAndFld.add("bd_psndoc.psncode");

		sbd.append(" from wa_data inner join  wa_psn on wa_data.psnid=wa_psn.psnid  " +
				" and wa_data.classid = wa_psn.classid and wa_data.cyear = wa_psn.cyear  and wa_data.cperiod = wa_psn.cperiod and  wa_psn.istopflag = 0 ");
		/*必须是尚未停发的人员*/

		Vector<String> vector = REUtil.getUsedTable(tableAndFld,condition);
		sbd.append(getJoinCause(vector));
		if(!StringUtils.isBlank(condition)){
			if(condition.trim().startsWith("and")){
				sbd.append(" where " + condition.trim().substring(3));
			}else{
				sbd.append(" where " + condition.trim());
			}
		}
		sbd.append(" order by bd_psndoc.psncode ");

		return (GeneralVO[]) getBaseDao().executeQuery(sbd.toString(), new GeneralVOProcessor(GeneralVO.class));
	}

	/**
	 * 得到有权限的薪资项目的集合
	 * select itemid from wa_item
	 */
	@SuppressWarnings("unchecked")
	public List<String> getItemIDWithPower(String userid, WaLoginContext waGlobalVOs) throws DAOException{

		String itemPowerSql = 	getItemIDSqlByUseridClassid(userid, waGlobalVOs);
		return(List<String>) getBaseDao().executeQuery(itemPowerSql,new ColumnListProcessor());

	}

	public List<String> getBMItemIDWithPower(String userid,WaLoginContext waGlobalVOs) throws DAOException{

		String itemPowerSql = 	getBMItemIDSqlByUseridClassid(userid, waGlobalVOs);
		return(List<String>) getBaseDao().executeQuery(itemPowerSql,new ColumnListProcessor());

	}


	public String getItemIDSqlByUseridClassid(String userid, WaLoginContext waGlobalVOs) {
		return ItemPowerUtil.getItemPowerCode(waGlobalVOs);
		//		String cyear = waGlobalVOs.getWaYear();
		//		String cperiod = waGlobalVOs.getWaPeriod();
		//		StringBuffer sqlB = new StringBuffer();
		//		sqlB.append("select  wa_item.iitemid "); // 1
		//		sqlB.append("  from wa_item ");
		//		sqlB.append(" where wa_item.pk_wa_item in ");
		//		sqlB.append("       ((select wa_itemright.pk_wa_item ");
		//		sqlB.append("          from wa_classitem, wa_itemright ");
		//		sqlB.append("         where wa_classitem.pk_wa_item = wa_itemright.pk_wa_item ");
		//		sqlB.append("           and wa_itemright.pk_wa_class = wa_classitem.pk_wa_class ");
		//		sqlB.append("           and wa_itemright.pk_wa_class ='"+ waGlobalVOs.getPk_wa_class()+"' ");
		//		sqlB.append("           and wa_classitem.cyear = '" + cyear + "' ");
		//		sqlB.append("           and wa_classitem.cperiod = '" + cperiod + "' ");
		//		sqlB.append("           and wa_itemright.cuserid = '" + userid + "') union  ");
		////		sqlB.append("    or wa_item.pk_wa_item in ");
		//		sqlB.append("       (select wa_itemright_group.pk_wa_item ");
		//		sqlB.append("          from wa_itemright_group, wa_classitem ");
		//		sqlB.append("         where wa_itemright_group.pk_wa_class ='"+ waGlobalVOs.getPk_wa_class()+"'");
		//		sqlB.append("           and wa_itemright_group.pk_wa_class = wa_classitem.pk_wa_class ");
		//		sqlB.append("           and wa_itemright_group.pk_wa_item = wa_classitem.pk_wa_item ");
		//		sqlB.append("           and wa_classitem.cyear = '" + cyear + "' ");
		//		sqlB.append("           and wa_classitem.cperiod = '" + cperiod + "' ");
		//		sqlB.append("           and wa_itemright_group.groupid in ");
		//		sqlB.append("               (select sm_role.pk_role ");
		//		sqlB.append("                  from sm_role, sm_user_role ");
		//		sqlB.append("                 where sm_role.pk_role = sm_user_role.pk_role ");
		//		sqlB.append("                   and sm_role.dr = 0 ");
		//		sqlB.append("                   and sm_user_role.dr = 0 ");
		//		sqlB.append("                   and sm_user_role.cuserid = '" + userid + "'))) ");
		//
		//		return sqlB.toString();
	}

	public String getBMItemIDSqlByUseridClassid(String userid, WaLoginContext waGlobalVOs) {
		String cyear = waGlobalVOs.getWaYear();
		String cperiod = waGlobalVOs.getWaPeriod();
		StringBuffer sqlB = new StringBuffer();

		sqlB.append(" select  bm_item.vitemfld  ");
		sqlB.append(" from bm_item ");
		sqlB.append("	 where bm_item.pk_bm_item in ");
		sqlB.append("	       ((select bm_itemright.pk_bm_item ");
		sqlB.append("	          from bm_classitem, bm_itemright ");
		sqlB.append("	         where bm_classitem.itemid = bm_itemright.pk_bm_item ");
		sqlB.append("	           and bm_itemright.pk_bm_class = bm_classitem.BMCLASSID ");
		sqlB.append("	           and bm_itemright.pk_bm_class ='"+ waGlobalVOs.getPk_wa_class()+"' ");
		sqlB.append("	           and bm_classitem.cyear =  '" + cyear + "' ");
		sqlB.append("	           and bm_classitem.cperiod ='" + cperiod + "' ");
		sqlB.append("	           and bm_itemright.cuserid = '" + userid + "') union ");
		//		sqlB.append("	    or bm_item.pk_bm_item in ");
		sqlB.append("	       (select bm_itemright_group.pk_bm_item ");
		sqlB.append("	          from bm_itemright_group, bm_classitem ");
		sqlB.append("	         where bm_itemright_group.pk_bm_class ='"+ waGlobalVOs.getPk_wa_class()+"' ");
		sqlB.append("	           and bm_itemright_group.pk_bm_class = bm_classitem.BMCLASSID ");
		sqlB.append("	           and bm_itemright_group.pk_bm_item = bm_classitem.ITEMID ");
		sqlB.append("	           and bm_classitem.cyear =  '" + cyear + "' ");
		sqlB.append("	           and bm_classitem.cperiod = '" + cperiod + "' ");
		sqlB.append("	           and bm_itemright_group.groupid in ");
		sqlB.append("	               (select sm_role.pk_role ");
		sqlB.append("	                  from sm_role, sm_user_role ");
		sqlB.append("               where sm_role.pk_role = sm_user_role.pk_role ");
		sqlB.append("                   and sm_role.dr = 0 ");
		sqlB.append("                   and sm_user_role.dr = 0 ");
		sqlB.append("                   and sm_user_role.cuserid = '" + userid + "'))) ");



		return sqlB.toString();
	}



	/**
	 * 
	 * @author xuanlt on 2010-1-8
	 * @param waContext
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 * @return  WaItemVO[]
	 */
	public  WaItemVO[] queryWaItemByUser(WaLoginContext waContext) throws nc.vo.pub.BusinessException{
		StringBuilder sbd = new StringBuilder();

		sbd.append(" select ci.pk_wa_item ,  "+ SQLHelper.getMultiLangNameColumn("ci.name")+ " ,i.ITEMKEY,i.IITEMTYPE,i.IFLDWIDTH,i.IFLDDECIMAL  ");
		sbd.append("from wa_classitem ci, wa_item i   ");
		sbd.append("where ci.pk_wa_item =i.pk_wa_item  " +
				//暂时不使用组织进行 限定 	"and ci.PK_GROUP = i.PK_GROUP and ci.PK_ORG= i.PK_ORG " +
				"and   ci.pk_wa_class = ? and ci.cyear = ? and ci.cperiod = ?  and ci.pk_group = ?");
		sbd.append("and ci.pk_org = ? and i.PK_WA_ITEM in (" +ItemPowerUtil.getItemPower(waContext)+
				") ");
		sbd.append("order by ci.IDISPLAYSEQ");

		SQLParameter para = new SQLParameter();
		para.addParam(waContext.getPk_wa_class());
		para.addParam(waContext.getWaYear());
		para.addParam(waContext.getWaPeriod());
		para.addParam(waContext.getPk_group());
		para.addParam(waContext.getPk_org());



		WaItemVO[] items = executeQueryVOs(sbd.toString(), para,WaItemVO.class);
		if(items==null){
			items  = new WaItemVO[0];
		}
		return items;
	}

	/***************************************************************************
	 * <br>Created on 2012-7-31 下午6:28:06<br>
	 * @param waContext
	 * @param condition
	 * @param strOrderBy
	 * @return
	 * @throws BusinessException
	 * @author daicy
	 ***************************************************************************/
	public AggHrIntfaceVO[] queryByCondition(WaLoginContext waContext,
			String condition, String strOrderBy) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();

		try{
			StringBuilder sbd  = new StringBuilder();

			SQLParameter para = new SQLParameter();
			int itfType = (Integer)ItfTypeEnum.WA_BANK.value();
			if(waContext.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
			{
				sbd.append("select * from hr_dataio_intface where pk_group=? and pk_org = ? and classid = ? and cyear=? and cperiod=? and iiftype <> "+itfType);
				para.addParam(waContext.getPk_group());
				para.addParam(waContext.getPk_org());
				para.addParam(waContext.getPk_prnt_class());
				para.addParam(waContext.getWaYear());
				para.addParam(waContext.getWaPeriod());
			}
			else
			{
				sbd.append("select * from hr_dataio_intface where pk_group=? and pk_org = ? and iiftype = "+itfType);
				para.addParam(waContext.getPk_group());
				para.addParam(waContext.getPk_org());
			}


			if(!StringUtils.isBlank(condition)){
				sbd.append(" and " + condition);
			}

			if(!StringUtils.isBlank(strOrderBy)){
				sbd.append( strOrderBy);
			}

			ArrayList<AggHrIntfaceVO> aggVOs = new ArrayList<AggHrIntfaceVO> ();
			HrIntfaceVO[] vos = executeQueryVOs(sbd.toString(), para, HrIntfaceVO.class);
			String insql = inSQLCreator.getInSQL(vcontents);
			String[] ifids = null;
			HashMap<String, FormatItemVO[]> mapformatItemVOs = new HashMap<String, FormatItemVO[]>();
			HashMap<String, IfsettopVO[]> mapifsettopVOs = new HashMap<String, IfsettopVO[]>();
			for(int i=0;vos!=null&&i<vos.length;i++)
			{
				AggHrIntfaceVO aggVO = new AggHrIntfaceVO();
				aggVO.setParentVO(vos[i]);
				ifids = (String[]) ArrayUtils.add(ifids, vos[i].getPk_dataio_intface());
				aggVOs.add(aggVO);
			}
			String ifidInSql = inSQLCreator.getInSQL(ifids);

			StringBuffer sql = new StringBuffer();
			sql.append("select * from hr_dataintface_b where ifid in (");
			sql.append(ifidInSql);
			//shenliangc 20140702 银行报盘增加户名
			sql.append(" ) and ( vcontent is null or vcontent not like 'wa_data%' or vcontent in (");
			sql.append(insql);
			sql.append(" ) or vcontent in (SELECT DISTINCT 'wa_data.'||wa_item.itemkey ");
			sql.append(" FROM wa_item  ");
			sql.append(" WHERE wa_item.pk_wa_item in(");
			sql.append(ItemPowerUtil.getItemPower(waContext));
			sql.append(" ))) order by ifid,iseq ");
			FormatItemVO[] formatItemVOs = executeQueryVOs(sql.toString(), FormatItemVO.class);
			
			for(int i=0;null!=ifids&&i<ifids.length;i++){
				String ifid = ifids[i];
				FormatItemVO[] tempItemVOs = null;
				for(FormatItemVO fvo:formatItemVOs){
					if(fvo.getIfid().equals(ifid)){
						tempItemVOs = (FormatItemVO[]) ArrayUtils.add(tempItemVOs, fvo);
					}
				}
				
				if(tempItemVOs!=null){
					mapformatItemVOs.put(ifid, tempItemVOs);
				}
			}
			
			StringBuffer ifsettopsql = new StringBuffer();
			ifsettopsql.append("select * from ");
			ifsettopsql.append(DataIOconstant.HR_IFSETTOP);
			ifsettopsql.append(" where ifid in (");
			ifsettopsql.append(ifidInSql);
			ifsettopsql.append(" ) order by ifid,iseq ");
			IfsettopVO[] ifsettopVOs = executeQueryVOs(ifsettopsql.toString(),IfsettopVO.class);

			for(int i=0;null!=ifids&&i<ifids.length;i++){
				String ifid = ifids[i];
				IfsettopVO[] tempsetTopVOs = null;
				for (IfsettopVO tvo : ifsettopVOs) {
					if(tvo.getIfid().equals(ifid)){
						tempsetTopVOs = (IfsettopVO[]) ArrayUtils.add(tempsetTopVOs, tvo);
					}
				}
				
				if (tempsetTopVOs != null) {
					mapifsettopVOs.put(ifid, tempsetTopVOs);
				}
			}

			for(int i=0;aggVOs!=null&&i<aggVOs.size();i++)
			{
				AggHrIntfaceVO aggVO = aggVOs.get(i);
				String pkifid = ((HrIntfaceVO)(aggVO.getParentVO())).getPk_dataio_intface();
				aggVO.setTableVO(DataIOconstant.HR_DATAINTFACE_B,mapformatItemVOs.get(pkifid));
				aggVO.setTableVO(DataIOconstant.HR_IFSETTOP, mapifsettopVOs.get(pkifid));
			}

			return aggVOs.toArray( new AggHrIntfaceVO[0] );

		}finally {
			inSQLCreator.clear();
		}



	}

	public PeriodVO[] queryPeriodVOs(AggHrIntfaceVO aggVO,String lastCYear,String lastCPeriod) throws DAOException{
		HrIntfaceVO parentVO = (HrIntfaceVO) aggVO.getParentVO();
		StringBuffer sbSQL = new StringBuffer("select wa_period.cyear,wa_period.cperiod from wa_period")
			.append(" inner join wa_periodscheme on wa_period.pk_periodscheme= wa_periodscheme.pk_periodscheme")
			.append(" inner join wa_waclass on wa_waclass.pk_periodscheme = wa_periodscheme.pk_periodscheme")
			.append(" where wa_waclass.pk_wa_class='").append(parentVO.getClassid())
			.append("' and (wa_period.cyear>'").append(parentVO.getCyear())
			.append("' or (wa_period.cyear='").append(parentVO.getCyear())
			.append("' and wa_period.cperiod>'").append(parentVO.getCperiod())
			.append("')) and (wa_period.cyear<'").append(lastCYear)
			.append("' or (wa_period.cyear='").append(lastCYear)
			.append("' and wa_period.cperiod<='").append(lastCPeriod)
			.append("'))  order by wa_period.cyear,wa_period.cperiod");
		return executeQueryVOs(sbSQL.toString(), PeriodVO.class);
	}


}
