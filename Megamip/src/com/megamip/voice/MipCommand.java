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
			// TODO Auto-generated method stub
			mipReceiver.pictureSearch(keywords);
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