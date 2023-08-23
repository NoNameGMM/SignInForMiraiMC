package me.nonamegmm.signinformiraimc;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.minecraft.economy.apis.UltiEconomyAPI;
import com.minecraft.economy.apis.UltiEconomy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class SignInForMiraiMC extends JavaPlugin implements Listener{
    private static UltiEconomyAPI economy;
    public static UltiEconomyAPI getEconomy() {
        return economy;
    }
    private Boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("UltiEconomy") != null) {
            economy = new UltiEconomy();
            return true;
        }
        return false;
    }
    @Override // 加载插件
    public void onLoad() {
        System.out.println("[SignInForMiraiMC] 插件正在加载！");
        if (!setupEconomy()){
            getServer().getConsoleSender().sendMessage("无法找到经济前置插件，关闭本插件。。。");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        this.saveConfig();
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
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String datenow = formatter.format(date);
        File file = new File(this.getDataFolder(), "data.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("time", datenow);
        try {
            data.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }
    @EventHandler
    public void onSignIn(MiraiGroupMessageEvent e) throws IOException {
        UltiEconomyAPI economy = SignInForMiraiMC.getEconomy();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String datenow = formatter.format(date);
        File file = new File(this.getDataFolder(), "data.yml");
        YamlConfiguration datafile = YamlConfiguration.loadConfiguration(file);
        if(!datenow.equals(datafile.get("time")))
        {
            OnDelete();
        }
        if (e.getMessage().equals("签到")) {
            long groupid = getConfig().getLong("QQ群号");
            long id = e.getGroupID();
            String nickname = e.getSenderName();
            if (id == groupid) {

                if (datafile.contains(nickname)) {
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("@" + nickname + " 你已经签到过了喔");
                }
                else {
                    File fileconnect = new File(this.getDataFolder(), "connect.yml");
                    YamlConfiguration connectfile = YamlConfiguration.loadConfiguration(fileconnect);
                    if(connectfile.contains(nickname)) {
                        double number = (int) (Math.random() * 50) + 1;
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("恭喜 @" + nickname + "\n" + "获得了" + number + "块钱");
                        String economyuser = (String) connectfile.get(nickname);
                        System.out.println(economyuser + " " + number);
                        economy.addTo(economyuser, number);
                        datafile.set(nickname, nickname);
                        datafile.save(file);
                    }
                    else {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("你还没有绑定游戏用户名呢" + "\n" + "请输入“绑定(您的游戏名)“绑定账号");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onConnect(MiraiGroupMessageEvent e) throws IOException {
        if (e.getMessage().contains("绑定")) {
            String connect = e.getMessage();
            connect = connect.replace("绑定","");
            if(connect.isEmpty()) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("请正确输入您的游戏名喔");
            }
            else {
                File file = new File(this.getDataFolder(), "connect.yml");
                YamlConfiguration connectfile = YamlConfiguration.loadConfiguration(file);
                connectfile.set(e.getSenderName(), connect);
                connectfile.save(file);
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("绑定成功");
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("您的名字是 " + connect);
            }
        }
    }
    public void OnDelete() throws IOException {
        File file = new File(this.getDataFolder(), "data.yml");
        if (file.exists()) {
            // 删除文件
            if (file.delete()) {
                System.out.println("文件删除成功！");
                if (file.createNewFile()) {
                    System.out.println("文件已创建");
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    String datenow = formatter.format(date);
                    File datafile = new File(this.getDataFolder(), "data.yml");
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(datafile);
                    data.set("time", datenow);
                } else {
                    System.out.println("文件已存在");
                }
            } else {
                System.out.println("文件删除失败。");
            }
        } else {
            System.out.println("文件不存在。");
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[SignInForMiraiMC] 插件已关闭！");
    }
}
