package net.jens.cookingmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties SALTED_BUTTER = new FoodProperties.Builder().nutrition(2).fast()
            .saturationMod(0.2f).effect(()-> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200),0.1f).build();
    public static final FoodProperties GOLDEN_HARVEST_LOAF = new FoodProperties.Builder().nutrition(2).fast()
            .saturationMod(0.2f).effect(()-> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200),0.1f).build();
}
