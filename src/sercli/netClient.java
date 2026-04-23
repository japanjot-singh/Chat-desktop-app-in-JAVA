package sercli;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class netClient extends Frame implements ActionListener {
    Label l1;
    static Socket s;
    static String Cname;

    Button bcon;
    Button bch;

    static boolean connFlag=false;
    boolean startFlag=false;
    netClient(){
        this.setLayout(new FlowLayout());

        l1=new Label("Start Conversation");
        bcon =new Button("Connect");
        bch=new Button("Chat");

        this.add(l1);
        this.add(bcon);
        this.add(bch);

        bcon.addActionListener(this);
        bch.addActionListener(this);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
    public void checkConn(){
        try{
            s=new Socket("localhost",4567);
            connFlag=true;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == bcon){
            checkConn();
            startFlag=true;
            if(connFlag ){
                repaint();
            }
        }
        if(ae.getSource()==bch){
            if(connFlag){
                chatClient cc=new chatClient(s);
                cc.setTitle(" client");
                cc.setSize(500,500);
                cc.setVisible(true);

            }
        }
    }
    public void paint(Graphics g){
        if(connFlag  && startFlag ){
            g.drawString("Connection Established ",230,230);
        }
    }
    public static void startC(String Cname){
        netClient f1=new netClient();
        netClient.Cname=Cname;
        f1.setTitle("Private Chat Client");
        f1.setSize(500,500);
        f1.setVisible(true);
    }

    public static void main(String args[]){
        startC(Cname);

    }
}
class chatClient extends Frame implements ActionListener,TextListener{
    Label ly,lo;

    TextArea tas,tar;

    Button se,re;


    Socket s;
    boolean emptyFlag=false;
    boolean textFlag=false;
    chatClient(Socket s){
        this.s=s;
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
                saveHistoryC();
                System.exit(0);
            }
        });
    }
    public void saveHistoryC() {
        try {
            Connection conh = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "Akaal-hi-akaal1699@");
            Statement stmth = conh.createStatement();
            System.out.println("Connected Successfully!");
            String sentText = tas.getText();
            String receivedText = tar.getText();

            String query1 = "UPDATE chatHistory SET sent='" + sentText +
                    "' WHERE username='" + netClient.Cname + "' AND type='client'";

            String query2 = "UPDATE chatHistory SET received='" + receivedText +
                    "' WHERE username='" + netClient.Cname + "' AND type='client'";

            stmth.executeUpdate(query1);
            stmth.executeUpdate(query2);

            System.out.println("Chat saved successfully!");

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    public void recieve() throws IOException
    {
        BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
        String sr;
        while((sr=br.readLine()) != null){
            tar.append(sr +"\n");
        }
    }
    public void send(String str) throws IOException{
        PrintWriter ps=new PrintWriter(s.getOutputStream(),true);
        ps.println(str);

    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == re){
            new Thread(() ->{
                try{
                    recieve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
        if(ae.getSource() ==se){
            String str=tas.getText();
            try{
                send(str);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    public void textValueChanged(TextEvent te){

    }
    public static void main(String args[]){

    }
}
