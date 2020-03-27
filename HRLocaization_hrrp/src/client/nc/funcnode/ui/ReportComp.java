package nc.funcnode.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hrrp.service.IBaseService;
import nc.itf.hrrp.service.IPdfOprator;
import nc.itf.org.IOrgConst;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.ui.ls.MessageBox;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pubapp.uif2app.query2.IQueryConditionDLGInitializer;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.refedit.IRefFilter;
import nc.ui.pubapp.uif2app.query2.refregion.QueryDefaultOrgFilter;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.QueryAreaCreator;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.uif2.NodeTypeUtil;
import nc.ui.uif2.editor.QueryTemplateContainer;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hrrp.report.AggReport;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.uif2.AppStatusRegistery;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 * <p>
 * Title: ReportComp
 * </P>
 * <p>
 * Description:
 * </p>
 * 
 * @author
 * @version 1.0
 * @since 2014-11-4
 */
public class ReportComp extends AbstractFunclet {

	private static final long serialVersionUID = -2522675117166054114L;
	private LoginContext context = null;
	private QueryTemplateContainer queryTemplateContainer;
	private AggReport aggvo;
	private QueryConditionEditor qce;
	private QueryConditionDLG qcd;

	public String getReportId() {
		return getParameter("ReportId");
	}

	public String getReportName() {
		return getParameter("ReportName");
	}

	/**
	 * @return the nodeKey
	 */
	public String getNodeKey() {
		return getParameter("nodeKey");
	}

	public ReportComp() {

		// this.setBounds(100, 100, 200, 200);
	}

	public void init() {
		fillAggvo();
		initContext();

		IQueryConditionDLG iQueryDlg = null;
		iQueryDlg = createQueryDlg_New();
		qcd = iQueryDlg.createQCDByIQCD(iQueryDlg);

		QueryConditionDLGDelegator qcdd = new QueryConditionDLGDelegator(
				iQueryDlg, context);
		// 业务单元和集团过滤组织
		new IQueryConditionDLGInitializer() {
			@Override
			public void initQueryConditionDLG(
					QueryConditionDLGDelegator queryconditiondlgdelegator) {
				List<String> targetFields = new ArrayList<String>();
				QueryDefaultOrgFilter orgFilter = new QueryDefaultOrgFilter(
						queryconditiondlgdelegator, "pk_org", targetFields);
				orgFilter.addEditorListener();
				queryconditiondlgdelegator.setRefFilter(null, "pk_org",
						new IRefFilter() {
							@Override
							public void doFilter(UIRefPane refPane) {
								// TODO
								if (context.getNodeType() == NODE_TYPE.GROUP_NODE)
									refPane.setWhereString("pk_group='"
											+ context.getPk_group() + "'");
								else if (context.getNodeType() == NODE_TYPE.ORG_NODE) {
									String[] orgs = context.getPkorgs();
									StringBuffer sbwhr = new StringBuffer();
									if (orgs.length < 1) {
										sbwhr.append("pk_adminorg='"
												+ context.getPk_org() + "'");
									} else if (orgs.length == 1) {
										sbwhr.append("pk_adminorg='" + orgs[0]
												+ "'");
									} else {
										sbwhr.append("( 1=2 ");
										for (String org : orgs) {
											sbwhr.append(" or pk_adminorg='"
													+ org + "'");

										}
										sbwhr.append(")");
									}
									refPane.setWhereString(sbwhr.toString());
								}
							}
						});
				queryconditiondlgdelegator.setRefFilter(null, "pk_dept",
						new IRefFilter() {
							@Override
							public void doFilter(UIRefPane refPane) {
								// TODO 自动生成的方法存根
								if (context.getNodeType() == NODE_TYPE.GROUP_NODE)
									refPane.setWhereString("pk_group='"
											+ context.getPk_group() + "'");
								else if (context.getNodeType() == NODE_TYPE.ORG_NODE) {
									String[] orgs = context.getPkorgs();
									StringBuffer sbwhr = new StringBuffer();
									if (orgs.length < 1) {
										sbwhr.append("pk_adminorg='"
												+ context.getPk_org() + "'");
									} else if (orgs.length == 1) {
										sbwhr.append("pk_adminorg='" + orgs[0]
												+ "'");
									} else {
										sbwhr.append("( 1=2 ");
										for (String org : orgs) {
											sbwhr.append(" or pk_adminorg='"
													+ org + "'");

										}
										sbwhr.append(")");
									}
									refPane.setWhereString(sbwhr.toString());
								}
							}

						});
				queryconditiondlgdelegator.setRefFilter(null, "code",
						new IRefFilter() {
							@Override
							public void doFilter(UIRefPane refPane) {
								// TODO 自动生成的方法存根
								if (context.getNodeType() == NODE_TYPE.GROUP_NODE)
									refPane.setWhereString("pk_group='"
											+ context.getPk_group() + "'");
								else if (context.getNodeType() == NODE_TYPE.ORG_NODE) {
									String[] orgs = context.getPkorgs();
									StringBuffer sbwhr = new StringBuffer();
									if (orgs.length < 1) {
										sbwhr.append("bd_psnjob.pk_org='"
												+ context.getPk_org() + "'");
									} else if (orgs.length == 1) {
										sbwhr.append("bd_psnjob.pk_org='" + orgs[0]
												+ "'");
									} else {
										sbwhr.append("( 1=2 ");
										for (String org : orgs) {
											sbwhr.append(" or bd_psnjob.pk_org='"
													+ org + "'");

										}
										sbwhr.append(")");
									}
									refPane.setWhereString(sbwhr.toString());
								}
							}

						});
				// 部门过滤
				// queryconditiondlgdelegator.setRefFilter(null, "pk_dept", new
				// IRefFilter(){
				//
				// @Override
				// public void doFilter(UIRefPane refPane) {
				// // TODO 自动生成的方法存根
				// refPane.m_refManage.getRefUI().getRefModel().setRefType(IRefConst.COMBOBOX);
				// refPane.setWhereString("pk_group='123123123123'");
				// }
				// });
			}
		}.initQueryConditionDLG(qcdd);

		qce = iQueryDlg.getQryCondEditor();
		// qce.getSelectedQuerySchemeVO().getSimpleEditorFilterEditors().getValidators()
		// qce.getAllFilterMeta()
		// qce.getTotalVO().getConditionVOs();
		ExtTabbedPane etp = qce.getEditorTabbedPane();

		qcd.beforeShowModal();
		this.setSize(500, 300);
		JPanel jpQry = new JPanel();
		jpQry.setLayout(null);
		jpQry.add(etp.getComponent(0)).setBounds(0, 0, 494, 300);
		setLayout(null);
		// jpQry.setBorder(new LineBorder(Color.BLUE));
		add(jpQry).setBounds(0, 0, 494, 300);// 查询面板

		add(getOpertPan(qce)).setBounds(0, 304, 494, 100);// 操作面板
	}

	private JPanel getOpertPan(QueryConditionEditor qceN) {
		// final QueryConditionEditor qceN=qce;
		JPanel jpOpt = new JPanel();
		jpOpt.setLayout(null);
		jpOpt.setSize(494, 100);
		// jpOpt.setBorder(new LineBorder(Color.RED));
		final UITextField txtPath = new UITextField();
		final JFileChooser jfc = new JFileChooser();
		JLabel lblPath = new JLabel("Export Path：");
		jpOpt.add(lblPath).setBounds(80, 5, 80, 30);
		jpOpt.add(txtPath).setBounds(150, 10, 160, 40);
		txtPath.setEditable(false);
		JButton btn = new JButton("..");
		JButton btnOk = new UIButton("Finish");
		final UICheckBox cbOpen = new UICheckBox();
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				// jfc.showSaveDialog(ReportSelDialog.getDialog());
				jfc.showSaveDialog(ReportComp.this);
				txtPath.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		});
		jpOpt.add(btn).setBounds(310, 10, 20, 20);
		cbOpen.setText("Open File");
		jpOpt.add(cbOpen).setBounds(150, 40, 80, 40);
		jpOpt.add(btnOk).setBounds(350, 50, 50, 30);
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FilterMeta fm : qce.getAllFilterMeta()) {
					if (fm.isRequired()) {
						String sqlStr = qcd
								.getFiltersByFieldCode(fm.getFieldCode())
								.get(0).getSqlString();
						if (sqlStr == null || sqlStr.trim().length() < 1) {
							MessageBox.showMessageDialog("ERROR", "QueryField:"
									+ fm.getFieldName() + " Is Null");
							return;
						}
					}
				}
				if (aggvo == null) {
					MessageBox.showMessageDialog("ERROR", "Config Error");
					return;
				}
				if (txtPath.getText() == null || txtPath.getText().length() < 1) {
					MessageBox
							.showMessageDialog("ERROR", "Output Path Is Null");
					return;
				}
				String where = qce.getQueryScheme().getWhereSQLOnly();
				doPdfBuild(where, txtPath.getText(), cbOpen.isSelected());
			}
		});
		return jpOpt;
	}

	/**
	 * <p>
	 * 方法名称：doPdfBuild
	 * </p>
	 * <p>
	 * 方法描述：创建PDF文档并保存
	 * </p>
	 * 
	 * @param sqlWhr
	 * @param path
	 * @param isOpen
	 * @author
	 * @since 2014-11-7
	 */
	private void doPdfBuild(String sqlWhr, String path, boolean isOpen) {
		// MessageBox.showMessageDialog("ERROR", sqlWhr + "----" + path);
		IPdfOprator pdfopt = NCLocator.getInstance().lookup(IPdfOprator.class);
		pdfopt.setAggvo(aggvo);
		pdfopt.setStrWhr(sqlWhr);
		try {
			byte[] fileBts = pdfopt.pdfBuild();
			download(path, fileBts);
			if (isOpen)
				Runtime.getRuntime()
						.exec("rundll32 url.dll FileProtocolHandler " + path
								+ ".pdf");
		} catch (Exception ex) {
			MessageBox.showMessageDialog("Error", ex.getMessage() + " ,Cause: " + ex.getCause());
			// ex.printStackTrace();
		}
	}

	/**
	 * <p>
	 * 方法名称：download
	 * </p>
	 * <p>
	 * 方法描述：文件保存客户端
	 * </p>
	 * 
	 * @param path
	 * @param bts
	 * @author
	 * @throws BusinessException
	 * @throws IOException
	 * @since 2014-11-7
	 */
	private void download(String path, byte[] bts) throws BusinessException {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(path + ".pdf");
			fos.write(bts);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			throw new BusinessException("File Path Error");
		} catch (IOException e) {
			throw new BusinessException("Write File Error");
		}

	}

	// private static void download(String path, OutputStream out) throws
	// Exception {
	// ObjectOutputStream oos = null;
	// ObjectInputStream ois = null;
	// InputStream in = null;
	// StringBuilder sbUrl = new StringBuilder(ClientToolKit.getServerURL());
	// Boolean flag = false; // 前台调用为false,后台调用为true
	// if (sbUrl.toString().contains("null")) {
	// flag = true;
	// sbUrl = new StringBuilder();
	// sbUrl.append("http://" +
	// InvocationInfoProxy.getInstance().getClientHost() + "/");
	// }
	// sbUrl.append("service/UploadServlet");
	// try {
	// URL url = new URL(sbUrl.toString());
	// URLConnection conn = url.openConnection();
	// conn.setDoInput(true);
	// conn.setDoOutput(true);
	// conn.setRequestProperty("content-type",
	// "application/x-java-serialized-object,charset=utf-8");
	// // 安全token
	// byte[] token = NetStreamContext.getToken();
	// ((HttpURLConnection) conn).setRequestMethod("POST");
	// conn.setRequestProperty("security_token", KeyUtil.encodeToken(token));
	//
	// // 根据前后台调用设置user_code属性的值
	// if (flag) {
	// conn.setRequestProperty("user_code",
	// InvocationInfoProxy.getInstance().getUserCode());
	// } else {
	// conn.setRequestProperty("user_code",
	// WorkbenchEnvironment.getInstance().getLoginUser().getUser_code());
	// }
	//
	// HashMap<String, String> headInfo = new HashMap<String, String>();
	//
	// headInfo.put("path", path);
	// oos = new ObjectOutputStream(conn.getOutputStream());
	// oos.writeObject(headInfo);
	// oos.flush();
	// oos.close();
	// in = conn.getInputStream();
	// if (in.available() != -1) {
	// ois = new ObjectInputStream(in);
	// Object obj = ois.readObject();
	// if (obj instanceof Exception) {
	// throw (Exception) obj;
	// }
	// byte[] buf = new byte[2048];
	// int len = -1;
	// while ((len = in.read(buf)) != -1) {
	// out.write(buf, 0, len);
	// out.flush();
	// }
	// }
	// return;
	// } catch (Exception e) {
	// throw e;
	// } catch (Throwable t) {
	// if (t instanceof Exception) {
	// throw (Exception) t;
	// } else {
	// StringBuffer bf = new StringBuffer("ThrowAble").append("\n");
	// for (StackTraceElement st : t.getStackTrace()) {
	// bf.append(st.toString()).append("\n");
	// }
	// throw new Exception(bf.toString());
	// }
	// } finally {
	// try {
	// if (oos != null) {
	// oos.close();
	// }
	// if (in != null) {
	// in.close();
	// }
	// } catch (Exception e2) {
	// throw new Exception("读写关闭异常!" + e2);
	// }
	// }
	// }

	protected void initContext() {
		context = new LoginContext();
		context.setNodeType(NodeTypeUtil
				.funcregisterVO2NODE_TYPE(getFuncletContext()
						.getFuncRegisterVO()));

		context.setNodeCode(getFuncCode());

		context.setPk_loginUser(WorkbenchEnvironment.getInstance()
				.getLoginUser().getPrimaryKey());

		if (WorkbenchEnvironment.getInstance().getGroupVO() != null) {
			context.setPk_group(WorkbenchEnvironment.getInstance().getGroupVO()
					.getPk_group());
		}

		context.setEntranceUI(this);
		context.setFuncInfo(getFuncletContext().getFuncSubInfo());
		if (getFuncletContext().getFuncSubInfo() != null) {
			String[] pkorgs = getFuncletContext().getFuncSubInfo()
					.getFuncPermissionPkorgs();
			// if(ArrayUtils.isEmpty(pkorgs))
			// context.setPkorgs(null);
			// else
			// context.setPkorgs(Arrays.asList(pkorgs));
			context.setPkorgs(pkorgs);
		}
		setPk_org(context);
		loadAppStatus();
		context.setStatusRegistery(this.statusRegistery);
	}

	protected AppStatusRegistery statusRegistery = null;

	// 装载应用状态信息
	protected void loadAppStatus() {
		statusRegistery = new AppStatusRegistery();
		String pkUser = WorkbenchEnvironment.getInstance().getLoginUser()
				.getCuserid();
		statusRegistery.load(getFuncCode(), pkUser);
	}

	protected void setPk_org(LoginContext context) {

		switch (context.getNodeType()) {
		case GLOBE_NODE:
			context.setPk_org(IOrgConst.GLOBEORG);
			break;
		case GROUP_NODE:
			context.setPk_org(context.getPk_group());
			break;
		case ORG_NODE:
			setDefaultOrg(context);
			break;
		}
	}

	protected void setDefaultOrg(LoginContext ctx) {
		try {
			String defaultOrg = OrgSettingAccessor.getDefaultOrgUnit();
			String[] pkorgs = ctx.getPkorgs();
			if (ArrayUtils.isEmpty(pkorgs)
					|| StringUtil.isEmptyWithTrim(defaultOrg))
				ctx.setPk_org(null);
			else {
				boolean match = false;
				for (String pkorg : pkorgs) {
					if (defaultOrg.equals(pkorg)) {
						match = true;
						break;
					}
				}
				if (match)
					ctx.setPk_org(defaultOrg);
				else
					ctx.setPk_org(null);
			}
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
	}

	protected IQueryConditionDLG createQueryDlg_New() {
		TemplateInfo tempinfo = getTemplateInfo();
		if (getTemplateContainer() == null) {
			QueryAreaCreator queryAreaCreator = new QueryAreaCreator(tempinfo,
					null);
			queryAreaCreator.setQcdParent(getContext().getEntranceUI());
			return queryAreaCreator;
		} else {
			QueryAreaCreator queryAreaCreator = new QueryAreaCreator(tempinfo,
					getTemplateContainer().getQueryTempletLoader());
			queryAreaCreator.setQcdParent(getContext().getEntranceUI());
			return queryAreaCreator;
		}
	}

	private TemplateInfo getTemplateInfo() {
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(getContext().getPk_group());
		// tempinfo.setPk_Org(getContext().getPk_org());
		// tempinfo.setAutoProcessPk_org(true);
		tempinfo.setFunNode(this.getFunNode());
		tempinfo.setUserid(getContext().getPk_loginUser());
		tempinfo.setNodekey(this.getNodeKey());
		tempinfo.setSealedDataShow(true);
		return tempinfo;
	}

	// private AbstractUIAppModel model = null;
	// public AbstractUIAppModel getModel() {
	// return this.model;
	// }
	private String funNode;

	public String getFunNode() {
		if (this.funNode == null) {
			this.funNode = getContext().getNodeCode();
		}
		return this.funNode;
	}

	private QueryTemplateContainer templateContainer = null;

	public QueryTemplateContainer getTemplateContainer() {
		return this.templateContainer;
	}

	/**
	 * @return the context
	 */
	public LoginContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(LoginContext context) {
		this.context = context;
	}

	/**
	 * @return the queryTemplateContainer
	 */
	public QueryTemplateContainer getQueryTemplateContainer() {
		return queryTemplateContainer;
	}

	/**
	 * @param queryTemplateContainer
	 *            the queryTemplateContainer to set
	 */
	public void setQueryTemplateContainer(
			QueryTemplateContainer queryTemplateContainer) {
		this.queryTemplateContainer = queryTemplateContainer;
	}

	private void fillAggvo() {
		String repId = getReportId();
		String repName = getReportName();
		IBaseService ibs = NCLocator.getInstance().lookup(IBaseService.class);
		AggReport[] aggvos = {};
		if (repId != null && repId.length() > 0) {
			try {
				aggvos = ibs.queryBillByPK(AggReport.class,
						new String[] { repId });
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				// e.printStackTrace();
			}

		} else if (repName != null && repName.length() > 0) {
			try {
				aggvos = ibs.queryAggVOByWhere(AggReport.class,
						"where repName='" + repName + "'");
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				// e.printStackTrace();
			}
		}
		if (aggvos == null)
			return;
		if (aggvos.length > 0)
			aggvo = aggvos[0];
	}

}
