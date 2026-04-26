package sercli;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


class netServer extends Frame implements ActionListener {
    Label l1;

    Button bcon;
    Button bconvo;
    ServerSocket ss;
    Socket s;
    static String uname;
    static String clientName;


    boolean connFlag=false;
    boolean chatFlag=false;
    netServer(){
        this.setLayout(new FlowLayout());

        l1=new Label("Start Conversation");
        bcon=new Button("Connect");
        bconvo=new Button("Chat");

        this.add(l1);
        this.add(bcon);
        this.add(bconvo);

        bcon.addActionListener(this);
        bconvo.addActionListener(this);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
    public  void connection() throws IOException{
        ss=new ServerSocket(4567);
        s=ss.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        clientName = br.readLine();
        connFlag=true;
    }
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bcon){
            try{
                connection();
                repaint();

            } catch (IOException e) {
                e.printStackTrace();
            }
            chatFlag=true;

        }

        if (ae.getSource() == bconvo){
            if(chatFlag){
                ChatNow cn=new ChatNow(s,clientName);
                cn.setTitle("server");
                cn.setSize(500,500);
                cn.setVisible(true);
            }
        }
    }
    public void paint(Graphics g){
        if(connFlag){
            g.drawString("Connection Established",230,230);
        }
    }
    public static void start(String name)
    {
        netServer.uname =name;
        netServer f1=new netServer();
        f1.setTitle("Private Chat");
        f1.setSize(500,500);
        f1.setVisible(true);
    }

    public static void main(String args[]){
        start(uname);
    }
}
class ChatNow extends Frame implements ActionListener,TextListener{
    String clientName;
    Label ly,lo;

    TextArea tas,tar;

    Button se,re;


    Socket s;
    boolean emptyFlag=false;
    boolean textFlag=false;
    ChatNow(Socket s,String clientName){
        this.s=s;
        this.clientName=clientName;
        this.setLayout(new FlowLayout());
        ly=new Label("Send");
        lo=new Label("Recieve");

        tas=new TextArea(10,5);
        tar=new TextArea(10,5);

        se=new Button("Send");
        re=new Button("Recieve");

        this.add(ly);
        this.add(tas);
        this.add(se);
        this.add(lo);
        this.add(tar);
        this.add(re);

        tas.addTextListener(this);
        tar.addTextListener(this);
        se.addActionListener(this);
        re.addActionListener(this);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                saveHistory();
                System.exit(0);
            }
        });
    }

    public void send(String str) throws IOException{
        PrintWriter ps=new PrintWriter(s.getOutputStream(),true);
        ps.println(str);

    }
    public void saveHistory() {
        try {
            Connection conh = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "Akaal-hi-akaal1699@");
            Statement stmth = conh.createStatement();
            System.out.println("Connected Successfully!");
            String sentText = tas.getText();
            String receivedText = tar.getText();

            String queryServer = "INSERT INTO chatHistory (username, type, sent, received, time) VALUES ('"
                    + netServer.uname + "', 'server', '" + tas.getText() + "', '" + tar.getText() + "', NOW())";

            String queryClient = "INSERT INTO chatHistory (username, type, sent, received, time) VALUES ('"
                    + clientName + "', 'client', '" + receivedText + "', '" + sentText + "', NOW())";

            stmth.executeUpdate(queryServer);
            stmth.executeUpdate(queryClient);
            System.out.println("Chat saved successfully!");

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == se){
            if(tas.getText().isEmpty()){
                emptyFlag=true;
                repaint();
            }

            if(!emptyFlag){
                String te=tas.getText();
                try{
                    send(te);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
        if(ae.getSource() ==re){
            new Thread(() ->{
                try {
                    recieve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }
    public void recieve() throws IOException{
        BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
        String sr;
        while((sr=br.readLine()) != null){
            tar.append(sr +"\n");
        }
    }
    public void paint(Graphics g){
        if(emptyFlag){
            g.drawString("please type message",250,250);
        }
    }
    public void textValueChanged(TextEvent te){

    }
    public static void main(String args[]){
        System.out.println(netClient.Cname);


    }
}