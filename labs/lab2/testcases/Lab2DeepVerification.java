import app.Menu;
import filehelper.ClubFileHelper;
import filehelper.PlayerFileHelper;
import io.ClubIO;
import io.PlayerIO;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import manager.ClubManager;
import manager.IClubManager;
import manager.IPlayerManager;
import manager.PlayerManager;
import model.Club;
import model.Player;
import model.Position;
import util.TablePrinter;
import util.CsvUtils;
import validation.ValidationUtils;

public class Lab2DeepVerification {
    private static int passed=0, failed=0;
    private static void check(String n, boolean ok){ if(ok){passed++;System.out.println("PASS - "+n);}else{failed++;System.out.println("FAIL - "+n);} }
    private static void expectIAE(String n, Runnable r){try{r.run();check(n,false);}catch(IllegalArgumentException e){check(n,true);}catch(Exception e){check(n,false);}}
    public static void main(String[] args) throws Exception {
        ClubManager clubs=new ClubManager();
        PlayerManager players=new PlayerManager(clubs);
        check("null club rejected", !clubs.addClub(null));
        check("infinite budget rejected", !clubs.addClub(new Club("CL-0001","A","S",Double.POSITIVE_INFINITY)));
        check("NaN budget rejected", !clubs.addClub(new Club("CL-0001","A","S",Double.NaN)));
        check("valid club added", clubs.addClub(new Club("CL-0001","Alpha","S",100)));
        check("second valid club added", clubs.addClub(new Club("CL-0002","Beta","S",200)));
        check("null player rejected", !players.addPlayer(null));
        check("null position rejected", !players.addPlayer(new Player("P0001","CL-0001","X",null,1)));
        check("shirt 100 rejected", !players.addPlayer(new Player("P0001","CL-0001","X",Position.FORWARD,100)));
        check("valid player added", players.addPlayer(new Player("P0001","CL-0001","One",Position.FORWARD,1)));
        check("blank update fields keep old and succeed", players.updatePlayer("P0001","","",""));
        check("null partial search empty", players.searchByPartialName(null).isEmpty());
        check("null position filter empty", players.filterByPosition(null).isEmpty());
        check("null shirt club safe", !players.isShirtNumberUsed(null,1,null));
        expectIAE("PlayerManager null dependency rejected", () -> new PlayerManager(null));
        expectIAE("ClubIO null scanner rejected", () -> new ClubIO(null,clubs));
        expectIAE("ClubIO null manager rejected", () -> new ClubIO(new Scanner(""),null));
        expectIAE("PlayerIO null dependency rejected", () -> new PlayerIO(new Scanner(""),clubs,null));
        expectIAE("TablePrinter null manager rejected", () -> TablePrinter.printPlayers(
                java.util.Collections.<Player>emptyList(), null));
        check("CSV parser rejects unclosed quote", rejectsMalformedCsv());

        Club copy=clubs.findById("CL-0001"); copy.setClubName("MUTATED");
        check("findById club defensive copy", "Alpha".equals(clubs.findById("CL-0001").getClubName()));
        Player pcopy=players.findById("P0001"); pcopy.setPlayerName("MUTATED");
        check("findById player defensive copy", "One".equals(players.findById("P0001").getPlayerName()));
        clubs.getItems().get(0).setClubName("MUTATED2");
        check("getItems entity defensive copy", "Alpha".equals(clubs.findById("CL-0001").getClubName()));

        int oldClubCount=clubs.getItems().size();
        expectIAE("replaceAll duplicate clubs rejected", () -> clubs.replaceAll(Arrays.asList(
                new Club("CL-0010","X","S",1), new Club("cl-0010","Y","T",2))));
        check("failed club replace preserves state", clubs.getItems().size()==oldClubCount);
        int oldPlayerCount=players.getItems().size();
        expectIAE("replaceAll duplicate shirts rejected", () -> players.replaceAll(Arrays.asList(
                new Player("P0010","CL-0001","X",Position.FORWARD,9),
                new Player("P0011","CL-0001","Y",Position.DEFENDER,9))));
        check("failed player replace preserves state", players.getItems().size()==oldPlayerCount);
        expectIAE("replaceAll null player item rejected", () -> players.replaceAll(Arrays.asList((Player)null)));

        Path tmp=Files.createTempFile("clubs-infinite-", ".txt");
        ClubFileHelper helper=new ClubFileHelper();
        check("file save rejects infinite budget", !helper.saveClubs(tmp.toString(), Arrays.asList(
                new Club("CL-0099","Inf","S",Double.POSITIVE_INFINITY))));
        Files.deleteIfExists(tmp);
        check("Position.parse null safe", Position.parse(null)==null);
        check("filter budget Infinity rejected", clubs.filterByBudget(Double.POSITIVE_INFINITY).isEmpty());
        check("finite threshold parser accepts zero", ValidationUtils.isFiniteDouble("0"));
        check("finite threshold parser accepts negative", ValidationUtils.isFiniteDouble("-1"));
        check("finite threshold parser rejects NaN", !ValidationUtils.isFiniteDouble("NaN"));
        check("finite threshold parser rejects Infinity", !ValidationUtils.isFiniteDouble("Infinity"));
        check("zero budget threshold accepted as empty result", clubs.filterByBudget(0).isEmpty());
        check("negative budget threshold accepted as empty result", clubs.filterByBudget(-1).isEmpty());
        Club filteredClub = clubs.filterByBudget(500).get(0);
        filteredClub.setClubName("MUTATED_FILTER");
        check("filterByBudget defensive entity copy", !"MUTATED_FILTER".equals(clubs.findById("CL-0001").getClubName()));
        Player searchedPlayer = players.searchByPartialName("One").get(0);
        searchedPlayer.setPlayerName("MUTATED_SEARCH");
        check("search result defensive entity copy", !"MUTATED_SEARCH".equals(players.findById("P0001").getPlayerName()));
        Player filteredPlayer = players.filterByPosition(Position.FORWARD).get(0);
        filteredPlayer.setPlayerName("MUTATED_POSITION");
        check("position filter defensive entity copy", !"MUTATED_POSITION".equals(players.findById("P0001").getPlayerName()));
        check("Position.parse case insensitive", Position.parse("wInGeR")==Position.WINGER);

        // Equal club names: different clubs use deterministic ID tie-break; same club uses shirt number.
        clubs.addClub(new Club("CL-0003","SameName","S",300));
        clubs.addClub(new Club("CL-0004","SameName","S",400));
        players.addPlayer(new Player("P0002","CL-0004","Four",Position.DEFENDER,2));
        players.addPlayer(new Player("P0003","CL-0003","Three",Position.DEFENDER,99));
        players.addPlayer(new Player("P0004","CL-0003","ThreeLow",Position.DEFENDER,3));
        java.util.List<Player> sorted = players.getPlayersSortedByClubName();
        int idxP3=-1, idxP4=-1, idxP2=-1;
        for(int i=0;i<sorted.size();i++){
            if("P0003".equals(sorted.get(i).getId())) idxP3=i;
            if("P0004".equals(sorted.get(i).getId())) idxP4=i;
            if("P0002".equals(sorted.get(i).getId())) idxP2=i;
        }
        check("same club sorts by shirt number", idxP4>=0 && idxP3>=0 && idxP4<idxP3);
        check("equal club names but different clubs do not use cross-club shirt order", idxP3>=0 && idxP2>=0 && idxP3<idxP2);

        Path blankClubs=Files.createTempFile("clubs-blank-", ".txt");
        Files.write(blankClubs, Arrays.asList("CL-0010, X, S, 10", "", "CL-0011, Y, S, 20"));
        boolean blankClubRejected=false;
        try{ helper.loadClubs(blankClubs.toString()); }catch(java.io.IOException ex){ blankClubRejected=true; }
        check("strict load rejects blank club line", blankClubRejected);
        Files.deleteIfExists(blankClubs);

        PlayerFileHelper playerHelper=new PlayerFileHelper();
        Path playerOut=Files.createTempFile("players-save-", ".txt");
        boolean missingClubSave=playerHelper.savePlayers(playerOut.toString(), Arrays.asList(
                new Player("P0099","CL-9999","Ghost",Position.FORWARD,9)), clubs.getItems());
        check("save rejects player with missing club reference", !missingClubSave);
        Files.deleteIfExists(playerOut);
        check("null club file path rejected", rejectsClubLoadNull(helper));
        PlayerFileHelper guardedPlayerHelper=new PlayerFileHelper();
        check("null player file path rejected", rejectsPlayerLoadNull(guardedPlayerHelper, clubs.getItems()));

        Path csvClubFile=Files.createTempFile("clubs-csv-", ".txt");
        Club commaClub=new Club("CL-0200","City, United","Brand \"X\"",123.5);
        check("club CSV save supports comma and quote", helper.saveClubs(csvClubFile.toString(), Arrays.asList(commaClub)));
        java.util.List<Club> commaClubs=helper.loadClubs(csvClubFile.toString());
        check("club CSV round trip preserves text", commaClubs.size()==1
                && "City, United".equals(commaClubs.get(0).getClubName())
                && "Brand \"X\"".equals(commaClubs.get(0).getSponsorBrand()));
        Path csvPlayerFile=Files.createTempFile("players-csv-", ".txt");
        Player commaPlayer=new Player("P0200","CL-0200","Doe, Jane",Position.WINGER,8);
        check("player CSV save supports comma", guardedPlayerHelper.savePlayers(csvPlayerFile.toString(),
                Arrays.asList(commaPlayer), commaClubs));
        java.util.List<Player> commaPlayers=guardedPlayerHelper.loadPlayers(csvPlayerFile.toString(), commaClubs);
        check("player CSV round trip preserves text", commaPlayers.size()==1
                && "Doe, Jane".equals(commaPlayers.get(0).getPlayerName()));
        Files.deleteIfExists(csvClubFile);
        Files.deleteIfExists(csvPlayerFile);

        // Function 13 integration: clear current state first, then fail strictly on an invalid line.
        // Preserve the submitted sample files so running this verification does not modify project data.
        Path clubDataPath = java.nio.file.Paths.get("clubs.txt");
        Path playerDataPath = java.nio.file.Paths.get("players.txt");
        byte[] originalClubs = Files.exists(clubDataPath) ? Files.readAllBytes(clubDataPath) : null;
        byte[] originalPlayers = Files.exists(playerDataPath) ? Files.readAllBytes(playerDataPath) : null;
        try {
            Files.write(clubDataPath, Arrays.asList(
                    "CL-0100, Load Club, Brand, 100"), StandardCharsets.UTF_8);
            Files.write(playerDataPath, Arrays.asList(
                    "P0100, CL-0100, Load Player, Forward, 9"), StandardCharsets.UTF_8);
            Menu menu=new Menu();
            Method loadData=Menu.class.getDeclaredMethod("loadData", boolean.class);
            loadData.setAccessible(true);
            check("menu strict load accepts valid files", ((Boolean)loadData.invoke(menu,false)).booleanValue());
            Field clubField=Menu.class.getDeclaredField("clubManager"); clubField.setAccessible(true);
            Field playerField=Menu.class.getDeclaredField("playerManager"); playerField.setAccessible(true);
            IClubManager menuClubs=(IClubManager)clubField.get(menu);
            IPlayerManager menuPlayers=(IPlayerManager)playerField.get(menu);
            check("valid menu load populated both managers", menuClubs.getItems().size()==1 && menuPlayers.getItems().size()==1);
            Files.write(clubDataPath, Arrays.asList(
                    "CL-0100, Load Club, Brand, 100", "", "CL-0101, Other, Brand, 200"), StandardCharsets.UTF_8);
            check("menu strict load fails on blank line", !((Boolean)loadData.invoke(menu,false)).booleanValue());
            check("failed reload leaves clear-first empty state", menuClubs.getItems().isEmpty() && menuPlayers.getItems().isEmpty());
        } finally {
            restoreFile(clubDataPath, originalClubs);
            restoreFile(playerDataPath, originalPlayers);
        }

        Club clubCopy = new Club(clubs.getClubById("CL-0001"));
        clubCopy.setClubName("COPY_ONLY");
        check("Club copy constructor is independent", !"COPY_ONLY".equals(clubs.getClubById("CL-0001").getClubName()));
        Player playerCopy = new Player(players.getPlayerById("P0001"));
        playerCopy.setPlayerName("COPY_ONLY");
        check("Player copy constructor is independent", !"COPY_ONLY".equals(players.getPlayerById("P0001").getPlayerName()));
        check("domain club getters expose required operations", clubs.getAllClubs().size() == clubs.getItems().size()
                && clubs.getClubById("CL-0001") != null);
        check("domain player getters expose required operations", players.getAllPlayers().size() == players.getItems().size()
                && players.getPlayerById("P0001") != null);

        Path managerClubFile = Files.createTempFile("club-manager-", ".txt");
        ClubManager persistedClubs = new ClubManager();
        persistedClubs.addClub(new Club("CL-0300", "Persisted", "Brand", 300));
        check("ClubManager saveToFile succeeds", persistedClubs.saveToFile(managerClubFile.toString()));
        ClubManager loadedClubs = new ClubManager();
        check("ClubManager loadFromFile succeeds", loadedClubs.loadFromFile(managerClubFile.toString())
                && loadedClubs.getClubById("CL-0300") != null);
        Path managerPlayerFile = Files.createTempFile("player-manager-", ".txt");
        PlayerManager persistedPlayers = new PlayerManager(loadedClubs);
        persistedPlayers.addPlayer(new Player("P0300", "CL-0300", "Persisted Player", Position.FORWARD, 30));
        check("PlayerManager saveToFile succeeds", persistedPlayers.saveToFile(managerPlayerFile.toString()));
        PlayerManager loadedPlayers = new PlayerManager(loadedClubs);
        check("PlayerManager loadFromFile succeeds", loadedPlayers.loadFromFile(managerPlayerFile.toString())
                && loadedPlayers.getPlayerById("P0300") != null);
        Files.deleteIfExists(managerClubFile);
        Files.deleteIfExists(managerPlayerFile);

        System.out.println("RESULT: "+passed+" passed, "+failed+" failed");
        if(failed>0) System.exit(1);
    }
    private static boolean rejectsClubLoadNull(ClubFileHelper helper){
        try{helper.loadClubs(null);return false;}catch(java.io.IOException ex){return true;}
    }
    private static boolean rejectsPlayerLoadNull(PlayerFileHelper helper, java.util.List<Club> clubs){
        try{helper.loadPlayers(null,clubs);return false;}catch(java.io.IOException ex){return true;}
    }
    private static boolean rejectsMalformedCsv(){
        try{CsvUtils.parseLine("A, \"unclosed");return false;}catch(java.io.IOException ex){return true;}
    }
    private static void restoreFile(Path path, byte[] original) throws java.io.IOException {
        if (original == null) {
            Files.deleteIfExists(path);
        } else {
            Files.write(path, original);
        }
    }

}
