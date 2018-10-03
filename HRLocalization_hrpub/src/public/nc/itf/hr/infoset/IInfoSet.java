package nc.itf.hr.infoset;

import nc.vo.hr.infoset.InfoItemMapVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.infoset.InfoSortVO;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * <br>
 * Created on 2009-12-4 14:29:34<br>
 * @author Rocex Wang
 ***************************************************************************/
public interface IInfoSet
{
    /***************************************************************************
     * 删除信息项<br>
     * Created on 2009-12-9 11:03:48<br>
     * @param blDispatchEvent
     * @param infoItemVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoItem(boolean blDispatchEvent, InfoItemVO... infoItemVOs) throws BusinessException;
    
    /***************************************************************************
     * 删除信息集<br>
     * Created on 2009-12-4 14:33:20<br>
     * @param blDispatchEvent
     * @param infoSetVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoSet(boolean blDispatchEvent, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * 删除信息集分类<br>
     * Created on 2009-12-9 10:59:24<br>
     * @param blDispatchEvent
     * @param infoSortVO 信息集分类
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException;
    
    /***************************************************************************
     * 增加信息项<br>
     * Created on 2009-12-9 11:04:10<br>
     * @param blDispatchEvent
     * @param infoItemVO
     * @return InfoItemVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoItemVO insertInfoItem(boolean blDispatchEvent, InfoItemVO infoItemVO) throws BusinessException;
    
    /***************************************************************************
     * 增加信息集<br>
     * Created on 2009-12-4 14:33:26<br>
     * @param blDispatchEvent
     * @param infoSetVO
     * @return InfoSetVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO insertInfoSet(boolean blDispatchEvent, InfoSetVO infoSetVO) throws BusinessException;
    
    /***************************************************************************
     * 增加信息集分类<br>
     * Created on 2009-12-9 11:02:39<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @return InfoSortVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSortVO insertInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException;
    
    /**************************************************************
     * 批量保存V5 和 V6 字段对应关系记录<br>
     * Created on 2012-10-20 10:32:24<br>
     * @param infoItemMapVOs
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    void saveInfoItemMap(InfoItemMapVO infoItemMapVOs[]) throws BusinessException;
    
    /***************************************************************************
     * 同步单据模板<br>
     * Created on 2009-12-24 9:28:51<br>
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    void syncBillTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * 同步元数据<br>
     * Created on 2009-12-22 15:41:14<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSortVO
     * @param infoSetVOs
     * @return InfoSetVO[]
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO[] syncMetaData(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /**************************************************************
     * 通过信息集类别主键同步元数据<br>
     * Created on 2013-5-29 10:12:49<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param strPk_sort
     * @return InfoSetVO[]
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    InfoSetVO[] syncMetaDataBySort(boolean blDispatchEvent, boolean blChangeAuditInfo, String strPk_sort) throws BusinessException;
    
    /***************************************************************************
     * 同步打印模板<br>
     * Created on 2009-12-24 9:28:45<br>
     * @param infoSetVOs
     * @param infoSortVO
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    void syncPrintTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * 同步查询模板<br>
     * Created on 2009-12-24 9:28:29<br>
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @param infoSortVO
     * @throws BusinessException
     ***************************************************************************/
    void syncQueryTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * 同步模板，包括单据模板、查询模板<br>
     * Created on 2009-12-24 9:24:25<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @throws BusinessException
     ***************************************************************************/
    void syncTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * 同步模板，包括单据模板、查询模板<br>
     * Created on 2015-8-6 10:42:47<br>
     * @param blDispatchEvent
     * @param strPk_sort
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void syncTempletBySort(boolean blDispatchEvent, String strPk_sort) throws BusinessException;
    
    /***************************************************************************
     * 同步信息集对应的VO<br>
     * Created on 2010-1-8 14:05:35<br>
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @throws BusinessException
     ***************************************************************************/
    void syncVO(InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-9 11:04:37<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoItemVO
     * @return InfoItemVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoItemVO updateInfoItem(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoItemVO infoItemVO) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-4 14:33:44<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSetVOs
     * @return InfoSetVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO[] updateInfoSet(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-9 11:03:07<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSortVO
     * @return InfoSortVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSortVO updateInfoSort(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO) throws BusinessException;
    
    /***************************************************************************
     * 添加东南亚本地化字段<br>
     * Created on 2018-10-02 02:16:35<br>
     * @author Ethan Wu
     * @param country
     * @throws BusinessException
     ***************************************************************************/
    void addLocalizationFields(String country) throws BusinessException;
}
