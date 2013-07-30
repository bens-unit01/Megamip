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
  
	public class Next implements Command{
		
		MipReceiver mipReceiver;
	  // constructors 
	 public Next() {}
	 public Next(MipReceiver mipReceiver){
		 this.mipReceiver = mipReceiver;
	}
	 
	public void execute(){
		mipReceiver.next();
	}
	 
	 
}

}