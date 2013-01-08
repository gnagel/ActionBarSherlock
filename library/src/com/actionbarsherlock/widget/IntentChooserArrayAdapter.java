package com.actionbarsherlock.widget;


import java.util.List;


import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.actionbarsherlock.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.trevorpage.tpsvg.SVGView;


public abstract class IntentChooserArrayAdapter extends ArrayAdapter<IntentChooserValue> implements OnNavigationListener {

	static final boolean	IS_HONECOMB	= android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;


	public IntentChooserArrayAdapter(final Context context) {
		super(context, 0);
	}


	@Override
	public final View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		final ViewGroup root = (ViewGroup) (
				null != convertView && convertView.getId() == R.id.abs__spinner_item
						? convertView
						: View.inflate(getContext(), R.layout.sherlock_svgspinner_dropdown_item, null)
				);

		final SVGView svgView = (SVGView) root.findViewById(R.id.abs__icon);
		final TextView textView = (TextView) root.findViewById(R.id.abs__title);

		final IntentChooserValue value = getItem(position);
		if (null == value) {
			throw new IllegalStateException("No IntentChooserValue available for position=" + position);
		}

		if (0 != value.raw_svg) {
			svgView.setImageSvg(value.raw_svg);
			svgView.setVisibility(View.VISIBLE);
		}
		else {
			svgView.setVisibility(View.GONE);
		}
		textView.setText(value.title);

		return root;
	}


	@Override
	public final View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView textView = (TextView) (
				null != convertView && convertView instanceof TextView
						? convertView
						: View.inflate(getContext(), R.layout.sherlock_svgspinner_item, null)
				);

		final IntentChooserValue value = getItem(position);
		if (null == value) {
			throw new IllegalStateException("No IntentChooserValue available for position=" + position);
		}

		textView.setText(value.title);

		return textView;
	}


	@Override
	public final boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		final IntentChooserValue value = getItem(itemPosition);
		if (null == value) {
			throw new IllegalStateException("No IntentChooserValue available for position=" + itemPosition);
		}

		// Exit early
		if (0 == value.raw_svg && null == value.intent) {
			return true;
		}

		if (null == value.intent) {
			throw new IllegalStateException("Null Intent for IntentChooserValue at position=" + itemPosition + ", title=" + value.title);
		}

		getContext().startActivity(value.intent);

		return true;
	}


	public abstract void onRefresh(Application application, ActionBar action_bar);


	public void setData(final List<IntentChooserValue> values) {
		clear();

		if (IS_HONECOMB) {
			addAll(values);
		}
		else {
			for (final IntentChooserValue value : values) {
				add(value);
			}
		}

		notifyDataSetChanged();
	}
}
