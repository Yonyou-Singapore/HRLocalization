;window.lfwtop=true;
function RepToolListComp(){
	this.autoQuery = false;
};

RepToolListComp.prototype.createSelf = function()
{
	if(window.IS_PAD) return;
	if(window.location.href.indexOf("hideToolistMenu=1") > -1) return;
	
	var olddiv = document.getElementById("toolDiv") ;
	if(olddiv) return ;
	
	var tool = this ;
	this.toolDiv = $ce("DIV");
	this.toolDiv.id = "toolDiv" ;
	this.toolDiv.className = "tool_div_normal" ;
	this.toolDiv.onclick=function(e)
	{
		e = e||event;
		if(this.className.indexOf("tool_div_show") > 0)
		{
			if(this.menu)
			{
				this.menu.hide();
			}
		}
		else
		{
			$("#toolDiv").addClass("tool_div_show");
			tool.showToolMenu(e);
		}
	} ;

	// 注册外部回掉函数
	window.clickHolders.push(this.toolDiv);
	this.toolDiv.outsideClick = function(e) {
		if (e != null && 2 == e.button){  // 鼠标右键
			if(tool.menu){
				tool.menu.hide() ;
			}
		}
		tool.hiddenList();	
	};
	
	this.mainDiv = $ce("DIV");
	this.mainDiv.appendChild(this.toolDiv) ;
	
	var parentDiv = document.getElementById("$d_flowv");
	parentDiv.appendChild(this.mainDiv);
	
	this.refurbishtoolDiv() ;
};
RepToolListComp.prototype.showToolMenu = function(e)
{
  	 if(this.menu){
  		this.menu.destroySelf();
  	 }
     this.menu = new ContextMenuComp("tool_list_menu", 0, 0, false);
     this.createToolMenuItem() ;
     this.menu.show(e);
     stopEvent(e);
     clearEventSimply(e);
};
RepToolListComp.prototype.createToolMenuItem = function()
{
	var tool = this ;
	var path = "/portal/sync/bqwebrtpub/base/html/nodes/ufofr/themes/images/bap/portal/toollist/" ;
	
	var func = function(){tool.hiddenList();showDefaultLoadingBar();RepToolListCompEvent.refresh();} ;
	this.createMenuItem("refresh", trans("ml_cells_refresh")/**刷新*/,func,path+"refresh.png") ;
	
	var href=window.location.href;
	var self = this;
	if(href.indexOf("isHiddenConditionBox=1") > -1)
	{
		var func = function(){
		self.autoQuery = !(self.autoQuery);
		tool.hiddenList();showDefaultLoadingBar();RepToolListCompEvent.autoQuery();} ;
		if(!self.autoQuery)
		{
			this.createMenuItem("autoQuery", trans("ml_cells_auto_query")/**自动查询*/,func,path+"checkbox_unselect.png") ;
		}
		else
		{
			this.createMenuItem("autoQuery", trans("ml_cells_auto_query")/**自动查询*/,func,path+"checkbox_select.png") ;
		}
	}
	var func = function(){tool.hiddenList();tool.outputFile('EXCEL');} ;
	this.createMenuItem("exportExcel", trans("ml_cells_expExcel")/**导出EXCEL*/,func,path+"xls.gif") ;
	
	var func = function(){tool.hiddenList();tool.outputFile('PDF');} ;
	this.createMenuItem("exportPdf", trans("ml_cells_expPdf")/**导出PDF*/,func,path+"pdf.gif",true) ;
	
	this.createMenuItem("expUrl",trans("ml_cells_expUrl")/**导出URL*/,RepToolListCompEvent.expURL,path+"getUrl.png");
	//create pcb
	var func = function(){tool.hiddenList();tool.outputFile('PCB');} ;
	this.createMenuItem("expPcbPdf", "Export PCB", func, path+"pdf.gif",true);
	//create eaform
	var func = function(){tool.hiddenList();tool.outputFile('EAFORM');} ;
	this.createMenuItem("expEaFormPdf", "Export EA Form", func, path+"pdf.gif",true);
	this.createFullScreenMenu();
	this.createHideDivItem(tool,path) ;
};

RepToolListComp.prototype.createFullScreenMenu=function(){
	var path = "/portal/sync/bqwebrtpub/base/html/nodes/ufofr/themes/images/bap/portal/toollist/" ;
	if(window==top  &&( window.isFullScreen || ( window.outerHeigth==screen.heigth && window.outerWidth==screen.width) )){
		this.createMenuItem("fullScreenView",trans("ml_cells_exit_fullscreen_view")/**退出全屏*/,RepToolListComp.closeFullScreenView,path+"fullscreen.gif",true);
	}else if(window!=top){
		this.createMenuItem("fullScreenView",trans("ml_cells_fullscreen_view")/**全屏预览*/,RepToolListComp.fullScreenView,path+"fullscreen.gif",true);
	}
};
RepToolListComp.fullScreenView=function(){
	var win=window.open(window.location.href,trans("ml_cells_fullscreen_view")/**全屏预览*/,
	"target=_blank,toobar=no,resizable=no,scrollbars=no,location=no,status=no,fullscreen=yes,width="+screen.width+",height="+screen.height);
	win.resizeTo(screen.width,screen.height);
	win.moveTo(0,0);
	win.isFullScreen=true;
};
RepToolListComp.closeFullScreenView=function(){
	window.close();
};
RepToolListComp.prototype.createHideDivItem = function(tool,path){
	if(this.topDivIsExist()){
		var func = function(){tool.hiddenList();tool.displayTopDiv();tool.refurbishtoolDiv();} ;
		if(this.topDivIsVisible()){
			this.createMenuItem("topdiv", trans("ml_cells_hiddenTopPanel")/**隐藏顶部面板*/,func,path+"top_hide.png") ;
		}else{
			this.createMenuItem("topdiv", trans("ml_cells_showTopPanel")/**显示顶部面板*/,func,path+"top_show.png") ;
		}
	}
	if(this.leftDivIsExist()){
		var func = function(){tool.hiddenList();tool.displayLeftDiv();tool.refurbishtoolDiv();} ;
		if(this.leftDivIsVisible()){
			this.createMenuItem("leftdiv", trans("ml_cells_hiddenLeftPanel")/**隐藏左侧面板*/,func,path+"left_hide.png",false) ;
		}else{
			this.createMenuItem("leftdiv", trans("ml_cells_showLeftPanel")/**显示左侧面板*/,func,path+"left_show.png",false) ;
		}
	}
};
RepToolListComp.prototype.createMenuItem = function(id,name,func,img,isStep){
	var item = this.menu.addMenu(id, name,null,img, false,false,{showModel:"0"});
	var mouseListener = new Listener("onclick");
	mouseListener.func = func ;
	item.addListener(mouseListener);
	if(isStep){
		this.menu.addSep();
	}
};
RepToolListComp.prototype.hiddenList = function(){
	var toolDiv = $("#toolDiv");
	toolDiv.removeClass("tool_div_show");
	var listDiv = document.getElementById("tool_list"); 
	if(listDiv){
		listDiv.style.visibility = "hidden";
		document.body.removeChild(listDiv) ;
	}
};
RepToolListComp.showLoading = function(){
	if(!RepToolListComp.loadingBar){
		RepToolListComp.loadingBar = new LoadingBarComp(document.body, "toolList_loadingBar", 0, 0, null, null, null, "center", "center", 100000);
//		RepToolListComp.loadingBar.setInnerHTML("正在生成文件，请等待...") ;
	}
	RepToolListComp.loadingBar.show() ;
};
RepToolListComp.hideLoading = function(){
	if(RepToolListComp.loadingBar){
		RepToolListComp.loadingBar.hide() ;
	}
};
RepToolListComp.prototype.getTopDiv = function(){
	var div = document.getElementById("$d_content_layouttop_Panel") ;
	return div;
};
RepToolListComp.prototype.getBelowDiv = function(){
	var div = document.getElementById("$d_content_layoutcenter_panel") ;
	return div;
};
RepToolListComp.prototype.getLeftDiv = function(){
	var div = document.getElementById("$d_flowvleft_Panel") ;
	return div;
};
RepToolListComp.prototype.getRightDiv = function(){
	var div = document.getElementById("$d_flowvcontent_panel") ;
	if(div){
		return div ;
	}
	if($d_content_layoutcenter_panel){
		return $d_content_layoutcenter_panel ;
	}
	return null ;
};

RepToolListComp.prototype.displayTopDiv = function(){
	var top = this.getTopDiv() ;
	var below = this.getBelowDiv() ;
	if(top && below){
		if(this.topDivIsVisible()){
			this.topHeight = top.style.height ;
			//隐藏顶部div
			top.style.height = "0px" ;
			var belowHeight = below.style.height ;
			belowHeight = parseInt(belowHeight) ;
			below.style.height = belowHeight + parseInt(this.topHeight) +"px" ;
			$(top).hide();
		}else{
			//显示顶部div
			top.style.height = this.topHeight ;
			var belowHeight = below.style.height ;
			belowHeight = parseInt(belowHeight) ;
			below.style.height = belowHeight - parseInt(this.topHeight) +"px" ;
			$(top).show();
		}
		try{
			this.refreshCellsComp() ;
		}catch(e){
			
		}
	}
};
RepToolListComp.prototype.displayLeftDiv = function(){
	var left = this.getLeftDiv() ;
	var right = this.getRightDiv() ;
	var clearBtn =  pageUI.getWidget("main").getComponent("topClearBtn");
	var queryBtn = pageUI.getWidget("main").getComponent("topQueryBtn");
	if(left && right){
		if(this.leftDivIsVisible()){
			this.leftWidth = left.style.width ;
			this.rightMarginLeft = right.style.marginLeft ;
			if(clearBtn && queryBtn){
				clearBtn.setVisible(true);
				queryBtn.setVisible(true);
				document.getElementById("topClearBtn").style.display="";
				document.getElementById("topQueryBtn").style.display="";
			}
			//隐藏左边div
			left.style.width = "0px" ;
			right.style.marginLeft = "0px" ;
			$(left).hide();
		}else{
			//显示左边div
			right.style.marginLeft = this.rightMarginLeft ;
			left.style.width = this.leftWidth ;
			if(clearBtn && queryBtn){
				clearBtn.setVisible(false);
			queryBtn.setVisible(false);
			document.getElementById("topClearBtn").style.display="none";
			document.getElementById("topQueryBtn").style.display="none";
			}
			$(left).show();
		}
		layoutInitFunc();
		this.refreshCellsComp() ;
	}
};
RepToolListComp.prototype.refreshCellsComp = function(){
	var comp = window.objects["myCells"] ;
	comp.refresh() ;
};
RepToolListComp.prototype.topDivIsExist = function(){
	var top = this.getTopDiv() ;
	if(top){
		return true ;
	}
	return false ;
};
RepToolListComp.prototype.leftDivIsExist = function(){
	var left = this.getLeftDiv() ;
	if(left){
		return true ;
	}
	return false ;
};
RepToolListComp.prototype.topDivIsVisible = function(){
	var top = this.getTopDiv() ;
	return this.divIsVisible(top) ;
};
RepToolListComp.prototype.leftDivIsVisible = function(){
	var left = this.getLeftDiv() ;
	return this.divIsVisible(left,"left") ;
};
RepToolListComp.prototype.divIsVisible = function(div,topOrLeft){
	if(!(div && div.style)){
		return false ;
	}
	var v = div.style.height == "0px" ;
	if(topOrLeft && topOrLeft == "left"){
		v = div.style.width == "0px" ;
	}
	if(v){
		return false ;
	}else{
		return true ;
	}
};
RepToolListComp.prototype.refurbishtoolDiv = function(){
	var tool = this.toolDiv ;
	if(this.topDivIsVisible()){
		tool.style.top = "18px" ;
		tool.style.right = "15px" ;
	}else {
		tool.style.top = "15px" ;
		tool.style.right = "15px" ;
	}
	tool.style.zIndex = 9999 ;
};
RepToolListComp.prototype.outputFile = function(type){
    var path = "";
    if(type=="EXCEL"){
    	path = "/pt/FreeReportExport/exportExcel";
    }else if(type=="PDF"){
    	path = "/pt/FreeReportExport/exportPdf";
    }else if(type=="PCB") {
    	path = "/pt/PdfDownloadForMalaysia/exportPcbPdf";
    } else if(type=="EAFORM") {
    	path = "/pt/PdfDownloadForMalaysia/exportEaformPdf";
    }
    var url = getRootPath()+path+"?pageUniqueId="+getPageUniqueId();
    this.downloadFile(url);	
};

RepToolListComp.alertDely = function(){
	var msgw,msgh,bordercolor;
	msgw=200;//提示窗口的宽度
	msgh=80;//提示窗口的高度
	bordercolor="#007DC4";//提示窗口的边框颜色

	var sWidth,sHeight;
	sWidth=document.body.offsetWidth;
	sHeight=document.body.offsetHeight;

	var bgObj=document.createElement("div");
	bgObj.setAttribute('id','bgDiv');
	bgObj.style.position="absolute";
	bgObj.style.top="0";
	bgObj.style.background="#777";
	bgObj.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75";
	bgObj.style.opacity="0.6";
	bgObj.style.left="0";
	bgObj.style.width=sWidth + "px";
	bgObj.style.height=sHeight + "px";
	bgObj.style.zIndex="10000";
	document.body.appendChild(bgObj);

	var msgObj=document.createElement("div")
	msgObj.setAttribute("id","msgDiv");
	msgObj.setAttribute("align","center");
	msgObj.style.position="absolute";
	msgObj.style.background="white";
	msgObj.style.font="12px/1.6em Verdana, Geneva, Arial, Helvetica, sans-serif";
	msgObj.style.border="1px solid " + bordercolor;
	msgObj.style.width=msgw + "px";
	msgObj.style.height=msgh + "px";
	msgObj.style.top=(document.documentElement.scrollTop + (sHeight-msgh)/2) + "px";
	msgObj.style.left=(sWidth-msgw)/2 + "px";
	msgObj.style.zIndex="10000";
	document.body.appendChild(msgObj);
	 
	var title = document.createElement("h4");
	title.setAttribute("id","msgTitle");
	title.setAttribute("align","right");
	title.style.margin = "0";
	title.style.padding = "3px";
	title.style.background = bordercolor;
	title.style.filter = "progid:DXImageTransform.Microsoft.Alpha(startX=20, startY=20, finishX=100, finishY=100,style=1,opacity=75,finishOpacity=100);";
	title.style.opacity = "0.75";
	title.style.border = "1px solid " + bordercolor;
	title.style.height = "18px";
	title.style.font = "12px Verdana, Geneva, Arial, Helvetica, sans-serif";
	title.style.color = "white";
	title.style.cursor = "pointer";
	title.innerHTML="<table border='0' width='100%'><tr><td align='left'><b>"+ 
		trans('ml_err')/**错误！*/ +"</b></td><td align='right' id='close'>"+trans('ml_close')/**关闭*/+"</td></tr></table></div>";
	document.getElementById("msgDiv").appendChild(title);
	var close = document.getElementById('close');
	close.onclick=function(){
		document.body.removeChild(bgObj);
		document.getElementById("msgDiv").removeChild(title);
		document.body.removeChild(msgObj);
	};

	var content = document.createElement("p");
	content.style.margin = "1em 0"
	content.setAttribute("id","msgTxt");
	content.innerHTML = trans("ml_msg")/**训练盘不支持导出！*/;
	document.getElementById("msgDiv").appendChild(content);
};

RepToolListComp.prototype.downloadFile = function(url){
	if(!this.frameDiv){
		this.frameDiv = $ce("DIV") ;
		this.frameDiv.id = "toolList_frameDiv" ;
		this.frameDiv.style.width="0px";
		this.frameDiv.style.height="0px";
		this.frameDiv.style.display = "none";
		document.body.appendChild(this.frameDiv); 
	}

	if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){ // if ff
		RepToolListComp.showLoading() ;//显示进度条
	}else if(/*@cc_on!@*/0){ //if ie
		RepToolListComp.showLoading() ;//显示进度条
	}
	
	this.frameDiv.innerHTML = '<iframe id = "toolList_frameDiv_iframe" src="'+url+'" onload="RepToolListComp.hideLoading();"></iframe>' ;
	var iframe = document.getElementById("toolList_frameDiv_iframe") ;
	
	if (/*@cc_on!@*/0) { //if IE
		iframe.onreadystatechange = function(){
			if (this.readyState == "interactive" || this.readyState == "loaded" || this.readyState == "complete"){
				RepToolListComp.hideLoading();
			}
		};
	} else {
		iframe.onload = function(){
			RepToolListComp.hideLoading();
		}
		iframe.addEventListener("load",function(){
			RepToolListComp.hideLoading();
		},false);
	}
	
	//如果iframe加载过程中发生异常，将调用下面的方法弹框提示
	if(iframe.attachEvent){
		iframe.attachEvent("onreadystatechange", function() {
			if (iframe.readyState === "complete" || iframe.readyState == "loaded") {
				//alert(trans("ml_msg")); //训练盘不支持导出！
				RepToolListComp.alertDely();
			}
		});
	}else { 
		iframe.addEventListener("load", function() {
			if (iframe.contentDocument.title === 'Error'){ // FF
				RepToolListComp.alertDely();
			}
		}, false);
	}
	
};
$(function(){
	if(window.IS_PAD){
		return;
	}
	;
	var href=window.location.href;
	if(href.indexOf("hide_toolist_menu=1") > -1) return;
	
	var repTool = new RepToolListComp();
	repTool.createSelf();
});
window.getLfwTop=function()
{
	//window.parent;
	try{
		var parentWin=window;
		while(parentWin){
		if(parentWin.showDialog)
		window.hasShowDialogWin = parentWin;
		if(parentWin.lfwtop){
		window.lfwtopwin = parentWin;
		break;
	}
	if(parentWin == parentWin.parent)
	break;
	parentWin = parentWin.parent;
	}
}
catch(error){
}
if(window.lfwtopwin) 
return window.lfwtopwin;
return window.hasShowDialogWin;
};
