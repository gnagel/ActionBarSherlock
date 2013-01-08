/*
 * Copyright (C) 2011 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actionbarsherlock.widget;


import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import com.actionbarsherlock.internal.widget.IcsListPopupWindow;
import com.trevorpage.tpsvg.SVGView;


/**
 * This class is a view for choosing an activity for handling a given {@link Intent}.
 * <p>
 * The view is composed of two adjacent buttons:
 * <ul>
 * <li>The left button is an immediate action and allows one click activity choosing. Tapping this button immediately executes the intent without requiring any further user input. Long press on this button shows a popup for changing the
 * default activity.</li>
 * <li>The right button is an overflow action and provides an optimized menu of additional activities. Tapping this button shows a popup anchored to this view, listing the most frequently used activities. This list is initially limited to a
 * small number of items in frequency used order. The last item, "Show all..." serves as an affordance to display all available activities.</li>
 * </ul>
 * </p>
 * 
 * @hide
 */
public final class IntentChooserView extends ViewGroup implements AdapterView.OnItemClickListener,
		View.OnClickListener, PopupWindow.OnDismissListener,
		OnGlobalLayoutListener {

	static final boolean						IS_HONEYCOMB	= Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;


	/**
	 * The content of this view.
	 */
	private final IcsLinearLayout				mActivityChooserContent;


	/**
	 * Stores the background drawable to allow hiding and latter showing.
	 */
	private final Drawable						mActivityChooserContentBackground;


	/**
	 * An adapter for displaying the activities in an {@link AdapterView}.
	 */
	private final IntentChooserViewArrayAdapter	mAdapter;


	private final Context						mContext;


	/**
	 * The expand activities action button;
	 */
	private final FrameLayout					mExpandActivityOverflowButton;


	/**
	 * The image for the expand activities action button;
	 */
	private final SVGView						mExpandActivityOverflowButtonImage;


	/**
	 * Flag whether this view is attached to a window.
	 */
	private boolean								mIsAttachedToWindow;


	/**
	 * The maximal width of the list popup.
	 */
	private final int							mListPopupMaxWidth;


	/**
	 * Popup window for showing the activity overflow list.
	 */
	private IcsListPopupWindow					mListPopupWindow;


	/**
	 * Listener for the dismissal of the popup/alert.
	 */
	private PopupWindow.OnDismissListener		mOnDismissListener;


	// /**
	// * The ActionProvider hosting this view, if applicable.
	// */
	// ActionProvider mProvider;

	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 */
	public IntentChooserView(final Context context) {
		this(context, null);
	}


	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 * @param attrs
	 *            A collection of attributes.
	 */
	public IntentChooserView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}


	/**
	 * Create a new instance.
	 * 
	 * @param context
	 *            The application environment.
	 * @param attrs
	 *            A collection of attributes.
	 * @param defStyle
	 *            The default style to apply to this view.
	 */
	public IntentChooserView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		final TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.IntentChooserView, defStyle, 0);

		final int expandActivityOverflowButtonSvg = attributesArray.getResourceId(
				R.styleable.IntentChooserView_expandActivityOverflowButtonSvg,
				0);

		attributesArray.recycle();

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.abs__activity_chooser_svgview, this, true);

		mActivityChooserContent = (IcsLinearLayout) findViewById(R.id.abs__activity_chooser_view_content);
		mActivityChooserContentBackground = mActivityChooserContent.getBackground();

		mExpandActivityOverflowButton = (FrameLayout) findViewById(R.id.abs__expand_activities_button);
		mExpandActivityOverflowButton.setOnClickListener(this);
		mExpandActivityOverflowButtonImage = (SVGView) mExpandActivityOverflowButton.findViewById(R.id.abs__image);
		if (0 != expandActivityOverflowButtonSvg) {
			mExpandActivityOverflowButtonImage.setImageSvg(expandActivityOverflowButtonSvg);
		}

		mAdapter = new IntentChooserViewArrayAdapter(context);
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateAppearance();
			}
		});

		final Resources resources = context.getResources();
		mListPopupMaxWidth = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(R.dimen.abs__config_prefDialogWidth));
	}


	/**
	 * Dismisses the popup window with activities.
	 * 
	 * @return True if dismissed, false if already dismissed.
	 */
	public boolean dismissPopup() {
		if (isShowingPopup()) {
			getListPopupWindow().dismiss();
			final ViewTreeObserver viewTreeObserver = getViewTreeObserver();
			if (viewTreeObserver.isAlive()) {
				viewTreeObserver.removeGlobalOnLayoutListener(this);
			}
		}
		return true;
	}


	/**
	 * Gets the list popup window which is lazily initialized.
	 * 
	 * @return The popup.
	 */
	private IcsListPopupWindow getListPopupWindow() {
		if (mListPopupWindow == null) {
			mListPopupWindow = new IcsListPopupWindow(getContext());
			mListPopupWindow.setAdapter(mAdapter);
			mListPopupWindow.setAnchorView(IntentChooserView.this);
			mListPopupWindow.setModal(true);
			mListPopupWindow.setOnItemClickListener(this);
			mListPopupWindow.setOnDismissListener(this);
		}
		return mListPopupWindow;
	}


	/**
	 * Gets whether the popup window with activities is shown.
	 * 
	 * @return True if the popup is shown.
	 */
	public boolean isShowingPopup() {
		return getListPopupWindow().isShowing();
	}


	private void notifyOnDismissListener() {
		if (mOnDismissListener != null) {
			mOnDismissListener.onDismiss();
		}
	}


	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		mIsAttachedToWindow = true;
	}


	// View.OnClickListener
	@Override
	public void onClick(final View view) {
		if (view == mExpandActivityOverflowButton) {
			showPopupUnchecked();
		}
		else {
			throw new IllegalArgumentException();
		}
	}


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		final ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.removeGlobalOnLayoutListener(this);
		}
		mIsAttachedToWindow = false;
	}


	// PopUpWindow.OnDismissListener#onDismiss
	@Override
	public void onDismiss() {
		notifyOnDismissListener();

		// if (mProvider != null) {
		// mProvider.subUiVisibilityChanged(false);
		// }
	}


	@Override
	public void onGlobalLayout() {
		if (isShowingPopup()) {
			if (!isShown()) {
				getListPopupWindow().dismiss();
			}
			else {
				getListPopupWindow().show();

				// if (mProvider != null) {
				// mProvider.subUiVisibilityChanged(true);
				// }
			}
		}
	}


	// AdapterView#OnItemClickListener
	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		dismissPopup();

		final IntentChooserViewArrayAdapter adapter = (IntentChooserViewArrayAdapter) parent.getAdapter();
		adapter.onItemClick(parent, view, position, id);
	}


	@Override
	protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
		mActivityChooserContent.layout(0, 0, right - left, bottom - top);

		if (getListPopupWindow().isShowing()) {
			showPopupUnchecked();
		}
		else {
			dismissPopup();
		}
	}


	@Override
	protected void onMeasure(final int widthMeasureSpec, int heightMeasureSpec) {
		final View child = mActivityChooserContent;
		// If the default action is not visible we want to be as tall as the
		// ActionBar so if this widget is used in the latter it will look as
		// a normal action button.
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);

		measureChild(child, widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());
	}


	public final void setData(final List<IntentChooserValue> values) {
		mAdapter.clear();
		if (IS_HONEYCOMB) {
			mAdapter.addAll(values);
		}
		else {
			for (final IntentChooserValue value : values) {
				mAdapter.add(value);
			}
		}
		updateAppearance();
	}


	/**
	 * Sets the content description for the button that expands the activity
	 * overflow list.
	 * description as a clue about the action performed by the button.
	 * For example, if a share activity is to be chosen the content
	 * description should be something like "Share with".
	 * 
	 * @param resourceId
	 *            The content description resource id.
	 */
	public void setExpandActivityOverflowButtonContentDescription(final int resourceId) {
		final CharSequence contentDescription = mContext.getString(resourceId);
		mExpandActivityOverflowButtonImage.setContentDescription(contentDescription);
	}


	/**
	 * Sets the background for the button that expands the activity
	 * overflow list.
	 * <strong>Note:</strong> Clients would like to set this drawable
	 * as a clue about the action the chosen activity will perform. For
	 * example, if a share activity is to be chosen the drawable should
	 * give a clue that sharing is to be performed.
	 * 
	 * @param drawable
	 *            The drawable.
	 */
	public void setExpandActivityOverflowButtonSvg(final int svg_id) {
		if (0 == svg_id) {
			throw new IllegalArgumentException("Invalid SVG Raw ID: " + svg_id);
		}
		mExpandActivityOverflowButtonImage.setImageSvg(svg_id);
	}


	/**
	 * Sets a listener to receive a callback when the popup is dismissed.
	 * 
	 * @param listener
	 *            The listener to be notified.
	 */
	public void setOnDismissListener(final PopupWindow.OnDismissListener listener) {
		mOnDismissListener = listener;
	}


	// /**
	// * Set the provider hosting this view, if applicable.
	// *
	// * @hide Internal use only
	// */
	// public void setProvider(final ActionProvider provider) {
	// mProvider = provider;
	// }

	/**
	 * Shows the popup window with activities.
	 * 
	 * @return True if the popup was shown, false if already showing.
	 */
	public boolean showPopup() {
		if (isShowingPopup() || !mIsAttachedToWindow) {
			return false;
		}
		showPopupUnchecked();
		return true;
	}


	/**
	 * Shows the popup no matter if it was already showing.
	 * 
	 * @param maxActivityCount
	 *            The max number of activities to display.
	 */
	private void showPopupUnchecked() {
		if (mAdapter.isEmpty()) {
			throw new IllegalStateException("No data in model. Did you call #setDataModel?");
		}

		getViewTreeObserver().addOnGlobalLayoutListener(this);

		final IcsListPopupWindow popupWindow = getListPopupWindow();
		if (!popupWindow.isShowing()) {
			final int contentWidth = Math.min(mAdapter.measureContentWidth(), mListPopupMaxWidth);
			popupWindow.setContentWidth(contentWidth);
			popupWindow.show();

			// if (mProvider != null) {
			// mProvider.subUiVisibilityChanged(true);
			// }

			popupWindow.getListView().setContentDescription(mContext.getString(R.string.abs__activitychooserview_choose_application));
		}
	}


	/**
	 * Updates the buttons state.
	 */
	private void updateAppearance() {
		// Expand overflow button.
		if (mAdapter.getCount() > 0) {
			mExpandActivityOverflowButton.setEnabled(true);
		}
		else {
			mExpandActivityOverflowButton.setEnabled(false);
		}

		mActivityChooserContent.setBackgroundDrawable(null);
		mActivityChooserContent.setPadding(0, 0, 0, 0);
	}
}


/**
 * Adapter for backing the list of activities shown in the popup.
 */
final class IntentChooserViewArrayAdapter extends ArrayAdapter<IntentChooserValue> implements AdapterView.OnItemClickListener {
	public IntentChooserViewArrayAdapter(final Context context) {
		super(context, 0);
	}


	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null || convertView.getId() != R.id.abs__list_item) {
			convertView = View.inflate(getContext(), R.layout.abs__activity_chooser_svgview_list_item, null);
		}

		final SVGView iconView = (SVGView) convertView.findViewById(R.id.abs__icon);
		iconView.setImageSvg(getItem(position).raw_svg);

		final TextView textView = (TextView) convertView.findViewById(R.id.abs__title);
		textView.setText(getItem(position).title);

		return convertView;
	}


	public int measureContentWidth() {
		// The user may have specified some of the target not to be shown but we
		// want to measure all of them since after expansion they should fit.
		int contentWidth = 0;
		View itemView = null;

		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int count = getCount();

		for (int i = 0; i < count; i++) {
			itemView = getView(i, itemView, null);
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			contentWidth = Math.max(contentWidth, itemView.getMeasuredWidth());
		}

		return contentWidth;
	}


	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final IntentChooserValue item = getItem(position);
		if (null == item.intent) {
			throw new IllegalStateException("Null intent for '" + item.title + "'");
		}

		getContext().startActivity(item.intent);
	}
}
