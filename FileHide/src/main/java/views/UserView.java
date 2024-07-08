package views;

import dao.DataDAO;
import model.Data;

import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private String email;
    UserView(String email){
        this.email=email;
    }
    public void home(){
        do{
            System.out.println("Welcome "+this.email);
            System.out.println("Press 1 to show hidden files");
            System.out.println("Press 2 to hide a new file");
            System.out.println("Press 3 to unhide a file ");
            System.out.println("Press 0 to exit");
            Scanner sc=new Scanner(System.in);
            int ch=Integer.parseInt(sc.nextLine());
           switch(ch){
               case 1:{
                   try {
                       List<Data> files= DataDAO.getALLFiles(this.email);
                       System.out.println("ID -File Name");
                       for(Data file:files){
                           System.out.println(file.getId() + " - "+file.getFileName());
                       }
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }}
                   break;
               case 2:{
                   System.out.println("Enter the file path");
                   String path=sc.nextLine();
                   File f=new File(path);
                   Data file=new Data(0,f.getName(),path,this.email);
                   try {
                       DataDAO.hideFile(file);
                   } catch (SQLException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }}
                   break;
               case 3:{
                   List<Data> files= null;
                   try {
                       files = DataDAO.getALLFiles(this.email);

                   System.out.println("ID -File Name");
                   for(Data file:files){
                       System.out.println(file.getId() + " - "+file.getFileName());
                   }
                   System.out.println("Enter the id of file to unhide ");
                   int id=Integer.parseInt(sc.nextLine());
                   boolean isValidId=false;
                   for(Data file:files){
                       if(file.getId()==id){
                           isValidId=true;
                           break;
                       }
                   }
                   if(isValidId){
                       DataDAO.unhide(id);
                   }else{
                       System.out.println("Wrong Id");
                   }
                   } catch (SQLException  e) {
                       e.printStackTrace();
                   }
                   catch (IOException e){
                       e.printStackTrace();
                   }}
                   break;
               case 0:System.exit(0);
               break;
           }
        }while(true);

    }
}
