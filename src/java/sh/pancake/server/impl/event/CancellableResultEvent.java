package sh.pancake.server.impl.event;

import javax.annotation.Nullable;

public class CancellableResultEvent<T> implements Cancellable {
    
    private T cancelledResult = null;

    @Nullable
    public T getCancelledResult() {
        return cancelledResult;
    }

    public void cancel(T cancelledResult) {
        this.cancelledResult = cancelledResult;
    }

    @Override
    public boolean isCancelled() {
        return cancelledResult != null;
    }

}
