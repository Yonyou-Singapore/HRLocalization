package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.itf.hr.wa.ICashcard;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.datainterface.DefaultExporter;
import nc.ui.wa.datainterface.TxtExporter;
import nc.ui.wa.datainterface.TxtExporterForBank;
import nc.ui.wa.datainterface.XlsExporter;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.datainterface.validator.FipEndValidationService;
import nc.ui.wa.datainterface.view.DataIOPanel;
import nc.ui.wa.datainterface.view.DefaultDataIOHeadPanel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FileTypeEnum;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.datainterface.BankEnterpriseVO;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.pub.HRWAMetadataConstants;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: xuanlt
 * @date: 2010-1-14 上午09:17:08
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class OutDataAction extends HrAction {
	private DefaultDataIOHeadPanel ioHeadPanel;
	private DataIOPanel ioMainPanel;

	/**
	 * @author xuanlt on 2010-1-14
	 */
	public OutDataAction() {
		super();
		this.setBtnName(ResHelper.getString("6013bnkitf", "06013bnkitf0050")/*
		 * @res
		 * "导出数据"
		 */);
		this.putValue(INCAction.CODE, "ExportData");
		this.putValue(Action.SHORT_DESCRIPTION,
				ResHelper.getString("6013bnkitf", "06013bnkitf0051")/*
				 * @res
				 * "导出数据(Ctrl+O)"
				 */);
	}

	/**
	 * @author xuanlt on 2010-1-14
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (getModel().getContext().getNodeCode()
				.equals(DataIOconstant.NODE_BANK)) {
			WaLoginContext loginContext = (WaLoginContext) getModel()
					.getContext();
			WaLoginVO waloginvo = loginContext.getWaLoginVO();
			BankEnterpriseVO[] bankEnterpriseVOs = NCLocator.getInstance()
					.lookup(ICashcard.class)
					.queryBankEnterpriseVOs(waloginvo, 1);
			if (!ArrayUtils.isEmpty(bankEnterpriseVOs)) {
				for (BankEnterpriseVO bankEnterpriseVO : bankEnterpriseVOs) {
					if (UFBoolean.TRUE.equals(bankEnterpriseVO.getFipendflag())) {
						throw new BusinessException(ResHelper.getString(
								"60130bankitf", "060130bankitf0001")
								/* @res "该方案财务结算数据已经传递生成工资清单，若继续请取消已传递工资清单" */);
					}
				}
			}

			FipEndValidationService apportionValidator=new FipEndValidationService();
			//校验多次发放薪资方案
			apportionValidator.validater(waloginvo);
		}

		String fileName = getIoHeadPanel().getFileLocation();
		if (StringUtils.isBlank(fileName)) {
			throw new BusinessException(ResHelper.getString("6013bnkitf", "06013bnkitf0052")/*
					 * @res
					 * "请指定文件位置!"
					 */);
		}

		if (ioMainPanel.getAppModel().getResults().size() == 0) {
			int flag = MessageDialog.showYesNoDlg(getModel().getContext()
					.getEntranceUI(), null, ResHelper.getString("6013bnkitf",
							"06013bnkitf0053")/* @res "确定要导出空数据吗？" */);
			if (flag == MessageDialog.ID_CANCEL || flag == MessageDialog.ID_NO) {
				return;
			}
		}

		AggHrIntfaceVO[] vos = null;
		HashMap<String, String> filtermap = new HashMap<String, String>();
		if (getModel().getContext().getNodeCode()
				.equals(DataIOconstant.NODE_BANK)) {
			vos = (AggHrIntfaceVO[]) ((BillManageModel) getModel()).getData()
					.toArray(new AggHrIntfaceVO[0]);
			//如果txt文件为空, 则不导出文件 add by weiningc 20200219 start
			HashMap<String, ArrayList<HashMap<String, Object>>> exportresults = ioMainPanel.getAppModel().getResults();
			for(Map.Entry<String, ArrayList<HashMap<String, Object>>> entry : exportresults.entrySet()) {
				if(entry.getValue() == null) {
					filtermap.put(entry.getKey(), null);
				} else {
					filtermap.put(entry.getKey(), "Not Blank");
				}
			}
			//end
		} else {
			AggHrIntfaceVO aggVO = (AggHrIntfaceVO) this.getModel()
					.getSelectedData();
			vos = new AggHrIntfaceVO[] { aggVO };
		}

		// HrIntfaceVO itfVO = null;
		for (int i = 0; vos != null && i < vos.length; i++) {
			//如果txt文件为空, 则不导出文件 add by weiningc 20200219 start
			if (getModel().getContext().getNodeCode()
					.equals(DataIOconstant.NODE_BANK)) {
				String blankflag = vos[i].getParentVO().getPrimaryKey();
				if(filtermap.get(blankflag) == null) {
					continue;
				}
			}
			//end
			// itfVO = (HrIntfaceVO) vos[i].getParentVO();
			// ArrayList<HashMap<String, Object>> datas = ((DataIOAppModel)
			// getModel())
			// .getResults().get(itfVO.getPk_dataio_intface());
			// if
			// (getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK)
			// && (datas == null || datas.size() == 0)) {
			// continue;
			// }
			DefaultExporter strategy = createExporter(vos[i]);
			if (strategy != null) {
				strategy.exportToFile();

			}
		}

		WaLoginContext loginContext = (WaLoginContext) getModel()
				.getContext();
		
		if("60130dataitf".equals(loginContext.getNodeCode())){
			WaBusilogUtil.writeWaClassBusiLog(loginContext,HRWAMetadataConstants.OUTDATA,HRWAMetadataConstants.WA_CLASS_OUTDATA_ID);
		}else if("60130bankitf".equals(loginContext.getNodeCode())){
			WaBusilogUtil.writeWaClassBusiLog(loginContext,HRWAMetadataConstants.BANKOUTDATA,HRWAMetadataConstants.WA_CLASS_BANKOUTDATA_ID);
		}
		
		if (!((DataIOAppModel) getModel()).isBlnIsCancel()) {
			//			MessageDialog.showHintDlg(this.getEntranceUI(), null,
			//					ResHelper.getString("6013bnkitf", "06013bnkitf0054")/*
			//																		 * @res
			//																		 * "数据导出操作成功！"
			//																		 */);
			putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("6013bnkitf", "06013bnkitf0054"));
		}
	}

	protected DefaultExporter createExporter(AggHrIntfaceVO aggVO) {
		// HR本地化需求改动：为银行报盘添加专门的导出策略，以免与数据接口功能发生冲突
		WaLoginContext loginContext = (WaLoginContext) getModel().getContext();
		DefaultExporter exporter = null;
		HrIntfaceVO headvo = (HrIntfaceVO) aggVO.getParentVO();
		if (headvo.getIfiletype().equals(FileTypeEnum.TXT.value()) && loginContext.getNodeCode().equals(DataIOconstant.NODE_DATAIO)) {
			exporter = new TxtExporter();
		} else if (headvo.getIfiletype().equals(FileTypeEnum.TXT.value()) && loginContext.getNodeCode().equals(DataIOconstant.NODE_BANK)) { 
			exporter = new TxtExporterForBank();
		}else {// excel
			exporter = new XlsExporter();
		}
		exporter.setParas(getIoHeadPanel().getParasConfig());
		exporter.setIntfaceInfs(new AggHrIntfaceVO[] { aggVO });
		exporter.setAppModel((DataIOAppModel) getModel());
		return exporter;
	}

	public DefaultDataIOHeadPanel getIoHeadPanel() {
		return ioHeadPanel;
	}

	public void setIoHeadPanel(DefaultDataIOHeadPanel ioHeadPanel) {
		this.ioHeadPanel = ioHeadPanel;
	}

	public DataIOPanel getIoMainPanel() {
		return ioMainPanel;
	}

	public void setIoMainPanel(DataIOPanel ioMainPanel) {
		this.ioMainPanel = ioMainPanel;
	}

}