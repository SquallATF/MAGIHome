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

import java.util.ArrayList;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import jp.co.evangelion.nervhome.FlingGesture.FlingListener;
import jp.co.evangelion.nervhome.extended.ScreenItemView;
import jp.co.evangelion.nervhome.selector.C148b;
import jp.co.evangelion.nervhome.selector.ScreenSelector;
import mobi.intuitit.android.widget.WidgetSpace;
import android.app.Activity;
import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * The workspace is a wide area with a wallpaper and a finite number of screens. Each
 * screen contains a number of icons, folders or widgets the user can interact with.
 * A workspace is meant to be used with a fixed width only.
 */
public class Workspace extends WidgetSpace implements Interfacel, DropTarget, DragSource, DragScroller, FlingListener, C148b, MultiTouchObjectCanvas<Object> {
    private static final int INVALID_SCREEN = -1;

    private int mDefaultScreen;


    private boolean mFirstLayout = true;

    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int TOUCH_SWIPE_DOWN_GESTURE = 2;
    private final static int TOUCH_SWIPE_UP_GESTURE = 3;

    private int mTouchState = TOUCH_STATE_REST;

    private OnLongClickListener mLongClickListener;

    private Launcher mLauncher;
    private DragController mDragger;

    private boolean mTouchedScrollableWidget = false;
    

    /**
     * Cache of vacant cells, used during drag events and invalidated as needed.
     */
    private CellLayout.CellInfo mVacantCache = null;

    private final int[] mTempCell = new int[2];
    private final int[] mTempEstimate = new int[2];


    //private boolean mAllowLongPress;
    private boolean mLocked;

    private int mTouchSlop;
 
    final Rect mDrawerBounds = new Rect();
    final Rect mClipBounds = new Rect();
    
    int numc;
    
    boolean flagd = false;    
    
    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private boolean flagl = true;
    private CustomScroller mScroller;
    private final FlingGesture mFlingGesture;

    /**
     * CellInfo for the cell that is currently being dragged
     */
    private CellLayout.CellInfo mDragInfo;

    /**
     * Target drop area calculated during last acceptDrop call.
     */
    private int[] mTargetCell = null;

    //rogro82@xda
	public static final int mHomeScreens = 7;
    //int mHomeScreensLoaded = 0;
    //ADW: port from donut wallpaper drawing
    private Paint mPaint;

    //ADW: sense zoom constants
	private static final int SENSE_OPENING = 1;
	private static final int SENSE_CLOSING = 2;
	private static final int SENSE_OPEN = 3;
	private static final int SENSE_CLOSED = 4;
    //ADW: sense zoom variables
	private boolean mSensemode=false;
	private int mStatus=SENSE_CLOSED;
	private final int mAnimationDuration=400;

	//Wysie: Multitouch controller
	private MultiTouchController<Object> multiTouchController;
	// Wysie: Values taken from CyanogenMod (Donut era) Browser
	private static final double ZOOM_SENSITIVITY = 1.6;
	private static final double ZOOM_LOG_BASE_INV = 1.0 / Math.log(2.0 / ZOOM_SENSITIVITY);
	private ScreenSelector mScreenSelector;
	private boolean flagJ = false;
	private boolean flagL = false;
	private boolean flagM = false;
	private int N;
	//ADW: custom desktop rows/columns
	private static final int mDesktopRows = 6;
	private static final int mDesktopColumns = 4;
    
	/**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     */
	public Workspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     * @param defStyle Unused.
     */
    public Workspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mDefaultScreen = 3;
        if(mDefaultScreen>mHomeScreens-1) mDefaultScreen=0;

        //ADW: create desired screens programatically
        LayoutInflater layoutInflter=LayoutInflater.from(context);
        for(int i=0;i<mHomeScreens;i++){
        	CellLayout screen=(CellLayout)layoutInflter.inflate(R.layout.workspace_screen, this, false);
        	addView(screen);
        }
        mFlingGesture = new FlingGesture();
        mFlingGesture.setListener(this);
        initWorkspace();
    }

    /**
     * Initializes various states for this workspace.
     */
    private void initWorkspace() {
        mScroller = new CustomScroller(getContext(), new ElasticInterpolator(0f));
        mScroller.setInterfacel(this);
        mCurrentScreen = mDefaultScreen;
        Launcher.setScreen(mCurrentScreen);
        mPaint=new Paint();
        mPaint.setDither(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        //Wysie: Use MultiTouchController only for multitouch events
        multiTouchController = new MultiTouchController<Object>(this, false);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        /* Rogro82@xda Extended : Only load the number of home screens set */
        //if(mHomeScreensLoaded < mHomeScreens){
            //mHomeScreensLoaded++;
            super.addView(child, index, params);
        //}
    }

    @Override
    public void addView(View child) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, params);
    }

    /**
     * @return The open folder on the current screen, or null if there is none
     */
    Folder getOpenFolder() {
        CellLayout currentScreen = (CellLayout) getChildAt(getCurrentScreen());
        if(currentScreen==null)return null;
        int count = currentScreen.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = currentScreen.getChildAt(i);
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
            if (lp.cellHSpan == mDesktopColumns && lp.cellVSpan == mDesktopRows && child instanceof Folder) {
                return (Folder) child;
            }
        }
        return null;
    }

    ArrayList<Folder> getOpenFolders() {
        final int screens = getChildCount();
        ArrayList<Folder> folders = new ArrayList<Folder>(screens);

        for (int screen = 0; screen < screens; screen++) {
            CellLayout currentScreen = (CellLayout) getChildAt(screen);
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                if (lp.cellHSpan == mDesktopColumns && lp.cellVSpan == mDesktopRows && child instanceof Folder) {
                    folders.add((Folder) child);
                    break;
                }
            }
        }

        return folders;
    }

    boolean isDefaultScreenShowing() {
        return getCurrentScreen() == mDefaultScreen;
    }

    /**
     * Returns the index of the currently displayed screen.
     *
     * @return The index of the currently displayed screen.
     */
    public int getCurrentScreen() {
        return mpi(mCurrentScreen);
    }

    /**
     * Sets the current screen.
     *
     * @param currentScreen
     */
    void setCurrentScreen(int currentScreen) {
        clearVacantCache();
        int screen = currentScreen - mpi(mCurrentScreen);
        int childCount = getChildCount();
        if( childCount/2 < Math.abs(screen)){
        	if (screen > 0) {
        		screen -= childCount;
        	} else {
        		screen += childCount;
        	}
        }
        mCurrentScreen = screen + mCurrentScreen;
        scrollTo(mCurrentScreen * getWidth(), 0);

        mLauncher.getDesktopIndicator().indicate(Mpb(mScroller.getCurrX(),
        		getWidth() * getChildCount()));
        invalidate();
    }

    /**
     * Adds the specified child in the current screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     */
    void addInCurrentScreen(View child, int x, int y, int spanX, int spanY) {
        addInScreen(child, getCurrentScreen(), x, y, spanX, spanY, false);
    }

    /**
     * Adds the specified child in the current screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     * @param insert When true, the child is inserted at the beginning of the children list.
     */
    void addInCurrentScreen(View child, int x, int y, int spanX, int spanY, boolean insert) {
        addInScreen(child, getCurrentScreen(), x, y, spanX, spanY, insert);
    }

    /**
     * Adds the specified child in the specified screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param screen The screen in which to add the child.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     */
    void addInScreen(View child, int screen, int x, int y, int spanX, int spanY) {
        addInScreen(child, screen, x, y, spanX, spanY, false);
    }

    /**
     * Adds the specified child in the specified screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param screen The screen in which to add the child.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     * @param insert When true, the child is inserted at the beginning of the children list.
     */
    void addInScreen(View child, int screen, int x, int y, int spanX, int spanY, boolean insert) {
        if (screen < 0 || screen >= getChildCount()) {
            /* Rogro82@xda Extended : Do not throw an exception else it will crash when there is an item on a hidden homescreen */
            return;
            //throw new IllegalStateException("The screen must be >= 0 and < " + getChildCount());
        }
        //ADW: we cannot accept an item from a position greater that current desktop columns/rows
        if(x>=mDesktopColumns || y>=mDesktopRows){
        	return;
        }
        clearVacantCache();

        final CellLayout group = (CellLayout) getChildAt(screen);
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = new CellLayout.LayoutParams(x, y, spanX, spanY);
        } else {
            lp.cellX = x;
            lp.cellY = y;
            lp.cellHSpan = spanX;
            lp.cellVSpan = spanY;
        }
        group.addView(child, insert ? 0 : -1, lp);
        if (!(child instanceof Folder)) {
            child.setOnLongClickListener(mLongClickListener);
        }
    }

    CellLayout.CellInfo findAllVacantCells(boolean[] occupied) {
        CellLayout group = (CellLayout) getChildAt(getCurrentScreen());
        if (group != null) {
            return group.findAllVacantCells(occupied, null);
        }
        return null;
    }

    CellLayout.CellInfo findAllVacantCellsFromModel() {
    	int currentScreen = getCurrentScreen();
        CellLayout group = (CellLayout) getChildAt(currentScreen);
        if (group != null) {
            int countX = group.getCountX();
            int countY = group.getCountY();
            boolean occupied[][] = new boolean[countX][countY];
            Launcher.getModel().findAllOccupiedCells(occupied, countX, countY, currentScreen);
            return group.findAllVacantCellsFromOccupied(occupied, countX, countY);
        }
        return null;
    }

    private void clearVacantCache() {
        if (mVacantCache != null) {
            mVacantCache.clearVacantCells();
            mVacantCache = null;
        }
    }

    /**
     * Registers the specified listener on each screen contained in this workspace.
     *
     * @param l The listener used to respond to long clicks.
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mLongClickListener = l;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setOnLongClickListener(l);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if(mLauncher.getDesktopIndicator()!=null){
                mLauncher.getDesktopIndicator().indicate(Mpb(mScroller.getCurrX(), getChildCount()*getWidth()));
            }
            postInvalidate();
        } else if (!flagl) {
            mCurrentScreen = Math.round((float)getScrollX()/(float)getWidth());
            //ADW: dots
            //indicatorLevels(mCurrentScreen);
            Launcher.setScreen(getCurrentScreen());
            flagl = true;
		}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }
        // The children are given the same width and height as the workspace
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        heightSpecSize-=getPaddingTop()+getPaddingBottom();
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mFirstLayout) {
        	final int currentScreen = getCurrentScreen();
            scrollTo(currentScreen * width, 0);
            mScroller.startScroll(0, 0, currentScreen * width, 0, 0);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;
        final int count = getChildCount();
        final int half = count / 2;
        int width = getWidth();
        int l1 = width == 0 ? 0 : getScrollX() / width;
        int i2 = l1 * width;
        int j2 = l1 % count;
        if (j2 < 0) j2+=count;
        final int mTop=getPaddingTop();
        final int mBottom=getPaddingBottom();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
            	int j3 = i - j2;
            	if(half < Math.abs(j3)) {
            		if(j3 < 0)
            			j3 += count;
            		else
            			j3 -= count;
            	}
            	childLeft = i2 + j3 * width;
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, mTop, childLeft + childWidth, mTop+child.getMeasuredHeight()-mBottom);
            }
        }
        
        if(!flagJ && flagd) {
        	if(mScreenSelector.mPa()) {
        		post(new Runnable(){

					@Override
					public void run() {
						mScreenSelector.mPa(numc);
						flagL = false;
					}
        			
        		});
        		flagd = false;
        	}
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if(!mLauncher.isAllAppsVisible() && getChildCount()>0){
            final Folder openFolder = getOpenFolder();
            if (openFolder != null) {
                return openFolder.requestFocus(direction, previouslyFocusedRect);
            } else {
                int focusableScreen;
                if (!flagl) {
                    focusableScreen = mpi(mNextScreen);
                } else {
                    focusableScreen = getCurrentScreen();
                }
                if(focusableScreen>getChildCount()-1)focusableScreen=getChildCount()-1;
                getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
            }
        }
        return false;
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            snapToScreen(mCurrentScreen - 1, false);
            return true;
        } else if (direction == View.FOCUS_RIGHT) {
            snapToScreen(mCurrentScreen + 1, false);
            return true;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (!mLauncher.isAllAppsVisible()) {
            final Folder openFolder = getOpenFolder();
            if (openFolder == null) {
                try{
                	final int currentScreen = getCurrentScreen();
                    getChildAt(currentScreen).addFocusables(views, direction);
                    if (direction == View.FOCUS_LEFT) {
                        if (currentScreen > 0) {
                            getChildAt(currentScreen - 1).addFocusables(views, direction);
                        }
                    } else if (direction == View.FOCUS_RIGHT){
                        if (currentScreen < getChildCount() - 1) {
                            getChildAt(currentScreen + 1).addFocusables(views, direction);
                        }
                    }
                }catch (Exception e){
                    //Adding focusables with screens not ready...
                }
            } else {
                openFolder.addFocusables(views, direction);
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if(flagL) {
    		return true;
    	}
		if(!mLauncher.mPp()) {
    		return false;
    	}
    	

        //Wysie: If multitouch event is detected
        if (multiTouchController.onTouchEvent(ev)) {
            return false;
        }

        if (mLocked || mLauncher.isAllAppsVisible()) {
            return true;
        }

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;


                // In order to be flexible enough for future complex gestures, we should check here is the movement belongs to a given pattern
                // However in order to not spend cpu time (speed) in checking this, it is preferable to leave the x scrolling as untouched and light as possible
                if (xMoved || yMoved) {
                    // If xDiff > yDiff means the finger path pitch is smaller than 45deg so we assume the user want to scroll X axis
                    if (xDiff > yDiff) {
                        // Scroll if the user moved far enough along the X axis
                        mTouchState = TOUCH_STATE_SCROLLING;

                    }
                    // If yDiff > xDiff means the finger path pitch is bigger than 45deg so we assume the user want to either scroll Y or Y-axis gesture
                    else if (getOpenFolder()==null)
                    {
                    	// As x scrolling is left untouched (more or less untouched;)), every gesture should start by dragging in Y axis. In fact I only consider useful, swipe up and down.
                    	// Guess if the first Pointer where the user click belongs to where a scrollable widget is.
                		mTouchedScrollableWidget = isWidgetAtLocationScrollable((int)mLastMotionX,(int)mLastMotionY);
                    	if (!mTouchedScrollableWidget)
                    	{
	                    	// Only y axis movement. So may be a Swipe down or up gesture
	                    	if ((y - mLastMotionY) > 0){
	                    		if(Math.abs(y-mLastMotionY)>(touchSlop*2))mTouchState = TOUCH_SWIPE_DOWN_GESTURE;
	                    	}else{
	                    		if(Math.abs(y-mLastMotionY)>(touchSlop*2))mTouchState = TOUCH_SWIPE_UP_GESTURE;
	                    	}
                    	}
                    }

                    // Either way, cancel any pending longpress
                    if (mAllowLongPress) {
                        mAllowLongPress = false;
                        // Try canceling the long press. It could also have been scheduled
                        // by a distant descendant, so use the mAllowLongPress flag to block
                        // everything
                        final View currentScreen = getChildAt(mpi(mCurrentScreen));
                        currentScreen.cancelLongPress();
                    }
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mAllowLongPress = true;

                //mTouchedScrollableWidget = isWidgetAtLocationScrollable((int)x,(int)y);

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchState != TOUCH_STATE_SCROLLING && mTouchState != TOUCH_SWIPE_DOWN_GESTURE && mTouchState != TOUCH_SWIPE_UP_GESTURE) {
                    final CellLayout currentScreen = (CellLayout) getChildAt(getCurrentScreen());
                    if (currentScreen != null &&!currentScreen.lastDownOnOccupiedCell()) {
                        getLocationOnScreen(mTempCell);
                    }
                }
                mTouchState = TOUCH_STATE_REST;
                mAllowLongPress = false;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	if(flagL) {
    		return true;
    	}
    	if (!mLauncher.mPp()) {
    		return true;
    	}
        //Wysie: If multitouch event is detected
        /*if (multiTouchController.onTouchEvent(ev)) {
            return false;
        }*/
        if (mLocked || mLauncher.isAllAppsVisible() || mSensemode) {
        	if (mSensemode) {
        		mScreenSelector.onTouchEvent(ev);
        	}
            return true;
        }

        mFlingGesture.ForwardTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            /*
             * If being flinged and user touches, stop the fling. isFinished
             * will be false if being flinged.
             */
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }

            // Remember where the motion event started
            mLastMotionX = x;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
            	if(!flagJ) {
            		mPc(false);
            		requestLayout();
            	}
                // Scroll to follow the motion event
                int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                scrollBy(deltaX, 0);
                mScreenSelector.mPa(getCurrentScreen(), (float)deltaX);
                mLauncher.getDesktopIndicator().indicate(Mpb(getScrollX(), getChildCount()*getWidth()));
            }
            break;
        case MotionEvent.ACTION_UP:
            mTouchState = TOUCH_STATE_REST;
            break;
        case MotionEvent.ACTION_CANCEL:
            mTouchState = TOUCH_STATE_REST;
        }

        return true;
    }

	@Override
	public void OnFling(final int Direction) {
		if (mTouchState == TOUCH_STATE_SCROLLING) {
			if (!flagJ) {
				mPc(false);
				requestLayout();
				post(new Runnable(){

					@Override
					public void run() {
						if (Direction == FlingGesture.FLING_LEFT) {
							N = FlingGesture.FLING_LEFT;
							snapToScreen(mCurrentScreen - 1);
				        } else if (Direction == FlingGesture.FLING_RIGHT) {
				        	N = FlingGesture.FLING_RIGHT;
				        	snapToScreen(mCurrentScreen + 1);
				        } else {
				            snapToScreen(mCurrentScreen);
				        }
					}
					
				});
			} else {
				if (Direction == FlingGesture.FLING_LEFT) {
					N = FlingGesture.FLING_LEFT;
					snapToScreen(mCurrentScreen - 1);
		        } else if (Direction == FlingGesture.FLING_RIGHT) {
		        	N = FlingGesture.FLING_RIGHT;
		        	snapToScreen(mCurrentScreen + 1);
		        } else {
		            snapToScreen(mCurrentScreen);
		        }
			}
		}
	}

	void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, false);
		
	}

    void snapToScreen(int whichScreen, boolean b) {
    	flagL = true;
        clearVacantCache();
        if(!flagJ) {
        	mPc(false);
        	requestLayout();
        }

        mNextScreen = whichScreen;
        flagl = false;
        
        int width = getWidth();
        int k = width * mNextScreen;
        int x = getScrollX();
        int i1 = k-x;
        if(!b && width < Math.abs(i1)) {
        	mNextScreen = mCurrentScreen;
        	i1 = width * mNextScreen - x;
        }
        
        if(!mSensemode) {
        	mScroller.startScroll(x, 0, i1, 0, 450);
        } else {
        	mScroller.startScroll(x, 0, i1, 0, mAnimationDuration);
        }

        invalidate();
        mScreenSelector.mPa(mpi(mCurrentScreen), mpi(mNextScreen));
    }

    void startDrag(CellLayout.CellInfo cellInfo) {
        View child = cellInfo.cell;

        int currentScreen = getCurrentScreen();
        mDragInfo = cellInfo;
        mDragInfo.screen = currentScreen;

        CellLayout current = ((CellLayout) getChildAt(currentScreen));
        final ItemInfo info = (ItemInfo)child.getTag();
        mLauncher.showActions(info, child);

        current.onDragChild(child);
        mDragger.startDrag(child, this, child.getTag(), DragController.DRAG_ACTION_MOVE);
        invalidate();
        clearVacantCache();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentScreen = getCurrentScreen();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try{
	    	SavedState savedState = (SavedState) state;
	        super.onRestoreInstanceState(savedState.getSuperState());
	        if (savedState.currentScreen != -1) {
	            mCurrentScreen = savedState.currentScreen;
	            Launcher.setScreen(getCurrentScreen());
	        }
        }catch (Exception e) {
			// TODO ADW: Weird bug http://code.google.com/p/android/issues/detail?id=3981
			//Should be completely fixed on eclair
			super.onRestoreInstanceState(null);
			Log.d("WORKSPACE","Google bug http://code.google.com/p/android/issues/detail?id=3981 found, bypassing...");
		}
    }

    void addApplicationShortcut(ApplicationInfo info, CellLayout.CellInfo cellInfo,
            boolean insertAtFirst) {
        final CellLayout layout = (CellLayout) getChildAt(cellInfo.screen);
        final int[] result = new int[2];

        layout.cellToPoint(cellInfo.cellX, cellInfo.cellY, result);
        onDropExternal(result[0], result[1], info, layout, insertAtFirst);
    }

    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
        final CellLayout cellLayout = getCurrentDropLayout();
        if (source != this) {
            onDropExternal(x - xOffset, y - yOffset, dragInfo, cellLayout);
        } else {
            // Move internally
            if (mDragInfo != null) {
                boolean moved=false;
                final View cell = mDragInfo.cell;
                int index = mScroller.isFinished() ? getCurrentScreen() : mNextScreen;
                if (index != mDragInfo.screen) {
                    final CellLayout originalCellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                    originalCellLayout.removeView(cell);
                    cellLayout.addView(cell);
                    moved=true;
                }
                mTargetCell = estimateDropCell(x - xOffset, y - yOffset,
                        mDragInfo.spanX, mDragInfo.spanY, cell, cellLayout, mTargetCell);
                cellLayout.onDropChild(cell, mTargetCell);
                if(mTargetCell[0]!=mDragInfo.cellX || mTargetCell[1]!=mDragInfo.cellY)
                    moved=true;
                final ItemInfo info = (ItemInfo)cell.getTag();
                if(moved){
                    CellLayout.LayoutParams lp = (CellLayout.LayoutParams) cell.getLayoutParams();
                    LauncherModel.moveItemInDatabase(mLauncher, info,
                            LauncherSettings.Favorites.CONTAINER_DESKTOP, index, lp.cellX, lp.cellY);
                //}else{
                    //mLauncher.showActions(info, cell);
                }
            }
        }
    }

    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
        clearVacantCache();
    }

    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
    }

    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
        clearVacantCache();
    }

    private void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout) {
        onDropExternal(x, y, dragInfo, cellLayout, false);
    }

    private void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout,
            boolean insertAtFirst) {
        // Drag from somewhere else
        ItemInfo info = (ItemInfo) dragInfo;

        View view;

        int currentScreen = getCurrentScreen();
        switch (info.itemType) {
        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            if (info.container == NO_ID) {
                // Came from all apps -- make a copy
                info = new ApplicationInfo((ApplicationInfo) info);
            }
            view = mLauncher.createShortcut(R.layout.application2, cellLayout,
                    (ApplicationInfo) info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
            view = FolderIcon.fromXml(R.layout.folder_icon, mLauncher,
                    (ViewGroup) getChildAt(currentScreen), ((UserFolderInfo) info));
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
            view = LiveFolderIcon.fromXml(
                    R.layout.live_folder_icon, mLauncher,
                    (ViewGroup) getChildAt(currentScreen),(LiveFolderInfo) info);
            break;
        default:
            throw new IllegalStateException("Unknown item type: " + info.itemType);
        }

        cellLayout.addView(view, insertAtFirst ? 0 : -1);
        view.setOnLongClickListener(mLongClickListener);
        mTargetCell = estimateDropCell(x, y, 1, 1, view, cellLayout, mTargetCell);
        cellLayout.onDropChild(view, mTargetCell);
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) view.getLayoutParams();

        final LauncherModel model = Launcher.getModel();
        model.addDesktopItem(info);
        LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
                LauncherSettings.Favorites.CONTAINER_DESKTOP, mCurrentScreen, lp.cellX, lp.cellY);
        mLauncher.mPq();
    }

    /**
     * Return the current {@link CellLayout}, correctly picking the destination
     * screen while a scroll is in progress.
     */
    private CellLayout getCurrentDropLayout() {
        int index = mpi(Math.round((float)getScrollX()/(float)getWidth()));
        final CellLayout layout = (CellLayout) getChildAt(index);
        if (layout!=null)
            return layout;
        else
            return (CellLayout) getChildAt(getCurrentScreen());
    }

    /**
     * {@inheritDoc}
     */
    public boolean acceptDrop(DragSource source, int x, int y,
            int xOffset, int yOffset, Object dragInfo) {
        final CellLayout layout = getCurrentDropLayout();
        final CellLayout.CellInfo cellInfo = mDragInfo;
        final int spanX = cellInfo == null ? 1 : cellInfo.spanX;
        final int spanY = cellInfo == null ? 1 : cellInfo.spanY;

        if (mVacantCache == null) {
            final View ignoreView = cellInfo == null ? null : cellInfo.cell;
            mVacantCache = layout.findAllVacantCells(null, ignoreView);
        }

        return mVacantCache.findCellForSpan(mTempEstimate, spanX, spanY, false);
    }

    /**
     * {@inheritDoc}
     */
    public Rect estimateDropLocation(int x, int y, int xOffset, int yOffset, Rect recycle) {
        final CellLayout layout = getCurrentDropLayout();

        final CellLayout.CellInfo cellInfo = mDragInfo;
        final int spanX = cellInfo == null ? 1 : cellInfo.spanX;
        final int spanY = cellInfo == null ? 1 : cellInfo.spanY;
        final View ignoreView = cellInfo == null ? null : cellInfo.cell;

        final Rect location = recycle != null ? recycle : new Rect();

        // Find drop cell and convert into rectangle
        int[] dropCell = estimateDropCell(x - xOffset, y - yOffset,
                spanX, spanY, ignoreView, layout, mTempCell);

        if (dropCell == null) {
            return null;
        }

        layout.cellToPoint(dropCell[0], dropCell[1], mTempEstimate);
        location.left = mTempEstimate[0];
        location.top = mTempEstimate[1];

        layout.cellToPoint(dropCell[0] + spanX, dropCell[1] + spanY, mTempEstimate);
        location.right = mTempEstimate[0];
        location.bottom = mTempEstimate[1];

        return location;
    }

    /**
     * Calculate the nearest cell where the given object would be dropped.
     */
    private int[] estimateDropCell(int pixelX, int pixelY,
            int spanX, int spanY, View ignoreView, CellLayout layout, int[] recycle) {
        // Create vacant cell cache if none exists
        if (mVacantCache == null) {
            mVacantCache = layout.findAllVacantCells(null, ignoreView);
        }

        // Find the best target drop location
        return layout.findNearestVacantArea(pixelX, pixelY, spanX, spanY, mVacantCache, recycle);
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
        registerProvider();
    }

    public void setDragger(DragController dragger) {
        mDragger = dragger;
    }

    public void onDropCompleted(View target, boolean success) {
        // This is a bit expensive but safe
        clearVacantCache();
        if (success){
            if (mDragInfo != null) {
            	if (target != this) {
                    final CellLayout cellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                    cellLayout.removeView(mDragInfo.cell);
                    final Object tag = mDragInfo.cell.getTag();
                    Launcher.getModel().removeDesktopItem((ItemInfo) tag);
            	}
            	final int currentScreen = getCurrentScreen();
            	if (mDragInfo.screen != currentScreen)
            		mPb(true);
            	else
            		mPd(currentScreen);
            }
        } else {
            if (mDragInfo != null) {
                final CellLayout cellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                cellLayout.onDropAborted(mDragInfo.cell);
            }
        }

        mDragInfo = null;
    }

    public void scrollLeft() {
    	if(!flagl) return;
        clearVacantCache();
        flagl = true;
        mPc(false);
        mScreenSelector.mPa(getCurrentScreen(), 0f);
        post(new Runnable(){

			@Override
			public void run() {
	            snapToScreen(mCurrentScreen - 1);
			}
        	
        });
    }

    public void scrollRight() {
    	if(!flagl) return;
        clearVacantCache();
        flagl = true;
        mPc(false);
        mScreenSelector.mPa(getCurrentScreen(), 0f);
        post(new Runnable(){

			@Override
			public void run() {
	            snapToScreen(mCurrentScreen + 1);
			}
        	
        });
    }

    public int getScreenForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getChildAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    public Folder getFolderForTag(Object tag) {
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            CellLayout currentScreen = ((CellLayout) getChildAt(screen));
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                if (lp.cellHSpan == mDesktopColumns && lp.cellVSpan == mDesktopRows && child instanceof Folder) {
                    Folder f = (Folder) child;
                    if (f.getInfo() == tag) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public View getViewForTag(Object tag) {
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            CellLayout currentScreen = ((CellLayout) getChildAt(screen));
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                if (child.getTag() == tag) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Unlocks the SlidingDrawer so that touch events are processed.
     *
     * @see #lock()
     */
    public void unlock() {
        mLocked = false;
    }

    /**
     * Locks the SlidingDrawer so that touch events are ignores.
     *
     * @see #unlock()
     */
    public void lock() {
        mLocked = true;
    }

    /**
     * @return True is long presses are still allowed for the current touch
     */
    public boolean allowLongPress() {
        return mAllowLongPress;
    }

    /**
     * Set true to allow long-press events to be triggered, usually checked by
     * {@link Launcher} to accept or block dpad-initiated long-presses.
     */
    public void setAllowLongPress(boolean allowLongPress) {
        mAllowLongPress = allowLongPress;
    }

    void removeShortcutsForPackage(String packageName) {
        final ArrayList<View> childrenToRemove = new ArrayList<View>();
        final LauncherModel model = Launcher.getModel();
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            int childCount = layout.getChildCount();

            childrenToRemove.clear();

            for (int j = 0; j < childCount; j++) {
                final View view = layout.getChildAt(j);
                Object tag = view.getTag();

                if (tag instanceof ApplicationInfo) {
                    final ApplicationInfo info = (ApplicationInfo) tag;
                    // We need to check for ACTION_MAIN otherwise getComponent() might
                    // return null for some shortcuts (for instance, for shortcuts to
                    // web pages.)
                    final Intent intent = info.intent;
                    final ComponentName name = intent.getComponent();

                    if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                            name != null && packageName.equals(name.getPackageName())) {
                        model.removeDesktopItem(info);
                        LauncherModel.deleteItemFromDatabase(mLauncher, info);
                        childrenToRemove.add(view);
                    }
                } else if (tag instanceof UserFolderInfo) {
                    final UserFolderInfo info = (UserFolderInfo) tag;
                    final ArrayList<ApplicationInfo> contents = info.contents;
                    final ArrayList<ApplicationInfo> toRemove = new ArrayList<ApplicationInfo>(1);
                    final int contentsCount = contents.size();
                    boolean removedFromFolder = false;

                    for (int k = 0; k < contentsCount; k++) {
                        final ApplicationInfo appInfo = contents.get(k);
                        final Intent intent = appInfo.intent;
                        final ComponentName name = intent.getComponent();

                        if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                                name != null && packageName.equals(name.getPackageName())) {
                            toRemove.add(appInfo);
                            LauncherModel.deleteItemFromDatabase(mLauncher, appInfo);
                            removedFromFolder = true;
                        }
                    }

                    contents.removeAll(toRemove);
                    if (removedFromFolder) {
                        final Folder folder = getOpenFolder();
                        if (folder != null) folder.notifyDataSetChanged();
                    }
                }
            }

            childCount = childrenToRemove.size();
            for (int j = 0; j < childCount; j++) {
                layout.removeViewInLayout(childrenToRemove.get(j));
            }

            if (childCount > 0) {
                layout.requestLayout();
                layout.invalidate();
            }
        }
        mPb(false);
    }

    void updateShortcutFromApplicationInfo(ApplicationInfo info) {
    	final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                final View view = layout.getChildAt(j);
                Object tag = view.getTag();
                if (tag instanceof ApplicationInfo) {
                	ApplicationInfo tagInfo = (ApplicationInfo)tag;
                    if (tagInfo.id == info.id)
                    {
                    	tagInfo.assignFrom(info);

                    	View newview = mLauncher.createShortcut(R.layout.application2, layout, tagInfo);
                    	layout.removeView(view);
                    	addInScreen(newview, info.screen, info.cellX, info.cellY, info.spanX,
                    			info.spanY, false);
                    	break;
                    }
                }
            }
        }
    }

    void updateShortcutsForPackage(String packageName) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                final View view = layout.getChildAt(j);
                Object tag = view.getTag();
                if (tag instanceof ApplicationInfo) {
                    ApplicationInfo info = (ApplicationInfo) tag;
                    // We need to check for ACTION_MAIN otherwise getComponent() might
                    // return null for some shortcuts (for instance, for shortcuts to
                    // web pages.)
                    final Intent intent = info.intent;
                    final ComponentName name = intent.getComponent();
                    if ((info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                            info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT)&&
                            Intent.ACTION_MAIN.equals(intent.getAction()) && name != null &&
                            packageName.equals(name.getPackageName())) {

                        final Drawable icon = Launcher.getModel().getApplicationInfoIcon(
                                mLauncher.getPackageManager(), info, mLauncher);
                        if (icon != null && icon != info.icon) {
                            info.icon.setCallback(null);
                            info.icon = Utilities.createIconThumbnail(icon, mLauncher);
                            info.filtered = true;
                            if (view instanceof ScreenItemView)
                            	((ScreenItemView) view).setIcon(icon);
                            else if (view instanceof TextView)
                            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null,
                                    info.icon, null, null);
                        }
                    }
                }else if (tag instanceof UserFolderInfo){
                	//TODO: ADW: Maybe there are icons inside folders.... need to update them too
                    final UserFolderInfo info = (UserFolderInfo) tag;
                    final ArrayList<ApplicationInfo> contents = info.contents;
                    final int contentsCount = contents.size();
                    for (int k = 0; k < contentsCount; k++) {
                        final ApplicationInfo appInfo = contents.get(k);
                        final Intent intent = appInfo.intent;
                        final ComponentName name = intent.getComponent();
                        if ((appInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                                info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT)&&
                                Intent.ACTION_MAIN.equals(intent.getAction()) && name != null &&
                                packageName.equals(name.getPackageName())) {

                            final Drawable icon = Launcher.getModel().getApplicationInfoIcon(
                                    mLauncher.getPackageManager(), appInfo, mLauncher);
                            boolean folderUpdated=false;
                            if (icon != null && icon != appInfo.icon) {
                            	appInfo.icon.setCallback(null);
                            	appInfo.icon = Utilities.createIconThumbnail(icon, mLauncher);
                            	appInfo.filtered = true;
                            	folderUpdated=true;
                            }
                            if(folderUpdated){
                                final Folder folder = getOpenFolder();
                                if (folder != null) folder.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }

    void moveToDefaultScreen0() {
    	mPc(false);
    	post(new Runnable(){

			@Override
			public void run() {
				moveToDefaultScreen();
			}
    		
    	});
    }

    void moveToDefaultScreen() {
        snapToScreen(mDefaultScreen - mpi(mCurrentScreen) + mCurrentScreen, true);
        getChildAt(mDefaultScreen).requestFocus();
    }

    public static class SavedState extends BaseSavedState {
        int currentScreen = 0;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentScreen = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentScreen);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    /**************************************************
     * ADW: Custom modifications
     */

    /**
     * Pagination indicators (dots)
     */
	public void openSense(boolean open){
		mScroller.abortAnimation();
		mSensemode = open;
	}

	/**
	 * Wysie: Multitouch methods/events
	 */
	@Override
	public Object getDraggableObjectAtPoint(PointInfo pt) {
		return this;
	}

	@Override
	public void getPositionAndScale(Object obj,
			PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(0.0f, 0.0f, true, 1.0f, false, 0.0f, 0.0f, false, 0.0f);
	}

	@Override
	public void selectObject(Object obj, PointInfo pt) {
		mAllowLongPress=false;
	}

	@Override
	public boolean setPositionAndScale(Object obj,
			PositionAndScale update, PointInfo touchPoint) {
        float newRelativeScale = update.getScale();
        int targetZoom = (int) Math.round(Math.log(newRelativeScale) * ZOOM_LOG_BASE_INV);
        // Only works for pinch in
        if (targetZoom < 0) { // Change to > 0 for pinch out, != 0 for both pinch in and out.
        	mLauncher.showPreviews(mLauncher.getDrawerHandle(), 0, getChildCount());
        	invalidate();
            return true;
        }
        return false;
	}

	@Override
	public Activity getLauncherActivity() {
		// TODO Auto-generated method stub
		return mLauncher;
	}
	public int currentDesktopRows(){
		return mDesktopRows;
	}
	public int currentDesktopColumns(){
		return mDesktopColumns;
	}
    public boolean isWidgetAtLocationScrollable(int x, int y) {
		// will return true if widget at this position is scrollable.
    	// Get current screen from the whole desktop
    	CellLayout currentScreen = (CellLayout) getChildAt(getCurrentScreen());
    	int[] cell_xy = new int[2];
    	// Get the cell where the user started the touch event
    	currentScreen.pointToCellExact(x, y, cell_xy);
        int count = currentScreen.getChildCount();

        // Iterate to find which widget is located at that cell
        // Find widget backwards from a cell does not work with (View)currentScreen.getChildAt(cell_xy[0]*currentScreen.getCountX etc etc); As the widget is positioned at the very first cell of the widgetspace
        for (int i = 0; i < count; i++) {
            View child = currentScreen.getChildAt(i);
            if ( child !=null)
            {
            	// Get Layount graphical info about this widget
	            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
	            // Calculate Cell Margins
	            int left_cellmargin = lp.cellX;
	            int rigth_cellmargin = lp.cellX+lp.cellHSpan;
	            int top_cellmargin = lp.cellY;
	            int botton_cellmargin = lp.cellY + lp.cellVSpan;
	            // See if the cell where we touched is inside the Layout of the widget beeing analized
	            if (cell_xy[0] >= left_cellmargin && cell_xy[0] < rigth_cellmargin && cell_xy[1] >= top_cellmargin && cell_xy[1] < botton_cellmargin)  {
	            	try {
		            	// Get Widget ID
		            	int id = ((AppWidgetHostView)child).getAppWidgetId();
		            	// Ask to WidgetSpace if the Widget identified itself when created as 'Scrollable'
		            	return isWidgetScrollable(id);
	            	} catch (Exception e)
	            	{}
	            }
           }
        }
        return false;
	}
    
    public void unbindWidgetScrollableViews() {
    	unbindWidgetScrollable();
	}

    void updateCountersForPackage(String packageName,int counter, int color) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                final View view = layout.getChildAt(j);
                Object tag = view.getTag();
                if (tag instanceof ApplicationInfo) {
                    ApplicationInfo info = (ApplicationInfo) tag;
                    // We need to check for ACTION_MAIN otherwise getComponent() might
                    // return null for some shortcuts (for instance, for shortcuts to
                    // web pages.)
                    final Intent intent = info.intent;
                    final ComponentName name = intent.getComponent();
                    if ((info.itemType==LauncherSettings.Favorites.ITEM_TYPE_APPLICATION||
                            info.itemType==LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) &&
                        Intent.ACTION_MAIN.equals(intent.getAction()) && name != null &&
                        packageName.equals(name.getPackageName())) {
                        view.invalidate();
                        Launcher.getModel().updateCounterDesktopItem(info, counter, color);
                    }
                }
            }
        }
    }

	public void setSelector(ScreenSelector  screenselector) {
		mScreenSelector = screenselector;
		screenselector.mPa(this);
	}

	private void tmpfunction() {
		if (mScreenSelector.mPa()) {
			mScreenSelector.mPa(getCurrentScreen());
		} else {
			postDelayed(new Runnable() {

				@Override
				public void run() {
					tmpfunction();
				}

			}, 100);
		}
	}

	@Override
	public void setl() {
		 getChildAt(numc).setVisibility(View.VISIBLE);
		 flagd = true;
		 flagJ = false;
		 requestLayout();

	}

	private static float Mpb(int i, int j) {
		if (j > 0) {
			float f = i % j;
			if (f < 0)
				f += j;
			return f / j;
		}
		return 0;
	}

	public void mPb(final boolean b) {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < mHomeScreens; i++) {
					mScreenSelector.mPa(i, getChildAt(i));
				}
				System.gc();

				if (b) {
					tmpfunction();
				}
				mScreenSelector.requestRender();
				flagM = true;
			}
		}, 500);
	}

	public void mPc(boolean b) {
		final int visibility = b ? View.VISIBLE: View.INVISIBLE;
		final int count = getChildCount();
		for(int i = 0;i< count; i++) {
			final View screan = getChildAt(i);
			if(screan!=null&& screan.getVisibility() != visibility)
				screan.setVisibility(visibility);
		}
		flagJ = !b;
	}

	public void mPd(final int screen) {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				mScreenSelector.mPa(screen,  getChildAt(screen));
				System.gc();
			}

		}, 500);
	}
	
	@Override
	public void mPe(int i) {
		numc = mpi(i);
	}

	public boolean mPf() {
		if(!mScroller.isFinished() || getWidth() == 0 || getScrollX() % getWidth() == 0) 
            return false;
        return true;
	}

	public boolean mPg() {
		return mTouchState == TOUCH_STATE_REST && mScroller.isFinished() && !flagL && !mPf();
	}

	public void mPh() {
		flagL = false;		
	}

	private int mpi(int currentScreen) {
		final int childCount = getChildCount();
		int k = currentScreen % childCount;
		if (k < 0)
			k += childCount;
		return k;
	}

	public void mPy() {
		mDefaultScreen = 3;
	}
	
	@Override
	public void  mPz() {
		if(mLauncher.mPj())
			mPb(true);
	}

	public boolean mPA() {
		return flagM;
	}

	public void mPB() {
		flagM = false;
	}

}
