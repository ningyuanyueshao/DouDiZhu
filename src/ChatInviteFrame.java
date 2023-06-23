import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.util.HashMap;

public class ChatInviteFrame {
    JFrame mainFrame;
    JPanel chatPanel;
    JPanel invitePanel;
    JTextArea chatTextArea;
    JTextField inputTextField;
    JScrollPane playersListScrollPane;
    DefaultListModel playerNameList;
    JLabel currentPlayerNameLabel;
    String currentSelectedPlayer = "";
    HashMap<String, String> chatRecord = new HashMap<>();
    ClientConnectThread clientConnectThread;
    String username;
    int roomID;
    PrintWriter printWriter;
    String[] allUsernames = null;

    ChatInviteFrame(PrintWriter printWriter) {
        this.printWriter = printWriter;
        mainFrame = new JFrame("Chat and Invite");
        mainFrame.setSize(800, 500);
        mainFrame.setLocation(300, 100);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        mainFrame.setLayout(new GridBagLayout());

        GridBagConstraints constraints1 = getGridBagConstraints(0, 0, 1, 1, 0.0, 1.0);
        GridBagConstraints constraints2 = getGridBagConstraints(1, 0, 4, 1, 1.0, 1.0);

        invitePanel = new JPanel();
        mainFrame.add(invitePanel, constraints1);
        initInvitePanel();

        chatPanel = new JPanel();
        mainFrame.add(chatPanel, constraints2);
        initChatPanel();

        mainFrame.setVisible(true);
    }

    void initChatPanel() {
        chatPanel.setLayout(new BorderLayout());

        // 添加聊天消息文本区域
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false); // 设置为不可编辑
        JScrollPane chatTextScrollPane = new JScrollPane(chatTextArea);
        chatPanel.add(chatTextScrollPane, BorderLayout.CENTER);

        // 创建输入框
        inputTextField = new JTextField();
        inputTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 创建发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 添加底部Panel，放置输入框和发送按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(inputTextField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 添加显示当前选择玩家文本标签
        currentPlayerNameLabel = new JLabel();
        currentPlayerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chatPanel.add(currentPlayerNameLabel, BorderLayout.NORTH);

    }

    void initInvitePanel() {
        invitePanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints1 = getGridBagConstraints(0, 0, 1, 1, 1.0, 0.0);
        GridBagConstraints constraints2 = getGridBagConstraints(0, 1, 1, 8, 1.0, 1.0);
        GridBagConstraints constraints3 = getGridBagConstraints(0, 9, 1, 1, 1.0, 0.0);

        playerNameList = new DefaultListModel();
        JList playersList = new JList(playerNameList);
        playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    changeChattingPlayer();
                }
            }
        });

        playersListScrollPane = new JScrollPane(playersList);
        invitePanel.add(playersListScrollPane, constraints2);

        // 添加邀请按钮
        JButton inviteButton = new JButton("邀请");
        inviteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invitePlayer();
            }
        });
        invitePanel.add(inviteButton, constraints3);

        // 添加刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPlayer();
            }
        });
        invitePanel.add(refreshButton, constraints1);

        //refreshPlayer(); //初始时先行刷新

    }

    public void getInfo(ClientConnectThread clientConnectThread, String username, int roomID) {
        this.clientConnectThread = clientConnectThread;
        this.username = username;
        this.roomID = roomID;
    }

    private void sendMessage() {
        String message = inputTextField.getText();

        if (currentSelectedPlayer.equals("")) {
            JOptionPane.showMessageDialog(mainFrame, "未选择玩家！");
            return;
        }
        if (message.equals("")) {
            JOptionPane.showMessageDialog(mainFrame, "输入内容为空！");
            return;
        }

        chatRecord.put(currentSelectedPlayer, chatRecord.get(currentSelectedPlayer) + ("我: " + message + "\n"));
        chatTextArea.setText(chatRecord.get(currentSelectedPlayer));
        inputTextField.setText(""); // 清空输入框

        clientConnectThread.giveChatItemsToServer(currentSelectedPlayer, "s:".concat(message)); //发送的消息前加上"s:"使服务端识别

    }

    public void receiveMessage(String sourcePlayer, String message) {
        chatRecord.put(currentSelectedPlayer, chatRecord.get(currentSelectedPlayer) + (sourcePlayer + ": " + message + "\n"));
        chatTextArea.setText(chatRecord.get(currentSelectedPlayer));
    }

    private void invitePlayer() {
        System.out.println("向服务器发送邀请信息");
        printWriter.println("o:"+username+"-"+roomID+"-"+currentSelectedPlayer);
    }

    private void refreshPlayer() {
        System.out.println("向服务器获取所有玩家信息");
        printWriter.println("u:");
        playerNameList.clear();
        while(true){
            try
            {
                Thread.sleep(10);
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            if(allUsernames != null)
                break;
        }
        for (int i = 0; i < allUsernames.length; i++) {
            playerNameList.addElement(allUsernames[i]); //重新添加所有刷新后在线玩家

            if (!chatRecord.containsKey(allUsernames[i])) //刷新出未出现过的玩家时
                chatRecord.put(allUsernames[i], "");
        }

        if (playerNameList.size() == 0)
            playerNameList.addElement("（没有玩家在线）");

        allUsernames = null;
    }

    private void changeChattingPlayer() {
        // 更新currentSelectedPlayer的值
        Object[] selectedPlayers = ((JList<?>) playersListScrollPane.getViewport().getView()).getSelectedValues();
        try {
            currentSelectedPlayer = (String) selectedPlayers[0];
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        // 更新currentPlayerNameLabel显示的内容
        currentPlayerNameLabel.setText(currentSelectedPlayer);
        // 更新chatTextArea显示的内容
        chatTextArea.setText(chatRecord.get(currentSelectedPlayer));

    }

    static GridBagConstraints getGridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.weightx = weightx;
        constraints.weighty = weighty;

        return constraints;

    }
}
