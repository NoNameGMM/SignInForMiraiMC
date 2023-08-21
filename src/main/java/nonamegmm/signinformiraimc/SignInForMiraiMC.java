package nonamegmm.signinformiraimc;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public final class SignInForMiraiMC extends JavaPlugin implements Listener{
    @Override // 加载插件
    public void onLoad() {
        System.out.println("[SignInForMiraiMC] 插件正在加载！");
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        this.saveConfig();
        this.saveResource("data.yml",false);
        System.out.println("[SignInForMiraiMC] 插件已启动！");
        System.out.println("[SignInForMiraiMC] 作者：NoNameGMM");
        System.out.println("[SignInForMiraiMC] 作者Github：");
        System.out.println("[SignInForMiraiMC] 正在获取config.yml！");
        long groupid = getConfig().getLong("QQ群号");
        System.out.println("[SignInForMiraiMC] 使用群号为：" + groupid);
        System.out.println("[SignInForMiraiMC] 配置成功！");
        Bukkit.getPluginManager().registerEvents(this, this);
        int pluginId = 19585; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }
    @EventHandler
    public void onGroupMessageReceive(MiraiGroupMessageEvent e) throws IOException {
        if (e.getMessage().equals("签到")) {
            long groupid = getConfig().getLong("QQ群号");
            long id = e.getGroupID();
            String nickname = e.getSenderName();
            System.out.println("awa");
            System.out.println(groupid);
            System.out.println(id);
            System.out.println(nickname);
            if (id == groupid) {
                File file = new File(this.getDataFolder(), "data.yml");
                YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                if (data.contains(nickname)) {
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("@" + nickname + " 你已经签到过了喔");
                }
                else {
                    int number = (int) (Math.random() * 50) + 1;
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("恭喜 @" + nickname + "\n" + "获得了" + number + "块钱");
                    data.set(nickname, nickname);
                    data.save(file);
                }
            }
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[SignInForMiraiMC] 插件已关闭！");
    }
}
