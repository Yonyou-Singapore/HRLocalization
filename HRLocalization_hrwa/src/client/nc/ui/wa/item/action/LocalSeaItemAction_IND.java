package nc.ui.wa.item.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.itf.hr.wa.ISeaLocalItemManageService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.wa.item.LocalLicenseUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.localenum.CountryItemEnum;

public class LocalSeaItemAction_IND extends NCAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String INDONESIA = "IND";
	private AbstractAppModel model;
	
	public LocalSeaItemAction_IND() {
		super();
		this.setBtnName("Indonesia items");
		putValue(INCAction.CODE, "IDNFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields for Indonesia");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		WaItemVO selectedData = (WaItemVO) this.getModel().getSelectedData();
		Boolean issure = MessageDialog.showYesNoCancelDlg(null, 
				"Tips",
				"Are you sure to increase the common salary item? This operation cannot be rolled back!") == UIDialog.ID_YES;
		Boolean issure_category = MessageDialog.showOkCancelDlg(null, 
				"Sure this selected category?",
				"Confirm this selected cagegory?") == UIDialog.ID_OK;
		if(selectedData != null && issure && issure_category) {
			NCLocator.getInstance().lookup(ISeaLocalItemManageService.class)
				.saveBactchItemForSeaLocal(selectedData, CountryItemEnum.getCode(INDONESIA));
		}
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}
	
}
