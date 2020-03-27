/**
 *
 */
package nc.ui.wa.datainterface.view;

import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.FileParas;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;


/**
 * @author xuanlt
 *
 */
public class DataIOPanel extends UITabbedPane
{
	// BillScrollPane

	private DataIOAppModel appModel;

	private DefaultDataIOHeadPanel headPanel;

	// private ArrayList<HashMap<String, Object>> datas;

	// private HashMap<String,BillModel> billModelMap = new
	// HashMap<String,BillModel>();

	public DataIOPanel()
	{}

	public void initUI()
	{
		// setTableModel(createBillModel());
		removeAll();
		AggHrIntfaceVO[] aggVOs = null;
		if (getAppModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
			{
			aggVOs = (AggHrIntfaceVO[]) (getAppModel()).getData().toArray(new AggHrIntfaceVO[0]);
		}
		else
		{
			aggVOs = new AggHrIntfaceVO[]
			{ getAppModel().getSelectedData() };
		}
		for (int i = 0; aggVOs != null && i < aggVOs.length; i++)
		{
			if (aggVOs[i] != null)
			{
				HrIntfaceVO vo = (HrIntfaceVO) aggVOs[i].getParentVO();
				BillScrollPane billScroll = new BillScrollPane();

				BillModel billModel = createBillModel(aggVOs[i]);
				billScroll.setTableModel(billModel);
				
				billScroll.setRowNOShow(true);
				billScroll.setTotalRowShow(true);
				addTab(vo.getVifname(), billScroll);
				this.getAppModel().getBillModelMap().put(vo.getPk_dataio_intface(), billModel);
			}
		}

	}

	// public BillScrollPane getDataPanel() {
	// if(bSPTable == null){
	// bSPTable = new nc.ui.pub.bill.BillScrollPane();
	// }
	// return bSPTable;
	// }

	private BillModel createBillModel(AggHrIntfaceVO aggVO)
	{
		BillModel model = new BillModel();
		FormatItemVO[] formatVOs = (FormatItemVO[]) aggVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		if (formatVOs != null && formatVOs.length > 0)
		{
			BillItem[] items = new BillItem[formatVOs.length];
			for (int i = 0; i < formatVOs.length; i++)
			{
				items[i] = new BillItem();
				items[i].setName(formatVOs[i].getVfieldname());
				String temp = formatVOs[i].getVcontent();
				//shenliangc 20140702 银行报盘增加户名
				if(StringUtils.isNotBlank(temp)){
					temp = temp.replace(".", "");
				}else{
					temp = formatVOs[i].getVfieldname();
				}
				items[i].setKey(temp);// 表名字段名
				items[i].setWidth(100);
				items[i].setLength(formatVOs[i].getIfldwidth());
				items[i].setEnabled(true);
				items[i].setEdit(false);
				items[i].setNull(false);
//				if (appModel.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO))
//				{
//					items[i].setEdit(true);
//					items[i].setEnabled(true);
//				}
				// items[i].setNull(false);
				// 当心数据库的数据类型与billitem的数据类型标示不一致
				Integer fieldtype = formatVOs == null ? null : formatVOs[i].getIfieldtype();
				items[i].setDataType(fieldtype);
				if (items[i].getDataType() == (Integer) FieldTypeEnum.DEC.value())
				{
					items[i].setDecimalDigits(formatVOs[i].getIflddecimal());
					//数值型才合计
					items[i].setTatol(true);
				}
				
			}

			model.setBodyItems(items);
			// model.setEnabled(false);
		}
		return model;
	}

	/**
	 *
	 * @param dbType
	 * @return
	 */
	private int convertType(int dbType)
	{
		if (dbType == 1)
		{
			return BillItem.DECIMAL;
		}
		else
		{
			return dbType;
		}
	}

	/**
	 * 得到接口的所有项目格式
	 *
	 * @return
	 */
	public FormatItemVO[] getFormatVOs()
	{
		AggHrIntfaceVO aggVO = this.getAppModel().getSelectedData();
		FormatItemVO[] vos = null;
		if (aggVO != null)
		{
			vos = (FormatItemVO[]) aggVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		}
		return vos;
	}

	// public void resetTableModel(){
	// BillModel model = createBillModel();

	// setTableModel(model);
	// }

	public void setData(CircularlyAccessibleValueObject[] data)
	{
	// if(data.length>0){
	// this.bodyVOName = data[0].getClass().getName();
	// }
	// getTableModel().setBodyDataVO(data);
	}

	/**
	 * 子类应该复写该方法，提供默认的名字
	 *
	 * @return
	 */
	protected String getDefaultBodyVOName()
	{
		return SuperVO.class.getName();
	}

	public FileParas getParasConfig() throws BusinessException
	{
		DefaultDataIOHeadPanel panel = getHeadPanel();
		FileParas paras = new FileParas();
		String fileName = panel.getFileLocation();
		if (StringUtils.isBlank(fileName))
		{
			throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0076")/*@res "请指定文件位置"*/);
		}
		paras.setFileLocation(fileName);
		//paras.setOutputHead(panel.isOutPutHead());
		//paras.setOutPutLineNo(panel.isOutPutLineNo());
		//paras.setHeadAjtBodyFmat(panel.isHeadAdjustBody());
		return paras;
	}

	public DataIOAppModel getAppModel()
	{
		return appModel;
	}

	public void setAppModel(DataIOAppModel model)
	{
		this.appModel = model;
	}

	public DefaultDataIOHeadPanel getHeadPanel()
	{
		return headPanel;
	}

	public void setHeadPanel(DefaultDataIOHeadPanel headPanel)
	{
		this.headPanel = headPanel;
	}

	// public ArrayList<HashMap<String, Object>> getDatas() {
	// return datas;
	// }
	//
	// public void setDatas(ArrayList<HashMap<String, Object>> datas) {
	// this.datas = datas;
	// }

	// public HashMap<String, BillModel> getBillModelMap() {
	// return billModelMap;
	// }
	//
	// public void setBillModelMap(HashMap<String, BillModel> billModelMap) {
	// this.billModelMap = billModelMap;
	// }

}