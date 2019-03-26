
package nc.itf.hrrp.service;

import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.model.pagination.IPageQueryService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;

/**
 * <p>Title: IBaseProxyServcie</P>
 * <p>Description: 客户端服务代理类</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public interface IBaseProxyServcie<T> extends IDataOperationService,
		IQueryService, IPaginationQueryService, ISingleBillService<T>,
		IPageQueryService {

}