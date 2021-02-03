package ru.hiddenalt.mtbe.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SettingsManager {

    protected static SettingsEntity loadedSettings;

    public static void reloadSettings()  {
        try {
            File file = new File(getSettingsFile());
            String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(SettingsEntity.class, new SettingsEntityAdapter())
                    .create();

            SettingsEntity entity = gson.fromJson(data, SettingsEntity.class);

            if(entity == null) throw new Exception("Settings file has invalid format");
            loadedSettings = entity;
        } catch (Exception e) {
            System.out.println("Cannot parse the settings file (resetting to defaults): " + e.getLocalizedMessage());
            resetToDefaults();
        }
    }

    public static void resetToDefaults(){
        loadedSettings = new SettingsEntity();
        resetScalars();
        resetColorMap();
        save();
    }

    public static void resetScalars(){
        if(loadedSettings == null) return;

        loadedSettings.mount = "http://localhost:8000/";
        //...
    }

    public static void resetColorMap(){
        if(loadedSettings == null) return;

        HashMap<Color, String> colormap = new HashMap<>();
        // TODO: default blocks map

//        for(int i = 1; i <= 200; i++){
//        Registry.BLOCK.getIds().forEach(identifier -> {
//            int t = (int) Math.round(Math.random() * 100);
//            colormap.put(new Color(t,t,t), identifier.getPath());
//        });

//        }


        loadedSettings.colormap = colormap;
    }

    public static void save() {
        try{
            File file = new File(getSettingsFile());
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(SettingsEntity.class, new SettingsEntityAdapter())
                    .create();
            String json = gson.toJson(loadedSettings);
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Cannot save settings to file: "+e.getLocalizedMessage());
        }
    }

    public static String getSettingsFile(){
        return "settings.mtbe.json";
    }

    public static SettingsEntity getSettings(){
        return loadedSettings;
    }

    public static void setSettings(SettingsEntity settingsEntity){
        SettingsManager.loadedSettings = settingsEntity;
    }

}
