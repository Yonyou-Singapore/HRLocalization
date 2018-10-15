/***************************************************************
 * \
 * \
 ***************************************************************/
package nc.vo.hr.datainterface;

import nc.vo.pub.SuperVO;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class IfsettopVO extends SuperVO
{
    private java.lang.String ifid;
    private java.lang.String pk_hr_ifsettop;
    private java.lang.Integer iseq;
    private java.lang.String vcontent;
    private java.lang.String vfieldname;
    private java.lang.Integer ifldwidth;
    private java.lang.Integer iflddecimal;
    private java.lang.Integer vseparator;
    private java.lang.Integer icaretpos;
    private java.lang.String vcaret;
    private java.lang.String dateformat;
    private java.lang.Integer iifsum;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    // HR本地化需求：添加折行和是否为首/尾行字段
    private java.lang.Integer inextline;
    private java.lang.Integer itoplineposition;
    
    public static final String IFID = "ifid";
    public static final String PK_HR_IFSETTOP = "pk_hr_ifsettop";
    public static final String ISEQ = "iseq";
    public static final String VCONTENT = "vcontent";
    public static final String VFIELDNAME = "vfieldname";
    public static final String IFLDWIDTH = "ifldwidth";
    public static final String IFLDDECIMAL = "iflddecimal";
    public static final String VSEPARATOR = "vseparator";
    public static final String ICARETPOS = "icaretpos";
    public static final String VCARET = "vcaret";
    public static final String DATEFORMAT = "dateformat";
    public static final String IIFSUM = "iifsum";
    // HR本地化需求：添加折行和是否为首/尾行字段
    public static final String INEXTLINE = "inextline";
    public static final String ITOPLINEPOSITION = "itoplineposition";
    
    /**
     * <p>
     * 返回表名称.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_ifsettop";
    }
    
    /**
     * 按照默认方式创建构造子.
     * 创建日期:
     */
    public IfsettopVO()
    {
        super();
    }
    
    /**
     * 属性dateformat的Getter方法.属性名：日期格式
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getDateformat()
    {
        return dateformat;
    }
    
    /**
     * 属性dr的Getter方法.属性名：dr
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr()
    {
        return dr;
    }
    
    /**
     * 属性icaretpos的Getter方法.属性名：补位符位置
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIcaretpos()
    {
        return icaretpos;
    }
    
    /**
     * 属性ifid的Getter方法.属性名：parentPK
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getIfid()
    {
        return ifid;
    }
    
    /**
     * 属性iflddecimal的Getter方法.属性名：小数位数
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIflddecimal()
    {
        return iflddecimal;
    }
    
    /**
     * 属性ifldwidth的Getter方法.属性名：项目宽度
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfldwidth()
    {
        return ifldwidth;
    }
    
    /**
     * 属性iifsum的Getter方法.属性名：合计行
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifsum()
    {
        return iifsum;
    }
    
    /**
     * 属性iseq的Getter方法.属性名：序号
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIseq()
    {
        return iseq;
    }
    
    /**
     * <p>
     * 取得父VO主键字段.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getParentPKFieldName()
    {
        return "ifid";
    }
    
    /**
     * 属性pk_wa_ifsettop的Getter方法.属性名：首末行设置表主键
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_hr_ifsettop()
    {
        return pk_hr_ifsettop;
    }
    
    /**
     * <p>
     * 取得表主键.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getPKFieldName()
    {
        return "pk_hr_ifsettop";
    }
    
    /**
     * <p>
     * 返回表名称.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getTableName()
    {
        return "hr_ifsettop";
    }
    
    /**
     * 属性ts的Getter方法.属性名：ts
     * 创建日期:
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs()
    {
        return ts;
    }
    
    /**
     * 属性vcaret的Getter方法.属性名：补位符内容
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVcaret()
    {
        return vcaret;
    }
    
    /**
     * 属性vcontent的Getter方法.属性名：项目
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVcontent()
    {
        return vcontent;
    }
    
    /**
     * 属性vfieldname的Getter方法.属性名：字段名称
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVfieldname()
    {
        return vfieldname;
    }
    
    /**
     * 属性vseparator的Getter方法.属性名：项目分割符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getVseparator()
    {
        return vseparator;
    }
    
    /**
     * 属性dateformat的Setter方法.属性名：日期格式
     * 创建日期:
     * @param newDateformat java.lang.String
     */
    public void setDateformat(java.lang.String newDateformat)
    {
        this.dateformat = newDateformat;
    }
    
    /**
     * 属性dr的Setter方法.属性名：dr
     * 创建日期:
     * @param newDr java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr)
    {
        this.dr = newDr;
    }
    
    /**
     * 属性icaretpos的Setter方法.属性名：补位符位置
     * 创建日期:
     * @param newIcaretpos java.lang.Integer
     */
    public void setIcaretpos(java.lang.Integer newIcaretpos)
    {
        this.icaretpos = newIcaretpos;
    }
    
    /**
     * 属性ifid的Setter方法.属性名：parentPK
     * 创建日期:
     * @param newIfid java.lang.String
     */
    public void setIfid(java.lang.String newIfid)
    {
        this.ifid = newIfid;
    }
    
    /**
     * 属性iflddecimal的Setter方法.属性名：小数位数
     * 创建日期:
     * @param newIflddecimal java.lang.Integer
     */
    public void setIflddecimal(java.lang.Integer newIflddecimal)
    {
        this.iflddecimal = newIflddecimal;
    }
    
    /**
     * 属性ifldwidth的Setter方法.属性名：项目宽度
     * 创建日期:
     * @param newIfldwidth java.lang.Integer
     */
    public void setIfldwidth(java.lang.Integer newIfldwidth)
    {
        this.ifldwidth = newIfldwidth;
    }
    
    /**
     * 属性iifsum的Setter方法.属性名：合计行
     * 创建日期:
     * @param newIifsum java.lang.Integer
     */
    public void setIifsum(java.lang.Integer newIifsum)
    {
        this.iifsum = newIifsum;
    }
    
    /**
     * 属性iseq的Setter方法.属性名：序号
     * 创建日期:
     * @param newIseq java.lang.Integer
     */
    public void setIseq(java.lang.Integer newIseq)
    {
        this.iseq = newIseq;
    }
    
    /**
     * 属性pk_wa_ifsettop的Setter方法.属性名：首末行设置表主键
     * 创建日期:
     * @param newPk_wa_ifsettop java.lang.String
     */
    public void setPk_hr_ifsettop(java.lang.String newPk_hr_ifsettop)
    {
        this.pk_hr_ifsettop = newPk_hr_ifsettop;
    }
    
    /**
     * 属性ts的Setter方法.属性名：ts
     * 创建日期:
     * @param newTs nc.vo.pub.lang.UFDateTime
     */
    public void setTs(nc.vo.pub.lang.UFDateTime newTs)
    {
        this.ts = newTs;
    }
    
    /**
     * 属性vcaret的Setter方法.属性名：补位符内容
     * 创建日期:
     * @param newVcaret java.lang.String
     */
    public void setVcaret(java.lang.String newVcaret)
    {
        this.vcaret = newVcaret;
    }
    
    /**
     * 属性vcontent的Setter方法.属性名：项目
     * 创建日期:
     * @param newVcontent java.lang.String
     */
    public void setVcontent(java.lang.String newVcontent)
    {
        this.vcontent = newVcontent;
    }
    
    /**
     * 属性vfieldname的Setter方法.属性名：字段名称
     * 创建日期:
     * @param newVfieldname java.lang.String
     */
    public void setVfieldname(java.lang.String newVfieldname)
    {
        this.vfieldname = newVfieldname;
    }
    
    /**
     * 属性vseparator的Setter方法.属性名：项目分割符
     * 创建日期:
     * @param newVseparator java.lang.Integer
     */
    public void setVseparator(java.lang.Integer newVseparator)
    {
        this.vseparator = newVseparator;
    }

    // HR本地化需求：折行和是否为首/尾行字段的getter和setter start
	public java.lang.Integer getInextline() {
		return inextline;
	}

	public void setInextline(java.lang.Integer inextline) {
		this.inextline = inextline;
	}

	public java.lang.Integer getItoplineposition() {
		return itoplineposition;
	}

	public void setItoplineposition(java.lang.Integer itoplineposition) {
		this.itoplineposition = itoplineposition;
	}
    // HR本地化需求：折行和是否为首/尾行字段的getter和setter end
    
    
    
}
