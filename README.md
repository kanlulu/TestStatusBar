

Android的状态栏由于**第三方厂商的自由发挥**和**国内设计偏向于iOS设计风格**的两大重要原因给Android开发者造成了巨大的困扰。

我们分别从以下三个问题入手来解决Android的状态栏的适配问题：

> 1. 状态栏变色
> 2. 沉浸式状态栏
> 3. 状态栏黑色图标设置

在正式来看这几个问题之前，我们先了解一下原生`Android`在状态栏上不同版本`SDK`的特性。主要有三个版本节点(`Android4.4.2、Android5.0和Android6.0`)需要注意：

|           Android版本            | 设置状态栏颜色 | 设置沉浸式状态栏 | 设置黑色图标 |
| :------------------------------: | :------------: | :--------------: | :----------: |
| `Android4.4.2 API19`以下（不含） |     false      |      false       |    false     |
|   `Android5.0 API21`以下(不含)   |     false      |       true       |    false     |
|   `Android6.0 API23`以下(不含)   |      true      |       true       |    false     |
|   `Android6.0 API23`及以上版本   |      true      |       true       |     true     |

看到这，你忽然觉得状态栏适配似乎没什么复杂的。但是你不要忽略了国内各家的第三方ROM和老板要求你强行适配其他原生不支持设置状态栏的低版本`Android`系统，当这个时候你就会发现事情没有那么简单了。

#### 一、设置状态栏颜色

**1.`Android5.0`及以上版本设置状态栏颜色**

在`Android5.0`以上我们可以直接使用`Window`中的方法`setStatusBarCorlor()`来设置颜色即可，当我们点开这个方法是我们发现还有一个问题需要注意，系统对此方法的注释是这样的：

```java
 /**
     * Sets the color of the status bar to {@code color}.
     *
     * For this to take effect,
     * the window must be drawing the system bar backgrounds with
     * {@link android.view.WindowManager.LayoutParams#FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS} and
     * {@link android.view.WindowManager.LayoutParams#FLAG_TRANSLUCENT_STATUS} must not be set.
     *
     * If {@code color} is not opaque, consider setting
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}.
     * <p>
     * The transitionName for the view background will be "android:status:background".
     * </p>
     */
    public abstract void setStatusBarColor(@ColorInt int color);

```

因此我们还需要给Window设置`FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS`和`FLAG_TRANSLUCENT_STATUS`这两个`flag`，设置状态栏的颜色才能生效。

代码如下：

```java
public class StatusBarUtils {
    public static void setStatusBarBackgroundColor(Activity activity, @ColorRes int colorResId) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        }
    }
}
```

**2.`Android4.4.2`版本设置状态栏颜色**

在`Android4.4.2`版本上系统没有提供方法给我们修改状态栏颜色，我们只能通过其他方法来修改状态栏的颜色。

我们可以先把状态栏设置成半透明，然后在`decorview`的顶部增加一个和状态栏等高的`View`通过改变这个`View`的颜色达到更改状态栏颜色的目的。

```java
public class StatusBarUtils {

    /**
     * 修改状态栏颜色
     * @param activity
     * @param colorResId
     */
    public static void setStatusBarBackgroundColor(Activity activity, @ColorRes int colorResId) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //开启半透明时 
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusBarView = new View(window.getContext());
            //状态栏高度
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            //我们可以设置普通的颜色也可以设置渐变色
            statusBarView.setBackgroundColor(activity.getResources().getColor(colorResId));
            decorViewGroup.addView(statusBarView);
        }
    }

    /**
     * 获取系统状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}

```

`Android4.4.2`系统下设置状态栏半透明时，会有一些需要注意的问题。

如果你使用了系统`titleBar`，当你设置了半透明状态栏之后发现我们的`layout`布局会占据状态栏的空间而`titleBar`的位置却没有改变。这个时候我们只要设置`android:fitsSystemWindows="true"`就可以了，它表示layout不占据状态栏的位置。

设置`android:fitsSystemWindows="true"`之前：

![unnormal_01](C:\Users\ASUS\Desktop\unnormal_01.png)

设置`android:fitsSystemWindows="true"`之后：

![normal_01](C:\Users\ASUS\Desktop\normal_01.png)

如果你不使用系统的`titleBar`(设置`<item name="windowNoTitle">true</item>`)就不要设置这个属性，如果你设置了也会有问题。

![unnormal](C:\Users\ASUS\Desktop\unnormal.png)

这些问题在开发中都是需要注意的。

***

#### 二、设置沉浸式状态栏

沉浸式状态栏只要系统版本大于等于`Android4.4.2 API19`就可以实现。

**通过style中设置沉浸式状态栏**

```xml
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="windowNoTitle">true</item>
    </style>
    
    <style name="TranslucentTheme" parent="AppTheme"/>

    <!--v19-->
    <style name="TranslucentTheme" parent="AppTheme">
        <item name="android:windowTranslucentStatus">true</item>
    </style>

    <!--v21-->
    <style name="TranslucentTheme" parent="AppTheme">
        <item name="android:windowTranslucentStatus">true</item>
        <!--Android 5.x开始需要把颜色设置透明，否则导航栏会有一层半透明的蒙层-->
        <item name="android:statusBarColor">@android:color/transparent</item>
    </style>
```

我们需要在不同版本的`values`文件夹下分别建立3个`style`，以适配不同版本的系统。

![chenj001](C:\Users\ASUS\Desktop\chenj001.jpg)

现在我们将整个布局都占据到了状态栏的空间，如果我们要把状态栏的位置留出来应该怎么办？

我们有三种方案可供选择：

1. 在布局的最外层设置`android:fitsSystemWindows="true"`。

   - 在布局文件中设置

     ```xml
     <android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
         xmlns:app="http://schemas.android.com/apk/res-auto"
         xmlns:tools="http://schemas.android.com/tools"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         android:fitsSystemWindows="true"
         tools:context=".SecondActivity">
     
     ......
     
     </android.support.constraint.ConstraintLayout>
     ```

   - 在代码中设置

     ```java
     /**
          * 设置页面最外层布局 FitsSystemWindows 属性
          * @param activity
          * @param value
          */
         public static void setFitsSystemWindows(Activity activity, boolean value) {
             ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
             View parentView = contentFrameLayout.getChildAt(0);
             if (parentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 parentView.setFitsSystemWindows(value);
             }
         }
     ```

2. 设置一个和状态栏等高的View占据状态栏的位置

   ```java
   /**
    * 添加状态栏占位视图
    *
    * @param activity
    */
   private void addStatusViewWithColor(Activity activity, int color) {
       ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);    
       View statusBarView = new View(activity);
       ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
               getStatusBarHeight(activity));
       statusBarView.setBackgroundColor(color);
       contentView.addView(statusBarView, lp);
   }
   ```

   看到这里你发现，这种需求用不到沉浸式状态栏的设置o(╥﹏╥)o。呃...，不管了你会用就好。

3. 设置状态栏高度的`paddingTop`

   ```java
       /**
        * 设置状态栏颜色 way2
        * 类似于setStatusBarBackgroundColor
        * 要先设置透明状态栏
        *
        * 直接设置setStatusBarColor()不能达到效果
        * @param activity
        * @param colorResId
        */
       public static void setStatusBarPaddingTop(Activity activity, @ColorRes int colorResId) {
           //设置 paddingTop
           Window window = activity.getWindow();
           ViewGroup rootView = (ViewGroup) window.getDecorView().findViewById(android.R.id.content);
           rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);
           //根布局添加占位状态栏
           ViewGroup decorView = (ViewGroup) window.getDecorView();
           View statusBarView = new View(activity);
           ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                   getStatusBarHeight(activity));
           statusBarView.setBackgroundColor(activity.getResources().getColor(colorResId));
           decorView.addView(statusBarView, lp);
       }
   ```

***

#### 三、设置状态栏黑色图标

`Android6.0 API23`及以上，系统提供了方法为我们设置黑色图标。

```java
    /**
     * For this to take effect,
     * the window must request FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
     * but not FLAG_TRANSLUCENT_STATUS.(不能使用透明状态栏)
     * Android 原生
     *
     * @param activity
     * @param bDark
     */
    private static void setDarkStatusIcon(Activity activity, boolean bDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
```

***需要注意的是，它不能和透明状态栏共存。***

`Android6.0`以下的系统无法直接设置状态栏的黑色图标，但是`MIUI`和`Flyme`提供了设置的方法。（`MIUI`在后来更新的版本中废弃了自己原先的方案，采用了Android原生的方案）。

```java
    /**
     * MUUI StatusBar text color
     *
     * @param activity
     * @param darkmode
     * @return
     */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);

            setDarkStatusIcon(activity, darkmode);
            return true;
        } catch (Exception e) {
            LogUtils.d("debug",e.getMessage());
        }
        return false;
    }

    /**
     * Flyme StatusBar text color
     *
     * @param activity
     * @param dark
     * @return
     */
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
                LogUtils.d("debug",e.getMessage());
            }
        }
        return result;
    }
```

***

***通过`colorPrimaryDark`设置状态栏颜色在`Android5.0`以下也是无效的。***

