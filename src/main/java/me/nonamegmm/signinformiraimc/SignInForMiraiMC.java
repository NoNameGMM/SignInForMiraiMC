package me.nonamegmm.signinformiraimc;

import com.minecraft.economy.apis.UltiEconomy;
import com.minecraft.economy.apis.UltiEconomyAPI;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
        System.out.println("[SignInForMiraiMC] 插件Github：https://github.com/NoNameGMM/SignInForMiraiMC/");
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
                        datafile.set(nickname, nickname);
                        datafile.save(file);
                        String economyuser = (String) connectfile.get(nickname);
                        economy.addTo(economyuser, number);
                    }
                    else {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("你还没有绑定游戏用户名呢" + "\n" + "请输入“/绑定(您的游戏名)“绑定账号");
                    }
                }
            }
        }
    }
    public static void bubbleSort(double[] arr,double[] arrs,String[] arrss) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            // 每轮遍历将最大的数移到末尾
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] < arr[j+1]) {
                    double temp = arr[j];
                    double temps = arrs[j];
                    String tempss = arrss[j];
                    arr[j] = arr[j+1];
                    arrs[j] = arrs[j+1];
                    arrss[j] = arrss[j+1];
                    arr[j+1] = temp;
                    arrs[j+1] = temps;
                    arrss[j+1] = tempss;
                }
            }
        }
    }
    @EventHandler
    public void onChart(MiraiGroupMessageEvent e) {
        if (e.getMessage().equals("金币排行榜")) {
            long groupid = getConfig().getLong("QQ群号");
            long id = e.getGroupID();
            if (id == groupid) {
                OfflinePlayer[] players = Bukkit.getOfflinePlayers();
                String[] names = new String[players.length];
                double[] money = new double[players.length];
                double[] bank = new double[players.length];
                for(int i = 0; i < players.length; i++) {
                    names[i] = players[i].getName();
                }
                UltiEconomyAPI economy = SignInForMiraiMC.getEconomy();
                for(int i = 0;i < players.length; i++) {
                    money[i] = economy.checkMoney(names[i]);
                    bank[i] = economy.checkBank(names[i]);
                }
                bubbleSort(money,bank,names);
                String[] moneyshow = new String[100000];
                String[] bankshow = new String[100000];
                for(int i = 0;i < 5; i++) {
                    moneyshow[i] = names[i] + " " + money[i] + "个金币";
                    bankshow[i] = names[i] + " " + bank[i] + "个金币";
                }
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("金币排行榜" + "\n" + "#现金：" + "\n" + "1." + moneyshow[0]  + "\n" + "2." + moneyshow[1]  + "\n" + "3." + moneyshow[2]  + "\n" + "4." + moneyshow[3]  + "\n" + "5." + moneyshow[4] + "\n" + "#存款：" + "\n" + "1." + bankshow[0]  + "\n" + "2." + bankshow[1]  + "\n" + "3." + bankshow[2]  + "\n" + "4." + bankshow[3]  + "\n" + "5." + bankshow[4]);
            }
        }
    }

    @EventHandler
    public void onConnect(MiraiGroupMessageEvent e) throws IOException {
        if (e.getMessage().contains("/绑定")) {
            File file = new File(this.getDataFolder(), "connect.yml");
            YamlConfiguration connectfile = YamlConfiguration.loadConfiguration(file);
            if(connectfile.contains(e.getSenderName())) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("你已经绑定过了喔");
            }
            else {
                String connect = e.getMessage();
                connect = connect.replace("/绑定","");
                connect = connect.replaceAll(" ","");
                if(connect.isEmpty()) {
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("请正确输入您的游戏名喔");
                }
                else {
                    connectfile.set(e.getSenderName(), connect);
                    connectfile.save(file);
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("绑定成功");
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage("您的名字是 " + connect);
                }
            }
        }
    }
    public void OnDelete() throws IOException {
        File file = new File(this.getDataFolder(), "data.yml");
        if (file.exists()) {
            // 删除文件
            if (file.delete()) {
                if (file.createNewFile()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    String datenow = formatter.format(date);
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                    data.set("time", datenow);
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
