/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import sh.pancake.mod.IClassModder;

public class ModdedClassLoader extends SecureClassLoader implements IURLExtendableClassLoader {

    static class DummyClassLoader extends ClassLoader {

		private static final Enumeration<URL> NULL_ENUMERATION = new Enumeration<URL>() {
			@Override
			public boolean hasMoreElements() {
				return false;
			}
	
			@Override
			public URL nextElement() {
				return null;
			}
		};
	
		static {
			registerAsParallelCapable();
		}
	
		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			throw new ClassNotFoundException(name);
		}
	
		@Override
		public URL getResource(String name) {
			return null;
		}
	
		@Override
		public Enumeration<URL> getResources(String var1) throws IOException {
			return NULL_ENUMERATION;
		}
	}

	static class Metadata {

		public static final Metadata EMPTY = new Metadata(null, null);

		protected final Manifest manifest;
		protected final CodeSource codeSource;

		Metadata(Manifest manifest, CodeSource codeSource) {
			this.manifest = manifest;
			this.codeSource = codeSource;
		}
	}
	
	static {
		registerAsParallelCapable();
	}

	private ClassLoader platform;
    private ClassLoader classLoader;
    private DynamicURLClassLoader extendedLoader;

	private IClassModder modder;
	
	private Map<String, Metadata> metadataCache;

    public ModdedClassLoader(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ModdedClassLoader(ClassLoader classLoader, IClassModder modder) {
        super(new DynamicURLClassLoader(new URL[0], new DummyClassLoader()));

		this.classLoader = classLoader;
		this.platform = ClassLoader.getPlatformClassLoader();
        this.extendedLoader = (DynamicURLClassLoader) getParent();
		this.modder = modder;
		
		this.metadataCache = new HashMap<>();
    }

    public IClassModder getModder() {
        return modder;
    }

    public void setModder(IClassModder modder) {
        this.modder = modder;
    }

    @Override
    public void addURL(URL url) {
		extendedLoader.addURL(url);
	}
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findLoadedClass(name);

			if (c != null) return c;
			
			try {
				c = platform.loadClass(name);
			} catch (ClassNotFoundException e) {
				// PASS
			}
	
			if (c == null) {
				try {
					byte[] pureInput = this.getClassByteArray(name, true);
					if (pureInput != null) {
						byte[] input = pureInput;
		
						if (this.modder != null) {
							input = modder.transformClassData(name, pureInput);
						}

						Metadata metadata = getMetadata(name, getResource(getClassFileName(name)));
		
						int pkgDelimiterPos = name.lastIndexOf('.');
						if (pkgDelimiterPos > 0) {
							String pkgString = name.substring(0, pkgDelimiterPos);
							if (super.getDefinedPackage(pkgString) == null) {
								super.definePackage(pkgString, null, null, null, null, null, null, null);
							}
						}
		
						c = super.defineClass(name, input, 0, input.length, metadata.codeSource);
					}
				} catch (Exception e) {
		
				}
			}

			if (c == null) {
				try {
					c = classLoader.loadClass(name);
				} catch (ClassNotFoundException e) {
					
				}
			}
	
			if (resolve && c != null) {
				resolveClass(c);
			}
	
			if (c == null) {
				throw new ClassNotFoundException(name + " is undefined");
			}
	
			return c;
		}
    }

    @Override
	public URL getResource(String name) {
		URL url = extendedLoader.getResource(name);

		if (url == null) {
			url = classLoader.getResource(name);
		}
		
		return url;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> first = extendedLoader.getResources(name);
		Enumeration<URL> second = classLoader.getResources(name);
		return new Enumeration<URL>() {
			@Override
			public boolean hasMoreElements() {
				return first.hasMoreElements() || second.hasMoreElements();
			}

			@Override
			public URL nextElement() {
				if (!first.hasMoreElements()) {
					return second.nextElement();
				}

				return first.nextElement();
			}
		};
	}

    public String getClassFileName(String name) {
		return name.replace('.', '/') + ".class";
    }

	public InputStream getResourceAsStream(String name, boolean skipOriginalLoader) {
		InputStream inputStream = extendedLoader.getResourceAsStream(name);

		if (inputStream == null && !skipOriginalLoader) {
			inputStream = classLoader.getResourceAsStream(name);
		}

		return inputStream;
    }
    
    public InputStream getResourceAsStream(String name) {
		return getResourceAsStream(name, false);
	}

    public byte[] getClassByteArray(String name, boolean skipOriginal) throws IOException {
        String classFile = getClassFileName(name);
        
		InputStream inputStream = getResourceAsStream(classFile, skipOriginal);

        if (inputStream == null)
            return null;

		int a = inputStream.available();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(a < 32 ? 32768 : a);
		inputStream.transferTo(outputStream);

		inputStream.close();
		return outputStream.toByteArray();
    }

    public byte[] getClassByteArray(String name) throws IOException {
        return this.getClassByteArray(name, false);
	}

	public boolean isClassLoaded(String className) {
		synchronized (getClassLoadingLock(className)) {
			Class<?> c = findLoadedClass(className);

			return c != null;
		}
	}

	protected URL getSource(String filename, URL resourceURL) {
		URL codeSourceURL = null;

		try {
			URLConnection connection = resourceURL.openConnection();
			if (connection instanceof JarURLConnection) {
				codeSourceURL = ((JarURLConnection) connection).getJarFileURL();
			} else {
				String path = resourceURL.getPath();

				if (path.endsWith(filename)) {
					codeSourceURL = new URL(resourceURL.getProtocol(), resourceURL.getHost(), resourceURL.getPort(), path.substring(0, path.length() - filename.length()));
				}
			}
		} catch (Exception e) {
			
		}

		return codeSourceURL;
	}
	
	Metadata getMetadata(String name, URL resourceURL) {
		if (resourceURL != null) {
			String filename = getClassFileName(name);
			URL codeSourceURL = getSource(filename, resourceURL);

			if (codeSourceURL != null) {
				return metadataCache.computeIfAbsent(codeSourceURL.toString(), (codeSourceStr) -> {
					Manifest manifest = null;
					Certificate[] certificates = null;
					URL fCodeSourceUrl = null;

					try {
						fCodeSourceUrl = new URL(codeSourceStr);
					} catch (IOException e) {
						System.err.println("Cannot load manifest: " + e);
					}

					return new Metadata(manifest, new CodeSource(fCodeSourceUrl, certificates));
				});
			} else {
				System.err.println("Cannot get CodeSource for " + name);
			}
		}

		return Metadata.EMPTY;
	}

}
