package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/***************************************************************************
 * HR本地化：插入本地化HR项目需要的人员基本信息通配型字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/
public class GlobalFieldStrategy extends AddFieldAbstractStrategy implements IAddLocalizationFieldStrategy {

	public GlobalFieldStrategy() {
		defdocMap = getDefdocList();
	}
	
	@Override
	public InfoSetVO[] addLocalField(InfoSetVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		for (InfoSetVO infoSet : vos) {
			if (infoSet.getInfoset_code()
					.equals(IAddLocalizationFieldStrategy.PERSONAL_INFO_TABLE)) {
				InfoItemVO[] bodyVOs = infoSet.getInfo_item();
				
				// 校验看是否字段已经加过，判断条件：判断编码是否以g_开头
				for (InfoItemVO bvo : bodyVOs) {
					if (bvo.getItem_code().startsWith("g_")) {
						throw new BusinessException("Global field already synced. If you face any discrepancy, please contact support.");
					}
				}
				
				ArrayList<InfoItemVO> newBodyVOsList = 
						new ArrayList<InfoItemVO>(Arrays.asList(bodyVOs));
				
				// Add Global Fields
				newBodyVOsList.add(addField("g_passportno", "Passport Number", "护照号", 0,
						101, "hrlocal-000001", "6007psn", UFBoolean.TRUE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("g_passportissuedate", "Passport Issue Date",
						"护照签发日期", 3, 19, "hrlocal-000002", "6007psn", UFBoolean.FALSE,
						UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("g_passportexpirydate", "Passport Expiry Date",
						"护照有效日期", 3, 19, "hrlocal-000003", "6007psn", UFBoolean.FALSE,
						UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("g_paymentmode", "Payment Mode", "付款方式", 5,
						20, "hrlocal-000004", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL001")));
				newBodyVOsList.add(addField("g_bank", "Bank Name", "银行", 5, 20, "hrlocal-000005",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0,
						defdocMap.get("SEALOCAL002")));
				newBodyVOsList.add(addField("g_bankcode", "Bank Code", "银行编码", 0, 101,
						"hrlocal-000006", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("g_bankbranchcode", "Bank Branch Code", "银行支行编码", 
						0, 101,	"hrlocal-000007", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("g_bankacno", "Bank A/C No", "银行账号", 0, 101,
						"hrlocal-000008", "6007psn", UFBoolean.TRUE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				
				infoSet.setInfo_item(newBodyVOsList.toArray(new InfoItemVO[0]));
			}
		}
		return vos;
	}

}
