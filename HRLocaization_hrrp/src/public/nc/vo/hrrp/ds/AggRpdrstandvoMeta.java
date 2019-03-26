package nc.vo.hrrp.ds;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggRpdrstandvoMeta extends AbstractBillMeta {
  public AggRpdrstandvoMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.hrrp.ds.Rpdrstandvo.class);
  }
}