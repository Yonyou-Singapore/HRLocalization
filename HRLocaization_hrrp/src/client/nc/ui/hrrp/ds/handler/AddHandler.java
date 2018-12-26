package nc.ui.hrrp.ds.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.hrrp.ds.Rpdrstandvo;
import nc.vo.pubapp.AppContext;

/**
 * <p>Title: AddHandler</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 * @since 2014-10-12
 */
public class AddHandler implements IAppEventHandler<AddEvent> {

	@Override
	public void handleAppEvent(AddEvent e) {
		String pk_group = e.getContext().getPk_group();
		String pk_org = e.getContext().getPk_org();
		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		// 设置主组织默认值
		panel.setHeadItem(Rpdrstandvo.PK_GROUP, pk_group);
		panel.setHeadItem(Rpdrstandvo.PK_ORG, pk_org);
		// 设置单据状态、日期默认值
		panel.setHeadItem(Rpdrstandvo.CREATIONTIME, AppContext.getInstance()
				.getBusiDate());
		panel.setHeadItem(Rpdrstandvo.CREATOR, e.getContext().getPk_loginUser());
		

	}

}
