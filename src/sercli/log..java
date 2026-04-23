package sercli;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;

class log extends Frame implements ItemListener,ActionListener
{
    Checkbox cs,cc;
    CheckboxGroup cbg;

    Label la,lc;

    log(){

        this.setLayout(new FlowLayout());

        cbg=new CheckboxGroup();
        cs=new Checkbox("Log-In",cbg,false);
        cc=new Checkbox("Create Account",cbg,false);

        la=new Label("Already have an account");
        lc=new Label("New User");

        this.add(la);
        this.add(cs);
        this.add(lc);
        this.add(cc);

        cs.addItemListener(this);
        cc.addItemListener(this);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

    }
    public static void main(String args[]){
        log j1=new log();
        j1.setTitle("Sign-In");
        j1.setSize(400,400);
        j1.setVisible(true);
    }

    public void itemStateChanged(ItemEvent ie){
        if(ie.getSource() == cc){
            FrameCRN fc=new FrameCRN("jdbc:mysql://localhost:3306/test","root","Akaal-hi-akaal1699@");
            fc.setTitle("Create Account");
            fc.setSize(400,400);
            fc.setVisible(true);
        }
        if(ie.getSource() == cs){
            FrameLON fl=new FrameLON("jdbc:mysql://localhost:3306/test","root","Akaal-hi-akaal1699@");
            fl.setTitle("Log-In");
            fl.setSize(400,400);
            fl.setVisible(true);
        }
    }
    public void actionPerformed(ActionEvent ae){

    }
}
class FrameCRN extends Frame implements ActionListener{
    Label lu,lp,lt;
    TextField tu,tp,tt;

    String Tuser,Tpass,Ttype;

    static String  url,user,pass;

    Button bs;

    boolean foundUser=false, foundType=false;

    String cch,cch2;

    FrameCRN(String url,String user,String pass){

        this.url=url;
        this.user=user;
        this.pass=pass;

        this.setLayout(new FlowLayout());

        lu=new Label("Enter Username");
        lp=new Label("Enter Password");
        lt=new Label("Enter type");

        tu=new TextField(20);
        tp=new TextField(20);
        tt=new TextField(20);
        tp.setEchoChar('*');

        bs=new Button("Save");

        this.add(lu);
        this.add(tu);
        this.add(lp);
        this.add(tp);
        this.add(lt);
        this.add(tt);
        this.add(bs);

        tu.addActionListener(this);
        tp.addActionListener(this);
        bs.addActionListener(this);
        tt.addActionListener(this);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
    public void actionPerformed(ActionEvent ae){

        if(ae.getSource() == bs){
            Tuser=tu.getText();
            Tpass=tp.getText();
            Ttype=tt.getText();

            try{
                Connection con=DriverManager.getConnection(url,user,pass);
                Statement stmt=con.createStatement();
                System.out.println("Connected Successfully!");
                ResultSet rs1= stmt.executeQuery("SELECT username,type FROM accountData");
                while (rs1.next()){
                    cch=rs1.getString("username");
                    cch2=rs1.getString("type");
                    if(Tuser.equals(cch) && Ttype.equals(cch2)){
                        foundUser=true;
                        foundType=true;
                        repaint();
                        return;

                    }
                }
                if(!foundUser && !foundType){
                    addData(Tuser,Tpass,Ttype,stmt);
                    if(Ttype.equalsIgnoreCase("server")){
                        netServer.start();
                    }
                    else{
                        netClient.startC();
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void paint(Graphics g){
        if(foundUser && foundType){
            g.drawString("Already Taken",130,130);
        }

    }

    public void addData(String au,String ap,String at,Statement stmt){

        String query="INSERT INTO accountData VALUES('"+au+"','"+ap+"','"+at+"')";
        try{
            int NewEntry=stmt.executeUpdate(query);
        }
        catch (SQLException se){
            se.printStackTrace();
        }
    }
    public static void main(String args[]){

    }
}

class FrameLON extends Frame implements ActionListener{
    Label llu,llp,llt;
    TextField ttu,ttp,ttt;
    Button bln;
    String lurl,luser,lpass;
    String ch,ps,ty;
    boolean fol=false;
    boolean attempt=false;
    FrameLON(String lurl,String luser,String lpass){
        this.lurl=lurl;
        this.luser=luser;
        this.lpass=lpass;

        this.setLayout(new FlowLayout());

        llu=new Label("Enter Username");
        llp=new Label("Enter Password");
        llt=new Label("Enter Type");

        ttu=new TextField(20);
        ttp=new TextField(20);
        ttp.setEchoChar('*');
        ttt=new TextField(20);

        bln=new Button("Next");

        this.add(llu);
        this.add(ttu);
        this.add(llp);
        this.add(ttp);
        this.add(llt);
        this.add(ttt);
        this.add(bln);

        ttu.addActionListener(this);
        ttp.addActionListener(this);
        ttp.addActionListener(this);
        bln.addActionListener(this);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == bln){
            String lu=ttu.getText();
            String lp=ttp.getText();
            String lt=ttt.getText();
            fol=false;
            attempt=true;

            try{
                Connection con=DriverManager.getConnection(lurl,luser,lpass);
                Statement stmt=con.createStatement();

                String query="SELECT username,password,type FROM accountData";
                ResultSet rs= stmt.executeQuery(query);

                while(rs.next()){
                     ch=rs.getString("username");
                     ps=rs.getString("password");
                     ty=rs.getString("type");
                    if(lu.equals(ch) && lp.equals(ps) && lt.equals(ty)){
                        fol=true;
                        break;

                    }

                }
                if(fol){
                    if(ty.equalsIgnoreCase("server")){
                        netServer.start();
                    }
                    else{
                        netClient.startC();
                    }

                }
                else{
                    repaint();
                }

            }
            catch (SQLException se){
                se.printStackTrace();

            }
        }
    }
    public void paint(Graphics g){
        if(attempt && !fol){
            g.drawString("Invalid Details",130,130);
        }

    }
}