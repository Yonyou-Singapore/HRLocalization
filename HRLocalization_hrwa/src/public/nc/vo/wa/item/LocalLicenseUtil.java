package nc.vo.wa.item;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import nc.bs.logging.Logger;
import nc.lic.model.LicData;
import nc.lic.model.LicKey;
import nc.lic.model.License;
import nc.lic.model.LicenseContainer;
import nc.lic.toolkit.LicToolKit;
import nc.vo.pub.BusinessException;

/**
 * 
 * @author weiningc
 *
 */
public class LocalLicenseUtil {
	/**
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	public static Boolean checkLocalLicense(String country) throws BusinessException {
		//当前服务器的mac address
		LicKey localKey = new LicenseContainer().getLocalKey();
		//获取license地址
		File licensefile = getLocalLicenseAddress(country);
		//parse whether have permission
		License[] license = LicToolKit.loadLicensefromFile(licensefile);
		if(license == null || license.length == 0) {
			throw new BusinessException("parse license file failed, please contact yonyou consultant.");
		}
		if(!localKey.getHardInfo().equals(license[0].getLicRequest().getLicKey().getHardInfo())) {
			throw new BusinessException("The license not match with the server, please contact yonyou consultant");
		}
		LicData licData = license[0].getLicData(country);
		if(licData == null) {
			throw new BusinessException("No permission for this country: " + country);
		}
		Date expirdate = new Date(licData.getExpireDate());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = format.format(expirdate);
		if(new Date().getTime() > licData.getExpireDate()) {
			throw new BusinessException("The registeration date is: " + datestr + " ,The license expired, please contact yonyou consultant");
		}
		return true;
	}
	
	public static File getLocalLicenseAddress(String country) throws BusinessException {
		String licensepath = LicToolKit.getNcHome() + "\\bin\\license-" + country+ ".resp";
		File file = new File(licensepath);
		Logger.info("License filepath:" + licensepath);
		if(!file.exists()) {
			throw new BusinessException(country + " not register, please contact yonyou consultant.");
		}
		return file;
	}
}
