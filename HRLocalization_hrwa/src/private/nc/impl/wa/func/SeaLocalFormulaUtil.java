package nc.impl.wa.func;

import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.PropertyEnumVO;

// Uitility class for SEA Localization formula
public class SeaLocalFormulaUtil {
	// Based on add or minus item, concat the salary item string
	public static String getConcatString(WaClassItemVO[] items) throws BusinessException {
		if (items != null && items.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (WaClassItemVO item : items) {
				// 假如不是减项 默认为增项
				if (item.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
					sb.append(" - coalesce(wa_data." + item.getItemkey() + ",0)");
				} else  {
					sb.append(" + coalesce(wa_data." + item.getItemkey() + ",0)");
				}
			}
			return sb.toString();
		} else {
			throw new BusinessException("Formula Calculation Error: Normal Renumeration or Bonus Renumeration Items not specified");
		}
	}
}
