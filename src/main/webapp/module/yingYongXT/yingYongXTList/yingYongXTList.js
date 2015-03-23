function onPageRady(){
	//查询
	$('#GFZRPS_zhengShi').find('#gongYSMC:eq(0)').keydown(function(event) {
		if (event.keyCode == 13) {
			$("#GFZRPS_zhengShi #GFZRPS_searchbtn").click();
		}
	});
	
	$("#GFZRPS_zhengShi #GFZRPS_searchbtn").click(function(){
		//
		var gongYSMCValue = $('#GFZRPS_zhengShi').find('#gongYSMC:eq(0)').val();
		if(gongYSMCValue==null || gongYSMCValue.length==0 || gongYSMCValue == "应用系统编号/名称"){
			LUI.Grid.getInstance('yingYongXTListGrid').datasource.load({
				filters : []
			});
		}else{
			LUI.Grid.getInstance('yingYongXTListGrid').datasource.load({
				filters : [{
					property : "yingYongXTBH",
					operator : 'like',
                  	assist:"['yingYongXTMC']",
					value : gongYSMCValue
				}]
			});
		}
	});
  
  	$('#appendBtn').click(function(){
      	window.open("/nim.html?_pt_=yingYongXT/yingYongXTAppend/yingYongXTAppend.html");
    });
  
	
}
function onGridRowRender(grid,ob,event){
	var rowEl = event.params.rowEl;
	var rowData = event.params.rowData;
	
	rowEl.find('#show').click(function(){
		window.open("/nim.html?_pt_=yingYongXT/yingYongXTShow/yingYongXTShow.html&_ps_={id:"+rowData.yingYongXTDM+"}");
	});
	rowEl.find('#edit').click(function(){
		window.open("/nim.html?_pt_=yingYongXT/yingYongXTEdit/yingYongXTEdit.html&_ps_={id:"+rowData.yingYongXTDM+"}");
	});
	rowEl.find('#delete').click(function(){
		//
      	LUI.Message.confirm('请确认...','是否删除当前应用系统记录？',function(success){
             if(success){
                LUI.DataUtils.execute('trans','yyxt','delete',[{yingYongXTDM:rowData.yingYongXTDM}],function(){
                	if(result.success){
                		LUI.Message.info("信息","删除成功！");
                	}
                });
               	grid.datasource.load();
             }
        });
	});
}