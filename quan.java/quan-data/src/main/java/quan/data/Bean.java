package quan.data;

import quan.data.mongo.DataJsonWriter;

public abstract class Bean extends Node {

    public String toJson() {
        try (DataJsonWriter dataJsonWriter = new DataJsonWriter(this)) {
            return dataJsonWriter.toJson();
        }
    }

}
