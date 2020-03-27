package nc.ui.pub.beans;

/**
 * 对话框基类 说明: 1.对话框为模态显示. 2.预设4个常量代表返回按钮类型,getResult()获取返回按钮类型(需要在相应的返回方法中设置)
 * 3.只提供了closeOK和closeCancel方法,未封装按钮控件及事件监听,可根据需要添加及覆盖方法. 4.显示对话框用showModal方法.
 * 5.对话框关闭后只是不显示,自身对象并没有销毁,因此可以继续使用对话框对象.
 * 6.不使用对话框时应立即销毁本对象,销毁本对象要显式调用destroy方法或直接赋null值. 7.不推荐用无参数的构造子,应使用指定父窗体的构造子.
 * 8.对话框提供了UIDialogEvent事件和UIDialogListener接口. 作者:祝奇 修改:张扬
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import nc.bs.logging.Logger;
import nc.ui.plaf.basic.UIDialogRootPaneUI;
import nc.ui.pub.beans.bill.IBillCardPanelKeyListener;
import nc.uitheme.ui.ThemeResourceCenter;

public class UIDialog extends javax.swing.JDialog implements WindowListener,IResetableDialog,
		java.awt.event.KeyListener {

	// protected transient UIDialogListener aUIDialogListener = null;
	protected transient EventListenerList m_listenerList = new EventListenerList();
	private static Color dlgContentBGColor = ThemeResourceCenter.getInstance()
			.getColor("themeres/dialog/dialogResConf",
					"dialogContentPane.backgroundColor");
	/** 父窗体--另外一个Dialog,Applet */
	/** 父窗体--另外一个Frame */
	// private java.awt.Container m_parent = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;

	public final static int ID_OK = 1;

	public final static int ID_CANCEL = 2;

	public final static int ID_YES = 4;

	public final static int ID_NO = 8;
	
	//dongdb +
	private boolean isResetable = false;

	private int m_nResult = 0;
	
	// private ClientEnvironment m_ceSingleton = null;

	protected static java.util.HashSet m_allSingleHotKeys = null;
	static {

		// 定义单个热键
		m_allSingleHotKeys = new HashSet();
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_ESCAPE));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_PAGE_UP));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_PAGE_DOWN));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_END));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_HOME));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_LEFT));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_UP));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_RIGHT));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_DOWN));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_DELETE));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F2));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F3));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F4));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F5));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F5));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F7));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F8));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F9));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F10));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F11));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_F12));
		m_allSingleHotKeys.add(Integer.valueOf(KeyEvent.VK_ENTER));
	}

	/**
	 * 构造子
	 * 
	 * @deprecated
	 */
	public UIDialog() {
		super();
		initialize();
	}

	
	/**
	 * 
	 * 创建日期:(2001-3-28 11:38:40)
	 * 
	 * @param parent
	 *            java.awt.Container
	 */
	public UIDialog(Container parent) {
		this(getTopWindow(parent));
	}

	
	//dongdb+
	public UIDialog(Container parent,boolean reset){		
		this(getTopWindow(parent),reset);
	}
	
	
	/**
	 * 
	 * 创建日期:(2001-3-27 17:40:12)
	 * 
	 * @param parent
	 *            java.awt.Container
	 * @param title
	 *            java.lang.String
	 */
	public UIDialog(Container parent, String title) {
		this(getTopWindow(parent), title);
	}
	
	//dongdb+
	public UIDialog(Container parent, String title,boolean reset) {
		this(getTopWindow(parent), title,reset);
	}

	/**
	 * 
	 * 创建日期:(2001-3-28 11:55:31)
	 * 
	 * @param owner
	 *            java.awt.Frame
	 */
	public UIDialog(java.awt.Frame owner) {
		super(owner);
		initialize();
	}
	
	//dongdb+
	public UIDialog(java.awt.Frame owner,boolean reset) {
		super(owner);
		this.isResetable=reset;
		initialize();
	}

	/**
	 * UFDialog 构造子注释.
	 * 
	 * @param owner
	 *            java.awt.Frame
	 * @param title
	 *            java.lang.String
	 */
	public UIDialog(java.awt.Frame owner, String title) {
		super(owner, title);
		initialize();
	}
	
	//dongdb+
	public UIDialog(java.awt.Frame owner, String title,boolean reset) {
		super(owner, title);
		this.isResetable=reset;
		initialize();
	}

	public UIDialog(Dialog owner, String title) {
		super(owner, title);
		initialize();
	}
	
	public UIDialog(Dialog owner, String title,boolean reset) {
		super(owner, title);
		this.isResetable=reset;
		initialize();
	}

	public UIDialog(Dialog owner) {
		super(owner);
		initialize();
	}
	
	public UIDialog(Dialog owner,boolean reset) {
		super(owner);
		this.isResetable=reset;
		initialize();
	}

	public UIDialog(Window owner, String title) {
		super(owner, title);
		initialize();
	}
	
	public UIDialog(Window owner, String title,boolean reset) {
		super(owner, title);
		this.isResetable=reset;
		initialize();
	}

	public UIDialog(Window owner) {
		super(owner);
		initialize();
	}
	
	
	public UIDialog(Window owner,boolean reset) {
		super(owner);
		this.isResetable=reset;
		initialize();
	}

	private static Window getTopWindow(Container parentContainer) {
		Container parent = parentContainer;
		while (parent != null
				&& !(parent instanceof Dialog || parent instanceof Frame)) {
			parent = parent.getParent();
		}
		if (parent == null) {
			parent = JOptionPane.getFrameForComponent(parentContainer);
		}
		return (Window) parent;
	}

	/**
	 * 增加对话框关闭事件的监听者
	 * 
	 * @param newListener
	 *            uferp.view.UIDialogListener 对话框关闭事件
	 */
	public void addUIDialogListener(UIDialogListener newListener) {
		m_listenerList.add(UIDialogListener.class, newListener);
		return;
	}

	/**
	 * This method was created by a SmartGuide. close Panel
	 */
	protected void close() {
		if (!isShowing())
			return;
		this.setVisible(false);
		if (isModal() && getDefaultCloseOperation() == DISPOSE_ON_CLOSE) {
			destroy();
		}
	}

	/**
	 * 以‘取消’模式关闭对话框 业务节点根据需要修改
	 */
	public void closeCancel() {
		setResult(ID_CANCEL);
		close();
		fireUIDialogClosed(new UIDialogEvent(this, UIDialogEvent.WINDOW_CANCEL));
		return;
	}

	/**
	 * 以‘确定’模式关闭对话框 业务节点根据需要修改
	 */
	public void closeOK() {
		setResult(ID_OK);
		close();
		fireUIDialogClosed(new UIDialogEvent(this, UIDialogEvent.WINDOW_OK));
		return;
	}

	/**
	 * 该方法在 VisualAge 中创建.
	 */
	public void destroy() {
		this.dispose();
	}

	/**
	 * 触发对话框关闭事件处理
	 * 
	 * @param event
	 *            uferp.view.UIDialogEvent 对话框关闭事件
	 */
	protected void fireUIDialogClosed(UIDialogEvent event) {
		/*
		 * if (aUIDialogListener == null) { return; };
		 * aUIDialogListener.UIDialogClosed(event);
		 */

		Object[] listeners = m_listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == UIDialogListener.class) {
				((UIDialogListener) listeners[i + 1]).dialogClosed(event);
			}
		}
	}

	
	//dongdb +
	
		public  boolean isResetable() {
			return this.isResetable;
		}

		public  void setReset(boolean ReSet) {
			this.isResetable = ReSet;
			setUIDialogUndecorationStyle(this);
		}
		
	class ShortCutKeyAction extends AbstractAction {

		int keycode = -1;

		final static int VK_ESCAPE = KeyEvent.VK_ESCAPE;

		final static String KEY_CLOSE_DIALOG = "CLOSEDIALOG";

		public ShortCutKeyAction(int keycode) {
			this.keycode = keycode;
		}

		public void actionPerformed(ActionEvent e) {
			switch (keycode) {
			case java.awt.event.KeyEvent.VK_ESCAPE:
				closeCancel();
				return;
			default:
				return;
			}
		}
	}

	/**
	 * 返回 JDialogContentPane 的特性值.
	 * 
	 * @return com.sun.java.swing.JPanel
	 */

	private javax.swing.JPanel getJDialogContentPane() {
		if (ivjJDialogContentPane == null) {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new BorderLayout());
		}
		return ivjJDialogContentPane;
	}

	/**
	 * 在 创建日期:(00-7-10 11:47:44)
	 * 
	 * @return int
	 */
	public int getResult() {
		return m_nResult;
	}

	/**
	 * 
	 * 创建日期:(2001-4-27 19:13:52)
	 * 
	 * @return java.lang.String
	 */
	public String getTitle() {
		return super.getTitle();
		// String title = super.getTitle();
		// if (title != null && !title.trim().equals(""))
		// // return
		// //
		// nc.ui.ml.NCLangRes.getInstance().getString(nc.vo.ml.IProductCode.PRODUCTCODE_COMMON,super.getTitle(),
		// // null);
		// return nc.ui.pub.beans.UIComponentUtil.getTranslatedString(title);
		// else
		// return title;
	}

	/**
	 * 本方法处理热键,以支持全键盘操作.本方法支持的热键包括组合热键和单热键两种. 组合热键指 Ctrl/Alt/Shift + 数字/字母;单热键包括
	 * KeyEvent.VK_ESCAPE KeyEvent.VK_PAGE_UP KeyEvent.VK_PAGE_DOWN
	 * KeyEvent.VK_END KeyEvent.VK_HOME KeyEvent.VK_LEFT KeyEvent.VK_UP
	 * KeyEvent.VK_RIGHT KeyEvent.VK_DOWN KeyEvent.VK_DELETE // KeyEvent.VK_F2
	 * KeyEvent.VK_F3 KeyEvent.VK_F4 KeyEvent.VK_F5 KeyEvent.VK_F5
	 * KeyEvent.VK_F7 KeyEvent.VK_F8 KeyEvent.VK_F9 KeyEvent.VK_F10
	 * KeyEvent.VK_F11 KeyEvent.VK_F12 F1由MainFrame统一处理,所以本方法不支持它.
	 * 
	 * 使用说明: 1. 在你的UIDialog子类中实现这个方法; 2. 实现该方法的示例代码如下: protected void
	 * hotKeyPressed(javax.swing.KeyStroke hotKey) {
	 * 
	 * int modifiers = hotKey.getModifiers(); if (modifiers == 0) { //Single hot
	 * key: switch (hotKey.getKeyCode()) { case KeyEvent.VK_ESCAPE:
	 * keyEscPressed(); break; case KeyEvent.VK_PAGE_UP: keyPageUpPressed();
	 * break; //... } } else { //Combined hot key: boolean ctrl = false; boolean
	 * alt = false; boolean shift = false; if ((modifiers & Event.CTRL_MASK) !=
	 * 0) { ctrl = true; } if ((modifiers & Event.ALT_MASK) != 0) { alt = true;
	 * } if ((modifiers & Event.SHIFT_MASK) != 0) { shift = true; } // 处理ctrl +
	 * S: if (ctrl && hotKey.getKeyCode() == KeyEvent.VK_S) { keyCtrlSPressed();
	 * } // ... } } 创建日期:(2001-8-28 16:41:19)
	 */
	protected void hotKeyPressed(javax.swing.KeyStroke hotKey,
			java.awt.event.KeyEvent e) {
	}

	/**
	 * 初始化连接
	 */

	private void initConnections() {
		this.addWindowListener(this);
		this.addKeyListener(this);
	}

	/**
	 * 初始化类.
	 */

	private void initialize() {
		// 用户代码开始处 {1}
		// 用户代码结束处
		setName("UIDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(400, 240);
		setModal(true);
		setContentPane(getJDialogContentPane());
		initConnections();
		setResizable(false);

		setUIDialogUndecorationStyle(this);
		// 用户代码开始处 {2}

		// 用户代码结束处
		
	}


	//dongdb change
	public static void setUIDialogUndecorationStyle(JDialog dlg) {
		dlg.setUndecorated(true);
		dlg.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		UIDialogRootPaneUI uiRootUI = new UIDialogRootPaneUI();
		if(dlg instanceof IResetableDialog)
		{
		  uiRootUI.setReset(((IResetableDialog)dlg).isResetable());
		}
//		if(getisReSet() == true){
//			dlg.getRootPane().setUI(uiRootUI);
//		}else{
//			uiRootUI.setReset(false);
//		}
		dlg.getRootPane().setUI(uiRootUI);
// 为什么要加下面的逻辑		
//		setReSet(false);
//		dlg.getRootPane().setUI(new UIDialogRootPaneUI());
	}

	/**
	 * 
	 * 创建日期:(2001-8-29 9:57:01)
	 * 
	 * @return boolean
	 */
	protected boolean isSingleHotKey(int keyCode) {
		return m_allSingleHotKeys.contains(Integer.valueOf(keyCode));
	}

	public void keyPressed(java.awt.event.KeyEvent e) {
		int keyCode = e.getKeyCode();
		int modifiers = e.getModifiers();
		if (keyCode == KeyEvent.VK_ESCAPE)
			closeCancel();
		if (keyCode == 16 || keyCode == 17 || keyCode == 18)
			return;
		if (modifiers > 1 || isSingleHotKey(keyCode)) {
			hotKeyPressed(KeyStroke.getKeyStroke(keyCode, modifiers), e);
		}
		// process billCardPanel shortCut event
		processBillHotKeyEvent(e);
	}

	public void keyReleased(java.awt.event.KeyEvent e) {
	}

	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	/**
	 * 对话框关闭事件监听者删除函数
	 * 
	 * @param newListener
	 *            uferp.view.UIDialogListener 对话框关闭事件
	 */
	public void removeUIDialogListener(UIDialogListener newListener) {
		// aUIDialogListener =
		// UIDialogEventMulticaster.remove(aUIDialogListener, newListener);
		m_listenerList.remove(UIDialogListener.class, newListener);
		return;
	}

	/**
	 * 
	 * 创建日期:(2001-4-10 15:38:42)
	 * 
	 * @param e
	 *            java.lang.Exception
	 */
	protected void reportException(Exception e) {
		Logger.debug(e);
	}

	/**
	 * 设置该对话框的父容器(生成对话框实例时必须由其父容器调用)
	 * 
	 * @param parent
	 *            java.awt.Container 父容器
	 * @deprecated
	 */
	public void setParent(Container parent) {
		// m_parent = parent;
		return;
	}

	/**
	 * 在 创建日期:(00-7-10 13:42:06)
	 * 
	 * @param n
	 *            int
	 */
	protected void setResult(int n) {
		m_nResult = n;
	}

	/**
	 * 功能描述:
	 * 
	 * 参数说明:
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public void setTitle(String s) {
		super.setTitle(s);
	}

	/**
	 * 显示对话框
	 * 
	 */
	public int showModal() {
		// 下句会使屏幕闪烁
		// if (getTopFrame() == null && getTopParent() != null)
		// getTopParent().setEnabled(false);
		//
		setModal(true);

		if (!isShowing()) {
			// setLocationRelativeTo(m_parent);
//				Dimension screan = Toolkit.getDefaultToolkit().getScreenSize();
//				Dimension dlgsize = this.getSize();
//				setLocation((screan.width - dlgsize.width) / 2,(screan.height - dlgsize.height) / 2);
			
			setLocationRelativeTo(getParent());
			
			show();
		}
		return getResult();
	}

	private transient Component comp = null;

	// 为解决FireFox对话框焦点丢失问题
	// since v5.5
	@Override
	public void show() {
		comp = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getFocusOwner();
		super.show();
	}

	// 为解决FireFox对话框焦点丢失问题
	// since v5.5
	public void hide() {
		super.hide();
		Runnable run = new Runnable() {
			public void run() {
				if (comp != null) {
					// Window f = SwingUtilities.getWindowAncestor(comp);
					// if(f != null&& !f.isFocused()){
					// f.requestFocus();
					// f.requestFocusInWindow();
					// }
					comp.requestFocusInWindow();
					comp.requestFocus();

				}
				comp = null;
			}
		};
		SwingUtilities.invokeLater(run);
	}

	/**
	 * process BillCardPanel ShortCut key event.
	 */
	private void processBillHotKeyEvent(java.awt.event.KeyEvent e) {
		java.awt.Component bcp = nc.ui.pub.beans.util.MiscUtils
				.findChildByClass(this, IBillCardPanelKeyListener.class);
		if (bcp instanceof IBillCardPanelKeyListener)
			((IBillCardPanelKeyListener) bcp).processShortKeyEvent(e);
	}

	// protected void processWindowEvent(WindowEvent e) {
	// super.processWindowEvent(e);
	// int defaultCloseOperation = getDefaultCloseOperation();
	// if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	// if (defaultCloseOperation != DO_NOTHING_ON_CLOSE) {
	// this.closeCancel();
	// }
	// // processActionEvent(new ActionEvent(this,
	// // ActionEvent.ACTION_PERFORMED, CANCEL_COMMAND));
	// } else if (e.getID() == WindowEvent.WINDOW_OPENED) {
	// // simulate tab from last to wrap around to first & properly set
	// // initial focus
	// //解决1.7下弹出Dialogue没有焦点问题。
	// if
	// (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()==null){
	// this.requestFocusInWindow();
	// this.requestFocus();
	// }
	// if (getComponentCount() > 0)
	// getComponent(getComponentCount() - 1).transferFocus();
	// }
	// }

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
//		Logger.error("KKK test:" + e.getID());
		int defaultCloseOperation = getDefaultCloseOperation();
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (defaultCloseOperation != DO_NOTHING_ON_CLOSE) {
				this.closeCancel();
			}
			// processActionEvent(new ActionEvent(this,
			// ActionEvent.ACTION_PERFORMED, CANCEL_COMMAND));
		} else if (e.getID() == WindowEvent.WINDOW_OPENED
				|| e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			// simulate tab from last to wrap around to first & properly set
			// initial focus
			
			
			// 解决1.7下弹出Dialogue没有焦点问题。
			if (isJre7u25()) {
//				Logger.error("KKK test:"
//						+ FocusManager.getCurrentManager().getFocusOwner());
				{
					Thread t = new Thread() {
						@Override
						public void run() {
							while (!isShowing()) {
								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
								}
							}
							requestFocusInWindow();
							requestFocus();
//							Logger.error("KKK test:"
//									+ FocusManager.getCurrentManager()
//											.getFocusOwner());
						}
					};
					t.start();
				}
				{
					Thread t = new Thread() {
						@Override
						public void run() {
							while (UIDialog.this.isShowing() && !UIDialog.this.hasFocus()) {
								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
								}
							}
							if (getComponentCount() > 0) {
								getComponent(getComponentCount() - 1)
										.transferFocus();
							}
						}
					};
					t.start();
				}
			} else {
				//这段代码的意义？
//				if (getComponentCount() > 0)
//					getComponent(getComponentCount() - 1).transferFocus();
			}
		}
	}

	private boolean isJre7() {
		return System.getProperty("java.version").startsWith("1.7");
	}
	
	private boolean isJre7u25() {
		return System.getProperty("java.version").equalsIgnoreCase("1.7.0_25");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent
	 * )
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent
	 * )
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	protected JRootPane createRootPane() {
		JRootPane rp = new JRootPane() {
			protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
				boolean b = super.processKeyBinding(ks, e, condition, pressed);
				if (pressed) {
					keyPressed(e);
				}
				return b;
			}

			@Override
			public void setContentPane(Container content) {
				if (content instanceof JComponent) {
					((JComponent) content).setOpaque(true);
				}
				content.setBackground(dlgContentBGColor);
				JPanel panel = new JPanel(new BorderLayout()) {
					@Override
					public void setOpaque(boolean isOpaque) {
						// TODO Auto-generated method stub
						super.setOpaque(true);
					}

				};
				panel.setOpaque(true);
				panel.setBackground(dlgContentBGColor);
				panel.add(content, BorderLayout.CENTER);
				super.setContentPane(panel);
			}

		};
		rp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
				ShortCutKeyAction.KEY_CLOSE_DIALOG);
		rp.getActionMap().put(ShortCutKeyAction.KEY_CLOSE_DIALOG,
				new ShortCutKeyAction(ShortCutKeyAction.VK_ESCAPE));
		return rp;
	}

	@Override
	public void setSize(int width, int height) {
		if (getRootPane() != null
				&& getRootPane().getUI() instanceof UIDialogRootPaneUI) {
			UIDialogRootPaneUI ui = (UIDialogRootPaneUI) getRootPane().getUI();
			JComponent titlePane = ui.getTitlePane();
			if (titlePane != null) {
				height += titlePane.getPreferredSize().height + 4;
				width += 6;
			}
		}

		super.setSize(width, height);

	}

	public void setSizeNoChange(int width, int height) {
		super.setSize(width, height);
	}

	@Override
	public void setLocation(int x, int y) {
		if (getRootPane() != null
				&& getRootPane().getUI() instanceof UIDialogRootPaneUI) {
			UIDialogRootPaneUI ui = (UIDialogRootPaneUI) getRootPane().getUI();
			JComponent titlePane = ui.getTitlePane();
			if (titlePane != null) {
				y -= titlePane.getPreferredSize().height;
			}

		}
		super.setLocation(x, y);

	}

	public void setLocationNoChange(int x, int y) {
		super.setLocation(x, y);
	}

}
