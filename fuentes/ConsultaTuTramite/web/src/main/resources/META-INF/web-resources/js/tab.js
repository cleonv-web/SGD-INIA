function makeTabSelected(hrefName){
	var myTabs=new Array("name01","name02","name03","name04");
	for (i = 0; i < myTabs.length; i++){
        $("#"+myTabs[i]).removeClass("ui-tabs-selected ui-state-active");
    }
    $("#"+hrefName).addClass("ui-tabs-selected ui-state-active");
}