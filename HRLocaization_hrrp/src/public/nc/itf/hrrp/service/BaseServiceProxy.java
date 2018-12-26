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
 * <p>Description: ����������</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public class BaseServiceProxy<T extends AbstractBill> implements IBaseProxyServcie<T> {

	/**
	 * ����VO
	 */
	private String billClazz = null;
	
	/**
	 * <p>�������ƣ�operateBill</p>
	 * <p>����������ɾ�����ݣ�֧������</p>
	 * @param bill Ҫɾ���ĵ���
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public T operateBill(T bill) throws Exception {
		delete(new AbstractBill[]{ bill });
		return bill;
	}
	
	/**
	 * <p>�������ƣ�queryObjectByPks</p>
	 * <p>������������ҳ��ѯ����</p>
	 * @param pks ������������
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		Object[] rets = null;
		if(this.getBillClazz() == null){
			throw new IllegalArgumentException("���ڴ�������ע�뵱ǰ���ݵ�VO��");
		}
		try{
			rets = getMainService().queryBillByPK(this.getBillClazz(), pks);
		}catch(BusinessException e){
			ExceptionUtils.wrappException(e);
		}
		return rets;
	}
	
	/**
	 * <p>�������ƣ�queryByPageQueryScheme</p>
	 * <p>������������ҳ��ѯ</p>
	 * @param queryScheme ��ѯ������Ϣ
	 * @param pageSize ҳ������
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize) throws Exception {
		return this.getMainService().queryByPageQueryScheme(queryScheme, pageSize, billClazz);
	}

	/**
	 * <p>�������ƣ�queryByQueryScheme</p>
	 * <p>�������������ݲ�ѯ������ѯ���ݣ���ʹ�÷�ҳ��</p>
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
	 * <p>�������ƣ�insert</p>
	 * <p>�����������������ݣ�����BP�����־</p>
	 * @param value ��������
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
	 * <p>�������ƣ�update</p>
	 * <p>�������������µ��ݣ�����BP�����־</p>
	 * @param value ��������
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
	 * <p>�������ƣ�delete</p>
	 * <p>����������ɾ�����ݣ�����BP�����־</p>
	 * @param value ��������
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public IBill[] delete(IBill[] value) throws BusinessException {
		getMainService().delete((AbstractBill[])value);
		return value;
	}
	
	/**
	 * <p>�������ƣ�insertWhitOutBP</p>
	 * <p>�������������浥�ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
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
	 * <p>�������ƣ�updateWhitOutBP</p>
	 * <p>�������������µ��ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
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
	 * <p>�������ƣ�deleteWhitOutBP</p>
	 * <p>����������ɾ�����ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
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
	 * <p>�������ƣ�queryPKSByWhere</p>
	 * <p>��������������SQL������ѯ����</p>
	 * @param clazz ��ѯ��VO��
	 * @param sqlWhere SQL��������where����
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends AbstractBill>  String[] queryPKSByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryPKSByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�querySuperVOByWhere</p>
	 * <p>�������������ݴ������where�ؼ��ֵ�sql������ѯSuperVO</p>
	 * @param clazz ��ѯ��VO��
	 * @param sqlWhere ������where �ؼ��ֺ���ʱ������Լ�����������Ψһ����Ҫ д�ľ���Ҫ��ѯ���ֶ�����vo�����Լ�dr=0����
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends SuperVO> K[] querySuperVOByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().querySuperVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�queryAggVOByWhere</p>
	 * <p>��������������SQL������ѯAggVO</p>
	 * @param clazz ��ѯ��AggVO��
	 * @param sqlWhere ��where�ؼ��ֵ�����
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <K extends AbstractBill> K[] queryAggVOByWhere(Class<K> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryAggVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�getMainService</p>
	 * <p>����������Զ�̵��û�������ӿ�</p>
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
