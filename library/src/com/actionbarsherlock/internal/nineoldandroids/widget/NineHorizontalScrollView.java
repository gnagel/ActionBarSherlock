package com.actionbarsherlock.internal.nineoldandroids.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;


import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;


public class NineHorizontalScrollView extends HorizontalScrollView {
	private final AnimatorProxy	mProxy;


	public NineHorizontalScrollView(final Context context) {
		super(context);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}


	public NineHorizontalScrollView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}


	public NineHorizontalScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}


	@Override
	public float getAlpha() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getAlpha();
		}
		else {
			return super.getAlpha();
		}
	}


	@Override
	public void setAlpha(final float alpha) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setAlpha(alpha);
		}
		else {
			super.setAlpha(alpha);
		}
	}


	@Override
	public void setVisibility(final int visibility) {
		if (mProxy != null) {
			if (visibility == GONE) {
				clearAnimation();
			}
			else if (visibility == VISIBLE) {
				setAnimation(mProxy);
			}
		}
		super.setVisibility(visibility);
	}
}
