
/*
 * TelepresenceMegamip
 * Flex_projects\develop\11\TelepresenceMegamip
 * */
package  {
	import com.bit101.components.CheckBox;
	import com.bit101.components.Panel;
	import com.bit101.components.PushButton;
	import com.bit101.components.Text;
	import com.soma.ui.vo.*;
	import com.soma.ui.*;
	import com.soma.ui.layouts.*;
	import flash.text.TextFormat;
	import flash.net.*;
	import flash.events.*;
	import flash.geom.Matrix;
	import flash.utils.Timer;
    
	import org.osmf.media.MediaElement;
	import org.osmf.net.StreamingURLResource;
	import org.osmf.net.StreamType;
	import org.osmf.containers.MediaContainer;
	import org.osmf.elements.VideoElement;
	import org.osmf.media.MediaPlayer;
	import org.osmf.media.URLResource;
	import org.osmf.media.MediaPlayerSprite;
   
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
	import flash.net.NetStream;
    import flash.media.Video;
    import flash.media.Microphone;
    import flash.media.Camera;
	import flash.media.CameraPosition;
	import flash.display.StageDisplayState;
	import flash.geom.Rectangle; 
	import com.bit101.components.ProgressBar;
	
	
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
		private var videoRotation:String = "0";
		private var progressBar:ProgressBar;
		private var progressBarTimer:Timer = new  Timer(150);
		private var progress:int = 0;
		private var startupMessage:TextField;
		
	
		
		public function Main() {
		 
			NativeApplication.nativeApplication.addEventListener(
			InvokeEvent.INVOKE, onInvoke);
			
			
		
		//	onInvoke2(null);
		

		}
		
		
		private function initHandler(evt:Event):void {
			
			NativeApplication.nativeApplication.addEventListener(
					InvokeEvent.INVOKE, onInvoke);
		}
		
		private function onInvoke(event:InvokeEvent):void
		{
		     externalArgs =  event.arguments[0] + "\n" + event.arguments[1];
			  var uri:String = event.arguments[0];
			  var args:Array = uri.split("//");
			  var fingerPrint:String = args[1];
			  videoRotation = args[2];
			  
			  farPeerID = fingerPrint;
			//initProgressBar();  
			initGui();
			initConnection(null);
			
			 megamipLSClient = new MegamipLSClient();
			 megamipLSClient.addEventListener(MegamipLSClient.UPDATE, lsReceiveData);
			 trace("onInvoke ... uri: " + uri + " fingerPrint: " + fingerPrint + " videoRotation: " + videoRotation);
			 
			 
			 
		}
		
		private function onInvoke2(event:MouseEvent):void
		{
			 
			  //farPeerID =   txtFingerPrint.text
			  farPeerID = "492510b93ca7214fe9a798f4edf8d07b64099bf79102cc1aea278d3f027e63af";
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
		//	nsRead.bufferTime = 3;
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
		  
		
		    camera = getCamera();
		
		    mic = Microphone.getMicrophone();
		    
		    if (camera != null){
			//	camera.setMode(camera.height,  camera.width, camera.fps);
			//	camera.setMode(640, 480, camera.fps);
			//camera.setMode(352, 288, camera.fps);   // works fine with logitech hd 720p - good image quality but some lags 
			camera.setMode(352, 288, 15, false); 
			//camera.setMode(320, 240, camera.fps);
			//camera.setMode(480, 640, camera.fps);
			//camera.setMode(1280, 800, camera.fps);
			
			//camera.setMode(480, 360, camera.fps);
			//camera.setMode(480, 360, camera.fps);
			
			// camera.setMode(480, 360, camera.fps);
			
			//camera.setMode(camera.width,  camera.height, camera.fps);
		
			camera.setQuality(0, 90);
		    camera.setKeyFrameInterval(7);
			camera.addEventListener(ActivityEvent.ACTIVITY, activityHandler);
			    
	
				videoPublish.attachCamera(camera);
				
				nsPublish.attachCamera(camera);
				//nsPublish.bufferTime = 3;
				
			
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
			
			
			
		
		videoRead.attachNetStream(nsRead);
		nsRead.play("media");
		
		//---------------------------
		
	
       
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
			
		   return Camera.getCamera();
		}
		

		
		private function setProgress(evt:TimerEvent):void {
	        progress++;
			progressBar.value = progress;
			if (progress >= 99) {
			  progressBarTimer.stop();
			}
			
		}
		
		
		private function initProgressBar():void {
			var baseUI:BaseUI = new BaseUI(stage);
			progressBar = new ProgressBar(stage);
			progressBar.maximum = 100;
			progressBar.setSize(300, 30);
			progressBarTimer.addEventListener(TimerEvent.TIMER, setProgress);
			var ctnProgressBar:ElementUI = baseUI.add(progressBar);
			ctnProgressBar.bottom = 200;
			ctnProgressBar.left = 200;
			ctnProgressBar.refresh();
			
			
			startupMessage = new TextField();
			startupMessage.type = TextFieldType.INPUT;
			startupMessage.width = 400;
			startupMessage.height = 100;
			startupMessage.multiline = true;
			startupMessage.background = true;
			startupMessage.backgroundColor = 0x0;
			
			var txtFormat:TextFormat = new TextFormat();
			txtFormat.font = "Verdana";
			txtFormat.size = 24;
			txtFormat.color = 0xffffff;
			startupMessage.defaultTextFormat = txtFormat;
			startupMessage.text = "       Incoming telepresence call \n \t\t\t please wait ...";
		
			var ctnTxtMessage:ElementUI = baseUI.add(startupMessage);
			ctnTxtMessage.bottom = 30;
			ctnTxtMessage.left = 200;
			ctnTxtMessage.refresh();
			
			
			addChild(startupMessage);
			addChild(progressBar);
			progressBarTimer.start();
		}
		
		
		private function initGui():void {
		    this.stage.align = StageAlign.TOP_LEFT;
			this.stage.scaleMode = StageScaleMode.NO_SCALE;
			this.stage.displayState = StageDisplayState.FULL_SCREEN_INTERACTIVE; 
			this.stage.color =  0x202020;
			
		//-- progress bar 
		
		
			
			
		 //-------------- videos display 
		 
		  
		
	    // videoRead 
	    videoRead = new Video();
		videoRead.smoothing = true;
		videoPublish = new Video();
		videoPublish.smoothing = true;
		
		var mat1:Matrix = new Matrix();
		var mat2:Matrix = new Matrix();
		videoRead.scaleX = 3;
		videoRead.scaleY = 3;
		videoPublish.scaleX = 0.4;
		videoPublish.scaleY = 0.4;
		var gap:int = (stage.fullScreenWidth - videoRead.width) / 2;
		
		trace("initGui rotation: " + videoRotation);
		
		if (videoRotation == "180") {
			
			// videoRead rotation and positionning 
			mat1.rotate(Math.PI);
			mat1.concat(videoRead.transform.matrix);
			videoRead.transform.matrix = mat1;
			
			videoRead.x = gap + videoRead.width;
			videoRead.y = stage.fullScreenHeight;
			
			// videoPublish rotation and positionning 
			mat2.rotate(Math.PI);
			mat2.concat(videoPublish.transform.matrix);
			videoPublish.transform.matrix = mat2;
			
			videoPublish.x = (gap - videoPublish.width)/2 + videoPublish.width ;
			videoPublish.y = videoPublish.height + 30;
			
		 }else { // no rotation 
		    
			videoRead.x = gap ;
			videoRead.y = 0;
			var gap2:int = gap + videoRead.width;
			videoPublish.x = (gap - videoPublish.width)/2  + gap2;
			videoPublish.y = stage.fullScreenHeight - ( videoPublish.height + 30 );
		  }
		  
		addChild(videoPublish);
        addChild(videoRead); 
		
		}
		
			//------------------------ LightStreamer communication methods ( remote control)
	
		public function lsReceiveData(event:Event):void{
        
			var message:String = megamipLSClient.getMessage();
			 trace("lsReceiveData event:" + event);
			 trace("lsReceiveData message:" +message);
			 sendMegamipCMD(message);
		    

		}
		private function sendMegamipCMD(str:String):void {
		trace("sendMegamipCMD ... str: " + str);	
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
		
		private function onSendMegamipCallback (event:Event):void {
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