package nc.ui.hr.infoset.action;

import java.awt.Event;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;

/***************************************************************************
 * 设置默认显示顺序<br>
 * Created on 2018-10-02 01:40:27<br>
 * @author Ethan Wu
 ***************************************************************************/
public class AddLocalizationFieldsAction extends HrAction{

	public AddLocalizationFieldsAction() {
		super();
		
		//TODO: 尽快建立按钮名称和描述的多语字段
		this.setBtnName("Add Local Fields");
		
		putValue(INCAction.CODE, "LocalFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields");
		// Ctrl+Alt+L用于快捷键 但是感觉没什么卵用
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('L', Event.CTRL_MASK + Event.ALT_MASK));
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
