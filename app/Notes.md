
Notes from https://class.coursera.org/android-001/lecture

# Activities

Launch from home screen
Task Backstack (for back button)

Lifecycle events:
 * onCreate: set up initial state; set activity content view, retain refs to UI components, configure views.
 * onStart: about to become visible; start visible-only behaviors, load persistent state.
 * onResume: about to start interacting; start foreground-only behaviors
 * -- working --
 * onPause: about to switch focus; shutdown fg-only behaviors, save persistent state.
 * onStop: no longer visible, but may be restarted; cache state. (may not be called if killed)
 * onDestroy: about to be destroyed; release activity resources.

Starting Activities:
 * startActivity(Intent)
 * startActivityForResult(callback)
  -> it calls setResult(resultCode, data)

Configuration changes:
 * By default, activities are killed and restarted.
 * Hard-to-compute data can be retained (deprecated in favor of Fragments):
  -> override onRetainNonConfigurationInstance(), it'll be called between onStop and onDestroy
  -> get retained data: getLastNonConfigurationInstance()
 * Handle Manually:
  -> androidmanifest.xml: <activity android:configChanges="orientation | screensize | keyboardHidden...">
  -> override onConfigurationChanged(newConfig) method.

# Fragment class

Behavior/Portion of UI within an Activity.

Lifecycle:
 * onAttach() - first attached to activity.
 * onCreate() - initialize
 * onCreateView() - set up & return its user interface.
 * onActivityCreated() - after onCreate() of Activity
 * onStart() - about to become visible
 * onResume() - about to become focused
 * -- working --
 * onPause() - lost focus
 * onStop() - no longer visible
 * onDestroyView() - view is detached from activity (clean up view resources)
 * onDestroy() - fragment is no longer in use.
 * onDetach() - detached (null out references to hosting activity)
 
Adding: either statically, or through FragmentManager (with Fragment Transaction)
Handling configuration changes:
 -> setRetainInstance(true) -> fragment object not destroyed.
 -> When Config Change: onPause -> onStop -> onDestroyView -> onDetach -> onAttach -> onCreateView -> onActivityCreated -> onStart -> onResume
 -> onDestroy() and onCreate() are NOT called. View is recreated (onCreateView, onActivityCreated called)
 -> See http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html


# UI Classes

View: base class, rectangular. 
 -> Methods: Set visibility, checked state, listeners, opacity, background, rotation, manage focus.
 -> Examples: Button, ToggleButton, Checkbox, RatingBar, AutocompleteTextView.
 -> Events: onClick, onLongClick, onFocusChanged, onKeyUp, onKeyDown, onWindowVisibilityChanged, etc.
 -> Drawing events: onMeasure, onLayout, onDraw.
 
ViewGroup: Invisible view that contains other views; base class for view containers & layouts
 -> Examples: RadioGroup, TimePicker, WebView, MapView, Gallery, Spinner
 
AdapterViews: children are managed by Adapter; Adapters: manage data, provides data Views.
 * ListView/ListAdapter/ListActivity: scrollable list of selectable items, can filter.
 * Spinner/SpinnerAdapter: scrollable list of items, user can select one (like a Combo Box).
 * Gallery/SpinnerAdapter: horizontally scrollable list (deprecated).
 * ViewPager/PagerAdapter: horizontally scrollable, compatibility.

ArrayAdapter.createFromResource()
new ArrayAdapter<String>(this, R.layout.list_item, getResources().getStringArray(R.array.colors))

Layouts: generic ViewGroup that defines a structure for the views it contains.
 * LinearLayout: views arranged in single horizontal/vertical row.
 * RelativeLayout: positioned relative to each other and to parent view.
 * TableLayout: rows & columns
 * GridView/ImageAdapter: two-dimensional, scrollable grid (like photo gallery).
   
Menus:
 * Types: Options: on menu button, Context: touch-n-hold, Submenu: when user selects menu item.
 * Create: res/menu/name.xml; inflate in onCreate<Type>Menu
 * Handle: on<Type>ItemsSelected()
 * Supported: Grouping, Shortcuts, Intents.

ActionBar (Android >= 3.0).
 * Activities, Fragments can add items; items can be hidden.
 * menu.xml: <item android:showAsAction="ifRoom|withText"> or never/overflow area.
 * Fragment: setHasOptions(true) in onCreate()
 * Can represent tabs (below menu):
  -> Each tab = one fragment. Only one visible at a time.
  -> tabBar = getActionBar(); tabBar.setNavigationMode(NAVIGATION_MODE_TABS)
  -> tabBar.addTab(tabBar.newTab().setText(str).setTabListener(new TabListener(fragment)));
  -> TabListener - custom class with onTabSelected(tab, fragmenttransaction), onTabUnselected, onTabReselected.

Dialogs:
 * new AlertDialogFragment().show(getFragmentManager(), "Alert")
  -> overridden onCreateDialog of DialogFragment.
    -> return new AlertDialog.Builder(getActivity()).setMessage("")...create()
 * ProgressDialog:
  -> same as AlertDialog, but use new ProgressDialog()

# User Notifications

Toasts: transitory messages.
 -> Toast.makeText(context, text, duration).show()
 -> Can set custom view: .setView

NotificationManager.

# BroadcastReceiver

Receive events (Intents) from the system.
Register:
 * Androidmanifest.xml: <receiver> <intent-filter>
 * registerReceiver()
 
Send events:
 * Normal vs Ordered, Sticky vs Non-sticky, Permissions.
 * sendOrderedBroadcast, sendStickyBroadcast

Delivery:
 * onReceive(). Should be short-lived. Can't start async operations (dialogs, startActivityForResult(), etc).

Debug:
 * Log extra intent resolution information: Intent.setFlag(FLAG_DEBUG_LOG_RESOLUTION);
 * adb shell dumpsys package


# Threads

Basic:
new Thread -> view.post(Runnable) or Activity.runOnUiThread(Runnable)

AsyncTask:
just implement class and execute() it. It'll post to UI thread.

Handler:
create new Handler() in the thread you want to post, then either post(Runnable) or sendMessage(Message).
 
# Alarms
Send intent at some time in the future.
getSystemService(ALARM_SERVICE).set(type, triggerAtTime, operation)
Time: absolute (RTC), relative to device boot time (ELAPSED).
Wake up behavior: wake (WAKEUP) or wait for waking.
PendingIntent getActivity, getBroadcast, getService
 
# Networking
 
 * Socket (low level)
 * HttpURLConnection: (HttpURLConnection) new URL("http://..").openConnection() -> .getInputStream() -> BufferedInputStream -> InputStreamReader -> BufferedReader -> .readLine()
 * AndroidHttpClient (from Apache): .newInstance("").execute(HttpGet(URL), new BasicResponseHandler()).close();
 * Internet recommends http://hgoebl.github.io/DavidWebb/
  
JSON: use specialized ResponseHandler: JSONResponseHandler, override handleResponse
-> str = new BasicResponseHandler().handleResponse(response)
-> JSONTokener(str) -> List<String> result
-> UI: setListAdapter(new ArrayAdapter<String>(MyActivity.this, R.layout.list_item, result));

XML: XmlPullParserFactory.newInstance().newPullParser().setInput(new InputStreamReader(response.getEntity().getContent()))
 -> getEventType(); getName(); .next();
 -> parseStartTag; parseEndTag; parseText.

See also:
 * http://www.mdswanson.com/blog/2014/04/07/durable-android-rest-clients.html
 * http://birbit.com/a-recipe-for-writing-responsive-rest-clients-on-android/
 * http://square.github.io/retrofit/
 * https://github.com/stephanenicolas/robospice

 
# Graphics & Animation
 
ImageView -> Simple graphics, no update
Canvas -> Complex graphics, updates/animation.

## ImageView

 * ShapeDrawable(new PathShape, OvalShape, RectShape)
 * BitmapDrawable: ImageDrawable 
 
## Canvas

 * View (infreq updates): subclass View, draw in onDraw() method to the canvas provided.
   * view.postInvalidate() -> redraw from other thread.
 * SurfaceView (freq. updates): subclass, secondary thread -> provide its own canvas.
   * also implement SurfaceHolder.Callback (lifecycle methods for SurfaceView)
   * Setup: getHolder()-> addCallback(): surfaceCreated() surfaceChanged() surfaceDestroyed()
   * Draw: holder.lockCanvas() -> draw -> unlockCanvasAndPost()

 * Canvas/Bitmap .drawText, .drawRect, .drawBitmap, .drawColor etc.
 * Paint class: style params

## View Animation

 * TransitionDrawable: 2-layer drawable, fade.
   -> trans = getResources().getDrawable(transition); trans.setCrossFadeEnabled(true); 
   -> (ImageView)findViewById().setImageDrawable(transition); trans.startTransition(5000);
 * AnimationDrawable: series of drawable for times. frame-by-frame.
   -> (ImageView).setBackgroundResource(R.drawable.view_animation); mAnim = imageView.getBackground()
   -> onWindowFocusChanged(hasFocus) { if (hasFocus) mAnim.start() }
 * Animation: tween view properties with tween functions.
   -> mAnim = AnimationUtils.loadAnimation(this, R.anim.view_animation)
   -> onWindowFocusChanged -> mImageView.startAnimation(mAnim);
 * ValueAnimator (general property animation), TimeInterpolator, AnimatorUpdateListener, TypeEvaluator, AnimatorSet.
   -> anim = ValueAnimator.ofObject(new SomeEvaluator(), from, to)
   -> anim.addUpdateListener( onAnimationUpdate(anim) { use(anim.getAnimatedValue() } )
   -> anim.setDuration(10000).start();
 * Property Animation system
   -> mImageView.animate().setDuration(3000).setInterpolator(new LinearInterpolator()).alpha(1.0f).withEndAction(runnable);

# Touch & Gestures

onTouchEvent(MotionEvent) -> true
Multitouch: ACTION_DOWN -> MOVE -> POINTER_DOWN -> MOVE -> POINTER_UP -> UP
GestureDetector: recognizes common gestures. Can use custom ones.

# Multimedia

AudioManager = Context.getSystemService(Context.AUDIO_SERVICE), SoundPool
MediaPlayer, MediaRecorder, Camera.

# Sensors

SensorManager = Context.getSystemService(Context.SENSOR_SERVICE)
Sensor = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
SensorEventListener
  onAccuracyChanged(sensor, int accuracy)
  onSensorChanged(SensorEvent ev)
sensorManager.registerListener(sensorEventListener, sensor, int rate);

Coordinate system: X: -> Y: ^ Z: to the user.

Lowpass, highpass filters.

# Data management
 
 * SharedPreferences (small amt. privimitive data)
  -> Persistent map. Automatically persisted across sessions. Usernames, preferences.
  -> prefs = Activity.getPreferences(MODE_PRIVATE) or Context.getSharedPreferences(name, mode) - not specific to activity.
    -> editor = prefs.edit(); editor.putInt, putString; then editor.commit()
    -> prefs.getBoolean, .getAll, .getString
  -> PreferenceFragment: UI for changing prefs. 
    -> res/xml/user_prefs.xml: <PreferenceScreen><EditTextPreference android:key, title, dialogMessage, dialogTitle, etc.>
    -> addPreferencesFromResource(R.xml.user_prefs); getPreferenceManager().findPreference(USERNAME); onSharedPreferenceChanged()
    
 * Internal Storage (medium private, temp)
  -> File API: openFileOutput(name, MODE_PRIVATE); openFileInput(name); getFileStreamPath(name).exists(), etc.
    -> pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(openFileOutput(..))); pw.println(); pw.close()
    -> br = new BufferedReader(new InputStreamReader(openFileInput(..))); br.readLine(); br.close()
  -> Cache directory: File Context.getCacheDir(): abs path.
  
 * External Storage (larger public data)
  -> Determine state: Environment.getExternalStorageState() == MEDIA_MOUNTED; MEDIA_MOUNTED_READ_ONLY; MEDIA_REMOVED;
  -> Permission: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  -> file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename); .exists() or .getAbsolutePath()
  -> new FileOutputStream(file); 
  -> Raw files from resources: getResources().openRawResource(R.raw.id) (they are copied).
  -> Cache directory: File Context.getExternalCacheDir().
  
 * SQLite (structured data)
  -> subclass SQLiteOpenHelper, 
    -> pass params to super(name, version); save context.
    -> override onCreate: db.execSQL(CREATE TABLE command)
    -> method deleteDatabase using context.
  -> db = helper.getWritableDatabase()
  -> db.insert(tablename, null, ContentValues); delete; update.
  -> db.query(tablename, helper.columns, ...) -> Cursor -> SimpleCursorAdapter
  -> Stored in /data/data/<package name>/databases/<db name> ; examine with adb -s emulator-5554 shell, then sqlite3 <path>

# ContentProviders
Database-like API for inter-app interchange. Includes change notifications.
URI: "content://com.android.contacts/contacts/"

# Services
No UI; Performing background execution; Support remote method execution.
Started by intent: Context.startService(Intent); By default run in the main thread of hosting app.
Bind to service: Context.bindService(Intent, ServiceConnection, flags): Send/receive msgs. Started on demand, shut down when no clients.

Messenger: manages Handler, can be sent from another app & process.
AIDL interface if service must be accessed concurrently.



# TODO
https://class.coursera.org/posa-002/lecture

http://www.reddit.com/r/androiddev/comments/1letwn/what_are_some_android_best_coding_practices/