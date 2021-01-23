package xerca.xercamod.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ConfigPredicate {
    private final Supplier<Boolean> condition;
    private final String configName;

    public ConfigPredicate(Supplier<Boolean> supplier, String name) {
        this.condition = supplier;
        this.configName = name;
    }

    public boolean test() {
        if(condition == null){
            return false;
        }
        return condition.get();
    }

    public static ConfigPredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            String configName = element.getAsJsonObject().get("config").getAsString();
            if(Config.conditionMap.containsKey(configName)){
                return new ConfigPredicate(Config.conditionMap.get(configName), configName);
            }
        }
        return null;
    }

    public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            if (this.condition != null) {
                jsonobject.addProperty("config", configName);
            }
            return jsonobject;
    }
}
