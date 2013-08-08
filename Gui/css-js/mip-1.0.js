
function next(){

      // unselect the current item 
     $("[data-wow-index ='"+ index+"']")
             .removeClass("selected")
             .addClass("not-selected");
      // select the next one  
        index++;
         index = index %8;
         
     $("[data-wow-index ='"+ index+"']")
             .removeClass("not-selected")
             .addClass("selected");
    
 
   
}
function show(){
    
    selectedItem = $("[data-wow-index ='"+ index+"'] a").trigger("click");
 
    
}

function go(){
    index = 0;
   var keyword = $("#txt_input").val();
   $("#results").empty();
   googleSearch( keyword);
     
}

function clearScreen(){
    
    index = 0;
    $("#results").empty();
  
}
function back(){
    
   //$.fancybox.close()  
  
 // var esc = $.Event("keydown", {keyCode:27});
   $('.fancybox-wrap').tap();
 // var x = $('.fancybox-opened');
  //alert(x);
    // $.fancybox.close
 // $('.fancybox-opened')
}


function showEyes(){
 $("#eyes").slideDown("slow");
}
function hideEyes(){
  $("#eyes").slideUp("slow");   
}
function showCenterPanel(){
     $("#principal").slideDown("slow");   
}
function hideCenterPanel() {
     $("#principal").slideUp("slow");   
}

function pictureSearch(keyword){
    
   
  
	var apiURL = 'http://ajax.googleapis.com/ajax/services/search/'+"images"+
                        '?v=1.0&callback=?';
	
	$.getJSON(apiURL,{
		    q	: keyword,
		    rsz	:8,
		    start	: 0
		},function(data){
		
		var content = "";
			for(var i = 0; i < data.responseData.results.length; i++){
			content = "<div class='pic not-selected' data-wow-index='"+i+"' >"+
                                  "<a href='"+data.responseData.results[i].url+"'>"+
                                  "<img src='"+data.responseData.results[i].tbUrl + "'/>"+
                                  "</a>"+
                                  "</div>";
                          
			  $('#results').append(content);
			}	
                        
                         //-- select the first item
                          var s =  $('.pic').first()
                                  .removeClass("not-selected")
                                  .addClass("selected");
                          
                          //--FancyBox --
                                $("a").fancybox({
                                       showCloseButton: false,
                                       openEffect : 'elastic',
				       closeEffect : 'elastic',
				       fitToView : true,
				       helpers : {
						title : {
							type : 'inside'
						}
					}
                                  
                               });

                       
                       
		}
	);
}

// video search functions  -------------------------------


function videoSearch(keyword){
    
   
  
	var apiURL = 'http://ajax.googleapis.com/ajax/services/search/'+'video'+
                        '?v=1.0&callback=?';
	
	$.getJSON(apiURL,{
		    q	: keyword,
		    rsz	:8,
		    start	: 0
		},function(data){
		var content = "";
			for(var i = 0; i < data.responseData.results.length; i++){
			content = "<div class='pic not-selected' data-wow-index='"+i+"' >"+
                                  "<a class='fancybox fancybox.iframe' href='"+data.responseData.results[i].playUrl+"'>"+
                                  "<img src='"+data.responseData.results[i].tbUrl + "'/>"+
                                  "</a>"+
                                  "</div>";
                          
			  $('#results').append(content);
			}	
                        
                         //-- select the first item
                          var s =  $('.pic').first()
                                  .removeClass("not-selected")
                                  .addClass("selected");
                          
                          //--FancyBox --
                                $("a").fancybox({

                                   helpers: {
                                       title : {
                                           type : 'float'
                                       }
                                   }
                               });

                       
                       
		}
	);
}

//  fire when the player has finnished loading 

function onPlayerReady(event){
    event.target.playVideo();
}

function onPlayerStateChange(event){
    
    if(event.data === 0){
        $.fancybox.next();
    }
}


function onYouTubePlayerAPIReady(){
    
    $(document).ready(function(){
       $(".fancybox")
       .attr('rel','gallery')
       .fancybox({
        openEffect : 'none',
        closeEffect :'none',
        nextEffect : 'none',
        prevEffect : 'none',
        padding : 0,
        margin : 50,
        beforeShow : function(){
            
       var id = $.fancybox.inner.find('iframe').attr('id'); 
     
        var player = new YT.Player(id, {
                        events: {
                            'onReady': onPlayerReady,
                            'onStateChange':onPlayerStateChange
                        }
                        })
        }
       });
        
    });
    
}
