package enp_count;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import enp_count.SimpleGUI;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

public class App {
    public static String sql = "-- Дата запроса \n" +

            "DECLARE @pDate date = CAST(? AS date)\n" +
            "\n" +
            "--****** Script for SelectTopNRows command from SSMS  *****\n" +
            "SELECT DISTINCT ENP\n" +
            "FROM [FOMS_CENTER_REVISION].[dbo].[xdrPolicyOpenData] AS pod (NOLOCK)\n" +
            "WHERE InsuranceTerritory = ? \n" +
            "AND  @pDate BETWEEN pod.PolicyActualFrom  AND  ISNULL(pod.PolicyActualTo,@pDate)\n" +
            "AND  pod.PolicyActualFrom != ISNULL(pod.PolicyActualTo,DATEADD(DAY,1,pod.PolicyActualFrom)) ";

    public static String url = "jdbc:jtds:sqlserver://10.255.160.75;databaseName=FOMS_CENTER_REVISION;integratedSecurity=true;Domain=GISOMS";
    public static String user = "Apatronov";
    public static String password = "N0vusadm7";
    public static Connection con = null;
    public static long start;
    public static long end;

    public static void main(String[] args) {
        SimpleGUI app = new SimpleGUI(sql);
        app.setVisible(true);
    }

    public static void go(String date1, String FileName, String territory) {
        try {
            con = DriverManager.getConnection(url, user, password);
            PrepareInsert(con, FileName, date1, territory);
            long diff = (end - start) / 1000;
            SimpleGUI.labelStatus.setText("                         Статус: готово");
            SimpleGUI.buttonStop.setEnabled(false);
            SimpleGUI.buttonGo.setEnabled(true);
            JOptionPane.showMessageDialog((Component) null, "Отчет\n" + FileName + ".xlsx \nсоздан в " + System.getProperty("user.dir") + "\\Отчеты ЕНП\\" + "\nДлительность = " + diff + "сек.", "Output", -1);

            SimpleGUI.labelStatus.invalidate();

        } catch (Exception var8) {
            try {
                JOptionPane.showMessageDialog((Component) null, var8.getMessage().toString());
                //Runtime.getRuntime().exec("java -jar enp_count-1.0-SNAPSHOT-jar-with-dependencies.jar");
                main(new String[0]);
                System.exit(0);
            }catch (Exception restarter){
                System.out.println("couldn't restart Application");}

        }
    }

    public static void PrepareInsert(Connection con, String FileName, String date1, String territory) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, date1);
            statement.setString(2, territory);
            String SheetName;
            SheetName = FileName.toString();
            BeginInsert(wb, SheetName, statement, FileName);

        } catch (Exception var11) {
            JOptionPane.showMessageDialog((Component) null, var11.getLocalizedMessage().toString());
        }
    }

    public static void BeginInsert(Workbook wb, String SheetName, PreparedStatement statement, String FileName) {
        start = System.currentTimeMillis();
        try {
            byte row = 0;
            ResultSet rs = null;
            try {
                rs = statement.executeQuery();
            } catch (Exception s) {
                JOptionPane.showMessageDialog((Component) null, s.getLocalizedMessage().toString());
                SimpleGUI.buttonGo.setEnabled(true);
                SimpleGUI.buttonStop.setEnabled(false);
            }

            new Date();
            new File(System.getProperty("user.dir") + "\\Отчеты ЕНП\\").mkdirs();
            String output = System.getProperty("user.dir") + "\\Отчеты ЕНП\\" + FileName + ".xlsx";

            Sheet list = wb.createSheet(SheetName);
            Row dataRow = list.createRow(row);

            try {
                Cell cell;
                FileOutputStream fileout;

                // блок шапки в отчете
                for (int x = 0; x < rs.getMetaData().getColumnCount(); ++x) {
                    cell = dataRow.createCell(x);
                    cell.setCellValue(rs.getMetaData().getColumnName(x + 1));
                }

                fileout = new FileOutputStream(output);
                for (int var22 = row + 1; rs.next(); ++var22) {

                    dataRow = list.createRow(var22);

                    for (int x = 0; x < rs.getMetaData().getColumnCount(); ++x) {
                        cell = dataRow.createCell(x);
                        cell.setCellValue(rs.getString(x + 1));
                    }

                }
                wb.write(fileout);
                fileout.close();

            } catch (Exception var20) {
                JOptionPane.showMessageDialog((Component) null, var20.getLocalizedMessage().toString());

            }
            end = System.currentTimeMillis();
        } catch (Exception var21) {
            JOptionPane.showMessageDialog((Component) null, var21.getLocalizedMessage().toString());
        }

    }
}
