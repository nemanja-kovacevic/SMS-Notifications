package net.nemanjakovacevic.smsnotifications.activities;

import java.util.HashMap;
import java.util.Map;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

/**
 * This activity presents information about application such as author, contact
 * email and icon legend. Users also can view FAQ page or other apps from this
 * author from here.
 * 
 * @author nemanjakovacevic
 * 
 */
public class AboutActivity extends Activity implements Keys {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		TextView emailLabel = (TextView) findViewById(R.id.mail_nemanja_kovacevic);
		emailLabel.setOnClickListener(new SendMail(this));

		TextView websiteLabel = (TextView) findViewById(R.id.website_nemanja_kovacevic);
		websiteLabel.setOnClickListener(new ShowUrl(this,
				getString(R.string.website_nemanja_kovacevic)));

		Button troubleShootButton = (Button) findViewById(R.id.if_app_is_not_working);
		troubleShootButton.setOnClickListener(new ShowUrl(this,
				getString(R.string.url_troubleshoot)));

		Button faqButton = (Button) findViewById(R.id.frequently_asked_questions);
		faqButton.setOnClickListener(new ShowUrl(this,
				getString(R.string.url_faq)));

		Button moreAppsButton = (Button) findViewById(R.id.more_apps_from_this_author);
		moreAppsButton.setOnClickListener(new ShowMoreApps(this));

		ImageButton facebookButton = (ImageButton) findViewById(R.id.facebook);
		facebookButton.setOnClickListener(new Facebook(this));

		ImageButton twitterButton = (ImageButton) findViewById(R.id.twitter);
		twitterButton.setOnClickListener(new Twitter(this));

		ImageButton shareButton = (ImageButton) findViewById(R.id.share);
		shareButton.setOnClickListener(new Share(this));

	}

	private static class SendMail implements OnClickListener {

		private Activity activity;

		public SendMail(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			String[] recipients = new String[] { EMAIL_AUTHOR, "", };
			emailIntent
					.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.setType("text/plain");
			try {
				activity.startActivity(Intent.createChooser(emailIntent,
						activity.getString(R.string.send_mail)));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(activity,
						activity.getString(R.string.no_email_client_installed),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private static class ShowUrl implements OnClickListener {

		private Activity activity;
		private String url;

		public ShowUrl(Activity activity, String url) {
			this.activity = activity;
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			Map<String, String> eventParams = new HashMap<String, String>();
			eventParams.put(ANALYTICS_HELP_URL_PARAM, url);
			FlurryAgent.onEvent(ANALYTICS_SHOW_MORE_APPS_EVENT, eventParams);
			
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
			launchBrowser.setData(Uri.parse(url));
			activity.startActivity(launchBrowser);
		}

	}

	private static class ShowMoreApps implements OnClickListener {

		private Activity activity;

		public ShowMoreApps(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FlurryAgent.onEvent(ANALYTICS_SHOW_MORE_APPS_EVENT);
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://search?q=pub:Nemanja Kovacevic"));
			activity.startActivity(intent);
		}

	}

	private static class Facebook implements OnClickListener {

		private Activity activity;

		public Facebook(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FlurryAgent.onEvent(ANALYTICS_FACEBOOK_PAGE_EVENT);
			Intent intent = getOpenFacebookIntent(activity);
			activity.startActivity(intent);
		}

	}

	public static Intent getOpenFacebookIntent(Context context) {
		try {
			context.getPackageManager()
					.getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/130006080424439"));
		} catch (Exception e) {
			return new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://www.facebook.com/pages/SMS-Obave%C5%A1tenja/130006080424439"));
		}
	}

	private static class Twitter implements OnClickListener {

		private Activity activity;

		public Twitter(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FlurryAgent.onEvent(ANALYTICS_TWITTER_PAGE_EVENT);
			
			Intent intent = getOpenTwitterIntent(activity);
			activity.startActivity(intent);
		}

	}
	
	public static Intent getOpenTwitterIntent(Context context) {
		try {
			context.getPackageManager()
					.getPackageInfo("com.twitter.android", 0);
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("twitter://user?screen_name=smsobavestenja"));
		} catch (Exception e) {
			return new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://twitter.com/#!/smsobavestenja"));
		}
	}

	private static class Share implements OnClickListener {

		private Activity activity;

		public Share(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FlurryAgent.onEvent(ANALYTICS_SHARE_EVENT);
			
			Intent intent = getShareIntent(activity);
			activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share)));
		}

	}
	
	public static Intent getShareIntent(Context context) {
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		intent.putExtra(Intent.EXTRA_SUBJECT, "SMS Obaveštenja");
		intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=net.nemanjakovacevic.smsnotifications");

		return intent;
	}

}
