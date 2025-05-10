package com.unassigned.innademod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatHornItem.class)
public class GoatHornItemMixin {

    // Injecting into the use() method of GoatHornItem
    @Inject(at = @At("RETURN"), method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(itemStack, 0);
    }

    @Inject(at = @At("HEAD"), method = "playSound", cancellable = true)
    private static void playSound(World world, PlayerEntity player, Instrument instrument, CallbackInfo ci) {
        // Replace the body of the method with the desired behavior
        SoundEvent soundEvent = (SoundEvent) instrument.soundEvent().value();
        float f = instrument.range() / 16.0F;
        world.playSoundFromEntity(player, player, soundEvent, SoundCategory.RECORDS, f, 1.0F);

        // Cancel further execution to skip emitting the GameEvent
        ci.cancel();
    }

}