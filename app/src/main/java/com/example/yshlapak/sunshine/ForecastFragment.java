package com.example.yshlapak.sunshine;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.yshlapak.sunshine.data.WeatherContract;

/**
 * Created by y.shlapak on Apr 06, 2015.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ForecastAdapter mForecastAdapter;

    private static final int FORECAST_LOADER = 0;
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }
}