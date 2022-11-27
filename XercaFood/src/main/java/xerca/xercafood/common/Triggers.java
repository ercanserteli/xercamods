package xerca.xercafood.common;

public class Triggers {
    public static final CustomTrigger ASSASSINATE = new CustomTrigger("assassinate");
    public static final CustomTrigger QUAKE = new CustomTrigger("quake");
    public static final CustomTrigger BEHEAD = new CustomTrigger("behead");

    public static final ConfigTrigger CONFIG_CHECK = new ConfigTrigger();

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
            ASSASSINATE, QUAKE, BEHEAD
    };
}