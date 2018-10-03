package nc.ui.hr.infoset.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.itf.hr.infoset.IInfoSet;
import nc.ui.hr.uif2.action.HrAction;

/***************************************************************************
 * 添加HR东南亚本地化人力项目通配字段按钮<br>
 * Created on 2018-10-02 23:23:23pm<br>
 * @author Ethan Wu
 ***************************************************************************/
public class AddGlobalFieldsAction extends HrAction {

	private static final long serialVersionUID = -8228947221930560397L;

	public AddGlobalFieldsAction() {
		super();
		
		//TODO: 尽快建立按钮名称和描述的多语字段
		this.setBtnName("Global");
		
		putValue(INCAction.CODE, "GlobalFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields for Global Requirement");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 前端校验判断是否已经生成
		NCLocator.getInstance().lookup(IInfoSet.class).addLocalizationFields("GLOBAL");
	}
}
