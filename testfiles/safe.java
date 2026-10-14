
import java.sql.*;

public class safe {
    void login(Connection conn,String userid)throws Exception{
        PreparedStatement ps=conn.prepareStatement("select * from users where id=?");
        ps.setString(1, userid);
        ResultSet rs=ps.executeQuery();
    }
}
