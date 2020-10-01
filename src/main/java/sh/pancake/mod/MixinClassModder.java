/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.mod;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.PancakeMixinTransformer;

public class MixinClassModder implements IClassModder {

	private PancakeMixinTransformer transformer;

	private boolean isInited;

    public MixinClassModder() {
		this.transformer = null;
		this.isInited = false;
	}

	@Override
	public void initModder() {
		if (isInited) throw new RuntimeException("Modder already inited!!");
		
		this.transformer = new PancakeMixinTransformer();
		this.isInited = true;
	}

	@Override
	public boolean isInited() {
		return isInited;
	}

	@Override
	public byte[] transformClassData(String name, byte[] data) {
		if (!isInited) throw new RuntimeException("Modder not inited yet!");

		return transformer.transformClassBytes(name, name, data);
	}

}