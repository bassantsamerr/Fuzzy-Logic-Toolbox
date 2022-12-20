import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

class gui implements ActionListener {
    public String in, out;
    public JTextField tf1 , tf2;
    public gui(){
        JFrame frame = new JFrame("Fuzzy Logic Tool box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel l1=new JLabel("Enter input File path");
        l1.setBounds(50,70, 100,30);
        tf1 = new JTextField(""); //
        tf1.setBounds(50,100, 200,30);

        JLabel l2=new JLabel("Enter output File path");
        l2.setBounds(50,130, 100,30);
        tf2 = new JTextField(""); //
        tf2.setBounds(50,160, 200,30);
        JButton b2=new JButton("enter");
        b2.setBounds(180,210,80,30);
        b2.addActionListener(this);

        //Adding Components to the frame.
        frame.add(tf1);
        frame.add(tf2);
        frame.add(l1);
        frame.add(l2);
        frame.add(b2);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        in=tf1.getText();
        out=tf2.getText();
        try {
            FuzzyLogicToolbox.runProgram(in,out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}