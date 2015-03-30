//将传输内容 显示为json字符串
function onFormLoaded(form){
	var record = form.datasource.getRecord(0);
  	var chuanRuNR = record.getFieldValue('jsonData');
  
  	var jsCodeMirrorEdit = CodeMirror.fromTextArea($('#chuanShuNR_textarea')[0], {
      lineNumbers: true,
      matchBrackets: true,
      continueComments: "Enter",
      extraKeys: {"Ctrl-Q": "toggleComment"},
      mode: "javascript"
    });
  if(chuanRuNR!=null){
    	jsCodeMirrorEdit.setValue(LUI.Util.stringify(chuanRuNR));
  }else{
    	jsCodeMirrorEdit.setValue('');
  }
	
}