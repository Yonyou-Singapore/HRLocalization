package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;

/**
 * 批量增加海外公共薪资项目
 * @author weiningc
 *
 */
public interface ISeaLocalItemManageService {
	
	/**
	 * @param vo 
	 * @throws BusinessException 
	 * @throws Exception 
	 * 
	 */
	void saveBactchItemForSeaLocal(WaItemVO vo) throws BusinessException, Exception;

}
