package com.megamip.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

}
