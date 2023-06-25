import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.lang.*;
/**
 * @author seaside
 * 2023-05-18 23:54
 */
public class DataBase {
    static String get_usr_pwd(String now_name) //使用用户名返回用户密码
    {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            String out_name;
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("Use login_information");
            String sqlStr = String.format("select user_pwd from user_pwd_table where user_name = '%s'", now_name);
            rs = stmt.executeQuery(sqlStr);
            rs.next();
            if (rs.getString(1) == null) {
                throw new SQLException();
            }
            out_name = rs.getString("user_pwd");
            return out_name;
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return null;
        }
        finally {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    static String get_usr_score(String usr_name)//使用用户名，返回用户分数
    {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            String out_user_score;
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("Use login_information");
            String sqlStr = String.format("select user_score from user_pwd_table where user_name = '%s'", usr_name);

            rs = stmt.executeQuery(sqlStr);
            rs.next();
            if (rs == null) {
                throw new SQLException();
            }
            out_user_score = rs.getString("user_pwd");
            return out_user_score;
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return null;
        }
        finally {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    static boolean Add_usr(String usr_name,String usr_pwd)//添加新注册的用户，如果添加成功，则返回true，添加失败返回false
    {
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            String out_user_score;
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("use login_information");
            stmt.executeUpdate("create table if not exists `user_pwd_table`(" +
                    "`user_name` varchar(20)," +
                    "`user_pwd` varchar(20)," +
                    "`user_score` int," +
                    "`recent_playtime` bigint" + ")character set utf8mb4 collate utf8mb4_general_ci");
            if (usr_name_isExist(usr_name))
            {
                return false;
            }
            else
            {
                String sqlStr = String.format("insert into user_pwd_table(user_name, user_pwd, user_score, recent_playtime) values ('%s','%s',0,0)",usr_name,usr_pwd);
                stmt.executeUpdate(sqlStr);
                return true;
            }
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            System.out.println("");
            return false;
        }
    }
    static boolean usr_name_isExist(String usr_name)//判断该用户是否已经存在
    {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("Use login_information");
            String sqlStr = String.format("select user_name from user_pwd_table where user_name = '%s'", usr_name);
            rs = stmt.executeQuery(sqlStr);
            rs.next();
            if(rs.getString(1) == null)
            {
                throw new SQLException();
            }
            else
            {
                return true;
            }
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return false;
        }
        finally {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    static boolean update_usr_score(String usr_name,int new_score)//修改该用户的分数
    {
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            String out_user_score;
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("Use login_information");
            String sqlStr = String.format("update user_pwd_table set user_score = %d where user_name = '%s'",new_score,usr_name);
            stmt.executeUpdate(sqlStr);
            return true;
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return false;
        }
    }

    static String get_All_user_name()
    {
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        String all_user_name;
        StringBuilder sb = new StringBuilder();
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");

            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("USE login_information");
            ResultSet rs = stmt.executeQuery("SELECT user_name FROM user_pwd_table ORDER BY recent_playtime DESC");
            while(rs.next())
            {
                sb.append(rs.getString("user_name")).append("-");
            }
            all_user_name = sb.toString();
            if (all_user_name.endsWith("_")) {
                all_user_name = all_user_name.substring(0, all_user_name.length() - 1);
            }
            return all_user_name;
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return null;
        }
    }
    static boolean update_usr_playtime(String user_name, long recent_playtime)
    {
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not loading Drive");
        }
        Connection connect = null;
        try {
            // 下面这一行也是固定语句，getConnection括号里面的模式：
            // “jdbc:mysql://数据库的地址（mysql server 的地址）/数据库名字?（要mysql中操作的数据库的名字）”
            // 后面再 + 上用来登录的用户名和密码
            // 同时，使用这个函数，必须处理异常
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_information", "root", "1357188405whb.");
            if (connect != null) {
                System.out.println("Connect successfully!");
            }
            stmt = connect.createStatement();
            stmt.executeQuery("Use login_information");
            String sqlStr = String.format("update user_pwd_table set recent_playtime = %d where user_name = '%s'",recent_playtime,user_name);
            stmt.executeUpdate(sqlStr);
            return true;
        } catch (SQLException e) { // 这个getConnection 规定必须抛出异常
            return false;
        }
    }
}