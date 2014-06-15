package com.megamip.util;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class MipUtils {

	// ------- constructors

	public MipUtils() {
		super();

	}

	
	
	public String getYoutubeVideoId(String youtubeUrl) {
		String video_id = "";
		Log.d("A3", "MipVideoPlayer#getYoutubeVideoId url: " + youtubeUrl);
		if (youtubeUrl != null && youtubeUrl.trim().length() > 0
				&& youtubeUrl.startsWith("http")) {

			String expression = "^.*((youtu.be"
					+ "\\/)"
					+ "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var
																								// regExp
																								// =
																								// /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			CharSequence input = youtubeUrl;
			Pattern pattern = Pattern.compile(expression,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String groupIndex1 = matcher.group(7);
				if (groupIndex1 != null && groupIndex1.length() == 11)
					video_id = groupIndex1;
			}
		}
		Log.d("A3", "MipVideoPlayer#getYoutubeVideoId video_id: " + video_id);
		return video_id;
	}
	
	public void closeApp(String packageName, Context context){
		
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pids = am
				.getRunningAppProcesses();
		int processid = 0;
		int uid = 0;
		int myUid = android.os.Process.myUid();
		for (int i = 0; i < pids.size(); i++) {
			ActivityManager.RunningAppProcessInfo info = pids
					.get(i);
		//	"air.air.MipVideoPlayer"
			if (info.processName
					.equalsIgnoreCase(packageName)) {
				processid = info.pid;
				uid = info.uid;
			}
		}
		
		
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("kill -9 "+processid);
		try {
			doCmds(cmdList);
			Log.d("A3", "kill process end ...");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	private static void doCmds(List<String> cmds) throws Exception {
	    Process process = Runtime.getRuntime().exec("su");
	    DataOutputStream os = new DataOutputStream(process.getOutputStream());

	    for (String tmpCmd : cmds) {
	            os.writeBytes(tmpCmd+"\n");
	    }

	    os.writeBytes("exit\n");
	    os.flush();
	    os.close();

	    process.waitFor();
	}    

}
