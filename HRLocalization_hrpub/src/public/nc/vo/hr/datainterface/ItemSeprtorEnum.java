/***************************************************************
 * \
 * \
 ***************************************************************/
package nc.vo.hr.datainterface;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:
 * @author
 * @version NCPrj ??
 */
public class ItemSeprtorEnum extends MDEnum
{
    public static final ItemSeprtorEnum COMMA = MDEnum.valueOf(ItemSeprtorEnum.class, 0);
    
    public static final ItemSeprtorEnum SEM = MDEnum.valueOf(ItemSeprtorEnum.class, 1);
    public static final ItemSeprtorEnum ERECT = MDEnum.valueOf(ItemSeprtorEnum.class, 2);
    public static final ItemSeprtorEnum NULL = MDEnum.valueOf(ItemSeprtorEnum.class, 3);
//		20151016 xiejie3 NCdp205398656 银行报盘项目分隔符添加“空格”，测试要求撤掉此补丁。
//    // 20150902 xiejie3 补丁合并，NCdp205398656
//    //shenliangc 银行报盘项目分隔符添加“空格”
//    //空格
//    public static final ItemSeprtorEnum SPACE = MDEnum.valueOf(ItemSeprtorEnum.class, 3);
//    
    
    public ItemSeprtorEnum(IEnumValue enumvalue)
    {
        super(enumvalue);
    }
    
}
