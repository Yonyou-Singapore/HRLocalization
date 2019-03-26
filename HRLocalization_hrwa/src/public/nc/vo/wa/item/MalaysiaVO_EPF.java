package nc.vo.wa.item;

import java.io.Serializable;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 * EPF¼ÆËãº¯ÊýVO
 * @author Ethan Wu
 * @since 2019-01-02
 */
public class MalaysiaVO_EPF extends SuperVO implements Serializable {

	/**
	 * System auto generated serial ID
	 */
	private static final long serialVersionUID = -2469378086660630880L;
	
	

	private String pk_cacu_data;
	private String pk_epfgroup;
	private UFBoolean my_isvoluntaryepf;
	private UFDouble my_nepfrate_employee;
	private String pk_wa_class;
	
	


	public String getPk_cacu_data() {
		return pk_cacu_data;
	}

	public void setPk_cacu_data(String pk_cacu_data) {
		this.pk_cacu_data = pk_cacu_data;
	}

	public String getPk_epfgroup() {
		return pk_epfgroup;
	}

	public void setPk_epfgroup(String pk_epfgroup) {
		this.pk_epfgroup = pk_epfgroup;
	}

	public UFBoolean getMy_isvoluntaryepf() {
		return my_isvoluntaryepf;
	}

	public void setMy_isvoluntaryepf(UFBoolean my_isvoluntaryepf) {
		this.my_isvoluntaryepf = my_isvoluntaryepf;
	}

	public UFDouble getMy_nepfrate_employee() {
		return my_nepfrate_employee;
	}

	public void setMy_nepfrate_employee(UFDouble my_nepfrate_employee) {
		this.my_nepfrate_employee = my_nepfrate_employee;
	}
	
	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public String getTableName() {
		return constructTableName();
	}
	
	public static String constructTableName() {
		String tableName = " (select  " +
				" a.pk_cacu_data pk_cacu_data, " +
				" def.pk_defdoc pk_epfgroup, " +
				" c.my_isvoluntaryepf my_isvoluntaryepf, " +
				" c.my_nepfrate_employee my_nepfrate_employee, " +
				" wa_data.pk_wa_class pk_wa_class " +
				" from wa_cacu_data a " +
				" inner join wa_data wa_data on wa_data.pk_wa_data = a.pk_wa_data " +
				" inner join bd_psndoc c on c.pk_psndoc = a.pk_psndoc " +
				" left join bd_defdoc def on def.pk_defdoc = c.my_epfgroup) ";
		return tableName;
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
