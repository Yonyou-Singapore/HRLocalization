package nc.ui.wa.item.model;

import nc.ui.bd.ref.AbstractRefModel;

public class TotalWaItemRefModel extends AbstractRefModel {
	private final String moTitle = "Total Salary Item Reference Model";
	private final String tableName = "wa_item";
	private final String[] fieldCode = {
			 "wa_item.name", "wa_item.itemkey"
	};
	private final String[] fieldName = {
			 "Salary Item Name", "Salary Item Key"
	};
	private final String valueField = "wa_item.itemkey";
	
	public TotalWaItemRefModel() {
		super();
		reset();
	}
	
	public void reset() {
		setRefNodeName(this.moTitle);
		setRefTitle(this.moTitle);
		setTableName(this.tableName);
		setFieldCode(this.fieldCode);
		setFieldName(this.fieldName);
		setPkFieldCode(this.valueField);
		setRefCodeField("wa_item.itemkey");
		setRefNameField("wa_item.name");
		setOrderPart(" wa_item.itemkey ");
		setWherePart(" 1=1 and wa_item.g_istotalitem = 'Y' and wa_item.dr = 0 ");
	}

	
	
}
