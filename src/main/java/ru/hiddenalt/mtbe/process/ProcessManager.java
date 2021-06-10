package ru.hiddenalt.mtbe.process;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ProcessManager {

    private static ArrayList<Process> processes = new ArrayList<>();

    public static int push(Process p){
        processes.add(p);
        int index = processes.indexOf(p);
        p.setPid(index);
        return index;
    }

    public static void push(Process ...p){
        processes.addAll(Arrays.asList(p));
    }

    public static int pushAndStart(Process p){
        int i = push(p);
        p.start();
        return i;
    }

    public static void pushAndStart(Process ...p){
        push(p);
        for (Process process: p)
            process.start();
    }

    public static void remove(Process p){
        processes.remove(p);
    }

    public static ArrayList<Process> getAll(){
        return processes;
    }

    public static void removeAll(){
        ArrayList<Process> _processes = new ArrayList<>(processes);

        for(Process p : _processes) {
            p.cancel();
            processes.remove(p);
        }
    }

    public static void startAll(){
        for(Process p : processes)
            p.start();
    }


    public static void cancelAll(){
        ArrayList<Process> _processes = new ArrayList<>(processes);

        for(Process p : _processes)
            p.cancel();

    }

    public static void pauseAll(){
        for(Process p : processes)
            p.pause();
    }

    public static void resumeAll(){
        for(Process p : processes)
            p.resume();
    }

}
