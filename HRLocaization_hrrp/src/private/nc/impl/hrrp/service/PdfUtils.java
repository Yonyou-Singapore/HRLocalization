package nc.impl.hrrp.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * <p>Title: PdfUtils</P> <p>Description: </p> 
 * @author 
 * @version 1.0
 * @since 2014-10-14
 */
public class PdfUtils {

	public static final String FONT_ArialUnicodeMS = "ArialUnicodeMS";

	/**
	 * get AcroFields.Item align
	 * 
	 * @param item
	 * @return
	 */
	public static int getItemAlignment(AcroFields.Item item) {
		int res = Element.ALIGN_LEFT;
		try {
			PdfNumber nfl = item.getMerged(0).getAsNumber(PdfName.Q);
			switch (nfl.intValue()) {
			case PdfFormField.Q_CENTER:
				res = Element.ALIGN_CENTER;
				break;
			case PdfFormField.Q_RIGHT:
				res = Element.ALIGN_RIGHT;
				break;
			case PdfFormField.Q_LEFT:
			default:
				res = Element.ALIGN_LEFT;
			}
		} catch (NullPointerException e) {
			res = Element.ALIGN_LEFT;
		} 
			return res;
	}

	public static String getFormType(int type) {
		String name = "unknow type";
		switch (type) {
		case AcroFields.FIELD_TYPE_CHECKBOX:
			name = "Checkbox";
			break;
		case AcroFields.FIELD_TYPE_COMBO:
			name = "Combobox";
			break;
		case AcroFields.FIELD_TYPE_LIST:
			name = "List";
			break;
		case AcroFields.FIELD_TYPE_NONE:
			name = "None";
			break;
		case AcroFields.FIELD_TYPE_PUSHBUTTON:
			name = "Pushbutton";
			break;
		case AcroFields.FIELD_TYPE_RADIOBUTTON:
			name = "Radiobutton";
			break;
		case AcroFields.FIELD_TYPE_SIGNATURE:
			name = "Signature";
			break;
		case AcroFields.FIELD_TYPE_TEXT:
			name = "Text";
			break;
		default:
			name = "unknow type";
		}
		return name;
	}

	public static float getTextSize(AcroFields.Item item) {
		final int DA_SIZE = AcroFields.DA_SIZE;
		PdfString da = item.getMerged(0).getAsString(PdfName.DA);
		Object dab[] = AcroFields.splitDAelements(da.toUnicodeString());
		for (Object object : dab) {
			System.out.println("dab:" + object);

		}
		System.out.println("font:" + dab[AcroFields.DA_FONT]);
		float size = ((Float) dab[DA_SIZE]).floatValue();
		if (size <= 0) {
			size = 9;
		}
		return size;
	}

	public static String getTextFont(AcroFields.Item item) {
		PdfString da = item.getMerged(0).getAsString(PdfName.DA);
		Object dab[] = AcroFields.splitDAelements(da.toUnicodeString());
		return (String) dab[AcroFields.DA_FONT];
	}
	// public static String getTextColor(AcroFields.Item item) {
	// PdfString da = item.getMerged(0).getAsString(PdfName.DA);
	// Object dab[] = AcroFields.splitDAelements(da.toUnicodeString());
	// return (C) dab[AcroFields.DA_COLOR];
	// }
}
