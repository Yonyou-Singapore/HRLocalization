package nc.bap.portal.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hrrp.service.IBaseService;
import nc.itf.hrrp.service.IPdfOprator;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.servletplus.annotation.Action;
import nc.uap.lfw.servletplus.annotation.Servlet;
import nc.uap.lfw.servletplus.core.impl.BaseAction;
import nc.util.iufo.mail.FreeReportPushUtil;
import nc.vo.hrrp.report.AggReport;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import uap.lfw.bq.portal.base.PrintCil;
import uap.lfw.bq.portal.report.runtime.RepPageModelService;
import uap.lfw.core.comp.report.cache.ReportLwCacheUtil;
import uap.lfw.core.comp.report.context.RepSessionProxy;
import uap.pub.bq.swchart.context.SWChartContext;
import uap.pub.bq.swchart.export.SWChartExporter;

import com.itextpdf.text.DocumentException;
import com.ufida.dataset.Context;
import com.ufida.dataset.IContext;
import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.report.free.plugin.Export.FrExcelPostProcessor;
import com.ufida.report.frquery.model.FrSimFilterValue;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.ExtDataModel;
import com.ufsoft.table.format.condition.AreaConditionValidator;

@Servlet(path = "/PdfDownloadForMalaysia")
public class PdfDownloadForMalaysiaAction extends BaseAction {

	@Action(method = "POST")
	public void exportPcbPdf() throws Exception {
		String reportName = "PCB2(II)";
		this.response.setContentType("application/pdf");
		this.response.addHeader("Content-Disposition", "attachment;filename="
				+ reportName + ".pdf");
		try {
			OutputStream stream = this.response.getOutputStream();
			IContext context = RepSessionProxy.getCurrentSession()
					.getCurrentContext();
			SWChartContext chartContext = new SWChartContext((Context) context);
			String condition = this.buildPcbQryCond(chartContext);
			outputPcbPdf(stream, condition, reportName);
			stream.flush();
		} catch (Exception e) {
			Logger.error(
					NCLangRes4VoTransl.getNCLangRes().getStrByID("1413003_0",
							"01413003-0392"), e);

			throw e;
		}
	}

	private String buildPcbQryCond(SWChartContext chartContext) {
		StringBuffer pcbcon = new StringBuffer();
		List<FrSimFilterValue> qryattrs = (List<FrSimFilterValue>) chartContext
				.getAttribute("key_lw_report_query_items");
		String loginusercode = (String) chartContext
				.getAttribute("macro_loginusercode");
		pcbcon.append(" 1=1");
		// 人员主键
		pcbcon.append(" and EMPLOYEECODE = '").append(loginusercode).append("'");
		if (qryattrs != null && qryattrs.size() > 0) {
			for (FrSimFilterValue attr : qryattrs) {
				// 期间 年
				if ("CYEAR".equalsIgnoreCase(attr.getFilterCode())) {
					ArrayList<String> cyear = (ArrayList<String>) attr
							.getValues();
					pcbcon.append(" and cyear").append(" = '")
							.append(cyear.get(0)).append("'");
				}
				// 公司名代替薪资方案code
				if (attr.getFilterCode().indexOf("name") >= 0) {
					ArrayList<String> orgname = (ArrayList<String>) attr
							.getValues();
					pcbcon.append(" and orgname").append(" = '")
							.append(orgname.get(0)).append("'");
				}
				// cperiod
//				if ("CPERIOD".equalsIgnoreCase(attr.getFilterCode())) {
//					ArrayList<String> cperiod = (ArrayList<String>) attr
//							.getValues();
//					pcbcon.append(" and cperiod").append(" = '")
//							.append(cperiod.get(0)).append("'");
//				}
			}
		}
		return pcbcon.toString();
	}

	@Action(method = "POST")
	public void exportEaformPdf() throws Exception {
		String reportName = "EA Form";
		this.response.setContentType("application/pdf");
		this.response.addHeader("Content-Disposition", "attachment;filename="
				+ reportName + ".pdf");
		try {
			OutputStream stream = this.response.getOutputStream();
			IContext context = RepSessionProxy.getCurrentSession()
					.getCurrentContext();
			SWChartContext chartContext = new SWChartContext((Context) context);
			String condition = this.buildEpfQryCond(chartContext);
			outputPcbPdf(stream, condition, reportName);
			stream.flush();
		} catch (Exception e) {
			Logger.error(
					NCLangRes4VoTransl.getNCLangRes().getStrByID("1413003_0",
							"01413003-0392"), e);

			throw e;
		}
	}

	private String buildEpfQryCond(SWChartContext chartContext) {
		StringBuffer pcbcon = new StringBuffer();
		List<FrSimFilterValue> qryattrs = (List<FrSimFilterValue>) chartContext
				.getAttribute("key_lw_report_query_items");
		String loginusercode = (String) chartContext
				.getAttribute("macro_loginusercode");
		pcbcon.append(" 1=1");
		// 人员主键
		pcbcon.append(" and EMPLOYEECODE = '").append(loginusercode).append("'");
		if (qryattrs != null && qryattrs.size() > 0) {
			for (FrSimFilterValue attr : qryattrs) {
				// 期间 年
				if ("CYEAR".equalsIgnoreCase(attr.getFilterCode())) {
					ArrayList<String> cyear = (ArrayList<String>) attr
							.getValues();
					pcbcon.append(" and cyear").append(" = '")
							.append(cyear.get(0)).append("'");
				}
				// 公司名代替薪资方案code
				if (attr.getFilterCode().indexOf("name") >= 0) {
					ArrayList<String> orgname = (ArrayList<String>) attr
							.getValues();
					pcbcon.append(" and orgname").append(" = '")
							.append(orgname.get(0)).append("'");
				}
				// cperiod
				if ("CPERIOD".equalsIgnoreCase(attr.getFilterCode())) {
					ArrayList<String> cperiod = (ArrayList<String>) attr
							.getValues();
					pcbcon.append(" and cperiod").append(" = '")
							.append(cperiod.get(0)).append("'");
				}
			}
		}
		return pcbcon.toString();
	}

	private void outputPcbPdf(OutputStream stream, String condition,
			String reportName) throws BusinessException, IOException,
			DocumentException {
		File dir = FreeReportPushUtil.getDownloadFile();
		String tempPath = dir.getAbsolutePath() + File.separator + "temp";

		FreeReportPushUtil.deleteDir(new File(tempPath));

		String dirPath = tempPath + File.separator + System.currentTimeMillis();
		File tempDir = new File(dirPath);
		tempDir.mkdirs();
		if (!(tempDir.exists())) {
			return;
		}
		String filePath = dirPath + File.separator + reportName + ".pdf";
		// 获取pcb字节
		IBaseService ibs = NCLocator.getInstance().lookup(IBaseService.class);
		AggReport aggvo = new AggReport();
		AggReport[] aggvos = ibs.queryAggVOByWhere(AggReport.class,
				"where repName='" + reportName + "'");
		if (aggvos != null && aggvos.length > 0) {
			aggvo = aggvos[0];
		}

		IPdfOprator pdfopt = NCLocator.getInstance().lookup(IPdfOprator.class);
		pdfopt.setAggvo(aggvo);
		pdfopt.setStrWhr(condition);

		FileInputStream inputStream = null;
		try {
			byte[] fileBts = pdfopt.pdfBuild();
			// inputStream = new FileInputStream(filePath);
			// byte[] b = new byte[1024];
			// while (inputStream.read(fileBts) > 0)
			// stream.write(fileBts);
			// }
			stream.write(fileBts);
		} catch (Exception e) {
			AppDebug.error(e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					AppDebug.error(e);
				}
			}
		}

		FreeReportPushUtil.deleteDir(tempDir);
	}

	public String encoding(String name) {
		try {
			if (this.request.getHeader("User-Agent").toLowerCase()
					.indexOf("firefox") >= 0)
				name = new String(name.getBytes("UTF-8"), "ISO8859-1");
			else if (this.request.getHeader("User-Agent").toUpperCase()
					.indexOf("MSIE") > 0)
				name = URLEncoder.encode(name, "UTF-8");
			else if (this.request.getHeader("User-Agent").toUpperCase()
					.indexOf("CHROME") > 0)
				name = new String(name.getBytes("UTF-8"), "ISO8859-1");
			else if (this.request.getHeader("User-Agent").toUpperCase()
					.indexOf("OPERA") > 0)
				name = new String(name.getBytes("UTF-8"), "ISO8859-1");
			else if (this.request.getHeader("User-Agent").toUpperCase()
					.indexOf("SAFARI") > 0)
				name = new String(name.getBytes("UTF-8"), "ISO8859-1");
			else if (this.request.getHeader("User-Agent").toUpperCase()
					.indexOf("GECKO") > 0)
				name = URLEncoder.encode(name, "UTF-8");
			else
				name = new String(name.getBytes("UTF-8"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			AppDebug.error(e);
		}
		this.response.reset();
		return name;
	}

}
