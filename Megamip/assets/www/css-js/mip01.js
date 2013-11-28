




// var global

var index = 0;
var selectedItem = null;
var timerId = null;
var player = null;

//----------animations 

function sequence01(){    // transition - shrink 


// fade-out fade-in effect ---------------------
 var $active = $('#eyes');
 var $next = $("#next");
transition($next, $active);


// shrink effect ----------------------------------
       var timerId1 = setInterval(
    function(){
      
    $active.removeAttr('style'); // resetting the style 
    $next.removeAttr('style');
     shrink();
      clearInterval(timerId1);

    }, 2000);


// count down animation  ----------------------------------

 var timerId2 = setInterval(
    function(){
      
    $active.removeAttr('style'); // resetting the style 
    $next.removeAttr('style');
     animationInit();
     setFrameTo(2);
     loop(2,4);
     clearInterval(timerId2);

    }, 3000);

//call to the "speak now"   ( onScreen ...) method ---------
    var timerId3 = setInterval(
    function(){
      
     // call through the javascript interface to show up the "speak now" screen
     try{  
     megaMipJSInterface.onSpeak();
      }catch(error){
        console.log("bloc catch err: "+error);
      }
     clearInterval(timerId3);
     animationInit();  // reset the eyes to the initial state 

    }, 4500);
   
   
   
    }




function setFrameTo(n){

  $("#eyes").removeAttr('class');
  $("#eyes").removeAttr('style');
  $("#next").removeAttr('class');
  $("#next").removeAttr('style');
  $("#notifications").removeAttr('class');
  $("#notifications").removeAttr('style');
  $("#shrink").removeAttr('class');
  $("#shrink").removeAttr('style');

  $("#eyes").addClass('active eyes-state-0'+n);
  $("#next").addClass('not-active eyes-state-01');
  $("#notifications").addClass('not-active eyes-state-08');
  $("#shrink").addClass('not-active');

 }
    function shrink(){

 // $.setDisabledByDefault(true);
   var $active = $( "#eyes" );
   var $next  = $("#shrink");

   
   $active.removeClass('eyes-state-01').addClass('eyes-state-09');
   $next.removeClass('not-active').addClass('active');

   $next.transition({ scale:1.2, delay: 200});
   $next.transition({ scale:0.5});
   }

   function transition( $active, $next){

     $next.fadeTo(2000, 0.25);
     $active.fadeTo(2000, 1);
     $next.fadeOut();

    }

    function animationInit(){
        $("#eyes").removeAttr('class');
        $("#eyes").removeAttr('style');
        $("#next").removeAttr('class');
        $("#next").removeAttr('style');
        $("#notifications").removeAttr('class');
        $("#notifications").removeAttr('style');
        $("#shrink").removeAttr('class');
        $("#shrink").removeAttr('style');
        
        $("#eyes").addClass('active eyes-state-00');
        $("#next").addClass('not-active eyes-state-01');
        $("#notifications").addClass('not-active eyes-state-08');
        $("#shrink").addClass('not-active');
    }


function loop(firstFrame,lastFrame){
  var $active = $( "#eyes" );
  var i=firstFrame;var j=0;
  var timerId = setInterval(function(){
    j=i; j++;
   if( i != lastFrame+1){
      $( "#eyes" ).removeClass("eyes-state-0"+i);
      $( "#eyes" ).addClass("eyes-state-0"+j);
      i++;

   }else{
       
         clearInterval(timerId);
    }

    },300);
}
   
//------------------
function showMic(){

var $eyes = $( "#eyes" );
$eyes.removeClass("eyes-state-00").addClass("eyes-state-01");

 var $active = $eyes;
 var i=1;var j=0;
// starting the mic animation 
 timerId = setInterval(function(){
  j=i; j++;
    if( i != 7){
 $eyes.removeClass("eyes-state-0"+i);
 $eyes.addClass("eyes-state-0"+j);
i++;

 }else{
    
    
    $eyes.removeClass("eyes-state-0"+i);
    $eyes.addClass("eyes-state-01");
    i=1; 
  // clearInterval(timerId);

  }

},300);

}


function hideMic(){

  if(timerId != null){
   clearInterval(timerId);  // we stop the mic animation 
    var currentClass = $( "#eyes" ).attr('class');
   $( "#eyes" ).removeClass(currentClass).addClass('eyes-state-00');
   timerId = null;
 }
}
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

function show(mMode){
    
   // selectedItem = $("[data-wow-index ='"+ index+"'] a").trigger("click");
   selectedItem = $("[data-wow-index ='"+ index+"'] a");
   var url = selectedItem.attr("href");
   console.log('show --- url: '+url);
   if(mMode == 'video')
   {
   try{  
     megaMipJSInterface.onLaunchVideo(url);
      }catch(error){
        console.log("show() -- bloc catch err: "+error);
      }
    }else{
        selectedItem.trigger("click");
    }
    
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
   $.fancybox.close()    
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

function log(displayString){
    hideEyes();showCenterPanel();
    $('#output').empty();
    $('#output').append(displayString);
    
}




//--------------------------------------------


function pictureSearch(keyword){
    
   
  
  var apiURL = 'http://ajax.googleapis.com/ajax/services/search/'+"images"+
                        '?v=1.0&callback=?';
 var params = {
                q : keyword,
                rsz :8,
                start : 0
              };
 $.ajax({
        url:apiURL,
        type:"GET",
        dataType : "JSON",
        data: params
       , success: function(json){

            var content = "";
            var newWidth = 0;
            animationInit();
            clearScreen();
            hideEyes();
            showCenterPanel();

      
          
            for(var i = 0; i < json.responseData.results.length; i++){
                           
                 content = "<div class='pic not-selected' data-wow-index='"+i+"'  >"+
                                          "<a href='"+json.responseData.results[i].url+"'>"+
                                          "<img src='"+json.responseData.results[i].tbUrl + "'/>"+
                                          "</a>"+
                                          "<p>"+json.responseData.results[i].visibleUrl+"</p><br/>"+
                           "<span style='font-weight:bold;' >Size: </span>"+json.responseData.results[i].width+"x"+json.responseData.results[i].height+
                                          "</div>";
                                  if(i == 3){
                                      content +="<br/>"
                                  }
                                      
                      $('#results').append(content);
               } 
                        
                         //-- select the first item
                          var s =  $('.pic').first()
                                  .removeClass("not-selected")
                                  .addClass("selected");
                          
                          //--FancyBox --
                               $("a").fancybox({
                                  'overlayShow' : false,
                                  'transitionIn'  : 'elastic',
                                  'transitionOut' : 'elastic',
                                  'closeClick':true, 
                                  'closeBtn':false,
                                  padding:0
                              });

                       
     
   
    }, 
    error: function(xhr, textStatus, errorThrown){
      window.setTimeout($.ajax(this, 1000));
    }
  });
 
}
// video search functions  -------------------------------


function videoSearch(keyword){
    
   
  
 var apiURL = 'https://gdata.youtube.com/feeds/api/videos?q='+keyword+"&max-results=8&callback=?&alt=json&format=1";

  $.getJSON(apiURL,
    function(data){


      animationInit();
      clearScreen();
      hideEyes();
      showCenterPanel();
                var date;    
    var content = "";
                var month, day, year;
      for(var i = 0; i < data.feed.entry.length; i++){
                        date = new Date(data.feed.entry[i].published.$t); 
                        month = date.getMonth()+1;
                        day = date.getDate();
                        year = date.getFullYear();
      content = "<div class='pic not-selected' data-wow-index='"+i+"' >"+
                                  "<a class='fancybox fancybox.iframe' href='"+data.feed.entry[i].media$group.media$content[0].url+"'>"+
                                  "<img src='"+data.feed.entry[i].media$group.media$thumbnail[2].url + "'/>"+"</a><br/><br/>"+
                                    "<p>Published: "+day+"-"+month+"-"+year+"</p><br/></div>";
                                  ;
                               //   "<p>Published: "+day+"-"+month+"-"+year+"</p><br/>"+
                                
                          
        $('#results').append(content);
      } 
                        
                         //-- select the first item
                          var s =  $('.pic').first()
                                  .removeClass("not-selected")
                                  .addClass("selected");
                          
                          //--FancyBox --
                                $("a").fancybox({
                                'closeClick':true,    
                                'closeBtn':false,
                                'padding':0,
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
  console.log("onPlayerReady ---");
    event.target.playVideo();
}

function onPlayerStateChange(event){
    
    console.log("onPlayerStateChange  ---");
    if(event.data === 0){
        $.fancybox.next();
    }
}


function onYouTubePlayerAPIReady(){
    
    console.log("onYouTubePlayerAPIReady ---");
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


///---------- tests -- module clavier 


function handleKeyboard(evt){

	console.info('key');


	try{  
     megaMipJSInterface.onKeyBoard(evt.keyCode);
      }catch(error){
        console.log("bloc catch err: "+error);
      }
 
}

document.onkeyup = handleKeyboard;
