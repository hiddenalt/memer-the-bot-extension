package ru.hiddenalt.mtbe.settings;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsEntityAdapter implements JsonSerializer<SettingsEntity>, JsonDeserializer<SettingsEntity>
{
    @Override
    public JsonElement serialize(SettingsEntity src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();

        result.addProperty("mount",             src.getMount());
        result.addProperty("schematicAlign",    src.getSchematicDimension().toString());


        JsonObject colormap = new JsonObject();
        for(Map.Entry<Color, String> instance : src.getColormap().entrySet()) {
            Color color = instance.getKey();
            colormap.add(
                color.getRed()+","+color.getGreen()+","+color.getBlue(),
                new JsonPrimitive(instance.getValue())
            );
        }
        result.add("colormap", colormap);

        // TODO: bankIds serialize
        result.add("bankIds", new JsonObject());

        return result;
    }

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public SettingsEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SettingsEntity result = new SettingsEntity();
        JsonObject jsonObject = json.getAsJsonObject();

        try{
            if (jsonObject.has("mount"))
                result.setMount(jsonObject.get("mount").getAsString());

            if (jsonObject.has("schematicAlign"))
                result.setSchematicDimension(SchematicDimension.valueOf(jsonObject.get("schematicAlign").getAsString()));

            if(jsonObject.has("colormap")){
                HashMap<Color, String> colormap = new HashMap<>();
                for(Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("colormap").entrySet()) {
                    String[] rgb = entry.getKey().split(",");
                    int r = Integer.parseInt(rgb[0]);
                    int g = Integer.parseInt(rgb[1]);
                    int b = Integer.parseInt(rgb[2]);

                    Color color = new Color(r,g,b);
                    String blockId = entry.getValue().getAsString();

                    colormap.put(color, blockId);
                }
                result.setColormap(colormap);
            }

            // TODO: bankIds deserialize

        } catch (NullPointerException | IllegalArgumentException ignored){
            return null;
        }

        return result;
    }
}