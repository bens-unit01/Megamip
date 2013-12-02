package com.megamip.voice;

public class MipCommand {
	
	
	
public class GuiShowMic implements Command {

		private MipReceiver mipReceiver;
		  // constructors 
		 private GuiShowMic() {}
		 public GuiShowMic(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
		}
		 
		public void execute(){
			mipReceiver.guiShowMic();
		}
		 
		
		
}
	
public class GuiHideMic implements Command {

		private MipReceiver mipReceiver;
		  // constructors 
		 private GuiHideMic() {}
		 public GuiHideMic(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
		}
		 
		public void execute(){
			mipReceiver.guiHideMic();
		}
		 
		
		
}
	
public class PictureSearch implements Command {
		
		private MipReceiver mipReceiver;
		private String keywords;
		// constructors 
		private  PictureSearch(){}
		
		public PictureSearch(MipReceiver mipReceiver, String keywords){
			
			this.mipReceiver = mipReceiver;
			this.keywords = keywords;
		}

		public void execute() {
			
			mipReceiver.pictureSearch(keywords);
		}

		

}
   
public class VideoSearch implements Command {

		private MipReceiver mipReceiver;
		private String keywords;
		
		// constructors 
		private VideoSearch(){}
		
		public VideoSearch(MipReceiver mipReceiver, String keywords){
			this.mipReceiver = mipReceiver;
			this.keywords = keywords;
		}
		@Override
		public void execute() {
			
			mipReceiver.videoSearch(keywords);
		}
		
}
  
public class GuiNext implements Command{
		
	private MipReceiver mipReceiver;
	  // constructors 
	 private GuiNext() {}
	 public GuiNext(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
	}
	 
	public void execute(){
		mipReceiver.guiNext();
	}
	 
	 
}
	
	
public class GuiBack implements Command{
		
	private MipReceiver mipReceiver;
	  // constructors 
	 private GuiBack() {}
	 public GuiBack(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
	}
	 
	public void execute(){
		mipReceiver.guiBack();
	}
	 
	 
}	



public class GuiHome implements Command{
		
		private MipReceiver mipReceiver;
		  // constructors 
		 private GuiHome() {}
		 public GuiHome(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
		}
		 
		public void execute(){
			mipReceiver.guiHome();
		}
	 
	 
}	



public class GuiShow implements Command{
		
		private MipReceiver mipReceiver;
		private String mMode;
		  // constructors 
		 private GuiShow() {}
		 public GuiShow(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
			 mMode = null;
		}
		 
		public GuiShow(MipReceiver mipReceiver, String mMode) {
			this.mipReceiver = mipReceiver;
			this.mMode = mMode;
		}
		public void execute(){
			mipReceiver.guiShow(mMode);
		}
		 
	 
}	

public class Speak implements Command{
	
		private MipReceiver mipReceiver;
		  // constructors 
		 private Speak() {}
		 public Speak(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
		}
		 
		public void execute(){
			mipReceiver.speak();
		}
 
 
}	

public class GuiShowMessage implements Command{
	
		 private MipReceiver mipReceiver;
		 private String message;
		  // constructors 
		 private GuiShowMessage() {}
		 public GuiShowMessage(MipReceiver mipReceiver, String message){
			 this.mipReceiver = mipReceiver;
			 this.message = message;
		}
		 
		public void execute(){
			mipReceiver.guiShowMessage(message);
		}
 
 
}	


public class GuiDisplayNotifications implements Command{
	
		 private MipReceiver mipReceiver;
		 private String notifications;
		
		  // constructors 
		 private GuiDisplayNotifications() {}
		 public GuiDisplayNotifications(MipReceiver mipReceiver, String notifications){
			 this.mipReceiver = mipReceiver;
			 this.notifications = notifications;
			
		}
		
		 
		public void execute(){
			mipReceiver.guiDisplayNotifications(notifications);
		}
 
 
}	



public class GuiDisplayNotifications2 implements Command{
	
		 private MipReceiver mipReceiver;
		 private String notifications;
		 private int period;
		
		  // constructors 
		 private GuiDisplayNotifications2() {}
		 public GuiDisplayNotifications2(MipReceiver mipReceiver, String notifications,int period){
			 this.mipReceiver = mipReceiver;
			 this.notifications = notifications;
			 this.period = period;
			
		}
		
		 
		public void execute(){
			mipReceiver.guiDisplayNotifications(notifications, period);
		}
 
 
}	

public class GuiBlink implements Command{
	
	 private MipReceiver mipReceiver;
	  // constructors 
	 private GuiBlink() {}
	 public GuiBlink(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
		 
	}
	 
	public void execute(){
		mipReceiver.guiBlink();
	}


}	
// Megamip motion classes 



public class MipMoveForward implements Command{
	
		 private MipReceiver mipReceiver;
		 private String message;
		  // constructors 
		 private MipMoveForward() {}
		 public MipMoveForward(MipReceiver mipReceiver){
			 this.mipReceiver = mipReceiver;
			 
		}
		 
		public void execute(){
			mipReceiver.mipMoveForward();
		}
		 
}	

public class MipMoveBackward implements Command{
	
	 private MipReceiver mipReceiver;
	 private String message;
	  // constructors 
	 private MipMoveBackward() {}
	 public MipMoveBackward(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
		 
	}
	 
	public void execute(){
		mipReceiver.mipMoveBackward();
	}
	 
}	


public class MipMoveLeft implements Command{
	
	 private MipReceiver mipReceiver;
	 private String message;
	  // constructors 
	 private MipMoveLeft() {}
	 public MipMoveLeft(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
		 
	}
	 
	public void execute(){
		mipReceiver.mipMoveLeft();
	}
	 
}	



public class MipMoveRight implements Command{
	
	 private MipReceiver mipReceiver;
	 private String message;
	  // constructors 
	 private MipMoveRight() {}
	 public MipMoveRight(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
		 
	}
	 
	public void execute(){
		mipReceiver.mipMoveRight();
	}
	 
}	
}