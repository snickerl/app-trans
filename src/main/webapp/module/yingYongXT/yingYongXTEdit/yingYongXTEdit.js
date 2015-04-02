function onPageReady(){
  $('#newYongHunBtn').click(function(){
  		//增加新的用户关联
  		var stlRecord = LUI.Datasource.getInstance('yingYongXTRecord').getRecord(0);
		var zdsRecordset = stlRecord.getFieldValue('shuJuPTYHs');
    	zdsRecordset.addRecord({});
  });
  	
}


function onYyxtDataSubmit(ds,ob,evt){
    if(evt.params.success){
      	window.close();
    }
      
}


function onGridRowRender(grid,ob,evt){
  	var isInitial = evt.params.isInitial;
	if(isInitial){
		var rowEl = evt.params.rowEl;
		var record = evt.params.record;
		rowEl.find('#delete').click(function(){
				//删除当前记录
				record.remove();
		})
	}
}