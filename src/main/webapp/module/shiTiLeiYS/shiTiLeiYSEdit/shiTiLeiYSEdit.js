function onPageRady(){
  $('#newZiDuanBtn').click(function(){
  		//增加新的字段映射
  		var stlysRecord = LUI.Datasource.getInstance('shiTiLeiYSRecord').getRecord(0);
		var zdsRecordset = stlysRecord.getFieldValue('zds');
    	zdsRecordset.addRecord({});
  });
  	
}


function onGridRowRender(grid,ob,evt){
  	var isInitial = evt.params.isInitial;
	if(isInitial){
		var rowEl = evt.params.rowEl;
		var record = evt.params.record;
		rowEl.find('#delete').click(function(){
				//删除当前记录
				record.remove();
		});
	}
}



function ziDuanLXChanged(field,ob,evt){
  	var newVal = evt.params.newValue;
	console.info('ziDuanLXChanged');
	var guanLianSTLField = field.cell.row.getCell('guanLianSTLYS').field;
  
	if(newVal==null){
		guanLianSTLField.enable();
	}else{
		if( newVal.ziDuanLXDH == 'object'){//对象	
          	if(!evt.params.isInitial){
                guanLianSTLField.setValue(null);
            }
			guanLianSTLField.enable();
		}else{
          	if(!evt.params.isInitial){
				guanLianSTLField.setValue(null);
              
            }
			guanLianSTLField.disable();
		}
	}
}

