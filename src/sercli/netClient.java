package sercli;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
class netClient extends Frame implements ActionListener {
    Label l1;
    static Socket s;

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
    public static void startC(){
        netClient f1=new netClient();
        f1.setTitle("Private Chat Client");
        f1.setSize(500,500);
        f1.setVisible(true);
    }

    public static void main(String args[]){
        startC();

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
                System.exit(0);
            }
        });
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
