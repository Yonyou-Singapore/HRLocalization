package nc.itf.hi.employee;

import java.util.List;

import nc.md.model.MetaDataException;
import nc.pub.hi.employeeimport.vo.EmployeeFormatVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * 
 * @author weiningc
 */
public interface IEmployeeImportService {
	/**
	 * 将excel转化为ExcelVO,对应元数据，列名，列全名，classname，参照范围(Reference ID)
	 * @param workbook
	 * @return
	 */
	List<EmployeeFormatVO> queryEmployeeFormatVO(String[] strings) throws BusinessException;
	
	/**
	 * 
	 * @param aggvos
	 * @return
	 * @throws BusinessException 
	 */
	int saveImportVOs(PsndocAggVO[] aggvos) throws BusinessException;

	int updateImportVOs(SuperVO[] updateaggvos) throws MetaDataException;
}
