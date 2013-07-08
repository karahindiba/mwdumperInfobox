package org.mediawiki.extractor;

import org.mediawiki.importer.Infobox;
import org.mediawiki.importer.SqlServerStream;
import org.mediawiki.importer.SqlWriter;
import org.mediawiki.importer.SqlWriter15;

import java.io.IOException;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 08.07.2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class InfoBoxAnalyser {
    private static ResultSet textTable = null;
    public static Connection connection = null;
    public static SqlWriter15 sqlWriter;

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        System.out.println("Bağlantı sağlanıyor...");
        Infobox infobox = null;
        ConnectDB();
        sqlWriter = openWriter();
        while (getTextTable().next()) {
            try {
                infobox = new Infobox();
                infobox.Text = getTextTable().getString("infobox");
                infobox.rev_id = getTextTable().getInt("rev_id");
                infobox.TextToProperty();
                System.out.println( " doğum yer = " + infobox.Propetys.get("p_dogum_yer"));
                writeDB(infobox);

            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            } catch (Exception e5) {
                e5.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + infobox.rev_id);
            }
        }
        try {
            sqlWriter.writeEndWiki();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("HATA!!! son commit" );
        }
    }

    public static ResultSet getTextTable() throws SQLException {
        if (textTable == null) {
            Statement statement = connection.createStatement();
            // TODO burayı incele
            // Deneme amaçlı şimdilik 10 tane ile çalış
            // Şimdilik sadece infobox olanlara bak
            textTable = statement.executeQuery("select rev_id,infobox from infobox");
            // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id and rev_id='9086735'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected static void writeDB(Infobox infobox) {
        try {
            sqlWriter.updateInfoboxAnalyse(infobox);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("HATA!!!" + infobox.rev_id);
        }
    }

    private static SqlWriter15 openWriter() {
        SqlServerStream sqlStream = new SqlServerStream(connection);
        return new SqlWriter15(new SqlWriter.MySQLTraits(), sqlStream, "");
    }
}