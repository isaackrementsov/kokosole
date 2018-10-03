//Javascript hell
var navbar = document.getElementsByClassName('navbar')[0];
var content = document.getElementsByClassName('content')[0];
content.style.marginTop = navbar.offsetHeight + 'px';
function opendiv(id, display){
    var div = document.getElementById(id, display);
    if(div.style.display == 'none'){
        div.style.display = display;
    }else{
        div.style.display = 'none';
    }
}
function opendivs(divArray){
    for(let i = 0; i < divArray.length; i++){
        let div = document.getElementById(divArray[i].id);
        if(div.style.display == 'none'){
            div.style.display = divArray[i].display;
        }else{
            div.style.display = 'none';
        }
    }
}
