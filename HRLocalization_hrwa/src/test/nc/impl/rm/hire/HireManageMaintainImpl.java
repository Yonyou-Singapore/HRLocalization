package nc.impl.rm.hire;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.PfMessageUtil;
import nc.bs.uap.lock.PKLock;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.rm.hire.validator.HireUniqueValidator;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.rm.IActiveManageService;
import nc.itf.rm.ICheckinManageMaintain;
import nc.itf.rm.IHireManageMaintain;
import nc.itf.rm.IHireQueryService;
import nc.itf.rm.IInterviewManageService;
import nc.itf.rm.IRMPsndocManageService;
import nc.itf.rm.IRMPsnlibManageService;
import nc.itf.rm.IRMPsnlibQueryMaintain;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.rm.checkin.AggCheckinVO;
import nc.vo.rm.checkin.CheckinStatusEnum;
import nc.vo.rm.checkin.CheckinVO;
import nc.vo.rm.hire.AggHireVO;
import nc.vo.rm.hire.HireConst;
import nc.vo.rm.hire.HireItemVO;
import nc.vo.rm.hire.HireSourceEnum;
import nc.vo.rm.hire.HireVO;
import nc.vo.rm.hire.HireValidator;
import nc.vo.rm.psndoc.common.RMApplyStatusEnum;
import nc.vo.rm.pub.IHRRMCommonConst;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.rm.psndoc.AggRMPsndocVO;
import nc.vo.rm.psndoc.RMPsndocVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MapUtils;

public class HireManageMaintainImpl implements IHireManageMaintain {

	private SimpleDocServiceTemplate serviceTemplate;
	private IPFWorkflowQry workflowQry;
	private IPersistenceUpdate perstUpdate;
	private IRMPsndocManageService psndocManageService;
	private IRMPsnlibQueryMaintain psnLibQuery;
	private ICheckinManageMaintain checkinManageService;
	private IInterviewManageService interviewManageService;
	private BillCodeContext billCodeContext;
	private IPersistenceRetrieve perstRetrieve;
	private IActiveManageService activeMagService;
	private IRMPsnlibManageService psnLibManageService;
	private HrBatchService batchService;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if(serviceTemplate==null){
			serviceTemplate = new SimpleDocServiceTemplate("158a7b88-0d9d-4504-bc16-f5be111f0a11");
		}
		return serviceTemplate;
	}
	private HrBatchService getBatchService(){
		if(batchService==null){
			batchService = new HrBatchService("158a7b88-0d9d-4504-bc16-f5be111f0a11");
		}
		return batchService;
	}
	public IPFWorkflowQry getIPFWorkflowQry() {
		if(workflowQry == null) {
			workflowQry = NCLocator.getInstance().lookup( IPFWorkflowQry.class );
		}
		return workflowQry;
	}
	public IPersistenceUpdate getIPersistenceUpdate() {
		if(perstUpdate == null) {
			perstUpdate = NCLocator.getInstance().lookup( IPersistenceUpdate.class );
		}
		return perstUpdate;
	}
	private IRMPsndocManageService getIRMPsndocManageService(){
		if(psndocManageService==null){
			psndocManageService = NCLocator.getInstance().lookup(IRMPsndocManageService.class);
		}
		return psndocManageService;
	}

	public IRMPsnlibQueryMaintain getPsnLibQuery() {
		if(psnLibQuery == null){
			psnLibQuery =  NCLocator.getInstance().lookup(IRMPsnlibQueryMaintain.class);
		}
		return psnLibQuery;
	}

	public ICheckinManageMaintain getCheckinManageService() {
		if(checkinManageService == null)
			checkinManageService = NCLocator.getInstance().lookup(ICheckinManageMaintain.class);
		return checkinManageService;
	}

	public IInterviewManageService getInterviewManageService() {
		if(interviewManageService == null)
			interviewManageService = NCLocator.getInstance().lookup(IInterviewManageService.class);
		return interviewManageService;
	}
	
	public IPersistenceRetrieve getIPersistenceRetrieve() {
		if(perstRetrieve == null) {
			perstRetrieve = NCLocator.getInstance().lookup( IPersistenceRetrieve.class );
		}
		return perstRetrieve;
	}
	
	public IActiveManageService getActiveMagService(){
		if(activeMagService == null)
			activeMagService = NCLocator.getInstance().lookup(IActiveManageService.class);
		return activeMagService;
	}
	
	public IRMPsnlibManageService getPsnLibManageService() {
		if(psnLibManageService == null)
			psnLibManageService = NCLocator.getInstance().lookup(IRMPsnlibManageService.class);
		return psnLibManageService;
	}
	/**
	 * 是否自动生成编码
	 * @param pk_group
	 * @param pk_org
	 * @return
	 */
	private boolean isAutoGenerateBillCode(String pk_group,	String pk_org) {
		return getBillCodeContext(pk_group, pk_org) != null;
	}

	private BillCodeContext getBillCodeContext(String pk_group, String pk_org) {
		if (billCodeContext == null) {
			try {
				billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class).getBillCodeContext(HireConst.BILLCODE,pk_group, pk_org);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
			}
		}
		return billCodeContext;
	}
	
	/**
	 * 提交编码
	 * @param vo
	 * @throws BusinessException
	 */
	private void commitBillCode(HireVO vo) throws BusinessException{
		//如果是自动生成编码，则提交
		if (!isAutoGenerateBillCode(vo.getPk_group(),vo.getPk_org())) 
			return;
		NCLocator.getInstance().lookup(IHrBillCode.class).commitPreBillCodes(HireConst.BILLCODE, 
				vo.getPk_group(), vo.getPk_org() , vo.getCode());
	}
	
	/**
	 * 批量      提交编码
	 * @param vo
	 * @throws BusinessException
	 */
	private void BatchCommitBillCode(HireVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return;
		//按组织分组
		Map<String, HireVO[]> hireMap = CommonUtils.group2ArrayByField(HireVO.PK_ORG, vos);
		for(String pk_org:hireMap.keySet()){
			HireVO[] hireVOs = hireMap.get(pk_org);
			if(ArrayUtils.isEmpty(hireVOs))
				continue;
			//如果是自动生成编码，则提交
			if (!isAutoGenerateBillCode(hireVOs[0].getPk_group(),pk_org)) 
				continue;
			NCLocator.getInstance().lookup(IHrBillCode.class).commitPreBillCodes(HireConst.BILLCODE, 
					hireVOs[0].getPk_group(), pk_org , StringPiecer.getStrArray(hireVOs, HireVO.CODE));
		}
	}
	
	/**
	 * 回退编码
	 * @param vo
	 * @throws BusinessException
	 */
	private void rollBackBillCode(HireVO vo) throws BusinessException{
		//如果是自动生成编码，则回退
		if (!isAutoGenerateBillCode(vo.getPk_group(),vo.getPk_org())) 
			return;
		NCLocator.getInstance().lookup(IBillcodeManage.class).rollbackPreBillCode(HireConst.BILLCODE,
				vo.getPk_group(), vo.getPk_org(),vo.getCode());
	}

	@Override
	public AggHireVO insertWithOutCheck(AggHireVO aggvo) throws BusinessException {
		if(aggvo == null)
			return null;
		
		HireVO vo = (HireVO) aggvo.getParentVO();
		if(StringUtils.isEmpty(vo.getPk_billtype()))
			vo.setPk_billtype(IHRRMCommonConst.BILLTYPE);
		try{
			//人员加锁
			lockPsndoc(aggvo);
			//更新应聘职位状态
			updateStatus(new AggHireVO[]{aggvo});
			AggHireVO reaggvo =  getServiceTemplate().insert(aggvo);
			commitBillCode((HireVO) reaggvo.getParentVO());
			return reaggvo;
		}finally{
			releasePsnLock(aggvo);
		}
	}
	

	
	@Override
	public AggHireVO insertWithCheck(LoginContext context,AggHireVO aggvo) throws BusinessException {
		if(aggvo == null)
			return null;
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new HireValidator());
		vService.validate(aggvo);
		//编码的唯一性校验
		HireUniqueValidator uniqueCheck = new HireUniqueValidator();
		uniqueCheck.checkUnique(aggvo);
//		//编制校验
//		HireCheckBuget bugetChecker = new HireCheckBuget();
//		String warningMsg = bugetChecker.checkBudget(context, new AggHireVO[]{aggvo});
//		if(!StringUtils.isEmpty(warningMsg))
//			return warningMsg;
		return insertWithOutCheck(aggvo);
	}
	
	@Override
	public AggHireVO updateWithOutCheck(AggHireVO aggvo) throws BusinessException {
		if(aggvo == null)
			return null;
		try{
			lockPsndoc(aggvo);
			//更新应聘职位状态
			updateStatus(new AggHireVO[]{aggvo});
			AggHireVO reaggvo =  getServiceTemplate().update(aggvo, true);
			return reaggvo;
		}finally{
			releasePsnLock(aggvo);
		}
	}
	public AggHireVO[] updateWithOutCheck(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		//更新应聘职位状态
		updateStatus(aggvos);
		AggHireVO[] reaggvos =  getBatchService().update(true,aggvos);
		return reaggvos;
		
	}
	@Override
	public AggHireVO updateWithCheck(LoginContext context,AggHireVO aggvo) throws BusinessException {
		if(aggvo == null)
			return null;
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new HireValidator());
		vService.validate(aggvo);
		//编码的唯一性校验
		HireUniqueValidator uniqueCheck = new HireUniqueValidator();
		uniqueCheck.checkUnique(aggvo);
//		//编制校验
//		HireCheckBuget bugetChecker = new HireCheckBuget();
//		String warningMsg = bugetChecker.checkBudget(context, new AggHireVO[]{aggvo});
//		if(!StringUtils.isEmpty(warningMsg))
//			return warningMsg;
		return updateWithOutCheck(aggvo);
	}

	@Override
	public void delete(AggHireVO aggvo) throws BusinessException {
		getServiceTemplate().delete(aggvo);
		//奇怪传过来的vostatus为unchanged
		aggvo.getHireVO().setStatus(VOStatus.DELETED);
		//更新应聘职位状态
		updateStatus(new AggHireVO[]{aggvo});
		//编码回滚
		rollBackBillCode((HireVO) aggvo.getParentVO());
	}

	@Override
	public void intoPsnlib(String pk_org, String pk_psndoc) throws BusinessException {
//		AggRMPsndocVO aggpsndocvo = getPsnLibQuery().queryByPK(pk_psndoc);
//		if(aggpsndocvo != null)
//			throw new BusinessException(ResHelper.getString("6021hire","06021hire0020")/*@res "人员已经存在于人才库，不能再次转人才库！"*/);
//		getIRMPsndocManageService().toPsnlib(pk_org, new String[]{pk_psndoc}, RMApplyStatusEnum.HIRE.toIntValue());
	}

	/**
	 * 审批流提交，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggHireVO[] doCommit(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		for ( AggHireVO aggvo : aggvos ) {
			HireVO billvo = (HireVO) aggvo.getParentVO();
			// 更新单据状态
			billvo.setApprove_state( IPfRetCheckInfo.COMMIT );
			billvo.setSubmit_date(PubEnv.getServerLiteralDate());
//			aggvo.setParentVO( billvo );
			aggvo = updateWithOutCheck(aggvo);

//			//更新人员状态为 录用中
//			HireItemVO[] itemvos = (HireItemVO[]) aggvo.getChildrenVO();
//			if(!ArrayUtils.isEmpty(itemvos)){
//				String[] pk_psndocs = new String[itemvos.length];
//				for(int i=0;i<itemvos.length;i++){
//					pk_psndocs[i] = itemvos[i].getPk_psndoc();
//				}
//				if(!ArrayUtils.isEmpty(pk_psndocs)){
//					//更改人员状态为录用中
//					getIRMPsndocManageService().updatePsndocStatusByPK(pk_psndocs, RMApplyStatusEnum.HIRE.toIntValue());
//					//更改面试状态
////					getInterviewManageService().toHireStatus(pk_psndocs);
//				}
//			}
//			//更改面试状态
//			String[] pk_interviews = StringPiecer.getStrArray(itemvos, HireItemVO.PK_INTERVIEW);
//			if(!ArrayUtils.isEmpty(pk_interviews)){
//				getInterviewManageService().toHireStatus(pk_interviews);
//			}
		}
		String pk_org = ((HireVO)aggvos[0].getParentVO()).getPk_org();
		Integer approvetype = SysInitQuery.getParaInt(pk_org, IHRRMCommonConst.PARAM_HIRE);
		if (approvetype != null && approvetype.intValue() == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			// 审批方式为直批时发送通知
//			deleted by weiningc 审批时不需要发送通知 20191007 start
//			sendMessage( PubEnv.getPk_group() , pk_org , aggvos );
			//end
		}
		return aggvos;
	}
	
	/**
	 * 发送通知消息
	 * @param pk_sort
	 * @param pkGroup
	 * @param pk_org
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessage(String pkGroup, String pk_org, AggHireVO[] aggvos)throws BusinessException {
		
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		//循环发送通知
		for(AggHireVO aggvo:aggvos){
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			messageVO.setBillVO(aggvo.getHireVO());
			messageVO.setMsgrescode(HireConst.HIREDIRAPPMSGCODE);
			messageVO.setPkorgs(new String[]{pk_org});
//			messageVO.setBusiVarValues(busiVarValues);//变量
//			messageVO.setReceiverPkUsers(//接收人 ，直批不用接收人
			messageSendServer. sendBuziMessage_RequiresNew(messageVO);
		}
	}
	

	/**
	 * 审批流删除，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggHireVO[] doDelete(AggHireVO[] aggvos) throws BusinessException {
		for ( AggHireVO aggvo : aggvos ) {
			HireVO mainvo = (HireVO) aggvo.getParentVO();
			String billType = mainvo.getPk_billtype();
			deleteOldWorknote( mainvo.getPrimaryKey() , billType );
			delete(aggvo);
		}
		return aggvos;
	}

	/**
	 * 审批流审批，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggHireVO[] doApprove(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
//		for(AggHireVO aggvo : aggvos) {
//			updateWithOutCheck(aggvo);
//		}
		updateWithOutCheck(aggvos);
		saveAfterBatchApprove(aggvos);
		return aggvos;
	}

	/**
	 * 审批流收回，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggHireVO[] doRecall(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		for(AggHireVO aggvo : aggvos) {
			HireVO billvo = (HireVO) aggvo.getParentVO();
			//  清空提交日期
			billvo.setSubmit_date(null);
			billvo.setStatus(VOStatus.UPDATED);
			updateWithOutCheck(aggvo);

//			//更新人员状态为  非 录用中
//			HireItemVO[] itemvos = (HireItemVO[]) aggvo.getChildrenVO();
//			if(!ArrayUtils.isEmpty(itemvos)){
////				String[] pk_psndocs  = StringPiecer.getStrArray(itemvos, HireItemVO.PK_PSNDOC);
////				if(!ArrayUtils.isEmpty(pk_psndocs)){
//					//更改面试状态
////					getInterviewManageService().toInterviewStatus(pk_psndocs);
////				}
//				for(HireItemVO item:itemvos){
//					String pk_psndoc = item.getPk_psndoc();
//					if(StringUtils.isEmpty(pk_psndoc)||item.getPsnstatus() == null)
//						continue;
//					//更改人员状态为非录用中
//					if(item.getPsnstatus() == null){
//						getIRMPsndocManageService().updatePsndocStatusByPK(new String[]{pk_psndoc},RMApplyStatusEnum.APPLY.toIntValue());
//					}else{
//						getIRMPsndocManageService().updatePsndocStatusByPK(new String[]{pk_psndoc},item.getPsnstatus());
//					}
//				}
//			}
		}
		return aggvos;
	}

	/**
	 * 审批流弃审，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggHireVO[] doUnApprove(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		String[] pks = StringPiecer.getStrArray(CommonUtils.getParentVOArrayFromAggVOs(HireVO.class, aggvos), HireVO.PK_HIRE);
		AggHireVO[] dbVOs = new HireQueryMaintainImpl().queryByPks(pks);
		for(AggHireVO aggvo : dbVOs) {
			int approvestate = aggvo.getHireVO().getApprove_state();
			checkPFPassingState(approvestate);
		}
		updateWithOutCheck(aggvos);
		return aggvos;
	}
	
	public void checkPFPassingState(int pfsate) throws BusinessException {
        // guoqt在审批节点操作单据是走nc.ui.hr.pf.action.PFUnApproveAction的doAction()方法，但是在消息中心的工作任务中是走该方法，所以要同时判断单据审批通过还是弃审，两种情况都不能取消审批
        if (IPfRetCheckInfo.PASSING == pfsate || IPfRetCheckInfo.NOPASS == pfsate)
        {
            throw new BusinessException(ResHelper.getString("6001pf", "06001pf0059")
            /* @res "单据审批通过或未通过,不能取消审批." */);
        }
        
    }

	/** 删除审批意见，审批流删除时调用
	 * @param primaryKey
	 * @param billtype
	 * @throws BusinessException
	 */
	public void deleteOldWorknote( String primaryKey , String billtype ) throws BusinessException {

		WorkflownoteVO workflownoteVOs[] = null;
		try {
			workflownoteVOs = getIPFWorkflowQry().queryWorkitems( primaryKey , billtype , WorkflowTypeEnum.Approveflow.getIntValue() , 0 );
		} catch( BusinessException ex ) {
			Logger.error( ex.getMessage() , ex );
		}
		if (workflownoteVOs != null && workflownoteVOs.length > 0) {
			PfMessageUtil.deleteMessagesOfWorknote(workflownoteVOs);
			getIPersistenceUpdate().deleteVOArray( null , workflownoteVOs , null );
		}
	}

	private void insertCheckinVO(HireItemVO itemvo) throws BusinessException{
		CheckinVO vo = new CheckinVO();
		vo.setPk_group(itemvo.getPk_group());
		vo.setPk_org(itemvo.getPk_org());
		vo.setPk_hire(itemvo.getPk_hire());
		vo.setPk_hire_body(itemvo.getPk_hire_body());
		vo.setPk_psndoc(itemvo.getPk_psndoc());
		vo.setPk_entryorg(itemvo.getPk_hire_org());
		vo.setPk_entrydept(itemvo.getPk_hire_dept());
		vo.setPk_entryjob(itemvo.getPk_hire_job());
		/**补丁合并： 添加职等、职级、岗位序列、职务类别、职务字段*/
		vo.setPk_entryjob2(itemvo.getPk_hire_job2());
		vo.setPk_entryjobtype(itemvo.getPk_hire_jobtype());
		vo.setPk_entrygrade(itemvo.getPk_hire_grade());
		vo.setPk_entryrank(itemvo.getPk_hire_rank());
		vo.setPk_entrypst(itemvo.getPk_hire_pst());
		//V636 职位发布联查应聘人员信息，添加字段
		vo.setPk_reg_job(itemvo.getPk_reg_job());
		
		vo.setCheckinorg(itemvo.getPk_plan_reg_org());
		vo.setPk_jobtype(itemvo.getHire_type());
		vo.setPk_psntype(itemvo.getPerson_type());
		vo.setContract_flag(itemvo.getContract_flag());
		vo.setContract_begindate(itemvo.getContract_begindate());
		vo.setContract_enddate(itemvo.getContract_enddate());
		vo.setContract_period(itemvo.getContract_period());
		vo.setContract_period_type(itemvo.getContract_period_type());
		vo.setContract_period_unit(itemvo.getContract_period_unit());
		vo.setPk_majororg(itemvo.getPk_majororg());
		vo.setWages_amount(itemvo.getWages_amount());
		vo.setIsprobationary(itemvo.getIsprobationary());
		vo.setProbationary_begindate(itemvo.getProbationary_begindate());
		vo.setProbationary_enddate(itemvo.getProbationary_enddate());
		vo.setProbationary_pay(itemvo.getProbationary_pay());
		vo.setProbationary_period(itemvo.getProbationary_period());
		vo.setProbationary_period_unit(itemvo.getProbationary_period_unit());
		vo.setCheckinstate(CheckinStatusEnum.WAIT.toIntValue());
		vo.setPk_activity(itemvo.getPk_active());
		vo.setPk_psndoc_job(itemvo.getPk_job());
		vo.setIsuse(UFBoolean.TRUE);
		if(!StringUtils.isEmpty(itemvo.getPk_plan_reg_org()))
			vo.setPk_org(itemvo.getPk_plan_reg_org());
		AggCheckinVO aggvo = new AggCheckinVO();
		aggvo.setParentVO(vo);
		getCheckinManageService().insert(aggvo);
	}
	@Override
	public void updateCheckinState(String[] pk_hireitems) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_hireitems))
			return;

		BaseDAO dao = new BaseDAO();
		InSQLCreator sqlCreator = new InSQLCreator();
		try{
			String inSQL = sqlCreator.getInSQL(pk_hireitems);
			String sql = "update rm_hire_body set registration_flag = 'Y' where pk_hire_body in ("+inSQL+") ";
			dao.executeUpdate(sql);
		}finally{
			sqlCreator.clear();
		}
		

	}
	@Override
	public AggHireVO[] batchInsert(HireItemVO[] itemvos, String pk_businessType,String pk_businessCode)throws BusinessException {
		if(ArrayUtils.isEmpty(itemvos))
			return null;
		String pk_org = itemvos[0].getPk_org();
		String pk_group = itemvos[0].getPk_group();
		AggHireVO[] aggvos = new AggHireVO[itemvos.length];
		String[] billCodes  = generateBillCodes(pk_org,itemvos.length);
		for(int i=0;i<itemvos.length;i++){
			HireVO mainvo = new HireVO();
			AggHireVO aggvo = new AggHireVO();
			mainvo.setPk_group(pk_group);
			mainvo.setPk_org(pk_org);
			mainvo.setApply_date(PubEnv.getServerLiteralDate());
			mainvo.setBillmaker(PubEnv.getPk_user());
			mainvo.setApprove_state(-1);
			mainvo.setPk_bustype(pk_businessType);
			mainvo.setBusiness_type(pk_businessCode);//交易类型编码
			mainvo.setCode(billCodes[i]);
			mainvo.setStatus(VOStatus.NEW);
			aggvo.setParentVO(mainvo);
			itemvos[i].setStatus(VOStatus.NEW);
			HireItemVO item = (HireItemVO) itemvos[i].clone();
			item.setStatus(VOStatus.NEW);
			HireItemVO[] items = new HireItemVO[1];
			items[0] = item;
			aggvo.setChildrenVO(items);
//			aggvos[i] = insertWithOutCheck(aggvo);
			aggvos[i] = aggvo;
			
		}
//		return aggvos;
		return batchInsertWithOutCheck(aggvos);
	}
	
	

	@Override
	public AggHireVO[] batchInsertWithCheck(LoginContext context,HireItemVO[] itemvos, String pk_businessType,String pk_businessCode)throws BusinessException {
		if(ArrayUtils.isEmpty(itemvos))
			return null;
		//编制校验
		//HireCheckBuget bugetChecker = new HireCheckBuget();
		//String warningMsg = bugetChecker.checkBudgetItems(context, itemvos);
		
		//人员信息校验
		HireValidator validator = new HireValidator();
		validator.checkPsnInfo(itemvos);
		
//		if(!StringUtils.isEmpty(warningMsg))
//			return warningMsg;
		
		return batchInsertWithOutCheck(itemvos, pk_businessType, pk_businessCode);
	}

	@Override
	public AggHireVO[] batchInsertWithOutCheck(HireItemVO[] itemvos, String pk_businessType,String pk_businessCode)throws BusinessException {
		if(ArrayUtils.isEmpty(itemvos))
			return null;
		String pk_org = itemvos[0].getPk_org();
		String pk_group = itemvos[0].getPk_group();
		AggHireVO[] aggvos = new AggHireVO[itemvos.length];
		String[] billCodes  = generateBillCodes(pk_org,itemvos.length);
		for(int i=0;i<itemvos.length;i++){
			HireVO mainvo = new HireVO();
			AggHireVO aggvo = new AggHireVO();
			mainvo.setPk_group(pk_group);
			mainvo.setPk_org(pk_org);
			mainvo.setApply_date(PubEnv.getServerLiteralDate());
			mainvo.setBillmaker(PubEnv.getPk_user());
			mainvo.setApprove_state(-1);
			mainvo.setPk_bustype(pk_businessType);
			mainvo.setBusiness_type(pk_businessCode);//交易类型编码
			mainvo.setCode(billCodes[i]);
			mainvo.setStatus(VOStatus.NEW);
			mainvo.setPk_billtype(IHRRMCommonConst.BILLTYPE);
			aggvo.setParentVO(mainvo);
			itemvos[i].setStatus(VOStatus.NEW);
			HireItemVO item = (HireItemVO) itemvos[i].clone();
			item.setStatus(VOStatus.NEW);
			HireItemVO[] items = new HireItemVO[1];
			items[0] = item;
			aggvo.setChildrenVO(items);
			aggvos[i] = aggvo;
		}
		return batchInsertWithOutCheck(aggvos);
	}
	
	public AggHireVO[] batchInsertWithOutCheck(AggHireVO[] aggvos) throws BusinessException {
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		NCObject[] billVOs = new NCObject[aggvos.length];
		HireVO[] hirevos = new HireVO[aggvos.length];
		//更新应聘职位状态,待优化为批量方法
		updateStatus(aggvos);
		for(int i=0;i<aggvos.length;i++){
			HireVO vo = (HireVO) aggvos[i].getParentVO();
			hirevos[i] = vo;
			// 添加审计信息
			AuditInfoUtil.addData(vo);
			billVOs[i] = NCObject.newInstance(aggvos[i]);
		}
		//加锁
		BDPKLockUtil.lockAggVO(aggvos);
		String[] pks = MDPersistenceService.lookupPersistenceService().saveBill(billVOs);
		BatchCommitBillCode(hirevos);
		return getServiceTemplate().queryByPks(AggHireVO.class, pks);
	}

	private String[] generateBillCodes(String pk_org, int length) throws BusinessException {
		//是否自动生成单据编号
		boolean isAuto = isAutoGenerateBillCode(PubEnv.getPk_group(), pk_org);
		if(isAuto){
			return NCLocator.getInstance().lookup(IHrBillCode.class).getBillCode(HireConst.BILLCODE, PubEnv.getPk_group(), pk_org,length);
		}
//		String prefix = "ZD" + HireConst.BILLCODE + PubEnv.getServerDate().toStdString();
		String prefix = "HIRE" + PubEnv.getServerDate().toStdString() + "-";
		//不自动生成单据号 /默认规则生成 "hire+yyyy-mm-dd-+流水号"
		String flowCode = getFlowCode( prefix);
		String[] billCodes =  new String[length];
		for ( int i = 0 ; i < length ; i++ ) {
			billCodes[i]= prefix + getFlowCode( flowCode , i ) ;
		}
		return billCodes;
	}

	/**
	 * 得到今天最大的流水号
	 *
	 * @param prefix
	 * @param codeField
	 * @param className
	 * @return 五位流水号 如:00001
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static String getFlowCode( String prefix) throws BusinessException {

		// 有指定前缀并且长度为22的ZDXXXX1111-11-11_00001
		String whereSql = HireVO.CODE + " like '" + prefix + "%' and len(" + HireVO.CODE + ") = 20 order by " + HireVO.CODE + " desc";
		HireVO[] vos = (HireVO[])new BaseDAO().retrieveByClause(HireVO.class, whereSql).toArray(new HireVO[0]);
		if (vos == null || vos.length == 0) {
			return "00001";
		}
		for ( HireVO vo : vos ) {
			String code = ( ( String ) vo.getAttributeValue(HireVO.CODE) ).substring( prefix.length() + 1 );
			try {
				Integer value = Integer.valueOf( code );
				if (value != null) {
					// 每天应该不会超过10万单据
					return StringUtils.leftPad( value + 1 + "" , 5 , '0' );
				}
			} catch( NumberFormatException ex ) {
				continue;
			}
		}
		return "00001";
	}

	/**
	 * 获取当前最大的流水号
	 *
	 * @param prefix
	 * @param i
	 *
	 * @return String
	 * @throws BusinessException
	 */
	public String getFlowCode( String code , int i ) throws BusinessException {
		Integer value = Integer.valueOf( code );
		return org.apache.commons.lang.StringUtils.leftPad( value + i + "" , 5 , '0' );
	}
	@Override
	public void saveAfterBatchApprove(Object[] datas) throws BusinessException {
		if(ArrayUtils.isEmpty(datas))
			return;
		for(Object obj:datas){
			AggHireVO aggvo = (AggHireVO) obj;
			//对于审批通过的人员自动插入到报到中
			HireVO vo = (HireVO) aggvo.getParentVO();
			if(vo.getApprove_state() == IPfRetCheckInfo.PASSING){
				HireItemVO[] itemvos = (HireItemVO[]) aggvo.getChildrenVO();
				if(!ArrayUtils.isEmpty(itemvos)){
					for(HireItemVO itemvo : itemvos){
						if(itemvo.getIshire().booleanValue()){
							//构造CheckInVO并保存
							insertCheckinVO(itemvo);
							//更改人员状态为报到
//							getIRMPsndocManageService().updatePsndocStatusByPK(new String[]{itemvo.getPk_psndoc()}, RMApplyStatusEnum.CHECKIN.toIntValue());
							getIRMPsndocManageService().updateApplyStatusByJobPks(RMApplyStatusEnum.CHECKIN.toIntValue(), RMApplyStatusEnum.HIRE.toIntValue(), new String[]{itemvo.getPk_job()});
							//更新招聘活动的实际录用人员
							if(!StringUtils.isEmpty(itemvo.getPk_active()))
								getActiveMagService().updateHireNum(itemvo.getPk_active(),1);
						}else{
							//未录用人员转应聘登记
							intoPsnReg(itemvo);
						}
					}
				}
			}else if(vo.getApprove_state() == IPfRetCheckInfo.NOPASS){
				HireItemVO[] itemvos = (HireItemVO[]) aggvo.getChildrenVO();
				if(!ArrayUtils.isEmpty(itemvos)){
					for(HireItemVO itemvo : itemvos){
						//审批不通过人员转应聘登记
						intoPsnReg(itemvo);
					}
				}
			}
		}
	}
	@Override
	public AggHireVO[] directApprove(int directApproveResult,String approveNote, AggHireVO[] aggvos) throws BusinessException {
		//审批意见
		WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[aggvos.length];
		HireVO[] updateMainVOs = new HireVO[aggvos.length];
		//需要更新的字段
		String[] updateFields = {HireVO.APPROVER, HireVO.APPROVE_DATE, HireVO.APPROVE_NOTE,
				HireVO.APPROVE_STATE};
		for ( int i = 0 ; i < aggvos.length ; i++ ) {
			HireVO mainvo = (HireVO) aggvos[i].getParentVO();
			// 获取回写信息，即设置需要修改的字段
			updateMainVOs[i] = changeBillData(mainvo, updateFields,approveNote, directApproveResult);
			// 执行审批操作前，将审批信息写到pub_workflownote中
			worknoteVOs[i] = buildWorkflownoteVO( directApproveResult ,  approveNote ,mainvo);
		}
		//批量新增审批意见
		getIPersistenceUpdate().insertVOArray( null , worknoteVOs , null );
		//批量更新信息
		getIPersistenceUpdate().updateVOArray(null, updateMainVOs, updateFields, null);
		//如果批准，转报到,未录用人员转应聘登记
//		if(directApproveResult == IPfRetCheckInfo.PASSING)
			saveAfterBatchApprove(aggvos);
		return aggvos;
	}
	
	/**
	 *  根据流程信息录入申请记录的流程回写信息
	 * @param updateFields： 需要更新的字段
	 * @param mainvo
	 * @param approveNote：审批意见
	 * @param directApproveResult：操作状态（批准等）
	 * @return
	 * @throws BusinessException
	 */
	private HireVO changeBillData( HireVO mainvo,String[] updateFields , String approveNote, Integer directApproveResult) throws BusinessException {
		if (mainvo == null) 
			return null;
		mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());	 //审批人pk_id
		mainvo.setAttributeValue(updateFields[1], PubEnv.getServerTime());	//审批时间
		mainvo.setAttributeValue(updateFields[2], approveNote);	//审批意见
		mainvo.setAttributeValue(updateFields[3], directApproveResult);	//操作状态
		return mainvo;
	}
	
	/**
	 * 生成审批意见
	 * @param directApproveResult	： 操作状态（批准等）
	 * @param approveNote	:	审批意见
	 * @param mainvo	:	调班主表
	 * @return
	 * @throws BusinessException
	 */
	public WorkflownoteVO buildWorkflownoteVO( int directApproveResult , String approveNote , HireVO mainvo )
		throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid( mainvo.getPk_hire());// 单据ID
		worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		worknoteVO.setChecknote( approveNote );// 审批意见
		worknoteVO.setDealdate( PubEnv.getServerTime() );// 审批时间
		worknoteVO.setSenddate( PubEnv.getServerTime() );// 发送日期
		worknoteVO.setPk_org( mainvo.getPk_org() );// 组织
		worknoteVO.setBillno( mainvo.getCode() );// 单据号
		worknoteVO.setSenderman( mainvo.getApprover() == null ? mainvo.getBillmaker() : mainvo.getApprover() );// 发送人
		worknoteVO.setApproveresult(IPfRetCheckInfo.NOSTATE == directApproveResult ? "R" : IPfRetCheckInfo.PASSING == directApproveResult
				? "Y": "N");
		worknoteVO.setApprovestatus( 1 );// 直批的状态
		worknoteVO.setIscheck(IPfRetCheckInfo.PASSING == directApproveResult ? "Y" : IPfRetCheckInfo.NOPASS == directApproveResult
				? "N": "X");
		worknoteVO.setActiontype( "APPROVE" );
		worknoteVO.setCheckman( mainvo.getApprover() );	//审批人
		worknoteVO.setWorkflow_type( WorkflowTypeEnum.Approveflow.getIntValue() );
		worknoteVO.setPk_billtype( mainvo.getPk_billtype() );// 单据类型
		return worknoteVO;
	}
	
	
	/**
	 * 给录用人员加锁，并发互斥问题
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	private void lockPsndoc(AggHireVO aggvo) throws BusinessException{
		if(aggvo == null)
			return;
		HireItemVO[] items = (HireItemVO[]) aggvo.getChildrenVO();
		if(ArrayUtils.isEmpty(items))
			return;
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		for(HireItemVO item : items){
			try {
				int i = 0;
				for (; i < 5&& !PKLock.getInstance().acquireLock(item.getPk_psndoc(), "hireapply",dsName); i++) {
					Thread.sleep(50);
				}
				if (i==5) {
					throw new BusinessException(ResHelper.getString("6021hire", "06021hire0030")/*@res "其他人正在录用或修改该人员，请稍后再试！"*/);
				}
			} catch (InterruptedException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage());
			}
		}
		
	}
	
	/**
	 * 释放录用人员锁
	 * @param pk_psndoc
	 */
	private void releasePsnLock(AggHireVO aggvo) throws BusinessException{
		if(aggvo == null)
			return;
		HireItemVO[] items = (HireItemVO[]) aggvo.getChildrenVO();
		if(ArrayUtils.isEmpty(items))
			return;
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		for(HireItemVO item : items){
			PKLock.getInstance().releaseLock(item.getPk_psndoc(), "hireapply", dsName);
		}
		
	}
	
	
	/**
	 * 更新录用人员的状态，改为录用中或其他状态
	 * @param aggvo
	 * @throws BusinessException
	 */
	private AggHireVO[] updateStatus(AggHireVO[] aggvos) throws BusinessException{
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		List<HireItemVO> itemList = new ArrayList<HireItemVO>();
		//录用来源为应聘人员管理，
		List<HireItemVO> psnItemList = new ArrayList<HireItemVO>();
		//来源为人才库
		List<HireItemVO> psnlibItemList = new ArrayList<HireItemVO>();
		
		//录用来源为应聘人员管理，
		List<HireItemVO> delPsnItemList = new ArrayList<HireItemVO>();
		//来源为人才库
		List<HireItemVO> delPsnlibItemList = new ArrayList<HireItemVO>();
		//面试主键
		List<String> newIntvList = new ArrayList<String>();
		
		for(AggHireVO aggvo : aggvos){
			HireVO hireVO = aggvo.getHireVO();
			HireItemVO[] items = (HireItemVO[]) aggvo.getChildrenVO();
			if(ArrayUtils.isEmpty(items))
				continue;
			for(HireItemVO item : items){
				itemList.add(item);
				//设置item状态
				if(VOStatus.DELETED == hireVO.getStatus())
					item.setStatus(VOStatus.DELETED);
				if(VOStatus.NEW == item.getStatus()){
					//根据来源分配
					//来源为应聘人员
					if(HireSourceEnum.RMPSNDOC.toIntValue() == item.getSourcetype()){
						psnItemList.add(item);
						String pk_interview = item.getPk_interview();
						if(!StringUtils.isEmpty(pk_interview))
							newIntvList.add(pk_interview);
					}else
						psnlibItemList.add(item);
					
				}else if(VOStatus.DELETED == item.getStatus()){
					if(HireSourceEnum.RMPSNDOC.toIntValue() == item.getSourcetype())
						delPsnItemList.add(item);
					else//人才库过滤的人员删除时返回初始状态
						delPsnlibItemList.add(item);
				}
			}
		}
		//更改应聘人员职位状态
		if(!CollectionUtils.isEmpty(psnItemList)){
			getIRMPsndocManageService().updateApplyStatusByJobPks(RMApplyStatusEnum.HIRE.toIntValue(),StringPiecer.getStrArray(psnItemList.toArray(new HireItemVO[0]), HireItemVO.PK_JOB));
			if(!CollectionUtils.isEmpty(newIntvList))
				getInterviewManageService().toHireStatus(newIntvList.toArray(new String[0]));
		}
		if(!CollectionUtils.isEmpty(psnlibItemList)){
			for(HireItemVO libItem : psnlibItemList){
				String pk_psn_job = getPsnLibManageService().updateStatusFromHire(libItem.getPk_org(), libItem.getPk_psndoc(), libItem.getPk_job(), RMApplyStatusEnum.HIRE.toIntValue());
				libItem.setPk_job(pk_psn_job);
			}
		}
		//删除录用人员，返回人员录用前的状态，如录用前没有状态则返回为应聘登记状态，若是面试过来的人员则面试状态重新设置为有效	
		if(!CollectionUtils.isEmpty(delPsnItemList)){
			Map<String,Integer> statusMap = new HashMap<String,Integer>();
			List<String> intvList = new ArrayList<String>();
			for(HireItemVO newItemVO : delPsnItemList){
				String pk_interview = newItemVO.getPk_interview();
				if(StringUtils.isNotBlank(pk_interview))
					intvList.add(pk_interview);
				statusMap.put(newItemVO.getPk_job(), newItemVO.getPsnstatus()==null?RMApplyStatusEnum.APPLY.toIntValue():newItemVO.getPsnstatus());
			}
			getIRMPsndocManageService().updateApplyStatusByJobPks(statusMap);
			
			if(!CollectionUtils.isEmpty(intvList))
				getInterviewManageService().toInterviewStatus(intvList.toArray(new String[0]));
		}
		//人才库过滤的人员删除时返回初始状态
		if(!CollectionUtils.isEmpty(delPsnlibItemList)){
			for(HireItemVO newItemVO : delPsnlibItemList){
				getPsnLibManageService().updateStatusFromHire(newItemVO.getPk_org(), newItemVO.getPk_psndoc(), newItemVO.getPk_job(), newItemVO.getPsnstatus()==null?RMApplyStatusEnum.INIT.toIntValue():newItemVO.getPsnstatus());
			}
		}
		return aggvos;
		
//		for(HireItemVO item : items){
//			
//			//新增人员，设置为录用状态，若是从面试来的，则面试设为无效
//			if(VOStatus.NEW == item.getStatus()){
//				//来源为应聘人员
//				if(HireSourceEnum.RMPSNDOC.toIntValue() == item.getSourcetype()){
//					getIRMPsndocManageService().updateApplyStatusByJobPks(RMApplyStatusEnum.HIRE.toIntValue(), item.getPsnstatus()==null?RMApplyStatusEnum.APPLY.toIntValue():item.getPsnstatus(), new String[]{item.getPk_job()});
//					String pk_interview = item.getPk_interview();
//					if(StringUtils.isNotBlank(pk_interview))
//						getInterviewManageService().toHireStatus(new String[]{pk_interview});
//				} else {//来源为人才库
//					String pk_psn_job = getPsnLibManageService().updateStatusFromHire(item.getPk_org(), item.getPk_psndoc(), item.getPk_job(), RMApplyStatusEnum.HIRE.toIntValue());
//					item.setPk_job(pk_psn_job);
//					
//				}
//			//删除录用人员，返回人员录用前的状态，如录用前没有状态则返回为应聘登记状态，若是面试过来的人员则面试状态重新设置为有效	
//			}else if(VOStatus.DELETED == item.getStatus()){
//				if(HireSourceEnum.RMPSNDOC.toIntValue() == item.getSourcetype()){
//					getIRMPsndocManageService().updateApplyStatusByJobPks(item.getPsnstatus()==null?RMApplyStatusEnum.APPLY.toIntValue():item.getPsnstatus(), RMApplyStatusEnum.HIRE.toIntValue(), new String[]{item.getPk_job()});
//					String pk_interview = item.getPk_interview();
//					if(StringUtils.isNotBlank(pk_interview))
//						getInterviewManageService().toInterviewStatus(new String[] {pk_interview});
//				}
//				else//人才库过滤的人员删除时返回初始状态
//					getPsnLibManageService().updateStatusFromHire(item.getPk_org(), item.getPk_psndoc(), item.getPk_job(), item.getPsnstatus()==null?RMApplyStatusEnum.INIT.toIntValue():item.getPsnstatus());
//			}
//		}
//		return aggvo;
	}
	
	/**
	 * 设置审批通过但未录用或审批不通过的人员为应聘登记状态
	 * @param item
	 * @throws BusinessException
	 */
	public void intoPsnReg(HireItemVO item) throws BusinessException{
		if(item == null )
			return;
		if(HireSourceEnum.RMPSNDOC.toIntValue() == item.getSourcetype())
			getIRMPsndocManageService().updateApplyStatusByJobPks(RMApplyStatusEnum.APPLY.toIntValue(), RMApplyStatusEnum.HIRE.toIntValue(), new String[]{item.getPk_job()});
		else
			getPsnLibManageService().backStatusFromHire(item.getPk_org(), item.getPk_psndoc(), item.getPk_job());
	}
	/**
	 * 保存校验
	 * 1.证件类型和证件号码是否为空
	 * 2.黑名单校验
	 * 3.已入职人员校验
	 * 4.是否为再聘人员
	 * 5.是否有相同应聘人员信息
	 * @param aggvo
	 * @throws BusinessException
	 */
	public String saveValidate(AggHireVO aggvo) throws BusinessException{
		if(aggvo == null)
			return null;
		//查询应聘人员信息
		AggRMPsndocVO[] aggRMPsndocVOs = NCLocator.getInstance().lookup(
				IHireQueryService.class).getAggRMPsndocVOsFromAggHireVO(aggvo);
		if(ArrayUtils.isEmpty(aggRMPsndocVOs))
			return null;
		//校验证件类型和证件号码是否为空
		Map<String, String> checkMap = null;
		checkMap = NCLocator.getInstance().lookup(
				IHireQueryService.class).checkIdinfo(aggRMPsndocVOs);
		if(!MapUtils.isEmpty(checkMap))
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6021jobrule","060210jobrule0066")
					/*@res "录用人员：{0} 证件信息为空，不允许录用。"*/, contactErrNameMsg(checkMap)));
		//校验黑名单人员
		checkMap = NCLocator.getInstance().lookup(
				IHireQueryService.class).checkBlackList(aggRMPsndocVOs);
		if(!MapUtils.isEmpty(checkMap))
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6021jobrule","060210jobrule0067")
					/*@res "录用人员：{0} 为黑名单人员，不允许录用。"*/, contactErrNameMsg(checkMap)));
		//校验已身故人员
		checkMap = NCLocator.getInstance().lookup(
				IHireQueryService.class).checkDiePsninfo(aggRMPsndocVOs);
		if(!MapUtils.isEmpty(checkMap))
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6021jobrule","060210jobrule0068")
					/*@res "录用人员：{0} 为已身故人员，不允许录用。"*/, contactErrNameMsg(checkMap)));
		//校验在职人员
		checkMap = NCLocator.getInstance().lookup(
				IHireQueryService.class).checkIsInJob(aggRMPsndocVOs);
		if(!MapUtils.isEmpty(checkMap))
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6021jobrule","060210jobrule0069")
					/*@res "录用人员：{0} 为在职人员，不允许录用。"*/, contactErrNameMsg(checkMap)));
		//再聘人员处理
		NCLocator.getInstance().lookup(IHireQueryService.class).reApplySetting(aggRMPsndocVOs);
		//校验同证件类型和证件号码人员
		checkMap = checkSameIdinfo(aggRMPsndocVOs);
		if(!MapUtils.isEmpty(checkMap)){
			//			return MessageFormat.format("录用人员：{0} 有相同证件信息的应聘人员，不允许录用。", contactErrNameMsg(checkMap));
			//若有相同证件信息的应聘人员不允许录用
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6021jobrule","060210jobrule0070")
					/*@res "录用人员：{0} 有相同证件信息的应聘人员，不允许录用。"*/, contactErrNameMsg(checkMap)));
		}
		return null;
	}

	/**
	 * 维护证件类型和证件号码后，校验是否已有相同证件类型和证件号码人员
	 * @param aggRMPsndocVOs
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String> checkSameIdinfo(AggRMPsndocVO[] aggRMPsndocVOs)
			throws BusinessException {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (aggRMPsndocVOs == null)
			return null;
		for (int i = 0; i < aggRMPsndocVOs.length; i++) {
			RMPsndocVO psndocVO1 = aggRMPsndocVOs[i].getPsndocVO();
			if (psndocVO1 == null)
				continue;
			String name1 = psndocVO1.getName();
			String id1 = psndocVO1.getId();
			String idType1 = psndocVO1.getIdtype();
			for (int j = i + 1; j < aggRMPsndocVOs.length; j++) {
				RMPsndocVO psndocVO2 = aggRMPsndocVOs[j].getPsndocVO();
				if (psndocVO2 == null) {
					continue;
				}
				String name2 = psndocVO2.getName();
				String id2 = psndocVO2.getId();
				String idType2 = psndocVO2.getIdtype();
				if (name1.equals(name2) && id1.equals(id2)
						&& idType1.equals(idType2)) {
					resultMap.put(id1, name1);
				}
			}
		}
		return resultMap;
	}
	/**
	 * 拼接姓名字符串
	 * @param map
	 * @return
	 */
	private String contactErrNameMsg(Map<String,String> map) {
		if(MapUtils.isEmpty(map))
			return null;
		StringBuffer sb = new StringBuffer();
		Set<String> keySet = map.keySet();
		for(String key : keySet){
			sb.append(map.get(key)+",");
		}
		//去掉最后一个“,”
		return StringUtils.isEmpty(sb.toString())?null:StringUtils.substring(sb.toString(), 0, sb.toString().length()-1);
	}
}