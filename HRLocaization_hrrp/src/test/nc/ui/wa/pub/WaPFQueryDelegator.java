/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package nc.ui.wa.pub;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.pf.PFNormalQueryPanel;
import nc.ui.hr.pf.model.PFAppModel;
import nc.ui.hr.uif2.HrQueryDelegator;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.vo.uif2.LoginContext;
import org.apache.commons.lang.ArrayUtils;

public class WaPFQueryDelegator extends HrQueryDelegator {
	public QueryConditionDLG getQueryDlg() {
		if (this.queryDlg == null) {
			this.queryDlg = super.getQueryDlg();

			PFNormalQueryPanel nqp = new PFNormalQueryPanel();
			nqp.setModel((PFAppModel) getModel());
			this.queryDlg.setNormalPanel(nqp);
		}
		return this.queryDlg;
	}

	protected void initCondition(CriteriaChangedEvent event) {
		super.initCondition(event);
		String code = event.getFieldCode();
		FilterEditorWrapper wapper = new FilterEditorWrapper(
				event.getFiltereditor());

		String pkOrg = getContext().getPk_org();

		if (!(wapper.getFieldValueElemEditorComponent() instanceof UIRefPane)) {
			return;
		}

		if (ArrayUtils.contains(new String[] { "pk_wa_class",
				"psnapp_b.pk_wa_grd" }, code)) {
			UIRefPane waClassRef = (UIRefPane) wapper
					.getFieldValueElemEditorComponent();

			if ((waClassRef != null) && (waClassRef.getRefModel() != null)) {
				waClassRef.setPk_org(pkOrg);
			}

		}

		if (code.equals("psnapp_b.pk_wa_prmlv_apply")) {
			UIRefPane prmlvRef = (UIRefPane) wapper
					.getFieldValueElemEditorComponent();
			prmlvRef.getRefModel()
					.addWherePart(
							" and wa_prmlv.pk_wa_grd in (select pk_wa_grd from wa_grade_ver where effect_flag = 'Y')");
		}

		if (code.equals("psnapp_b.pk_wa_seclv_apply")) {
			UIRefPane seclvRef = (UIRefPane) wapper
					.getFieldValueElemEditorComponent();
			seclvRef.getRefModel()
					.addWherePart(
							" and wa_seclv.pk_wa_grd in (select pk_wa_grd from wa_grade_ver where effect_flag = 'Y')");
		}
	}
}