/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.evangelion.nervhome;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

public class DeleteZone extends ImageView implements DropTarget, DragController.DragListener {
	private static final int POSITION_NONE = 0;
    private static final int POSITION_TOP = 1;
    private static final int POSITION_BOTTOM = 2;
    private static final int POSITION_TOP_SHRINK = 3;
    private static final int POSITION_BOTTOM_SHRINK = 4;
    private static final int TRANSITION_DURATION = 250;
    private static final int ANIMATION_DURATION = 200;

    private final int[] mLocation = new int[2];

    private Launcher mLauncher;
    private boolean mTrashMode;

    private AnimationSet mInAnimation;
    private AnimationSet mOutAnimation;

    private int mPosition=-1;
    private DragLayer mDragLayer;

    private final RectF mRegion = new RectF();
    private TransitionDrawable mTransition;
	
	public DeleteZone(Context context) {
		super(context);
	}

    public DeleteZone(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteZone(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeleteZone, defStyle, 0);
        a.recycle();
    }

    /*@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTransition = (TransitionDrawable) getBackground();
    }*/

    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
        return mPosition!=POSITION_NONE;
    }

    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo, Rect recycle) {
        return null;
    }

    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
    	//mLauncher.mr();

    	final ItemInfo item = (ItemInfo) dragInfo;

        if (item.container == -1) return;

        final LauncherModel model = Launcher.getModel();
        if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            if (item instanceof LauncherAppWidgetInfo) {
                model.removeDesktopAppWidget((LauncherAppWidgetInfo) item);
            } else {
                model.removeDesktopItem(item);
            }
        } else {
            if (source instanceof UserFolder) {
                final UserFolder userFolder = (UserFolder) source;
                final UserFolderInfo userFolderInfo = (UserFolderInfo) userFolder.getInfo();
                model.removeUserFolderItem(userFolderInfo, item);
            }
        }
        if (item instanceof UserFolderInfo) {
            final UserFolderInfo userFolderInfo = (UserFolderInfo)item;
            LauncherModel.deleteUserFolderContentsFromDatabase(mLauncher, userFolderInfo);
            model.removeUserFolder(userFolderInfo);
        } else if (item instanceof LauncherAppWidgetInfo) {
            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            final LauncherAppWidgetHost appWidgetHost = mLauncher.getAppWidgetHost();
            mLauncher.getWorkspace().unbindWidgetScrollableId(launcherAppWidgetInfo.appWidgetId);
            if (appWidgetHost != null) {
                appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
            }
        }
        LauncherModel.deleteItemFromDatabase(mLauncher, item);
    }
    
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo){
        mTransition.reverseTransition(TRANSITION_DURATION);
    }
    
    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
    }
    
    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo){
        mTransition.reverseTransition(TRANSITION_DURATION);
    }
    
    public void onDragStart(View v, DragSource source, Object info, int dragAction) {
        if(mPosition==-1){
            int position = POSITION_TOP;
            setPosition(position);
        }
        final ItemInfo item = (ItemInfo) info;
        if (item != null) {
            mTrashMode = true;
            createAnimations();
            
            final int[] location = mLocation;
            getLocationOnScreen(location);
            if(mPosition==POSITION_BOTTOM_SHRINK){
                mLauncher.getWorkspace().setPadding(0, 0, 0, getHeight());
                mLauncher.setDockPadding(getHeight());
            }else if(mPosition==POSITION_TOP_SHRINK){
                mLauncher.getWorkspace().setPadding(0, getHeight(),0,0);
                mLauncher.setDockPadding(0);
            }
            mLauncher.getWorkspace().requestLayout();
            mRegion.set(location[0], location[1], location[0] + getRight() - getLeft(),
                    location[1] + getBottom() - getTop());
            mDragLayer.setDeleteRegion(mRegion);
            mTransition.resetTransition();
            startAnimation(mInAnimation);
            setVisibility(VISIBLE);
        }
    }
    
    public void onDragEnd() {
        if (mTrashMode) {
            mTrashMode = false;
            mDragLayer.setDeleteRegion(null);
            startAnimation(mOutAnimation);
            setVisibility(INVISIBLE);
            mLauncher.getWorkspace().setPadding(0, 0, 0, 0);
            mLauncher.setDockPadding(0);
            mLauncher.getWorkspace().requestLayout();
        }
    }
    
    private void createAnimations() {
        if (mInAnimation == null) {
            mInAnimation = new FastAnimationSet();
            final AnimationSet animationSet = mInAnimation;
            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            if (mPosition == POSITION_TOP) {
                animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f));
            } else {
                animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f));
            }
            animationSet.setDuration(ANIMATION_DURATION);
        }
        if (mOutAnimation == null) {
            mOutAnimation = new FastAnimationSet();
            final AnimationSet animationSet = mOutAnimation;
            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            if (mPosition == POSITION_TOP) {
                animationSet.addAnimation(new FastTranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f));
            } else {
                animationSet.addAnimation(new FastTranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f));
            }
            animationSet.setDuration(ANIMATION_DURATION);
        }
    	
    }
    
    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    
    void setDragController(DragLayer dragLayer) {
        mDragLayer = dragLayer;
    }
    
    private static class FastTranslateAnimation extends TranslateAnimation {
        public FastTranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue,
                int fromYType, float fromYValue, int toYType, float toYValue) {
            super(fromXType, fromXValue, toXType, toXValue,
                    fromYType, fromYValue, toYType, toYValue);
        }

        @Override
        public boolean willChangeTransformationMatrix() {
            return true;
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }
    }

    private static class FastAnimationSet extends AnimationSet {
        FastAnimationSet() {
            super(false);
        }

        @Override
        public boolean willChangeTransformationMatrix() {
            return true;
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }
    }

	@Override
	public void setBackground(Drawable d) {
		// TODO Auto-generated method stub
		super.setBackground(d);
        mTransition = (TransitionDrawable) d;
	}
	public void setPosition(int position){
	    if(position!=mPosition){
	        mPosition=position;
	        FrameLayout.LayoutParams params=(LayoutParams) getLayoutParams();
	        if(mPosition==POSITION_TOP||mPosition==POSITION_TOP_SHRINK) {
	            params.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
	        }else if(mPosition==POSITION_BOTTOM||mPosition==POSITION_BOTTOM_SHRINK){
	            params.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
	        }
	        mInAnimation=null;
	        mOutAnimation=null;
	        setLayoutParams(params);
	    }
	}
}
