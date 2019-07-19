package nc.ui.hr.infoset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class infoset extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.model.InfoSetModelService getManageModelService(){
 if(context.get("manageModelService")!=null)
 return (nc.ui.hr.infoset.model.InfoSetModelService)context.get("manageModelService");
  nc.ui.hr.infoset.model.InfoSetModelService bean = new nc.ui.hr.infoset.model.InfoSetModelService();
  context.put("manageModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("boadatorfactory");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.model.InfoSetModel getInfoSetModel(){
 if(context.get("infoSetModel")!=null)
 return (nc.ui.hr.infoset.model.InfoSetModel)context.get("infoSetModel");
  nc.ui.hr.infoset.model.InfoSetModel bean = new nc.ui.hr.infoset.model.InfoSetModel();
  context.put("infoSetModel",bean);
  bean.setContext(getContext());
  bean.setService(getManageModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.view.InfoSetTemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.hr.infoset.view.InfoSetTemplateContainer)context.get("templateContainer");
  nc.ui.hr.infoset.view.InfoSetTemplateContainer bean = new nc.ui.hr.infoset.view.InfoSetTemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("InfoSort");  list.add("InfoSet");  list.add("InfoItem");  return list;}

public nc.ui.hr.infoset.model.InfoSetDataManager getInfoSetDataManager(){
 if(context.get("infoSetDataManager")!=null)
 return (nc.ui.hr.infoset.model.InfoSetDataManager)context.get("infoSetDataManager");
  nc.ui.hr.infoset.model.InfoSetDataManager bean = new nc.ui.hr.infoset.model.InfoSetDataManager();
  context.put("infoSetDataManager",bean);
  bean.setContext(getContext());
  bean.setModel(getInfoSetModel());
  bean.setTypeField("pk_infoset_sort");
  bean.setService(getManageModelService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.view.InfoSetListView getInfoSetListView(){
 if(context.get("infoSetListView")!=null)
 return (nc.ui.hr.infoset.view.InfoSetListView)context.get("infoSetListView");
  nc.ui.hr.infoset.view.InfoSetListView bean = new nc.ui.hr.infoset.view.InfoSetListView();
  context.put("infoSetListView",bean);
  bean.setNodekey("InfoSet");
  bean.setModel(getInfoSetModel());
  bean.setMultiSelectionEnable(false);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setEditInfoItemAction(getViewInfoItemAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.view.InfoSetFormEditor getInfoSetFormEditor(){
 if(context.get("infoSetFormEditor")!=null)
 return (nc.ui.hr.infoset.view.InfoSetFormEditor)context.get("infoSetFormEditor");
  nc.ui.hr.infoset.view.InfoSetFormEditor bean = new nc.ui.hr.infoset.view.InfoSetFormEditor();
  context.put("infoSetFormEditor",bean);
  bean.setNodekey("InfoSet");
  bean.setModel(getInfoSetModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setEditInfoItemAction(getEditInfoItemAction());
  bean.setActions(getManagedList1());
  bean.setTabActions(getManagedList2());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getAddInfoItemAction());  list.add(getEditInfoItemAction());  list.add(getDeleteInfoItemAction());  return list;}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getInfoSetListViewActions());  list.add(getInfoSetFormEditorActions());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getInfoSetFormEditorActions(){
 if(context.get("infoSetFormEditorActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("infoSetFormEditorActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getInfoSetFormEditor());  context.put("infoSetFormEditorActions",bean);
  bean.setModel(getInfoSetModel());
  bean.setActions(getManagedList4());
  bean.setEditActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getAddInfoSetAction());  list.add(getEditInfoSetAction());  list.add(getDeleteInfoSetAction());  list.add(getSeparatorAction());  list.add(getRefreshInfoSetAction());  list.add(getSeparatorAction());  list.add(getSetShowOrder());  list.add(getSeparatorAction());  list.add(getSyncMetaData());  list.add(getSyncTemplet());  list.add(getSeparatorAction());  list.add(getSetInfoItemMap());  return list;}

private List getManagedList5(){  List list = new ArrayList();  list.add(getSaveInfoSetAction());  list.add(getSeparatorAction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getInfoSetListViewActions(){
 if(context.get("infoSetListViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("infoSetListViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getInfoSetListView());  context.put("infoSetListViewActions",bean);
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getAddInfoSetAction());  list.add(getEditInfoSetAction());  list.add(getDeleteInfoSetAction());  list.add(getSeparatorAction());  list.add(getRefreshInfoSetAction());  list.add(getSeparatorAction());  list.add(getSetShowOrder());  list.add(getSeparatorAction());  list.add(getSyncMetaData());  list.add(getSyncTemplet());  list.add(getSeparatorAction());  list.add(getSetInfoItemMap());  list.add(getSeparatorAction());  list.add(getLocalizationFieldsMenuAction());  return list;}

public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillNotNullValidateService)context.get("billNotNullValidator");
  nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(getInfoSetFormEditor());  context.put("billNotNullValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getLocalizationFieldsMenuAction(){
 if(context.get("localizationFieldsMenuAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("localizationFieldsMenuAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("localizationFieldsMenuAction",bean);
  bean.setCode("localFieldsGroup");
  bean.setName(getI18nFB_e25168());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_e25168(){
 if(context.get("nc.ui.uif2.I18nFB#e25168")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#e25168");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#e25168",bean);  bean.setResDir("6007psn");
  bean.setResId("hrlocal-000000");
  bean.setDefaultValue("Localization Fields");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#e25168",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getAddLocalizationFieldsMY());  list.add(getAddLocalizationFieldsSG());  return list;}

public nc.ui.hr.infoset.action.AddMalaysiaFieldsAction getAddLocalizationFieldsMY(){
 if(context.get("addLocalizationFieldsMY")!=null)
 return (nc.ui.hr.infoset.action.AddMalaysiaFieldsAction)context.get("addLocalizationFieldsMY");
  nc.ui.hr.infoset.action.AddMalaysiaFieldsAction bean = new nc.ui.hr.infoset.action.AddMalaysiaFieldsAction();
  context.put("addLocalizationFieldsMY",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.AddSingaporeFieldsAction getAddLocalizationFieldsSG(){
 if(context.get("addLocalizationFieldsSG")!=null)
 return (nc.ui.hr.infoset.action.AddSingaporeFieldsAction)context.get("addLocalizationFieldsSG");
  nc.ui.hr.infoset.action.AddSingaporeFieldsAction bean = new nc.ui.hr.infoset.action.AddSingaporeFieldsAction();
  context.put("addLocalizationFieldsSG",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("separatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("separatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.AddInfoSortAction getAddInfoSortAction(){
 if(context.get("addInfoSortAction")!=null)
 return (nc.ui.hr.infoset.action.AddInfoSortAction)context.get("addInfoSortAction");
  nc.ui.hr.infoset.action.AddInfoSortAction bean = new nc.ui.hr.infoset.action.AddInfoSortAction();
  context.put("addInfoSortAction",bean);
  bean.setModel(getTreeAppModel());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.AddInfoSetAction getAddInfoSetAction(){
 if(context.get("addInfoSetAction")!=null)
 return (nc.ui.hr.infoset.action.AddInfoSetAction)context.get("addInfoSetAction");
  nc.ui.hr.infoset.action.AddInfoSetAction bean = new nc.ui.hr.infoset.action.AddInfoSetAction();
  context.put("addInfoSetAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setTreeModel(getTreeAppModel());
  bean.setFormEditor(getInfoSetFormEditor());
  bean.setBillListView(getInfoSetListView());
  bean.setDefaultValueProvider(getDefaultValueProvider());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.AddInfoItemAction getAddInfoItemAction(){
 if(context.get("addInfoItemAction")!=null)
 return (nc.ui.hr.infoset.action.AddInfoItemAction)context.get("addInfoItemAction");
  nc.ui.hr.infoset.action.AddInfoItemAction bean = new nc.ui.hr.infoset.action.AddInfoItemAction();
  context.put("addInfoItemAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setCardPanel(getInfoSetFormEditor());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.EditInfoSortAction getEditInfoSortAction(){
 if(context.get("editInfoSortAction")!=null)
 return (nc.ui.hr.infoset.action.EditInfoSortAction)context.get("editInfoSortAction");
  nc.ui.hr.infoset.action.EditInfoSortAction bean = new nc.ui.hr.infoset.action.EditInfoSortAction();
  context.put("editInfoSortAction",bean);
  bean.setModel(getTreeAppModel());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.EditInfoSetAction getEditInfoSetAction(){
 if(context.get("editInfoSetAction")!=null)
 return (nc.ui.hr.infoset.action.EditInfoSetAction)context.get("editInfoSetAction");
  nc.ui.hr.infoset.action.EditInfoSetAction bean = new nc.ui.hr.infoset.action.EditInfoSetAction();
  context.put("editInfoSetAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.EditInfoItemAction getEditInfoItemAction(){
 if(context.get("editInfoItemAction")!=null)
 return (nc.ui.hr.infoset.action.EditInfoItemAction)context.get("editInfoItemAction");
  nc.ui.hr.infoset.action.EditInfoItemAction bean = new nc.ui.hr.infoset.action.EditInfoItemAction();
  context.put("editInfoItemAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setEditor(getInfoSetFormEditor());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.EditInfoItemAction getViewInfoItemAction(){
 if(context.get("viewInfoItemAction")!=null)
 return (nc.ui.hr.infoset.action.EditInfoItemAction)context.get("viewInfoItemAction");
  nc.ui.hr.infoset.action.EditInfoItemAction bean = new nc.ui.hr.infoset.action.EditInfoItemAction();
  context.put("viewInfoItemAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setEditor(getInfoSetListView());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.DeleteInfoSortAction getDeleteInfoSortAction(){
 if(context.get("deleteInfoSortAction")!=null)
 return (nc.ui.hr.infoset.action.DeleteInfoSortAction)context.get("deleteInfoSortAction");
  nc.ui.hr.infoset.action.DeleteInfoSortAction bean = new nc.ui.hr.infoset.action.DeleteInfoSortAction();
  context.put("deleteInfoSortAction",bean);
  bean.setModel(getTreeAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.DeleteInfoSetAction getDeleteInfoSetAction(){
 if(context.get("deleteInfoSetAction")!=null)
 return (nc.ui.hr.infoset.action.DeleteInfoSetAction)context.get("deleteInfoSetAction");
  nc.ui.hr.infoset.action.DeleteInfoSetAction bean = new nc.ui.hr.infoset.action.DeleteInfoSetAction();
  context.put("deleteInfoSetAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.FirstLineAction getFirstLineAction(){
 if(context.get("firstLineAction")!=null)
 return (nc.ui.uif2.actions.FirstLineAction)context.get("firstLineAction");
  nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
  context.put("firstLineAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.PreLineAction getPreLineAction(){
 if(context.get("preLineAction")!=null)
 return (nc.ui.uif2.actions.PreLineAction)context.get("preLineAction");
  nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
  context.put("preLineAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.NextLineAction getNextLineAction(){
 if(context.get("nextLineAction")!=null)
 return (nc.ui.uif2.actions.NextLineAction)context.get("nextLineAction");
  nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
  context.put("nextLineAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.LastLineAction getLastLineAction(){
 if(context.get("lastLineAction")!=null)
 return (nc.ui.uif2.actions.LastLineAction)context.get("lastLineAction");
  nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
  context.put("lastLineAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.DeleteInfoItemAction getDeleteInfoItemAction(){
 if(context.get("deleteInfoItemAction")!=null)
 return (nc.ui.hr.infoset.action.DeleteInfoItemAction)context.get("deleteInfoItemAction");
  nc.ui.hr.infoset.action.DeleteInfoItemAction bean = new nc.ui.hr.infoset.action.DeleteInfoItemAction();
  context.put("deleteInfoItemAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setCardPanel(getInfoSetFormEditor());
  bean.setValidationService(getDeleteInfoItemAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.RefreshInfoSetAction getRefreshInfoSetAction(){
 if(context.get("refreshInfoSetAction")!=null)
 return (nc.ui.hr.infoset.action.RefreshInfoSetAction)context.get("refreshInfoSetAction");
  nc.ui.hr.infoset.action.RefreshInfoSetAction bean = new nc.ui.hr.infoset.action.RefreshInfoSetAction();
  context.put("refreshInfoSetAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setFormEditor(getInfoSetFormEditor());
  bean.setDataManager(getInfoSetDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.RefreshInfoTreeAction getRefreshInfoTreeAction(){
 if(context.get("refreshInfoTreeAction")!=null)
 return (nc.ui.hr.infoset.action.RefreshInfoTreeAction)context.get("refreshInfoTreeAction");
  nc.ui.hr.infoset.action.RefreshInfoTreeAction bean = new nc.ui.hr.infoset.action.RefreshInfoTreeAction();
  context.put("refreshInfoTreeAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setFuncNodeInitDataListener(getInitDataListener());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.SaveInfoSetAction getSaveInfoSetAction(){
 if(context.get("saveInfoSetAction")!=null)
 return (nc.ui.hr.infoset.action.SaveInfoSetAction)context.get("saveInfoSetAction");
  nc.ui.hr.infoset.action.SaveInfoSetAction bean = new nc.ui.hr.infoset.action.SaveInfoSetAction();
  context.put("saveInfoSetAction",bean);
  bean.setModel(getInfoSetModel());
  bean.setEditor(getInfoSetFormEditor());
  bean.setValidationService(getBillNotNullValidator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.CancelInfoSetAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.hr.infoset.action.CancelInfoSetAction)context.get("cancelAction");
  nc.ui.hr.infoset.action.CancelInfoSetAction bean = new nc.ui.hr.infoset.action.CancelInfoSetAction();
  context.put("cancelAction",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.SyncMetaDataAction getSyncMetaData(){
 if(context.get("syncMetaData")!=null)
 return (nc.ui.hr.infoset.action.SyncMetaDataAction)context.get("syncMetaData");
  nc.ui.hr.infoset.action.SyncMetaDataAction bean = new nc.ui.hr.infoset.action.SyncMetaDataAction();
  context.put("syncMetaData",bean);
  bean.setModel(getInfoSetModel());
  bean.setBillListView(getInfoSetListView());
  bean.setValidationService(getSyncMetaData());
  bean.setHierachicalDataAppModel(getTreeAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.SyncTempletAction getSyncTemplet(){
 if(context.get("syncTemplet")!=null)
 return (nc.ui.hr.infoset.action.SyncTempletAction)context.get("syncTemplet");
  nc.ui.hr.infoset.action.SyncTempletAction bean = new nc.ui.hr.infoset.action.SyncTempletAction();
  context.put("syncTemplet",bean);
  bean.setModel(getInfoSetModel());
  bean.setBillListView(getInfoSetListView());
  bean.setValidationService(getSyncTemplet());
  bean.setHierachicalDataAppModel(getTreeAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.SetShowOrderAction getSetShowOrder(){
 if(context.get("setShowOrder")!=null)
 return (nc.ui.hr.infoset.action.SetShowOrderAction)context.get("setShowOrder");
  nc.ui.hr.infoset.action.SetShowOrderAction bean = new nc.ui.hr.infoset.action.SetShowOrderAction();
  context.put("setShowOrder",bean);
  bean.setModel(getInfoSetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.action.SetInfoSetMapAction getSetInfoItemMap(){
 if(context.get("setInfoItemMap")!=null)
 return (nc.ui.hr.infoset.action.SetInfoSetMapAction)context.get("setInfoItemMap");
  nc.ui.hr.infoset.action.SetInfoSetMapAction bean = new nc.ui.hr.infoset.action.SetInfoSetMapAction();
  context.put("setInfoItemMap",bean);
  bean.setModel(getTreeAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getInfoSetModel());
  bean.setSaveaction(getSaveInfoSetAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_1c1e623());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1c1e623(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1c1e623")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1c1e623");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1c1e623",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_4e95c3());
  bean.setDown(getHSNode_1cb3bc8());
  bean.setDividerLocation(0.4f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_4e95c3(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#4e95c3")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#4e95c3");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#4e95c3",bean);
  bean.setComponent(getPrimaryOrgPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_1cb3bc8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#1cb3bc8")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#1cb3bc8");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#1cb3bc8",bean);
  bean.setLeft(getVSNode_1ad3d93());
  bean.setRight(getTBNode_71c72c());
  bean.setDividerLocation(0.2f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1ad3d93(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1ad3d93")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1ad3d93");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1ad3d93",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_14a3871());
  bean.setDown(getCNode_64f37b());
  bean.setDividerLocation(28f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_14a3871(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#14a3871")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#14a3871");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#14a3871",bean);
  bean.setComponent(getTreeToolBar());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_64f37b(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#64f37b")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#64f37b");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#64f37b",bean);
  bean.setName(getI18nFB_1bf8b95());
  bean.setComponent(getTreePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1bf8b95(){
 if(context.get("nc.ui.uif2.I18nFB#1bf8b95")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1bf8b95");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1bf8b95",bean);  bean.setResDir("6001infset");
  bean.setDefaultValue("信息集分类");
  bean.setResId("X6001infoset01");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1bf8b95",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_71c72c(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#71c72c")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#71c72c");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#71c72c",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getCNode_a5a4b5());  list.add(getVSNode_f3882f());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_a5a4b5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#a5a4b5")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#a5a4b5");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#a5a4b5",bean);
  bean.setComponent(getInfoSetListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_f3882f(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#f3882f")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#f3882f");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#f3882f",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_e09591());
  bean.setDown(getCNode_7b7ba8());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_e09591(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#e09591")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#e09591");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#e09591",bean);
  bean.setComponent(getEditorToolBarPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_7b7ba8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#7b7ba8")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7b7ba8");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#7b7ba8",bean);
  bean.setComponent(getInfoSetFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel(){
 if(context.get("editorToolBarPanel")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("editorToolBarPanel");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("editorToolBarPanel",bean);
  bean.setModel(getInfoSetModel());
  bean.setTitleAction(getEditorReturnAction());
  bean.setActions(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction(){
 if(context.get("editorReturnAction")!=null)
 return (nc.ui.uif2.actions.ShowMeUpAction)context.get("editorReturnAction");
  nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
  context.put("editorReturnAction",bean);
  bean.setGoComponent(getInfoSetListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.view.PrimaryOrgPanel getPrimaryOrgPanel(){
 if(context.get("primaryOrgPanel")!=null)
 return (nc.ui.hr.uif2.view.PrimaryOrgPanel)context.get("primaryOrgPanel");
  nc.ui.hr.uif2.view.PrimaryOrgPanel bean = new nc.ui.hr.uif2.view.PrimaryOrgPanel();
  context.put("primaryOrgPanel",bean);
  bean.setModel(getInfoSetModel());
  bean.setDataManager(getInfoSetDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.model.InfoSortAppModelService getTreeModelService(){
 if(context.get("treeModelService")!=null)
 return (nc.ui.hr.infoset.model.InfoSortAppModelService)context.get("treeModelService");
  nc.ui.hr.infoset.model.InfoSortAppModelService bean = new nc.ui.hr.infoset.model.InfoSortAppModelService();
  context.put("treeModelService",bean);
  bean.setBeanId("25d22505-69cf-48da-93fb-ea1dcdca1251");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.view.InfoSortTreeCreateStrategy getTreeCreateStrategy(){
 if(context.get("treeCreateStrategy")!=null)
 return (nc.ui.hr.infoset.view.InfoSortTreeCreateStrategy)context.get("treeCreateStrategy");
  nc.ui.hr.infoset.view.InfoSortTreeCreateStrategy bean = new nc.ui.hr.infoset.view.InfoSortTreeCreateStrategy();
  context.put("treeCreateStrategy",bean);
  bean.setRootName(getI18nFB_ec1211());
  bean.setFactory(getBoadatorfactory());
  bean.setClassName("nc.vo.hr.infoset.InfoSortVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_ec1211(){
 if(context.get("nc.ui.uif2.I18nFB#ec1211")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#ec1211");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#ec1211",bean);  bean.setResDir("6001infset");
  bean.setDefaultValue("信息集分类");
  bean.setResId("X6001infoset01");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#ec1211",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.hr.infoset.model.InfoSetInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.hr.infoset.model.InfoSetInitDataListener)context.get("InitDataListener");
  nc.ui.hr.infoset.model.InfoSetInitDataListener bean = new nc.ui.hr.infoset.model.InfoSetInitDataListener();
  context.put("InitDataListener",bean);
  bean.setModel(getTreeAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.HierachicalDataAppModel getTreeAppModel(){
 if(context.get("treeAppModel")!=null)
 return (nc.ui.uif2.model.HierachicalDataAppModel)context.get("treeAppModel");
  nc.ui.uif2.model.HierachicalDataAppModel bean = new nc.ui.uif2.model.HierachicalDataAppModel();
  context.put("treeAppModel",bean);
  bean.setContext(getContext());
  bean.setService(getTreeModelService());
  bean.setTreeCreateStrategy(getTreeCreateStrategy());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.view.InfoSortTreePanel getTreePanel(){
 if(context.get("treePanel")!=null)
 return (nc.ui.hr.infoset.view.InfoSortTreePanel)context.get("treePanel");
  nc.ui.hr.infoset.view.InfoSortTreePanel bean = new nc.ui.hr.infoset.view.InfoSortTreePanel();
  context.put("treePanel",bean);
  bean.setModel(getTreeAppModel());
  bean.setRootvisibleflag(true);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.beans.toolbar.ToolBarPanel getTreeToolBar(){
 if(context.get("treeToolBar")!=null)
 return (nc.ui.pub.beans.toolbar.ToolBarPanel)context.get("treeToolBar");
  nc.ui.pub.beans.toolbar.ToolBarPanel bean = new nc.ui.pub.beans.toolbar.ToolBarPanel();
  context.put("treeToolBar",bean);
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getAddInfoSortAction());  list.add(getEditInfoSortAction());  list.add(getDeleteInfoSortAction());  list.add(getRefreshInfoTreeAction());  return list;}

public nc.ui.hr.infoset.model.InfoSetTypeAndDocMediator getTypeAndDocMediator(){
 if(context.get("typeAndDocMediator")!=null)
 return (nc.ui.hr.infoset.model.InfoSetTypeAndDocMediator)context.get("typeAndDocMediator");
  nc.ui.hr.infoset.model.InfoSetTypeAndDocMediator bean = new nc.ui.hr.infoset.model.InfoSetTypeAndDocMediator();
  context.put("typeAndDocMediator",bean);
  bean.setTypeModel(getTreeAppModel());
  bean.setDocModel(getInfoSetModel());
  bean.setDocModelDataManager(getInfoSetDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.infoset.model.InfoSetDefaultValueProvider getDefaultValueProvider(){
 if(context.get("defaultValueProvider")!=null)
 return (nc.ui.hr.infoset.model.InfoSetDefaultValueProvider)context.get("defaultValueProvider");
  nc.ui.hr.infoset.model.InfoSetDefaultValueProvider bean = new nc.ui.hr.infoset.model.InfoSetDefaultValueProvider();
  context.put("defaultValueProvider",bean);
  bean.setDataManager(getInfoSetDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getHyperLinkClickMediator(){
 if(context.get("hyperLinkClickMediator")!=null)
 return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator)context.get("hyperLinkClickMediator");
  nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
  context.put("hyperLinkClickMediator",bean);
  bean.setModel(getInfoSetModel());
  bean.setShowUpComponent(getInfoSetFormEditor());
  bean.setHyperLinkColumn("infoset_name");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
