package nc.itf.hrrp.service;


import nc.bs.framework.common.NCLocator;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

/**
 * <p>Title: BaseServiceProxy</P>
 * <p>Description: 基础代理类</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public class BaseServiceProxy<T extends AbstractBill> implements IBaseProxyServcie<T> {

	/**
	 * 单据VO
	 */
	private String billClazz = null;
	
	/**
	 * <p>方法名称：operateBill</p>
	 * <p>方法描述：删除单据，支持批量</p>
	 * @param bill 要删除的单据
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public T operateBill(T bill) throws Exception {
		delete(new AbstractBill[]{ bill });
		return bill;
	}
	
	/**
	 * <p>方法名称：queryObjectByPks</p>
	 * <p>方法描述：分页查询单据</p>
	 * @param pks 单据主键数组
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		Object[] rets = null;
		if(this.getBillClazz() == null){
			throw new IllegalArgumentException("请在代理类中注入当前单据的VO类");
		}
		try{
			rets = getMainService().queryBillByPK(this.getBillClazz(), pks);
		}catch(BusinessException e){
			ExceptionUtils.wrappException(e);
		}
		return rets;
	}
	
	/**
	 * <p>方法名称：queryByPageQueryScheme</p>
	 * <p>方法描述：分页查询</p>
	 * @param queryScheme 查询条件信息
	 * @param pageSize 页单据数
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize) throws Exception {
		return this.getMainService().queryByPageQueryScheme(queryScheme, pageSize, billClazz);
	}

	/**
	 * <p>方法名称：queryByQueryScheme</p>
	 * <p>方法描述：根据查询方案查询单据（不使用分页）</p>
	 * @param queryScheme
	 * @return
	 * @throws Exception
	 * @author 
	 */
	@SuppressWarnings("unchecked")
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		return getMainService().queryByQueryScheme((Class<T>)Class.forName(billClazz), queryScheme);
	}

	/**
	 * <p>方法名称：insert</p>
	 * <p>方法描述：新增单据，包含BP层和日志</p>
	 * @param value 单据数组
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] insert(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		retVo = getMainService().saveBills((AbstractBill[])value);
		return retVo;
	}
	
	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：更新单据，包含BP层和日志</p>
	 * @param value 单据数组
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] update(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		retVo = getMainService().update((AbstractBill[])value);
		return retVo;
	}

	/**
	 * <p>方法名称：delete</p>
	 * <p>方法描述：删除单据，包含BP层和日志</p>
	 * @param value 单据数组
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] delete(IBill[] value) throws BusinessException {
		getMainService().delete((AbstractBill[])value);
		return value;
	}
	
	/**
	 * <p>方法名称：insertWhitOutBP</p>
	 * <p>方法描述：保存单据，适合不存在单据类型和BP的情况</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] insertWhitOutBP(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		retVo = getMainService().save((AbstractBill[])value);
		return retVo;
	}
	
	/**
	 * <p>方法名称：updateWhitOutBP</p>
	 * <p>方法描述：更新单据，适合不存在单据类型和BP的情况</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] updateWhitOutBP(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		retVo = getMainService().update((AbstractBill[])value);
		return retVo;
	}
	
	/**
	 * <p>方法名称：deleteWhitOutBP</p>
	 * <p>方法描述：删除单据，适合不存在单据类型和BP的情况</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] deleteWhitOutBP(IBill[] value) throws BusinessException {
		getMainService().delete((AbstractBill[])value);
		return value;
	}
	

	/**
	 * <p>方法名称：queryPKSByWhere</p>
	 * <p>方法描述：根据SQL条件查询主键</p>
	 * @param clazz 查询的VO类
	 * @param sqlWhere SQL条件，含where条件
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends AbstractBill>  String[] queryPKSByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryPKSByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：querySuperVOByWhere</p>
	 * <p>方法描述：根据传入的有where关键字的sql条件查询SuperVO</p>
	 * @param clazz 查询的VO类
	 * @param sqlWhere 必须有where 关键字和临时表表名以及其他条件，唯一不需要 写的就是要查询的字段名、vo表名以及dr=0条件
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends SuperVO> K[] querySuperVOByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().querySuperVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：queryAggVOByWhere</p>
	 * <p>方法描述：根据SQL条件查询AggVO</p>
	 * @param clazz 查询的AggVO类
	 * @param sqlWhere 含where关键字的条件
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends AbstractBill> K[] queryAggVOByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryAggVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：getMainService</p>
	 * <p>方法描述：远程调用基础服务接口</p>
	 * @return
	 * @author 
	 */
	protected IBaseService getMainService(){
		return NCLocator.getInstance().lookup(IBaseService.class);
	}
	
	public String getBillClazz() {
		return billClazz;
	}

	public void setBillClazz(String billClazz) {
		this.billClazz = billClazz;
	}

}
