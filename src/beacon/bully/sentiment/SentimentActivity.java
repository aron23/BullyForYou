package beacon.bully.sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import beacon.bully.sentiment.R;

import android.R.raw;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class SentimentActivity extends Activity {
    private static final String  DEFAULT_API = "01V";
	private TextView commentText;
    private ArrayList<String> comment_ids = new ArrayList<String>();
    private ArrayList<String> comments = new ArrayList<String>();
    private ArrayList<Double> salience_scores = new ArrayList<Double>();
	private ArrayList<String> bully_votes = new ArrayList<String>();
	private ArrayList<String> not_bully_votes = new ArrayList<String>();
    private int position = 0;

	private TextView salienceScore;
	private TextView bullyVotes;
	private TextView notBullyVotes;
	
	private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    
    private ViewSwitcher switcher;
	private TextView instructions;
	
	private boolean apiAttempted;
	
	private InputStream commentInputStream;
	private boolean commentsCollected;
	private boolean voteAttempted;
	private String myapi;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        appSharedPrefs = this.getPreferences(Activity.MODE_PRIVATE);
        prefsEditor = appSharedPrefs.edit();
        
        LinearLayout bg = (LinearLayout) findViewById(R.id.bullybody);
        double myscale = ScalingUtils.getWidth(this, appSharedPrefs, prefsEditor);
        
        LayoutParams params = bg.getLayoutParams();
        params.height = (int) (myscale*1.77777);
        bg.setLayoutParams(params);
        
        switcher = (ViewSwitcher) findViewById(R.id.screenSwitcher);

        commentText = (TextView) findViewById(R.id.comment_text);
        commentText.setMovementMethod(new ScrollingMovementMethod());
        salienceScore = (TextView)findViewById(R.id.salience_score_text);
        bullyVotes = (TextView)findViewById(R.id.bully_votes_text);
        notBullyVotes = (TextView)findViewById(R.id.not_bully_votes_text);
        myapi = appSharedPrefs.getString("api_key", "");
        if (myapi.length() >0) {
        	refreshCommentSet(null);
        	//switcher.showNext();
        } else {
        	myapi= DEFAULT_API;
        	prefsEditor.putString("api_key", myapi);
        	prefsEditor.commit();
        	refreshCommentSet(null);
        }
        
        
        

    }

	public void refreshCommentSet(View view) {
    	commentInputStream = null;
        comment_ids = new ArrayList<String>();
        comments = new ArrayList<String>();
        commentsCollected = false;
		
			
			
			new Thread(new Runnable(){
				public void run(){		
					commentInputStream = WebUtil.getInputStreamFromUrl("https://beaconinitiative.com/api/get/bully/23");
					BufferedReader br = new BufferedReader(new InputStreamReader(commentInputStream));
					String strLine;
					try {
					  //Read File Line By Line
					while ((strLine = br.readLine()) != null)   {
						String[] commented = strLine.split(":::");
						if (commented.length < 2)
							continue;
						comment_ids.add(commented[0]);
						comments.add(Html.fromHtml(commented[1]).toString());	
						salience_scores.add(Double.valueOf(commented[3]));
						bully_votes.add(commented[4]);
						not_bully_votes.add(commented[5]);
						if (commented.length < 4)
							continue;
						
					}
					} catch(IOException e) {
						Log.e("Sentiment processing error", e.getMessage());
					} finally {
						try {
							commentInputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					commentsCollected = true;
				}
				
			},"collect comments").start();
			
			while (!commentsCollected) {}

			
		
        commentText.setText(comments.get(position));
        salienceScore.setText(String.valueOf((int)Math.round(salience_scores.get(position)*100)));
        bullyVotes.setText(bully_votes.get(position));
        notBullyVotes.setText(not_bully_votes.get(position));
	}

//    public void setAPI(View view) {
//    	new Thread(new Runnable(){
//			public void run(){		
//				EditText api = (EditText)findViewById(R.id.api_key);
//		    	String myapi = api.getText().toString();
//				if (WebUtil.isKeyValid(myapi)) {
//			    	prefsEditor.putString("api_key", myapi);
//			    	prefsEditor.commit();
//		    	}
//				apiAttempted=true;
//			}
//		},"set api").start();
//    	
//    	while(!apiAttempted){}
//    	if (appSharedPrefs.getString("api_key", "").length() >0) {
//    		refreshCommentSet(null);
//	    	switcher.showNext();
//        }
//    }
	
    public void voteBully(View view) {
    	voteAttempted=false;
    	new Thread(new Runnable(){
			public void run(){		
				WebUtil.vote(comment_ids.get(comments.indexOf(commentText.getText().toString())), appSharedPrefs.getString("api_key", ""), "bully");
				voteAttempted=true;
			}
		},"set api").start();
    	while (!voteAttempted) {}
    	incrementComment(view);
    }

	public void voteNotBully(View view) {
    	voteAttempted=false;
    	new Thread(new Runnable(){
			public void run(){		
				WebUtil.vote(comment_ids.get(comments.indexOf(commentText.getText().toString())), appSharedPrefs.getString("api_key", ""), "not_bully");
				voteAttempted=true;
			}
		},"set api").start();
    	while (!voteAttempted) {}
    	incrementComment(view);
    }
   
	public void incrementComment(View view) {
    	position++;
    	if (position >= comments.size()) {
    		position = 0;
    		refreshCommentSet(view);
    	}
    	commentText.setText(comments.get(position));
    	salienceScore.setText(String.valueOf((int)Math.round(salience_scores.get(position)*100)));
        bullyVotes.setText(bully_votes.get(position));
        notBullyVotes.setText(not_bully_votes.get(position));
    }
    public void decrementComment(View view) {
    	position--;
    	if (position < 0)
    		position = comments.size()-1;
    	commentText.setText(comments.get(position));
    }

    public static int getResId(String variableName, Context context, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } 
    }


    
    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();
    
        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }
        
        return strBuf.toString();
    }
    
    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i+=2) {
            char c = str.charAt(i);
            bytes[i/2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i+1);
            bytes[i/2] += (c - 'a');
        }
        return bytes;
    }

    
    
}