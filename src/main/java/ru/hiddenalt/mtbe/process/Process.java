package ru.hiddenalt.mtbe.process;

public abstract class Process {

    protected int pid = Process.ABSTRACT_PAID;
    public static int ABSTRACT_PAID = -1;

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    protected boolean isActive = false;
    protected boolean isPaused = false;

    abstract public void start();
    abstract public void cancel();
    abstract public void pause();
    abstract public void resume();

    abstract public String getType();
    abstract public String getName();


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
