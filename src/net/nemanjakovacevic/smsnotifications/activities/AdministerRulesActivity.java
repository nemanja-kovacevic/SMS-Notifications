package net.nemanjakovacevic.smsnotifications.activities;

import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.activities.adapters.RulesAdapter;
import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util;
import net.nemanjakovacevic.smsnotifications.util.Util.MessageType;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AdministerRulesActivity extends ListActivity implements Keys {

	public static final String MODE = "MODE";
	public static final int LOCAL_RULES = 1;
	public static final int GLOBAL_RULES = 2;
	
	private static int CURRENT_MODE = 0;
	
	private RulesAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CURRENT_MODE = getIntent().getIntExtra(MODE, 1);
		
		if(CURRENT_MODE == LOCAL_RULES){
			List<RuleSet> localRuleSets = RuleSetDAO.getLocalRuleSets(this);
			adapter = new RulesAdapter(this, localRuleSets, RulesAdapter.Hint.SHOWING_LOCAL_RULES);
			setListAdapter(adapter);
		}else if (CURRENT_MODE == GLOBAL_RULES){
			
		}
		
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
				Object item = adapterView.getAdapter().getItem(position);
				if(item instanceof RulesAdapter.CheckForUpdatesAuxiliaryItem){
					final ProgressDialog dialog = ProgressDialog.show(AdministerRulesActivity.this, null, AdministerRulesActivity.this.getString(R.string.checking_for_updates));
					
					new AsyncTask<Void, Void, List<RuleSet>>() {

						private InternetConnenctionException exception;
						private Context context = AdministerRulesActivity.this;
						
						@Override
						protected List<RuleSet> doInBackground(Void... params) {
							List<RuleSet> mergedRuleSets = null;
							try {
								mergedRuleSets = RuleSetDAO.getMergedRuleSets(AdministerRulesActivity.this);
								return mergedRuleSets;
							} catch (InternetConnenctionException e) {
								exception = e;
								return null;
							}
						}
						
						@Override
						protected void onPostExecute(List<RuleSet> mergedRuleSets) {
							if(exception != null){
								dialog.dismiss();
								Util.notifyUser(context, MessageType.INTERNET_CONECTION_PROBLEM, R.string.check_for_update_internet_connection_problem);
								return;
							}
							
							adapter =  new RulesAdapter(context, mergedRuleSets, RulesAdapter.Hint.SHOWING_GLOBAL_RULES);
							setListAdapter(adapter);
							dialog.dismiss();
						};
					}.execute();
					
				}
				
			}
			
		});
	}
	
}
