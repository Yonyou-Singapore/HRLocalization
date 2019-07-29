package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.SqlUtils;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.employee.IEmployeeImportService;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.impl.Attribute;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.pub.hi.employeeimport.vo.EmployeeFormatVO;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.hi.psndoc.view.ImportEmployeePanel;
import nc.ui.hi.psndoc.view.PsndocListView;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.hi.psndoc.Glbdef11VO;
import nc.vo.hi.psndoc.Glbdef12VO;
import nc.vo.hi.psndoc.Glbdef13VO;
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
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SuppressWarnings("restriction")
public class ImportEmployeeAction extends HrAction {
	private static final long serialVersionUID = 1L;
	
	private PsndocListView listView;
	public static final String funcode = "60070employee";
	//每次向后台提交的最大数
	public static int everyTimeSubmit = 100;
	
	//CCDC人员信息导入组织为劳工所属组织  PK_Group
	private String pk_group;
	private String pk_org;
	


	// 参照缓存 key: funcode + value value:PKvalue
	private HashMap<String, String> refValueAndKey = new HashMap<String, String>();

	public ImportEmployeeAction() {
		super();
		setBtnName(ResHelper.getString("hrhi_localimport",
				"hrhi_localimport001")/* @res "导入人员信息" */);
		setCode(IActionCode.IMPORT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ImportEmployeePanel employeePanel = new ImportEmployeePanel();

		if (employeePanel.getChooser()
				.showDialog(
						null,
						ResHelper.getString("hrhi_localimport",
								"hrhi_localimport002")/* @res "导入文件" */) == UIFileChooser.CANCEL_OPTION) {
			return;
		}
		File file = employeePanel.getChooser().getSelectedFile();

		if (file == null) {
			putValue(MESSAGE_AFTER_ACTION,
					ResHelper.getString("6001uif2", "06001uif20002") /*
																	 * @res
																	 * "已取消。"
																	 */);
			return;
		}
		String pk_group = getModel().getContext().getPk_group();
		String pk_org = getModel().getContext().getPk_org();
		this.setPk_group(pk_group);
		if(pk_org == null) {
			ExceptionUtils.wrappBusinessException("Please choose organization.");
		}
		this.setPk_org(pk_org);
		String path = file.getPath();
		try{
			Workbook workbook = this.openWorkbook(path);
			// 1.只读取第一个sheet,且数据不能为空
			this.checkSheetFormat(workbook);
			// 2.读取excel
			this.readExcel(workbook);
		} finally {
			System.gc();
		}

		
	}

	/**
	 * 
	 * @param workbook
	 * @throws Exception
	 */
	private void readExcel(Workbook workbook) throws Exception {
		// 读取第一行信息,字段信息
		Map<Integer, String> firstRowColumn = this.readFirstRow(workbook);

		// 1. 查询EmployeeFormatVO
		List<EmployeeFormatVO> employeeFormatVOs = NCLocator
				.getInstance()
				.lookup(IEmployeeImportService.class)
				.queryEmployeeFormatVO(
						new ArrayList<String>(firstRowColumn.values())
								.toArray(new String[0]));
		Map<Integer, EmployeeFormatVO> employeeFormatMap = this
				.convertListToMap(employeeFormatVOs, firstRowColumn);
		//校验是否全部列都已匹配
		this.checkAllColumnIsContain(employeeFormatMap, firstRowColumn);
		// 2. 解析第一个页签
		List<Map<String, SuperVO>> analysisExcelToVO = this.analysisExcelToVO(workbook.getSheetAt(0), employeeFormatVOs,
				employeeFormatMap);
		//3. 保存或更新解析后的VO
		int num = this.processVO(analysisExcelToVO, firstRowColumn);
		
		//4. 显示结果
		showResultHint(num);

	}

	private int processVO(List<Map<String, SuperVO>> vos, Map<Integer, String> firstRowColumn) {
		if(vos == null || vos.size() < 1) {
			return 0;
		}
		//1. 查询哪些人员update， 哪些人员inert
		List<PsndocVO> psnvolist = new ArrayList<PsndocVO>();
		for(Map<String, SuperVO> supervo : vos) {
			PsndocVO values = (PsndocVO) supervo.get("nc.vo.hi.psndoc.PsndocVO");
			psnvolist.add(values);
		}
		//查询人员
		PsndocVO[] psndocVOs = this.queryPsnVOByCode(psnvolist);
		//调用接口DB操作
		return this.callInterfaceToDB(vos, psndocVOs, firstRowColumn);
	}
	

	private int callInterfaceToDB(List<Map<String, SuperVO>> vos,
			PsndocVO[] psndocVOs, Map<Integer, String> firstRowColumn) {
		int num=0;
		//组装PsndocAggVO 
		List<PsndocAggVO> aggvos = this.buildPsndocAggVOs(vos, psndocVOs);
		//判断哪些是inert 哪些是update 且update时只支持单表更新
		List<PsndocAggVO> inertaggvos = this.seperateVOsForInsert(aggvos);
		List<SuperVO> updateaggvos = this.seperateVOsForUpdate(aggvos, firstRowColumn);
		//调用接口
		IEmployeeImportService service = NCLocator.getInstance().lookup(IEmployeeImportService.class);
		//inert
		try {
			if(inertaggvos != null && inertaggvos.size() > 0 && 
					updateaggvos != null && updateaggvos.size() > 0) {
				ExceptionUtils.wrappBusinessException("Error: Cannot have both inserted data and updated data.");
			}
			if(inertaggvos != null && inertaggvos.size() > 0) {
				int len = inertaggvos.size();
				// 每次同步200个，减轻服务器压力
				int cnt = len / everyTimeSubmit;
				for(int i=0; i<=cnt; i++) {
					if(i<cnt) {
						inertaggvos.subList(i * everyTimeSubmit, (i + 1) * everyTimeSubmit);
					} else{
						inertaggvos.subList(cnt * 100, len);
					}
					num = service.saveImportVOs(inertaggvos.toArray(new PsndocAggVO[0]));
				}
			}
			if(updateaggvos != null && updateaggvos.size() > 0) {
				int len = inertaggvos.size();
				// 每次同步100个，减轻服务器压力
				int cnt = len / everyTimeSubmit;
				for(int i=0; i<=cnt; i++) {
					if(i<cnt) {
						updateaggvos.subList(i * everyTimeSubmit, (i + 1) * everyTimeSubmit);
					} else{
						updateaggvos.subList(cnt * 100, len);
					}
				}
				num = service.updateImportVOs(updateaggvos.toArray(new SuperVO[0]));
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		//update
		//转入人员档案   TODO
		return num;
	}
	
	/**
	 * 需要补全VO 如果是update 需要补全， 如果是insert 必要字段补全
	 * @param vos
	 * @param psndocVOs
	 * @return
	 */
	private List<PsndocAggVO> buildPsndocAggVOs(List<Map<String, SuperVO>> vos,
			PsndocVO[] psndocVOs) {
		//根据工作记录查询aggvo
		Map<String, PsndocVO> queryedPsnVOmap = this.queryPsnVOByPKsToMap(psndocVOs);
		
		List<PsndocAggVO> aggvos = new ArrayList<PsndocAggVO>();
		for(Map<String, SuperVO> supervo : vos) {
			PsndocAggVO aggvo = new PsndocAggVO();
			for(Map.Entry<String, SuperVO> entry : supervo.entrySet()) {
				String classname = entry.getKey();
				SuperVO value = entry.getValue();
				String code = (String)value.getAttributeValue("code");
				//主集 PsndocVO
				if("nc.vo.hi.psndoc.PsndocVO".equals(classname)) {
					if(queryedPsnVOmap != null && queryedPsnVOmap.keySet().contains(code)) {
						this.setPsndocDefaultValue((PsndocVO)value, queryedPsnVOmap.get(code));
					} else{
						this.setPsndocDefaultValueForNew((PsndocVO)value, queryedPsnVOmap);
					}
					aggvo.setParentVO(value);
				} else {
					//子集
					this.setSubDefaultValue(value, (PsndocVO)supervo.get("nc.vo.hi.psndoc.PsndocVO"), queryedPsnVOmap);
					SuperVO[] arraysupervo = this.parseSuperVO(value);
					aggvo.setTableVO(value.getTableName(), arraysupervo);
				}
			}
			aggvos.add(aggvo);
		}
		return aggvos;
	}

	private void setPsndocDefaultValueForNew(PsndocVO value,
			Map<String, PsndocVO> queryedPsnaggVOmap) {
		value.setAttributeValue(PsndocVO.PK_GROUP, pk_group);
		value.setAttributeValue(PsndocVO.PK_ORG, pk_org);
		value.setPk_hrorg(pk_org);
		if(((PsndocVO)value).getIdtype() == null) {
			((PsndocVO)value).setIdtype("0001B210000000000J57");//FIN
		} 
		if(((PsndocVO)value).getCountry() == null) {
			((PsndocVO)value).setCountry("0001Z010000000079UKB");//国家 毛里求斯
		}
		if(((PsndocVO)value).getAttributeValue("glbdef14") == null) {
			((PsndocVO)value).setAttributeValue("glbdef14", "1001B2100000000004VE");//国籍 毛里求斯
		}
		if(((PsndocVO)value).getId() == null) {
			((PsndocVO)value).setId(value.getCode());//证件号码设置为员工编号
		}
		//是否干部 等等字段
		if(value.getIscadre() == null) {
			value.setIscadre(UFBoolean.FALSE);
		}
		if(value.getIshiskeypsn() == null) {
			value.setIshiskeypsn(UFBoolean.FALSE);
		}
		if(value.getIshisleader() == null) {
			value.setIshisleader(UFBoolean.FALSE);
		}
		if(value.getIsshopassist() == null) {
			value.setIsshopassist(UFBoolean.FALSE);
		}
		value.setStatus(VOStatus.NEW);
		
	}

	private void setPsndocDefaultValue(PsndocVO value, PsndocVO parentVO) {
		String[] attributeNames = value.getAttributeNames();
		//update 主表补齐数据
		for(String attr : attributeNames) {
			if(value.getAttributeValue(attr) == null) {
				value.setAttributeValue(attr, parentVO.getAttributeValue(attr));
			}
		}
		//审计信息
		value.setModifier(getModel().getContext().getPk_loginUser());
		value.setModifiedtime(new UFDateTime());
		value.setStatus(VOStatus.UPDATED);
		
	}

	private Map<String, PsndocVO> queryPsnVOByPKsToMap(
			PsndocVO[] psndocVOs) {
		Map<String, PsndocVO> psnvosmap = null;
		if(psndocVOs == null || psndocVOs.length < 1) {
			return null;
		}
		if(psndocVOs != null) {
			psnvosmap = new HashMap<String, PsndocVO>();
			for(PsndocVO vo : psndocVOs) {
				psnvosmap.put(vo.getCode(), vo);
			}
		}
		return psnvosmap;
	}
	
	/**
	 * 
	 * @param value    要填充的vo
	 * @param superVO  获取code
	 * @param queryedPsnaggVOmap   填充默认值，的情况
	 */
	private void setSubDefaultValue(SuperVO value, PsndocVO superVO, Map<String, PsndocVO> queryedPsnaggVOmap) {
		String code = superVO.getCode();
		if(queryedPsnaggVOmap != null && !StringUtils.isBlank(superVO.getCode()) && queryedPsnaggVOmap.get(code) != null) {
			//更新的情况  增量更新
			String pk_psndoc = queryedPsnaggVOmap.get(code).getPk_psndoc();
			value.setAttributeValue("pk_psndoc", pk_psndoc);
			//最新纪录
			value.setAttributeValue("lastflag", UFBoolean.TRUE);
			value.setAttributeValue("pk_org", pk_org);
			value.setAttributeValue("pk_group", pk_group);
			if(value instanceof PsnJobVO) {
				this.setPsnJobDefaultValue((PsnJobVO)value, code, false);
			} else if(value instanceof PsnOrgVO) {
				//是否转入人员档案 
				((PsnOrgVO) value).setIndocflag(UFBoolean.TRUE); 
			}
			//审计信息
			value.setAttributeValue("modifier", getModel().getContext().getPk_loginUser());
			value.setAttributeValue("modifiedtime", new UFDateTime());
			value.setStatus(VOStatus.NEW);
		} else {
			if(value instanceof PsnJobVO) {
				this.setPsnJobDefaultValue((PsnJobVO)value, code, true);
			} else if(value instanceof PsnOrgVO) {
				((PsnOrgVO) value).setPk_org(pk_org);//MP
				((PsnOrgVO) value).setPk_group(pk_group);//MP
				//是否转入人员档案 
				((PsnOrgVO) value).setIndocflag(UFBoolean.FALSE);
			} else {
//				value.setAttributeValue(arg0, arg1)
			}
			//最新纪录
			value.setAttributeValue("pk_group", pk_group);
			value.setAttributeValue("pk_org", pk_org);
			value.setAttributeValue("lastflag", UFBoolean.TRUE);
			value.setStatus(VOStatus.NEW);
		}
		
	}

	private void setPsnJobDefaultValue(PsnJobVO value, String code, boolean isMainJob) {
		//员工号
		((PsnJobVO) value).setClerkcode(code);
		((PsnJobVO) value).setPk_org(pk_org);//MP
		((PsnJobVO) value).setPk_group(pk_group);//MP
		//人员任职ID
		((PsnJobVO) value).setAssgid(Integer.valueOf(1));
		//人员类别
		((PsnJobVO) value).setPsntype(Integer.valueOf(0));
		//显示顺序
		((PsnJobVO) value).setShoworder(Integer.valueOf(9999999));
		//pk_hrorg
		((PsnJobVO) value).setPk_hrorg(pk_org);//MP
		//是否主职
		((PsnJobVO) value).setIsmainjob(UFBoolean.valueOf(isMainJob));
		((PsnJobVO) value).setRecordnum(Integer.valueOf(9999999));
		//异动事件
		((PsnJobVO) value).setTrnsevent(Integer.valueOf(1));//入职
		//人员类别
		((PsnJobVO) value).setPk_psncl("1001B210000000000144"); //合同工
		
	}

	private SuperVO[] parseSuperVO(SuperVO value) {
		SuperVO[] arraysupervo = null;
//		if(value instanceof PsnJobVO) {
//			arraysupervo = new PsnJobVO[]{(PsnJobVO) value};
//		} else if(value instanceof Glbdef4VO) {
//			arraysupervo = new Glbdef4VO[]{(Glbdef4VO) value};
//		} else if(value instanceof Glbdef3VO) {
//			arraysupervo = new Glbdef3VO[]{(Glbdef3VO) value};
//		} else if(value instanceof Glbdef7VO) {
//			arraysupervo = new Glbdef7VO[]{(Glbdef7VO) value};
//		} else if(value instanceof Glbdef9VO) {
//			arraysupervo = new Glbdef9VO[]{(Glbdef9VO) value};
//		} else if(value instanceof PsnOrgVO) {
//			arraysupervo = new PsnOrgVO[]{(PsnOrgVO) value};
//		} else if(value instanceof Glbdef12VO) {
//			arraysupervo = new Glbdef12VO[]{(Glbdef12VO) value};
//		} else if(value instanceof Glbdef11VO) {
//			arraysupervo = new Glbdef11VO[]{(Glbdef11VO) value};
//		} else if(value instanceof Glbdef13VO) {
//			arraysupervo = new Glbdef13VO[]{(Glbdef13VO) value};
//		}
		arraysupervo = (SuperVO[]) Array.newInstance(value.getClass(), 1);
		arraysupervo[0] = value;
		return arraysupervo;
	}

	private List<SuperVO> seperateVOsForUpdate(List<PsndocAggVO> aggvos,
			Map<Integer, String> firstRowColumn) {
		if(aggvos == null || aggvos.size() < 1) {
			return null;
		}
		List<SuperVO> supervos = new ArrayList<SuperVO>();
		String subtablename = null;
		for(PsndocAggVO agg : aggvos) {
			PsndocVO parentVO = agg.getParentVO();
			String pk = parentVO.getPk_psndoc();
			if(pk == null) {
				continue;
			}
			if(StringUtils.isEmpty(parentVO.getCode())) {
				continue;
			}
			String[] tableNames = agg.getTableNames();
			if(tableNames != null && tableNames.length > 0) {
				for(String name : tableNames) {
					SuperVO[] tableVO = agg.getTableVO(name);
					if(tableVO != null && tableVO.length > 0) {
						subtablename = tableVO[0].getTableName();
						if(!StringUtils.isBlank(subtablename) && !subtablename.equals(name)) {
							ExceptionUtils.wrappBusinessException("Only suport single table update, subtablename1 :" +
									subtablename + " ,subtablename2: " + name);
						}
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
			if(StringUtils.isBlank(vo.getParentVO().getCode())) {
				continue;
			}
			if(vo.getParentVO().getPk_psndoc() == null) {
				psninsertlist.add(vo);
			} 
		}
		
		return psninsertlist;
	}
	
	
	/**
	 * 人员编码==》PK
	 * @param psnvolist
	 * @return
	 */
	private PsndocVO[] queryPsnVOByCode(List<PsndocVO> psnvolist) {
		List<String> psncodes = new ArrayList<String>();
		for(PsndocVO vo : psnvolist) {
			if(!StringUtils.isBlank(vo.getCode())) {
				psncodes.add(vo.getCode());
			}
		}
		PsndocVO[] psndocvos = null;
		IPsndocQryService service = NCLocator.getInstance()
					.lookup(IPsndocQryService.class);
		try {
			psndocvos = service.queryPsndocVOByCondition(psncodes.toArray(new String[0]));
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("Failed: query psn info by code failed.");
		}
		
		return psndocvos;
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
	 *            列index和EmployeeFormatVO
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
		// 获取需要实例化的class

		int count = sheetdata[0].length;
		if (count == 0) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0102")
			/* @res "信息填写不全，请去原文件修改" */);
		}
		
		Map<String, Class<? extends SuperVO>> allclassname = this
				.getInstanceClass(employeeFormatVOs);

		List<Map<String, SuperVO>> intanceArrayAllClass = new ArrayList<Map<String, SuperVO>>();
		
		sb = new StringBuffer();
		refValueAndKey.clear();
		// 按行遍历
		for (int row = 0; row < sheetdata.length; row++) {
			// 每行VO全部实例化
			Map<String, SuperVO> intanceAllClass = this
					.intanceAllClass(allclassname);
			// 先按照最简单的场景,每行一个主集，一个子集
			for (int column = 0; column < sheetdata[row].length; column++) {
				if(sheetdata[row][column] == null) {
					continue;
				}
				EmployeeFormatVO formatvo = employeeFormatMap.get(column);
				if(formatvo == null) {
					ExceptionUtils.wrappBusinessException("Please check column match, Column Number: "+(column + 1));
				}
				String fullclassname = formatvo.getFullclassname();

				Boolean isContainClass = allclassname.get(fullclassname) == null ? false
						: true;
				if (isContainClass) {
					// 解析属性
					IBean bean = MDBaseQueryFacade.getInstance()
							.getBeanByFullClassName(fullclassname);
					this.setAttributeValue(intanceAllClass.get(fullclassname),
							formatvo.getItemcode(), formatvo.getShortname(), sheetdata[row][column],
							bean, column, sb);
				}
			}
			intanceArrayAllClass.add(intanceAllClass);

		}
		if(sb.length() > 0) {
			ExceptionUtils.wrappBusinessException(sb.toString());
		}
		return intanceArrayAllClass;

	}


	/**
	 * 所有的列是否都对应
	 * @param employeeFormatMap
	 * @param firstRowColumn
	 */
	private void checkAllColumnIsContain(
			Map<Integer, EmployeeFormatVO> employeeFormatMap,
			Map<Integer, String> firstRowColumn) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Integer, String> entry : firstRowColumn.entrySet()) {
			Integer columnindex = entry.getKey();
			if(employeeFormatMap.get(columnindex) == null) {
				sb.append("[" + (columnindex+1) + ", " + entry.getValue() + "], ");
			}
		}
		if(sb.length() > 0) {
			ExceptionUtils.wrappBusinessException("Please check column match, Column Number and Name: "+ sb.toString());
		}
			
	}

	/**
	 * 实例化全部的class
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
	 * 实例化class
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
	 * 导入数据默认为String
	 * 
	 * @param sheet
	 * @return
	 * @throws BusinessException
	 */
	private String[][] getSheetData(Sheet sheet) throws BusinessException {

		int rownums = sheet.getPhysicalNumberOfRows();
		String[][] sheetdata = new String[rownums - 1][sheet.getRow(1)
				.getLastCellNum()];// 行列

		for (int rowindex = 1; rowindex < rownums; rowindex++) {// 从第二行开始
																// 第二行开始为数据
			Row row = sheet.getRow(rowindex);
			if (row == null) {
				continue;
			}
			short columnnums = row.getLastCellNum();
			for (int columnindex = 0; columnindex < columnnums; columnindex++) {
				Cell cell = row.getCell(columnindex);
				if (cell == null || (cell.getCellType() == cell.CELL_TYPE_STRING && StringUtils.isBlank(cell.getStringCellValue()))) {
					continue;
				}
				
				switch (cell.getCellType()) {
				// String
				case HSSFCell.CELL_TYPE_STRING:
					sheetdata[rowindex - 1][columnindex] = cell
							.getStringCellValue().trim();
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					//是否日期格式
					boolean cellDateFormatted = HSSFDateUtil.isCellDateFormatted(cell);
					if(cellDateFormatted) {
						SimpleDateFormat sdf = null;  
						 if (cell.getCellStyle().getDataFormat() == HSSFDataFormat  
			                        .getBuiltinFormat("h:mm")) {  
			                    sdf = new SimpleDateFormat("HH:mm");  
			                } else {// 日期  
			                    sdf = new SimpleDateFormat("yyyy-MM-dd");  
			                } 
						 Date date = cell.getDateCellValue();  
			             String format = sdf.format(date);  
			             sheetdata[rowindex - 1][columnindex] = format;
					} else {
						//不支持数字格式。 会有小数点的存在
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						sheetdata[rowindex - 1][columnindex] = cell.getStringCellValue();
					}
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					continue;
				default:
					// 待扩展
					throw new BusinessException(
							"Excel cell format only support String and Date,Please check row :"
									+ rowindex + ",column: "
									+ (columnindex + 1));
				}
			}
		}
		return sheetdata;
	}

	protected void setAttributeValue(SuperVO supervo, String strAttrName, String fixedValue,
			String strAttrValue, IBean bean, int columnIndex, StringBuffer errmsg)
			throws BusinessException {

		Attribute attr = (Attribute) bean.getAttributeByName(strAttrName);
		if (attr == null) {
			supervo.setAttributeValue(strAttrName, strAttrValue);
			return;
		}

		if (supervo.getPKFieldName().equals(attr.getName())) {
			return;
		}

		// 先对元数据上没设定非空的非空项进行校验
//		IDataIOHookPublic hookPublic = DefaultHookPublic.getInstance(funcode);
//		List<String> listNotNullColumn = hookPublic.getNotNullColumn("");

//		if (!listNotNullColumn.isEmpty()
//				&& listNotNullColumn.contains(attr.getName())
//				&& StringUtils.isEmpty(strAttrValue)) {
//			throw new BusinessException(ResHelper.getString("6001dataimport",
//					"06001dataimport0034", attr.getDisplayName())
//			/* @res "[{0}]不能为空；" */);
//		}

		// cast data
		try {
			Object attrValue = this.castDatasToAttributeValue(supervo, strAttrName,
					strAttrValue, bean, attr);

			supervo.setAttributeValue(strAttrName, attrValue);
			//若有固定值， 强制赋值为固定值 fixedValue==>>shortname
			if(!StringUtils.isBlank(fixedValue)) {
				supervo.setAttributeValue(strAttrName, fixedValue);
			}
		} catch (BusinessException e) {
			errmsg.append(strAttrValue + ResHelper.getString("6001dataimport",
					"06001dataimport0035", attr.getDisplayName())
			/* @res "[{0}]值与类型不匹配；" */);
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
		} else if (type.getTypeType() == IType.ENUM) // 枚举
		{
			return castEnum(strAttrValue, supervo, attr);
		} else if (type.getTypeType() == IType.ENTITY
				|| type.getTypeType() == IType.REF)// 参照
		{
			return castRef(strAttrValue, supervo, attr, bean);
		} else {
			return strAttrValue;
		}
	}

	private Object castRef(String strAttrValue, SuperVO superVO,
			Attribute attr, IBean bean) throws BusinessException {
		AbstractRefModel refModel = this.getRefModel(pk_org, attr.getRefModelName());
		if (refModel == null) {
			return null;
		}

		// 构造参照编码/名称与主键Map。已经翻译过的参照不再做翻译。将上级参照主键+参照编码/名称作为key
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
			// 先用code匹配，如果没有匹配到，再用name去匹配
			refModel.matchData(new String[] { refModel.getRefCodeField(),
					refModel.getRefNameField() }, new String[] { strAttrValue });

			strPkValues = refModel.getPkValues();
		}

		if (strPkValues == null || strPkValues.length == 0
				|| strPkValues[0].equals("")) {
			refValueAndKey.put(refKey + strAttrValue, "");
			throw new BusinessException(strAttrValue + ", " + ResHelper.getString("6001dataimport",
					"06001dataimport0033", attr.getDisplayName())
			/* @res "[{0}]参照设置不正确，未匹配到数据；" */);
		} else if (strPkValues.length > 1) {
			throw new BusinessException(strAttrValue + ResHelper.getString("6001dataimport",
					"06001dataimport0084", attr.getDisplayName())
			/* @res "[{0}]参照到的值不唯一；" */);
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
		refModel.setPk_org(pk_org); //组织级   MP组织 PK

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

		// 处理错误信息
		throw new BusinessException(ResHelper.getString("6001dataimport",
				"06001dataimport0032", attr.getDisplayName())
		/* @res "[{0}]枚举值设置不正确；" */);

	}

	private Object castDate(String strAttrValue, SuperVO supervo, Attribute attr)
			throws BusinessException {
		if (attr.getLength() < StringHelper.length(strAttrValue)) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0091", attr.getDisplayName())
			/* @res "[{0}]超出字段最大长度；" */);

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
				/* @res "[{0}]超出字段的上限或下限[[{1}],[{2}]]；" */);

			}

			String[] temp = strAttrValue.split("\\.");
			if (temp != null && temp.length > 1
					&& attr.getPrecise() < temp[1].length()) {
				throw new BusinessException(ResHelper.getString(
						"6001dataimport", "06001dataimport0093",
						attr.getDisplayName())
				/* @res "[{0}]超出字段的精度；" */);

			}

			UFDouble returnValue = new UFDouble(dblAttrValue, attr.getPrecise());

			return returnValue;
		} catch (NumberFormatException ex) {
			throw new BusinessException(strAttrValue + ResHelper.getString("6001dataimport",
					"06001dataimport0109", attr.getDisplayName(),
					attr.getMinValue(), attr.getMaxValue())
			/* @res "[{0}]所填数据与字段类型不匹配;" */);
		} catch (IllegalArgumentException ex) {
			// 处理错误信息
			throw new BusinessException(strAttrValue + ResHelper.getString("6001dataimport",
					"06001dataimport0106", attr.getDisplayName())
			/* @res "[{0}]填写的不是正确的数值类型；" */);
		}
	}

	private Object castInt(String strAttrValue, SuperVO supervo, Attribute attr)
			throws BusinessException {
		long lAttrValue = 0;

		try {
			lAttrValue = Long.parseLong(strAttrValue);
		} catch (NumberFormatException e) {
			throw new BusinessException(strAttrValue + ResHelper.getString("6001dataimport",
					"06001dataimport0109", attr.getDisplayName())
			/* @res "[{0}]所填数据与字段类型不匹配；" */);

		}

		if (attr.getMinValue() != null
				&& lAttrValue < Long.parseLong(attr.getMinValue())
				|| attr.getMaxValue() != null
				&& lAttrValue > Long.parseLong(attr.getMaxValue())) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0092", attr.getDisplayName())
			/* @res "[{0}]超出字段的上限或下限；" */);

		}

		return strAttrValue;
	}

	private Object castString(String strAttrValue, SuperVO supervo,
			Attribute attr) throws BusinessException {
		if (attr.getLength() < StringHelper.length(strAttrValue)) {
			throw new BusinessException(ResHelper.getString("6001dataimport",
					"06001dataimport0091", attr.getDisplayName())
			/* @res "[{0}]超出字段最大长度；" */);

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
			// 第一行必须是String
			if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
				// 列名不能重复
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
			/* @res "选择文件没有页签信息！" */);
		}
	}

	/**
	 * 显示导入的结果
	 * 
	 * @param sum
	 * @author heqiaoa
	 * 
	 */
	@SuppressWarnings("restriction")
	private void showResultHint(int sum) {
		// FIXME 高能预警，此处暂时没加多语支持
		String hintTitleStr = "Success import";
		if (sum > 0) {
			MessageDialog.showHintDlg(this.getEntranceUI(), hintTitleStr,
					String.valueOf(sum) );
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		} else {
//			MessageDialog.showErrorDlg(this.getEntranceUI(), hintTitleStr,
//					hintDetailStr);
//			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		}
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

	public PsndocListView getListView() {
		return listView;
	}

	public void setListView(PsndocListView listView) {
		this.listView = listView;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	

	
	
}
