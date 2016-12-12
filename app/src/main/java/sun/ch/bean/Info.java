package sun.ch.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/12/12.
 */
public class Info {
    private String appName;
    private long appSize;
    private Drawable appIcon;
    private boolean isSDCard;
    private boolean isSystemApp;
    private String packAgeName;

    public Info(String appName, long appSize, Drawable appIcon, boolean isSDCard, boolean isSystemApp) {
        this.appName = appName;
        this.appSize = appSize;
        this.appIcon = appIcon;
        this.isSDCard = isSDCard;
        this.isSystemApp = isSystemApp;
    }

    public Info() {
    }

    public String getAppName() {
        return appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getPackAgeName() {
        return packAgeName;
    }

    public void setPackAgeName(String packAgeName) {
        this.packAgeName = packAgeName;
    }

    public boolean isSDCard() {
        return isSDCard;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setSDCard(boolean SDCard) {
        isSDCard = SDCard;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    @Override
    public String toString() {
        return "Info{" +
                "appName='" + appName + '\'' +
                ", appSize=" + appSize +
                ", appIcon=" + appIcon +
                ", isSDCard=" + isSDCard +
                ", isSystemApp=" + isSystemApp +
                ", packAgeName='" + packAgeName + '\'' +
                '}';
    }
}
