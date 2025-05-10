package com.unassigned.innademod.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WindChargeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**
     * @author InnaDeMod: unassigned_
     * @reason preventing cooldown from being applied to windcharge item.
     */
    @Overwrite
    private ItemStack applyRemainderAndCooldown(LivingEntity user, ItemStack stack) {
        UseRemainderComponent useRemainderComponent = stack.get(DataComponentTypes.USE_REMAINDER);
        UseCooldownComponent useCooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
        int i = stack.getCount();
        ItemStack itemStack = (ItemStack)(Object)this;

        if (useRemainderComponent != null) {
            boolean var10003 = user.isInCreativeMode();
            Objects.requireNonNull(user);
            itemStack = useRemainderComponent.convert((ItemStack)(Object)this, i, var10003, user::giveOrDropStack);
        }

        if (useCooldownComponent != null && !(stack.getItem() instanceof WindChargeItem)) {
            useCooldownComponent.set(stack, user);
        }

        return itemStack;
    }
}