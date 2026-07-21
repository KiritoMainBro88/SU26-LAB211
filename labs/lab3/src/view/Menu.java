package view;

/** Utility for numbered console menus. */
public final class Menu {

    private Menu() {
    }

    public static void showMenu(Object[] options) {
        int order = 1;
        for (Object option : options) {
            System.out.println(order + ". " + option);
            order++;
        }
        System.out.print("Your options from 1 - " + options.length + ": ");
    }
}
