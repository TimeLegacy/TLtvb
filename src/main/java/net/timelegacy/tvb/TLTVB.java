package net.timelegacy.tvb;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TLTVB extends JavaPlugin {

  private static TLTVB plugin = null;
  public FileConfiguration config;

  public TLTVB getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    config = this.getConfig();
    saveDefaultConfig();
    getConfig().options().copyDefaults(true);
    saveConfig();

    plugin = this;
    getCommand("db").setExecutor(new DataBaseLogic());
    DataBaseLogic.connect(config.getString("URI"), config.getString("database"));
    getServer().getPluginManager().registerEvents(new DataBaseLogic(), this);
  }
}
