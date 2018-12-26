package nc.ui.hrrp.ds.handler;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pubapp.uif2app.query2.IQueryConditionDLGInitializer;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.refregion.QueryDefaultOrgFilter;

/**
 * <p>Title: QueryConditionInitializer</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 * @since 2014-10-12
 */
public class QueryConditionInitializer implements
IQueryConditionDLGInitializer {

@Override
	public void initQueryConditionDLG(QueryConditionDLGDelegator condDLGDelegator) {
	// TODO ��ʼ����ѯģ���߼�
		//�������Ŀǰ�ǿյģ���ҵ�������Ҫ����֯���˵Ĳ����ֶμ��뵽���������
		List<String> targetFields = new ArrayList<String>();
		// TODO ������Ҫ����֯���˵Ĳ����ֶ�
		QueryDefaultOrgFilter orgFilter=new QueryDefaultOrgFilter(condDLGDelegator,"pk_org",targetFields);
		orgFilter.addEditorListener();
	}
}