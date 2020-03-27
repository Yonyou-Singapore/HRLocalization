package nc.ui.wa.datainterface.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.ref.WaClassRefModel;
import nc.vo.hr.datainterface.FileTypeEnum;
import nc.vo.hr.datainterface.ItemSeprtorEnum;
import nc.vo.hr.datainterface.LineTopEnum;
import nc.vo.hr.datainterface.LineTopPositionEnum;
import nc.vo.hr.datainterface.RelatedItemEnum;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.DataIOconstant;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author xuanlt
 */
public class ParaInfPanel extends UIPanel
{

	/**
	 * @author xuanlt on 2010-1-5
	 */
	private AbstractUIAppModel model = null;

	private UIComboBox relatedItem;
	private UICheckBox usedItemSeparatorChb;
	private UIComboBox itemSeparatorComb;

	private UICheckBox usedPlaceholderChb;

	private UICheckBox kiloSeprtChb;

	private UICheckBox dotChb;
	private UICheckBox outputTableHeadChb;
	private UICheckBox headSameToBodyChb;
	private UICheckBox linetopSetChb;
	private UICheckBox linetopSetChb2;
	private UIComboBox linetopSetComb;
	private UIComboBox linetopSetComb2;
	private UIComboBox linetopPosiComb;
	private UIComboBox linetopPosiComb2;

	private UITextField formatNameTextField;
	private UIComboBox fileTypeCombox;
	private UITextArea memTexttArea;

	private UIPanel dataParaPanel;

	private UIPanel rowPanel;

	private UIPanel tablePanel;

	private UIRefPane bankRefPane;
	
	private UIRefPane salaryschemaRefPane;
	private WaClassRefModel classRefModel = null;
	private UITextArea filenameSettingTextArea;
	private UICheckBox displayStopSalary;

	public ParaInfPanel(AbstractUIAppModel appModel)
	{
		this.model = appModel;
		init();
	}

	private UIPanel buildGeneralPanel()
	{
		UIPanel panel = new UIPanel();

		// FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref",
		// "");
		FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, panel);
		builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0120")/*@res "常规"*/));

		if (getAppModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0082")/*@res "报送银行"*/, getBankRefPane());
			//UILabel notNullLabel = new UILabel();
		//	notNullLabel.setText("*");
		//	notNullLabel.setForeground(Color.red);
		//	builder.append(notNullLabel);
			builder.nextLine();
		}
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0072")/*@res "接口名称"*/, getFormatNameTextField());
		builder.nextLine();
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0073")/*@res "文件类型"*/, getFileTypeCombox());
		builder.nextLine();
		// builder.append("备注", getMemTextArea(),5);
		builder.append(ResHelper.getString("common","UC000-0001376")/*@res "备注"*/, getMemTextArea());
		//增加薪资方案过滤及文件名称设置  add by weiningc 20200213 start
		if (getAppModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK)) {
			builder.nextLine();
			builder.append("Salary Schema", getSalarySchemaRefPane());
			builder.nextLine();
			builder.append("Export File Name", getFilenameSettingTextArea());
			//增加是否查询停薪人员 add by weiningc 20200302 start
			builder.nextLine();
			builder.append("Employee stop payment ", getDisplayStopSalaryPanel());
		}
		//end
		

		return (UIPanel) builder.getPanel();
	}

	public UICheckBox getDisplayStopSalaryPanel() {
		if (displayStopSalary == null)
		{
			displayStopSalary = new UICheckBox();
			displayStopSalary.setPreferredSize(new Dimension(200, 20));
		}
		return displayStopSalary;
	}

	public UIRefPane getSalarySchemaRefPane() {
		if (salaryschemaRefPane == null)
		{
			salaryschemaRefPane = new UIRefPane();
			salaryschemaRefPane.setVisible(true);
			salaryschemaRefPane.setPreferredSize(new Dimension(200, 20));
			//			waClassRefPane.setBounds(new Rectangle(x+100, 5, 122, 20));
			//waClassRefPane.setSize(new Dimension(200,20));
			salaryschemaRefPane.setButtonFireEvent(true);
			WaClassRefModel refmodel = getClassRefModel();
			refmodel.setPk_org(getAppModel().getContext().getPk_org());
			salaryschemaRefPane.setMultiSelectedEnabled(true);//可多选
			salaryschemaRefPane.setRefModel(refmodel);
		}
		return salaryschemaRefPane;
	}
	
	public UITextArea getFilenameSettingTextArea()
	{
		if (filenameSettingTextArea == null)
		{
			filenameSettingTextArea = new UITextArea();
			filenameSettingTextArea.setName("filenameSettingTextArea");
			// memTexttArea.setPreferredSize(new Dimension(200,80));
			filenameSettingTextArea.setPreferredSize(new Dimension(200, 20));
			filenameSettingTextArea.setLineWrap(true);
			filenameSettingTextArea.setMaxLength(1024);
		}
		return filenameSettingTextArea;
	}
	
	/**
	 * @author zhangg on 2009-11-24
	 * @return the classRefModel
	 */
	public WaClassRefModel getClassRefModel()
	{

		if (classRefModel == null)
		{
			classRefModel = new nc.ui.wa.ref.WaClassRefModel();
			//			classRefModel.setRefNodeName("薪资方案");
		}
		return classRefModel;

	}

	private UIPanel buildDataParaPanel()
	{
		dataParaPanel = new UIPanel();

		FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, dataParaPanel);
		builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0121")/*@res "数据参数"*/));

		//builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0122")/*@res "关联项目"*/, getRelatedItemComb());
		//builder.nextLine();
		builder.append("", getUsedItemSeparatorChb());
		builder.append("", getItemSeparatorComb());
		builder.nextLine();
		builder.append("", getUsedPlaceholderChb());
		UILabel label = new UILabel(ResHelper.getString("6013bnkitf","06013bnkitf0123")/*@res "数字前补零，字符前补空格"*/);
		builder.append("        ", label);
		builder.nextLine();
		builder.append("", getKiloSeprtChb());
		builder.append("        ", getDotChb());
		builder.nextLine();
		dataParaPanel = (UIPanel) builder.getPanel();
		return dataParaPanel;
	}

	private UIPanel buildTableHeadPanel()
	{
		tablePanel = new UIPanel();
		FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, tablePanel);
		builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0124")/*@res "表头设置"*/));

		builder.append("                ", getOutputTableHeadChb());
		builder.append("        ", getHeadSameToBodyChb());
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0122")/*@res "关联项目"*/, getRelatedItemComb());
		tablePanel = (UIPanel) builder.getPanel();
		return tablePanel;
	}

	private UIPanel buildRowPanel()
	{
		rowPanel = new UIPanel();

		FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref, 4dlu," + "right:pref, 2dlu, left:pref", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, rowPanel);
		builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0098")/*@res "标志行"*/));

		builder.append("                ", getLinetopSetChb());
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0125")/*@res "         位置"*/, getLinetopPosiComb());
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0126")/*@res "输出"*/, getLinetopSetComb());
		builder.nextLine();
		builder.append("                ", getLinetopSetChb2());
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0125")/*@res "         位置"*/, getLinetopPosiComb2());
		builder.append(ResHelper.getString("6013bnkitf","06013bnkitf0126")/*@res "输出"*/, getLinetopSetComb2());

		rowPanel = (UIPanel) builder.getPanel();
		return rowPanel;
	}

	private void init()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildGeneralPanel());
		add(buildTableHeadPanel());
		add(buildDataParaPanel());
		add(buildRowPanel());
		fileTypeCombox.setSelectedIndex(1);
	}

	public AbstractUIAppModel getAppModel()
	{
		return model;
	}

	public void setAppModel(AbstractUIAppModel model)
	{
		this.model = model;
	}

	public void initValue()
	{}

	public UICheckBox getUsedItemSeparatorChb()
	{
		if (usedItemSeparatorChb == null)
		{
			usedItemSeparatorChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0127")/*@res "使用统一的项目分隔符"*/);
			usedItemSeparatorChb.setPreferredSize(new Dimension(200, 20));
		}
		return usedItemSeparatorChb;
	}

	public UICheckBox getUsedPlaceholderChb()
	{
		if (usedPlaceholderChb == null)
		{
			usedPlaceholderChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0128")/*@res "使用统一的补位符"*/);
			usedPlaceholderChb.setPreferredSize(new Dimension(200, 20));
		}
		return usedPlaceholderChb;
	}

	public UICheckBox getKiloSeprtChb()
	{
		if (kiloSeprtChb == null)
		{
			kiloSeprtChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0129")/*@res "数字型带千位符"*/);
			kiloSeprtChb.setPreferredSize(new Dimension(200, 20));
		}
		return kiloSeprtChb;
	}

	public UICheckBox getDotChb()
	{
		if (dotChb == null)
		{
			dotChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0130")/*@res "数字型带小数点"*/);
			dotChb.setPreferredSize(new Dimension(200, 20));
		}
		return dotChb;
	}

	public UIComboBox getItemSeparatorComb()
	{
		// 项目分隔符
		if (itemSeparatorComb == null)
		{
			itemSeparatorComb = new UIComboBox();
			itemSeparatorComb.setName(ResHelper.getString("6013bnkitf","06013bnkitf0131")/*@res "项目分隔符"*/);
			itemSeparatorComb.addItem(new DefaultConstEnum(ItemSeprtorEnum.COMMA.value(), ItemSeprtorEnum.COMMA.getName()));
			itemSeparatorComb.addItem(new DefaultConstEnum(ItemSeprtorEnum.SEM.value(), ItemSeprtorEnum.SEM.getName()));
			itemSeparatorComb.addItem(new DefaultConstEnum(ItemSeprtorEnum.ERECT.value(), ItemSeprtorEnum.ERECT.getName()));
			itemSeparatorComb.setPreferredSize(new Dimension(200, 20));
		}
		return itemSeparatorComb;
	}

	public UIComboBox getRelatedItemComb()
	{
		// 关联项目
		if (relatedItem == null)
		{
			relatedItem = new UIComboBox(){
				protected void selectedItemChanged()
				{
					super.selectedItemChanged();
					String col = (String) this.getSelectdItemValue();
					((DataIOAppModel)model).setCol(col);
				}
			};
			relatedItem.addItem(new DefaultConstEnum(RelatedItemEnum.CODE.value(), RelatedItemEnum.CODE.getName()));
			relatedItem.addItem(new DefaultConstEnum(RelatedItemEnum.ID.value(), RelatedItemEnum.ID.getName()));
			// relatedItem.addItem(new
			// DefaultConstEnum(RelatedItemEnum.ID.value(),RelatedItemEnum.ID.getName()));
			relatedItem.setPreferredSize(new Dimension(200, 20));
			
		}
		return relatedItem;
	}

	public UICheckBox getOutputTableHeadChb()
	{
		if (outputTableHeadChb == null)
		{
			outputTableHeadChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0104")/*@res "输出表头"*/);
			outputTableHeadChb.setPreferredSize(new Dimension(200, 20));
			outputTableHeadChb.setSelected(true);
		}
		return outputTableHeadChb;
	}

	public UICheckBox getHeadSameToBodyChb()
	{
		if (headSameToBodyChb == null)
		{
			headSameToBodyChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0132")/*@res "表头表体一致"*/);
			headSameToBodyChb.setPreferredSize(new Dimension(200, 20));
			headSameToBodyChb.setSelected(true);
		}
		return headSameToBodyChb;
	}

	public UICheckBox getLinetopSetChb()
	{
		if (linetopSetChb == null)
		{
			linetopSetChb = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0098") + " 1"/*@res "标志行"*/);
			linetopSetChb.setPreferredSize(new Dimension(200, 20));
		}
		return linetopSetChb;
	}
	
	public UICheckBox getLinetopSetChb2()
	{
		if (linetopSetChb2 == null)
		{
			linetopSetChb2 = new UICheckBox(ResHelper.getString("6013bnkitf","06013bnkitf0098") + " 2"/*@res "标志行"*/);
			linetopSetChb2.setPreferredSize(new Dimension(200, 20));
		}
		return linetopSetChb2;
	}
	

	public UIComboBox getLinetopSetComb()
	{
		// 标志行输出
		if (linetopSetComb == null)
		{
			linetopSetComb = new UIComboBox();
			linetopSetComb.addItem(new DefaultConstEnum(LineTopEnum.SLINE.value(), LineTopEnum.SLINE.getName()));
			linetopSetComb.addItem(new DefaultConstEnum(LineTopEnum.MLINE.value(), LineTopEnum.MLINE.getName()));
			linetopSetComb.setPreferredSize(new Dimension(200, 20));
			linetopSetComb.setEnabled(false);
		}
		return linetopSetComb;
	}
	
	public UIComboBox getLinetopSetComb2()
	{
		// 标志行输出
		if (linetopSetComb2 == null)
		{
			linetopSetComb2 = new UIComboBox();
			linetopSetComb2.addItem(new DefaultConstEnum(LineTopEnum.SLINE.value(), LineTopEnum.SLINE.getName()));
			linetopSetComb2.addItem(new DefaultConstEnum(LineTopEnum.MLINE.value(), LineTopEnum.MLINE.getName()));
			linetopSetComb2.setPreferredSize(new Dimension(200, 20));
			linetopSetComb2.setEnabled(false);
		}
		return linetopSetComb2;
	}

	public UIComboBox getLinetopPosiComb()
	{
		// 标志行位置
		if (linetopPosiComb == null)
		{
			linetopPosiComb = new UIComboBox();
			linetopPosiComb.addItem(new DefaultConstEnum(LineTopPositionEnum.HEAD.value(), LineTopPositionEnum.HEAD.getName()));
			linetopPosiComb.addItem(new DefaultConstEnum(LineTopPositionEnum.TAIL.value(), LineTopPositionEnum.TAIL.getName()));
			linetopPosiComb.setPreferredSize(new Dimension(200, 20));
			linetopPosiComb.setEnabled(false);
		} 
		return linetopPosiComb;
	}
	
	public UIComboBox getLinetopPosiComb2()
	{
		// 标志行位置
		if (linetopPosiComb2 == null)
		{
			linetopPosiComb2 = new UIComboBox();
			linetopPosiComb2.addItem(new DefaultConstEnum(LineTopPositionEnum.HEAD.value(), LineTopPositionEnum.HEAD.getName()));
			linetopPosiComb2.addItem(new DefaultConstEnum(LineTopPositionEnum.TAIL.value(), LineTopPositionEnum.TAIL.getName()));
			linetopPosiComb2.setPreferredSize(new Dimension(200, 20));
			linetopPosiComb2.setEnabled(false);
		}
		return linetopPosiComb2;
	}

	public UIComboBox getFileTypeCombox()
	{
		if (fileTypeCombox == null)
		{
			fileTypeCombox = new UIComboBox()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					super.actionPerformed(e);
				}

				@Override
				protected void selectedItemChanged()
				{
					super.selectedItemChanged();
					int fileType = (Integer) this.getSelectdItemValue();
					boolean isVisible = fileType == (Integer) FileTypeEnum.TXT.value();
					//如果是TxT格式的则，数字型带小数点勾选，否则不勾选
					if(isVisible){
						getDotChb().setSelected(true);
					}else{
						getDotChb().setSelected(false);
					}
					if (getAppModel() != null)
					{
						LoginContext context = getAppModel().getContext();
						if (getDataParaPanel() != null)
						{
							getDataParaPanel().setVisible(isVisible);
						}
						if (context.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
						{
							isVisible = false;
						}
					}

					if (!isVisible)
					{
						// 如果是excel切换到txt，则标志行属性需要清空
						getLinetopSetChb().setSelected(false);
					}

					// if (getTablePanel() != null)
					// {
					// getTablePanel().setVisible(true);
					// }
					if (getRowPanel() != null)
					{
						getRowPanel().setVisible(isVisible);
					}
				}

			};
			DefaultConstEnum defaultEnum1 = new DefaultConstEnum(FileTypeEnum.TXT.value(), FileTypeEnum.TXT.getName());
			DefaultConstEnum defaultEnum2 = new DefaultConstEnum(FileTypeEnum.XLS.value(), FileTypeEnum.XLS.getName());
			fileTypeCombox.addItem(defaultEnum1);
			fileTypeCombox.addItem(defaultEnum2);
			fileTypeCombox.setPreferredSize(new Dimension(200, 20));
//			fileTypeCombox.setSelectedItem(defaultEnum2);
		}
		return fileTypeCombox;
	}

	public UITextField getFormatNameTextField()
	{
		if (formatNameTextField == null)
		{
			formatNameTextField = new UITextField();
			formatNameTextField.setPreferredSize(new Dimension(200, 20));
			formatNameTextField.setMaxLength(50);
			formatNameTextField.setShowMustInputHint(true);
		}
		return formatNameTextField;
	}

	public UITextArea getMemTextArea()
	{
		if (memTexttArea == null)
		{
			memTexttArea = new UITextArea();
			memTexttArea.setName("memTexttArea");
			// memTexttArea.setPreferredSize(new Dimension(200,80));
			memTexttArea.setPreferredSize(new Dimension(200, 20));
			memTexttArea.setLineWrap(true);
			memTexttArea.setMaxLength(1024);
		}
		return memTexttArea;
	}

	public UIPanel getRowPanel()
	{
		return rowPanel;
	}

	public void setRowPanel(UIPanel rowPanel)
	{
		this.rowPanel = rowPanel;
	}

	public UIPanel getTablePanel()
	{
		return tablePanel;
	}

	public void setTablePanel(UIPanel tablePanel)
	{
		this.tablePanel = tablePanel;
	}

	public UIPanel getDataParaPanel()
	{
		return dataParaPanel;
	}

	public void setDataParaPanel(UIPanel dataParaPanel)
	{
		this.dataParaPanel = dataParaPanel;
	}

	public UIRefPane getBankRefPane()
	{
		if (bankRefPane == null)
		{
			bankRefPane = new UIRefPane();
			// bankRefPane.setRefModel(new BankDocDefaultRefTreeModel());
			bankRefPane.setRefNodeName("银行类别");
			// bankRefPane.setRefEditable(false);
			// bankRefPane.setReturnCode(true);
			bankRefPane.setPreferredSize(new Dimension(200, 20));
			//设定为必输项
			bankRefPane.getUITextField().setShowMustInputHint(true);
		}
		return bankRefPane;
	}

}