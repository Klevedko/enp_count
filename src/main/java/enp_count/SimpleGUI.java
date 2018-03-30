package enp_count;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;

import static enp_count.App.url;
import static enp_count.App.user;

class SimpleGUI extends JFrame {

    public static JButton buttonGo = new JButton("Start Report");
    public static JButton buttonStop = new JButton("Stop Report");
    private JButton buttonSQL = new JButton("SQL");
    private JButton buttonSAVE = new JButton("Сохранить");
    private JTextField input1 = new JTextField("", 5);
    private JTextField input2 = new JTextField("71140", 5);
    private JTextArea input3;
    private JTextField password = new JTextField("N0vusadm", 1);
    private JTextField inputNameFile = new JTextField(5);
    private JLabel label1 = new JLabel("  На дату( Например 2017/01/01):");
    private JLabel label2 = new JLabel("  Номер региона");
    private JLabel label3 = new JLabel("  SQL-query:");
    public static JLabel labelStatus = new JLabel("                         Статус:");
    private JLabel labelNameFile = new JLabel("  Сохранить в: ");
    public Thread thread;

    public SimpleGUI(String sql) {
        super("Список ЕНП. Численность ЗЛ в ЦС ЕРЗ");
        Date dateNow = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        input1.setText(simpleDateFormat.format(dateNow));
        inputNameFile.setText("Список ЕНП. Численность ЗЛ в ЦС ЕРЗ на " + input1.getText().replaceAll("/", ".") + " по территории " + input2.getText());
        this.input3 = new JTextArea(sql);
        this.setAlwaysOnTop(true);
        this.setBounds(220, 400, 750, 170);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        buttonStop.setEnabled(false);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(6, 2, 1, 2));
        container.add(this.label1);
        container.add(this.input1);
        container.add(this.label2);
        container.add(this.input2);
        container.add(this.label3);
        container.add(this.buttonSQL);
        container.add(this.labelNameFile);
        container.add(this.inputNameFile);
        container.add(this.buttonGo);
        container.add(this.labelStatus);
        container.add(this.buttonStop);

        input1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                input1.invalidate();
                inputNameFile.setText("Список ЕНП. Численность ЗЛ в ЦС ЕРЗ на " + input1.getText().replaceAll("/", ".") + " по территории " + input2.getText());
                inputNameFile.invalidate();
            }
        });

        input2.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                input2.invalidate();
                inputNameFile.setText("Список ЕНП. Численность ЗЛ в ЦС ЕРЗ на " + input1.getText().replaceAll("/", ".") + " по территории " + input2.getText());
                inputNameFile.invalidate();
            }
        });

        this.buttonSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleGUI r = new SimpleGUI(4, App.sql);
                r.setVisible(true);
            }
        });
        this.buttonGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        App.go(SimpleGUI.this.getter1(), SimpleGUI.this.getter5(), SimpleGUI.this.getter2());
                    }
                });
                SimpleGUI.this.setAlwaysOnTop(false);
                buttonGo.setEnabled(false);
                buttonStop.setEnabled(true);
                labelStatus.setText("                         Статус: идет формирование");
                labelStatus.invalidate();
                thread.start();
            }
        });
        this.buttonStop.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime.getRuntime().exec("java -jar enp_count-1.0-SNAPSHOT-jar-with-dependencies.jar");
                    System.exit(0);
                } catch (Exception ee) {
                    e.getActionCommand();
                }
                labelStatus.setText("                         Статус: формирование прекращено");
                labelStatus.invalidate();
                buttonGo.setEnabled(true);
                buttonStop.setEnabled(false);

            }
        });
    }

    public SimpleGUI(int w, String sql) {

        super("sql");
        final JTextArea input3 = new JTextArea(App.sql);
        this.setBounds(100, 100, 630, 630);
        JPanel panel = new JPanel();
        panel.setLayout((LayoutManager) null);
        this.setAlwaysOnTop(true);
        input3.setBounds(0, 0, 600, 550);
        this.buttonSAVE.setBounds(0, 550, 600, 30);
        this.buttonSAVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                App.sql = input3.getText().toString();
                input3.setText(App.sql);
                input3.invalidate();
                SimpleGUI.this.dispose();
            }
        });
        panel.add(input3, "North");
        panel.add(this.buttonSAVE, "South");

        this.getContentPane().add(panel);
        this.setPreferredSize(new Dimension(550, 600));
    }

    public SimpleGUI(String pas, boolean conRes) {
        super("Пароль пользователя");
        this.setBounds((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2,
                350, 60);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(1, 2, 1, 2));
        container.add(this.password);
        container.add(this.buttonSAVE);
        this.buttonSAVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkConnect(password.getText());
            }
        });
        password.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkConnect(password.getText());
                }}
            });
    }
public void checkConnect(String pas) {
    try {
        App.con = DriverManager.getConnection(url, user, pas);
        SimpleGUI.this.dispose();
        SimpleGUI app = new SimpleGUI(App.sql);
        app.setVisible(true);
    } catch (Exception conExep) {
        System.out.println(conExep.getLocalizedMessage());
        JOptionPane.showMessageDialog((Component) null, conExep.getLocalizedMessage().toString());
    }
}


    public String getter1() {
        return this.input1.getText().toString();
    }

    public String getter2() {
        return this.input2.getText().toString();
    }

    public String getter5() {
        return this.inputNameFile.getText().toString();
    }

    public JTextField getPassword() {
        return password;
    }

    public void setPassword(JTextField password) {
        this.password = password;
    }
}
