package nc.bs.sm.accountmanage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.framework.exception.ComponentException;
import nc.bs.framework.server.util.NewObjectService;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.sm.accountmanage.acccheck.AccCheckConfReader;
import nc.bs.sm.accountmanage.acccheck.AccCheckConfVO;
import nc.bs.sm.accountmanage.acccheck.AccChecker;
import nc.bs.sm.data.DBInstalledModule;
import nc.bs.update.db.table.TableUpdateProcessor;
import nc.bs.update.db.verify.Verify;
import nc.itf.uap.sfapp.IAccountCreateService;
import nc.itf.uap.sfapp.IAccountManageService;
import nc.itf.uap.sfapp.ModuleInfo;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.crossdb.CrossDBPreparedStatement;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.designer.service.IPublishService;
import nc.newinstall.config.ConfigKey;
import nc.newinstall.config.ModuleConfig;
import nc.newinstall.config.ModuleConfig.RelatedModule;
import nc.newinstall.data.InstalledModule;
import nc.newinstall.util.CheckTreeNode;
import nc.newinstall.util.DBInstallTreeScaner;
import nc.newinstall.util.FileUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.accountmanage.CodeVerinfoTreeNode;
import nc.vo.sm.accountmanage.DBInstallProgress;
import nc.vo.sm.accountmanage.DBInstallSetup;

/**
 * 创建日期:2006-3-22
 * 
 * @author licp
 * @since 5.0
 */
public class AccountManageImpl implements IAccountManageService,
		IUpdataAccountConstant, IBLOBProcessor {
	private ThreadLocal<String> langCodeThreadLocal = new ThreadLocal<String>();
	private ThreadLocal<Boolean> doneUAPCheckThreadLocal = new ThreadLocal<Boolean>();
	private ThreadLocal<DBInstallProgress> progressThreadLocal = new ThreadLocal<DBInstallProgress>();

	private ThreadLocal<CheckTreeNode> checkTreeRootNodeThreadLocal = new ThreadLocal<CheckTreeNode>();

	private ThreadLocal<ArrayList<String>> hadInstalledModuleThreadLocal = new ThreadLocal<ArrayList<String>>();

	private ThreadLocal<ArrayList<String>> hadInstalledModuleThreadLocalOrder = new ThreadLocal<ArrayList<String>>();

	private final String ncHome = RuntimeEnv.getInstance().getProperty(
			RuntimeEnv.SERVER_LOCATION_PROPERTY);

	// 保存的多语信息
	private List<String> dbmllist = null;

	// 需要安装的产品及模块的信息

	private static final String SPLITER = "#";

	/**
     *
     */
	public AccountManageImpl() {
		super();
	}

	private void registerDataSource(String dsName) {
		InstallLogTool.log("### 开始注册数据源: " + dsName + " ###");
		InvocationInfoProxy.getInstance().setUserDataSource(dsName);

	}

	private IAccountCreateService getAccountCreateService()
			throws BusinessException {
		IAccountCreateService service = null;
		try {
			service = (IAccountCreateService) NCLocator.getInstance().lookup(
					IAccountCreateService.class.getName());
		} catch (ComponentException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
		return service;
	}

	public IUpdateAccount[] getDefaultUpdateAccount(ModuleConfig config,
			boolean isNewDisk, boolean isUpdate) {
		IUpdateAccount[] updateAccounts = ClassAdjustSupport
				.getDefaultIUpdateAccounts(isNewDisk, isUpdate);
		int count = updateAccounts == null ? 0 : updateAccounts.length;
		for (int i = 0; i < count; i++) {
			if (updateAccounts[i] instanceof AbstractUpdateAccount) {
				AbstractUpdateAccount aua = (AbstractUpdateAccount) updateAccounts[i];
				aua.setConfig(config);
				// 设置传递多语langcode 的List dbmllist
				aua.setMultilanglist(dbmllist);
				if (isUpdate)
					aua.setPatchVersions(getInstalledModulePatchVersionsByCode(config
							.getCode()));
			}

		}
		return updateAccounts;
	}

	private String[] getInstalledModulePatchVersionsByCode(String moduleCode) {
		AccountInstallDAO dao = null;
		String[] versions = new String[0];
		try {
			dao = new AccountInstallDAO();
			versions = dao.getInstalledModulePatchVersionsByCode(moduleCode);
		} catch (Exception e) {
			InstallLogTool.logException(e);
		} finally {
			if (dao != null)
				dao.close();
		}
		return versions;
	}

	private Object newInstance(String moduleName, String className) {
		Object obj = null;
		try {
			if (moduleName == null) {
				obj = NewObjectService.newInstance(className);
			} else {
				obj = NewObjectService.newInstance(moduleName, className);
			}
		} catch (Exception e) {
		}
		return obj;
	}

	private IUpdateAccount[] getUpdateAccounts(ModuleConfig config) {
		ArrayList al = new ArrayList();
		InstallLogTool
				.log("=======================加载代码调整类:======================================================");
		String[] classNames = config.getDataUpdateClassNames();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		// IUpdateAccount[] updateAccounts = new IUpdateAccount[count];
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("加载" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "模块" + config.getName() + "注册的代码调整类没有加载成功："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof IUpdateAccount) {
				if (obj instanceof AbstractUpdateAccount) {
					AbstractUpdateAccount aua = (AbstractUpdateAccount) obj;
					aua.setConfig(config);
					aua.setMultilanglist(dbmllist);
					// 如果不是新盘,是补丁盘设置 patchversion
					if (!config.isNewDisk()) {
						aua.setPatchVersions(getInstalledModulePatchVersionsByCode(config
								.getCode()));
					}
				}
				al.add(obj);
			} else {
				String msg = "模块"
						+ config.getName()
						+ "注册的代码调整类没有没有实现nc.bs.sm.accountmanage.IUpdateAccount接口："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================加载代码调整类结束==========================================================");
		return (IUpdateAccount[]) al.toArray(new IUpdateAccount[0]);
	}

	private INewInstallAdjust[] getNewInstallAdjustClass(ModuleConfig config) {
		ArrayList al = new ArrayList();
		InstallLogTool
				.log("=======================加载新安装代码调整类:======================================================");
		//
		String[] classNames = config.getAdjustClassNames();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("加载" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "模块" + config.getName() + "注册的新安装代码调整类没有加载成功："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof INewInstallAdjust) {
				if (obj instanceof AbstractNewInstallAdjust) {
					AbstractNewInstallAdjust adjust = (AbstractNewInstallAdjust) obj;
					adjust.setConfig(config);
				}
				al.add(obj);
			} else {
				String msg = "模块"
						+ config.getName()
						+ "注册的代码调整类没有没有实现nc.bs.sm.accountmanage.INewInstallAdjust接口："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================加载代码调整类结束==========================================================");
		return (INewInstallAdjust[]) al.toArray(new INewInstallAdjust[0]);
	}

	private IPatchInstall[] getPatchInstall(ModuleConfig config) {
		ArrayList<Object> al = new ArrayList<Object>();
		InstallLogTool
				.log("=======================加载补丁调整:======================================================");
		String[] classNames = config.getPatchinstallclassnemes();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("加载" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "模块" + config.getName() + "注册的补丁安装调整类没有加载成功："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof IPatchInstall) {
				al.add(obj);
			} else {
				String msg = "模块"
						+ config.getName()
						+ "注册的代码调整类没有没有实现nc.bs.sm.accountmanage.IPatchInstall接口："
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================加载代码调整类结束==========================================================");
		return (IPatchInstall[]) al.toArray(new IPatchInstall[0]);
	}

	/**
	 * 获取待安装的产品树
	 */
	public CheckTreeNode getProductTree(String dsName) throws BusinessException {
		AccountInstallDAO dao = null;
		ArrayList<String> al = null;
		try {
			dao = new AccountInstallDAO(dsName);
			al = dao.getInstalledMoudleCodes();
		} catch (Exception e) {
		} finally {
			if (dao != null)
				dao.close();
		}
		if (al == null) {
			al = new ArrayList<String>();
		}
		return DBInstallTreeScaner.getBCManageTreeWithHidden(
				ISysConstant.ncHome, al);

	}

	public InstalledModule[] getInstalledModules(String dsName)
			throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModules();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	/**
	 * 根据编码查询已安转的模块，如果没有安装，返回null
	 * 
	 * @deprecated
	 */
	public InstalledModule getInstalledModuleByCode(String dsName,
			String moduleCode) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleByCode(moduleCode);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * @deprecated
	 * @param dsName
	 * @param config
	 * @return
	 * @throws BusinessException
	 */
	private InstalledModule getInstalledModuleByInfo(String dsName,
			ModuleConfig config) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleByInfo(config);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	private static CheckTreeNode[] getNeedInstallProduct(CheckTreeNode root) {
		return getNeedInstallNode(root);
	}

	// TODO::此处用Map给待安装的产品分个类，每个类别下对层级做排序，......Tomorrow
	private static CheckTreeNode[] getNeedInstallNode(CheckTreeNode node) {
		ArrayList al = new ArrayList();
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			CheckTreeNode child = (CheckTreeNode) node.getChildAt(i);
			if (child.isSelected()) {
				ModuleInfo minfo = child.getModuleInfo();
				if (minfo != null && minfo.isNewDisk())
					al.add(child);
			}
		}
		return (CheckTreeNode[]) al.toArray(new CheckTreeNode[0]);

	}

	private static CheckTreeNode[] getNeedInstallPatch(CheckTreeNode root) {
		ArrayList al = new ArrayList();
		Enumeration enumer = root.breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			CheckTreeNode node = (CheckTreeNode) enumer.nextElement();
			if (node.isLeaf() && node.isSelected()) {
				ModuleInfo minfo = node.getModuleInfo();
				if (minfo != null && !minfo.isNewDisk())
					al.add(node);
			}

		}

		return (CheckTreeNode[]) al.toArray(new CheckTreeNode[0]);

	}

	/**
	 * 创建数据库
	 */
	public void doInstallDB(DBInstallSetup installSetup)
			throws BusinessException {
		// 注册当前线程的数据源
		String dsName = installSetup.getDsName();
		registerDataSource(dsName);

		initInstalledInfo();
		//
		doneUAPCheckThreadLocal.set(false);
		langCodeThreadLocal.set(installSetup.getLangCode());
		//
		String progressID = installSetup.getProgressID();
		DBInstallProgress progress = InstallProgressCenter.getInstance()
				.getDBInstallProgress(progressID);
		progressThreadLocal.set(progress);
		// 设置内容多语
		dbmllist = installSetup.getDbmlcode();
		Logger.error("AccountManage.doInstallDB:need installed language: dbmllist===="
				+ dbmllist);
		// 开始安装
		CheckTreeNode root = installSetup.getProductScriptTreeRoot();
		checkTreeRootNodeThreadLocal.set(root);
		CheckTreeNode[] needInstallProducts = getNeedInstallProduct(root);
		CheckTreeNode[] needInstallPatchs = getNeedInstallPatch(root);

		// 记录安装顺序
		initInstalledInfoOrder();
		// 记录新盘安装顺序
		writeProductInstallOrder(needInstallProducts);

		// 记录补丁安装顺序
		writePatchInstallOrder(needInstallPatchs);

		int count = needInstallProducts.length;
		int patchCount = needInstallPatchs.length;
		progress.productCount = count + (patchCount > 0 ? 1 : 0);

		// 记录产品安装顺序
		ArrayList<IAllUpdateAccount> needClasses = WholeUpdateClassFactory
				.getNeedClasses();
		/** 升级前代码调整 */
		for (int i = 0; i < needClasses.size(); i++) {
			IAllUpdateAccount iAllUpdateAccount = needClasses.get(i);
			if (iAllUpdateAccount instanceof IAllUpdateAccount2) {
				IAllUpdateAccount2 update = (IAllUpdateAccount2) iAllUpdateAccount;
				Logger.error("执行整个升级前调整类" + update.getClass().getName());
				getAccountCreateService().wholeClassAdjust_RequiresNew(update,
						1);
			}

		}

		for (int i = 0; i < count; i++) {
			synchronized (InstallProgressCenter.class) {
				progress.currProductIndex = i;
				// progress.currProductName =
				// needInstallProducts[i].getModuleConfig().getName();
				progress.currProductName = NCLangResOnserver.getInstance()
						.getStrByID(
								"sfapp",
								needInstallProducts[i].getModuleInfo()
										.getModulecode());
				progress.currModuleIndex = 0;
			}
			doInstallProduct(needInstallProducts[i]);
		}
		if (patchCount > 0) {
			progress.currProductIndex = count;
			progress.currProductName = "模块补丁";
			progress.currModuleIndex = 0;
			doInstallPatchs(needInstallPatchs);

		}
		progress.currProductIndex = progress.productCount;

		/** 升级后代码调整 */
		for (int i = 0; i < needClasses.size(); i++) {
			IAllUpdateAccount iAllUpdateAccount = needClasses.get(i);
			Logger.error("执行整个升级后调整类" + iAllUpdateAccount.getClass().getName());
			getAccountCreateService().wholeClassAdjust_RequiresNew(
					iAllUpdateAccount, 2);
		}
	}

	/**
	 * 记录产品安装顺序
	 * 
	 * @param needInstallProducts
	 */
	private void writeProductInstallOrder(CheckTreeNode[] needInstallProducts) {
		Logger.error("firewolf product install order");
		if (needInstallProducts != null && needInstallProducts.length > 0) {
			for (int i = 0; i < needInstallProducts.length; i++) {
				CheckTreeNode product = needInstallProducts[i];
				LogProductInstallOrder(product);
			}
		}
	}

	/**
	 * 记录补丁安装顺序
	 * 
	 * @param needInstallProducts
	 */
	private void writePatchInstallOrder(CheckTreeNode[] needInstall) {
		Logger.error("firewolf patch install order");
		if (needInstall != null && needInstall.length > 0) {
			for (int i = 0; i < needInstall.length; i++) {
				CheckTreeNode product = needInstall[i];
				logInfo(product, i);
			}
		}
	}

	private void LogProductInstallOrder(CheckTreeNode product) {
		ModuleConfig config = product.getModuleInfo().getLvlConfig(0);
		Logger.error("order:  product=====code: " + config.getCode()
				+ "  name: " + config.getName() + "  level: "
				+ config.getLevel() + "  version: " + config.getVersion());
		CheckTreeNode[] needInstallModules = getNeedInstallNode(product);
		int count = needInstallModules.length;
		for (int i = 0; i < count; i++) {
			logInfo(needInstallModules[i], i);
		}
	}

	private void logInfo(CheckTreeNode module, int index) {
		ModuleInfo minfo = module.getModuleInfo();
		Integer[] levels = minfo.getLvlmap().keySet().toArray(new Integer[0]);
		Arrays.sort(levels);
		for (Integer integer : levels) {
			ModuleConfig lvlconfig = minfo.getLvlmap().get(integer);
			if (isHadInstalledLog(lvlconfig))
				continue;
			RelatedModule[] relatedModules = lvlconfig.getRelatedModules();
			for (int i = 0, count = relatedModules == null ? 0
					: relatedModules.length; i < count; i++) {
				String relatedCode = relatedModules[i].getCode();
				CheckTreeNode node = getNodeByCode(relatedCode);
				if (node != null && node.isSelected()) {
					if (node.isLeaf()) {// 目前，节点是叶子，说明是模块或补丁，
//						logInfo(node, index++);
					} else if (node.getModuleInfo().isNewDisk()) {// 不是叶子的新盘，应该是产品
//						LogProductInstallOrder((node)); 
					}
				}
			}
			Logger.error("order:  module====code:" + lvlconfig.getCode()
					+ "  name: " + lvlconfig.getName() + "  level: "
					+ lvlconfig.getLevel() + "  hycode: "
					+ lvlconfig.getHyCode() + "  version: "
					+ lvlconfig.getVersion());
			markInstalledOrder(lvlconfig);
		}
	}

	private boolean isHadInstalledLog(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocalOrder.get();
		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();
		if (al.contains(code)) {
			return true;
		} else {
			return false;
		}
	}

	private void markInstalledOrder(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocalOrder.get();

		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();

		al.add(code);
	}

	private void initInstalledInfoOrder() throws BusinessException {
		hadInstalledModuleThreadLocalOrder.set(null);
		ArrayList hadInstalledModule = new ArrayList();
		hadInstalledModuleThreadLocalOrder.set(hadInstalledModule);
		InstalledModule[] installedMosules = getInstalledModules(null);

		if (installedMosules != null && installedMosules.length > 0) {
			String keycode = null;
			for (InstalledModule imo : installedMosules) {
				keycode = imo.getCode() + "_new" + imo.getLevel()
						+ imo.getVersion() + SPLITER + imo.getHycode();
				hadInstalledModuleThreadLocalOrder.get().add(keycode);
			}

		}

	}

	private void initInstalledInfo() throws BusinessException {
		hadInstalledModuleThreadLocal.set(null);
		ArrayList hadInstalledModule = new ArrayList();
		hadInstalledModuleThreadLocal.set(hadInstalledModule);
		InstalledModule[] installedMosules = getInstalledModules(null);

		if (installedMosules != null && installedMosules.length > 0) {
			String keycode = null;
			for (InstalledModule imo : installedMosules) {
				keycode = imo.getCode() + "_new" + imo.getLevel()
						+ imo.getVersion() + SPLITER + imo.getHycode();
				hadInstalledModuleThreadLocal.get().add(keycode);
			}
		}
	}

	private void doInstallPatchs(CheckTreeNode[] patchs)
			throws BusinessException {
		int count = patchs == null ? 0 : patchs.length;
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		progress.moduleCount = count;
		for (int i = 0; i < count; i++) {
			doInstallModule(patchs[i], i);
		}

	}

	private void doInstallProduct(CheckTreeNode product)
			throws BusinessException {
		Logger.error("确保进了这个方法doInstallProduct");
		// 产品的话都认为是0层
		ModuleConfig config = product.getModuleInfo().getLvlConfig(0);
		CheckTreeNode[] needInstallModules = getNeedInstallNode(product);
		int count = needInstallModules.length;
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		progress.moduleCount = count;
		for (int i = 0; i < count; i++) {
			doInstallModule(needInstallModules[i], i);
		}

		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		getAccountCreateService().updateInstalledModuleVersion_RequiresNew(
				dsName, config);

	}

	private void createDB(TableUpdateProcessor tup, String moduleCode,
			String dbScriptPath) throws BusinessException {
		try {
			// 由于历史原因，对于10开头的模块，在建数据库时，统一使用“10”作为模块号
			if (moduleCode.trim().startsWith("10"))
				moduleCode = "10";

			// 进行数据库物理布局校验
			Verify verify = new Verify();
			InstallLogTool.log("开始进行数据库物理布局校验, moduleCode =" + moduleCode
					+ ", dbScriptPath=" + dbScriptPath);
			int verifyResult = verify.verify(moduleCode, dbScriptPath);
			if (verifyResult != 0)
				throw new Exception(NCLangResOnserver.getInstance().getStrByID(
						"102003", "UPP102003-000010")/*
													 * @res
													 * "对数据库物理布局校验失败，不能进行升级。原因请看后台日志"
													 */);

			InstallLogTool.log("开始进行数据库结构升级 , moduleCode =" + moduleCode
					+ ", dbScriptPath=" + dbScriptPath);
			// 库结构升级
			tup.process(moduleCode, dbScriptPath, null);
			InstallLogTool.log("数据库结构升级结束");
		} catch (Exception e) {
			InstallLogTool.logException(e);
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}

	}

	private boolean isHadInstalled(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocal.get();
		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();
		if (al.contains(code)) {
			return true;
		} else {
			return false;
		}
	}

	private void markInstalled(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocal.get();

		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();

		al.add(code);
	}

	private CheckTreeNode getNodeByCode(String code) {
		CheckTreeNode node = null;
		CheckTreeNode root = checkTreeRootNodeThreadLocal.get();
		Enumeration enumer = root.depthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			CheckTreeNode temp = (CheckTreeNode) enumer.nextElement();
			ModuleInfo minfo = temp.getModuleInfo();
			if (minfo != null && code.equals(minfo.getModulecode())) {
				Map<Integer, ModuleConfig> lvlmap = minfo.getLvlmap();
				for (Integer level : lvlmap.keySet()) {
					ModuleConfig moduleConfig = lvlmap.get(level);
					if (!moduleConfig.isVisible() || temp.isSelected()) {// 被选中或者是隐藏模块,都认为已经选了，
						node = temp;
						return node;
					}
				}
			}
		}
		return node;
	}

	private void doInstallModule(CheckTreeNode module, int progressIndex)
			throws BusinessException {
		Logger.error("确保进了这个方法");

		ModuleInfo minfo = module.getModuleInfo();
		String moduleCode = minfo.getModulecode();

		Integer[] levels = minfo.getLvlmap().keySet().toArray(new Integer[0]);
		Arrays.sort(levels);

		for (Integer integer : levels) {
			ModuleConfig lvlconfig = minfo.getLvlmap().get(integer);
			if (!lvlconfig.isVisible() || minfo.getLevelSelect(integer)) {
				if (isHadInstalled(lvlconfig))
					continue;

				RelatedModule[] relatedModules = lvlconfig.getRelatedModules();
				for (int i = 0, count = relatedModules == null ? 0
						: relatedModules.length; i < count; i++) {
					String relatedCode = relatedModules[i].getCode();
					CheckTreeNode node = getNodeByCode(relatedCode);
					if (node != null) {
						if (node.isLeaf()) {// 目前，节点是叶子，说明是模块或补丁，
//							doInstallModule(node, progressIndex++);
						} else if (node.getModuleInfo().isNewDisk()) {// 不是叶子的新盘，应该是产品
//							doInstallProduct(node);
						}
					}
				}

				CheckTreeNode parentNode = (CheckTreeNode) module.getParent();
				if (parentNode != null) {
					DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
							.get();

					synchronized (InstallProgressCenter.class) {
						progress.currProductName = NCLangResOnserver
								.getInstance().getStrByID(
										"sfapp",
										parentNode.getModuleInfo()
												.getModulecode());
					}
				}
				doinstallSingleModule(progressIndex, lvlconfig);
			}

		}

	}

	private void doinstallSingleModule(int progressIndex, ModuleConfig config)
			throws BusinessException {

		String moduleName = config.getName();
		String moduleVersion = config.getVersion();
		String moduleCode = config.getCode();
		// liuxing0添加
		boolean isNewDisk = true;
		boolean isUpdate = false;
		boolean isNeedSqltrans = true; // 是否需要根据SqlTrans配置文件将insert语句改变成update语句
		int[] types = null;// 是一个数组，type[0]用来表示是安装类型，0表示新装，1表示升级；type[1]表示安装完后当前模块信息对于sm_product_version的操作:0表示insert，1表示update
		// 新加一个type[2]用来表示对于business中的数据是不是要根据SqlTrans来转换成update，还是直接转换成update，0表示前者，1表示后者

		// =========下面一段为测试安装代码，比较有用留着
		// if(config!=null){
		// System.out.println("XXXXXXXXXXX从日志来看已经安装了模块"+config.getCode()+"##Level##  "+config.getLevel());
		// Logger.error("XXXXXXXXXXX从日志来看已经安装了模块"+config.getCode()+"##Level##  "+config.getHyCode());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// getAccountCreateService().updateInstalledModuleVersion_RequiresNew(dsName,
		// config);
		// markInstalled(config);
		// return;
		// }
		// =========以上是测试安装的好助手=============

		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		synchronized (InstallProgressCenter.class) {
			progress.currModuleIndex = progressIndex;
			// progress.currModuleName = module.getModuleConfig().getName();
			progress.currModuleName = NCLangResOnserver.getInstance()
					.getStrByID("sfapp", config.getCode());
			progress.currSubstep = "";
			progress.subStepPercent = 0;
			progress.currDetail = "";

		}

		Logger.error("start install module : modulecode=" + moduleCode
				+ ",level=" + config.getLevel() + ",version=" + moduleVersion
				+ ",modulename=" + moduleName + ",configPath="
				+ config.getConfigFilePath());
		//
		InstallLogTool.log("开始安装模块:" + moduleName + ",   moduleCode='"
				+ moduleCode + "'");
		// 查询以前的版本号
		// String strCode = moduleCode;
		// if(moduleCode.startsWith("10")){
		// String preGeneCode = config.getPreviousGenerationCode();
		// if (preGeneCode != null && preGeneCode.trim().length() > 0) {
		// strCode = preGeneCode;
		// }
		// }
		// InstalledModule installedModule = null;
		DBInstalledModule installedModule = null;

		installedModule = getInstalledModuleInfoByInfo(null, config);
		if (installedModule == null) {
			String preGeneCode = config.getPreviousGenerationCode();
			if (preGeneCode != null && preGeneCode.trim().length() > 0) {
				StringTokenizer st = new StringTokenizer(preGeneCode, ",");
				while (st.hasMoreTokens()) {
					String tCode = st.nextToken().trim();
					installedModule = getInstalledModuleInfoByCode(null, tCode);
					if (installedModule != null) {
						break;
					}
				}
			}
		}

		// liuxing0注掉
		// if (installedModule == null)
		// installedModule = getInstalledModuleByInfo(null, config);
		// boolean isNewDisk = config.isNewDisk();
		// boolean isUpdate = !isNewDisk || installedModule != null;
		// liuxing0新加
		isNewDisk = config.isNewDisk();
		types = getInstallAndOperType(null, config); // 获取安装类型和对数据库的操作类型
		isUpdate = types[0] == 1 ? true : false;
		isUpdate = isUpdate || (installedModule != null);
		isNeedSqltrans = (types[2] == 1) ? false : true;
		isNeedSqltrans = isUpdate;

		if (isUpdate) {
			Logger.error("update module" + config.getCode());
			InstallLogTool.log("对模块:'" + moduleName + "'进行升级安装");
		} else {
			Logger.error("install module" + config.getCode());
			InstallLogTool.log("对模块:'" + moduleName + "'进行新安装");
		}
		// 升级检查不走注释
		// if (moduleCode.startsWith("00") && isNewDisk && installedModule !=
		// null && !config.getVersion().equals(installedModule.getVersion())
		if (moduleCode.startsWith("00") && isNewDisk
				&& !doneUAPCheckThreadLocal.get()) {
			doneUAPCheckThreadLocal.set(true);
			String dsName = InvocationInfoProxy.getInstance()
					.getUserDataSource();
			doneUAPCheck(dsName, config);
		}
		// 新装产品数据库结构升级前代码调整
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "正在进行新安装的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().newInstallAdjust_RequiresNew(
								adjusts[i], moduleVersion, AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

		//
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		//
		IUpdateAccount[] defaultUpdateAccounts = getDefaultUpdateAccount(
				config, isNewDisk, isUpdate);

		int count = defaultUpdateAccounts == null ? 0
				: defaultUpdateAccounts.length;

		// AU中配置的代码调整
		if (count > 0 && isNeedHandleAU(config)) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000000")/*
																			 * @res
																			 * "正在进行数据库结构升级前的默认代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 3;
			for (int i = 0; i < count; i++) {
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}
		IUpdateAccount[] updateAccounts = getUpdateAccounts(config);
		// 数据库结构升级前的代码调整
		if (isUpdate && updateAccounts.length > 0) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000002")/*
																			 * @res
																			 * "正在进行数据库结构升级前的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 5;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}
		// 数据库结构升级
		TableUpdateProcessor tup = new TableUpdateProcessor();
		String configFile = config.getConfigFilePath();
		File configFileParent = new File(configFile).getParentFile();
		String dbCreateName = config
				.getUnicodeString(ConfigKey.CONFIG_DB_CREATE_SCRIPT);
		if (!isNullStr(dbCreateName)) {
			String dbScriptPath = configFileParent.getPath() + "/"
					+ dbCreateName + "/";
			dbScriptPath = dbScriptPath.replace('\\', '/');
			if (new File(dbScriptPath).exists()) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000003")/*
															 * @res
															 * "正在进行数据库结构升级"
															 */;
				progress.subStepPercent = 10;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000004")/*
															 * @res "库结构脚本路径:"
															 */
						+ getString(dbScriptPath);
				try {

					createDB(tup, moduleCode, dbScriptPath);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					throw e;
				}

				// 系统升级的时候更新了表结构，升级完成后需要清空表缓存，否则有可能导致DAO操作的时候字段不是最新的，执行失败
				JdbcPersistenceManager.clearAllTableInfo();

			}
			// 数据字典升级
			// 2010-5-6 暂时去掉数据字典升级，看元数据是否能完全取代再定是否使用
			// String dataDictScriptPath = dbScriptPath + "SQLSERVER";
			// if(new File(dataDictScriptPath).exists()){
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000005")/*@res
			// "正在进行数据字典升级"*/;
			// progress.subStepPercent = 20;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000006")/*@res
			// "数据字典脚本路径:"*/ + getString(dataDictScriptPath);
			// getAccountCreateService().installDataDict_RequiresNew(dataDictScriptPath);
			// }
		}
		// 新装产品数据初始化前代码调整
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "正在进行新安装的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().newInstallAdjust_RequiresNew(
								adjusts[i], moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

		// AU中代码调整
		if (count > 0 && isNeedHandleAU(config)) {
			for (int i = 0; i < count; i++) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000007")/*
															 * @res
															 * "正在进行数据升级前的默认代码调整"
															 */;
				InstallLogTool.log(progress.currSubstep);
				progress.subStepPercent = 23;
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}

		// 数据升级前的代码调整
		if (isUpdate && updateAccounts.length > 0) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000008")/*
																			 * @res
																			 * "正在进行数据升级前的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 25;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());

					}

				}
			}
		}

		// 数据库的删除列
		// v502版去掉删除列的操作，避免删除用户增加的字段，2008/1/19
		// if (isUpdate && !isNullStr(dbCreateName)) {
		// try {
		// tup.process_delcolumn();
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		// }

		// 数据升级
		progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
				.getStrByID("accountmanager", "UPPaccountmanager-000009")/*
																		 * @res
																		 * "正在进行数据升级"
																		 */;
		progress.subStepPercent = 30;
		Logger.error("开始数据升级");
		doDataUpdate(config, isUpdate, isNeedSqltrans);
		Logger.error("数据升级后AU代码调整" + configFile);
		// AU中初始化数据后代码调整
		if (count > 0 && isNeedHandleAU(config)) {

			for (int i = 0; i < count; i++) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000010")/*
															 * @res
															 * "正在进行数据升级后的默认代码调整"
															 */;
				InstallLogTool.log(progress.currSubstep);
				progress.subStepPercent = 93;
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}
		Logger.error("数据升级后代码调整");
		// 数据升级后的代码调整
		if (isUpdate && updateAccounts.length > 0) {

			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000011")/*
																			 * @res
																			 * "正在进行数据升级后的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 95;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());

					}

				}

			}
		}
		progress.subStepPercent = 98;

		// 执行新安装的代码调整
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "正在进行新安装的代码调整"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "开始运行"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService()
								.newInstallAdjust_RequiresNew(adjusts[i],
										moduleVersion, AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}
		// 记录安装版本号
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		// liuxing0注掉
		// getAccountCreateService().updateInstalledModuleVersion_RequiresNew(dsName,
		// config);
		getAccountCreateService().updateInstalledModuleInfo_RequiresNew(
				types[1], dsName, config);

		//
		markInstalled(config);
		// since 60 注掉，iufo和portal与一般的一样--------增加一段代码，对iufo和portal进行数据源的设置
		// if ("iufo".equalsIgnoreCase(moduleCode) ||
		// "99".equalsIgnoreCase(moduleCode)) {
		// IAppendProductConfService service = (IAppendProductConfService)
		// NCLocator.getInstance().lookup(IAppendProductConfService.class.getName());
		// if ("iufo".equalsIgnoreCase(moduleCode)) {
		// service.setIUFODsName(dsName);
		// }
		// // ewei since60 portal按一般处理方式处理
		// // else if("99".equalsIgnoreCase(moduleCode)){
		// // service.setNCPortalDsName(dsName);
		// // }
		// }
		//
		progress.subStepPercent = 100;
		progress.currDetail = "";
	}

	private DBInstalledModule getInstalledModuleInfoByCode(String dsName,
			String tCode) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleInfoByCode(tCode);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	private DBInstalledModule getInstalledModuleInfoByInfo(String dsName,
			ModuleConfig config) throws BusinessException {
		DBInstalledModule installedModule = null;
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			installedModule = dao.getInstalledModuleInfoByInfo(config);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return installedModule;
			// throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
		return installedModule;
	}

	/**
	 * 获取对当前模块的处理方式：升级或者安装； 升级完后对数据库的操作类型：insert或者update
	 * 
	 * @param config
	 * @return int[]
	 *         type[0]用来表示是安装类型，0表示新装，1表示升级；type[1]表示安装完后当前模块信息对于sm_product_version的操作
	 *         :0表示insert，1表示update
	 *         type[2]用来表示将insert语句改变成update要不要通过SqlTrans配置文件来确定
	 *         ，0表示需要，1表示不需要直接变成update
	 * @throws BusinessException
	 */
	private int[] getInstallAndOperType(String dsName, ModuleConfig config)
			throws BusinessException {
		String currLevel = config.getLevel() + "";
		int[] types = new int[] { 0, 0, 0 };
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			ArrayList<String> levelsStr = dao.getdevLevelsStrByCode(config
					.getCode());
			if (null == levelsStr || 0 == levelsStr.size()) {// 数据库中没有当前模块的信息：新装，insert
				types[0] = 0;
				types[1] = 0;
				types[2] = 0;
			} else if (1 == levelsStr.size()) {// 数据库中有一条记录
				if (null == levelsStr.get(0)) {// 已经安装的是63的：升级，insert
					types[0] = 1;
					types[1] = 0;
					types[2] = 0;
				} else if (levelsStr.get(0).equals(currLevel)) {// 已经安装的那一个和当前安装的层次相同：升级，update当前level的
					types[0] = 1;
					types[1] = 1;
					types[2] = 0;
				} else {// 已经安装的和当前的不同层次：新装，insert
					types[0] = 0;
					types[1] = 0;
					types[2] = 1;
				}
			} else { // 数据库中已经存在多个
				if (levelsStr.contains(null)) { // 有63的结构：升级，update库中level为当前level或者为null的
					types[0] = 1;
					types[1] = 1;
					types[2] = 1;
				} else {
					if (levelsStr.contains(currLevel)) { // 要安装的层次已经安装：升级，update
						types[0] = 1;
						types[1] = 1;
						types[2] = 1;
					} else {// 要安装的不存在，新装，insert
						types[0] = 0;
						types[1] = 0;
						types[2] = 1;
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
		return types;
	}

	private boolean isNeedHandleAU(ModuleConfig config) {

		String handleflag = config.getUnicodeString("isNeedHandleAU");
		if (handleflag != null && "N".equalsIgnoreCase(handleflag)) {
			return false;
		}

		return true;
	}

	private void doneUAPCheck(String dsName, ModuleConfig config)
			throws BusinessException {
		HashMap<String, AccCheckConfVO> hm = AccCheckConfReader
				.loadAccCheckConfigs();
		try {
			List<String> installedModule = new AccountInstallDAO(dsName)
					.getInstalledMoudleCodes();
			Iterator<String> iter = hm.keySet().iterator();
			while (iter.hasNext()) {
				String code = (String) iter.next();
				if (installedModule.contains(code)) {
					AccChecker.runChecker(hm.get(code), config);
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	private boolean isNullStr(String s) {
		return s == null || s.trim().length() == 0;
	}

	private void doDataUpdate(ModuleConfig config, boolean isUpdate,
			boolean isNeedSqlTrans) throws BusinessException {
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		ArrayList<String> ignoredScriptFilePath = config
				.getIgnoredScriptFilePath();
		try {
			// dao = new AccountInstallDAO();
			String configFile = config.getConfigFilePath();
			File configFileParent = new File(configFile).getParentFile();
			boolean hasDynamicTempletData = config.isHasDynTempletData();
			// 单据模板：
			String billTempName = config
					.getUnicodeString(ConfigKey.CONFIG_BILL_TEMPLET_SCRIPT);
			String billTempletScriptFilePath = configFileParent.getPath() + "/"
					+ billTempName + "/";
			if (!isNullStr(billTempName)) {
				if (config.isNewDisk()) {
					InstallLogTool.log("开始初始化单据模版数据:"
							+ billTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000013")/*
																 * @res
																 * "正在安装单据模板"
																 */;
					progress.subStepPercent = 30;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000014")/*
																 * @res
																 * "单据模板脚本路径:"
																 */
							+ getString(billTempletScriptFilePath);
					// installBillTemplet(new File(billTempletScriptFilePath),
					// isUpdate, hasDynamicTempletData, ignoredScriptFilePath);
					execScript(new File(billTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("开始安装单据模版补丁脚本:"
							+ billTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000013")/*
																 * @res
																 * "正在安装单据模板"
																 */;
					progress.subStepPercent = 30;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000014")/*
																 * @res
																 * "单据模板脚本路径:"
																 */
							+ getString(billTempletScriptFilePath);
					execScript(new File(billTempletScriptFilePath),
							ignoredScriptFilePath, true);
					// installBillTempletPatch(new
					// File(billTempletScriptFilePath), hasDynamicTempletData);
				}
				if (isNeedInstallBlob(config, billTempName)) {
					processMouduleBlob(new File(billTempletScriptFilePath));
				}
			}
			// 查询模板：
			String queryTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_QUERY_TEMPLET_SCRIPT);
			if (!isNullStr(queryTempletName)) {
				String queryTempletScriptFilePath = configFileParent.getPath()
						+ "/" + queryTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("开始初始化查询模版数据:"
							+ queryTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000015")/*
																 * @res
																 * "正在安装查询模板"
																 */;
					progress.subStepPercent = 35;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000016")/*
																 * @res
																 * "查询模板脚本路径:"
																 */
							+ getString(queryTempletScriptFilePath);
					execScript(new File(queryTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("开始安装查询模版补丁脚本:"
							+ queryTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000015")/*
																 * @res
																 * "正在安装查询模板"
																 */;
					progress.subStepPercent = 35;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000016")/*
																 * @res
																 * "查询模板脚本路径:"
																 */
							+ getString(queryTempletScriptFilePath);
					// installQuerytempletPatch(new
					// File(queryTempletScriptFilePath), hasDynamicTempletData);
					execScript(new File(queryTempletScriptFilePath),
							ignoredScriptFilePath, true);

				}
				// 2011-9-14,ewei 注释掉，查询模板也默认安装blob
				// if(isNeedInstallBlob(config,queryTempletName)){
				processMouduleBlob(new File(queryTempletScriptFilePath));
				// }
			}
			// 报表模板：
			String reportTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_REPORT_TEMPLET_SCRIPT);
			if (!isNullStr(reportTempletName)) {
				String reportTempletScriptFilePath = configFileParent.getPath()
						+ "/" + reportTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("开始初始化报表模版数据:"
							+ reportTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000017")/*
																 * @res
																 * "正在安装报表模版"
																 */;
					progress.subStepPercent = 40;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000018")/*
																 * @res
																 * "报表模版脚本路径:"
																 */
							+ getString(reportTempletScriptFilePath);
					execScript(new File(reportTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("开始初始化报表模版补丁脚本:"
							+ reportTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000017")/*
																 * @res
																 * "正在安装报表模版"
																 */;
					progress.subStepPercent = 40;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000018")/*
																 * @res
																 * "报表模版脚本路径:"
																 */
							+ getString(reportTempletScriptFilePath);
					// installReporttempletPatch(new
					// File(reportTempletScriptFilePath),
					// hasDynamicTempletData);
					execScript(new File(reportTempletScriptFilePath),
							ignoredScriptFilePath, true);
				}
				if (isNeedInstallBlob(config, reportTempletName)) {
					processMouduleBlob(new File(reportTempletScriptFilePath));
				}
			}
			// 打印模板：
			String printTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_PRINT_TEMPLET_SCRIPT);
			if (!isNullStr(printTempletName)) {
				String printTempletScriptFilePath = configFileParent.getPath()
						+ "/" + printTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("开始初始化打印模版数据:"
							+ printTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000019")/*
																 * @res
																 * "正在安装打印模板"
																 */;
					progress.subStepPercent = 45;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000020")/*
																 * @res
																 * "打印模板脚本路径:"
																 */
							+ getString(printTempletScriptFilePath);
					execScript(new File(printTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("开始初始化打印模版补丁脚本:"
							+ printTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000019")/*
																 * @res
																 * "正在安装打印模板"
																 */;
					progress.subStepPercent = 45;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000020")/*
																 * @res
																 * "打印模板脚本路径:"
																 */
							+ getString(printTempletScriptFilePath);
					installPrinttempletPatch(new File(
							printTempletScriptFilePath), hasDynamicTempletData);

				}
				if (isNeedInstallBlob(config, printTempletName)) {
					processMouduleBlob(new File(printTempletScriptFilePath));
				}
			}
			// 单据类型：
			String billTypeScriptName = config
					.getUnicodeString(ConfigKey.CONFIG_BILL_TYPE_SCRIPT);
			if (!isNullStr(billTypeScriptName)) {
				String billTypeScriptFilePath = configFileParent.getPath()
						+ "/" + billTypeScriptName + "/";
				InstallLogTool.log("开始初始化单据类型数据:" + billTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000021")/*
															 * @res "正在安装单据类型"
															 */;
				progress.subStepPercent = 50;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000022")/*
															 * @res "单据类型脚本路径:"
															 */
						+ getString(billTypeScriptFilePath);
				// installBillType(new File(billTypeScriptFilePath), isUpdate,
				// ignoredScriptFilePath);
				execScript(new File(billTypeScriptFilePath),
						ignoredScriptFilePath, true);
				if (isNeedInstallBlob(config, billTypeScriptName)) {
					processMouduleBlob(new File(billTypeScriptFilePath));
				}
			}
			// 业务类型：
			String busiTypeName = config
					.getUnicodeString(ConfigKey.CONFIG_BUSI_TYPE_SCRIPT);
			if (!isNullStr(busiTypeName)) {
				String busiTypeScriptFilePath = configFileParent.getPath()
						+ "/" + busiTypeName + "/";
				InstallLogTool.log("开始初始化业务类型数据:" + busiTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000023")/*
															 * @res "正在安装业务类型"
															 */;
				progress.subStepPercent = 55;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000024")/*
															 * @res "业务类型脚本路径:"
															 */
						+ getString(busiTypeScriptFilePath);
				execScript(new File(busiTypeScriptFilePath),
						ignoredScriptFilePath, true);
				if (isNeedInstallBlob(config, busiTypeName)) {
					processMouduleBlob(new File(busiTypeScriptFilePath));
				}
			}
			// 系统类型：
			String sysTypeName = config
					.getUnicodeString(ConfigKey.CONFIG_SYSTEM_TYPE_SCRIPT);
			if (!isNullStr(sysTypeName)) {
				String sysTypeScriptFilePath = configFileParent.getPath() + "/"
						+ sysTypeName + "/";
				InstallLogTool.log("开始初始化系统类型数据:" + sysTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000025")/*
															 * @res "正在安装系统类型"
															 */;
				progress.subStepPercent = 60;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000026")/*
															 * @res "系统类型脚本路径:"
															 */
						+ getString(sysTypeScriptFilePath);
				execScript(new File(sysTypeScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, sysTypeName)) {
					processMouduleBlob(new File(sysTypeScriptFilePath));
				}
			}
			// 科目分类
			String subClassName = config
					.getUnicodeString(ConfigKey.CONFIG_SUBJ_CLASS_SCRIPT);
			if (!isNullStr(subClassName)) {
				String subClassScriptFilePath = configFileParent.getPath()
						+ "/" + subClassName + "/";
				InstallLogTool.log("开始初始化科目分类数据:" + subClassScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000027")/*
															 * @res "正在安装科目分类"
															 */;
				progress.subStepPercent = 65;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000028")/*
															 * @res "科目分类脚本路径:"
															 */
						+ getString(subClassScriptFilePath);
				execScript(new File(subClassScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, subClassName)) {
					processMouduleBlob(new File(subClassScriptFilePath));
				}
			}
			// 凭证模板：
			String voucherTempName = config
					.getUnicodeString(ConfigKey.CONFIG_VOUCHER_TEMPLET_SCRIPT);
			if (!isNullStr(voucherTempName)) {
				String voucherTempletScriptFilePath = configFileParent
						.getPath() + "/" + voucherTempName + "/";
				InstallLogTool.log("开始初始化凭证模板数据:"
						+ voucherTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000029")/*
															 * @res "正在安装凭证模板"
															 */;
				progress.subStepPercent = 70;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000030")/*
															 * @res "凭证模板脚本路径:"
															 */
						+ getString(voucherTempletScriptFilePath);
				execScript(new File(voucherTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, voucherTempName)) {
					processMouduleBlob(new File(voucherTempletScriptFilePath));
				}
			}
			// 项目模板：
			String projectTempName = config
					.getUnicodeString(ConfigKey.CONFIG_PROJECT_TEMPLET_SCRIPT);
			if (!isNullStr(projectTempName)) {
				String projectTempletScriptFilePath = configFileParent
						.getPath() + "/" + projectTempName + "/";
				InstallLogTool.log("开始初始化项目模板数据:"
						+ projectTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000031")/*
															 * @res "正在安装项目模板"
															 */;
				progress.subStepPercent = 75;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000032")/*
															 * @res "项目模板脚本路径:"
															 */
						+ getString(projectTempletScriptFilePath);
				execScript(new File(projectTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, projectTempName)) {
					processMouduleBlob(new File(projectTempletScriptFilePath));
				}
			}
			// 系统模板：
			String sysTempName = config
					.getUnicodeString(ConfigKey.CONFIG_SYS_TEMPLET_SCRIPT);
			if (!isNullStr(sysTempName)) {
				String sysTempletScriptFilePath = configFileParent.getPath()
						+ "/" + sysTempName + "/";
				InstallLogTool.log("开始初始化系统模板数据:" + sysTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000033")/*
															 * @res "正在安装系统模板"
															 */;
				progress.subStepPercent = 80;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000034")/*
															 * @res "系统模板脚本路径:"
															 */
						+ getString(sysTempletScriptFilePath);
				execScript(new File(sysTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, sysTempName)) {
					processMouduleBlob(new File(sysTempletScriptFilePath));
				}
			}
			// 产品组内脚本：
			String businessName = config
					.getUnicodeString(ConfigKey.CONFIG_BUSINESS_SCRIPT);
			if (!isNullStr(businessName)) {
				String businessScriptFilePath = configFileParent.getPath()
						+ "/" + businessName + "/";
				InstallLogTool.log("开始初始化产品组内数据:" + businessScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000035")/*
															 * @res "正在安装产品组内脚本"
															 */;
				progress.subStepPercent = 85;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000036")/*
															 * @res
															 * "产品组内脚本脚本路径:"
															 */
						+ getString(businessScriptFilePath);
				execScript(new File(businessScriptFilePath),
						ignoredScriptFilePath, isUpdate, isNeedSqlTrans);
				InstallLogTool.logErr("================开始安装模块内blob:"
						+ config.getUnicodeString(ConfigKey.CONFIG_CODE)
						+ "=============");
				processMouduleBlob(new File(businessScriptFilePath));
				InstallLogTool.logErr("================结束安装模块内blob:"
						+ config.getUnicodeString(ConfigKey.CONFIG_CODE)
						+ "=============");
			}
			// 菜单脚本
			String menuName = config
					.getUnicodeString(ConfigKey.CONFIG_MENU_SCRIPT);
			if (!isNullStr(menuName)) {
				String menuScriptFilePath = configFileParent.getPath() + "/"
						+ menuName + "/";
				InstallLogTool.log("开始初始化菜单脚本数据:" + menuScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000037")/*
															 * @res "正在安装菜单脚本"
															 */;
				progress.subStepPercent = 90;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000038")/*
															 * @res "菜单脚本路径:"
															 */
						+ getString(menuScriptFilePath);
				execScript(new File(menuScriptFilePath), ignoredScriptFilePath,
						true);
				if (isNeedInstallBlob(config, menuName)) {
					processMouduleBlob(new File(menuScriptFilePath));
				}
			}
			// // 元数据脚本
			// String metadata =
			// config.getUnicodeString(ConfigKey.CONFIG_METADATA_SCRIPT);
			// if (!isNullStr(metadata)) {
			// String metadataScriptFilePath = configFileParent.getPath() + "/"
			// + metadata + "/";
			// InstallLogTool.log("开始初始化元数据脚本数据:" + metadataScriptFilePath);
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000075")/*@res
			// "正在安装元数据脚本"*/;
			// progress.subStepPercent = 93;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000076")/*@res
			// "元数据脚本路径:"*/ + getString(metadataScriptFilePath);
			// execScript(new
			// File(metadataScriptFilePath),ignoredScriptFilePath);
			// }
			String updateMD = config.getUnicodeString("updateMetaData");
			// 新增属性，如果标识为不发布元数据，那么新增时也不会发布,一定为false才不发布
			String publishMD = config.getUnicodeString("publishMetaData");
			IPatchInstall[] patches = getPatchInstall(config);
			if ((config.isNewDisk() && (publishMD == null || !publishMD.trim()
					.equalsIgnoreCase("false")))
					|| (updateMD != null && updateMD.trim().equalsIgnoreCase(
							"true"))) {
				try {
					InstallLogTool.log("开始发布元数据");
					IPublishService service = (IPublishService) NCLocator
							.getInstance().lookup(
									IPublishService.class.getName());
					service.publishMetaDataForInstall(config.getModuleStamp());
				} catch (Exception e) {
					InstallLogTool.logException(e);
					throw new Exception("发布元数据异常：" + e.getMessage(), e);
				}
			} else if ((!config.isNewDisk())
					&& (patches != null && patches.length > 0)) {
				try {
					PatchInstallContext context = new PatchInstallContext();
					context.setConfig(config);
					context.setMultilanglist(dbmllist);
					context.setPatchVersions(getInstalledModulePatchVersionsByCode(config
							.getCode()));
					if (patches != null && patches.length > 0) {
						for (IPatchInstall iPatchInstall : patches) {
							iPatchInstall.pulishMetaData(context);
						}
					}
				} catch (Exception e) {
					InstallLogTool.logException(e);
					throw new Exception("发布补丁元数据异常：" + e.getMessage(), e);
				}
			}
			// 发布关联元数据,用于在uapother中使用的模块 ewei+
			InstallLogTool.log("================准备调用"
					+ config.getConfigFilePath() + "关联模块元数据发布=============");
			String publishModule = config.getUnicodeString("publishModule");
			processMDPublish(publishModule);
			InstallLogTool.log("================结束调用关联模块元数据发布=============");

			// 多语化的脚本
			String mlScript = config
					.getUnicodeString(ConfigKey.CONFIG_ML_SCRIPT);
			if (!isNullStr(mlScript)) {
				String mlDirPath = configFileParent.getPath() + "/" + mlScript
						+ "/";
				String defLang = (String) langCodeThreadLocal.get();
				if (defLang == null) {
					defLang = "simpchn";
				}
				mlDirPath += defLang;
				InstallLogTool.log("执行多语化脚本：" + mlDirPath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000039")/*
															 * @res "正在安装多语化脚本"
															 */;
				progress.subStepPercent = 95;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000040")/*
															 * @res "多语化脚本路径:"
															 */
						+ getString(mlDirPath);
				execScript(new File(mlDirPath), ignoredScriptFilePath, true);
			}
			// 数据字典初始化数据
			// String ddcName =
			// config.getUnicodeString(ConfigKey.CONFIG_DDC_INITDATA);
			// if (!isNullStr(ddcName)) {
			// String ddcInitDataFilePath = configFileParent.getPath() + "/" +
			// ddcName + "/";
			// InstallLogTool.log("开始初始化数据字典数据:" + ddcInitDataFilePath);
			// File ddcDataDir = new File(ddcInitDataFilePath);
			// if (ddcDataDir.exists() && ddcDataDir.isDirectory()) {
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
			// "UPPaccountmanager-000041")/*
			// * @res
			// * "正在安装数据字典初始化数据"
			// */;
			// progress.subStepPercent = 97;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
			// "UPPaccountmanager-000042")/*
			// * @res
			// * "数据字典初始化数据路径:"
			// */
			// + getString(ddcInitDataFilePath);
			// InitForkey ifor = new InitForkey();
			// java.io.File files[] = ddcDataDir.listFiles();
			// for (int i = 0, n = (files == null ? 0 : files.length); i < n;
			// i++) {
			// ifor.init(files[i]);
			// }
			// }
			// if(isNeedInstallBlob(config,ddcName)){
			// processMouduleBlob(new File(ddcInitDataFilePath));
			// }
			// }
			String dbmlName = config
					.getUnicodeString(ConfigKey.CONFIG_DBML_SCRIPT);
			if (!isNullStr(dbmlName)) {
				String dbmlpath = configFileParent.getPath() + "/" + dbmlName
						+ "/";
				InstallLogTool.log("开始安装国际化数据多语资源:" + dbmlpath);
				File dbmlDataDir = new File(dbmlpath);
				if (dbmlDataDir.exists() && dbmlDataDir.isDirectory()) {
					progress.currSubstep = NCLangResOnserver.getInstance()
							.getStrByID("accountmanager",
									"AccountManageImpl-000000")/* 正在安装内容多语数据 */;
					progress.subStepPercent = 98;
					progress.currDetail = NCLangResOnserver.getInstance()
							.getStrByID("accountmanager",
									"AccountManageImpl-000001", null,
									new String[] { getString(dbmlpath) })/*
																		 * 内容多语路径{
																		 * 0}
																		 */;
					Logger.error("安装多语，多语目录：" + dbmlpath); /* -=notranslate=- */
					installDBML(new File(dbmlpath), isUpdate);
				}
			}
			Logger.error("安装多语：多语安装完成"); /* -=notranslate=- */
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
		}
	}

	/************************************** 处理uapother中模块元数据发布 ***********************/

	private void processMDPublish(String modulestr) throws Exception {
		if (modulestr == null || modulestr.isEmpty())
			return;
		// InstallLogTool.log("开始发布uapother元数据: "+modulestr);
		InstallLogTool.log("开始发布ncpub元数据: " + modulestr);
		String[] modules = modulestr.split(",");
		for (int i = 0; i < modules.length; i++) {
			try {
				// InstallLogTool.log("开始发布uapother元数据: "+modules[i]);
				InstallLogTool.log("开始发布ncpub元数据: " + modules[i]);
				IPublishService service = (IPublishService) NCLocator
						.getInstance().lookup(IPublishService.class.getName());
				service.publishMetaDataForInstall(modules[i]);
			} catch (Exception e) {
				InstallLogTool.logException(e);
				throw new Exception("开始发布uncpub元数据：" + modules[i]
						+ e.getMessage(), e);
			}
		}
	}

	/************************************** 处理blob ****************************************/
	private boolean isNeedInstallBlob(ModuleConfig config, String dirname) {
		String blobscript = config
				.getUnicodeString(ConfigKey.CONFIG_BLOB_SCRIPT);
		if (blobscript == null || blobscript.length() == 0)
			return false;
		if (blobscript.indexOf(dirname) > -1) {
			InstallLogTool.logErr("=================" + dirname
					+ "的blob字段需要进行升级处理===================");
		}
		return blobscript.indexOf(dirname) > -1;
	}

	private void processMouduleBlob(File file) throws FileNotFoundException,
			BusinessException, IOException {
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.getName().endsWith(".blob");
			}
		});
		if (files != null && files.length > 0) {
			for (File file2 : files) {
				InstallLogTool.logErr("=================扫描blob文件"
						+ file2.getName() + "===================");
				if (file2.isDirectory()) {
					processMouduleBlob(file2);
				} else {
					InstallLogTool.logErr("=================" + file
							+ "的blob字段正在进行升级处理===================");
					BLOBParser parser = new BLOBParser("UTF-8");
					parser.setProcessor(this);
					parser.load(new FileInputStream(file2));
				}
			}
		}

	}

	@Override
	public void processBlobValue(InputStream is, int length, String tablename,
			String pkname, String pkvalue, String columname) {
		try {
			if (length == 0)
				return;
			Connection con = getSession().getConnection();
			InstallLogTool.logErr("=================blob长度" + length
					+ "===================");
			InstallLogTool.logErr("=================blob表名" + tablename
					+ "===================");
			InstallLogTool.logErr("=================blob主键名" + pkname
					+ "===================");
			InstallLogTool.logErr("=================blob主键值" + pkvalue
					+ "===================");
			InstallLogTool.logErr("=================blob字段名名" + columname
					+ "===================");
			if (con instanceof CrossDBConnection) {
				String sql = "update " + tablename + " set " + columname
						+ "= ? where " + pkname + " = '" + pkvalue + "'";
				CrossDBPreparedStatement ps = null;
				try {
					byte[] datas = new byte[length];
					is.read(datas);
					InputStream iis = new ByteArrayInputStream(datas);
					CrossDBConnection crosscon = (CrossDBConnection) con;
					ps = (CrossDBPreparedStatement) crosscon
							.prepareStatement(sql);
					ps.setBinaryStream(1, iis);
					ps.execute();
				} catch (SQLException e) {
					Logger.error(e.getMessage(), e);
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
						if (con != null) {
							con.close();
						}
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	private JdbcSession getSession() throws DbException {
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		JdbcSession session = new JdbcSession(dsName);
		return session;
	}

	/***************************************************************************************************/

	private void installDBML(File dir, boolean isupdate)
			throws BusinessException {
		InstallLogTool.logErr("==================开始安装数据多语:"
				+ dir.getAbsolutePath() + "========================");
		Logger.error("dbmllist========" + dbmllist);
		if (dbmllist != null && dbmllist.size() > 0) {
			Map<String, Integer> langcodemap = new HashMap<String, Integer>();
			for (int i = 0; i < dbmllist.size(); i++) {
				langcodemap.put(dbmllist.get(i), i + 1);
			}
			Logger.error("安装多语：多语种类如下：" + langcodemap.toString()); /*
																	 * -=notranslate
																	 * =-
																	 */
			DBMLInstaller installer = new DBMLInstaller();
			installer.setLangmap(langcodemap);
			installer.exeGenScript(dir);
		}

	}

	/************************************ ewei+ 升级数据库多语脚本 ********************/

	private void execScript(File dir, ArrayList<String> ignorePathAL,
			boolean isupdate, boolean isNeedSqlTrans) throws BusinessException {
		if (!dir.exists())
			return;
		File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		int count = childFiles == null ? 0 : childFiles.length;
		childFiles = FileUtil.sortFileByName(childFiles);
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		for (int i = 0; i < count; i++) {
			File file = childFiles[i];
			if (file.isDirectory()) {
				execScript(file, ignorePathAL, isupdate, isNeedSqlTrans);
			} else {
				if (needExecScriptPath(file, ignorePathAL, isupdate)) {
					DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
							.get();
					InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
					Logger.error("start install sql,file path:"
							+ file.getPath());
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000043")/*
																 * @res
																 * "开始执行sql脚本:"
																 */
							+ getString(file.getPath());
					ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
					String[] sqls = (String[]) al.toArray(new String[0]);
					if (sqls.length > 0) {
						try {
							getAccountCreateService().execSqls_RequiresNew(
									dsName, sqls, isNeedSqlTrans);
						} catch (BusinessException e) {
							InstallLogTool.logException(e);
							Logger.error(e.getMessage(), e);
							throw e;
						}
					}
					InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
				} else {
					InstallLogTool.log(">>>sql脚本文件被忽略:" + file.getPath());
				}
			}

		}
	}

	private void execScript(File dir, ArrayList<String> ignorePathAL,
			boolean isNeedSqlTrans) throws BusinessException {
		execScript(dir, ignorePathAL, false, isNeedSqlTrans);
		// if (!dir.exists())
		// return;
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// execScript(file, ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000043")/*
		// * @res
		// * "开始执行sql脚本:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql脚本文件被忽略:" + file.getPath());
		// }
		// }
		//
		// }

	}

	private boolean needExecScriptPath(File file, ArrayList<String> ignorePathAL) {
		return needExecScriptPath(file, ignorePathAL, false);
	}

	private boolean needExecScriptPath(File file,
			ArrayList<String> ignorePathAL, boolean isupdate) {

		String path = file.getPath();
		path = path.replace('\\', '/');
		boolean needExec = true;
		int size = ignorePathAL == null ? 0 : ignorePathAL.size();
		for (int i = 0; i < size; i++) {
			String pathModel = ignorePathAL.get(i);
			pathModel = pathModel.replace('\\', '/');
			// if(StringUtil.match(pathModel, path)){
			if (path.matches(pathModel)) {
				needExec = false;
				break;
			}
		}

		if (isupdate) {
			String ignore = "/pub_bcr_rulebase/";
			if (path.contains(ignore)) {
				needExec = false;
			}
		}

		return needExec;
	}

	private void installBillTemplet(File dir, boolean isUpdate,
			boolean isHasDynamicTempletData, ArrayList<String> ignorePathAL)
			throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// if (!System.getProperties().contains(dir.getPath()))
		// System.setProperty(dir.getPath(), "0");
		// ITemplateInstall pfidu = (ITemplateInstall)
		// NCLocator.getInstance().lookup(ITemplateInstall.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillTemplet(file, isUpdate, isHasDynamicTempletData,
		// ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000044")/*
		// * @res
		// * "开始执行单据模版sql脚本:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// if (isUpdate) {
		// String str = System.getProperty(dir.getPath());
		// if (str.equals("0")) {
		// String[] billTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pk_billtemplet");
		// pfidu.installDeleteBillTemplet(billTempletKeyValAry,
		// isHasDynamicTempletData);
		// System.setProperty(dir.getPath(), "1");
		// }
		// }
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql脚本被忽略:" + file.getPath());
		// }
		// }
		//
		// }
	}

	private void installBillTempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// //
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillTempletPatch(file, isHasDynamicTempletData);
		// } else {
		// // if(needExecScriptPath(file, ignorePathAL)){
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail = "开始安装单据模版补丁脚本:" + getString(file.getPath());
		// ;//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "开始执行单据模版sql脚本:"*/ + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] billTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_billtemplet", "pk_billtemplet");
		// tp.pacthDeleteBillTemplet(billTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomBillTemplet(billTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(billTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// // }else{
		// // InstallLogTool.log(">>>sql脚本被忽略:" + file.getPath());
		// // }
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("对单据脚本进行调整：" + dir.getPath());
		// tp.ajustCustomBillTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installQuerytempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		//
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installQuerytempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail = "开始安装查询模版补丁脚本:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "开始执行单据模版sql脚本:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] queryTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_query_templet", "id");
		// tp.pacthdeleteQuerytemplet(queryTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomQueryTemplet(queryTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(queryTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("对查询模版脚本进行调整：" + dir.getPath());
		// tp.ajustCustomQueryTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installReporttempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installReporttempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail = "开始安装报表模版补丁脚本:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "开始执行单据模版sql脚本:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] reportTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_report_templet", "pk_templet");
		// tp.pacthdeleteReporttemplet(reportTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomReportTemplet(reportTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(reportTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("对报表模版脚本进行调整：" + dir.getPath());
		// tp.ajustCustomReportTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installPrinttempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installPrinttempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail = "开始安装打印模版补丁脚本:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "开始执行单据模版sql脚本:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] printTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_print_template", "ctemplateid");
		// tp.pacthdeletePrinttemplet(printTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// }
		//
		// }
	}

	private void installBillType(File dir, boolean isUpdate,
			ArrayList<String> ignorePathAL) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplateInstall pfidu = (ITemplateInstall)
		// NCLocator.getInstance().lookup(ITemplateInstall.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillType(file, isUpdate, ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// InstallLogTool.log(">>>开始执行sql脚本:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000045")/*
		// * @res
		// * "开始执行单据类型sql脚本:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// if (isUpdate) {
		// String[] billTypeKeyValAry = new
		// SqlParser(sqls).getValueByCol("pk_billtypecode");
		// pfidu.installDeleteBillType(billTypeKeyValAry);
		// }
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql脚本执行完毕:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql脚本被忽略:" + file.getPath());
		// }
		// }
		//
		// }
	}

	@Override
	public boolean isNewInstalOr65to65(String dsName) {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			if (!dao.isVersionTableExsist() || !dao.is63DBTable()) {// 表不存在，或者已经是65的版本表了，
				return true;
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return false;
	}

	private String getString(String str) {
		int len = 50;
		if (isNullStr(str)) {
			return str;
		} else if (str.length() <= len) {
			return str;
		} else {
			String s = str.substring(str.length() - len);
			return "..." + s;
		}

	}

	public String createDBInstallProgress() throws BusinessException {
		return InstallProgressCenter.getInstance().createProgress();
	}

	public void removeProgress(String id) throws BusinessException {
		InstallProgressCenter.getInstance().removeDBInstallProgress(id);
	}

	public DBInstallProgress getProgress(String id) throws BusinessException {
		return InstallProgressCenter.getInstance().getDBInstallProgress(id);

	}

	public CodeVerinfoTreeNode getCodeVerinfoTree() throws BusinessException {

		return CodeVerinfoTreeScaner.getTreeStruct(ISysConstant.ncHome);
	}

	public void doCreateDB(String moduleCode, String scriptPath, String dsName)
			throws BusinessException {
		TableUpdateProcessor tup = new TableUpdateProcessor();
		try {
			tup.processWithDbName(moduleCode, scriptPath, dsName, null);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	@Override
	public void updateDBML(Map<String, Integer> langmap)
			throws BusinessException {
		AppendDBML append = new AppendDBML();
		Logger.error("AccountManageImpl添加多语" + langmap.toString()); /*
																	 * -=notranslate
																	 * =-
																	 */
		append.appendDBML(langmap);
	}

	@Override
	public DBInstalledModule[] getDbInstallModules(String dsName)
			throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getDbInstalledModules();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}