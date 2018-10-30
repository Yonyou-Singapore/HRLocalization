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
public class FormatItemVO extends SuperVO
{
    private java.lang.String ifid;
    private java.lang.String pk_dataintface_b;
    private java.lang.Integer iseq;
    private java.lang.Integer isourcetype = 1;
    private java.lang.String vcontent;
    private java.lang.String vfieldname;
    private java.lang.Integer ifieldtype;
    private java.lang.Integer ifldwidth;
    private java.lang.Integer iflddecimal;
    private java.lang.Integer vseparator;
    private java.lang.Integer icaretpos = 0;
    private java.lang.String vcaret;
    private java.lang.String vincludebefore;
    private java.lang.String vincludeafter;
    private java.lang.Integer iiforder;
    private java.lang.String vformulastr;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    
    // HR本地化对银行报盘改动：添加折行字段
    private java.lang.Integer inextline = 0;
    
    public static final String IFID = "ifid";
    public static final String PK_DATAINTFACE_B = "pk_dataintface_b";
    public static final String ISEQ = "iseq";
    public static final String ISOURCETYPE = "isourcetype";
    public static final String VCONTENT = "vcontent";
    public static final String VFIELDNAME = "vfieldname";
    public static final String IFIELDTYPE = "ifieldtype";
    public static final String IFLDWIDTH = "ifldwidth";
    public static final String IFLDDECIMAL = "iflddecimal";
    public static final String VSEPARATOR = "vseparator";
    public static final String ICARETPOS = "icaretpos";
    public static final String VCARET = "vcaret";
    public static final String VINCLUDEBEFORE = "vincludebefore";
    public static final String VINCLUDEAFTER = "vincludeafter";
    public static final String IIFORDER = "iiforder";
    public static final String VFORMULASTR = "vformulastr";
    
    // HR本地化对银行报盘改动：添加折行字段
    public static final String INEXTLINE = "inextline";
    
    /**
     * <p>
     * 返回表名称.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_dataintface_b";
    }
    
    /**
     * 按照默认方式创建构造子.
     * 创建日期:
     */
    public FormatItemVO()
    {
        super();
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
     * 属性ifieldtype的Getter方法.属性名：字段类型
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfieldtype()
    {
        return ifieldtype;
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
     * 属性iiforder的Getter方法.属性名：排序
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiforder()
    {
        return iiforder;
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
     * 属性isourcetype的Getter方法.属性名：数据来源
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIsourcetype()
    {
        return isourcetype;
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
     * 属性pk_dataintface_b的Getter方法.属性名：接口设置子表主键
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_dataintface_b()
    {
        return pk_dataintface_b;
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
        return "pk_dataintface_b";
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
        return "hr_dataintface_b";
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
     * 属性vformulastr的Getter方法.属性名：公式显示
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVformulastr()
    {
        return vformulastr;
    }
    
    /**
     * 属性vincludeafter的Getter方法.属性名：后包含符
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVincludeafter()
    {
        return vincludeafter;
    }
    
    /**
     * 属性vincludebefore的Getter方法.属性名：前包含符
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVincludebefore()
    {
        return vincludebefore;
    }
    
    /**
     * 属性vseparator的Getter方法.属性名：项目分隔符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getVseparator()
    {
        return vseparator;
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
     * 属性ifieldtype的Setter方法.属性名：字段类型
     * 创建日期:
     * @param newIfieldtype java.lang.Integer
     */
    public void setIfieldtype(java.lang.Integer newIfieldtype)
    {
        this.ifieldtype = newIfieldtype;
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
     * 属性iiforder的Setter方法.属性名：排序
     * 创建日期:
     * @param newIiforder java.lang.Integer
     */
    public void setIiforder(java.lang.Integer newIiforder)
    {
        this.iiforder = newIiforder;
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
     * 属性isourcetype的Setter方法.属性名：数据来源
     * 创建日期:
     * @param newIsourcetype java.lang.Integer
     */
    public void setIsourcetype(java.lang.Integer newIsourcetype)
    {
        this.isourcetype = newIsourcetype;
    }
    
    /**
     * 属性pk_dataintface_b的Setter方法.属性名：接口设置子表主键
     * 创建日期:
     * @param newPk_dataintface_b java.lang.String
     */
    public void setPk_dataintface_b(java.lang.String newPk_dataintface_b)
    {
        this.pk_dataintface_b = newPk_dataintface_b;
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
     * 属性vformulastr的Setter方法.属性名：公式显示
     * 创建日期:
     * @param newVformulastr java.lang.String
     */
    public void setVformulastr(java.lang.String newVformulastr)
    {
        this.vformulastr = newVformulastr;
    }
    
    /**
     * 属性vincludeafter的Setter方法.属性名：后包含符
     * 创建日期:
     * @param newVincludeafter java.lang.String
     */
    public void setVincludeafter(java.lang.String newVincludeafter)
    {
        this.vincludeafter = newVincludeafter;
    }
    
    /**
     * 属性vincludebefore的Setter方法.属性名：前包含符
     * 创建日期:
     * @param newVincludebefore java.lang.String
     */
    public void setVincludebefore(java.lang.String newVincludebefore)
    {
        this.vincludebefore = newVincludebefore;
    }
    
    /**
     * 属性vseparator的Setter方法.属性名：项目分隔符
     * 创建日期:
     * @param newVseparator java.lang.Integer
     */
    public void setVseparator(java.lang.Integer newVseparator)
    {
        this.vseparator = newVseparator;
    }

    // HR本地化需求：折行字段的getter和setter start
	public java.lang.Integer getInextline() {
		return inextline;
	}

	public void setInextline(java.lang.Integer inextline) {
		this.inextline = inextline;
	}
	// HR本地化需求：折行字段的getter和setter end
    
}
