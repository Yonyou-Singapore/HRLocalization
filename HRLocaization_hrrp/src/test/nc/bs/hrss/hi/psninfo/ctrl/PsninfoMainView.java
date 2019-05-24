package nc.bs.hrss.hi.psninfo.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.hrss.hi.psninfo.AlterationParse;
import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.advpanel.cata.CatagoryPanel;
import nc.bs.hrss.pub.advpanel.cata.CatagorySelectorCtrl;
import nc.bs.hrss.pub.cmd.LineDownCmd;
import nc.bs.hrss.pub.cmd.LineUpCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hrss.hi.setalter.ISetalterService;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
import nc.uap.lfw.core.cache.LfwCacheManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.event.TextEvent;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.jsp.uimeta.UIControl;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.EduVO;
import nc.vo.hi.psndoc.NationDutyVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TitleVO;
import nc.vo.hr.validator.HKIDCardValidator;
import nc.vo.hr.validator.IDCardValidator;
import nc.vo.hr.validator.IFieldValidator;
import nc.vo.hr.validator.ValidateWithLevelException;
import nc.vo.hrss.hi.psninfo.AlterationVO;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.hrss.hi.setalter.SetConsts;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class PsninfoMainView implements IController {

	/**
	 * 保存卡片子集
	 */
	public void save(MouseEvent<MenuItem> mouseEvent) {
		SuperVO currVO = getCurrSuperVO();
		String pk_psndoc = (String) currVO
				.getAttributeValue(HrssSetalterVO.PK_PSNDOC);
		//证件类型和证件号码不可编辑，所以这个地方的校验去掉
//		// 校验身份证是否合法
//		if (PsninfoConsts.PSNDOC_DS_ID.equals(getCurrDataset())) {
//			if (!validateId((PsndocVO) currVO))
//				return;
//		}
		try {
			// 从session中取到人员信息中需要审核的字段的list
			@SuppressWarnings("unchecked")
			ArrayList<String> checkFiledList = (ArrayList<String>) AppUtil.getAppAttr("checkFiledList");
			// 变化的字段
			ArrayList<String> changeFiledList = new ArrayList<String>();
			// 需要更新的字段
			ArrayList<String> needUpdateFiledList = new ArrayList<String>();
			IUAPQueryBS uapQry = null;
			boolean isNeedAudit = false;
			try {
				String pk = currVO.getPrimaryKey();
				uapQry = ServiceLocator.lookup(IUAPQueryBS.class);
				SuperVO oldVO = (SuperVO) uapQry.retrieveByPK(currVO.getClass(), pk);
				findChangeFiledList(currVO, changeFiledList,oldVO);
			
				for(String filed : changeFiledList){
					if(!CollectionUtils.isEmpty(checkFiledList) && checkFiledList.contains(filed)){
						isNeedAudit = true;
					} else {
						// 把变化的字段并且是不需要审核的字段的值设置到人员信息表中对应的Vo
						oldVO.setAttributeValue(filed, currVO.getAttributeValue(filed));
						needUpdateFiledList.add(filed);
					}
				}
				if(!CollectionUtils.isEmpty(needUpdateFiledList)){
					// 把需要更新的字段插入到数据库中
					ServiceLocator.lookup(IPersistenceUpdate.class).updateVO(null,
							oldVO, needUpdateFiledList.toArray(new String[0]), null);
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			
			if(isNeedAudit){
				 // 需审核时
				HrssSetalterVO alterVO = getNoSubmitHrssSetalterVO();
				ISetalterService service = ServiceLocator
						.lookup(ISetalterService.class);
				String pk_infoset = PsninfoUtil
						.getInfosetPKByCode(getCurrDataset());
				String xml = PsninfoUtil.updSuperVOToXML(currVO);
				if (xml == null) {
				} else if (alterVO == null) {
					alterVO = getNewHrssSetalterVO(pk_psndoc, pk_infoset, xml);
					service.insertVO(alterVO);
				} else {
					alterVO.setAlter_context(xml);
					alterVO.setAlter_date(new UFDate());
					service.updateVO(alterVO);
				}
			
			}
			// 人员信息中的证件类型和证件号码不允许修改，所有不需要同步身份证信息
//			if (!isNeedAudit) { // 不需审核时
//				// 同步身份证信息
//				PsninfoUtil.updPsndocCert((PsndocVO) currVO);
//			} else { // 需审核时
//				HrssSetalterVO alterVO = getNoSubmitHrssSetalterVO();
//				ISetalterService service = ServiceLocator
//						.lookup(ISetalterService.class);
//				String pk_infoset = PsninfoUtil
//						.getInfosetPKByCode(getCurrDataset());
//				String xml = PsninfoUtil.updSuperVOToXML(currVO);
//				if (xml == null) {
//				} else if (alterVO == null) {
//					alterVO = getNewHrssSetalterVO(pk_psndoc, pk_infoset, xml);
//					service.insertVO(alterVO);
//				} else {
//					alterVO.setAlter_context(xml);
//					alterVO.setAlter_date(new UFDate());
//					service.updateVO(alterVO);
//				}
//			}


			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset());
			CommonUtil
					.showShortMessage(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_pe-res",
									"0c_pe-res0093")/* @res "保存成功！" */);
		} catch (BusinessException e1) {
			new HrssException(e1).deal();
		} catch (HrssException e1) {
			e1.alert();
		} catch (Exception e1) {
			new HrssException(e1).deal();
		}
	}

	private List<String> getAttrsNoPhoto(SuperVO currVO) {
		List<String> list = new ArrayList<String>();
		String[] attrs = currVO.getAttributeNames();
		for (String attr : attrs) {
			if (PsndocVO.PHOTO.equals(attr)
					|| PsndocVO.PREVIEWPHOTO.equals(attr)) {
				continue;
			}
			list.add(attr);
		}
		return list;
	}

	/**
	 * 校验身份证是否合法
	 * 
	 * @param value
	 * @return
	 * @throws ValidateWithLevelException
	 * @throws BusinessException
	 */
	private boolean validateId(PsndocVO psndocVO) {
		String id = psndocVO.getId();
		String idtype = psndocVO.getIdtype();
		
		String idtype_cn = "1001Z01000000000AI36";
		String idtype_hk = "1001Z01000000000CHUK";

		if (StringUtils.isEmpty(idtype) || idtype_cn.equals(idtype) || idtype_hk.equals(idtype)) {
			// 不是身份证或香港身份证不校验
			return true;
		}

		IFieldValidator v = null;
		if (idtype_cn.equals(idtype)) {
			v = new IDCardValidator();
		} else {
			v = new HKIDCardValidator();
		}

		try {
			v.validate(id);
		} catch (ValidateWithLevelException ex) {
			String msg = ex.getMessage()
					+ ", "
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"c_hi-res", "0c_trn-res0052")/*
														 * @ res "确认继续吗?"
														 */;
			return CommonUtil.showConfirmDialog(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"c_hi-res", "0c_hi-res0019")/*
														 * @ res "确认继续"
														 */, msg);
			// AppInteractionUtil.showConfirmDialog("validateid",
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			// "c_hi-res", "0c_hi-res0019")/*
			// * @ res "确认继续"
			// */, msg, null);
			// return
			// AppInteractionUtil.getConfirmDialogResult("validateid").booleanValue();
		}

		return true;
	}

	private SuperVO getCurrSuperVO() {
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset masterDs = widget.getViewModels().getDataset(getCurrDataset());
		new DefaultDataValidator().validate(masterDs, new LfwView());
		SuperVO[] superVOs = ser.serialize(masterDs);
		return superVOs[0];
	}

	private String getCurrDataset() {
		return PsninfoUtil.getCurrDataset();
	}

	private HrssSetalterVO getNewHrssSetalterVO(String pk_psndoc,
			String pk_infoset, String xml) {
		SessionBean session = SessionUtil.getSessionBean();
		HrssSetalterVO vo = new HrssSetalterVO();
		vo.setPk_psndoc(pk_psndoc);
		vo.setPk_infoset(pk_infoset);
		vo.setData_status(PsninfoConsts.STATUS_NOSUMIT);
		vo.setAlter_context(xml);
		vo.setPk_operator(session.getUserVO().getCuserid());
		vo.setAlter_date(new UFDate());
		vo.setPk_group(SessionUtil.getPk_group());
		vo.setPk_org(SessionUtil.getPsndocVO().getPsnJobVO().getPk_hrorg());
		vo.setPk_psnjob(session.getPsnjobVO().getPk_psnjob());
		vo.setPk_dept(session.getPk_dept());
		vo.setConfirm_flag(UFBoolean.FALSE);
		return vo;
	}

	public void CancelLisn(MouseEvent<MenuItem> mouseEvent) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID,
				CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	public void pluginReSearch(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID,
				CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	/**
	 * 还原
	 * 
	 * @param mouseEvent
	 */
	public void RevertLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psndoc = session.getPsndocVO().getPk_psndoc();
		try {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(session
					.getPsndocVO().getPk_psndoc(), pk_infoset);
			if (vo == null) {
				new HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("c_hi-res", "0c_hi-res0003")/*
																 * @ res
																 * "不存在待提交记录!"
																 */).alert();
			}
			service.deleteVO(vo);
			SuperVO[] superVOs = queryVOs(pk_psndoc, getCurrDataset());
			LfwView widget = AppLifeCycleContext.current().getViewContext()
					.getView();
			Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
			new SuperVO2DatasetSerializer().serialize(superVOs, ds,
					Row.STATE_NORMAL);
			if (!ArrayUtils.isEmpty(superVOs))
				ds.setRowSelectIndex(0);
			if (PsninfoConsts.INFOSET_CODE_PSNDOC.equals(getCurrDataset())) {
				ds.setValue("patha", "pt/psnImage/download?pk_psndoc="
						+ pk_psndoc + "&random=" + Math.random());
			}
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset()); // // 设置提示信息
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 提交
	 * 
	 * @param mouseEvent
	 */
	public void CommitLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psndoc = session.getPsndocVO().getPk_psndoc();
		try {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(pk_psndoc,
					pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0020")/*
															 * @ res "提交失败"
															 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0004")/*
															 * @ res "该记录不能提交"
															 */);
				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0004")/*
				// * @
				// * res
				// * "该记录不能提交"
				// */)
				// .alert();
			}
			vo.setAttributeValue(HrssSetalterVO.DATA_STATUS, SetConsts.NOAUDIT);
			vo.setAlter_date(new UFDate());
			service.commitVO(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 收回
	 * 
	 * @param mouseEvent
	 */
	public void CallbackLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psndoc = session.getPsndocVO().getPk_psndoc();
		try {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			HrssSetalterVO vo = service.queryNoAuditHrssSetalterVO(pk_psndoc,
					pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0021")/*
															 * @ res "提交失败"
															 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0005")/*
															 * @ res
															 * "该记录已修改，请刷新后操作！"
															 */);

				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0005")/*
				// * @
				// * res
				// * "该记录已修改，请刷新后操作！"
				// */)
				// .alert();
			}
			vo.setAttributeValue(HrssSetalterVO.DATA_STATUS, SetConsts.NOSUBMIT);
			vo.setAlter_date(new UFDate());
			service.commitVO(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 继续修改
	 * 
	 * @param mouseEvent
	 */
	public void GoonUpdateLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psndoc = session.getPsndocVO().getPk_psndoc();
		try {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			HrssSetalterVO vo = service.queryNoSubOrAudOrConfirmHrssSetalterVO(
					session.getPsndocVO().getPk_psndoc(), pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0022")/*
															 * @ res "继续修改失败"
															 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0005")/*
															 * @ res
															 * "该记录已修改，请刷新后操作！"
															 */);
				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0005")/*
				// * @
				// * res
				// * "该记录已修改，请刷新后操作！"
				// */)
				// .alert();
			}
			service.goonUpdate(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 放弃修改
	 * 
	 * @param mouseEvent
	 */
	public void NoUpdateLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psndoc = session.getPsndocVO().getPk_psndoc();
		try {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			HrssSetalterVO vo = service.queryNoSubOrAudOrConfirmHrssSetalterVO(
					pk_psndoc, pk_infoset);
			if (vo == null) {

				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0023")/*
															 * @ res "放弃修改失败"
															 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_hi-res", "0c_hi-res0005")/*
															 * @ res
															 * "该记录已修改，请刷新后操作！"
															 */);
				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0005")/*
				// * @
				// * res
				// * "该记录已修改，请刷新后操作！"
				// */)
				// .alert();
			}
			service.confirmAudit(vo);
			SuperVO[] superVOs = queryVOs(pk_psndoc, getCurrDataset());
			LfwView widget = AppLifeCycleContext.current().getViewContext()
					.getView();
			Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
			new SuperVO2DatasetSerializer().serialize(superVOs, ds,
					Row.STATE_NORMAL);
			if (!ArrayUtils.isEmpty(superVOs))
				ds.setRowSelectIndex(0);
			if (PsninfoConsts.INFOSET_CODE_PSNDOC.equals(getCurrDataset())) {
				ds.setValue("patha", "pt/psnImage/download?pk_psndoc="
						+ pk_psndoc + "&random=" + Math.random());
			}
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			PsninfoUtil.SetCompState(getCurrDataset());
		} catch (BusinessException e1) {
			new HrssException(e1).deal();
		} catch (HrssException e1) {
			e1.alert();
		}
	}

	public void AddLineLisn(MouseEvent<MenuItem> mouseEvent) {
		ApplicationContext applicationContext = AppLifeCycleContext.current()
				.getApplicationContext();
		applicationContext.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS,
				HrssConsts.POPVIEW_OPERATE_ADD);
		String dataset = getCurrDataset();
		LfwView widget = AppLifeCycleContext.current().getWindowContext()
				.getViewContext(HrssConsts.PAGE_MAIN_WIDGET).getView();
		Dataset ds = widget.getViewModels().getDataset(dataset);
		DialogSize size = PsninfoUtil.getDatasetWidth(dataset);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(PsninfoConsts.CURR_DATASET, ds.getVoMeta());
		paramMap.put("nodecode", PsninfoConsts.PSNINFO_NODECODE);
		String title = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				"0c_hi-res0006")/* @res "详细信息" */;
		CommonUtil.showWindowDialog("PsnInfoDetail", title,
				String.valueOf(size.getWidth()),
				String.valueOf(size.getHeight()), paramMap,
				ApplicationContext.TYPE_DIALOG, false, false);
	}

	public void DeleteLineLisn(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
		RowData rd = ds.getCurrentRowData();
		Row[] rows = rd.getRows();
		if (ArrayUtils.isEmpty(rows))
			return;
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		Row delRow = ds.getCurrentRowSet().getCurrentRowData().getSelectedRow();
		int index = ds.getCurrentRowSet().getCurrentRowData()
				.getRowIndex(delRow);
		SuperVO[] delVOs = ser.serialize(ds, delRow);
		SuperVO superVO = delVOs[0];
		try {
			boolean isNeedAudit = PsninfoUtil.isNeedAudit(getCurrDataset());
			if (!isNeedAudit) {
				ServiceLocator.lookup(IVOPersistence.class).deleteVO(superVO);
				if (EduVO.getDefaultTableName().equals(superVO.getTableName())) {
					// 学历信息
					UFBoolean lasteducation = (UFBoolean) (superVO
							.getAttributeValue("lasteducation"));
					if (lasteducation.booleanValue()) {
						// 当删除的学历信息是最高学历时，需要把个人信息中的学历和学位清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setEdu(null);
						psnVO.setPk_degree(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(
								psnVO);
					}

				}

				if (NationDutyVO.getDefaultTableName().equals(
						superVO.getTableName())) {
					// 职业资格
					UFBoolean istop = (UFBoolean) (superVO
							.getAttributeValue("istop"));
					if (istop.booleanValue()) {
						// 当删除的职业资格是最高时，需要把个人信息中的职业资格清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setProf(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(
								psnVO);
					}
				}

				if (TitleVO.getDefaultTableName()
						.equals(superVO.getTableName())) {
					// 职称信息
					UFBoolean tiptop_flag = (UFBoolean) (superVO
							.getAttributeValue("tiptop_flag"));
					if (tiptop_flag.booleanValue()) {
						// 当删除的职称信息是最高时，需要把个人信息中的专业技术职务清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setTitletechpost(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(
								psnVO);
					}
				}
			} else {
				delAuditInfo(getCurrDataset(), index, superVO);
			}
		} catch (BusinessException e) {
			new HrssException(e).alert();
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (Exception e) {
			new HrssException(e).alert();
		}
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID,
				CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	/**
	 * 需审核时删除处理
	 * 
	 * @param superVO
	 * @param dataset
	 * @throws HrssException
	 * @throws Exception
	 */
	private void delAuditInfo(String dataset, int row, SuperVO superVO)
			throws HrssException, Exception {
		String pk_psndoc = PsninfoUtil.getPsndocPK();
		ISetalterService service = ServiceLocator
				.lookup(ISetalterService.class);
		String pk_infoset = PsninfoUtil.getInfosetPKByCode(dataset);
		// 查询未提交的修改记录
		HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(pk_psndoc,
				pk_infoset);
		AlterationVO alterVO = null;
		if (vo != null) {
			alterVO = AlterationParse.parseXML(vo.getAlter_context());
		}
		SuperVO[] allSuperVOs = PsninfoUtil.querySubSet(dataset);
		allSuperVOs[row].setStatus(Row.STATE_DELETED);
		AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(allSuperVOs,
				alterVO);
		String xml = AlterationParse.generateXML(afterVO);
		if (vo == null) { // 不存在未提交记录时新增
			vo = getNewHrssSetalterVO(pk_psndoc, pk_infoset, xml);
			service.insertVO(vo);
		} else { // 存在未提交记录时修改
			vo.setAlter_date(new UFDate());
			vo.setAlter_context(xml);
			service.updateVO(vo);
		}
	}

	public void PsndocDSLoad(DataLoadEvent dataLoadEvent) {
		String execScript = "pageUI.getWidget('main').getTab('tag2905').hideTabHead();";
		AppLifeCycleContext.current().getApplicationContext()
				.addExecScript(execScript);
		WindowContext windowContext = AppLifeCycleContext.current()
				.getWindowContext();
		windowContext.addAppAttribute(PsninfoConsts.CURR_DATASET,
				PsninfoConsts.INFOSET_CODE_PSNDOC);
		windowContext.addAppAttribute(PsninfoConsts.CURR_COMP_ID,
				PsninfoConsts.PSNDOC_FORM_ID);
		// 清空缓存的数据集需审核信息
		windowContext.addAppAttribute(PsninfoConsts.HRSSSETS, null);
		String pk_psndoc = PsninfoUtil.getPsndocPK();
		try {
			PsninfoUtil.querySetVOs(pk_psndoc);
			Dataset dataset = AppLifeCycleContext.current().getWindowContext()
					.getViewContext(CatagoryPanel.WIDGET_ID).getView()
					.getViewModels()
					.getDataset(CatagorySelectorCtrl.CID_DS_CATAGORY);
			if (dataset.getCurrentRowData() != null
					&& !ArrayUtils.isEmpty(dataset.getCurrentRowData()
							.getRows())) {
				dataset.setRowSelectIndex(0);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			e.alert();
		}
	}

	/**
	 * 根据点击的左侧子集切换右侧页签
	 * 
	 * @param keys
	 */
	public void pluginmainIn(Map<String, Object> keys) {
		TranslatedRow transRow = (TranslatedRow) keys.get("inTabId");
		String tabId = (String) transRow.getValue("param");
		ViewContext viewContext = AppLifeCycleContext.current()
				.getWindowContext().getCurrentViewContext();
		WindowContext windowContext = AppLifeCycleContext.current()
				.getWindowContext();
		UIMeta uiMeta = (UIMeta) viewContext.getUIMeta();
//		MenubarComp menubar = viewContext.getView().getViewMenus()
//				.getMenuBar("mymenu");
		UITabComp tabComp = (UITabComp) uiMeta
				.findChildById(PsninfoConsts.TABLAYOUT_MAIN_ID);
		List<UILayoutPanel> itemList = tabComp.getPanelList();
		String pk_psndoc = PsninfoUtil.getPsndocPK();
		try {
			for (int i = 0; i < itemList.size(); i++) {
				UITabItem tab = (UITabItem) itemList.get(i);
				if (!tab.getId().equals(tabId)) {
					tab.setVisible(false);
					continue;
				}
				String datasetId = "";
				String componentId = null;
				if (tab.getElement() instanceof UIControl) {
					componentId = (String) tab.getElement().getAttribute("id");
					windowContext.addAppAttribute(PsninfoConsts.CURR_COMP_ID,
							componentId);
					WebComponent comp = viewContext.getView()
							.getViewComponents().getComponent(componentId);
					if (comp != null) {
						if (comp instanceof FormComp) {
							datasetId = ((FormComp) comp).getDataset();
							// 解决切换页签，界面混乱添加
							((FormComp) comp).setFocus(true);
						} else if (comp instanceof GridComp) {
							WebComponent compForm = viewContext.getView()
									.getViewComponents()
									.getComponent("psnjobForm");
							if (compForm == null) {
								compForm = viewContext.getView()
										.getViewComponents()
										.getComponent("psninfoForm");
							}
							if (compForm != null) {
								((FormComp) compForm).setFocus(false);
							}
							datasetId = ((GridComp) comp).getDataset();
							LfwCacheManager.getSessionCache().put(pk_psndoc,
									new ArrayList<SuperVO>());
						}
					}
				} else {
					datasetId = PsninfoConsts.INFOSET_CODE_RELATION;
					WebComponent compForm = viewContext.getView()
							.getViewComponents().getComponent("psnjobForm");
					if (compForm == null) {
						compForm = viewContext.getView().getViewComponents()
								.getComponent("psninfoForm");
					}
					if (compForm != null) {
						((FormComp) compForm).setFocus(false);
					}
				}
//				// 动态给【上移】【下移】【(上移、下移的)保存】菜单添加提交规则
//				addSubmitRuleForMenuItem(datasetId, menubar);

				// 设置当前页面可见
				tab.setVisible(true);
				windowContext.addAppAttribute(PsninfoConsts.CURR_DATASET,
						datasetId);

				// 查询当前数据集的数据
				SuperVO[] superVOs = queryVOs(pk_psndoc, datasetId);

				// 把数据进行序列化
				PsninfoUtil.pluginDSLoad(datasetId, superVOs);

				// 设置当前选中的页签
				tabComp.setCurrentItem(Integer.toString(i));
				break;
			}
			// 设置页面组件状态
			PsninfoUtil.SetCompState(getCurrDataset());
		} catch (LfwBusinessException e) {
			new HrssException(e).alert();
		}
	}

//	/**
//	 * 给【上移】、【下移】、【保存】添加提交规则
//	 * 
//	 * @param datasetId
//	 * @param menubar
//	 */
//	private void addSubmitRuleForMenuItem(String datasetId, MenubarComp menubar) {
//		MenuItem[] menuItems = new MenuItem[3];
//		menuItems[0] = menubar.getItem("remove_up");
//		menuItems[1] = menubar.getItem("remove_down");
//		menuItems[2] = menubar.getItem("remove_save");
//
//		for (MenuItem menuItem : menuItems) {
//			List<EventConf> eventConfList = menuItem.getEventConfList();
//			// 提交规则
//			EventSubmitRule sr = new EventSubmitRule();
//			ViewRule wr = new ViewRule();
//			wr.setId(ViewUtil.getCurrentView().getId());
//			DatasetRule dsr = new DatasetRule();
//			dsr.setId(datasetId);
//			dsr.setType(DatasetRule.TYPE_ALL_LINE);
//			wr.addDsRule(dsr);
//
//			EventConf itemEvent = eventConfList.get(0);
//
//			sr.addViewRule(wr);
//			itemEvent.setSubmitRule(sr);
//		}
//
//	}

	private SuperVO[] queryVOs(String pk_psndoc, String dataset)
			throws LfwBusinessException {
		AppLifeCycleContext.current().getApplicationContext()
				.getClientSession().setAttribute("dataset", dataset);
		try {
			PsndocAggVO aggVO = ServiceLocator.lookup(IPsndocQryService.class)
					.queryPsndocVOByPk(pk_psndoc);
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNDOC)
					|| dataset.equals(PsninfoConsts.INFOSET_CODE_PSNJOB_CURR)) {
				boolean isNeedAudit = PsninfoUtil.isNeedAudit(dataset);
				if (isNeedAudit) {
					PsninfoUtil.querySetVOs(pk_psndoc);
				}
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNDOC)) {
				return new SuperVO[] { aggVO.getParentVO() };
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNJOB_CURR)) {
				return new SuperVO[] { aggVO.getParentVO().getPsnJobVO() };
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PARTTIME)) {
				// 当由部门人员信息节点进入我的档案节点，并且该工作记录不是主职时
				SessionBean session = SessionUtil.getSessionBean();
				String ismainjob = (String) session
						.getExtendAttributeValue("ismainjob");
				if (!StringUtils.isEmpty(ismainjob)
						&& (ismainjob.equals("N") || (ismainjob.equals("n")))) {
					return PsninfoUtil.queryParttime(pk_psndoc);
				}

			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_RELATION)) {
				return PsninfoUtil.queryRelationVOs(pk_psndoc);
			}
			return PsninfoUtil.querySubSet(dataset);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return null;
	}

	/**
	 * 查询待提交记录
	 * 
	 * @param session
	 * @return
	 * @throws BusinessException
	 * @throws HrssException
	 */
	private HrssSetalterVO getNoSubmitHrssSetalterVO()
			throws BusinessException, HrssException {
		HrssSetalterVO alterVO = null;
		boolean isNeedAudit = PsninfoUtil.isNeedAudit(getCurrDataset());
		if (isNeedAudit) {
			ISetalterService service = ServiceLocator
					.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil
					.getInfosetPKByCode(getCurrDataset());
			SessionBean session = SessionUtil.getSessionBean();
			alterVO = service.queryNoSubmitHrssSetalterVO(session.getPsndocVO()
					.getPk_psndoc(), pk_infoset);
		}
		return alterVO;
	}

	public void selValueChange(TextEvent textEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = widget.getViewModels().getDataset(
				PsninfoConsts.INFOSET_CODE_RELATION);
		String pk_relation = textEvent.getSource().getValue();
		String pk_psndoc = PsninfoUtil.getPsndocPK();
		SuperVO[] vos = PsninfoUtil.getRelation(pk_relation, pk_psndoc);
		new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
	}

	/**
	 * 查看详细操作
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		LfwView widget = AppLifeCycleContext.current().getWindowContext()
				.getViewContext(HrssConsts.PAGE_MAIN_WIDGET).getView();
		String dataset = getCurrDataset();
		ApplicationContext applicationContext = AppLifeCycleContext.current()
				.getApplicationContext();
		applicationContext.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS,
				HrssConsts.POPVIEW_OPERATE_VIEW);
		Dataset ds = widget.getViewModels().getDataset(dataset);
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		SuperVO superVO = ser.serialize(ds, ds.getSelectedRow())[0];
		if (ds.getVoMeta() != null
				&& CtrtVO.class.getName().equals(ds.getVoMeta())) {
			// 表明当前数据集是合同信息，需要做特殊处理，需要把业务类型放在session中
			SessionUtil.setAttribute(CtrtVO.CONTTYPE,
					((CtrtVO) superVO).getConttype());
		}
		String primaryKey = AppLifeCycleContext.current().getParameter(
				"primaryKey");
		String rowIndex = AppLifeCycleContext.current()
				.getParameter("rowIndex");
		applicationContext.addAppAttribute("primaryKey", primaryKey);
		applicationContext.addAppAttribute("rowIndex", rowIndex);
		applicationContext.addAppAttribute(PsninfoConsts.SUPERVO_DETAIL,
				superVO);
		DialogSize size = PsninfoUtil.getDatasetWidth(dataset);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(PsninfoConsts.CURR_DATASET, ds.getVoMeta());
		paramMap.put("nodecode", PsninfoConsts.PSNINFO_NODECODE);
		String title = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				"0c_hi-res0006")/* @res "详细信息" */;
		CommonUtil.showWindowDialog("PsnInfoDetail", title,
				String.valueOf(size.getWidth()),
				String.valueOf(size.getHeight()), paramMap,
				ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 【上移】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeUp(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil
					.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_hi-res",
									"0c_hi-res0027")/*
													 * @res
													 * "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
													 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
		if (ds.getSelectedIndex() == null) {
			return;
		}
		int index = ds.getSelectedIndex();

		// 此为第一行数据，不可进行上移操作！
		if (index <= 0) {
			return;
		}
		CmdInvoker.invoke(new LineUpCmd(ds.getId(), null));
	}

	/**
	 * 判断当前数据集是否存在未生效的数据
	 */
	@SuppressWarnings("unchecked")
	private boolean isUnOperativeRecord() {
		try {
			String pk_infoset;
			pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDataset());
			// 查询待提交/待审核/审核不通过且用户未确认记录
			HashMap<String, HrssSetalterVO> setsVOs = new HashMap<String, HrssSetalterVO>();
			if (AppLifeCycleContext.current().getWindowContext()
					.getAppAttribute(PsninfoConsts.SET_ALTER_MAP) != null) {
				setsVOs = (HashMap<String, HrssSetalterVO>) AppLifeCycleContext
						.current().getWindowContext()
						.getAppAttribute(PsninfoConsts.SET_ALTER_MAP);
			}
			HrssSetalterVO alterVO = setsVOs.get(pk_infoset);
			if (alterVO != null) {
				return true;
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		return false;
	}

	/**
	 * 【下移】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeDown(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil
					.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_hi-res",
									"0c_hi-res0027")/*
													 * @res
													 * "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
													 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
		if (ds.getSelectedIndex() == null) {
			return;
		}
		int index = ds.getSelectedIndex();
		// 在这里判断下标，是因为子表默认选中-1，如果没有数据，需要根据Index来判断
		if (index < 0) {
			return;
		}
		int maxIndex = ds.getCurrentRowData().getRows().length;
		// 此为最后一行数据，不可进行下移操作！
		if (index == maxIndex - 1) {
			return;
		}
		CmdInvoker.invoke(new LineDownCmd(ds.getId(), null));
	}

	/**
	 * 【列表的保存】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeSave(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil
					.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_hi-res",
									"0c_hi-res0027")/*
													 * @res
													 * "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
													 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDataset());
		Row[] row = ds.getAllRow();
		if (row == null || row.length < 1) {
			return;
		}
		// 为每行数据的recordnum赋值
		for (int i = 0; i < row.length; i++) {
			if(ds.nameToIndex("recordnum") != -1)
				row[i].setValue(ds.nameToIndex("recordnum"), i);
		}

		// 把表格中的数据转换成SuperVO
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		SuperVO[] superVOs = ser.serialize(ds);

		try {
			// 调用接口保存数据
			ServiceLocator.lookup(IVOPersistence.class).updateVOArray(superVOs);
			CommonUtil
					.showShortMessage(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_pe-res",
									"0c_pe-res0093")/* @res "保存成功！" */);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

	}
	
	/**
	 * 通过对比找出变化的字段
	 * @param currVO
	 * @param changeFiledList
	 * @param oldVO 
	 */
	private void findChangeFiledList(SuperVO vo,
			ArrayList<String> changeFiledList, SuperVO oldVO) {
			String[] attrs = vo.getAttributeNames();
			for (String attr : attrs) {
				if ("ts".equals(attr) || PsndocVO.PHOTO.equals(attr)
						|| PsndocVO.PREVIEWPHOTO.equals(attr)
						|| PsndocVO.CREATIONTIME.equals(attr)
						|| PsndocVO.MODIFIEDTIME.equals(attr)) {
					continue;
				}
				Object objValue = vo.getAttributeValue(attr);
				Object oldObjValue = oldVO.getAttributeValue(attr);
				if ((objValue == null || StringUtils.isEmpty(objValue.toString()))
						&& (oldObjValue == null || StringUtils.isEmpty(oldObjValue
								.toString()))) {
					continue;
				}
				if (!((objValue == null && oldObjValue == null) || (objValue != null && oldObjValue != null && objValue.equals(oldObjValue)))) {
					changeFiledList.add(attr);
					continue;
				}

				if ((objValue != null && objValue.equals(oldObjValue)) || (oldObjValue != null && oldObjValue.equals(objValue))) {
					continue;
				}
				changeFiledList.add(attr);
			}
		
	}
	
	/**
	 * 
	 * @param mouseEvent
	 */
	public void pcbDownload(MouseEvent<MenuItem> mouseEvent) {
		SuperVO currVO = getCurrSuperVO();
		String pk_psndoc = (String) currVO
				.getAttributeValue(HrssSetalterVO.PK_PSNDOC);
	}
	
	
	public void eaformDownload(MouseEvent<MenuItem> mouseEvent) {
		SuperVO currVO = getCurrSuperVO();
		String pk_psndoc = (String) currVO
				.getAttributeValue(HrssSetalterVO.PK_PSNDOC);
	}
	
	
	
	
	
	
	
	
	
	
	
}