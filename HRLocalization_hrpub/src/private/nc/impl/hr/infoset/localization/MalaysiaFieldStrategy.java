package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/***************************************************************************
 * HR本地化：插入马来西亚HR项目需要的人员基本信息字段<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/

public class MalaysiaFieldStrategy extends AddFieldAbstractStrategy implements IAddLocalizationFieldStrategy {

	public MalaysiaFieldStrategy() {
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
				
				// 校验看是否字段已经加过，判断条件：判断编码是否以m_开头
				for (InfoItemVO bvo : bodyVOs) {
					if (bvo.getItem_code().startsWith("m_")) {
						throw new BusinessException("Malaysia field already synced. If you face any discrepancy, please contact support.");
					}
				}
				
				ArrayList<InfoItemVO> newBodyVOsList = 
						new ArrayList<InfoItemVO>(Arrays.asList(bodyVOs));
				
				newBodyVOsList.add(addField("m_category", "Personnel Category", "人员类别", 
						5, 20, "hrlocal-000009", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL003")));
				newBodyVOsList.add(addField("m_entrydate", "Entry Date", "入境日期", 3, 19,
						"hrlocal-000010", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_numberofchildren", "Number of children", "子女数",
						1, 8, "hrlocal-000011", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_religion", "Religion", "宗教信仰", 5, 20,
						"hrlocal-000012", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL004")));
				newBodyVOsList.add(addField("m_isdisabled", "Disabled", "是否残疾", 4, 1, "hrlocal-000013",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_isspousedisabled", "Spouse Disabled", "配偶是否残疾",
						4, 1, "hrlocal-000014", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_isspouseworking", "Spouse Working", "配偶是否工作",
						4, 1, "hrlocal-000015", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_epfgroup", "EPF Group", "EPF Group", 5, 20,
						"hrlocal-000016", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL005")));
				newBodyVOsList.add(addField("m_socsogroup", "Socso Group", "Socso Group", 5, 20,
						"hrlocal-000017", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL007")));
				newBodyVOsList.add(addField("m_eisgroup", "EIS Group", "EIS Group", 5, 20,
						"hrlocal-000018", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL008")));
				newBodyVOsList.add(addField("m_pcbgroup", "PCB Group", "PCB Group", 5, 20,
						"hrlocal-000019", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL006")));
				newBodyVOsList.add(addField("m_epfno", "EPF No.", "EPF No.", 0, 101, "hrlocal-000020",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_socsono", "Socso No.", "Socso No.", 0, 101, "hrlocal-000021",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_taxno", "Tax No.", "Tax No.", 0, 101, "hrlocal-000022",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_taxbranch", "Tax Branch", "Tax Branch", 5, 20,
						"hrlocal-000023", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL009")));
				newBodyVOsList.add(addField("m_spousetaxno", "Spouse Tax No.", "Spouse Tax No.", 0, 101,
						"hrlocal-000024", "6008psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_spousetaxbranch", "Spouse Tax Branch", "Spouse Tax Branch", 5, 20,
						"hrlocal-000025", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL009")));
				newBodyVOsList.add(addField("m_totalpayable", "Total Payable (previous employer)", "前雇主总应发合计",
						2, 28, "hrlocal-000026", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_taxexemption", "Total Tax Exemption (previous employer)", "前雇主总免税合计",
						2, 28, "hrlocal-000027", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalepf", "Total EPF (previous employer)", "前雇主总EPF合计",
						2, 28, "hrlocal-000028", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totaleis", "Total EIS (previous employer)", "前雇主总EIS合计",
						2, 28, "hrlocal-000029", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalsocso", "Total SOCSO (previous employer)", "前雇主总SOCSO合计",
						2, 28, "hrlocal-000030", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalzakat", "Total Zakat (previous employer)", "前雇主总宗教税",
						2, 28, "hrlocal-000031", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalpcb", "Total PCB (previous employer)", "前雇主总个人所得税",
						2, 28, "hrlocal-000032", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				
				infoSet.setInfo_item(newBodyVOsList.toArray(new InfoItemVO[0]));
			}
		}
		return vos;
	}
	
}
