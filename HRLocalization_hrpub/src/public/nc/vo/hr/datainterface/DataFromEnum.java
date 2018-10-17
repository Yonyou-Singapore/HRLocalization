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
public class DataFromEnum extends MDEnum
{
    public static final DataFromEnum SINGLE = MDEnum.valueOf(DataFromEnum.class, 0);
    
    public static final DataFromEnum FORMULAR = MDEnum.valueOf(DataFromEnum.class, 1);
    
    public DataFromEnum(IEnumValue enumvalue)
    {
        super(enumvalue);
    }
    
}
