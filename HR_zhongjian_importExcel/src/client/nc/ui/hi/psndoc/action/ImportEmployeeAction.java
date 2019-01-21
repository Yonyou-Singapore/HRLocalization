package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.arap.util.SqlUtils;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.ValidationException;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.employee.IEmployeeImportService;
import nc.itf.hr.dataio.IDataIOHookPublic;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.impl.Attribute;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.pub.hi.employeeimport.vo.EmployeeFormatVO;
import nc.pub.tools.VOUtils;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.hi.psndoc.view.ImportEmployeePanel;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;
import nc.vo.hi.psndoc.Glbdef3VO;
import nc.vo.hi.psndoc.Glbdef4VO;
import nc.vo.hi.psndoc.Glbdef7VO;
import nc.vo.hi.psndoc.Glbdef9VO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.dataio.DefaultHookPublic;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SuppressWarnings("restriction")
public class ImportEmployeeAction extends HrAction {

	public static final String funcode = "60070employee";
	
	//CCDC��Ա��Ϣ������֯Ϊ�͹�������֯  PK_Group
	public static final String pk_org = "0001B2100000000008HW";

	// ������Class
	public static final PsndocVO PsndocVOClass = new PsndocVO();

	private static final long serialVersionUID = 1L;

	// ���ջ��� key: funcode + value value:PKvalue
	private HashMap<String, String> refValueAndKey = new HashMap<String, String>();

	public ImportEmployeeAction() {
		super();
		setBtnName(ResHelper.getString("hrhi_localimport",
				"hrhi_localimport001")/* @res "������Ա��Ϣ" */);
		setCode(IActionCode.IMPORT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ImportEmployeePanel employeePanel = new ImportEmployeePanel();

		if (employeePanel.getChooser()
				.showDialog(
						null,
						ResHelper.getString("hrhi_localimport",
								"hrhi_localimport002")/* @res "�����ļ�" */) == UIFileChooser.CANCEL_OPTION) {
			return;
		}
		File file = employeePanel.getChooser().getSelectedFile();

		if (file == null) {
			putValue(MESSAGE_AFTER_ACTION,
					ResHelper.getString("6001uif2", "06001uif20002") /*
																	 * @res
																	 * "��ȡ����"
																	 */);
			return;
		}
		String path = file.getPath();
		Workbook workbook = this.openWorkbook(path);
		// 1.ֻ��ȡ��һ��sheet,�ұ���Ϊ����ҳǩ,�����ݲ���Ϊ��
		this.checkSheetFormat(workbook);
		// 2.��ȡexcel
		this.readExcel(workbook);

//		Map<String, File> psnPhotoFileMap = new HashMap<String, File>();
//
//		psnPhotoFileMap.remove("key");
//
//		// У�� ����Աά��Ȩ�޵���Ա���ܵ�����Ƭ add by yanglt 20140807
//		checkDataPermission(psnPhotoFileMap);
//
//		// ��װPsndocVO����Ƭ��Ϣ
//		PsndocVO[] psndocVOs = assemblePsndocVO(psnPhotoFileMap);
//
//		// ÿ�����̨�ύ�������
//		int everyTimeSubmit = 100;

//		int sum = 0;
//		// ����
//		if (!ArrayUtils.isEmpty(psndocVOs)) {
//			savePsndocVO(psndocVOs, everyTimeSubmit);
//			sum = psndocVOs.length;
//		}
		
	}

	/**
	 * 
	 * @param workbook
	 * @throws Exception
	 */
	private void readExcel(Workbook workbook) throws Exception {
		// ��ȡ��һ����Ϣ,�ֶ���Ϣ
		Map<Integer, String> firstRowColumn = this.readFirstRow(workbook);

		// 1. ��ѯEmployeeFormatVO
		List<EmployeeFormatVO> employeeFormatVOs = NCLocator
				.getInstance()
				.lookup(IEmployeeImportService.class)
				.queryEmployeeFormatVO(
						new ArrayList<String>(firstRowColumn.values())
								.toArray(new String[0]));
		Map<Integer, EmployeeFormatVO> employeeFormatMap = this
				.convertListToMap(employeeFormatVOs, firstRowColumn);
		// 2. ������һ��ҳǩ
		List<Map<String, SuperVO>> analysisExcelToVO = this.analysisExcelToVO(workbook.getSheetAt(0), employeeFormatVOs,
				employeeFormatMap);
		//3. �������½������VO
		int num = this.processVO(analysisExcelToVO, firstRowColumn);
		
		//4. ��ʾ���
		showResultHint(num);

	}

	private int processVO(List<Map<String, SuperVO>> vos, Map<Integer, String> firstRowColumn) {
		if(vos == null || vos.size() < 1) {
			return 0;
		}
		//1. ��ѯ��Щ��Աupdate�� ��Щ��Աinert
		List<PsndocVO> psnvolist = new ArrayList<PsndocVO>();
		for(Map<String, SuperVO> supervo : vos) {
			PsndocVO values = (PsndocVO) supervo.get("nc.vo.hi.psndoc.PsndocVO");
			psnvolist.add(values);
		}
		//��ѯ��Ա
		List<String> psnjobPKs = this.queryPsnjobPK(psnvolist);
		//���ýӿ�DB����
		return this.callInterfaceToDB(vos, psnjobPKs, firstRowColumn);
	}
	

	private int callInterfaceToDB(List<Map<String, SuperVO>> vos,
			List<String> psnjobPKs, Map<Integer, String> firstRowColumn) {
		int num=0;
		//��װPsndocAggVO 
		List<PsndocAggVO> aggvos = this.buildPsndocAggVOs(vos, psnjobPKs);
		//�ж���Щ��inert ��Щ��update ��updateʱֻ֧�ֵ������
		List<PsndocAggVO> inertaggvos = this.seperateVOsForInsert(aggvos);
		List<SuperVO> updateaggvos = this.seperateVOsForUpdate(aggvos, firstRowColumn);
		//���ýӿ�
		IEmployeeImportService service = NCLocator.getInstance().lookup(IEmployeeImportService.class);
		//inert
		try {
			if(inertaggvos != null && inertaggvos.size() > 0) {
				num = service.saveImportVOs(inertaggvos.toArray(new PsndocAggVO[0]));
			}
			if(updateaggvos != null && updateaggvos.size() > 0) {
				num = service.updateImportVOs(updateaggvos.toArray(new SuperVO[0]));
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		//update
		//ת����Ա����   TODO
		return num;
	}
	
	/**
	 * ��Ҫ��ȫVO �����update ��Ҫ��ȫ�� �����insert ��Ҫ�ֶβ�ȫ
	 * @param vos
	 * @param psnjobPKs
	 * @return
	 */
	private List<PsndocAggVO> buildPsndocAggVOs(List<Map<String, SuperVO>> vos,
			List<String> psnjobPKs) {
		//���ݹ�����¼��ѯaggvo
		Map<String, PsndocAggVO> queryedPsnaggVOmap = this.queryPsnAggVOByJobPKsToMap(psnjobPKs);
		
		List<PsndocAggVO> aggvos = new ArrayList<PsndocAggVO>();
		for(Map<String, SuperVO> supervo : vos) {
			PsndocAggVO aggvo = new PsndocAggVO();
			for(Map.Entry<String, SuperVO> entry : supervo.entrySet()) {
				String classname = entry.getKey();
				SuperVO value = entry.getValue();
				String code = (String)value.getAttributeValue("code");
				//���� PsndocVO
				if("nc.vo.hi.psndoc.PsndocVO".equals(classname)) {
					if(queryedPsnaggVOmap != null && queryedPsnaggVOmap.keySet().contains(code)) {
						this.setPsndocDefaultValue((PsndocVO)value, queryedPsnaggVOmap.get(code).getParentVO());
					} else{
						value.setAttributeValue(PsndocVO.PK_GROUP, pk_org);
						value.setStatus(VOStatus.NEW);
					}
					aggvo.setParentVO(value);
				} else {
					//�Ӽ�
					this.setSubDefaultValue(value, (PsndocVO)supervo.get("nc.vo.hi.psndoc.PsndocVO"), queryedPsnaggVOmap);
					SuperVO[] arraysupervo = this.parseSuperVO(value);
					aggvo.setTableVO(value.getTableName(), arraysupervo);
				}
			}
			aggvos.add(aggvo);
		}
		return aggvos;
	}

	private void setPsndocDefaultValue(PsndocVO value, PsndocVO parentVO) {
		value.setAttributeValue(PsndocVO.PK_PSNDOC, parentVO.getPk_psndoc());
		value.setStatus(VOStatus.UPDATED);
		
	}

	private Map<String, PsndocAggVO> queryPsnAggVOByJobPKsToMap(
			List<String> psnjobPKs) {
		Map<String, PsndocAggVO> aggvosmap = null;
		PsndocAggVO[] aggvos = null;
		if(psnjobPKs == null || psnjobPKs.size() < 1) {
			return null;
		}
		IPsndocQryService service = NCLocator.getInstance().lookup(IPsndocQryService.class);
		try {
			aggvos = service.queryPsndocVOByPks(psnjobPKs.toArray(new String[0]));
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		if(aggvos != null) {
			aggvosmap = new HashMap<String, PsndocAggVO>();
			for(PsndocAggVO agg : aggvos) {
				aggvosmap.put(agg.getParentVO().getCode(), agg);
			}
		}
		return aggvosmap;
	}
	
	/**
	 * 
	 * @param value    Ҫ����vo
	 * @param superVO  ��ȡcode
	 * @param queryedPsnaggVOmap   ���Ĭ��ֵ�������
	 */
	private void setSubDefaultValue(SuperVO value, PsndocVO superVO, Map<String, PsndocAggVO> queryedPsnaggVOmap) {
		String code = superVO.getCode();
		if(queryedPsnaggVOmap != null && !StringUtils.isBlank(superVO.getCode()) && queryedPsnaggVOmap.get(code) != null) {
			//���µ����  ��������
			String pk_psndoc = queryedPsnaggVOmap.get(code).getParentVO().getPk_psndoc();
			value.setAttributeValue("pk_psndoc", pk_psndoc);
			if(value instanceof PsnJobVO) {
				this.setPsnJobDefaultValue((PsnJobVO)value, code, false);
			} else if(value instanceof PsnOrgVO) {
				((PsnOrgVO) value).setPk_org("0001B2100000000081YE");//MP
				((PsnOrgVO) value).setPk_group(pk_org);//MP
				//�Ƿ�ת����Ա���� 
				((PsnOrgVO) value).setIndocflag(UFBoolean.TRUE);
			}
			value.setStatus(VOStatus.NEW);
		} else {
			if(value instanceof PsnJobVO) {
				this.setPsnJobDefaultValue((PsnJobVO)value, code, true);
			} else if(value instanceof PsnOrgVO) {
				((PsnOrgVO) value).setPk_org("0001B2100000000081YE");//MP
				((PsnOrgVO) value).setPk_group(pk_org);//MP
				//�Ƿ�ת����Ա���� 
				((PsnOrgVO) value).setIndocflag(UFBoolean.FALSE);
			} else {
//				value.setAttributeValue(arg0, arg1)
			}
			value.setStatus(VOStatus.NEW);
		}
		
	}

	private void setPsnJobDefaultValue(PsnJobVO value, String code, boolean isMainJob) {
		//Ա����
		((PsnJobVO) value).setClerkcode(code);
		((PsnJobVO) value).setPk_org("0001B2100000000081YE");//MP
		((PsnJobVO) value).setPk_group(pk_org);//MP
		//��Ա��ְID 
		((PsnJobVO) value).setAssgid(Integer.valueOf(1));
		//��Ա���
		((PsnJobVO) value).setPsntype(Integer.valueOf(0));
		//��ʾ˳��
		((PsnJobVO) value).setShoworder(Integer.valueOf(9999999));
		//pk_hrorg
		((PsnJobVO) value).setPk_hrorg("0001B2100000000081YE");//MP
		//���±��
		((PsnJobVO) value).setLastflag(UFBoolean.TRUE);
		//�Ƿ���ְ
		((PsnJobVO) value).setIsmainjob(UFBoolean.valueOf(isMainJob));
		((PsnJobVO) value).setRecordnum(Integer.valueOf(9999999));
		
	}

	private SuperVO[] parseSuperVO(SuperVO value) {
		SuperVO[] arraysupervo = null;
		if(value instanceof PsnJobVO) {
			arraysupervo = new PsnJobVO[]{(PsnJobVO) value};
		} else if(value instanceof Glbdef4VO) {
			arraysupervo = new Glbdef4VO[]{(Glbdef4VO) value};
		} else if(value instanceof Glbdef3VO) {
			arraysupervo = new Glbdef3VO[]{(Glbdef3VO) value};
		} else if(value instanceof Glbdef7VO) {
			arraysupervo = new Glbdef7VO[]{(Glbdef7VO) value};
		} else if(value instanceof Glbdef9VO) {
			arraysupervo = new Glbdef9VO[]{(Glbdef9VO) value};
		} else if(value instanceof PsnOrgVO) {
			arraysupervo = new PsnOrgVO[]{(PsnOrgVO) value};
		}
		return arraysupervo;
	}

	private List<SuperVO> seperateVOsForUpdate(List<PsndocAggVO> aggvos,
			Map<Integer, String> firstRowColumn) {
		if(aggvos == null || aggvos.size() < 1) {
			return null;
		}
		List<SuperVO> supervos = new ArrayList<SuperVO>();
		for(PsndocAggVO agg : aggvos) {
			PsndocVO parentVO = agg.getParentVO();
			String pk = parentVO.getPk_psndoc();
			if(pk == null) {
				continue;
			}
			String[] tableNames = agg.getTableNames();
			if(tableNames != null && tableNames.length > 0) {
				for(String name : tableNames) {
					SuperVO[] tableVO = agg.getTableVO(name);
					if(tableVO != null && tableVO.length > 0) {
						supervos.add(tableVO[0]);
					}
				}
			} else {
				supervos.add(parentVO);
			}
		}
		
		return supervos;
	}

	private List<PsndocAggVO> seperateVOsForInsert(List<PsndocAggVO> aggvos) {
		if(aggvos == null || aggvos.size() < 1) {
			return null;
		}
		List<PsndocAggVO> psninsertlist = new ArrayList<PsndocAggVO>();
		for(PsndocAggVO vo : aggvos) {
			if(vo.getParentVO().getPk_psndoc() == null) {
				psninsertlist.add(vo);
			} 
		}
		
		return psninsertlist;
	}
	
	
	/**
	 * ��Ա����==��PK
	 * @param psnvolist
	 * @return
	 */
	private List<String> queryPsnjobPK(List<PsndocVO> psnvolist) {
		List<String> psncodes = new ArrayList<String>();
		List<String> jobPK = null;
		for(PsndocVO vo : psnvolist) {
			psncodes.add(vo.getCode());
		}
		List<PsnJobVO> psnjobs = null;
		IPsndocQryService service = NCLocator.getInstance()
					.lookup(IPsndocQryService.class);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(SqlUtils.getInStr(PsnJobVO.CLERKCODE, psncodes.toArray(new String[0]), false));
			sb.append(" and hi_psnjob.ismainjob = 'Y' and hi_psnjob.lastflag = 'Y'");
			psnjobs = service.queryPsninfoByCondition(sb.toString());
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("Failed: query psn info by code failed.");
		}
		if(psnjobs != null) {
			jobPK = new ArrayList<String>();
			for(PsnJobVO job : psnjobs) {
				jobPK.add(job.getPk_psnjob());
			}
		}
		return jobPK;
	}

	/**
	 * Map<Integer, employeeFormatVOs>
	 * 
	 * @param employeeFormatVOs
	 * @param firstRowColumn
	 * @return
	 */
	private Map<Integer, EmployeeFormatVO> convertListToMap(
			List<EmployeeFormatVO> employeeFormatVOs,
			Map<Integer, String> firstRowColumn) {
		Map<Integer, EmployeeFormatVO> map = new HashMap<Integer, EmployeeFormatVO>();
		for (EmployeeFormatVO vo : employeeFormatVOs) {
			String columnname = vo.getDefname();
			if (firstRowColumn.values().contains(columnname)) {
				int columnIndex = this.getColumnIndex(firstRowColumn,
						columnname);
				map.put(columnIndex, vo);
			}

		}
		return map;
	}

	/**
	 * 
	 * @param sheet
	 * @param employeeFormatVOs
	 * @param employeeFormatMap
	 *            ��index��EmployeeFormatVO
	 * @throws BusinessException
	 * @throws ClassNotFoundException
	 */
	private List<Map<String, SuperVO>> analysisExcelToVO(Sheet sheet,
			List<EmployeeFormatVO> employeeFormatVOs,
			Map<Integer, EmployeeFormatVO> employeeFormatMap) throws Exception {
		int rownums = sheet.getLastRowNum();
		// nameMap
		Map<String, EmployeeFormatVO> nameMap = this
				.getMapForEmployeeFormatVO(employeeFormatVOs);
		StringBuffer sb = new StringBuffer();
		for(String name : nameMap.keySet()) {
			sb.append(name).append(", ");
		}
		int isOK = MessageDialog.showOkCancelDlg(this.getEntranceUI(), 
				"Success analysis below columns:", sb.toString());
		if(isOK == MessageDialog.YES_NO_OPTION) {
			return null;
		}
		// String[][] data
		String[][] sheetdata = this.getSheetData(sheet);
		// ��ȡ��Ҫʵ������class

		int count = sheetdata[0].length;
		if (count == 0) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0102")
			/* @res "��Ϣ��д��ȫ����ȥԭ�ļ��޸�" */);
		}

		Map<String, Class<? extends SuperVO>> allclassname = this
				.getInstanceClass(employeeFormatVOs);

		List<Map<String, SuperVO>> intanceArrayAllClass = new ArrayList<Map<String, SuperVO>>();

		// ���б���
		for (int row = 0; row < sheetdata.length; row++) {
			// ÿ��VOȫ��ʵ����
			Map<String, SuperVO> intanceAllClass = this
					.intanceAllClass(allclassname);
			// �Ȱ�����򵥵ĳ���,ÿ��һ��������һ���Ӽ�
			for (int column = 0; column < sheetdata[row].length; column++) {
				EmployeeFormatVO formatvo = employeeFormatMap.get(column);
				String fullclassname = formatvo.getFullclassname();

				Boolean isContainClass = allclassname.get(fullclassname) == null ? false
						: true;
				if (isContainClass) {
					// ��������
					IBean bean = MDBaseQueryFacade.getInstance()
							.getBeanByFullClassName(fullclassname);
					this.setAttributeValue(intanceAllClass.get(fullclassname),
							formatvo.getItemcode(), sheetdata[row][column],
							bean, column);
				}
			}
			intanceArrayAllClass.add(intanceAllClass);

		}
		return intanceArrayAllClass;

	}



	/**
	 * ʵ����ȫ����class
	 * 
	 * @param instanceClass
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private Map<String, SuperVO> intanceAllClass(
			Map<String, Class<? extends SuperVO>> instanceClass)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Map<String, SuperVO> intanceclassmap = new HashMap<String, SuperVO>();
		for (Map.Entry<String, Class<? extends SuperVO>> entry : instanceClass
				.entrySet()) {
			String classname = entry.getKey();
			SuperVO instance = (SuperVO) Class.forName(classname).newInstance();
			intanceclassmap.put(classname, instance);
		}
		return intanceclassmap;
	}

	/**
	 * ʵ����class
	 * 
	 * @param employeeFormatVOs
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Map<String, Class<? extends SuperVO>> getInstanceClass(
			List<EmployeeFormatVO> employeeFormatVOs)
			throws ClassNotFoundException {
		Map<String, Class<? extends SuperVO>> infoSetClass = new HashMap<String, Class<? extends SuperVO>>();
		for (EmployeeFormatVO vo : employeeFormatVOs) {
			String classname = vo.getFullclassname();
			if (!StringUtils.isEmpty(classname)) {
				Class<? extends SuperVO> superclass = (Class<? extends SuperVO>) Class
						.forName(classname);
				if (!infoSetClass.values().contains(superclass)) {
					infoSetClass.put(classname, superclass);
				}
			}
		}
		return infoSetClass;

	}

	/**
	 * ��������Ĭ��ΪString
	 * 
	 * @param sheet
	 * @return
	 * @throws BusinessException
	 */
	private String[][] getSheetData(Sheet sheet) throws BusinessException {

		int rownums = sheet.getPhysicalNumberOfRows();
		String[][] sheetdata = new String[rownums - 1][sheet.getRow(1)
				.getLastCellNum()];// ����

		for (int rowindex = 1; rowindex < rownums; rowindex++) {// �ӵڶ��п�ʼ
																// �ڶ��п�ʼΪ����
			Row row = sheet.getRow(rowindex);
			if (row == null) {
				continue;
			}
			short columnnums = row.getLastCellNum();
			for (int columnindex = 0; columnindex < columnnums; columnindex++) {
				Cell cell = row.getCell(columnindex);
				if (cell == null || StringUtils.isBlank(cell.getStringCellValue())) {
					continue;
				}
				switch (cell.getCellType()) {
				// String
				case HSSFCell.CELL_TYPE_STRING:
					sheetdata[rowindex - 1][columnindex] = cell
							.getStringCellValue();
					break;
				default:
					// ����չ
					throw new BusinessException(
							"Excel cell format only support String.Please check row :"
									+ rowindex + ",column: "
									+ (columnindex + 1));
				}
			}
		}
		return sheetdata;
	}

	protected void setAttributeValue(SuperVO supervo, String strAttrName,
			String strAttrValue, IBean bean, int columnIndex)
			throws BusinessException {

		Attribute attr = (Attribute) bean.getAttributeByName(strAttrName);
		if (attr == null) {
			supervo.setAttributeValue(strAttrName, strAttrValue);
			return;
		}

		if (supervo.getPKFieldName().equals(attr.getName())) {
			return;
		}

		// �ȶ�Ԫ������û�趨�ǿյķǿ������У��
		IDataIOHookPublic hookPublic = DefaultHookPublic.getInstance(funcode);
		List<String> listNotNullColumn = hookPublic.getNotNullColumn("");

		if (!listNotNullColumn.isEmpty()
				&& listNotNullColumn.contains(attr.getName())
				&& StringUtils.isEmpty(strAttrValue)) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0034", attr.getDisplayName())
			/* @res "[{0}]����Ϊ�գ�" */);
		}

		// cast data
		Object attrValue = this.castDatasToAttributeValue(supervo, strAttrName,
				strAttrValue, bean, attr);

		try {
			supervo.setAttributeValue(strAttrName, attrValue);
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0035", attr.getDisplayName())
			/* @res "[{0}]ֵ�����Ͳ�ƥ�䣻" */);
		}
	}

	/**
	 * @param supervo
	 * @param strAttrName
	 * @param strAttrValue
	 * @param bean
	 * @param attr
	 * @return
	 * @throws BusinessException
	 */
	private Object castDatasToAttributeValue(SuperVO supervo,
			String strAttrName, String strAttrValue, IBean bean, Attribute attr)
			throws BusinessException {

		if (attr == null) {
			return strAttrValue;
		}

		IType type = attr.getDataType();

		if (ArrayUtils.contains(new int[] { IType.TYPE_UFBoolean,
				IType.TYPE_BOOL, IType.TYPE_Boolean }, type.getTypeType())) {
			if (StringUtils.isEmpty(strAttrValue)) {
				return UFBoolean.valueOf("N");
			}
			return UFBoolean.valueOf(strAttrValue);
		} else if (ArrayUtils.contains(new int[] { IType.TYPE_CHAR,
				IType.TYPE_String, IType.TYPE_MEMO }, type.getTypeType())) {
			return castString(strAttrValue, supervo, attr);
		} else if (ArrayUtils.contains(new int[] { IType.TYPE_INT,
				IType.TYPE_Integer, IType.TYPE_SHORT, IType.TYPE_LONG },
				type.getTypeType())) {
			return castInt(strAttrValue, supervo, attr);
		} else if (ArrayUtils.contains(new int[] { IType.TYPE_UFDouble,
				IType.TYPE_UFMoney, IType.TYPE_BIGDECIMAL, IType.TYPE_DOUBLE,
				IType.TYPE_Double, IType.TYPE_FLOAT, }, type.getTypeType())) {
			return castDouble(strAttrValue, supervo, attr);
		} else if (ArrayUtils.contains(new int[] { IType.TYPE_Date,
				IType.TYPE_UFDate, IType.TYPE_UFDate_BEGIN,
				IType.TYPE_UFDate_END, IType.TYPE_UFDATE_LITERAL },
				type.getTypeType())) {
			return castDate(strAttrValue, supervo, attr);
		} else if (type.getTypeType() == IType.ENUM) // ö��
		{
			return castEnum(strAttrValue, supervo, attr);
		} else if (type.getTypeType() == IType.ENTITY
				|| type.getTypeType() == IType.REF)// ����
		{
			return castRef(strAttrValue, supervo, attr, bean);
		} else {
			return strAttrValue;
		}
	}

	private Object castRef(String strAttrValue, SuperVO superVO,
			Attribute attr, IBean bean) throws BusinessException {
		refValueAndKey.clear();
		AbstractRefModel refModel = this.getRefModel(pk_org, attr.getRefModelName());
		if (refModel == null) {
			return null;
		}

		// ������ձ���/����������Map���Ѿ�������Ĳ��ղ��������롣���ϼ���������+���ձ���/������Ϊkey
		Map<String, String> refRuleMap = ((DefaultHookPublic) DefaultHookPublic
				.getInstance(funcode)).getDefaultRefRule(bean.getName());

		String refKey = refModel.getRefNodeName();
		String strAttrName2 = "";
		String strAttrName3 = strAttrValue;

		while (!StringUtils
				.isEmpty(strAttrName2 = refRuleMap.get(strAttrName3))) {
			if (superVO != null) {
				refKey += superVO.getAttributeValue(strAttrName2);
			}

			strAttrName3 = strAttrName2;
		}

		String[] strPkValues = null;

		if (refValueAndKey.get(refKey + strAttrValue) != null) {
			if (!refValueAndKey.get(refKey + strAttrValue).equals("")) {
				return refValueAndKey.get(refKey + strAttrValue);
			}

			strPkValues = new String[] { "" };
		} else {
			// ����codeƥ�䣬���û��ƥ�䵽������nameȥƥ��
			refModel.matchData(new String[] { refModel.getRefCodeField(),
					refModel.getRefNameField() }, new String[] { strAttrValue });

			strPkValues = refModel.getPkValues();
		}

		if (strPkValues == null || strPkValues.length == 0
				|| strPkValues[0].equals("")) {
			refValueAndKey.put(refKey + strAttrValue, "");
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0033", attr.getDisplayName())
			/* @res "[{0}]�������ò���ȷ��δƥ�䵽���ݣ�" */);
		} else if (strPkValues.length > 1) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0084", attr.getDisplayName())
			/* @res "[{0}]���յ���ֵ��Ψһ��" */);
		}

		refValueAndKey.put(refKey + strAttrValue, strPkValues[0]);

		return strPkValues[0];
	}

	public AbstractRefModel getRefModel(String pk_org, String strRefModelName) {
		AbstractRefModel refModel = RefPubUtil.getRefModel(strRefModelName);

		if (refModel == null) {
			return null;
		}

		refModel.setCacheEnabled(true);
		if(refModel instanceof nc.ui.om.ref.HRDeptRefModel) {
			refModel.setPk_org("0001B2100000000081YE"); //��֯��   MP��֯ PK
		} else {
			refModel.setPk_org(pk_org);
		}

		return refModel;
	}

	private Object castEnum(String strAttrValue, SuperVO supervo, Attribute attr)
			throws BusinessException {
		IConstEnum[] constEnums = ((EnumType) attr.getDataType())
				.getConstEnums();

		for (IConstEnum constEnum : constEnums) {
			if (constEnum.getName().equals(strAttrValue)) {
				return constEnum.getValue();
			}
		}

		// ���������Ϣ
		throw new BusinessException(ResHelper.getString("6001dataimport",
				"06001dataimport0032", attr.getDisplayName())
		/* @res "[{0}]ö��ֵ���ò���ȷ��" */);

	}

	private Object castDate(String strAttrValue, SuperVO supervo, Attribute attr)
			throws BusinessException {
		if (attr.getLength() < StringHelper.length(strAttrValue)) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0091", attr.getDisplayName())
			/* @res "[{0}]�����ֶ���󳤶ȣ�" */);

		}

		try {
			String fullName = "nc.vo.pub.lang."
					+ attr.getDataType().getDescription();
			Class c = Class.forName(fullName);
			Constructor constructor = c.getConstructor(String.class);
			Object obj = constructor.newInstance(strAttrValue);

			return obj;
		} catch (Exception ex) {
			Logger.error(ex.getMessage());

			return strAttrValue;
		}
	}

	private Object castDouble(String strAttrValue, SuperVO supervo,
			Attribute attr) throws BusinessException {
		double dblAttrValue = 0;

		try {
			dblAttrValue = Double.parseDouble(strAttrValue);

			if (attr.getMinValue() != null
					&& Double.parseDouble(attr.getMinValue()) > dblAttrValue
					|| attr.getMaxValue() != null
					&& Double.parseDouble(attr.getMaxValue()) < dblAttrValue) {
				throw new BusinessException(ResHelper.getString(
						"6001dataimport", "06001dataimport0092",
						attr.getDisplayName(), attr.getMinValue(),
						attr.getMaxValue())
				/* @res "[{0}]�����ֶε����޻�����[[{1}],[{2}]]��" */);

			}

			String[] temp = strAttrValue.split("\\.");
			if (temp != null && temp.length > 1
					&& attr.getPrecise() < temp[1].length()) {
				throw new BusinessException(ResHelper.getString(
						"6001dataimport", "06001dataimport0093",
						attr.getDisplayName())
				/* @res "[{0}]�����ֶεľ��ȣ�" */);

			}

			UFDouble returnValue = new UFDouble(dblAttrValue, attr.getPrecise());

			return returnValue;
		} catch (NumberFormatException ex) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0109", attr.getDisplayName(),
					attr.getMinValue(), attr.getMaxValue())
			/* @res "[{0}]�����������ֶ����Ͳ�ƥ��;" */);
		} catch (IllegalArgumentException ex) {
			// ���������Ϣ
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0106", attr.getDisplayName())
			/* @res "[{0}]��д�Ĳ�����ȷ����ֵ���ͣ�" */);
		}
	}

	private Object castInt(String strAttrValue, SuperVO supervo, Attribute attr)
			throws BusinessException {
		long lAttrValue = 0;

		try {
			lAttrValue = Long.parseLong(strAttrValue);
		} catch (NumberFormatException e) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0109", attr.getDisplayName())
			/* @res "[{0}]�����������ֶ����Ͳ�ƥ�䣻" */);

		}

		if (attr.getMinValue() != null
				&& lAttrValue < Long.parseLong(attr.getMinValue())
				|| attr.getMaxValue() != null
				&& lAttrValue > Long.parseLong(attr.getMaxValue())) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0092", attr.getDisplayName())
			/* @res "[{0}]�����ֶε����޻����ޣ�" */);

		}

		return strAttrValue;
	}

	private Object castString(String strAttrValue, SuperVO supervo,
			Attribute attr) throws BusinessException {
		if (attr.getLength() < StringHelper.length(strAttrValue)) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0091", attr.getDisplayName())
			/* @res "[{0}]�����ֶ���󳤶ȣ�" */);

		}

		return strAttrValue;
	}

	/**
	 * 
	 * @param employeeFormatVOs
	 */
	private Map<String, EmployeeFormatVO> getMapForEmployeeFormatVO(
			List<EmployeeFormatVO> employeeFormatVOs) {
		Map<String, EmployeeFormatVO> map = new HashMap<String, EmployeeFormatVO>();
		for (EmployeeFormatVO vo : employeeFormatVOs) {
			map.put(vo.getDefname(), vo);
		}
		return map;

	}

	private Map<Integer, String> readFirstRow(Workbook workbook)
			throws BusinessException {
		Map<Integer, String> columnindexToName = new HashMap<Integer, String>();
		Sheet mainsheet = workbook.getSheetAt(0);
		Row firstrow = mainsheet.getRow(0);
		if (firstrow == null) {
			throw new BusinessException("First row must have column name.");
		}
		int columnnumber = firstrow.getLastCellNum();
		for (int columnIndex = 0; columnIndex <= columnnumber; columnIndex++) {
			Cell cell = firstrow.getCell(columnIndex);
			if (cell == null || StringUtils.isBlank(cell.getStringCellValue())) {
				continue;
			}
			// ��һ�б�����String
			if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
				// ���������ظ�
				if (cell.getStringCellValue() != null
						&& cell.getStringCellValue().equals(
								columnindexToName.values().contains(
										cell.getStringCellValue()))) {
					int index = this.getColumnIndex(columnindexToName,
							cell.getStringCellValue());
					throw new BusinessException(
							"Column names are duplicate, please check first row, column: "
									+ (columnIndex + 1) + " ," + index);
				}
				columnindexToName.put(columnIndex, cell.getStringCellValue());
			} else {
				throw new BusinessException("First row type should be string.");
			}
		}
		return columnindexToName;
	}

	/**
	 * 
	 * @param columnindexToName
	 * @param stringCellValue
	 * @return
	 */
	private int getColumnIndex(Map<Integer, String> columnindexToName,
			String stringCellValue) {
		for (Map.Entry<Integer, String> entry : columnindexToName.entrySet()) {
			if (stringCellValue != null
					&& stringCellValue.equals(entry.getValue())) {
				return entry.getKey();
			}

		}
		return -1;
	}

	/**
	 * 
	 * @param workbook
	 */
	private void checkSheetFormat(Workbook workbook) {
		if (workbook == null) {
			ExceptionUtils.wrappBusinessException("Please choose a excel!");
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			ExceptionUtils.wrappBusinessException(ResHelper.getString(
					"6001dataimport", "06001dataimport0014")
			/* @res "ѡ���ļ�û��ҳǩ��Ϣ��" */);
		}
	}

	/**
	 * ��ʾ����Ľ��
	 * 
	 * @param sum
	 * @author heqiaoa
	 * 
	 */
	@SuppressWarnings("restriction")
	private void showResultHint(int sum) {
		// FIXME ����Ԥ�����˴���ʱû�Ӷ���֧��
		String hintTitleStr = (0 != sum ? "�ɹ�����: " + sum + " ��" : "����ʧ��");
//		String hintDetailStr = (0 != sum ? "��Ա��������Ϊ��" + codesHint.toString()
//				: "û���ҵ��ܵ�����ļ����ļ����е�ͼƬ�ļ���Ӧ��Ϊ����Ա����+�ļ�����׺��ʽ��������123.PNG");
		if (sum > 0) {
			MessageDialog.showHintDlg(this.getEntranceUI(), hintTitleStr,
					String.valueOf(sum));
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		} else {
//			MessageDialog.showErrorDlg(this.getEntranceUI(), hintTitleStr,
//					hintDetailStr);
//			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		}
	}


	private void savePsndocVO(PsndocVO[] psndocVOs, int everyTimeSubmit)
			throws BusinessException {
		Map<Integer, List<PsndocVO>> psndocVOMap = new HashMap<Integer, List<PsndocVO>>();
		int j = 0;
		List<PsndocVO> psnList = null;
		for (int i = 0; i < psndocVOs.length; i++) {
			if (i / everyTimeSubmit == j) {
				psnList = new ArrayList<PsndocVO>();
				psndocVOMap.put(j, psnList);
				j++;
			}
			psnList.add(psndocVOs[i]);
		}

		// ÿeveryTimeSubmit���ύһ�Ρ�
		IPersistenceUpdate iperupdate = NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);
		for (Iterator<Entry<Integer, List<PsndocVO>>> ite = psndocVOMap
				.entrySet().iterator(); ite.hasNext();) {
			Entry<Integer, List<PsndocVO>> entry = ite.next();
			List<PsndocVO> psndocVOList = entry.getValue();
			iperupdate.updateVOArray(null,
					psndocVOList.toArray(new PsndocVO[0]), null, null);
		}
	}

	/**
	 * ��װPsndocVO����Ƭ��Ϣ
	 * 
	 * @param psnPhotoFileMap
	 * @return
	 * @throws BusinessException
	 */
	private PsndocVO[] assemblePsndocVO(Map<String, File> psnPhotoFileMap)
			throws BusinessException {
		if (psnPhotoFileMap.isEmpty()) {
			return null;
		}

		List<String> psnCodeList = new ArrayList<String>();
		for (Iterator<String> ite = psnPhotoFileMap.keySet().iterator(); ite
				.hasNext();) {
			String key = ite.next();
			psnCodeList.add(key);
		}

		PsndocVO[] psndocVOs = NCLocator.getInstance()
				.lookup(IPsndocQryService.class)
				.queryPsndocVOByCondition(psnCodeList.toArray(new String[0]));
		if (ArrayUtils.isEmpty(psndocVOs)) {
			return null;
		}

		for (int i = 0; i < psndocVOs.length; i++) {
			File photoFile = psnPhotoFileMap.get(psndocVOs[i].getCode());
			// ת��Ϊ���ļ�
			FileInputStream ins = null;
			try {
				ins = new FileInputStream(photoFile);
				byte[] bytes = inputStreamToByte(ins);
				if (bytes.length >= 204800) {
					throw new BusinessException("��Ա����:"
							+ psndocVOs[i].getCode() + " ����Ƭ���ܴ���200KB");
				}
				psndocVOs[i].setPhoto(bytes);

				// ��������ͼ
				byte[] imgData = VOUtils.transPreviewPhoto(bytes);
				psndocVOs[i].setPreviewphoto(imgData);
			} catch (BusinessException e) {
				throw new BusinessException("��Ա����:" + psndocVOs[i].getCode()
						+ " ����Ƭ���ܴ���200KB");
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			} finally {
				// IOUtil.close(ins);
				if (ins != null) {
					try {
						ins.close();
					} catch (IOException e) {
						Logger.error(e.getMessage(), e);
					}
				}
			}

			psndocVOs[i].setStatus(VOStatus.UPDATED);
		}

		return psndocVOs;
	}

	public byte[] inputStreamToByte(FileInputStream fileStream)
			throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = fileStream.read()) != -1) {
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2014-8-26 20:31:31<br>
	 * 
	 * @return Workbook
	 * @author Rocex Wang
	 * @param path
	 ***************************************************************************/
	protected Workbook openWorkbook(String path) {
		Workbook workbook = null;

		InputStream is = null;
		try {
			is = new FileInputStream(path);
			if (isExcel2003(path)) {
				workbook = new HSSFWorkbook(is);
			} else {
				workbook = new XSSFWorkbook(is);
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			ExceptionUtils.wrappException(ex);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return workbook;
	}

	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

}
