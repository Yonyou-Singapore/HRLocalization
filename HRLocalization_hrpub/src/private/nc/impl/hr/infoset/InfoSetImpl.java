package nc.impl.hr.infoset;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.codesync.scanner.ScanService;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.framework.provision.server.IProvisionService;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pf.pub.PfDataCache;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.hr.infoset.localization.AddLocalFieldStrategyFactory;
import nc.impl.hr.tools.formconfig.FormConfigCompiler;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.hr.infoset.DefaultHookPrivate;
import nc.itf.hr.infoset.IHookPrivate;
import nc.itf.hr.infoset.IInfoSet;
import nc.itf.hr.infoset.IInfoSetQry;
import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.itf.uap.billtemplate.IBillTemplateBase;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.itf.uap.billtemplate.IBillTemplateUpgrade;
import nc.itf.uap.print.IPrintTemplateQry;
import nc.itf.uap.querytemplate.IQueryTemplate;
import nc.itf.uap.querytemplate.IQueryTemplateBase;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.md.MDBaseQueryFacade;
import nc.md.gen.generator.ICodeGenerator;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.IComponent;
import nc.md.model.IPrimaryKey;
import nc.md.model.MetaDataException;
import nc.md.model.access.javamap.NCBeanStyle;
import nc.md.model.type.IEnumType;
import nc.md.model.type.IType;
import nc.md.persist.designer.service.IMDVOManegerService;
import nc.md.persist.designer.service.IPublishService;
import nc.md.persist.designer.vo.ClassVO;
import nc.md.persist.designer.vo.ColumnVO;
import nc.md.persist.designer.vo.PropertyVO;
import nc.md.persist.ui.model.IMDPublishModel;
import nc.md.util.MDUtil;
import nc.md.util.MDVOUtil;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.hr.infoset.IInfoSetEventType;
import nc.vo.hr.infoset.InfoItemMapVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetEventObject;
import nc.vo.hr.infoset.InfoSetHelper;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.infoset.InfoSortVO;
import nc.vo.hr.tools.formconfig.CodeGenUtils;
import nc.vo.ml.LanguageVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.SuperVOUtil;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.print.PrintTempletmanageItemVO;
import nc.vo.pub.query.IQueryConstants;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryConditionVOMeta;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.pub.query.QueryTempletVO;
import nc.vo.pub.query.QueryTempletVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
import nc.vo.querytemplate.md.MDTemplateUtil;
import nc.vo.sm.funcreg.ModuleVO;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.VOHashKeyAdapter;
import nc.vo.trade.voutils.VOUtil;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.wa.item.LocalLicenseUtil;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/***************************************************************************
 * <br>
 * Created on 2009-12-4 14:37:33<br>
 * @author Rocex Wang
 ***************************************************************************/
public class InfoSetImpl implements IInfoSet, IInfoSetQry
{
    private final BaseDAO baseDAO = new BaseDAO();
    
    private final String DOC_NAME = "InfoSet";
    
    private final SimpleDocServiceTemplate serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
    
    private final String strDefaultPkCorp = "@@@@";
    
    class SortedEnumeration implements Enumeration
    {
        int iIndex = 0;
        
        List list = new ArrayList();
        
        /**************************************************************
         * Created on 2013-10-29 13:41:00<br>
         * @author Rocex Wang
         * @param ee
         **************************************************************/
        public SortedEnumeration(Enumeration ee)
        {
            super();
            
            List list2 = EnumerationUtils.toList(ee);
            
            Object[] array = list2.toArray();
            
            Arrays.sort(array);
            
            list.addAll(Arrays.asList(array));
        }
        
        /**************************************************************
         * {@inheritDoc}<br>
         * Created on 2013-10-29 13:40:55<br>
         * @see java.util.Enumeration#hasMoreElements()
         * @author Rocex Wang
         **************************************************************/
        @Override
        public boolean hasMoreElements()
        {
            return list.size() > iIndex;
        }
        
        /**************************************************************
         * {@inheritDoc}<br>
         * Created on 2013-10-29 13:40:55<br>
         * @see java.util.Enumeration#nextElement()
         * @author Rocex Wang
         **************************************************************/
        @Override
        public Object nextElement()
        {
            if (hasMoreElements())
            {
                return list.get(iIndex++);
            }
            
            throw new NoSuchElementException();
        }
    }
    
    class SortedProperties extends Properties
    {
        @Override
        public synchronized Enumeration<Object> keys()
        {
            Enumeration<Object> keys = new SortedEnumeration(super.keys());
            
            return keys;
        }
        
        /**************************************************************
         * {@inheritDoc}<br>
         * Created on 2014-3-17 15:19:44<br>
         * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
         * @author Rocex Wang
         **************************************************************/
        @Override
        public synchronized Object put(Object key, Object value)
        {
            if (key == null || value == null)
            {
                return null;
            }
            
            return super.put(key, value);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-28 11:09:23<br>
     * @param mapBodyVO
     * @param listBodyVO
     * @author Rocex Wang
     ***************************************************************************/
    void addRemainderBodyTemplet(Map<String, BillTempletBodyVO> mapBodyVO, List<BillTempletBodyVO> listBodyVO)
    {
        for (Iterator<BillTempletBodyVO> iterator = mapBodyVO.values().iterator(); iterator.hasNext();)
        {
            addToBodyTemplet(listBodyVO, iterator.next());
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-28 10:44:23<br>
     * @param listBodyVO
     * @param bodyVO
     * @author Rocex Wang
     ***************************************************************************/
    void addToBodyTemplet(List<BillTempletBodyVO> listBodyVO, BillTempletBodyVO bodyVO)
    {
        if (bodyVO == null || listBodyVO == null)
        {
            return;
        }
        
        boolean blExist = false;
        
        String strBillItemKey = getBillItemKey(bodyVO.getPos(), bodyVO.getTableCode(), bodyVO.getItemkey());
        
        for (Iterator<BillTempletBodyVO> iterator = listBodyVO.iterator(); iterator.hasNext();)
        {
            BillTempletBodyVO bodyVO2 = iterator.next();
            
            String strBillItemKey2 = getBillItemKey(bodyVO2.getPos(), bodyVO2.getTableCode(), bodyVO2.getItemkey());
            
            if (ObjectUtils.equals(strBillItemKey, strBillItemKey2))
            {
                blExist = true;
                break;
            }
        }
        
        if (!blExist)
        {
            listBodyVO.add(bodyVO);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-6 15:08:52<br>
     * @param listPrintItemVO
     * @param printItemVO
     * @author Rocex Wang
     ***************************************************************************/
    void addToPrintItemVO(List<PrintTempletmanageItemVO> listPrintItemVO, PrintTempletmanageItemVO printItemVO)
    {
        if (listPrintItemVO == null || printItemVO == null)
        {
            return;
        }
        
        listPrintItemVO.add(printItemVO);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-5 10:09:00<br>
     * @param listQryConditionVO
     * @param qryConditionVO
     * @author Rocex Wang
     ***************************************************************************/
    void addToQryConditionVO(List<QueryConditionVO> listQryConditionVO, QueryConditionVO qryConditionVO)
    {
        if (listQryConditionVO == null || qryConditionVO == null)
        {
            return;
        }
        
        listQryConditionVO.add(qryConditionVO);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-25 16:08:43<br>
     * @param bean
     * @param iPos
     * @param mapFromDbBodyVO
     * @param mapFromMdBodyVO
     * @param listBodyVO
     * @author Rocex Wang
     ***************************************************************************/
    void addTs(IBean bean, int iPos, Map<String, BillTempletBodyVO> mapFromDbBodyVO, Map<String, BillTempletBodyVO> mapFromMdBodyVO,
            List<BillTempletBodyVO> listBodyVO)
    {
        String strBillItemKey = getBillItemKey(iPos, bean.getName(), "ts");
        
        BillTempletBodyVO fromDbBodyVO = mapFromDbBodyVO.get(strBillItemKey);
        
        if (fromDbBodyVO != null)
        {
            addToBodyTemplet(listBodyVO, fromDbBodyVO);
            return;
        }
        
        BillTempletBodyVO fromMdBodyVO = mapFromMdBodyVO.get(strBillItemKey);
        if (fromMdBodyVO != null)
        {
            fromMdBodyVO.setShoworder(bean.getAttributes().size());
            
            addToBodyTemplet(listBodyVO, fromMdBodyVO);
        }
    }
    
    /****************************************************************************
     * 根据信息集描述信息调整数据库结构，只增加表、字段，不删除表、不调整字段类型、不删除字段。<br>
     * Created on 2010-4-30 8:40:46<br>
     * @see nc.itf.hr.infoset.IInfoSet#adjustDB(InfoSetVO[])
     * @author Rocex Wang
     ****************************************************************************/
    public void adjustDB(IHookPrivate hookPrivate, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        for (InfoSetVO infoSetVO2 : infoSetVOs)
        {
            if (infoSetVO2 == null)
            {
                continue;
            }
            
            InfoSetVO infoSetVO = (InfoSetVO) infoSetVO2.clone();
            
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            if (infoItemVOs != null)
            {
                List<InfoItemVO> listInfoItemVO = new ArrayList<InfoItemVO>();
                
                for (InfoItemVO infoItemVO : infoItemVOs)
                {
                    listInfoItemVO.add(infoItemVO);
                    
                    if (IBillItem.MULTILANGTEXT == infoItemVO.getData_type())
                    {
                        for (int i = 2; i < 7; i++)
                        {
                            InfoItemVO infoItemVO2 = (InfoItemVO) infoItemVO.clone();
                            infoItemVO2.setItem_code(infoItemVO.getItem_code() + i);
                            listInfoItemVO.add(infoItemVO2);
                        }
                    }
                }
                
                infoSetVO.setInfo_item(listInfoItemVO.toArray(new InfoItemVO[0]));
            }
            
            // 如果缺失表，则新建表，包括字段
            boolean blTableExisted = baseDAO.isTableExisted(infoSetVO.getTable_code());
            
            if (!blTableExisted)
            {
                createDBTable(infoSetVO);
                
                hookPrivate.createDBIndex(infoSetVO);
                
                continue;
            }
            
            // 如果表中缺失字段，则新建字段
            if (infoSetVO.getInfo_item() == null || infoSetVO.getInfo_item().length == 0)
            {
                continue;
            }
            
            // 收集所有的字段名
            List<String> listFieldCode = new ArrayList<String>();
            
            for (InfoItemVO infoItemVO : infoSetVO.getInfo_item())
            {
                listFieldCode.add(infoItemVO.getItem_code().toLowerCase());
            }
            
            boolean blFieldExisted[] = SQLHelper.isExistFields(infoSetVO.getTable_code(), listFieldCode.toArray(new String[0]));
            
            for (int i = 0; i < blFieldExisted.length; i++)
            {
                if (!blFieldExisted[i])
                {
                    createDBField(infoSetVO.getTable_code(), infoSetVO.getInfo_item()[i]);
                }
            }
            
            // 清空对该表的缓存
            JdbcPersistenceManager.clearColumnTypes(infoSetVO.getTable_code());
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2016-1-13 14:18:03<br>
     * @param strMainMetaId
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    public void adjustQueryTemplet(String strMainMetaId) throws BusinessException
    {
        // 查询主表
        Collection collQueryTempletVO = baseDAO.retrieveByClause(QueryTempletVO.class, new QueryTempletVOMeta(), "metaclass='" + strMainMetaId + "'");
        
        if (collQueryTempletVO.isEmpty())
        {
            return;
        }
        
        QueryTempletVO[] templateVOs = (QueryTempletVO[]) collQueryTempletVO.toArray(new QueryTempletVO[collQueryTempletVO.size()]);
        
        ArrayList<String> listPkTemplet = (ArrayList<String>) VOUtil.extractFieldValues(templateVOs, "id", null);
        
        Collection collConditionVO =
            baseDAO.retrieveByClause(QueryConditionVO.class, new QueryConditionVOMeta(),
                "pk_templet in (" + new InSQLCreator().getInSQL(listPkTemplet.toArray(new String[0])) + ")");
        
        QueryConditionVO[] conditionVOs = (QueryConditionVO[]) collConditionVO.toArray(new QueryConditionVO[collConditionVO.size()]);
        
        if (conditionVOs.length > 0)
        {
            List<String> listDeletedConditionId = new ArrayList<String>();
            
            HashMap<String, List<QueryConditionVO>> mapPkTempletConditionVO =
                Hashlize.hashlizeVOs(conditionVOs, new VOHashKeyAdapter(new String[]{"pk_templet"}));
            
            for (QueryTempletVO templateVO : templateVOs)
            {
                if (!templateVO.isMetadata())
                {
                    continue;
                }
                
                IBean bean = null;
                try
                {
                    bean = MDBaseQueryFacade.getInstance().getBeanByID(templateVO.getMetaclass());
                }
                catch (Exception e)
                {
                    String errMsg = ResHelper.getString("_Template", "UPP_NewQryTemplate-0156"
                    /* 功能节点【{0}】下的查询模板【{1}】所依赖的元数据不存在，请修改或删除查询模板！ */, null, new String[]{templateVO.getNodeCode(), templateVO.getModelName()});
                    
                    throw new BusinessException(errMsg);
                }
                
                List<QueryConditionVO> listConditionVO = mapPkTempletConditionVO.get(templateVO.getId());
                
                if (listConditionVO.isEmpty())
                {
                    continue;
                }
                
                for (QueryConditionVO conditionVO : listConditionVO)
                {
                    if (!conditionVO.isNotMDCondition())
                    {
                        String fieldCode = conditionVO.getFieldCode();
                        if (bean.getAttributeByPath(fieldCode) == null)
                        {
                            listDeletedConditionId.add(conditionVO.getId());
                        }
                    }
                }
            }
            
            if (!listDeletedConditionId.isEmpty())
            {
                baseDAO.deleteByPKs(new QueryConditionVOMeta(), listDeletedConditionId.toArray(new String[0]));
            }
        }
    }
    
    /***************************************************************************
     * 检查信息集类别下的所有信息集是否合法，比如，有没有一个是主集<br>
     * Created on 2010-5-4 9:22:09<br>
     * @param strPk_infoSort
     * @param infoSetNewVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void checkInfoSet(String strPk_infoSort, InfoSetVO... infoSetNewVOs) throws BusinessException
    {
        InfoSetVO infoSetVOs[] = queryInfoSetBySortPk(strPk_infoSort);
        
        if (infoSetVOs == null)
        {
            infoSetVOs = (InfoSetVO[]) ArrayUtils.addAll(infoSetVOs, infoSetNewVOs);
        }
        else if (infoSetNewVOs != null)
        {
            for (InfoSetVO infoSetVO : infoSetNewVOs)
            {
                int iFindIndex = -1;
                
                for (int i = 0; i < infoSetVOs.length; i++)
                {
                    if (infoSetVO.equalsContent(infoSetVOs[i], new String[]{InfoSetVO.PK_INFOSET}))
                    {
                        iFindIndex = i;
                    }
                }
                
                if (iFindIndex == -1)
                {
                    infoSetVOs = (InfoSetVO[]) ArrayUtils.add(infoSetVOs, infoSetVO);
                }
                else
                {
                    infoSetVOs[iFindIndex] = infoSetVO;
                }
            }
        }
        
        // 检查是否有且仅有一个主集
        int iMainInfoSetCount = 0;
        
        for (InfoSetVO infoSetVO2 : infoSetVOs)
        {
            if (infoSetVO2 == null)
            {
                continue;
            }
            
            if (infoSetVO2.getMain_table_flag() != null && infoSetVO2.getMain_table_flag().booleanValue())
            {
                iMainInfoSetCount++;
            }
        }
        
        if (iMainInfoSetCount == 0)
        {
            throw new BusinessException(ResHelper.getString("6001infset", "06001infset0069")
            /* @res "信息集类别下没有主集！" */);
        }
        
        if (iMainInfoSetCount > 1)
        {
            throw new BusinessException(ResHelper.getString("6001infset", "06001infset0070")
            /* @res "同一个信息集类别下不能有多个主集！" */);
        }
        
        // 检查同一个信息集分类下面不能有同名的信息集
        String strInfoSets[] = {"infoset_name", "infoset_name2", "infoset_name3", "infoset_name4", "infoset_name5", "infoset_name6"};
        
        for (String strInfoSet : strInfoSets)
        {
            HashSet<String> hashInfoSetName = new HashSet<String>();// 信息集名称
            
            for (InfoSetVO infoSetVO2 : infoSetVOs)
            {
                String strInfoSetName = (String) infoSetVO2.getAttributeValue(strInfoSet);
                
                if (StringUtils.isBlank(strInfoSetName))
                {
                    continue;
                }
                
                if (hashInfoSetName.contains(strInfoSetName))
                {
                    throw new BusinessException(ResHelper.getString("6001infset", "06001infset0071", strInfoSetName)
                    /* @res "同一个信息集类别下不能有两个相同信息集名称的子集[{0}]！" */);
                }
                
                hashInfoSetName.add(strInfoSetName);
                
                // 检查同一个信息集中是否有相同名称的信息项
                infoSetVO2.checkInfoItems();
            }
        }
        
        // 检查同一个信息集中是否有相同名称的信息项
        for (InfoSetVO infoSetVO2 : infoSetVOs)
        {
            infoSetVO2.checkInfoItems();
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-8-13 14:26:48<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#checkMdVersion(java.util.Map)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Map<IComponent, UFDateTime> checkMdVersion(final Map<String, UFDateTime> mapComponetTs) throws BusinessException
    {
        String strSQL = "select id,ts from md_component where id in(select componentid from md_class where id in(select meta_data_id from hr_infoset))";
        
        return (Map<IComponent, UFDateTime>) new BaseDAO().executeQuery(strSQL, new BaseProcessor()
        {
            @Override
            public Object processResultSet(ResultSet rs) throws SQLException
            {
                HashMap<IComponent, UFDateTime> map = new HashMap<>();
                
                while (rs.next())
                {
                    String strMdId = (String) rs.getObject(1);
                    UFDateTime ts = new UFDateTime(rs.getString(2));
                    
                    UFDateTime ts2 = mapComponetTs.get(strMdId);
                    
                    if (ts2 == null || ts.after(ts2))
                    {
                        try
                        {
                            IComponent component = MDBaseQueryFacade.getInstance().getComponentByID(strMdId);
                            
                            map.put(component, ts);
                        }
                        catch (MetaDataException ex)
                        {
                            Logger.error(ex.getMessage(), ex);
                        }
                    }
                }
                
                return map;
            }
        });
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-24 18:48:53<br>
     * @param bean
     * @param iPos
     * @param iTabIndex
     * @return BillTabVO
     * @author Rocex Wang
     ***************************************************************************/
    BillTabVO createBillTabVO(IBean bean, int iPos, int iTabIndex)
    {
        BillTabVO billTabVO = new BillTabVO();
        
        billTabVO.setPos(iPos);
        billTabVO.setPosition(iPos);
        
        billTabVO.setTabindex(iTabIndex);
        billTabVO.setTabcode(bean.getName());
        billTabVO.setTabname(bean.getDisplayName());
        billTabVO.setResid(bean.getName());
        
        billTabVO.setMetadatapath(bean.getName());
        billTabVO.setMetadataclass(bean.getFullName());
        
        return billTabVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-24 18:48:56<br>
     * @param bean
     * @param iPos
     * @return BillTempletBodyVO
     * @author Rocex Wang
     ***************************************************************************/
    BillTempletBodyVO[] createBillTempletBodyVO(IBean bean, int iPos)
    {
        List<BillTempletBodyVO> listBodyVO = new ArrayList<BillTempletBodyVO>();
        
        List<IAttribute> listAttribute = bean.getAttributesOfModel();
        
        listAttribute.add(bean.getAttributeByName("ts"));
        
        IPrimaryKey primaryKey = bean.getPrimaryKey();
        
        for (IAttribute attribute : listAttribute)
        {
            if (MDUtil.isCollectionType(attribute.getDataType()))
            {
                continue;
            }
            
            IMetaDataProperty metaData = MetaDataPropertyFactory.creatMetaDataProperty(attribute);
            
            BillTempletBodyVO bodyVO = new BillTempletBodyVO();
            
            bodyVO.setCardflag(Boolean.TRUE);
            bodyVO.setDatatype(metaData.getDataType());
            // bodyVO.setDefaultshowname(metaData.getShowName());
            bodyVO.setDefaultvalue(metaData.getDefaultValue());
            bodyVO.setEditflag(metaData.isEditable());
            bodyVO.setForeground(-1);
            bodyVO.setInputlength(metaData.getInputLength());
            bodyVO.setItemkey(metaData.getName());
            bodyVO.setLeafflag(UFBoolean.FALSE);
            bodyVO.setList(true);
            bodyVO.setListflag(Boolean.TRUE);
            bodyVO.setListshoworder(attribute.getSequence());
            bodyVO.setListshowflag(!primaryKey.getPKColumn().getName().equals(metaData.getName()));
            bodyVO.setLockflag(Boolean.FALSE);
            bodyVO.setMetadatapath(metaData.getName());
            bodyVO.setMetadataproperty(metaData.getFullName());
            bodyVO.setNewlineflag(UFBoolean.FALSE);
            bodyVO.setNullflag(metaData.isNotNull());
            bodyVO.setPos(iPos);
            bodyVO.setReviseflag(UFBoolean.FALSE);
            bodyVO.setShowflag(!primaryKey.getPKColumn().getName().equals(metaData.getName()));
            bodyVO.setShoworder(attribute.getSequence());
            bodyVO.setTableCode(bean.getName());
            bodyVO.setTableName(bean.getDisplayName());
            bodyVO.setTotalflag(Boolean.FALSE);
            bodyVO.setUsereditflag(Boolean.TRUE);
            bodyVO.setUserflag(Boolean.TRUE);
            bodyVO.setUsershowflag(Boolean.TRUE);
            bodyVO.setWidth(iPos == IBillItem.BODY ? 100 : 1);
            
            bodyVO.setStatus(VOStatus.NEW);
            
            listBodyVO.add(bodyVO);
        }
        
        return listBodyVO.toArray(new BillTempletBodyVO[0]);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-24 18:48:59<br>
     * @return BillTempletHeadVO
     * @author Rocex Wang
     * @param infoSortVO
     ***************************************************************************/
    BillTempletHeadVO createBillTempletHeadVO(InfoSortVO infoSortVO)
    {
        BillTempletHeadVO headVO = new BillTempletHeadVO();
        
        headVO.setBillType(infoSortVO.getBill_type_code());
        headVO.setBillTempletName("SYSTEM");
        headVO.setBillTempletCaption(infoSortVO.getSort_name());
        headVO.setPkCorp(strDefaultPkCorp);
        headVO.setPkBillTypeCode(infoSortVO.getBill_type_code());
        
        BillStructVO billStructVO = new BillStructVO();
        
        headVO.setStructvo(billStructVO);
        
        return headVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-25 9:01:34<br>
     * @param bean
     * @param infoSortVO
     * @param infoSetVO
     * @param iPos
     * @return BillTempletVO
     * @author Rocex Wang
     ***************************************************************************/
    BillTempletVO createBillTempletVO(IBean bean, InfoSortVO infoSortVO, InfoSetVO infoSetVO, int iPos)
    {
        BillTempletVO billTempletVO = new BillTempletVO();
        
        BillTabVO billTabVO = createBillTabVO(bean, iPos, infoSetVO.getShoworder());
        
        BillTempletHeadVO headVO = createBillTempletHeadVO(infoSortVO);
        BillTempletBodyVO bodyVOs[] = createBillTempletBodyVO(bean, iPos);
        
        BillStructVO billStructVO = new BillStructVO();
        billStructVO.setBillTabVOs(new BillTabVO[]{billTabVO});
        
        headVO.setStructvo(billStructVO);
        
        billTempletVO.setParentVO(headVO);
        billTempletVO.setChildrenVO(bodyVOs);
        
        return billTempletVO;
    }
    
    /***************************************************************************
     * 给表中添加字段<br>
     * Created on 2010-5-15 10:00:17<br>
     * @param strTableCode
     * @param infoItemVO
     * @author Rocex Wang
     * @throws DAOException
     ***************************************************************************/
    void createDBField(String strTableCode, InfoItemVO infoItemVO) throws DAOException
    {
        String strSQL = null;
        
        if (DataSourceCenter.getInstance().getDatabaseType() == DBConsts.DB2)
        {
            strSQL = "alter table {0} add column {1}";
        }
        else if (DataSourceCenter.getInstance().getDatabaseType() == DBConsts.ORACLE)
        {
            strSQL = "alter table {0} add ({1})";
        }
        else
        {
            strSQL = "alter table {0} add {1}";
        }
        
        String strField = createDBFieldSQL(infoItemVO);
        
        strSQL = MessageFormat.format(strSQL, strTableCode, strField);
        
        try
        {
            baseDAO.executeUpdate(strSQL);
        }
        catch (Exception ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
    }
    
    /***************************************************************************
     * SQL语句的字段描述<br>
     * Created on 2010-5-22 14:14:36<br>
     * @param infoItemVO
     * @return String
     * @author Rocex Wang
     ***************************************************************************/
    String createDBFieldSQL(InfoItemVO infoItemVO)
    {
        int iDataBaseType = DataSourceCenter.getInstance().getDatabaseType();
        
        String strSQL = "{0} {1} {2}";
        
        String strChar = "char";
        String strVarchar = "varchar";
        
        if (DataSourceCenter.getInstance().getDatabaseType() == DBConsts.SQLSERVER)
        {
            strChar = "nchar";
            strVarchar = "nvarchar";
        }
        else if (DataSourceCenter.getInstance().getDatabaseType() == DBConsts.ORACLE)
        {
            strVarchar = "varchar2";
        }
        
        String strDataType = strVarchar + "(100)";
        
        int iDataLength = infoItemVO.getMax_length() == null || infoItemVO.getMax_length() < 0 ? 100 : infoItemVO.getMax_length();
        
        switch (infoItemVO.getData_type())
        {
            case IBillItem.BOOLEAN :
                strDataType = strChar + "(1)";
                break;
            case IBillItem.COMBO :
                String strEnumId = infoItemVO.getEnum_id();
                IType enumClassVO = null;
                if (strEnumId != null)
                {
                    try
                    {
                        enumClassVO = MDBaseQueryFacade.getInstance().getTypeByID(strEnumId, IType.STYLE_SINGLE);
                    }
                    catch (MetaDataException ex)
                    {
                        Logger.error(ex.getMessage(), ex);
                    }
                }
                
                // 只处理整形，剩下的都是字符型的
                if (enumClassVO != null && enumClassVO instanceof IEnumType
                    && IBDMetaDataIDConst.TYPE_INTEGER.equals(((IEnumType) enumClassVO).getReturnType().getID()))
                {
                    strDataType = "int";
                }
                else
                {
                    strDataType = strVarchar + "(" + iDataLength + ")";
                }
                break;
            case IBillItem.DATE :
            case IBillItem.DATETIME :
                strDataType = strVarchar + "(19)";
                break;
            case IBillItem.DECIMAL :
            case IBillItem.MONEY :
                int iPrecise = infoItemVO.getPrecise() == null ? 0 : infoItemVO.getPrecise();
                strDataType = "decimal(" + iDataLength + "," + iPrecise + ")";
                break;
            case IBillItem.IMAGE :
                if (iDataBaseType == DBConsts.ORACLE)
                {
                    strDataType = "blob";
                }
                else if (iDataBaseType == DBConsts.DB2)
                {
                    strDataType = "blob(128m)";
                }
                else
                {
                    strDataType = "image";
                }
                
                break;
            case IBillItem.INTEGER :
                strDataType = "int";
                break;
            case IBillItem.MULTILANGTEXT :
                strDataType = strVarchar + "(" + iDataLength + ")";
                break;
            case IBillItem.TIME :
                strDataType = strVarchar + "(8)";
                break;
            case IBillItem.UFREF :
                strDataType = strVarchar + "(20)";
                break;
            case IBillItem.STRING :
            case IBillItem.TEXTAREA :
            case IBillItem.EMAILADDRESS :
            default :
                strDataType = strVarchar + "(" + iDataLength + ")";
                break;
        }
        
        // 自定义项非必输
        String strNullEnble =
            infoItemVO.getCustom_attr() != null && infoItemVO.getCustom_attr().booleanValue() || infoItemVO.getNullable() == null
                || infoItemVO.getNullable().booleanValue() ? DataSourceCenter.getInstance().getDatabaseType() != DBConsts.DB2 ? "null" : "" : "not null";
        
        strSQL = MessageFormat.format(strSQL, infoItemVO.getItem_code(), strDataType, strNullEnble);
        
        return strSQL;
    }
    
    /***************************************************************************
     * 给数据库中添加表<br>
     * Created on 2010-5-15 9:51:09<br>
     * @param infoSetVO
     * @author Rocex Wang
     * @throws DAOException
     ***************************************************************************/
    void createDBTable(InfoSetVO infoSetVO) throws DAOException
    {
        InfoItemVO infoItems[] = null;
        
        if (infoSetVO == null || (infoItems = infoSetVO.getInfo_item()) == null || infoItems.length == 0)
        {
            return;
        }
        
        // 对infoItems排序
        VOUtil.sort(infoItems, new String[]{InfoItemVO.ITEM_CODE}, new int[]{VOUtil.ASC});
        
        String strSQL = "create table {0} ({1},constraint pk_{2} primary key ({3}),{4})";
        
        String strFieldSQL = "";
        
        for (InfoItemVO infoItem : infoItems)
        {
            // 如果是主键字段，强制非空
            if (ObjectUtils.equals(infoSetVO.getPk_field_code(), infoItem.getItem_code()))
            {
                infoItem.setNullable(UFBoolean.FALSE);
            }
            else if (ArrayUtils.contains(new String[]{"dr", "ts"}, infoItem.getItem_code().toLowerCase()))// 如果是ts、dr字段，跳过
            {
                continue;
            }
            
            strFieldSQL += createDBFieldSQL(infoItem) + ",";
        }
        
        if (strFieldSQL.length() != 0)
        {
            strFieldSQL = strFieldSQL.substring(0, strFieldSQL.length() - 1);
        }
        
        String strTsDr = null;
        
        int iDataBaseType = DataSourceCenter.getInstance().getDatabaseType();
        
        if (iDataBaseType == DBConsts.ORACLE)
        {
            strTsDr = "ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),dr number(10) default 0";
        }
        else if (iDataBaseType == DBConsts.DB2)
        {
            strTsDr = "ts char(19) null,dr smallint null default 0";
        }
        else
        {
            strTsDr = "ts char(19) null default convert(char(19),getdate(),20),dr smallint null default 0";
        }
        
        strSQL = MessageFormat.format(strSQL, infoSetVO.getTable_code(), strFieldSQL, infoSetVO.getTable_code(), infoSetVO.getPk_field_code(), strTsDr);
        
        baseDAO.executeUpdate(strSQL);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-6 15:08:01<br>
     * @param infoSetVO
     * @param strPrefix
     * @return PrintTempletmanageItemVO[]
     * @author Rocex Wang
     * @param strFuncCode
     ***************************************************************************/
    PrintTempletmanageItemVO[] createPrintItemVO(InfoSetVO infoSetVO, String strPrefix, String strFuncCode)
    {
        InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
        
        List<PrintTempletmanageItemVO> listBodyVO = new ArrayList<PrintTempletmanageItemVO>();
        
        for (InfoItemVO infoItemVO : infoItemVOs)
        {
            PrintTempletmanageItemVO printItemVO = new PrintTempletmanageItemVO();
            
            printItemVO.setStatus(VOStatus.NEW);
            printItemVO.setItype(null);
            printItemVO.setPk_corp(strDefaultPkCorp);
            printItemVO.setResid(infoItemVO.getResid());
            printItemVO.setUserdefflag(infoItemVO.getCustom_attr());
            printItemVO.setVnodecode(strFuncCode);
            printItemVO.setVtablecode(null);
            printItemVO.setVtablename(null);
            printItemVO.setVvarexpress(strPrefix + infoItemVO.getItem_code());
            printItemVO.setVvarname(infoItemVO.getItem_name());
            
            listBodyVO.add(printItemVO);
        }
        
        return listBodyVO.toArray(new PrintTempletmanageItemVO[0]);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-5 10:05:22<br>
     * @param bean
     * @param blMainTable
     * @return QueryConditionVO[]
     * @author Rocex Wang
     ***************************************************************************/
    QueryConditionVO[] createQueryConditionVO(IBean bean, boolean blMainTable)
    {
        List<QueryConditionVO> listQryConditionVO = new ArrayList<QueryConditionVO>();
        
        List<IAttribute> listAttribute = bean.getAttributesOfModel();
        
        int iShowOrder = 0;
        
        for (IAttribute attribute : listAttribute)
        {
            if (attribute.isHide())// 不显示的不用加到查询条件里面
            {
                continue;
            }
            
            IMetaDataProperty metadata = MetaDataPropertyFactory.creatMetaDataProperty(attribute);
            
            int iDataType = metadata.getDataType();
            
            // 对象、图片不作为查询条件
            if (iDataType == IBillItem.OBJECT || iDataType == IBillItem.IMAGE)
            {
                continue;
            }
            
            int iDispType = 0;
            int iReturnType = 2;
            
            // 数值型的需要设置数据精度
            String strConsultCode = dealDigitQryConditionVO(attribute, iDataType);
            
            UFBoolean blIsUsed = UFBoolean.TRUE;
            
            // 数据类型上的一点转换
            switch (iDataType)
            {
                case IBillItem.MULTILANGTEXT :
                    iDataType = IQueryConstants.MULTILANG;
                    break;
                case IBillItem.DATETIME :
                    iDataType = IQueryConstants.TIME;
                    break;
                case IBillItem.TIME :
                    iDataType = IQueryConstants.UFTIME;
                    break;
                case IBillItem.TEXTAREA :
                    iDataType = IQueryConstants.STRING;
                    break;
                case IBillItem.MONEY :
                    iDataType = IQueryConstants.DECIMAL;
                    break;
                case IBillItem.LITERALDATE :
                    iDataType = IQueryConstants.LITERALDATE;
                    break;
                case IBillItem.UFREF :
                    iDispType = 1;
                    break;
                case IBillItem.COMBO :
                    iDispType = 1;
                    break;
                case InfoItemVO.DATE_FORMULA :
                    blIsUsed = UFBoolean.FALSE;
                    break;
                default :
                    break;
            }
            
            QueryConditionVO conditionVO = new QueryConditionVO();
            
            conditionVO.setDataType(iDataType);
            conditionVO.setConsultCode(strConsultCode);
            conditionVO.setDispSequence(iShowOrder++);
            conditionVO.setDispType(iDispType);
            conditionVO.setFieldCode(blMainTable ? metadata.getName() : bean.getName() + "." + metadata.getName());
            conditionVO.setFieldName(blMainTable ? metadata.getShowName() : bean.getDisplayName() + "." + metadata.getShowName());
            conditionVO.setIfAutoCheck(UFBoolean.TRUE);
            conditionVO.setIfDefault(UFBoolean.FALSE);// 默认都是不放到右边编辑器里面的
            conditionVO.setIfGroup(UFBoolean.FALSE);
            conditionVO.setIfImmobility(UFBoolean.FALSE);
            conditionVO.setIfMust(UFBoolean.FALSE);
            conditionVO.setIfOrder(UFBoolean.FALSE);
            conditionVO.setIfSum(UFBoolean.FALSE);
            conditionVO.setIfUsed(blIsUsed);
            conditionVO.setIsCondition(UFBoolean.TRUE);
            conditionVO.setOperaCode(MDTemplateUtil.getQTOperatorsByType(iDataType, iReturnType));
            conditionVO.setOrderSequence(0);
            conditionVO.setPkCorp(strDefaultPkCorp);
            conditionVO.setReturnType(iReturnType);
            conditionVO.setTableCode(bean.getTable().getName());
            conditionVO.setTableName(bean.getDisplayName());
            // 查询模板的resid会自动读取元数据的，所以这个地方就不要设置值了，因为md_property表中的resid 的长度大于pub_query_condition表中的resid的长度
            // conditionVO.setResid(attribute.getResID());
            
            if (iDataType == IQueryConstants.UFREF && attribute.getRefModelName() != null)
            {
                conditionVO.setConsultCode(attribute.getRefModelName());
            }
            else if (iDataType == IQueryConstants.USERCOMBO)
            {
                conditionVO.setConsultCode(getComboDesc(attribute));
            }
            
            conditionVO.setStatus(VOStatus.NEW);
            
            listQryConditionVO.add(conditionVO);
        }
        
        return listQryConditionVO.toArray(new QueryConditionVO[0]);
    }
    
    /**************************************************************
     * 数值型的需要设置数据精度<br>
     * Created on 2013-7-18 18:43:48<br>
     * @param attribute
     * @author Rocex Wang
     * @param strMaxValue
     * @param strMinValue
     **************************************************************/
    String dealDigitQryConditionVO(IAttribute attribute, int iDataType)
    {
        Integer iPrecise = attribute.getPrecise();
        String strMinValue = attribute.getMinValue();
        String strMaxValue = attribute.getMaxValue();
        
        // 数值型的需要设置数据精度
        if (!ArrayUtils.contains(new int[]{IBillItem.INTEGER, IBillItem.DECIMAL, IBillItem.DEFAULT_DECIMAL_DIGITS, IBillItem.MONEY}, iDataType))
        {
            return "-99";
        }
        
        String strConsultCode = "";
        
        boolean blMinIsDigit = NumberUtils.isDigits(strMinValue);
        boolean blMaxIsDigit = NumberUtils.isDigits(strMaxValue);
        
        if (blMinIsDigit && blMaxIsDigit)
        {
            strConsultCode = strMinValue + "," + strMaxValue;
        }
        else if (blMinIsDigit && !blMaxIsDigit)
        {
            strConsultCode = strMinValue;
        }
        else if (!blMinIsDigit && blMaxIsDigit)
        {
            strConsultCode = "@," + strMaxValue;
        }
        
        if (IBillItem.INTEGER != iDataType && iPrecise != null)
        {
            strConsultCode = iPrecise.toString() + (strConsultCode.length() > 0 ? "," + strConsultCode : "");
        }
        
        return strConsultCode.length() > 0 ? strConsultCode : "-99";
    }
    
    /***************************************************************************
     * 对下拉框的处理<br>
     * Created on 2010-8-18 9:33:35<br>
     * @param metaData
     * @param infoItemVO
     * @param propertyVO
     * @author Rocex Wang
     ***************************************************************************/
    void dealMdCombo(IMDPublishModel metaData, InfoItemVO infoItemVO, PropertyVO propertyVO)
    {
        if (IBillItem.COMBO != infoItemVO.getData_type())
        {
            return;
        }
        
        propertyVO.setDataTypeStyle(IType.STYLE_SINGLE);
        
        String strDataType = infoItemVO.getEnum_id() == null ? propertyVO.getDataType() : infoItemVO.getEnum_id();
        
        ClassVO enumClassVO = metaData.getClassVOByID(strDataType);
        
        if (enumClassVO == null)// 如果在当前组件中找不到枚举的ClassVO，就在其它组件中查找。
        {
            IType newEnumClassVO = null;
            
            try
            {
                newEnumClassVO = MDBaseQueryFacade.getInstance().getTypeByID(strDataType, IType.STYLE_SINGLE);
            }
            catch (MetaDataException ex)
            {
                Logger.error(ex.getMessage(), ex);
            }
            
            if (newEnumClassVO == null)// 如果其它组件中也找不到，就创建一个
            {
                enumClassVO =
                    MDVOUtil.createClassVO(metaData.getComponentVO().getId(), infoItemVO.getItem_code(), infoItemVO.getItem_code(), "fullClassName",
                        infoItemVO.getItem_name() + ResHelper.getString("6001infset", "06001infset0072")
                        /* @res "枚举" */, null, false);
                
                enumClassVO.setClassType(IType.ENUM);
                enumClassVO.setReturnType(IBDMetaDataIDConst.TYPE_INTEGER);
                
                propertyVO.setDataType(enumClassVO.getId());
                
                AuditInfoUtil.addData(enumClassVO);
                
                metaData.addClassVO(enumClassVO);
            }
            else
            {
                propertyVO.setDataType(newEnumClassVO.getID());
            }
        }
        else
        {
            propertyVO.setDataType(enumClassVO.getId());
        }
    }
    
    /***************************************************************************
     * 对参照的处理<br>
     * Created on 2010-8-18 9:32:38<br>
     * @param metaData
     * @param infoSetVO
     * @param infoItemVO
     * @param propertyVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void dealMdRef(IMDPublishModel metaData, InfoSetVO infoSetVO, InfoItemVO infoItemVO, PropertyVO propertyVO) throws BusinessException
    {
        // || StringUtils.isNotBlank(propertyVO.getDataType())
        if (IBillItem.UFREF != infoItemVO.getData_type() || StringUtils.isBlank(infoItemVO.getRef_model_name()))
        {
            return;
        }
        
        Collection<ClassVO> collRefClassVO = null;
        
        RefInfoVO refInfoVO = (RefInfoVO) baseDAO.retrieveByPK(RefInfoVO.class, infoItemVO.getRef_model_name());
        
        String strErrMsg =
            ResHelper.getString("6001infset", "06001infset0073", infoSetVO.getInfoset_name(), infoSetVO.getInfoset_code(), infoItemVO.getItem_name(),
                infoItemVO.getItem_code(), infoItemVO.getRef_model_name());
        /*
         * @res"没有查询到参照描述信息，请检查bd_refinfo表中是否注册！信息集名称：[{0}]，信息集编码：[{1}]，信息项名称：[{2}]
         * ，信息项编码：[{3}]，对应的参照信息：[{4}]"
         */
        
        if (refInfoVO == null)
        {
            throw new BusinessException(strErrMsg);
        }
        
        // 先用严格的条件查询，排除命名空间不同的但是名称相同的实体，如果还查不到，就不带命名空间查询实体
        String strSQL =
            "md_class.name='" + refInfoVO.getMetadataTypeName() + "' and md_class.componentid in(select id from md_component where ownmodule='"
                + refInfoVO.getModuleName() + "')";
        
        collRefClassVO = baseDAO.retrieveByClause(ClassVO.class, strSQL);
        
        Logger
            .error(MessageFormat
                .format(
                    "nc.impl.hr.infoset.InfoSetImpl.dealMdRef():refInfoVO.getMetadataTypeName()->{0},refInfoVO.getModuleName()->{1},refInfoVO.getMetadatanamespace()->{2},toString()->{3}",
                    refInfoVO.getMetadataTypeName(), refInfoVO.getModuleName(), refInfoVO.getMetadatanamespace(), refInfoVO.toString()));
        
        if (collRefClassVO == null || collRefClassVO.isEmpty())
        {
            String strMetadataNamespace = refInfoVO.getMetadatanamespace();
            
            String strSQL2 =
                "md_class.name='" + refInfoVO.getMetadataTypeName() + "' and md_class.componentid in(select id from md_component where namespace='"
                    + strMetadataNamespace + "')";
            
            collRefClassVO = baseDAO.retrieveByClause(ClassVO.class, strSQL2);
        }
        
        if (collRefClassVO == null || collRefClassVO.isEmpty())
        {
            strSQL = "md_class.name='" + refInfoVO.getMetadataTypeName() + "'";
            
            collRefClassVO = baseDAO.retrieveByClause(ClassVO.class, strSQL);
            
            if (collRefClassVO == null || collRefClassVO.isEmpty())
            {
                throw new BusinessException(strErrMsg);
            }
        }
        
        ClassVO refClassVO = collRefClassVO.iterator().next();
        
        propertyVO.setDataType(refClassVO.getId());
        propertyVO.setDataTypeStyle(IType.STYLE_REF);
        propertyVO.setRefModelName(refInfoVO.getName());
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:05:30<br>
     * @see nc.itf.hr.infoset.IInfoSet#deleteInfoItem(boolean, InfoItemVO...)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void deleteInfoItem(boolean blDispatchEvent, InfoItemVO... infoItemVOs) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        for (InfoItemVO infoItemVO : infoItemVOs)
        {
            serviceTemplate.delete(infoItemVO);
        }
        
        InfoSetHelper.removeCache(infoItemVOs);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-4 14:40:39<br>
     * @see nc.itf.hr.infoset.IInfoSet#deleteInfoSet(boolean, InfoSetVO...)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void deleteInfoSet(boolean blDispatchEvent, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO == null)
            {
                continue;
            }
            
            serviceTemplate.delete(infoSetVO);
        }
        
        InfoSetHelper.removeCache(infoSetVOs);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:01:38<br>
     * @see nc.itf.hr.infoset.IInfoSet#deleteInfoSort(boolean, InfoSortVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void deleteInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        serviceTemplate.delete(infoSortVO);
        
        InfoSetHelper.removeCache(infoSortVO);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-24 15:04:12<br>
     * @param iPos
     * @param strTabCode
     * @param strItemKey
     * @return String
     * @author Rocex Wang
     ***************************************************************************/
    String getBillItemKey(int iPos, String strTabCode, String strItemKey)
    {
        return iPos == 0 || iPos == 2 ? "0.2." + strItemKey : iPos + "." + strTabCode + "." + strItemKey;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2011-8-29 10:44:40<br>
     * @param iPos
     * @param strTabCode
     * @param strItemKey
     * @return String
     * @author Rocex Wang
     ***************************************************************************/
    String getBillTabKey(int iPos, String strTabCode, String strItemKey)
    {
        return iPos + "." + strTabCode + "." + strItemKey;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2011-8-29 10:44:14<br>
     * @param attr
     * @return String
     * @author Rocex Wang
     ***************************************************************************/
    String getComboDesc(IAttribute attr)
    {
        String combo_desc = "-99";
        
        IEnumType type = (IEnumType) attr.getDataType();
        
        if (type.getReturnType().getTypeType() == IType.TYPE_Integer)
        {
            combo_desc = IQueryConstants.COMBO_INTEGER_META + "," + type.getID();// "IM";
        }
        else
        {
            combo_desc = IQueryConstants.COMBO_STRING_META + "," + type.getID(); // "SM";
        }
        
        return combo_desc;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-29 11:16:15<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#getMaxFieldCodeNumber(String, String, int)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public int getMaxFieldCodeNumber(String strTableName, String strPreCode, int iMax) throws BusinessException
    {
        if (iMax < 1)
        {
            iMax = 1;
        }
        
        for (int i = iMax;; i++)
        {
            boolean blFieldExisted = SQLHelper.isExistField(strTableName, strPreCode + i);
            
            if (!blFieldExisted)
            {
                return i;
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-16 14:56:10<br>
     * @param mainBean
     * @param metaData
     * @param infoSetVO
     * @return ClassVO
     * @author Rocex Wang
     ***************************************************************************/
    ClassVO getMdClassVO(IBean mainBean, IMDPublishModel metaData, InfoSetVO infoSetVO)
    {
        ClassVO classVO = metaData.getClassVOByName(infoSetVO.getInfoset_code());
        
        if (classVO == null || infoSetVO.getMeta_data_id() == null)// 新增的信息集
        {
            classVO =
                MDVOUtil.createClassVO(mainBean.getOwnerComponent().getID(), infoSetVO.getTable_code(), infoSetVO.getInfoset_code(),
                    infoSetVO.getVo_class_name(), infoSetVO.getInfoset_name(), null, false);
            
            classVO.setClassType(IType.ENTITY);
            classVO.setIsAuthen(UFBoolean.TRUE);
            classVO.setResid(infoSetVO.getResid());
            classVO.setHelp(ICodeGenerator.TAG_CONTAINSDYNAMICATTR);
            classVO.setAccessorClassName(NCBeanStyle.class.getName());
            
            if (infoSetVO.getMeta_data_id() != null)
            {
                classVO.setId(infoSetVO.getMeta_data_id());
            }
            
            AuditInfoUtil.addData(classVO);
            
            int iLastDotIndex = mainBean.getFullName().lastIndexOf(".");
            
            String strNameSpace = "";
            
            if (iLastDotIndex > 0)
            {
                strNameSpace = mainBean.getFullName().substring(0, iLastDotIndex);
            }
            
            infoSetVO.setMeta_data(strNameSpace + "." + classVO.getName());
            infoSetVO.setMeta_data_id(classVO.getId());
            infoSetVO.setStatus(VOStatus.UPDATED);
            
            metaData.addClassVO(classVO);
        }
        else
        {
            classVO.setName(infoSetVO.getInfoset_code());
            classVO.setDisplayName(infoSetVO.getInfoset_name());
            
            AuditInfoUtil.updateData(classVO);
        }
        
        return classVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-16 15:03:57<br>
     * @param metaData
     * @param infoSetVO
     * @param infoItemVO
     * @param propertyVO
     * @return ColumnVO
     * @author Rocex Wang
     ***************************************************************************/
    ColumnVO getMdColumnVO(IMDPublishModel metaData, InfoSetVO infoSetVO, InfoItemVO infoItemVO, PropertyVO propertyVO)
    {
        ColumnVO columnVO = metaData.getColumnVOByPropertyID(propertyVO.getId());
        
        if (columnVO == null)
        {
            boolean blIsPk = ObjectUtils.equals(infoSetVO.getPk_field_code(), infoItemVO.getItem_code());
            boolean blIsNullable = infoItemVO.getNullable() == null ? true : infoItemVO.getNullable().booleanValue();
            
            columnVO =
                MDVOUtil.creatColumnVO(infoItemVO.getItem_code(), infoItemVO.getItem_name(), infoSetVO.getTable_code(), blIsPk, blIsNullable, null,
                    ObjectUtils.toString(infoItemVO.getData_type()), infoItemVO.getMax_length());
            
            columnVO.setResid(infoItemVO.getResid());
            columnVO.setColumnSequence(infoItemVO.getShoworder());
            
            AuditInfoUtil.addData(columnVO);
            
        }
        else
        {
            columnVO.setName(infoItemVO.getItem_code());
            columnVO.setDisplayName(infoItemVO.getItem_name());
            columnVO.setTableID(infoSetVO.getTable_code());
            columnVO.setNullable(infoItemVO.getNullable());
            
            AuditInfoUtil.updateData(columnVO);
        }
        
        return columnVO;
    }
    
    /***************************************************************************
     * 如果不是主实体，就需要创建主实体和子实体之间的组合关系，组合字段用子实体的页签名<br>
     * Created on 2010-5-19 9:33:22<br>
     * @param metaData
     * @param infoSetVO
     * @param mainClassVO
     * @param subClassVO
     * @param iShowOrder
     * @throws MetaDataException
     * @author Rocex Wang
     ***************************************************************************/
    void getMdCompAssociationForEntity(IMDPublishModel metaData, InfoSetVO infoSetVO, ClassVO mainClassVO, ClassVO subClassVO, int iShowOrder)
            throws MetaDataException
    {
        if (ObjectUtils.equals(mainClassVO.getName(), subClassVO.getName()))
        {
            return;
        }
        
        boolean blFindProperty = true;
        
        PropertyVO startPropertyVO = metaData.getPropertyVOByName(mainClassVO.getName(), infoSetVO.getInfoset_code());
        
        if (startPropertyVO == null)
        {
            blFindProperty = false;
            
            startPropertyVO = MDVOUtil.createPropertyVO(infoSetVO.getInfoset_code(), infoSetVO.getInfoset_name(), mainClassVO.getId(), true, null, null);
            
            startPropertyVO.setDataTypeStyle(IType.STYLE_ARRAY);
            startPropertyVO.setAccessorClassName("nc.md.model.access.BodyOfAggVOAccessor");
        }
        
        startPropertyVO.setAttrsequence(++iShowOrder);
        
        ColumnVO columnVO = metaData.getColumnVOByPropertyID(startPropertyVO.getId());
        
        if (columnVO == null)
        {
            columnVO =
                MDVOUtil.creatColumnVO(metaData.getPropertyVOByID(mainClassVO.getKeyAttribute()).getName(), infoSetVO.getInfoset_name(),
                    infoSetVO.getTable_code(), false, true, null, subClassVO.getId(), 0);
        }
        
        if (blFindProperty)
        {
            metaData.delPropertyVOByID(startPropertyVO.getId());
        }
        
        metaData.addpropertyVO(startPropertyVO, columnVO);
        
        metaData.addCompAssociationForEntity(mainClassVO.getId(), startPropertyVO.getId(), subClassVO.getId(), true);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-16 14:59:33<br>
     * @param metaData
     * @param classVO
     * @param infoSetVO
     * @param infoItemVO
     * @return PropertyVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    PropertyVO getMdPropertyVO(IMDPublishModel metaData, ClassVO classVO, InfoSetVO infoSetVO, InfoItemVO infoItemVO) throws BusinessException
    {
        PropertyVO propertyVO = metaData.getPropertyVOByName(classVO.getName(), infoItemVO.getItem_code());
        
        if (propertyVO == null)
        {
            propertyVO = MDVOUtil.createPropertyVO(infoItemVO.getItem_code(), infoItemVO.getItem_name(), classVO.getId(), true, null, null);
            
            propertyVO.setCalculation(UFBoolean.FALSE);
            propertyVO.setDataTypeStyle(IType.STYLE_SINGLE);
            propertyVO.setDynamicattr(infoItemVO.getCustom_attr());
            propertyVO.setNotSerialize(UFBoolean.FALSE);
            
            if (infoItemVO.getId() != null)
            {
                propertyVO.setId(infoItemVO.getId());
            }
            
            propertyVO.setVisibility(0);
            
            propertyVO.setResid(infoItemVO.getResid());
            
            String strDataType = InfoSetHelper.getDataTypeByBillItem(infoItemVO.getData_type());
            
            propertyVO.setDataType(strDataType);
            
            if (infoItemVO.getCustom_attr() != null && infoItemVO.getCustom_attr().booleanValue())
            {
                propertyVO.setAccessorClassName("nc.md.model.access.NCBeanAccessor");
            }
            
            AuditInfoUtil.addData(propertyVO);
        }
        else
        {
            AuditInfoUtil.updateData(propertyVO);
        }
        
        propertyVO.setFixedLength(infoItemVO.getFixed_length());
        propertyVO.setHided(infoItemVO.getHided());
        propertyVO.setAttrLength(infoItemVO.getMax_length());
        propertyVO.setDisplayName(infoItemVO.getItem_name());
        propertyVO.setNullable(infoItemVO.getNullable());
        propertyVO.setPrecise(infoItemVO.getPrecise());
        propertyVO.setReadOnly(infoItemVO.getRead_only());
        propertyVO.setAttrsequence(infoItemVO.getShoworder());
        
        if (IBillItem.UFREF == infoItemVO.getData_type())// 对参照的处理
        {
            dealMdRef(metaData, infoSetVO, infoItemVO, propertyVO);
        }
        else if (IBillItem.COMBO == infoItemVO.getData_type())// 对下拉框的处理
        {
            dealMdCombo(metaData, infoItemVO, propertyVO);
        }
        
        return propertyVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-8-13 14:26:48<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#getMdVersion(java.util.Map)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Map<String, UFDateTime> getMdVersion(final Map<String, UFDateTime> mapComponetTs) throws BusinessException
    {
        String strSQL = "select id,ts from md_component where id in(select componentid from md_class where id in(select meta_data_id from hr_infoset))";
        
        return (Map<String, UFDateTime>) new BaseDAO().executeQuery(strSQL, new BaseProcessor()
        {
            @Override
            public Object processResultSet(ResultSet rs) throws SQLException
            {
                HashMap<String, UFDateTime> map = new HashMap<>();
                
                while (rs.next())
                {
                    String strMdId = (String) rs.getObject(1);
                    UFDateTime ts = new UFDateTime(rs.getString(2));
                    
                    map.put(strMdId, ts);
                }
                
                return map;
            }
        });
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-24 11:04:11<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#getNewTableName(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public String getNewTableName(String strPreTableName) throws BusinessException
    {
        List<String> listTables = SQLHelper.getTableNames(strPreTableName);
        
        for (int i = 1;; i++)
        {
            boolean blTableExisted = listTables.contains((strPreTableName + i).toLowerCase());
            
            if (!blTableExisted)
            {
                return strPreTableName + i;
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:05:30<br>
     * @see nc.itf.hr.infoset.IInfoSet#insertInfoItem(boolean, InfoItemVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoItemVO insertInfoItem(boolean blDispatchEvent, InfoItemVO infoItemVO) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        InfoItemVO infoItemVO2 = serviceTemplate.insert(infoItemVO);
        
        InfoSetHelper.putCache(infoItemVO2);
        
        return infoItemVO2;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-4 14:41:37<br>
     * @see nc.itf.hr.infoset.IInfoSet#insertInfoSet(boolean, InfoSetVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO insertInfoSet(boolean blDispatchEvent, InfoSetVO infoSetVO) throws BusinessException
    {
        checkInfoSet(infoSetVO.getPk_infoset_sort(), infoSetVO);
        
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        InfoSetVO infoSetVO2 = serviceTemplate.insert(infoSetVO);
        
        InfoSetHelper.putCache(infoSetVO2);
        
        return infoSetVO2;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:05:30<br>
     * @see nc.itf.hr.infoset.IInfoSet#insertInfoSort(boolean, nc.vo.hr.infoset.InfoSortVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSortVO insertInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        InfoSortVO infoSortVO2 = serviceTemplate.insert(infoSortVO);
        
        InfoSetHelper.putCache(infoSortVO2);
        
        return infoSortVO2;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-25 9:02:44<br>
     * @param mapTabVO
     * @param mapBodyVO
     * @param billTempletVO
     * @author Rocex Wang
     ***************************************************************************/
    void putToMap(Map<String, BillTabVO> mapTabVO, Map<String, BillTempletBodyVO> mapBodyVO, BillTempletVO billTempletVO)
    {
        if (billTempletVO == null)
        {
            return;
        }
        
        if (billTempletVO.getHeadVO() != null && billTempletVO.getHeadVO().getStructvo() != null
            && billTempletVO.getHeadVO().getStructvo().getBillTabVOs() != null)
        {
            BillTabVO billTabVOs[] = billTempletVO.getHeadVO().getStructvo().getBillTabVOs();
            
            for (BillTabVO billTabVO : billTabVOs)
            {
                String strBillTabKey = getBillTabKey(billTabVO.getPos(), billTabVO.getTabcode(), billTabVO.getTabcode());
                
                // 如果能找到，就用新的同步旧的
                if (mapTabVO.containsKey(strBillTabKey))
                {
                    BillTabVO billTabVO2 = mapTabVO.get(strBillTabKey);
                    billTabVO2.setTabname(billTabVO.getTabname());
                    billTabVO2.setTabindex(billTabVO.getTabindex());
                    
                    continue;
                }
                
                mapTabVO.put(strBillTabKey, billTabVO);
            }
        }
        
        if (billTempletVO.getBodyVO() != null)
        {
            BillTempletBodyVO[] bodyVOs = billTempletVO.getBodyVO();
            
            for (BillTempletBodyVO bodyVO : bodyVOs)
            {
                String strBillItemKey = getBillItemKey(bodyVO.getPos(), bodyVO.getTableCode(), bodyVO.getItemkey());
                
                mapBodyVO.put(strBillItemKey, bodyVO);
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-6 13:33:14<br>
     * @param mapPrintItemVO
     * @param printItemVOs
     * @author Rocex Wang
     ***************************************************************************/
    void putToMap(Map<String, PrintTempletmanageItemVO> mapPrintItemVO, PrintTempletmanageItemVO[] printItemVOs)
    {
        if (printItemVOs == null || printItemVOs.length == 0)
        {
            return;
        }
        
        for (PrintTempletmanageItemVO printItemVO : printItemVOs)
        {
            mapPrintItemVO.put(printItemVO.getVvarexpress(), printItemVO);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-5 9:57:01<br>
     * @param mapQryConditionVO
     * @param conditionVOs
     * @author Rocex Wang
     ***************************************************************************/
    void putToMap(Map<String, QueryConditionVO> mapQryConditionVO, QueryConditionVO[] conditionVOs)
    {
        if (conditionVOs == null || conditionVOs.length == 0)
        {
            return;
        }
        
        for (QueryConditionVO qryConditionVO : conditionVOs)
        {
            mapQryConditionVO.put(qryConditionVO.getFieldCode().toLowerCase(), qryConditionVO);
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2011-4-19 9:08:02<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoItemByPk(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoItemVO queryInfoItemByPk(String strPk_infoItem) throws BusinessException
    {
        return (InfoItemVO) baseDAO.retrieveByPK(InfoItemVO.class, strPk_infoItem);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-8-17 14:50:17<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoItemByPks(java.lang.String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Map<String, InfoItemVO> queryInfoItemByPks(String... strPk_infoItems) throws BusinessException
    {
        String strSQL = InfoItemVO.PK_INFOSET_ITEM + " in (" + new InSQLCreator().getInSQL(strPk_infoItems) + ")";
        
        Collection collInfoItemVO = baseDAO.retrieveByClause(InfoItemVO.class, strSQL);
        
        Map<String, InfoItemVO> mapInfoItemVO = new HashMap<String, InfoItemVO>();
        
        if (collInfoItemVO == null || collInfoItemVO.isEmpty())
        {
            return mapInfoItemVO;
        }
        
        for (Iterator<InfoItemVO> iterator = collInfoItemVO.iterator(); iterator.hasNext();)
        {
            InfoItemVO infoItemVO = iterator.next();
            
            mapInfoItemVO.put(infoItemVO.getPk_infoset_item(), infoItemVO);
        }
        
        return mapInfoItemVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-4 14:44:42<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSet(nc.vo.uif2.LoginContext, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO[] queryInfoSet(LoginContext context, String condition) throws BusinessException
    {
        InfoSetVO infoSetVOs[] = serviceTemplate.queryByCondition(context, InfoSetVO.class, condition);
        
        syncInfoItemWithMd(infoSetVOs);
        
        return infoSetVOs;
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2013-6-26 15:21:52<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSet(java.lang.String)
     * @author Rocex Wang
     **************************************************************/
    @Override
    public InfoSetVO[] queryInfoSet(String condition) throws BusinessException
    {
        InfoSetVO[] infoSetVOs = serviceTemplate.queryByCondition(InfoSetVO.class, condition);
        
        syncInfoItemWithMd(infoSetVOs);
        
        return infoSetVOs;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-4 14:44:45<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSetByPk(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO queryInfoSetByPk(String strPk_infoSet) throws BusinessException
    {
        InfoSetVO infoSetVO = serviceTemplate.queryByPk(InfoSetVO.class, strPk_infoSet);
        
        syncInfoItemWithMd(infoSetVO);
        
        return infoSetVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-8-18 16:14:27<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSetByPks(java.lang.String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Map<String, InfoSetVO> queryInfoSetByPks(String... strPkInfoSets) throws BusinessException
    {
        InfoSetVO[] infoSetVOs = serviceTemplate.queryByPks(InfoSetVO.class, strPkInfoSets);
        
        Map<String, InfoSetVO> mapInfoSetVO = new HashMap<String, InfoSetVO>();
        
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return mapInfoSetVO;
        }
        
        syncInfoItemWithMd(infoSetVOs);
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            mapInfoSetVO.put(infoSetVO.getPk_infoset(), infoSetVO);
        }
        
        return mapInfoSetVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-2-25 9:13:12<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSetBySortPk(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO[] queryInfoSetBySortPk(String strPk_sort) throws BusinessException
    {
        String strCondition = InfoSetVO.PK_INFOSET_SORT + "='" + strPk_sort + "' order by main_table_flag desc,showorder";
        
        InfoSetVO infoSetVOs[] = serviceTemplate.queryByCondition(InfoSetVO.class, strCondition);
        
        syncInfoItemWithMd(infoSetVOs);
        
        return infoSetVOs;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-10 15:52:24<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSort(nc.vo.uif2.LoginContext, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSortVO[] queryInfoSort(LoginContext context, String condition) throws BusinessException
    {
        Collection collInfoSortVO = baseDAO.retrieveByClause(InfoSortVO.class, condition);
        
        return (InfoSortVO[]) collInfoSortVO.toArray(new InfoSortVO[0]);
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2014-2-28 15:34:06<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSort4Tree(nc.vo.uif2.LoginContext, java.lang.String)
     * @author Rocex Wang
     **************************************************************/
    @Override
    public InfoSortVO[] queryInfoSort4Tree(LoginContext loginContext, String strWhere) throws BusinessException
    {
        // 开发模式下显式所有
        boolean blDebug = RuntimeEnv.getInstance().isDevelopMode();
        
        String strShowWhere = "";
        
        if (!blDebug)
        {
            strShowWhere = " where show_flag='Y'";
        }
        
        String strSQL =
            "select null module_code,systypecode sort_code,systypename sort_name,moduleid pk_infoset_sort,resid from dap_dapsystem"
                + " where parentcode='60' and moduleid in(select module_code from hr_infoset_sort" + strShowWhere + ")";
        
        ArrayList<InfoSortVO> listInfoSortVO = (ArrayList<InfoSortVO>) baseDAO.executeQuery(strSQL, new BeanListProcessor(InfoSortVO.class));
        
        for (InfoSortVO infoSortVO : listInfoSortVO)
        {
            infoSortVO.setSort_name(ResHelper.getString2("funcode", infoSortVO.getResid(), infoSortVO.getSort_name()));
        }
        
        if (!blDebug)
        {
            if (StringUtils.isBlank(strWhere))
            {
                strWhere = "show_flag='Y'";
            }
            else
            {
                strWhere += " and show_flag='Y'";
            }
        }
        
        Collection<InfoSortVO> collResult = baseDAO.retrieveByClause(InfoSortVO.class, strWhere);
        
        for (InfoSortVO infoSortVO : collResult)
        {
            infoSortVO.setSort_name(ResHelper.getString2(infoSortVO.getRespath(), infoSortVO.getResid(), infoSortVO.getSort_name()));
        }
        
        listInfoSortVO.addAll(collResult);
        
        return listInfoSortVO.toArray(new InfoSortVO[0]);
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2012-11-2 11:00:48<br>
     * @see nc.itf.hr.infoset.IInfoSetQry#queryInfoSortByPks(String...)
     * @author Rocex Wang
     **************************************************************/
    @Override
    public InfoSortVO[] queryInfoSortByPks(String... strPkInfoSorts) throws BusinessException
    {
        String strCondition = InfoSortVO.PK_INFOSET_SORT + " in(" + new InSQLCreator().getInSQL(strPkInfoSorts) + ")";
        
        InfoSortVO infoSortVOs[] = queryInfoSort(null, strCondition);
        
        return infoSortVOs;
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2012-12-11 20:04:08<br>
     * @see nc.itf.hr.infoset.IInfoSet#saveInfoItemMap(nc.vo.hr.infoset.InfoItemMapVO[])
     * @author Rocex Wang
     **************************************************************/
    @Override
    public void saveInfoItemMap(InfoItemMapVO[] infoItemMapVOs) throws BusinessException
    {
        if (infoItemMapVOs == null || infoItemMapVOs.length == 0)
        {
            return;
        }
        
        baseDAO.deleteVOArray(infoItemMapVOs);
        
        baseDAO.insertVOArray(infoItemMapVOs);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-8 10:01:46<br>
     * @param blDeleteFromDB
     * @param superVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    final void saveVOs(boolean blDeleteFromDB, SuperVO... superVOs) throws BusinessException
    {
        if (superVOs == null)
        {
            return;
        }
        
        List<SuperVO> listInsertVOs = new ArrayList<SuperVO>();
        List<SuperVO> listUpdateVOs = new ArrayList<SuperVO>();
        List<SuperVO> listDeleteVOs = new ArrayList<SuperVO>();
        
        for (SuperVO superVO : superVOs)
        {
            switch (superVO.getStatus())
            {
                case VOStatus.NEW :
                {
                    superVO.setAttributeValue("dr", 0);
                    listInsertVOs.add(superVO);
                    break;
                }
                case VOStatus.UPDATED :
                {
                    superVO.setAttributeValue("dr", 0);
                    listUpdateVOs.add(superVO);
                    break;
                }
                case VOStatus.DELETED :
                {
                    if (blDeleteFromDB)
                    {
                        listDeleteVOs.add(superVO);
                    }
                    else
                    {
                        superVO.setAttributeValue("dr", 1);
                        listUpdateVOs.add(superVO);
                    }
                    break;
                }
            }
        }
        
        if (listDeleteVOs.size() > 0)
        {
            baseDAO.deleteVOArray(listDeleteVOs.toArray(new SuperVO[0]));
        }
        
        if (listUpdateVOs.size() > 0)
        {
            baseDAO.updateVOArray(listUpdateVOs.toArray(new SuperVO[0]));
        }
        
        if (listInsertVOs.size() > 0)
        {
            baseDAO.insertVOArray(listInsertVOs.toArray(new SuperVO[0]));
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-4 14:44:27<br>
     * @param docValidatorFactory
     * @author Rocex Wang
     ***************************************************************************/
    public void setValidatorFactory(IValidatorFactory docValidatorFactory)
    {
        serviceTemplate.setValidatorFactory(docValidatorFactory);
    }
    
    /***************************************************************************
     * 对信息集、信息项排序<br>
     * Created on 2010-8-13 9:56:46<br>
     * @param infoSetVOs
     * @author Rocex Wang
     ***************************************************************************/
    void sortInfoSetVO(InfoSetVO... infoSetVOs)
    {
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        SuperVOUtil.sortByAttributeName(infoSetVOs, InfoSetVO.SHOW_ORDER, true);
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            
            if (infoItemVOs != null)
            {
                SuperVOUtil.sortByAttributeName(infoItemVOs, InfoItemVO.SHOWORDER, true);
                
                infoSetVO.setInfo_item(infoItemVOs);
            }
        }
    }
    
    /***************************************************************************
     * 同步单据模板<br>
     * Created on 2009-12-24 9:28:51<br>
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    @Override
    public void syncBillTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSortVO == null || infoSortVO.getBill_type_code() == null || infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        checkInfoSet(infoSortVO.getPk_infoset_sort(), (InfoSetVO[]) null);
        
        syncInfoItemWithMd(infoSetVOs);
        
        String strBillTypeCodes[] = infoSortVO.getBill_type_code().split(";");
        
        if (strBillTypeCodes == null || strBillTypeCodes.length == 0)
        {
            return;
        }
        
        for (String strBillTypeCode : strBillTypeCodes)
        {
            syncSingleBillTemplet(blDispatchEvent, infoSortVO, strBillTypeCode, infoSetVOs);
        }
    }
    
    /***************************************************************************
     * 如果数据库中没有模板信息，就以信息项为蓝本创建一个模板字段信息，否则，以数据库中的数据为准<br>
     * Created on 2009-12-24 15:33:13<br>
     * @param bodyVO
     * @param infoSetVO
     * @param infoItemVO
     * @author Rocex Wang
     * @param iPos
     * @return BillTempletBodyVO
     ***************************************************************************/
    BillTempletBodyVO syncBillTempletByInfoItem(BillTempletBodyVO bodyVO, InfoSetVO infoSetVO, InfoItemVO infoItemVO, int iPos)
    {
        if (infoItemVO == null)
        {
            return bodyVO;
        }
        
        if (bodyVO == null)
        {
            bodyVO = new BillTempletBodyVO();
            bodyVO.setStatus(VOStatus.NEW);
            
            bodyVO.setItemkey(infoItemVO.getItem_code());
            // bodyVO.setDefaultshowname(infoItemVO.getItem_name());
            bodyVO.setDatatype(infoItemVO.getData_type());
            bodyVO.setEditflag(infoItemVO.getRead_only() == null ? true : !infoItemVO.getRead_only().booleanValue());
            bodyVO.setInputlength(infoItemVO.getMax_length());
            bodyVO.setShoworder(infoItemVO.getShoworder());
            bodyVO.setPos(bodyVO.getPos() == null ? iPos : bodyVO.getPos());
            bodyVO.setList(true);
            bodyVO.setListflag(true);
            bodyVO.setListshoworder(infoItemVO.getShoworder());
            bodyVO.setListshowflag(infoItemVO.getHided() == null ? true : !infoItemVO.getHided().booleanValue());
            bodyVO.setTableCode(bodyVO.getTableCode() == null ? infoSetVO.getInfoset_code() : bodyVO.getTableCode());
            bodyVO.setTableName(bodyVO.getTableName() == null ? infoSetVO.getInfoset_name() : bodyVO.getTableName());
            
            // 如果是小数或者金额，就设置数据精度
            if (infoItemVO.getPrecise() != null && ArrayUtils.contains(new int[]{IBillItem.DATETIME, IBillItem.MONEY}, infoItemVO.getData_type()))
            {
                bodyVO.setReftype(infoItemVO.getPrecise() + ",,");
            }
        }
        
        if (ObjectUtils.equals(IBillItem.UFREF, bodyVO.getDatatype()))
        {
            if (UFBoolean.TRUE.equals(infoItemVO.getRef_leaf_flag()))
            {
                if (bodyVO.getReftype() == null)
                {
                    try
                    {
                        bodyVO.setReftype(MDBaseQueryFacade.getInstance().getAttributeByFullName(bodyVO.getMetadataproperty()).getRefModelName());
                    }
                    catch (MetaDataException ex)
                    {
                        Logger.error(ex.getMessage(), ex);
                    }
                }
                
                if (bodyVO.getReftype() != null && !bodyVO.getReftype().contains(",nl=N"))
                {
                    bodyVO.setReftype(bodyVO.getReftype() + ",nl=N");
                }
            }
            else
            {
                if (bodyVO.getReftype() != null && bodyVO.getReftype().contains(",nl=N"))
                {
                    bodyVO.setReftype(bodyVO.getReftype().replace(",nl=N", ""));
                }
            }
        }
        
        return bodyVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-24 15:14:46<br>
     * @param fromDbBodyVO
     * @param fromMdBodyVO
     * @author Rocex Wang
     * @return BillTempletBodyVO
     ***************************************************************************/
    BillTempletBodyVO syncBillTempletByMd(BillTempletBodyVO fromDbBodyVO, BillTempletBodyVO fromMdBodyVO)
    {
        if (fromMdBodyVO == null)
        {
            return fromDbBodyVO;
        }
        
        if (fromDbBodyVO == null)
        {
            fromDbBodyVO = fromMdBodyVO;
            fromDbBodyVO.setStatus(VOStatus.NEW);
            
            return fromDbBodyVO;
        }
        
        // 把元数据中的信息同步到数据库中存在的字段上，先不同步，以数据库中的数据为准
        // fromDbBodyVO.setCardflag(fromMdBodyVO.getCardflag());
        // fromDbBodyVO.setDatatype(fromMdBodyVO.getDatatype());
        // fromDbBodyVO.setDefaultshowname(fromMdBodyVO.getDefaultshowname());
        // fromDbBodyVO.setDefaultvalue(fromMdBodyVO.getDefaultvalue());
        // fromDbBodyVO.setEditflag(fromMdBodyVO.getEditflag());
        // fromDbBodyVO.setForeground(fromMdBodyVO.getForeground());
        // fromDbBodyVO.setInputlength(fromMdBodyVO.getInputlength());
        // fromDbBodyVO.setItemkey(fromMdBodyVO.getItemkey());
        // fromDbBodyVO.setLeafflag(fromMdBodyVO.getLeafflag());
        // fromDbBodyVO.setListflag(fromMdBodyVO.getListflag());
        // fromDbBodyVO.setListShow(fromMdBodyVO.getListShow());
        // fromDbBodyVO.setListshowflag(fromMdBodyVO.getListshowflag());
        // fromDbBodyVO.setLockflag(fromMdBodyVO.getLockflag());
        // fromDbBodyVO.setMetadatapath(fromMdBodyVO.getMetadatapath());
        // fromDbBodyVO.setMetadataproperty(fromMdBodyVO.getMetadataproperty());
        // fromDbBodyVO.setNewlineflag(UFBoolean.FALSE);
        // fromDbBodyVO.setNullflag(fromMdBodyVO.getNullflag());
        // fromDbBodyVO.setPos(fromDbBodyVO.getPos() == null ? fromMdBodyVO.getPos() : fromDbBodyVO.getPos());
        // fromDbBodyVO.setReviseflag(fromMdBodyVO.getReviseflag());
        // fromDbBodyVO.setShowflag(fromMdBodyVO.getShowflag());
        // fromDbBodyVO.setShoworder(fromMdBodyVO.getShoworder());
        // fromDbBodyVO.setTableCode(fromDbBodyVO.getTableCode() == null ? fromMdBodyVO.getTableCode() :
        // fromDbBodyVO.getTableCode());
        // fromDbBodyVO.setTableName(fromDbBodyVO.getTableName() == null ? fromMdBodyVO.getTableName() :
        // fromDbBodyVO.getTableName());
        // fromDbBodyVO.setTotalflag(fromMdBodyVO.getTotalflag());
        // fromDbBodyVO.setUsereditflag(fromMdBodyVO.getUsereditflag());
        // fromDbBodyVO.setUserflag(fromMdBodyVO.getUserflag());
        // fromDbBodyVO.setUsershowflag(fromMdBodyVO.getUsershowflag());
        // fromDbBodyVO.setWidth(fromMdBodyVO.getWidth());
        // fromDbBodyVO.setStatus(VOStatus.UPDATED);
        
        return fromDbBodyVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-6 15:15:03<br>
     * @param infoSetVOs
     * @throws MetaDataException
     * @author Rocex Wang
     ***************************************************************************/
    void syncInfoItemWithMd(InfoSetVO... infoSetVOs) throws MetaDataException
    {
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO == null)
            {
                continue;
            }
            
            String strMetaData = infoSetVO.getMeta_data_id();
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            
            if (strMetaData == null || strMetaData.length() == 0 || infoItemVOs == null || infoItemVOs.length == 0)
            {
                continue;
            }
            
            Logger.error("syncInfoItemWithMd-->Infoset_code->" + infoSetVO.getInfoset_code() + ",Infoset_name->" + infoSetVO.getInfoset_name() + ",MetaData->"
                + strMetaData);
            
            // 有可能元数据不完整，所以先用这个方法查一下
            IType type = MDBaseQueryFacade.getInstance().getTypeByID(strMetaData, IType.STYLE_SINGLE);
            
            if (type == null)
            {
                continue;
            }
            
            IBean bean = MDBaseQueryFacade.getInstance().getBeanByID(strMetaData);
            
            for (InfoItemVO infoItemVO : infoItemVOs)
            {
                infoItemVO.setAttribute(bean.getAttributeByName(infoItemVO.getItem_code()));
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-22 15:43:51<br>
     * @see nc.itf.hr.infoset.IInfoSet#syncMetaData(boolean, boolean,InfoSortVO, nc.vo.hr.infoset.InfoSetVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO[] syncMetaData(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs)
            throws BusinessException
    {
        if (infoSetVOs == null || infoSetVOs.length == 0)
        {
            return null;
        }
        
        Logger.error("\n*******************************有人同步了元数据*********************************************\n* 时间：" + PubEnv.getServerTime() + "\n* 用户："
            + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
            + "\n*******************************************************************************************");
        
        Logger.error("后台开始同步元数据！");
        
        checkInfoSet(infoSortVO.getPk_infoset_sort(), (InfoSetVO[]) null);
        
        InfoSetVO mainInfoSetVO = null;
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO.getMain_table_flag() != null && infoSetVO.getMain_table_flag().booleanValue())
            {
                mainInfoSetVO = infoSetVO;
                break;
            }
        }
        
        if (mainInfoSetVO == null)
        {
            throw new BusinessException(ResHelper.getString("6001infset", "06001infset0076")/* @res "没有主实体！" */);
        }
        
        IBean mainBean = MDBaseQueryFacade.getInstance().getBeanByID(mainInfoSetVO.getMeta_data_id());
        
        if (mainBean == null)
        {
            throw new BusinessException(ResHelper.getString("6001infset", "06001infset0077")
            /* @res "根据beanId没有找到bean！beanId：{0}" */, mainInfoSetVO.getMeta_data_id());
        }
        
        IMDVOManegerService mdManegerService = NCLocator.getInstance().lookup(IMDVOManegerService.class);
        
        IMDPublishModel metaData = mdManegerService.queryMetadataVOByCompID(mainBean.getOwnerComponent().getID());
        
        if (metaData == null)
        {
            throw new BusinessException(ResHelper.getString("6001infset", "06001infset0078")/* @res "没有找到组件！" */);
        }
        
        Logger.error("后台开始通过信息集生成元数据！");
        
        ClassVO mainClassVO = metaData.getMainClassVO();
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO.getInfo_item() == null || infoSetVO.getInfo_item().length == 0)
            {
                throw new BusinessException(ResHelper.getString("6001infset", "06001infset0079", infoSetVO.getInfoset_name())
                /* @ res "信息集“{0}”缺少属性字段！" */);
            }
            
            Logger.error("后台开始通过信息集生成元数据！" + infoSetVO.getInfoset_code());
            
            int iShowOrder = 0;
            
            ClassVO classVO = getMdClassVO(mainBean, metaData, infoSetVO);
            
            for (InfoItemVO infoItemVO : infoSetVO.getInfo_item())
            {
                boolean blFindProperty = metaData.getPropertyVOByName(classVO.getName(), infoItemVO.getItem_code()) != null;
                
                PropertyVO propertyVO = getMdPropertyVO(metaData, classVO, infoSetVO, infoItemVO);
                
                ColumnVO columnVO = getMdColumnVO(metaData, infoSetVO, infoItemVO, propertyVO);
                
                if (columnVO != null)
                {
                    
                    if (blFindProperty)
                    {
                        metaData.delPropertyVOByID(propertyVO.getId());
                    }
                    
                    metaData.addpropertyVO(propertyVO, columnVO);
                    
                    // 对于参照，还需要增加关联关系
                    if (IBillItem.UFREF == infoItemVO.getData_type())
                    {
                        metaData.addRelationAssociationForEntity(propertyVO.getClassID(), propertyVO.getId(), propertyVO.getDataType(), true, false);
                    }
                    
                    if (infoItemVO.getShoworder() > iShowOrder)
                    {
                        iShowOrder = infoItemVO.getShoworder();
                    }
                    
                    infoItemVO.setMeta_data(infoSetVO.getMeta_data() + "." + infoItemVO.getItem_code());
                    infoItemVO.setStatus(VOStatus.UPDATED);
                }
            }
            
            PropertyVO keyPropertyVO = metaData.getPropertyVOByName(classVO.getName(), infoSetVO.getPk_field_code());
            
            if (keyPropertyVO == null)
            {
                String strMsg = ResHelper.getString("6001infset", "06001infset0080", infoSetVO.getInfoset_name())
                /* @res "实体 {0} 没有主键字段！" */;
                
                throw new BusinessException(strMsg);
            }
            
            classVO.setKeyAttribute(keyPropertyVO.getId());
            
            // 如果不是主实体，就需要创建主实体和子实体之间的组合关系，组合字段用子实体的页签名
            getMdCompAssociationForEntity(metaData, infoSetVO, mainClassVO, classVO, iShowOrder);
            
            Logger.error("后台结束通过信息集生成元数据！" + infoSetVO.getInfoset_code());
            
            // 清空对该表的缓存
            JdbcPersistenceManager.clearColumnTypes(infoSetVO.getTable_code());
        }
        
        Logger.error("后台生成内存中元数据完成！");
        
        IHookPrivate hookPrivate = DefaultHookPrivate.getInstance(infoSortVO);
        
        // 前事件通知
        if (blDispatchEvent)
        {
            BusinessEvent beforeEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_METADATA_BEFORE, metaData);
            EventDispatcher.fireEvent(beforeEvent);
            
            if (hookPrivate != null)
            {
                metaData = hookPrivate.syncMetadataBefore(metaData);
            }
        }
        
        Logger.error("发送前事件完成！");
        
        mdManegerService.insertMetadataVO(metaData);
        
        Logger.error("同步元数据完成！");
        
        updateInfoSet(false, blChangeAuditInfo, infoSetVOs);
        
        infoSetVOs = queryInfoSetBySortPk(infoSortVO.getPk_infoset_sort());
        
        adjustDB(hookPrivate, infoSetVOs);
        
        Logger.error("调整数据库完成");
        
        syncVO(infoSortVO, infoSetVOs);
        
        Logger.error("调整VO类完成");
        
        syncRes(metaData, infoSortVO, infoSetVOs);
        
        // 清理SuperVO中对元数据的缓存
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            VOMetaFactory.getInstance().clear(infoSetVO.getMeta_data());
        }
        
        InfoSetHelper.clearInfoSetCache();
        
        Logger.error("清理后台元数据缓存完成！");
        
        // 后事件通知
        if (blDispatchEvent)
        {
            BusinessEvent afterEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_METADATA_AFTER, infoSetVOs);
            EventDispatcher.fireEvent(afterEvent);
            
            if (hookPrivate != null)
            {
                metaData = hookPrivate.syncMetadataAfter(metaData);
            }
        }
        
        Logger.error("发送后事件完成！");
        
        return infoSetVOs;
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2013-5-29 10:13:24<br>
     * @see nc.itf.hr.infoset.IInfoSet#syncMetaDataBySort(boolean, boolean, java.lang.String)
     * @author Rocex Wang
     **************************************************************/
    @Override
    public InfoSetVO[] syncMetaDataBySort(boolean blDispatchEvent, boolean blChangeAuditInfo, String strPk_sort) throws BusinessException
    {
        InfoSortVO infoSortVOs[] = queryInfoSortByPks(strPk_sort);
        InfoSetVO infoSetVOs[] = queryInfoSetBySortPk(strPk_sort);
        
        return syncMetaData(blDispatchEvent, blChangeAuditInfo, infoSortVOs == null || infoSortVOs.length == 0 ? null : infoSortVOs[0], infoSetVOs);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-6 15:09:41<br>
     * @param printItemVOFromDB
     * @param printItemVOFromIS
     * @return PrintTempletmanageItemVO
     * @author Rocex Wang
     ***************************************************************************/
    PrintTempletmanageItemVO syncPrintItemByIS(PrintTempletmanageItemVO printItemVOFromDB, PrintTempletmanageItemVO printItemVOFromIS)
    {
        if (printItemVOFromIS == null)
        {
            return printItemVOFromDB;
        }
        
        if (printItemVOFromDB == null)
        {
            printItemVOFromIS.setStatus(VOStatus.NEW);
            
            return printItemVOFromIS;
        }
        
        printItemVOFromDB.setStatus(VOStatus.UPDATED);
        
        printItemVOFromDB.setItype(printItemVOFromIS.getItype());
        printItemVOFromDB.setPk_corp(printItemVOFromIS.getPk_corp());
        printItemVOFromDB.setResid(printItemVOFromIS.getResid());
        printItemVOFromDB.setUserdefflag(printItemVOFromIS.getUserdefflag());
        printItemVOFromDB.setVnodecode(printItemVOFromIS.getVnodecode());
        printItemVOFromDB.setVtablecode(printItemVOFromIS.getVtablecode());
        printItemVOFromDB.setVvarexpress(printItemVOFromIS.getVvarexpress());
        printItemVOFromDB.setVvarname(printItemVOFromIS.getVvarname());
        
        return printItemVOFromDB;
    }
    
    /***************************************************************************
     * 同步打印模板<br>
     * Created on 2009-12-24 9:28:45<br>
     * @param infoSetVOs
     * @param infoSortVO
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    @Override
    public void syncPrintTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSortVO == null || infoSortVO.getFunc_code() == null || infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        checkInfoSet(infoSortVO.getPk_infoset_sort(), (InfoSetVO[]) null);
        
        syncInfoItemWithMd(infoSetVOs);
        
        String strFuncCodes[] = infoSortVO.getFunc_code().split(";");
        
        if (strFuncCodes == null || strFuncCodes.length == 0)
        {
            return;
        }
        
        for (String strFuncCode : strFuncCodes)
        {
            syncSinglePrintTemplet(blDispatchEvent, infoSortVO, strFuncCode, infoSetVOs);
        }
    }
    
    /***************************************************************************
     * 同步查询模板，只增加到待查项里面<br>
     * Created on 2009-12-24 9:28:29<br>
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @param infoSortVO
     * @throws BusinessException
     ***************************************************************************/
    @Override
    public void syncQueryTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSortVO == null || infoSortVO.getFunc_code() == null || infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        checkInfoSet(infoSortVO.getPk_infoset_sort(), (InfoSetVO[]) null);
        
        syncInfoItemWithMd(infoSetVOs);
        
        // 对信息集、信息项排序
        sortInfoSetVO(infoSetVOs);
        
        String strMainMetaId = null;
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO.getMain_table_flag().booleanValue())
            {
                strMainMetaId = infoSetVO.getMeta_data_id();
                
                break;
            }
        }
        
        adjustQueryTemplet(strMainMetaId);
        
        String strSQL = MessageFormat.format(" pk_corp=''{0}'' and metaclass=''{1}''", strDefaultPkCorp, strMainMetaId);
        
        QueryTempletTotalVO queryTempletTotalVOFromDBs[] = NCLocator.getInstance().lookup(IQueryTemplate.class).queryQueryTempletTotalVOByWherePart(strSQL);
        
        if (queryTempletTotalVOFromDBs == null || queryTempletTotalVOFromDBs.length == 0)
        {
            return;
        }
        
        for (QueryTempletTotalVO queryTempletTotalVO : queryTempletTotalVOFromDBs)
        {
            syncSingleQueryTemplet(blDispatchEvent, queryTempletTotalVO, infoSortVO, infoSetVOs);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-5 10:06:22<br>
     * @param qryConditionVO
     * @param bean
     * @param infoItemVO
     * @param blMainTable
     * @return QueryConditionVO
     * @author Rocex Wang
     ***************************************************************************/
    QueryConditionVO syncQueryTempletByInfoItem(QueryConditionVO qryConditionVO, IBean bean, InfoItemVO infoItemVO, boolean blMainTable)
    {
        if (infoItemVO == null || infoItemVO.getHided().booleanValue())
        {
            return qryConditionVO;
        }
        
        if (qryConditionVO == null)
        {
            qryConditionVO = new QueryConditionVO();
            qryConditionVO.setStatus(VOStatus.NEW);
            
            qryConditionVO.setFieldCode(blMainTable ? infoItemVO.getItem_code() : bean.getName() + "." + infoItemVO.getItem_code());
            qryConditionVO.setFieldName(blMainTable ? infoItemVO.getItem_name() : bean.getDisplayName() + "." + infoItemVO.getItem_name());
            // qryConditionVO.setDataType(infoItemVO.getData_type());
            qryConditionVO.setMaxLength(infoItemVO.getMax_length());
            qryConditionVO.setPkCorp(strDefaultPkCorp);
            
            // 如果是小数或者金额，就设置数据精度
            if (infoItemVO.getPrecise() != null && ArrayUtils.contains(new int[]{IBillItem.DECIMAL, IBillItem.MONEY}, infoItemVO.getData_type()))
            {
                qryConditionVO.setConsultCode(infoItemVO.getMax_length() + "," + infoItemVO.getPrecise());
            }
        }
        
        return qryConditionVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-5 10:06:19<br>
     * @param fromDbQryConditionVO
     * @param fromMdQryConditionVO
     * @return QueryConditionVO
     * @author Rocex Wang
     ***************************************************************************/
    QueryConditionVO syncQueryTempletByMd(QueryConditionVO fromDbQryConditionVO, QueryConditionVO fromMdQryConditionVO)
    {
        if (fromMdQryConditionVO == null)
        {
            return fromDbQryConditionVO;
        }
        
        if (fromDbQryConditionVO == null)
        {
            fromDbQryConditionVO = fromMdQryConditionVO;
            fromDbQryConditionVO.setStatus(VOStatus.NEW);
            
            return fromDbQryConditionVO;
        }
        
        // fromDbQryConditionVO.setComboItems(fromMdQryConditionVO.getComboItems());
        // fromDbQryConditionVO.setComboType(fromMdQryConditionVO.getComboType());
        // fromDbQryConditionVO.setConsultCode(fromMdQryConditionVO.getConsultCode());
        // fromDbQryConditionVO.setDataType(fromMdQryConditionVO.getDataType());
        // fromDbQryConditionVO.setDispSequence(fromMdQryConditionVO.getDispSequence());
        // fromDbQryConditionVO.setDispType(fromMdQryConditionVO.getDispType());
        // fromDbQryConditionVO.setDispValue(fromMdQryConditionVO.getDispValue());
        // fromDbQryConditionVO.setEnums(fromMdQryConditionVO.getEnums());
        // fromDbQryConditionVO.setFieldCode(fromMdQryConditionVO.getFieldCode());
        // fromDbQryConditionVO.setFieldName(fromMdQryConditionVO.getFieldName());
        // fromDbQryConditionVO.setFromMetaData(fromMdQryConditionVO.isFromMetaData());
        // fromDbQryConditionVO.setGuideline(fromMdQryConditionVO.getGuideline());
        // fromDbQryConditionVO.setIfAutoCheck(fromMdQryConditionVO.getIfAutoCheck());
        // fromDbQryConditionVO.setIfDataPower(fromMdQryConditionVO.getIfDataPower());
        //
        // if (fromDbQryConditionVO.getIfDefault() == null)
        // {
        // fromDbQryConditionVO.setIfDefault(fromMdQryConditionVO.getIfDefault());
        // }
        //
        // fromDbQryConditionVO.setIfDesc(fromMdQryConditionVO.getIfDesc());
        // fromDbQryConditionVO.setIfGroup(fromMdQryConditionVO.getIfGroup());
        // fromDbQryConditionVO.setIfImmobility(fromMdQryConditionVO.getIfImmobility());
        // fromDbQryConditionVO.setIfMust(fromMdQryConditionVO.getIfMust());
        // fromDbQryConditionVO.setIfOrder(fromMdQryConditionVO.getIfOrder());
        // fromDbQryConditionVO.setIfSum(fromMdQryConditionVO.getIfSum());
        // fromDbQryConditionVO.setIfUsed(fromMdQryConditionVO.getIfUsed());
        // fromDbQryConditionVO.setInstrumentedsql(fromMdQryConditionVO.getInstrumentedsql());
        // fromDbQryConditionVO.setIsAttrRefUsed(fromMdQryConditionVO.getIsAttrRefUsed());
        // fromDbQryConditionVO.setIsCondition(fromMdQryConditionVO.getIsCondition());
        // fromDbQryConditionVO.setIsSubIncluded(fromMdQryConditionVO.getIsSubIncluded());
        // fromDbQryConditionVO.setIsSysFuncRefUsed(fromMdQryConditionVO.getIsSysFuncRefUsed());
        // fromDbQryConditionVO.setIsUserDef(fromMdQryConditionVO.getIsUserDef());
        // fromDbQryConditionVO.setMaxLength(fromMdQryConditionVO.getMaxLength());
        // // fromDbQryConditionVO.setNewMaxLength(fromMdQryConditionVO.);
        // fromDbQryConditionVO.setNodecode(fromMdQryConditionVO.getNodecode());
        // fromDbQryConditionVO.setOperaCode(fromMdQryConditionVO.getOperaCode());
        // fromDbQryConditionVO.setOperaName(fromMdQryConditionVO.getOperaName());
        // fromDbQryConditionVO.setOrderSequence(fromMdQryConditionVO.getOrderSequence());
        // fromDbQryConditionVO.setPkCorp(fromMdQryConditionVO.getPkCorp());
        // fromDbQryConditionVO.setPrerestrict(fromMdQryConditionVO.getPrerestrict());
        // fromDbQryConditionVO.setResid(fromMdQryConditionVO.getResid());
        // fromDbQryConditionVO.setReturnType(fromMdQryConditionVO.getReturnType());
        // fromDbQryConditionVO.setTableCode(fromMdQryConditionVO.getTableCode());
        // fromDbQryConditionVO.setTableName(fromMdQryConditionVO.getTableName());
        // fromDbQryConditionVO.setValue(fromMdQryConditionVO.getValue());
        // fromDbQryConditionVO.setWhere(fromMdQryConditionVO.getWhere());
        //
        // fromDbQryConditionVO.setStatus(VOStatus.UPDATED);
        
        return fromDbQryConditionVO;
    }
    
    /**************************************************************
     * 更新资源文件 思路：比较现有的资源和VO中的名称是否一致，如果不一致，则以VO中的名称重新生成资源以补丁的形式放到 nchome\resources\lang\ 目录下<br>
     * Created on 2013-10-23 14:29:23<br>
     * @param metaData
     * @param infoSortVO
     * @param infoSetVOs
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    void syncRes(IMDPublishModel metaData, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        String strResPath = metaData.getComponentVO().getResmodule();
        
        LanguageVO enableLangVOs[] = MultiLangContext.getInstance().getEnableLangVOs();
        
        if (enableLangVOs == null || enableLangVOs.length == 0)
        {
            return;
        }
        
        // 收集每个语种下新增加的多语资源，然后合并到文件系统中的资源文件中
        for (LanguageVO languageVO : enableLangVOs)
        {
            Properties properties = new SortedProperties();
            
            String iLangIndex = MultiLangHelper.getLangIndex(languageVO);
            
            for (InfoSetVO infoSetVO : infoSetVOs)
            {
                if (StringUtils.isNotEmpty(infoSetVO.getResid()) && UFBoolean.TRUE.equals(infoSetVO.getUser_def_flag()))
                {
                    String strRes = NCLangResOnserver.getInstance().getString(languageVO.getLangcode(), strResPath, null, infoSetVO.getResid());
                    
                    String strInfoSetName = (String) infoSetVO.getAttributeValue("infoset_name" + iLangIndex);
                    
                    if (!ObjectUtils.equals(strRes, strInfoSetName))
                    {
                        properties.put(infoSetVO.getResid(), strInfoSetName);
                    }
                }
                
                for (InfoItemVO infoItemVO : infoSetVO.getInfo_item())
                {
                    if (StringUtils.isNotEmpty(infoItemVO.getResid()) && UFBoolean.TRUE.equals(infoItemVO.getCustom_attr()))
                    {
                        String strRes = NCLangResOnserver.getInstance().getString(languageVO.getLangcode(), strResPath, null, infoItemVO.getResid());
                        
                        String strItemName = (String) infoItemVO.getAttributeValue("item_name" + iLangIndex);
                        
                        if (!ObjectUtils.equals(strRes, strItemName))
                        {
                            properties.put(infoItemVO.getResid(), strItemName);
                        }
                    }
                }
            }
            
            writeProperties(languageVO, strResPath, properties);
        }
    }
    
    /***************************************************************************
     * 同步单个单据模板<br>
     * e Created on 2010-6-8 15:50:28<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @param strBillTypeCode
     * @param infoSetVOs
     * @throws BusinessException
     * @throws MetaDataException
     * @author Rocex Wang
     ***************************************************************************/
    void syncSingleBillTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, String strBillTypeCode, InfoSetVO... infoSetVOs) throws BusinessException,
            MetaDataException
    {
        List<BillTempletBodyVO> listBodyVO = new ArrayList<BillTempletBodyVO>();
        
        Map<String, BillTempletBodyVO> mapBodyVOInDb = new HashMap<String, BillTempletBodyVO>();
        Map<String, BillTempletBodyVO> mapBodyVOInMd = new HashMap<String, BillTempletBodyVO>();
        
        Map<String, BillTabVO> mapTabVO = new HashMap<String, BillTabVO>();
        
        // 读出数据库中已有的单据模板
        BillTempletVO fromDbBillTempletVO =
            NCLocator.getInstance().lookup(IBillTemplateQry.class).findDefaultCardTempletData(strBillTypeCode, null, null, null);
        
        SuperVOUtil.sortByAttributeName(infoSetVOs, InfoSetVO.SHOW_ORDER, true);
        
        IHookPrivate hookPrivate = DefaultHookPrivate.getInstance(infoSortVO);
        
        if (hookPrivate != null)
        {
            fromDbBillTempletVO = hookPrivate.prepareBillTemplate(fromDbBillTempletVO, infoSortVO, infoSetVOs);
        }
        
        // 把为null的信息集剔除掉
        while (ArrayUtils.contains(infoSetVOs, null))
        {
            infoSetVOs = (InfoSetVO[]) ArrayUtils.removeElement(infoSetVOs, null);
        }
        
        // 数据库中是否已经存在模板，如果存在，后面就是更新操作，如果不存在，后面就是新增操作
        boolean blExistInDb = fromDbBillTempletVO != null;
        
        putToMap(mapTabVO, mapBodyVOInDb, fromDbBillTempletVO);
        
        IBean mainBean = null;
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            SuperVOUtil.sortByAttributeName(infoSetVO.getInfo_item(), InfoItemVO.SHOWORDER, true);
            
            // 通过元数据生成单据模板
            IBean bean = MDBaseQueryFacade.getInstance().getBeanByID(infoSetVO.getMeta_data_id());
            
            int iPos = IBillItem.BODY;
            
            if (infoSetVO.getMain_table_flag().booleanValue())
            {
                iPos = IBillItem.HEAD;
                
                mainBean = bean;
            }
            
            BillTempletVO fromMdBillTempletVO = createBillTempletVO(bean, infoSortVO, infoSetVO, iPos);
            fromMdBillTempletVO.getHeadVO().setPkBillTypeCode(strBillTypeCode);
            
            putToMap(mapTabVO, mapBodyVOInMd, fromMdBillTempletVO);
            
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            
            // 如果信息集中已经没有信息项了，就从单据模板中删除该信息集已经所包含的信息项
            if (infoItemVOs == null || infoItemVOs.length == 0)
            {
                for (Iterator<String> iterator = mapBodyVOInDb.keySet().iterator(); iterator.hasNext();)
                {
                    String strMapKey = iterator.next();
                    
                    if (strMapKey.contains(iPos + "." + infoSetVO.getInfoset_code() + "."))
                    {
                        BillTempletBodyVO fromDbBodyVO = mapBodyVOInDb.get(strMapKey);
                        fromDbBodyVO.setStatus(VOStatus.DELETED);
                        
                        addToBodyTemplet(listBodyVO, fromDbBodyVO);
                    }
                }
                
                continue;
            }
            
            // 以数据库中已有的信息为基础，把元数据、信息项的信息同步到单据模板字段上
            for (InfoItemVO infoItemVO : infoItemVOs)
            {
                String strBillItemKey = getBillItemKey(iPos, bean.getName(), infoItemVO.getItem_code());
                
                BillTempletBodyVO bodyVOInDb = mapBodyVOInDb.get(strBillItemKey);
                BillTempletBodyVO bodyVOInMd = mapBodyVOInMd.get(strBillItemKey);
                
                // BillTempletBodyVO fromDbBodyVO2 = null;
                // if (bodyVOInDb != null)
                // {
                // fromDbBodyVO2 = (BillTempletBodyVO) bodyVOInDb.clone();
                //
                // bodyVOInDb.setShoworder(infoItemVO.getShoworder());
                // }
                
                BillTempletBodyVO newBodyVO = null;
                
                // 规则：如果数据库中已有数据，则不用元数据和信息集同步
                if (bodyVOInDb == null)
                {
                    newBodyVO = syncBillTempletByMd(bodyVOInDb, bodyVOInMd);
                    newBodyVO = syncBillTempletByInfoItem(newBodyVO, infoSetVO, infoItemVO, iPos);
                }
                
                if (hookPrivate != null)
                {
                    newBodyVO = hookPrivate.whichToDB(bodyVOInDb, newBodyVO, infoSetVO, infoItemVO);
                }
                
                if (newBodyVO != null)
                {
                    if (VOStatus.NEW == newBodyVO.getShoworder())
                    {
                        newBodyVO.setShoworder(infoItemVO.getShoworder());
                        newBodyVO.setListshoworder(infoItemVO.getShoworder());
                    }
                    
                    addToBodyTemplet(listBodyVO, newBodyVO);
                }
            }
            
            // 把 TS 加进来
            addTs(bean, iPos, mapBodyVOInDb, mapBodyVOInMd, listBodyVO);
        }
        
        // 把数据库和元数据中剩下的也增加进来
        addRemainderBodyTemplet(mapBodyVOInDb, listBodyVO);
        addRemainderBodyTemplet(mapBodyVOInMd, listBodyVO);
        
        // 特殊处理一下表体上的参照，因为表体上的参数默认显示的是编码，而不是名称，这里需要处理成显示名称
        for (BillTempletBodyVO bodyVO : listBodyVO)
        {
            if (bodyVO.getPos() != null && IBillItem.BODY == bodyVO.getPos() && bodyVO.getDatatype() != null && IBillItem.UFREF == bodyVO.getDatatype())
            {
                if (bodyVO.getReftype() == null)
                {
                    bodyVO.setReftype(MDBaseQueryFacade.getInstance().getAttributeByFullName(bodyVO.getMetadataproperty()).getRefModelName() + ",code=N");
                    
                    continue;
                }
                
                if (!bodyVO.getReftype().contains("code=N"))
                {
                    bodyVO.setReftype(bodyVO.getReftype() + ",code=N");
                }
            }
        }
        
        BillTempletHeadVO headVO = null;
        
        if (fromDbBillTempletVO != null)
        {
            headVO = fromDbBillTempletVO.getHeadVO();
        }
        else
        {
            fromDbBillTempletVO = new BillTempletVO();
            
            headVO = createBillTempletHeadVO(infoSortVO);
            
            headVO.setPkBillTypeCode(strBillTypeCode);
            headVO.setMetadataclass(mainBean != null ? mainBean.getFullName() : null);
            headVO.setBillType(mainBean != null ? mainBean.getName() : strBillTypeCode);
            headVO.setBillTempletCaption(mainBean != null ? mainBean.getDisplayName() : infoSortVO.getSort_name());
        }
        
        BillStructVO billStructVO = new BillStructVO();
        billStructVO.setBillTabVOs(mapTabVO.values().toArray(new BillTabVO[0]));
        
        headVO.setStructvo(billStructVO);
        
        fromDbBillTempletVO.setParentVO(headVO);
        fromDbBillTempletVO.setChildrenVO(listBodyVO.toArray(new BillTempletBodyVO[0]));
        
        // 前事件通知
        InfoSetEventObject infoSetEventObject = new InfoSetEventObject();
        infoSetEventObject.setBillTempletVO(fromDbBillTempletVO);
        infoSetEventObject.setInfoSortVO(infoSortVO);
        infoSetEventObject.setInfoSetVOs(infoSetVOs);
        
        if (blDispatchEvent)
        {
            EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_BILL_TEMPLATE_BEFORE, infoSetEventObject));
        }
        
        if (hookPrivate != null)
        {
            fromDbBillTempletVO = hookPrivate.syncBillTemplateBefore(infoSetEventObject);
        }
        
        // 升级系统模板
        IBillTemplateBase billTemplate = NCLocator.getInstance().lookup(IBillTemplateBase.class);
        
        if (blExistInDb)
        {
            billTemplate.overWriteBillTempletVO(fromDbBillTempletVO);
        }
        else
        {
            billTemplate.insertEx(fromDbBillTempletVO);
        }
        
        // 升级用户自定义模板
        NCLocator.getInstance().lookup(IBillTemplateUpgrade.class).ajustCustomBillTempletData(strBillTypeCode, strBillTypeCode);
        
        // 后事件通知
        infoSetEventObject = new InfoSetEventObject();
        infoSetEventObject.setBillTempletVO(fromDbBillTempletVO);
        infoSetEventObject.setInfoSortVO(infoSortVO);
        infoSetEventObject.setInfoSetVOs(infoSetVOs);
        
        if (blDispatchEvent)
        {
            EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_BILL_TEMPLATE_AFTER, infoSetEventObject));
        }
        
        if (hookPrivate != null)
        {
            fromDbBillTempletVO = hookPrivate.syncBillTemplateAfter(infoSetEventObject);
        }
    }
    
    /***************************************************************************
     * 同步单个打印模板<br>
     * Created on 2010-6-8 16:15:50<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @param strFuncCode
     * @param infoSetVOs
     * @throws BusinessException
     * @throws MetaDataException
     * @author Rocex Wang
     ***************************************************************************/
    void syncSinglePrintTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, String strFuncCode, InfoSetVO... infoSetVOs) throws BusinessException,
            MetaDataException
    {
        List<PrintTempletmanageItemVO> listPrintItemVO = new ArrayList<PrintTempletmanageItemVO>();
        
        PrintTempletmanageItemVO printItemVOs[] = NCLocator.getInstance().lookup(IPrintTemplateQry.class).findItemsForHeader(strFuncCode, strDefaultPkCorp);
        
        Map<String, PrintTempletmanageItemVO> mapPrintItemFromDB = new HashMap<String, PrintTempletmanageItemVO>();
        Map<String, PrintTempletmanageItemVO> mapPrintItemFromIS = new HashMap<String, PrintTempletmanageItemVO>();
        
        if (printItemVOs != null)
        {
            putToMap(mapPrintItemFromDB, printItemVOs);
        }
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            // 通过元数据生成打印信息项
            IBean bean = MDBaseQueryFacade.getInstance().getBeanByID(infoSetVO.getMeta_data_id());
            
            boolean blMainTable = infoSetVO.getMain_table_flag().booleanValue();
            
            // 默认是表体，只有一个表体的时候不要前缀，先不考虑表尾的情况
            String strPrefix = blMainTable ? "h_" : infoSetVOs.length > 2 ? bean.getName() + "__" : "";
            
            PrintTempletmanageItemVO fromMdPrintItemVOs[] = createPrintItemVO(infoSetVO, strPrefix, strFuncCode);
            
            putToMap(mapPrintItemFromIS, fromMdPrintItemVOs);
            
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            
            // 如果信息集中已经没有信息项了，就从打印信息项中删除该信息集已经所包含的信息项
            if (infoItemVOs == null || infoItemVOs.length == 0)
            {
                for (Iterator<String> iterator = mapPrintItemFromDB.keySet().iterator(); iterator.hasNext();)
                {
                    String strMapKey = iterator.next();
                    
                    if (strMapKey.contains(strPrefix))
                    {
                        PrintTempletmanageItemVO fromDbPrintItemVO = mapPrintItemFromDB.get(strMapKey);
                        fromDbPrintItemVO.setStatus(VOStatus.DELETED);
                        
                        addToPrintItemVO(listPrintItemVO, fromDbPrintItemVO);
                    }
                }
                
                continue;
            }
            
            // 以数据库中已有的信息为基础，把元数据、信息项的信息同步到打印信息项上
            for (InfoItemVO infoItemVO : infoItemVOs)
            {
                String strMapKey = strPrefix + infoItemVO.getItem_code();
                
                PrintTempletmanageItemVO printItemVOFromDB = mapPrintItemFromDB.get(strMapKey);
                PrintTempletmanageItemVO printItemVOFromIS = mapPrintItemFromIS.get(strMapKey);
                
                printItemVOFromDB = syncPrintItemByIS(printItemVOFromDB, printItemVOFromIS);
                
                addToPrintItemVO(listPrintItemVO, printItemVOFromDB);
            }
        }
        
        printItemVOs = listPrintItemVO.toArray(new PrintTempletmanageItemVO[0]);
        
        IHookPrivate hookPrivate = DefaultHookPrivate.getInstance(infoSortVO);
        
        // 前事件通知
        if (blDispatchEvent)
        {
            BusinessEvent beforeEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_PRINT_TEMPLATE_BEFORE, printItemVOs);
            EventDispatcher.fireEvent(beforeEvent);
            
            if (hookPrivate != null)
            {
                printItemVOs = hookPrivate.syncPrintTemplateBefore(printItemVOs);
            }
        }
        
        saveVOs(true, printItemVOs);
        
        // 后事件通知
        if (blDispatchEvent)
        {
            BusinessEvent afterEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_PRINT_TEMPLATE_AFTER, printItemVOs);
            EventDispatcher.fireEvent(afterEvent);
            
            if (hookPrivate != null)
            {
                printItemVOs = hookPrivate.syncPrintTemplateAfter(printItemVOs);
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 16:19:21<br>
     * @param blDispatchEvent
     * @param queryTempletTotalVOFromDB
     * @param infoSortVO
     * @param infoSetVOs
     * @throws BusinessException
     * @throws MetaDataException
     * @author Rocex Wang
     ***************************************************************************/
    void syncSingleQueryTemplet(boolean blDispatchEvent, QueryTempletTotalVO queryTempletTotalVOFromDB, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs)
            throws BusinessException, MetaDataException
    {
        Map<String, QueryConditionVO> mapQryConditionFromDb = new HashMap<String, QueryConditionVO>();
        Map<String, QueryConditionVO> mapQryConditionFromMd = new HashMap<String, QueryConditionVO>();
        
        if (queryTempletTotalVOFromDB != null)
        {
            putToMap(mapQryConditionFromDb, queryTempletTotalVOFromDB.getConditionVOs());
        }
        
        // 数据库中是否已经存在模板，如果存在，后面就是更新操作，如果不存在，后面就是新增操作
        boolean blExistInDb = queryTempletTotalVOFromDB != null;
        
        IBean mainBean = null;
        
        int iShowOrder = 0;
        
        List<QueryConditionVO> listQryConditionVO = new ArrayList<QueryConditionVO>();
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            // 通过元数据生成查询模板
            IBean bean = MDBaseQueryFacade.getInstance().getBeanByID(infoSetVO.getMeta_data_id());
            
            boolean blMainTable = infoSetVO.getMain_table_flag().booleanValue();
            
            if (blMainTable)
            {
                mainBean = bean;
            }
            
            QueryConditionVO fromMdBillTempletVOs[] = createQueryConditionVO(bean, blMainTable);
            
            putToMap(mapQryConditionFromMd, fromMdBillTempletVOs);
            
            InfoItemVO infoItemVOs[] = infoSetVO.getInfo_item();
            
            // 如果信息集中已经没有信息项了，就从查询模板中删除该信息集已经所包含的信息项
            if (infoItemVOs == null || infoItemVOs.length == 0)
            {
                for (Iterator<String> iterator = mapQryConditionFromDb.keySet().iterator(); iterator.hasNext();)
                {
                    String strMapKey = iterator.next();
                    
                    if (strMapKey.contains(infoSetVO.getInfoset_code() + "."))
                    {
                        QueryConditionVO fromDbQryConditionVO = mapQryConditionFromDb.get(strMapKey);
                        fromDbQryConditionVO.setStatus(VOStatus.DELETED);
                        
                        addToQryConditionVO(listQryConditionVO, fromDbQryConditionVO);
                    }
                }
                
                continue;
            }
            
            // 以数据库中已有的信息为基础，把元数据、信息项的信息同步到单据模板字段上
            for (InfoItemVO infoItemVO : infoItemVOs)
            {
                String strMapKey = infoItemVO.getItem_code().toLowerCase();
                
                if (!blMainTable)
                {
                    strMapKey = (bean.getName() + "." + strMapKey).toLowerCase();
                }
                
                QueryConditionVO fromDbQryConditionVO = mapQryConditionFromDb.get(strMapKey);
                QueryConditionVO fromMdQryConditionVO = mapQryConditionFromMd.get(strMapKey);
                
                QueryConditionVO newQryConditionVO = null;
                
                // 规则：如果数据库中已有数据，则不用元数据和信息集同步
                if (fromDbQryConditionVO == null && !infoItemVO.getHided().booleanValue() && infoItemVO.getCustom_attr().booleanValue())
                {
                    newQryConditionVO = syncQueryTempletByMd(fromDbQryConditionVO, fromMdQryConditionVO);
                    newQryConditionVO = syncQueryTempletByInfoItem(newQryConditionVO, bean, infoItemVO, blMainTable);
                }
                
                if (newQryConditionVO != null)
                {
                    newQryConditionVO.setDispSequence(iShowOrder++);
                    
                    addToQryConditionVO(listQryConditionVO, newQryConditionVO);
                }
                else if (fromDbQryConditionVO != null)
                {
                    fromDbQryConditionVO.setStatus(VOStatus.UPDATED);
                    fromDbQryConditionVO.setDispSequence(iShowOrder++);
                    
                    addToQryConditionVO(listQryConditionVO, fromDbQryConditionVO);
                }
            }
        }
        
        // 数据库中是否已经存在模板，如果存在，后面就是更新操作，如果不存在，后面就是新增操作
        if (queryTempletTotalVOFromDB == null)
        {
            queryTempletTotalVOFromDB = new QueryTempletTotalVO();
            
            QueryTempletVO queryTempletVO = new QueryTempletVO();
            queryTempletVO.setMetaclass(mainBean == null ? null : mainBean.getID());
            queryTempletVO.setModelCode(mainBean == null ? null : mainBean.getName());
            queryTempletVO.setModelName(mainBean == null ? null : mainBean.getDisplayName());
            queryTempletVO.setNodeCode(mainBean == null ? null : mainBean.getName());
            queryTempletVO.setResid(mainBean == null ? infoSortVO.getResid() : mainBean.getResID());
            queryTempletVO.setPkCorp(strDefaultPkCorp);
            queryTempletVO.setStatus(VOStatus.NEW);
            
            queryTempletTotalVOFromDB.setTemplet(queryTempletVO);
        }
        
        queryTempletTotalVOFromDB.setConditionVOs(listQryConditionVO.toArray(new QueryConditionVO[0]));
        
        IHookPrivate hookPrivate = DefaultHookPrivate.getInstance(infoSortVO);
        
        // 前事件通知
        if (blDispatchEvent)
        {
            BusinessEvent beforeEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_QUERY_TEMPLATE_BEFORE, queryTempletTotalVOFromDB);
            EventDispatcher.fireEvent(beforeEvent);
            
            if (hookPrivate != null)
            {
                queryTempletTotalVOFromDB = hookPrivate.syncQueryTemplateBefore(queryTempletTotalVOFromDB);
            }
        }
        
        if (blExistInDb)
        {
            NCLocator.getInstance().lookup(IQueryTemplateBase.class).updateAggrVO(queryTempletTotalVOFromDB);
        }
        else
        {
            NCLocator.getInstance().lookup(IQueryTemplateBase.class).insertTotal(queryTempletTotalVOFromDB);
        }
        
        // 后事件通知
        if (blDispatchEvent)
        {
            BusinessEvent afterEvent = new BusinessEvent(DOC_NAME, IInfoSetEventType.SYNC_QUERY_TEMPLATE_AFTER, infoSetVOs);
            EventDispatcher.fireEvent(afterEvent);
            
            if (hookPrivate != null)
            {
                queryTempletTotalVOFromDB = hookPrivate.syncQueryTemplateAfter(queryTempletTotalVOFromDB);
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-24 9:25:32<br>
     * @see nc.itf.hr.infoset.IInfoSet#syncTemplet(boolean, InfoSortVO, InfoSetVO...)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void syncTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSortVO == null || infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        checkInfoSet(infoSortVO.getPk_infoset_sort(), (InfoSetVO[]) null);
        
        syncInfoItemWithMd(infoSetVOs);
        
        syncBillTemplet(blDispatchEvent, infoSortVO, infoSetVOs);
        
        syncQueryTemplet(blDispatchEvent, infoSortVO, infoSetVOs);
        
        // 打印模板因为已经用到了元数据，所以先不用同步
        // syncPrintTemplet(blDispatchEvent, infoSortVO, infoSetVOs);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-8-6 10:46:07<br>
     * @see nc.itf.hr.infoset.IInfoSet#syncTempletBySort(boolean, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void syncTempletBySort(boolean blDispatchEvent, String strPk_sort) throws BusinessException
    {
        InfoSortVO infoSortVOs[] = queryInfoSortByPks(strPk_sort);
        InfoSetVO infoSetVOs[] = queryInfoSetBySortPk(strPk_sort);
        
        syncTemplet(blDispatchEvent, infoSortVOs == null || infoSortVOs.length == 0 ? null : infoSortVOs[0], infoSetVOs);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-8 14:16:51<br>
     * @see nc.itf.hr.infoset.IInfoSet#syncVO(nc.vo.hr.infoset.InfoSortVO, nc.vo.hr.infoset.InfoSetVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void syncVO(InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException
    {
        if (infoSortVO == null || infoSetVOs == null || infoSetVOs.length == 0)
        {
            return;
        }
        
        String strModule = null;
        
        ModuleVO moduleVOs[] = PfDataCache.getAllModules();
        if (moduleVOs != null)
        {
            for (ModuleVO moduleVO : moduleVOs)
            {
                if (moduleVO.getModuleid().equals(infoSortVO.getModule_code()))
                {
                    strModule = moduleVO.getDevmodule();
                    break;
                }
            }
        }
        
        if (strModule == null || strModule.trim().length() == 0)
        {
            Logger.warn("没有生成VO类！");
            return;
        }
        
        String strSrcDir = CodeGenUtils.buildFileURL(RuntimeEnv.getInstance().getNCHome(), "modules", strModule, "src", "public", "");
        
        try
        {
            FileUtils.forceMkdir(new File(strSrcDir));
        }
        catch (IOException ex)
        {
            throw new BusinessException(ex.getMessage(), ex);
        }
        
        IPublishService publishService = NCLocator.getInstance().lookup(IPublishService.class);
        
        boolean blGenNewClass = false;
        
        for (InfoSetVO infoSetVO : infoSetVOs)
        {
            if (infoSetVO.getVo_class_name() == null || infoSetVO.getVo_class_name().trim().length() == 0)
            {
                continue;
            }
            
            try
            {
                Logger.error("\n*******************************查找VO类开始*********************************************" + "\n* 时间：" + PubEnv.getServerTime()
                    + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost() + "\n* VO类："
                    + infoSetVO.getVo_class_name() + "\n*******************************************************************************************");
                
                Class.forName(infoSetVO.getVo_class_name());
                
                Logger.error("\n*******************************查找VO类结束，并且找到了VO类*********************************************");
            }
            catch (ClassNotFoundException ex)
            {
                Logger.error("\n*******************************缺少VO类*********************************************" + "\n* 时间：" + PubEnv.getServerTime()
                    + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
                    + "\n*******************************************************************************************");
                
                blGenNewClass = true;
                
                publishService.generateCodeByBeanID(infoSetVO.getMeta_data_id(), "0", strSrcDir);
            }
        }
        
        if (blGenNewClass)
        {
            String strScriptPath = CodeGenUtils.buildFileURL(RuntimeEnv.getInstance().getNCHome(), "modules", "hrpub", "config", "scriptbuild_infoset.xml");
            
            FormConfigCompiler compiler = new FormConfigCompiler();
            compiler.setModule(strModule);
            
            compiler.compileJavaFile(strScriptPath);
            
            // 通知客户端更新代码
            ScanService scan = new ScanService(RuntimeEnv.getInstance().getNCHome());
            scan.rescan();
            
            try
            {
                NCLocator.getInstance().lookup(IProvisionService.class).reloadPackIndex();
            }
            catch (Exception ex)
            {
                Logger.error(ex.getMessage(), ex);
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:05:30<br>
     * @see nc.itf.hr.infoset.IInfoSet#updateInfoItem(boolean, boolean,InfoItemVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoItemVO updateInfoItem(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoItemVO infoItemVO) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        InfoItemVO infoItemVO2 = serviceTemplate.update(infoItemVO, blChangeAuditInfo);
        
        InfoSetHelper.putCache(infoItemVO2);
        
        return infoItemVO2;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-4 14:44:34<br>
     * @see nc.itf.hr.infoset.IInfoSet#updateInfoSet(boolean, boolean,InfoSetVO...)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSetVO[] updateInfoSet(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSetVO... infoSetVOs) throws BusinessException
    {
        checkInfoSet(infoSetVOs[0].getPk_infoset_sort(), infoSetVOs);
        
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        for (int i = 0; i < infoSetVOs.length; i++)
        {
            if (blChangeAuditInfo)
            {
                InfoItemVO infoItemVOs[] = infoSetVOs[i].getInfo_item();
                
                if (infoItemVOs != null)
                {
                    for (InfoItemVO infoItemVO : infoItemVOs)
                    {
                        SimpleDocServiceTemplate.setAuditInfoAndTs(infoItemVO, blChangeAuditInfo);
                    }
                }
            }
            
            infoSetVOs[i] = serviceTemplate.update(infoSetVOs[i], blChangeAuditInfo);
        }
        
        InfoSetHelper.putCache(infoSetVOs);
        
        return infoSetVOs;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2009-12-9 11:05:30<br>
     * @see nc.itf.hr.infoset.IInfoSet#updateInfoSort(boolean, boolean,nc.vo.hr.infoset.InfoSortVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public InfoSortVO updateInfoSort(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO) throws BusinessException
    {
        serviceTemplate.setDispatchEvent(blDispatchEvent);
        
        InfoSortVO infoSortVO2 = serviceTemplate.update(infoSortVO, blChangeAuditInfo);
        
        InfoSetHelper.putCache(infoSortVO2);
        
        return infoSortVO2;
    }
    
    /**************************************************************
     * <br>
     * Created on 2013-10-23 17:02:32<br>
     * @param languageVO
     * @param strResPath
     * @param properties
     * @author Rocex Wang
     **************************************************************/
    void writeProperties(LanguageVO languageVO, String strResPath, Properties properties)
    {
        if (properties.isEmpty())
        {
            return;
        }
        
        InputStreamReader reader = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream buffInputStream = null;
        
        OutputStreamWriter writer = null;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream buffOutputStream = null;
        
        try
        {
            String strFilePath = RuntimeEnv.getInstance().getNCHome() + "/resources/lang/" + languageVO.getLangcode() + "/" + strResPath + "/";
            
            File filePath = new File(strFilePath);
            
            if (!filePath.exists())
            {
                FileUtils.forceMkdir(filePath);
            }
            
            File file = new File(strFilePath + "infoset.properties");
            
            if (!file.exists())
            {
                file.createNewFile();
            }
            else
            {
                // 文件已经存在，需要把原来的和新加的合并后保存
                fileInputStream = new FileInputStream(file);
                buffInputStream = new BufferedInputStream(fileInputStream);
                reader = new InputStreamReader(buffInputStream, languageVO.getCharsetname() == null ? "UTF-16" : languageVO.getCharsetname());
                
                Properties propertiesNew = new SortedProperties();
                
                propertiesNew.load(reader);
                
                propertiesNew.putAll(properties);
                properties.putAll(propertiesNew);
            }
            
            fileOutputStream = new FileOutputStream(file, false);
            buffOutputStream = new BufferedOutputStream(fileOutputStream);
            writer = new OutputStreamWriter(buffOutputStream, languageVO.getCharsetname() == null ? "UTF-16" : languageVO.getCharsetname());
            
            properties.store(writer, "This resource file by the information set maintenance, DO NOT manually modify!");
        }
        catch (Exception ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(buffInputStream);
            
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(buffOutputStream);
        }
    }

    /**************************************************************
     * 添加预置字段的接口实现方法<br>
     * Created on 2018-10-02 02:18:32<br>
     * @author Ethan Wu
     **************************************************************/
	@Override
	public void addLocalizationFields(String country) throws BusinessException {
		// TODO 试一下到底可不可以全查
		// 获取信息集 这边没有指定哪一个是为了以后其他信息集也需要本地化字段
		LocalLicenseUtil.checkLocalLicense(country);
		String whereSql = " infoset_code = 'bd_psndoc' ";
		InfoSetVO[] bd_psndocInfoSet = queryInfoSet(whereSql);
		
		IAddLocalizationFieldStrategy strategy = 
				AddLocalFieldStrategyFactory.getStrategy(country);
		bd_psndocInfoSet = strategy.addLocalField(bd_psndocInfoSet);
		
		InfoSetVO[] bills = updateInfoSet(true, true, bd_psndocInfoSet);
		if (bills != null && bills.length > 0) {
			return;
		} else {
			throw new BusinessException("Insertion failed");
		}
	}
}
