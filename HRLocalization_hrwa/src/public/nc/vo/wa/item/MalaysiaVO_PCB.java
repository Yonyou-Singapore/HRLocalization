package nc.vo.wa.item;

import java.io.Serializable;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * PCB计算函数VO
 * @author weiningc
 *
 */
public class MalaysiaVO_PCB extends SuperVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2018112716439900L;
	
	//人员信息属于Resident, Non-resident, return-expert, konwledge?
	public static final String MY_RESIDENT = "01";
	public static final String MY_NONRESIDENT = "02";
	public static final String MY_RETURNEXPER = "03";
	public static final String MY_KNOWLEDGE = "04";
	
	//人员属于哪个扣税类别 1&3？ or2？
	public static final String MY_GROUP1_3 = "01";
	public static final String MY_GROUP2 = "02";
	
	//已有字段
	private UFDouble y1;
	private UFDouble yt;
	private UFDouble k1;
	private UFDouble kt;
	private UFDouble lp1;
	private UFDouble x;
	private UFDouble y;
	private UFDouble k;
	private UFDouble lp;
	private UFDouble z;
	
	private UFDouble children;
	private UFDouble totalpayable;
	private UFDouble totalepf;
	private UFDouble totaltax;
	private UFDouble totalpcb;
	
	private UFBoolean isspouseworking;
	private UFBoolean isdisable;
	private UFBoolean issponsedisable;
	
	//
	private String pcbcategory;
	private String pcbgroup;
	private String pk_wa_class;
	private String creator;
	private String pk_cacu_data;
	
	private UFDate begindate;
	
	public UFDouble getY1() {
		return y1;
	}

	public void setY1(UFDouble y1) {
		this.y1 = y1;
	}

	public UFDouble getYt() {
		return yt;
	}

	public void setYt(UFDouble yt) {
		this.yt = yt;
	}

	public UFDouble getK1() {
		return k1;
	}

	public void setK1(UFDouble k1) {
		this.k1 = k1;
	}

	public UFDouble getKt() {
		return kt;
	}

	public void setKt(UFDouble kt) {
		this.kt = kt;
	}

	public UFDouble getLp1() {
		return lp1;
	}

	public void setLp1(UFDouble lp1) {
		this.lp1 = lp1;
	}

	public UFDouble getX() {
		return x;
	}

	public void setX(UFDouble x) {
		this.x = x;
	}

	public UFDouble getY() {
		return y;
	}

	public void setY(UFDouble y) {
		this.y = y;
	}

	public UFDouble getK() {
		return k;
	}

	public void setK(UFDouble k) {
		this.k = k;
	}

	public UFDouble getLp() {
		return lp;
	}

	public void setLp(UFDouble lp) {
		this.lp = lp;
	}

	public UFDouble getZ() {
		return z;
	}

	public void setZ(UFDouble z) {
		this.z = z;
	}

	public UFDouble getChildren() {
		return children;
	}

	public void setChildren(UFDouble children) {
		this.children = children;
	}

	public UFDouble getTotalpayable() {
		return totalpayable;
	}

	public void setTotalpayable(UFDouble totalpayable) {
		this.totalpayable = totalpayable;
	}

	public UFDouble getTotalepf() {
		return totalepf;
	}

	public void setTotalepf(UFDouble totalepf) {
		this.totalepf = totalepf;
	}

	public UFDouble getTotaltax() {
		return totaltax;
	}

	public void setTotaltax(UFDouble totaltax) {
		this.totaltax = totaltax;
	}

	public UFDouble getTotalpcb() {
		return totalpcb;
	}

	public void setTotalpcb(UFDouble totalpcb) {
		this.totalpcb = totalpcb;
	}

	public UFBoolean getIsspouseworking() {
		return isspouseworking;
	}

	public void setIsspouseworking(UFBoolean isspouseworking) {
		this.isspouseworking = isspouseworking;
	}

	public UFBoolean getIsdisable() {
		return isdisable;
	}

	public void setIsdisable(UFBoolean isdisable) {
		this.isdisable = isdisable;
	}

	public UFBoolean getIssponsedisable() {
		return issponsedisable;
	}

	public void setIssponsedisable(UFBoolean issponsedisable) {
		this.issponsedisable = issponsedisable;
	}

	public String getPcbcategory() {
		return pcbcategory;
	}

	public void setPcbcategory(String pcbcategory) {
		this.pcbcategory = pcbcategory;
	}

	public String getPcbgroup() {
		return pcbgroup;
	}

	public void setPcbgroup(String pcbgroup) {
		this.pcbgroup = pcbgroup;
	}

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPk_cacu_data() {
		return pk_cacu_data;
	}

	public void setPk_cacu_data(String pk_cacu_data) {
		this.pk_cacu_data = pk_cacu_data;
	}

	public UFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(UFDate begindate) {
		this.begindate = begindate;
	}

	@Override
	public String getTableName() {
		return constructTableName();
	}
	
	public static String constructTableName() {
		String tablename = " (select "
			       + " d.f_514 y1,"
			       + " d.f_515 yt,"
			       + " d.f_519 k1,"
			       + " d.f_520 kt,"
			       + " d.f_522 lp1,"
			       + " d.f_529 x,"
			       + " d.f_530 y,"
			       + " d.f_531 k,"
			       + " d.f_532 lp,"
			       + " d.f_533 z,"
			       + " p.my_numberofchildren children,"
			       + " p.my_totalpayable totalpayable,"
			       + " p.my_totalepf totalepf,"
			       + " p.my_taxexemption totaltax,"
			       + " p.my_totalpcb totalpcb,"
			       + " p.my_isspouseworking isspouseworking,"
			       + " p.my_isdisabled isdisable,"
			       + " p.my_isspousedisabled issponsedisable,"
			       + " p.my_category pcbcategory,"
			       + " p.my_pcbgroup         pcbgroup,"
                   + " d.pk_wa_class 		 pk_wa_class,"
                   + " c.creator 			 creator,"
                   + " c.pk_cacu_data        pk_cacu_data,"
                   + " hiorg.begindate       begindate"
			  + " from wa_cacu_data c"
			 + " inner join wa_data d"
			   + "  on c.pk_wa_data = d.pk_wa_data"
			 + " inner join bd_psndoc p"
			    + " on p.pk_psndoc = c.pk_psndoc"
			 + " left join bd_defdoc def"
			    + " on def.pk_defdoc = p.my_category"
			 + " left join hi_psnorg hiorg"
			    + " on hiorg.pk_psndoc = p.pk_psndoc) ";
		return tablename;
	}
	@Override
	public void setPrimaryKey(String pk_cacu_data) {
		this.pk_cacu_data = pk_cacu_data;
	}
	@Override
	public String getPrimaryKey() {
		return this.pk_cacu_data;
	}
}
