package nc.vo.hrrp.dr;

import nc.itf.hrrp.service.BillType;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrrp.dr.Rpderatevo")
@BillType(billType = "RPDR")
public class AggRpderatevo extends AbstractBill {

  /**
	 * 
	 */
	private static final long serialVersionUID = 6424801565701727161L;

@Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggRpderatevoMeta.class);
    return billMeta;
  }

  @Override
  public Rpderatevo getParentVO() {
    return (Rpderatevo) this.getParent();
  }
}