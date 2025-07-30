package ru.sandfoxy.horizen.modules.features.hack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.events.OnCheatCommandMessage;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.utils.ChatHelper;

import java.util.List;

public class ChatHandler extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public ChatHandler() {
        super("ChatHandler", CATEGORY.SETTINGS, "Hmmm. You shouldn't see this...");
        OnCheatCommandMessage.EVENT.register((message, toHud) -> handleMessage(message));
    }

    private void handleMessage(String message){
        String[] args = message.trim().split("\\s+");
        String command = args[0].toLowerCase();
        
        switch (command){
            case "gps" -> handleGPSCommand(args);
            case "help" -> handleHelpCommand(args);
            default -> ChatHelper.sendToPlayer("&cТакой команды не существует! Используйте &e.help &cдля списка команд.");
        }
    }
    
    private void handleGPSCommand(String[] args) {
        if (args.length < 2) {
            ChatHelper.sendToPlayer("&cИспользование: .gps <add|list|del|delete|help>");
            return;
        }
        
        String subCommand = args[1].toLowerCase();
        
        switch (subCommand) {
            case "add" -> handleGPSAdd(args);
            case "list" -> handleGPSList();
            case "del", "delete" -> handleGPSDelete(args);
            case "help" -> handleGPSHelp();
            default -> ChatHelper.sendToPlayer("&cНеизвестная GPS команда. Используйте &e.gps help &cдля справки.");
        }
    }
    
    private void handleGPSAdd(String[] args) {
        if (args.length < 4) {
            ChatHelper.sendToPlayer("&cИспользование:");
            ChatHelper.sendToPlayer("&e.gps add <x> <z> [название]");
            ChatHelper.sendToPlayer("&e.gps add <x> <y> <z> [название]");
            return;
        }
        
        try {
            if (args.length == 4) {
                // gps add x z
                int x = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                GPS.addGPSPoint(x, z, "");
                ChatHelper.sendToPlayer("&aGPS точка добавлена: &f(" + x + ", 64, " + z + ")");
            } else if (args.length == 5) {
                // Проверяем, это gps add x z название или gps add x y z
                try {
                    int x = Integer.parseInt(args[2]);
                    int y = Integer.parseInt(args[3]);
                    int z = Integer.parseInt(args[4]);
                    GPS.addGPSPoint(x, y, z, "");
                    ChatHelper.sendToPlayer("&aGPS точка добавлена: &f(" + x + ", " + y + ", " + z + ")");
                } catch (NumberFormatException e) {
                    // Это gps add x z название
                    int x = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    String name = args[4];
                    GPS.addGPSPoint(x, z, name);
                    ChatHelper.sendToPlayer("&aGPS точка добавлена: &f" + name + " (" + x + ", 64, " + z + ")");
                }
            } else if (args.length >= 6) {
                // gps add x y z название
                int x = Integer.parseInt(args[2]);
                int y = Integer.parseInt(args[3]);
                int z = Integer.parseInt(args[4]);
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 5; i < args.length; i++) {
                    if (i > 5) nameBuilder.append(" ");
                    nameBuilder.append(args[i]);
                }
                String name = nameBuilder.toString();
                GPS.addGPSPoint(x, y, z, name);
                ChatHelper.sendToPlayer("&aGPS точка добавлена: &f" + name + " (" + x + ", " + y + ", " + z + ")");
            }
        } catch (NumberFormatException e) {
            ChatHelper.sendToPlayer("&cОшибка: координаты должны быть числами!");
        }
    }
    
    private void handleGPSList() {
        List<GPS.GPSPoint> points = GPS.getAllGPSPoints();
        
        if (points.isEmpty()) {
            ChatHelper.sendToPlayer("&eСписок GPS точек пуст.");
            return;
        }
        
        ChatHelper.sendToPlayer("&aСписок GPS точек (&f" + points.size() + "&a):");
        for (int i = 0; i < points.size(); i++) {
            GPS.GPSPoint point = points.get(i);
            String displayName = point.name.isEmpty() ? 
                "&7#" + (i + 1) + " &f(" + point.x + ", " + point.y + ", " + point.z + ")" :
                "&7#" + (i + 1) + " &f" + point.name + " (" + point.x + ", " + point.y + ", " + point.z + ")";
            
            // Вычисляем расстояние, если игрок в мире
            if (mc.player != null) {
                double distance = mc.player.getPos().distanceTo(new Vec3d(point.x + 0.5, point.y, point.z + 0.5));
                displayName += " &7[" + String.format("%.1fm", distance) + "]";
            }
            
            ChatHelper.sendToPlayer(displayName);
        }
    }
    
    private void handleGPSDelete(String[] args) {
        if (args.length < 3) {
            ChatHelper.sendToPlayer("&cИспользование:");
            ChatHelper.sendToPlayer("&egps del <название>");
            ChatHelper.sendToPlayer("&egps del <номер>");
            return;
        }
        
        String target = args[2];
        
        // Попробуем как номер
        try {
            int index = Integer.parseInt(target) - 1; // Пользователи вводят с 1, а массив с 0
            List<GPS.GPSPoint> points = GPS.getAllGPSPoints();
            
            if (index >= 0 && index < points.size()) {
                GPS.GPSPoint point = points.get(index);
                GPS.removeGPSPointByIndex(index);
                String displayName = point.name.isEmpty() ? 
                    "(" + point.x + ", " + point.y + ", " + point.z + ")" :
                    point.name + " (" + point.x + ", " + point.y + ", " + point.z + ")";
                ChatHelper.sendToPlayer("&aGPS точка удалена: &f" + displayName);
                return;
            } else {
                ChatHelper.sendToPlayer("&cНекорректный номер точки! Используйте &egps list &cдля просмотра.");
                return;
            }
        } catch (NumberFormatException e) {
            // Не число, попробуем как название
        }
        
        // Попробуем как название
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) nameBuilder.append(" ");
            nameBuilder.append(args[i]);
        }
        String name = nameBuilder.toString();
        
        List<GPS.GPSPoint> pointsBefore = GPS.getAllGPSPoints();
        GPS.removeGPSPoint(name);
        List<GPS.GPSPoint> pointsAfter = GPS.getAllGPSPoints();
        
        if (pointsBefore.size() > pointsAfter.size()) {
            ChatHelper.sendToPlayer("&aGPS точка удалена: &f" + name);
        } else {
            ChatHelper.sendToPlayer("&cGPS точка с названием &f'" + name + "' &cне найдена!");
        }
    }
    
    private void handleGPSHelp() {
        ChatHelper.sendToPlayer("&a=== Команды GPS ===");
        ChatHelper.sendToPlayer("&e.gps add <x> <z> &7- добавить точку (Y=64)");
        ChatHelper.sendToPlayer("&e.gps add <x> <y> <z> &7- добавить точку с Y");
        ChatHelper.sendToPlayer("&e.gps add <x> <z> <название> &7- добавить именованную точку");
        ChatHelper.sendToPlayer("&e.gps add <x> <y> <z> <название> &7- добавить именованную точку с Y");
        ChatHelper.sendToPlayer("&e.gps list &7- показать все точки");
        ChatHelper.sendToPlayer("&e.gps del <название> &7- удалить по названию");
        ChatHelper.sendToPlayer("&e.gps del <номер> &7- удалить по номеру из списка");
        ChatHelper.sendToPlayer("&e.gps help &7- эта справка");
    }
    
    private void handleHelpCommand(String[] args) {
        ChatHelper.sendToPlayer("&a=== Доступные команды ===");
        ChatHelper.sendToPlayer("&e.gps &7- управление точками на GPS");
        ChatHelper.sendToPlayer("&e.lobby &7- управление лобби для совместной игры");
        ChatHelper.sendToPlayer("&e.help &7- показать эту справку");
        ChatHelper.sendToPlayer("");
        ChatHelper.sendToPlayer("&7Для подробной справки: &e.gps help &7или &e.lobby help");
    }
}
