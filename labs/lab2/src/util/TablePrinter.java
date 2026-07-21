package util;

import java.util.List;
import java.util.Locale;
import manager.IPlayerManager;
import model.Club;
import model.Player;

/** Fixed-width table output for clubs and players. */
public final class TablePrinter {

    private TablePrinter() {
    }

    public static void printClubs(List<Club> clubs) {
        if (clubs == null || clubs.isEmpty()) {
            System.out.println("No club found.");
            return;
        }
        System.out.println("+------------+------------------------------+----------------------+----------+");
        System.out.println("| Club ID    | Club Name                    | Sponsor Brand        | Budget   |");
        System.out.println("+------------+------------------------------+----------------------+----------+");
        for (Club club : clubs) {
            System.out.printf("| %-10s | %-28s | %-20s | %8s |%n",
                    shorten(club.getId(), 10), shorten(club.getClubName(), 28),
                    shorten(club.getSponsorBrand(), 20), formatNumber(club.getBudget(), 8));
        }
        System.out.println("+------------+------------------------------+----------------------+----------+");
    }

    public static void printPlayers(List<Player> players, IPlayerManager playerManager) {
        if (playerManager == null) {
            throw new IllegalArgumentException("Player manager cannot be null.");
        }
        if (players == null || players.isEmpty()) {
            System.out.println("No player found.");
            return;
        }
        System.out.println("+----------+------------+------------------------------+---------------------------+------------+-----+");
        System.out.println("| PlayerID | Club ID    | Club Name                    | Player Name               | Position   | No. |");
        System.out.println("+----------+------------+------------------------------+---------------------------+------------+-----+");
        for (Player player : players) {
            System.out.printf("| %-8s | %-10s | %-28s | %-25s | %-10s | %3d |%n",
                    shorten(player.getId(), 8), shorten(player.getClubId(), 10),
                    shorten(playerManager.getClubName(player.getClubId()), 28),
                    shorten(player.getPlayerName(), 25),
                    shorten(player.getPosition().getDisplayName(), 10), player.getShirtNumber());
        }
        System.out.println("+----------+------------+------------------------------+---------------------------+------------+-----+");
    }


    private static String formatNumber(double value, int width) {
        String fixed = String.format(Locale.ROOT, "%.2f", value);
        if (fixed.length() <= width) {
            return fixed;
        }
        return String.format(Locale.ROOT, "%.1e", value);
    }

    private static String shorten(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return maxLength <= 3 ? text.substring(0, maxLength)
                : text.substring(0, maxLength - 3) + "...";
    }
}
