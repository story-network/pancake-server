/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class EntityHurtEvent extends EntityEventImpl<Entity> {

    private DamageSource source;
    private float amount;

    public EntityHurtEvent(Entity entity, DamageSource source, float amount) {
        super(entity);

        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() {
        return source;
    }
    
    public void setSource(DamageSource source) {
        this.source = source;
    }

    public float getAmount() {
        return amount;
    }
    
    public void setAmount(float amount) {
        this.amount = amount;
    }


    
}
