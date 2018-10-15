package nc.ui.wa.datainterface.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.itf.IDisplayColumns;
import nc.ui.hr.datainterface.itf.INavigatee;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.border.UITitledBorder;
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
import nc.ui.wa.datainterface.model.WaDrawItemsStrategy;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.CaretposEnum;
import nc.vo.hr.datainterface.DataFromEnum;
import nc.vo.hr.datainterface.DataIOItemVO;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;

/**
 * 数据接口 第三步设置界面
 */
public class IOItemsPanel extends UIPanel implements BillEditListener, INavigatee, IDisplayColumns, ActionListener, ItemListener, BillEditListener2
{
	/**
	 * @author xuanlt on 2010-1-7
	 */
	private String nodekey = "dataio";
	private String lineKey = "dline";
	private BillManageModel model = null;

	private AggHrIntfaceVO aggVO = null;

	private String vcontentName = "";

	private DataIOItemVO[] vos = null;

	private BillListPanel billListPanel = null;

	private SignLinePanel signLinePanel = null;
	// HR本地化需求：多加一个标志行的配置界面
	private SignLinePanel signLinePanel2 = null;

	//	private UINavigator navigator = null;

	private UIPanel westPanel = null;

	protected ModuleItemStrategy drawItemsCreator = null;

	private UIButton ivjbnAddBankRow;
	private UIButton ivjbnDelBankRow;

	private UIButton ivjbnDown;
	private UIButton ivjbnUp;

	 /*数据来源——单一型*/
//	private static final String SINGLETYPE = "0";
	 /**数据来源——公式型*/
	 private static final String FORMULARTYPE = "1";
	//
	// /*数据类型*/
	// private static final int STRINGTYPE = 0;
	// private static final int DECIMALTYPE = 1;
	// private static final int BOOLEANTYPE = 2;
	// private static final int DATETYPE = 3;

	// private static final IConstEnum[] DATASOURCE =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(SINGLETYPE, "单一型"),
	// new DefaultConstEnum(FORMULARTYPE, "公式型")};
	// private static final IConstEnum[] FIELDTYPE =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(STRINGTYPE, "字符型 "),
	// new DefaultConstEnum(DECIMALTYPE, "数字型"),
	// new DefaultConstEnum(BOOLEANTYPE, "布尔型 "),
	// new DefaultConstEnum(DATETYPE, "日期型"),
	// };

	// 0-不补 1-补前 2-补后
	// UIComboBox cmb = new UIComboBox(new String[] {"不补","补前", "补后"});

	public IOItemsPanel(BillManageModel appModel, AggHrIntfaceVO aggVO)
	{
		super();
		this.model = appModel;
		this.aggVO = aggVO;
		initUI();
		initValue();
	}

	public void initUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(createItemsPanel());
		if (needSignLinePanel())
		{
			SignLinePanel spanel = getSignLinePanel();
			spanel.setModel(getModel());
			spanel.initUI();
			add(spanel);
			
			// HR本地化需求：多加一个标志行的配置面板
			SignLinePanel spanel2 = getSignLinePanel2();
			spanel2.setModel(getModel());
			spanel2.initUI();
			add(spanel2);
		}

		WaDrawItemsStrategy drawitem = new WaDrawItemsStrategy(model);

		// WaLoginContext context = (WaLoginContext)model.getContext();
		// drawitem.setContext(context);

		setDrawItemsCreator(drawitem);
		javax.swing.JScrollPane scrollPane = getBillListPanel().getBodyTabbedPane().getSelectedScrollPane();
		if (scrollPane instanceof BillScrollPane)
		{
			BillScrollPane billScrollPane = (BillScrollPane) scrollPane;
			billScrollPane.addEditListener(this);
			billScrollPane.addEditListener2(this);
		}
		// initColumnEditor();

		// //允许修改
		// setFullTableEdit(true);
		// setFullTableEditable(getBillListPanel().getBodyBillModel(), 0);
		// setName(DataIOconstant.IOITEMSPANEL);
		// initConnection();

	}

	public CircularlyAccessibleValueObject[] getFormatsVO()
	{
		CircularlyAccessibleValueObject changedVOs[] = getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).getBodyValueChangeVOs(FormatItemVO.class.getName());
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
				if (childrenVO2[i].getPrimaryKey() == null )
				{
					if(childrenVO2[i].getStatus() == nc.vo.pub.VOStatus.DELETED){
						childrenVO2[i].setStatus(nc.vo.pub.VOStatus.UNCHANGED);
					}else{
						childrenVO2[i].setStatus(nc.vo.pub.VOStatus.NEW);
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
		}
	}

	public CircularlyAccessibleValueObject[] getIfsettopvo()
	{
		if (needSignLinePanel())
		{
			return getSignLinePanel().getIfsettopvo();
		}
		else
		{
			return new IfsettopVO[0];
		}
	}

	public UIPanel createItemsPanel()
	{
		UIPanel panel = new UIPanel();
		panel.setBorder(new UITitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0112")/*@res "项目及格式设置"*/));
		panel.setLayout(new BorderLayout());
		billListPanel = getBillListPanel();
		BillListData bld = billListPanel.getBillListData();
		BillItem[] bodyItems = bld.getBodyItemsForTable(DataIOconstant.HR_DATAINTFACE_B);
		if (bodyItems != null)
		{
			for (int temp = 0; bodyItems != null && temp < bodyItems.length; temp++)
			{
				/**
				 * 重置数据内容
				 */
				if (bodyItems[temp].getKey().equals(DataIOconstant.VCONTENT))
				{
					BillItem tempItem = bodyItems[temp];
					this.vcontentName = tempItem.getName();
					bodyItems[temp] = new DataIOComboxBillItem();
					bodyItems[temp].setKey(tempItem.getKey());
					bodyItems[temp].setDataType(IBillItem.COMBO);
					// bodyItems[temp].setDecimalDigits(tempItem.getDecimalDigits());
					bodyItems[temp].setLength(tempItem.getLength());
					int i = tempItem.getForeground();
					bodyItems[temp].setName(tempItem.getName());
					bodyItems[temp].setForeground(i);
					bodyItems[temp].setWidth(tempItem.getWidth());
					bodyItems[temp].setEdit(true);
					bodyItems[temp].setShowOrder(tempItem.getShowOrder());
				}
				// 默认设置 数据类型与 序号不可以编辑
				// 项目分割符与补位符也不可编辑
				if (bodyItems[temp].getKey().equals(DataIOconstant.ISEQ) || bodyItems[temp].getKey().equals(DataIOconstant.IFIELDTYPE) || bodyItems[temp].getKey().equals(DataIOconstant.VSEPARATOR) || bodyItems[temp].getKey().equals(DataIOconstant.VCARET))
				{
					bodyItems[temp].setEdit(false);
				}
				if (model.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)&&bodyItems[temp].getKey().equals(DataIOconstant.VCONTENT) ) {
					bodyItems[temp].setEdit(false);
				}

			}
			
			
			
			
			
			for (int i = 0; i < bodyItems.length; i++) {
				if(bodyItems[i].getKey().equals("icaretpos")){
					if(bodyItems[i].getComponent() instanceof UIComboBox)
						((UIComboBox) bodyItems[i].getComponent()).removeItemAt(0);
				}
			}
			
			// 重新设置bld。
			bld.setBodyItems(DataIOconstant.HR_DATAINTFACE_B, bodyItems);
			
			
		}

		billListPanel.setListData(bld);
		billListPanel.addBodyEditListener(this);

		billListPanel.getUISplitPane().getLeftComponent().setVisible(false);
		panel.add(billListPanel);
		if (!model.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)) {
			panel.add(getWestPanel(), BorderLayout.EAST);
		}
		return panel;
	}

	public BillListPanel getBillListPanel()
	{
		if (billListPanel == null)
		{
			billListPanel = new BillListPanel();

			billListPanel.setBillType(model.getContext().getNodeCode());
			billListPanel.setOperator(model.getContext().getPk_loginUser());
			billListPanel.setCorp(model.getContext().getPk_group());
			BillTempletVO template = billListPanel.getDefaultTemplet(billListPanel.getBillType(), null, billListPanel.getOperator(), billListPanel.getCorp(), getNodekey(), null);

			if (template == null)
			{
				Logger.error("没有找到nodekey：" + nodekey + "对应的卡片模板");
				throw new IllegalArgumentException(ResHelper.getString("6013bnkitf","06013bnkitf0113")/*@res "没有找到设置的单据模板信息"*/);
			}

			billListPanel.setListData(new BillListData(template));

			billListPanel.setEnabled(true);
			billListPanel.setBorder(null);
			billListPanel.getHeadTable().setBorder(null);

			// 不响应自动增行键盘事件
			billListPanel.getParentListPanel().setAutoAddLine(false);

			// 和单据模板同步排序
			billListPanel.getHeadBillModel().addSortRelaObjectListener(model);

			billListPanel.getHeadTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// billlistPanel.set

		}
		//20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		BillPanelUtils.disabledRightMenuAndAutoAddLine(billListPanel);
		return billListPanel;

	}

	public void initValue()
	{
		if (getAggVO() == null)
		{
			return;
		}

		getBillListPanel().setHeaderValueVO(new CircularlyAccessibleValueObject[]
				{ getAggVO().getParentVO() });

		// getBilllistPanel().getHeadBillModel().addLine();
		// getBilllistPanel().getHeadBillModel().setBodyRowObjectByMetaData(getAggVO().getParentVO(),
		// 0);

		// 参照显示主键问题
		// getBillListPanel().getHeadBillModel().loadLoadRelationItemValue();

		getBillListPanel().setBodyValueVO(getAggVO().getTableVO(DataIOconstant.HR_DATAINTFACE_B));
		getBillListPanel().getBodyBillModel().updateValue();

		convertDrawItem();

		// 处理统一的项目。统一的项目分割符，统一的补位符。如果不统一，则进一步区分数据接口与其他功能结点
		setUniform();
		setColumEnable();

		if (needSignLinePanel())
		{
			getSignLinePanel().setAggVO(getAggVO());
			getSignLinePanel().initValue();
		}
	}

	public void setDrawItemsCreator(ModuleItemStrategy itemsCreator)
	{
		this.drawItemsCreator = itemsCreator;
		this.vos = itemsCreator.getCorrespondingItems();
		BillPanelUtils.initComboBox(getBillListPanel(), IBillItem.BODY, DataIOconstant.HR_DATAINTFACE_B, DataIOconstant.VCONTENT, this.vos, Boolean.FALSE);
	}

	private void convertDrawItem()
	{
		// 得到数据内容里面的key，根据可以得到对应的,得到所对应的列
		UITable table = getBillListPanel().getBodyTable();
		DefaultTableColumnModel model = (DefaultTableColumnModel) table.getColumnModel();

		// 得到vcontent所对应的列
		int column = model.getColumnIndex(this.vcontentName);
		for (int index = 0; index < table.getRowCount(); index++)
		{
			Object value = table.getValueAt(index, column);
			table.setValueAt(getDataioitem(value), index, column);
		}
	}

	protected Object getDataioitem(Object key)
	{
		for (int index = 0; vos != null && index < vos.length; index++)
		{
			if (vos[index] != null && vos[index].getPrimaryKey() != null)
			{
				if (vos[index].getPrimaryKey().equals(key))
				{
					return vos[index];
				}
			}
		}
		return key;
	}

	private void setUniform()
	{
		UITable table = getBillListPanel().getBodyTable();
		HrIntfaceVO vo = getHeaderVO();
		FormatItemVO[] formatitemvos = (FormatItemVO[]) getAggVO().getTableVO(DataIOconstant.HR_DATAINTFACE_B);
 
		boolean isEditSepa = vo.getIifseparator().equals(BooleanEnum.YES.value());
		boolean isEditCaret = vo.getIifcaret().equals(BooleanEnum.YES.value());
		LoginContext context = this.model.getContext();
		for (int index = 0; index < table.getRowCount(); index++)
		{
			/* 设置每一行都为修改标识 */
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(index, BillModel.MODIFICATION);

			if (isEditSepa)
			{
				// 使用项目分割符
//				setBodyValueAt(DataIOconstant.VSEPARATOR,index,DataIOconstant.ITEMSEPERATOR.get(getHeaderVO().getIseparator()));//
				setBodyValueAt(DataIOconstant.VSEPARATOR, index, getHeaderVO().getIseparator());//
			}
			else
			{
				// 不 统一使用项目分割符 。数据接口就采用empty
				if (context.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
				{
					setBodyValueAt(DataIOconstant.VSEPARATOR, index,formatitemvos[index].getVseparator());
				}
			}

			if (isEditCaret)
			{
				// 统一使用补位符。补位位置统一是前补位
				setBodyValueAt(DataIOconstant.ICARETPOS, index, CaretposEnum.BEFORE.value());
				// 根据数据类型确定补位符

				Integer fieldType = formatitemvos[index].getIfieldtype();
				if (fieldType.intValue() == (Integer) FieldTypeEnum.DEC.value())
				{
					setBodyValueAt(DataIOconstant.VCARET, index, "0");//
				}
				else
				{
					setBodyValueAt(DataIOconstant.VCARET, index, " ");//
				}
			}
			else
			{
				// 不 统一使用项目分割符 。数据接口就不补（CARETPOS[0]）
				if (context.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
				{
					// setBodyValueAt(DataIOconstant.ICARETPOS, index,
					// CaretposEnum.NO.value());

				}
			}
		}
	}

	// 根据是否统一使用项目分割符，与 是否统一使用补位符 来设置“项目分割符”与“补位符”是否可以编辑
	private void setColumEnable()
	{
		HrIntfaceVO vo = getHeaderVO();
		if (vo != null)
		{
			boolean isEditSepa = vo.getIifseparator().equals(BooleanEnum.NO.value());
			getBillListPanel().getBodyItem(DataIOconstant.VSEPARATOR).setEdit(isEditSepa);

			boolean isEditCaret = vo.getIifcaret().equals(BooleanEnum.NO.value());
			getBillListPanel().getBodyItem(FormatItemVO.VCARET).setEdit(isEditCaret);
			getBillListPanel().getBodyItem(FormatItemVO.ICARETPOS).setEdit(isEditCaret);
		}
	}

	public String getNodekey()
	{
		return nodekey;
	}

	public void setNodekey(String nodekey)
	{
		this.nodekey = nodekey;
	}

	public String getLineKey()
	{
		return lineKey;
	}

	public void setLineKey(String lineKey)
	{
		this.lineKey = lineKey;
	}

	protected UIPanel getTailPanel()
	{

		return null;
	}

	protected void initConnection()
	{

	}

	//	public UINavigator getNavigator()
	//	{
	//		return navigator;
	//	}
	//
	//	public void setNavigator(UINavigator navigator)
	//	{
	//		this.navigator = navigator;
	//	}

	private boolean needSignLinePanel()
	{
		return this.getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK);

	}

	public SignLinePanel getSignLinePanel()
	{
		if (signLinePanel == null)
		{
			signLinePanel = new SignLinePanel();
		}
		return signLinePanel;
	}
	
	// HR本地化需求：多加一个标志行配置
	public SignLinePanel getSignLinePanel2() 
	{
		if (signLinePanel2 == null)
		{
			signLinePanel2 = new SignLinePanel();
		}
		return signLinePanel2;
	}

	public void actionPerformed(ActionEvent e)
	{

		//
		// if(e.getSource() == getUIBnTxtPre2()){
		// //根据接口文件类型决定上一步
		// int fielType = getHeaderVO().getIfiletype();
		// if(fielType == DataIOconstant.TXTFILE){
		// navigator.ShowPrePanel();
		// }else if(fielType == DataIOconstant.XLSFILE){
		// navigator.show(DataIOconstant.BASEINFPANEL);
		// }
		//
		// }else if (e.getSource() == getUIBnTxtCancel2()){
		// navigator.ShowCancelPanel();
		// }else if (e.getSource() == getUIBnTxtOK()){
		// try {
		// Object obj = getValueChangedData();
		// getParentUI().getDataModel().onSave(obj);
		// ((DataIOTemplateUI)getParentUI()).refreshData();
		// navigator.ShowOKPanel();
		// } catch (Exception ept) {
		// getParentUI().showErrorMessage(ept.getMessage());
		// Logger.error(ept.getMessage(),ept);
		// }
		// }

	}

	private UIPanel getWestPanel() {
		if (westPanel == null) {
			westPanel = new UIPanel();
			westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
			if (!getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)) {
				westPanel.add(getbnAddBankRow());
				westPanel.add(getbnDelBankRow());
			}
			westPanel.add(getbnUp());
			westPanel.add(getbnDown());
		}
		return westPanel;
	}

	private nc.ui.pub.beans.UIButton getbnAddBankRow()
	{
		if (ivjbnAddBankRow == null)
		{
			try
			{
				ivjbnAddBankRow = new nc.ui.pub.beans.UIButton();
				ivjbnAddBankRow.setName("bnAddBankRow");
				ivjbnAddBankRow.setText(ResHelper.getString("common","UC001-0000012")/*@res "增行"*/);

				ivjbnAddBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnAddBankRow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{

						addLine(DataIOconstant.HR_DATAINTFACE_B);

						initDefaultValue();
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnAddBankRow;
	}

	private nc.ui.pub.beans.UIButton getbnDelBankRow()
	{
		if (ivjbnDelBankRow == null)
		{
			try
			{
				ivjbnDelBankRow = new nc.ui.pub.beans.UIButton();
				ivjbnDelBankRow.setName("bnDelBankRow");
				ivjbnDelBankRow.setText(ResHelper.getString("common","UC001-0000013")/*@res "删行"*/);

				ivjbnDelBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnDelBankRow.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						// getBilllistPanel()
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
		return ivjbnDelBankRow;
	}

	public void moveUp(String strCode, int row)
	{
		if (row > 0 && row <= getBillListPanel().getBodyBillModel(strCode).getRowCount() - 1)
		{
			BillModel billmodel = getBillListPanel().getBodyBillModel(strCode);
			billmodel.moveRow(row, row, row - 1);
			getBillListPanel().getBodyTable().getSelectionModel().setSelectionInterval(row - 1, row - 1);

			// 设置行号
			billmodel.setValueAt(row, row - 1, DataIOconstant.ISEQ);
			billmodel.setValueAt(row + 1, row, DataIOconstant.ISEQ);
			// 设置row-1 与 row 行的状态

			String pk = (String) billmodel.getValueAt(row - 1, FormatItemVO.PK_DATAINTFACE_B);

			int vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row - 1, vostatus);

			pk = (String) billmodel.getValueAt(row, FormatItemVO.PK_DATAINTFACE_B);
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row, vostatus);

		}
	}

	public void moveDown(String strCode, int row)
	{
		if (row >= 0 && row <= getBillListPanel().getBodyBillModel(strCode).getRowCount() - 2)
		{
			BillModel billmodel = getBillListPanel().getBodyBillModel(strCode);
			billmodel.moveRow(row, row, row + 1);
			getBillListPanel().getBodyTable().getSelectionModel().setSelectionInterval(row + 1, row + 1);
			// 设置行号
			billmodel.setValueAt(row + 2, row + 1, DataIOconstant.ISEQ);
			billmodel.setValueAt(row + 1, row, DataIOconstant.ISEQ);

			String pk = (String) billmodel.getValueAt(row + 1, FormatItemVO.PK_DATAINTFACE_B);

			int vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			// 设置row+1 与 row 行的状态
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row + 1, vostatus);

			pk = (String) billmodel.getValueAt(row, FormatItemVO.PK_DATAINTFACE_B);

			vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}

			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row, vostatus);

		}
	}

	public void addLine(String strTableCode)
	{
		getBillListPanel().getBodyBillModel().setEnabled(true);
		BillScrollPane headPanel = getBillListPanel().getBodyScrollPane(strTableCode);
		headPanel.addLine();

		//
		// BillModel billModel =
		// getBilllistPanel().getBodyBillModel(strTableCode);
		//
		// setFullTableEditable(billModel, getBodySelectedRow(strTableCode));
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

	/**
	 * 初始化每一行的公共数据 序号 、类型(字符型)、长度（20）、小数位数（0）、数据来源（公式型） 如果统一项目分隔符（），则初始统一项目分隔符
	 *
	 *如果使用统一补位，则补位位置默认补前，默认补空格 。不使用统一补位符则 不补
	 */
	private void initDefaultValue()
	{
		int row = getBillListPanel().getBodyTable(DataIOconstant.HR_DATAINTFACE_B).getSelectedRow();
		setBodyValueAt(DataIOconstant.ISEQ, row, row + 1);
		setBodyValueAt(DataIOconstant.IFIELDTYPE, row, FieldTypeEnum.STR.value());//
		setBodyValueAt(DataIOconstant.IFLDWIDTH, row, 20);
		setBodyValueAt(DataIOconstant.IFLDDECIMAL, row, 0);
		// setBodyValueAt(DataIOconstant.ICARETPOS, row,
		// CaretposEnum.NO.value());//
		setBodyValueAt(DataIOconstant.ISOURCETYPE, row, DataFromEnum.FORMULAR.value());//
		HrIntfaceVO vo = getHeaderVO();

		if (vo != null)
		{
			// 统一使用项目分隔符
			if (vo.getIifseparator().intValue() == 1)
			{
				setBodyValueAt(DataIOconstant.VSEPARATOR, row, vo.getIseparator());//
			}
			// 统一使用补位符
			if (vo.getIifcaret().intValue() == 1)
			{
				setBodyValueAt(DataIOconstant.ICARETPOS, row, CaretposEnum.BEFORE.value());//
				setBodyValueAt(DataIOconstant.VCARET, row, " ");//
			}
		}
	}

	private void setBodyValueAt(String strItemKey, int iRowIndex, Object objValue)
	{
		// 得到Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		// 设定值
		billModel.setValueAt(objValue, iRowIndex, strItemKey);
	}

	private Object getBodyValueAt(String strItemKey, int iRowIndex)
	{
		// 得到Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		// 设定值
		return billModel.getValueAt(iRowIndex, strItemKey);
	}

	protected HrIntfaceVO getHeaderVO()
	{
		if (getAggVO() == null)
		{
			return null;
		}
		return (HrIntfaceVO) getAggVO().getParentVO();
	}

	/**
	 * 返回 bnDown 特性值。
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIButton getbnDown()
	{
		if (ivjbnDown == null)
		{
			try
			{
				ivjbnDown = new nc.ui.pub.beans.UIButton();
				ivjbnDown.setName("bnDown");
				ivjbnDown.setText(ResHelper.getString("6013bnkitf","06013bnkitf0114")/*@res "下移"*/);

				ivjbnDown.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						if (row < 0)
						{
							return;
						}
						moveDown(DataIOconstant.HR_DATAINTFACE_B, row);
						// moveDown(
						// getBodySelectedRow(DataIOconstant.HR_DATAINTFACE_B));
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnDown;
	}

	private nc.ui.pub.beans.UIButton getbnUp()
	{
		if (ivjbnUp == null)
		{
			try
			{
				ivjbnUp = new nc.ui.pub.beans.UIButton();
				ivjbnUp.setName("bnUp");
				ivjbnUp.setText(ResHelper.getString("6013bnkitf","06013bnkitf0115")/*@res "上移"*/);
				ivjbnUp.addActionListener(this);
				ivjbnUp.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						if (row < 0)
						{
							return;
						}
						moveUp(DataIOconstant.HR_DATAINTFACE_B, row);
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnUp;
	}

	public BillManageModel getModel()
	{
		return model;
	}

	public void setModel(BillManageModel model)
	{
		this.model = model;

	}

	public ModuleItemStrategy getDrawItemsCreator()
	{
		return drawItemsCreator;
	}

	// public void setDrawItemsCreator(ModuleItemStrategy itemsCreator) {
	// this.drawItemsCreator = itemsCreator;
	// this.drawItemsCreator.getCorrespondingItems();
	//
	// // //name
	// // ConstEnumFactory<T> factory= new ConstEnumFactory();
	//
	// // initComboBox(IBillItem.BODY, DataIOconstant.VCONTENT,
	// this.drawItemsCreator.getCorrespondingItems(), Boolean.FALSE);
	// }

	/**
	 * 数据内容改变时，以下列会随之改变 列名、类型、长度、小数位数。 如果使用统一补位符，则补位符也会随之改变(数字型补零、其他类型补空格)。
	 *
	 * @return
	 */
	@Override
	public void afterEdit(BillEditEvent billEditEvent)
	{

		if (billEditEvent.getKey().equals(DataIOconstant.ISOURCETYPE))
		{

			// clearValue();
			Integer dataFrom = (Integer) billEditEvent.getValue();
			UITable table = getBillListPanel().getBodyTable();
			// getBilllistPanel().getBodyItem("").setItemEditor(itemEditor)
			TableColumn tablecolumn = table.getColumnModel().getColumn(1);
			// TableColumn tablecolumn =
			// table.getColumn(DataIOconstant.VCONTENT); //hardcode
			if (dataFrom.equals(DataFromEnum.FORMULAR.value()))
			{
				// 为选定单元格设置编辑器
				UIComboBox box = new UIComboBox();
				box.addItems(this.vos);
				tablecolumn.setCellEditor(new BillCellEditor(box));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(false);
			}
			else
			{
				tablecolumn.setCellEditor(new BillCellEditor(new UITextField()));
				// 设置编辑器
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(true);
			}

			setBodyValueAt(DataIOconstant.VFIELDNAME, billEditEvent.getRow(), "");
			setBodyValueAt(DataIOconstant.VCONTENT, billEditEvent.getRow(), "");
		}

		Object obj = billEditEvent.getValue();
		if (obj instanceof DataIOItemVO)
		{
			DataIOItemVO vo = (DataIOItemVO) obj;
			setBodyValueAt(DataIOconstant.VFIELDNAME, billEditEvent.getRow(), vo.getVname());

			// setBodyValueAt(DataIOconstant.VCONTENT,billEditEvent.getRow(),vo.getPrimaryKey());

			setBodyValueAt(DataIOconstant.IFIELDTYPE, billEditEvent.getRow(), vo.getIitemtype());
			setBodyValueAt(DataIOconstant.IFLDWIDTH, billEditEvent.getRow(), vo.getIfldwidth());
			setBodyValueAt(DataIOconstant.IFLDDECIMAL, billEditEvent.getRow(), vo.getIflddecimal());

			HrIntfaceVO headerVO = getHeaderVO();
			// 统一使用补位符
			if (headerVO.getIifcaret().intValue() == 1)
			{
				String caret = " ";
				if (vo.getIitemtype() != 0)
				{// 非数字型
					caret = "0";
				}
				setBodyValueAt(DataIOconstant.ICARETPOS, billEditEvent.getRow(), CaretposEnum.BEFORE.value());//
				setBodyValueAt(DataIOconstant.VCARET, billEditEvent.getRow(), caret);//
			}
		}

	}




	public AggHrIntfaceVO getAggVO()
	{
		return aggVO;
	}

	public void setAggVO(AggHrIntfaceVO aggVO)
	{
		this.aggVO = aggVO;
	}

	/**
	 * @author xuanlt on 2009-12-30
	 * @see nc.ui.pub.bill.BillEditListener#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void bodyRowChange(BillEditEvent billEditEvent)
	{}

	public boolean beforeEdit(BillEditEvent billEditEvent)
	{
		if (getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			UITable table = getBillListPanel().getBodyTable();
			TableColumn tablecolumn = table.getColumn(vcontentName);
			int currentRow = billEditEvent.getRow();
			
			//NCdp205527341  直接比较字符串值可能会出现多语问题,改为比较类型.   0为单一型,1为公式型   ---lizt
//			Object isourcetype = getBillListPanel().getBodyBillModel().getValueAt(currentRow, DataIOconstant.ISOURCETYPE);
//			if (isourcetype.toString().trim().equals(ResHelper.getString("6013bnkitf","06013bnkitf0116")/*@res "公式型"*/))
			
			Map<String, Object>[] valueMap = getBillListPanel().getBodyBillModel().getBodyChangeValueByMetaData();
			Object isourcetype = valueMap[currentRow].get(DataIOconstant.ISOURCETYPE);
			if(isourcetype.toString().trim().equals(FORMULARTYPE))
			{
				UIComboBox box = new UIComboBox();
				box.addItems(vos);
				tablecolumn.setCellEditor(new BillCellEditor(box));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(false);
			}
			else
			{
				tablecolumn.setCellEditor(new BillCellEditor(new UITextField()));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(true);
			}
		}
		return true;
	}

	/**
	 * @author xuanlt on 2010-1-8
	 * @see nc.ui.hr.datainterface.itf.IDisplayColumns#getBillItems()
	 */
	@Override
	public String[] getBillItems()
	{
		return null;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}

}