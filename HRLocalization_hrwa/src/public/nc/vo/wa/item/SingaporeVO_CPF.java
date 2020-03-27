package nc.vo.wa.item;

import java.io.Serializable;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * Singapore CPF
 * @author weiningc
 *
 */
public class SingaporeVO_CPF extends SuperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//薪资方案
	private String pk_wa_class;
	
	//临时表主键
	private String pk_cacu_data;
	
	//公司性质  由公式传过来 如private company或者public
	private String companymode;
	
	//缴纳方式 FG 或者 GG
	private String fullemployercpf;
	
	//PR审批时间
	private UFDate pr_approvaldate;
	
	//ID Type 例如是PR 或者 公民
	private String idtype;
	
	//是否PR
	private UFBoolean sgpr;
	
	//出生日期  
	private UFDate birthdate;
	
	//OW
	private UFDouble ow;
	
	//AW
	private UFDouble aw;
	
	//tw
	private UFDouble tw;
	
	//NPE
	private UFDouble npe;
	
	//aw ceilling
	private UFDouble awceilling;
	
	//人员code
	private String psncode;
	
	//人员状态 若是离职,需要特殊处理，使用的AW Ceilling不同
	private UFBoolean endflag;
	

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public String getPk_cacu_data() {
		return pk_cacu_data;
	}

	public void setPk_cacu_data(String pk_cacu_data) {
		this.pk_cacu_data = pk_cacu_data;
	}

	public String getCompanymode() {
		return companymode;
	}

	public void setCompanymode(String companymode) {
		this.companymode = companymode;
	}


	public UFDate getPr_approvaldate() {
		return pr_approvaldate;
	}

	public void setPr_approvaldate(UFDate pr_approvaldate) {
		this.pr_approvaldate = pr_approvaldate;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public UFDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(UFDate birthdate) {
		this.birthdate = birthdate;
	}

	public UFDouble getOw() {
		return ow;
	}

	public void setOw(UFDouble ow) {
		this.ow = ow;
	}

	public UFDouble getAw() {
		return aw;
	}

	public void setAw(UFDouble aw) {
		this.aw = aw;
	}

	public UFDouble getTw() {
		return tw;
	}

	public void setTw(UFDouble tw) {
		this.tw = tw;
	}
	
	public String getFullemployercpf() {
		return fullemployercpf;
	}

	public void setFullemployercpf(String fullemployercpf) {
		this.fullemployercpf = fullemployercpf;
	}
	
	

	public UFBoolean getSgpr() {
		return sgpr;
	}

	public void setSgpr(UFBoolean sgpr) {
		this.sgpr = sgpr;
	}
	
	public UFDouble getNpe() {
		return npe;
	}

	public void setNpe(UFDouble npe) {
		this.npe = npe;
	}

	public UFDouble getAwceilling() {
		return awceilling;
	}

	public void setAwceilling(UFDouble awceilling) {
		this.awceilling = awceilling;
	}
	
	public String getPsncode() {
		return psncode;
	}
	
	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public UFBoolean getEndflag() {
		return endflag;
	}

	public void setEndflag(UFBoolean endflag) {
		this.endflag = endflag;
	}

	@Override
	public void setPrimaryKey(String pk_cacu_data) {
		this.pk_cacu_data = pk_cacu_data;
	}
	

	@Override
	public String getPrimaryKey() {
		return this.pk_cacu_data;
	}
	
	@Override
	public String getTableName() {
		StringBuffer sb = new StringBuffer();
		sb.append("(select ");
		sb.append(" c.pk_wa_class        pk_wa_class,");
		sb.append(" c.pk_cacu_data       pk_cacu_data,");
		sb.append(" null as              companymode,");
		sb.append(" case p.sg_fullemployercpf when 'Y' then 'FG'");
		sb.append(" else 'GG' end fullemployercpf, ");//FG和GG
		sb.append(" p.sg_dprapprovaldate pr_approvaldate,");
		sb.append(" def.code          idtype,");
		sb.append(" p.sg_ispr            sgpr,");
		sb.append(" p.birthdate          birthdate,");
		sb.append(" w.f_1067              ow,");//capping后的ow
		sb.append(" w.f_1001              aw,");//这里也是取capping后的aw TODO
		sb.append(" w.f_1011              tw,");
		sb.append(" w.f_1003			  npe,");
		sb.append(" w.f_1002			  awceilling,");
		sb.append(" p.code 			   psncode,");
		sb.append(" po.endflag		   endflag");
		sb.append(" from wa_cacu_data c");
		sb.append(" inner join wa_data w");
		sb.append(" on c.pk_cacu_data = w.pk_wa_data");
		sb.append(" inner join bd_psndoc p");
		sb.append(" on p.pk_psndoc = c.pk_psndoc");
		sb.append(" inner join hi_psnorg po on po.pk_psndoc = p.pk_psndoc");
		//id type使用标准字段idtype 
		sb.append(" left join bd_psnidtype def on def.pk_identitype = p.idtype)");
		return sb.toString();
	}
	
}
