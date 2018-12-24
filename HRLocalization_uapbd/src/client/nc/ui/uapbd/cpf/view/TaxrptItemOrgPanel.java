package nc.ui.uapbd.cpf.view;

import nc.itf.hr.org.IPrimaryOrgQry;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.uapbd.cpf.model.TaxrptItemModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;

@SuppressWarnings("restriction")
public class TaxrptItemOrgPanel extends PrimaryOrgPanel{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.UISTATE_CHANGED==event.getType()){
			if(getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT || getModel().getUiState() == UIState.DISABLE){
				getRefPane().setEnabled(false);
			}else{
				getRefPane().setEnabled(true);
			}
			if("TotalSet".equals(((TaxrptItemModel)getModel()).getCurrentType())){
				getRefPane().setEnabled(false);
			}
		}
	}
	
	@Override
	public void setControlType(int controlType) {
		super.setControlType(IPrimaryOrgQry.CONTROLTYPE_ADMINORG);
	}
}
