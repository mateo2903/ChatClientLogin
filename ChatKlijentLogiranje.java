package hr.vsite.hr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatKlijentLogiranje extends JFrame {

    JPanel contentPane;
    JTextArea textArea;
    JTextPane textPane;
    ChatClient config;

    /**
     * Launch the application.
     */
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatKlijentLogiranje frame = new ChatKlijentLogiranje();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ChatKlijentLogiranje(){
        log.info("Constructor of MyClientChatApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 350);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);

        JButton btnNewButton = new JButton("Send");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnNewButton, -5, SpringLayout.SOUTH, contentPane);
        btnNewButton.setMaximumSize(new Dimension(70, 20));
        btnNewButton.setPreferredSize(new Dimension(70, 20));

        btnNewButton.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Sending message with 'SEND BUTTON'!");
                SendText();
            }
        });
        sl_contentPane.putConstraint(SpringLayout.EAST, btnNewButton, -5, SpringLayout.EAST, contentPane);
        contentPane.add(btnNewButton);

        textPane = new JTextPane();

        textPane.addKeyListener((KeyListener) new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    e.consume();
                    log.info("Sending message with 'ENTER'!");
                    SendText();
                }
            }
        });
        textPane.setPreferredSize(new Dimension(20, 20));

        textPane.setBorder((Border) new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        sl_contentPane.putConstraint(SpringLayout.WEST, textPane, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, textPane, -5, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, textPane, -5, SpringLayout.WEST, btnNewButton);
        contentPane.add(textPane);

        JButton btnNewButton_1 = new JButton("Config");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnNewButton_1, -30, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnNewButton, 5, SpringLayout.SOUTH, btnNewButton_1);
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    config = new ChatClient();
                    config.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    config.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnNewButton_1.setPreferredSize(new Dimension(60, 25));

        sl_contentPane.putConstraint(SpringLayout.WEST, btnNewButton_1, 5, SpringLayout.EAST, textPane);
        contentPane.add(btnNewButton_1);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sl_contentPane.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scroll, -5, SpringLayout.NORTH, textPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, scroll, -5, SpringLayout.WEST, btnNewButton);
        contentPane.add(scroll);

        connect();
        log.info("Constructor Done!");
    }

    private void SendText()
    {
        if(!textPane.getText().equals(""))
        {
            log.info("Sending text...");
            textArea.append(textPane.getText() + "\n");
            textPane.setText("");
            send();
            log.info("Text sent.");
        }
        else {
            log.warn("No text to send.");
        }
    }

    Socket soc = null;
    PrintWriter pw = null;
    BufferedReader br = null;
    private static final Logger log = LoggerFactory.getLogger(ChatKlijentLogiranje.class);
    private void connect(){
        try {
            UserConfig.loadParams();
            soc = new Socket(UserConfig.getHost(), UserConfig.getPort());
            pw = new PrintWriter(soc.getOutputStream());

            br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String response;
            try {
                response = br.readLine();
                if (textArea.getText().length()>0)
                    textArea.append("\n");
                textArea.append(response);
                textArea.append("\n");
                textPane.setText(null);
            } catch (IOException e) {
                log.error("Greska kod čitanja inicijalnog odgovora", e);
                JOptionPane.showMessageDialog(textArea, "Greška kod èitanja inicijalnog odgovora", "Greška!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (UnknownHostException e) {
            log.error("Nepoznati host", e);
            this.dispose();
        } catch (IOException e) {
            log.error("IO iznimka", e);
            this.dispose();
        }
    }

    private void send(){
        pw.println(textPane.getText());
        if (pw.checkError())
        {
            JOptionPane.showMessageDialog(textArea, "Greška kod slanja poruke",
                    "Greška!", JOptionPane.ERROR_MESSAGE);
        }
        String response;
        try {
            response = br.readLine();
            if (textArea.getText().length()>0)
                textArea.append("\n");
            textArea.append(response);
            textPane.setText(null);
        } catch (IOException e) {
            log.error("Greška kod citanja", e);
            JOptionPane.showMessageDialog(textArea, "Greška kod citanja odgovora",
                    "Greška!", JOptionPane.ERROR_MESSAGE);
        }
    }
}

