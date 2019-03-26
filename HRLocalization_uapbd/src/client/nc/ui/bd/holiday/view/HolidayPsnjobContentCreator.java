package nc.ui.bd.holiday.view;

import nc.hr.utils.ResHelper;
import nc.ui.hr.formula.variable.AbstractContentCreator;
import nc.ui.hr.formula.variable.Content;

/**
 * 变量区域的
 * 人员基本信息的item创建器
 * @author
 */
public class HolidayPsnjobContentCreator extends
				AbstractContentCreator
{

	@Override
	public Content[] createContents(Object... params) {
		Content[] contents = new Content[1];
		Content jobGradeContent = new Content();
		jobGradeContent.setContentName(ResHelper.getString("common","sealocal-000001"));
		jobGradeContent.setColName("JOBGRADE");
		jobGradeContent.setDescription(ResHelper.getString("common","sealocal-000001"));
		jobGradeContent.setRefModelClass(nc.ui.om.ref.JobGradeRefModel2.class.getName());
		contents[0]=jobGradeContent;
		return contents;
	}

}