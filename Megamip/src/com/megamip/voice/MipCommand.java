package com.megamip.voice;

public class MipCommand {
	
	
	
	public class PictureSearch implements Command {
		
		MipReceiver mipReceiver;
		String keywords;
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

		MipReceiver mipReceiver;
		String keywords;
		
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
		
		MipReceiver mipReceiver;
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
		
		MipReceiver mipReceiver;
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
		
		MipReceiver mipReceiver;
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
		
		MipReceiver mipReceiver;
	  // constructors 
	 private GuiShow() {}
	 public GuiShow(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
	}
	 
	public void execute(){
		mipReceiver.guiShow();
	}
	 
	 
}	

public class Speak implements Command{
	
	MipReceiver mipReceiver;
  // constructors 
 private Speak() {}
 public Speak(MipReceiver mipReceiver){
	 this.mipReceiver = mipReceiver;
}
 
public void execute(){
	mipReceiver.speak();
}
 
 
}	


}