package smartin.pedestal.dataReader;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

public class SwordJson {

    public String id;
    public TranslationObject pedestal;
    public TranslationObject hanger;
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public class TranslationObject {
        @Nullable
        public Double[] rotation = new Double[]{0d,0d,0d};
        public Double[] translation = new Double[]{1d,1d,1d};
        public Double[] scale = new Double[]{1d,1d,1d};
        public String toString(){
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
