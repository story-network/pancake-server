package sh.pancake.server;

public enum ServerPhase {

    NOT_STARTED(-1),
    PREPARING(0),
    PRE_INIT(1),

    INIT(2),
    POST_INIT(3),
    STARTED(4),

    STOPPING(999998),
    FINISHED(999999);

    private int index;

    ServerPhase(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
}
