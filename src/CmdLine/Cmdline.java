package CmdLine;

import java.util.HashMap;

public class Cmdline {

    public String name;
    public int threadNum;
    public int volumeSize;
    public boolean sfx;
    public boolean shared;
    public boolean delete;
    public String password;
    public boolean encrypt;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    // key - cpu threads, value - index
    int getThreadNumKey(int threadNum) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(2, 0);
        map.put(4, 1);
        map.put(6, 2);
        map.put(8, 3);
        map.put(10, 4);
        map.put(12, 5);
        map.put(14, 6);
        map.put(16, 7);
        return map.get(threadNum);
    }

    int getDictionarySize(Level level) {
        HashMap<Level, Integer> map = new HashMap<>();
        map.put(Level.fast, 1);
        map.put(Level.normal, 16);
        map.put(Level.maximum, 32);
        return map.get(level);
    }

    int getThreadNum() {
        return threadNum;
    }

    void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    Cmdline() {
    }

    public static void main(String[] args) {
        Win w = new Win();
        w.construct();
    }
}
