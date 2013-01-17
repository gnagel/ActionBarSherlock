package com.actionbarsherlock.widget;


import java.util.ArrayList;


import android.app.Application;
import android.content.Intent;
import android.os.Parcelable;


public abstract class IntentChooserFactoryPairList<K extends Parcelable, T extends Parcelable> {
	public IntentChooserValue createIntentChooserBlack(final Application application, final K key, final ArrayList<T> values) {
		final Intent intent = toIntent(application, key, values);
		if (null == intent) {
			throw new IllegalStateException("Null intent from toIntent!");
		}

		final int svg_raw = toSvgBlack(application);
		if (0 == svg_raw) {
			throw new IllegalStateException("0 svg_raw from toSvgWhite!");
		}

		final String title = toTitle(application, key, values);
		if (null == title) {
			throw new IllegalStateException("Null title from toTitle!");
		}

		return new IntentChooserValue(svg_raw, title, intent);
	}


	public IntentChooserValue createIntentChooserWhite(final Application application, final K key, final ArrayList<T> values) {
		final Intent intent = toIntent(application, key, values);
		if (null == intent) {
			throw new IllegalStateException("Null intent from toIntent!");
		}

		final int svg_raw = toSvgWhite(application);
		if (0 == svg_raw) {
			throw new IllegalStateException("0 svg_raw from toSvgWhite!");
		}

		final String title = toTitle(application, key, values);
		if (null == title) {
			throw new IllegalStateException("Null title from toTitle!");
		}

		return new IntentChooserValue(svg_raw, title, intent);
	}


	public abstract Intent toIntent(Application application, K key, final ArrayList<T> values);


	public abstract String toSubtext(final Application application, final K key, final ArrayList<T> values);


	public abstract int toSvgBlack(Application application);


	public abstract int toSvgWhite(Application application);


	public abstract String toTitle(Application application, K key, final ArrayList<T> values);
}
