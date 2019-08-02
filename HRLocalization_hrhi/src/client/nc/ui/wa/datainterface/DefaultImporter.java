/**
 *
 */
package nc.ui.wa.datainterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.FileParas;
import nc.ui.pub.bill.BillModel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.datainterface.DataIOconstant;


/**
 * @author xuanlt
 *
 */
public class DefaultImporter implements ImportStrategy {
	public static final String BLANK = " ";

	FileParas paras = null;
	/**
	 * 接口信息
	 */
	AggHrIntfaceVO intfaceInf = null;
	/**
	 *  存放数据的billModel
	 */
	BillModel billmodel = null;

	/**
     * 每一行数据的vo的名字
     */
	String bodyVOName = "";

	SuperVO[] vos  = null;

	ArrayList<HashMap<String, Object>> datas;

	public DefaultImporter() {


	}

	protected void openFile() throws Exception{

	}




	protected void beforeSetData() throws Exception{

	}

    protected void afterSetData() throws Exception{

	}

    protected void setDataToTable() {
//    	if(vos!=null){
//    	   billmodel.clearBodyData();
//    	   billmodel.setBodyDataVO(vos);
//    	}
//    	billmodel.clearBodyData();
//
//		for(int i=0;datas!=null&&i<datas.size();i++)
//		{
//			billmodel.insertRow(i);
//			BillItem[] items =billmodel.getBodyItems();
//			for(int j=0;items!=null&&j<items.length;j++ )
//			{
//				Object value = datas.get(i).get(items[j].getKey());
//				if(items[j].getKey()!=null&&value!=null)
//				{
//					billmodel.setValueAt(value, i, items[j].getKey());
//				}
//			}
//
//		}

	}

	protected void closeFile() throws Exception{

	}



	/* (non-Javadoc)
	 * @see nc.ui.hr.dataio.itf.ImportStrategy#importFromFile()
	 * 导入数据 ，不会出现单一型（也就是固定值）类型
	 */
	public void importFromFile() throws Exception {
        try {
			openFile();
			datas = readData();
			beforeSetData();
			setDataToTable();
			afterSetData();

		} catch (BusinessException e) {
			Logger.error(e);
			throw e;
		}catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e.getMessage());
//			throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0106")/*@res "读取文件错误"*/);

		}finally  {
			closeFile();
		}
	}


	public FileParas getParas() {
		return paras;
	}

	public void setParas(FileParas paras) {
		this.paras = paras;
	}

	public AggHrIntfaceVO getIntfaceInf() {
		return intfaceInf;
	}


	public void setIntfaceInf(AggHrIntfaceVO intfaceInf) {
		this.intfaceInf = intfaceInf;
	}

	public BillModel getBillmodel() {
		return billmodel;
	}

	public void setBillmodel(BillModel billmodel) {
		this.billmodel = billmodel;
	}

	public String getBodyVOName() {
		return bodyVOName;
	}

	public void setBodyVOName(String bodyVOName) {
		this.bodyVOName = bodyVOName;
	}

	public FormatItemVO[] getFormatItemVOs(){
		if(getIntfaceInf()!=null){
			return (FormatItemVO[])getIntfaceInf().getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		}
		return null;
	}

	public String getFieldCode(String tableAndFld) {
		return tableAndFld.substring(tableAndFld.indexOf(".") + 1);
	}


	protected ArrayList<HashMap<String, Object>> readData() throws Exception{
		return null;
	}

	protected Object newInstance(Class c) throws SQLException {
		try {
			return c.newInstance();

		} catch (InstantiationException e) {
			throw new SQLException("Cannot create " + c.getName() + ": "
					+ e.getMessage());

		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot create " + c.getName() + ": "
					+ e.getMessage());
		}
	}

	public SuperVO[] getVos()
	{
		return vos;
	}

	public void setVos(SuperVO[] vos)
	{
		this.vos = vos;
	}

	@Override
	public ArrayList<HashMap<String, Object>> getDatas() {
		return this.datas;
	}

	public void setDatas(ArrayList<HashMap<String, Object>> datas) {
		this.datas = datas;
	}
}