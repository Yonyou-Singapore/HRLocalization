package nc.constant.hrrp.report;

/**
 * <p>Title: Constant</P>
 * <p>Description: 常量类</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public class Constant {
	
	/**
	 * 不允许构造该类的实例
	 */
	protected Constant() {
	}

	/**
	 * <p>Title: FORMAT</P>
	 * <p>Description: 数据格式</p>
	 * <p>Copyright: Copyright (c) 2008</p>
	 * @author 
	 * @version 1.0
	 * @since 2014-10-29
	 */
	public interface FORMAT{
		/**
		 * 原数据
		 */
		public static final String DATA = "1001ZZ10000000000U6H";
		/**
		 * 原值
		 */
		public static final String NONE = "1001ZZ10000000000U6I";
		/**
		 * 两位小数数字
		 */
		public static final String NUMBER = "1001ZZ10000000000U6J";
		/**
		 * 布尔
		 */
		public static final String BOOL = "1001ZZ10000000000U6K";
		/**
		 * 单字符
		 */
		public static final String SINGLE = "1001ZZ10000000000U6L";		
		/**
		 * 整数
		 */
		public static final String INT = "1001ZZ10000000000U6M";
		/**
		 * 小数
		 */
		public static final String DECIMAL = "1001ZZ10000000000U6N";
		/**
		 * 当前页码
		 */
		public static final String PAGE_NOW = "1001ZZ10000000000U6O";
		/**
		 * 总页数
		 */
		public static final String TOTAL = "1001ZZ10000000000U6P";
		/**
		 * 日期
		 */
		public static final String DATE = "1001ZZ10000000000U6Q";
		/**
		 * 分割格式
		 */
		public static final String SPLITE = "1001ZZ10000000000U6R";
		
		/**
		 * 空格分割
		 */
		public static final String SPACE = "1001ZZ10000000000U6S";
		/**
		 * 泰文格式
		 */
		public static final String TAIWEN="1001ZZ10000000000U6T";
	}
	
	/**
	 * <p>Title: BOOL</P>
	 * <p>Description: 布尔值</p>
	 * @author 
	 * @version 1.0
	 */
	public interface BOOL{
		/**
		 *  是
		 */
		public static final String YES = "1001ZZ10000000000U6C";
		
		/**
		 *  否
		 */
		public static final String NO = "1001ZZ10000000000U6D";
		/**
		 *  文本
		 */
		public static final String TEXT = "1001ZZ10000000000U6E";
	}
	
	public interface TFCONSTANT{
		/**
		 *  DEFDOCLIST
		 */
		public static final String TFPKDEFDOCLIST = "1001ZZ10000000001O6Q";
		/**
		 *  人员对应字段名
		 */
		public static final String PSN_TAXFREE_FIELD = "psn_field";
		/**
		 *  TAX8对应字段名
		 */
		public static final String PSN_TAX8_FIELD = "psn_tax8";
		/**
		 *  TAX对应字段前缀
		 */
		public static final String PSN_TAX_FIELD = "psn_tax";
		
		public static final String PSN_SNIC_FIELD = "psn_snic";
	}
	
	public interface REPCONSTANT{
		/**
		 *  字体文件名
		 */
		public static final String FONT_NAME = "cordia.ttf";
		/**
		 *  系统字体路径
		 */
		public static final String FONT_SYS_PATH = "C:\\Windows\\Fonts\\cordia.ttf";
	}
}
