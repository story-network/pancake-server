/*
 * Created on Sat Oct 17 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import sh.pancake.server.IPancakeExtra;
import sh.pancake.server.util.HolderBasedMap;

public class PayloadHandlerMap extends HolderBasedMap<IPancakeExtra, String, PayloadHandlerMap.Info> {

    public Info getInfoFor(IPancakeExtra extra, ResourceLocation location) {
        return super.computeIfAbsentOf(extra, location.toString(), (key) -> new Info());
    }

    public List<Info> getAllInfoList(ResourceLocation location) {
        String key = location.toString();

        return super.valuesMap().stream()
            .filter((map) -> map.containsKey(key))
            .map((map) -> map.get(key))
            .collect(Collectors.toList());
    }
    
    class Info {

        private List<IPayloadMessageReader> readerList;

        public Info() {
            this.readerList = new ArrayList<>();
        }

        public void addReader(IPayloadMessageReader reader) {
            readerList.add(reader);
        }

        public boolean removeReader(IPayloadMessageReader reader) {
            return readerList.remove(reader);
        }

        public void clear() {
            readerList.clear();
        }

        public Iterator<IPayloadMessageReader> iterator() {
            return readerList.iterator();
        }

        public void forEach(Consumer<IPayloadMessageReader> func) {
            readerList.forEach(func);
        }

    }

}
