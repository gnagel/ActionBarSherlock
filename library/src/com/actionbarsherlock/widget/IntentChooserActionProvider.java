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


import android.content.Context;
import android.view.View;


import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.SubMenu;


public abstract class IntentChooserActionProvider extends ActionProvider {
	private IntentChooserView										intentChooserView	= null;


	/**
	 * Context for accessing resources.
	 */
	private final Context											mContext;


	private final ArrayList<IntentChooserView.IntentChooserValue>	values				= new ArrayList<IntentChooserView.IntentChooserValue>();


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


	public abstract int getExpandActivityOverflowButtonContentDescription();


	public abstract int getExpandActivityOverflowButtonSvg();


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasSubMenu() {
		return true;
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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onPrepareSubMenu(final SubMenu subMenu) {
		throw new IllegalStateException();
	}


	public final void setData(final List<IntentChooserView.IntentChooserValue> values) {
		this.values.clear();
		this.values.addAll(values);

		if (null != intentChooserView) {
			intentChooserView.setData(this.values);
		}
	}
}
