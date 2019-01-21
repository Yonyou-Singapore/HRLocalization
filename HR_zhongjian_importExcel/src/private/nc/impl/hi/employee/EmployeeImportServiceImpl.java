package nc.impl.hi.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.employee.IEmployeeImportService;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.hi.employeeimport.vo.EmployeeFormatVO;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.dataio.excel.ExcelVO;
import nc.vo.pp.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.pub.SqlBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class EmployeeImportServiceImpl implements IEmployeeImportService {

	private BaseDAO dao = null;

	public EmployeeImportServiceImpl() {
		if (dao == null) {
			dao = new BaseDAO();
		}
	}

	@Override
	public List<EmployeeFormatVO> queryEmployeeFormatVO(String[] columnNames)
			throws BusinessException {
		List<EmployeeFormatVO> results = null;
		SqlBuilder sb = new SqlBuilder();
		sb.append(" deflistcode", EmployeeFormatVO.DEFDOCLISTCODE);
		sb.append(" and defname", columnNames);
		try {
			results = (List<EmployeeFormatVO>) dao.retrieveByClause(
					EmployeeFormatVO.class, sb.toString());
		} catch (DAOException e) {
			ExceptionUtils.wrapException(
					"query employee failed! please contact consultant.", e);
		}
		return results;
	}

	@Override
	public int saveImportVOs(PsndocAggVO[] aggvos) throws BusinessException {
		int num = 0;
		if (aggvos == null || aggvos.length < 1) {
			return num;
		}
		IPsndocService service = NCLocator.getInstance().lookup(
				IPsndocService.class);
		for (PsndocAggVO agg : aggvos) {
			service.savePsndocForImport(agg);
			num++;
		}
		return num;
	}

	@Override
	public int updateImportVOs(SuperVO[] updateaggvos) throws MetaDataException {
		int num = 0;
		IMDPersistenceService service = MDPersistenceService
				.lookupPersistenceService();
		//更新的字段
		String[] attributeNames = updateaggvos[0].getAttributeNames();
		List<String> fields = new ArrayList<String>();
		for(String attr : attributeNames) {
			if(PsndocVO.PK_PSNDOC.equals(attr) || PsndocVO.CODE.equals(attr) || PsndocVO.PK_ORG.equals(attr) 
					|| PsndocVO.PK_GROUP.equals(attr)) {
				continue;
			}
			if(updateaggvos[0].getAttributeValue(attr) != null) {
				fields.add(attr);
			}
		}
		//更新主集
		if(fields.size() < 0) {
			return 0;
		}
		if(fields.size() > 0 && updateaggvos instanceof PsndocVO[]) {
			service.updateBillWithAttrs(updateaggvos, fields.toArray(new String[0]));
			num = updateaggvos.length;
		} else {
			//子集为增量保存
			service.saveBill(updateaggvos);
			num = updateaggvos.length;
		}
		return num;
	}

}
