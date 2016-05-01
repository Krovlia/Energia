package processing.app;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.apache.log4j.BasicConfigurator;

import static processing.app.I18n._;

public class GitUtils {
  private static Git git = null;
  
  static{
    BasicConfigurator.configure();
  }
  
  public static void checkoutRepository(){
    RemoteRepositoryUrlOkCancelDialog remoteRepositoryUrlOkCancelDialog = new RemoteRepositoryUrlOkCancelDialog(null, true);
    remoteRepositoryUrlOkCancelDialog.setLocationRelativeTo(null);
    remoteRepositoryUrlOkCancelDialog.setVisible(true);
    
    String repositoryUrl = remoteRepositoryUrlOkCancelDialog.getRepositoryUrl();
    if (repositoryUrl != null && !repositoryUrl.isEmpty()) {
      
      File targetDirectory = null;
      
      try{
        JFileChooser chooser = new JFileChooser();
    //chooser.setCurrentDirectory(new java.io.File("."));
    //chooser.setDialogTitle("choosertitle");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setDialogTitle("Select destination folder");
    
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      targetDirectory = chooser.getSelectedFile();
      }
      } catch (NullPointerException npe){
        PathChoosingOkCancelDialog pathChoosingOkCancelDialog = new PathChoosingOkCancelDialog(null, true);
        pathChoosingOkCancelDialog.setLocationRelativeTo(null);
        pathChoosingOkCancelDialog.setVisible(true);
        
        String path = pathChoosingOkCancelDialog.getLocalPath();
        
        if (path != null && !path.isEmpty()) {
          targetDirectory = new File(path);
        }
      }
      
    if (targetDirectory != null) {
      
      LoginPasswordOkCancelDialog loginPasswordOkCancelDialog = new LoginPasswordOkCancelDialog(null, true);
      loginPasswordOkCancelDialog.setLocationRelativeTo(null);
      loginPasswordOkCancelDialog.setVisible(true);
      
      String login = loginPasswordOkCancelDialog.getLogin();
      String password = loginPasswordOkCancelDialog.getPassword();
      
      if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
        try {
        Git git = 
            Git
            .cloneRepository()
            .setURI(repositoryUrl)
            .setDirectory(targetDirectory)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password))
            .call();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      }
    }
    }
  }
  
  public static void createOrOpenGitRepository(){
    File targetDirectory = null;
    try{
    JFileChooser chooser = new JFileChooser();
    //chooser.setCurrentDirectory(new java.io.File("."));
    //chooser.setDialogTitle("choosertitle");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      targetDirectory = chooser.getSelectedFile();
      }
    
    } catch (NullPointerException npe){
      PathChoosingOkCancelDialog pathChoosingOkCancelDialog = new PathChoosingOkCancelDialog(null, true);
      pathChoosingOkCancelDialog.setLocationRelativeTo(null);
      pathChoosingOkCancelDialog.setVisible(true);
      
      String path = pathChoosingOkCancelDialog.getLocalPath();
      
      if (path != null && !path.isEmpty()) {
        targetDirectory = new File(path);
      }
    }
    
    if (targetDirectory != null) {
      //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
      //System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
      
      //Git git = null;
      FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
      repositoryBuilder.addCeilingDirectory(targetDirectory);
      repositoryBuilder.findGitDir(targetDirectory);
      
      closeRepository();
      
      try{
          if(repositoryBuilder.getGitDir() == null) {
            git = Git.init().setDirectory(targetDirectory).call();
            
            JOptionPane.showMessageDialog(null,
                                          _("There are no existing repository. New repository will be created"),
                                          _("Warning"),
                                          JOptionPane.WARNING_MESSAGE);
          } else {
            git = new Git(repositoryBuilder.build());
          }
          //git.close();
      } catch (Exception ex){
        
      }
    }
  }
  
  public static void pull(){
    if (isRepositoryOpen()) {
      try {
        LoginPasswordOkCancelDialog loginPasswordOkCancelDialog = new LoginPasswordOkCancelDialog(null, true);
        loginPasswordOkCancelDialog.setLocationRelativeTo(null);
        loginPasswordOkCancelDialog.setVisible(true);
        
        String login = loginPasswordOkCancelDialog.getLogin();
        String password = loginPasswordOkCancelDialog.getPassword();
        
        if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
          git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password)).call();
        }
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public static void add(){
    if (isRepositoryOpen()) {
      /*JFileChooser chooser = new JFileChooser();
      chooser.setMultiSelectionEnabled(true);
      //chooser.setAcceptAllFileFilterUsed(false);
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        File[] files = chooser.getSelectedFiles();*/
        
        try{
          /*for (int i = 0; i < files.length; i++) {
          File file = files[i];*/
          //git.add().addFilepattern(file.getPath()).call();
          git.add().addFilepattern(".").call();
          //git.getRepository().getDirectory();
        //}
        }catch(Exception e){
          e.printStackTrace();
        }
     // }
    }
  }
  
  public static void commit(){
    if (isRepositoryOpen()) {
      CommitMessageOkCancelDialog commitMessageOkCancelDialog = new CommitMessageOkCancelDialog(null, true);
      commitMessageOkCancelDialog.setLocationRelativeTo(null);
      commitMessageOkCancelDialog.setVisible(true);
      
      String message = commitMessageOkCancelDialog.getMessage();
      
      if (message != null && !message.isEmpty()) {
        try{
        git.commit().setMessage(message).call();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      }
    }
  }
  
  /*public static void push(){
    if (isRepositoryOpen()) {
      LoginPasswordOkCancelDialog loginPasswordOkCancelDialog = new LoginPasswordOkCancelDialog(null, true);
      loginPasswordOkCancelDialog.setLocationRelativeTo(null);
      loginPasswordOkCancelDialog.setVisible(true);
      
      String login = loginPasswordOkCancelDialog.getLogin();
      String password = loginPasswordOkCancelDialog.getPassword();
      
      if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
      git
      .push()
      .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password))
      .setForce(true)
      .setPushAll();
      }
    }
  }*/
  
  public static void push(){ 
    if (isRepositoryOpen()) { 
      LoginPasswordOkCancelDialog loginPasswordOkCancelDialog = new LoginPasswordOkCancelDialog(null, true); 
      loginPasswordOkCancelDialog.setLocationRelativeTo(null); 
      loginPasswordOkCancelDialog.setVisible(true); 

      String login = loginPasswordOkCancelDialog.getLogin(); 
      String password = loginPasswordOkCancelDialog.getPassword(); 

      if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) { 
        try { 
          git 
          .push() 
          .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password)) 
          .setForce(true) 
          .setPushAll().call(); 
        } catch (Exception e) { 
          // TODO Auto-generated catch block 
          e.printStackTrace(); 
        } 
      } 
    } 
  }
  
  public static void closeRepository() {
	if (git != null) {
		git.close();
		git = null;
	}
  }
  
  public static boolean isRepositoryOpen(){
    return git != null;
  }
}
