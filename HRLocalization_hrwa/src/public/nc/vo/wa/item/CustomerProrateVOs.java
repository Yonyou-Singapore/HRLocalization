package nc.vo.wa.item;

import nc.vo.pub.SuperVO;

public class CustomerProrateVOs extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_wa_class;
	private String pk_wa_item;
	private String pk_psndoc;
	//工作天数类型，如5,5.5,6等等
	private String g_eepaygroup;
	//Prorate类型
	private String sg_prorate;
	
	
	
	public String getPk_wa_item() {
		return pk_wa_item;
	}
	public void setPk_wa_item(String pk_wa_item) {
		this.pk_wa_item = pk_wa_item;
	}
	public String getPk_psndoc() {
		return pk_psndoc;
	}
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}
	public String getG_eepaygroup() {
		return g_eepaygroup;
	}
	public void setG_eepaygroup(String g_eepaygroup) {
		this.g_eepaygroup = g_eepaygroup;
	}
	public String getSg_prorate() {
		return sg_prorate;
	}
	public void setSg_prorate(String sg_prorate) {
		this.sg_prorate = sg_prorate;
	}
	
	public String getPk_wa_class() {
		return pk_wa_class;
	}
	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}
	@Override
	public String getPrimaryKey() {
		return this.pk_wa_item;
	}
	
	@Override
	public String getTableName() {
		StringBuffer sb = new StringBuffer();
		sb.append("(select wc.pk_wa_class, wc.pk_wa_item pk_wa_item, p.pk_psndoc pk_psndoc , wc.sg_prorate sg_prorate, def.code g_eepaygroup");
		sb.append(" from bd_psndoc p inner join  wa_classitem wc on p.pk_group = wc.pk_group left join bd_defdoc def on def.pk_defdoc = p.g_eepaygroup)");
		return sb.toString();
	}
	
}
