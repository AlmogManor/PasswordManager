package main;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TrayWidget {

    public static final String PASSWORDS_FOLDER = "C:/Users/Manor/OneDrive/VSCodeWorkspace/Java/PasswordManager/data/passwords";
    public static final String IV_FOLDER = "C:/Users/Manor/OneDrive/VSCodeWorkspace/Java/PasswordManager/data/iv";
    public static final String PASSWORD_FILE_TYPE = ".password";
    public static final String IV_FILE_TYPE = ".iv";
    public static final Image ICON = new ImageIcon(TrayWidget.class.getResource("/resources/icon.png")).getImage();
    private static final String FILE_VALIDATION_STRING = "valid";

    private static int clickMode = 0; // 0 - left, 1 - right

    public static void main(String[] args) {
        File directory = new File(PASSWORDS_FOLDER);
        if (!directory.exists())
            directory.mkdir();
        directory = new File(IV_FOLDER);
        if (!directory.exists())
            directory.mkdir();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!SystemTray.isSupported())
            return;

        PopupMenu menu = createMenu();

        TrayIcon trayIcon = new TrayIcon(ICON, "Passwords", menu);

        final Frame frame = new Frame("");
        frame.setUndecorated(true);
        frame.setType(JFrame.Type.UTILITY);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    frame.add(menu);
                    clickMode = 0;
                    menu.show(frame, e.getXOnScreen(), e.getYOnScreen());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    clickMode = 1;
                }
            }
        });
        
        try {
            frame.setResizable(false);
            frame.setVisible(true);
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a popup menu
     * 
     * @return the popup menu
     */
    private static PopupMenu createMenu() {
        PopupMenu menu = new PopupMenu();

        MenuItem item = new MenuItem("add");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem(menu);
            }
        });
        menu.add(item);

        item = new MenuItem("remove");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeItem(menu);
            }
        });
        menu.add(item);

        menu.addSeparator();

        createExistingFiles(menu);

        return menu;
    }

    /**
     * removes an item from the popup menu
     * 
     * @param menu
     */
    private static void removeItem(PopupMenu menu) {
        String[] input = GUI.removeEntry();
        if (input == null)
            return;

        int position = itemPosition(menu, input[0]);

        if (position == -1)
            return;

        String rawData = AES.decrypt(input[0], input[1]);

        if (rawData == null || !rawData.startsWith(FILE_VALIDATION_STRING)) {
            GUI.wrongPassword();
            return;
        }

        menu.remove(position);
        File f = new File(IV_FOLDER + "/" + input[0] + IV_FILE_TYPE);
        f.delete();
        f = new File(PASSWORDS_FOLDER + "/" + input[0] + PASSWORD_FILE_TYPE);
        f.delete();
    }

    /**
     * add an item to the menu
     * 
     * @param menu menu to add the item to
     */
    private static void addItem(PopupMenu menu) {
        String[] userInput = GUI.createEntry();
        if (userInput == null)
            return;

        String filename = userInput[0];
        String username = userInput[1];
        String password = userInput[2];
        String key = userInput[3];

        if (itemPosition(menu, filename) != -1) {
            GUI.entryExists();
            return;
        }

        String data = FILE_VALIDATION_STRING + ":" + username + ":" + password;

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        try {
            Files.write(new File(TrayWidget.IV_FOLDER + "/" + filename + IV_FILE_TYPE).toPath(), iv);

            AES.encrypt(filename, data, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createMenuItem(menu, filename);
    }

    /**
     * returns the position of the item in the menu, -1 if the item doesn't appear
     * in the menu
     * 
     * @param menu popup menu
     * @param name item to search
     * @return position of the item in the menu, -1 if the item doesn't appear in
     *         the menu
     */
    private static int itemPosition(PopupMenu menu, String name) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (name.equals(menu.getItem(i).getLabel()))
                return i;
        }
        return -1;
    }

    /**
     * create menu items for existing files on program startup
     * 
     * @param menu menu to add the items to
     */
    private static void createExistingFiles(PopupMenu menu) {
        File directoryPath = new File(PASSWORDS_FOLDER);
        String files[] = directoryPath.list();

        for (String fileName : files) {
            createMenuItem(menu, fileName.substring(0, fileName.length() - PASSWORD_FILE_TYPE.length()));
        }
    }

    /**
     * creates a menu item and adds it to the menu
     * 
     * @param menu     menu to add to
     * @param itemName item name (also acts as the file name)
     */
    private static void createMenuItem(PopupMenu menu, String itemName) {
        MenuItem item = new MenuItem(itemName);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMenuItem(itemName);
            }
        });

        int pos = getMenuPosition(menu, itemName);

        menu.insert(item, pos);
    }

    /**
     * returns the position that the item should be added at in the menu
     * 
     * @param menu     popup menu
     * @param itemName items' name
     * @return position
     */
    private static int getMenuPosition(PopupMenu menu, String itemName) {
        for (int i = 2; i < menu.getItemCount(); i++) {
            if (itemName.compareTo(menu.getItem(i).getLabel()) < 0)
                return i;
        }

        return menu.getItemCount();
    }

    /**
     * this function is called when a menu item is clicked
     * 
     * @param filename name of the file written in the menu item
     */
    private static void handleMenuItem(String filename) {
        String keyString = GUI.enterPassword();
        if (keyString == null)
            return;

        String rawData = AES.decrypt(filename, keyString);

        if (rawData == null || !rawData.startsWith(FILE_VALIDATION_STRING)) {
            GUI.wrongPassword();
            return;
        }

        String[] data = rawData.split(":");

        String username = data[1];
        String password = data[2];

        // automatically copy the password
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(password), null);

        if (clickMode == 1)
            GUI.displayEntry(filename, username, password);
    }
}
