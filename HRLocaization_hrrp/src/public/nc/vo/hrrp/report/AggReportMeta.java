package nc.vo.hrrp.report;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggReportMeta extends AbstractBillMeta {
  public AggReportMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.hrrp.report.Report.class);
    this.addChildren(nc.vo.hrrp.report.Field.class);
  }
}