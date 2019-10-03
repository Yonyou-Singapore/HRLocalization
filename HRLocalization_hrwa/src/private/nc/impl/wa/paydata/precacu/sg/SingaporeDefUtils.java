package nc.impl.wa.paydata.precacu.sg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nc.bs.dao.BaseDAO;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.defdoc.DefdoclistVO;
import nc.vo.pubapp.pattern.pub.SqlBuilder;

/**
 * 
 * @author weiningc
 *
 */
public class SingaporeDefUtils {
	private BaseDAO dao = null;
	public SingaporeDefUtils() {
		if(dao == null) {
			dao = new BaseDAO();
		}
	}
	public static Map<String, String> queryDefForSingapore() {
		Map<String, String> singaporedefmap = new HashMap<String, String>();

		//先查bd_defdoclist拿到pk_defdoclist
		SqlBuilder sb = new SqlBuilder();
		sb.append("code", new String[] {"SEALOCAL010"}); //ID-Type-Singapore
		sb.append(" and dr=0 and enablestate = 2");
		try{
			NCObject[] deflistvos = MDPersistenceService.lookupPersistenceQueryService().
					queryBillOfNCObjectByCond(DefdoclistVO.class, sb.toString(), false);
			List<String> deflistpks = new ArrayList<String>();
			if(deflistvos != null && deflistvos.length >0 
					&& deflistvos[0].getContainmentObject() instanceof DefdoclistVO) {
				for(NCObject deflist : deflistvos) {
					deflistpks.add(((DefdoclistVO)deflist.getContainmentObject()).getPk_defdoclist());
				}
			}
			sb = new SqlBuilder();
			sb.append("pk_defdoclist", deflistpks.toArray(new String[0]));
			sb.append(" and dr = 0");
//			NCObject[] defvos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(DefdocVO.class, sb.toString(), false);
			//使用BaseDAO查询自定义项
			Collection<DefdocVO> defvos = new BaseDAO().retrieveByClause(DefdocVO.class, sb.toString()	);
			if(defvos != null && defvos.size() >0) {
				for(DefdocVO defvo : defvos) {
					singaporedefmap.put(defvo.getPk_defdoc(), defvo.getCode());
				}
			}
			} catch(Exception e) {
				ExceptionUtils.wrapException(e.getMessage(), e);
		}

		return singaporedefmap;
	}
}
