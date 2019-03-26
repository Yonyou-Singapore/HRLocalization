package nc.vo.hrrp.dr;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggRpderatevoMeta extends AbstractBillMeta {
  public AggRpderatevoMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.hrrp.dr.Rpderatevo.class);
  }
}