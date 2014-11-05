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

import static android.util.Log.d;
import static android.util.Log.e;
import static android.util.Log.w;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mobi.intuitit.android.content.LauncherIntent;
import mobi.intuitit.android.content.LauncherMetadata;
import jp.co.evangelion.nervhome.ActionButton.SwipeListener;
import jp.co.evangelion.nervhome.extended.C123c;
import jp.co.evangelion.nervhome.extended.QuickActionWindow;
import jp.co.evangelion.nervhome.extended.ScreenItemView;
import jp.co.evangelion.nervhome.extended.drawer.MagiDrawer;
import jp.co.evangelion.nervhome.selector.C150a;
import jp.co.evangelion.nervhome.selector.ScreenSelector;
import jp.co.evangelion.nervhome.selector.StereoScreenSelector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.LiveFolders;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Default launcher application.
 */
public class Launcher extends Activity implements View.OnClickListener, OnLongClickListener, SwipeListener, C150a {
	static final String LOG_TAG = "MAGI";
    static final boolean LOGD = false;

    private static final boolean PROFILE_STARTUP = false;
    private static final boolean PROFILE_ROTATE = false;
    private static final boolean DEBUG_USER_INTERFACE = false;

    private static final int MENU_GROUP_ADD = 1;
    private static final int MENU_GROUP_CATALOGUE = 2;
    private static final int MENU_GROUP_NORMAL = 2;


    private static final int MENU_ADD = Menu.FIRST + 1;
    private static final int MENU_WALLPAPER_SETTINGS = MENU_ADD + 1;
    private static final int MENU_2D3D = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_SETTINGS = MENU_2D3D + 1;
    private static final int MENU_LICENSE = MENU_SETTINGS + 1;
    private static final int MENU_HELP = MENU_SETTINGS + 2;

    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_LIVE_FOLDER = 4;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_LIVE_FOLDER = 8;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_ANYCUT=10;
    private static final int REQUEST_SHOW_APP_LIST = 11;
    private static final int REQUEST_EDIT_SHIRTCUT = 12;
    private static final int REQUEST_13 = 13;
    private static final int REQUEST_14 = 14;

    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

    static final String EXTRA_CUSTOM_WIDGET = "custom_widget";
    static final String SEARCH_WIDGET = "search_widget";

    static final int WALLPAPER_SCREENS_SPAN = 2;
    static final int SCREEN_COUNT = 5;
    static final int DEFAULT_SCREN = 3;
    static final int NUMBER_CELLS_X = 4;
    static final int NUMBER_CELLS_Y = 4;

    private static final int DIALOG_CREATE_SHORTCUT = 1;
    static final int DIALOG_RENAME_FOLDER = 2;
    static final int DIALOG_CHOOSE_GROUP = 3;
    static final int DIALOG_NEW_GROUP = 4;
    static final int DIALOG_DELETE_GROUP_CONFIRM = 5;

    private static final String PREFERENCES = "launcher.preferences";

    // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: boolean
    private static final String RUNTIME_STATE_ALL_APPS_FOLDER = "launcher.all_apps_folder";
    // Type: long
    private static final String RUNTIME_STATE_USER_FOLDERS = "launcher.user_folder";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cellX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cellY";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_spanX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_spanY";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_COUNT_X = "launcher.add_countX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_COUNT_Y = "launcher.add_countY";
    // Type: int[]
    private static final String RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS = "launcher.add_occupied_cells";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
    // Type: boolean
    private static final String RUNTIME_STATE_DOCKBAR = "launcher.dockbar";

	private static final LauncherModel sModel = new LauncherModel();
	
    private static final Object sLock = new Object();
	private static int sScreen = DEFAULT_SCREN;
	
	private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
	private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
	private final BroadcastReceiver mScreenReceiver = new ScreenReceiver();
	private final ContentObserver mObserver = new FavoritesChangeObserver();
	private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();
	
	private LayoutInflater mInflater;
	
	private DragLayer mDragLayer;
	private Workspace mWorkspace;
	
	private AppWidgetManager mAppWidgetManager;
	private LauncherAppWidgetHost mAppWidgetHost;

    static final int APPWIDGET_HOST_ID = 1024;
    
	private CellLayout.CellInfo mAddItemCellInfo;
	private CellLayout.CellInfo mMenuAddInfo;
	private final int[] mCellCoordinates = new int[2];
	private FolderInfo mFolderInfo;
	
    /**
     * ADW: now i use an ActionButton instead of a fixed app-drawer button
     */
	private ActionButton mHandleView;
    /**
     * mAllAppsGrid will be "AllAppsGridView" or "AllAppsSlidingView"
     * depending on user settings, so I cast it later.
     */
	private Drawer mAllAppsGrid;
	
	private boolean mDesktopLocked = true;
	private Bundle mSavedState;
	
	private SpannableStringBuilder mDefaultKeySsb = null;
	
	private boolean mDestroyed;
	
	private boolean mIsNewIntent;
	
	private boolean mRestoring;
	private boolean mWaitingForResult;
	private boolean mLocaleChanged;
	
	private Bundle mSavedInstanceState;
	
	private DesktopBinder mBinder;
	
	private ActionButton mLAB;
	private ActionButton mRAB;
	private ActionButton mLAB2;
	private ActionButton mRAB2;
	private View mDrawerToolbar;
	private DeleteZone mDeleteZone;
	/**
	 * ADW: variables to store actual status of elements
	 */
	private boolean allAppsOpen = false;
	private final boolean allAppsAnimating = false;
	private boolean showingPreviews = false;
	
	private DesktopIndicator mDesktopIndicator;
	private boolean useDrawerCatalogNavigation = false;
	


	public boolean isDesktopBlocked() {
		return mBlockDesktop;
	}
	
	private boolean mBlockDesktop=true;
	
	/**
	 * ADW: Home/Swype down binding constants
	 */
	protected static final int BIND_NONE=0;
	protected static final int BIND_DEFAULT=1;
	protected static final int BIND_HOME_PREVIEWS=2;
	protected static final int BIND_PREVIEWS=3;
	protected static final int BIND_APPS=4;
	protected static final int BIND_NOTIFICATIONS=5;
	protected static final int BIND_HOME_NOTIFICATIONS=6;
	protected static final int BIND_DOCKBAR=8;
	protected static final int BIND_APP_LAUNCHER=9;
	
	private int mHomeBinding=BIND_DEFAULT;
	
	private Typeface themeFont = null;
	private StereoScreenSelector mScreenSelector;
	private boolean Q = false;
	private boolean bR = false;
	private boolean S = false;
	private boolean T = false;
	private static Locale mLocale = null;
	private QuickActionWindow mPopupWindow = null;
	private View mCurtain;
	private static final int mCurtainBackground = Color.argb(127, 0, 0, 0);
	private boolean Y;
	private List<Folder> Z;

	///TODO:ADW. Current code fully ready for upto 9
	//but need to add more drawables for the desktop dots...
	//or completely redo the desktop dots implementation
	private final static int MAX_SCREENS=7;
	//ADW: NAVIGATION VALUES FOR THE NEXT/PREV CATALOG ACTIONS
	private final static int ACTION_CATALOG_PREV=1;
	private final static int ACTION_CATALOG_NEXT=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(mLocale!=null&& !mLocale.equals(Locale.getDefault()))
			ApplicationsAdapter.clearAll();
		mLocale = Locale.getDefault();
		super.onCreate(savedInstanceState);
		mInflater = getLayoutInflater();
		
		LauncherActions.getInstance().init(this);
		
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		 
		mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
		mAppWidgetHost.startListening();

		if (PROFILE_STARTUP) {
			android.os.Debug.startMethodTracing("/sdcard/launcher");
		}

		mpy();
		checkForLocaleChange();
		setWallpaperDimension();
		setContentView(R.layout.launcher);
		setupViews();

		registerIntentReceivers();
		registerContentObservers();

		mSavedState = savedInstanceState;
		restoreState(mSavedState);

		if (PROFILE_STARTUP) {
			android.os.Debug.stopMethodTracing();
		}

		if (!mRestoring) {
			startLoaders();
		}

        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);

        Y =  PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen_stereo", false);
	}
	
	private void checkForLocaleChange() {
        final LocaleConfiguration localeConfiguration = new LocaleConfiguration();
        readConfiguration(this, localeConfiguration);

        final Configuration configuration = getResources().getConfiguration();

        final String previousLocale = localeConfiguration.locale;
        final String locale = configuration.locale.toString();

        final int previousMcc = localeConfiguration.mcc;
        final int mcc = configuration.mcc;

        final int previousMnc = localeConfiguration.mnc;
        final int mnc = configuration.mnc;

        mLocaleChanged = !locale.equals(previousLocale) || mcc != previousMcc || mnc != previousMnc;

        if (mLocaleChanged) {
            localeConfiguration.locale = locale;
            localeConfiguration.mcc = mcc;
            localeConfiguration.mnc = mnc;

            writeConfiguration(this, localeConfiguration);
        }
	}

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            // Ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    private void startLoaders() {
        boolean loadApplications = sModel.loadApplications(true, this, mLocaleChanged);
        sModel.loadUserItems(!mLocaleChanged, this, mLocaleChanged, loadApplications);

        mRestoring = false;
    }

    private void setWallpaperDimension() {
        WallpaperManager wpm = WallpaperManager.getInstance(this);

        Display display = getWindowManager().getDefaultDisplay();
        boolean isPortrait = display.getWidth() < display.getHeight();

        final int width = isPortrait ? display.getWidth() : display.getHeight();
        final int height = isPortrait ? display.getHeight() : display.getWidth();
        wpm.suggestDesiredDimensions(width * WALLPAPER_SCREENS_SPAN, height);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_13 || requestCode == REQUEST_14){
    		T = true;
    	}
        mWaitingForResult = false;
        S = true;
        // The pattern used here is that a user PICKs a specific application,
        // which, depending on the target, might need to CREATE the actual target.

        // For example, the user would PICK_SHORTCUT for "Music playlist", and we
        // launch over to the Music app to actually CREATE_SHORTCUT.

        if (resultCode == RESULT_OK && mAddItemCellInfo != null) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeAddApplication(this, data, mAddItemCellInfo, !mDesktopLocked);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeAddShortcut(data, mAddItemCellInfo, !mDesktopLocked);
                    break;
                case REQUEST_PICK_LIVE_FOLDER:
                    addLiveFolder(data);
                    break;
                case REQUEST_CREATE_LIVE_FOLDER:
                    completeAddLiveFolder(data, mAddItemCellInfo, !mDesktopLocked);
                    break;
                case REQUEST_PICK_APPWIDGET:
                    addAppWidget(data);
                    break;
                case REQUEST_CREATE_APPWIDGET:
                    completeAddAppWidget(data, mAddItemCellInfo, !mDesktopLocked);
                    break;
                case REQUEST_PICK_ANYCUT:
                	completeAddShortcut(data,mAddItemCellInfo, !mDesktopLocked);
                	break;
                case REQUEST_EDIT_SHIRTCUT:
                	completeEditShirtcut(data);
                	break;
                case REQUEST_13:
                	completeEditShirtcut(data);
                	S = false;
                	break;
            }
        }
		else if (resultCode == RESULT_OK ) {
			switch(requestCode) {
				case REQUEST_SHOW_APP_LIST: {
					showAllApps();
				} break;
	        	case REQUEST_EDIT_SHIRTCUT:
	        		completeEditShirtcut(data);
	        		break;
	        	case REQUEST_13:
	        		completeEditShirtcut(data);
	        		break;
			}
			S = false;
        }
		else if ((requestCode == REQUEST_PICK_APPWIDGET ||
                requestCode == REQUEST_CREATE_APPWIDGET) && resultCode == RESULT_CANCELED &&
                data != null) {
            // Clean up the appWidgetId if we canceled
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
		else if ((requestCode == REQUEST_13 || requestCode == REQUEST_14) && resultCode == RESULT_CANCELED) {
			S = false;
		}
    }

    @Override
	protected void onResume() {
		super.onResume();
		T = false;
		mpC();
        if (mRestoring) {
            startLoaders();
        }

        // If this was a new intent (i.e., the mIsNewIntent flag got set to true by
        // onNewIntent), then close the search dialog if needed, because it probably
        // came from the user pressing 'home' (rather than, for example, pressing 'back').
        if (mIsNewIntent) {
            // Post to a handler so that this happens after the search dialog tries to open
            // itself again.
            mWorkspace.post(new Runnable() {
                public void run() {
                    //ADW: changed from using ISearchManager to use SearchManager (thanks to Launcher+ source code)
                	SearchManager searchManagerService = (SearchManager) Launcher.this
                    .getSystemService(Context.SEARCH_SERVICE);
                    try {
                        searchManagerService.stopSearch();
                    } catch (Exception e) {
                        e(LOG_TAG, "error stopping search", e);
                    }
                }
            });
        }

        mIsNewIntent = false;
        mWorkspace.mPh();
        if(bR)
        	mpu();
    }

    private void mpu() {
    	 if(mWorkspace.mPf())
             mWorkspace.snapToScreen(mWorkspace.getCurrentScreen());
	}

	private boolean mpC() {
    	try {
    		getClass();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return false;
	}

	@Override
    protected void onPause() {
        super.onPause();
        dismissPreviews();
        mpu();

    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Flag any binder to stop early before switching
        if (mBinder != null) {
            mBinder.mTerminate = true;
        }
        //if(mMessWithPersistence)setPersistent(false);
        if (PROFILE_ROTATE) {
            android.os.Debug.startMethodTracing("/sdcard/launcher-rotate");
        }
        return null;
    }

    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
    		return true;
    	}
        boolean handled = super.onKeyDown(keyCode, event);
        if (!handled && acceptFilter() && keyCode != KeyEvent.KEYCODE_ENTER) {
            boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb,
                    keyCode, event);
            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
                // something usable has been typed - start a search
                // the typed text will be retrieved and cleared by
                // showSearchDialog()
                // If there are multiple keystrokes before the search dialog takes focus,
                // onSearchRequested() will be called for every keystroke,
                // but it is idempotent, so it's fine.
                return onSearchRequested();
            }
        }

        return handled;
    }

    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    /**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

        final int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
        if (currentScreen > -1) {
            mWorkspace.setCurrentScreen(currentScreen);
        }

        final int addScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);
        if (addScreen > -1) {
            mAddItemCellInfo = new CellLayout.CellInfo();
            final CellLayout.CellInfo addItemCellInfo = mAddItemCellInfo;
            addItemCellInfo.valid = true;
            addItemCellInfo.screen = addScreen;
            addItemCellInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            addItemCellInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            addItemCellInfo.spanX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
            addItemCellInfo.spanY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
            addItemCellInfo.findVacantCellsFromOccupied(
                    savedState.getBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS),
                    savedState.getInt(RUNTIME_STATE_PENDING_ADD_COUNT_X),
                    savedState.getInt(RUNTIME_STATE_PENDING_ADD_COUNT_Y));
            mRestoring = true;
        }

        boolean renameFolder = savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = sModel.getFolderById(this, id);
            mRestoring = true;
        }
    }

    /**
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
    	 mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
         final DragLayer dragLayer = mDragLayer;

         mWorkspace = (Workspace) dragLayer.findViewById(R.id.workspace);
         final Workspace workspace = mWorkspace;
         
         mAllAppsGrid = new MagiDrawer(this);
         mDeleteZone = (DeleteZone) dragLayer.findViewById(R.id.delete_zone);

         mHandleView = (ActionButton) dragLayer.findViewById(R.id.btn_mab);
         mHandleView.setFocusable(true);
         mHandleView.setLauncher(this);
         mHandleView.setOnClickListener(this);
         dragLayer.addDragListener(mHandleView);
 		
         mAllAppsGrid.setDragger(dragLayer);
         mAllAppsGrid.setLauncher(this);

         workspace.setOnLongClickListener(this);
         workspace.setDragger(dragLayer);
         workspace.setLauncher(this);

         mDeleteZone.setLauncher(this);
         mDeleteZone.setDragController(dragLayer);

         dragLayer.setIgnoredDropTarget((View)mAllAppsGrid);
         dragLayer.setDragScoller(workspace);
         dragLayer.addDragListener(mDeleteZone);

         //ADW: Action Buttons (LAB/RAB)
         mLAB = (ActionButton) dragLayer.findViewById(R.id.btn_lab);
         mLAB.setLauncher(this);
         mLAB.setSpecialIcon(R.drawable.arrow_left);
         mLAB.setSpecialAction(ACTION_CATALOG_PREV);
         dragLayer.addDragListener(mLAB);
         mRAB = (ActionButton) dragLayer.findViewById(R.id.btn_rab);
         mRAB.setLauncher(this);
         mRAB.setSpecialIcon(R.drawable.arrow_right);
         mRAB.setSpecialAction(ACTION_CATALOG_NEXT);
         dragLayer.addDragListener(mRAB);
         mLAB.setOnClickListener(this);
         mRAB.setOnClickListener(this);
         //ADW: secondary aActionButtons
         mLAB2 = (ActionButton) dragLayer.findViewById(R.id.btn_lab2);
         mLAB2.setLauncher(this);
         dragLayer.addDragListener(mLAB2);
         mRAB2 = (ActionButton) dragLayer.findViewById(R.id.btn_rab2);
         mRAB2.setLauncher(this);
         dragLayer.addDragListener(mRAB2);
         mLAB2.setOnClickListener(this);
         mRAB2.setOnClickListener(this);
        
         //ADW: ActionButtons swipe gestures
         mHandleView.setSwipeListener(this);
         mLAB.setSwipeListener(this);
         mLAB2.setSwipeListener(this);
         mRAB.setSwipeListener(this);
         mRAB2.setSwipeListener(this);
         
         mHandleView.setDragger(dragLayer);
         mLAB.setDragger(dragLayer);
         mRAB.setDragger(dragLayer);
         mRAB2.setDragger(dragLayer);
         mLAB2.setDragger(dragLayer);
         
         //ADW linearlayout with apptray, lab and rab
         mDrawerToolbar=findViewById(R.id.drawer_toolbar);
         mHandleView.setNextFocusUpId(R.id.drag_layer);
         mHandleView.setNextFocusLeftId(R.id.drag_layer);
         mLAB.setNextFocusUpId(R.id.drag_layer);
         mLAB.setNextFocusLeftId(R.id.drag_layer);
         mRAB.setNextFocusUpId(R.id.drag_layer);
         mRAB.setNextFocusLeftId(R.id.drag_layer);
         mLAB2.setNextFocusUpId(R.id.drag_layer);
         mLAB2.setNextFocusLeftId(R.id.drag_layer);
         mRAB2.setNextFocusUpId(R.id.drag_layer);
         mRAB2.setNextFocusLeftId(R.id.drag_layer);
         
         mDesktopIndicator=(DesktopIndicator) (findViewById(R.id.desktop_indicator));

 		//ADW: Add focusability to screen items
         mLAB.setFocusable(true);
         mRAB.setFocusable(true);
         mLAB2.setFocusable(true);
         mRAB2.setFocusable(true);
         
         themeFont = Typeface.createFromAsset(getAssets(), "HelveticaNeueLTPro-Bd.otf");
         
         mCurtain=findViewById(R.id.curtain);
         mCurtain.setBackgroundColor(mCurtainBackground);
         mpy();
         mRAB.setVisibility(View.VISIBLE);
         mLAB.setVisibility(View.VISIBLE);
         mRAB.setVisibility(View.VISIBLE);
         mLAB2.setVisibility(View.VISIBLE);
         mHandleView.hideBg(false);
         mRAB.hideBg(false);
         mLAB.hideBg(false);
         mRAB2.hideBg(false);
         mLAB2.hideBg(false);
         
         if(mDesktopIndicator!=null){
        	 mDesktopIndicator.setType(0);
         }
         
         mScreenSelector = (StereoScreenSelector)(ScreenSelector)findViewById(R.id.home_selector);
         mScreenSelector.mPa(this);
         mWorkspace.setSelector(mScreenSelector);
         
    }

    private void mpy() {
    	 if(mWorkspace != null)
             mWorkspace.mPy();		
	}

	/**
     * Creates a view representing a shortcut.
     *
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from R.layout.application.
     */
    View createShortcut(ApplicationInfo info) {
        return createShortcut(R.layout.application2,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), info);
    }

    /**
     * Creates a view representing a shortcut inflated from the specified resource.
     *
     * @param layoutResId The id of the XML layout used to create the shortcut.
     * @param parent The group the shortcut belongs to.
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from layoutResId.
     */
    View createShortcut(int layoutResId, ViewGroup parent, ApplicationInfo info) {
    	ScreenItemView favorite = (ScreenItemView) mInflater.inflate(layoutResId, parent, false);

    	//favorite.setTypeface(themeFont);
    	favorite.mPa(themeFont);
        //favorite.setText(info.title);
        favorite.mPa(info.title);
        //favorite.setCompoundDrawablesWithIntrinsicBounds(null, info.icon, null, null);
        favorite.mPa(info.icon);
        favorite.setTag(info);
        favorite.setOnClickListener(this);
        return favorite;
    }

    /**
     * Add an application shortcut to the workspace.
     *
     * @param data The intent describing the application.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    void completeAddApplication(Context context, Intent data, CellLayout.CellInfo cellInfo,
            boolean insertAtFirst) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final ApplicationInfo info = infoFromApplicationIntent(context, data);
        if (info != null) {
            mWorkspace.addApplicationShortcut(info, cellInfo, insertAtFirst);
        }
    }

    private static ApplicationInfo infoFromApplicationIntent(Context context, Intent data) {
        ComponentName component = data.getComponent();
        PackageManager packageManager = context.getPackageManager();
        ActivityInfo activityInfo = null;
        try {
            activityInfo = packageManager.getActivityInfo(component, 0 /* no flags */);
        } catch (NameNotFoundException e) {
            e(LOG_TAG, "Couldn't find ActivityInfo for selected application", e);
        }

        if (activityInfo != null) {
            ApplicationInfo itemInfo = new ApplicationInfo();

            itemInfo.title = activityInfo.loadLabel(packageManager);
            if (itemInfo.title == null) {
                itemInfo.title = activityInfo.name;
            }

            itemInfo.setActivity(component, Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            //itemInfo.icon = activityInfo.loadIcon(packageManager);
            itemInfo.container = ItemInfo.NO_ID;

            itemInfo.icon = LauncherModel.getIcon(packageManager, context, activityInfo);

            return itemInfo;
        }

        return null;
    }

    /**
     * Add a shortcut to the workspace.
     *
     * @param data The intent describing the shortcut.
     * @param cellInfo The position on screen where to create the shortcut.
     * @param insertAtFirst
     */
    private void completeAddShortcut(Intent data, CellLayout.CellInfo cellInfo,
            boolean insertAtFirst) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final ApplicationInfo info = addShortcut(this, data, cellInfo, false);

        if (!mRestoring) {
            sModel.addDesktopItem(info);

            final View view = createShortcut(info);
            mWorkspace.addInCurrentScreen(view, cellInfo.cellX, cellInfo.cellY, 1, 1, insertAtFirst);
        } else if (sModel.isDesktopLoaded()) {
            sModel.addDesktopItem(info);
        }
        mPq();
    }

    public void mPq() {
    	mWorkspace.mPd(mWorkspace.getCurrentScreen());
	}

	/**
     * Add a widget to the workspace.
     *
     * @param data The intent describing the appWidgetId.
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidget(Intent data, CellLayout.CellInfo cellInfo,
            final boolean insertAtFirst) {

        Bundle extras = data.getExtras();
        final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        if (LOGD) d(LOG_TAG, "dumping extras content="+extras.toString());

        final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        // Calculate the grid spans needed to fit this widget
        CellLayout layout = (CellLayout) mWorkspace.getChildAt(cellInfo.screen);
        Rect rect = AppWidgetHostView.getDefaultPaddingForWidget(this, appWidgetInfo.provider, null);
        final int[] spans = layout.rectToCell(appWidgetInfo.minWidth + rect.left + rect.right, appWidgetInfo.minHeight + rect.top + rect.bottom);
        if(!findSlot(mAddItemCellInfo, spans, mCellCoordinates[0], mCellCoordinates[1])){
            if(appWidgetId != -1){
            	mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
            return;
        }
        
        LauncherAppWidgetInfo info = new LauncherAppWidgetInfo(appWidgetId);
        info.spanX = spans[0];
        info.spanY = spans[1];
        LauncherModel.addItemToDatabase(this, info, LauncherSettings.Favorites.CONTAINER_DESKTOP,
        		mWorkspace.getCurrentScreen(), spans[0], spans[1], false);
        if(!mRestoring) {
        	sModel.addDesktopAppWidget(info);
        	info.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        	info.hostView.setAppWidget(appWidgetId, appWidgetInfo);
        	info.hostView.setTag(info);
        	mWorkspace.addInCurrentScreen(info.hostView, mCellCoordinates[0], mCellCoordinates[1],
        			info.spanX, info.spanY, !mDesktopLocked);
        } else if (sModel.isDesktopLoaded()) {
        	 sModel.addDesktopAppWidget(info);
        }
        if (appWidgetInfo != null) {
        	appwidgetReadyBroadcast(appWidgetId, appWidgetInfo.provider, spans);
        }
        mPq();
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    static ApplicationInfo addShortcut(Context context, Intent data,
            CellLayout.CellInfo cellInfo, boolean notify) {

        final ApplicationInfo info = infoFromShortcutIntent(context, data);
        LauncherModel.addItemToDatabase(context, info, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                cellInfo.screen, cellInfo.cellX, cellInfo.cellY, notify);

        return info;
    }

    private static ApplicationInfo infoFromShortcutIntent(Context context, Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Bitmap bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);

        Drawable icon = null;
        boolean filtered = false;
        boolean customIcon = false;
        ShortcutIconResource iconResource = null;

        if (bitmap != null) {
            icon = new FastBitmapDrawable(Utilities.createBitmapThumbnail(bitmap, context));
            filtered = true;
        } else {
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                try {
                    iconResource = (ShortcutIconResource) extra;
                    final PackageManager packageManager = context.getPackageManager();
                    Resources resources = packageManager.getResourcesForApplication(
                            iconResource.packageName);
                    final int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    icon = resources.getDrawable(id);
                    customIcon = CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(intent.getAction());
                } catch (Exception e) {
                    w(LOG_TAG, "Could not load shortcut icon: " + extra);
                }
            }
        }

        if (icon == null) {
            icon = context.getPackageManager().getDefaultActivityIcon();
        }

        final ApplicationInfo info = new ApplicationInfo();
        info.icon = icon;
        info.filtered = filtered;
        info.title = name;
        info.intent = intent;
        info.customIcon = customIcon;
        info.iconResource = iconResource;

        return info;
    }

    void closeSystemDialogs() {
        getWindow().closeAllPanels();

        try {
            dismissDialog(DIALOG_CREATE_SHORTCUT);
            // Unlock the workspace if the dialog was showing
            mWorkspace.unlock();
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }

        try {
            dismissDialog(DIALOG_RENAME_FOLDER);
            // Unlock the workspace if the dialog was showing
            mWorkspace.unlock();
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
		        try {
        dismissDialog(DIALOG_CHOOSE_GROUP);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
        try {
            dismissDialog(DIALOG_NEW_GROUP);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
        try {
            dismissDialog(DIALOG_DELETE_GROUP_CONFIRM);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
     }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            closeSystemDialogs();
            mr();

            // Set this flag so that onResume knows to close the search dialog if it's open,
            // because this was a new intent (thus a press of 'home' or some such) rather than
            // for example onResume being called when the user pressed the 'back' button.
            mIsNewIntent = true;

            if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) !=
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) {
                if(!isAllAppsVisible() && hasWindowFocus())
                	fireHomeBinding(mHomeBinding);
            	closeDrawer();
                final View v = getWindow().peekDecorView();
                if (v != null && v.getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            } else {
                closeDrawer();
            }
        }
    }

	void mr() {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing())
				try {
					mPopupWindow.dismiss();
				} catch (Exception exception) {
				}
			mPopupWindow = null;
		}
	}

	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // NOTE: Do NOT do this. Ever. This is a terrible and horrifying hack.
        //
        // Home loads the content of the workspace on a background thread. This means that
        // a previously focused view will be, after orientation change, added to the view
        // hierarchy at an undeterminate time in the future. If we were to invoke
        // super.onRestoreInstanceState() here, the focus restoration would fail because the
        // view to focus does not exist yet.
        //
        // However, not invoking super.onRestoreInstanceState() is equally bad. In such a case,
        // panels would not be restored properly. For instance, if the menu is open then the
        // user changes the orientation, the menu would not be opened in the new orientation.
        //
        // To solve both issues Home messes up with the internal state of the bundle to remove
        // the properties it does not want to see restored at this moment. After invoking
        // super.onRestoreInstanceState(), it removes the panels state.
        //
        // Later, when the workspace is done loading, Home calls super.onRestoreInstanceState()
        // again to restore focus and other view properties. It will not, however, restore
        // the panels since at this point the panels' state has been removed from the bundle.
        //
        // This is a bad example, do not do this.
        //
        // If you are curious on how this code was put together, take a look at the following
        // in Android's source code:
        // - Activity.onRestoreInstanceState()
        // - PhoneWindow.restoreHierarchyState()
        // - PhoneWindow.DecorView.onAttachedToWindow()
        //
        // The source code of these various methods shows what states should be kept to
        // achieve what we want here.

        Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
        SparseArray<Parcelable> savedStates = null;
        int focusedViewId = View.NO_ID;

        if (windowState != null) {
            savedStates = windowState.getSparseParcelableArray("android:views");
            windowState.remove("android:views");
            focusedViewId = windowState.getInt("android:focusedViewId", View.NO_ID);
            windowState.remove("android:focusedViewId");
        }

        super.onRestoreInstanceState(savedInstanceState);

        if (windowState != null) {
            windowState.putSparseParcelableArray("android:views", savedStates);
            windowState.putInt("android:focusedViewId", focusedViewId);
            windowState.remove("android:Panels");
        }

        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	//ADW: If we leave the menu open, on restoration it will try to auto find
    	//the ocupied cells. But this could happed before the workspace is fully loaded,
    	//so it can cause a NPE cause of the way we load the desktop columns/rows count.
    	//I prefer to just close it than diggin the code to make it load later...
    	//Accepting patches :-)
    	closeOptionsMenu();
        super.onSaveInstanceState(outState);

        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getCurrentScreen());
        if(mWorkspace!=null){
            final ArrayList<Folder> folders = mWorkspace.getOpenFolders();
            if (folders.size() > 0) {
                final int count = folders.size();
                long[] ids = new long[count];
                for (int i = 0; i < count; i++) {
                    final FolderInfo info = folders.get(i).getInfo();
                    ids[i] = info.id;
                }
                outState.putLongArray(RUNTIME_STATE_USER_FOLDERS, ids);
            }
        }
        final boolean isConfigurationChange = getChangingConfigurations() != 0;

        // When the drawer is opened and we are saving the state because of a
        // configuration change
        if (allAppsOpen && isConfigurationChange) {
            outState.putBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, true);
        }
        if (mAddItemCellInfo != null && mAddItemCellInfo.valid && mWaitingForResult) {
            final CellLayout.CellInfo addItemCellInfo = mAddItemCellInfo;
            final CellLayout layout = (CellLayout) mWorkspace.getChildAt(addItemCellInfo.screen);

            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, addItemCellInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, addItemCellInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, addItemCellInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X, addItemCellInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y, addItemCellInfo.spanY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_COUNT_X, layout.getCountX());
            outState.putInt(RUNTIME_STATE_PENDING_ADD_COUNT_Y, layout.getCountY());
            outState.putBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS,
                   layout.getOccupiedCells());
        }

        if (mFolderInfo != null && mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
        }
    }

    @Override
    public void onDestroy() {
        mDestroyed = true;

        super.onDestroy();

        try {
            mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            w(LOG_TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }

        TextKeyListener.getInstance().release();

        mAllAppsGrid.setAdapter(null);

        sModel.unbind();
        sModel.abortLoaders();
        mWorkspace.unbindWidgetScrollableViews();
        getContentResolver().unregisterContentObserver(mObserver);
        getContentResolver().unregisterContentObserver(mWidgetObserver);
        unregisterReceiver(mApplicationsReceiver);
        unregisterReceiver(mCloseSystemDialogsReceiver);
        unregisterReceiver(mScreenReceiver);
        mWorkspace.unregisterProvider();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(intent==null)return;
        //ADW: closing drawer, removed from onpause
        if (requestCode !=REQUEST_SHOW_APP_LIST && //do not close drawer if it is for switching catalogue.
                !CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(intent.getAction()))
            closeDrawer();
        if (requestCode >= 0) mWaitingForResult = true;
        try{
            super.startActivityForResult(intent, requestCode);
        }catch (Exception e){
            Toast.makeText(this,R.string.activity_not_found,Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery,
            Bundle appSearchData, boolean globalSearch) {

        closeDrawer();

        showSearchDialog(initialQuery, selectInitialQuery, appSearchData, globalSearch);
    }

    /**
     * Show the search dialog immediately, without changing the search widget.
     *
     * @see Activity#startSearch(String, boolean, android.os.Bundle, boolean)
     */
    void showSearchDialog(String initialQuery, boolean selectInitialQuery,
            Bundle appSearchData, boolean globalSearch) {

        if (initialQuery == null) {
            // Use any text typed in the launcher as the initial query
            initialQuery = getTypedText();
            clearTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }

    /**
     * Cancel search dialog if it is open.
     */
    void stopSearch() {
        // Close search dialog
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.stopSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDesktopLocked && mSavedInstanceState == null) return false;

        super.onCreateOptionsMenu(menu);
        menu.add(MENU_GROUP_ADD, MENU_ADD, 0, R.string.menu_add)
                .setIcon(R.drawable.menu_add)
                .setAlphabeticShortcut('A');
        menu.add(MENU_GROUP_NORMAL, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
                 .setIcon(R.drawable.menu_wallpaper)
                 .setAlphabeticShortcut('W');
        menu.add(MENU_GROUP_NORMAL, MENU_2D3D, 0, R.string.menu_swich_2d_3d)
                .setIcon(R.drawable.menu_2d3d)
                .setAlphabeticShortcut('E');

        final Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
        settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        menu.add(MENU_GROUP_NORMAL, MENU_SETTINGS, 0, R.string.menu_settings)
                .setIcon(R.drawable.menu_setting).setAlphabeticShortcut('P')
                .setIntent(settings);
        
        final Intent help = new Intent(Intent.ACTION_VIEW);
        help.addCategory(Intent.CATEGORY_BROWSABLE);
        help.setData(Uri.parse("http://www.evangelion.co.jp/nerv_keitai/help/"));
        help.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        		Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        menu.add(MENU_GROUP_NORMAL, MENU_HELP, 0, R.string.help).setIcon(R.drawable.other28).setIntent(help);
        menu.add(MENU_GROUP_NORMAL, MENU_LICENSE, 0, R.string.license_information).setIcon(R.drawable.other1);
     return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean flag = false;
        if(mPopupWindow != null) {
        	if(mPopupWindow.isShowing()) {
        		mPopupWindow.dismiss();
        		flag = true;
        	}
        }
        // We can't trust the view state here since views we may not be done binding.
        // Get the vacancy state from the model instead.
		mMenuAddInfo = mWorkspace.findAllVacantCellsFromModel();
		menu.setGroupVisible(MENU_GROUP_ADD, mMenuAddInfo != null && mMenuAddInfo.valid && (!allAppsOpen) && !Q && !flag );
		menu.setGroupVisible(MENU_GROUP_NORMAL, !allAppsOpen && !Q);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
            	addItems();
                return true;
            case MENU_WALLPAPER_SETTINGS:
                startWallpaper();
                return true;
            case MENU_2D3D:
            	switch2d3d();
                return true;
            case MENU_LICENSE:
            	startActivityForResult(new Intent(this,LicenseActivity.class), REQUEST_14);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
     /**
     * Indicates that we want global search for this activity by setting the globalSearch
     * argument for {@link #startSearch} to true.
     */

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        return true;
    }

    private void addItems() {
        showAddDialog(mMenuAddInfo);
    }

    private void removeShortcutsForPackage(String packageName) {
        if (packageName != null && packageName.length() > 0) {
            mWorkspace.removeShortcutsForPackage(packageName);
        }
    }

    private void updateShortcutsForPackage(String packageName) {
        if (packageName != null && packageName.length() > 0) {
            mWorkspace.updateShortcutsForPackage(packageName);
            //ADW: Update ActionButtons icons
            mLAB.reloadIcon(packageName);
            mLAB2.reloadIcon(packageName);
            mRAB.reloadIcon(packageName);
            mRAB2.reloadIcon(packageName);
            mHandleView.reloadIcon(packageName);
        }
    }

    void addAppWidget(final Intent data) {
        // TODO: catch bad widget exception when sent
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        try
        {
        	Bundle metadata = getPackageManager().getReceiverInfo(appWidget.provider,
        			PackageManager.GET_META_DATA).metaData;
        	if (metadata != null) {
	        	if (metadata.containsKey(LauncherMetadata.Requirements.APIVersion))
	        	{
	        			int requiredApiVersion = metadata.getInt(LauncherMetadata.Requirements.APIVersion);
	        			if (requiredApiVersion > LauncherMetadata.CurrentAPIVersion)
	        			{
	        				onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_CANCELED, data);
	        				// Show a nice toast here to tell the user why the widget is rejected.
	        				new AlertDialog.Builder(this)
	        					.setTitle(R.string.adw_version)
	        					.setCancelable(true)
	        					.setIcon(R.drawable.magi)
	        					.setPositiveButton(getString(android.R.string.ok), null)
	        					.setMessage(getString(R.string.scrollable_api_required))
	        					.create().show();
	        				return;
	        			}
	        	}
        	}
        }
        catch(PackageManager.NameNotFoundException expt)
        {
        	// No Metadata available... then it is all OK...
        }
        configureOrAddAppWidget(data);
    }

    private void configureOrAddAppWidget(Intent data) {
    	int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
    	AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, data);
        }
    }

    void processShortcut(Intent intent, int requestCodeApplication, int requestCodeShortcut) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            startActivityForResult(pickIntent, requestCodeApplication);
        } else {
            startActivityForResult(intent, requestCodeShortcut);
        }
    }

    void addLiveFolder(Intent intent) {
        // Handle case where user selected "Folder"
        String folderName = getResources().getString(R.string.group_folder);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (folderName != null && folderName.equals(shortcutName)) {
            addFolder(!mDesktopLocked);
        } else {
            startActivityForResult(intent, REQUEST_CREATE_LIVE_FOLDER);
        }
    }

    void addFolder(boolean insertAtFirst) {
        UserFolderInfo folderInfo = new UserFolderInfo();
        folderInfo.title = getText(R.string.folder_name);

        CellLayout.CellInfo cellInfo = mAddItemCellInfo;
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        // Update the model
        LauncherModel.addItemToDatabase(this, folderInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                mWorkspace.getCurrentScreen(), cellInfo.cellX, cellInfo.cellY, false);
        sModel.addDesktopItem(folderInfo);
        sModel.addFolder(folderInfo);
        
        mPq();

        // Create the view
        FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), folderInfo);
        if(themeFont!=null)((ScreenItemView)newFolder).mPa(themeFont);
        mWorkspace.addInCurrentScreen(newFolder,
                cellInfo.cellX, cellInfo.cellY, 1, 1, insertAtFirst);
    }

    private void completeAddLiveFolder(Intent data, CellLayout.CellInfo cellInfo,
            boolean insertAtFirst) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final LiveFolderInfo info = addLiveFolder(this, data, cellInfo, false);

        if (!mRestoring) {
            sModel.addDesktopItem(info);

            final View view = LiveFolderIcon.fromXml(R.layout.live_folder_icon, this,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), info);
            if(themeFont!=null)((TextView)view).setTypeface(themeFont);
            mWorkspace.addInCurrentScreen(view, cellInfo.cellX, cellInfo.cellY, 1, 1, insertAtFirst);
        } else if (sModel.isDesktopLoaded()) {
            sModel.addDesktopItem(info);
        }
        mPq();
    }

    static LiveFolderInfo addLiveFolder(Context context, Intent data,
            CellLayout.CellInfo cellInfo, boolean notify) {

        Intent baseIntent = data.getParcelableExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT);
        String name = data.getStringExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME);

        Drawable icon = null;
        boolean filtered = false;
        Intent.ShortcutIconResource iconResource = null;

        Parcelable extra = data.getParcelableExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON);
        if (extra != null && extra instanceof Intent.ShortcutIconResource) {
            try {
                iconResource = (Intent.ShortcutIconResource) extra;
                final PackageManager packageManager = context.getPackageManager();
                Resources resources = packageManager.getResourcesForApplication(
                        iconResource.packageName);
                final int id = resources.getIdentifier(iconResource.resourceName, null, null);
                icon = resources.getDrawable(id);
            } catch (Exception e) {
                w(LOG_TAG, "Could not load live folder icon: " + extra);
            }
        }

        if (icon == null) {
            icon = context.getResources().getDrawable(R.drawable.ic_launcher_folder);
        }

        final LiveFolderInfo info = new LiveFolderInfo();
        info.icon = icon;
        info.filtered = filtered;
        info.title = name;
        info.iconResource = iconResource;
        info.uri = data.getData();
        info.baseIntent = baseIntent;
        info.displayMode = data.getIntExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE,
                LiveFolders.DISPLAY_MODE_GRID);

        LauncherModel.addItemToDatabase(context, info, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                cellInfo.screen, cellInfo.cellX, cellInfo.cellY, notify);
        sModel.addFolder(info);

        return info;
    }

    private boolean findSingleSlot(CellLayout.CellInfo cellInfo) {
        final int[] xy = new int[2];
        if (findSlot(cellInfo, xy, 1, 1)) {
            cellInfo.cellX = xy[0];
            cellInfo.cellY = xy[1];
            return true;
        }
        return false;
    }

    private boolean findSlot(CellLayout.CellInfo cellInfo, int[] xy, int spanX, int spanY) {
        if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
            boolean[] occupied = mSavedState != null ?
                    mSavedState.getBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS) : null;
            cellInfo = mWorkspace.findAllVacantCells(occupied);
            if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
                Toast.makeText(this, getString(R.string.out_of_space), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void showNotifications() {
    	try {
            Object service = getSystemService("statusbar");
            if (service != null) {
                Method expand = service.getClass().getMethod("expand");
                expand.invoke(service);
            }
        } catch (Exception e) {
        }
    }

    private void startWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        WallpaperManager wm = WallpaperManager.getInstance(this);
        WallpaperInfo wi = wm.getWallpaperInfo();
        if (wi != null && wi.getSettingsActivity() != null) {
            LabeledIntent li = new LabeledIntent(getPackageName(),
                    R.string.configure_wallpaper, 0);
            li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
        }
        startActivity(chooser);
    }
    
    private void switch2d3d(){
    	SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	Y = !Y;
    	SharedPreferences.Editor editor = sharedpreferences.edit();
    	editor.putBoolean("screen_stereo", Y);
    	editor.commit();
    	mScreenSelector.setStereoView(Y);
    }

    /**
     * Registers various intent receivers. The current implementation registers
     * only a wallpaper intent receiver to let other applications change the
     * wallpaper.
     */
    private void registerIntentReceivers() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mApplicationsReceiver, filter);
        filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        registerReceiver(mApplicationsReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, filter);

    }

    /**
     * Registers various content observers. The current implementation registers
     * only a favorites observer to keep track of the favorites applications.
     */
    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true, mObserver);
        resolver.registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI,
                true, mWidgetObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!event.isCanceled()) {
                        mWorkspace.dispatchKeyEvent(event);
                        if (allAppsOpen) {
                            closeDrawer();
                        } else {
                        	if(!Q)
                        		closeFolder();
                        }
                        if(isPreviewing()){
                        	dismissPreviews();
                        }
                        mr();
                    }
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void closeDrawer() {
        if (allAppsOpen) {
            closeAllApps();
            if (mAllAppsGrid.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
            }
        }
    }

    private void closeFolder() {
        Folder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            closeFolder(folder);
        }
    }

    void closeFolder(Folder folder) {
        folder.getInfo().opened = false;
        ViewGroup parent = (ViewGroup) folder.getParent();
        if (parent != null) {
            parent.removeView(folder);
        }
        folder.onClose();
        if (Z != null && Z.contains(folder)) {
        	Z.remove(folder);
        }
    }

    /**
     * When the notification that favorites have changed is received, requests
     * a favorites list refresh.
     */
    private void onFavoritesChanged() {
        mDesktopLocked = true;
        sModel.loadUserItems(false, this, false, false);
    }

    /**
     * Re-listen when widgets are reset.
     */
    private void onAppWidgetReset() {
        mAppWidgetHost.startListening();
    }

    void onDesktopItemsLoaded(ArrayList<ItemInfo> shortcuts,
            ArrayList<LauncherAppWidgetInfo> appWidgets) {
        if (mDestroyed) {
            if (LauncherModel.DEBUG_LOADERS) {
                d(LauncherModel.LOG_TAG, "  ------> destroyed, ignoring desktop items");
            }
            return;
        }
        bindDesktopItems(shortcuts, appWidgets);
    }

    /**
     * Refreshes the shortcuts shown on the workspace.
     */
    private void bindDesktopItems(ArrayList<ItemInfo> shortcuts,
            ArrayList<LauncherAppWidgetInfo> appWidgets) {

        final ApplicationsAdapter drawerAdapter = sModel.getApplicationsAdapter();
        if (shortcuts == null || appWidgets == null || drawerAdapter == null) {
            if (LauncherModel.DEBUG_LOADERS) d(LauncherModel.LOG_TAG, "  ------> a source is null");
            return;
        }

        final Workspace workspace = mWorkspace;
        int count = workspace.getChildCount();
        for (int i = 0; i < count; i++) {
            ((ViewGroup) workspace.getChildAt(i)).removeAllViewsInLayout();
        }
        if (DEBUG_USER_INTERFACE) {
            android.widget.Button finishButton = new android.widget.Button(this);
            finishButton.setText("Finish");
            workspace.addInScreen(finishButton, 1, 0, 0, 1, 1);

            finishButton.setOnClickListener(new android.widget.Button.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // Flag any old binder to terminate early
        if (mBinder != null) {
            mBinder.mTerminate = true;
        }

        mBinder = new DesktopBinder(this, shortcuts, appWidgets, drawerAdapter);
        mBinder.startBindingItems();
    }

    private void bindItems(Launcher.DesktopBinder binder,
            ArrayList<ItemInfo> shortcuts, int start, int count) {

        final Workspace workspace = mWorkspace;
        final boolean desktopLocked = mDesktopLocked;
        final int end = Math.min(start + DesktopBinder.ITEMS_COUNT, count);
        int i = start;

        for ( ; i < end; i++) {
            final ItemInfo item = shortcuts.get(i);
            switch ((int)item.container){
	            case LauncherSettings.Favorites.CONTAINER_LAB:
	            	mLAB.UpdateLaunchInfo(item);
	            	break;
	            case LauncherSettings.Favorites.CONTAINER_RAB:
	            	mRAB.UpdateLaunchInfo(item);
	            	break;
	            case LauncherSettings.Favorites.CONTAINER_LAB2:
	            	mLAB2.UpdateLaunchInfo(item);
	            	break;
	            case LauncherSettings.Favorites.CONTAINER_RAB2:
	            	mRAB2.UpdateLaunchInfo(item);
	            	break;
                case LauncherSettings.Favorites.CONTAINER_MAB:
                    mHandleView.UpdateLaunchInfo(item);
                    break;
				default:
		            switch (item.itemType) {
		                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
		                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
		                    final View shortcut = createShortcut((ApplicationInfo) item);
		                    workspace.addInScreen(shortcut, item.screen, item.cellX, item.cellY, 1, 1,
		                            !desktopLocked);
		                    break;
		                case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
		                    final FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
		                            (ViewGroup) workspace.getChildAt(workspace.getCurrentScreen()),
		                            (UserFolderInfo) item);
		                    if(themeFont!=null)((ScreenItemView)newFolder).mPa(themeFont);
		                    workspace.addInScreen(newFolder, item.screen, item.cellX, item.cellY, 1, 1,
		                            !desktopLocked);
		                    break;
		                case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
		                    final FolderIcon newLiveFolder = LiveFolderIcon.fromXml(
		                            R.layout.live_folder_icon, this,
		                            (ViewGroup) workspace.getChildAt(workspace.getCurrentScreen()),
		                            (LiveFolderInfo) item);
		                    if(themeFont!=null)((ScreenItemView)newLiveFolder).mPa(themeFont);
		                    workspace.addInScreen(newLiveFolder, item.screen, item.cellX, item.cellY, 1, 1,
		                            !desktopLocked);
		                    break;
		            }
            }
        }

        workspace.requestLayout();

        if (end >= count) {
            finishBindDesktopItems();
            binder.startBindingDrawer();
        } else {
            binder.obtainMessage(DesktopBinder.MESSAGE_BIND_ITEMS, i, count).sendToTarget();
        }
    }

    private void finishBindDesktopItems() {
        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
            }

            final long[] userFolders = mSavedState.getLongArray(RUNTIME_STATE_USER_FOLDERS);
            if (userFolders != null) {
                for (long folderId : userFolders) {
                    final FolderInfo info = sModel.findFolderById(folderId);
                    if (info != null) {
                        openFolder(info);
                    }
                }
                final Folder openFolder = mWorkspace.getOpenFolder();
                if (openFolder != null) {
                    openFolder.requestFocus();
                }
            }

            final boolean allApps = mSavedState.getBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, false);
            if (allApps) {
                showAllApps();
            }
            mSavedState = null;
        }

        if (mSavedInstanceState != null) {
            //ADW: sometimes on rotating the phone, some widgets fail to restore its states.... so... damn.
            try{
                super.onRestoreInstanceState(mSavedInstanceState);
            }catch(Exception e){}
            mSavedInstanceState = null;
        }

        if (allAppsOpen && !mAllAppsGrid.hasFocus()) {
            mAllAppsGrid.requestFocus();
        }

        mDesktopLocked = false;

    }

    private void bindDrawer(Launcher.DesktopBinder binder,
            ApplicationsAdapter drawerAdapter) {
        drawerAdapter.buildViewCache((ViewGroup)mAllAppsGrid);
        mAllAppsGrid.setAdapter(drawerAdapter);
        binder.startBindingAppWidgetsWhenIdle();
    }

    private void bindAppWidgets(Launcher.DesktopBinder binder,
            LinkedList<LauncherAppWidgetInfo> appWidgets) {

        final Workspace workspace = mWorkspace;
        final boolean desktopLocked = mDesktopLocked;

        if (!appWidgets.isEmpty()) {
            final LauncherAppWidgetInfo item = appWidgets.removeFirst();

            final int appWidgetId = item.appWidgetId;
            final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
            item.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

            if (LOGD) {
                d(LOG_TAG, String.format("about to setAppWidget for id=%d, info=%s",
                       appWidgetId, appWidgetInfo));
            }

            item.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            item.hostView.setTag(item);

            workspace.addInScreen(item.hostView, item.screen, item.cellX,
                    item.cellY, item.spanX, item.spanY, !desktopLocked);

            workspace.requestLayout();
            // finish load a widget, send it an intent
            if(appWidgetInfo!=null)
            	appwidgetReadyBroadcast(appWidgetId, appWidgetInfo.provider, new int[] {item.spanX, item.spanY});
        }

        if (appWidgets.isEmpty()) {
            if (PROFILE_ROTATE) {
                android.os.Debug.stopMethodTracing();
            }
            bR = true;
            mWorkspace.mPb(true);
        } else {
            binder.obtainMessage(DesktopBinder.MESSAGE_BIND_APPWIDGETS).sendToTarget();
        }
    }

    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
    public void onClick(View v) {
        Object tag = v.getTag();
        //TODO:ADW Check whether to display a toast if clicked mLAB or mRAB withount binding
        if(tag==null && v instanceof ActionButton){
    		Toast t=Toast.makeText(this, R.string.toast_no_application_def, Toast.LENGTH_SHORT);
    		t.show();
    		return;
    	}
        if (tag instanceof ApplicationInfo) {
            // Open shortcut
        	final ApplicationInfo info=(ApplicationInfo) tag;
            final Intent intent = info.intent;
            if (CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(intent.getAction())) {
            	fireHomeBinding(intent.getIntExtra(LauncherActions.DefaultLauncherAction.EXTRA_BINDINGVALUE, 0));
            	return;
            }
            int[] pos = new int[2];
            v.getLocationOnScreen(pos);
            try{
            intent.setSourceBounds(
                    new Rect(pos[0], pos[1], pos[0]+v.getWidth(), pos[1]+v.getHeight()));
            }catch(NoSuchMethodError e){};
            startActivitySafely(intent);
        } else if (tag instanceof FolderInfo) {
            handleFolderClick((FolderInfo) tag);
        }
    }

    public void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            mWorkspace.setCurrentScreen(mWorkspace.getCurrentScreen());
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            e(LOG_TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }

    private void handleFolderClick(FolderInfo folderInfo) {
        if (!folderInfo.opened) {
            // Close any open folder
            closeFolder();
            // Open the requested folder
            openFolder(folderInfo);
        } else {
            // Find the open folder...
            Folder openFolder = mWorkspace.getFolderForTag(folderInfo);
            int folderScreen;
            if (openFolder != null) {
                folderScreen = mWorkspace.getScreenForView(openFolder);
                // .. and close it
                closeFolder(openFolder);
                if (folderScreen != mWorkspace.getCurrentScreen()) {
                    // Close any folder open on the current screen
                    closeFolder();
                    // Pull the folder onto this screen
                    openFolder(folderInfo);
                }
            }
        }
    }

    /**
     * Opens the user fodler described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderInfo The FolderInfo describing the folder to open.
     */
    private void openFolder(FolderInfo folderInfo) {
        Folder openFolder;

        if (folderInfo instanceof UserFolderInfo) {
            openFolder = UserFolder.fromXml(this);
        } else if (folderInfo instanceof LiveFolderInfo) {
            openFolder = jp.co.evangelion.nervhome.LiveFolder.fromXml(this, folderInfo);
        } else {
            return;
        }

        openFolder.setDragger(mDragLayer);
        openFolder.setLauncher(this);

        openFolder.bind(folderInfo);
        folderInfo.opened = true;

        if(folderInfo.container==LauncherSettings.Favorites.CONTAINER_DOCKBAR ||
        		folderInfo.container==LauncherSettings.Favorites.CONTAINER_LAB ||
        		folderInfo.container==LauncherSettings.Favorites.CONTAINER_RAB ||
        		folderInfo.container==LauncherSettings.Favorites.CONTAINER_LAB2 ||
        		folderInfo.container==LauncherSettings.Favorites.CONTAINER_RAB2){
        	mWorkspace.addInScreen(openFolder, mWorkspace.getCurrentScreen(), 0, 0, mWorkspace.currentDesktopColumns(), mWorkspace.currentDesktopRows());
        }else{
        	mWorkspace.addInScreen(openFolder, folderInfo.screen, 0, 0, mWorkspace.currentDesktopColumns(), mWorkspace.currentDesktopRows());
        }
        openFolder.onOpen();
        if (Z != null) {
        	Z  = new ArrayList<Folder>();
        }
        Z.add(openFolder);
    }

    /**
     * Returns true if the workspace is being loaded. When the workspace is loading,
     * no user interaction should be allowed to avoid any conflict.
     *
     * @return True if the workspace is locked, false otherwise.
     */
    boolean isWorkspaceLocked() {
        return mDesktopLocked;
    }

    public boolean onLongClick(View v) {
        if (mDesktopLocked) {
            return false;
        }

        if (!(v instanceof CellLayout)) {
            v = (View) v.getParent();
        }

        CellLayout.CellInfo cellInfo = (CellLayout.CellInfo) v.getTag();

        // This happens when long clicking an item with the dpad/trackball
        if (cellInfo == null) {
            return true;
        }

        if (mWorkspace.allowLongPress()) {
            if (cellInfo.cell == null) {
                if (cellInfo.valid) {
                    // User long pressed on empty space
                    mWorkspace.setAllowLongPress(false);
                    showAddDialog(cellInfo);
                }
            } else {
                if (!(cellInfo.cell instanceof Folder)) {
                    // User long pressed on an item
                    mWorkspace.startDrag(cellInfo);
                }
            }
        }
        return true;
    }

    static LauncherModel getModel() {
        return sModel;
    }

    public void closeAllApplications() {
        closeAllApps();
    }

    View getDrawerHandle() {
        return mHandleView;
    }

    /*boolean isDrawerDown() {
        return !mDrawer.isMoving() && !mDrawer.isOpened();
    }

    boolean isDrawerUp() {
        return mDrawer.isOpened() && !mDrawer.isMoving();
    }

    boolean isDrawerMoving() {
        return mDrawer.isMoving();
    }*/

    Workspace getWorkspace() {
        return mWorkspace;
    }
    //ADW: we return a View, so classes using this should cast
    // to AllAppsGridView or AllAppsSlidingView if they need to access proper members
    View getApplicationsGrid() {
        return (View)mAllAppsGrid;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                return new CreateShortcut().createDialog();
            case DIALOG_RENAME_FOLDER:
                return new RenameFolder().createDialog();
     }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                break;
            case DIALOG_RENAME_FOLDER:
                if (mFolderInfo != null) {
                    EditText input = (EditText) dialog.findViewById(R.id.folder_name);
                    final CharSequence text = mFolderInfo.title;
                    input.setText(text);
                    input.setSelection(0, text.length());
                }
                break;
        }
    }
    void showRenameDialog(FolderInfo info) {
        mFolderInfo = info;
        mWaitingForResult = true;
        showDialog(DIALOG_RENAME_FOLDER);
    }

    private void showAddDialog(CellLayout.CellInfo cellInfo) {
        mAddItemCellInfo = cellInfo;
        mWaitingForResult = true;
        showDialog(DIALOG_CREATE_SHORTCUT);
    }

    private void pickShortcut(int requestCode, int title) {
        Bundle bundle = new Bundle();

//        ArrayList<String> shortcutNames = new ArrayList<String>();
//        shortcutNames.add(getString(R.string.group_applications));
//        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
//
//        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
//        shortcutIcons.add(ShortcutIconResource.fromContext(Launcher.this,
//                        R.drawable.ic_launcher_application));
//        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);
//
//        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
//        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
//        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(title));
//        pickIntent.putExtras(bundle);
//
//        startActivityForResult(pickIntent, requestCode);
    }

    private class RenameFolder {
        private EditText mInput;

        Dialog createDialog() {
            mWaitingForResult = true;
            final View layout = View.inflate(Launcher.this, R.layout.rename_folder, null);
            mInput = (EditText) layout.findViewById(R.id.folder_name);

            AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
            builder.setIcon(0);
            builder.setTitle(getString(R.string.rename_folder_title));
            builder.setCancelable(true);
            builder.setOnCancelListener(new Dialog.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    cleanup();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanup();
                    }
                }
            );
            builder.setPositiveButton(getString(R.string.rename_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeFolderName();
                    }
                }
            );
            builder.setView(layout);

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    mWorkspace.lock();
                }
            });

            return dialog;
        }

        private void changeFolderName() {
            final String name = mInput.getText().toString();
            if (!TextUtils.isEmpty(name)) {
                // Make sure we have the right folder info
                mFolderInfo = sModel.findFolderById(mFolderInfo.id);
                mFolderInfo.title = name;
                LauncherModel.updateItemInDatabase(Launcher.this, mFolderInfo);

                if (mDesktopLocked) {
                    sModel.loadUserItems(false, Launcher.this, false, false);
                } else {
                    final FolderIcon folderIcon = (FolderIcon)
                            mWorkspace.getViewForTag(mFolderInfo);
                    if (folderIcon != null) {
                        folderIcon.mPa(name);
                        folderIcon.invalidate();
                        getWorkspace().requestLayout();
                    } else {
                        mDesktopLocked = true;
                        sModel.loadUserItems(false, Launcher.this, false, false);
                    }
                }
            }
            cleanup();
        }

        private void cleanup() {
            mWorkspace.unlock();
            try{
                dismissDialog(DIALOG_RENAME_FOLDER);
            }catch (Exception e){
                //Restarted while dialog or whatever causes IllegalStateException???
            }
            mWaitingForResult = false;
            mFolderInfo = null;
        }
    }
    /**
     * Displays the shortcut creation dialog and launches, if necessary, the
     * appropriate activity.
     */
    private class CreateShortcut implements DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
            DialogInterface.OnShowListener {

        private AddAdapter mAdapter;

        Dialog createDialog() {
            mWaitingForResult = true;

            mAdapter = new AddAdapter(Launcher.this);

            final AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
            builder.setTitle(getString(R.string.menu_item_add_item));
            builder.setAdapter(mAdapter, this);

            builder.setInverseBackgroundForced(true);

            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(this);
            dialog.setOnDismissListener(this);
            dialog.setOnShowListener(this);

            return dialog;
        }

        public void onCancel(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        public void onDismiss(DialogInterface dialog) {
            mWorkspace.unlock();
        }

        private void cleanup() {
            mWorkspace.unlock();
            try{
                dismissDialog(DIALOG_CREATE_SHORTCUT);
            }catch (Exception e){
                //Restarted while dialog or whatever causes IllegalStateException???
            }
        }

        /**
         * Handle the action clicked in the "Add to home" dialog.
         */
        public void onClick(DialogInterface dialog, int which) {
            cleanup();

            switch (which) {
                case AddAdapter.ITEM_SHORTCUT: {
                    // Insert extra item to handle picking application
                    pickShortcut(REQUEST_PICK_SHORTCUT, R.string.title_select_shortcut);
                    break;
                }

                case AddAdapter.ITEM_APPWIDGET: {
                    int appWidgetId = Launcher.this.mAppWidgetHost.allocateAppWidgetId();

                    Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                    pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    // start the pick activity
                    startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
                    break;
                }

                case AddAdapter.ITEM_LIVE_FOLDER: {
                	addFolder(!mDesktopLocked);
                    break;
                }

                case AddAdapter.ITEM_ANYCUT: {
                	AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
                	builder.setTitle(R.string.group_magi_widgets);
                	final C123c c123c = new C123c(Launcher.this, mAppWidgetManager);
                	builder.setAdapter(c123c, new Dialog.OnClickListener(){
                		 @Override
                         public void onClick(DialogInterface dialog, int which) {
                        	 Intent intent = c123c.mPa(which);
                        	 int j = mAppWidgetHost.allocateAppWidgetId();
                        	 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, j);
                        	 mAppWidgetManager.bindAppWidgetIdIfAllowed(j, intent.getComponent());
                        	 addAppWidget(intent);
                         }
                	 });
                	builder.show();
                    break;
                }
                case AddAdapter.ITEM_LAUNCHER_ACTION: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
                    builder.setTitle(getString(R.string.launcher_actions));
                    final ListAdapter adapter = LauncherActions.getInstance().getSelectActionAdapter();
                    builder.setAdapter(adapter, new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LauncherActions.Action action = (LauncherActions.Action)adapter.getItem(which);
                                    Intent result = new Intent();
                                    result.putExtra(Intent.EXTRA_SHORTCUT_NAME, action.getName());
                                    result.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                                            LauncherActions.getInstance().getIntentForAction(action));
                                    ShortcutIconResource iconResource = new ShortcutIconResource();
                                    iconResource.packageName = Launcher.this.getPackageName();
                                    iconResource.resourceName = getResources().getResourceName(action.getIconResourceId());
                                    result.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
                                    onActivityResult(REQUEST_CREATE_SHORTCUT, RESULT_OK, result);
                                }
                            });
                    builder.create().show();
                    break;
                }
            }
        }

        public void onShow(DialogInterface dialog) {
            mWorkspace.lock();
        }
    }

    /**
     * Receives notifications when applications are added/removed.
     */
    private class ApplicationsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {

	            final String packageName = intent.getData().getSchemeSpecificPart();
	            final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

	            if (LauncherModel.DEBUG_LOADERS) {
	                d(LauncherModel.LOG_TAG, "application intent received: " + action +
	                        ", replacing=" + replacing);
	                d(LauncherModel.LOG_TAG, "  --> " + intent.getData());
	            }

	            if (!Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
	                if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
	                    if (!replacing) {
	                        removeShortcutsForPackage(packageName);
	                        if (LauncherModel.DEBUG_LOADERS) {
	                            d(LauncherModel.LOG_TAG, "  --> remove package");
	                        }
	                        sModel.removePackage(Launcher.this, packageName);
	                    }
	                    // else, we are replacing the package, so a PACKAGE_ADDED will be sent
	                    // later, we will update the package at this time
	                } else {
	                    if (!replacing) {
	                        if (LauncherModel.DEBUG_LOADERS) {
	                            d(LauncherModel.LOG_TAG, "  --> add package");
	                        }
	                        sModel.addPackage(Launcher.this, packageName);
	                    } else {
	                        if (LauncherModel.DEBUG_LOADERS) {
	                            d(LauncherModel.LOG_TAG, "  --> update package " + packageName);
	                        }
	                        sModel.updatePackage(Launcher.this, packageName);
	                        updateShortcutsForPackage(packageName);
	                    }
	                }
	                removeDialog(DIALOG_CREATE_SHORTCUT);
	            } else {
	                if (LauncherModel.DEBUG_LOADERS) {
	                    d(LauncherModel.LOG_TAG, "  --> sync package " + packageName);
	                }
	                try {
	                	android.content.pm.ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);		                
		                if(appInfo != null && appInfo.enabled) {
	                        removeShortcutsForPackage(packageName);
	                        sModel.removePackage(Launcher.this, packageName);
		                } else {
			                sModel.syncPackage(Launcher.this, packageName);
		                }
	                } catch (NameNotFoundException e){
	                	e.printStackTrace();
		                sModel.syncPackage(Launcher.this, packageName);
	                }
	            }
            } else {
                if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
                     String packages[] = intent.getStringArrayExtra(
                             Intent.EXTRA_CHANGED_PACKAGE_LIST);
                     if (packages == null || packages.length == 0) {
                         return;
                     }else{
                    	 for(int i=0;i<packages.length;i++){
                    		 sModel.addPackage(Launcher.this, packages[i]);
                    		 updateShortcutsForPackage(packages[i]);
                    	 }
                     }
                } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                     String packages[] = intent.getStringArrayExtra(
                             Intent.EXTRA_CHANGED_PACKAGE_LIST);
                     if (packages == null || packages.length == 0) {
                         return;
                     }else{
                    	 for(int i=0;i<packages.length;i++){
                    		 sModel.removePackage(Launcher.this, packages[i]);
                    		 //ADW: We tell desktop to update packages
                    		 //(probably will load the standard android icon)
                    		 //to show the user the app is no more available.
                    		 //We may add the froyo code to just load a grayscale version of the icon, but...
                    		 updateShortcutsForPackage(packages[i]);
                    	 }
                     }
                }
            }
        }
    }

    /**
     * Receives notifications when applications are added/removed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
        }
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private class FavoritesChangeObserver extends ContentObserver {
        public FavoritesChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onFavoritesChanged();
        }
    }

    /**
     * Receives notifications whenever the appwidgets are reset.
     */
    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onAppWidgetReset();
        }
    }
    
    private class ScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			mWorkspace.mPb(true);
		}    	
    }

    private static class DesktopBinder extends Handler implements MessageQueue.IdleHandler {
        static final int MESSAGE_BIND_ITEMS = 0x1;
        static final int MESSAGE_BIND_APPWIDGETS = 0x2;
        static final int MESSAGE_BIND_DRAWER = 0x3;

        // Number of items to bind in every pass
        static final int ITEMS_COUNT = 6;

        private final ArrayList<ItemInfo> mShortcuts;
        private final LinkedList<LauncherAppWidgetInfo> mAppWidgets;
        private final ApplicationsAdapter mDrawerAdapter;
        private final WeakReference<Launcher> mLauncher;

        public boolean mTerminate = false;

        DesktopBinder(Launcher launcher, ArrayList<ItemInfo> shortcuts,
                ArrayList<LauncherAppWidgetInfo> appWidgets,
                ApplicationsAdapter drawerAdapter) {

            mLauncher = new WeakReference<Launcher>(launcher);
            mShortcuts = shortcuts;
            mDrawerAdapter = drawerAdapter;

            // Sort widgets so active workspace is bound first
            final int currentScreen = launcher.mWorkspace.getCurrentScreen();
            final int size = appWidgets.size();
            mAppWidgets = new LinkedList<LauncherAppWidgetInfo>();

            for (int i = 0; i < size; i++) {
                LauncherAppWidgetInfo appWidgetInfo = appWidgets.get(i);
                if (appWidgetInfo.screen == currentScreen) {
                    mAppWidgets.addFirst(appWidgetInfo);
                } else {
                    mAppWidgets.addLast(appWidgetInfo);
                }
            }

            if (LauncherModel.DEBUG_LOADERS) {
                d(Launcher.LOG_TAG, "------> binding " + shortcuts.size() + " items");
                d(Launcher.LOG_TAG, "------> binding " + appWidgets.size() + " widgets");
            }
        }

        public void startBindingItems() {
            if (LauncherModel.DEBUG_LOADERS) d(Launcher.LOG_TAG, "------> start binding items");
            obtainMessage(MESSAGE_BIND_ITEMS, 0, mShortcuts.size()).sendToTarget();
        }

        public void startBindingDrawer() {
            obtainMessage(MESSAGE_BIND_DRAWER).sendToTarget();
        }

        public void startBindingAppWidgetsWhenIdle() {
            // Ask for notification when message queue becomes idle
            final MessageQueue messageQueue = Looper.myQueue();
            messageQueue.addIdleHandler(this);
        }

        public boolean queueIdle() {
            // Queue is idle, so start binding items
            startBindingAppWidgets();
            return false;
        }

        public void startBindingAppWidgets() {
            obtainMessage(MESSAGE_BIND_APPWIDGETS).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            Launcher launcher = mLauncher.get();
            if (launcher == null || mTerminate) {
                return;
            }

            switch (msg.what) {
                case MESSAGE_BIND_ITEMS: {
                    launcher.bindItems(this, mShortcuts, msg.arg1, msg.arg2);
                    break;
                }
                case MESSAGE_BIND_DRAWER: {
                    launcher.bindDrawer(this, mDrawerAdapter);
                    break;
                }
                case MESSAGE_BIND_APPWIDGETS: {
                    launcher.bindAppWidgets(this, mAppWidgets);
                    break;
                }
            }
        }
    }
    /****************************************************************
     * ADW: Start custom functions/modifications
     ***************************************************************/

    private void updateAlmostNexusVars(){
//		allowDrawerAnimations=AlmostNexusSettingsHelper.getDrawerAnimated(Launcher.this);
//		newPreviews=AlmostNexusSettingsHelper.getNewPreviews(this);
//		mHomeBinding=AlmostNexusSettingsHelper.getHomeBinding(this);
//		mSwipedownAction=AlmostNexusSettingsHelper.getSwipeDownActions(this);
//		mSwipeupAction=AlmostNexusSettingsHelper.getSwipeUpActions(this);
//		hideStatusBar=AlmostNexusSettingsHelper.getHideStatusbar(this);
//		showDots=AlmostNexusSettingsHelper.getUIDots(this);
//		mDockStyle=AlmostNexusSettingsHelper.getmainDockStyle(this);
//		showDockBar=AlmostNexusSettingsHelper.getUIDockbar(this);
//		autoCloseDockbar=AlmostNexusSettingsHelper.getUICloseDockbar(this);
//		autoCloseFolder=AlmostNexusSettingsHelper.getUICloseFolder(this);
//		hideABBg=AlmostNexusSettingsHelper.getUIABBg(this);
//		uiHideLabels=AlmostNexusSettingsHelper.getUIHideLabels(this);
//		if(mWorkspace!=null){
//			mWorkspace.setSpeed(AlmostNexusSettingsHelper.getDesktopSpeed(this));
//			mWorkspace.setBounceAmount(AlmostNexusSettingsHelper.getDesktopBounce(this));
//			mWorkspace.setDefaultScreen(AlmostNexusSettingsHelper.getDefaultScreen(this));
//			mWorkspace.setWallpaperScroll(AlmostNexusSettingsHelper.getWallpaperScrolling(this));
//		}
//		int animationSpeed=AlmostNexusSettingsHelper.getZoomSpeed(this);
//        if(mAllAppsGrid!=null){
//        	mAllAppsGrid.setAnimationSpeed(animationSpeed);
//        }
//        wallpaperHack=AlmostNexusSettingsHelper.getWallpaperHack(this);
//        useDrawerCatalogNavigation=AlmostNexusSettingsHelper.getDrawerCatalogsNavigation(this);
    }
    /**
     * ADW: Refresh UI status variables and elements after changing settings.
     */
    private void updateAlmostNexusUI(){
//    	if(mIsEditMode || mIsWidgetEditMode)return;
//    	updateAlmostNexusVars();
//		float scale=AlmostNexusSettingsHelper.getuiScaleAB(this);
//		boolean tint=AlmostNexusSettingsHelper.getUIABTint(this);
//		int tintcolor=AlmostNexusSettingsHelper.getUIABTintColor(this);
//		if(scale!=uiScaleAB || tint!=uiABTint|| tintcolor!=uiABTintColor){
//			uiScaleAB=scale;
//			uiABTint=tint;
//			uiABTintColor=tintcolor;
//			mRAB.updateIcon();
//			mLAB.updateIcon();
//			mRAB2.updateIcon();
//			mLAB2.updateIcon();
//			mHandleView.updateIcon();
//		}
//		if(!showDockBar){
//			if(mDockBar.isOpen())mDockBar.close();
//		}
//    	fullScreen(hideStatusBar);
//    	if(!mDockBar.isOpen() && !showingPreviews){
//	    	if(!isAllAppsVisible()){
//	    		mNextView.setVisibility(showDots?View.VISIBLE:View.GONE);
//		    	mPreviousView.setVisibility(showDots?View.VISIBLE:View.GONE);
//	    	}
//    	}
//    	switch (mDockStyle) {
//        case DOCK_STYLE_1:
//            mRAB.setVisibility(View.GONE);
//            mLAB.setVisibility(View.GONE);
//            mRAB2.setVisibility(View.GONE);
//            mLAB2.setVisibility(View.GONE);
//            if(!mDockBar.isOpen() && !showingPreviews)mDrawerToolbar.setVisibility(View.VISIBLE);
//            break;
//        case DOCK_STYLE_3:
//            mRAB.setVisibility(View.VISIBLE);
//            mLAB.setVisibility(View.VISIBLE);
//            mRAB2.setVisibility(View.GONE);
//            mLAB2.setVisibility(View.GONE);
//            if(!mDockBar.isOpen() && !showingPreviews)mDrawerToolbar.setVisibility(View.VISIBLE);
//            break;
//        case DOCK_STYLE_5:
//            mRAB.setVisibility(View.VISIBLE);
//            mLAB.setVisibility(View.VISIBLE);
//            mRAB2.setVisibility(View.VISIBLE);
//            mLAB2.setVisibility(View.VISIBLE);
//            if(!mDockBar.isOpen() && !showingPreviews)mDrawerToolbar.setVisibility(View.VISIBLE);
//            break;
//        case DOCK_STYLE_NONE:
//            mDrawerToolbar.setVisibility(View.GONE);
//        default:
//            break;
//        }
//    	mHandleView.hideBg(hideABBg);
//    	mRAB.hideBg(hideABBg);
//    	mLAB.hideBg(hideABBg);
//    	mRAB2.hideBg(hideABBg);
//    	mLAB2.hideBg(hideABBg);
//    	if(mWorkspace!=null){
//    		mWorkspace.setWallpaperHack(wallpaperHack);
//    	}
//    	if(mDesktopIndicator!=null){
//    		mDesktopIndicator.setType(AlmostNexusSettingsHelper.getDesktopIndicatorType(this));
//    		mDesktopIndicator.setAutoHide(AlmostNexusSettingsHelper.getDesktopIndicatorAutohide(this));
//    		if(mWorkspace!=null){
//    			mDesktopIndicator.setItems(mWorkspace.getChildCount());
//    		}
//    		if(isAllAppsVisible()){
//    			if(mDesktopIndicator!=null)mDesktopIndicator.hide();
//    		}
//    	}

    }
    /**
     * ADW:Create a smaller copy of an icon for use inside Action Buttons
     * @param info
     * @return
     */
    Drawable createSmallActionButtonIcon(ItemInfo info){
        Drawable d = null;
        final Resources resources = getResources();
        if(info!=null){
            if(info instanceof ApplicationInfo){
                if (!((ApplicationInfo)info).filtered) {
                	((ApplicationInfo)info).icon = Utilities.createIconThumbnail(((ApplicationInfo)info).icon, this);
                	((ApplicationInfo)info).filtered = true;
                }
                d=((ApplicationInfo)info).icon;
            }else if(info instanceof LiveFolderInfo){
            	d=((LiveFolderInfo)info).icon;
                if (d == null) {
                	d = Utilities.createIconThumbnail(resources.getDrawable(R.drawable.ic_launcher_folder), this);
                	((LiveFolderInfo)info).filtered = true;
                }
            }else if(info instanceof UserFolderInfo){
        		d = resources.getDrawable(R.drawable.ic_launcher_folder);
            }
        }
        if (d == null) {
        	d = Utilities.createIconThumbnail(
            resources.getDrawable(R.drawable.ab_empty), this);
        }
        d=Utilities.drawReflection(d, this);

    	return d;
    }
    Drawable createSmallActionButtonDrawable(Drawable d){
        d=Utilities.drawReflection(d, this);
        return d;
    }
    //ADW: Previews Functions
    public void previousScreen(View v) {
	    mWorkspace.scrollLeft();
	}
    public void nextScreen(View v) {
	    mWorkspace.scrollRight();
	}
    protected boolean isPreviewing(){
    	return showingPreviews;
    }
    private void hideDesktop(boolean enable){
    	if(enable){
	    	mDrawerToolbar.setVisibility(View.GONE);
    	}else{
    		mDrawerToolbar.setVisibility(View.VISIBLE);
    	}
    }
    public void dismissPreviews(){
    	if(showingPreviews){
    		mScreenSelector.mPc(mWorkspace.getCurrentScreen());
    		Q = false;
    		hideDesktop(false);
    		mpc(false);
    		mDragLayer.addView(mCurtain);
    		mDragLayer.addView(mWorkspace);
    		mPk();
    		mDesktopIndicator.bringToFront();
    		showingPreviews = false;
            mWorkspace.openSense(false);
            mWorkspace.unlock();
            mWorkspace.mPc(true);
            mWorkspace.mPd(mWorkspace.getCurrentScreen());
            mWorkspace.invalidate();
    	}
    }

    public void mPk() {
    	mDeleteZone.bringToFront();
        mDrawerToolbar.bringToFront();
	}

	private void mpc(boolean b) {
		if (b) {
			mDesktopIndicator.setVisibility(View.GONE);
		} else {
			mDesktopIndicator.setVisibility(View.VISIBLE);
		}
		
	}

	public void showPreviews(final View anchor, int start, int end) {
        if(mWorkspace!=null && mWorkspace.getChildCount()>0 && !Q && mWorkspace.mPA()){
            showingPreviews=true;
            hideDesktop(true);
            mpc(true);
            mWorkspace.lock();
            mWorkspace.openSense(true);
            Q = true;
            mDragLayer.removeView(mWorkspace);
            mDragLayer.removeView(mCurtain);
            mWorkspace.mPc(false);
            mWorkspace.post(new Runnable() {

				@Override
				public void run() {
					mScreenSelector.mPb(mWorkspace.getCurrentScreen());					
				}
            	
            });
        }
    }
    /**
     * ADW: Override this to hide statusbar when necessary
     */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
    	super.onWindowFocusChanged(hasFocus);
		S = false;
	}

	/************************************************
	 * ADW: Functions to handle Apps Grid
	 */
    public void showAllApps(){
		if(!allAppsOpen && mAllAppsGrid!=null){
			if (Y) {
				mScreenSelector.setStereoView(false);
			}
			allAppsOpen = true;
            mWorkspace.lock();
            mDragLayer.removeView(mWorkspace);
            mDragLayer.removeView(mCurtain);
            mAllAppsGrid.open();
		}
    }

    private void closeAllApps(){
		if(allAppsOpen && mAllAppsGrid!=null){
			if (Y) {
                mScreenSelector.setStereoView(true);
                mWorkspace.mPb(true);
			}
            mHandleView.setNextFocusUpId(R.id.drag_layer);
            mHandleView.setNextFocusLeftId(R.id.drag_layer);
            mLAB.setNextFocusUpId(R.id.drag_layer);
            mLAB.setNextFocusLeftId(R.id.drag_layer);
            mRAB.setNextFocusUpId(R.id.drag_layer);
            mRAB.setNextFocusLeftId(R.id.drag_layer);
            mLAB2.setNextFocusUpId(R.id.drag_layer);
            mLAB2.setNextFocusLeftId(R.id.drag_layer);
            mRAB2.setNextFocusUpId(R.id.drag_layer);
            mRAB2.setNextFocusLeftId(R.id.drag_layer);
			allAppsOpen=false;
			mDragLayer.addView(mCurtain);
            mDragLayer.addView(mWorkspace);
	        mWorkspace.unlock();
            mLAB.setSpecialMode(false);
            mRAB.setSpecialMode(false);
            mAllAppsGrid.close();
            mPk();
            mDesktopIndicator.bringToFront();

		}
    }
    boolean isAllAppsVisible() {
    	return allAppsOpen;
    	/*if(mAllAppsGrid!=null)
    		return mAllAppsGrid.getVisibility()==View.VISIBLE;
    	else
    		return false;*/
    }
	private void appwidgetReadyBroadcast(int appWidgetId, ComponentName cname, int[] widgetSpan) {
		Intent motosize = new Intent("com.motorola.blur.home.ACTION_SET_WIDGET_SIZE");

		motosize.setComponent(cname);
		motosize.putExtra("appWidgetId", appWidgetId);
		motosize.putExtra("spanX", widgetSpan[0]);
		motosize.putExtra("spanY", widgetSpan[1]);
		motosize.putExtra("com.motorola.blur.home.EXTRA_NEW_WIDGET", true);
		sendBroadcast(motosize);

		Intent ready = new Intent(LauncherIntent.Action.ACTION_READY).putExtra(
				LauncherIntent.Extra.EXTRA_APPWIDGET_ID, appWidgetId).putExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId).putExtra(
				LauncherIntent.Extra.EXTRA_API_VERSION, LauncherMetadata.CurrentAPIVersion).
				setComponent(cname);
		sendBroadcast(ready);
	}
	/**
	 * ADW: Home binding actions
	 */
	public void fireHomeBinding(int bindingValue){
    	//ADW: switch home button binding user selection
		//mIsEditMode || mIsWidgetEditMode
		if(T)return;
        switch (bindingValue) {
		case BIND_DEFAULT:
			dismissPreviews();
			if (!mWorkspace.isDefaultScreenShowing() && mWorkspace.mPg()) {
				mWorkspace.moveToDefaultScreen0();
			}
			break;
		case BIND_HOME_PREVIEWS:
        	if (!mWorkspace.isDefaultScreenShowing()) {
        		dismissPreviews();
                mWorkspace.moveToDefaultScreen0();
            }else{
            	if(!showingPreviews){
            		showPreviews(mHandleView, 0, Workspace.mHomeScreens);
            	}else{
            		dismissPreviews();
            	}
            }
			break;
		case BIND_PREVIEWS:
        	if(!showingPreviews){
        		if(isAllAppsVisible())
        			closeDrawer();
        		showPreviews(mHandleView, 0, Workspace.mHomeScreens);
        	}else{
        		dismissPreviews();
        	}
			break;
		case BIND_APPS:
			dismissPreviews();
			if(isAllAppsVisible()){
				closeDrawer();
			}else{
				showAllApps();
			}
			break;
		case BIND_NOTIFICATIONS:
			dismissPreviews();
			showNotifications();
			break;
		case BIND_HOME_NOTIFICATIONS:
        	if (!mWorkspace.isDefaultScreenShowing()) {
        		dismissPreviews();
                mWorkspace.moveToDefaultScreen0();
            }else{
    			dismissPreviews();
    			showNotifications();
            }
			break;
		default:
			break;
		}
	}

	public DesktopIndicator getDesktopIndicator(){
		return mDesktopIndicator;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		//if(mMessWithPersistence)setPersistent(false);
		super.onStart();
		mWorkspace.mPB();
		//int currentOrientation=getResources().getConfiguration().orientation;
		//if(currentOrientation!=savedOrientation){
			//mShouldRestart=true;
		//}
	}

	@Override
	protected void onStop() {
		//if(!mShouldRestart){
			//savedOrientation=getResources().getConfiguration().orientation;
	    	//if(mMessWithPersistence)setPersistent(true);
		//}
		// TODO Auto-generated method stub
		super.onStop();
	}
	public Typeface getThemeFont(){
		return themeFont;
	}

	void editShirtcut(ApplicationInfo info) {
		Intent edit = new Intent(Intent.ACTION_EDIT);
		edit.setClass(this, CustomShirtcutActivity.class);
		edit.putExtra(CustomShirtcutActivity.EXTRA_APPLICATIONINFO, info.id);
		startActivityForResult(edit, REQUEST_EDIT_SHIRTCUT);
	}

	private void completeEditShirtcut(Intent data) {
		//TODO TODOTODO
		if (!data.hasExtra(CustomShirtcutActivity.EXTRA_APPLICATIONINFO))
			return;
		long appInfoId = data.getLongExtra(CustomShirtcutActivity.EXTRA_APPLICATIONINFO, 0);
		ApplicationInfo info = LauncherModel.loadApplicationInfoById(this, appInfoId);
		if (info != null) {
	        Drawable icon = null;
	        boolean customIcon = false;
	        ShortcutIconResource iconResource = null;
            info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			if (data.getBooleanExtra("origin_customed", false)) {
				if(CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(info.intent.getAction())) {
					final int id = LauncherActions.getIconResourceId(info.intent.getIntExtra(LauncherActions.DefaultLauncherAction.EXTRA_BINDINGVALUE, 0));
					icon = getResources().getDrawable(id);
				} else {
					try {
						icon = getPackageManager().getActivityIcon(info.intent);
						info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
					} catch (NameNotFoundException e){
						e.printStackTrace();
					}
				}
			} else {
				Bitmap bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
				if (bitmap != null) {
		            icon = new FastBitmapDrawable(Utilities.createBitmapThumbnail(bitmap, this));
		            customIcon = true;
				}
				
			}

	        if (icon == null) {
	            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
	            if (extra != null && extra instanceof ShortcutIconResource) {
	                try {
	                    iconResource = (ShortcutIconResource) extra;
	                    final PackageManager packageManager = getPackageManager();
	                    Resources resources = packageManager.getResourcesForApplication(
	                            iconResource.packageName);
	                    final int id = resources.getIdentifier(iconResource.resourceName, null, null);
	                    icon = resources.getDrawable(id);
	                } catch (Exception e) {
	                    w(LOG_TAG, "Could not load shortcut icon: " + extra);
	                }
	            }
	        }

	        if (icon != null) {
		        info.icon = icon;
		        info.customIcon = customIcon;
		        info.iconResource = iconResource;
	        }
			info.title = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
			info.intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
			LauncherModel.updateItemInDatabase(this, info);

			if (info.container == LauncherSettings.Favorites.CONTAINER_MAB)
				mHandleView.UpdateLaunchInfo(info);
			else if (info.container == LauncherSettings.Favorites.CONTAINER_LAB)
				mLAB.UpdateLaunchInfo(info);
			else if (info.container == LauncherSettings.Favorites.CONTAINER_LAB2)
				mLAB2.UpdateLaunchInfo(info);
			else if (info.container == LauncherSettings.Favorites.CONTAINER_RAB)
				mRAB.UpdateLaunchInfo(info);
			else if (info.container == LauncherSettings.Favorites.CONTAINER_RAB2)
				mRAB2.UpdateLaunchInfo(info);

			mWorkspace.updateShortcutFromApplicationInfo(info);
		}
	}

	private void updateCounters(View view, String packageName, int counter, int color){
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
                if(view instanceof CounterImageView)
                    ((CounterImageView) view).setCounter(counter, color);
                //else if
                view.invalidate();
                sModel.updateCounterDesktopItem(info, counter, color);
            }
        }
	}
    private void updateCountersForPackage(String packageName, int counter, int color) {
        if (packageName != null && packageName.length() > 0) {
            mWorkspace.updateCountersForPackage(packageName,counter, color);
            //ADW: Update ActionButtons icons
            updateCounters(mHandleView, packageName, counter, color);
            updateCounters(mLAB, packageName, counter, color);
            updateCounters(mRAB, packageName, counter,color);
            updateCounters(mLAB2, packageName, counter,color);
            updateCounters(mRAB2, packageName, counter,color);
            sModel.updateCounterForPackage(this,packageName,counter, color);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub
        final ComponentName name = intent.getComponent();
        if(name!=null)
            updateCountersForPackage(name.getPackageName(),0,0);
        try{
            super.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            e(LOG_TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }
    public void showActions(final ItemInfo info, final View view){
    	if (info instanceof LauncherAppWidgetInfo || info instanceof OtherInfo || info instanceof FolderInfo) {
    		return;
    	}
    	mPopupWindow = new QuickActionWindow(this);
    	view.setTag(R.id.TAG_PREVIEW, mPopupWindow);
    	mPopupWindow.mPa(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ApplicationInfo appInfo = (ApplicationInfo)info;
				Intent intent = new Intent(CustomShirtcutActivity.ACTION_ADW_PICK_ICON);
				intent.putExtra(CustomShirtcutActivity.EXTRA_APPLICATIONINFO, info.id);
				intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.title);
				Intent appIntent = appInfo.intent;
				if (appIntent != null) {
					intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, appIntent);
					if (CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(appIntent.getAction())) {
						int bindingValue = appIntent.getIntExtra(LauncherActions.DefaultLauncherAction.EXTRA_BINDINGVALUE, 0);
						intent.putExtra("launcher_action_icon_id", LauncherActions.getIconResourceId(bindingValue));
					}
					if (appInfo.icon instanceof BitmapDrawable) {
						intent.putExtra("ICON", ((BitmapDrawable)appInfo.icon).getBitmap());
						intent.putExtra("origin_customed", appInfo.customIcon);
					}
					startActivityForResult(intent, REQUEST_13);
					mr();
				}
			}    		
    	});
    	mPopupWindow.mPa(view);
    }
    @Override
    public void onSwipe() {
        //TODO: specify different action for each ActionButton?
    }
    public void setDockPadding(int pad){
        mDrawerToolbar.setPadding(0, 0,0,pad);
    }
    
	void ma(UserFolderInfo info) {
		if (Z != null) {
			final int count = Z.size();
			for (int i = 0; i < count; i++) {
				Folder folder = Z.get(i);
				if (info.equals(folder.mInfo)) {
					if(info.opened) {
						closeFolder(folder);
					}
				}
			}
		}
	}

	public boolean mPp() {
		if (mScreenSelector == null) {
			return false;
		}
		return mScreenSelector.mPa();
	}

	public boolean mPj() {
		return bR;
	}

	@Override
	public void mPc(int p) {
		mWorkspace.setCurrentScreen(p);
		dismissPreviews();
	}
}
