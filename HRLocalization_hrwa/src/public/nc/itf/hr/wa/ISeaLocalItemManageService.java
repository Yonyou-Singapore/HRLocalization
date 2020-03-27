package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;

/**
 * 批量增加海外公共薪资项目
 * @author weiningc
 *
 */
public interface ISeaLocalItemManageService {

	void saveBactchItemForSeaLocal(WaItemVO vo, String countryitem) throws BusinessException, Exception;

}
