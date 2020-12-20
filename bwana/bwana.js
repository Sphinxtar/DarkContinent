if (window.top.frames.length!==0) {
 if (window.location.href.replace)
  window.top.location.replace(self.location.href);
 else
  window.top.location.href=self.document.href;
}

var xmlObj=null;

function north() {
 jack('/dc/bwana?cmd=mov&parm=n&amt=mbag');
}

function south() {
 jack('/dc/bwana?cmd=mov&parm=s&amt=mbag');
}

function east() {
 jack('/dc/bwana?cmd=mov&parm=e&amt=mbag');
}

function west() {
 jack('/dc/bwana?cmd=mov&parm=w&amt=mbag');
}

function jump() {
 jack('/dc/bwana?cmd=jmp&parm=&amt=mbag');
}

function jack(url) {
  if(window.XMLHttpRequest)
  {
    xmlObj = new XMLHttpRequest();
  }
  else if(window.ActiveXObject)
  {
    xmlObj = new ActiveXObject('MSXML2.XMLHTTP');
  } else {
    alert('No Object To This Browser');
    return;
  }
  xmlObj.onreadystatechange = jacked;
  xmlObj.open('GET', url, true);
  xmlObj.send(null);
}

function jacking(obj)
{   
 /* 
  0 UNINITIALIZED obj created but uninitialized
  1 LOADING       obj created but not yet sent
  2 LOADED        sent no answer yet
  3 INTERACTIVE   retrieval in progress
  4 COMPLETED     all data received
  */
 if(obj.readyState === 0) { document.getElementById('stats').innerHTML = 'Unh drum speak...'; }
// if(obj.readyState == 1) { document.getElementById('stats').innerHTML = 'Ahhheeeaaah!'; }
 if(obj.readyState == 2) { document.getElementById('stats').innerHTML = 'Um drum silent...'; }
 if(obj.readyState == 3) { document.getElementById('stats').innerHTML ='Ungowa!'; }
 if(obj.readyState == 4) { 
   if(obj.status == 200) {
     return true; 
   } else if(obj.status == 404) {
     document.getElementById('content').innerHTML = 'Ugh, file not found bwana';
   } else {
     document.getElementById('content').innerHTML = 'Unh, bad juju fetching XML.';
   }
  }
  return false;
}

function jacked()
{
  if(jacking(xmlObj))
  {
    var str = xmlObj.responseText;
    var b = str.search(/<bull>/m);
    var e = str.search(/<\/bull>/m);
    if(b>0&&e>0) {
      document.getElementById('content').innerHTML = str.substring(b+6,e);
    }
    b = str.search(/<map>/m);
    e = str.search(/<\/map>/m);
    if(b>0&&e>0) {
      document.getElementById('map').innerHTML = str.substring(b+5,e);
    }
    b = str.search(/<terra>/m);
    e = str.search(/<\/terra>/m);
    if(b>0&&e>0) {
      document.getElementById('tfirma').innerHTML = str.substring(b+7,e);
    }
    b = str.search(/<bwana>/m);
    e = str.search(/<\/bwana>/m);
    if(b>0&&e>0) {
      document.getElementById('guy').innerHTML = str.substring(b+7,e);
    }
    b = str.search(/<messager>/m);
    e = str.search(/<\/messager>/m);
    if(b>0&&e>0) {
      document.getElementById('content').innerHTML = str.substring(b+10,e);
      document.getElementById('msg').value = '';
//      alert(str.substring(b+10,e));
    }
  }
}

function use(me) {
self.focus();
var tool=document.getElementById('cmd');
tool.value=me.name;
}

function docmd() {
 self.focus();
 var geck='0';
 var geck2='none';
 var amt='mbag';
 var tul=document.getElementById('cmd');

 if (tul.value =='log') {
  document.getElementById('content').innerHTML = "<p>Those drums are driving me crazy!</p>";
  amt='v';
 }
  
 if (tul.value=='kil'||tul.value=='stl') {
  if (document.forms[0].others.length>1) {
   geck2=document.forms[0].others[document.forms[0].others.selectedIndex].value;
  } else {
   geck2=document.forms[0].others.value;
  }
  amt='bag';
 }

 if (tul.value=='snd') { 
   geck=document.getElementById('to').value;
   geck2=document.getElementById('msg').value.replace(/\n/gi,' ');
   amt='a';
   document.getElementById('content').innerHTML = "";
   if (geck2.length<2) {
    return false;
   }
 }

 if (tul.value=='set') { 
  geck=document.getElementById('parm').value;
 } 

 if (tul.value=='use'||tul.value=='put'||tul.value=='get'||tul.value=='kil') {
  if (document.forms[0].g1.length) {
   for (var i=0; i<document.forms[0].g1.length; i++) {
    if (document.forms[0].g1[i].checked) {
     geck=document.forms[0].g1[i].value;
    }
   }
  } else {
   geck=document.forms[0].g1.value;
  }
  amt='bag';
 }
 jack('/dc/bwana?cmd=' + tul.value + '&parm=' + geck + '&amt=' + amt + '&parmn=' + geck2 + '&');
 return false;
}

Image1= new Image(20,22);
Image1.src = 'resources/0.jpg';

Image2= new Image(20,22); 
Image2.src = 'resources/a.jpg';

Image3= new Image(20,22);
Image3.src = 'resources/b.jpg';

Image4= new Image(20,22);
Image4.src = 'resources/c.jpg';

Image5= new Image(20,22);
Image5.src = 'resources/d.jpg';

Image6= new Image(20,22);
Image6.src = 'resources/e.jpg';

Image7= new Image(20,22);
Image7.src = 'resources/f.jpg';

Image8= new Image(20,22);
Image8.src = 'resources/g.jpg';

Image9= new Image(20,22);
Image9.src = 'resources/h.jpg';

Image10= new Image(20,22);
Image10.src = 'resources/i.jpg';

Image11= new Image(20,22);
Image11.src = 'resources/j.jpg';

Image12= new Image(20,22);
Image12.src = 'resources/k.jpg';

Image13= new Image(20,22);
Image13.src = 'resources/l.jpg';

Image14= new Image(20,22);
Image14.src = 'resources/m.jpg';

Image15= new Image(20,22);
Image15.src = 'resources/n.jpg';

Image16= new Image(20,22);
Image16.src = 'resources/o.jpg';

Image17= new Image(20,22);
Image17.src = 'resources/p.jpg';

Image18= new Image(20,22);
Image18.src = 'resources/r.jpg';

Image19= new Image(20,22);
Image19.src = 'resources/s.jpg';

Image20= new Image(20,22);
Image20.src = 'resources/t.jpg';

Image21= new Image(20,22);
Image21.src = 'resources/u.jpg';

Image22= new Image(20,22);
Image22.src = 'resources/w.jpg';

Image23= new Image(20,22);
Image23.src = 'resources/y.jpg';

Image24= new Image(20,22);
Image24.src = 'resources/z.jpg';

Image25= new Image(20,22);
Image25.src = 'resources/you.jpg';

// kick it off 
jack('/dc/bwana?cmd=jmp&parm=&amt=mbag');
