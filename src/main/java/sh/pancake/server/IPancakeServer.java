package sh.pancake.server;

import sh.pancake.classloader.ModdedClassLoader;

public interface IPancakeServer {

     // VERSION TO DOWNLOAD
     String getVersion();
 
     // Impl must call finishMixinInit after finshing mixin configuration
     void start(String[] args, ModdedClassLoader loader, Runnable finishMixinInit);

}
