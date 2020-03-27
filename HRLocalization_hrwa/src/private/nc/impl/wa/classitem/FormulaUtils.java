package nc.impl.wa.classitem;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.itf.hr.formula.DefaultFormulaManager;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.vo.hr.func.HrFormula;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.classitem.WaClassItemVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.springframework.util.StringUtils;

/**
 * 
 * @author: wh 
 * @date: 2009-12-29 下午01:10:08
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public class FormulaUtils implements DefaultFormulaManager {

	private static VelocityEngine	velocityEngine		= null;

	  private static final String VM_LOAD_PATH = 
	    	File.separator+"resources"+File.separator+"hr"+File.separator+"wa"+File.separator  + "formula"+ File.separator + "vm"+File.separator;//

	private static final String		DEFAULT_ENCODING	= "GBK";

	static {
		// 现在暂时使用GBK作为文件编码
		Properties props = new Properties();
		props.setProperty(RuntimeConstants.ENCODING_DEFAULT, DEFAULT_ENCODING);
		props.setProperty(RuntimeConstants.INPUT_ENCODING, DEFAULT_ENCODING);
		props.setProperty(RuntimeConstants.OUTPUT_ENCODING, DEFAULT_ENCODING);

		try {			
			String fileurl = RuntimeEnv.getInstance().getCanonicalNCHome()+VM_LOAD_PATH;
	    	
			File loaderPath = new File(fileurl);

			// 载入VM文件的根目录
			props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, loaderPath.getAbsolutePath());
			//add by weiningc 202200210 start
			props.setProperty("runtime.log.logsystem.class", NullLogChute.class.getName());
			//end
			velocityEngine = new VelocityEngine();

			
			velocityEngine.init(props);
			
		} catch (Exception e) {
			Logger.error("模板引擎初始化错误", e);
		}
	}


	public  FormulaUtils() {
		
	}
	
	
	

	/* (non-Javadoc)
	 * @see nc.impl.wa.classitem.DefaultFormulaManager#getSystemFormula(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public  HrFormula getSystemFormula(String pk_org,String pk_wa_class,String cyear,String cperiod, String itemKey) {

		try {
			HrFormula f = new HrFormula();
			WaClassItemVO[] items = NCLocator.getInstance().lookup(IClassItemQueryService.class).queryAllClassItemInfos(
					pk_org,pk_wa_class,cyear,cperiod);
			VelocityContext vmContext = new VelocityContext();
			//20160105 shenliangc NCdp205568261 集团方案修改系统项目保存后，进行薪资发放计算时，系统项目都没有进行扣税
			//集团方案中的系统预置项目的公式都显示自定义公式；应该为系统默认公式
			if(!ArrayUtils.isEmpty(items)){
				for(WaClassItemVO itemVO : items){
					itemVO.setName(itemVO.getMultilangName());
				}
			}
			vmContext.put("items", items);
			StringWriter writer = new StringWriter();
				
			velocityEngine.mergeTemplate( File.separator + itemKey + ".sql.vm", vmContext, writer);				
			String scriptLang = writer.toString();
			//shenliangc 20140606 
			//解决薪资发放项目系统项目公式修改后恢复默认，无法恢复为系统公式问题。
			//连带修改，保证恢复默认系统公式中不包含多余空格。
			scriptLang = StringUtils.trimAllWhitespace(scriptLang);
			f.setScirptLang(scriptLang);
			
			StringWriter writer2 = new StringWriter();	
			
			
			velocityEngine.mergeTemplate(itemKey + ".desc.vm", vmContext, writer2);	
			String busiLang = writer2.toString();
			//shenliangc 20140606 
			//解决薪资发放项目系统项目公式修改后恢复默认，无法恢复为系统公式问题。
			//连带修改，保证恢复默认系统公式中不包含多余空格。
			busiLang = StringUtils.trimAllWhitespace(busiLang);
			f.setBusinessLang(busiLang);
			f.setItemKey(itemKey);
			f.setDefault(true);
			return f;

		} catch (BusinessException e) {
			Logger.error("查询时出现异常", e);
			throw new IllegalStateException(e);
		} catch (ResourceNotFoundException e) {
			Logger.error("未找到模板文件", e);
			throw new IllegalStateException(e);
		} catch (Exception e) {
			Logger.error("其他运行时错误", e);
			throw new IllegalStateException(e);
		}
	}
	
	

	public static String getNchome(){
		Logger.info(RuntimeEnv.getInstance().getNCHome());
    	return RuntimeEnv.getInstance().getNCHome();
    }
	
    private static File getLoaderDir(){
    	String fileurl = getNchome()+VM_LOAD_PATH;
    	return new File(fileurl);
    }
    
}
