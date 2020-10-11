/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.util.ArrayList;
import java.util.List;

public class PayloadChannelManager {
    

    class Info {

        private boolean notify;

        private List<IPayloadMessageReader> readerList;

        public Info() {
            this.notify = false;

            this.readerList = new ArrayList<>();
        }
        
        public boolean shouldNotify() {
            return notify;
        }

    }

}
