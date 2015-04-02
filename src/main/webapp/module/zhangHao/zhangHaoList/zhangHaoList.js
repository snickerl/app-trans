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
		if(gongYSMCValue==null || gongYSMCValue.length==0 || gongYSMCValue == "账号名称/代号"){
			LUI.Grid.getInstance('yongHuGrid').datasource.load({
				filters : []
			});
		}else{
			LUI.Grid.getInstance('yongHuGrid').datasource.load({
				filters : [{
					property : "dengLuDH",
					operator : 'like',
                  	assist:"['yongHuMC']",
					value : gongYSMCValue
				}]
			});
		}
	});
  
  	$('#appendBtn').click(function(){
      	window.open("/nim.html?_pt_=yongHu/yongHuAppend/yongHuAppend.html");
    });
  
	
}
function onGridRowRender(grid,ob,event){
	var rowEl = event.params.rowEl;
	var rowData = event.params.rowData;
  
	rowEl.find('#editdBtn').click(function(){
		window.open("/nim.html?_pt_=yongHu/yongHuEdit/yongHuEdit.html&_ps_={id:"+rowData.yongHuDM+"}");
	});
	rowEl.find('#changePasswordBtn').click(function(){
		window.open("/nim.html?_pt_=yongHu/changePassword/changePassword.html&_ps_={id:"+rowData.yongHuDM+"}");
	});
  	rowEl.find('#qiYongBtn').click(function(){
		//
      	LUI.Message.confirm('请确认...','是否启用当前账号？',function(success){
             if(success){
                LUI.DataUtils.execute('sys','yh','qiYong',[{yongHuDM:rowData.yongHuDM}],function(){
                	if(result.success){
                		LUI.Message.info("信息","账号启用成功！");
                	}
                });
               	grid.datasource.load();
             }
        });
	});
	rowEl.find('#tingYongBtn').click(function(){
		//
      	LUI.Message.confirm('请确认...','是否停用当前账号？',function(success){
             if(success){
                LUI.DataUtils.execute('sys','yh','tingYong',[{yongHuDM:rowData.yongHuDM}],function(){
                	if(result.success){
                		LUI.Message.info("信息","账号停用成功！");
                	}
                });
               	grid.datasource.load();
             }
        });
	});
}