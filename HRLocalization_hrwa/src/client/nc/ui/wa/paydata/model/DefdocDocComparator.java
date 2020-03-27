package nc.ui.wa.paydata.model;

import java.util.Comparator;

import nc.vo.bd.defdoc.DefdocVO;

public class DefdocDocComparator implements Comparator<DefdocVO> {

	@Override
	public int compare(DefdocVO o1, DefdocVO o2) {
		if(o1.getCode() == null )
			return -1;
		else if(o2.getCode() == null)
			return 1;
		
		return o1.getCode().compareTo(o2.getCode());
	}

}
