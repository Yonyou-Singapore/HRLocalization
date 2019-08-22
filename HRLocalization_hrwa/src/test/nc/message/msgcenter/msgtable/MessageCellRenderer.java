package nc.message.msgcenter.msgtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import nc.message.label.bs.ui.MessageLabelColorsDelegator;
import nc.message.reconstruction.MessageCenterModel;
import nc.message.reconstruction.MessageCenterUIConst;
import nc.message.vo.MessageLabelColorVOs;
import nc.message.vo.NCMessage;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.style.Style;
import nc.uitheme.ui.ThemeResourceCenter;

public class MessageCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1032344820852708935L;
	
	MessageCenterModel msgmodel = MessageCenterModel.getInstance();
	Map<String,String> labelMap = msgmodel.getLabelMap();
	
	public final ImageIcon iconStateUnDeal = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/email_unread.png");

	public final ImageIcon iconStateDealed = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/email_read.png");

	public final ImageIcon iconProHigh = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/high.png");
	
	public final ImageIcon iconProOrdinary = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/ordinary.png");
	
	public final ImageIcon iconProLow = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/low.png");
	
	public final ImageIcon iconAttach = ThemeResourceCenter.getInstance().getImage("themeres/infocenter/attachment.png");

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel lb = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
		if (column == 0) {//是否选中列
			UICheckBox jbx = new UICheckBox();
			if (value != null && !value.equals("") && ((Boolean) value).booleanValue())
				jbx.setSelected(true);
			else
				jbx.setSelected(false);
			jbx.setHorizontalAlignment(javax.swing.JLabel.CENTER);
			if (isSelected) {
				jbx.setBackground((Color) UIManager.get("Table.selectionBackground"));
			} else {
				jbx.setBackground((Color) UIManager.get("Table.background"));
			}
			return jbx;
		}
		if(column == 1){//状态列
			lb.setHorizontalAlignment(JLabel.CENTER);
			if (value != null && !value.equals("") && ((Boolean) value).booleanValue()){
				lb.setIcon(iconStateDealed);
			}	
			else{
				lb.setIcon(iconStateUnDeal);
			}
			lb.setText("");
			return lb;
		}
		if(column == 2){//优先级
			lb.setHorizontalAlignment(JLabel.CENTER);
			if(value!=null&&value instanceof Integer){	
				Integer intvalue = (Integer)value;
				if(intvalue.intValue()<0){
					lb.setIcon(iconProLow);
				}else if(intvalue.intValue()>=0 && intvalue.intValue()<=9){
					lb.setIcon(iconProOrdinary);
				}else if(intvalue.intValue() > 9){
					lb.setIcon(iconProHigh);
				}
			}else{
				lb.setIcon(null);
			}
			lb.setText("");
			return lb;
		}
		if(column==3){//分类
			labelMap = msgmodel.getLabelMap();
			if(value!=null && !value.equals("") && !value.equals("~")){
				if(labelMap.containsKey(value.toString())){
					//根据Map中存储的数据来实现对消息中心标签的更新
					lb.setIcon(ThemeResourceCenter.getInstance().getImage(labelMap.get(value.toString())));
				}else{
					lb.setIcon(null);
				}
			}else{
				lb.setIcon(null);
				}
			lb.setHorizontalAlignment(JLabel.CENTER);
			lb.setText("");
			return lb;
		}
		if(column == 4){//附件
			lb.setHorizontalAlignment(JLabel.CENTER);
			if(value != null && !value.equals("") && ((Boolean) value).booleanValue())
				lb.setIcon(iconAttach);
			else
				lb.setIcon(null);
			lb.setText("");
			return lb;
		}if(column == 5){//标题
			NCMessage msg = ((AbstractMsgTBModel)table.getModel()).getMessage(row);
			int rbg = 0;
			if(msg.getMessage().getSubcolor()!=null&&msg.getMessage().getSubcolor().length()>0){
				rbg = Integer.valueOf(msg.getMessage().getSubcolor());
			}
			if(msg.getMessage().getIsread()!=null&&msg.getMessage().getIsread().booleanValue()){
				lb.setFont(new Font(Style.getFontname(),Font.PLAIN,12));				
			}else{
				lb.setFont(new Font(Style.getFontname(),Font.BOLD,12));				
			}
			if(value != null && !value.equals("")){
				lb.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
				lb.setIcon(null);
				lb.setText(value.toString());
				if(rbg!=0){
					lb.setForeground(new Color(rbg));
				}else{
					if(msg.getMessage().getIsread()!=null&&msg.getMessage().getIsread().booleanValue()){						
						lb.setForeground(MessageCenterUIConst.MCWORDCOLOR);
					}else{						
						lb.setForeground(MessageCenterUIConst.MCUNREADCOLOR);
					}				
				}
				lb.setHorizontalAlignment(JLabel.LEFT);
			}
			return lb;
		}
		else{
			lb.setHorizontalAlignment(JLabel.CENTER);
			lb.setForeground(MessageCenterUIConst.MCWORDCOLOR);
			lb.setIcon(null);
			return lb;
		}
	}

}
