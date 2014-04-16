if(typeof($jskey)!="object"){$jskey={};}$jskey.upload={uploadArray:[],put:function(key,desc,callback){var item={"key":key,"desc":desc,"callback":callback,"state":0};for(var i=0;i<this.uploadArray.length;i++){if(item.key==this.uploadArray[i].key){try{this.uploadArray[i].callback("undo");}catch(e){}this.uploadArray.splice(i,1);break;}}this.uploadArray[this.uploadArray.length]=item;return;},remove:function(key){for(var i=0;i<this.uploadArray.length;i++){if(key==this.uploadArray[i].key){try{this.uploadArray[i].callback("undo");}catch(e){}this.uploadArray.splice(i,1);return;}}},updateState:function(key,state){for(var i=0;i<this.uploadArray.length;i++){if(key==this.uploadArray[i].key){this.uploadArray[i].state=state;return;}}},check:function(){var _msg="";var _errmsg="";var _doingmsg="";var _err=0;var _doing=0;for(var i=0;i<this.uploadArray.length;i++){switch(this.uploadArray[i].state){case 0:_doing++;_doingmsg=_doingmsg+this.uploadArray[i].desc+"正在上传;\n";break;case-1:_err++;_errmsg=_errmsg+this.uploadArray[i].desc+"上传错误;\n";break;}}if(_doing>0||_err>0){_msg=_doingmsg+_errmsg+"\n合计:";if(_doing>0){_msg=_msg+_doing+"个文件正在上传;";}if(_err>0){_msg=_msg+_err+"个文件上传错误;";}}if(_msg !=""){return confirm(_msg+"\n是否忽略？");}return true;},replaceAll:function(s,t,u){i=s.indexOf(t);r="";if(i==-1)return s;r+=s.substring(0,i)+u;if(i+t.length<s.length){r+=this.replaceAll((s.substring(i+t.length,s.length)),t,u);}return r;},createIframe:function(id){var frameId='jskeyUploadFrame'+id;var io;if(window.ActiveXObject){io=document.createElement('<iframe id="'+frameId+'" name="'+frameId+'" src="javascript:false" />');}else{io=document.createElement('iframe');io.id=frameId;io.name=frameId;io.src="javascript:false";}io.style.position='absolute';io.style.top='-1000px';io.style.left='-1000px';document.body.appendChild(io);return io;},createForm:function(id,fileId,url){var frameId='jskeyUploadFrame'+id;var formId='jskeyUploadForm'+id;var _fileId='jskeyUploadFile'+id;var form;var _old=document.getElementById(fileId);var _new;if(window.ActiveXObject){form=document.createElement('<form id="'+formId+'" name="'+formId+'"></form>');_new=document.createElement(_old.outerHTML);}else{form=document.createElement('form');form.id=formId;form.name=formId;var attr;var attrs=_old.attributes;var str="<input";for(var i=0;i<attrs.length;i++){attr=attrs[i];if(attr.specified){str+=" "+attr.name+'="'+attr.value+'"';}}str+="/>";var d=document.createElement('div');d.innerHTML=str;_new=d.childNodes[0];}_old.id=_fileId;_old.name=_fileId;form.method='POST';form.target=frameId;form.action=url;form.style.position="absolute";form.style.top="-1200px";form.style.left="-1200px";if(form.encoding){form.encoding='multipart/form-data';}else{form.enctype='multipart/form-data';}_new.value="";_old.parentNode.insertBefore(_new,_old);form.appendChild(_old);document.body.appendChild(form);return form;},doUpload:function(s){var id=new Date().getTime();var _n=document.getElementById(s.fileId).value;if(_n==""){alert("未选择文件！");return false;}_n=this.replaceAll(this.replaceAll(_n,"\\","\/"),"\"","");_n=_n.substring(_n.lastIndexOf("\/",_n.length)+1,_n.length);s.url+=((s.url.indexOf("?")!=-1)?"&":"?")+"filename="+encodeURI(encodeURI(_n));var io=this.createIframe(id);var form=this.createForm(id,s.fileId,s.url);var requestDone=false;var _upload=this;var uploadCallback=function(isError){requestDone=true;if(isError !="undo"){try{var isSuccess=false;if(isError !="error"){var xml="null";if(io.contentWindow){xml=io.contentWindow.document.body.innerHTML;}else if(io.contentDocument){xml=io.contentDocument.document.body.innerHTML;}eval("var data="+xml+";");if(typeof data=="object"){isSuccess=true;s.success(data);}}if(isSuccess){_upload.updateState(s.fileKey,1);}else{_upload.updateState(s.fileKey,-1);s.error(null);}}catch(e){_upload.updateState(s.fileKey,-1);s.error(e);}}setTimeout(function(){try{if(io.parentNode){io.parentNode.removeChild(io);}if(form.parentNode){form.parentNode.removeChild(form);}}catch(e){}},100);};if(s.timeout>0){setTimeout(function(){if(!requestDone){uploadCallback("error");}},s.timeout);}try{_upload.put(s.fileKey,s.fileDesc,uploadCallback);form.submit();if(window.attachEvent){io.attachEvent('onload',uploadCallback);}else{io.addEventListener('load',uploadCallback,false);}}catch(e){if(!requestDone){uploadCallback("error");}}return true;}};