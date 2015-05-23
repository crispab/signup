package util;

public class Conversion {
    public static long asLong(scala.Option<Object> optionLong) {
        return Long.parseLong(optionLong.get().toString());
    }

}
