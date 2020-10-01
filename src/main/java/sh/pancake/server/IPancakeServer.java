package sh.pancake.server;

import sh.pancake.classloader.ModdedClassLoader;

public interface IPancakeServer {

     // VERSION TO DOWNLOAD
     String getVersion();
 
     void start(String[] args, ModdedClassLoader loader);

}
