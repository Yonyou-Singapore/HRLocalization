package nc.ui.hi.psndoc.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.HRCMTermUnitUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.org.IOrgConst;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.IComponent;
import nc.md.model.access.javamap.BeanStyleEnum;
import nc.md.model.type.IEnumType;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.PinYinHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.BatchMatchContext;
import nc.ui.bd.ref.IRefConst;
import nc.ui.bd.ref.model.RegionDefaultRefTreeModel;
import nc.ui.cp.cpindi.ref.CPindiGradeRefModel;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.pub.EvalUtils;
import nc.ui.hi.pub.HiAppEventConst;
import nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil;
import nc.ui.hr.tools.uilogic.FieldRelationUtil;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.uif2.view.HrPsnclTemplateContainer;
import nc.ui.om.ref.HRDeptRefModel;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.cp.cpindi.CPIndiGradeVO;
import nc.vo.cp.cpindi.CPIndiVO;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.psndoc.ReqVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrainVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.hr.validator.CommnonValidator;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.post.PostVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-2 9:38:01<br>
 * @author Rocex Wang
 ***************************************************************************/
public class PsndocFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener, BillEditListener2, FocusListener
{
    private PsndocDataManager dataManger;
    
    private FieldRelationUtil fieldRelationUtil; // 字段逻辑处理工具
    
    // 工作记录中的业务数据：部门、岗位、职务、职务类别、职级、职等、岗位序列等，在人员维护的时候不能修改
    private final String strBusiFieldInJobs[] = {
        PsnJobVO.PK_DEPT,
        PsnJobVO.PK_POST,
        PsnJobVO.PK_JOB,
        PsnJobVO.SERIES,
        PsnJobVO.PK_JOBGRADE,
        PsnJobVO.PK_JOBRANK,
        PsnJobVO.PK_POSTSERIES};
    
    private String strPk_psncl; // 记录当前正在维护的人员类别
    
    private SuperFormEditorValidatorUtil superValidator; // 校验器
    
    // 合同子集中试用相关的字段
    private final String[] ctrtTrialFlds = {
        CtrtVO.PROMONTH,
        CtrtVO.PROBEGINDATE,
        CtrtVO.PROBENDDATE,
        CtrtVO.PROBSALARY,
        CtrtVO.STARTSALARY,
        CtrtVO.PROP_UNIT};
    
    // 合同中合同期限相关字段
    private final String[] ctrtFlds = {CtrtVO.TERMMONTH, CtrtVO.BEGINDATE, CtrtVO.ENDDATE, CtrtVO.CONT_UNIT};
    
    private HashSet<String> hashSubHaveLoad = new HashSet<String>();
    // 编辑态加载过的子集
    
    private final String[] fldBlastList = new String[]{
        PsndocVO.ISHISKEYPSN,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNDOC,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT_V,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG_V,
        PsnJobVO.PK_DEPT_V,
        PsnJobVO.PK_ORG_V,
        PsnJobVO.PK_PSNDOC};
    
    private int selectedRow = 0;// 记录子表中改变的行
    
    private boolean isEditBeginDate;
    private boolean isEditEndDate;
    
    /**
     * 给字表页签按钮设置权限
     */
    // @Override
    // public List<NCAction> getTabActions()
    // {
    // List<NCAction> al = new ArrayList<NCAction>();
    // List<NCAction> actions = super.getTabActions();
    // if (actions == null || actions.size() == 0)
    // {
    // return al;
    // }
    // for (NCAction action : actions)
    // {
    // FuncPermissionState state = getModel().getContext().getFuncInfo().getButtonPermissionState((String)
    // action.getValue(INCAction.CODE));
    // if (FuncPermissionState.REGISTERD_HASPERMISSION == state || FuncPermissionState.NOREGISTERD == state)
    // {
    // // 没注册的或是有权限的可以显示
    // al.add(action);
    // }
    // }
    // return al;
    // }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 9:07:52<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterBodyChange(BillEditEvent evt)
    {
        try
        {
            if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_GROUP.equals(evt.getKey()))
            {
                // 集团
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_ORG, PsnJobVO.PK_DEPT);
                // 清空 组织、部门、岗位、职务、职务类别、职级、职等
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_ORG.equals(evt.getKey()))
            {
                // 组织
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_DEPT);
                // 清空 部门、岗位、职务、职务类别、职级、职等
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_DEPT.equals(evt.getKey()))
            {
                // 部门
                // 清空 岗位、职务、职务类别、职等、职级
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_POST.equals(evt.getKey()))
            {
                // 岗位
                String pk_post = getStrValue(evt.getValue());
                PostVO post = pk_post == null ? null : getService().queryByPk(PostVO.class, pk_post, true);
                if (post != null)
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_DEPT, post.getPk_dept());// 部门
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POSTSERIES, post.getPk_postseries());// 岗位序列
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOB, post.getPk_job());// 职务
                    JobVO jobVO = post.getPk_job() == null ? null : getService().queryByPk(JobVO.class, post.getPk_job(), true);
                    if (jobVO != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, jobVO.getPk_jobtype());// 职务类别
                    }
                    if (post.getEmployment() != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.OCCUPATION, post.getEmployment());// 职业
                    }
                    if (post.getWorktype() != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.WORKTYPE, post.getWorktype());// 工种
                    }
                    
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, null, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, null);// 职务类别
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOB, null);
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POSTSERIES, null);
                }
                
                if (post == null)
                {
                    BillEditEvent event =
                        new BillEditEvent(getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.PK_POST), post == null ? null
                            : post.getPk_job(), PsnJobVO.PK_JOB, evt.getRow(), evt.getPos());
                    event.setTableCode(evt.getTableCode());
                    afterBodyChange(event);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.SERIES.equals(evt.getKey()))
            {
                // 职务类别
                String series = getStrValue(evt.getValue());
                String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                if (StringUtils.isBlank(pk_job) && StringUtils.isNotBlank(series))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(series, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series))
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// 职等
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_POSTSERIES.equals(evt.getKey()))
            {
                // 岗位序列
                String pk_postseries = getStrValue(evt.getValue());
                String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isNotBlank(pk_postseries))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isBlank(pk_postseries))
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// 职等
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_JOB.equals(evt.getKey()))
            {
                // 职务
                String pk_job = getStrValue(evt.getValue());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class, pk_job, true);
                if (job != null)
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, job.getPk_jobtype());// 职务类别
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, null);// 职务类别
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_JOBGRADE.equals(evt.getKey()))
            {
                // 职级
                String pk_jobgrage = getStrValue(evt.getValue());
                if (StringUtils.isNotBlank(pk_jobgrage))
                {
                    String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_postseries =
                        (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);
                    if (!resultMap.isEmpty())
                    {
                        defaultrank = resultMap.get("defaultrank");
                    }
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// 职等
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.TRNSEVENT.equals(evt.getKey()))
            {
                // 异动事件
                afterTrnsEventChange(evt);
            }
            else if (ReqVO.getDefaultTableName().equals(evt.getTableCode()) && ReqVO.PK_POSTREQUIRE_H.equals(evt.getKey()))
            {
                // 胜任能力
                // 清空 达到级别
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), ReqVO.PK_POSTREQUIRE_B);
            }
            else if (CapaVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                if (CapaVO.PK_PE_INDI.equals(evt.getKey()))
                {
                    // 胜任能力
                    // 清空 达到级别
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_PE_SCOGRDITEM);
                    if (evt.getValue() == null)
                    {
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.INDICODE);
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_INDI_TYPE);
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORESTANDARD);
                    }
                    else
                    {
                        CPIndiVO indi =
                            (CPIndiVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                                .retrieveByPk(null, CPIndiVO.class, (String) evt.getValue());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.INDICODE, indi.getIndicode());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_INDI_TYPE, indi.getPk_indi_type());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORESTANDARD, indi.getScorestandard());
                    }
                }
                else if (CapaVO.PK_PE_SCOGRDITEM.equals(evt.getKey()))
                {
                    if (evt.getValue() == null)
                    {
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORE);
                    }
                    else
                    {
                        CPIndiGradeVO grade =
                            (CPIndiGradeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                                .retrieveByPk(null, CPIndiGradeVO.class, (String) evt.getValue());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORE, grade.getGradeseq());
                    }
                }
            }
            else if (TrialVO.getDefaultTableName().equals(evt.getTableCode()) && TrialVO.TRIALRESULT.equals(evt.getKey()))
            {
                // 试用 ---试用结果
                Integer trialResult = (Integer) evt.getValue();
                int rowCount = getBillCardPanel().getBillTable(TrialVO.getDefaultTableName()).getRowCount();
                int editRow = evt.getRow();
                if (editRow < rowCount - 1 && (trialResult == null || trialResult == 2))
                {
                    // 插入试用记录时不能选择延长使用期
                    MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(), null,
                        ResHelper.getString("6007psn", "06007psn0164")/*
                                                                       * @res "历史试用记录不能选择延长试用期的试用结果或不选择试用结果"
                                                                       */);
                    getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(evt.getOldValue(), evt.getRow(),
                        TrialVO.TRIALRESULT);
                    return;
                }
                // 转正通过或转正未通过时试用结束
                getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(
                    UFBoolean.valueOf(trialResult != null && (trialResult == 1 || trialResult == 3)), evt.getRow(), TrialVO.ENDFLAG);
            }
            else if (CtrtVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                afterCtrtEdit(evt);
            }
            else if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                if (PartTimeVO.ENDFLAG.equals(evt.getKey()))
                {
                    // 结束标志 勾上则是否在岗为false
                    Boolean endflag = (Boolean) evt.getValue();
                    if (endflag != null && endflag.booleanValue())
                    {
                        getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(), PartTimeVO.POSTSTAT);
                    }
                }
            }
            getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
            String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES, evt.getRow());
            String postseries = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES, evt.getRow());
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
            if (StringUtils.isBlank(series) && StringUtils.isBlank(postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post))
            {
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, PsnJobVO.PK_JOBGRADE);
            }
            else
            {
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, PsnJobVO.PK_JOBGRADE);
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, PsnJobVO.PK_JOB);
            }
            getBillCardPanel().getBillModel(evt.getTableCode()).loadLoadRelationItemValue();
            
            getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * 合同页签相关项目变化监听
     */
    private void afterCtrtEdit(BillEditEvent evt)
    {
        // 试用相关的操作
        if (ArrayUtils.contains(ctrtTrialFlds, evt.getKey()) || CtrtVO.IFPROP.equals(evt.getKey()))
        {
            String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROP_UNIT);
            Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);
            // 默认是月
            trialtype = trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype;
            
            float days = HRCMTermUnitUtils.getDaysByUnit(trialtype);
            UFLiteralDate begindate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROBEGINDATE);
            UFLiteralDate enddate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROBENDDATE);
            Integer promonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROMONTH);
            if (CtrtVO.IFPROP.equals(evt.getKey()))
            {
                if (!(Boolean) evt.getValue())
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
                }
                else
                {// 当“是否试用”勾上时，“试用期限单位”默认为“月”，长度为3
                    getBillCardPanel().getBillModel(CtrtVO.getDefaultTableName()).setValueAt(HRCMTermUnitUtils.TERMUNIT_MONTH,
                        evt.getRow(), CtrtVO.PROP_UNIT);
                    getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.PROMONTH).setLength(
                        HRCMTermUnitUtils.TERMUNIT_MONTH_LENGTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROMONTH))
            {// 试用期限
                if (promonth == null)
                {
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.PROMONTH);
                    }
                }
                else if (begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
                else if (enddate != null)
                {
                    // 结束日期不为空,计算开始日期
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROBEGINDATE))
            {// 试用开始日期
                if (promonth != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
                else if (enddate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.PROMONTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROBENDDATE))
            {// 试用结束日期
                if (enddate == null)
                {
                    if (begindate != null && promonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                            evt.getRow(), CtrtVO.PROBENDDATE);
                    }
                }
                else if (begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.PROMONTH);
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROP_UNIT))
            {// 试用期限单位
             // 更改了试用期限单位，试用期限的长度也要相应的发生变化
                Integer termUnit = (Integer) evt.getValue();
                getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.PROMONTH).setLength(
                    HRCMTermUnitUtils.getLengthByTermUnit(termUnit));
                
                if (promonth == null)
                {// 试用期限为空
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.PROMONTH);
                    }
                }
                else if (begindate == null)
                {// 开始日期为空
                    if (enddate != null && promonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth, trialtype),
                            evt.getRow(), CtrtVO.PROBEGINDATE);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
            }
        }
        else if (ArrayUtils.contains(ctrtFlds, evt.getKey()) || CtrtVO.TERMTYPE.equals(evt.getKey()))
        {
            String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.CONT_UNIT);
            Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);
            // 默认是月
            trialtype = trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype;
            
            float days = HRCMTermUnitUtils.getDaysByUnit(trialtype);
            UFLiteralDate begindate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.BEGINDATE);
            UFLiteralDate enddate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.ENDDATE);
            Integer termmonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.TERMMONTH);
            /*
             * if (CtrtVO.PK_TERMTYPE.equals(evt.getKey())) { refreshTermTypeStat1(evt); if (termmonth != null
             * && begindate != null) { getBillCardPanel().getBillModel().setValueAt(
             * HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype), evt.getRow(),
             * CtrtVO.ENDDATE); } }else
             */if (CtrtVO.TERMTYPE.equals(evt.getKey()))
            {// 合同期限
                refreshTermTypeStat(evt);
                if (termmonth != null && begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.TERMMONTH))
            {// 合同期限
                if (termmonth == null)
                {
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else if (begindate != null /* && enddate == null */)
                {
                    // 开始日期不为空,计算结束日期
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
                else if (enddate != null)
                {
                    // 结束日期不为空,计算开始日期
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.BEGINDATE);
                }
                
            }
            else if (evt.getKey().equals(CtrtVO.BEGINDATE))
            {// 合同开始日期
                if (termmonth != null)
                {
                    if (enddate == null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                            evt.getRow(), CtrtVO.ENDDATE);
                    }
                    else
                    {
                        if (begindate != null && (begindate.beforeDate(enddate) || begindate.compareTo(enddate) == 0))
                        {
                            getBillCardPanel().getBillModel().setValueAt(
                                Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days), evt.getRow(), CtrtVO.TERMMONTH);
                        }
                        else
                        {
                            MessageDialog.showWarningDlg(this, null, ResHelper.getString("6007psn", "06007psn0351") /* "结束日期必须在开始日期之后！" */);
                            getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), CtrtVO.BEGINDATE);
                        }
                    }
                }
                else if (enddate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.TERMMONTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.ENDDATE))
            {// 合同结束日期
                if (enddate == null)
                {
                    if (begindate != null && termmonth != null)
                    {
                        // getBillCardPanel().getBillModel().setValueAt(VOUtils.getDateAfterMonth(begindate,
                        // termmonth, trialtype), evt.getRow(),
                        // CtrtVO.ENDDATE);
                    }
                }
                else if (begindate != null)
                {
                    if (termmonth == null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.BEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.CONT_UNIT))
            {// 合同期限单位
             // 更改了合同期限单位，合同期限的长度也要相应的发生变化
                Integer termUnit = (Integer) evt.getValue();
                getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.TERMMONTH).setLength(
                    HRCMTermUnitUtils.getLengthByTermUnit(termUnit));
                
                if (termmonth == null)
                {// 试用期限为空
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else if (begindate == null)
                {// 开始日期为空
                    if (enddate != null && termmonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                            evt.getRow(), CtrtVO.BEGINDATE);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
            }
        }
    }
    
    // private void refreshTermTypeStat(BillEditEvent evt) {
    // String pkTermtype = (String) evt.getValue();
    // TermTypeVO vo = null;
    // try {
    // vo = StringUtils.isBlank(pkTermtype) ? null : NCLocator
    // .getInstance().lookup(ITermtypeQueryService.class)
    // .queryTermType(pkTermtype);
    // } catch (Exception e) {
    // Logger.error(e.getMessage(), e);
    // }
    // if (vo == null || vo.getTermcode() == null) {
    // return;
    // }
    // if (ITermTypePub.TERMTYPE_NONFIXED == vo.getTermtype()) { // 无固定期限
    // clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] {
    // CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), false,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.IFPROP });// 是否试用可编辑
    // } else if (ITermTypePub.TERMTYPE_TASK == vo.getTermtype()) { // 以完成一定工作任务为期限
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), false,
    // new String[] { CtrtVO.IFPROP });// 是否试用置灰不可编辑
    // getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(
    // UFBoolean.FALSE, evt.getRow(), CtrtVO.IFPROP);
    // clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
    // } else { // 固定期限
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE,
    // CtrtVO.IFPROP });
    // }
    // }
    
    private void refreshTermTypeStat(BillEditEvent evt)
    {
        String termtype = (String) evt.getValue();
        
        if (HRCMTermUnitUtils.TERM_TYPE_NONFIXED.equals(termtype))
        { // 无固定期限
            clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.IFPROP});// 是否试用可编辑
        }
        else if (HRCMTermUnitUtils.TERM_TYPE_TASK.equals(termtype))
        { // 以完成一定工作任务为期限
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.IFPROP});// 是否试用置灰不可编辑
            getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(), CtrtVO.IFPROP);
            clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
        }
        else
        { // 固定期限
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE, CtrtVO.IFPROP});
        }
    }
    
    private void setBodyItemEdit(String strTabCode, int iRowIndex, boolean isEdit, String... strBodyItemKeys)
    {
        if (strBodyItemKeys == null || strBodyItemKeys.length == 0)
        {
            return;
        }
        BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(strTabCode);
        if (billModel == null)
        {
            return;
        }
        for (String strItemKey : strBodyItemKeys)
        {
            billModel.setCellEditable(iRowIndex, strItemKey, isEdit);
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-20 15:16:41<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#afterEdit(nc.ui.pub.bill.BillEditEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void afterEdit(BillEditEvent evt)
    {
        if (IBillItem.HEAD == evt.getPos())
        {
            afterHeadChange(evt);
        }
        else if (IBillItem.BODY == evt.getPos())
        {
            afterBodyChange(evt);
            
            BillItem item = this.getBillCardPanel().getBodyItem(evt.getTableCode(), evt.getKey());
            if (item != null)
            {
                // enddate->dateadd( begindate, glbdef1, "D")
                this.getBillCardPanel().getBillModel(evt.getTableCode()).execFormula(evt.getRow(), item.getEditFormulas());
                // this.getBillCardPanel().execBodyFormulas(evt.getRow(), item.getEditFormulas());
            }
        }
        super.afterEdit(evt);
        
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 9:07:36<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterHeadChange(BillEditEvent evt)
    {
        
        try
        {
            if (PsndocVO.NAME.equals(evt.getKey()))
            {
                // 姓名
                MultiLangText multiLangText = (MultiLangText) evt.getValue();
                BillItem item = getBillCardPanel().getHeadItem(PsndocVO.SHORTNAME);
                if (item != null && multiLangText != null)
                {
                    item.setValue(PinYinHelper.getPinYinHeadChar(multiLangText.getText(multiLangText.getCurrLangIndex())));
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG).equals(evt.getKey()))
            {
                // 组织
                // 清空 部门
                clearHeadItemValue(new String[]{PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT});
                // 如果岗位不为空 则清空、岗位、职务、职务类别、职等、职级
                Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (obj != null)
                {
                    clearHeadItemValue(new String[]{
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK});
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT).equals(evt.getKey()))
            {
                // 部门
                // 清空 岗位、职务、职务类别、职等、职级
                Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (obj != null)
                {
                    clearHeadItemValue(new String[]{
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES});
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST).equals(evt.getKey()))
            {
                // 岗位
                String pk_post = getStrValue(evt.getValue());
                PostVO post = pk_post == null ? null : getService().queryByPk(PostVO.class, pk_post, true);
                if (post != null)
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT, post.getPk_dept());// 部门
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES, post.getPk_postseries());// 岗位序列
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, post.getPk_job());// 职务
                    JobVO jobVO = post.getPk_job() == null ? null : getService().queryByPk(JobVO.class, post.getPk_job(), true);
                    if (jobVO != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, jobVO.getPk_jobtype());// 职务类别
                    }
                    if (post.getEmployment() != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.OCCUPATION, post.getEmployment());// 职业
                    }
                    if (post.getWorktype() != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.WORKTYPE, post.getWorktype());// 工种
                    }
                    
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, null, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                    
                    // 职务为空时,职级不可编辑
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
                    // 清空职务,同时清空职级,此时职等可以编辑
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).setEnabled(false);
                }
                else
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES, null);// 岗位序列
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, null);// 职务
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, null);// 职务类别
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).setEnabled(true);
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).setEnabled(true);
                }
                
                if (post == null)
                {
                    BillEditEvent event =
                        new BillEditEvent(getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB),
                            post == null ? null : post.getPk_job(), PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, evt.getRow(),
                            evt.getPos());
                    event.setTableCode(evt.getTableCode());
                    afterHeadChange(event);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).equals(evt.getKey()))
            {
                // 职务
                String pk_job = getStrValue(evt.getValue());
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class, pk_job, true);
                if (job != null)
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, job.getPk_jobtype());// 职务类别
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                    
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).setEnabled(false);
                }
                else
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, null);// 职务类别
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// 职等
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).setEnabled(true);
                }
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).equals(evt.getKey()))
            {
                // 职务类别
                String series = getStrValue(evt.getValue());
                String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (StringUtils.isBlank(pk_job) && StringUtils.isNotBlank(series))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(series, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series))
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// 职等
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).equals(evt.getKey()))
            {
                // 岗位序列
                String pk_postseries = getStrValue(evt.getValue());
                String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
                if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isNotBlank(pk_postseries))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isBlank(pk_postseries))
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// 职级
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// 职等
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).equals(evt.getKey()))
            {
                // 职级
                String pk_jobgrage = getStrValue(evt.getValue());
                if (StringUtils.isNotBlank(pk_jobgrage))
                {
                    String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
                    String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                    String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                    String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);
                    if (!resultMap.isEmpty())
                    {
                        defaultrank = resultMap.get("defaultrank");
                    }
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// 职等
                }
                else
                {
                    // 职级清空后,若职务关联了职等,则用职务上的职等
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// 职等
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT).equals(evt.getKey()))
            {
                // 异动事件
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSTYPE);
                if (item != null)
                {
                    item.clearViewData();
                    ((UIRefPane) item.getComponent()).getRefModel().setWherePart(PsnJobVO.TRNSEVENT + "=" + objValue);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.ENDFLAG).equals(evt.getKey()))
            {
                // 是否结束
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.ENDFLAG);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.POSTSTAT);
                if (item != null)
                {
                    item.setValue(UFBoolean.valueOf(!((Boolean) objValue).booleanValue()));
                }
                item = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDFLAG);
                if (item != null)
                {
                    item.setValue(objValue);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_FLAG).equals(evt.getKey()))
            {
                // 是否试用
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_FLAG);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_TYPE);
                if (item != null)
                {
                    item.clearViewData();
                    item.setEdit(objValue instanceof Boolean ? (Boolean) objValue : false);
                }
            }
            // 增加填写证件类型或者证件号时将表体身份证件子集复制
            else if (PsndocVO.ID.equals(evt.getKey()))
            {
                // 证件号
                // clearBodyItemValue(CertVO.getDefaultTableName(), 0, CertVO.ID);
                Object obj = getHeadItemValue(PsndocVO.ID);
                // setBodyValue(CertVO.getDefaultTableName(), 0, CertVO.ID, obj);
                Object objidtype = getHeadItemValue(PsndocVO.IDTYPE);
                HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) obj, (String) objidtype);
                if (map != null)
                {
                    setHeadItemValue(PsndocVO.SEX, map.get("sex"));
                    setHeadItemValue(PsndocVO.BIRTHDATE, map.get("birthday"));
                }
            }
            else if (PsndocVO.IDTYPE.equals(evt.getKey()))
            {
                // 证件类型
                // clearBodyItemValue(CertVO.getDefaultTableName(), 0, CertVO.IDTYPE);
                Object obj = getHeadItemValue(PsndocVO.IDTYPE);
                // setBodyValue(CertVO.getDefaultTableName(), 0, CertVO.IDTYPE, obj);
                Object objid = getHeadItemValue(PsndocVO.ID);
                HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) objid, (String) obj);
                if (map != null)
                {
                    setHeadItemValue(PsndocVO.SEX, map.get("sex"));
                    setHeadItemValue(PsndocVO.BIRTHDATE, map.get("birthday"));
                }
            }
            
            else if("sg_dprapprovaldate".equals(evt.getKey())){
            	//如果是PR,并且有审批日期的话, 需要自动生成表体的数据 add by weiningc 20200326 start
            	PsnIdtypeVO psnIdtypeVO =
            			(PsnIdtypeVO) NCLocator.getInstance(). 
            			lookup(IPersistenceRetrieve.class).retrieveByPk(null, PsnIdtypeVO.class, getHeadItemValue(PsndocVO.IDTYPE).toString());
            	String sgprcode = psnIdtypeVO.getCode();
            	if("NIRC-BLUE".equals(psnIdtypeVO.getCode())) {
            		UFDate sgapprovedate = (UFDate) getHeadItemValue("sg_dprapprovaldate");
            		String id = (String) getHeadItemValue(PsndocVO.ID);
            		this.createCert(sgapprovedate, id, psnIdtypeVO);
            	}
            }
            //end
            
            String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            if (StringUtils.isBlank(series) && StringUtils.isBlank(postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post))
            {
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(false);
            }
            else
            {
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
            }
            
        }
        catch (Exception ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
    }
    
    private void createCert(UFDate sgapprovedate, String id, PsnIdtypeVO idtypevo) {
    	//如果子集有PR以外的条目,保留， 如果全部都是PR的条目，删除后重新生成
    	Boolean isautoadd = false;
    	BillModel certmodel = getBillCardPanel().getBillModel(CertVO.getDefaultTableName());
    	if(certmodel != null) {
    		int rowCount = certmodel.getRowCount();
    		for(int i=0; i<rowCount; i++) {
    			String prcode = (String) getBillCardPanel().getBodyValueAt(i, CertVO.MEMO);
    			if(!StringUtils.isBlank(prcode) && prcode.startsWith("PR0")) {
    				isautoadd = true;
    			}
    		}
    	}
    	
    	if(!isautoadd) {
    		certmodel.clearBodyData();
    		int recount = certmodel.getRowCount();
    		int count = 1;
    		//effectivedate
    		UFDate effectivedate_pr01 = sgapprovedate.getDateAfter(365);
    		UFDate effectivedate_pr02 = effectivedate_pr01.getDateAfter(365);
    		UFDate effectivedate_pr03 = effectivedate_pr02.getDateAfter(365);
    		Map<String, UFDate> prdate = new HashMap<String, UFDate>();
    		prdate.put("PR01", getUFDateByCondiftion(effectivedate_pr01, true, false, null));
    		prdate.put("PR02", getUFDateByCondiftion(effectivedate_pr02, false, true, 1));
    		prdate.put("PR03", getUFDateByCondiftion(effectivedate_pr03, false, true, 1));
    		
    		for(int i=recount; i<recount + 3; i++) {
    			certmodel.addLine();
    			getBillCardPanel().setBodyValueAt(idtypevo.getPk_identitype(), i, CertVO.IDTYPE);
    			getBillCardPanel().setBodyValueAt(id, i, CertVO.ID);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_group(), i, CertVO.PK_GROUP);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_org(), i, CertVO.PK_ORG);
    			getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, i, CertVO.ISEFFECT);
    			getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, i, CertVO.ISSTART);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_loginUser(), i, CertVO.CREATOR);
    			getBillCardPanel().setBodyValueAt(PubEnv.getServerTime(), i, CertVO.CREATIONTIME);
    			getBillCardPanel().setBodyValueAt("PR0" + count, i, CertVO.MEMO);
    			getBillCardPanel().setBodyValueAt(prdate.get("PR0" + count), i, "enddate");
    			count ++;
    		}
    	}
    	
//    	CertVO cert = new CertVO();
//        cert.setIdtype(psndocVO.getIdtype());
//        cert.setId(psndocVO.getId());
//        cert.setPk_group(getModel().getContext().getPk_group());
//        cert.setPk_org(getModel().getContext().getPk_org());
//        cert.setIseffect(UFBoolean.TRUE);
//        cert.setIsstart(UFBoolean.TRUE);
//        cert.setCreator(getModel().getContext().getPk_loginUser());
//        cert.setCreationtime(PubEnv.getServerTime());
		
	}
    
    private UFDate getUFDateByCondiftion(UFDate ufdate, Boolean monthstart, Boolean nextmonth, Integer n_month) {
    	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = java.util.Calendar.getInstance(); 
    	calendar.setTime(ufdate.toDate());
    	if(monthstart) {
    		int dayofstart = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    		calendar.set(Calendar.DATE, dayofstart);
    		calendar.set(Calendar.HOUR_OF_DAY, 00);
    		calendar.set(Calendar.MINUTE, 00);
    		calendar.set(Calendar.SECOND, 00);
    	}
    	if(nextmonth != null) {
    		calendar.add(Calendar.DAY_OF_MONTH, n_month);
    		int dayofstart = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    		calendar.set(Calendar.DATE, dayofstart);
    		calendar.set(Calendar.HOUR_OF_DAY, 00);
    		calendar.set(Calendar.MINUTE, 00);
    		calendar.set(Calendar.SECOND, 00);
    	}
    	
    	return new UFDate(df.format(calendar.getTime()));
    }

	private HashMap<String, Object> generateGenderAndBirthdayFromID(String id, String idtype)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (id == null || id.length() < 1 || idtype == null || !idtype.equals("1001Z01000000000AI36"))
        {
            return null;
        }
        map.put("sex", getSex(id));
        UFLiteralDate birthday = null;
        try
        {
            birthday = getBirthdate(id) == null ? null : UFLiteralDate.getDate(getBirthdate(id));
        }
        catch (Exception e)
        {
            birthday = null;
        }
        map.put("birthday", birthday);
        return map;
    }
    
    private Integer getSex(String ID)
    {
        if (ID.length() != 15 && ID.length() != 18)
        {
            return null;
        }
        int isex = 2;
        isex = ID.length() == 15 ? Integer.parseInt(ID.substring(14)) : Integer.parseInt(ID.substring(16, 17));
        return isex % 2 == 0 ? 2 : 1;
    }
    
    private String getBirthdate(String ID)
    {
        if (ID.length() != 15 && ID.length() != 18)
        {
            // 不是15位或18位返回null
            return null;
        }
        String birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID.substring(6, 14);
        String year = birth.substring(0, 4);
        String month = birth.substring(4, 6);
        String date = birth.substring(6);
        return year + "-" + month + "-" + date;
    }
    
    /**
     * 为表头项目设值
     */
    protected void setHeadValue(String itemKey, Object value)
    {
        getBillCardPanel().getHeadItem(itemKey).setValue(null);
        if (value != null)
        {
            getBillCardPanel().getHeadItem(itemKey).setValue(value);
        }
    }
    
    /**
     * 为表体项目设值
     */
    protected void setBodyValue(String tabCode, int row, String itemKey, Object value)
    {
        getBillCardPanel().getBillModel(tabCode).setValueAt(null, row, itemKey);
        if (value != null)
        {
            getBillCardPanel().getBillModel(tabCode).setValueAt(value, row, itemKey);
        }
    }
    
    /**
     * 得到参照的值
     */
    private String getStrValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        else if (value instanceof String[])
        {
            return ((String[]) value)[0];
        }
        return value.toString();
    }
    
    /***************************************************************************
     * 异动事件 变化后事件<br>
     * Created on 2010-6-8 8:55:22<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterTrnsEventChange(BillEditEvent evt)
    {
        Object objValue = evt.getValue();
        // 离职、离职后变动 在这里不可选
        if (ArrayUtils.contains(new Object[]{TrnseventEnum.DISMISSION.value(), TrnseventEnum.TRANSAFTERDIS.value()}, objValue))
        {
            String msg = ResHelper.getString("6007psn", "06007psn0165")/*
                                                                        * @res "“离职”、“离职后变动”不可选！"
                                                                        */;
            ShowStatusBarMsgUtil.showErrorMsgWithClear(msg, msg, getModel().getContext());
            getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(evt.getOldValue(), evt.getRow(), PsnJobVO.TRNSEVENT);
            return;
        }
        BillItem item = getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.TRNSTYPE);
        if (item != null)
        {
            getBillCardPanel().setBodyValueAt(null, evt.getRow(), PsnJobVO.TRNSTYPE, evt.getTableCode());
            ((UIRefPane) item.getComponent()).getRefModel().setWherePart(PsnJobVO.TRNSEVENT + "=" + objValue);
        }
    }
    
    /****************************************************************************
     * 表体字段编辑前事件{@inheritDoc}<br>
     * Created on 2010-6-8 9:31:01<br>
     * @see nc.ui.pub.bill.BillEditListener2#beforeEdit(nc.ui.pub.bill.BillEditEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean beforeEdit(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        if (HICommonValue.FUNC_CODE_REGISTER.equals(getModel().getContext().getNodeCode())
            && PsnJobVO.getDefaultTableName().equals(evt.getTableCode()))// 最新的任职记录不能在子表中维护
        {
            PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
            BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
            if ((psndocAggVO == null || psndocAggVO.getParentVO() == null || psndocAggVO.getParentVO().getPsnJobVO() == null || billModel == null)
                && getModel().getUiState() != UIState.ADD)
            {
                // 不是新增的情况下不能编辑
                return false;
            }
            int roeCount = getBillCardPanel().getBillTable(evt.getTableCode()).getRowCount();
            if (roeCount > 0 && evt.getRow() == roeCount - 1 && getModel().getUiState() != UIState.ADD)
            {
                // 编辑时 最新记录不能修改
                return false;
            }
        }
        
        if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_ORG.equals(evt.getKey()))
        {
            // 组织
            beforePartTimePk_OrgEdit(evt);
        }
        else if (PsnJobVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_ORG.equals(evt.getKey()))
        {
            // 组织
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_orgs");
                if (!StringUtils.isBlank(powerSql))
                {
                    enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
                }
                // 逐级管控(入职登记——工作信息-组织)
                try
                {
                    String gkSql =
                        NCLocator.getInstance().lookup(IPsndocService.class)
                            .queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);
                    if (!StringUtils.isEmpty(gkSql))
                    {
                        enableSql += " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
                    }
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
                ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_DEPT.equals(evt.getKey()))
        {
            // 部门
            beforePkDeptEdit(evt, evt.getTableCode());
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.TRNSTYPE.equals(evt.getKey()))
        {
            // 异动类型
            beforeTrnsEventEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_POST.equals(evt.getKey()))
        {
            // 岗位
            beforePkPostEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOB.equals(evt.getKey()))
        {
            // 职务
            PsnJobVO psnjob =
                (PsnJobVO) getBillCardPanel().getBillModel(evt.getTableCode()).getBodyValueRowVO(evt.getRow(), PsnJobVO.class.getName());
            // if (psnjob.getPk_post() != null)
            // {
            // return false;
            // }
            beforePkJobEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.SERIES.equals(evt.getKey()))
        {
            // 职务类别
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isNotBlank(pk_job) || StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_POSTSERIES.equals(evt.getKey()))
        {
            // 岗位序列
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOBGRADE.equals(evt.getKey()))
        {
            // 职级
            beforePkJobGradeEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOBRANK.equals(evt.getKey()))
        {
            // 职等
            BillItem item = (BillItem) evt.getSource();
            String pk_jobrank = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBRANK + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isBlank(pk_jobrank))
            {
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
                return true;
            }
            
            String pk_jobgrade = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_postseries =
                (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
        }
        else if (ReqVO.getDefaultTableName().equals(evt.getTableCode()) && ReqVO.PK_POSTREQUIRE_B.equals(evt.getKey()))
        {
            // 达到级别
            beforePkPostRequire_b(evt);
        }
        else if (CapaVO.getDefaultTableName().equals(evt.getTableCode()) && CapaVO.PK_PE_SCOGRDITEM.equals(evt.getKey()))
        {
            // 员工达到等级
            BillItem item = (BillItem) evt.getSource();
            // Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(),
            // CapaVO.PK_PE_INDI).getValueObject();
            Object objValue = getBodyItemValue(evt.getTableCode(), CapaVO.PK_PE_INDI + IBillItem.ID_SUFFIX, evt.getRow());
            if (objValue != null)
            {
                ((CPindiGradeRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_indi((String) objValue);
            }
        }
        else if (PsnChgVO.getDefaultTableName().endsWith(evt.getTableCode()) && PsnChgVO.PK_CORP.equals(evt.getKey()))
        {
            // 任职单位
            // AdminOrgDefaultRefModel model = new AdminOrgDefaultRefModel();
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setPk_group(PubEnv.getPk_group());
            String powerSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                    "org_orgs");
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setUseDataPower(false);
            if (!StringUtils.isBlank(powerSql))
            {
                ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(
                    " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")");
            }
            String where =
                " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) and pk_adminorg in ( select pk_corp from org_corp where enablestate = 2 )";
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(where);
            // ((UIRefPane) ((BillItem)
            // evt.getSource()).getComponent()).setRefModel(model);
        }
        else if (CtrtVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            return beforeCtrtEdit(evt);
        }
        else if (CertVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            Boolean isStart = (Boolean) getBodyItemValue(CertVO.getDefaultTableName(), CertVO.ISSTART, evt.getRow());
            
            if (null != isStart)
            {
                if (Boolean.TRUE.equals(isStart))
                {
                    setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CertVO.IDTYPE, CertVO.ID});
                }
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_PSNCL.equals(evt.getKey()))
        {
            // 人员类别
            Object grpObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                String powerSql =
                    HiSQLHelper.getPsnPowerSql((String) grpObjValue, HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                        "bd_psncl");
                if (!StringUtils.isBlank(powerSql))
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
                }
            }
        }
        else if (PsnJobVO.POSTSTAT.equals(evt.getKey()))
        {
            // 是否在岗
            if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                Boolean isEnd = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), PsnJobVO.ENDFLAG);
                return isEnd != null && !isEnd;
            }
        }
        else if (KeyPsnVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            Boolean endflag = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), KeyPsnVO.ENDFLAG);
            if (endflag != null && endflag.booleanValue() && !evt.getKey().equals(KeyPsnVO.MEMO))
            {
                return false;
            }
        }
        
        selectedRow = evt.getRow();
        return billItemSource.isEdit();
    }
    
    /**
     * 合同页签编辑前监听
     * @return
     */
    private boolean beforeCtrtEdit(BillEditEvent evt)
    {
        if (evt.getRow() == 0 && CtrtVO.CONTTYPE.equals(evt.getKey()))
        {
            return false;
        }
        if (ArrayUtils.contains(ctrtTrialFlds, evt.getKey()))
        {
            Boolean ifProp = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.IFPROP);
            return ifProp != null && ifProp.booleanValue();
        }
        
        if (CtrtVO.PK_CONTTEXT.equals(evt.getKey()))
        {
            BillItem item = (BillItem) evt.getSource();
            UIRefPane rp = (UIRefPane) item.getComponent();
            /** 1002Z710000000017GUF是合同模板类型 */
            rp.getRefModel().addWherePart(
                " and hrcm_contmodel.pk_org in ('" + IOrgConst.GLOBEORG + "','" + getModel().getContext().getPk_group() + "','"
                    + getModel().getContext().getPk_org() + "') and hrcm_contmodel.VMODELTYPE = '1002Z710000000017GUF' ");
            return item.isEdit();
        }
        
        String termtype = (String) getBodyItemValue(CtrtVO.getDefaultTableName(), CtrtVO.TERMTYPE, evt.getRow());
        
        if (StringUtils.isNotBlank(termtype))
        {
            if (HRCMTermUnitUtils.TERM_TYPE_NONFIXED.equals(termtype))
            { // 无固定期限
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.IFPROP});// 是否试用可编辑
            }
            else if (HRCMTermUnitUtils.TERM_TYPE_TASK.equals(termtype))
            { // 以完成一定工作任务为期限
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.IFPROP});// 是否试用置灰不可编辑
            }
            else
            { // 固定期限
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE, CtrtVO.IFPROP});
            }
        }
        
        return getBillCardPanel().getBillModel(evt.getTableCode()).getItemByKey(evt.getKey()).isEdit();
    }
    
    /****************************************************************************
     * 表头字段编辑前事件{@inheritDoc}<br>
     * Created on 2010-5-20 15:03:32<br>
     * @see nc.ui.pub.bill.BillCardBeforeEditListener#beforeEdit(nc.ui.pub.bill.BillItemEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean beforeEdit(BillItemEvent evt)
    {
        if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT).equals(evt.getItem().getKey()))
        {
            // 部门
            Object objValue = getHeadItemValue((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG));
            if (evt.getItem() != null)
            {
                ((UIRefPane) evt.getItem().getComponent()).getRefModel().setPk_org((String) objValue);
                String cond = " and ( " + SQLHelper.getNullSql("hrcanceled") + " or hrcanceled = 'N' ) and depttype <> 1 ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    cond += " and " + powerSql;
                }
                ((UIRefPane) evt.getItem().getComponent()).getRefModel().addWherePart(cond);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSTYPE).equals(evt.getItem().getKey()))
        {
            // 入职类型
            String strTrnEvent = PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT;
            Object objValue = getHeadItemValue(strTrnEvent);
            if (objValue != null)
            {
                BillItem item = evt.getItem();
                if (item != null)
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + PsnJobVO.TRNSEVENT + "=" + objValue);
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST).equals(evt.getItem().getKey()))
        {
            // 岗位
            String pk_org = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG);
            String pk_dept = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT);
            BillItem item = evt.getItem();
            if (item != null)
            {
                PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
                postModel.setPk_org(pk_org);
                postModel.setPkdept(pk_dept);
                String cond =
                    " and ( " + SQLHelper.getNullSql(PostVO.TABLENAME + ".hrcanceled") + " or " + PostVO.TABLENAME + ".hrcanceled = 'N' ) ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    cond += " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
                }
                postModel.addWherePart(cond);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).equals(evt.getItem().getKey()))
        {
            // 职务
            Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG);
            BillItem item = evt.getItem();
            if (item != null)
            {
                if (objValue != null)
                {
                    ((UIRefPane) item.getComponent()).setPk_org(objValue.toString());
                }
                else
                {
                    ((UIRefPane) item.getComponent()).setPk_org(getModel().getContext().getPk_group());
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).equals(evt.getItem().getKey()))
        {
            // 职级
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                
                ((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
            if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post)) item.setEnabled(false);
            else
                item.setEnabled(true);
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).equals(evt.getItem().getKey()))
        {
            // 职等
            BillItem item = evt.getItem();
            String pk_jobrank = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK);
            if (StringUtils.isBlank(pk_jobrank))
            {
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(null, null);
                // ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
                return true;
            }
            
            String pk_jobgrade = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE);
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNCL).equals(evt.getItem().getKey()))
        {
            // 人员类别
            BillItem item = evt.getItem();
            if (item != null)
            {
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                        "bd_psncl");
                if (!StringUtils.isBlank(powerSql))
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG).equals(evt.getItem().getKey()))
        {
            // 组织
            BillItem item = evt.getItem();
            if (item != null)
            {
                String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";
                
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_orgs");
                if (!StringUtils.isBlank(powerSql))
                {
                    enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ") ";
                }
                ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
                
                // 逐级管控(入职登记——工作信息-组织)
                try
                {
                    String gkSql =
                        NCLocator.getInstance().lookup(IPsndocService.class)
                            .queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);
                    if (!StringUtils.isEmpty(gkSql))
                    {
                        enableSql += " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
                    }
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).equals(evt.getItem().getKey()))
        {
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            if (StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).equals(evt.getItem().getKey()))
        {
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            if (StringUtils.isNotBlank(pk_job))
            {
                return false;
            }
        }
        else if (("nativeplace").equals(evt.getItem().getKey()))
        {
            // 籍贯、户口所在地字段选择参照时国家地区设置成为默认值为中国。 add by yanglt 20140809
            AbstractRefModel nativeRefModel = ((UIRefPane) getBillCardPanel().getHeadItem("nativeplace").getComponent()).getRefModel();
            ((RegionDefaultRefTreeModel) nativeRefModel).setPk_country("0001Z010000000079UJJ");// 籍贯
        }
        else if ("permanreside".equals(evt.getItem().getKey()))
        {
            
            AbstractRefModel refModel = ((UIRefPane) getBillCardPanel().getHeadItem("permanreside").getComponent()).getRefModel();
            ((RegionDefaultRefTreeModel) refModel).setPk_country("0001Z010000000079UJJ");// 户口所在地
        }
        return true;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 10:37:53<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePartTimePk_OrgEdit(BillEditEvent evt)
    {
        Object objValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        if (objValue != null && evt.getSource() != null)
        {
            BillItem item = (BillItem) evt.getSource();
            ((UIRefPane) item.getComponent()).getRefModel().setPk_group(objValue.toString());
            String enableSql = "  ";
            String powerSql =
                HiSQLHelper.getPsnPowerSql(objValue.toString(), HICommonValue.RESOUCECODE_ORG, HICommonValue.OPERATIONCODE_PARTDEFAULT,
                    "org_orgs");
            if (!StringUtils.isBlank(powerSql))
            {
                enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
            }
            ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
            ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 14:37:32<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkDeptEdit(BillEditEvent evt, String defaultTableName)
    {
        Object orgObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_ORG + IBillItem.ID_SUFFIX, evt.getRow());
        Object grpObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        // if (orgObjValue != null && evt.getSource() != null) {
        HRDeptRefModel deptRefModel = (HRDeptRefModel) ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel();
        deptRefModel.setPk_org((String) orgObjValue);
        // and ( " + SQLHelper.getNullSql("hrcanceled") + " or hrcanceled = 'N'
        // ) 子表的部门不限制是否撤销,保存时交验
        String cond = " and depttype <> 1 ";
        String powerSql =
            HiSQLHelper.getPsnPowerSql((String) grpObjValue, HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
        // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
        if (!StringUtils.isBlank(powerSql))
        {
            cond += " and " + powerSql;
        }
        if (PartTimeVO.getDefaultTableName().equals(defaultTableName))
        {
            deptRefModel.setShowDisbleOrg(Boolean.TRUE);
        }
        else
        {
            deptRefModel.setShowDisbleOrg(Boolean.FALSE);
        }
        deptRefModel.addWherePart(cond);
        // }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 11:15:13<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkJobEdit(BillEditEvent evt)
    {
        Object objValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        BillItem item = (BillItem) evt.getSource();
        if (item != null)
        {
            ((UIRefPane) item.getComponent()).getRefModel().setPk_group((String) objValue);
            // if (objValue != null) {
            // ((UIRefPane) item.getComponent()).setPk_org(objValue.toString());
            // } else {
            // ((UIRefPane)
            // item.getComponent()).setPk_org(getModel().getContext().getPk_group());
            // }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-14 9:23:49<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkJobGradeEdit(BillEditEvent evt)
    {
        // 职级
        String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_postseries = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
        
        BillItem item = (BillItem) evt.getSource();
        /** 20150812 修改sonar中blocker问题 */
        if (item == null) return;
        FilterTypeEnum filterType = null;
        String gradeSource = "";
        Map<String, Object> resultMap = null;
        try
        {
            resultMap =
                NCLocator.getInstance().lookup(IPsndocQryService.class).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
        if (!resultMap.isEmpty())
        {
            filterType = (FilterTypeEnum) resultMap.get("filterType");
            gradeSource = (String) resultMap.get("gradeSource");
        }
        
        ((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
        
        if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
            && StringUtils.isBlank(pk_post)) item.setEnabled(false);
        else
            item.setEnabled(true);
        
    }
    
    /***************************************************************************
     * 表体岗位编辑前事件<br>
     * Created on 2010-7-8 19:25:59<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkPostEdit(BillEditEvent evt)
    {
        String pk_org = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_ORG + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_dept = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_DEPT + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_group = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        BillItem item = (BillItem) evt.getSource();
        if (item != null)
        {
            PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
            postModel.setPk_group(pk_group);
            postModel.setPk_org(pk_org);
            if (!StringUtils.isBlank(pk_dept))
            {
                postModel.setPkdept(pk_dept);
            }
            else
            {
                postModel.setPkdept(null);
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(pk_group, HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    String cond = " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
                    postModel.addWherePart(cond);
                }
            }
        }
    }
    
    /***************************************************************************
     * 达到级别<br>
     * Created on 2010-7-26 16:49:47<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkPostRequire_b(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), ReqVO.PK_POSTREQUIRE_H).getValueObject();
        if (objValue != null)
        {
            ((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(" and pk_cindex" + "='" + objValue + "'");
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 10:06:50<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforeTrnsEventEdit(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.TRNSEVENT).getValueObject();
        if (objValue != null)
        {
            ((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(
                " and " + PsnJobVO.TRNSEVENT + "=" + objValue + " and enablestate = " + IPubEnumConst.ENABLESTATE_ENABLE);
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#canBeHidden()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean canBeHidden()
    {
        if (ArrayUtils.contains(new UIState[]{UIState.ADD, UIState.EDIT}, getModel().getUiState()))
        {
            return false;
        }
        return super.canBeHidden();
    }
    
    /***************************************************************************
     * 清空表体字段中的值<br>
     * Created on 2010-7-19 10:38:58<br>
     * @param strTabCode
     * @param iRowIndex
     * @param strBodyItemKeys
     * @author Rocex Wang
     ***************************************************************************/
    private void clearBodyItemValue(String strTabCode, int iRowIndex, String... strBodyItemKeys)
    {
        if (strBodyItemKeys == null || strBodyItemKeys.length == 0)
        {
            return;
        }
        BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(strTabCode);
        if (billModel == null)
        {
            return;
        }
        for (String strItemKey : strBodyItemKeys)
        {
            billModel.setValueAt(null, iRowIndex, strItemKey);
        }
    }
    
    /***************************************************************************
     * 清空表头字段中的值<br>
     * Created on 2010-7-19 10:34:21<br>
     * @param strHeadItemKeys
     * @author Rocex Wang
     ***************************************************************************/
    private void clearHeadItemValue(String... strHeadItemKeys)
    {
        if (strHeadItemKeys == null || strHeadItemKeys.length == 0)
        {
            return;
        }
        for (String strItemKey : strHeadItemKeys)
        {
            BillItem item = getBillCardPanel().getHeadItem(strItemKey);
            if (item != null)
            {
                item.clearViewData();
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-14 11:58:44<br>
     * @author Rocex Wang
     ***************************************************************************/
    private void filteByPsncl()
    {
        
        // 相关人员不支持根据人员类别切换模板
        
        if (HICommonValue.FUNC_CODE_POI.equals(getModel().getNodeCode()) || HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
        {
            disableHeaditems();
            // getBillCardPanel().setBillData(getBillCardPanel().getBillData());
            if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
            {
                PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
            }
            return;
        }
        
        disableHeaditems();
        
        if (ObjectUtils.equals(strPk_psncl, getModel().getPk_psncl()))
        {
            return;
        }
        strPk_psncl = getModel().getPk_psncl();
        
        BillTempletBodyVO[] billTempletBodyVOs = getBillCardPanel().getBillData().getBillTempletVO().getBodyVO();
        if (billTempletBodyVOs == null || billTempletBodyVOs.length <= 0)
        {
            return;
        }
        
        HashMap<String, PsnclinfosetVO> getConfigMap =
            new HrPsnclTemplateContainer().getPsnclConfigMap(getModel().getContext().getPk_org(), strPk_psncl);
        
        if (getConfigMap == null || getConfigMap.isEmpty())
        {
            
            // 没取到为按原模板设置
            for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs)
            {
                int pos = billTempletBodyVO.getPos();
                BillItem item = null;
                if (BillItem.HEAD == pos)
                {
                    item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
                }
                else if (BillItem.BODY == pos)
                {
                    item = getBillCardPanel().getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
                }
                if (item == null)
                {
                    continue;
                }
                if (item.getTableCode().equals(KeyPsnVO.getDefaultTableName()))
                {
                    item.setShow(false);
                    item.setNull(false);
                }
                else if (ArrayUtils.contains(fldBlastList, item.getKey()))
                {
                    item.setShow(false);
                    item.setNull(false);
                }
                else
                {
                    item.setShow(billTempletBodyVO.getShowflag());
                    item.setNull(billTempletBodyVO.getNullflag());
                }
            }
            
            afterFilterPsncl();
            
            return;
        }
        
        for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs)
        {
            int pos = billTempletBodyVO.getPos();
            BillItem item = null;
            if (BillItem.HEAD == pos)
            {
                item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
            }
            else if (BillItem.BODY == pos)
            {
                item = getBillCardPanel().getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
            }
            if (item == null)
            {
                continue;
            }
            
            if (BillItem.BODY == pos
                && (PsnOrgVO.getDefaultTableName().equals(billTempletBodyVO.getTable_code()) || KeyPsnVO.getDefaultTableName().equals(
                    billTempletBodyVO.getTable_code())))
            {
                item.setShow(false);
                item.setNull(false);
                continue;
            }
            
            if (ArrayUtils.contains(fldBlastList, item.getKey()))
            {
                item.setShow(false);
                item.setNull(false);
                continue;
            }
            
            item.setShow(billTempletBodyVO.getShowflag());
            item.setNull(billTempletBodyVO.getNullflag());
            // 根据 单据模板的metadata，从Map中取 PsnclinfosetVO
            PsnclinfosetVO configVO = getConfigMap.get(billTempletBodyVO.getMetadataproperty());
            if (configVO == null)
            {
                // Map中没有，看是不是自定义项，如果是自定义项，则不显示
                continue;
            }
            
            item.setShow(configVO.getUsedflag() != null && configVO.getUsedflag().booleanValue() && billTempletBodyVO.getShowflag());
            if (!item.isShow())
            {
                item.setNull(false);
            }
            
            item.setNull(configVO.getMustflag() != null && configVO.getMustflag().booleanValue() && item.isShow());
            
        }
        afterFilterPsncl();
    }
    
    private void afterFilterPsncl()
    {
        
        hideQutifySet();
        
        getBillCardPanel().setBillData(getBillCardPanel().getBillData());
        
        // 更改业务子集页签的颜色
        PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
        // 所有的子集不支持表格排序,不支持右键菜单
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().getBillTable(strTabCode).removeSortListener();
                getBillCardPanel().getBodyPanel(strTabCode).setBBodyMenuShow(false);
            }
        }
        
        setCellRenderer();
    }
    
    private void hideQutifySet()
    {
        boolean isJQStart = false;
        isJQStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRJQ);
        if (isJQStart)
        {
            return;
        }
        // 没有启用任职资格 则不显示任职资格页签
        BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
        if (bm == null)
        {
            return;
        }
        BillItem[] items = bm.getBodyItems();
        for (int i = 0; items != null && i < items.length; i++)
        {
            items[i].setShow(false);
        }
        
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-8 19:27:44<br>
     * @param strTabCode
     * @param strItemKey
     * @return Object
     * @author Rocex Wang
     ***************************************************************************/
    private Object getBodyItemValue(String strTabCode, String strItemKey, int iRowIndex)
    {
        return getBillCardPanel().getBillModel(strTabCode).getValueAt(iRowIndex, strItemKey);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @return the dataManger
     ***************************************************************************/
    public PsndocDataManager getDataManger()
    {
        return dataManger;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:00:30<br>
     * @author Rocex Wang
     * @return the fieldRelationUtil
     ***************************************************************************/
    public FieldRelationUtil getFieldRelationUtil()
    {
        return fieldRelationUtil;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-10 9:16:03<br>
     * @see nc.ui.uif2.editor.BillForm#getModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocModel getModel()
    {
        return (PsndocModel) super.getModel();
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @return the superValidator
     ***************************************************************************/
    public SuperFormEditorValidatorUtil getSuperValidator()
    {
        return superValidator;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.uif2.editor.BillForm#getValue()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Object getValue()
    {
        
        // 被改变的数据，包括新增的、修改的、删除的
        PsndocAggVO psndocChangedAggVO = (PsndocAggVO) super.getValue();
        
        // 维护节点 如果能力素质被改变了则赋成直接增加
        if (getModel().getContext().getNodeCode().equals(HICommonValue.FUNC_CODE_EMPLOYEE))
        {
            SuperVO[] capa = psndocChangedAggVO.getTableVO(CapaVO.getDefaultTableName());
            for (int i = 0; capa != null && i < capa.length; i++)
            {
                if (capa[i].getStatus() == VOStatus.UPDATED)
                {
                    capa[i].setAttributeValue(CapaVO.ASSSOURCETYPE, 3);
                }
            }
        }
        
        // 界面上现有的全部数据，包括新增的、修改的，以及没有改变的，但是不包括删除的
        PsndocAggVO psndocAggVO = (PsndocAggVO) getBillCardPanel().getBillData().getBillObjectByMetaData();
        
        // 把删除的子集中的数据合并进来
        psndocAggVO.mergeDeletedAggVO(psndocChangedAggVO);
        
        try
        {
            validateData(psndocAggVO);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        
        // 找到主表中的数据
        PsndocVO psndocVO = psndocAggVO.getParentVO();
        // 把表头上组织关系和工作记录信息也收集起来
        getBillCardPanel().getBillData().getHeaderValueVO(psndocVO);
        psndocVO.setPk_hrorg(getModel().getContext().getPk_org());
        psndocVO.getPsnOrgVO().setPk_hrorg(getModel().getContext().getPk_org());
        psndocVO.getPsnJobVO().setPk_hrorg(getModel().getContext().getPk_org());
        resetMacaId(psndocAggVO);
        // 把任职记录中的pk_org同步到基本信息和组织关系的pk_org中,不在前台做,放到后台做
        if (psndocVO.getPsnOrgVO().getPk_group() == null)
        {
            psndocVO.getPsnOrgVO().setPk_group(psndocVO.getPk_group());
        }
        if (psndocVO.getPsnJobVO().getPk_group() == null)
        {
            psndocVO.getPsnJobVO().setPk_group(psndocVO.getPk_group());
        }
        // psndoc不能完全根据UIState来，因为有可能采集来的是系统中的人员。
        psndocVO.setStatus(psndocVO.getPk_psndoc() == null ? VOStatus.NEW : VOStatus.UPDATED);
        if (HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
        {
            // 初次入职、修改、返聘再聘修改的时候3个VO状态都是一致的
            psndocVO.getPsnJobVO().setStatus(psndocVO.getStatus());
            psndocVO.getPsnOrgVO().setStatus(psndocVO.getStatus());
        }
        else if (HICommonValue.JOB_REHIRE.equals(getModel().getInJobType()))
        {
            // 返聘、再聘情况下新增的时候，人员信息状态是修改，组织关系和任职记录的状态是新增
            psndocVO.getPsnJobVO().setStatus(VOStatus.NEW);
            psndocVO.getPsnOrgVO().setStatus(VOStatus.NEW);
        }
        
        if (HICommonValue.JOB_REHIRE.equals(getModel().getInJobType()))
        {
            // 返聘再聘对于数据库中已存在的数据状态设为update
            SuperVO[] childVO = psndocAggVO.getAllChildrenVO();
            for (int i = 0; childVO != null && i < childVO.length; i++)
            {
                if (childVO[i].getPrimaryKey() != null && VOStatus.NEW == childVO[i].getStatus())
                {
                    childVO[i].setStatus(VOStatus.UPDATED);
                }
            }
        }
        // 往aggvo中合并主表数据
        psndocAggVO.setParentVO(psndocVO);
        
        // 为了防止非空校验，先附一个临时的值
        this.setBodyValue("hi_psndoc_cert", 0, psndocVO.IDTYPE, psndocVO.getIdtype());
        this.setBodyValue("hi_psndoc_cert", 0, psndocVO.ID, psndocVO.getId());
        
        return psndocAggVO;
    }
    
    /**
     * <br>
     * 保存前去掉澳门身份证后增加的特殊符号 Created on 2013-12-12 15:37:55<br>
     * @param psndocAggVO
     * @author caiqm
     */
    private void resetMacaId(PsndocAggVO psndocAggVO)
    {
        SuperVO[] subVOs = psndocAggVO.getAllChildrenVO();
        PsndocVO psndocVO = psndocAggVO.getParentVO();
        String pid = psndocVO.getId();
        if (HICommonValue.IDTYPE_MACAU.equals(psndocVO.getIdtype()) && pid.endsWith(")")
            && "(".equals(String.valueOf(pid.charAt(pid.length() - 3))))
        {
            psndocVO.setId(pid.substring(0, pid.length() - 3) + pid.charAt(pid.length() - 2));
        }
        
        for (int m = 0; m < subVOs.length; m++)
        {
            if (subVOs[m] instanceof CertVO && HICommonValue.IDTYPE_MACAU.equals(((CertVO) subVOs[m]).getIdtype()))
            {
                String id = ((CertVO) subVOs[m]).getId();
                if (id.endsWith(")") && "(".equals(String.valueOf(id.charAt(id.length() - 3))))
                {
                    ((CertVO) subVOs[m]).setId(id.substring(0, id.length() - 3) + id.charAt(id.length() - 2));
                }
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#handleEvent(nc.ui.uif2.AppEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void handleEvent(AppEvent evt)
    {
        
        if (AppEventConst.SHOW_EDITOR == evt.getType())
        {
            getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, evt.getSource(), null));
        }
        
        if (AppEventConst.SELECTION_CHANGED == evt.getType())
        {
            onSelectionChanged();
            // 默认加载身份证件子集，以保证主集信息可以正确回写到子集上
            SuperVO subVOs[] = null;
            BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
            try
            {
                subVOs = getDataManger().querySubVO(PsnJobVO.getDefaultTableName(), null);
            }
            catch (BusinessException ex)
            {
                throw new BusinessRuntimeException(ex.getMessage(), ex);
            }
            if (subVOs != null && subVOs.length > 0)
            {
                billModel.clearBodyData();
                billModel.addLine(subVOs.length);
                for (int i = 0; i < subVOs.length; i++)
                {
                    billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                    billModel.setRowState(i, BillModel.NORMAL);
                }
                billModel.execLoadFormula();
            }
            getHashSubHaveLoad().add(PsnJobVO.getDefaultTableName());
        }
        else
        {
            super.handleEvent(evt);
        }
    }
    
    @Override
    protected void onNotEdit()
    {
        super.onNotEdit();
        getBillCardPanel().getBillData().clearShowWarning();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:07<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#initUI()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void initUI()
    {
        
        if (getTemplateContainer() instanceof HrPsnclTemplateContainer)
        {
            HrPsnclTemplateContainer templateContainer = (HrPsnclTemplateContainer) getTemplateContainer();
            templateContainer.setPk_org(getModel().getContext().getPk_org());
        }
        super.initUI();
        // BillItem itemBeginDate=getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" +
        // PsnOrgVO.BEGINDATE);
        isEditBeginDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).isEdit();
        isEditEndDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).isEdit();
        
        hideQutifySet();
        getBillCardPanel().setBillData(getBillCardPanel().getBillData());
        
        getBillCardPanel().addBodyEditListener2(this);
        getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null && strTabCodes.length > 0)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().addEditListener(strTabCode, this);
                getBillCardPanel().addBodyEditListener2(strTabCode, this);
            }
        }
        // 添加子页签监听
        getBillCardPanel().getBodyTabbedPane().addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent event)
            {
                subTabChanged(event);
            }
        });
        // 安置字段变化逻辑监听器
        if (getFieldRelationUtil() != null)
        {
            getFieldRelationUtil().setFormeditor(this);
            getFieldRelationUtil().putBombToFormEditor();
        }
        // 校验器（在此处设置其内部成员，虽然在其他地方也注入了校验器，它是单例的，其他地方使用此校验器时，initUI应该已经完成）
        if (superValidator != null)
        {
            superValidator.setFormeditor(this);
            superValidator.getComponentMap().put("model", getModel());
            superValidator.getComponentMap().put("utils", new EvalUtils(this.getModel().getContext()));
        }
        // 更改业务子集页签的颜色
        PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
        getBillCardPanel().getBodyTabbedPane().setTabLayoutPolicy(ExtTabbedPane.SCROLL_TAB_LAYOUT);
        
        // 去掉字表的右键菜单
        String[] tabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
        for (int i = 0; tabCodes != null && i < tabCodes.length; i++)
        {
            getBillCardPanel().getBillTable(tabCodes[i]).removeSortListener();
            getBillCardPanel().getBodyPanel(tabCodes[i]).setBBodyMenuShow(false);
        }
        
        disableHeaditems();
        
        setCellRenderer();
        
        if (HICommonValue.FUNC_CODE_REGISTER.equals(getModel().getContext().getNodeCode()))
        {
            // 只有在入职登记时，才为异动事件下拉框过滤掉【离职】和【离职后变动】两个item
            DefaultConstEnum[] enumItems = initTransevent();
            BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT);
            UIComboBox combobox = (UIComboBox) item.getComponent();
            combobox.removeAllItems();
            combobox.addItems(enumItems);
        }
        
        ((UIRefPane) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).getComponent()).getUITextField()
            .addFocusListener(this);
    }
    
    /**
     * 为异动事件下拉框过滤掉【离职】和【离职后变动】两个item
     * @return
     */
    private DefaultConstEnum[] initTransevent()
    {
        List<DefaultConstEnum> items = new ArrayList<DefaultConstEnum>();
        // 加载元数据中定义的“异动事件”枚举，初始化下拉列表框组件
        try
        {
            IComponent ibean = MDBaseQueryFacade.getInstance().getComponentByID("f57904bd-0037-4cea-842d-f33708084ab8");
            List<IEnumType> enums = ibean.getEnums();
            
            // 在返回的所有的枚举组件中查找指定名称的枚举
            IConstEnum[] agreementTypeEnum = null;
            for (IEnumType iet : enums)
            {
                if ("trnsevent".equals(iet.getName()))
                {
                    agreementTypeEnum = iet.getConstEnums();
                    break;
                }
            }
            // 根据枚举值，构造下拉列表框的值域对象
            items = new ArrayList<DefaultConstEnum>();
            for (IConstEnum pte : agreementTypeEnum)
            {
                
                if ((Integer) pte.getValue() == 4/* 离职 */|| (Integer) pte.getValue() == 5/* 离职后变动 */)
                {
                    continue;
                }
                items.add(new DefaultConstEnum(pte.getValue(), pte.getName()));
            }
        }
        catch (Throwable t)
        {
            Logger.error("[异动事件]枚举加载失败，可能是指定的元数据不存在，或注入的枚举名称错误。");
        }
        return items.toArray(new DefaultConstEnum[items.size()]);
    }
    
    private void disableHeaditems()
    {
        try
        {
            String[] keys = getModel().getHiddenKeys();
            
            for (int i = 0; keys != null && i < keys.length; i++)
            {
                BillItem item = getBillCardPanel().getHeadItem(keys[i]);
                if (item != null)
                {
                    item.setEnabled(false);
                }
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
    }
    
    /***************************************************************************
     * 处理子集加载数据<br>
     * Created on 2010-3-2 11:03:11<br>
     * @author Rocex Wang
     ***************************************************************************/
    public void loadCurrentRowSubData()
    {
        // if (!this.isShowing())
        // {
        // // 卡片不显示的时候不加载字表
        // return;
        // }
        
        int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
        if (tabIndex < 0)
        {
            // 如果当前选择的页签为-1 返回
            return;
        }
        BillModel billModel = getBillCardPanel().getBillModel();
        String strTabCode = billModel.getTabvo().getTabcode();
        // 不判断是否已加载,每次都查
        if (getModel().getBusinessInfoSet().contains(strTabCode) && UIState.ADD == getModel().getUiState())
        {
            // 新增情况下不加载业务子集
            return;
        }
        
        if ((UIState.EDIT == getModel().getUiState() || UIState.ADD == getModel().getUiState())
            && (getHashSubHaveLoad().contains(strTabCode)) && billModel.getRowCount() > 0)
        {
            // 编辑态,如果加载过就不再加载
            return;
        }
        
        SuperVO subVOs[] = null;
        try
        {
            subVOs = getDataManger().querySubVO(strTabCode, null);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        if (subVOs != null && subVOs.length > 0)
        {
            billModel.clearBodyData();
            billModel.addLine(subVOs.length);
            for (int i = 0; i < subVOs.length; i++)
            {
                billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                billModel.setRowState(i, BillModel.NORMAL);
            }
            billModel.execLoadFormula();
        }
        getHashSubHaveLoad().add(strTabCode);
        getModel().fireEvent(new AppEvent("changeBtnState"));
    }
    
    @Override
    public void showMeUp()
    {
        super.showMeUp();
        if (UIState.ADD != getModel().getUiState())
        {
            synchronizeDataFromModel();
        }
        loadCurrentRowSubData();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#onAdd()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void onAdd()
    {
        getHashSubHaveLoad().clear();
        filteByPsncl();
        super.onAdd();
        
        // 设置界面默认值
        BillItem[] items = getBillCardPanel().getBillData().getHeadTailItems();
        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                BillItem item = items[i];
                if ("hi_psnorg_orgrelaid".equals(item.getKey()) || "hi_psnjob_poststat".equals(item.getKey()))
                {
                    continue;
                }
                Object value = item.getDefaultValueObject();
                if (value != null)
                {
                    item.setValue(value);
                }
                
            }
        }
        
        // 设置人员编码或员工号由编码规则自动生成时，需要执行一次这些字段上的编辑公式 heqiaoa 20150522
        if (getModel().getPsndocCodeContext() != null)
        {
            BillItem billItemCode = getBillCardPanel().getHeadItem(PsndocVO.CODE);
            if (billItemCode != null && HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
            {
                // 人员编码
                billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
                getBillCardPanel().execHeadEditFormulas();
            }
        }
        if (getModel().getPsndocClerkCodeContext() != null)
        {
            BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE);
            if (item2 != null && HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
            {
                // 员工号自动生成
                item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
                getBillCardPanel().execHeadEditFormulas();
            }
        }
        disableHeaditems();
        getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(false);
        // 代码里面展现界面时自动加载公式(显示公式)
        /**
         * @author yangzxa 2015年9月24日09:29:44 因为修改入职登记中澳门身份证人员后面括号显示多次。</br> 如果不将下一行代码注释掉，会导致公式执行两次
         */
        // getBillCardPanel().execHeadTailLoadFormulas();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#onEdit()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void onEdit()
    {
        getHashSubHaveLoad().clear();
        filteByPsncl();
        super.onEdit();
        // 员工维护的时候，工作记录中的业务字段不允许修改
        if (HICommonValue.FUNC_CODE_EMPLOYEE.equals(getModel().getNodeCode())
            || HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
        {
            for (String strFieldCode : strBusiFieldInJobs)
            {
                BillItem billItem = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + strFieldCode);
                if (billItem != null)
                {
                    billItem.setEdit(false);
                }
            }
            BillItem[] item = getBillCardPanel().getHeadShowItems("hi_psnjob");
            for (int i = 0; item != null && i < item.length; i++)
            {
                if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE).equals(item[i].getKey()))
                {
                    continue;
                }
                item[i].setEdit(false);
            }
            
            // 如果当前人员是离职人员,那组织关系中的开始结束日期不能修改
            Boolean isEnd =
                (Boolean) getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDFLAG).getValueObject();
            if (isEnd != null && isEnd.booleanValue())
            {
                getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).setEdit(false);
                getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).setEdit(false);
            }
            else
            {
                if (isEditBeginDate == true)
                    getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).setEdit(true);
                if (isEditEndDate == true)
                    getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).setEdit(true);
            }
        }
        
        if (getModel().getPsndocCodeContext() != null)
        {
            BillItem billItemCode = getBillCardPanel().getHeadItem(PsndocVO.CODE);
            if (billItemCode != null)
            {
                billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
            }
        }
        
        if (getModel().getPsndocClerkCodeContext() != null)
        {
            BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE);
            if (item2 != null)
            {
                // 员工号自动生成
                item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
            }
        }
        
        String[] codes = new String[]{/* PsndocVO.ID, PsndocVO.IDTYPE, */PsndocVO.NAME};
        for (String strFieldCode : codes)
        {
            BillItem billItem = getBillCardPanel().getHeadItem(strFieldCode);
            if (billItem != null)
            {
                billItem.setEdit(billItem.isEdit());
            }
        }
        // BillItem billItemID = getBillCardPanel().getHeadItem(PsndocVO.ID);
        // if (billItemID != null)
        // {
        // billItemID.setEdit(false);
        // }
        //
        // BillItem billItemIDType = getBillCardPanel().getHeadItem(PsndocVO.IDTYPE);
        // if (billItemIDType != null)
        // {
        // billItemIDType.setEdit(false);
        // }
        
        BillItem billItemPsncl = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNCL);
        if (billItemPsncl != null)
        {
            billItemPsncl.setEdit(false);
        }
        
        // 员工信息维护、查询节点不能对业务子集进行维护
        if (ArrayUtils.contains(new String[]{
            HICommonValue.FUNC_CODE_EMPLOYEE,
            HICommonValue.FUNC_CODE_PSN_INFO,
            HICommonValue.FUNC_CODE_KEYPSN}, getModel().getContext().getNodeCode()))
        {
            for (Iterator iterator = getModel().getBusinessInfoSet().iterator(); iterator.hasNext();)
            {
                String strTabCode = (String) iterator.next();
                if ((CtrtVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRCM))
                    || (CapaVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRCP))
                    || (TrainVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRTRM))
                    || (AssVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRPE)))
                {
                    continue;
                }
                
                if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getContext().getNodeCode())
                    && KeyPsnVO.getDefaultTableName().equals(strTabCode))
                {
                    continue;
                }
                
                BillModel billModel = getBillCardPanel().getBillModel(strTabCode);
                if (billModel != null)
                {
                    billModel.setEnabled(false);
                }
            }
        }
        // 职级
        BillItem itemJOBGRADE = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE);
        String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
        String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
        String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
        String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
        
        if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
            && StringUtils.isBlank(pk_post)) itemJOBGRADE.setEnabled(false);
        else
            itemJOBGRADE.setEnabled(true);
        disableHeaditems();
        
        // 默认加载身份证件子集，以保证主集信息可以正确回写到子集上
        SuperVO subVOs[] = null;
        BillModel billModel = getBillCardPanel().getBillModel(CertVO.getDefaultTableName());
        try
        {
            subVOs = getDataManger().querySubVO(CertVO.getDefaultTableName(), null);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        if (subVOs != null && subVOs.length > 0)
        {
            billModel.clearBodyData();
            billModel.addLine(subVOs.length);
            for (int i = 0; i < subVOs.length; i++)
            {
                billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                billModel.setRowState(i, BillModel.NORMAL);
            }
            billModel.execLoadFormula();
        }
        getHashSubHaveLoad().add(CertVO.getDefaultTableName());
        getModel().fireEvent(new AppEvent("changeBtnState"));
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @param dataManger the dataManger to set
     ***************************************************************************/
    public void setDataManger(PsndocDataManager dataManger)
    {
        this.dataManger = dataManger;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-19 15:48:39<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#setDefaultValue()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void setDefaultValue()
    {
        super.setDefaultValue();
        // 籍贯和户口所在地设置默认国家为中国——heqiaoa 2014-09-23
        BillItem nativeplace_item = getBillCardPanel().getHeadItem("nativeplace");
        BillItem permanreside_item = getBillCardPanel().getHeadItem("permanreside");
        if (null != nativeplace_item && null != nativeplace_item.getComponent())
        {
            RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) nativeplace_item.getComponent()).getRefModel();
            regoin.setPk_country("0001Z010000000079UJJ"); // 默认设置当前行政区划均属于中国境内
        }
        if (null != permanreside_item && null != permanreside_item.getComponent())
        {
            RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) permanreside_item.getComponent()).getRefModel();
            regoin.setPk_country("0001Z010000000079UJJ"); // 默认设置当前行政区划均属于中国境内
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:00:30<br>
     * @author Rocex Wang
     * @param fieldRelationUtil the fieldRelationUtil to set
     ***************************************************************************/
    public void setFieldRelationUtil(FieldRelationUtil fieldRelationUtil)
    {
        this.fieldRelationUtil = fieldRelationUtil;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-10 9:16:28<br>
     * @param model
     * @author Rocex Wang
     ***************************************************************************/
    public void setModel(PsndocModel model)
    {
        super.setModel(model);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @param superValidator the superValidator to set
     ***************************************************************************/
    public void setSuperValidator(SuperFormEditorValidatorUtil superValidator)
    {
        this.superValidator = superValidator;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.uif2.editor.BillForm#setValue(java.lang.Object)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void setValue(Object object)
    {
        getBillCardPanel().getBillData().setHeaderValueVO(null);
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().getBillModel(strTabCode).clearBodyData();
            }
        }
        super.setValue(object);
        IBusinessEntity be = getBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity();
        BillItem headItems[] = getBillCardPanel().getBillData().getHeadTailItems();
        if (headItems == null)
        {
            return;
        }
        PsndocVO psndocVO = null;
        NCObject ncobject = null;
        if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD)
        {
            ncobject = DASFacade.newInstanceWithContainedObject(be, object);
        }
        else if (be.getBeanStyle().getStyle() == BeanStyleEnum.NCVO || be.getBeanStyle().getStyle() == BeanStyleEnum.POJO)
        {
            if (object instanceof AggregatedValueObject)
            {
                object = ((AggregatedValueObject) object).getParentVO();
                ncobject = DASFacade.newInstanceWithContainedObject(be, object);
            }
            else
            {
                ncobject = DASFacade.newInstanceWithContainedObject(be, object);
            }
        }
        if (ncobject == null)
        {
            return;
        }
        psndocVO = (PsndocVO) ncobject.getModelConsistObject();
        // 设置表头数据
        try
        {
            BatchMatchContext.getShareInstance().setInBatchMatch(true);
            BatchMatchContext.getShareInstance().clear();
            for (BillItem item : headItems)
            {
                if ((item.getKey().startsWith("hi_psnorg_") || item.getKey().startsWith("hi_psnjob_"))
                    && item.getMetaDataProperty() != null)
                {
                    Object value = psndocVO.getAttributeValue(item.getKey());
                    if (item.isIsDef())
                    {
                        value = item.converType(value);
                    }
                    item.setValue(value);
                }
            }
            BatchMatchContext.getShareInstance().executeBatch();
        }
        finally
        {
            BatchMatchContext.getShareInstance().setInBatchMatch(false);
        }
        // nc.ui.uif2.editor.BillForm.setValue(Object)中已执行过公式
        // ——heqiaoa 2014-08-23
        // if (isAutoExecLoadFormula())
        // {
        // getBillCardPanel().getBillData().getBillModel().execLoadFormula();
        // execLoadFormula();
        // }
        
    }
    
    /***************************************************************************
     * 切换子表页签事件<br>
     * Created on 2010-4-16 15:18:20<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void subTabChanged(ChangeEvent evt)
    {
        loadCurrentRowSubData();
        getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, evt.getSource(), null));
    }
    
    private void fillData()
    {
        
        if (!this.isShowing())
        {
            return;
        }
        try
        {
            // 选中的数据不为空时才加载数据
            if (getModel().getSelectedData() != null)
            {
                Object agg = NCLocator.getInstance().lookup(IPsndocService.class).fillData4Psndoc(getModel().getSelectedData());
                int i = agg == null ? -1 : getModel().findBusinessData(agg);
                if (i >= 0)
                {
                    getModel().getData().set(i, agg);
                }
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.uif2.editor.BillForm#synchronizeDataFromModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void synchronizeDataFromModel()
    {
        fillData();
        
        Object selectedData = getModel().getSelectedData();
        
        if (selectedData == null)
        {
            getModel().setPk_psncl(null);
            getModel().setCurrentPkPsndoc(null);
        }
        
        if (selectedData instanceof PsndocAggVO)
        {
            getModel().setPk_psncl(((PsndocAggVO) selectedData).getParentVO().getPsnJobVO().getPk_psncl());
            getModel().setCurrentPkPsndoc(((PsndocAggVO) selectedData).getParentVO().getPk_psndoc());
        }
        
        filteByPsncl();
        super.synchronizeDataFromModel();
        this.getBillCardPanel().getBillData().loadLoadHeadRelation();
        
        // 刷新单条后,加载子表数据
        PsndocAggVO seldata = (PsndocAggVO) getModel().getSelectedData();
        if (seldata == null)
        {
            return;
        }
        int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
        if (tabIndex < 0)
        {
            // 如果当前选择的页签为-1 返回
            return;
        }
        else
        {
            BillModel billModel = getBillCardPanel().getBillModel();
            String strTabCode = billModel.getTabvo().getTabcode();
            if (seldata.getTableVO(strTabCode) == null || seldata.getTableVO(strTabCode).length == 0)
            {
                loadCurrentRowSubData();
            }
        }
    }
    
    private SimpleDocServiceTemplate getService()
    {
        return new SimpleDocServiceTemplate("PsndocFormEditor");
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-13 9:07:21<br>
     * @throws BusinessException
     * @author Rocex Wang
     * @param psndocAggVO
     ***************************************************************************/
    private void validateData(PsndocAggVO psndocAggVO) throws BusinessException
    {
        
        // 此处不校验合同子集
        String[] busiSet =
            new String[]{
                PsnOrgVO.getDefaultTableName(),
                PsnJobVO.getDefaultTableName(),
                TrialVO.getDefaultTableName(),
                PsnChgVO.getDefaultTableName(),/* CtrtVO.getDefaultTableName(), */
                RetireVO.getDefaultTableName()};
        
        String[] checkSet = new String[]{PsnJobVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(), RetireVO.getDefaultTableName()};
        
        String[] tabCodes = psndocAggVO.getTableCodes();
        for (int i = 0; tabCodes != null && i < tabCodes.length; i++)
        {
            if (psndocAggVO.getTableVO(tabCodes[i]) == null || psndocAggVO.getTableVO(tabCodes[i]).length == 0)
            {
                continue;
            }
            if (CtrtVO.getDefaultTableName().equals(tabCodes[i]))
            {
                continue;
            }
            
            BillItem begin = getBillCardPanel().getBodyItem(tabCodes[i], "begindate");
            BillItem end = getBillCardPanel().getBodyItem(tabCodes[i], "enddate");
            if (begin == null || end == null)
            {
                // 有没有开始结束日期的子集
                continue;
            }
            
            boolean isBusinessSub = ArrayUtils.contains(busiSet, tabCodes[i]);
            boolean isCheckBtwRds = ArrayUtils.contains(checkSet, tabCodes[i]);
            String tableName = getBillCardPanel().getBillData().getBodyTableName(tabCodes[i]);
            String beginName = begin.getName();
            String endName = end.getName();
            CommnonValidator.validateLiteralDate(psndocAggVO.getTableVO(tabCodes[i]), "begindate", beginName, "enddate", endName,
                tableName, isBusinessSub, isCheckBtwRds);
        }
        
    }
    
    public void setHashSubHaveLoad(HashSet<String> hashSubHaveLoad)
    {
        this.hashSubHaveLoad = hashSubHaveLoad;
    }
    
    public HashSet<String> getHashSubHaveLoad()
    {
        return hashSubHaveLoad;
    }
    
    private void setCellRenderer()
    {
        BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
        if (bm == null)
        {
            return;
        }
        int colIndex = bm.getBodyColByKey(QulifyVO.AUTHENYEAR);
        UITable bt = getBillCardPanel().getBillTable(QulifyVO.getDefaultTableName());
        if (bt == null)
        {
            return;
        }
        colIndex = bt.convertColumnIndexToView(colIndex);
        if (colIndex < 0)
        {
            return;
        }
        bt.getColumnModel().getColumn(colIndex).setCellRenderer(new AuthenyearCellRenderer());
        
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-12-11 16:59:17<br>
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     * @author caiqm
     */
    @Override
    public void focusGained(FocusEvent arg0)
    {
        String id = (String) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).getValueObject();
        if (StringUtils.isNotEmpty(id))
        {
            if (id.endsWith(")"))
            {
                id = id.substring(0, id.length() - 3) + id.charAt(id.length() - 2);
            }
            getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).setValue(id);
        }
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-12-11 16:59:17<br>
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     * @author caiqm
     */
    @Override
    public void focusLost(FocusEvent arg0)
    {
        String[] formulas = getBillCardPanel().getBodyItem(CertVO.ID).getLoadFormula();
        getBillCardPanel().getBillData().getBillModel(CertVO.getDefaultTableName()).execFormulas(selectedRow, formulas);
    }
    
    private IPersistenceRetrieve retrieveService;
    
    private IPersistenceRetrieve getServiece()
    {
        if (retrieveService == null)
        {
            return NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
        }
        return retrieveService;
    }
    
}
