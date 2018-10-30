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
public class HrIntfaceVO extends SuperVO
{
    private java.lang.String pk_dataio_intface;
    private java.lang.String pk_bankdoc;
    private java.lang.String vifname;
    private java.lang.String vifname2;
    private java.lang.String vifname3;
    private java.lang.String vifname4;
    private java.lang.String vifname5;
    private java.lang.String vifname6;
    private java.lang.Integer iiftype;
    private nc.vo.pub.lang.UFDouble vfilename;
    private java.lang.Integer ifiletype;
    private java.lang.Integer iifdot;
    private java.lang.Integer iifkilobit;
    private java.lang.Integer iifseparator;
    private java.lang.Integer iseparator;
    private java.lang.Integer iiftop;
    private java.lang.Integer toplineposition;
    private java.lang.Integer toplinenum;
    
    // Add an extra flag line start
    private java.lang.Integer iiftop2;
    private java.lang.Integer toplineposition2;
    private java.lang.Integer toplinenum2;
    // Add an extra flag line end
    
    private java.lang.String vmemo;
    private java.lang.String operatorid;
    private java.lang.String vtable;
    private java.lang.String vcol;
    private java.lang.Integer iifcaret;
    private java.lang.String classid;
    private java.lang.String cyear;
    private java.lang.String cperiod;
    private java.lang.Integer idefault;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.Integer iouthead;
    private java.lang.Integer iheadadjustbody;
    private nc.vo.pub.lang.UFDate date1;
    private nc.vo.pub.lang.UFDate date2;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    
    public static final String PK_DATAIO_INTFACE = "pk_dataio_intface";
    public static final String PK_BANKDOC = "pk_bankdoc";
    public static final String VIFNAME = "vifname";
    public static final String VIFNAME2 = "vifname2";
    public static final String VIFNAME3 = "vifname3";
    public static final String VIFNAME4 = "vifname4";
    public static final String VIFNAME5 = "vifname5";
    public static final String VIFNAME6 = "vifname6";
    public static final String IIFTYPE = "iiftype";
    public static final String VFILENAME = "vfilename";
    public static final String IFILETYPE = "ifiletype";
    public static final String IIFDOT = "iifdot";
    public static final String IIFKILOBIT = "iifkilobit";
    public static final String IIFSEPARATOR = "iifseparator";
    public static final String ISEPARATOR = "iseparator";
    public static final String IIFTOP = "iiftop";
    public static final String TOPLINEPOSITION = "toplineposition";
    public static final String TOPLINENUM = "toplinenum";
    public static final String VMEMO = "vmemo";
    public static final String OPERATORID = "operatorid";
    public static final String VTABLE = "vtable";
    public static final String VCOL = "vcol";
    public static final String IIFCARET = "iifcaret";
    public static final String CLASSID = "classid";
    public static final String CYEAR = "cyear";
    public static final String CPERIOD = "cperiod";
    public static final String IDEFAULT = "idefault";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String IOUTHEAD = "iouthead";
    public static final String IHEADADJUSTBODY = "iheadadjustbody";
    public static final String DATE1 = "date1";
    public static final String DATE2 = "date2";
    
    /**
     * <p>
     * 返回表名称.
     * <p>
     * 创建日期:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_dataio_intface";
    }
    
    /**
     * 按照默认方式创建构造子.
     * 创建日期:
     */
    public HrIntfaceVO()
    {
        super();
    }
    
    /**
     * 属性classid的Getter方法.属性名：薪资方案
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getClassid()
    {
        return classid;
    }
    
    /**
     * 属性cperiod的Getter方法.属性名：薪资月度
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getCperiod()
    {
        return cperiod;
    }
    
    /**
     * 属性cyear的Getter方法.属性名：薪资年度
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getCyear()
    {
        return cyear;
    }
    
    /**
     * 属性date1的Getter方法.属性名：扩展3
     * 创建日期:
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getDate1()
    {
        return date1;
    }
    
    /**
     * 属性date2的Getter方法.属性名：扩展4
     * 创建日期:
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getDate2()
    {
        return date2;
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
     * 属性idefault的Getter方法.属性名：默认接口
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIdefault()
    {
        return idefault;
    }
    
    /**
     * 属性ifiletype的Getter方法.属性名：外部文件类型
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfiletype()
    {
        return ifiletype;
    }
    
    /**
     * 属性iheadadjustbody的Getter方法.属性名：表头表体一致
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIheadadjustbody()
    {
        return iheadadjustbody;
    }
    
    /**
     * 属性iifcaret的Getter方法.属性名：需要补位符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifcaret()
    {
        return iifcaret;
    }
    
    /**
     * 属性iifdot的Getter方法.属性名：需要小数点
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifdot()
    {
        return iifdot;
    }
    
    /**
     * 属性iifkilobit的Getter方法.属性名：需要千位分隔符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifkilobit()
    {
        return iifkilobit;
    }
    
    /**
     * 属性iifseparator的Getter方法.属性名：需要项目分隔符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifseparator()
    {
        return iifseparator;
    }
    
    /**
     * 属性iiftop的Getter方法.属性名：有标志行
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiftop()
    {
        return iiftop;
    }
    
    /**
     * 属性iiftype的Getter方法.属性名：接口类型
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiftype()
    {
        return iiftype;
    }
    
    /**
     * 属性iouthead的Getter方法.属性名：输出表头
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIouthead()
    {
        return iouthead;
    }
    
    /**
     * 属性iseparator的Getter方法.属性名：项目分隔符
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIseparator()
    {
        return iseparator;
    }
    
    /**
     * 属性operatorid的Getter方法.属性名：操作员
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getOperatorid()
    {
        return operatorid;
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
        return null;
    }
    
    /**
     * 属性pk_bankdoc的Getter方法.属性名：报送银行
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_bankdoc()
    {
        return pk_bankdoc;
    }
    
    /**
     * 属性pk_dataio_intface的Getter方法.属性名：接口设置表主键
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_dataio_intface()
    {
        return pk_dataio_intface;
    }
    
    /**
     * 属性pk_group的Getter方法.属性名：所属集团
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_group()
    {
        return pk_group;
    }
    
    /**
     * 属性pk_org的Getter方法.属性名：所属组织
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getPk_org()
    {
        return pk_org;
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
        return "pk_dataio_intface";
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
        return "hr_dataio_intface";
    }
    
    /**
     * 属性toplinenum的Getter方法.属性名：标志行输出
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getToplinenum()
    {
        return toplinenum;
    }
    
    /**
     * 属性toplineposition的Getter方法.属性名：标志行位置
     * 创建日期:
     * @return java.lang.Integer
     */
    public java.lang.Integer getToplineposition()
    {
        return toplineposition;
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
     * 属性vcol的Getter方法.属性名：关联项目
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVcol()
    {
        return vcol;
    }
    
    /**
     * 属性vfilename的Getter方法.属性名：外部文件名称
     * 创建日期:
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getVfilename()
    {
        return vfilename;
    }
    
    /**
     * 属性vifname的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname()
    {
        return vifname;
    }
    
    /**
     * 属性vifname2的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname2()
    {
        return vifname2;
    }
    
    /**
     * 属性vifname3的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname3()
    {
        return vifname3;
    }
    
    /**
     * 属性vifname4的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname4()
    {
        return vifname4;
    }
    
    /**
     * 属性vifname5的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname5()
    {
        return vifname5;
    }
    
    /**
     * 属性vifname6的Getter方法.属性名：$map.displayName
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVifname6()
    {
        return vifname6;
    }
    
    /**
     * 属性vmemo的Getter方法.属性名：备注
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVmemo()
    {
        return vmemo;
    }
    
    /**
     * 属性vtable的Getter方法.属性名：关联表
     * 创建日期:
     * @return java.lang.String
     */
    public java.lang.String getVtable()
    {
        return vtable;
    }
    
    /**
     * 属性classid的Setter方法.属性名：薪资方案
     * 创建日期:
     * @param newClassid java.lang.String
     */
    public void setClassid(java.lang.String newClassid)
    {
        this.classid = newClassid;
    }
    
    /**
     * 属性cperiod的Setter方法.属性名：薪资月度
     * 创建日期:
     * @param newCperiod java.lang.String
     */
    public void setCperiod(java.lang.String newCperiod)
    {
        this.cperiod = newCperiod;
    }
    
    /**
     * 属性cyear的Setter方法.属性名：薪资年度
     * 创建日期:
     * @param newCyear java.lang.String
     */
    public void setCyear(java.lang.String newCyear)
    {
        this.cyear = newCyear;
    }
    
    /**
     * 属性date1的Setter方法.属性名：扩展3
     * 创建日期:
     * @param newDate1 nc.vo.pub.lang.UFDate
     */
    public void setDate1(nc.vo.pub.lang.UFDate newDate1)
    {
        this.date1 = newDate1;
    }
    
    /**
     * 属性date2的Setter方法.属性名：扩展4
     * 创建日期:
     * @param newDate2 nc.vo.pub.lang.UFDate
     */
    public void setDate2(nc.vo.pub.lang.UFDate newDate2)
    {
        this.date2 = newDate2;
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
     * 属性idefault的Setter方法.属性名：默认接口
     * 创建日期:
     * @param newIdefault java.lang.Integer
     */
    public void setIdefault(java.lang.Integer newIdefault)
    {
        this.idefault = newIdefault;
    }
    
    /**
     * 属性ifiletype的Setter方法.属性名：外部文件类型
     * 创建日期:
     * @param newIfiletype java.lang.Integer
     */
    public void setIfiletype(java.lang.Integer newIfiletype)
    {
        this.ifiletype = newIfiletype;
    }
    
    /**
     * 属性iheadadjustbody的Setter方法.属性名：表头表体一致
     * 创建日期:
     * @param newIheadadjustbody java.lang.Integer
     */
    public void setIheadadjustbody(java.lang.Integer newIheadadjustbody)
    {
        this.iheadadjustbody = newIheadadjustbody;
    }
    
    /**
     * 属性iifcaret的Setter方法.属性名：需要补位符
     * 创建日期:
     * @param newIifcaret java.lang.Integer
     */
    public void setIifcaret(java.lang.Integer newIifcaret)
    {
        this.iifcaret = newIifcaret;
    }
    
    /**
     * 属性iifdot的Setter方法.属性名：需要小数点
     * 创建日期:
     * @param newIifdot java.lang.Integer
     */
    public void setIifdot(java.lang.Integer newIifdot)
    {
        this.iifdot = newIifdot;
    }
    
    /**
     * 属性iifkilobit的Setter方法.属性名：需要千位分隔符
     * 创建日期:
     * @param newIifkilobit java.lang.Integer
     */
    public void setIifkilobit(java.lang.Integer newIifkilobit)
    {
        this.iifkilobit = newIifkilobit;
    }
    
    /**
     * 属性iifseparator的Setter方法.属性名：需要项目分隔符
     * 创建日期:
     * @param newIifseparator java.lang.Integer
     */
    public void setIifseparator(java.lang.Integer newIifseparator)
    {
        this.iifseparator = newIifseparator;
    }
    
    /**
     * 属性iiftop的Setter方法.属性名：有标志行
     * 创建日期:
     * @param newIiftop java.lang.Integer
     */
    public void setIiftop(java.lang.Integer newIiftop)
    {
        this.iiftop = newIiftop;
    }
    
    /**
     * 属性iiftype的Setter方法.属性名：接口类型
     * 创建日期:
     * @param newIiftype java.lang.Integer
     */
    public void setIiftype(java.lang.Integer newIiftype)
    {
        this.iiftype = newIiftype;
    }
    
    /**
     * 属性iouthead的Setter方法.属性名：输出表头
     * 创建日期:
     * @param newIouthead java.lang.Integer
     */
    public void setIouthead(java.lang.Integer newIouthead)
    {
        this.iouthead = newIouthead;
    }
    
    /**
     * 属性iseparator的Setter方法.属性名：项目分隔符
     * 创建日期:
     * @param newIseparator java.lang.Integer
     */
    public void setIseparator(java.lang.Integer newIseparator)
    {
        this.iseparator = newIseparator;
    }
    
    /**
     * 属性operatorid的Setter方法.属性名：操作员
     * 创建日期:
     * @param newOperatorid java.lang.String
     */
    public void setOperatorid(java.lang.String newOperatorid)
    {
        this.operatorid = newOperatorid;
    }
    
    /**
     * 属性pk_bankdoc的Setter方法.属性名：报送银行
     * 创建日期:
     * @param newPk_bankdoc java.lang.String
     */
    public void setPk_bankdoc(java.lang.String newPk_bankdoc)
    {
        this.pk_bankdoc = newPk_bankdoc;
    }
    
    /**
     * 属性pk_dataio_intface的Setter方法.属性名：接口设置表主键
     * 创建日期:
     * @param newPk_dataio_intface java.lang.String
     */
    public void setPk_dataio_intface(java.lang.String newPk_dataio_intface)
    {
        this.pk_dataio_intface = newPk_dataio_intface;
    }
    
    /**
     * 属性pk_group的Setter方法.属性名：所属集团
     * 创建日期:
     * @param newPk_group java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group)
    {
        this.pk_group = newPk_group;
    }
    
    /**
     * 属性pk_org的Setter方法.属性名：所属组织
     * 创建日期:
     * @param newPk_org java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org)
    {
        this.pk_org = newPk_org;
    }
    
    /**
     * 属性toplinenum的Setter方法.属性名：标志行输出
     * 创建日期:
     * @param newToplinenum java.lang.Integer
     */
    public void setToplinenum(java.lang.Integer newToplinenum)
    {
        this.toplinenum = newToplinenum;
    }
    
    /**
     * 属性toplineposition的Setter方法.属性名：标志行位置
     * 创建日期:
     * @param newToplineposition java.lang.Integer
     */
    public void setToplineposition(java.lang.Integer newToplineposition)
    {
        this.toplineposition = newToplineposition;
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
     * 属性vcol的Setter方法.属性名：关联项目
     * 创建日期:
     * @param newVcol java.lang.String
     */
    public void setVcol(java.lang.String newVcol)
    {
        this.vcol = newVcol;
    }
    
    /**
     * 属性vfilename的Setter方法.属性名：外部文件名称
     * 创建日期:
     * @param newVfilename nc.vo.pub.lang.UFDouble
     */
    public void setVfilename(nc.vo.pub.lang.UFDouble newVfilename)
    {
        this.vfilename = newVfilename;
    }
    
    /**
     * 属性vifname的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname java.lang.String
     */
    public void setVifname(java.lang.String newVifname)
    {
        this.vifname = newVifname;
    }
    
    /**
     * 属性vifname2的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname2 java.lang.String
     */
    public void setVifname2(java.lang.String newVifname2)
    {
        this.vifname2 = newVifname2;
    }
    
    /**
     * 属性vifname3的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname3 java.lang.String
     */
    public void setVifname3(java.lang.String newVifname3)
    {
        this.vifname3 = newVifname3;
    }
    
    /**
     * 属性vifname4的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname4 java.lang.String
     */
    public void setVifname4(java.lang.String newVifname4)
    {
        this.vifname4 = newVifname4;
    }
    
    /**
     * 属性vifname5的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname5 java.lang.String
     */
    public void setVifname5(java.lang.String newVifname5)
    {
        this.vifname5 = newVifname5;
    }
    
    /**
     * 属性vifname6的Setter方法.属性名：$map.displayName
     * 创建日期:
     * @param newVifname6 java.lang.String
     */
    public void setVifname6(java.lang.String newVifname6)
    {
        this.vifname6 = newVifname6;
    }
    
    /**
     * 属性vmemo的Setter方法.属性名：备注
     * 创建日期:
     * @param newVmemo java.lang.String
     */
    public void setVmemo(java.lang.String newVmemo)
    {
        this.vmemo = newVmemo;
    }
    
    /**
     * 属性vtable的Setter方法.属性名：关联表
     * 创建日期:
     * @param newVtable java.lang.String
     */
    public void setVtable(java.lang.String newVtable)
    {
        this.vtable = newVtable;
    }


    
    // Add getter setter for Bank Upload start
	public java.lang.Integer getIiftop2() {
		return iiftop2;
	}

	public void setIiftop2(java.lang.Integer iiftop2) {
		this.iiftop2 = iiftop2;
	}

	public java.lang.Integer getToplineposition2() {
		return toplineposition2;
	}

	public void setToplineposition2(java.lang.Integer toplineposition2) {
		this.toplineposition2 = toplineposition2;
	}

	public java.lang.Integer getToplinenum2() {
		return toplinenum2;
	}

	public void setToplinenum2(java.lang.Integer toplinenum2) {
		this.toplinenum2 = toplinenum2;
	}
	// Add getter setter for Bank Upload end
}
