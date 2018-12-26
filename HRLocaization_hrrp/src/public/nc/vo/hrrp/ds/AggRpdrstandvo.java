package nc.vo.hrrp.ds;

import nc.itf.hrrp.service.BillType;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrrp.ds.Rpdrstandvo")
@BillType(billType = "RPDS")
public class AggRpdrstandvo extends AbstractBill {

  /**
	 * 
	 */
	private static final long serialVersionUID = -4480009768201877843L;

@Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggRpdrstandvoMeta.class);
    return billMeta;
  }

  @Override
  public Rpdrstandvo getParentVO() {
    return (Rpdrstandvo) this.getParent();
  }
}