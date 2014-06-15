package com.megamip.voice;

import android.util.Log;

public class MipCommand {

	public class GuiShowMic implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiShowMic() {
		}

		public GuiShowMic(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiShowMic();
		}

	}

	public class GuiHideMic implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiHideMic() {
		}

		public GuiHideMic(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiHideMic();
		}

	}

	public class PictureSearch implements Command {

		

		private MipReceiver mipReceiver;
		private String keywords;

		// constructors
		private PictureSearch() {
		}

		public PictureSearch(MipReceiver mipReceiver, String keywords) {

			this.mipReceiver = mipReceiver;
			this.keywords = keywords;
		}
		
		public PictureSearch(MipReceiver mipReceiver) {

			this.mipReceiver = mipReceiver;
			
		}
		
		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}

		public void execute() {

			mipReceiver.pictureSearch(keywords);
		}

	}

	public class VideoSearch implements Command {

		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}

		private MipReceiver mipReceiver;
		private String keywords;

		// constructors
		private VideoSearch() {
		}

		public VideoSearch(MipReceiver mipReceiver, String keywords) {
			this.mipReceiver = mipReceiver;
			this.keywords = keywords;
		}
		
		public VideoSearch(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		@Override
		public void execute() {

			mipReceiver.videoSearch(keywords);
		}

	}
	
	

	public class GuiNext implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiNext() {
		}

		public GuiNext(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiNext();
		}

	}
	
	public class GuiPrev implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiPrev() {
		}

		public GuiPrev(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiPrev();
		}

	}

	public class GuiBack implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiBack() {
		}

		public GuiBack(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiBack();
		}

	}

	public class GuiHome implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiHome() {
		}

		public GuiHome(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiHome();
		}

	}

	public class GuiShow implements Command {

		
		private MipReceiver mipReceiver;
		private String mMode;

		// constructors
		private GuiShow() {
		}

		public GuiShow(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
			mMode = null;
		}

		public GuiShow(MipReceiver mipReceiver, String mMode) {
			this.mipReceiver = mipReceiver;
			this.mMode = mMode;
		}
		
		public void setmMode(String mMode) {
			this.mMode = mMode;
		}


		public void execute() {
			mipReceiver.guiShow(mMode);
		}

	}
	
public class GuiDisplayNotificationsPanel implements Command {

		
		private MipReceiver mipReceiver;
		

		// constructors
		private GuiDisplayNotificationsPanel() {
		}

		public GuiDisplayNotificationsPanel(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		
		}

		public void execute() {
			mipReceiver.guiDisplayNotificationsPanel();
		}

}

	public class Speak implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private Speak() {
		}

		public Speak(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.speak();
		}

	}

	public class GuiShowMessage implements Command {

		

		private MipReceiver mipReceiver;
		private String message;

		// constructors
		private GuiShowMessage() {
		}

		public GuiShowMessage(MipReceiver mipReceiver, String message) {
			this.mipReceiver = mipReceiver;
			this.message = message;
		}
		
		public GuiShowMessage(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}


		public void execute() {
			mipReceiver.guiShowMessage(message);
		}

	}

	public class GuiDisplayNotifications implements Command {

		public void setNotifications(String notifications) {
			this.notifications = notifications;
		}

		private MipReceiver mipReceiver;
		private String notifications;

		// constructors
		private GuiDisplayNotifications() {
		}

		public GuiDisplayNotifications(MipReceiver mipReceiver,
				String notifications) {
			this.mipReceiver = mipReceiver;
			this.notifications = notifications;

		}
		public GuiDisplayNotifications(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
        }

		public void execute() {
			mipReceiver.guiDisplayNotifications(notifications);
		}

	}

	  public class GuiDisplayNotifications2 implements Command {

      public void setNotifications(String notifications) {
			this.notifications = notifications;
		}
      
      public void setPeriod(int period) {
			this.period = period;
		}

		private MipReceiver mipReceiver;
		private String notifications;
		private int period;

		// constructors
		private GuiDisplayNotifications2() {
		}

		public GuiDisplayNotifications2(MipReceiver mipReceiver,
				String notifications, int period) {
			this.mipReceiver = mipReceiver;
			this.notifications = notifications;
			this.period = period;

		}

		public GuiDisplayNotifications2(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		public void execute() {
			mipReceiver.guiDisplayNotifications(notifications, period);
		}

	}

	public class GuiBlink implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private GuiBlink() {
		}

		public GuiBlink(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.guiBlink();
		}

	}

	// Megamip motion classes

	public class MipMoveForward implements Command {

		private MipReceiver mipReceiver;
		private String message;

		// constructors
		private MipMoveForward() {
		}

		public MipMoveForward(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.mipMoveForward();
		}

	}

	public class MipMoveForward2 implements Command {

		private MipReceiver mipReceiver;
		private String params;

		// constructors
		private MipMoveForward2() {
		}

		public MipMoveForward2(MipReceiver mipReceiver, String params) {
			this.mipReceiver = mipReceiver;
			this.params = params;
		}

		public void execute() {
			mipReceiver.mipMoveForward(params);
		}

	}

	public class MipMoveBackward implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private MipMoveBackward() {
		}

		public MipMoveBackward(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.mipMoveBackward();
		}

	}

	public class MipMoveBackward2 implements Command {

		private MipReceiver mipReceiver;
		private String params;

		// constructors
		private MipMoveBackward2() {
		}

		public MipMoveBackward2(MipReceiver mipReceiver, String params) {
			this.mipReceiver = mipReceiver;
			this.params = params;
		}

		public void execute() {
			mipReceiver.mipMoveBackward(params);
		}

	}

	public class MipMoveLeft implements Command {

		private MipReceiver mipReceiver;
		private String message;

		// constructors
		private MipMoveLeft() {
		}

		public MipMoveLeft(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.mipMoveLeft();
		}

	}

	public class MipMoveLeft2 implements Command {

		private MipReceiver mipReceiver;
		private String params;

		// constructors
		private MipMoveLeft2() {
		}

		public MipMoveLeft2(MipReceiver mipReceiver, String params) {
			this.mipReceiver = mipReceiver;
			this.params = params;
		}

		public void execute() {
			mipReceiver.mipMoveLeft(params);
		}

	}

	public class MipMoveRight implements Command {

		private MipReceiver mipReceiver;
		private String message;

		// constructors
		private MipMoveRight() {
		}

		public MipMoveRight(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.mipMoveRight();
		}

	}

	public class MipMoveRight2 implements Command {

		private MipReceiver mipReceiver;
		private String params;

		// constructors
		private MipMoveRight2() {
		}

		public MipMoveRight2(MipReceiver mipReceiver, String params) {
			this.mipReceiver = mipReceiver;
			this.params = params;
		}

		public void execute() {
			mipReceiver.mipMoveRight(params);
		}

	}

	public class MipStop implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private MipStop() {
		}

		public MipStop(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;

		}

		public void execute() {
			mipReceiver.mipStop();
		}

	}

	public class VisorMoveUp implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private VisorMoveUp() {
		}

		public VisorMoveUp(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		@Override
		public void execute() {
			mipReceiver.visorMoveUp();

		}

	}

	public class VisorMoveDown implements Command {

		private MipReceiver mipReceiver;

		// constructors
		private VisorMoveDown() {
		}

		public VisorMoveDown(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}

		@Override
		public void execute() {
			mipReceiver.visorMoveDown();

		}

	}

	public class MoveProjectorTo implements Command {

	

		private MipReceiver mipReceiver;
		private String params;

		// constructors
		private MoveProjectorTo() {
		}

		public MoveProjectorTo(MipReceiver mipReceiver, String params) {
			this.mipReceiver = mipReceiver;
			this.params = params;
		}
		
		public MoveProjectorTo(MipReceiver mipReceiver) {
			this.mipReceiver = mipReceiver;
		}
		
		public void setParams(String params) {
			this.params = params;
		}
		@Override
		public void execute() {
			try {
				Log.d("A3", "bloc try MoveProjectorTo - MipCommand params: "
						+ params);
				mipReceiver.moveProjectorTo(params);
			} catch (Exception e) {
				Log.d("A3",
						"bloc catch MoveProjectorTo - MipCommand e: "
								+ e.toString());
			}

		}

	}

}