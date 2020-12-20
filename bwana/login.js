function findObject(id)
{
 if(document.getElementById)
  return document.getElementById(id);
 if(document.all)
  return document.all[id];
 return { style: {} };
}

function getcookie(cookiename) {
 var cookiestring=""+document.cookie;
 var index1=cookiestring.indexOf(cookiename);
 if (index1==-1 || cookiename==="") return '';
 var index2=cookiestring.indexOf(';',index1);
 if (index2==-1) index2=cookiestring.length; 
 return unescape(cookiestring.substring(index1+cookiename.length+1,index2));
}

function fillform() {  
 cookieValue = getcookie('name');
 if(cookieValue !== null) {
  var d = findObject("name");
  d.value=(cookieValue)?cookieValue.replace(/"/g,''):'';
 }
 cookieValue = getcookie('pwrd');
 if(cookieValue !== null) {
  var p = findObject("pwrd");
  p.value=(cookieValue)?cookieValue:'';
  p = document.getElementById("confirm");
  p.value===(cookieValue)?cookieValue:'';
 }
 cookieValue = getcookie('mail');
 if(cookieValue !== null) {
  var m = findObject("mail");
  m.value=(cookieValue)?cookieValue:''; 
 }
}

function checkplease(which){
 self.focus();
 var pass=true;
 if(which.name.value.length<2){
  var msg="You'll Have To Be Funnier Than That";
  pass=false;
 }
 if(pass&&which.pwrd.value.length<5){
  msg="You're a little short down there.";
  pass=false;
 }
 if(pass&&which.pwrd.value!=which.confirm.value){
  msg="Password Must Match Confirmation";
  pass=false;
 }
 if(!pass){
  var so=findObject('signon');
  so.innerHTML=msg;
 }else{
  which.cmd.value="adb";
  which.amt.value="f";
 }
 return pass;
}

if (window.top.frames.length!==0) {
 if (window.location.href.replace !== 0) {
  window.top.location.replace(self.location.href);
 }
 else
 {
  window.top.location.href=self.document.href;
 }
}
