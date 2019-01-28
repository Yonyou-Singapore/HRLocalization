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

	//平台的 如不能使用信息集，则只能写死，采用下面的方式
	@Override
	public Content[] createContents(Object... params) {
		Content[] contents = new Content[1];
		Content jobGradeContent = new Content();
		jobGradeContent.setContentName("Job Grade");
		jobGradeContent.setColName("JOBGRADE");
		jobGradeContent.setDescription("Job Grade");
		jobGradeContent.setRefModelClass(nc.ui.om.ref.JobGradeRefModel2.class.getName());
		contents[0]=jobGradeContent;
		return contents;
	}

}