/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.hrrp.pdfrep;

import nc.md.model.impl.MDEnum;
import nc.md.model.IEnumValue;
	
/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:
 * @author 
 * @version NCPrj ??
 */
public class HrrpFormat extends MDEnum{
	public HrrpFormat(IEnumValue enumvalue){
		super(enumvalue);
	}
	
public static final HrrpFormat SRCVALUE= MDEnum.valueOf(HrrpFormat.class, "1");
public static final HrrpFormat SPLITSEGMENT= MDEnum.valueOf(HrrpFormat.class, "11");
public static final HrrpFormat SPLITSPACE= MDEnum.valueOf(HrrpFormat.class, "12");
public static final HrrpFormat SPLITTHAI= MDEnum.valueOf(HrrpFormat.class, "13");
public static final HrrpFormat SPLIT_INTEGER= MDEnum.valueOf(HrrpFormat.class, "6");
public static final HrrpFormat SPLIT_DECIMAL= MDEnum.valueOf(HrrpFormat.class, "7");
public static final HrrpFormat SPLIT_CURRENTPAGE= MDEnum.valueOf(HrrpFormat.class, "2");
public static final HrrpFormat SPLIT_TOTALPAGE= MDEnum.valueOf(HrrpFormat.class, "3");
	

} 
