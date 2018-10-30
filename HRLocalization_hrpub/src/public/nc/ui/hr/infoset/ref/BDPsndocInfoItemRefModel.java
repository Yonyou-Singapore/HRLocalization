package nc.ui.hr.infoset.ref;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;

/***************************************************************************
 * HR本地化：人员基础档案信息集参照 专门用于银行报盘的参照<br>
 * Created on 2018-10-17 16:34:22<br>
 * @author Ethan Wu
 ***************************************************************************/
public class BDPsndocInfoItemRefModel extends AbstractRefModel
{
    
    public BDPsndocInfoItemRefModel()
    {
        super();
        
        reset();
    }

    @Override
    public void reset()
    {
        setRefTitle(ResHelper.getString("6001infset", "2infoset-000053")/* @res "信息集" */);
        setRefNodeName("人员基础档案信息集"); /*-=notranslate=-*/
        setDefaultFieldCount(3);
        setMutilLangNameRef(false);
        
        // 这边打算用系统自带的公式解析 输入既定的公式格式 然后导出
        setPkFieldCode(InfoItemVO.PK_INFOSET_ITEM);
        setRefCodeField("concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")");
        setRefNameField("concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")");
        setTableName(InfoItemVO.getDefaultTableName());
        setFieldCode(new String[]{ "concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")", InfoItemVO.ITEM_NAME, InfoItemVO.META_DATA});
        setHiddenFieldCode(new String[]{
        		InfoItemVO.PK_INFOSET_ITEM,
        		InfoItemVO.PK_INFOSET,
        		InfoItemVO.RESPATH,
        		InfoItemVO.RESID});
        setWherePart(" 1 = 1 and meta_data like 'hrhi.bd_psndoc.%' ");
        setFieldName(new String[]{ResHelper.getString("6001infset", "2infset-000052")
        /* @res "信息集编码" */, ResHelper.getString("6001infset", "2infset-000053")
        /* @res "信息集名称" */, ResHelper.getString("6001infset", "2infset-000032")
        /* @res "对应元数据" */});
        
        resetFieldName();
    }
}
