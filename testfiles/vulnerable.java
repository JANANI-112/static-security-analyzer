import java.sql.*;

public class vulnerable {
    void login(Connection conn,String userid)throws Exception{
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery("select * from users where id="+userid);
    }
}
