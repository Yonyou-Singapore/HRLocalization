package nc.ui.hi.psndoc.view;

import java.awt.BorderLayout;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import nc.ui.org.orgview.action.DefaultFileFilter;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;

public class ImportEmployeePanel extends UIPanel {

	private static final long serialVersionUID = 1L;

	private UIFileChooser chooser;

	public ImportEmployeePanel() {
		super();
	}
	
	public void initUI() {
		setLayout(new BorderLayout());
	}
	
	public UIFileChooser getChooser() {
		if (chooser == null)
        {
            chooser = new UIFileChooser();
            chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter defaultFilter = new DefaultFileFilter(".xls", "XLS " + "filename" + " (.xls)");
    		Object[] imageFormats = ImageIO.getReaderFormatNames();
    		HashSet<String> formats = new HashSet<String>();
    		for (int i = 0; i < imageFormats.length; i++) {
    			String ext = imageFormats[i].toString().toLowerCase();
    			formats.add(ext);
    		}
    		imageFormats = formats.toArray();
    		for (int i = 0; i < imageFormats.length; i++) {
    			String ext = imageFormats[i].toString();
    			if (ext.equalsIgnoreCase("wbmp") || ext.equalsIgnoreCase("png"))
    				continue;
    			chooser.addChoosableFileFilter(new DefaultFileFilter("." + ext, ext.toUpperCase() + " " + "filename" + " (." + ext + ")"));
    		}
            chooser.setFileFilter(defaultFilter);
        }
        return chooser;
	}

	public void setChooser(UIFileChooser chooser) {
		this.chooser = chooser;
	}

}
