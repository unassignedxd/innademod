package com.unassigned.innademod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.*;

@Config(name = InnaDeMod.MOD_ID)
public class InnaDeModAutoConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static InnaDeModAutoConfig INSTANCE;

    public static void init() {
        AutoConfig.register(InnaDeModAutoConfig.class, me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(InnaDeModAutoConfig.class).getConfig();
        INSTANCE.deduplicate();
        AutoConfig.getConfigHolder(InnaDeModAutoConfig.class).save();
    }

    @Comment("Play a custom sound when the player respawns")
    public boolean enableRespawnSound = true;

    @Comment("Prevents wind charges from going on cooldown")
    public boolean bypassWindChargeCooldown = true;

    @Comment("Prevents goat horns from going on cooldown")
    public boolean bypassGoatHornCooldown = true;

    @Comment("Enable command aliases like /c or /s")
    public boolean enableCmdAliases = true;

    @Comment("A list of dimension IDs where advancements are blocked")
    public List<String> blockedDimensions = new ArrayList<>(List.of("my_mod:creative_realm"));

    @ConfigEntry.Gui.Excluded
    public List<AliasWrapper> cmdAliases = getDefaultAliasesList();

    public static class CommandAlias {
        @Comment("Whether this alias is active")
        public boolean enabled = true;

        @Comment("List of alias commands")
        public List<String> alias = new ArrayList<>();

        @Comment("The actual command this alias runs")
        public String command = "";
    }

    public static class AliasWrapper {
        public String key;
        public CommandAlias alias;
    }

    private static List<AliasWrapper> getDefaultAliasesList() {
        List<AliasWrapper> list = new ArrayList<>();

        AliasWrapper enter = new AliasWrapper();
        enter.key = "creativerse_enter_alias";
        enter.alias = new CommandAlias();
        enter.alias.alias = List.of("/c", "/creative");
        enter.alias.command = "/trigger creativerse_enter";

        AliasWrapper exit = new AliasWrapper();
        exit.key = "creativerse_exit_alias";
        exit.alias = new CommandAlias();
        exit.alias.alias = List.of("/s", "/survival");
        exit.alias.command = "/trigger creativerse_exit";

        list.add(enter);
        list.add(exit);

        return list;
    }

    public void deduplicate() {
        blockedDimensions = new ArrayList<>(new HashSet<>(blockedDimensions));

        LinkedHashMap<String, CommandAlias> map = new LinkedHashMap<>();
        for (AliasWrapper w : cmdAliases) {
            map.putIfAbsent(w.key, w.alias);
        }
        List<AliasWrapper> unique = new ArrayList<>();
        for (var entry : map.entrySet()) {
            AliasWrapper aw = new AliasWrapper();
            aw.key = entry.getKey();
            aw.alias = entry.getValue();
            unique.add(aw);
        }
        this.cmdAliases = unique;
    }
}
