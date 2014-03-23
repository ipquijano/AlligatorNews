package com.angbaboy.newsalligator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	Button getStatus;
	Button logout;
	Button showWeight;
	List<String> statusList;
	List<String> likeList;
	
	private Facebook facebookClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		getStatus = (Button) findViewById(R.id.btnGetStatus);
		logout = (Button) findViewById(R.id.logout);
		showWeight = (Button) findViewById(R.id.btnShowWeight);
		facebookClient = new Facebook("1375915182646108");
		statusList = new ArrayList<String>();
		likeList = new ArrayList<String>();
		
		showWeight.setEnabled(false);
		
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Session.getActiveSession() != null) {
				    Session.getActiveSession().closeAndClearTokenInformation();
				}
				Session.setActiveSession(null);
				finish();
			}
		});
		
		getStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Session currentSession = Session.getActiveSession();
				if (currentSession == null || currentSession.getState().isClosed()) {
			        Session session = new Session.Builder(MainActivity.this).build();
			        Session.setActiveSession(session);
			        currentSession = session;
			    }
				
				if (currentSession.isOpened()) {
					getStatusRequest(currentSession);
					getLikesRequest(currentSession);
//					new StatusCallAsync().execute(currentSession);
				} else if ( !currentSession.isOpened() ) {
					// Ask for username and password
			        OpenRequest op = new Session.OpenRequest((Activity) MainActivity.this);

			        op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
			        op.setCallback(null);

			        List<String> permissions = new ArrayList<String>();
//			        permissions.add("publish_stream");
//			        permissions.add("email");
//			        permissions.add("user_birthday");
			        permissions.add("user_likes");
			        permissions.add("user_status");
			        op.setPermissions(permissions);

			        Session session = new Builder(MainActivity.this).build();
			        Session.setActiveSession(session);
			        session.openForRead(op);
				}
				
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		showWeight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<Status> allStatus = new ArrayList<Status>();
				for ( int i = 0; i < statusList.size(); i++ ) {
					allStatus.add(new Status(statusList.get(i), new ArrayList<Term>(), statusList.get(i).length(), i));
				}
				
				for ( Status stat : allStatus ) {
					String[] tokens = stat.getStat().split("[!,.?:; ]|and|or");

					for (int i = 0; i < tokens.length; i++){
						Term term = new Term(tokens[i].trim(), 0,  0,  0,  0, i,  0 ); // added .trim() to tokens[]
						tokens[i] = tokens[i].trim();
						//System.out.println("term at " + i + " = " + term.getTerm());
						stat.getTerms().add(term);
					}

				}

				List<Term> allTerm = new ArrayList<Term>();
				
				List<String> allLikes = new ArrayList<String>();
				for ( int i = 0; i < likeList.size(); i++ ) {
					allLikes.add(likeList.get(i));
				}

				Tokenizer.computeTermWeight(allStatus, allTerm, allLikes);
			}
		});

	}
	private void getLikesRequest(final Session session) {
		
		Bundle params = new Bundle();
		params.putInt("limit", 300);
		
		Request request = new Request(
							session, 
							"me/likes",
							params,
							HttpMethod.GET,
							new Request.Callback() {
								@Override
								public void onCompleted(Response response) {
									// TODO Handle response data (JSON)
									try {
										GraphObject go  = response.getGraphObject(); 
										
								        JSONObject jso = go.getInnerJSONObject();
								        JSONArray data = jso.getJSONArray("data");
								        for ( int i = 0; session == Session.getActiveSession(); i++ ) {
											JSONObject dataObject = data.getJSONObject(i);
											likeList.add(dataObject.getString("name"));
											Log.i("Like " + i, "Added like: " + dataObject.getString("name"));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									showWeight.setEnabled(true);
								}
							});
		request.executeAsync();
	}


	private void getStatusRequest(final Session session) {
		
		Bundle params = new Bundle();
		params.putInt("limit", 100);
		
		Request request = new Request(
							session, 
							"/me/statuses",
							params,
							HttpMethod.GET,
							new Request.Callback() {
								@Override
								public void onCompleted(Response response) {
									// TODO Handle response data (JSON)
									try {
										GraphObject go  = response.getGraphObject(); // returns NULL, why :(
										
								        JSONObject jso = go.getInnerJSONObject();
								        JSONArray data = jso.getJSONArray("data");
										for ( int i = 0; i < data.length(); i++ ) {
											JSONObject dataObject = data.getJSONObject(i);
											statusList.add(dataObject.getString("message"));
											Log.i("Status " + i, "Added status: " + dataObject.getString("message"));
										}
									} catch (Exception e) {
										e.printStackTrace();
//										Log.i("MESSAGE", session.getAccessToken());
//										Log.i("MESSAGE", response.toString());
//										Log.i("MESSAGE", session.getExpirationDate().toString());
									}
//									getLikesRequest(session);
//									showWeight.setEnabled(true);
								}
							});
		
		request.executeAsync();
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebookClient.authorizeCallback(requestCode, resultCode, data);
		if ( Session.getActiveSession() != null ) {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
		
		Session currentSession = Session.getActiveSession();
		if ( currentSession == null || currentSession.getState().isClosed() ) {
			Session session = new Session.Builder(MainActivity.this).build();
			Session.setActiveSession(session);
			currentSession = session;
		}
		
		if ( currentSession.isOpened() ) {
			Session.openActiveSession(this, true, new StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if ( session.isOpened() ) {
						Request.executeMeRequestAsync(session, new GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									TextView welcome = (TextView) findViewById(R.id.welcome);
									welcome.setText("Hello " + user.getName() + "!");
								}
							}
						});
					}
				}
			});
		}
	}
	
}
