package nc.ui.wa.datainterface.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.datainterface.model.WaSignLineNameStrategy;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.DataIOItemVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItemSeprtorEnum;
import nc.vo.hr.datainterface.LineTopPositionEnum;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.datainterface.DataIOconstant;

/**
 *
 * @author: xuanlt
 * @date: 2010-1-6 下午03:11:01
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class SignLinePanel extends UIPanel implements BillEditListener, BillEditListener2
{

	private String nodekey = "dline";
	private BillManageModel model = null;

	private UIButton bnLineAddBankRow;
	private UIButton bnLineDelBankRow;
	private UIPanel buttonPanel = null;

	private String nameOfFldName = "";

	private ModuleItemStrategy drawItemsCreator = null;
	// vos 对应 nameOfFldName
	private DataIOItemVO[] vos = null;
	
	private int flagLinePosition;
	
	// HR本地化需求：重载了构造器 这样标志行可以用来分开收尾 start
	public SignLinePanel() {
		super();
	}
	
	public SignLinePanel(int flagLinePosition) {
		super();
		this.flagLinePosition = flagLinePosition;
	}
	// HR本地化需求：重载了构造器 这样标志行可以用来分开收尾 end
	
	public String getNodekey()
	{
		return nodekey;
	}

	public void setNodekey(String nodekey)
	{
		this.nodekey = nodekey;
	}

	public BillManageModel getModel()
	{
		return model;
	}

	public void setModel(BillManageModel model)
	{
		this.model = model;
	}

	public AggHrIntfaceVO getAggVO()
	{
		return aggVO;
	}

	public void setAggVO(AggHrIntfaceVO aggVO)
	{
		this.aggVO = aggVO;

	}

	public void initValue()
	{
		if (getAggVO() == null)
		{
			return;
		}
		setDrawItemsCreator();
		getBillListPanel().setHeaderValueVO(new CircularlyAccessibleValueObject[]
		{ getAggVO().getParentVO() });
		
		// HR本地化改动：根据是否为首尾行将表体拆开
		CircularlyAccessibleValueObject[] bvos = getAggVO().getTableVO(DataIOconstant.HR_IFSETTOP);
		ArrayList<IfsettopVO> tempList = new ArrayList<IfsettopVO>(); 
		if (bvos == null) {
			return;
		}
		for (CircularlyAccessibleValueObject bvo : bvos) {
			IfsettopVO flagLineVO = (IfsettopVO) bvo;
			if (flagLineVO.getItoplineposition().equals(this.flagLinePosition)) {
				tempList.add(flagLineVO);
			}
		}
		getBillListPanel().setBodyValueVO(tempList.toArray(new CircularlyAccessibleValueObject[0]));
		getBillListPanel().getBodyBillModel().updateValue();
		convertDrawItem();
	}

	private void convertDrawItem()
	{
		// 得到数据内容里面的key，根据可以得到对应的
		// dataioItemvO
		UITable table = getBillListPanel().getBodyTable();
		// 得到所对应的列
		DefaultTableColumnModel model = (DefaultTableColumnModel) table.getColumnModel();
		int column = model.getColumnIndex(nameOfFldName);
		// int column2 = model.getColumnIndex(nameOfVcontent);

		/**
		 * 转换 字段名称 与 数据内容中的下拉项目
		 */
		for (int index = 0; index < table.getRowCount(); index++)
		{
			Object value = table.getValueAt(index, column);
			table.setValueAt(getDataioitem(value), index, column);
			// Object value2 = table.getValueAt(index, column2);
			// table.setValueAt(getDataioitem2(value2), index, column2);
		}

	}

	/**
	 * 为数据内容选择数据
	 *
	 * @param key
	 * @return
	 */
	protected Object getDataioitem(Object key)
	{
		for (int index = 0; index < vos.length; index++)
		{
			if (vos[index].getPrimaryKey().equals(key))
			{
				return vos[index];
			}
		}
		return key;
	}

	private DataIOAppModel getDataIOAppModel()
	{
		return (DataIOAppModel) model;
	}

	private AggHrIntfaceVO aggVO = null;

	private BillListPanel billListPanel = null;

	public void initUI()
	{
		this.setLayout(new BorderLayout());

		billListPanel = new BillListPanel();
		BillTempletVO template = null;

		billListPanel.setBillType(model.getContext().getNodeCode());
		billListPanel.setOperator(model.getContext().getPk_loginUser());
		billListPanel.setCorp(model.getContext().getPk_group());
		template = billListPanel.getDefaultTemplet(billListPanel.getBillType(), null, billListPanel.getOperator(), billListPanel.getCorp(), getNodekey(), null);

		if (template == null)
		{
			Logger.error("没有找到nodekey：" + nodekey + "对应的卡片模板");
			throw new IllegalArgumentException(ResHelper.getString("6013bnkitf","06013bnkitf0113")/*@res "没有找到设置的单据模板信息"*/);
		}

		BillListData bld = new BillListData(template);
		BillItem[] bodyItems = bld.getBodyItemsForTable(DataIOconstant.HR_IFSETTOP);
		for (int temp = 0; temp < bodyItems.length; temp++)
		{

			if (bodyItems[temp].getKey().equals(DataIOconstant.VCONTENT))
			{
				BillItem tempItem = bodyItems[temp];
				bodyItems[temp] = new DataIOComboxBillItem();
				bodyItems[temp].setKey(tempItem.getKey());
				bodyItems[temp].setDataType(IBillItem.COMBO);
				// bodyItems[temp].setDecimalDigits(tempItem.getDecimalDigits());
				bodyItems[temp].setLength(tempItem.getLength());
				bodyItems[temp].setName(tempItem.getName());
				this.nameOfFldName = tempItem.getName();
				bodyItems[temp].setWidth(tempItem.getWidth());
				bodyItems[temp].setEdit(true);
				bodyItems[temp].setShowOrder(tempItem.getShowOrder());
			}
			// //默认设置 数据类型与 序号不可以编辑
			// //项目分割符与补位符也不可编辑
			// if(bodyItems[temp].getKey().equals(DataIOconstant.ISEQ) ||
			// bodyItems[temp].getKey().equals(DataIOconstant.IFIELDTYPE)
			// ||bodyItems[temp].getKey().equals(DataIOconstant.VSEPARATOR)||bodyItems[temp].getKey().equals(DataIOconstant.VCARET)){
			// bodyItems[temp].setEdit(false);
			// }

		}

		// 重新设置bld。
		bld.setBodyItems(DataIOconstant.HR_IFSETTOP, bodyItems);
		billListPanel.setListData(bld);

		billListPanel.setEnabled(true);
		billListPanel.setBorder(null);
		billListPanel.getHeadTable().setBorder(null);

		// 不响应自动增行键盘事件
		billListPanel.getParentListPanel().setAutoAddLine(false);

		// // 可通过复选项多选
		// billListPanel.setMultiSelect(isMultiSelectionEnable());
		// // 和单据模板同步事件：复选框选择事件
		// billListPanel.getHeadBillModel().addRowStateChangeEventListener(this);

		// 和单据模板同步排序
		billListPanel.getHeadBillModel().addSortRelaObjectListener(model);

		// 和单据模板同步事件:选择当前行事件/编辑当前行事件
		billListPanel.addBodyEditListener(this);
		billListPanel.getChildListPanel().addEditListener2(this);

		billListPanel.getHeadTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 如果没有注射进来，则使用默认的Setter

		// billListPanelValueSetter = new VOBillListPanelValueSetter();
		billListPanel.getUISplitPane().getLeftComponent().setVisible(false);
		billListPanel.add(getLineWestPanel(), BorderLayout.EAST);
		this.add(billListPanel, BorderLayout.CENTER);
	}

	public void setDrawItemsCreator()
	{
		this.drawItemsCreator = new WaSignLineNameStrategy();
		this.vos = this.drawItemsCreator.getCorrespondingItems();
		BillPanelUtils.initComboBox(getBillListPanel(), IBillItem.BODY, DataIOconstant.HR_IFSETTOP, DataIOconstant.VCONTENT, this.vos, Boolean.FALSE);
	}

	public BillListPanel getBillListPanel()
	{
		return billListPanel;
	}

	public void setBillListPanel(BillListPanel billListPanel)
	{
		this.billListPanel = billListPanel;
	}

	private nc.ui.pub.beans.UIButton getLineAddBankRow()
	{

		if (bnLineAddBankRow == null)
		{
			try
			{
				bnLineAddBankRow = new nc.ui.pub.beans.UIButton();
				bnLineAddBankRow.setName("bnLineAddBankRow");
				bnLineAddBankRow.setText(ResHelper.getString("common","UC001-0000012")/*@res "增行"*/);

				bnLineAddBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				bnLineAddBankRow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{

						addLine(DataIOconstant.HR_IFSETTOP);

						initDefaultValue();
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return bnLineAddBankRow;
	}

	private nc.ui.pub.beans.UIButton getLineDelBankRow()
	{
		if (bnLineDelBankRow == null)
		{
			try
			{
				bnLineDelBankRow = new nc.ui.pub.beans.UIButton();
				bnLineDelBankRow.setName("bnLineDelBankRow");
				bnLineDelBankRow.setText(ResHelper.getString("common","UC001-0000013")/*@res "删行"*/);

				bnLineDelBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				bnLineDelBankRow.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						int rowCount = getBillListPanel().getBodyTable().getRowCount();
						if (row < 0)
						{
							return;
						}
						getBillListPanel().getBodyBillModel().delLine(new int[]
						{ row });
						// int[] rows =
						// getBodySelectedRows(DataIOconstant.WA_IFSETTOP);
						// if(rows.length>0){
						// deleteLine(DataIOconstant.WA_IFSETTOP);
						// TableUtil.setSelectedRow(getBillListPanel().getBodyTable(DataIOconstant.WA_IFSETTOP),rows[0]);
						// //row[0] 以后的行号随之变化
						delLineNum(row);
						row = row - 1;
						if (row < 0 && rowCount > 1)
						{
							row = 0;
						}
						if (row >= 0)
						{
							getBillListPanel().getBodyTable().setRowSelectionInterval(row, row);
						}
					}
				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return bnLineDelBankRow;
	}

	/**
	 * row 以后的行号减一
	 *
	 * @param row
	 */
	private void delLineNum(int row)
	{
		int count = getBillListPanel().getBodyBillModel().getRowCount();
		for (int i = row; i < count; i++)
		{
			getBillListPanel().getBodyBillModel().setValueAt(i + 1, i, DataIOconstant.ISEQ);
			// 设置row+1 与 row 行的状态
			getBillListPanel().getBodyBillModel().setRowState(i, BillModel.MODIFICATION);
		}
	}

	public void addLine(String strTableCode)
	{
		getBillListPanel().setEnabled(true);
		BillScrollPane headPanel = getBillListPanel().getBodyScrollPane(strTableCode);
		headPanel.addLine();

		//
		// BillModel billModel =
		// getBilllistPanel().getBodyBillModel(strTableCode);
		//
		// setFullTableEditable(billModel, getBodySelectedRow(strTableCode));
	}

	private void initDefaultValue()
	{
		int row = getBillListPanel().getBodyTable(DataIOconstant.HR_IFSETTOP).getSelectedRow();
		setBodyValueAt(DataIOconstant.ISEQ, row, row + 1);
		setBodyValueAt(IfsettopVO.VSEPARATOR, row, ItemSeprtorEnum.COMMA.value());//
		setBodyValueAt(IfsettopVO.ITOPLINEPOSITION, row, this.flagLinePosition);
		// setBodyValueAt(DataIOconstant.VFIELDNAME,row,getDrawItemsCreator().getCorrespondingItems()[0]);//
		// setBodyValueAt(DataIOconstant.IFLDWIDTHTOP,row,20);
		// setBodyValueAt(DataIOconstant.IFLDDECIMALTOP,row,0);
		// setBodyValueAt(DataIOconstant.ICARETPOSTOP,row,DataIOconstant.CARETPOS[0]);//
		// setBodyValueAt(DataIOconstant.IIFSUMTOP,row,0);//
	}

	public ModuleItemStrategy getDrawItemsCreator()
	{
		return drawItemsCreator;
	}

	private UIPanel getLineWestPanel()
	{
		if (buttonPanel == null)
		{
			buttonPanel = new UIPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			buttonPanel.add(getLineAddBankRow());
			buttonPanel.add(getLineDelBankRow());
			// buttonPanel.add(getbnUp());
			// buttonPanel.add(getbnDown());
		}
		return buttonPanel;
	}

	/**
	 * @author xuanlt on 2010-1-6
	 * @see nc.ui.pub.bill.BillEditListener#afterEdit(nc.ui.pub.bill.BillEditEvent)
	 */

	public void afterEdit(BillEditEvent billEditEvent)
	{
		// 得到Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		int changeRow = billEditEvent.getRow();
		Object obj = billEditEvent.getValue();
		String key = billEditEvent.getKey();

		if (key.equals(DataIOconstant.VCONTENT))
		{
			if (obj instanceof DataIOItemVO)
			{
				DataIOItemVO vo = (DataIOItemVO) obj;
				setBodyValueAt(DataIOconstant.IFLDWIDTHTOP, changeRow, vo.getIfldwidth());
				setBodyValueAt(DataIOconstant.IFLDDECIMALTOP, changeRow, vo.getIflddecimal());
				setBodyValueAt(DataIOconstant.VFIELDNAME, changeRow, "");
				setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "");
				setBodyValueAt(DataIOconstant.IIFSUMTOP, changeRow, 0);
			}
			if (obj.toString().equals(ResHelper.getString("6013bnkitf","06013bnkitf0070")/*@res "发放日期"*/))
			{
				// 设定值
				// billModel.getItemByKey(DataIOconstant.VFIELDNAME).setComponent(getStartDate());
				setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "YYYY-MM-DD");
			}
			else
			{ // 设定值
				// billModel.getItemByKey(DataIOconstant.VFIELDNAME).setComponent(new
				// UITextField());
				setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "");
			}

		}
		
		if(key.equals(DataIOconstant.VFIELDNAME)){
			if(obj instanceof UFLiteralDate){
				String date = ((UFLiteralDate)obj).toString();
				setBodyValueAt(DataIOconstant.VFIELDNAME, changeRow, date);
			}
		}
	}

	private UIRefPane getStartDate()
	{
		UIRefPane startDate = new UIRefPane();
		// startDate.setBounds(new Rectangle(120, 54, 100, 22));
		startDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
		return startDate;
	}

	public CircularlyAccessibleValueObject[] getIfsettopvo()
	{
		CircularlyAccessibleValueObject changedVOs[] = getBillListPanel().getBodyBillModel(DataIOconstant.HR_IFSETTOP).getBodyValueChangeVOs(IfsettopVO.class.getName());
		resetVOsState(changedVOs);
		return changedVOs;
	}

	protected void resetVOsState(CircularlyAccessibleValueObject childrenVO2[])
	{
		try
		{
			for (int i = 0; i < childrenVO2.length; i++)
			{
				// 没有主键，则是新增的数据
				if (childrenVO2[i].getPrimaryKey() == null && childrenVO2[i].getStatus() != nc.vo.pub.VOStatus.DELETED)
				{
					childrenVO2[i].setStatus(nc.vo.pub.VOStatus.NEW);
				}
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
		}
	}

	private void setBodyValueAt(String strItemKey, int iRowIndex, Object objValue)
	{
		// 得到Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		// 设定值
		billModel.setValueAt(objValue, iRowIndex, strItemKey);
	}

	/**
	 * @author xuanlt on 2010-1-6
	 * @see nc.ui.pub.bill.BillEditListener#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void bodyRowChange(BillEditEvent billEditEvent)
	{
	// 得到Billmodel
	// BillModel billModel = getBillListPanel().getBodyBillModel();
	// int changeRow = billEditEvent.getRow();
	//
	// String value = billModel.getValueAt(changeRow,
	// DataIOconstant.VCONTENT).toString();
	// if (value.toString().equals("发放日期"))
	// {
	// // 设定值
	// billModel.getItemByKey(DataIOconstant.VFIELDNAME).setComponent(getStartDate());
	// setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "YYYY-MM-DD");
	// }
	// else
	// { // 设定值
	// billModel.getItemByKey(DataIOconstant.VFIELDNAME).setComponent(new
	// UITextField());
	// setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "");
	// }
	}

	@Override
	public boolean beforeEdit(BillEditEvent billEditEvent)
	{
		int changeRow = billEditEvent.getRow();
		String key = billEditEvent.getKey();
		if (key.equals(IfsettopVO.VFIELDNAME))
		{
			int row = getBillListPanel().getBodyTable().getSelectedRow();
			Object obj = getBillListPanel().getBodyBillModel().getValueAt(row, IfsettopVO.VCONTENT);
			if (obj instanceof DataIOItemVO)
			{
				DataIOItemVO vo = (DataIOItemVO) obj;
				if (vo.getFldname().equals(DataIOconstant.PSNCOUNT))
				{
					return false;
				}
			}
		}

		if (getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			UITable table = getBillListPanel().getBodyTable();
			TableColumn tablecolumn = table.getColumn(ResHelper.getString("6013bnkitf","16013bnkitf0001")/*@res "列名"*/);
			Object obj = getBillListPanel().getBodyBillModel().getValueAt(changeRow, IfsettopVO.VCONTENT);
			if (obj == null)
			{
				return true;
			}
			if (obj.toString().equals(ResHelper.getString("6013bnkitf","06013bnkitf0070")/*@res "发放日期"*/))
			{
				// 设定值
				tablecolumn.setCellEditor(new BillCellEditor(getStartDate()));
				setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "YYYY-MM-DD");
			}
			else
			{ // 设定值
				tablecolumn.setCellEditor(new BillCellEditor(new UITextField()));
				setBodyValueAt(DataIOconstant.DATEFORMAT, changeRow, "");
			}
		}
		return true;
	}

}