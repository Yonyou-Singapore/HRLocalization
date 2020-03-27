package nc.ui.hr.infoset.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.itf.hr.infoset.IInfoSet;
import nc.ui.hr.uif2.action.HrAction;

public class AddIndonesiaFieldsAction extends HrAction {
	public static final String INDONESIA = "IND";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddIndonesiaFieldsAction() {
		super();
		
		//TODO: 尽快建立按钮名称和描述的多语字段
		this.setBtnName("Indonesia");
		
		putValue(INCAction.CODE, "IDNFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields for Indonesia");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		NCLocator.getInstance().lookup(IInfoSet.class).addLocalizationFields(INDONESIA);
	}

}
