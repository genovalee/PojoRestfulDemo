package client;

import client.db.entity.T0nj0547d;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsertDbT0nj0547d {
    private Connection conn = null;
    private List<T0nj0547d> t0nj0547d = new ArrayList<T0nj0547d>();
    private String bussrfno = "";
    private String regofc = "";

    public InsertDbT0nj0547d() {
    }

    public InsertDbT0nj0547d(Connection conn, List<T0nj0547d> t0nj0547d, String bussrfno, String regofc) {
        this.conn = conn;
        this.t0nj0547d = t0nj0547d;
        this.bussrfno = bussrfno;
        this.regofc = regofc;
    }

    protected void InsertDbT0nj0547d() throws SQLException {
        Date date = new Date(System.currentTimeMillis());
        Timestamp today = new Timestamp(date.getTime());
        String sql = "INSERT INTO t0nj0547d(bussrfno, regofc, it, salit, salitcomt, txdat) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (T0nj0547d dt : t0nj0547d) {
            ps.setString(1, bussrfno);
            ps.setString(2, regofc);
            ps.setString(3, dt.getIt());
            ps.setString(4, dt.getSalit());
            ps.setString(5, dt.getSalitcomt());
            ps.setTimestamp(6, today);
            ps.execute();
        }
        ps.close();
    }
}
