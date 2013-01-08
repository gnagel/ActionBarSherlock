package com.actionbarsherlock.widget;


import android.content.Intent;


public final class IntentChooserValue {
	public final Intent	intent;


	public final int	raw_svg;


	public final String	title;


	public IntentChooserValue(final int raw_svg, final String title, final Intent intent) {
		super();
		this.raw_svg = raw_svg;
		this.title = title;
		this.intent = intent;
	}
}
