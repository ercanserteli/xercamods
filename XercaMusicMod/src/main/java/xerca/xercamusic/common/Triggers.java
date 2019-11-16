package xerca.xercamusic.common;

public class Triggers {
    public static final CustomTrigger BECOME_MUSICIAN = new CustomTrigger("become_musician");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
            BECOME_MUSICIAN
    };
}