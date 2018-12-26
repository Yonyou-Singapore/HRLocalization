package nc.ui.hrrp.ds.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;

/**
 * <p>Title: CardHeadTailBeforeEditHandler</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 * @since 2014-10-12
 */
public class CardHeadTailBeforeEditHandler  implements IAppEventHandler<CardHeadTailBeforeEditEvent> {

	/**
	 * <p>方法名称：handleAppEvent</p>
	 * <p>方法描述：</p>
	 * @param e
	 * @author
	 * @since 2014-9-15
	 */
	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		// TODO 自动生成的方法存根
		e.setReturnValue(true);
	}

}
