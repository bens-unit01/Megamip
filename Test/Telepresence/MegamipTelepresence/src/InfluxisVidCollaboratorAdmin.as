package 
{
	//Flash Classes
	//import flash.ui.ContextMenu;
	//import flash.ui.ContextMenuItem;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.NetStatusEvent;
	
	//Influxis Classes
	import org.influxis.flotools.core.InfluxisApplication;
	import org.influxis.as3.display.Label;
	
	//VidCollaborator Classes
	import org.influxis.application.vidcollaborator.VidCollaboratorAdmin;
	
	/*
	 * 	TODO - 
	 * 		> 1. Make sure it works without client com4 framework.
	 * 		> 2. Need to change viewer button to start cam share to something more obvious
	 * 		> 3. Add click to play ability to list.
	 * 
	 * 		Bugs - 
	 * 			> 1. 
	 */
	
	public class InfluxisVidCollaboratorAdmin extends InfluxisApplication 
	{
		private var _sVersion:String = "1.0";
		private static var APP:String = "VideoCollaborator";
		
		private var _sessionID:String;
		private var _sessionsEnabled:Boolean;
		private var _allowRequests:Boolean = true;
		private var _viewerLink:String;
		private var _tINFXt:String;
		private var _fINFXt:String;
		
		private var _admin:VidCollaboratorAdmin;
		private var _initLabel:Label;
		
		/*
		 * INIT API
		 */
		
		public function InfluxisVidCollaboratorAdmin( rtmpPath:String = null, 
													  settingsPath:String = null, 
													  useSettings:Boolean = true, 
													  autoConnect:Boolean = true, 
													  stageAutoSize:Boolean = false ): void
		{
			if( settingsPath ) this.settingsPath = settingsPath;
			this.stageAutoSize = stageAutoSize;
			this.useSettings = useSettings;
			this.autoConnect = autoConnect;
			if( rtmpPath ) this.rtmp = rtmpPath;
			
			super();
			
			//contextMenu = new ContextMenu();
			//contextMenu.customItems.push( new ContextMenuItem(APP+" v"+_sVersion, true, false) );
		}
		
		/*
		 * PROTECTED API
		 */
		
		//Loads Settings from the settings loader which covers htmlvar/flashvars/and xml
		override protected function onSettingsReady(p_bConnect:Boolean = true):void 
		{
			super.onSettingsReady(p_bConnect);
			
			//Parse out the sessionID
			var sessionID:String = String(settingsLoader.dataProvider.rtmp.@sessionID) == "" ? null : String(settingsLoader.dataProvider.rtmp.@sessionID);
			if ( settingsLoader.parameters && settingsLoader.parameters.sessionID != undefined ) sessionID = String(settingsLoader.parameters.sessionID);
			_sessionID = settingsLoader.getHtmlVarsAt("sessionID") == null ? sessionID : String(settingsLoader.getHtmlVarsAt("sessionID"));
			
			//Parse out the sessionEnabled
			var sessionsEnabled:Boolean = String(settingsLoader.dataProvider.rtmp.@sessionsEnabled) == "" ? _sessionsEnabled : String(settingsLoader.dataProvider.rtmp.@sessionsEnabled) == "true";
			if ( settingsLoader.parameters && settingsLoader.parameters.sessionsEnabled != undefined ) sessionsEnabled = String(settingsLoader.parameters.sessionsEnabled) == "true";
			_sessionsEnabled = settingsLoader.getHtmlVarsAt("sessionsEnabled") == null ? sessionsEnabled : String(settingsLoader.getHtmlVarsAt("sessionsEnabled")) == "true";
			
			//Parse out the sessionEnabled
			var allowRequests:Boolean = String(settingsLoader.dataProvider.rtmp.@allowRequests) == "" ? _allowRequests : String(settingsLoader.dataProvider.rtmp.@allowRequests) == "true";
			if ( settingsLoader.parameters && settingsLoader.parameters.allowRequests != undefined ) allowRequests = String(settingsLoader.parameters.allowRequests) == "true";
			_allowRequests = settingsLoader.getHtmlVarsAt("allowRequests") == null ? allowRequests : String(settingsLoader.getHtmlVarsAt("allowRequests")) == "true";
			
			//Parse out the viewerLink (in case they want to change it from default)
			var viewerLink:String = String(settingsLoader.dataProvider.rtmp.@viewerLink) == "" ? _viewerLink : String(settingsLoader.dataProvider.rtmp.@viewerLink);
			if ( settingsLoader.parameters && settingsLoader.parameters.viewerLink != undefined ) viewerLink = String(settingsLoader.parameters.viewerLink);
			_viewerLink = settingsLoader.getHtmlVarsAt("viewerLink") == null ? viewerLink : String(settingsLoader.getHtmlVarsAt("viewerLink"));
			
			//Social tokens can only be loaded via xml or flashvars
			_fINFXt = String(settingsLoader.dataProvider.rtmp.@fbToken) == "" ? null : String(settingsLoader.dataProvider.rtmp.@fbToken);
			if ( settingsLoader.parameters && settingsLoader.parameters.fbToken != undefined ) _fINFXt = String(settingsLoader.parameters.fbToken);
			_tINFXt = String(settingsLoader.dataProvider.rtmp.@twToken) == "" ? null : String(settingsLoader.dataProvider.rtmp.@twToken);
			if ( settingsLoader.parameters && settingsLoader.parameters.twToken != undefined ) _tINFXt = String(settingsLoader.parameters.twToken);
			
			//If a session id was passed in via flash vars then feed it to the collaborator :D
			if ( initialized )
			{
				if ( _sessionID ) _admin.sessionID = _sessionID;
				_admin.sessionsEnabled = _sessionsEnabled;
				_admin.facebookToken = _fINFXt;
				_admin.twitterToken = _tINFXt;
				_admin.allowRequests = _allowRequests;
				_admin.viewerLink = _viewerLink;
			}	
		}
		
		override protected function connectAccepted(info:Object = null, reconnection:Boolean = false):void 
		{
			super.connectAccepted(info, reconnection);
			if( initialized ) _initLabel.text = getLabelAt("statusStarting");
		}
		
		/*
		 * HANDLERS
		 */
		
		private function __onVidCollabEvent( ...args ): void
		{
			_admin.removeEventListener( VidCollaboratorAdmin.INITIALIZED, __onVidCollabEvent );
			removeChild(_initLabel);
		}
		 
		/*
		 * DISPLAY API
		 */
		 
		override protected function createChildren():void 
		{
			super.createChildren();
			_admin = new VidCollaboratorAdmin();
			_admin.addEventListener( VidCollaboratorAdmin.INITIALIZED, __onVidCollabEvent );
			_initLabel = new Label("statusLabel");
			addChildren(_admin, _initLabel);
		}
		
		override protected function childrenCreated():void 
		{
			super.childrenCreated();
			_initLabel.text = connected ? getLabelAt("statusStarting") : getLabelAt("statusConnecting");
			if ( _sessionID ) _admin.sessionID = _sessionID;
			_admin.sessionsEnabled = _sessionsEnabled;
			_admin.facebookToken = _fINFXt;
			_admin.twitterToken = _tINFXt;
			_admin.allowRequests = _allowRequests;
			_admin.viewerLink = _viewerLink;
		}
		
		override protected function arrange():void 
		{
			super.arrange();
			_admin.setActualSize(width, height);
			_initLabel.setActualSize(width, height);
		}
		
		/*
		 * GETTER / SETTER
		 */
		
		public function set allowRequests( value:Boolean ): void
		{
			if ( _allowRequests == value ) return;
			_allowRequests = value;
			if ( initialized ) _admin.allowRequests = _allowRequests;
		}
		
		public function get allowRequests(): Boolean
		{
			return _allowRequests;
		}
		
		public function set viewerLink( value:String ): void
		{
			if ( _viewerLink == value ) return;
			_viewerLink = value;
			if ( initialized ) _admin.viewerLink = _viewerLink;
		}
		
		public function get viewerLink(): String
		{
			return _viewerLink;
		}
		
		public function set twitterToken( value:String ): void
		{
			if ( _tINFXt == value ) return;
			_tINFXt = value;
			if ( initialized ) _admin.twitterToken = _tINFXt;
		}
		
		public function get twitterToken(): String
		{
			return _tINFXt;
		}
		
		public function set facebookToken( value:String ): void
		{
			if ( _fINFXt == value ) return;
			_fINFXt = value;
			if ( initialized ) _admin.facebookToken = _fINFXt;
		}
		
		public function get facebookToken(): String
		{
			return _fINFXt;
		}
	}
}