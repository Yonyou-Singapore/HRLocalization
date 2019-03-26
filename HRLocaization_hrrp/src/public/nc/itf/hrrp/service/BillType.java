package nc.itf.hrrp.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Title: BillType</P>
 * <p>Description: 配置当前单据的单据类型
 * 				   处理场景：单据对象在保存和更新的时候获取单据类型
 * </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BillType {
	public String billType() ;
	
	public String pk_org() default "pk_org";
	
	public String pk_group() default "pk_group";
	
	public String billNo() default "billno";
}
