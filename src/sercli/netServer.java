package sercli;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;


class netServer extends Frame implements ActionListener {
    Label l1;

    Button bcon;
    Button bconvo;
    ServerSocket ss;
    Socket s;


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
                ChatNow cn=new ChatNow(s);
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
    public static void start(){
        netServer f1=new netServer();
        f1.setTitle("Private Chat");
        f1.setSize(500,500);
        f1.setVisible(true);
    }

    public static void main(String args[]){
        start();
    }
}
class ChatNow extends Frame implements ActionListener,TextListener{
    Label ly,lo;

    TextArea tas,tar;

    Button se,re;


    Socket s;
    boolean emptyFlag=false;
    boolean textFlag=false;
    ChatNow(Socket s){
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

    public void send(String str) throws IOException{
        PrintWriter ps=new PrintWriter(s.getOutputStream(),true);
        ps.println(str);

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

    }
}