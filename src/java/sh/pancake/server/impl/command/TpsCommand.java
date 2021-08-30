/*
 * Created on Sun Aug 29 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.command;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;

public class TpsCommand {
    
    public static void register(PancakeCommandDispatcher<PancakeCommandStack> dispatcher) {
        dispatcher.register(
            PancakeCommandStack.literal("tps")
            .requires((ctx) -> ctx.hasPermission(4))
            .executes((ctx) -> {
                CommandSourceStack source = ctx.getSource().getInnerStack();
                source.sendSuccess(getTpsText(source.getServer()), false);

                return 1;
            })
        );
    }

    private static Component getTpsText(MinecraftServer server) {
        TextComponent component = new TextComponent("");

        synchronized (server.tickTimes) {
            TickInfo totalInfo = elsapedInfo(server.tickTimes, MinecraftServer.MS_PER_TICK, 0, server.tickTimes.length);
        
            component.append(new TextComponent("Total tick ("));
            component.append(getTickInfoText(totalInfo));
            component.append(")\n");
    
            int current = server.getTickCount() % server.tickTimes.length;
    
            for (int i = 0; i < 5; i++) {
                int cur = (current + i * 20) % server.tickTimes.length;
                TickInfo info = elsapedInfo(server.tickTimes, MinecraftServer.MS_PER_TICK, cur, 20);
                
                component.append(" - ");
                component.append(getTickInfoText(info));
    
                if (i < 4) {
                    component.append(", \n");
                }
            }
    
            return component;
        }
    }

    private static TickInfo elsapedInfo(long[] tickTimes, int minTickMs, int current, int sample) {
        long maxTime = Long.MIN_VALUE;
        long minTime = Long.MAX_VALUE;

        long all = 0;

        int arrayLen = tickTimes.length;

        int start = current - sample;
        if (start < 0) {
            start = arrayLen + start;
        }

        for (int i = 0; i < sample; i++) {
            long time = tickTimes[(start + i) % arrayLen];

            maxTime = Math.max(maxTime, time);
            minTime = Math.min(minTime, time);

            all += time;
        }

        return new TickInfo(
            1000000000f / (float) minTime,
            1000000000f / (float) maxTime,
            sample > 0 && all > 0 ? (1000000000f / (all / (float) sample)) : 0,
            sample
        );
    }

    private static Component getTickInfoText(TickInfo info) {
        TextComponent component = new TextComponent("");

        component.append("samples: ").append(String.valueOf(info.samples));
        component.append(", ");
        component.append("max: ").append(getTickText(Math.round(info.maxTick * 100f) / 100f));
        component.append(", ");
        component.append("min: ").append(getTickText(Math.round(info.minTick * 100f) / 100f));
        component.append(", ");
        component.append("average: ").append(getTickText(Math.round(info.averageTick * 100f) / 100f));

        return component;
    }

    private static Component getTickText(float tick) {
        if (tick >= 20) {
            return new TextComponent(String.valueOf(tick) + "*").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD);
        } else if (tick >= 17) {
            return new TextComponent(String.valueOf(tick)).withStyle(ChatFormatting.GREEN);
        } else if (tick >= 12) {
            return new TextComponent(String.valueOf(tick)).withStyle(ChatFormatting.YELLOW);
        } else if (tick >= 8) {
            return new TextComponent(String.valueOf(tick)).withStyle(ChatFormatting.RED);
        } else {
            return new TextComponent("!" + String.valueOf(tick)).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
        }
    }

    private static class TickInfo {

        public final float maxTick;
        public final float minTick;

        public final float averageTick;

        public final int samples;

        public TickInfo(float max, float min, float avr, int samples) {
            this.maxTick = max;
            this.minTick = min;
            this.averageTick = avr;

            this.samples = samples;
        }

    }

}
