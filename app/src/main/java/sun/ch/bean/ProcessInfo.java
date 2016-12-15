package sun.ch.bean;

import android.graphics.drawable.Drawable;

/**
 * 进程信息
 */
public class ProcessInfo {
    private Drawable icon;
    private String processName;
    private long processSize;
    private boolean isSystem;

    public Drawable getIcon() {
        return icon;
    }

    public String getProcessName() {
        return processName;
    }

    public long getProcessSize() {
        return processSize;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setProcessSize(long processSize) {
        this.processSize = processSize;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
