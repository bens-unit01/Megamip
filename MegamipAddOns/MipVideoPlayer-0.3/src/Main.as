package 
{
	import flash.desktop.NativeApplication;
	import flash.events.Event;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.ui.Multitouch;
	import flash.ui.MultitouchInputMode;
	import flash.events.InvokeEvent;
	import flash.display.Loader;
	import flash.display.StageDisplayState;
	import com.soma.ui.vo.*;
	import com.soma.ui.*;
	import com.soma.ui.layouts.*;
	import flash.text.TextFormat;
	import flash.net.URLRequest;
    import flash.system.Security;
    import flash.desktop.NativeApplication;
	import flash.events.Event;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.display.Loader;
    import flash.display.StageDisplayState;
    import flash.events.IEventDispatcher;
	  
	 
	
	
	/**
	 * ...
	 * @author bens
	 */
	public class Main extends Sprite 
	{
		protected var player:Sprite;
	    protected var mPlayer:Object;
	    protected var loader:Loader = new Loader();
		protected var videoId:String;
		protected var rotationAngle:String = "0";
		
		public function Main():void 
		{
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
			stage.addEventListener(Event.DEACTIVATE, deactivate);
			
			// touch or gesture?
			Multitouch.inputMode = MultitouchInputMode.TOUCH_POINT;
			
			// entry point
			
			// new to AIR? please read *carefully* the readme.txt files!
			
			NativeApplication.nativeApplication.addEventListener(
			InvokeEvent.INVOKE, onInvoke);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.CLOSE, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.COMPLETE, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.DEACTIVATE, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.EXIT_FRAME, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.SUSPEND, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.CLEAR, deactivate);
			NativeApplication.nativeApplication.addEventListener(flash.events.Event.CANCEL, deactivate);
	        //if (stage) init();
            //else addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function deactivate(e:Event):void 
		{
			// make sure the app behaves well (or exits) when in background
			trace("deactivate ...");
			NativeApplication.nativeApplication.exit();
		}
		
		private function onInvoke(event:InvokeEvent):void {
		
	         var arg0:String = event.arguments[0];
	         var arg1:String = event.arguments[1];
			 var params:Array = event.arguments[0].split("//");
			 videoId = params[1].toString();
		     rotationAngle = params[2].toString();
			 
			  //videoId = "HkMNOlYcpHg";
		     //rotationAngle = "180".toString();
			 trace("onInvoke arg0: " + arg0 + " arg1: " + arg1 + "videoId: " + params[1] + " rotation: " + params[2] );
		
			 init(null);
	    }
	
	
	    private function init(e:Event = null):void {
		
				trace("start ...");
				this.stage.align = StageAlign.TOP_LEFT;
				this.stage.scaleMode = StageScaleMode.NO_SCALE;
				this.stage.displayState = StageDisplayState.FULL_SCREEN_INTERACTIVE; 
				this.stage.color = 0x282828;
				
				removeEventListener(Event.ADDED_TO_STAGE, init);
				Security.loadPolicyFile("http://www.youtube.com/crossdomain.xml");
			
				loader.load(new URLRequest("http://www.youtube.com/apiplayer?version=3"));
				loader.contentLoaderInfo.addEventListener(Event.INIT, onLoaderInit);
		
	    }
		
		  private function onLoaderInit(e:Event):void
        {
    		trace("onLoaderInit ... stage.width: "+stage.width+" "+stage.fullScreenHeight+" "+stage.fullScreenWidth);
			mPlayer = loader.content;
			//player = Sprite(loader.content);
			player = Sprite(mPlayer);
			player.scaleX = 2;
			player.scaleY = 2;
			
			if ( rotationAngle == "180" ) {
				player.x = stage.fullScreenWidth;
			    player.y = stage.fullScreenHeight;
				player.rotation = 180;
			}
			
			
			var hbox2:VBoxUI = new VBoxUI(this, 300, 35);
			hbox2.backgroundColor = 0x282828;
			hbox2.backgroundAlpha = 0.4;
			hbox2.ratio = ElementUI.RATIO_IN;
			hbox2.childrenGap = new GapUI(5, 5);
			hbox2.childrenPadding = new PaddingUI(15,15, 15, 15);
			hbox2.childrenAlign = VBoxUI.ALIGN_TOP_LEFT;
			hbox2.addChild(player);
			
			 
			  addChild(player);
			 

			var dispatcher:IEventDispatcher = player as IEventDispatcher;
			
			dispatcher.addEventListener("onReady", onPlayerReady);
			dispatcher.addEventListener("onError", onPlayerError);
			dispatcher.addEventListener("onStateChange", onPlayerStateChange);
			dispatcher.addEventListener("onPlayerQualityChange", onVideoPlaybackQualityChange);
        }
		
		 private function onPlayerReady(e:Event):void
		{
	   
			trace("Player ready ...");
		
			
			mPlayer.cueVideoById(videoId , 0, "medium");
			//mPlayer.setPlaybackQuality("large");
			mPlayer.playVideo();
			
			
			
			
		}
		private function onPlayerError(e:Event):void
		{
			trace("Player error ... "+e.type+" -- "+e.toString()+" ");
		}
		private function onPlayerStateChange(e:Event):void
		{
			trace("Player state ... quality: " + mPlayer.getPlaybackQuality() );
			trace("quality levels:------\n");
			var tab:Array = mPlayer.getAvailablePlaybackRates();
			
			for (var i:int = 0; i < tab.length ; i++) {
				
			 trace("-- "+tab[i] + "\n");
				}
		}
		private function onVideoPlaybackQualityChange(e:Event):void
		{
			trace("Video quality ...");
		}
   

		
	}
	
}