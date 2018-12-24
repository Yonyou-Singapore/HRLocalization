package nc.vo.wa.item;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nc.vo.pub.lang.UFDouble;

/**
 * Maysia PCB及EPF等等 精度处理比较奇葩，
 * 1,2,3,4进位为5， 6,7,8,9进位为10，如 2.22为2.25， 2.26为2.30
 * @author weiningc
 *
 */
public class UFDoubleScaleUtils extends UFDouble {
	
	public static UFDouble setScale(UFDouble value, UFDouble rounding, RoundingMode mode) {
		BigDecimal bd = value.toBigDecimal();
		BigDecimal deciaml = rounding.toBigDecimal();
		BigDecimal scaled = deciaml.signum()==0 ? bd :
			(bd.divide(deciaml,0,mode)).multiply(deciaml);
		return new UFDouble(scaled);
	}
	
}
