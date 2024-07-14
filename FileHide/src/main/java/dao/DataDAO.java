// package dao;

// import db.MyConnection;
// import model.Data;

// import java.io.*;
// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;
// public class DataDAO {

//     
//     


// public static List<Data> getALLFiles(String email) throws SQLException {
//     Connection connection= MyConnection.getConnection();
//     PreparedStatement ps=connection.prepareStatement("select * from data where email=?  ");
//     ps.setString(1,email);
//     ResultSet rs=ps.executeQuery();
//     List<Data> files=new ArrayList<>();
//     while(rs.next()){
//         int id=rs.getInt(1);
//         String name=rs.getString(2);
//         String path=rs.getString(3);
//         files.add(new Data(id,name,path));
//     }
//     return files;
//  }


//  //hide
//     public static int hideFile(Data file) throws SQLException, IOException {
//     Connection connection=MyConnection.getConnection();
//     PreparedStatement ps=connection.prepareStatement(
//             "insert into data(name,path,email,bin_data) values(?,?,?,?)");
//     ps.setString(1,file.getFileName());
//     ps.setString(2,file.getPath());
//     ps.setString(3,file.getEmail());
//     File f=new File(file.getPath());
//         FileReader fr=new FileReader(f);
//         ps.setCharacterStream(4,fr,f.length());
//         int ans=ps.executeUpdate();
//         fr.close();
//         f.delete();
//         return ans;
//     }

//     //unhide
//     public static void unhide(int id) throws SQLException,IOException{
//     Connection connection=MyConnection.getConnection();
//     PreparedStatement ps=connection.prepareStatement("select path ,bin_data from data where id=?");
//     ps.setInt(1,id);
//     ResultSet rs=ps.executeQuery();
//     rs.next();
//     String path=rs.getString("path");
//         Clob c=rs.getClob("bin_data");
//         Reader r=c.getCharacterStream();
//         FileWriter fw=new FileWriter(path);
//         int i;
//         while((i=r.read())!=-1){
//             fw.write((char)i);

//         }
//         fw.close();
//         ps=connection.prepareStatement("delete from data where id=?");
//         ps.setInt(1,id);
//         ps.executeUpdate();
//         System.out.println("Successfully unhidden");

//     }
// }

//here is the updated code which can stores and retrieves any binary file of size 4GB

package dao;

import db.MyConnection;
import model.Data;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataDAO {
    
    // hide
    // unhide

    public static List<Data> getALLFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from data where email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        List<Data> files = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));
        }
        return files;
    }

    // hide
    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "insert into data(name, path, email, bin_data) values(?,?,?,?)");
        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());

        File f = new File(file.getPath());
        try (FileInputStream fis = new FileInputStream(f)) {
            ps.setBinaryStream(4, fis, (int) f.length());
            int ans = ps.executeUpdate();
            return ans;
        } finally {
            f.delete();
        }
    }

    // unhide
    public static void unhide(int id) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select path, bin_data from data where id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String path = rs.getString("path");
            Blob blob = rs.getBlob("bin_data");
            try (InputStream is = blob.getBinaryStream();
                 FileOutputStream fos = new FileOutputStream(path)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            ps = connection.prepareStatement("delete from data where id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Successfully unhidden");
        }
    }
}

