package com.actionbarsherlock.widget;


import android.app.Application;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;


public abstract class IntentChooserFactory<T extends Parcelable> implements IntentChooserFactoryToSVGs, IntentChooserFactoryToStrings {

	public static class BooleanParcelable implements Parcelable {
		public static final BooleanParcelableCreator	CREATOR	= new BooleanParcelableCreator();


		public boolean									value	= false;


		public BooleanParcelable(final boolean value) {
			this.value = value;
		}


		@Override
		public int describeContents() {
			return 0;
		}


		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			CREATOR.writeToParcel(dest, this);
		}
	}


	public static class BooleanParcelableCreator implements Parcelable.Creator<BooleanParcelable> {
		@Override
		public BooleanParcelable createFromParcel(final Parcel source) {
			final int value = source.readInt();
			if (value == -1) {
				return null;
			}

			return new BooleanParcelable(value == 1);
		}


		@Override
		public BooleanParcelable[] newArray(final int size) {
			return new BooleanParcelable[size];
		}


		public void writeToParcel(final Parcel dest, final BooleanParcelable value) {
			if (null == value) {
				dest.writeInt(-1);
			}
			else {
				dest.writeInt(value.value ? 1 : 0);
			}
		}
	}


	/**
	 * Placeholder Parcelable to use when we don't have data to serialize
	 * 
	 * @author glenn
	 */
	public static class VoidParcelable implements Parcelable {
		@Override
		public int describeContents() {
			return 0;
		}


		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			throw new IllegalAccessError();
		}
	}


	/**
	 * Parcelable.Creator for {@link VoidParcelable}
	 * 
	 * @author glenn
	 */
	public static class VoidParcelableCreator implements Parcelable.Creator<VoidParcelable> {
		@Override
		public VoidParcelable createFromParcel(final Parcel source) {
			throw new IllegalAccessError();
		}


		@Override
		public VoidParcelable[] newArray(final int size) {
			throw new IllegalAccessError();
		}
	}


	public IntentChooserValue createIntentChooserBlack(final Application application, final T... params) {
		final Intent intent = toIntent(application, params);
		if (null == intent) {
			throw new IllegalStateException("Null intent from toIntent!");
		}

		final int svg_raw = toSvgBlack(application);
		if (0 == svg_raw) {
			throw new IllegalStateException("0 svg_raw from toSvgBlack!");
		}

		final String title = toTitle(application);
		if (null == title) {
			throw new IllegalStateException("Null title from toTitle!");
		}

		return new IntentChooserValue(svg_raw, title, intent);
	}


	public IntentChooserValue createIntentChooserProduct(final Application application, final T... params) {
		final Intent intent = toIntent(application, params);
		if (null == intent) {
			throw new IllegalStateException("Null intent from toIntent!");
		}

		final int svg_raw = toSvgProduct(application);
		if (0 == svg_raw) {
			throw new IllegalStateException("0 svg_raw from toSvgProduct!");
		}

		final String title = toTitle(application);
		if (null == title) {
			throw new IllegalStateException("Null title from toTitle!");
		}

		return new IntentChooserValue(svg_raw, title, intent);
	}


	public IntentChooserValue createIntentChooserWhite(final Application application, final T... params) {
		final Intent intent = toIntent(application, params);
		if (null == intent) {
			throw new IllegalStateException("Null intent from toIntent!");
		}

		final int svg_raw = toSvgWhite(application);
		if (0 == svg_raw) {
			throw new IllegalStateException("0 svg_raw from toSvgWhite!");
		}

		final String title = toTitle(application);
		if (null == title) {
			throw new IllegalStateException("Null title from toTitle!");
		}

		return new IntentChooserValue(svg_raw, title, intent);
	}


	public abstract Intent toIntent(Application application, T... params);


	@Override
	public abstract String toSubtext(Application application);


	@Override
	public abstract int toSvgBlack(Application application);


	@Override
	public abstract int toSvgProduct(Application application);


	@Override
	public abstract int toSvgWhite(Application application);


	@Override
	public abstract String toTitle(Application application);
}
