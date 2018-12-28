package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.ValidationException;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IHiActionCode;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.pub.tools.VOUtils;
import nc.ui.hi.psndoc.view.ImportEmployeePanel;
import nc.ui.hi.psndoc.view.ImportPhotoPanel;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class ImportEmployeeAction extends HrAction {

	private static final long serialVersionUID = 1L;
	
	public ImportEmployeeAction() {
		super();
		setBtnName(ResHelper.getString("hrhi_localimport", "hrhi_localimport001")/* @res "������Ա��Ϣ" */);
		setCode(IActionCode.IMPORT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ImportEmployeePanel employeePanel = new ImportEmployeePanel();
		
		if(employeePanel.getChooser().showDialog(null, ResHelper.getString("hrhi_localimport", "hrhi_localimport002")/* @res "�����ļ�" */)
				== UIFileChooser.CANCEL_OPTION
				){
			return;
		}
		File file = employeePanel.getChooser().getSelectedFile();
		
		if (file == null)
		{
			putValue(MESSAGE_AFTER_ACTION, ResHelper.getString("6001uif2", "06001uif20002") /* @res "��ȡ����" */);
			return;
		}
		String path = file.getPath();
		Workbook workbook = this.openWorkbook(path);
		
		String[] allNames = file.list();
		Map<String, File> psnPhotoFileMap = new HashMap<String, File>();
		
		psnPhotoFileMap.remove("key");
		
		
		//У�� ����Աά��Ȩ�޵���Ա���ܵ�����Ƭ add by yanglt 20140807
		checkDataPermission(psnPhotoFileMap);
		
		//��װPsndocVO����Ƭ��Ϣ
		PsndocVO[] psndocVOs = assemblePsndocVO(psnPhotoFileMap);
		
		//ÿ�����̨�ύ�������
		int everyTimeSubmit = 100;
		
		int sum = 0;
		//����
		if (!ArrayUtils.isEmpty(psndocVOs))
		{
			savePsndocVO(psndocVOs, everyTimeSubmit);
			sum = psndocVOs.length;
		}
	
		showResultHint(sum, psnPhotoFileMap);
	}
	
	/**
	 * ��ʾ����Ľ��
	 * @param sum
	 * @param psnPhotoFileMap
	 * @author heqiaoa
	 *  
	 */
	@SuppressWarnings("restriction")
	private void showResultHint(int sum, Map<String, File> psnPhotoFileMap ){
		StringBuilder codesHint = new StringBuilder();
		
		if(null!=psnPhotoFileMap && psnPhotoFileMap.keySet()!= null){
			for(String code : psnPhotoFileMap.keySet()){
				codesHint.append( code + ','); 
			}
			if(codesHint.length()>1){
				codesHint.setLength(codesHint.length() - 1);
			}
		}
		//FIXME ����Ԥ�����˴���ʱû�Ӷ���֧��
		String hintTitleStr  = (0!=sum ? "�ɹ�����: " + sum + " ��" : "����ʧ��");
		String hintDetailStr = (0!=sum ? "��Ա��������Ϊ��" + codesHint.toString(): 
			"û���ҵ��ܵ�����ļ����ļ����е�ͼƬ�ļ���Ӧ��Ϊ����Ա����+�ļ�����׺��ʽ��������123.PNG");
		if(sum > 0){
			MessageDialog.showHintDlg(this.getEntranceUI(), hintTitleStr, hintDetailStr);
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		} else {
			MessageDialog.showErrorDlg(this.getEntranceUI(), hintTitleStr, hintDetailStr);
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, hintTitleStr);
		}
	}
	
	//У�� ����Աά��Ȩ�޵���Ա���ܵ�����Ƭ add by yanglt 20140807
	protected void checkDataPermission(Map<String, File> psnPhotoFileMap) throws BusinessException
    {
        initDataPermission();
        if (!HrDataPermHelper.isNeedtoCheck(getResourceCode(), getMdOperateCode(), getOperateCode()))
        {
            return;
        }
        
        // ������Ƭԭ���߼��ǲ���������Դ��֯�������˵�Ȩ�ޣ�����������̫�󣬵����ڴ�������޷�������Ƭ
        // �����޸�Ϊ������Ƭ�����Ӧ����Ա��У���Ƿ���Ȩ��
        // add by zhangqian 2015-8-18
        InSQLCreator isc = new InSQLCreator();
        String strInSql = isc.getInSQL(psnPhotoFileMap.keySet().toArray(new String[0]));
        String psndocCodeInSql = " and code in(" + strInSql + ")";
		
		String condition = "bd_psndoc.pk_psndoc in (select pk_psndoc from hi_psnjob where pk_hrorg = '"
				+ getModel().getContext().getPk_org() + "'" + psndocCodeInSql + ")";
		// ֧�ֶ���ְ��Ա��Ƭ�ĵ���  heqiaoa 20150415
		// + "' and endflag = 'N' and lastflag = 'Y')";
    		
        List<String> fieldList = new ArrayList<String>();
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_PSNDOC);
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_GROUP);
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_HRORG);
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_ORG);
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.CODE);
        fieldList.add(PsndocVO.getDefaultTableName() + "." + PsndocVO.NAME);
        Object[] objData = NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsndocVOByCondition(null, 
        		fieldList, null, condition, null);
        if (ArrayUtils.isEmpty(objData)) 
        {
            return;
        }
        ValidationException ex = HrDataPermHelper.checkDataPermission(getResourceCode(), getMdOperateCode(), getOperateCode(), objData, getContext());
        //ȥ��û��ά��Ȩ�޵���Ա
        if (ex != null && ex.getFailureMessage() != null)
        {
            List<String> psnCodeList = ex.getFailureMessage();
            for (int i=0; i<psnCodeList.size(); i++)
            {
                String psncode = psnCodeList.get(i).split(",")[0].substring(1);
                if (psnPhotoFileMap.containsKey(psncode))
                {
                    psnPhotoFileMap.remove(psncode);
                }
            }
        }
        
        //ȥ�����ڹ���Χ�ڵ���Ա
        List<String> psncodeList = new ArrayList<String>();
        for (int i=0; i<objData.length; i++)
        {
            IBDObject bdObject = new BDObjectAdpaterFactory().createBDObject(objData[i]);
            psncodeList.add(String.valueOf(bdObject.getCode()));
        }
        
        List<String> removePsncode = new ArrayList<String>();
        for (Object key : psnPhotoFileMap.keySet())
        {
            if (!psncodeList.contains(key))
            {
                removePsncode.add(String.valueOf(key));
            }
        }
        
        for (int i=0; i<removePsncode.size(); i++)
        {
            psnPhotoFileMap.remove(removePsncode.get(i));
        }
    }
	
	
	private void savePsndocVO(PsndocVO[] psndocVOs, int everyTimeSubmit) throws BusinessException 
	{
		Map<Integer, List<PsndocVO>> psndocVOMap = new HashMap<Integer, List<PsndocVO>>();
		int j = 0;
		List<PsndocVO> psnList = null;
		for (int i=0; i<psndocVOs.length; i++)
		{
			if (i/everyTimeSubmit == j) 
			{
				psnList = new ArrayList<PsndocVO>();
				psndocVOMap.put(j, psnList);
				j++;
			}
			psnList.add(psndocVOs[i]);
		}
		
		//ÿeveryTimeSubmit���ύһ�Ρ�
		IPersistenceUpdate iperupdate = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
		for (Iterator<Entry<Integer, List<PsndocVO>>> ite = psndocVOMap.entrySet().iterator(); ite.hasNext();) 
		{
			Entry<Integer, List<PsndocVO>> entry = ite.next();
			List<PsndocVO> psndocVOList = entry.getValue();
			iperupdate.updateVOArray(null, psndocVOList.toArray(new PsndocVO[0]), null, null);
		}
	}
	
	/**
	 * ��װPsndocVO����Ƭ��Ϣ
	 * @param psnPhotoFileMap
	 * @return
	 * @throws BusinessException
	 */
	private PsndocVO[] assemblePsndocVO(Map<String, File> psnPhotoFileMap) throws BusinessException 
	{
		if (psnPhotoFileMap.isEmpty()) 
		{
			return null;
		}
	
		List<String> psnCodeList = new ArrayList<String>();
		for (Iterator<String> ite = psnPhotoFileMap.keySet().iterator(); ite.hasNext();) 
		{
			String key = ite.next();
			psnCodeList.add(key);
		}
		
		PsndocVO[] psndocVOs = NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsndocVOByCondition(psnCodeList.toArray(new String[0]));
		if (ArrayUtils.isEmpty(psndocVOs))
		{
		    return null;
		}
		
		for (int i=0; i<psndocVOs.length; i++) 
		{
			File photoFile = psnPhotoFileMap.get(psndocVOs[i].getCode());
			//ת��Ϊ���ļ�
			FileInputStream ins = null;
			try 
			{
				ins = new FileInputStream(photoFile);
				byte[] bytes = inputStreamToByte(ins);
				if (bytes.length >= 204800)
				{
				    throw new BusinessException("��Ա����:" + psndocVOs[i].getCode()+" ����Ƭ���ܴ���200KB");
				}
				psndocVOs[i].setPhoto(bytes);
				
				//��������ͼ
				byte[] imgData = VOUtils.transPreviewPhoto(bytes);
				psndocVOs[i].setPreviewphoto(imgData);
			}
			catch(BusinessException e)
			{
			    throw new BusinessException("��Ա����:" + psndocVOs[i].getCode()+" ����Ƭ���ܴ���200KB");
			}
			catch(Exception e)
			{
				Logger.error(e.getMessage(), e);
			}
			finally 
			{
				// IOUtil.close(ins);
				if (ins != null) 
				{
					try 
					{
						ins.close();
					} 
					catch (IOException e) 
					{
						Logger.error(e.getMessage(), e);
					}
				}
			}
			
			psndocVOs[i].setStatus(VOStatus.UPDATED);
		}
		
		return psndocVOs;
	}
	
	public byte[] inputStreamToByte(FileInputStream fileStream) throws IOException 
	{
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = fileStream.read()) != -1) 
		{
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}


	@Override
	protected boolean isActionEnable() 
	{
		return super.isActionEnable();
	}
	
	/***************************************************************************
     * <br>
     * Created on 2014-8-26 20:31:31<br>
     * @return Workbook
     * @author Rocex Wang
	 * @param path 
     ***************************************************************************/
    protected Workbook openWorkbook(String path)
    {
        Workbook workbook = null;
        
        InputStream is = null;
        try
        {
            is = new FileInputStream(path);
            
            workbook = Workbook.getWorkbook(is);
        }
        catch (Exception ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
        return workbook;
    }

}
