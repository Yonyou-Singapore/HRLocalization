package nc.itf.hr.infoset.localization;

import nc.vo.hr.infoset.InfoSetVO;

/***************************************************************************
 * 信息集本地化添加预置字段策略接口 <br>
 * Created on 2018-10-02 14:44:30pm
 * @author Ethan Wu
 ***************************************************************************/
public interface IAddLocalizationFieldStrategy {

	public static final String PERSONAL_INFO_TABLE = "bd_psndoc";
	
	public InfoSetVO[] addLocalField(InfoSetVO[] vos);
}
