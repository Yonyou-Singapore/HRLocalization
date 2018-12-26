package nc.impl.hrrp.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.data.bill.BillDelete;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.itf.hrrp.service.BillType;
import nc.itf.hrrp.service.IBaseService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.ITableMeta;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

/**
 * <p>Title: BaseServiceImpl</P>
 * <p>Description: �������������������ʵ�֣�Ҫ���ҵ����̳и��ಢʵ��ҵ���ض��Ľӿڣ��Ի�ô�������������</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public class BaseServiceImpl implements IBaseService {
	
	@Override
	public List<Map> QueryBySql(String sqlStr) throws DAOException {
		List<Map> lit=new ArrayList<Map>();
		BaseDAO dao = new BaseDAO();
//		objs = (Object[]) dao.executeQuery(sqlStr,new ArrayProcessor());
		lit=(List<Map>) dao.executeQuery(sqlStr, new MapListProcessor());
		return lit;
	}
	@Override
	public Object QuerySigBySql(String sqlStr) throws DAOException {
		BaseDAO dao = new BaseDAO();
		Object  rst = dao.executeQuery(sqlStr, new ColumnProcessor());
		return rst;
	}

	@Override
	public <T extends SuperVO> T[] saveSuperVO(T[] vos) throws BusinessException{
		if (vos != null && vos.length > 0) {
			VOInsert<T> bo = new VOInsert<T>();
			return bo.insert(vos);
		}
		return null;
	}
	
	@Override
	public <T extends SuperVO> T[] updateSuperVO(T[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			VOUpdate<T> bo = new VOUpdate<T>();
			return bo.update(vos);
		}
		return null;
	}

	@Override
	public <T extends SuperVO> void deleteSuperVO(T[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			VODelete<T> bo = new VODelete<T>();
			bo.delete(vos);
		}
	}

	@Override
	public <T extends AbstractBill> T[] save(T[] vos) throws BusinessException{
		try{
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<T> transferTool = new BillTransferTool<T>(vos);
			T[] mergedVO = transferTool.getClientFullInfoBill();
			// ����BillInsert
			BillInsert<T> bo = new BillInsert<T>();
			T[] retvos = bo.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> T[] update(T[] vos) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<T> transTool = new BillTransferTool<T>(vos);
			// ��ȫǰ̨VO
			T[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			T[] originBills = transTool.getOriginBills();
			BillUpdate<T> bo = new BillUpdate<T>();
			T[] retBills = bo.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	/**
	 * <p>�������ƣ�updateWithoutDifference</p>
	 * <p>�������������²��������ز��������VO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill> T[] updateWithoutDifference(T[] vos) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<T> transTool = new BillTransferTool<T>(vos);
			// ��ȫǰ̨VO
			T[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			T[] originBills = transTool.getOriginBills();
			BillUpdate<T> bo = new BillUpdate<T>();
			T[] retBills = bo.update(fullBills, originBills);
			// ���췵������
//			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> void delete(T[] vos) throws BusinessException {
		try{
			// ���� �Ƚ�ts
			BillTransferTool<T> transferTool = new BillTransferTool<T>(vos);
			T[] fullBills = transferTool.getClientFullInfoBill();
			BillDelete<T> bo = new BillDelete<T>();
			bo.delete(fullBills);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
	}
	
	@Override
	public <T extends AbstractBill> T[] saveBills(T[] bills)  throws BusinessException{
		try{
			
			T[] retvos = null ;
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<T> transferTool = new BillTransferTool<T>(bills);
			T[] mergedVO = transferTool.getClientFullInfoBill();
			
			//ִ��ǰ����
			AroundProcesser<T>	processor = this.buildAddProcessor(bills);
			if(processor != null){
				retvos = processor.before(mergedVO);
			}
//			IMzValidator mzValidator=buildValidation(bills);
//			if(mzValidator!=null){
//				mzValidator.saveBeforeValidate(mergedVO);
//			}
			// ����BillInsert
			BillInsert<T> bo = new BillInsert<T>();
			retvos = bo.insert(mergedVO);
			// ִ�к����
			if(processor != null){
				retvos = processor.after(retvos);
			}
			//�����У��
//			if(mzValidator!=null){
//				mzValidator.saveAfterValidate(mergedVO);
//			}
			
			//����ҵ����־
//			saveLogs(retvos, null, logs, false);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	/**
	 * 
	 * <p>�������ƣ�buildValidation</p>
	 * <p>��������������ҵ��У�鼯��</p>
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	protected <T extends AbstractBill>IMzValidator buildValidation(T[] bills) throws BusinessException{
//		if(bills == null || bills.length == 0){
//			return null ;
//		}	
//		BillValidate billValidate=bills[0].getClass().getAnnotation(BillValidate.class);
//		if(billValidate==null){
//			return null;
//		}
//		IMzValidator validator=null;
//		try{
//			validator=(IMzValidator)Class.forName(billValidate.validator()).newInstance();
//		}catch (Exception e) {
//			throw new BusinessException("ִ��ǰУ��ע������д�������ϵ����Ա");
//		}
//		return validator;
//	}
	
	@Override
	public <T extends AbstractBill> T[] updateBills(T[] bills) throws BusinessException{
		try {
			
			T[] retBills = null ;
			
			// ���� + ���ts
			BillTransferTool<T> transTool = new BillTransferTool<T>(bills);
			// ��ȫǰ̨VO
			T[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			T[] originBills = transTool.getOriginBills();
			
			//ִ��ǰ����
//			CompareAroundProcesser<T>	processor = this.buildUpdateProcessor(bills);
//			if(processor != null){
//				retBills = processor.before(fullBills,originBills);
//			}
			
//			IMzValidator mzValidator=buildValidation(bills);
			//ִ��ǰУ��
//			if(mzValidator!=null){
//				mzValidator.updateBeforeValidate(fullBills);
//			}
			BillUpdate<T> bo = new BillUpdate<T>();
			retBills = bo.update(fullBills, originBills);
			// ִ�к����
//			if(processor != null){
//				retBills = processor.after(fullBills,originBills);
//			}
			//ִ�к�У��
//			if(mzValidator!=null){
//				mzValidator.updateAfterValidate(fullBills);
//			}
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	@Override
	public <T extends AbstractBill> void deleteBills(T[] bills) throws BusinessException{
		try{
			
			T[]  retVos = null; 
			// ���� �Ƚ�ts
			BillTransferTool<T> transferTool = new BillTransferTool<T>(bills);
			T[] fullBills = transferTool.getClientFullInfoBill();
			
			// ִ��ǰ����
//			AroundProcesser<T> processor = this.buildDeleteProcessor(bills);
//			if(processor != null){
//				retVos  = processor.before(fullBills);
//			}
//			IMzValidator mzValidator=buildValidation(bills);
//			if(mzValidator!=null){
//				mzValidator.deleteBeforeValidate(fullBills);
//			}
			BillDelete<T> bo = new BillDelete<T>();
			bo.delete(retVos);
			// ִ�к����
//			if(processor != null){
//				retVos = processor.after(retVos);
//			}
//			if(mzValidator!=null){
//				mzValidator.deleteAfterValidate(fullBills);
//			}
			//����ҵ����־
//			saveLogs(bills, null, logs, false);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
	}
	
//	@Override
//	public <T extends AbstractBill> T[] saveBills(T[] bills, MzAsFile[] files) throws BusinessException {
//		T[] returnVo = this.saveBills(bills, logs);
//		returnVo = this.saveOrUpdateFiles(returnVo, files);
//		return returnVo;
//	}

//	@Override
//	public <T extends AbstractBill> T[] updateBills(T[] bills, MzAsFile[] files) throws BusinessException {
//		T[] returnVo = this.updateBills(bills, logs);
//		returnVo = this.saveOrUpdateFiles(returnVo, files);
//		return returnVo;
//	}

//	@Override
//	public <T extends AbstractBill> T[] deleteBills(T[] bills, MzAsFile[] files) throws BusinessException {
//		this.deleteBills(bills, logs);
//		deleteFiles(bills,files);
//		return bills;
//	}
	
//	private <T extends AbstractBill> T[] saveOrUpdateFiles(T[] bills, MzAsFile[] files) throws BusinessException{
//		String pk = null;
//		if (bills.length > 0) {
//			pk = bills[0].getParent().getPrimaryKey();
//		}
//		if (pk == null) {
//			return bills;
//		}
//		List<MzAsFile> addList = new ArrayList<MzAsFile>();
//		List<MzAsFile> updateList = new ArrayList<MzAsFile>();
//		List<MzAsFile> delList = new ArrayList<MzAsFile>();
//		for (int i = 0; i < files.length; i++) {
//			MzAsFile file = files[i];
//			if (file.getStatus() == FileStatus.ADD) {
//				file.setAobp0148(pk);
//				addList.add(file);
//			} else if (file.getStatus() == FileStatus.UPDATE) {
//				updateList.add(file);
//			} else if (file.getStatus() == FileStatus.DEL) {
//				delList.add(file);
//			}
//		}
//		MzAsFile[] addFiles = new MzAsFile[addList.size()];
//		MzAsFile[] updateFiles = new MzAsFile[updateList.size()];
//		MzAsFile[] delFiles = new MzAsFile[delList.size()];
//		addFiles = addList.toArray(addFiles);
//		updateFiles = updateList.toArray(updateFiles);
//		delFiles = delList.toArray(delFiles);
//		try {
//			if (addFiles.length > 0) {
//				this.saveSuperVO(addFiles);
//			}
//			if (updateFiles.length > 0) {
//				this.updateSuperVO(updateFiles);
//			}
//			if (delFiles.length > 0) {
//				this.deleteSuperVO(delFiles);
//			}
//		} catch (Exception e) {
//			Logger.error(e);
//			throw new BusinessException(e.getMessage());
//		}
//		return bills;
//	}
	
	/**
	 * 
	 * <p>�������ƣ�deleteFiles</p>
	 * <p>����������ɾ�����ݿ��и�����¼</p>
	 * @param bills
	 * @param files ����vo
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	private <T extends AbstractBill> T[] deleteFiles(T[] bills, MzAsFile[] files) throws BusinessException{
//		String pk = null;
//		if (bills.length > 0) {
//			pk = bills[0].getParent().getPrimaryKey();
//		}
//		if (pk == null) {
//			return bills;
//		}
//		deleteSuperVO(files);
//		return bills;
//	}
	
	/**
	 * <p>�������ƣ�synchroBills</p>
	 * <p>����������ͬ������</p>
	 * @param bills ͬ�����̣�1.��ѯ����ע���
	 * 						   2.����ע����е���Ϣ����������󣬸����Ƿ������ͥ��Ϣ�ֱ𹹽���ͥ�������ͼ�ͥ��Ա�������
	 * 							 ��������Ա��ݱ�ʶ����ͥ��Ա��������д��ͥ����������
	 * 						   3.����ҵ��������ӱ���д������������
	 * 						   4.�ڵ��ݶ����л�дҵ����������ͷ����������
	 * 						   5.�����ɵķ�������ҵ�������У��
	 * @param logs ҵ����־
	 * @return     ���º�ĵ��ݶ������VO
	 * @throws BusinessException
	 * @author 
	 */
//	@Override
//	public <T extends AbstractBill> T[] synchroBills(T[] bills) throws BusinessException {
//		return NCLocator.getInstance().lookup(IBusinessService.class).synchroBills(bills, logs);
//	}

//	 @Override
//	  public int update(String sql, PubSQLParameter param) throws BusinessException {
//	    PersistenceManager pm = null;
//	    Connection conn = null;
//	    PreparedStatement pstmt = null;
//	    ResultSet rs = null;
//	    try {
//	      pm = PersistenceManager.getInstance();
//	      conn = pm.getJdbcSession().getConnection();
//	      pstmt = conn.prepareStatement(sql);
//	      this.setSQLParameter(pstmt, param);
//	      return pstmt.executeUpdate();
//	    } catch (DbException e) {
//	    	Logger.error("ִ��SQL������ " + sql, e);
//	    	throw new BusinessException(e);
//		} catch (SQLException e) {
//			Logger.error("ִ��SQL������ " + sql, e);
//	    	throw new BusinessException(e);
//		} finally {
//	      DBUtil.closeRs(rs);
//	      DBUtil.closeStmt(pstmt);
//	      DBUtil.closeConnection(conn);
//	      if( pm != null){
//	    	  pm.release();
//	      }
//	    }
//	  }
//
//	 @Override
//	  public int[] update(String sql, List<PubSQLParameter> params)   throws BusinessException {
//	    PersistenceManager pm = null;
//	    Connection conn = null;
//	    PreparedStatement pstmt = null;
//	    ResultSet rs = null;
//	    try {
//	      pm = PersistenceManager.getInstance();
//	      conn = pm.getJdbcSession().getConnection();
//	      pstmt = conn.prepareStatement(sql);
//	      addBatchParameter(pstmt, params);
//	      return pstmt.executeBatch();
//	    } catch (DbException e) {
//	    	Logger.error("ִ��SQL������ " + sql, e);
//	    	throw new BusinessException(e);
//	    } catch (SQLException e) {
//	    	Logger.error("ִ��SQL������ " + sql, e);
//	    	throw new BusinessException(e);
//		} finally {
//	      DBUtil.closeRs(rs);
//	      DBUtil.closeStmt(pstmt);
//	      DBUtil.closeConnection(conn);
//	      if( pm != null ){
//	    	  pm.release();
//	      }
//	    }
//	  }
//
//	  private void addBatchParameter(PreparedStatement pstmt, List<PubSQLParameter> params) throws SQLException {
//	    if (pstmt != null && params != null && params.size() > 0) {
//	      int size = params.size();
//	      for (int i = 0; i < size; i++) {
//	        this.setSQLParameter(pstmt, params.get(i));
//	        pstmt.addBatch();
//	      }
//	    }
//	  }
	  
	  /**
	   * <p>�������ƣ�setSQLParameter</p>
	   * <p>������������Ԥ����������ò���</p>
	   * @param stmt      Ԥ����������
	   * @param param     SQL��������
	   * @param update    �Ƿ��Ǹ��²���(:::)
	   * @throws SQLException
	   * @author 
	   */
//	  private void setSQLParameter(PreparedStatement stmt, PubSQLParameter param ) throws SQLException {
//	    if (param == null || param.size() == 0) {
//	      return;
//	    }
//	    int size = param.size();
//	    for (int i = 0; i < size; i++) {
//	      SQLParameter item = param.getSQLParameter(i);
//	      stmt.setObject(i + 1, item.getValue());
//	    }
//	  }
	
	/**
	 * <p>�������ƣ�queryBillReg</p>
	 * <p>������������ѯ����ע����ȡע����Ϣ</p>
	 * @param billType �������͵�����
	 * @author 
	 * @throws BusinessException 
	 */
//	@Override
//	public BillRegister queryBillReg(String billType) throws BusinessException {
//		if(StringUtil.isEmpty(billType)){
//			throw new BusinessException("��ȡ��ǰ���ݵĵ�������ʧ�ܣ�");
//		}
//		String sqlWhere = " where aobb0001='" + billType + "' and dr=0";
//		VOQuery<BillRegister> queryVo = new VOQuery<BillRegister>(BillRegister.class);
//		BillRegister[] billReg = queryVo.queryWithWhereKeyWord(sqlWhere, null);
//		if(billReg == null || billReg.length == 0){
//			throw new BusinessException("�޷��ҵ���Ӧ�ĵ�������(" + billType + ")��ע����Ϣ��");
//		}else{
//			if(billReg.length > 1){
//				throw new BusinessException("ͬһ�������͵�ע����Ϣ���ڶ�����¼���޷����к���������");
//			}else if(StringUtil.isEmpty(billReg[0].getAobb0023())){
//				throw new BusinessException("�޷��ӵ���ע����л�ȡҵ�����AggVO�࣡");
//			}else if(StringUtil.isEmpty(billReg[0].getAobb0024())){
//				throw new BusinessException("�޷��ӵ���ע����л�ȡ���ݶ��������Ա��ϢVO�࣡");
//			}else if(StringUtil.isEmpty(billReg[0].getAobb0025())){
//				throw new BusinessException("�޷��ӵ���ע����л�ȡҵ����������Ա��ϢVO�࣡");
//			}else if(StringUtil.isEmpty(billReg[0].getAobb0026())){
//				throw new BusinessException("�޷��ӵ���ע����л�ȡ��Ա��ݱ�ʶ��");
//			}else if(StringUtil.isEmpty(billReg[0].getAobb0028())){
//				throw new BusinessException("�޷��ӵ���ע����л�ȡҵ�����ʽ��");
//			}
//		}
//		return billReg[0];
//	}
	
	/**
	 * <p>�������ƣ�buildAddProcessor</p>
	 * <p>�������������ݵ��ݶ����챣�洦���������𱣴�ǰ������ִ��</p>
	 * @param vos     ���ݶ�������
	 * @return AroundProcesser 
	 * @author 
	 */
	private <T extends AbstractBill> AroundProcesser<T> buildAddProcessor(T[] vos) throws BusinessException{
		if(vos == null || vos.length == 0){
			return null ;
		}	
		
		BillType annotation = vos[0].getClass().getAnnotation(BillType.class);
		if(annotation == null){
			throw new BusinessException("���� " + vos[0] + " δ���BillTypeע�⣡");
		}
		String billTypeValue = annotation.billType();
		if(billTypeValue == null){
			throw new BusinessException("���� " + vos[0] + " ע���еĵ�����������ֵ����Ϊ�գ�");
		}
		
		AroundProcesser<T>  processor = new AroundProcesser<T>(null);
		IRule<T> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processor.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		
		String billno = annotation.billNo();
		String pk_org = annotation.pk_org();
		String pk_group = annotation.pk_group();
		
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype(billTypeValue);
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem(billno);
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setGroupItem(pk_group);
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem(pk_org);
		processor.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processor.addBeforeRule(rule);
//		if(hasChildren(vos)){
//			rule = new nc.bs.pubapp.pub.rule.CheckNotNullRule();
//			processor.addBeforeRule(rule);
//		}	
		IRule<T> billCodeCheckRule = this.buildBillCodeCheckRule(billTypeValue, billno, pk_group, pk_org);
		processor.addAfterRule(billCodeCheckRule);
		return processor ;
	}
	
	/**
	 * <p>�������ƣ�buildBillCodeCheckRule</p>
	 * <p>��������������һ�����ݵ�λ������</p>
	 * @param billType   ��������
	 * @param billCode   ���ݴ���
	 * @param pk_group   ����
	 * @param pk_org     ��֯
	 * @return IRule
	 * @author 
	 */
	private <T extends AbstractBill> IRule<T> buildBillCodeCheckRule(String billType,String billCode,String pk_group,String pk_org) {
		IRule<T> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype(billType);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem(billCode);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem(pk_group);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem(pk_org);
        return rule ;
	}
	
	/**
	 * <p>�������ƣ�hasChildren</p>
	 * <p>�����������ж������Ƿ�����ӱ�</p>
	 * @param vos    ҵ�񵥾ݶ���
	 * @return boolean
	 * @author 
	 */
//	private <T extends AbstractBill> boolean hasChildren(T[] vos) {
//		return vos[0].getMetaData().getChildren() != null  && vos[0].getMetaData().getChildren().length>0 ;
//	}
	
	/**
	 * <p>�������ƣ�buildUpdateProcessor</p>
	 * <p>�������������ݵ��ݶ�������´������������ڸ���ǰ������ִ��</p>
	 * @param vos  ���ݶ�������
	 * @return AroundProcesser
	 * @author 
	 */
//	private <T extends AbstractBill> CompareAroundProcesser<T> buildUpdateProcessor(T[] vos) throws BusinessException {
//		if(vos == null || vos.length == 0){
//			return null ;
//		}
//		
//		BillType annotation = vos[0].getClass().getAnnotation(BillType.class);
//		if(annotation == null){
//			throw new BusinessException("���� " + vos[0] + " δ���BillTypeע�⣡");
//		}
//		String billTypeValue = annotation.billType();
//		if(billTypeValue == null){
//			throw new BusinessException("���� " + vos[0] + " ע���еĵ�����������ֵ����Ϊ�գ�");
//		}
//		
//		CompareAroundProcesser<T>  processor = new CompareAroundProcesser<T>(null);
//		IRule<T> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
//		processor.addBeforeRule(rule);
//		
//		String billno = annotation.billNo();
//		String pk_org = annotation.pk_org();
//		String pk_group = annotation.pk_group();
//		
//		ICompareRule<T> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
//		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCbilltype(billTypeValue);
//		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem(billno);
//		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem(pk_group);
//		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem(pk_org);
//		processor.addBeforeRule(ruleCom);
//		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
//		processor.addBeforeRule(rule);
//		
//		IRule<T>  billCodeCheckRule = this.buildBillCodeCheckRule(billTypeValue, billno, pk_group, pk_org);
//		processor.addAfterRule(billCodeCheckRule);
//		return processor ;
//	}

	/**
	 * <p>�������ƣ�buildDeleteProcessor</p>
	 * <p>�������������ݵ��ݶ�����ɾ����������������ɾ��ǰ������ִ��</p>
	 * @param vos  ���ݶ�������
	 * @return AroundProcesser
	 * @author 
	 */
//	private <T extends AbstractBill> AroundProcesser<T> buildDeleteProcessor(T[] vos) throws BusinessException {
//		AroundProcesser<T>  processor = new AroundProcesser<T>(null);
//		return processor ;
//	}
	
	
	
	@Override
	public <T extends AbstractBill> T[] queryByQueryScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException {
		T[] bills = null;
		try {
			BillLazyQuery<T> query = new BillLazyQuery<T>(clazz);
			// chencd , ����Ȩ�޿���
			QuerySchemeProcessor processor = new QuerySchemeProcessor(queryScheme);
//            this.addFuncPermissionOrgSQL(clazz, processor);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}
	
	@Override
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize, String clazz) throws BusinessException{
		String[] allPks = this.queryPKSByScheme(queryScheme, clazz);
		List<String> firstQueryPks = PaginationUtils.getFistPageNeedQryList(allPks, pageSize);
		Object[] firstPageData = this.queryBillByPK(clazz, firstQueryPks.toArray(new String[0]));
		PaginationTransferObject paginationTransferObject = new PaginationTransferObject();
		paginationTransferObject.setAllPks(allPks);
		paginationTransferObject.setFirstPageData(firstPageData);
		return paginationTransferObject;
	}
	
	@Override
	public <T extends AbstractBill> T[] queryBillByPK(Class<T> clazz, String[] billIds) throws BusinessException{
		T[] bills = null;
		BillQuery<T> query = new BillQuery<T>(clazz);
		bills = query.query(billIds);
		return PaginationUtils.filterNotExistBills(bills, billIds);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractBill> T[] queryBillByPK(String clazz, String[] billIds) throws BusinessException {
		T[] bills = null;
		BillQuery<T> query = null;
		try {
			query = new BillQuery<T>((Class<T>) Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			Logger.error(e);
			throw new BusinessException("�Ҳ�����" + clazz);
		}
		bills = query.query(billIds);
		return PaginationUtils.filterNotExistBills(bills, billIds);
	}
	
	@Override
	public <T extends SuperVO> T[] querySuperVOByPK(Class<T> clazz, String[] pks) throws BusinessException{
		if(pks==null || pks.length<=0){
			throw new BusinessException("�������������Ϊ�գ�");
		}
		T[] vos = null;
		VOQuery<T> query = new VOQuery<T>(clazz);
		vos = query.query(pks);
		return vos;
	}
	
	@Override
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String whereSql) throws BusinessException{
		T[] vos = null;
		VOQuery<T> query = new VOQuery<T>(clazz);
		vos = query.queryWithWhereKeyWord(whereSql, null);
		return vos;
	}

	@Override
	public <T extends AbstractBill> String[] queryPKSByScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException{
//		StringBuffer sql = new StringBuffer();
//		QuerySchemeProcessor processor = new QuerySchemeProcessor(queryScheme);
//		this.addFuncPermissionOrgSQL(clazz, processor);
//		String mainAlias = processor.getMainTableAlias();
//		String pkName = null;
//		sql.append(" select ");
//		sql.append(mainAlias);
//		sql.append(".");
//		try {
//			pkName = clazz.newInstance().getMetaData().getParent().getPrimaryAttribute().getName();
//		} catch (InstantiationException e) {
//			Logger.error(e);
//			throw new BusinessException("ʵ��������");
//		} catch (IllegalAccessException e) {
//			Logger.error(e);
//			throw new BusinessException("��Ч�ķ��ʣ�");
//		}
		String sql = null;
		try{
			sql = buildQuerySql(queryScheme, clazz);
		}catch(Exception e){
			Logger.error(e);
			throw new BusinessException("����sql����"+e);
		}
		if(sql.length()<1){
			throw new BusinessException("����sql����");
		}
		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql.toString());
		String[] keys = rowset.toOneDimensionStringArray();
		return keys;
	}
	
	private <T extends AbstractBill> String buildQuerySql(IQueryScheme queryScheme, Class<T> clazz) throws BusinessException, InstantiationException, IllegalAccessException {
		QuerySchemeProcessor processor = null;
	    String mainTableAlias = null;
	    boolean isCreateByUAP = true; // ������UAPƽ̨�����QueryScheme����ʵ����������Ƿ���UAPƽ̨����
	    if (queryScheme.get(IQueryScheme.KEY_SQL_TABLE_LIST).toString().length()>1) {
	    	processor = new QuerySchemeProcessor(queryScheme);
//            this.addFuncPermissionOrgSQL(clazz, processor);
	    	mainTableAlias = processor.getMainTableAlias();   
	    }else{
	    	isCreateByUAP = false;
	    }
	    String pkName = null;
	    StringBuffer sql = new StringBuffer();
	    String where = null;
	    T Instance = clazz.newInstance();
//	    T Instance = clazz.newInstance();
	    ITableMeta[] tables = null;
	    if (isCreateByUAP) {
	      pkName = 	Instance.getMetaData().getParent().getPrimaryAttribute().getColumn().getName();
	      where =	processor.getFinalFromWhere();
	    }else{
	      IVOMeta meta = Instance.getMetaData().getParent();
	      tables= meta.getStatisticInfo().getTables();
	      pkName = meta.getPrimaryAttribute().getColumn().getName();
	      mainTableAlias=tables[0].getName();
	    }
	    sql.append("select distinct ");
	    sql.append(mainTableAlias).append(".").append(pkName).append(" ");
	    if (isCreateByUAP) {
	      sql.append(where);
	    }else{
	      sql.append(" from ");
	      sql.append(mainTableAlias).append(" where 1=1 ");
	    }
	    // ���ӵ�SQL
	    String add_sql = (String) queryScheme.get("ADD_SQL");
	    if (add_sql != null && !"".equals(add_sql)) {
	      sql.append(add_sql);
	    }
	    return sql.toString().replaceAll("'%", "'");
	}

	/**
	 * <p>�������ƣ�addFuncPermissionOrgSQL</p>
	 * <p>��������������Ĭ�ϵ���֯Ȩ�޹���</p>
	 * @param clazz      ���
	 * @param processor  ��ѯ����������
	 * @author 
	 */
//	private <T> void addFuncPermissionOrgSQL(Class<T> clazz, QuerySchemeProcessor processor) {
//		PermissionControl annotation = clazz.getAnnotation(PermissionControl.class);
//		boolean isControl = true;
//		if (annotation != null) {
//			isControl = annotation.isControl();
//		}
//		if (isControl) {
//			processor.appendFuncPermissionOrgSql();
//		}
//	}


	@Override
	public <T extends AbstractBill> List<T> queryBOByPK(Class<T> clazz, String pk) throws BusinessException{
		T[] bills = null;
		BillQuery<T> query = new BillQuery<T>(clazz);
		bills = query.query(new String[] { pk });
		return Arrays.asList(bills);
	}

//	@Override
//	public List<ServiceObject> querySOById(String id) throws BusinessException{
//		ServiceObject[] vos = null;
//		StringBuffer sqlWhere = new StringBuffer();
//		sqlWhere.append(" where (pkxc0100='").append(id).append("' or azcp0001='").append(id).append("')");
//		String[] pks = null;
//		pks = querySuperVOPKSByWhere(ServiceObject.class, sqlWhere.toString());
//		VOQuery<ServiceObject> voQuery = new VOQuery<ServiceObject>(ServiceObject.class);
//		vos = voQuery.query(pks);
//		return ConvertUtil.arrayToList(vos);
//	}
	
//	@Override
//	public List<ServiceObject> querySOByWhere(String where)
//			throws BusinessException {
//		ServiceObject[] vos = null;
//		String[] pks = null;
//		pks = querySuperVOPKSByWhere(ServiceObject.class, where);
//		VOQuery<ServiceObject> voQuery = new VOQuery<ServiceObject>(ServiceObject.class);
//		if(pks == null){
//			return null;
//		}
//		vos = voQuery.query(pks);
//		return ConvertUtil.arrayToList(vos);
//	}
	
//	@Override
//	public ServiceObject[] queryServiceObj(String srvType, String sqlWhere) throws BusinessException{
//		StringBuffer sql = new StringBuffer();
//		sql.append(sqlWhere);
//		sql.append(" and ").append(ServiceObject.AZCP0033).append("='").append(srvType).append("'");
//		if(srvType.equals(Constant.AZCP0033.AZCP0033_1)){
//			List<ServiceObject> retSrvs = null;
//			ServiceObject[] fmyObj = this.querySuperVOByWhere(ServiceObject.class, sql.toString());
//			if(fmyObj != null){
//				if(fmyObj.length == 1){
//					sql = new StringBuffer();
//					sql.append(" where ").append(ServiceObject.AZCP0025).append("='").append(fmyObj[0].getPkxc0100()).append("'");
//					sql.append(" and ").append(ServiceObject.AZCP0033).append("='").append(Constant.AZCP0033.AZCP0033_0).append("'");
//					ServiceObject[] srvObjs = querySuperVOByWhere(ServiceObject.class, sql.toString());
//					if(srvObjs != null && srvObjs.length > 0){
//						retSrvs = new ArrayList<ServiceObject>();
//						retSrvs.add(fmyObj[0]);
//						for(int i=0; i<srvObjs.length; i++){
//							retSrvs.add(srvObjs[i]);
//						}
//						return ConvertUtil.convertArrayType(retSrvs.toArray());
//					}
//				}else if(fmyObj.length > 0){
//					throw new BusinessException("�Ըû���Ϊ��ͥ�ķ��������ڶ�����¼���޷��Զ�������Ϣ��");
//				}
//			}
//			
//		}else if(srvType.equals(Constant.AZCP0033.AZCP0033_0)){
//			ServiceObject[] srvObj = this.querySuperVOByWhere(ServiceObject.class, sql.toString());
//			if(srvObj != null && srvObj.length > 0){
//				return srvObj;
//			}
//		}
//		return null;
//	}

	@Override
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere) throws BusinessException {
		try {
			IBillMeta meta = clazz.newInstance().getMetaData();
			Class<? extends ISuperVO> pClazz = meta.getVOClass(meta.getParent());
			return querySuperVOPKSByWhere(pClazz, sqlWhere);
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		String[] billIds = queryPKSByWhere(clazz, sqlWhere);
		if(billIds == null || billIds.length < 0){
			return null;
		}
		T[] bills = null;
		BillQuery<T> query = new BillQuery<T>(clazz);
		bills = query.query(billIds);
		return bills;
	}
	
	/**
	 * <p>�������ƣ�querySuperVOPKSByWhere</p>
	 * <p>��������������Where������ѯSuperVO���������</p>
	 * @param clazz
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 * @author 
	 * @throws InstantiationException 
	 * @throws Exception 
	 */
	public <T extends ISuperVO> String[] querySuperVOPKSByWhere(Class<T> clazz, String sqlWhere)  throws BusinessException, InstantiationException, Exception{
		if (sqlWhere.length()>1) {
//		  T cInstance = this.getVOInstance(parentClass);
		  T cInstance =clazz.newInstance();
//		  T cInstance = clazz.newInstance();
	      IVOMeta meta = cInstance.getMetaData();
	      ITableMeta[] tables = meta.getStatisticInfo().getTables();
	      String pkName = meta.getPrimaryAttribute().getName();
	      tables= meta.getStatisticInfo().getTables();
	      String tableName =tables[0].getName();
	      StringBuffer sql = new StringBuffer();
	      sql.append("select distinct ");
	      sql.append(tableName).append(".");
	      sql.append(pkName).append(" ");
	      sql.append("from ");
	      if(tables.length > 1){
	    	  for(int i=0; i<tables.length; i++){
	    		  sql.append(tables[i]).append(",");
	    	  }
	    	  sql.deleteCharAt(sql.length()-1);
	      }else{
	    	  sql.append(tableName);
	      }
	      sql.append(" ").append(sqlWhere);
	      
	      DataAccessUtils utils = new DataAccessUtils();
	      IRowSet rowset = utils.query(sql.toString());
	      return (null != rowset && rowset.size() > 0) ? rowset
	          .toOneDimensionStringArray() : null;
	    }
	    return null;
	}
	
	@Override
	public String getSequenceValue(String seqName) throws BusinessException {
		return this.getSequenceValue(seqName, -1, false);
	}


	@Override
	public <T extends AbstractBill> T[] save(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(T clientFullVO : clientFullVOs){
//	        clientFullVO.getParentVO().setAttributeValue(Constant.APPROVE_STATUS, BillStatusEnum.COMMIT.value());
	        clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		T[] returnVos =new BillUpdate<T>().update(clientFullVOs, originBills);
		return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] unsave(T[] clientFullVOs, T[] originBills) throws BusinessException {
		 // ��VO�־û������ݿ���
	    for(T clientBill : clientFullVOs) {
//	        clientBill.getParentVO().setAttributeValue(Constant.APPROVE_STATUS, BillStatusEnum.FREE.value());
	        clientBill.getParentVO().setStatus(VOStatus.UPDATED);
	    }
	    BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
	    return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] approve(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
		return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] unapprove(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
	    return returnVos;
	}

	/**
	 * <p>�������ƣ�synchroBills</p>
	 * <p>����������</p>
	 * @param bills
	 * @return
	 * @throws BusinessException
	 * @author 
	 * @since 2014-10-27
	 */
	@Override
	public <T extends AbstractBill> T[] synchroBills(T[] bills) throws BusinessException {
		// TODO �Զ����ɵķ������
		return null;
	}

	/**
	 * <p>�������ƣ�getSequenceValue</p>
	 * <p>����������</p>
	 * @param seqName
	 * @param seqLength
	 * @param isAddTime
	 * @return
	 * @throws BusinessException
	 * @author 
	 * @since 2014-10-27
	 */
	@Override
	public String getSequenceValue(String seqName, int seqLength, boolean isAddTime) throws BusinessException {
		// TODO �Զ����ɵķ������
		return null;
	}

	/**
	 * <p>�������ƣ�queryPKSByScheme</p>
	 * <p>����������</p>
	 * @param queryScheme
	 * @param clazz
	 * @return
	 * @throws BusinessException
	 * @author 
	 * @since 2014-10-27
	 */
	@Override
	public <T extends AbstractBill> String[] queryPKSByScheme(IQueryScheme queryScheme, String clazz)
			throws BusinessException {
		String sql = null;
		Class<T> billClass ;
		try {
			billClass=(Class<T>) Class.forName(clazz);
			sql = buildQuerySql(queryScheme, billClass);
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException("����sql����", e);
		}
		if(sql.length()<1){
			throw new BusinessException("����sql����");
		}
		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql);
		String[] keys = rowset.toOneDimensionStringArray();
		return keys;
	}
	
	/**
	 * <p>�������ƣ�getWFProcessDefId</p>
	 * <p>��������������������ͨ����չ�ֶεõ�ֵ</p>
	 * @param pkMessage
	 * @return
	 * @author 
	 */
	@Override
	public List getWFProcessDefId(String pkMessage) {
		BaseDAO dao = new BaseDAO();
		StringBuffer sql = new StringBuffer();
		sql.append("select wft.processdefid, wft.activitydefid from sm_msg_content msg inner join pub_workflownote wfn on msg.pk_detail=wfn.pk_checkflow inner join pub_wf_task wft on wfn.pk_wf_task=wft.pk_wf_task where msg.pk_message='");
		sql.append(pkMessage);
		sql.append("'");
		List queryResult = null;
		try {
			queryResult = (List) dao.executeQuery(sql.toString(),
					new MapListProcessor());
			if(queryResult.size()==0){
				sql.delete(0, sql.length());
				sql.append("select wfi.processdefid,'' as activitydefid from sm_msg_content msg inner join pub_workflownote wfn on msg.pk_detail=wfn.pk_checkflow inner join pub_wf_instance wfi on wfn.billno=wfi.billno and wfn.pk_billtype =wfi.billtype where msg.pk_message='");
				sql.append(pkMessage);
				sql.append("'");
				queryResult = (List) dao.executeQuery(sql.toString(),
						new MapListProcessor());
			}
		} catch (DAOException e) {
			Logger.error(e);
		}
		return queryResult;
	}

	/**
	 * <p>�������ƣ�QueryByProc</p>
	 * <p>����������</p>
	 * @param procName
	 * @param strWhr
	 * @return
	 * @throws DAOException
	 * @author 
	 * @since 2014-11-7
	 */
	@Override
	public List<Map> QueryByProc(String procName, String strWhr) throws Exception {
		try{
		JdbcSession jdbcSession = null ;
		Connection conn = null ;
		CallableStatement callStmt = null ;
		PersistenceManager  persistenceManager = null ;
		ResultSet rs=null;
		List<Map> lst=new ArrayList<Map>();
			persistenceManager = PersistenceManager.getInstance();
			jdbcSession = persistenceManager.getJdbcSession();
			conn = jdbcSession.getConnection();
			callStmt = conn.prepareCall("{call dbo." + procName +"(?,?)}");
			callStmt.setString(1, " where 1=1");
			callStmt.registerOutParameter(2, java.sql.Types.INTEGER);
//			callStmt.setString(1, strWhr);
			rs=callStmt.executeQuery();
//			System.out.println(rs.getRow());
		if(rs==null)
			return null;
		while(rs.next())
		{
			System.out.println(rs.getString(0));
			Map map=new HashMap();
			for(int col=0,cnt=rs.getMetaData().getColumnCount();col<cnt;col++)
			{
				String val=rs.getString(col);
				System.out.println(val);
				map.put(rs.getMetaData().getColumnName(col), val);
			}
			lst.add(map);
		}
		callStmt.getInt(2);
		return lst;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

}
