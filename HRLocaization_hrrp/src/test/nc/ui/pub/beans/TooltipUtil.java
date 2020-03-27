package nc.ui.pub.beans;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import nc.ui.pub.print.version55.util.PTStringUtil;

/**
 * 提示的工具类
 * 
 * @author hxr Created on 2004-12-28
 */
public class TooltipUtil {

	private static final int LABEL_DEFAULT = 0;
	private static final int LABEL_COMBOBOX = 1;
	private static final int LABEL_LABEL_COMBO_POPMENU = 2;

	static String getTip4Combo(JLabel c, String sTipText) {
		return getTip4Label(c, sTipText, LABEL_COMBOBOX, null);
	}

	public static String getTip4Label(JLabel c, String sTipText) {

		return getTip4Label(c, sTipText, LABEL_DEFAULT, null);
	}

	static String getTip4ComboPopMenu(JLabel c, String sTipText,
			UIComboBox combo) {

		return getTip4Label(c, sTipText, LABEL_LABEL_COMBO_POPMENU, combo);
	}

	/**
	 * 返回标签Label的提示 算法：见getTip
	 * 
	 * @param label
	 *            标签
	 * @param sTipText
	 *            提示文
	 * @return
	 */
	private static String getTip4Label(JLabel c, String sTipText, int type,
			Component ancestor) {
		//add by weiningc 20191220 start
		if (1==1)
			return "ssssssssssssssbbbbbbbbb";
		//end
		if (sTipText != null && !sTipText.equals(c.getText()))
			return sTipText;

		JLabel label = (JLabel) c;
		String text = label.getText();
		Icon icon = (label.isEnabled()) ? label.getIcon() : label
				.getDisabledIcon();

		if ((icon == null) && (text == null)) {
			return null;
		}

		Insets paintViewInsets = new Insets(0, 0, 0, 0);
		Rectangle paintIconR = new Rectangle();
		Rectangle paintViewR = new Rectangle();
		Rectangle paintTextR = new Rectangle();

		FontMetrics fm = label.getFontMetrics(label.getFont());
		Insets insets = c.getInsets(paintViewInsets);

		paintViewR.x = insets.left;
		paintViewR.y = insets.top;

		if (type == LABEL_COMBOBOX) {
			paintViewR.width = -c.getX() - (insets.left + insets.right);
			paintViewR.height = -c.getY() - (insets.top + insets.bottom);
		} else if (type == LABEL_LABEL_COMBO_POPMENU) {
			if (ancestor != null) {
				paintViewR.width = ancestor.getWidth()
						- (insets.left + insets.right);
				paintViewR.height = ancestor.getHeight()
						- (insets.top + insets.bottom);
				if (ancestor instanceof UIComboBox) {
					int rows = ((UIComboBox) ancestor).getItemCount();
					int row = ((UIComboBox) ancestor).getMaximumRowCount();
					if (rows > row) {
						JScrollPane sp = (JScrollPane) SwingUtilities
								.getAncestorOfClass(JScrollPane.class, c);
						if (sp != null && sp.getVerticalScrollBar() != null) {
							paintViewR.width -= sp.getVerticalScrollBar()
									.getWidth();
						}
					}
				}
			}
		} else {
			paintViewR.width = c.getWidth() - (insets.left + insets.right);
			paintViewR.height = c.getHeight() - (insets.top + insets.bottom);
		}

		paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
		paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

		String clippedText = layoutCL(label, fm, text, icon, paintViewR,
				paintIconR, paintTextR);

		if (clippedText != null && !clippedText.equals(label.getText()))
			return label.getText();
		else
			return null;

	}

	private static String layoutCL(JLabel label, FontMetrics fontMetrics,
			String text, Icon icon, Rectangle viewR, Rectangle iconR,
			Rectangle textR) {

		return SwingUtilities.layoutCompoundLabel((JComponent) label,
				fontMetrics, text, icon, label.getVerticalAlignment(),
				label.getHorizontalAlignment(),
				label.getVerticalTextPosition(),
				label.getHorizontalTextPosition(), viewR, iconR, textR,
				label.getIconTextGap());
	}

	/**
	 * 返回按钮AbstractButton的提示 算法：见getTip
	 * 
	 * @param button
	 *            AbstractButton抽象按钮
	 * @param sTipText
	 *            提示文本
	 * @return
	 */
	public static String getTip4Button(AbstractButton button, String sTipText) {

		if (sTipText != null && !sTipText.equals(button.getText()))
			return sTipText;

		AbstractButton c = button;
		AbstractButton b = (AbstractButton) c;

		Dimension size = c.getSize();

		FontMetrics fm = c.getFontMetrics(c.getFont());

		Rectangle viewRect = new Rectangle(size);
		Rectangle iconRect = new Rectangle();
		Rectangle textRect = new Rectangle();

		Insets i = c.getInsets();
		viewRect.x += i.left;
		viewRect.y += i.top;
		viewRect.width -= (i.right + viewRect.x);
		viewRect.height -= (i.bottom + viewRect.y);

		Icon altIcon = b.getIcon();

		if (altIcon == null && b.getUI() instanceof BasicRadioButtonUI) {
			altIcon = ((BasicRadioButtonUI) b.getUI()).getDefaultIcon();
		}

		String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
				altIcon, b.getVerticalAlignment(), b.getHorizontalAlignment(),
				b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
				viewRect, iconRect, textRect, b.getIconTextGap());

		if (text != null && !text.equals(button.getText()))
			return button.getText();
		else
			return null;

	}

	public static String getComposedText(String value,JComponent comp) {
		String retValue = value;
		if (retValue == null) {
			return null;
		}
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.FontMetrics fm = comp.getFontMetrics(comp.getFont());
//		del chenth 20180612 不需要校验长度，如果检验的话，长度没超过，但有换行的情况，显示时就没有效果了。
//		int tatolLen = fm.stringWidth(value);
//		if (tatolLen < size.width) {
//			return retValue;
//		}
		// 按汉字计算长度
		int len = fm.stringWidth("中");/* -=notranslate=- */
		int num = size.width / len;
		
		//update chenth 20170713 Tips支持换行显示
		List<String> rows = PTStringUtil.getConentOfLineSeparators(value);
		
		StringBuffer st = new StringBuffer();
		st.append("<html><body>");
		for(String row : rows){
			char[] c = row.toCharArray();
			int count = c.length / num + 1;
			int index = 0;
			
			for (int i = 0; i < count; i++) {
				if (i == count - 1) {
					st.append(row.substring(index, row.length()));
				} else {
					st.append(row.substring(index, index + num));
					st.append("<br>");
					index += num;
				}
			}
			st.append("<br>");
		}
		st.append("</body></html>");
		retValue = st.toString();
		return retValue;
	}

}