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
	// TODO 初始化查询模板逻辑
		//这个数组目前是空的，请业务组把需要按组织过滤的参照字段加入到这个数组中
		List<String> targetFields = new ArrayList<String>();
		// TODO 加入需要按组织过滤的参照字段
		QueryDefaultOrgFilter orgFilter=new QueryDefaultOrgFilter(condDLGDelegator,"pk_org",targetFields);
		orgFilter.addEditorListener();
	}
}