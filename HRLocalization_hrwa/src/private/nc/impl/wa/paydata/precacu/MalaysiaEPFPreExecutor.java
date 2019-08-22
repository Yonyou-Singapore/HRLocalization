package nc.impl.wa.paydata.precacu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.pub.BusinessException;
import nc.vo.wa.item.MalaysiaVO_EPF;
import nc.vo.wa.pub.WaLoginContext;

public class MalaysiaEPFPreExecutor extends AbstractFormulaExecutor {

	private static BaseDAO dao = null;
	
	public MalaysiaEPFPreExecutor() {
		if (dao == null) {
			dao = new BaseDAO();
		}
	}
	
	
	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
//		System.out.println("Enter executor");
		// Query necessary items from personnel information table
		List<MalaysiaVO_EPF> result = this.queryEPFItems(context);
		
		this.updateCacudata(result);
		// Prepare temporary table
//		System.out.println(result.toString());
	}
	
	@SuppressWarnings("unchecked")
	private List<MalaysiaVO_EPF> queryEPFItems(WaLoginContext context) {
		List<MalaysiaVO_EPF> results = null;
		String condition = " pk_wa_class = ? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_wa_class());
		try {
			results = (List<MalaysiaVO_EPF>) dao.retrieveByClause(MalaysiaVO_EPF.class, condition, parameter);
		} catch (Exception e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
		return results;
	}
	
	private void updateCacudata(List<MalaysiaVO_EPF> calcuData) throws BusinessException {
		//创建临时表
		this.createTempTable(calcuData);
		
		String sql = " update wa_cacu_data w"
			+ " set (w.cacu_value) ="
			+ " (select case when my_nepfrate_employee = 0 then -1 else my_nepfrate_employee end "
			+ " from tmp_wa_epf p where w.pk_cacu_data = p.pk_cacu_data)"
			+ " where exists (select 1 from tmp_wa_epf)";
		super.executeSQLs(sql);
	}
	
	private void createTempTable(List<MalaysiaVO_EPF> calcuData) {
		Connection con = null;
		String tempTable = null;
		// 创建临时表
		try {
			con = ConnectionFactory.getConnection();
			String tableName = "tmp_wa_epf";
			String columns = "pk_cacu_data varchar2(20), my_nepfrate_employee number(28,8)," +
					"my_isvoluntaryepf varchar2(20)";
			tempTable = new TempTable().createTempTable(con, tableName, columns, "pk_cacu_data");
			prepareTempTable(calcuData, tableName);
		} catch (SQLException e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
	}
	
	private void prepareTempTable(List<MalaysiaVO_EPF> calcuData, String tempTable) {
		// TODO to be implemented
		String sql = "insert into " + tempTable 
				+ " (pk_cacu_data,my_nepfrate_employee,my_isvoluntaryepf) values (?,?,?)";
		PreparedStatement stm = null;
		Connection conn = null;
		try {
			conn = ConnectionFactory.getConnection();
			((CrossDBConnection) conn).setAddTimeStamp(false);
			stm = conn.prepareStatement(sql);
			for (MalaysiaVO_EPF entry : calcuData) {
				stm.setString(1, entry.getPk_cacu_data());
				stm.setDouble(2, entry.getMy_nepfrate_employee() == null ? 0.00 
						: entry.getMy_nepfrate_employee().doubleValue());
				stm.setString(3, entry.getMy_isvoluntaryepf() == null ? "N" 
						: entry.getMy_isvoluntaryepf().toString());
				stm.addBatch();
			}
			stm.executeBatch();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				
			}
		}
	}

}
