package nc.ui.wa.datainterface.view;

import java.awt.Container;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.sec.esapi.NCESAPI;
import nc.hr.utils.ResHelper;
import nc.itf.hr.datainterface.IDataIOManageService;
import nc.itf.hr.wa.IWaClass;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.CaretposEnum;
import nc.vo.hr.datainterface.DataFromEnum;
import nc.vo.hr.datainterface.DataIOItemVO;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FileTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItfTypeEnum;
import nc.vo.hr.datainterface.LineTopEnum;
import nc.vo.hr.datainterface.LineTopPositionEnum;
import nc.vo.hr.datainterface.RelatedItemEnum;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: xuanlt
 * @date: 2010-1-5 下午03:07:35
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class DataIOWizard
{

	private DataIOAppModel appModel = null;
	private AggHrIntfaceVO aggVO = null;
	private Container parent;

	public DataIOWizard(Container parent, DataIOAppModel appModel, AggHrIntfaceVO aggVO)
	{
		this.parent = parent;
		this.appModel = appModel;
		this.aggVO = aggVO;
	}

	@SuppressWarnings("restriction")
	public WizardDialog createWizardDialog()
	{

		List<WizardStep> steps = getSteps();
		WizardDialog wizDlg = new WizardDialog(getParent(), steps, null);
		wizDlg.setWizardDialogListener(new IWizardDialogListener()
		{
			@Override
			public void wizardFinish(WizardEvent event) throws WizardActionException
			{
				try
				{
					// 保存
					List<WizardStep> steps = event.getModel().getSteps();
					// BaseInfPanel basePanel =
					// (BaseInfPanel)steps.get(0).getComp();
					ParaInfPanel paraPanel = (ParaInfPanel) steps.get(0).getComp();
					IOItemsPanel ioItemsPanel = null;
					if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))
						ioItemsPanel= (IOItemsPanel) steps.get(2).getComp();
					else
						ioItemsPanel= (IOItemsPanel) steps.get(1).getComp();
					aggVO = syncAggVO(aggVO, paraPanel);
					FormatItemVO[] itemVOs = (FormatItemVO[]) ioItemsPanel.getFormatsVO();
					//20150925 shenliangc 数据接口导出文本文件时所有项目都必须设置项目分隔符。begin
					if(FileTypeEnum.TXT.value() == ((HrIntfaceVO)aggVO.getParentVO()).getIfiletype()
							&& !ArrayUtils.isEmpty(itemVOs)){
						for(FormatItemVO itemVO:itemVOs){
							if(itemVO.getVseparator() == null){
								throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0124")/*@res "导出文本文件时所有项目都必须设置项目分隔符！"*/);
							}
						}
					}
					//20150925 shenliangc 数据接口导出文本文件时所有项目都必须设置项目分隔符。end				
					for (int i = 0; itemVOs != null && i < itemVOs.length; i++)
					{
						itemVOs[i].setIseq(i + 1);

						if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))
						{
							itemVOs[i].setIsourcetype(Integer.valueOf(DataFromEnum.FORMULAR.getEnumValue().getValue()));
						}
					}
					aggVO.setTableVO(DataIOconstant.HR_DATAINTFACE_B,itemVOs);
					if (ioItemsPanel.getSignLinePanel().getBillListPanel() != null)
					{
						
						// 之前系统只截取了UI发生变化的行，但是为了顺序正常工作，要把所有VO重新整理一遍
						// 这边是首行
						CircularlyAccessibleValueObject[] headLines = ioItemsPanel.getSignLinePanel().getBillListPanel().getBodyBillModel().getBodyValueVOs(IfsettopVO.class.getName());
						for (int i = 0; headLines != null && i < headLines.length; i++)
						{
							((IfsettopVO) headLines[i]).setIseq(i + 1);
							((IfsettopVO) headLines[i]).setIifsum((Integer) BooleanEnum.NO.value());
							((IfsettopVO) headLines[i]).setItoplineposition(LineTopPositionEnum.HEAD.toIntValue());
							if (((IfsettopVO) headLines[i]).getPk_hr_ifsettop() != null) {
								((IfsettopVO) headLines[i]).setStatus(VOStatus.UPDATED);
							} else {
								((IfsettopVO) headLines[i]).setStatus(VOStatus.NEW);
							}
						}
						
						// 这边是尾行
						CircularlyAccessibleValueObject[] tailLines = ioItemsPanel.getSignLinePanel2().getBillListPanel().getBodyBillModel().getBodyValueVOs(IfsettopVO.class.getName());
						for (int i = 0; tailLines != null && i < tailLines.length; i++) {
							((IfsettopVO) tailLines[i]).setIseq(i + 1);
							((IfsettopVO) tailLines[i]).setIifsum((Integer) BooleanEnum.NO.value());
							((IfsettopVO) tailLines[i]).setItoplineposition(LineTopPositionEnum.TAIL.toIntValue());
							if (((IfsettopVO) tailLines[i]).getPk_hr_ifsettop() != null) {
								((IfsettopVO) tailLines[i]).setStatus(VOStatus.UPDATED);
							} else {
								((IfsettopVO) tailLines[i]).setStatus(VOStatus.NEW);
							}
						}
						
						ArrayList<CircularlyAccessibleValueObject> lineVOs = new ArrayList<CircularlyAccessibleValueObject>();
						for (CircularlyAccessibleValueObject obj : headLines) {
							lineVOs.add(obj);
						}
						for (CircularlyAccessibleValueObject obj : tailLines) {
							lineVOs.add(obj);
						}
						aggVO.setTableVO(DataIOconstant.HR_IFSETTOP, lineVOs.toArray(new CircularlyAccessibleValueObject[0]));
					}

					// 保存
					getAppModel().save(aggVO);
					
					if (StringUtils.isBlank(((HrIntfaceVO) aggVO.getParentVO()).getPrimaryKey())){ //新增时才有同步功能
						//数据接口节点在没有选择薪资方案及其期间时不可用
						String nodeCode = appModel.getContext().getNodeCode();
						//					Integer node = ((DataIOAppModel)getModel()).getNode();
						if(nodeCode.equals(DataIOconstant.NODE_DATAIO))
						{
							/*是否同步后续期间*/
							AggHrIntfaceVO[] aggVOs = new AggHrIntfaceVO[]{aggVO};
							WaLoginVO waLoginVO = ((WaLoginContext)appModel.getContext()).getWaLoginVO();
							String selectYear = waLoginVO.getPeriodVO().getCyear();
							String selectPeriod = waLoginVO.getPeriodVO().getCperiod();
							String currentYear = waLoginVO.getCyear();
							String currentPeriod = waLoginVO.getCperiod();
							if (!waLoginVO.getPk_wa_class().equals(waLoginVO.getPk_prnt_class())) {
								WaClassVO parentVO = NCLocator.getInstance().lookup(IWaClass.class).queryWaclassBypk(waLoginVO.getPk_prnt_class());
								currentYear = parentVO.getCyear();
								currentPeriod = parentVO.getCperiod();
							}
							if (!selectYear.equals(currentYear) || !selectPeriod.equals(currentPeriod)) {
								//说明当前选择的不是最新期间
								int result = MessageDialog.showYesNoDlg(null, null, ResHelper.getString("6013bnkitf","06013bnkitf0153")/*@res "是否从当前期间开始后续期间统一使用该数据接口设置？"*/);
								if(result == MessageDialog.ID_YES){
									appModel.syncAggHrIntfaceVO(aggVOs, currentYear, currentPeriod);
								}
							}
						}
					}

				}
				catch (Exception e1)
				{
					Logger.debug(e1.getMessage());
					Logger.error(e1.getMessage(), e1);
					WizardActionException e = new WizardActionException(e1);
					e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0077")/*@res "错误提示:"*/, e1.getMessage());
					throw e;
				}
			}

			@Override
			public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException
			{}

			@Override
			public void wizardCancel(WizardEvent event) throws WizardActionException
			{}

		});
		wizDlg.setResizable(true);
		return wizDlg;
	}

	private List<WizardStep> getSteps()
	{
		List<WizardStep> steps = new ArrayList<WizardStep>();

		steps.add(getStep1());
		if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))		{
			steps.add(getStep2());
		}
		steps.add(getStep3());

		return steps;
	}

	private WizardStep getStep2(){
		WizardStep step2 = new WizardStep();
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_BANK)){
			step2.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0150")/*@res "新增银行报盘"*/);
		}
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)){
			step2.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0149")/*@res "新增数据接口"*/);
		}
		step2.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0149")/*@res "新增数据接口"*/);
		step2.setDescription(ResHelper.getString("6013bnkitf","06013bnkitf0079")/*@res "选择项目"*/);
		WaLoginContext context = (WaLoginContext) appModel.getContext();
		step2.setComp(new ItemSelPanel(context,aggVO));
		//校验是否选择了关联项目
		step2.addValidator(new IWizardStepValidator()
		{

			@Override
			public void validate(JComponent comp, WizardModel model) throws WizardStepValidateException
			{
				// 得到用户输入值
				ItemSelPanel itemSelPanel = (ItemSelPanel) comp;

				WizardStepValidateException e = new WizardStepValidateException();

				if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)){
					Object[] rightPowerVOs = itemSelPanel.getUIListToList().getRightData();
					if (ArrayUtils.isEmpty(rightPowerVOs)) {
						e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0161")/*@res "接口项目定义错误"*/
								,ResHelper.getString("6013bnkitf","06013bnkitf0162")/*@res "接口定义项目不能为空！"*/);
					}else{
						if (((DataIOAppModel)appModel).getCol().equals((String)RelatedItemEnum.CODE.value())){
							boolean isContainCode = false;
							for (int i = 0; i < rightPowerVOs.length; i++) {
								if (((WaClassItemVO)rightPowerVOs[i]).getItemkey().equals("bd_psndoc.code")) {
									isContainCode = true;
								}
							}
							if (!isContainCode) {
								e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0161")/*@res "接口项目定义错误"*/
										,ResHelper.getString("6013bnkitf","06013bnkitf0163")/*@res "关联项目[人员编码]必须选择！"*/);
							}
						}else{
							boolean isContainType = false;
							boolean isContainId = false;
							for (int i = 0; i < rightPowerVOs.length; i++) {
								if (((WaClassItemVO)rightPowerVOs[i]).getItemkey().equals("bd_psndoc.idtype")) {
									isContainType = true;
								}
								if (((WaClassItemVO)rightPowerVOs[i]).getItemkey().equals("bd_psndoc.id")) {
									isContainId = true;
								}
							}
							if (!isContainType || !isContainId) {
								e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0161")/*@res "接口项目定义错误"*/
										,ResHelper.getString("6013bnkitf","06013bnkitf0164")/*@res "关联项目[证件类型+证件号码]必须同时选择！"*/);
							}
						}
					}

					if (!e.getMsgs().isEmpty())
					{
						throw e;
					}
				}
			}
		});

		return step2;
	}


	private WizardStep getStep1(){

		WizardStep step1 = new WizardStep();
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_BANK)){
			step1.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0150")/*@res "新增银行报盘"*/);
		}
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)){
			step1.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0149")/*@res "新增数据接口"*/);
		}
		step1.setDescription(ResHelper.getString("6013bnkitf","06013bnkitf0081")/*@res "基本信息,数据参数"*/);
		ParaInfPanel paraPanel = new ParaInfPanel(getAppModel());
		step1.setComp(paraPanel);
		if (aggVO != null)
		{
			HrIntfaceVO vo = (HrIntfaceVO) aggVO.getParentVO();

			paraPanel.getBankRefPane().setPK(vo.getPk_bankdoc());
			paraPanel.getFormatNameTextField().setText(vo.getVifname());
			// paraPanel.getOutFileNameTextField().setText(vo.getVfilename());
			Integer filetype = vo.getIfiletype();
			paraPanel.getFileTypeCombox().setSelectedItem(filetype);
			paraPanel.getMemTextArea().setText(vo.getVmemo());

			// 关联项目，分隔符，小数千分位，小数点，占位符,表头.标志行
			paraPanel.getRelatedItemComb().setSelectedItem(vo.getVcol());
			paraPanel.getUsedItemSeparatorChb().setSelected(vo.getIifseparator().equals(BooleanEnum.YES.value()));
			paraPanel.getItemSeparatorComb().setSelectedItem(vo.getIseparator());
			paraPanel.getKiloSeprtChb().setSelected(vo.getIifkilobit().equals(BooleanEnum.YES.value()));
			paraPanel.getDotChb().setSelected(vo.getIifdot().equals(BooleanEnum.YES.value()));
			paraPanel.getUsedPlaceholderChb().setSelected(vo.getIifcaret().equals(BooleanEnum.YES.value()));

			paraPanel.getOutputTableHeadChb().setSelected(vo.getIouthead().equals(BooleanEnum.YES.value()));
			paraPanel.getHeadSameToBodyChb().setSelected(vo.getIheadadjustbody() == null ? false : vo.getIheadadjustbody().equals(BooleanEnum.YES.value()));

			paraPanel.getLinetopSetChb().setSelected(vo.getIiftop().equals(BooleanEnum.YES.value()));
			paraPanel.getLinetopSetComb().setSelectedItem(vo.getToplinenum());
			paraPanel.getLinetopPosiComb().setSelectedItem(vo.getToplineposition());
			
			// HR本地化需求：添加多一行标志行
			paraPanel.getLinetopSetChb2().setSelected(vo.getIiftop2().equals(BooleanEnum.YES.value()));
			paraPanel.getLinetopSetComb2().setSelectedItem(vo.getToplinenum2());
			paraPanel.getLinetopPosiComb2().setSelectedItem(vo.getToplineposition2());
			

		}
		step1.addListener(new IWizardStepListener()
		{
			@Override
			public void stepActived(WizardStepEvent event)
			{

				// BaseInfPanel basePanel =
				// (BaseInfPanel)event.getStep().getModel().getSteps().get(0).getComp();
				ParaInfPanel paraPanel = (ParaInfPanel) event.getStep().getComp();

				// 隐藏表头，和标志行
				if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))
				{
					paraPanel.getRowPanel().setVisible(false);
					// paraPanel.getTablePanel().setVisible(false);
				}
			}

			@Override
			public void stepDisactived(WizardStepEvent event)
			{

			}

		});

		step1.addValidator(new IWizardStepValidator()
		{

			@Override
			public void validate(JComponent comp, WizardModel model) throws WizardStepValidateException
			{
				// 得到用户输入值
				ParaInfPanel paraPanel = (ParaInfPanel) comp;

				WizardStepValidateException e = new WizardStepValidateException();

				//银行报盘的非空校验
				if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
				{
					if (StringUtils.isBlank(paraPanel.getBankRefPane().getRefPK()))
					{
						e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0082"), ResHelper.getString("6013bnkitf","06013bnkitf0075")+"\n"+"["+ResHelper.getString("6013bnkitf","06013bnkitf0082")+"]");
					}
					if (StringUtils.isBlank(paraPanel.getFormatNameTextField().getText()))
					{
						if(e.getMsgs().isEmpty()){
							e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0082"), ResHelper.getString("6013bnkitf","06013bnkitf0075")+"\n"+"["+ResHelper.getString("6013bnkitf","06013bnkitf0072")+"]");
						}else{
							e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0082"), ResHelper.getString("6013bnkitf","06013bnkitf0075")+"\n"+"["+ResHelper.getString("6013bnkitf","06013bnkitf0082")+"]"+ResHelper.getString("6013bnkitf","06013bnkitf0151")+"["+ResHelper.getString("6013bnkitf","06013bnkitf0072")+"]");
						}
					}
					if(!e.getMsgs().isEmpty()){
						throw e;
					}
				}
				//数据接口非空校验
				else if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)){
					if (StringUtils.isBlank(paraPanel.getFormatNameTextField().getText()))
					{
						e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0072")/*@res "接口名称"*/, ResHelper.getString("6013bnkitf","06013bnkitf0075")+"\n"+"["+ResHelper.getString("6013bnkitf","06013bnkitf0072")+"]");
					}

					IDataIOManageService service = NCLocator.getInstance().lookup(IDataIOManageService.class);

					if (((paraPanel.getUsedItemSeparatorChb().isSelected() && paraPanel.getItemSeparatorComb().getSelectedIndex() == 0) || !paraPanel.getUsedItemSeparatorChb().isSelected()) && paraPanel.getKiloSeprtChb().isSelected())
					{

						e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0072")/*@res "接口名称"*/, ResHelper.getString("6013bnkitf","06013bnkitf0084")/*@res "使用千分位的时候，逗号不能作为分隔符!"*/);
					}
					try
					{
						AggHrIntfaceVO[] aggVOs = service.queryByCondition(getAppModel().getContext(), " vifname='" + NCESAPI.clientSqlEncode(paraPanel.getFormatNameTextField().getText()) + "'", null);
						String pk = null;
						if (aggVO != null)
						{
							HrIntfaceVO vo = (HrIntfaceVO) aggVO.getParentVO();
							pk = vo.getPk_dataio_intface();
						}

						boolean exist = (aggVOs != null && aggVOs.length > 1) || (

								aggVOs != null && aggVOs.length == 1 && pk != null && !aggVOs[0].getParentVO().getPrimaryKey().equals(pk));

						if (exist)
						{
							e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0072")/*@res "接口名称"*/, ResHelper.getString("6013bnkitf","06013bnkitf0085")/*@res "相同组织下存在同名的接口名称定义,请修改!"*/);
						}

					}
					catch (BusinessException e1)
					{
						e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0086")/*@res "校验"*/, e1.getMessage());
					}

					if (!e.getMsgs().isEmpty())
					{
						throw e;
					}
				}
			}
		});

		return step1;
	}

	private WizardStep getStep3(){
		WizardStep step3 = new WizardStep();
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_BANK)){
			step3.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0150")/*@res "新增银行报盘"*/);
		}
		if(appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)){
			step3.setTitle(ResHelper.getString("6013bnkitf","06013bnkitf0149")/*@res "新增数据接口"*/);
		}
		step3.setDescription(ResHelper.getString("6013bnkitf","06013bnkitf0088")/*@res "格式设置"*/);
		IOItemsPanel iopanel = new IOItemsPanel(getAppModel(), aggVO);
		step3.setComp(iopanel);

		step3.addListener(new IWizardStepListener()
		{

			@Override
			public void stepActived(WizardStepEvent event)
			{
				IOItemsPanel ioItemPanel = (IOItemsPanel) event.getStep().getComp();
				List<WizardStep> steps = event.getStep().getModel().getSteps();

				ParaInfPanel paraPanel = (ParaInfPanel) steps.get(0).getComp();
				boolean isSelected = paraPanel.getLinetopSetChb().isSelected();

				ioItemPanel.getSignLinePanel().setVisible(isSelected);

				if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))		{
					ItemSelPanel selPanel = (ItemSelPanel) steps.get(1).getComp();
					ioItemPanel.setAggVO(syncAggVO(syncAggVO(ioItemPanel.getAggVO(), paraPanel),selPanel));
				}else{
					ioItemPanel.setAggVO(syncAggVO(ioItemPanel.getAggVO(), paraPanel));
				}
				if (paraPanel.getFileTypeCombox().getSelectdItemValue().equals(FileTypeEnum.XLS.value()))
				{
					// 根据文件类型，隐藏相应列
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.VSEPARATOR);
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.ICARETPOS);
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.VCARET);
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.VINCLUDEBEFORE);
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.VINCLUDEAFTER);
				}
				else
				{
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.VSEPARATOR);
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.ICARETPOS);
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.VCARET);
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.VINCLUDEBEFORE);
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.VINCLUDEAFTER);

				}
				// 数据来源只有在文本文件类型中，并且是银行报盘时候才显示
				// 银行报盘 有 公式型和
				if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
				{
					ioItemPanel.getBillListPanel().showBodyTableCol(FormatItemVO.ISOURCETYPE);
				}
				else
				{
					ioItemPanel.getBillListPanel().hideBodyTableCol(FormatItemVO.ISOURCETYPE);
				}
				ioItemPanel.initValue();

			}

			@Override
			public void stepDisactived(WizardStepEvent event)
			{}

		});

		step3.addValidator(new IWizardStepValidator()
		{

			@Override
			public void validate(JComponent comp, WizardModel model) throws WizardStepValidateException
			{
				WizardStepValidateException e = new WizardStepValidateException();
				IOItemsPanel ioItemsPanel = (IOItemsPanel) comp;
				FormatItemVO[] itemVOs = (FormatItemVO[]) ioItemsPanel.getBillListPanel().getBodyBillModel().getBodyValueChangeVOs(FormatItemVO.class.getName());

				if (itemVOs == null || itemVOs.length <=0)
				{
					e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, ResHelper.getString("6013bnkitf","06013bnkitf0089")/*@res "格式项目至少有一行不能为空"*/);
				}
				for (int i = 0; itemVOs != null && i < itemVOs.length; i++)
				{
					String strErrorMessageBegin = MessageFormat.format(ResHelper.getString("6013bnkitf","06013bnkitf0090")/*@res "第{0}行，"*/, i + 1);
					//shenliangc 20140702 银行报盘增加户名
					// 如果不是单一型 直接返回
					if (itemVOs[i].getIsourcetype().intValue() != 0)
					{
						if (itemVOs[i].getVcontent() == null || itemVOs[i].getVcontent().equals(""))
						{
							e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin + ResHelper.getString("6013bnkitf","06013bnkitf0091")/*@res "项目不能为空，请填写!"*/);
							break;
						}
					}
					if (itemVOs[i].getVfieldname() == null || itemVOs[i].getVfieldname().equals(""))
					{
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin + ResHelper.getString("6013bnkitf","06013bnkitf0092")/*@res "字段名称不能为空，请填写!"*/);
						break;
					}

					if (itemVOs[i].getIfldwidth() == null || itemVOs[i].getIfldwidth().intValue() == 0)
					{
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin + ResHelper.getString("6013bnkitf","06013bnkitf0093")/*@res "项目宽度不能为空，请填写!"*/);
						break;
					} else if (itemVOs[i].getIfldwidth().intValue() > ItemPropertyConst.String_MAX_width
							|| itemVOs[i].getIfldwidth().intValue() <=ItemPropertyConst.String_Digit) {
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin+ ResHelper.getString("6013bnkitf","06013bnkitf0094")/*@res "项目宽度设置不正确，请重新输入!"*/);
						break;
					}
					if (itemVOs[i].getIflddecimal() == null
							&& itemVOs[i].getIfieldtype() == ItemPropertyConst.Float_type)
					{
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin + ResHelper.getString("6013bnkitf","06013bnkitf0095")/*@res "小数位数不能为空，请填写!"*/);
						break;
					}else if (itemVOs[i].getIflddecimal().intValue() > ItemPropertyConst.Float_MAX_decimalwidth
							|| itemVOs[i].getIflddecimal().intValue() < ItemPropertyConst.Float_MIN_decimalwidth) {
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin+ ResHelper.getString("6013bnkitf","06013bnkitf0096")/*@res "小数位数设置不正确，请重新输入!"*/);
						break;
					}
					if (!volidateSingle(itemVOs[i]))
					{
						e.addMsg(ResHelper.getString("6013bnkitf","16013bnkitf0004")/*@res "格式项目"*/, strErrorMessageBegin + ResHelper.getString("6013bnkitf","06013bnkitf0097")/*@res "单一型内容格式不正确，请重新填写!"*/);
						break;
					}
				}

				BillListPanel singLinePanel = ioItemsPanel.getSignLinePanel().getBillListPanel();
				if (singLinePanel != null)
				{
					IfsettopVO[] setVOs = (IfsettopVO[]) singLinePanel.getBodyBillModel().getBodyValueChangeVOs(IfsettopVO.class.getName());

					for (int i = 0; setVOs != null && i < setVOs.length; i++)
					{
						if (setVOs[i].getVcontent() == null || setVOs[i].getVcontent().equals(""))
						{
							e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0098")/*@res "标志行"*/, ResHelper.getString("6013bnkitf","06013bnkitf0099")/*@res "标志行项目不能为空，请填写!"*/);
							break;
						}
					}
					BillModel billModel = singLinePanel.getBodyBillModel();
					int intRowCount = billModel.getRowCount();
					for(int i=0;i<intRowCount;i++){
						DataIOItemVO dataIOItemVO = (DataIOItemVO)billModel.getValueAt(i, IfsettopVO.VCONTENT);
						if(dataIOItemVO==null)continue;
						if (DataIOconstant.UNITCODE.equals(dataIOItemVO.getPrimaryKey())){
							Object valueObj = billModel.getValueAt(i, IfsettopVO.VFIELDNAME);
							if(valueObj == null || StringUtils.isBlank(valueObj.toString())){
								e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0098")/*@res "标志行"*/, ResHelper.getString("6013bnkitf","06013bnkitf0136")/*@res "单位代号不能为空，请填写！"*/);
								break;
							}
						}
						if (DataIOconstant.DATE.equals(dataIOItemVO.getPrimaryKey())){
							Object valueObj = billModel.getValueAt(i, IfsettopVO.VFIELDNAME);
							if(valueObj == null || StringUtils.isBlank(valueObj.toString())){
								e.addMsg(ResHelper.getString("6013bnkitf","06013bnkitf0098")/*@res "标志行"*/, ResHelper.getString("6013bnkitf","06013bnkitf0137")/*@res "发放日期不能为空，请填写！"*/);
								break;
							}
						}
					}
				}

				if (!e.getMsgs().isEmpty())
				{
					throw e;
				}
			}
		});

		return step3;
	}

	private boolean volidateSingle(FormatItemVO itemVO)
	{
		// 如果不是单一型 直接返回
		if (itemVO.getIsourcetype().intValue() != 0)
		{
			return true;
		}

		return validateSingleFormat(itemVO.getVcontent(), itemVO);
	}

	private boolean validateSingleFormat(String value, FormatItemVO formatItemVO)
	{
		if (value == null)
		{
			value = "";
		}
		value = value.trim();
		int width = formatItemVO.getIfldwidth().intValue();
		int decimald = formatItemVO.getIflddecimal().intValue();
		if ((formatItemVO.getIfieldtype() + "").equals(FieldTypeEnum.DEC.getEnumValue().getValue()) && value.length() > width + decimald)
		{
			return false;
		}
		if ((formatItemVO.getIfieldtype() + "").equals(FieldTypeEnum.STR.getEnumValue().getValue()) && value.length() > width)
		{
			return false;
		}
		if ((formatItemVO.getIfieldtype() + "").equals(FieldTypeEnum.DATE.getEnumValue().getValue()))
		{
			try
			{
				if (!StringUtil.isEmpty(value))
				{
					new UFDate(value);
				}
			}
			catch (Exception e)
			{
				return false;
			}
		}
		if ((formatItemVO.getIfieldtype() + "").equals(FieldTypeEnum.BOO.getEnumValue().getValue()))
		{
			try
			{
				if (!StringUtil.isEmpty(value))
				{
					new Boolean(value);
				}
			}
			catch (Exception e)
			{
				return false;
			}
		}
		if ((formatItemVO.getIfieldtype() + "").equals(FieldTypeEnum.DEC.getEnumValue().getValue()))
		{
			int decimal = 0;
			int dotPosition = value.indexOf(".");
			if (dotPosition >= 0)
			{
				decimal = value.substring(dotPosition + 1).length();
			}
			if (decimal > formatItemVO.getIflddecimal().intValue())
			{
				return false;
			}
			try
			{
				if (value.indexOf("-") != -1)
				{
					value = value.substring(value.indexOf("-"));
				}
				new UFDouble(value);
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return true;
	}

	private AggHrIntfaceVO syncAggVO(AggHrIntfaceVO aggHrVO, ItemSelPanel panel){
		if(aggHrVO==null){
			return null;
		}
		FormatItemVO[] childvos = null;
		Object[] vos = panel.getSelectedItems();
		Vector<String> v = new Vector<String>();
		//		FormatItemVO[] itemVOs = (FormatItemVO[])aggVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		//		for(FormatItemVO vo: itemVOs){
		//			vo.setStatus(VOStatus.DELETED);
		//		}
		FormatItemVO[] itemVOs =(FormatItemVO[])aggHrVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		Map<String,FormatItemVO> map = new HashMap<String,FormatItemVO>();
		if(itemVOs!=null){
			for(FormatItemVO vo:itemVOs ){
				map.put(vo.getVcontent(), vo);
			}
		}
		if (vos != null) {
			for (Object obj : vos) {
				WaClassItemVO vo = (WaClassItemVO) obj;
				if (vo.getItemkey().indexOf(".") > 0) {
					v.add(vo.getItemkey());
				} else {
					v.add("wa_data." + vo.getItemkey());
				}
			}
			childvos = new FormatItemVO[vos.length];
			FormatItemVO itemvo = null;
			for (int i = 0; i < vos.length; i++) {
				WaClassItemVO vo = (WaClassItemVO) vos[i];
				String content = vo.getItemkey().indexOf(".") > 0 ? vo.getItemkey() : "wa_data." + vo.getItemkey();
				if (map.containsKey(content)) {
					itemvo = map.get(content);
				} else {
					itemvo = new FormatItemVO();
					itemvo.setVcontent(content);
					if (TypeEnumVO.FLOATTYPE.value().equals(vo.getIitemtype())) {
						itemvo.setIfieldtype((Integer) FieldTypeEnum.DEC.value());
					} else if (TypeEnumVO.CHARTYPE.value().equals(vo.getIitemtype())) {
						itemvo.setIfieldtype((Integer) FieldTypeEnum.STR.value());
					} else if (TypeEnumVO.DATETYPE.value().equals(vo.getIitemtype())) {
						itemvo.setIfieldtype((Integer) FieldTypeEnum.DATE.value());
					}
					itemvo.setIsourcetype(Integer.valueOf(DataFromEnum.FORMULAR.getEnumValue().getValue()));
					itemvo.setIcaretpos((Integer) CaretposEnum.NO.value());
					itemvo.setIfldwidth(vo.getIfldwidth());
					itemvo.setIflddecimal(vo.getIflddecimal());
					itemvo.setVfieldname(vo.getMultilangName());
				}
				itemvo.setIfid(null);
				itemvo.setPk_dataintface_b(null);

//				if(FieldTypeEnum.DEC.value().equals(itemvo.getIfieldtype()) && 0==itemvo.getIflddecimal()){
//					itemvo.setIflddecimal(2);
//				}
				itemvo.setStatus(VOStatus.NEW);
				childvos[i] = itemvo;
			}
		}
		aggHrVO.setTableVO(DataIOconstant.HR_DATAINTFACE_B, childvos);
		return aggHrVO;
	}

	public AggHrIntfaceVO syncAggVO(AggHrIntfaceVO aggVO, ParaInfPanel paraPanel)
	{

		if (aggVO == null)
		{
			// 新建时，赋默认值
			aggVO = new AggHrIntfaceVO();
			HrIntfaceVO vo = new HrIntfaceVO();

			WaLoginContext context = (WaLoginContext) appModel.getContext();
			// 组织，集团，用户信息
			vo.setPk_org(context.getPk_org());
			vo.setPk_group(context.getPk_group());
			vo.setOperatorid(context.getPk_loginUser());

			// 如果是数据接口，则需要给薪资年度，薪资方案，期间赋值
			if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))
			{
				vo.setIiftype((Integer) ItfTypeEnum.WA_DATAIO.value());

				String parentpK = WaLoginVOHelper.getParentClassPK(context.getWaLoginVO());
				vo.setClassid(parentpK);
				vo.setCyear(context.getWaYear());
				vo.setCperiod(context.getWaPeriod());
			}
			else
			{
				vo.setIiftype((Integer) ItfTypeEnum.WA_BANK.value());
			}

			aggVO.setParentVO(vo);
		}

		// 接口名称，外部文件及其类型，报送银行
		// ((HrIntfaceVO)aggVO.getParentVO()).setVifname(basePanel.getFormatNameTextField().getText());
		// ((HrIntfaceVO)aggVO.getParentVO()).setVfilename(basePanel.getOutFileNameTextField().getText());
		// ((HrIntfaceVO)aggVO.getParentVO()).setIfiletype((Integer)basePanel.getFileTypeCombox().getSelectdItemValue());
		// ((HrIntfaceVO)aggVO.getParentVO()).setPk_bankdoc(basePanel.getBankRefPane().getRefPK());
		// ((HrIntfaceVO)aggVO.getParentVO()).setVmemo(basePanel.getMemTextArea().getText());

		((HrIntfaceVO) aggVO.getParentVO()).setPk_bankdoc(paraPanel.getBankRefPane().getRefPK());
		((HrIntfaceVO) aggVO.getParentVO()).setVifname(paraPanel.getFormatNameTextField().getText());
		// ((HrIntfaceVO)aggVO.getParentVO()).setVfilename(paraPanel.getOutFileNameTextField().getText());
		((HrIntfaceVO) aggVO.getParentVO()).setIfiletype((Integer) paraPanel.getFileTypeCombox().getSelectdItemValue());

		((HrIntfaceVO) aggVO.getParentVO()).setVmemo(paraPanel.getMemTextArea().getText());

		// 关联字段
		String colName = (String) paraPanel.getRelatedItemComb().getSelectdItemValue();
		((HrIntfaceVO) aggVO.getParentVO()).setVcol(colName);
		((HrIntfaceVO) aggVO.getParentVO()).setVtable("bd_psndoc");

		// 分隔符
		Integer ifseparator = paraPanel.getUsedItemSeparatorChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIifseparator(ifseparator);
		((HrIntfaceVO) aggVO.getParentVO()).setIseparator((Integer) paraPanel.getItemSeparatorComb().getSelectdItemValue());

		// 补位符
		Integer iplaceholder = paraPanel.getUsedPlaceholderChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIifcaret(iplaceholder);

		// 千位，小数点
		Integer kilo = paraPanel.getKiloSeprtChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		Integer dot = paraPanel.getDotChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIifkilobit(kilo);
		((HrIntfaceVO) aggVO.getParentVO()).setIifdot(dot);

		// 表头
		Integer outhead = paraPanel.getOutputTableHeadChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		Integer same = paraPanel.getHeadSameToBodyChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIouthead(outhead);
		((HrIntfaceVO) aggVO.getParentVO()).setIheadadjustbody(same);

		// HR本地化：添加了第二个标志行的同步，这样就可以同时配置首行和尾行
		// 两个标志行写死一首一尾 通过checkbox去判定到底用不用
		// 标志行1
		Integer linetop = paraPanel.getLinetopSetChb().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIiftop(linetop);
		((HrIntfaceVO) aggVO.getParentVO()).setToplinenum(LineTopEnum.MLINE.toIntValue());
		((HrIntfaceVO) aggVO.getParentVO()).setToplineposition(LineTopPositionEnum.HEAD.toIntValue());
		
		// 标志行2
		Integer linetop2 = paraPanel.getLinetopSetChb2().isSelected() ? (Integer) BooleanEnum.YES.value() : (Integer) BooleanEnum.NO.value();
		((HrIntfaceVO) aggVO.getParentVO()).setIiftop2(linetop2);
		((HrIntfaceVO) aggVO.getParentVO()).setToplinenum2(LineTopEnum.MLINE.toIntValue());
		((HrIntfaceVO) aggVO.getParentVO()).setToplineposition2(LineTopPositionEnum.TAIL.toIntValue());

		return aggVO;
	}

	public AggHrIntfaceVO getAggVO()
	{
		return aggVO;
	}

	public void setAggVO(AggHrIntfaceVO aggVO)
	{
		this.aggVO = aggVO;
	}

	public DataIOAppModel getAppModel()
	{
		return appModel;
	}

	public Container getParent()
	{
		return parent;
	}

	public void setParent(Container parent)
	{
		this.parent = parent;
	}

}