var basket = [];
function zollStart(){
	document.getElementById("element_date_1").value=10;
	document.getElementById("element_date_2").value=15;
	document.getElementById("element_date_3").value=2016;
	printBasket();
}
function addClick(){
	var selectitem = document.getElementById("element_items").value;
	var select = document.getElementById("element_items");
	var selectitemtext = document.getElementById("element_items").selectedOptions[0].text;
	var itemamount = document.getElementById("element_amount").value;
	var basketentry = {};
	basketentry["name"]=selectitem;
	basketentry["amount"]=itemamount;
	if(selectitem && selectitem != '' && itemamount >0){
		//document.getElementById("productliststartmessage").innerHTML="Added to basket:";
		//var newli = document.createElement("li");
		//newli.innerHTML= "<b>" + itemamount +  " * " + selectitemtext + "</b>";	
		//document.getElementById("productlistul").appendChild(newli);
		basket.push(basketentry);
	}
	printBasket();
}

function printBasket(){
	document.getElementById("productliststartmessage").innerHTML="<ul><li><large>No items added yet!</large></li></ul>";
	document.getElementById("productlistul").innerHTML="";
	document.getElementById("results").innerHTML="";	
	for (var i=0;i<basket.length;i++){
		var itemamount = basket[i].amount;
		var selectitemtext = basket[i].name;
		document.getElementById("productliststartmessage").innerHTML="Added to basket:";
		var newli = document.createElement("li");
		newli.innerHTML= "<b>" + itemamount +  " * " + selectitemtext + "</b>";	
		document.getElementById("productlistul").appendChild(newli);
	}
}

function resetClick(){

	basket=[];
	printBasket();
}
function sampleClick(){
  var body ={
  "items": [
	{
		"name":"beef",
		"amount":2
	},
	{
		"name":"pork",
		"amount":0.25
	},
	{
		"name":"beef",
		"amount":1.5
	},
	{
		"name":"wine",
		"amount":9
	},
	{
		"name":"vodka",
		"amount":0.75
	},
	{
		"name":"crossbow",
		"amount":1
	},
	{
		"name":"cake",
		"amount":10
	}
  ]
  }
  ;
  basket=body.items;
  printBasket();
  calcClick();
}

function calcClick(){
	if (basket.length==0) {
		alert("Please add some items.");
		return;
	}
	var from=document.getElementById("element_from").selectedOptions[0].text;
	var to=document.getElementById("element_to").selectedOptions[0].text;
	var importers=document.getElementById("element_people").value;

	  var xhr = new XMLHttpRequest();
	  xhr.onreadystatechange=function() {
        if (xhr.readyState === 4){   
          if(xhr.status != 200){  
	        alert("Unsupported feature called, only import to Switzerland is currently supported.");
			return;
          }
        } 
	  }
	  xhr.open("post", "http://localhost:8080/api/chzoll/check?from="+from+"&to="+to+"&importers="+importers, true);
	  xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

	  var body = {"items": basket};
	  
	  xhr.send(JSON.stringify(body));

	  xhr.onloadend = function () {
		  var items = JSON.parse(this.response).items;
		  var resultsdiv = document.getElementById("results");
		  resultsdiv.innerHTML="";
		  var title = document.createElement("h2");
		  title.innerHTML="Customs calculation results";
		  resultsdiv.appendChild(title);
		  for (var i=0;i<items.length;++i){
			  var a = items[i].amount;
			  var cn = items[i].category.name;
			  var c = items[i].comment;
			  var n = items[i].name;
			  var t1 =  document.createElement("h3");
			  var free = false;
			  var pay = false;
			  var warn = false;
			  if (c == "" || c == null || c =="PAY:0.0" ){
			  	free = true;
			  } else if ( c.substr(0,4)=="PAY:"){
				  pay = true;
			  } else {
				  warn = true;
			  }
			  t1.innerHTML = n + " ("+cn+")";
			  t1.style="text-transform: capitalize;"
			  if (free) {
                t1.style="text-transform: capitalize; background-color: #a1d490";
			  } else if (pay) {
			  	t1.style="text-transform: capitalize; background-color: #f9af40";
			  } else {
			  	t1.style="text-transform: capitalize; background-color: #ed6a6a";
			  }
			  var t2 = document.createElement("li");
			  t2.innerHTML="Amount imported : " + a;
			  var t3 = document.createElement("li");
			  if (free){
				t3.innerHTML="No customs fees to pay!";
			  } else if (pay) {
				  t3.innerHTML="Fees to pay: CHF "+parseFloat(c.substr(4)).toFixed(2);
			  } else {
			  	t3.innerHTML="Customs result : " + c;
			  }
			  resultsdiv.appendChild(t1);
			  resultsdiv.appendChild(t2);
			  resultsdiv.appendChild(t3);
			  if (i!=items.length-1){
			  resultsdiv.appendChild(document.createElement("hr"));
			  }
		  }
	  };
  
}