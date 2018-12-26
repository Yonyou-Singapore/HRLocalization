package nc.ui.hrrp.ds.model;

import nc.itf.hrrp.service.BaseServiceProxy;
import nc.ui.pubapp.uif2app.model.BillManageModel;

/**
 * <p>Title: PtBillManageModel</P>
 * <p>Description: </p>
 * @author 
 * @version 1.0
 * @since 2014-10-12
 */
public class DrManageModel extends BillManageModel {
	private String mutualType;
	private BaseServiceProxy ptProxy;
	public String getMutualType() {
		return mutualType;
	}

	public void setMutualType(String mutualType) {
		this.mutualType = mutualType;
	}

	/**
	 * @return the questProxy
	 */
	public BaseServiceProxy getPtProxy() {
		return ptProxy;
	}

	/**
	 * @param questProxy the questProxy to set
	 */
	public void setPtProxy(BaseServiceProxy ptProxy) {
		this.ptProxy = ptProxy;
	}
}
