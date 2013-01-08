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


import java.util.ArrayList;
import java.util.List;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;


import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.SubMenu;


public abstract class IntentChooserActionProvider extends ActionProvider {
	private IntentChooserView					intentChooserView	= null;


	/**
	 * Context for accessing resources.
	 */
	private final Context						mContext;


	private final ArrayList<IntentChooserValue>	values				= new ArrayList<IntentChooserValue>();


	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 *            Context for accessing resources.
	 */
	public IntentChooserActionProvider(final Context context) {
		super(context);
		mContext = context;
	}


	public final int getCount() {
		return this.values.size();
	}


	public abstract Intent getExpandActivityDefaultIntent();


	public abstract int getExpandActivityOverflowButtonContentDescription();


	public abstract int getExpandActivityOverflowButtonSvg();


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasSubMenu() {
		return false; // Always return false & execute onPerformDefaultAction() instead
	}


	public final boolean isEmpty() {
		return this.values.isEmpty();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final View onCreateActionView() {
		// Create the view and set its data model.
		this.intentChooserView = new IntentChooserView(mContext);
		this.intentChooserView.setData(values);

		intentChooserView.setExpandActivityOverflowButtonSvg(getExpandActivityOverflowButtonSvg());

		// Set content description.
		intentChooserView.setExpandActivityOverflowButtonContentDescription(getExpandActivityOverflowButtonContentDescription());

		return intentChooserView;
	}


	@Override
	public boolean onPerformDefaultAction() {
		final Intent intent = getExpandActivityDefaultIntent();
		if (null != intent) {
			mContext.startActivity(intent);
			return true;
		}

		return super.onPerformDefaultAction();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onPrepareSubMenu(final SubMenu subMenu) {
		// Disabled when hasSubMenu() == false
		throw new IllegalStateException();
	}


	/**
	 * Refresh the contents of the ActionBar's ActionProvider.
	 * Example: a background task has finished downloading your <bag of data>, refresh the menu with the new options.
	 * 
	 * @param application
	 */
	public abstract void onRefresh(final Application application);


	public final void setData(final List<IntentChooserValue> values) {
		this.values.clear();
		this.values.addAll(values);

		if (null != intentChooserView) {
			intentChooserView.setData(this.values);
		}
	}
}
