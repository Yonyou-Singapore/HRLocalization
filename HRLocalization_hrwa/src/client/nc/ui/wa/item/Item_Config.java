package nc.ui.wa.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Item_Config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.bd.defdoc.DefdocLoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.ui.bd.defdoc.DefdocLoginContext)context.get("context");
  nc.ui.bd.defdoc.DefdocLoginContext bean = new nc.ui.bd.defdoc.DefdocLoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.model.ItemModelService getManageModelService(){
 if(context.get("ManageModelService")!=null)
 return (nc.ui.wa.item.model.ItemModelService)context.get("ManageModelService");
  nc.ui.wa.item.model.ItemModelService bean = new nc.ui.wa.item.model.ItemModelService();
  context.put("ManageModelService",bean);
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

public nc.ui.wa.item.model.ItemAppDataModel getManageAppModel(){
 if(context.get("ManageAppModel")!=null)
 return (nc.ui.wa.item.model.ItemAppDataModel)context.get("ManageAppModel");
  nc.ui.wa.item.model.ItemAppDataModel bean = new nc.ui.wa.item.model.ItemAppDataModel();
  context.put("ManageAppModel",bean);
  bean.setService(getManageModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getListViewActions());  list.add(getCardEditorActions());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardEditorActions(){
 if(context.get("cardEditorActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("cardEditorActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());  context.put("cardEditorActions",bean);
  bean.setActions(getManagedList1());
  bean.setEditActions(getManagedList2());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getNullaction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getPrintactiongroupAction()); list.add(getLocalSeaItemFieldsActionMY());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSaveAddAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions(){
 if(context.get("listViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("listViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());  context.put("listViewActions",bean);
  bean.setActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getNullaction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getDisplayAction());  list.add(getNullaction());  list.add(getPrintGroupListAction());list.add(getLocalSeaItemFieldsActionMY());  return list;}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList4());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add("waitem");  list.add("HRA002");  list.add("pubitemp");  return list;}

public nc.ui.wa.item.model.ItemModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.wa.item.model.ItemModelDataManager)context.get("modelDataManager");
  nc.ui.wa.item.model.ItemModelDataManager bean = new nc.ui.wa.item.model.ItemModelDataManager();
  context.put("modelDataManager",bean);
  bean.setService(getManageModelService());
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
  bean.setTypeField("category_id");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.view.ItemListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.wa.item.view.ItemListView)context.get("listView");
  nc.ui.wa.item.view.ItemListView bean = new nc.ui.wa.item.view.ItemListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setMultiSelectionMode(1);
  bean.setMultiSelectionEnable(false);
  bean.setPos("head");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("waitem");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.view.ItemBillFormEditor getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.wa.item.view.ItemBillFormEditor)context.get("billFormEditor");
  nc.ui.wa.item.view.ItemBillFormEditor bean = new nc.ui.wa.item.view.ItemBillFormEditor();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("waitem");
  bean.setActions(getManagedList5());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.funcnode.ui.action.SeparatorAction getNullaction(){
 if(context.get("nullaction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullaction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullaction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.AddItemAction getAddAction(){
 if(context.get("addAction")!=null)
 return (nc.ui.wa.item.action.AddItemAction)context.get("addAction");
  nc.ui.wa.item.action.AddItemAction bean = new nc.ui.wa.item.action.AddItemAction();
  context.put("addAction",bean);
  bean.setDefaultValueProvider(getDefaultValueProvider());
  bean.setDataManager(getModelDataManager());
  bean.setModel(getManageAppModel());
  bean.setFormEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.EditItemAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.wa.item.action.EditItemAction)context.get("editAction");
  nc.ui.wa.item.action.EditItemAction bean = new nc.ui.wa.item.action.EditItemAction();
  context.put("editAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

//xml增加bean不好使，故直接在java文件中增加 add by weiningc 20181121  start
public nc.ui.wa.item.action.LocalSeaItemAction_MY getLocalSeaItemFieldsActionMY(){
	 if(context.get("localSeaItemFieldsActionMY")!=null)
	 return (nc.ui.wa.item.action.LocalSeaItemAction_MY)context.get("localSeaItemFieldsActionMY");
	 nc.ui.wa.item.action.LocalSeaItemAction_MY bean = new nc.ui.wa.item.action.LocalSeaItemAction_MY();
	 context.put("localSeaItemFieldsActionMY",bean);
	 bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
	}
//end

public nc.ui.wa.item.action.DeleteItemAction getDeleteAction(){
 if(context.get("deleteAction")!=null)
 return (nc.ui.wa.item.action.DeleteItemAction)context.get("deleteAction");
  nc.ui.wa.item.action.DeleteItemAction bean = new nc.ui.wa.item.action.DeleteItemAction();
  context.put("deleteAction",bean);
  bean.setModel(getManageAppModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.pub.action.DelExceptionHanler getExceptionHandler(){
 if(context.get("exceptionHandler")!=null)
 return (nc.ui.wa.pub.action.DelExceptionHanler)context.get("exceptionHandler");
  nc.ui.wa.pub.action.DelExceptionHanler bean = new nc.ui.wa.pub.action.DelExceptionHanler();
  context.put("exceptionHandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.RefreshItemAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.wa.item.action.RefreshItemAction)context.get("refreshAction");
  nc.ui.wa.item.action.RefreshItemAction bean = new nc.ui.wa.item.action.RefreshItemAction();
  context.put("refreshAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getInitDataListener());
  bean.setFormEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintactiongroupAction(){
 if(context.get("printactiongroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("printactiongroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("printactiongroupAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_17b2b9());
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_17b2b9(){
 if(context.get("nc.ui.uif2.I18nFB#17b2b9")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#17b2b9");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#17b2b9",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("打印");
  bean.setResId("X60130002");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#17b2b9",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList6(){  List list = new ArrayList();  list.add(getTemplatePrintAction());  list.add(getTemplatePreviewAction());  list.add(getCardOutPut());  return list;}

public nc.funcnode.ui.action.GroupAction getPrintGroupListAction(){
 if(context.get("PrintGroupListAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintGroupListAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintGroupListAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_1d6b08e());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1d6b08e(){
 if(context.get("nc.ui.uif2.I18nFB#1d6b08e")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1d6b08e");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1d6b08e",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("打印");
  bean.setResId("X60130002");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1d6b08e",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getPrintListAction());  list.add(getPreviewListAction());  list.add(getExportListAction());  return list;}

public nc.ui.hr.uif2.action.print.DirectPrintAction getPrintListAction(){
 if(context.get("PrintListAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPrintAction)context.get("PrintListAction");
  nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
  context.put("PrintListAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPreviewAction getPreviewListAction(){
 if(context.get("PreviewListAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPreviewAction)context.get("PreviewListAction");
  nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
  context.put("PreviewListAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.ExportListAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.hr.uif2.action.print.ExportListAction)context.get("exportListAction");
  nc.ui.hr.uif2.action.print.ExportListAction bean = new nc.ui.hr.uif2.action.print.ExportListAction();
  context.put("exportListAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.TemplatePrintAction getTemplatePrintAction(){
 if(context.get("TemplatePrintAction")!=null)
 return (nc.ui.hr.uif2.action.print.TemplatePrintAction)context.get("TemplatePrintAction");
  nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
  context.put("TemplatePrintAction",bean);
  bean.setModel(getManageAppModel());
  bean.setNodeKey("pubitemp");
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setDatasource(getDatasource());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.TemplatePreviewAction getTemplatePreviewAction(){
 if(context.get("TemplatePreviewAction")!=null)
 return (nc.ui.hr.uif2.action.print.TemplatePreviewAction)context.get("TemplatePreviewAction");
  nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
  context.put("TemplatePreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setNodeKey("pubitemp");
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setDatasource(getDatasource());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.ExportCardAction getCardOutPut(){
 if(context.get("cardOutPut")!=null)
 return (nc.ui.hr.uif2.action.print.ExportCardAction)context.get("cardOutPut");
  nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
  context.put("cardOutPut",bean);
  bean.setModel(getManageAppModel());
  bean.setNodeKey("pubitemp");
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setDatasource(getDatasource());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.WaPubItemDataSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.wa.item.action.WaPubItemDataSource)context.get("datasource");
  nc.ui.wa.item.action.WaPubItemDataSource bean = new nc.ui.wa.item.action.WaPubItemDataSource();
  context.put("datasource",bean);
  bean.setModel(getManageAppModel());
  bean.setSingleData(true);
  bean.setCardEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.SaveItemAction getSaveAction(){
 if(context.get("saveAction")!=null)
 return (nc.ui.wa.item.action.SaveItemAction)context.get("saveAction");
  nc.ui.wa.item.action.SaveItemAction bean = new nc.ui.wa.item.action.SaveItemAction();
  context.put("saveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setValidationService(getBillNotNullValidator());
  bean.setInterceptor(getSaveItemInterceptor_1e165a2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.item.action.SaveItemInterceptor getSaveItemInterceptor_1e165a2(){
 if(context.get("nc.ui.wa.item.action.SaveItemInterceptor#1e165a2")!=null)
 return (nc.ui.wa.item.action.SaveItemInterceptor)context.get("nc.ui.wa.item.action.SaveItemInterceptor#1e165a2");
  nc.ui.wa.item.action.SaveItemInterceptor bean = new nc.ui.wa.item.action.SaveItemInterceptor();
  context.put("nc.ui.wa.item.action.SaveItemInterceptor#1e165a2",bean);
  bean.setModel(getManageAppModel());
  bean.setContainer(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.SaveAddItemAction getSaveAddAction(){
 if(context.get("saveAddAction")!=null)
 return (nc.ui.wa.item.action.SaveAddItemAction)context.get("saveAddAction");
  nc.ui.wa.item.action.SaveAddItemAction bean = new nc.ui.wa.item.action.SaveAddItemAction();
  context.put("saveAddAction",bean);
  bean.setModel(getManageAppModel());
  bean.setAddAction(getAddAction());
  bean.setSaveAction(getSaveAction());
  bean.setInterceptor(getSaveItemInterceptor_7662f0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.item.action.SaveItemInterceptor getSaveItemInterceptor_7662f0(){
 if(context.get("nc.ui.wa.item.action.SaveItemInterceptor#7662f0")!=null)
 return (nc.ui.wa.item.action.SaveItemInterceptor)context.get("nc.ui.wa.item.action.SaveItemInterceptor#7662f0");
  nc.ui.wa.item.action.SaveItemInterceptor bean = new nc.ui.wa.item.action.SaveItemInterceptor();
  context.put("nc.ui.wa.item.action.SaveItemInterceptor#7662f0",bean);
  bean.setModel(getManageAppModel());
  bean.setContainer(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.CancelItemAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.wa.item.action.CancelItemAction)context.get("cancelAction");
  nc.ui.wa.item.action.CancelItemAction bean = new nc.ui.wa.item.action.CancelItemAction();
  context.put("cancelAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.FirstLineItemAction getFirstLineAction(){
 if(context.get("firstLineAction")!=null)
 return (nc.ui.wa.item.action.FirstLineItemAction)context.get("firstLineAction");
  nc.ui.wa.item.action.FirstLineItemAction bean = new nc.ui.wa.item.action.FirstLineItemAction();
  context.put("firstLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.PreLineItemAction getPreLineAction(){
 if(context.get("preLineAction")!=null)
 return (nc.ui.wa.item.action.PreLineItemAction)context.get("preLineAction");
  nc.ui.wa.item.action.PreLineItemAction bean = new nc.ui.wa.item.action.PreLineItemAction();
  context.put("preLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.NextLineItemAction getNextLineAction(){
 if(context.get("nextLineAction")!=null)
 return (nc.ui.wa.item.action.NextLineItemAction)context.get("nextLineAction");
  nc.ui.wa.item.action.NextLineItemAction bean = new nc.ui.wa.item.action.NextLineItemAction();
  context.put("nextLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.LastLineItemAction getLastLineAction(){
 if(context.get("lastLineAction")!=null)
 return (nc.ui.wa.item.action.LastLineItemAction)context.get("lastLineAction");
  nc.ui.wa.item.action.LastLineItemAction bean = new nc.ui.wa.item.action.LastLineItemAction();
  context.put("lastLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.DisplayOrderAction getDisplayAction(){
 if(context.get("displayAction")!=null)
 return (nc.ui.wa.item.action.DisplayOrderAction)context.get("displayAction");
  nc.ui.wa.item.action.DisplayOrderAction bean = new nc.ui.wa.item.action.DisplayOrderAction();
  context.put("displayAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveaction(getSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillNotNullValidateService)context.get("billNotNullValidator");
  nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(getBillFormEditor());  context.put("billNotNullValidator",bean);
  bean.setNextValidateService(getSaveValidationService_597cd9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.item.validator.SaveValidationService getSaveValidationService_597cd9(){
 if(context.get("nc.ui.wa.item.validator.SaveValidationService#597cd9")!=null)
 return (nc.ui.wa.item.validator.SaveValidationService)context.get("nc.ui.wa.item.validator.SaveValidationService#597cd9");
  nc.ui.wa.item.validator.SaveValidationService bean = new nc.ui.wa.item.validator.SaveValidationService();
  context.put("nc.ui.wa.item.validator.SaveValidationService#597cd9",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategy(){
 if(context.get("treeCreateStrategy")!=null)
 return (nc.vo.bd.meta.BDObjectTreeCreateStrategy)context.get("treeCreateStrategy");
  nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
  context.put("treeCreateStrategy",bean);
  bean.setFactory(getBoadatorfactory());
  bean.setRootName(getI18nFB_113c849());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_113c849(){
 if(context.get("nc.ui.uif2.I18nFB#113c849")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#113c849");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#113c849",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("项目分类");
  bean.setResId("X60130003");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#113c849",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.bd.defdoc.DefdocAppService getTreeModelService(){
 if(context.get("treeModelService")!=null)
 return (nc.ui.bd.defdoc.DefdocAppService)context.get("treeModelService");
  nc.ui.bd.defdoc.DefdocAppService bean = new nc.ui.bd.defdoc.DefdocAppService();
  context.put("treeModelService",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.model.ItemDefdocSingleNodeDataManager getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.wa.item.model.ItemDefdocSingleNodeDataManager)context.get("InitDataListener");
  nc.ui.wa.item.model.ItemDefdocSingleNodeDataManager bean = new nc.ui.wa.item.model.ItemDefdocSingleNodeDataManager();
  context.put("InitDataListener",bean);
  bean.setModel(getHAppModel());
  bean.setContext(getContext());
  bean.setService(getTreeModelService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.model.WaItemHierachicalDataAppModel getHAppModel(){
 if(context.get("HAppModel")!=null)
 return (nc.ui.wa.item.model.WaItemHierachicalDataAppModel)context.get("HAppModel");
  nc.ui.wa.item.model.WaItemHierachicalDataAppModel bean = new nc.ui.wa.item.model.WaItemHierachicalDataAppModel();
  context.put("HAppModel",bean);
  bean.setService(getTreeModelService());
  bean.setTreeCreateStrategy(getTreeCreateStrategy());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.view.HrTreePanel getTreePanel(){
 if(context.get("treePanel")!=null)
 return (nc.ui.hr.uif2.view.HrTreePanel)context.get("treePanel");
  nc.ui.hr.uif2.view.HrTreePanel bean = new nc.ui.hr.uif2.view.HrTreePanel();
  context.put("treePanel",bean);
  bean.setModel(getHAppModel());
  bean.setRootvisibleflag(true);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.mediator.TypeAndDocMediator getTypeAndDocMediator(){
 if(context.get("typeAndDocMediator")!=null)
 return (nc.ui.hr.uif2.mediator.TypeAndDocMediator)context.get("typeAndDocMediator");
  nc.ui.hr.uif2.mediator.TypeAndDocMediator bean = new nc.ui.hr.uif2.mediator.TypeAndDocMediator();
  context.put("typeAndDocMediator",bean);
  bean.setTypeModel(getHAppModel());
  bean.setDocModel(getManageAppModel());
  bean.setDocModelDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.model.ItemDefaultValueProvider getDefaultValueProvider(){
 if(context.get("defaultValueProvider")!=null)
 return (nc.ui.wa.item.model.ItemDefaultValueProvider)context.get("defaultValueProvider");
  nc.ui.wa.item.model.ItemDefaultValueProvider bean = new nc.ui.wa.item.model.ItemDefaultValueProvider();
  context.put("defaultValueProvider",bean);
  bean.setModelDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.AddItemCateg getTypeAddAction(){
 if(context.get("typeAddAction")!=null)
 return (nc.ui.wa.item.action.AddItemCateg)context.get("typeAddAction");
  nc.ui.wa.item.action.AddItemCateg bean = new nc.ui.wa.item.action.AddItemCateg();
  context.put("typeAddAction",bean);
  bean.setModel(getHAppModel());
  bean.setDefaultValueProvider(getTypeDefaultValueProvider());
  bean.setFormEditor(getTypeFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.EditItemCategAction getTypeEditAction(){
 if(context.get("typeEditAction")!=null)
 return (nc.ui.wa.item.action.EditItemCategAction)context.get("typeEditAction");
  nc.ui.wa.item.action.EditItemCategAction bean = new nc.ui.wa.item.action.EditItemCategAction();
  context.put("typeEditAction",bean);
  bean.setModel(getHAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.DeleteItemCategAction getTypeDeteteAction(){
 if(context.get("typeDeteteAction")!=null)
 return (nc.ui.wa.item.action.DeleteItemCategAction)context.get("typeDeteteAction");
  nc.ui.wa.item.action.DeleteItemCategAction bean = new nc.ui.wa.item.action.DeleteItemCategAction();
  context.put("typeDeteteAction",bean);
  bean.setModel(getHAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.RefreshAction getTypeRefreshAction(){
 if(context.get("typeRefreshAction")!=null)
 return (nc.ui.uif2.actions.RefreshAction)context.get("typeRefreshAction");
  nc.ui.uif2.actions.RefreshAction bean = new nc.ui.uif2.actions.RefreshAction();
  context.put("typeRefreshAction",bean);
  bean.setModel(getHAppModel());
  bean.setDataManager(getInitDataListener());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.ItemCategSaveAction getTypeSaveAction(){
 if(context.get("typeSaveAction")!=null)
 return (nc.ui.wa.item.action.ItemCategSaveAction)context.get("typeSaveAction");
  nc.ui.wa.item.action.ItemCategSaveAction bean = new nc.ui.wa.item.action.ItemCategSaveAction();
  context.put("typeSaveAction",bean);
  bean.setModel(getHAppModel());
  bean.setEditor(getTypeFormEditor());
  bean.setValidationService(getTypeNotNullValidator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.ItemCategSaveAddAction getTypeSaveAddAction(){
 if(context.get("typeSaveAddAction")!=null)
 return (nc.ui.wa.item.action.ItemCategSaveAddAction)context.get("typeSaveAddAction");
  nc.ui.wa.item.action.ItemCategSaveAddAction bean = new nc.ui.wa.item.action.ItemCategSaveAddAction();
  context.put("typeSaveAddAction",bean);
  bean.setModel(getHAppModel());
  bean.setAddAction(getTypeAddAction());
  bean.setSaveAction(getTypeSaveAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.action.ItemCategCancelAction getTypeCancelAction(){
 if(context.get("typeCancelAction")!=null)
 return (nc.ui.wa.item.action.ItemCategCancelAction)context.get("typeCancelAction");
  nc.ui.wa.item.action.ItemCategCancelAction bean = new nc.ui.wa.item.action.ItemCategCancelAction();
  context.put("typeCancelAction",bean);
  bean.setModel(getHAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.validator.BillNotNullValidateService getTypeNotNullValidator(){
 if(context.get("typeNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillNotNullValidateService)context.get("typeNotNullValidator");
  nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(getTypeFormEditor());  context.put("typeNotNullValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.item.view.ItemTypeFormEditor getTypeFormEditor(){
 if(context.get("TypeFormEditor")!=null)
 return (nc.ui.wa.item.view.ItemTypeFormEditor)context.get("TypeFormEditor");
  nc.ui.wa.item.view.ItemTypeFormEditor bean = new nc.ui.wa.item.view.ItemTypeFormEditor();
  context.put("TypeFormEditor",bean);
  bean.setModel(getHAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("HRA002");
  bean.setName(getI18nFB_1c24eec());
  bean.setActions(getManagedList8());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1c24eec(){
 if(context.get("nc.ui.uif2.I18nFB#1c24eec")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1c24eec");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1c24eec",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("薪资项目分类");
  bean.setResId("X60130004");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1c24eec",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList8(){  List list = new ArrayList();  list.add(getTypeSaveAction());  list.add(getTypeSaveAddAction());  list.add(getTypeCancelAction());  return list;}

public nc.ui.wa.item.model.TypeDefaultValueProvider getTypeDefaultValueProvider(){
 if(context.get("typeDefaultValueProvider")!=null)
 return (nc.ui.wa.item.model.TypeDefaultValueProvider)context.get("typeDefaultValueProvider");
  nc.ui.wa.item.model.TypeDefaultValueProvider bean = new nc.ui.wa.item.model.TypeDefaultValueProvider();
  context.put("typeDefaultValueProvider",bean);
  bean.setModel(getHAppModel());
  bean.setDefdocListCode("HRWA002_0xx");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.controller.TypeFormEditorController getTypeEditorController_Mediator(){
 if(context.get("typeEditorController_Mediator")!=null)
 return (nc.ui.hr.uif2.controller.TypeFormEditorController)context.get("typeEditorController_Mediator");
  nc.ui.hr.uif2.controller.TypeFormEditorController bean = new nc.ui.hr.uif2.controller.TypeFormEditorController();
  context.put("typeEditorController_Mediator",bean);
  bean.setModel(getHAppModel());
  bean.setContentPanel(getTypeFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction(){
 if(context.get("editorReturnAction")!=null)
 return (nc.ui.uif2.actions.ShowMeUpAction)context.get("editorReturnAction");
  nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
  context.put("editorReturnAction",bean);
  bean.setGoComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel(){
 if(context.get("editorToolBarPanel")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("editorToolBarPanel");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("editorToolBarPanel",bean);
  bean.setModel(getManageAppModel());
  bean.setTitleAction(getEditorReturnAction());
  bean.setActions(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.ui.wa.item.view.WaToolBarPanel getTypeToolbar(){
 if(context.get("typeToolbar")!=null)
 return (nc.ui.wa.item.view.WaToolBarPanel)context.get("typeToolbar");
  nc.ui.wa.item.view.WaToolBarPanel bean = new nc.ui.wa.item.view.WaToolBarPanel();
  context.put("typeToolbar",bean);
  bean.setModel(getHAppModel());
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getTypeAddAction());  list.add(getTypeEditAction());  list.add(getTypeDeteteAction());  return list;}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getTBNode_8e2685());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_8e2685(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#8e2685")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#8e2685");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#8e2685",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add(getVSNode_1134a00());  list.add(getVSNode_9843d8());  return list;}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1134a00(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1134a00")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1134a00");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1134a00",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_1cb7009());
  bean.setDown(getHSNode_1fdf9ae());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1cb7009(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1cb7009")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1cb7009");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1cb7009",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_1fdf9ae(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#1fdf9ae")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#1fdf9ae");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#1fdf9ae",bean);
  bean.setLeft(getVSNode_1aa58bd());
  bean.setRight(getCNode_a820bf());
  bean.setDividerLocation(0.2f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1aa58bd(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1aa58bd")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1aa58bd");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1aa58bd",bean);
  bean.setUp(getCNode_1286e27());
  bean.setDown(getCNode_157ce51());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1286e27(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1286e27")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1286e27");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1286e27",bean);
  bean.setComponent(getTypeToolbar());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_157ce51(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#157ce51")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#157ce51");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#157ce51",bean);
  bean.setComponent(getTreePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_a820bf(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#a820bf")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#a820bf");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#a820bf",bean);
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_9843d8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#9843d8")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#9843d8");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#9843d8",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_15f5a0a());
  bean.setDown(getCNode_110797e());
  bean.setDividerLocation(26f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_15f5a0a(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#15f5a0a")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#15f5a0a");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#15f5a0a",bean);
  bean.setComponent(getEditorToolBarPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_110797e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#110797e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#110797e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#110797e",bean);
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.view.PrimaryOrgPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.hr.uif2.view.PrimaryOrgPanel)context.get("orgpanel");
  nc.ui.hr.uif2.view.PrimaryOrgPanel bean = new nc.ui.hr.uif2.view.PrimaryOrgPanel();
  context.put("orgpanel",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getInitDataListener());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator(){
 if(context.get("mouseClickShowPanelMediator")!=null)
 return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator)context.get("mouseClickShowPanelMediator");
  nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
  context.put("mouseClickShowPanelMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setShowUpComponent(getBillFormEditor());
  bean.setHyperLinkColumn("code");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
