/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.console;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.ServerPhase;

public class ServerConsole extends SimpleTerminalConsole {

    private final PancakeServerService service;

    public ServerConsole(PancakeServerService service) {
        this.service = service;
    }

    @Override
    protected boolean isRunning() {
        return service.getPhase().getIndex() > ServerPhase.NOT_STARTED.getIndex() && service.getPhase().getIndex() < ServerPhase.FINISHED.getIndex();
    }

    @Override
    protected void runCommand(String command) {
        DedicatedServer server = service.getDedicatedServer();

        // TEMP
        server.handleConsoleInput(command, server.createCommandSourceStack());
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder.appName("PancakeServer"));
    }

    @Override
    protected void shutdown() {
        service.stopServer();
    }

}
