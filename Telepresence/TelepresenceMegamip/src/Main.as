package  {
	import com.bit101.components.CheckBox;
	import com.bit101.components.Panel;
	import com.bit101.components.PushButton;
	import com.soma.ui.vo.*;
	import com.soma.ui.*;
	import com.soma.ui.layouts.*;
	import flash.text.TextFormat;
	import flash.net.*;
    
	import org.osmf.media.MediaElement;
	import org.osmf.net.StreamingURLResource;
	import org.osmf.net.StreamType;
	import org.osmf.containers.MediaContainer;
	import org.osmf.elements.VideoElement;
	import org.osmf.media.MediaPlayer;
	import org.osmf.media.URLResource;
	import org.osmf.media.MediaPlayerSprite;

	
	import flash.events.MouseEvent;
	import flash.net.NetConnection;
	import flash.desktop.NativeApplication;
	import flash.events.Event;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.media.H264VideoStreamSettings;
	import flash.media.H264Profile;
	import flash.media.H264Level;
	import flash.ui.Multitouch;
	import flash.ui.MultitouchInputMode;
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFieldType;
	import flash.net.NetConnection;
    import flash.events.NetStatusEvent;
    import flash.events.ActivityEvent;
	import flash.events.MouseEvent;
	import flash.events.InvokeEvent;
	import flash.net.NetStream;
    import flash.media.Video;
    import flash.media.Microphone;
    import flash.media.Camera;
	import flash.media.CameraPosition;
	import flash.display.StageDisplayState;
	import flash.geom.Rectangle; 
	
	
	public class Main extends Sprite {

	// consts 
	private const SERVER_ADRESS:String = "rtmfp://p2p.rtmfp.net";
	private const DEVELOPER_KEY:String = "ab4cf6a661db0fc2f51f582d-6aeff8c9e9ef";
	private const platform:String = "tablet";
		
		
	// members 
	    private var txtFingerPrint:TextField;
	    private var btnConnect:PushButton;
		private var btnPublish:PushButton;
		private var btnRead:PushButton;
		private var nc:NetConnection;
		private var nsRead:NetStream;
		private var nsPublish:NetStream;
        private var videoPublish:Video;
		private var videoRead:Video;
        private var camera:Camera;
        private var mic:Microphone;
		private var videoElementPublish:VideoElement;
		private var videoElementRead:VideoElement;
		private var farPeerID:String;
		private var myPeerID:String;
		
		private var megamipLSClient:MegamipLSClient;
		public static var externalArgs:String = "";
		
	
		
		public function Main() {
		   
			
			// addEventListener(Event.ENTER_FRAME, initHandler);
			//addEventListener(InvokeEvent, onInvoke);
			NativeApplication.nativeApplication.addEventListener(
					InvokeEvent.INVOKE, onInvoke);
			initGui();
			initListeners();
			//----------- tests 
			
		//	txtFingerPrint.text = "publisherID"+NativeApplication.nativeApplication.publisherID;
		}
		//-------------------------------------------------------------------------------------------------------
		
		
		private function initHandler(evt:Event):void {
			
			NativeApplication.nativeApplication.addEventListener(
					InvokeEvent.INVOKE, onInvoke);
		}
		
		private function onInvoke(event:InvokeEvent):void
		{
			 externalArgs =  event.arguments[0] + "\n" + event.arguments[1];
			  var fingerPrint:String = event.arguments[0];
			  fingerPrint = fingerPrint.substr(fingerPrint.indexOf("//") + 2);
			  txtFingerPrint.text = externalArgs + "\n" +fingerPrint;
			  farPeerID = fingerPrint;
			  
			 
			initConnection(null);
			
			 megamipLSClient = new MegamipLSClient();
			 megamipLSClient.addEventListener(MegamipLSClient.UPDATE, lsReceiveData);
			  
		}
		
		private function onInvoke2(event:MouseEvent):void
		{
			 
			  farPeerID =   txtFingerPrint.text
			  initConnection(null);
			  megamipLSClient = new MegamipLSClient();
			  megamipLSClient.addEventListener(MegamipLSClient.UPDATE, lsReceiveData);
			  
		}
		private function initSendStream(event:MouseEvent):void{
		
			
		trace("initSendStream");

		nsPublish = new NetStream(nc, NetStream.DIRECT_CONNECTIONS);
		nsPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);
		//nsPublish.publish("media");



     // tricky !! 

			var sendStreamClient:Object = new Object();

			sendStreamClient.onPeerConnect = function(callerns:NetStream):Boolean{
		  
				farPeerID= callerns.farID;

				trace(" farPeerID"+farPeerID)
				return true;
			}
		 
		  nsPublish.client = sendStreamClient;
		  publishLiveStream();

		}

		private function initRecvStream(event:MouseEvent):void{

			nsRead = new NetStream(nc, farPeerID);
			nsRead.bufferTime = 3;
			nsRead.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);
			//nsRead.play("media");
			nsRead.client = this;
			readLiveStream();
		}


		public function receiveSomeData(str:String):void{

		   // we use str !!

		}

		private function sendSomeData():void{

			nsPublish.send("receiveSomeData", "texte a envoyer");
		}



		private function netStatusHandler(event:NetStatusEvent):void{
			
			trace(event.info.code);
		}
		
		/*
		 * 
		 * */
		 private function ncStatusHandler(event:NetStatusEvent):void{
			trace(event.info.code);
			myPeerID = nc.nearID;

			txtFingerPrint.text = myPeerID;
		}
		
		
		//------------------------------------------------------------------------------------------------------

		/*
		 * 
		 * Connecting to Cirrus
		 * */
		
		 private function initConnection(event:MouseEvent):void{


			// recuperation du fingerPrint 
	//		var txtFP:String = txtFingerPrint.text;
		//	farPeerID = txtFP;
			nc = new NetConnection();
			nc.addEventListener(NetStatusEvent.NET_STATUS, netStatusReadhHandler);
			nc.connect(SERVER_ADRESS,DEVELOPER_KEY);

		}
		
		
				/*
		 *  Connect and start publishing the live stream
		 */
		private function publishHandler(event:MouseEvent):void {
			trace("Okay, let's connect now");
			
			nc = new NetConnection();
            nc.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
            nc.connect("rtmp://dL6fny.cloud.influxis.com/Telepresence1/");
			trace("Okay, let's connect now");
		}
		
		/*
		 * read the live stream
		 * */
		private function readHandler(event:MouseEvent):void {
			trace("Okay, start reading the live stream now");
			
			nc = new NetConnection();
            nc.addEventListener(NetStatusEvent.NET_STATUS, netStatusReadhHandler);
            nc.connect("rtmp://dL6fny.cloud.influxis.com/Telepresence1/");
		
		}
		
		
		
	
		
		/*
		 * 
		 * 
		 * */
        private function netStatusPublishHandler(event:NetStatusEvent):void
        {
            trace("connected is: " + nc.connected );
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
               // 	metaData.width = 400;
                //	metaData.height = 200;
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
            trace("connected is: " + nc.connected );
			trace("event.info.level: " + event.info.level);
			trace("event.info.code: " + event.info.code);
			
            switch (event.info.code)
            {
                case "NetConnection.Connect.Success":
	                trace("Congratulations! you're connected");
					 initRecvStream(null);
				   //readLiveStream();
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
				
				case "NetStream.Connect.Closed":
					deactivate(null);
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
		  /*  nsPublish = new NetStream(nc, NetStream.CONNECT_TO_FMS);
		    nsPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
			nsPublish.client = new CustomClient();*/
		    
		    camera = getCamera();
		    mic = Microphone.getMicrophone();
		    
		    if (camera != null){
				camera.setMode(480, 360, 30, true);
				camera.setQuality(0, 90);
				camera.setKeyFrameInterval(1);
				camera.addEventListener(ActivityEvent.ACTIVITY, activityHandler);
			    
			/*	videoPublish = new Video();
				videoPublish.height = 120;
				videoPublish.width = 120;
				videoPublish.x = 370;
				videoPublish.y = 15;
				videoPublish.attachCamera(camera);
			*/	
				// display the publishing stream 
				
				//videoElementPublish.resource = resource;
				videoPublish.attachCamera(camera);
				
				nsPublish.attachCamera(camera);
				nsPublish.bufferTime = 3;
			/*	var settings:H264VideoStreamSettings = new H264VideoStreamSettings();
			    settings.setProfileLevel(H264Profile.BASELINE, H264Level.LEVEL_4);*/
			//	nsPublish.videoStreamSettings = settings ;
                
            //   addChild(videoPublish);
			}
			
			if (mic != null) {
				mic.addEventListener(ActivityEvent.ACTIVITY, activityHandler);
								
			    nsPublish.attachAudio(mic);
			}
			
			if (camera != null || mic != null){
				// start publishing
			    // triggers NetStream.Publish.Start
			    nsPublish.publish("media", "live");
				
				 var metaData:Object = new Object();
           // metaData.codec = nsPublish.videoStreamSettings.codec;
           // metaData.profile =  settings.profile;
           //metaData.level = settings.level;
            metaData.fps = camera.fps;
            metaData.bandwith = camera.bandwidth;
            metaData.height = camera.height;
            metaData.width = camera.width;
            metaData.keyFrameInterval = camera.keyFrameInterval;
            metaData.copyright = "WowWee Canada - Megamip";
            nsPublish.send("@setDataFrame", "onMetaData", metaData);
			
			
		    } else {
			    trace("Please check your camera and microphone");
		    }
	    }  
				
		/*
		 * 
		 * */
		
		private function readLiveStream():void {
			
			
			
		//	var mediaPlayerSprite:MediaPlayerSprite = new MediaPlayerSprite();
		//	var videoElement:VideoElement = new VideoElement();

			//videoElementRead.resource = new StreamingURLResource("rtmp://dL6fny.cloud.influxis.com/Telepresence1/_definst_/user01", StreamType.LIVE);
		//videoElementRead.resource =  nsRead.re;
		
		
		videoRead.attachNetStream(nsRead);
		nsRead.play("media");
		/*	mediaPlayerSprite.media = videoElement;
			mediaPlayerSprite.width = 640; 
            mediaPlayerSprite.height = 360; 
			addChild(mediaPlayerSprite);
			*/
			
		// we start publishing ... 	
		  initSendStream(null); 
		 }
			
		 
		 /*
	 * 
	 * 
	 * */	
		private function deactivate(e:Event):void 
		{
			// make sure the app behaves well (or exits) when in background
			NativeApplication.nativeApplication.exit();
		}
	
			
		/*
		 * 
		 * */	
		private function getCamera():Camera {
			
			
			var returnValue:Camera;
		if (platform != "desktop") {
			
			for (var i:int = 0; i < 2; i++ ) {
				var cam:Camera = Camera.getCamera(String(i));
				if (cam.position == CameraPosition.FRONT || cam.position == CameraPosition.UNKNOWN) {
					returnValue = cam;	
				}
			}	
		}else { 
			
			returnValue = Camera.getCamera();
		}
		
		 return returnValue;
		}
		
		private function initListeners():void {
			
			btnConnect.addEventListener(MouseEvent.CLICK, onInvoke2);
			btnPublish.addEventListener(MouseEvent.CLICK, initSendStream);	
		    btnRead.addEventListener(MouseEvent.CLICK, initRecvStream);
		}
		
		private function initGui():void {
			
		
			this.stage.align = StageAlign.TOP_LEFT;
			this.stage.scaleMode = StageScaleMode.NO_SCALE;
			this.stage.displayState = StageDisplayState.FULL_SCREEN_INTERACTIVE; 
			
			var container1:MediaContainer = new MediaContainer();
			var container2:MediaContainer = new MediaContainer();
			var player1:MediaPlayerSprite = new MediaPlayerSprite();
			var player2:MediaPlayerSprite = new MediaPlayerSprite();
			
			
			
		
			
			
			
			
			var baseUI:BaseUI = new BaseUI(stage);
			
			
		 //-------------- videos display 
		 
		   videoPublish = new Video();
			videoPublish.scaleX = 0.4;
			videoPublish.scaleY = 0.4;
			
		  videoRead = new Video();
		  videoRead.scaleX = 3;
		  videoRead.scaleY = 3;
	//	  var screenRectangle:Rectangle = new Rectangle(videoRead.x, videoRead.y, videoRead.width, videoRead.height); 
	//	  stage.fullScreenSourceRect = screenRectangle; 
		   
			//me1.resource = resource;
		//	me2.resource = resource;
			
		//	player1.media = me1;
		//	player2.media = me2;
		/*	container1.addMediaElement(me1);
			container1.width = 210;
			container1.height = 210;
		*/
			/*videoElementRead = new VideoElement();
			videoElementRead.resource = resource;
			player2.media = videoElementRead;
			container2.addMediaElement(videoElementRead);
			
			
			container2.width = stage.stageWidth;
			container2.height = stage.stageHeight;*/
			
			
		/*   var element3:ElementUI = baseUI.add(videoPublish); // positioning of the small screen 
		   element3.top = 10;
		   element3.right = 3;
		
		   */
		  
		//------ -----------  panels	
	
	
		var bottomPanel:Panel = new Panel(this);
		bottomPanel.setSize(1100, 150);
	    var element5:ElementUI = baseUI.add(bottomPanel);
		element5.right = 3;
		element5.bottom = 3;

		//------------connection buttons  
		
		btnConnect = new PushButton(bottomPanel, 20, 60);
		 btnConnect.label = "Connect";
		 btnConnect.width = 100;
		
		btnPublish = new PushButton(bottomPanel, 20, 60);
		 btnPublish.label = "Publish";
		 btnPublish.width = 100;
		 
		btnRead = new PushButton(bottomPanel, 20, 60);
		 btnRead.label = "Read";
		 btnRead.width = 100;
		
		txtFingerPrint = new TextField();
		txtFingerPrint.type = TextFieldType.INPUT;
		txtFingerPrint.width = 500;
		txtFingerPrint.height = 50;
		txtFingerPrint.multiline = false;
		txtFingerPrint.background = true;
		
		
		var txtFormat:TextFormat = new TextFormat();
		txtFormat.font = "Verdana";
		txtFingerPrint.defaultTextFormat = txtFormat;
		txtFingerPrint.backgroundColor = 0xF5F5DC;
		txtFingerPrint.text = "FingerPrint: "+externalArgs;
		
		var hbox2:HBoxUI = new HBoxUI(bottomPanel, 300,35);
		hbox2.backgroundColor = 0x868686;
		hbox2.backgroundAlpha = 0.4;
		hbox2.ratio = ElementUI.RATIO_IN;
		hbox2.childrenGap = new GapUI(5, 5);
		hbox2.childrenPadding = new PaddingUI(15,15, 15, 15);
		hbox2.childrenAlign = HBoxUI.ALIGN_CENTER_LEFT;
		hbox2.addChild(btnConnect);
		hbox2.addChild(btnPublish);
		hbox2.addChild(btnRead);
		hbox2.addChild(txtFingerPrint);
		
		hbox2.addChild(videoPublish);
		hbox2.refresh();
	

	//    addChild(container2);
        addChild(videoRead); 
		addChild(hbox2);	
		
		}
		
			//------------------------ LightStreamer communication methods ( remote control)
	
		public function lsReceiveData(event:Event):void{
        
			var message:String = megamipLSClient.getMessage();
			 trace("lsReceiveData event:" + event);
			 trace("lsReceiveData message:" +message);
			 sendMegamipCMD(message);
		    

		}
		private function sendMegamipCMD(str:String):void {
			
		var url:String = "http://localhost:8080/"+str;
		var request:URLRequest = new URLRequest(url);
		request.method = URLRequestMethod.GET;

		var variables:URLVariables = new URLVariables();
		variables.name = "cmd_params";
		request.data = variables;

		var loader:URLLoader = new URLLoader();
		loader.addEventListener(Event.COMPLETE, onSendMegamipCallback);
		loader.dataFormat = URLLoaderDataFormat.TEXT;
		loader.load(request);

			
		}
		
		function onSendMegamipCallback (event:Event):void {
		trace(event.target.data);
		}
	}
}


class CustomClient {
		public function onMetaData(info:Object):void {
			trace("width: " + info.width);
			trace("height: " + info.height);
		}
	}