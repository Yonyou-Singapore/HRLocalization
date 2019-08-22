package nc.message.reconstruction;

import nc.message.msgcenter.msgbox.IMessageBoxCode;
import nc.message.msgcenter.msgtable.AbstractMSGTable;
import nc.message.msgcenter.msgtable.DefaultMessageTable;
import nc.message.msgcenter.msgtable.SendMsgTable;

public class MSGTableFactory
{
	private static MSGTableFactory instance = new MSGTableFactory();

	private MSGTableFactory()
	{
		super();
	}

	public static MSGTableFactory getInstance()
	{
		return instance;
	}

	public AbstractMSGTable createMSGTable(String boxcode)
	{
		if (boxcode.equals(IMessageBoxCode.SEND) || boxcode.equals(IMessageBoxCode.DELETE))
			return new SendMsgTable();
		return new DefaultMessageTable();
	}

	public int[] getMSGTableColumnWidth(String boxcode)
	{
		if (boxcode.equals(IMessageBoxCode.SEND) || boxcode.equals(IMessageBoxCode.DELETE))
			return new int[]{ 60, 70, 70, 800, 100, 200 };
		return new int[]{ 60, 70, 70, 70, 70, 800, 100, 200, 100 };
	}
}
