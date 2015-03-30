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
		if(gongYSMCValue==null || gongYSMCValue.length==0 || gongYSMCValue == "源/目标应用系统代号"){
			LUI.Grid.getInstance('yingYongXTListGrid').datasource.load({
				filters : []
			});
		}else{
			LUI.Grid.getInstance('yingYongXTListGrid').datasource.load({
				filters : [{
					property : "sourceXTDH",
					operator : 'like',
                  	assist:"['targetXTDH']",
					value : gongYSMCValue
				}]
			});
		}
	});
  
}
function onGridRowRender(grid,ob,event){
	var rowEl = event.params.rowEl;
	var rowData = event.params.rowData;
	
	rowEl.find('#show').click(function(){
		window.open("/nim.html?_pt_=chuanShuJL/chuanShuJLShow/chuanShuJLShow.html&_ps_={id:"+rowData.chuanShuJLDM+"}");
	});
}