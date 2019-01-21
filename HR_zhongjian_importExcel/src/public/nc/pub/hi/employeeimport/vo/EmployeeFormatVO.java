package nc.pub.hi.employeeimport.vo;

import java.io.Serializable;

import nc.vo.pub.SuperVO;

/**
 * CCDC�����ʽVO
 * @author weiningc
 *
 */
public class EmployeeFormatVO extends SuperVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * CCDC excel ���������Զ�����nameΪ�̶�ֵ
	 */
	public static final String DEFDOCLISTCODE = "HRHIEXCELIMPORT_MAPPING";
	
	//tablename
	private String tablecode;
	//classname
	private String fullclassname;
	//Ԫ����
	private String metacode;
	//detailԪ����
	private String metaitemcode;
	//attrcode
	private String itemcode;
	//data style �����...
	private Integer datastyle;
	//data type ��UFDate, UFDouble, String...
	private Integer datatype;
	
	//defname �Զ�����������
	private String defname;
	
	//deflistcode �Զ����� bd_defdoclist���� 
	private String deflistcode;
	
	public String getTablecode() {
		return tablecode;
	}
	public void setTablecode(String tablecode) {
		this.tablecode = tablecode;
	}
	public String getFullclassname() {
		return fullclassname;
	}
	public void setFullclassname(String fullclassname) {
		this.fullclassname = fullclassname;
	}
	public String getMetacode() {
		return metacode;
	}
	public void setMetacode(String metacode) {
		this.metacode = metacode;
	}
	public String getMetaitemcode() {
		return metaitemcode;
	}
	public void setMetaitemcode(String metaitemcode) {
		this.metaitemcode = metaitemcode;
	}
	
	public String getItemcode() {
		return itemcode;
	}
	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}
	public Integer getDatastyle() {
		return datastyle;
	}
	public void setDatastyle(Integer datastyle) {
		this.datastyle = datastyle;
	}
	public Integer getDatatype() {
		return datatype;
	}
	public void setDatatype(Integer datatype) {
		this.datatype = datatype;
	}
	public String getDefname() {
		return defname;
	}
	public void setDefname(String defname) {
		this.defname = defname;
	}
	public String getDeflistcode() {
		return deflistcode;
	}
	public void setDeflistcode(String deflistcode) {
		this.deflistcode = deflistcode;
	}
	@Override
	public String getTableName() {
		return "(select info.table_code    tablecode,"
       + " info.vo_class_name fullclassname,"
       + " info.meta_data     metacode,"
       + " item.meta_data     metaitemcode,"
       + " item.item_code     itemcode,"
       + " md.datatypestyle   datastyle,"
       + " md.datatype        datatype,"
       + " def.name           defname,"
       + " deflist.code        deflistcode"
       + " from hr_infoset_item item"
       + " inner join hr_infoset info"
       + " on item.pk_infoset = info.pk_infoset"
       + " inner join md_property md"
       + " on md.id = item.id"
       + " inner join bd_defdoc def"
       + " on def.code = item.meta_data"
       + " inner join bd_defdoclist deflist"
       + " on deflist.pk_defdoclist = def.pk_defdoclist)";
	}
	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.metaitemcode;
	}
	@Override
	public void setPrimaryKey(String metaitemcode) {
		// TODO Auto-generated method stub
		super.setPrimaryKey(metaitemcode);
	}
}
