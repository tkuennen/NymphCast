package com.nyanko.nymphcastplayer.ui.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nyanko.nymphcastplayer.MainActivity;
import com.nyanko.nymphcastplayer.R;
import com.nyanko.nymphcastplayer.ui.main.MediaContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MediaFragment extends Fragment {

	// TODO: Customize parameter argument names
	private static final String ARG_COLUMN_COUNT = "column-count";
	// TODO: Customize parameters
	private int mColumnCount = 1;
	private OnListFragmentInteractionListener mListener;
	public static MediaRecyclerViewAdapter mAdapter;
	public ArrayList<Audio> audioList;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MediaFragment() {
	}

	// TODO: Customize parameter initialization
	@SuppressWarnings("unused")
	public static MediaFragment newInstance(int columnCount) {
		MediaFragment fragment = new MediaFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_media_list, container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}

			mAdapter = new MediaRecyclerViewAdapter(MediaContent.ITEMS, mListener);
			recyclerView.setAdapter(mAdapter);
		}


		// Load the local media list.
		// TODO: for new just load audio files.
		loadAudio();
		mAdapter.notifyDataSetChanged();

		return view;
	}

	public void loadAudio() {
		Context appContext = MainActivity.getContextOfApplication();
		ContentResolver contentResolver = appContext.getContentResolver();

		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
		String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
		Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

		if (cursor != null && cursor.getCount() > 0) {
			audioList = new ArrayList<Audio>();
			MediaContent.ITEMS.clear();
			audioList.clear();
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				//Uri data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

				Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

				// Save to audioList and MediaContent.
				audioList.add(new Audio(contentUri, title, album, artist));
				MediaContent.ITEMS.add(new MediaContent.MediaItem(title, artist, album, contentUri));
			}
		}

		if (cursor != null) {
			cursor.close();
		}
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	/* public void dataSetChanged() {
		mAdapter.notifyDataSetChanged();
	} */

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnListFragmentInteractionListener {
		// TODO: Update argument type and name
		void onListFragmentInteraction(MediaContent.MediaItem item);
	}
}
