package org.example.hakmana.model.userMngmnt;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.hakmana.model.DatabaseConnection;
import org.example.hakmana.view.scene.LoginPageController;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class SystemUser {
    private DatabaseConnection databaseConnection;
    private Connection conn;
    private ResultSet rs;

   // private OAuth2ForGmail auth;

    private boolean checkCode;
    private String userName;
    private String fullName;
    private String post;
    private String empId;
    private String pwd;
    private String email;
    private String phoneNum;
    private boolean isRemember;

    // Constructors
    public SystemUser() {
        setDatabaseConnection();
        setConn();
    }

    public SystemUser(String userName, String fullName, String post, String empId, String pwd, String email, String phoneNum) {
        this.userName = userName;
        this.fullName = fullName;
        this.post = post;
        this.empId = empId;
        this.pwd = pwd;
        this.email = email;
        this.phoneNum = phoneNum;
        setDatabaseConnection();
        setConn();
    }

    private void setDatabaseConnection() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    private void setConn() {
        conn = databaseConnection.getConnection();
    }

    public ResultSet getRs() {
        return rs;
    }

    public boolean isCheckCode() {
        return checkCode;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }

    public void setCheckCode(boolean checkCode) {
        this.checkCode = checkCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /*-----------User verification for password reset-------------*/
    //return reultset acording to the user mail
    public void setResultSet() throws SQLException {
        String query = "SELECT * FROM systemuser WHERE email = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, getEmail());
        rs = ps.executeQuery();
    }

    //check mail in database and return boolean val
    public boolean dbMailChecker(String mail) throws SQLException {
        setEmail(mail);
        setResultSet();
        return getRs().next();// return false if result set is empty else (rs not empty)place cursor to the 1 row.
    }

    //generate verification code
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new SecureRandom();
        for (int i = 0; i < 6; i++) { // Adjust code length as needed
            code.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return code.toString();
    }

    //store verification code under the username
    public void dbUpdate(String verificationCode) throws SQLException {
        String sql = "UPDATE systemuser SET verification_code = ?, code_expiry = DATE_ADD(NOW(), INTERVAL 15 MINUTE) WHERE email = ?"; // Update with expiry time
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, verificationCode);
        ps.setString(2, getEmail());
        ps.executeUpdate();
    }


    //send verification code to the email
    public void sendEmail(String verificationCode) throws Exception {
     //   auth=new OAuth2ForGmail();

        String fromEmail = "hakmanaedm@gmail.com"; // sender email
        Properties props = new Properties();

        //start new mail session
        Session session = Session.getDefaultInstance(props, null);

        //set the email
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));//set email sender
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(getEmail()));//set email rciever
        email.setSubject("Password Reset Verification Code");

        String emailBody = """
                You requested a password reset for your account.
                 
                Your verification code is:\s""" + verificationCode+"""
                \n
                Please enter this code in the app to reset your password.
                
                This code will expire in 15 minutes.
                
                If you did not request a password reset, please ignore this email.
                
                """;

        email.setText(emailBody);

      //  auth.sendMail(email);

    }

    //check verification with the db
    public boolean verifyWithDb(String code) throws SQLException {
        setResultSet();//have to run and assign resultset
        while (getRs().next()) {
            if (code.equals(rs.getString("verification_code"))) {
                // System.out.println("code matched");
                setCheckCode(true);
                return true;
            }
        }
        return false;
    }

    //change the password
    public boolean pswrdChanger(String newPsswrd) throws SQLException {
        if (isCheckCode()) {//check the verification code success or not
            setResultSet();
            if (getRs().next()) {
                setUserName(getRs().getString("userName"));
                setFullName(getRs().getString("fullName"));
                setPwd(getRs().getString("pwd"));
            }

            conn.setAutoCommit(false);

            String sql = "UPDATE systemUser SET pwd=SHA1(?) WHERE username=?"; // Update with expiry time
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPsswrd);
            ps.setString(2, getUserName());
            ps.executeUpdate();

            //Check confirmation when password change
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("password cahnge for the user" + getUserName());

            Optional<ButtonType> result = alert.showAndWait();//wait until button press in alert box

            //if alert box ok pressed execute sql quires
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // commit the sql quires
                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } else {
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }
        }
        return false;
    }

    public String getPassword(String tempUserName){
        try {
            String query = "SELECT * FROM systemUser WHERE userName = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, tempUserName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                String storedPassword = resultSet.getString("pwd");
                return storedPassword;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public String getIsRemUName(){
        try {
            String query = "SELECT userName FROM systemUser WHERE isRemember = TRUE;";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                String isRemUName = resultSet.getString("userName");
                return isRemUName;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return "";
    }

    public String[] getSystemUserDetails(){
        try {
            String query = "SELECT * FROM systemUser WHERE userName = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, LoginPageController.curentUser);
            ResultSet resultSet = preparedStatement.executeQuery();

            String[] userDetArr=new String[5];
            if (resultSet.next()) {
                userDetArr[0]= (resultSet.getString("fullName") == null) ? "" : resultSet.getString("fullName");
                userDetArr[1] = (resultSet.getString("post") == null) ? "" : resultSet.getString("post");
                userDetArr[2] = (resultSet.getString("email") == null) ? "" : resultSet.getString("email");
                userDetArr[3] = (resultSet.getString("phoneNum") == null) ? "" : resultSet.getString("phoneNum");
                userDetArr[4] = (resultSet.getString("empId") == null) ? "" : resultSet.getString("empId");
            }

            return  userDetArr;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public void dbIsRemember(){
        String query;
        PreparedStatement preparedStatement;
        try {
            if(isRemember){
                query = "UPDATE systemUser SET isRemember = TRUE WHERE userName = ?";
                preparedStatement=conn.prepareStatement(query);
                preparedStatement.setString(1, this.getUserName());
            }else{
                query = "UPDATE systemUser SET isRemember = FALSE;";
                preparedStatement=conn.prepareStatement(query);
            }
            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
