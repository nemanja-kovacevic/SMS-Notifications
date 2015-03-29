package net.nemanjakovacevic.smsnotifications.activities.adapters;

import java.util.ArrayList;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.util.Util;
import net.nemanjakovacevic.smsnotifications.util.Util.MessageType;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RulesAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Object> items;
	private Context context;
	private Hint hint;

	public RulesAdapter(Context context, List<RuleSet> ruleSets, Hint hint) {
		this.items = createItems(ruleSets, hint);
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.hint = hint;
	}

	private List<Object> createItems(List<RuleSet> ruleSets, Hint hint) {
		if (ruleSets == null) {
			return null;
		}
		List<Object> items = new ArrayList<Object>();
		items.addAll(ruleSets);
		if (hint == Hint.SHOWING_LOCAL_RULES) {
			items.add(new CheckForUpdatesAuxiliaryItem());
		}
		return items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Hint getHint() {
		return hint;
	}

	public void remove(int position) {
		items.remove(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Object item = getItem(position);
		if (item instanceof RuleSet) {
			ViewHolder holder;
			if (convertView == null) {
				holder = null;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder == null) {
				convertView = mInflater.inflate(R.layout.list_rule_sets_item, null);
				holder = new ViewHolder();
				holder.ruleCountryName = (TextView) convertView.findViewById(R.id.list_rule_sets_item_country_name);
				holder.ruleCountryCode = (TextView) convertView.findViewById(R.id.list_rule_sets_item_country_code);
				holder.ruleVersion = (TextView) convertView.findViewById(R.id.list_rule_sets_item_version);
				holder.install = (Button) convertView.findViewById(R.id.list_rule_sets_item_install);
				holder.update = (Button) convertView.findViewById(R.id.list_rule_sets_item_update);
				holder.remove = (Button) convertView.findViewById(R.id.list_rule_sets_item_remove);
				convertView.setTag(holder);
			}

			holder.install.setOnClickListener(new InstallRuleOnClickListener(position, this));
			holder.update.setOnClickListener(new UpdateRuleOnClickListener(position, this));
			holder.remove.setOnClickListener(new RemoveRuleOnClickListener(position, this));

			RuleSet ruleSet = (RuleSet) getItem(position);

			holder.ruleCountryName.setText(ruleSet.getCountryName());
			holder.ruleCountryCode.setText(ruleSet.getCountryCode().toUpperCase());
			String version = String.valueOf(Math.max(ruleSet.getLocalVersion(), ruleSet.getGlobalVersion()));
			holder.ruleVersion.setText(context.getString(R.string.version) + version);

			if (ruleSet.getLocalVersion() == 0) {
				holder.install.setEnabled(true);
				holder.update.setEnabled(false);
				holder.remove.setEnabled(false);
			} else if (ruleSet.getGlobalVersion() == 0) {
				holder.install.setEnabled(false);
				holder.update.setEnabled(false);
				holder.remove.setEnabled(true);
			} else if (ruleSet.getGlobalVersion() > ruleSet.getLocalVersion()) {
				holder.install.setEnabled(false);
				holder.update.setEnabled(true);
				holder.remove.setEnabled(true);
			} else {
				holder.install.setEnabled(false);
				holder.update.setEnabled(false);
				holder.remove.setEnabled(true);
			}
		} else if (item instanceof CheckForUpdatesAuxiliaryItem) {
			convertView = mInflater.inflate(R.layout.list_rule_sets_check_for_updates_item, null);
		}
		return convertView;
	}

	public Context getContext() {
		return context;
	}

	static class ViewHolder {
		TextView ruleCountryName;
		TextView ruleCountryCode;
		TextView ruleVersion;
		Button install;
		Button update;
		Button remove;
	}

	public static enum Hint {
		SHOWING_LOCAL_RULES, SHOWING_GLOBAL_RULES
	}

	public static class CheckForUpdatesAuxiliaryItem {

	}

	public static class InstallRuleOnClickListener implements View.OnClickListener {

		private int position;
		private RulesAdapter adapter;

		public InstallRuleOnClickListener(int position, RulesAdapter adapter) {
			this.position = position;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			final ProgressDialog progressDialog = ProgressDialog.show(adapter.getContext(), null, adapter.getContext()
					.getString(R.string.installing_rules));
			new AsyncTask<Void, Void, InternetConnenctionException>() {

				@Override
				protected InternetConnenctionException doInBackground(Void... params) {
					RuleSet ruleSet = (RuleSet) adapter.getItem(position);

					int localVersion = ruleSet.getLocalVersion();
					ruleSet.setLocalVersion(ruleSet.getGlobalVersion());

					try {
						RuleSetDAO.installRuleSet(adapter.getContext(), ruleSet);
					} catch (InternetConnenctionException e) {
						ruleSet.setLocalVersion(localVersion);
						return e;
					}
					return null;
				}

				@Override
				protected void onPostExecute(InternetConnenctionException exception) {
					adapter.notifyDataSetChanged();
					progressDialog.dismiss();
					
					if (exception != null) {
						Util.notifyUser(adapter.getContext(), MessageType.INTERNET_CONECTION_PROBLEM, R.string.install_rules_internet_connection_problem);
					}
				};

			}.execute();
		}

	}

	public static class UpdateRuleOnClickListener implements View.OnClickListener {

		private int position;
		private RulesAdapter adapter;

		public UpdateRuleOnClickListener(int position, RulesAdapter adapter) {
			this.position = position;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			final ProgressDialog progressDialog = ProgressDialog.show(adapter.getContext(), null, adapter.getContext()
					.getString(R.string.updating_rules));
			new AsyncTask<Void, Void, InternetConnenctionException>() {

				@Override
				protected InternetConnenctionException doInBackground(Void... params) {
					RuleSet ruleSet = (RuleSet) adapter.getItem(position);
					try {
						RuleSetDAO.updateRuleSet(adapter.getContext(), ruleSet);
					} catch (InternetConnenctionException e) {
						adapter.remove(position);
						return e;
					}
					return null;
				}

				@Override
				protected void onPostExecute(InternetConnenctionException exception) {
					
					adapter.notifyDataSetChanged();
					progressDialog.dismiss();
					
					if(exception != null){
						Util.notifyUser(adapter.getContext(), MessageType.INTERNET_CONECTION_PROBLEM, R.string.updating_rules_internet_connection_problem);
					}
				};

			}.execute();
		}

	}

	public static class RemoveRuleOnClickListener implements View.OnClickListener {

		private int position;
		private RulesAdapter adapter;

		public RemoveRuleOnClickListener(int position, RulesAdapter adapter) {
			this.position = position;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			final ProgressDialog progressDialog = ProgressDialog.show(adapter.getContext(), null, adapter.getContext()
					.getString(R.string.removing_rules));
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					RuleSet ruleSet = (RuleSet) adapter.getItem(position);
					RuleSetDAO.removeRuleSet(adapter.getContext(), ruleSet);
					if (adapter.getHint() == Hint.SHOWING_LOCAL_RULES) {
						adapter.remove(position);
					} else {
						ruleSet.setLocalVersion(0);
						ruleSet.setFileName(null);
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					adapter.notifyDataSetChanged();
					progressDialog.dismiss();
				};

			}.execute();
		}

	}

}
