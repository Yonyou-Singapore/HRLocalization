package nc.itf.hrrp.service;

import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

/**
 * <p>Title: IBaseService</P>
 * <p>Description: 动态人员信息库基础服务接口</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public interface IBaseService {

	/**
	 * <p>方法名称：QueryBySql</p>
	 * <p>方法描述：查询</p>
	 * @param sqlStr
	 * @return
	 * @throws DAOException
	 * @author 
	 * @since  2014-10-29
	 */
	public List<Map> QueryByProc(String procName,String strWhr) throws Exception;
	/**
	 * <p>方法名称：QueryBySql</p>
	 * <p>方法描述：查询</p>
	 * @param sqlStr
	 * @return
	 * @throws DAOException
	 * @author 
	 * @since  2014-10-29
	 */
	public List<Map> QueryBySql(String sqlStr) throws DAOException;
	/**
	 * <p>方法名称：QueryBySql</p>
	 * <p>方法描述：查询</p>
	 * @param sqlStr
	 * @return
	 * @throws DAOException
	 * @author 
	 * @since  2014-10-29
	 */
	public Object QuerySigBySql(String sqlStr) throws DAOException;
	/**
	 * <p>方法名称：save</p>
	 * <p>方法描述：保存SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 * @since 2013-9-9
	 */
	public <T extends SuperVO> T[] saveSuperVO(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：更新SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends SuperVO> T[] updateSuperVO(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：delete</p>
	 * <p>方法描述：删除SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends SuperVO> void deleteSuperVO(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：save</p>
	 * <p>方法描述：保存AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill> T[] save(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：更新AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill> T[] update(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：delete</p>
	 * <p>方法描述：删除AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill> void delete(T[] vos) throws BusinessException;
	
	/**
	 * <p>方法名称：saveBills</p>
	 * <p>方法描述：保存单据</p>
	 * @param bills 需要保存的单据对象
	 * @param logs 业务日志对象，必须和单据对象一一对应，否则不予保存，可以为null
	 * @return
	 * @author 
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] saveBills(T[] bills) throws BusinessException;

	/**
	 * <p>方法名称：deleteBills</p>
	 * <p>方法描述：删除单据：逻辑删除</p>
	 * @param bills 需要删除的单据对象
	 * @param logs 业务日志对象，必须和单据对象一一对应，否则不予保存，可以为null
	 * @author 
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> void deleteBills(T[] bills) throws BusinessException;
	
	/**
	 * <p>方法名称：updateBills</p>
	 * <p>方法描述：更新单据：业务单据更新</p>
	 * @param bills 需要更新的单据对象
	 * @param logs 业务日志对象，必须和单据对象一一对应，否则不予保存，可以为null
	 * @return
	 * @author 
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] updateBills(T[] bills) throws BusinessException;
	
	/**
	 * <p>方法名称：saveBills</p>
	 * <p>方法描述：保存带附件的单据</p>
	 * @param bills 单据对象
	 * @param files 附件
	 * @param logs 日志
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	public <T extends AbstractBill>  T[] saveBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>方法名称：updateBills</p>
	 * <p>方法描述：更新带附件的单据</p>
	 * @param bills 单据对象
	 * @param files 附件
	 * @param logs 日志
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	public <T extends AbstractBill>  T[] updateBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>方法名称：deleteBills</p>
	 * <p>方法描述：删除带附件的单据</p>
	 * @param bills 单据对象
	 * @param files 附件
	 * @param logs 日志
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	public <T extends AbstractBill>  T[] deleteBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>方法名称：save</p>
	 * <p>方法描述：提交</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] save(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>方法名称：unsave</p>
	 * <p>方法描述：收回</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] unsave(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>方法名称：approve</p>
	 * <p>方法描述：审批</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] approve(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>方法名称：unapprove</p>
	 * <p>方法描述：弃审</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] unapprove(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>方法名称：synchroBills</p>
	 * <p>方法描述：同步单据：从单据对象中分离业务对象和服务对象</p>
	 * @param bills 需要处理的单据对象
	 * @param logs 业务日志对象，必须和单据对象一一对应，否则不予保存，不能为null
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] synchroBills(T[] bills) throws BusinessException;
	
	/**
	 * <p>方法名称：queryByQueryScheme</p>
	 * <p>方法描述：根据查询方案查询单据（不使用分页）</p>
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] queryByQueryScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException;
   
	/**
	 * <p>方法名称：queryByPageQueryScheme</p>
	 * <p>方法描述：分页查询</p>
	 * @param queryScheme 查询条件信息
	 * @param pageSize 页单据数
	 * @param clazz 单据AggVO类
	 * @return
	 * @author 
	 */
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize, String clazz) throws BusinessException;
	
   /**
    * <p>方法名称：queryBillByPK</p>
    * <p>方法描述：根据主键数组查询单据</p>
    * @param clazz 当前查询对象对应的VO类
    * @param billIds 主键数组
    * @return
    * @author 
    * @throws BusinessException 
    */
	public <T extends AbstractBill>  T[] queryBillByPK(Class<T> clazz, String[] billIds) throws BusinessException;
	
	/**
	 * <p>方法名称：queryBillByPK</p>
	 * <p>方法描述：根据主键数组查询单据</p>
	 * @param billIds 主键数组
	 * @param clazz 当前查询对象对应的VO类的全路径
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  T[] queryBillByPK(String clazz, String[] billIds) throws BusinessException;
	
	/**
	 * <p>方法名称：querySuperVOByPK</p>
	 * <p>方法描述：根据主键查询SuperVO</p>
	 * @param clazz 当前SuperVO类
	 * @param pks 主键数组
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends SuperVO> T[] querySuperVOByPK(Class<T> clazz, String[] pks) throws BusinessException;
	
	/**
	 * <p>方法名称：querySuperVOByWhere</p>
	 * <p>方法描述：根据传入的有where关键字的sql条件查询SuperVO</p>
	 * @param clazz 当前SuperVO类
	 * @param whereSql  必须有where 关键字和临时表表名以及其他条件，唯一不需要 写的就是要查询的字段名、vo表名以及dr=0条件
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String whereSql) throws BusinessException;

   /**
    * <p>方法名称：queryPKSByScheme</p>
    * <p>方法描述：根据查询方案查询符合条件的主键</p>
    * @param clazz 当前查询对象对应的VO类
    * @param queryScheme 查询方案
    * @return
    * @author 
    * @throws BusinessException 
    */
	public <T extends AbstractBill>  String[] queryPKSByScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException;
	
	/**
	 * <p>方法名称：queryPKSByScheme</p>
	 * <p>方法描述：根据查询方案查询符合条件的主键</p>
	 * @param queryScheme 查询方案
	 * @param clazz 当前查询对象对应的VO类的全路径
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill>  String[] queryPKSByScheme(IQueryScheme queryScheme, String clazz) throws BusinessException;

   /**
    * <p>方法名称：queryBOById</p>
    * <p>方法描述：根据业务对象的主键查询业务对象</p>
    * @param clazz 当前查询对象对应的VO类
    * @param id 人员的关键字
    * @return
    * @author 
    * @throws BusinessException 
    */
	public <T extends AbstractBill> List<T> queryBOByPK(Class<T> clazz, String pk) throws BusinessException;

	/**
	 * <p>方法名称：querySOById</p>
	 * <p>方法描述：根据服务对象的关键字（主键、身份证号、社保证号、一卡通号）查询服务对象，目前仅支持主键和身份证号</p>
	 * @param id 
	 * @return
	 * @author 
	 * @throws BusinessException 
	 */
//	public List<ServiceObject> querySOById(String id) throws BusinessException;
	/**
	 * 
	 * <p>方法名称：querySOByWhere</p>
	 * <p>方法描述：根据where条件查询服务对象</p>
	 * @param where(参数包含where关键字)
	 * @return
	 * @throws BusinessException
	 * @author 
	 * 
	 */
//	public List<ServiceObject> querySOByWhere(String where) throws BusinessException;
	
	/**
	 * <p>方法名称：queryBillReg</p>
	 * <p>方法描述：查询单据注册表获取注册信息</p>
	 * @param billType 单据类型
	 * @return
	 * @author 
	 */
//	public BillRegister queryBillReg(String billType) throws BusinessException;
	
	/**
	 * <p>方法名称：queryServiceObj</p>
	 * <p>方法描述：</p>
	 * @param srvType
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
//	public ServiceObject[] queryServiceObj(String srvType, String sqlWhere) throws BusinessException;
	
   /**
    * <p>方法名称：queryPKSByWhere</p>
    * <p>方法描述：根据SQL条件查询主键</p>
    * @param clazz 查询的AggVO类
    * @param sqlWhere 含where关键字的条件
    * @return
    * @author 
    * @throws BusinessException 
    */
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere) throws BusinessException;
	
	/**
	 * <p>方法名称：queryAggVOByWhere</p>
	 * <p>方法描述：根据SQL条件查询AggVO</p>
	 * @param clazz 查询的AggVO类
	 * @param sqlWhere 含where关键字的条件
	 * @return
	 * @throws BusinessException
	 * @author 
	 */
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException;

	 /**
	   * <p>方法名称：execFunc</p>
	   * <p>方法描述：执行一个函数</p>
	   * <p><pre>
	   * 使用方法：     
	   *      example1 : 有一个函数名字为SI_FUNC_GET_SALARY_YEAR(YEAR NUMBER),则使用调用方法如下：
	   *      // other logic 
	   *      ISIBaseService srv = .../// 获取服务器端的服务对象
	   *      PubSQLParameter param = new PubSQLParameter();
	   *      param.addParam(PubSQLParameter.SQL_PARAM_OUT,java.sql.Types.DOUBLE,null);
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.Integer,2008);
	   *      List  list = srv.execFunc("SI_FUNC_GET_SALARY_YEAR",param);
	   *      double salary = list.isEmpty()? 0d:(double)list.get(0);
	   *      // other logic 
	   *      
	   *      注意：
	   *           1、对于函数的调用，至少应该传入一个输出参数，作为函数的返回值
	   *           2、对于在包中的函数，传入的函数名称必须包含包名，如  PKG_COMMON_K.SI_FUNC_GET_SALARY_YEAR
	   *    </pre>
	   * @param funcName   函数的名字
	   * @param param      函数的参数
	   * @return List  返回值
	   * @throws BusinessException
	   * @author 
	   */
//	public List<?> execFunc(String funcName, PubSQLParameter params) throws BusinessException;

	/**
	   * <p>方法名称：execProc</p>
	   * <p>方法描述：执行一个存储过程</p>
	   * <p><pre>
	   * 使用方法：     
	   *      example1 : 有一个存储过程名字为SI_PROC_GENERATE_PLAN(UNIT_ID varchar2,MONTH NUMBER,ERROR_MSG VARCHAR2),则使用调用方法如下：
	   *      // other logic 
	   *      ISIBaseService srv = .../// 获取服务器端的服务对象
	   *      PubSQLParameter param = new PubSQLParameter();
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.VARCHAR,'100101010');
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.Integer,200806);
	   *      param.addParam(PubSQLParameter.SQL_PARAM_OUT,java.sql.Types.VARCHAR,null);
	   *      List  list = srv.execFunc("SI_PROC_GENERATE_PLAN",param);
	   *      String result = list.isEmpty()? null:(String)list.get(0);
	   *      // other logic 
	   *      
	   *      注意：1、对于在包中的存储过程，传入的存储过程名称必须包含包名，如  PKG_COMMON_K.SI_PROC_GENERATE_PLAN
	   * </pre>
	   * @param procName    存储过程名称，必要时包括包名
	   * @param param       存储过程参数，包括输入参数和输出参数
	   * @return List       存储过程返回输出值
	   * @throws BusinessException
	   * @author 
	   */
//	public List<?> execProc(String procName, PubSQLParameter params) throws BusinessException;

	/**
	   * <p>方法名称：execReportFunc</p>
	   * <p>方法描述：执行一个报表过程(包括存储过程和函数)，具体调用形式可以参考 ISIBaseService.execFunc(..)</p>
	   * <p>本接口提供的目的是处理打印单据数据源数据查询的问题，由于UAP打印数据源需要一个Map形式的参数，而开发<br>
	   * 人员往往把SQL语句直接写死在调用的ACTION中，这样当打印单据的部分数据发生调整时必须重新调整JAVA类，为了<br>
	   * 剥离这部分工作，特将数据查询过程封装到存储过程中，对SQL的执行统一由Oracle函数PKG_COMMON.FUNC_QUERY_FORMAT<br>
	   * 来处理，开发人员只需要在自己的存储过程中组织SQL和相关参数，将组织完成的SQL传入上述Oracle函数，返回指定格式的数据<br>
	   * 由PubSQLResult对象对数据进行分析处理成Map格式的数据以供后续业务的应用（PubSQLResult对象暂未实现），目前仅支持JSON格式。</p>
	   * @param procName   过程名称： 可以是存储过程，也可以是函数
	   * @param param      过程的执行参数，包括输入参数和输出参数
	   * @return PubSQLResult
	   * @throws BusinessException
	   * @author 
	   */
//	public List<?> execReportFunc(String funcName, PubSQLParameter params) throws BusinessException;

    /**
     * <p>方法名称：getSequenceValue</p>
     * <p>方法描述：获取序列的下一个值，对序列的值不做任何处理，直接返回</p>
     * @param seqName   序列名称
     * @return          序列的值
     * @throws BusinessException
     * @author 
     */
    public String getSequenceValue(String seqName) throws BusinessException ;
    
	/**
	 * <p>方法名称：getSequenceValue</p>
	 * <p>方法描述：获取序列的下一个值，对返回的序列根据指定的长度进行填充处理，如果isAddTime为true则左边填充时间，如序列的值为1，指定的长度为20个字符<br>
	 * ，isAddTime为真则，返回的值格式为：20121212241334000001</p>
	 * @param seqName      序列的名称
	 * @param seqLength    返回值得填充长度
	 * @param isAddTime    返回值左边是否填充时间前缀
	 * @return 序列值
	 * @throws BusinessException
	 * @author 
	 */
	public String getSequenceValue(String seqName, int seqLength , boolean isAddTime) throws BusinessException;
	
	
	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：单条SQL语句更新操作</p>
	 * @param sql      要执行的更新语句，要求是预处理语句
	 * @param param    SQL语句参数，没有参数可以为空
	 * @return int     影响的记录条数
	 * @throws BusinessException
	 * @author 
	 */
//	public int update(String sql, PubSQLParameter param) throws BusinessException ;
	
	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：批处理更新</p>
	 * @param sql      要执行的SQL语句，要求是预处理语句
	 * @param params   批量参数
	 * @return int[]   影响的记录条数
	 * @throws BusinessException
	 * @author 
	 */
//	public int[] update(String sql, List<PubSQLParameter> params)  throws BusinessException ;
	
	/**
	 * 
	 * <p>方法名称：query</p>
	 * <p>方法描述：根据sql语句进行查询</p>
	 * @param sql  查询语句
	 * @param param  查询条件
	 * @param process  结果格式
	 * @return
	 * @throws Exception
	 * @author 
	 */
//	public Object query(String sql, PubSQLParameter param,
//		      ResultSetProcessor process) throws BusinessException ;

	/**
	 * <p>方法名称：getWFProcessDefId</p>
	 * <p>方法描述：工作流定义通过扩展字段得到值</p>
	 * @param pkMessage
	 * @return
	 * @author 
	 */
	public List getWFProcessDefId(String pkMessage);
}