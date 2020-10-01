package sh.pancake.mod;

public interface IClassModder {

    boolean isInited();
    void initModder();

    byte[] transformClassData(String name, byte[] data);

}