package nc.impl.wa.classitem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.wa.category.WaClassDAO;
import nc.impl.wa.classitempower.ClassItemPowerServiceImpl;
import nc.impl.wa.common.WaCommonImpl;
import nc.impl.wa.item.ItemServiceImpl;
import nc.impl.wa.paydata.PaydataServiceImpl;
import nc.impl.wa.payslip.PayslipDAO;
import nc.itf.bd.defdoc.IDefdocQryService;
import nc.itf.hr.frame.PersistenceDbException;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemManageService;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.wa.item.util.ItemUtils;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.formula.FormulaXmlHelper;
import nc.vo.hr.formula.FunctionKey;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.func.HrFormula;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.formulaset.ContentVO;
import nc.vo.pub.formulaset.ItemVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.RoundTypeEnum;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.classitempower.ItemPowerVO;
import nc.vo.wa.formula.HrWaXmlReader;
import nc.vo.wa.func.WaDatasourceManager;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemConstant;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <b> 说明：薪资项目在保存的时候 ，不仅仅需要锁住当前要更改的薪资项目
 * 还要锁住当前类别所有的薪资项目（因为薪资项目之间存在结转关系与计算之间的依赖关系） 。 所以直接对薪资类别主键加锁</b>
 *
 * @author: wh
 *
 * @date: 2009-12-8 下午01:29:38
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class ClassItemManageServiceImpl implements IClassItemManageService, IClassItemQueryService {

	private final ItemServiceImpl itemServiceImpl = new ItemServiceImpl();
	WaCommonImpl waCommonImpl = new WaCommonImpl();
	ClassItemPowerServiceImpl classItemPowerServiceImpl = new ClassItemPowerServiceImpl();
	PaydataServiceImpl paydataServiceImpl = new PaydataServiceImpl();
	ClassitemDAO classitemDAO = new ClassitemDAO();
	IValidatorFactory validatorFactory = new ClassItemValidatorFactory();
	private final String DOC_NAME = "Classitem";

	public IValidatorFactory getValidatorFactory() {
		if(validatorFactory==null){
			validatorFactory = new ClassItemValidatorFactory();
		}
		return validatorFactory;
	}

	private ClassitemDAO getClassitemDAO(){
		if(classitemDAO==null){
			classitemDAO = new ClassitemDAO();
		}

		return classitemDAO;
	}

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
			serviceTemplate.setDispatchEvent(false);
			serviceTemplate.setLocker(new ClassItemDocLocker());
			serviceTemplate.setValidatorFactory(new ClassItemValidatorFactory());
		}

		return serviceTemplate;
	}

	@Override
	public WaClassItemVO[] queryClassItemByCondition(LoginContext context, String condition) throws BusinessException {

		return getClassitemDAO().queryByCondition(context,condition);
	}


	/**
	 * 根据薪资类别、薪资期间、所在组织、项目分类以及权限限制 查询薪资发放项目 发放项目按照显示顺序排序（使用order by就可以了）。
	 *
	 * @param typePk
	 *            : 项目分类
	 * @author xuanlt on 2010-1-21
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryByWaItemType(nc.vo.wa.pub.WaLoginContext,
	 *      java.lang.String)
	 */
	@Override
	@SuppressWarnings( { "unchecked", "serial" })
	public WaClassItemVO[] queryByWaItemType(WaLoginContext context, String typePk) throws BusinessException {

		return getClassitemDAO().queryByWaItemType(context, typePk);
	}


	@Override
	public WaClassItemVO queryWaClassItemVOByPk(String pk_waclassitem)
			throws BusinessException {
		return getClassitemDAO().queryWaClassItemVOByPk(pk_waclassitem);
	}

	@Override
	public WaClassItemVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(WaClassItemVO.class, pk);
	}
	
	//20160113 shenliangc NCdp205572656  离职结薪节点删除所有人员数据后，发放项目汇总下还有被删除的离职结薪次数下的新增项目
	//批量删除：用于删除薪资方案次数
	//目前这种写法有SONAR问题，但是内层逻辑比较复杂，重写批量逻辑风险太大。暂时这样处理吧。
	@Override
	public void deleteWaClassItemVOs(WaClassItemVO[] vos) throws BusinessException {
		if(!ArrayUtils.isEmpty(vos)){
			for(WaClassItemVO vo : vos){
				WaClassItemVO newvo = this.queryByPk(vo.getPk_wa_classitem());
				newvo.setStatus(vo.getStatus());
				deleteWaClassItemVO(newvo);
			}
		}
	}
	

	@Override
	public void deleteWaClassItemVO(WaClassItemVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		if(checkGroupItem(vo)){
			throw new BusinessException(ResHelper.getString("60130payitem","060130payitem0222")
					/*@res "组织不能删除集团分配的项目！"*/);
		}

		//		if(checkLeaveItem(vo)){
		//			throw new BusinessException(ResHelper.getString("60130payitem","060130payitem0223")
		///*@res "该项目已存在离职发薪数据，不能删除！"*/);
		//		}

		//首先清空该项目的发放数据
		clearClassItemData(vo);


		getServiceTemplate().delete(vo);
		CacheProxy.fireDataDeleted(vo.getTableName(), vo.getPk_wa_classitem());
		if (needRegenFormula(vo)) {
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		
		// HR本地化：将有汇总项的公式全部重算
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		
		// HR本地化：将所有EPF，EIS，SOCSO打勾项汇总到预置的薪资项目
		
		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		synParentClassItem(VOStatus.DELETED, vo);



	}


	@Override
	public boolean checkGroupItem(WaClassItemVO vo) throws BusinessException{
		boolean flag = false;
		String sql = "select pk_wa_class from wa_classitem "
				+ "where pk_wa_class = (select pk_sourcecls from wa_assigncls,wa_inludeclass "
				+ "where wa_assigncls.classid = wa_inludeclass.pk_parentclass and wa_inludeclass.pk_childclass= ? and cyear = ? and cperiod = ?) "
				+ "and itemkey = ? and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getItemkey());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		flag = getClassitemDAO().isValueExist(sql,param);  //多次方案！！

		if(!flag){
			sql = "select pk_wa_class from wa_classitem "
					+ "where pk_wa_class = (select pk_sourcecls from wa_assigncls "
					+ "where wa_assigncls.classid = ? ) "
					+ "and itemkey = ? and cyear = ? and cperiod = ?";
			param = new SQLParameter();
			param.addParam(vo.getPk_wa_class());
			param.addParam(vo.getItemkey());
			param.addParam(vo.getCyear());
			param.addParam(vo.getCperiod());
			return getClassitemDAO().isValueExist(sql,param);
		}

		return flag;


	}

	public boolean checkLeaveItem(WaClassItemVO vo) throws BusinessException{
		String sql = "SELECT pk_wa_classitem "
				+ "FROM wa_classitem "
				+ "WHERE pk_wa_class IN(SELECT pk_childclass "
				+ "						  FROM wa_inludeclass "
				+ "						 WHERE pk_parentclass = (SELECT pk_parentclass "
				+ "												   FROM wa_inludeclass "
				+ "												  WHERE pk_childclass = ? "
				+ "                                                 and cyear = wa_classitem.cyear "
				+ "                                                 and cperiod = wa_classitem.cperiod)  "
				+ "                       and cyear = wa_classitem.cyear "
				+ "                       and cperiod = wa_classitem.cperiod  and batch >100 )" // 加入离职结薪条件
				+ "	AND cyear = ? "
				+ "	AND cperiod = ? "
				+ "	AND itemkey = ? "
				+ "	AND pk_wa_class <> ?  ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getItemkey());
		param.addParam(vo.getPk_wa_class());
		return getClassitemDAO().isValueExist(sql,param);
	}

	private void clearClassItemData(WaClassItemVO vo) throws BusinessException{
		NCLocator.getInstance().lookup(IPaydataManageService.class).clearClassItemData(vo);
	}

	/**
	 * 重新设置薪资发放数据的标志 重新设置薪资期间状态
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	private void resetPaydataFlag(String   pk_wa_class, String cyear,String cperiod) throws BusinessException{
		NCLocator.getInstance().lookup(IPaydataManageService.class).updatePaydataFlag(pk_wa_class, cyear, cperiod);
	}

	@Override
	public WaClassItemVO insertWaClassItemVO(WaClassItemVO vo)
			throws BusinessException {

		BDPKLockUtil.lockString(vo.getPk_wa_class());
		vo = insertS(vo);
		// 同步缓存
		WaCacheUtils.synCache(vo.getTableName());

		return vo;
	}


	private  WaClassItemVO insertS(WaClassItemVO vo) throws BusinessException {

		//首先判断是否增加私有项目
		if(isaddPrivateItem(vo)){
			WaItemVO itemvo = vo.abstracts();
			int oldIfromflag = itemvo.getIfromflag();
			String oldVformula = itemvo.getVformula();
			String oldVformulastr = itemvo.getVformulastr();
			//修改：默认用itemkey定义code
			//			IdGenerator idGenerator = DBAUtil.getIdGenerator();
			//			String code = idGenerator.generate();
			//			itemvo.setCode(code);

			//如果ifromflag是其他数据源——不是 FORMULA、WA_WAGEFORM、USER_INPUT、FIX_VALUE、WA_GRADE、TIMESCOLLECT——

			//则将Ifromflag 设置为USER_INPUT。 公式设置为空
			if(WaDatasourceManager.isOtherDatasource(itemvo.getIfromflag())){
				itemvo.setIfromflag(FromEnumVO.USER_INPUT.value());
				itemvo.setVformula(null);
				itemvo.setVformulastr(null);
			}
			itemvo =  NCLocator.getInstance().lookup(IItemManageService.class).insertWaItemVO(itemvo);
			oldVformula = StringUtils.replace(oldVformula, "itemkey", itemvo.getItemkey());
			vo.merge(itemvo);
			vo.setIfromflag(oldIfromflag);
			vo.setVformula(oldVformula);
			vo.setVformulastr(oldVformulastr);
		}
		vo =  insert(vo, true);


		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		//查询父方案.如果父方案是多次发薪 .则同步父方案
		//		synParentClassItem(VOStatus.NEW,vo);



		return vo;
	}




	private void synParentClassItem(int action, WaClassItemVO vo) throws BusinessException {
		//确定是否存在父类别
		IWaClass  waClass  = NCLocator.getInstance().lookup(IWaClass.class);
		WaClassVO parentvo = waClass.queryParentClass(vo.getPk_wa_class(),vo.getCyear()	, vo.getCperiod());
		if(parentvo == null){
			if ( action == VOStatus.NEW  ) {
				insertItemPower(vo);
			}
			if ( action == VOStatus.UPDATED    ) {
				//				 deleteItemPower(vo);
				//				 insertItemPower(vo);     //把所有人的全删了，添加自己的权限，不知道干什么用的，故屏蔽。
				
//				20151028  xiejie3  NCdp205519152 测试要求修改为发放手工输入项时，默认为可修改。begin
				//20151201 shenliangc NCdp205519152 不是问题，代码回退。
//				updateItemPowerEditflag(vo);
//				end
				//
				
			}
			if (  action == VOStatus.DELETED    ) {

				if(!getClassitemDAO().isItemExistDifPeriod(vo)){
					//删除项目权限
					deleteItemPower(vo);
				}

				//只清空父方案的薪资条数据
				new PayslipDAO().deletePayslipItemData(vo);

			}
			//没有查询到父方案,直接返回
			return ;
		}

		WaClassItemVO newvo =(WaClassItemVO) vo.clone();
		newvo.setPk_wa_class(parentvo.getPk_wa_class());

		//父方案不需要计算, 所有的计算顺序都忽略
		newvo.setIcomputeseq(0);

		//		//汇总薪资项目的数据来源是：发放次数汇总
		//数据来源保持不变 。
		//		newvo.setIfromflag(8);

		if ( action == VOStatus.NEW  ) {
			//父类别是否有该项目
			if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){
				//父类别中已有该项目.直接返回
				return ;
			}

			newvo.setPk_wa_classitem(null);

			//不存在,则同步到父类别中
			newvo.setStatus(VOStatus.NEW);

			getMDPersistenceService().saveBill(newvo);
			//发放项目默认有权限
			insertItemPower(newvo);

		}

		if ( action == VOStatus.UPDATED    ) {
			//父类别是否有该项目
			if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){
				//有,需要更新

				newvo.setStatus(VOStatus.UPDATED);

				//更新父类别的项目需要进一步优化. 现在 使用了2次 sql操作
				WaClassItemVO   newvoold =   getClassitemDAO().queryClassItemVO(newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),newvo.getPk_wa_class());
				newvo.setPk_wa_classitem(newvoold.getPk_wa_classitem());

				//				  deleteItemPower(newvo);
				//				  insertItemPower(newvo);    //把所有人的全删了，添加自己的权限，不知道干什么用的，故屏蔽。
//				20151028  xiejie3  NCdp205519152 测试要求修改为发放手工输入项时，默认为可修改。begin
				//20151201 shenliangc NCdp205519152 不是问题，代码回退。
//				updateItemPowerEditflag(newvo);
//				end
				getMDPersistenceService().saveBill(newvo);

			}
		}

		if (  action == VOStatus.DELETED    ) {
			//是否可以删除.看看其他子方案里面是否有 .如果没有,就可以删除
			if(getClassitemDAO().isExistInOtherChildClass(parentvo.getPk_wa_class(),vo.getPk_wa_class(),newvo)){
				//父类别是否有该项目
				if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){  //daicy 9-18
					//有,需要更新
					//删除项目是汇总项目更新为上一次的项目
					WaInludeclassVO[] subclasses = new WaClassDAO().querySubClasses(parentvo.getPk_wa_class(), newvo.getCyear(),newvo.getCperiod(),false);
					if(null != subclasses ){
						int i = 0;
						for(;  i < subclasses.length ;i++){
							if(subclasses[i].getPk_childclass().equals(vo.getPk_wa_class())){
								break;
							}
						}
						if( i < subclasses.length){
							int batch = subclasses[i].getBatch();
							if(batch > 1 ){
								i = i - 1;
								if(i >=0 && i < subclasses.length){
									WaClassItemVO   chilenewvoold =   getClassitemDAO().queryClassItemVO(
											newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),subclasses[i].getPk_childclass());
									WaClassItemVO   parentnewvoold =   getClassitemDAO().queryClassItemVO(
											newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),subclasses[i].getPk_parentclass());
									// 2015-12-28 NCdp205565481 zhousze 薪资发放项目，离职结薪中的项目删除报未知错误，这里判空处理 begin
									if(chilenewvoold != null && parentnewvoold != null){
							    		chilenewvoold.setStatus(VOStatus.UPDATED);
								    	chilenewvoold.setPk_wa_classitem(parentnewvoold.getPk_wa_classitem());
							    		chilenewvoold.setPk_wa_class(parentnewvoold.getPk_wa_class());
							     		//getServiceTemplate().update(chilenewvoold,true);
							       		getMDPersistenceService().saveBill(chilenewvoold);
									}
									// end
								}

							}
						}

					}

				}

				return;
			}




			//清空薪资发放表中的数据
			clearClassItemData(newvo);

			if(!getClassitemDAO().isItemExistDifPeriod(newvo)){
				//删除项目权限
				deleteItemPower(newvo);
			}


			//只清空父方案的薪资条数据
			new PayslipDAO().deletePayslipItemData(vo);


			getClassitemDAO().deleteWaclassItem(newvo);
		}

		//清空数据与标示
		resetPaydataFlag(newvo.getPk_wa_class(),newvo.getCyear(),newvo.getCperiod());
	}



	/**
	 * 集团方案插入项目
	 * (1)查看所有的子方案（当前期间）是否有已经审核发放数据的。如果有则给予提示
	 * (2)没有，可以执行新增操作
	 *    子方案中没有重复项目，直接新增
	 *    子方案中有的，需要进行覆盖
	 *
	 *    如果项目中包含为加入的项目怎么办？
	 *
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassItemVO insertGroupClassItem(WaClassItemVO vo) throws BusinessException {

		/**
		 * 查看所有的子方案（当前期间）是否有已经审核发放数据的。如果有则给予提示
		 */
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//抛出异常，给出提示
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "存在已经审核的数据，不能增加项目 "*/);
		}

		/**
		 * 首先插入自己的项目
		 */
		vo = insertS(vo);

		/**
		 * 插入子组织的项目
		 */

		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);
		if(allvos!=null){
			for (WaClassVO waClassVO : allvos) {
				//guoqt NCZX问题NCdp205050978集团方案分配到多个组织后各组织期间不一致时，保存时提示引用项目未添加到方案
				if(waClassVO.getCyear().equals(vo.getCyear())&&waClassVO.getCperiod().equals(vo.getCperiod())){
					insertClassItem2SubClass(vo,waClassVO);
				}
			}
		}

		//同步缓存
		WaCacheUtils.synCache(vo.getTableName());
		return vo;
	}


	private void insertClassItem2SubClass(WaClassItemVO vo,WaClassVO subClassvo) throws BusinessException{

		try {
			WaClassItemVO newvo =(WaClassItemVO) vo.clone();
			newvo.setPk_group(subClassvo.getPk_group());
			newvo.setPk_org(subClassvo.getPk_org());
			newvo.setPk_wa_class(subClassvo.getPk_wa_class());
			newvo.setPk_wa_classitem(null);
			newvo.getPk_wa_item();


			//如果已经有该项目，则更新，否则就插入
			WaClassItemVO  oldItemvo =  getClassitemDAO().queryClassItemVO(newvo.getPk_wa_item(), newvo.getCyear(),newvo.getCperiod(),newvo.getPk_wa_class());
			if(oldItemvo==null){
				newvo.setStatus(VOStatus.NEW);

				insertS(newvo);
			}else{
				newvo.setStatus(VOStatus.UPDATED);
				newvo.setPk_wa_classitem(oldItemvo.getPk_wa_classitem());
				//版本校验
				newvo.setTs(oldItemvo.getTs());
				updateWaClassItemVO(newvo);

			}
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "向子方案{0}中新增发放项目失败*/
		}
	}

	//	20150728 xiejie3 补丁合并，NCdp205382570在薪资发放中，显示设置里面薪资项目有些不是显示内容，begin
	//guoqt同步薪资发放项目显示顺序
		private void updateClassItem2SubClass(WaClassItemVO[] ordervo,WaClassVO subClassvo) throws BusinessException{
			try {
				IClassItemQueryService queryItem = NCLocator.getInstance().lookup(IClassItemQueryService.class);
				WaClassItemVO[] allItems = queryItem.queryItemInfoVO(subClassvo.getPk_org(), subClassvo.getPk_wa_class(), subClassvo.getCyear(), subClassvo.getCperiod());
				List<String> vositemkey = new ArrayList(ordervo.length);
				for(int i=0;i<ordervo.length;i++){
					vositemkey.addAll(Arrays.asList(ordervo[i].getItemkey()));
				}
				List<WaClassItemVO> vos = new ArrayList(allItems.length);
				for(int j=0;j<allItems.length;j++){
					//先判断该薪资项目是否是集团项目
					if(vositemkey.contains(allItems[j].getItemkey())){
						for(int k=0;k<vositemkey.size();k++){
							if(allItems[j].getItemkey().equals(ordervo[k].getItemkey())){
								allItems[j].setStatus(VOStatus.UPDATED);
								//更新顺序
								allItems[j].setIdisplayseq(ordervo[k].getIdisplayseq());
								vos.addAll(Arrays.asList(allItems[j]));
							}
						}
					}else{
						//组织自己增加的项目
						allItems[j].setStatus(VOStatus.UPDATED);
						//更新顺序
						allItems[j].setIdisplayseq(allItems[j].getIdisplayseq()+allItems.length);
						vos.addAll(Arrays.asList(allItems[j]));
					}
				}
				WaClassItemVO[] updatevos=vos.toArray(new WaClassItemVO[0]);
				this.getClassitemDAO().getBaseDao().updateVOArray(updatevos);
			} catch (DAOException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "向子方案{0}中新增发放项目失败*/
			}
		}
//		end
	

	private void deleteClassItem2SubClass(WaClassItemVO vo,WaClassVO subClassvo) throws BusinessException{

		try {
			WaClassItemVO newvo =(WaClassItemVO) vo.clone();
			newvo.setPk_group(subClassvo.getPk_group());
			newvo.setPk_org(subClassvo.getPk_org());
			newvo.setPk_wa_class(subClassvo.getPk_wa_class());
			newvo.setPk_wa_classitem(null);
			newvo.getPk_wa_item();


			//如果已经有该项目，则更新，否则就插入
			WaClassItemVO oldItemvo = getClassitemDAO().queryClassItemVO(
					newvo.getPk_wa_item(), newvo.getCyear(),
					newvo.getCperiod(), newvo.getPk_wa_class());
			if (oldItemvo != null) {
				deleteWaClassItemVO(oldItemvo);
			}
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "向子方案{0}中新增发放项目失败*/
		}
	}

	private static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}


	private boolean isaddPrivateItem(WaClassItemVO vo){
		return StringUtils.isBlank(vo.getPk_wa_item());

	}
	private WaClassItemVO insert(WaClassItemVO vo, boolean regenFormula) throws BusinessException {


		String categoryId = vo.getCategory_id();
		// set idisplayorder
		vo.setIdisplayseq(getMaxDisplayOrder(vo));
		//设定默认计算顺序
		vo.setIcomputeseq(0);
		Integer iitemtype = vo.getIitemtype();
		Integer iproperty = vo.getIproperty();
		// 2015-09-28 zhousze "薪资发放项目"新增保存后需要刷新一次，保证保存后界面的数据是最新的 begin
//		vo = getServiceTemplate().insert(vo);
		WaClassItemVO oldvo = getServiceTemplate().insert(vo);
		vo = NCLocator.getInstance().lookup(IClassItemQueryService.class).queryWaClassItemVOByPk(oldvo.getPrimaryKey());
		// end
		vo.setIitemtype(iitemtype);
		vo.setIproperty(iproperty);

		if (regenFormula && // 是否需要重新生成系统公式
				needRegenFormula(vo)) {
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		
		// HR本地化：将有汇总项的公式全部重算
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());

		//
		if (!StringUtils.isBlank(categoryId)) {
			DefdocVO[] doc = NCLocator.getInstance().lookup(IDefdocQryService.class).queryDefdocByPk(
					new String[] { categoryId });
			if (doc != null) {
				vo.setCategoryVO(doc[0]);
			} else {
				throw new IllegalStateException(ResHelper.getString("60130classpower","060130classpower0177")/*@res "没有查询到对应的项目分类："*/ + categoryId);
			}
		}

		//设计计算顺序
		if(regenFormula){
			resetCompuSeq(vo);
		}


		//查询父方案.如果父方案是多次发薪 .则同步父方案
		synParentClassItem(VOStatus.NEW,vo);
		return vo;
	}

	private void insertItemPower(WaClassItemVO vo) throws BusinessException{
		ItemPowerVO itemPowerVO = new ItemPowerVO();
		itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
		itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
		itemPowerVO.setModuleflag(Integer.valueOf(0));
		itemPowerVO.setPk_group(vo.getPk_group());
		itemPowerVO.setPk_org(vo.getPk_org());
		itemPowerVO.setPk_subject(PubEnv.getPk_user());
		itemPowerVO.setSubject_type(IWaSalaryctymgtConstant.SUB_TYPE_USER);
		itemPowerVO.setEditflag(vo.getIfromflag().intValue() == 2/* 手工输入项目 */? UFBoolean.TRUE : UFBoolean.FALSE);
		classItemPowerServiceImpl.insertItemPowerVO(itemPowerVO);
	}

	private void deleteItemPower(WaClassItemVO vo) throws BusinessException{
		ItemPowerVO itemPowerVO = new ItemPowerVO();
		itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
		itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
		itemPowerVO.setModuleflag(Integer.valueOf(0));
		itemPowerVO.setPk_org(vo.getPk_org());

		classItemPowerServiceImpl.deleteItemPowerVO(itemPowerVO);
	}
	
	//20151031 shenliangc 薪资发放项目修改数据来源保存的同时更新发放项目权限数据。
	//手工输入——》手工输入，非手工输入——》非手工输入，项目权限数据保持不变；
	//非手工输入——》手工输入，手工输入——》非手工输入，如果修改后为手工输入，则可编辑权更新为Y，否则更新为N。
	//20151201 shenliangc NCdp205519152 不是问题，代码回退。
	private void updateItemPowerEditflag(WaClassItemVO vo) throws BusinessException{
		WaClassItemVO oldVO = this.queryWaClassItemVOByPk(vo.getPk_wa_classitem());
		if((oldVO.getIfromflag().intValue() != vo.getIfromflag().intValue()) && (oldVO.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue() 
				|| vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue())){
			ItemPowerVO itemPowerVO = new ItemPowerVO();
			itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
			itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
			itemPowerVO.setModuleflag(Integer.valueOf(0));
			itemPowerVO.setPk_org(vo.getPk_org());
			itemPowerVO.setPk_subject(PubEnv.getPk_user());
			itemPowerVO.setEditflag(vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue()/* 手工输入项目 */? UFBoolean.TRUE : UFBoolean.FALSE);
			classItemPowerServiceImpl.updateItemPowerVOEditflag(itemPowerVO);
			//非手工输入——》手工输入，还要将wa_data中的明细数据更新为初始值。
			if(vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue()){
				this.paydataServiceImpl.clearPaydataByClassitem(vo);
			}
		}
	}
	
	
	/**
	 * 是否需要重新设置计算顺序
	 *
	 * @author xuanlt on 2010-5-28
	 * @param vo
	 * @return
	 * @return  boolean
	 */
	private boolean  needResetCompuSeq(WaClassItemVO vo){
		//		if(vo.getFromEnumVO().equals(FromEnumVO.FORMULA)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.OTHER_SYSTEM)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.HI)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM) ){
		//			return true;
		//		}
		//		return false;

		return true;
	}
	/**
	 * 重新设置计算顺序
	 * @author xuanlt on 2010-5-28
	 * @param vo
	 * @throws BusinessException
	 * @return  void
	 */
	private void resetCompuSeq(WaClassItemVO vo) throws BusinessException{
		//重新设置计算顺序
		WaClassVO classVO = new WaClassVO();
		classVO.setPk_wa_class(vo.getPk_wa_class());
		classVO.setCyear(vo.getCyear());
		classVO.setCperiod(vo.getCperiod());

		//得到需要设置排序的薪资发放项目
		resetCompuSeq(classVO);

	}

	/**
	 * 重新设置某个薪资类别的计算顺序
	 * Pk_wa_class
	 * Cyear
	 * Cperiod
	 * @param classVO
	 * @throws BusinessException
	 */
	@Override
	public void resetCompuSeq(WaClassVO classVO) throws BusinessException{

		//得到需要设置排序的薪资发放项目
		updateItemCaculateSeu(classVO);
		//		WaClassItemVO[] vos = getClassitemVOsForSequ(classVO);
		//		//设置计算顺序
		//		 vos = new ItemSort().getSortedWaClassItemVOs(vos,classVO);
		//
		//		//保存计算顺序
		//		classitemDAO.updateItemCaculateSeu(vos);
	}



	@Override
	public  WaClassItemVO[] getClassitemVOsForSequ(WaClassVO vo) throws BusinessException{
		String where = " pk_wa_class = '" + vo.getPk_wa_class()  + "' and cperiod = '" + vo.getCperiod() + "' and cyear = '"+vo.getCyear()+"'";


		return getServiceTemplate().queryByCondition(WaClassItemVO.class, where);
	}

	public  WaClassItemVO[] getClassitemsBySeq(WaClassVO vo) throws BusinessException{
		String where = " pk_wa_class = '" + vo.getPk_wa_class()  + "' and cperiod = '" + vo.getCperiod() + "' and cyear = '"+vo.getCyear()+"'  order by icomputeseq";


		return getServiceTemplate().queryByCondition(WaClassItemVO.class, where);
	}


	public boolean isItemExist(WaClassVO waclassVO, String itemKey) throws BusinessException {
		try {

			return classitemDAO.isItemExist(waclassVO, itemKey);
		} catch (Exception e) {

			throw new BusinessException(e.getMessage());
		}
	}

	private boolean needRegenFormula(WaClassItemVO vo) {
		return vo.getPropertyEnumVO() != PropertyEnumVO.OTHER;
	}

	@Override
	public void regenerateSystemFormula(String pk_org, String pk_wa_class, String cyear, String cperiod)
			throws BusinessException {

		try {

			WaClassItemVO[] items = null;
			try {
				items = classitemDAO.queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod,
						" wa_item.itemkey in ("+getRegenerateItem()+") and wa_classitem.issysformula = 'Y'");
			} catch (DAOException e) {
				Logger.error(e);
				throw new BusinessRuntimeException(ResHelper.getString("60130classpower","060130classpower0178")/*@res "重新设置系统项目的公式失败。"*/);
			}
			if (items == null || items.length == 0) {
				return;
			}
			BDPKLockUtil.lockSuperVO(items);

			for (int i = 0; i < items.length; i++) {
				HrFormula formula = new FormulaUtils().getSystemFormula(pk_org, pk_wa_class, cyear, cperiod, items[i]
						.getItemkey());
				items[i].setVformula(formula.getScirptLang());
				//20151208 shenliangc NCdp205556679  薪资补发多次发放薪资方案，只有一次发放的薪资项目值补发值没有累计。
				//根本原因是期末处理生成下期间发放项目后系统项目公式没有包含多次方案子方案中添加的发放项目。
				//20151209 修改后重新构造补丁
				items[i].setVformulastr(formula.getBusinessLang());
			}
			BaseDAO baseDAO = new BaseDAO();
			baseDAO.updateVOArray(items, new String[] { WaClassItemVO.VFORMULA, WaClassItemVO.VFORMULASTR});
			WaCacheUtils.synCache(items[0].getTableName());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0179")/*@res "生成系统项目的默认公式失败！"*/);
		}finally{

		}
	}
	
	private void generateTotalItemFormula(String pk_org, String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		try {
			WaClassItemVO[] items = null;
			try {
				// 获取本期间，本薪资方案的所有薪资发放项目
				items = classitemDAO.queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod);
			} catch (DAOException e) {
				Logger.error(e);
				throw new BusinessRuntimeException(ResHelper.getString("60130classpower","060130classpower0178")/*@res "重新设置系统项目的公式失败。"*/);
			}
			if (items == null || items.length == 0) {
				return;
			}
			BDPKLockUtil.lockSuperVO(items);
				
			for (WaClassItemVO parent : items) {
				if (parent.getG_istotalitem() == null ? false : parent.getG_istotalitem().booleanValue()) {
					StringBuilder formulaSb = new StringBuilder();
					StringBuilder formulaStrSb = new StringBuilder();
					
					// 清空vformula和vformulaStr字段
					parent.setVformula(null);
					parent.setVformulastr(null);
					
					// 遍历所有薪资发放项目，重构公式
					for (WaClassItemVO child : items) {
						if (child.getG_totaltoitem() != null && child.getG_totaltoitem().equals(parent.getItemkey())) {
							if (!child.getItemkey().equals(parent.getItemkey())) {
								if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
									formulaSb.append(" - wa_data." + child.getItemkey());
									formulaStrSb.append(" - wa_data." + child.getItemkey());
								} else {
									formulaSb.append(" + wa_data." + child.getItemkey());
									formulaStrSb.append(" + wa_data." + child.getItemkey());
								}
							}
						}
					}
					parent.setVformula(formulaSb.toString());
					parent.setVformulastr(formulaStrSb.toString());
				} else {
					continue;
				}
			}
			
			summingEPFNormalItems(items);
			summingEPFAdditionalItems(items);
			summingEISItems(items);
			summingSOCSOItems(items);
			
			BaseDAO baseDAO = new BaseDAO();
			baseDAO.updateVOArray(items, new String[] { WaClassItemVO.VFORMULA, WaClassItemVO.VFORMULASTR});
			WaCacheUtils.synCache(items[0].getTableName());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0179")/*@res "生成系统项目的默认公式失败！"*/);
		}
	}
	
	private void summingEPFNormalItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_epf_normal_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// 清空vformula和vformulaStr字段
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// 遍历所有薪资发放项目，重构公式
				for (WaClassItemVO child : items) {
					if (child.getMy_isepf_n() != null && child.getMy_isepf_n().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingEPFAdditionalItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_epf_bonus_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// 清空vformula和vformulaStr字段
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// 遍历所有薪资发放项目，重构公式
				for (WaClassItemVO child : items) {
					if (child.getMy_isepf_a() != null && child.getMy_isepf_a().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingEISItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_eis_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// 清空vformula和vformulaStr字段
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// 遍历所有薪资发放项目，重构公式
				for (WaClassItemVO child : items) {
					if (child.getMy_iseis() != null && child.getMy_iseis().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingSOCSOItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_socso_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// 清空vformula和vformulaStr字段
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// 遍历所有薪资发放项目，重构公式
				for (WaClassItemVO child : items) {
					if (child.getMy_issocso() != null && child.getMy_issocso().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}

	private String getRegenerateItem (){
		//应发合计，扣款合计，本次扣税基数
		return WaItemConstant.CustomFormularSysItemKeyStr;
	}
	@Override
	public WaClassItemVO[] insertClassItemVOs(WaClassItemVO[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			BDPKLockUtil.lockString(vos[0].getPk_wa_class());
		}
		/**
		 * 查看所有的子方案（当前期间）是否有已经审核发放数据的。如果有则给予提示
		 */
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vos[0].getPk_wa_class());
		classvo.setCyear(vos[0].getCyear());
		classvo.setCperiod(vos[0].getCperiod());
		WaClassVO[] subvos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(subvos)){
			//抛出异常，给出提示
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "存在已经审核的数据，不能增加项目 "*/);
		}
		//boolean needReGenFormula = false;
		for (int i = 0; i < vos.length; i++) {
			if(vos[i].getPk_group().equals(vos[i].getPk_org())){
				insertGroupClassItem(vos[i]);
			}else{
				vos[i] = insert(vos[i], false);  //不用regenerateSystemFormula
			}
		}
		//t统一处理
		regenerateSystemFormula(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0]
				.getCperiod());
		
		// HR本地化：将有汇总项的公式全部重算
		generateTotalItemFormula(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0]
				.getCperiod());

		//得到需要设置排序的薪资发放项目
		resetCompuSeq(classvo);

		resetPaydataFlag(vos[0].getPk_wa_class(),vos[0].getCyear(),vos[0].getCperiod());
		//同步缓存
		WaCacheUtils.synCache(vos[0].getTableName());
		return queryItemInfoVO(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0].getCperiod(),null);
	}

	@Override
	public WaClassItemVO[] updateWaClassItemVO(WaClassItemVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		vo = updateS(vo);
		WaCacheUtils.synCache(vo.getTableName());
		return queryItemInfoVO(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(),null);
	}

	private WaClassItemVO updateS(WaClassItemVO vo) throws BusinessException {
		//20151028  xiejie3  NCdp205519152 测试要求修改为发放手工输入项时，默认为可修改。begin
		//20151201 shenliangc NCdp205519152 不是问题，代码回退。
//		updateItemPowerEditflag(vo);
		//end
		vo = getServiceTemplate().update(vo,true);
		/**
		 * 必须先修改公式再设置计算顺序
		 */
		//如果是数值型 并且更改计税标识，需要重新设置扣税基数的公式
		if(needRegenerateSystemFormula(vo)){
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		// HR本地化：将有汇总项的公式全部重算
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());

		//设计计算顺序
		if(needResetCompuSeq(vo)){
			resetCompuSeq(vo);
		}
		//修改项目，增加项目 应该将  对应的薪资数据清空计算标识、审核、发放
		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		//同步父方案
		synParentClassItem(VOStatus.UPDATED, vo);
		return vo;
	}

	private boolean 	needRegenerateSystemFormula(WaClassItemVO vo){
		return vo.getIitemtype()==0 && !ItemUtils.isSystemItemKey(vo.getItemkey());
	}


	@Override
	public WaClassItemVO[] batchAddClassItemVOs(WaLoginContext context, String[] pk_wa_items) throws BusinessException {
		BDPKLockUtil.lockString(pk_wa_items);

		//锁定这些项目使用到的项目分类。查看项目分类是否还存在

		//查看共公告项目是否存在
		WaItemVO[] item = NCLocator.getInstance().lookup(IItemManageService.class).queryWaItemVOByPks(pk_wa_items);
		if (item == null || item.length != pk_wa_items.length) {
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0180")/*@res "数据已不同步，请重新打开节点在试"*/);
		}

		if(context.getNodeType().equals(NODE_TYPE.ORG_NODE)){
			return batchAddParentAndCurrentclass(context.getPk_group(),context.getPk_org(),context.getPk_wa_class(),
					context.getCyear(),context.getCperiod(), item);

		}else{
			//集团的
			/**
			 * 查看集团分配出去的组织方案（当前期间）是否有已经审核发放数据的。如果有则给予提示
			 */
			//guoqt年度期间应该是选择的年度期间，而不是集团方案的年度期间
			context.getWaLoginVO().setCyear(context.getCyear());
			context.getWaLoginVO().setCperiod(context.getCperiod());
			WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(context.getWaLoginVO());
			if(!ArrayUtils.isEmpty(vos)){
				//抛出异常，给出提示
				//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
				throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "存在已经审核的数据，不能增加项目 "*/);
			}

			//首先给集团方案执行插入
			WaClassItemVO[]  resultvos = batchAddParentAndCurrentclass(context.getPk_group(),
					context.getPk_org(),context.getPk_wa_class(),context.getCyear(),context.getCperiod(),item);

			//然后给所有的分配出去的组织方案执行插入
			WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(context.getWaLoginVO());
			if(allvos!=null){
				for (WaClassVO waClassVO : allvos) {
					//guoqt NCZX问题NCdp205050978集团方案分配到多个组织后各组织期间不一致时，保存时提示引用项目未添加到方案
					if(waClassVO.getCyear().equals(context.getCyear())&&waClassVO.getCperiod().equals(context.getCperiod())){
						batchAddParentAndCurrentclass(waClassVO.getPk_group(),waClassVO.getPk_org(),waClassVO.getPk_wa_class(),
								waClassVO.getCyear(),waClassVO.getCperiod(), item);
					}
					
				}
			}

			return resultvos;
		}


	}


	private  WaClassItemVO[]  batchAddParentAndCurrentclass(String pk_group,String pk_org,String pk_wa_class,String cyear,String cperiod ,WaItemVO[] item) throws BusinessException{
		WaClassItemVO[] classItems = new WaClassItemVO[item.length];
		IWaClass  waClass  = NCLocator.getInstance().lookup(IWaClass.class);



		//首先插入所有项目
		WaClassItemVO 	tempclassitem = new WaClassItemVO();
		tempclassitem.setPk_org(pk_org);
		tempclassitem.setPk_group(pk_group);
		tempclassitem.setPk_wa_class(pk_wa_class);
		tempclassitem.setCyear(cyear);
		tempclassitem.setCperiod(cperiod);
		int beginseq = getMaxDisplayOrder(tempclassitem);

		for (int i = 0; i < classItems.length; i++) {

			classItems[i] = new WaClassItemVO();
			classItems[i].setPk_org(pk_org);
			classItems[i].setPk_group(pk_group);
			classItems[i].setPk_wa_class(pk_wa_class);
			classItems[i].setCyear(cyear);
			classItems[i].setCperiod(cperiod);
			classItems[i].merge(item[i]);
			classItems[i].setIdisplayseq(beginseq+i);

			classItems[i].setIcomputeseq(0);
			classItems[i].setStatus(VOStatus.NEW);
			//Round_type默认值为0
			//if(classItems[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())){
			classItems[i].setRound_type(RoundTypeEnum.ROUND.value());
			//}

		}

		WaClassVO tempvo = new WaClassVO();
		tempvo.setPk_org(pk_org);
		tempvo.setPk_group(pk_group);
		tempvo.setPk_wa_class(pk_wa_class);
		tempvo.setCyear(cyear);
		tempvo.setCperiod(cperiod);
		batchadd(tempvo, classItems);

		//生成所有项目的系统公式
		regenerateSystemFormula(pk_org, pk_wa_class, cyear, cperiod);//(tempclassitem);
		
		// HR本地化：将有汇总项的公式全部重算
		generateTotalItemFormula(pk_org, pk_wa_class, cyear, cperiod);

		//重新设定计算顺序
		resetCompuSeq(tempclassitem);

		//当前方案重新设定发放标示
		resetPaydataFlag(tempvo.getPk_wa_class(),tempvo.getCyear(),tempvo.getCperiod());

		//正常结薪薪资项目默认权限
		if(WaLoginVOHelper.isNormalClass(tempvo)){
			insertItemPower(classItems);
		}

		//同步父方案	.父方案不需要重新设定计算顺序与生成系统公式
		WaClassVO parentvo = waClass.queryParentClass(pk_wa_class,cyear,cperiod);

		if(parentvo!=null){
			for (int i = 0; i < classItems.length; i++) {
				classItems[i] = new WaClassItemVO();
				classItems[i].setPk_org(pk_org);
				classItems[i].setPk_group(pk_group);
				classItems[i].setPk_wa_class(parentvo.getPk_wa_class());
				classItems[i].setCyear(parentvo.getCyear());
				classItems[i].setCperiod(parentvo.getCperiod());
				classItems[i].merge(item[i]);
				classItems[i].setIdisplayseq(beginseq+i);

				classItems[i].setIcomputeseq(0);
				classItems[i].setStatus(VOStatus.NEW);
				//Round_type默认值为0
				//if(classItems[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())){
				classItems[i].setRound_type(RoundTypeEnum.ROUND.value());
				//}
			}

			ArrayList<WaClassItemVO> classItemVOList = new ArrayList<WaClassItemVO>();
			HashMap<String, String> map = getClassitemDAO().isItemExist(parentvo, classItems);
			for(WaClassItemVO waClassItemVO:classItems){
				if(map.get(waClassItemVO.getItemkey())==null){
					classItemVOList.add(waClassItemVO);
				}
			}

			batchadd(parentvo, classItems);

			//父方案默认分配权限
			if(!classItemVOList.isEmpty())
				insertItemPower(classItemVOList.toArray( new WaClassItemVO[classItemVOList.size()]));
			//重新设定发放标示（父方案的）
			resetPaydataFlag(parentvo.getPk_wa_class(),tempvo.getCyear(),tempvo.getCperiod());

		}

		//更新缓存
		WaCacheUtils.synCache(WaClassItemVO.TABLE_NAME);


		return queryItemInfoVO( pk_org,pk_wa_class,cyear,cperiod,null);
	}

	private void insertItemPower(WaClassItemVO[] vos) throws BusinessException {
		ArrayList<ItemPowerVO> itemPowerVOs = new ArrayList<ItemPowerVO>();
		for (WaClassItemVO vo : vos) {
			ItemPowerVO itemPowerVO = new ItemPowerVO();
			itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
			itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
			itemPowerVO.setModuleflag(Integer.valueOf(0));
			itemPowerVO.setPk_group(vo.getPk_group());
			itemPowerVO.setPk_org(vo.getPk_org());
			itemPowerVO.setPk_subject(AuditInfoUtil.getCurrentUser());
			itemPowerVO.setSubject_type(IWaSalaryctymgtConstant.SUB_TYPE_USER);
			itemPowerVO.setEditflag(vo.getIfromflag().intValue() == 2/* 手工输入项目 */? UFBoolean.TRUE : UFBoolean.FALSE);
			itemPowerVOs.add(itemPowerVO);
		}
		new ClassItemPowerServiceImpl().insertItemPowerVOs(
				itemPowerVOs.toArray(new ItemPowerVO[0]),
				itemPowerVOs.toArray(new ItemPowerVO[0]));
	}

	/**
	 * @throws DAOException
	 * @throws MetaDataException
	 * 
	 */
	private void batchadd(WaClassVO waclassvo,WaClassItemVO[] classItems) throws DAOException, MetaDataException{
		//首先删除已有的项目
		String delete  = "  delete from wa_classitem where pk_wa_class = '"+waclassvo.getPk_wa_class()+"' and cyear = '"
				+waclassvo.getCyear()+"' and cperiod = '"+waclassvo.getCperiod()+"' and itemkey in ("+FormatVO.formatArrayToString(classItems, WaClassItemVO.ITEMKEY)+") ";
		getClassitemDAO().getBaseDao().executeUpdate(delete);

		//批量插入
		getMDPersistenceService().saveBill(classItems);

	}





	@Override
	public WaClassItemVO[] setDisplayOrder(WaClassItemVO[] data) throws BusinessException {
		BDPKLockUtil.lockSuperVO(data);
		BDVersionValidationUtil.validateSuperVO(data);
		String[] pks = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			data[i].setIdisplayseq(i);
			pks[i] = data[i].getPrimaryKey();
		}
		new BaseDAO().updateVOArray(data, new String[] { WaClassItemVO.IDISPLAYSEQ });
		data = getServiceTemplate().queryByPks(WaClassItemVO.class, pks);
		Arrays.sort(data, new DisplayOrderComparator());
		return data;
	}

	/**
	 *
	 * @author: wh
	 * @date: 2009-12-11 下午02:35:39
	 * @since: eHR V6.0
	 * @走查人:
	 * @走查日期:
	 * @修改人:
	 * @修改日期:
	 */
	private final class DisplayOrderComparator implements Comparator<WaClassItemVO> {
		@Override
		public int compare(WaClassItemVO o1, WaClassItemVO o2) {

			return o1.getIdisplayseq() - o2.getIdisplayseq();
		}
	}

	/**
	 * @author wh on 2009-12-18
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryForCopy(java.lang.String,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public WaClassItemVO[] queryForCopy(String src_pk_wa_class, WaLoginContext context)
			throws BusinessException {
		String where = "  wa_classitem.pk_wa_item not in (select pk_wa_item from wa_classitem where pk_wa_class = '"
				+ context.getPk_wa_class() + "' and pk_org = '" + context.getPk_org() + "' and cyear = '"
				+ context.getWaYear() + "' and cperiod = '" + context.getWaPeriod() + "')";


		return getClassitemDAO().queryItemInfoVO(context.getPk_org(),src_pk_wa_class,context.getWaYear() ,context.getWaPeriod(),where);
	}

	@Override
	public WaClassItemVO[] queryCustomItemInfos(String pk_org, String pk_wa_class, String cyear,
			String cperiod) throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod,
				" wa_item.defaultflag = 'N'");
	}

	synchronized private int getMaxDisplayOrder(WaClassItemVO vo) throws BusinessException {

		String sql = "select max(idisplayseq)+1 from wa_classitem where pk_org = ? and pk_wa_class = ? and cyear = ? and cperiod = ?";
		SQLParameter par = new SQLParameter();
		par.addParam(vo.getPk_org());
		par.addParam(vo.getPk_wa_class());
		par.addParam(vo.getCyear());
		par.addParam(vo.getCperiod());
		Integer seq = (Integer) new BaseDAO().executeQuery(sql, par, new ColumnProcessor());
		if (seq == null)
			return 0;
		return seq;
	}

	@Override
	public String queryItemKeyByPk(String pk_wa_classitem) throws BusinessException {

		String sql = "select wa_item.itemkey from wa_item inner join wa_classitem on wa_item.pk_wa_item = wa_classitem.pk_wa_item where pk_wa_classitem = ?";
		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_classitem);
		BaseDAO dao = new BaseDAO();
		String itemKey = (String) dao.executeQuery(sql, par, new ColumnProcessor());

		return itemKey;
	}

	/**
	 * @author xuanlt on 2010-1-18
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItemInfos(nc.vo.uif2.LoginContext,
	 *      java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItemInfos(String pk_org, String pk_wa_class, String cyear,
			String cperiod) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod, null);

	}

	@Override
	public WaClassItemVO[] queryItemInfoVO(String pk_org,
			String pk_wa_class, String year, String period, String condition)
					throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, year, period, condition);
	}

//	20151104 xiejie3  显示设置增加薪资项目权限控制。
	@Override
	public WaClassItemVO[] queryAllClassItemInfosByPower(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), " wa_item.itemkey  in ("+ItemPowerUtil.getItemPowerCode(context)+")   ");

	}
//	end
	@Override
	public WaClassItemVO[] queryAllClassItemInfos(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), null);

	}

	//2015-08-17 zhousze 合并补丁 NCdp205246061   各种查询界面的查询条件中选择“数字型薪资项目”(或“字符型薪资项目”)时，
	// 还能够选到无权限看到的项目 begin
	public WaClassItemVO[] queryAllClassItemPowerInfos(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), " wa_item.pk_wa_item in ("+ItemPowerUtil.getItemPower(context)+")");
	}
	// end

	@Override
	public ItemVO[] getFormulaInitVO(WaLoginContext context) throws BusinessException {
		ArrayList<ItemVO> itemList = new ArrayList<ItemVO>();

		initClassItem(context, itemList);

		if (waCommonImpl.isHiEnabled(context.getPk_group())) {
			// TODO 查询信息集，目前信息集还没有做
		} else {
			// 只安装薪资不安装员工信息，需要在公式设置的项目一栏加入“人员类别”、“所在部门”
			itemServiceImpl.initItemByPsncl(context, itemList);
			itemServiceImpl.initItemByDept(context, itemList);
		}

		return itemList.toArray(new ItemVO[0]);
	}

	public void initClassItem(WaLoginContext context, ArrayList<ItemVO> itemList) throws BusinessException {
		// 得到所有的薪资发放项目
		WaClassItemVO[] clsItems = queryAllClassItemInfos(context);
		// 添加发放项目

		if (ArrayUtils.isEmpty(clsItems)) {
			Logger.warn("没有查询到薪资发放项目，这是不正常的！");
			return;
		}
		ContentVO[] contentVOs = new ContentVO[clsItems.length];
		for (int i = 0; i < contentVOs.length; i++) {
			contentVOs[i] = buildByWaItemVO(clsItems[i]);
		}
		ItemVO waItem = new ItemVO(NCLangResOnserver.getInstance().getStrByID("common", "UC000-0003385")/*
		 * @res
		 * "薪资项目"
		 */);
		waItem.setContent(contentVOs);
		itemList.add(waItem);

	}

	private ContentVO buildByWaItemVO(WaClassItemVO item) {
		ContentVO content = new ContentVO(item.getMultilangName());
		content.setColNameFlag(true); //
		content.setTableName("wa_data");
		content.setDigitFlag(item.getTypeEnumVO() == TypeEnumVO.FLOATTYPE);
		content.setColName(item.getItemkey());
		return content;
	}

	/**
	 * @author xuanlt on 2010-2-24
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItems(java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItems(String pk_wa_class) throws BusinessException {
		return getClassitemDAO().queryItemsByClassId(pk_wa_class, null);
	}

	/**
	 * @author xuanlt on 2010-2-24
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItems(java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItemsForFormular(String pk_wa_class) throws BusinessException {
		return getClassitemDAO().queryItemsByClassIdForFormular(pk_wa_class, null);
	}

	/**
	 * @author liangxr on 2010-3-17
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryItemInfoVO(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryItemInfoVO(String pk_org, String pk_wa_class, String year, String period)
			throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, year, period);
	}

	/**
	 * @author xuanlt on 2009-12-22
	 * @see nc.itf.hr.wa.IItemQueryService#queryMaxversionItems(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassItemVO[] queryMaxversionItems(WaClassVO vo) throws BusinessException {
		try {
			PersistenceDAO dao = new PersistenceDAO();
			StringBuilder sbd = new StringBuilder();
			sbd.append(" select * from wa_classitem  where cyear||cperiod=  (select max(cyear||cperiod) from wa_classitem where pk_wa_class = ? ) and pk_wa_class = ? and pk_group = ? and pk_org = ? ");
			SQLParameter para = new SQLParameter();
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());

			return dao.retrieveBySQL(WaClassItemVO.class, sbd.toString(), para);
		} catch (PersistenceDbException e) {
			Logger.error(e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0181")/*@res "查询集团薪资方案所关联的公共项目失败"*/,e);
		}

	}

	/**
	 * @author xuanlt on 2009-12-22
	 * @see nc.itf.hr.wa.IItemQueryService#queryMaxversionItems(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassItemVO[] queryVersionItems(WaClassVO vo,String cyear,String cperiod) throws BusinessException {
		try {
			PersistenceDAO dao = new PersistenceDAO();
			StringBuilder sbd = new StringBuilder();
			sbd.append(" select * from wa_classitem  where cyear = ? and cperiod = ? and pk_wa_class = ? and pk_group = ? and pk_org = ? ");
			SQLParameter para = new SQLParameter();
			para.addParam(cyear);
			para.addParam(cperiod);
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());

			return dao.retrieveBySQL(WaClassItemVO.class, sbd.toString(), para);
		} catch (PersistenceDbException e) {
			Logger.error(e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0181")/*@res "查询集团薪资方案所关联的公共项目失败"*/,e);
		}

	}

	@Override
	public WaItemVO[] queryCustomItems(WaLoginContext context) throws BusinessException{
		return new QueryByWaLoginContextAction(context," and defaultflag = 'N'").query();
	}


	/**
	 *
	 * @author: wh
	 * @date: 2009-12-29 下午02:43:25
	 * @since: eHR V6.0
	 * @走查人:
	 * @走查日期:
	 * @修改人:
	 * @修改日期:
	 */
	private final class QueryByWaLoginContextAction implements ItemQueryAction {
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	pk_org;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	period;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	year;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	pk_wa_class;

		private final String	condition;

		private QueryByWaLoginContextAction(WaLoginContext context,String condition){
			this(context.getPk_org(),context.getWaPeriod(),context.getWaYear(),context.getPk_wa_class(),condition);
		}

		/**
		 * @author wh on 2009-12-29
		 * @param pk_org
		 * @param period
		 * @param year
		 * @param pk_wa_class
		 */
		private QueryByWaLoginContextAction(String pk_org, String period, String year, String pk_wa_class,String condition) {
			this.pk_org = pk_org;
			this.period = period;
			this.year = year;
			this.pk_wa_class = pk_wa_class;
			this.condition = condition == null ? "" : condition;
		}

		@Override
		public WaItemVO[] query() {

			try {
				String where = " pk_wa_item in (select pk_wa_item from wa_classitem where pk_org = '" + pk_org
						+ "' and pk_wa_class = '" + pk_wa_class + "' and cyear = '" + year + "' and cperiod = '"
						+ period +"')"+condition+"  order by code";
				return getServiceTemplate().queryByCondition(WaItemVO.class, where);
			} catch (BusinessException e) {
				Logger.error(e);
				return null;
			}
		}
	}

	interface ItemQueryAction{
		WaItemVO[] query();
	}



	/**
	 *
	 * @param pk_class
	 * @param year
	 * @param period
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public GeneralVO[] getItemCaculateSeu(WaClassVO vo ) throws BusinessException {
		try {
			//updateItemCaculateSeu(vo);

			List<GeneralVO> generalVOList = new LinkedList<GeneralVO>();
			WaClassItemVO[] classitemVOs =getClassitemsBySeq(vo);
			if (classitemVOs != null && classitemVOs.length > 0) {
				WaClassVO waclassVO = NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByPK(vo.getPk_wa_class());
				waclassVO.setCyear(vo.getCyear());
				waclassVO.setCperiod(vo.getCperiod());

				ItemSort itemSort = new ItemSort();
				HashMap<WaClassItemVO, List<WaClassItemVO>> map = itemSort.getItemHashMap(classitemVOs, waclassVO);


				Iterator<WaClassItemVO> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					GeneralVO generalVO = new GeneralVO();
					WaClassItemVO classitemVO = iterator.next();
					generalVO.setAttributeValue("itemName", classitemVO.getMultilangName());
					List<WaClassItemVO> list = map.get(classitemVO);
					if (list != null) {
						generalVO.setAttributeValue("dependItems", FormatVO.formatArrayToString(list.toArray(new WaClassItemVO[list.size()]), "name", "", ""));
					}
					generalVO.setAttributeValue("dataFrom", classitemVO.getFromEnumVO().getName());
					generalVOList.add(generalVO);
				}
			}
			return generalVOList.size() == 0 ? null : generalVOList.toArray(new GeneralVO[generalVOList.size()]);
		} catch (DAOException de) {
			Logger.error(de.getMessage(),de);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0182")/*@res "得到薪资发放项目的计算顺序失败"*/);
		}catch(BusinessException be){
			Logger.error(be.getMessage(),be);
			throw  be;
		}catch(Exception e){
			Logger.error(e.getMessage(),e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0182")/*@res "得到薪资发放项目的计算顺序失败"*/);
		}
	}


	/**
	 * 更新数据的计算顺序
	 * @param pk_class
	 * @param year
	 * @param period
	 * @throws BusinessException
	 */
	public void updateItemCaculateSeu(WaClassVO vo ) throws BusinessException {
		try {
			WaClassItemVO[] classitemVOs =getClassitemVOsForSequ(vo);
			if (classitemVOs != null && classitemVOs.length > 0) {

				//填充薪资规则的计算公式
				classitemVOs = fillWageFormRule(classitemVOs);
				WaClassVO waclassVO = NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByPK(vo.getPk_wa_class());
				waclassVO.setCyear(vo.getCyear());
				waclassVO.setCperiod(vo.getCperiod());
				ItemSort itemSort = new ItemSort();
				classitemVOs = itemSort.getSortedWaClassItemVOs(classitemVOs, waclassVO);
				updateItemCaculateSeu(classitemVOs);

				//更新薪资发放项目顺序，要同步缓存
				if(!ArrayUtils.isEmpty(classitemVOs)){
					WaCacheUtils.synCache(classitemVOs[0].getTableName());
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}


	private  WaClassItemVO[] fillWageFormRule(WaClassItemVO[] classitemVOs) throws BusinessException{
		//填充薪资规则的计算公式
		for (WaClassItemVO classitemVO : classitemVOs) {
			if(classitemVO.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)){//薪资规则表
				String formula = classitemVO.getVformula();
				if (!StringUtils.isBlank(formula)) {
					Map<String, FunctionVO> map =  HrWaXmlReader.getInstance().getFormulaParser();
					FunctionVO functionVO =		map.get(FunctionKey.WAGEFORM);
					String[] pks = FormulaXmlHelper.getArguments(
							functionVO, formula);
					if (classitemVO.getVformula() != null) {
						classitemVO.setVformula(getFormulaFromWageForm(pks[0]));
					}
				} else {
					classitemVO.setVformula("");
				}
			}
		}

		return classitemVOs;

	}

	/**
	 * 薪资规则表定义的计算顺序！
	 *
	 * @param pk_wa_wageForm
	 * @return
	 * @throws BusinessException
	 */
	public String getFormulaFromWageForm(String pk_wa_wageForm) throws BusinessException {
		StringBuffer sqlB = new StringBuffer("select  vformula  from wa_wageformdet where pk_wa_wageform = '" + pk_wa_wageForm + "'");
		GeneralVO[] generalVOs = (GeneralVO[])new BaseDAO().executeQuery(sqlB.toString(), new GeneralVOProcessor(GeneralVO.class));

		if (generalVOs != null) {
			return FormatVO.formatArrayToString(generalVOs, "vformula", "");
		} else {
			return null;
		}
	}

	public void updateItemCaculateSeu(WaClassItemVO[] classitemVOs) throws BusinessException {
		PersistenceManager sessionManager = null;
		try {
			StringBuffer sqlB = new StringBuffer();
			sqlB.append("update wa_classitem set wa_classitem.icomputeseq = ? where wa_classitem.pk_wa_classitem = ?"); // 1
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (WaClassItemVO classitemVO : classitemVOs) {
				SQLParameter parameters = new SQLParameter();
				parameters.addParam(classitemVO.getIcomputeseq());
				parameters.addParam(classitemVO.getPk_wa_classitem());
				session.addBatch(sqlB.toString(), parameters);
			}
			session.executeBatch();
		} catch (DbException e) {
			throw new nc.vo.pub.BusinessException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}

	}

	@Override
	public HrFormula getSystemFormula(String pk_org, String pk_wa_class,
			String cyear, String cperiod, String itemKey) {
		HrFormula f =  new FormulaUtils().getSystemFormula(pk_org, pk_wa_class, cyear,
				cperiod, itemKey);

		return f;
	}


	/**
	 * 给出一个薪资项目，得到依赖于该项目的薪资发放项目
	 *  对于系统项目使用默认公式的，即使依赖于该项目，也不统计（允许删除该项目，并且删除后重新设置计算公式）
	 *  	    对于系统项目不使用默认公式的，才统计
	 *
	 * @param classItemVO
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassItemVO[]  getDependorItems(WaClassItemVO classItemVO) throws BusinessException{
		String itemkey = classItemVO.getItemkey();

		ArrayList<WaClassItemVO> list = new ArrayList<WaClassItemVO>();
		WaClassVO vo = new WaClassVO();
		vo.setPk_wa_class(classItemVO.getPk_wa_class());
		vo.setCyear(classItemVO.getCyear());
		vo.setCperiod(classItemVO.getCperiod());
		WaClassItemVO[] classitemVOs =getClassitemVOsForSequ(vo);
		if (classitemVOs != null && classitemVOs.length > 0) {
			ISortHelper  helper = new WaOrtherFuncSortHelper();


			//填充薪资规则的计算公式
			classitemVOs = fillWageFormRule(classitemVOs);
			//如果来源是其他系统薪资，需要额外处理
			for (int index = 0; index < classitemVOs.length; index++) {
				if(!classitemVOs[index].getIssysformula().booleanValue()){
					if(classitemVOs[index].getIfromflag()==6){
						//使用帮助类额外处理,看看该项目是否以来itemkey
						List<String> ll = 	helper.getDependItemKeys(classitemVOs[index]);
						if(ll.contains(itemkey)){
							list.add(classitemVOs[index]);
						}

					}else{

						String formular=classitemVOs[index].getVformula();
						if (!StringUtils.isBlank(formular)) {
							Pattern p = Pattern.compile(itemkey + "[0-9]");
							Matcher m = p.matcher(formular);
							if (formular.contains(itemkey) && !m.find())
								list.add(classitemVOs[index]);
						}

					}
				}
			}
		}

		return list.toArray(new WaClassItemVO[list.size()] );
	}


	/**
	 * 更新
	 * @param vo
	 * @return
	 */
	@Override
	public WaClassItemVO[] updateGroupClassItem(WaClassItemVO vo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//抛出异常，给出提示
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0183")/*@res "存在已经审核的数据，不能修改项目 "*/);
		}

		//首先个更新自己的
		vo = updateS(vo);

		//然后更新子方案的
		/**
		 * 插入子组织的项目
		 */

		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);


		if(!ArrayUtils.isEmpty(allvos)){
			for (WaClassVO waClassVO : allvos) {
				//guoqt NCZX问题NCdp205050978集团方案分配到多个组织后各组织期间不一致时，保存时提示引用项目未添加到方案
				if(waClassVO.getCyear().equals(vo.getCyear())&&waClassVO.getCperiod().equals(vo.getCperiod())){
					insertClassItem2SubClass(vo,waClassVO);
				}
			}
		}

		WaCacheUtils.synCache(vo.getTableName());
		return queryItemInfoVO(vo.getPk_org(),vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod(),null);
		
		//	20150728 xiejie3 补丁合并，NCdp205382570在薪资发放中，显示设置里面薪资项目有些不是显示内容，begin 	
	}
	//guoqt同步薪资发放项目显示顺序
	public WaClassItemVO[] updateGroupClassItemdisp(WaClassItemVO[] ordervo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(ordervo[0].getPk_wa_class());
		classvo.setCyear(ordervo[0].getCyear());
		classvo.setCperiod(ordervo[0].getCperiod());
		//判断薪资方案是否审核
		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//抛出异常，给出提示
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0183")/*@res "存在已经审核的数据，不能修改项目 "*/);
		}

		//然后更新子方案的显示顺序
		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);
		//重设显示顺序
		for(int i = 0;i<ordervo.length;i++){
			ordervo[i].setIdisplayseq(i);
		}
		if(!ArrayUtils.isEmpty(allvos)){
			for (WaClassVO waClassVO : allvos) {
				updateClassItem2SubClass(ordervo,waClassVO);
			}
		}

		WaCacheUtils.synCache(ordervo[0].getTableName());
		return queryItemInfoVO(ordervo[0].getPk_org(),ordervo[0].getPk_wa_class(),ordervo[0].getCyear(),ordervo[0].getCperiod(),null);
//		end

	}


	/**
	 * 删除集团薪资方案
	 * @param vo
	 * @return
	 */
	@Override
	public WaClassItemVO[] deleteGroupClassItem(WaClassItemVO vo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//抛出异常，给出提示
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "子方案中"*/+ ResHelper.getString("60130classpower","060130classpower0184")/*@res "存在已经审核的数据，不能删除项目 "*/);
		}

		//首先个删除自己的
		deleteWaClassItemVO(vo);
		CacheProxy.fireDataDeleted(vo.getTableName(), vo.getPk_wa_classitem());
		//然后更新子方案的
		/**
		 * 插入子组织的项目
		 */

		WaClassVO[] allvos = getClassitemDAO().queryGroupAssignedWaclass(classvo);
		if (allvos != null) {
			for (WaClassVO waClassVO : allvos) {
				deleteClassItem2SubClass(vo, waClassVO);
			}
		}
		return queryItemInfoVO(vo.getPk_org(),vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod(),null);

	}

	@Override
	public boolean copyClassItems(WaClassVO srcVO,WaClassVO destVO) throws BusinessException{
		//查询出所有的发放项目
		WaClassItemVO[] newvos = 	getClassitemDAO().queryItemInfoVO(srcVO.getPk_org(), srcVO.getPk_wa_class(), srcVO.getCyear(), srcVO.getCperiod());
		//替换薪资方案，薪资年月  状态 。
		for (int i = 0; i < newvos.length; i++) {
			WaClassItemVO waClassItemVO = newvos[i];
			waClassItemVO.setPk_wa_class(destVO.getPk_wa_class());
			waClassItemVO.setCyear(destVO.getCyear());
			waClassItemVO.setCperiod(destVO.getCperiod());
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setStatus(VOStatus.NEW);

		}
		getMDPersistenceService().saveBillWithRealDelete(newvos);
		WaCacheUtils.synCache(newvos[0].getTableName());
		return true;

		//保存

	}

	@Override
	public WaClassItemVO[] queryByCondition(LoginContext context, String strFromCond,String strWhereCond,String strOrderCond)
			throws BusinessException {
		return getClassitemDAO().queryByCondition(context, strFromCond, strWhereCond, strOrderCond);
	}

	@Override
	public WaClassItemVO[] queryItemWithAccount(String pk_org,
			String pk_wa_class, String year, String period)
					throws BusinessException {
		return getClassitemDAO().queryItemWithAccount(pk_org, pk_wa_class, year, period);
	}

	//2014/05/23 shenliangc为解决薪资发放节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	//名称不同步的原因是查询项目名称逻辑没有添加年度期间限制，全部取方案起始期间的发放项目名称。
	@Override
	public WaClassItemVO[] queryItemsByPK_wa_class(String pk_wa_class, String cyear, String cperiod) throws BusinessException
	{

		return this.getClassitemDAO().queryItemsByPK_wa_class(pk_wa_class, cyear, cperiod);
	}

	public WaClassItemVO queryItemWithItemkeyAndPK_wa_class(String itemkey, String pk_wa_class, String cyear, String cperiod) throws BusinessException
	{
		return null;
	}
	//	public WaClassItemVO[] batchAddGroupClassItem(WaLoginContext context , String[] pk_wa_items) throws BusinessException{
	////		for (int i = 0; i < pk_wa_items.length; i++) {
	////		    insertGroupClassItem(vo)
	////		}
	//	}
	
	
	//shenliangc 20140823 本方案本期间内薪资发放项目名称已存在
	public boolean itemNameIsExist(String pk_wa_class, String cyear, String cperiod, WaClassItemVO vo) throws BusinessException{
		return this.getClassitemDAO().itemNameIsExist(pk_wa_class, cyear, cperiod, vo);
	}

}
