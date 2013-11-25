package  {
	
	import com.bit101.components.CheckBox;
	import com.bit101.components.Panel;
	import com.bit101.components.PushButton;
	import com.soma.ui.vo.*;
	import com.soma.ui.*;
	import com.soma.ui.layouts.*;

	import org.osmf.media.MediaElement;
	import org.osmf.net.StreamingURLResource;
	import org.osmf.net.StreamType;
	import org.osmf.containers.MediaContainer;
	import org.osmf.elements.VideoElement;
	import org.osmf.media.MediaPlayer;
	import org.osmf.media.URLResource;
	import org.osmf.media.MediaPlayerSprite;

	import flash.events.TouchEvent;
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
	import flash.text.TextFormat;
	import flash.text.TextFieldType;
	import flash.net.NetConnection;
    import flash.events.NetStatusEvent;
    import flash.events.ActivityEvent;
	import flash.events.MouseEvent;
	import flash.net.NetStream;
    import flash.media.Video;
    import flash.media.Microphone;
    import flash.media.Camera;
	import flash.media.CameraPosition;
	

	
	
	public class Main extends Sprite {

	// consts 
	private const SERVER_ADRESS:String = "rtmfp://p2p.rtmfp.net";
	private const DEVELOPER_KEY:String = "ab4cf6a661db0fc2f51f582d-6aeff8c9e9ef";
		
		// members 
		private var txtFingerPrint:TextField;
		private var btnStart:PushButton;
		private var btnForward:PushButton;
		private var btnLeft:PushButton;
		private var btnRight:PushButton;
		private var btnBack:PushButton;
		private var btnExit:PushButton;
		private var btnRead:PushButton;
		
		private var ncRead:NetConnection;
		private var ncPublish:NetConnection;
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
	
		
		public function Main() {
			
			initGui();
			initListeners();
		 
			
		}
		//---------------------------------------     P2P ---------------------------------------------------------
		
		
		private function initSendStream(event:MouseEvent):void{
			
		trace("initSendStream");
		txtFingerPrint.appendText("\n Connected !!\n Publishing ...");

		nsPublish = new NetStream(nc, NetStream.DIRECT_CONNECTIONS);
		nsPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
		nsPublish.publish("media");
        trace("initSendStream farID: " +nsPublish.farID);


     // tricky !! 

			var sendStreamClient:Object = new Object();

			sendStreamClient.onPeerConnect = function(callerns:NetStream):Boolean{
		  
				farPeerID= callerns.farID;

				trace("onPeerConnect farPeerID" + farPeerID);
				return true;
			}
		 
		  nsPublish.client = sendStreamClient;
          publishLiveStream();
		}

		private function initRecvStream(event:MouseEvent):void{

			nsRead = new NetStream(nc, farPeerID);
			nsRead.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);
			//nsRead.play("media");
			nsRead.client = this;
			
			//  +------------------### log ###-------------------+
			trace("connected to stream nc farID: " + nc.farID + " nearID: " + nc.nearID);
			trace("nsRead  farID: " + nsRead.farID);
			txtFingerPrint.appendText("\n Reading stream from Megamip ...");
		   readLiveStream();
		}


		public function receiveSomeData(str:String):void{

		   // we use str !!

		}

		private function sendSomeData():void{

			nsPublish.send("receiveSomeData", "texte a envoyer");
		}



		private function netStatusHandler(event:NetStatusEvent):void{
			
			trace("netStatusHandler event: " + event.info.code);
			/*if (event.info.code == "NetStream.Play.PublishNotify") {
				readLiveStream();
			}*/
		}
		
		//----------------------------------   P2P  --------------------------------------------------------------
		
		/*
		 * 
		 * Connecting to Cirrus
		 * */
		
		 private function initConnection(event:MouseEvent):void{


			// recuperation du fingerPrint 
		    /*var txtFP:String = txtFingerPrint.text;
			farPeerID = txtFP;
			*/
			
			//             +----------### log ###----------+ 
			txtFingerPrint.appendText( "--------------\n Connecting ...");
			
			
			nc = new NetConnection();
			nc.addEventListener(NetStatusEvent.NET_STATUS, ncStatusHandler);
			nc.connect(SERVER_ADRESS, DEVELOPER_KEY);
			
			
			

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
		 * 
		 * */
		
		 private function ncStatusHandler(event:NetStatusEvent):void{
			trace("ncStatusHandler - "+event.info.code);
			myPeerID = nc.nearID;

			//txtFingerPrint.text = myPeerID;
			// connecting to LightStreamer server and sending the p2p fingerprint
			if(event.info.code == "NetConnection.Connect.Success"){
				megamipLSClient = new MegamipLSClient();
				megamipLSClient.addEventListener(MegamipLSClient.CONNECTED, sendPeerID);
				//megamipLSClient.sendMessage("LAUNCH:"+myPeerID);
			}
		}
		
		private function sendPeerID(e:Event):void {
		 
			// we start publishing 
			initSendStream(null);
			megamipLSClient.sendMessage("LAUNCH:"+myPeerID);
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
                	//metaData.width = 400;
                	//metaData.height = 200;
                	//nsPublish.send("@setDataFrame", "onMetaData", metaData);
				//	publishLiveStream();
	                break;
					
	            case "NetStream.Publish.BadName":
	                trace("The stream name is already used");
	                break;
				case "NetStream.Play.Start":	
					 trace("netStatusPublishHandler - we start reading ");
					 initRecvStream(null);
	                 break;
	        }
        }
        
		 /*
		 * 
		 * 
		 * */
		
		    private function netStatusReadhHandler(event:NetStatusEvent):void
        {
            trace("connected is: " + ncRead.connected );
			trace("event.info.level: " + event.info.level);
			trace("event.info.code: " + event.info.code);
			
            switch (event.info.code)
            {
                case "NetConnection.Connect.Success":
	                trace("Congratulations! you're connected");
	               // readLiveStream();
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
		  		  /*  nsPublish = new NetStream(nc, NetStream.CONNECT_TO_FMS);
		    nsPublish.addEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
			nsPublish.client = new CustomClient();*/
		    
		    camera = getCamera();
		    mic = Microphone.getMicrophone();
		    
		    if (camera != null){
				//camera.setMode(1280, 720, 30, true);
				//camera.setMode(480, 360, 30, true);
				camera.setMode(960 , 540, 15, false);
				//camera.setQuality(8192, 100);
				camera.setQuality(0, 100);
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
			 //  nsPublish.videoStreamSettings = settings ;
                
               addChild(videoPublish);
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
          //  metaData.codec = nsPublish.videoStreamSettings.codec;
          // metaData.profile =  settings.profile;
          //  metaData.level = settings.level;
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

		//	videoElementRead.resource = new StreamingURLResource("rtmp://dL6fny.cloud.influxis.com/Telepresence1/_definst_/megamip01", StreamType.LIVE);
		
		/*	mediaPlayerSprite.media = videoElement;
			mediaPlayerSprite.width = 640; 
            mediaPlayerSprite.height = 360; 
			addChild(mediaPlayerSprite);
			*/
			
		   videoRead.attachNetStream(nsRead);
		    nsRead.play("media");
			//addChild(videoRead);
		 }
			
		 
		 /*
	 * 
	 * 
	 * */	
		private function deactivate(e:Event):void 
		{
			// make sure the app behaves well (or exits) when in background
			trace("deactivate() -- exiting TelepresenceUser...");
			nc.removeEventListener(NetStatusEvent.NET_STATUS, ncStatusHandler);
			nsPublish.removeEventListener(NetStatusEvent.NET_STATUS, netStatusPublishHandler);
			nsRead.removeEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);
			
			nsPublish.close();
			nsRead.close();
			nc.close();
			NativeApplication.nativeApplication.exit();
		}
	
			
		/*
		 * 
		 * */	
		private function getCamera():Camera {
			
			
			var returnValue:Camera;
		
			
			 for (var i:int = 0; i < 2; i++ ) {
				var cam:Camera = Camera.getCamera(String(i));
				if (cam.position == CameraPosition.FRONT) {
					returnValue = cam;	
				}
			}	
			
		
	//	returnValue = Camera.getCamera();	
		return returnValue;
		}
		
		private function initListeners():void {
		   btnStart.addEventListener(MouseEvent.CLICK, initConnection);
		   btnExit.addEventListener(MouseEvent.CLICK, deactivate);	
		   // btnRead.addEventListener(MouseEvent.CLICK, initRecvStream);
		   btnForward.addEventListener(MouseEvent.CLICK, moveForward);
		  // btnForward.addEventListener(MouseEvent.MOUSE_DOWN, moveForward);
		   btnBack.addEventListener(MouseEvent.CLICK, moveBackward);
		   btnLeft.addEventListener(MouseEvent.CLICK, moveLeft);
		   btnRight.addEventListener(MouseEvent.CLICK, moveRight);
		}
		private function initGui():void {
			
			this.stage.align = StageAlign.TOP_LEFT;
			this.stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.addEventListener(Event.DEACTIVATE, deactivate);
			var container1:MediaContainer = new MediaContainer();
			var container2:MediaContainer = new MediaContainer();
			var player1:MediaPlayer = new MediaPlayer();
			var player2:MediaPlayerSprite = new MediaPlayerSprite();
			
			
			
			var resource:URLResource = new URLResource("http://mediapm.edgesuite.net/strobe/content/test/AFaerysTale_sylviaApostol_640_500_short.flv");
		    videoElementPublish = new VideoElement();
			videoElementRead = new VideoElement();
			
			
			
			
			
			var baseUI:BaseUI = new BaseUI(stage);
			
			
		 //-------------- videos display 
		//	videoElementPublish.resource = resource;
			videoPublish = new Video();
			videoPublish.scaleX = 0.5;
			videoPublish.scaleY = 0.5;
		//	videoPublish.height = 120;
		//	videoPublish.width = 120;
	
		videoRead = new Video();
		videoRead.scaleX = 1.5;
		videoRead.scaleY = 1.5;
			
		//	videoElementRead.resource = resource;
			
			//player1.media = videoPublish;    //-----------------
			
		//	player2.media = videoElementRead;
			//container1.addMediaElement(videoPublish);
			//container1.width = 210;
			//container1.height = 210;
	//		container2.addMediaElement(videoElementRead);
			
			
			container2.width = 1100;
			container2.height = 600;
			
			
		   var element3:ElementUI = baseUI.add(videoPublish); // positioning of the small screen 
		   element3.top = 30;
		   element3.right = 10;
		
		   
		  
		//------ -----------  panels	
		var rightPanel:Panel = new Panel(this);
		rightPanel.setSize(250, 700);
		var element2:ElementUI = baseUI.add(rightPanel);
		element2.right = 3;
		element2.bottom = 3;
		
		
		var bottomPanel:Panel = new Panel(this);
		bottomPanel.setSize(1100, 150);
	    var element5:ElementUI = baseUI.add(bottomPanel);
		element5.right = 3;
		element5.bottom = 3;
		
		
		
		//------------  remote control buttons 
		
	
		 btnForward = new PushButton(rightPanel, 20, 60);
		 btnForward.label = "Forward";
		 btnForward.width = 70;
		 btnForward.height = 35;
		 
		 btnLeft = new PushButton(rightPanel, 20, 60);
		 btnLeft.label = "Left";
		 btnLeft.width = 70;
		 btnLeft.height = 35;
		 
		 btnRight  = new PushButton(rightPanel, 20, 60);
		 btnRight.label = "Right";
		 btnRight.width = 70;
		 btnRight.height = 35;
		 
		 btnBack  = new PushButton(rightPanel, 20, 60);
		 btnBack.label = "Back";
		 btnBack.width = 70;
		 btnBack.height = 35;
		 
		 var vbox:VBoxUI= new VBoxUI(rightPanel, 120,330);
		vbox.backgroundColor = 0x133300;
		vbox.backgroundAlpha = 0.4;
		vbox.ratio = ElementUI.RATIO_IN;
		vbox.childrenGap = new GapUI(5, 5);
		vbox.childrenPadding = new PaddingUI(5, 5, 5, 10);
		vbox.childrenAlign = VBoxUI.ALIGN_BOTTOM_CENTER;
	
		
		
		var hbox1:HBoxUI = new HBoxUI(vbox, 150,40);
		hbox1.backgroundColor = 0x8FF30B;
		hbox1.backgroundAlpha = 0.4;
	   //box.ratio = ElementUI.RATIO_IN;
		hbox1.childrenGap = new GapUI(5, 5);
		hbox1.childrenPadding = new PaddingUI(2, 2, 2, 2);
		hbox1.addChild(btnLeft);
		hbox1.addChild(btnRight);
		vbox.addChild(btnBack);
	    vbox.addChild(hbox1);
		vbox.addChild(btnForward);
		
		
		//------------connection buttons  
		
		 btnStart = new PushButton(bottomPanel, 20, 60);
		 btnStart.label = "Start";
		 btnStart.width = 100;
		 btnStart.height = 35;
		 
		 
		
		 btnExit = new PushButton(bottomPanel, 20, 60);
		 btnExit.label = "Exit";
		 btnExit.width = 100;
		 btnExit.height = 35;
		 
		 btnRead  = new PushButton(bottomPanel, 20, 60);
		 btnRead.label = "--";
		 btnRead.width = 100;
		 btnRead.height = 35;
		 
		
		var txtFormat:TextFormat = new TextFormat();
		txtFingerPrint = new TextField();
		txtFingerPrint.type = TextFieldType.INPUT;
		txtFingerPrint.width = 300;
		txtFingerPrint.height = 50;
		txtFingerPrint.multiline = false;
		txtFingerPrint.background = true;
		txtFormat.font = "Verdana";
		txtFingerPrint.defaultTextFormat = txtFormat;
		txtFingerPrint.backgroundColor = 0xF5F5DC;
		txtFingerPrint.text = "FingerPrint ...";
		
		var hbox2:HBoxUI = new HBoxUI(bottomPanel, 300,35);
		hbox2.backgroundColor = 0x868686;
		hbox2.backgroundAlpha = 0.4;
		hbox2.ratio = ElementUI.RATIO_IN;
		hbox2.childrenGap = new GapUI(5, 5);
		hbox2.childrenPadding = new PaddingUI(15,15, 15, 15);
		hbox2.childrenAlign = HBoxUI.ALIGN_CENTER_LEFT;
		hbox2.addChild(btnStart);
		hbox2.addChild(btnExit);
		hbox2.addChild(btnRead);
		hbox2.addChild(txtFingerPrint);
		hbox2.refresh();
	
		addChild(videoRead); 
	    addChild(hbox2);
	    addChild(vbox);
		addChild(container1);
	    addChild(container2);
	
		
		}
		
		
		
		//------------------------ LightStreamer communication methods ( remote control )
	
		public function p2pReceiveData(str:String):void{

		   // we use str !!

		}

		private function lsSendData(str:String):void{
			
			 megamipLSClient.sendMessage(str);
		}
		
		private function moveForward(evt:MouseEvent):void {
			
		   	lsSendData(MegamipRC.CMD_FORWARD);
			//trace("moveForward ---");
		}
		private function moveBackward(evt:MouseEvent):void {
			
		   	lsSendData(MegamipRC.CMD_BACK);
		}
		private function moveLeft(evt:MouseEvent):void {
			
		   	lsSendData(MegamipRC.CMD_LEFT);
		}
		private function moveRight(evt:MouseEvent):void {
			
		   	lsSendData(MegamipRC.CMD_RIGHT);
		}
	}
	
	
	
}

class CustomClient {
		public function onMetaData(info:Object):void {
			trace("width: " + info.width);
			trace("height: " + info.height);
		}
	}