package net.timelegacy.tvb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DataBaseLogic implements CommandExecutor, Listener {

  static MongoDatabase mongoDatabase;
  private static MongoClient mongoClient;
  private static MongoCollection<Document> queue;

  public static boolean connect(String uri, String database) {
    mongoClient = MongoClients.create(uri);
    mongoDatabase = mongoClient.getDatabase(database);
    queue = mongoDatabase.getCollection("queue");
    return true;
  }

  /**
   * Disconnect from mongodb
   */
  public static boolean disconnect() {
    mongoClient.close();
    return true;
  }

  private static void error(String args[]) {
    output("§cThere was an issue with the db command, here is what was ran...");
    output("");
    output(args.toString());
    output("");
  }

  private static void output(String message) {
    Bukkit.getConsoleSender().sendMessage(message);
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
    if (commandSender instanceof Player) {
      return true;
    } else {
      if (args.length > 2) {

        String product = "";
        for (int i = 1; i < args.length; i++) {
          product = product + " " + args[i];
        }
        Document doc =
            new Document("uuid", (args[0]))
                .append("type", "payment")
                .append("time", System.currentTimeMillis())
                .append("product", product);
        queue.insertOne(doc);
      } else {
        error(args);
      }
      return true;
    }
  }

  @EventHandler
  public void onVotifierEvent(VotifierEvent event) {
    Vote vote = event.getVote();
    Document doc =
        new Document("uuid", Bukkit.getOfflinePlayer(vote.getUsername()).getUniqueId().toString())
            .append("type", "vote")
            .append("time", System.currentTimeMillis())
            .append("source", vote.getServiceName())
            .append("ip", vote.getAddress());
    queue.insertOne(doc);
  }
}
