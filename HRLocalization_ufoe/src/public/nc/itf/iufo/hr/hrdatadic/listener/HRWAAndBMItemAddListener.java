/**
 * 
 */
package nc.itf.iufo.hr.hrdatadic.listener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.iufo.hr.consts.HRDataProviderConst;
import nc.vo.iufo.hr.PubEnv;
import nc.vo.iufo.hr.enums.HRStatMainBody;
import nc.vo.iufo.hr.hrstatcond.HRDataDictionaryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.item.WaItemVO;

/***************************************************************************
 * <br>
 * Created on 2013-11-25 10:30:58<br>
 * 
 * @author wangxbd
 ***************************************************************************/

public class HRWAAndBMItemAddListener implements IBusinessListener {
	private BaseDAO baseDao;

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2013-11-25 10:31:40<br>
	 * 
	 * @see nc.bs.businessevent.IBusinessListener#doAction(nc.bs.businessevent.IBusinessEvent)
	 * @author wangxbd
	 ****************************************************************************/

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if (!(event instanceof BusinessEvent)) {
			return;
		}
		BusinessEvent be = (BusinessEvent) event;
		SuperVO superVO = null;
		SuperVO[] superVOs = null; // 海外本地话使用，批量添加公共薪资项目
		if (be.getObject() instanceof WaItemVO[]) {
			superVOs = (SuperVO[]) be.getObject();
		} else {
			superVO = (SuperVO) be.getObject();
		}
		if ((superVO == null || !superVO.getAttributeValue("pk_org").equals(
				superVO.getAttributeValue("pk_group")))
				&& superVOs == null) {
			return;
		}
		if (superVOs != null) {
			this.constructVOsToSave(superVOs, be);
		} else {
			HRDataDictionaryVO dicVO = new HRDataDictionaryVO();
			String pk_parent = "";
			if (be.getSourceID().equals("WAITEM")) {
				pk_parent = HRDataProviderConst.WATABLENAME_PK;
			} else {
				pk_parent = HRDataProviderConst.BMTABLENAME_PK;
			}
			dicVO.setPk_parent(pk_parent);
			dicVO.setPk_child(superVO.getPrimaryKey());
			dicVO.setPk_group(PubEnv.getPk_group());
			dicVO.setPk_org(PubEnv.getPk_group());
			dicVO.setCreator(InvocationInfoProxy.getInstance().getUserId());
			dicVO.setCreationtime(PubEnv.getServerTime());
			dicVO.setShow_control(UFBoolean.TRUE);
			dicVO.setStatmainbody(HRStatMainBody.PERSON.getCode());
			getBaseDao().insertVO(dicVO);

		}
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public void constructVOsToSave(SuperVO[] superVOs, BusinessEvent be)
			throws DAOException {
		List<HRDataDictionaryVO> dicVOlist = new ArrayList<HRDataDictionaryVO>();
		for (SuperVO vo : superVOs) {
			HRDataDictionaryVO dicVO = new HRDataDictionaryVO();
			String pk_parent = "";
			if (be.getSourceID().equals("WAITEM")) {
				pk_parent = HRDataProviderConst.WATABLENAME_PK;
			} else {
				pk_parent = HRDataProviderConst.BMTABLENAME_PK;
			}
			dicVO.setPk_parent(pk_parent);
			dicVO.setPk_child(vo.getPrimaryKey());
			dicVO.setPk_group(PubEnv.getPk_group());
			dicVO.setPk_org(PubEnv.getPk_group());
			dicVO.setCreator(InvocationInfoProxy.getInstance().getUserId());
			dicVO.setCreationtime(PubEnv.getServerTime());
			dicVO.setShow_control(UFBoolean.TRUE);
			dicVO.setStatmainbody(HRStatMainBody.PERSON.getCode());
			dicVOlist.add(dicVO);
		}
		getBaseDao().insertVOArray(dicVOlist.toArray(new HRDataDictionaryVO[0]));
	}
}
