package nc.bs.hrss.pub.pf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.uap.ctrl.tpl.bill.gen.BillTemplateConst;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIFlowvLayout;
import nc.uap.lfw.jsp.uimeta.UIFlowvPanel;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UIView;
import nc.uap.wfm.constant.WfmConstants;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * Web制单单据审批的PageModel
 * 
 * @author qiaoxp
 * 
 */
public class WebBillApprovePageMode extends PageModel {

	@Override
	protected LfwWindow createPageMeta() {
		// 单据主键
		String billId = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_BillID, billId);

		// 单据是否可编辑标识
		String billEditable = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(PFUtil.FLAG_BILL_EDITABLED);
		AppUtil.addAppAttr(PFUtil.FLAG_BILL_EDITABLED, billEditable);

		// 单据类型
		String billType = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(HrssConsts.BILL_TYPE_CODE);
		AppUtil.addAppAttr(HrssConsts.BILL_TYPE_CODE, billType);

		// 流程类型
		String flowTypePk = LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter(BillTemplateConst.BILLTYPE);
		if (!StringUtils.isEmpty(flowTypePk)) {
			try {
				flowTypePk = URLDecoder.decode(flowTypePk, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LfwLogger.error(e.getMessage(), e);
			}
		}
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_FolwTypePk, flowTypePk);

		LfwWindow pm = super.createPageMeta();
		return pm;
	}

	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		UIMeta pageUm = (UIMeta) getUIMeta();
		UIView uiWidget = (UIView) pageUm.findChildById(HrssConsts.PAGE_MAIN_WIDGET);
		UIView taskExtUIWidget = (UIView) pageUm.findChildById("pubview_exetask");

		if (uiWidget.getUimeta().getElement() instanceof UIFlowvLayout) {
			UIFlowvLayout flowvlayout = (UIFlowvLayout) uiWidget.getUimeta().getElement();
			if (flowvlayout != null) {
				UIFlowvPanel flowvPanel = (UIFlowvPanel) flowvlayout.findChildById("flowvPanelTask");
				if (flowvPanel != null) {
					flowvPanel.setElement(taskExtUIWidget);
					if (pageUm.getElement() instanceof UIFlowvLayout) {
						UIFlowvLayout pageflowvlayout = (UIFlowvLayout) pageUm.getElement();
						List<UILayoutPanel> panelList = pageflowvlayout.getPanelList();
						pageflowvlayout.removePanel(panelList.get(0));
						//调整流程信息panel位置到最后 add by weiningc 20190531 start
						List<UILayoutPanel> changedpanel = flowvPanel.getLayout().getPanelList();
						UILayoutPanel flowapprovepanel = changedpanel.get(1);
						flowvPanel.getLayout().removePanel(changedpanel.get(1));
						flowvPanel.getLayout().addPanel(changedpanel.size()-1, flowapprovepanel);
						//end 
					}
				}
			}
		}

	}

}