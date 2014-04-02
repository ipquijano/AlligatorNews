package com.simplerssreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

public class PcWorldRssParser extends Activity {

	// We don't use namespaces
	private final String ns = null;

	public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			inputStream.close();
		}
	}
	
	//Feed Search and Retrieval
	private List<RssItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "rss");
		String title = null;
		String link = null;
		List<RssItem> items = new ArrayList<RssItem>();
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);	
		String restoredText = prefs.getString("topterms", null);
		List<String> allTopTerms = convertTopTerms(restoredText);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			} else if (name.equals("link")) {
				link = readLink(parser);
			}
			if (title != null && link != null) {
						Log.i("TITLE", title);
						RssItem item = new RssItem(title, link);
						items.add(item);
						title = null;
						link = null;
			}
		}
		return items;
	}
	
	public List<String> convertTopTerms(String topTerms) {
		List<String> listString = new ArrayList<String>();
		if ( topTerms != null ) {
			String[] termArray = topTerms.split(",");
			for ( String s : termArray ) {
				listString.add(s.trim());
			}
			return listString;
		}
		return null;
	}

	private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	// For the tags title and link, extract their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
