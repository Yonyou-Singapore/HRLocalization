package nc.bs.update.db.compare;

/**
 * 此处插入类型说明。
 * 创建日期：(2002-5-28 12:27:37)
 * @author：Administrator
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import nc.bs.logging.Logger;
import nc.bs.update.db.IConstant;
import nc.bs.update.db.IXml;
import nc.bs.update.db.LogWriter;
import nc.bs.update.db.Record;
import nc.bs.update.db.table.SQLTransferMeaningUtil;

public class DbCompare {

	private DataInfo datainfo1 = null; // 数据库1数据库对象信息

	private DataInfo datainfo2 = null; // 数据库2数据库对象信息

	private ParamInfo paraminfo = null; // 参数信息

	/**
	 * DbCompare 构造子注解。
	 */
	public DbCompare() {
		super();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 13:26:23)
	 * 
	 * @return tools.DataInfo
	 * @param strOdbc
	 *            java.lang.String
	 * @param strUser
	 *            java.lang.String
	 * @param strPassword
	 *            java.lang.String
	 * @param paraminfo
	 *            tools.ParamInfo
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private DataInfo getDataInfo(String strOdbc, String strUser,
			String strPassword) throws java.lang.Exception {

		DataInfo datainfo = new DataInfo();
		String tableAry = getTables();
		if (tableAry == null)
			return datainfo;
		// begin Table Info
		if (paraminfo.isTable) {
			datainfo.setTableInfo(getDbCompareDM().getTableInfo(tableAry));
			datainfo.setColumnInfo(getDbCompareDM().getColumnInfo(tableAry));
		}
		if (paraminfo.isIndex) {
			datainfo.setIndexInfo(getDbCompareDM().getIndexInfo(tableAry));
		}
		if (paraminfo.isView) {
			// datainfo.setViewInfo(getDbCompareDM().getViewInfo( getTables()));
		}
		if (paraminfo.isTrigger) {
			// datainfo.setTriggerInfo(getDbCompareDM().getTriggerInfo(
			// getTables()));
		}
		if (paraminfo.isConstraint) {
			datainfo.setConstraintPKInfo(getDbCompareDM().getConstraintPKInfo(
					tableAry));
			datainfo.setConstraintFKInfo(getDbCompareDM().getConstraintFKInfo(
					tableAry));
			datainfo.setConstraintCKInfo(getDbCompareDM().getConstraintCKInfo(
					tableAry));
			datainfo.setConstraintDFInfo(getDbCompareDM().getConstraintDFInfo(
					tableAry));
		}

		return datainfo;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 12:47:30)
	 * 
	 * @return tools.ParamInfo
	 */
	private ParamInfo getParamInfo() {
		return paraminfo;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 21:49:43)
	 * 
	 * @param paraminfo
	 *            tools.ParamInfo
	 */
	public void setParamInfo(ParamInfo paraminfo) {
		this.paraminfo = paraminfo;
	}

	private DbCompareDM m_objDbCompareDM = null; // 比较DM

	private DdlGenerator m_objDdlGenerator = null;// Ddl语句生成器

	private TableFileParser m_objTableFileParser = null;// script 解析器

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 12:45:51)
	 */
	private Record[] compare(DataInfo info1, DataInfo info2) {
		Record[] infos_tb = null, infos_idx = null, infos_contPK = null;

		// compare Table
		if (paraminfo.isTable) {
			infos_tb = compareTable(info1, info2);
		}

		// compare index
		if (paraminfo.isIndex) {
			infos_idx = compareIndex(info1, info2);
		}

		int nTb = ((infos_tb != null) ? infos_tb.length : 0);
		int nIdx = ((infos_idx != null) ? infos_idx.length : 0);

		int nContPK =0;// ((infos_contPK != null) ? infos_contPK.length : 0);

		Record[] n = new Record[nTb + nIdx + nContPK];
		if (nTb > 0)
			System.arraycopy(infos_tb, 0, n, 0, nTb);
		if (nIdx > 0)
			System.arraycopy(infos_idx, 0, n, nTb, nIdx);

//		if (nContPK > 0)
//			System.arraycopy(infos_contPK, 0, n, nTb + nIdx, nContPK);
		return n;
	}

	/**
	 * 合并排序法 创建日期：(2002-5-28 12:45:51)
	 */
	private Record[] compareColumn(ArrayList<ColumnInfo> colinfo1,
			ArrayList<ColumnInfo> colinfo2) {
		ArrayList<Record> alRec = new ArrayList<Record>();
		HashMap<String, ColumnInfo> data1ColMap = new HashMap<String, ColumnInfo>();
		// 需要删除列的ArrayList
		for (ColumnInfo info : colinfo1) {
			data1ColMap.put(info.getColumnName().toLowerCase(), info);
		}
		HashMap<String, ColumnInfo> data2ColMap = new HashMap<String, ColumnInfo>();
		for (ColumnInfo info : colinfo2) {
			data2ColMap.put(info.getColumnName().toLowerCase(), info);
			if (!data1ColMap.containsKey(info.getColumnName().toLowerCase())) {
				// 需要删除列
				// 处理SQL SERVER 缺省值
				if (getParamInfo().getDbType().equals(IConstant.DB_SQL)) {
					ConstraintInfo c2 = getDdlGenerator().getConstraintDF(info);
					if (c2 != null) {
						Record rec2 = getDdlGenerator()
								.reqConstraintChangeInfo(c2, null,
										IConstant.O_DELETE);
						alRec.add(rec2);
					}
				}

				Record rec2 = getDdlGenerator().reqColumnChangeInfo(info, null,
						IConstant.O_DELETE); // drop
				alRec.add(rec2);
			}
		}

		for (ColumnInfo info : colinfo1) {
			if (data2ColMap.containsKey(info.getColumnName().toLowerCase())) {
				ColumnInfo info2 = data2ColMap.get(info.getColumnName().toLowerCase());
				// 判定是否进行修改
				String type1 = getDdlGenerator()
						.getColumnInfo(info, true, true);
				String type2 = getDdlGenerator().getColumnInfo(info2, true,
						true);
				if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
					if (type2.equalsIgnoreCase("clob(2097152)")) {
						type2 = "clob(2m)";
					}
					if (type1.equalsIgnoreCase("blob(134217728)")) {
						type2 = "blob(128m)";
					}
				}
				if (!removeSign(type1).equalsIgnoreCase(removeSign(type2))) {
					Record rec2 = getDdlGenerator().reqColumnChangeInfo(info,
							info2, IConstant.O_MODIFY);
					alRec.add(rec2);
				}
				// 如果列相等，处理SQL SERVER缺省值
				if (getParamInfo().getDbType().equals(IConstant.DB_SQL)) {
					ConstraintInfo c1 = getDdlGenerator().getConstraintDF(info);
					ConstraintInfo c2 = getDdlGenerator()
							.getConstraintDF(info2);
					if (c1 != null
							&& c2 != null
							&& !c1.getComment().equalsIgnoreCase(
									c2.getComment())) {
						Record rec1 = getDdlGenerator()
								.reqConstraintChangeInfo(c1, null,
										IConstant.O_NEW);
						Record rec2 = getDdlGenerator()
								.reqConstraintChangeInfo(c2, null,
										IConstant.O_DELETE);
						alRec.add(rec1);
						alRec.add(rec2);
					} else if (c1 == null && c2 != null) {
						Record rec2 = getDdlGenerator()
								.reqConstraintChangeInfo(c2, null,
										IConstant.O_DELETE);
						alRec.add(rec2);
					} else if (c1 != null && c2 == null) {
						Record rec1 = getDdlGenerator()
								.reqConstraintChangeInfo(c1, null,
										IConstant.O_NEW);
						alRec.add(rec1);
					}
				}
			} else {
				// 进行新增列
				Record rec2 = getDdlGenerator().reqColumnChangeInfo(info, null,
						IConstant.O_NEW); // add
				if(getDbType().equals(IConstant.DB_ORA) && info.getDefault() != null && info.getDefault().trim().length() > 0){
					String sql = rec2.getContent();
					int index1 = sql.indexOf(" add ");
					int index2 =sql.indexOf(" default ");
					if(index1 != -1 && index2 != -1){
						String sql1 = sql.substring(0, index2);
						String sql2 = sql.substring(0,index1)+" modify "+sql.substring(index1+" add ".length(), sql.length());
						Record record = rec2.clone();
						rec2.setContent(sql1);
						record.setContent(sql2);
						alRec.add(rec2);
						alRec.add(record);
					}else{
						alRec.add(rec2);
					}
					
				}else{
					alRec.add(rec2);
				}
			}
		}

		return alRec.toArray(new Record[alRec.size()]);
	}
	
	//ewei+  判断默认值时，sqlserver的默认值读回来和脚本中不同，处理之 2010-4-13
	private String removeSign(String tyStr){
		return tyStr.replaceAll("[\\(\\{\\[\\)\\s\\}\\]]", "");
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 12:45:51)
	 */
	private Record[] compareIndex(DataInfo info1, DataInfo info2) {
		// index
		ObjectInfo[] index1 = info1.getIndexInfo();
		ObjectInfo[] index2 = info2.getIndexInfo();
		ArrayList<Record> alRec = new ArrayList<Record>();
		if (index1 == null)
			return null;
		HashMap<String, ObjectInfo> data2IdxMap = new HashMap<String, ObjectInfo>();
		if (index2 != null) {
			for (ObjectInfo oidx : index2) {
				data2IdxMap.put(oidx.getObjName().toLowerCase(), oidx);
			}
		}

		for (ObjectInfo info : index1) {
			if (data2IdxMap.containsKey(info.getObjName().toLowerCase())) {

				// 判定是否修改
				// 比较索引列
				ObjectInfo d2info = data2IdxMap.get(info.getObjName()
						.toLowerCase());
				if (!info.getComment().trim().equalsIgnoreCase(
						d2info.getComment().trim())) {
					LogWriter.write("索引列不相同，进行修改索引" + info.getObjName()/*-=notranslate=-*/
							+ " In 表:" + info.getDependTable());/*-=notranslate=-*/
					Record rInfo = getDdlGenerator().reqIndexChangeInfo(info,
							d2info, IConstant.O_MODIFY);
					alRec.add(rInfo);
				} else {
					// 比较索引位置
					if (getIsPosition()) {
						if (!info.getObjPosi().equalsIgnoreCase(
								d2info.getObjPosi())) {
							LogWriter.write("索引位置不相同，进行修改索引"/*-=notranslate=-*/
									+ info.getObjName() + " In 表:"/*-=notranslate=-*/
									+ info.getDependTable());
							Record rInfo = getDdlGenerator()
									.reqIndexChangeInfo(info, d2info,
											IConstant.O_MODIFY);
							alRec.add(rInfo);
						}
					}
				}
			} else {
				LogWriter.write("新增加表索引：" + info.getObjName() + "In 表："/*-=notranslate=-*/
						+ info.getDependTable());
				// 新增索引
				Record rInfo = getDdlGenerator().reqIndexChangeInfo(info, null,
						IConstant.O_NEW);
				alRec.add(rInfo);
			}
		}
		if (alRec.size() > 0)
			return alRec.toArray(new Record[alRec.size()]);
		else
			return null;

	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 12:45:51)
	 */
	private Record[] compareTable(DataInfo info1, DataInfo info2) {
		// Table
		ObjectInfo[] table1 = info1.getTableInfo();
		ObjectInfo[] table2 = info2.getTableInfo();

		// if (table1 == null)
		// return null;
		// int start = 0;
		// int n2 = (table2 != null) ? table2.length : 0;
		//
		// ArrayList arr = new ArrayList(); //
		// 记录所有更新过的table，包括create,update,delete
		//
		// java.util.Vector v = new java.util.Vector();
		// Record info = null;
		// int i = 0, j = 0, mode = 10;
		// while (i < table1.length || j < table2.length) {
		// String strTable = null;
		// mode = 10;
		// if (i < table1.length)
		// LogWriter.write("Comparing Table:" + table1[i].getObjName()
		// + "current i=" + String.valueOf(i));
		// if (j < table2.length)
		// LogWriter.write("Comparing Table:" + table2[j].getObjName()
		// + "current j=" + String.valueOf(j));
		// if (i < table1.length && j < table2.length) {
		// int ip = table1[i].getObjName().toUpperCase().compareTo(
		// table2[j].getObjName().toUpperCase());
		// if (ip > 0)
		// mode = -1;
		// else if (ip < 0)
		// mode = 1;
		// else if (ip == 0) {
		// // compare column
		// ColumnInfo[] colinfo1 = info1.getTableColumns(table1[i]
		// .getObjName());
		// ColumnInfo[] colinfo2 = info2.getTableColumns(table2[j]
		// .getObjName());
		// Record[] colinfo = compareColumn(colinfo1, colinfo2);
		//
		// boolean n_flag = false;
		// for (int k = 0; k < colinfo.length; k++) {
		// if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
		// colinfo[k].setObjPosi(table1[i].getObjPosi());
		// colinfo[k].setObjIndPosi(table1[i].getIndexPosi());
		// if (colinfo[k].getOperation().equalsIgnoreCase(
		// IConstant.O_MODIFY))
		// n_flag = true;
		// }
		// v.addElement(colinfo[k]);
		// }
		// if (colinfo.length > 0 && n_flag) {
		// strTable = table1[i].getObjName();
		// }
		// // compare posi
		// if (getIsPosition()) {
		// if (!table1[i].getObjPosi().equalsIgnoreCase(
		// table2[j].getObjPosi())) {
		// info = getDdlGenerator().reqTableChangeInfo(
		// table1[i], null, IConstant.O_MOVE);
		// if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
		// info.setObjPosi(table1[i].getObjPosi());
		// info.setObjIndPosi(table1[i].getIndexPosi());
		// }
		// if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)
		// && strTable != null) {
		// } else {
		// v.addElement(info);
		// }
		// if (strTable == null) {
		// strTable = table1[i].getObjName();
		// }
		// }
		// }
		// }
		// } else if (i >= table1.length && j < table2.length) {
		// mode = -1;
		// } else if (i < table1.length && j >= table2.length) {
		// mode = 1;
		// }
		// if (mode == 1) {
		// info = getDdlGenerator().reqTableChangeInfo(table1[i],
		// info1.getTableColumns(table1[i].getObjName()),
		// IConstant.O_NEW);
		// if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
		// info.setObjPosi(table1[i].getObjPosi());
		// info.setObjIndPosi(table1[i].getIndexPosi());
		// }
		// v.addElement(info);
		// // 记录新建的table
		// LogWriter.write("记录新建的表:" + table1[i].getObjName());
		// strTable = table1[i].getObjName();
		// if (strTable != null)
		// arr.add(strTable);
		// i++;
		//
		// }
		//
		// // 是否index1最后一个新增,则最后一个index2为删除
		// if (j == table2.length - 1 && i >= table1.length && mode == 1)
		// mode = -1;
		//
		// if (mode == -1) {
		// info = getDdlGenerator().reqTableChangeInfo(table2[j], null,
		// IConstant.O_DELETE); // drop
		// if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
		// info.setObjPosi(table2[j].getObjPosi());
		// info.setObjIndPosi(table2[j].getIndexPosi());
		// }
		// v.addElement(info);
		//
		// // 记录删除的table fanguanjun 20070903不再记录删除的表
		// // strTable = table2[j].getObjName();
		// // if (strTable != null)
		// // arr.add(strTable);
		// j++;
		// } else if (mode == 10) {
		// // 记录差异的table
		// if (strTable != null)
		// arr.add(strTable);
		// i++;
		// j++;
		// }
		// }
		//
		// Record[] infos = new Record[v.size()];
		// v.copyInto(infos);
		// setTabChange(arr);
		// return infos;

		Record recInfo = null;
		ArrayList<String> chgAry = new ArrayList<String>();
		ArrayList<Record> alRec = new ArrayList<Record>();
		//pk info
		ConstraintInfo[] dbPKInfos = info2.getConstraintPKInfo();
		HashSet<String> pkTables = new HashSet<String>();
		//存放数据库中有主建的表名称
		for (int i = 0; i < (dbPKInfos==null?0:dbPKInfos.length); i++) {
			pkTables.add(dbPKInfos[i].getDependTable().toLowerCase());
		}
		// 存储数据库结构的表
		HashMap<String, ObjectInfo> data2Map = new HashMap<String, ObjectInfo>();
		if (table2 != null) {
			for (int i = 0; i < table2.length; i++) {
				ObjectInfo oInfo = table2[i];
				data2Map.put(oInfo.getObjName().toLowerCase(), oInfo);
			}
		}
		if (table1 == null)
			return null;
		for (int i = 0; i < table1.length; i++) {
			ObjectInfo info = table1[i];

			if (data2Map.containsKey(info.getObjName().toLowerCase())) {
				// 记载变化的表
				String strTable = null;
				ObjectInfo oinfo2 = data2Map.get(info.getObjName()
						.toLowerCase());
				// compare column
				ArrayList<ColumnInfo> colinfo1 = info1.getTableColumns(info
						.getObjName());
				ArrayList<ColumnInfo> colinfo2 = info2.getTableColumns(oinfo2
						.getObjName());
				Record[] colinfo = compareColumn(colinfo1, colinfo2);

				// boolean n_flag = false;
				for (int k = 0; k < colinfo.length; k++) {
					if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
						colinfo[k].setObjPosi(info.getObjPosi());
						colinfo[k].setObjIndPosi(info.getIndexPosi());
						// if (colinfo[k].getOperation().equalsIgnoreCase(
						// IConstant.O_MODIFY))
						// n_flag = true;
					}
					alRec.add(colinfo[k]);
					LogWriter.write("表字段发生变化,table:" + info.getObjName());/*-=notranslate=-*/
				}
				if (colinfo.length > 0) {
					strTable = info.getObjName();
				}else{
					String dbTableName = info.getObjName().toLowerCase();
					if(!pkTables.contains(dbTableName)){
						strTable = info.getObjName();
				
					}
				}
				
				// compare posi
				if (getIsPosition()) {
					if (info.getObjPosi()!=null && !info.getObjPosi()
							.equalsIgnoreCase(oinfo2.getObjPosi())) {
						recInfo = getDdlGenerator().reqTableChangeInfo(info,
								null, IConstant.O_MOVE);
						if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
							recInfo.setObjPosi(info.getObjPosi());
							recInfo.setObjIndPosi(info.getIndexPosi());
						}
						if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)
								&& strTable != null) {
						} else {
							alRec.add(recInfo);
							LogWriter.write("表空间发生变化,table:"/*-=notranslate=-*/
									+ info.getObjName());
						}
						if (strTable == null) {
							strTable = table1[i].getObjName();
						}
					}
				}
				if (strTable != null)
					chgAry.add(strTable);
			} else {
				// 新增的表
				recInfo = getDdlGenerator().reqTableChangeInfo(info,
						info1.getTableColumns(info.getObjName()),
						IConstant.O_NEW);
				if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
					recInfo.setObjPosi(table1[i].getObjPosi());
					recInfo.setObjIndPosi(table1[i].getIndexPosi());
				}
				alRec.add(recInfo);

				chgAry.add(info.getObjName());
				// 记录新建的table
				LogWriter.write("记录新建的表:" + table1[i].getObjName());/*-=notranslate=-*/
			}
		}
		setTabChange(chgAry);
		if (alRec.size() > 0)
			return alRec.toArray(new Record[alRec.size()]);
		else
			return null;

	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-24 19:17:18)
	 * 
	 * @return nc.bs.update.db.compare.DataInfo
	 */
	public DataInfo getDataInfo1() {
		return datainfo1;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-24 19:17:31)
	 * 
	 * @return nc.bs.update.db.compare.DataInfo
	 */
	public DataInfo getDataInfo2() {
		return datainfo2;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-16 9:58:36)
	 * 
	 * @return nc.bs.update.db.compare.DbCompareDM
	 */
	public DbCompareDM getDbCompareDM() throws Exception {
		if (m_objDbCompareDM == null) {
			m_objDbCompareDM = new DbCompareDM();
			m_objDbCompareDM.setConnection(con);
		}
		return m_objDbCompareDM;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-18 10:53:50)
	 * 
	 * @return nc.bs.update.db.compare.DdlGenerator
	 */
	private DdlGenerator getDdlGenerator() {
		if (m_objDdlGenerator == null) {
			m_objDdlGenerator = new DdlGenerator();
			m_objDdlGenerator.setDbType(paraminfo.strDbType);
		}
		return m_objDdlGenerator;
	}

	/**
	 * 得到差异文件路径 创建日期：(2002-9-25 9:55:51)
	 * 
	 * @return java.lang.String
	 */
	public String getDiffPath() {
		/*
		 * String path = IConstant.PATH_SQL; if
		 * (getParamInfo().getDbType().equals(IConstant.DB_ORA)) path =
		 * IConstant.PATH_ORA; else if
		 * (getParamInfo().getDbType().equals(IConstant.DB_SQL)) path =
		 * IConstant.PATH_SQL; else if
		 * (getParamInfo().getDbType().equals(IConstant.DB_UDB)) path =
		 * IConstant.PATH_UDB;
		 */
		return getParamInfo().getFilePath() + IConstant.FILE_DIFF;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-23 14:23:29)
	 * 
	 * @return nc.bs.update.db.compare.TableFileParser
	 */
	public TableFileParser getFileParser() {
		if (m_objTableFileParser == null) {
			m_objTableFileParser = new TableFileParser();
		}
		return m_objTableFileParser;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-23 14:51:47)
	 * 
	 * @return nc.bs.update.db.compare.TableFileParser
	 */
	public TableFileParser getTableFileParser() {
		if (m_objTableFileParser == null) {
			m_objTableFileParser = new TableFileParser();
		}
		return m_objTableFileParser;
	}

	/**
	 * 樊冠军,修改为根据脚本的数据进行比较版本 创建日期：(2002-9-16 10:00:13)
	 * 
	 * @return java.lang.String
	 */
	public String getTables() {
		StringBuffer sbRet = new StringBuffer();
		ObjectInfo[] table1 = getDataInfo1().getTableInfo();
		for (int i = 0; i < table1.length; i++) {
			ObjectInfo info = table1[i];
			String strTableName = info.getObjName();
			if (!paraminfo.strDbType.equals(IConstant.DB_SQL))
				strTableName = strTableName.toUpperCase();
			if (i == table1.length - 1)
				sbRet.append("'").append(strTableName).append("'");
			else
				sbRet.append("'").append(strTableName).append("',");
		}
		if (sbRet.toString().length() > 1)
			return "(" + sbRet.toString() + ")";
		else
			return null;
		/*
		 * String s = getModuleInfos().getInfo(paraminfo.strModuleCode); if
		 * (paraminfo.strDbType.equals(IConstant.DB_ORA)) s = s.toUpperCase();
		 * return s;
		 */
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 22:12:01)
	 * 
	 * @param infos
	 *            tools.Record
	 */
	private void saveXmlFile(Record[] infos) {
		if (infos == null)
			return;
		java.io.FileOutputStream out = null;

		try {
			String strDiffFile = getDiffPath();
			// delete file
			try {
				java.io.File file = new java.io.File(strDiffFile);
				file.delete();
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}

			out = new java.io.FileOutputStream(strDiffFile, true);
			writeXmlHead(out, paraminfo.getModuleCode(), paraminfo.getDbType());

			// create or append file
			for (int i = 0; i < infos.length; i++) {
				Record info = infos[i];
				StringBuffer buffer = new StringBuffer("<" + IXml.ROW + ">");
				buffer.append(info.toXmlString());
				buffer.append("</" + IXml.ROW + ">");

				out.write(buffer.toString().getBytes());
				out.write('\n');
			}

			// 写入/module
			writeXmlTail(out);
			out.close();

		} catch (Exception e) {
			if (out != null)
				try {
					out.close();
				} catch (Exception ee) {
				}
			;
			LogWriter.write("save XML File error:" + e.getMessage());
		}
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-6-5 9:22:42)
	 * 
	 * @param out
	 *            java.io.FileOutputStream
	 */
	private void writeXmlTail(java.io.FileOutputStream out)
			throws java.io.IOException {

		StringBuffer buffer = new StringBuffer("</" + IXml.MODULE + ">");

		out.write(buffer.toString().getBytes());
		out.write('\n');
	}

	private java.sql.Connection con = null;

	/**
	 * 此处插入方法说明。 创建日期：(2002-5-28 22:05:56)
	 * 
	 * @param paraminfo
	 *            tools.ParamInfo
	 */
	public int compare() throws Exception {

		Record[] infos = compare(getDataInfo1(), getDataInfo2());

		if (infos.length >= 0) {
			saveXmlFile(infos);
		}

		return infos.length;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-27 16:14:30)
	 */
	public int initData() throws Exception {
		int r = 0;

		getDbCompareDM().setDbType(paraminfo.getDbType());

		if (paraminfo.getFilePath() != null) {// 文件比较
			getTableFileParser().setDbType(paraminfo.getDbType());
			getTableFileParser().setDbScriptType(paraminfo.getDbScriptType());
			getTableFileParser().setFilePath(paraminfo.getFilePath());
			getTableFileParser().setModule(paraminfo.getModuleCode());

			r = getTableFileParser().parse();
			if (r == -1)
				return r;
			datainfo1 = getTableFileParser().getDataInfo();
		} else {// 读取数据库信息
			datainfo1 = getDataInfo(paraminfo.strOdbcName1, paraminfo.strUser1,
					paraminfo.strPassword1);
		}

		// 记录新表
		getDbCompareDM().preProcess(paraminfo.getModuleCode(),
				datainfo1.getTableInfo());

		// 读取数据库信息
		datainfo2 = getDataInfo(paraminfo.strOdbcName2, paraminfo.strUser2,
				paraminfo.strPassword2);

		return r;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-10-14 9:24:59)
	 * 
	 * @param con
	 *            java.sql.Connection
	 */
	public void setConnection(Connection con) {
		this.con = con;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-6-5 9:22:42)
	 * 
	 * @param out
	 *            java.io.FileOutputStream
	 */
	private void writeXmlHead(java.io.FileOutputStream out,
			String strModuleCode, String strDbType) throws java.io.IOException {

		StringBuffer buffer = new StringBuffer(
				"<?xml version='1.0' encoding='gb2312'?>");
		buffer.append("\n");
		buffer.append("<" + IXml.MODULE + " " + IXml.CODE + "= '"
				+ SQLTransferMeaningUtil.tmsql(strModuleCode) + "' ");
		buffer.append(IXml.DBTYPE + "='" + SQLTransferMeaningUtil.tmsql(strDbType) + "'");
		buffer.append(">");

		out.write(buffer.toString().getBytes());
		out.write('\n');
	}

	public String m_strDbType = null; // 数据库类型

	public String[] m_TabChange = null; // 增删改的table记录

	/**
	 * 此处插入类型说明。 创建日期：(2003-8-7 12:27:37)
	 * 
	 * @author：duanl
	 */
	public String getDbType() {
		return m_strDbType;
	}

	/**
	 * 获得数据库部署器开关 创建日期：(2003-8-7 12:06:37)
	 * 
	 * @return boolean
	 */
	public boolean getIsPosition() {
		boolean flag = false;

		ParamInfo para = new ParamInfo();

		TableFileParser parse = new TableFileParser();

		if (parse.getIsPosition()) {

			if (getDbType().equalsIgnoreCase(IConstant.DB_SQL)) {
				if (para.isSQL) {
					flag = true;
				}
			} else if (getDbType().equalsIgnoreCase(IConstant.DB_ORA)) {
				if (para.isORA) {
					flag = true;
				}
			} else if (getDbType().equalsIgnoreCase(IConstant.DB_UDB)) {
				if (para.isUDB) {
					flag = true;
				}
			}

		}

		return flag;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-8-7 9:24:59)
	 * 
	 */
	public String[] getTabChange() {
		return m_TabChange;
	}

	/**
	 * 此处插入类型说明。 创建日期：(2003-8-7 12:27:37)
	 * 
	 * @author：duanl
	 */
	public void setDbType(String strDbType) {
		m_strDbType = strDbType;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-8-7 9:24:59)
	 * 
	 * @param con
	 *            java.util.ArrayList
	 */
	public void setTabChange(ArrayList<String> arr) {

		if (!arr.isEmpty()) {
			m_TabChange = new String[arr.size()];
			arr.toArray(m_TabChange);
		} else {
			m_TabChange = new String[0];
		}

	}
}