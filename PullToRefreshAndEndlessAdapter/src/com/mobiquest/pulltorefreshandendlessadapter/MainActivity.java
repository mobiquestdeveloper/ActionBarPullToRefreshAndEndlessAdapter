package com.mobiquest.pulltorefreshandendlessadapter;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;

public class MainActivity extends ActionBarActivity {

	private PullToRefreshAttacher mPullToRefreshAttacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fragment);

		/**
		 * Here we create a PullToRefreshAttacher manually without an Options
		 * instance. PullToRefreshAttacher will manually create one using
		 * default values.
		 */
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

		// Now add ListFragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.ptr_fragment, new SampleListFragment()).commit();
	}

	PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	public static class SampleListFragment extends ListFragment implements
			PullToRefreshAttacher.OnRefreshListener {

		private PullToRefreshAttacher mPullToRefreshAttacher;

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			/**
			 * Set the List Adapter to display the sample items
			 */
			ArrayList<Integer> items = new ArrayList<Integer>();

			for (int i = 0; i < 25; i++) {
				items.add(i);
			}

			setListAdapter(new DemoAdapter(items));

			mPullToRefreshAttacher = ((MainActivity) getActivity())
					.getPullToRefreshAttacher();

			// Set the Refreshable View to be the ListView and the refresh
			// listener to be this.
			mPullToRefreshAttacher.addRefreshableView(getListView(), this);
		}

		@Override
		public void onRefreshStarted(View view) {
			/**
			 * Simulate Refresh with 4 seconds sleep
			 */
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);

					// Notify PullToRefreshAttacher that the refresh has
					// finished
					mPullToRefreshAttacher.setRefreshComplete();
				}
			}.execute();
		}

		class DemoAdapter extends EndlessAdapter {
			DemoAdapter(ArrayList<Integer> list) {
				super((MainActivity) getActivity(),
						new SpecialAdapter(list), R.layout.pending);
			}

			@Override
			protected boolean cacheInBackground() throws Exception {
				SystemClock.sleep(3000); // pretend to do work

				if (getWrappedAdapter().getCount() < 75) {
					return (true);
				}

				throw new Exception("Gadzooks!");
			}

			@Override
			protected void appendCachedData() {
				if (getWrappedAdapter().getCount() < 75) {
					@SuppressWarnings("unchecked")
					ArrayAdapter<Integer> a = (ArrayAdapter<Integer>) getWrappedAdapter();

					for (int i = 0; i < 25; i++) {
						a.add(a.getCount());
					}
				}
			}
		}

		class SpecialAdapter extends ArrayAdapter<Integer> {
			SpecialAdapter(ArrayList<Integer> items) {
				super((MainActivity) getActivity(), R.layout.row,
						android.R.id.text1, items);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row = super.getView(position, convertView, parent);

				// further customize your rows here

				return (row);
			}
		}
	}

}

