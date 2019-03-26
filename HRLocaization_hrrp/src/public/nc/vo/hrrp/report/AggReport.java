package nc.vo.hrrp.report;

import nc.itf.hrrp.service.BillType;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrrp.report.Report")
@BillType(billType = "PDFR")
public class AggReport extends AbstractBill {

  /**
	 * 
	 */
	private static final long serialVersionUID = -5365335645656286555L;

@Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggReportMeta.class);
    return billMeta;
  }

  @Override
  public Report getParentVO() {
    return (Report) this.getParent();
  }
}