package 
{
	import flash.desktop.NativeApplication;
	import flash.events.Event;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.ui.Multitouch;
	import flash.ui.MultitouchInputMode;
	
	
	import flash.display.Sprite;
	import flash.text.TextField;
	
    import flash.net.NetConnection;
    import flash.events.NetStatusEvent;
    import flash.events.ActivityEvent;
	import flash.events.MouseEvent;
    
    import flash.net.NetStream;
    import flash.media.Video;
    import flash.media.Microphone;
    import flash.media.Camera;
	
	


    
	
	/**
	 * ...
	 * @author bens
	 * 
	 */
	
	
	//public class Main extends InfluxisVidCollaboratorAdmin 
	public class Main extends Sprite
	{
		
		
			
			
		var ncPublish:NetConnection;
		var ncRead:NetConnection;
        var nsRead:NetStream;
		var nsPublish:NetStream;
        var videoPublish:Video;
		var videoRead:Video;
        var camera:Camera;
        var mic:Microphone;
        private var publishBtn:Sprite = new Sprite();
		private var readBtn:Sprite = new Sprite();
	    private var clearBtn:Sprite = new Sprite();
		private var stopBtn:Sprite = new Sprite();
		
	
	
		
        public function Main()
        {
			
			
			
			
			
			
			
			//----------------------
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
			stage.addEventListener(Event.DEACTIVATE, deactivate);
			
			// touch or gesture?
			Multitouch.inputMode = MultitouchInputMode.TOUCH_POINT;
			
			initGui();
			publishBtn.addEventListener(MouseEvent.CLICK, publishHandler);
			readBtn.addEventListener(MouseEvent.CLICK, readHandler);
			clearBtn.addEventListener(MouseEvent.CLICK, clearHandler);
			stopBtn.addEventListener(MouseEvent.CLICK, stopHandler);
			readBtn.addEventListener(MouseEvent.CLICK, stopHandler);
			
			//---------------------------
			
	
			
        }
		
		private function initGui():void {
			
			var textLabel1:TextField = new TextField();
              publishBtn.graphics.clear();
              publishBtn.graphics.beginFill(0xD4D4D4); // grey color
              publishBtn.graphics.drawRoundRect(90, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
              publishBtn.graphics.endFill();
              textLabel1.text = "Publish";
              textLabel1.x = 110;
              textLabel1.y = 303;
              textLabel1.selectable = false;
              publishBtn.addChild(textLabel1);
			  
			 //--------------------------------------
			 
			 var textLabel4:TextField = new TextField();
              readBtn.graphics.clear();
              readBtn.graphics.beginFill(0xD4D4D4); // grey color
              readBtn.graphics.drawRoundRect(180, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
              readBtn.graphics.endFill();
              textLabel4.text = "Read";
              textLabel4.x = 200;
              textLabel4.y = 303;
              textLabel4.selectable = false;
              readBtn.addChild(textLabel4);
			  
			//  -------------------------------------------
			var textLabel2:TextField = new TextField();
              stopBtn.graphics.clear();
              stopBtn.graphics.beginFill(0xD4D4D4); // grey color
              stopBtn.graphics.drawRoundRect(270, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
              stopBtn.graphics.endFill();
              textLabel2.text = "Stop";
              textLabel2.x = 290;
              textLabel2.y = 303;
              textLabel2.selectable = false;
              stopBtn.addChild(textLabel2);
			  
			  //--------------------------------------------
			  
			  var textLabel3:TextField = new TextField();
              clearBtn.graphics.clear();
              clearBtn.graphics.beginFill(0xD4D4D4); // grey color
              clearBtn.graphics.drawRoundRect(360, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
              clearBtn.graphics.endFill();
              textLabel3.text = "Clear";
              textLabel3.x = 380;
              textLabel3.y = 303;
              textLabel3.selectable = false;
              clearBtn.addChild(textLabel3);
			
			
			this.addChild(publishBtn);
			this.addChild(stopBtn);
			this.addChild(clearBtn);
			this.addChild(readBtn);
			
			
			}
		/*
		 *  Connect and start publishing the live stream
		 */
		private function publishHandler(event:MouseEvent):void {
			trace("Okay, let's connect now");
			
			ncPublish = new NetConnection();
            ncPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
            ncPublish.connect("rtmp://dL6fny.cloud.influxis.com/Telepresence1/");
			trace("Okay, let's connect now");
		}
		
		/*
		 * read the live stream
		 * */
		private function readHandler(event:MouseEvent):void {
			trace("Okay, start reading the live stream now");
			
			ncRead = new NetConnection();
            ncRead.addEventListener(NetStatusEvent.NET_STATUS, netStatusReadhHandler);
            ncRead.connect("rtmp://dL6fny.cloud.influxis.com/Telepresence1/");
			
			
		
		}
		
		/*
		 *  Disconnect from the server
		 */
		private function stopHandler(event:MouseEvent):void {
			trace("Now we're disconnecting");
			ncPublish.close();
			
		}
		
		/*
		 *  Clear the MetaData associated with the stream
		 */
		private function clearHandler(event:MouseEvent):void {
			if (nsPublish){
				trace("Clearing MetaData");
				nsPublish.send("@clearDataFrame", "onMetaData");
			}
		}
        
        
		
		/*
		 * 
		 * 
		 * */
        private function netStatusPublishHandler(event:NetStatusEvent):void
        {
            trace("connected is: " + ncPublish.connected );
			trace("event.info.level: " + event.info.level);
			trace("event.info.code: " + event.info.code);
			
            switch (event.info.code)
            {
                case "NetConnection.Connect.Success":
	                trace("Congratulations! you're connected");
	                publishLiveStream();
	                break;
                case "NetConnection.Connect.Rejected":
	                trace ("Oops! the connection was rejected");
	                break;
	            case "NetStream.Play.Stop":
	                trace("The stream has finished playing");
	                break;
	            case "NetStream.Play.StreamNotFound":
	                trace("The server could not find the stream you specified"); 
	                break;
	            case "NetStream.Publish.Start":
				
	                trace("Adding metadata to the stream");
	                // when publishing starts, add the metadata to the stream
                	var metaData:Object = new Object();
                	metaData.title = "myStream";
                	metaData.width = 400;
                	metaData.height = 200;
                	nsPublish.send("@setDataFrame", "onMetaData", metaData);
	                break;
					
	            case "NetStream.Publish.BadName":
	                trace("The stream name is already used");
	                break;
	        }
        }
        
        /*
		 * 
		 * 
		 * */
		
		    private function netStatusReadhHandler(event:NetStatusEvent):void
        {
            trace("connected is: " + ncPublish.connected );
			trace("event.info.level: " + event.info.level);
			trace("event.info.code: " + event.info.code);
			
            switch (event.info.code)
            {
                case "NetConnection.Connect.Success":
	                trace("Congratulations! you're connected");
	                readLiveStream();
	                break;
                case "NetConnection.Connect.Rejected":
	                trace ("Oops! the connection was rejected");
	                break;
	            case "NetStream.Play.Stop":
	                trace("The stream has finished playing");
	                break;
	            case "NetStream.Play.StreamNotFound":
	                trace("The server could not find the stream you specified"); 
	                break;
	            case "NetStream.Play.Start":
				
	                trace("reading stream ...");
	               
	                break;
					
	            case "NetStream.Publish.BadName":
	                trace("The stream name is already used");
	                break;
	        }
        }
		
		/*
		 * 
		 * 
		 * */
		
		
		
       	private function activityHandler(event:ActivityEvent):void {
		    trace("activityHandler: " + event);
		    trace("activating: " + event.activating);
	    } 
        
		/*
		 *  Create a live stream, attach the camera and microphone, and
		 *  publish it to the local server
		 */
        private function publishLiveStream():void {
		    nsPublish = new NetStream(ncPublish, NetStream.CONNECT_TO_FMS);
		    nsPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
			nsPublish.client = new CustomClient();
		
		    
		    camera = Camera.getCamera();
		    mic = Microphone.getMicrophone();
		    
		    if (camera != null){
				
				camera.addEventListener(ActivityEvent.ACTIVITY, activityHandler);
			    
				videoPublish = new Video();
				videoPublish.height = 120;
				videoPublish.width = 120;
				videoPublish.x = 370;
				videoPublish.y = 15;
				videoPublish.attachCamera(camera);
				                
				nsPublish.attachCamera(camera);
                
                addChild(videoPublish);
			}
			
			if (mic != null) {
				mic.addEventListener(ActivityEvent.ACTIVITY, activityHandler);
								
			    nsPublish.attachAudio(mic);
			}
			
			if (camera != null || mic != null){
				// start publishing
			    // triggers NetStream.Publish.Start
			    nsPublish.publish("user01", "live");
		    } else {
			    trace("Please check your camera and microphone");
		    }
	    }  
				
		/*
		 * 
		 * */
		
		private function readLiveStream():void {
			
			nsRead = new NetStream(ncRead, NetStream.CONNECT_TO_FMS);
		    nsRead.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
			nsRead.client = new CustomClient();
		    
			videoRead = new Video();
		
			videoRead.attachNetStream(nsRead);
			this.addChild(videoRead);
		    nsRead.play("megamip01");
			
		   
		 }
			
			
		/*
		 * 
		 * 
		 * */	
		private function deactivate(e:Event):void 
		{
			// make sure the app behaves well (or exits) when in background
			//NativeApplication.nativeApplication.exit();
		}
	
		
		
	
		
	
	
    }  // end Main class
		
	
	
	
	
	
	
} // end Package

class CustomClient {
		public function onMetaData(info:Object):void {
			trace("width: " + info.width);
			trace("height: " + info.height);
		}
	}