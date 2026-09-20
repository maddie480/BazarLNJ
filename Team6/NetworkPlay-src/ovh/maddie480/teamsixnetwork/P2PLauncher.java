package ovh.maddie480.teamsixnetwork;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class P2PLauncher {
    private static class Server {
        private final String name;
        private final int id;
        private final int playerCount;

        public Server(String name, int id, int playerCount) {
            this.name = name;
            this.id = id;
            this.playerCount = playerCount;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name + " (" + playerCount + " player" + (playerCount == 1 ? "" : "s") + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Server server = (Server) o;
            return id == server.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Team 6 Network (tm)");
        window.setLayout(new BorderLayout());

        JList<Server> serverList = new JList<>();
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        window.add(serverList, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        window.add(buttons, BorderLayout.SOUTH);

        JButton join = new JButton("Join");
        join.setEnabled(false);
        buttons.add(join);

        JButton create = new JButton("Create");
        buttons.setEnabled(false);
        buttons.add(create);

        serverList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            join.setEnabled(serverList.getSelectedValue() != null);
        });

        window.setSize(300, 300);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        final Object lock = new Object();
        final AtomicBoolean goOn = new AtomicBoolean(true);

        new Thread(() -> {
            try {
                Socket sock = new Socket("team6.maddie480.ovh", 0xb00b);
                synchronized (lock) {
                    OutputStream os = sock.getOutputStream();
                    os.write(4);
                    os.write(8);
                    os.write(0);
                    os.flush();
                }

                join.addActionListener(u -> {
                    synchronized (lock) {
                        try {
                            sock.getOutputStream().write(serverList.getSelectedValue().getId());

                            int result = readSingleByte(sock.getInputStream());
                            if (result == 0) {
                                JOptionPane.showMessageDialog(window,
                                        "Could not join the selected server. Please try again!",
                                        "Whoops!",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                            handoff(sock, window, false);
                            goOn.set(false);
                        } catch (IOException e) {
                            ragequit(e, window);
                        }
                    }
                });

                create.addActionListener(u -> {
                    String name = JOptionPane.showInputDialog("Choose a name for your server.");
                    if (name == null) return;
                    if (name.isEmpty()) name = "<unnamed>";

                    byte[] rawName = name.getBytes(StandardCharsets.UTF_8);
                    if (rawName.length > 250) {
                        JOptionPane.showMessageDialog(window,
                                "This name is too long. Try again!",
                                "Whoops!",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    synchronized (lock) {
                        try {
                            OutputStream os = sock.getOutputStream();
                            os.write(1);
                            os.write(rawName.length);
                            os.write(rawName);
                            os.flush();

                            handoff(sock, window, true);
                            goOn.set(false);
                        } catch (IOException e) {
                            ragequit(e, window);
                        }
                    }
                });

                while (true) {
                    synchronized (lock) {
                        if (!goOn.get()) break;
                        refreshServerList(sock, serverList, join);
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                ragequit(e, window);
            }
        }).start();
    }

    private static void handoff(Socket sock, JFrame parent, boolean server) {
        JFrame statusBox = new JFrame("Team 6 Network (tm)");
        statusBox.setLayout(new GridLayout(server ? 3 : 2, 1));

        JLabel one = new JLabel("  Close the window to stop the program.  ");
        one.setHorizontalAlignment(SwingConstants.CENTER);
        statusBox.add(one);
        JLabel two = new JLabel("Waiting...");
        two.setHorizontalAlignment(SwingConstants.CENTER);
        statusBox.add(two);

        statusBox.pack();
        statusBox.setLocationRelativeTo(parent);
        statusBox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusBox.setVisible(true);

        parent.dispose();

        new Thread(() -> {
            try {
                if (server) {
                    P2PServer.launch(sock);
                } else {
                    P2PClient.launch(sock);
                }
            } catch (Exception e) {
                ragequit(e, statusBox);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (server) {
                    int ct = P2PServer.getClientCount();
                    two.setText(ct + " player" + (ct == 1 ? "" : "s") + " connected");
                } else {
                    two.setText(P2PClient.isGameConnected() ? "Game connected!" : "Waiting...");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ragequit(e, statusBox);
                }
            }
        }).start();
    }

    private static void ragequit(Exception e, JFrame window) {
        JOptionPane.showMessageDialog(window,
                "An error occurred while communicating with the server.\n" + e,
                "Whoops!",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private static void refreshServerList(Socket sock, JList<Server> list, JButton joinButton) throws IOException {
        Server selectedBefore = list.getSelectedValue();

        sock.getOutputStream().write(0);
        sock.getOutputStream().flush();

        InputStream is = sock.getInputStream();
        List<Server> servers = new ArrayList<>();

        while (true) {
            int serverId = readSingleByte(is);
            if (serverId == 0) break;

            int playerCount = readSingleByte(is);
            int nameSize = readSingleByte(is);

            byte[] name = new byte[nameSize];
            unstoppableRead(is, name);

            servers.add(new Server(new String(name, StandardCharsets.UTF_8), serverId, playerCount));
        }

        servers.sort(Comparator.comparing(Server::getId));

        list.setListData(servers.toArray(new Server[0]));

        if (selectedBefore != null) list.setSelectedValue(selectedBefore, false);
        if (selectedBefore != null && list.getSelectedValue() == null) joinButton.setEnabled(false);
    }

    static int readSingleByte(InputStream is) throws IOException {
        int i = is.read();
        if (i == -1) throw new IOException("Connection closed");
        return i;
    }

    static void unstoppableRead(InputStream is, byte[] dest) throws IOException {
        unstoppableRead(is, dest, 0, dest.length);
    }

    static void unstoppableRead(InputStream is, byte[] dest, int offset, int count) throws IOException {
        while (count > 0) {
            int readCount = is.read(dest, offset, count);
            if (readCount == -1) throw new IOException("Connection closed");
            offset += readCount;
            count -= readCount;
        }
    }
}
