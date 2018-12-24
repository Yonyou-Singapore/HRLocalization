package nc.vo.wa.item;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nc.vo.pub.lang.UFDouble;

/**
 * Maysia PCB��EPF�ȵ� ���ȴ���Ƚ����⣬
 * 1,2,3,4��λΪ5�� 6,7,8,9��λΪ10���� 2.22Ϊ2.25�� 2.26Ϊ2.30
 * @author weiningc
 *
 */
public class UFDoubleScaleUtils extends UFDouble {
	
	/**
	 * 1.12,1.11 = 1.10, 1.13,1.14 = 1.15 12��λ�� 34��λ 89��λ�� 67��λ
	 * @param value
	 * @param mode
	 * @return
	 */
	public static UFDouble setScaleForSpecial(UFDouble value, RoundingMode mode) {
		BigDecimal bd = value.toBigDecimal();
		BigDecimal deciaml = new UFDouble(0.05).toBigDecimal();
		BigDecimal scaled = deciaml.signum()==0 ? bd :
			(bd.divide(deciaml,0,mode)).multiply(deciaml);
		return new UFDouble(scaled);
	}
	
	/**
	 * 
	 * @param value
	 * @param mode
	 * 1.11->1.15 ,1.14->1.15, 1.16->1.20 ȫ����λ
	 * @return
	 */
	public static UFDouble setScaleForSpecial2(UFDouble value) {
		BigDecimal bd = value.toBigDecimal();
		if(bd == null || BigDecimal.ZERO.compareTo(bd) == 1){
			return new UFDouble(BigDecimal.ZERO.setScale(2));
		}
		bd = bd.setScale(2,UFDouble.ROUND_FLOOR);
		String strBd = bd.toString();
		String strLastBit = strBd.substring(strBd.length()-1,strBd.length());
		BigDecimal bdTemp = BigDecimal.ZERO;
		bdTemp.setScale(2);
		int iLastBit = Integer.valueOf(strLastBit);
		if(iLastBit%5 != 0){
			bdTemp = BigDecimal.valueOf(iLastBit).divide(BigDecimal.valueOf(100));
			bdTemp = bdTemp.multiply(BigDecimal.valueOf(-1));
			bdTemp = bdTemp.add(iLastBit>5 ? new BigDecimal("0.1") : new BigDecimal("0.05"));
		}
		bd = bd.add(bdTemp);
		return new UFDouble(bd);
	}
	
}
