package com.unassigned.innademod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnaDeMod implements ModInitializer {
	public static final String MOD_ID = "innademod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier PLAYER_RESPAWN_ID = Identifier.of("innademod", "entity.player.respawn");
	public static final SoundEvent PLAYER_RESPAWN = SoundEvent.of(PLAYER_RESPAWN_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[InnaDeMod] Initializing InnaDeMod...");

		LOGGER.info("[InnaDeMod] Initializing AutoConfig...");
		InnaDeModAutoConfig.init();
		InnaDeModAutoConfig config = InnaDeModAutoConfig.INSTANCE;

		LOGGER.info("[InnaDeMod] Loaded config with blocked dimensions: " + config.blockedDimensions);

		// Register sound
		Registry.register(Registries.SOUND_EVENT, PLAYER_RESPAWN_ID, PLAYER_RESPAWN);

		// Block advancements in configured dimensions
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			Identifier dimId = player.getWorld().getRegistryKey().getValue();

			if (config.blockedDimensions.contains(dimId.toString())) {
				System.out.println("[InnaDeMod] Blocking advancements for " + player.getName().getString() + " in " + dimId);
			}
		});

		// Command alias registration
		if (config.enableCmdAliases && config.cmdAliases != null && !config.cmdAliases.isEmpty()) {
			CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
				for (InnaDeModAutoConfig.AliasWrapper wrapper : config.cmdAliases) {
					InnaDeModAutoConfig.CommandAlias alias = wrapper.alias;
					if (!alias.enabled || alias.alias.isEmpty()) continue;

					for (String trigger : alias.alias) {
						String clean = trigger.startsWith("/") ? trigger.substring(1) : trigger;
						dispatcher.register(
								CommandManager.literal(clean).executes(ctx -> {
									String command = alias.command;
									if (command.startsWith("/")) command = command.substring(1);
									try {
										ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), command);
										return 1;
									} catch (Exception e) {
										System.err.println("[InnaDeMod] Failed to execute alias command: " + e.getMessage());
										return 0;
									}
								})
						);
					}
				}
			});
		}

		// Respawn sound
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (config.enableRespawnSound) {
				playRespawnSound(newPlayer);
			}
		});
	}
	private static void playRespawnSound(ServerPlayerEntity player) {
		if(InnaDeModAutoConfig.INSTANCE.enableRespawnSound)
		{
			player.getWorld().playSound(
					null,
					player.getBlockPos(),
					PLAYER_RESPAWN,
					SoundCategory.PLAYERS,
					1.0F,
					1.0F
			);
		}
	}
}