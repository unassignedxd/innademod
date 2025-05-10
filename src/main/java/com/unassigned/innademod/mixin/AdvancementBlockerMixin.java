package com.unassigned.innademod.mixin;

import com.unassigned.innademod.InnaDeModAutoConfig;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class AdvancementBlockerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(at=@At("HEAD"), method="grantCriterion", cancellable=true)
    public void grantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir)
    {
        RegistryKey<World> currentDim = owner.getWorld().getRegistryKey();
        String dimIdString = currentDim.getValue().toString();

        if (InnaDeModAutoConfig.INSTANCE.blockedDimensions.contains(dimIdString)) {
            cir.setReturnValue(false);
            cir.cancel();
        }

    }

}
