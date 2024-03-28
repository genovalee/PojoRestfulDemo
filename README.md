# POJO RESTFul Api 程式撰寫、測試 
## 建立 MD 資料表

MASTER TABLE:
<pre style="color:#000000;background:#ffffff;">
Create table t0nj0547 
( 
  BUSSRFNO     VARCHAR2(8),           --商業統一編號  
  BUSSNM       VARCHAR2(255),         --商業名稱  
  COSTSID      VARCHAR2(2),           --公司狀態代碼  
  COSTSIDCOMT  VARCHAR2(255),         --公司狀態說明
  REGOFC       VARCHAR2(15),          --登記機關  
  REGOFCCOMT   VARCHAR2(255),         --登記機關說明  
  BUSSLOCATION VARCHAR2(512),         --商業所在地, 
  TXDAT        DATE, 
  constraint PK_T0NJ0547 primary key (BUSSRFNO, REGOFC) 
); 
</pre>
DETAIL TABLE

<pre style="color:#000000;background:#ffffff;">
Create table t0nj0547d 
( 
  BUSSRFNO  VARCHAR2(8),               --商業統一編號  
  REGOFC    VARCHAR2(255),             --登記機關  
  IT        VARCHAR2(4),               --營業項目序號  
  SALIT     VARCHAR2(10),              --營業項目代號  
  SALITCOMT VARCHAR2(2000),            --營業項目名稱 
  TXDAT     DATE, 
  constraint PK_T0NJ0547D primary key (BUSSRFNO, REGOFC, SALIT), 
  constraint FK_T0NJ0547D foreign key (BUSSRFNO, REGOFC) 
  references T0NJ0547 (BUSSRFNO, REGOFC) on delete cascade 
); 
</pre>

## 增設Table物件，建立Java Class並宣告payload對應的變數，並產生get/set method

<pre style="color:#000000;background:#ffffff;">
public class T0nj0547d{
    private String bussrfno;
    private String regofc;
    private String it;
    private String salit;
    private String salitcomt;
}	
</pre>

<pre style="color:#000000;background:#ffffff;">
public class T0nj0547{
    private String bussrfno;
    private String bussnm;
    private String costsid;
    private String costsidcomt;
    private String orgnTyNm;
    private String regofc;
    private String regofccomt;
    private String busslocation;
    private List&lt;T0nj0547d&gt; t0nj0547d;
}
</pre>
## 增設Java Interface作為service post 實作之用
### post使用addT0nj0547方法

<pre style="color:#000000;background:#ffffff;">
interface T0nj0547Service {
    Response addT0nj0547(T0nj0547 t47);
}
</pre>
## 增設Java Class實作上面宣告的Interface
### Override addT0nj0547方法
<pre style="color:#000000;background:#ffffff;">
public class T0nj0547ServiceImp implements T0nj0547Service {
    private Connection conn
    private final String ACCESSKEY = "eHh4eHh4Onl5eXl5eQ";
    private String resp = "";

    public T0nj0547ServiceImp() throws NamingException, SQLException {
        super();
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/XXXXDS");
        conn = ds.getConnection();
        conn.setAutoCommit(true);
    }

    @Override
    public Response addT0nj0547(T0nj0547 t47) {
      try {
            if (authString == null || authString.isEmpty()) {
                resp = "{\"error\":\"Accesskey is invalid\"}";
                return Response.status(Response.Status.UNAUTHORIZED)
                               .entity(resp).build();
            }
            if (!ACCESSKEY.equals(authString)) {
                resp = "{\"error\":\"Accesskey is invalid\"}";
                return Response.status(Response.Status.UNAUTHORIZED)
                               .entity(resp).build();
            }
            // 將payload轉換為T0nj0547物件
            ObjectMapper objectMapper = new ObjectMapper();
            T0nj0547 t0nj0547 = objectMapper.readValue(payload, T0nj0547.class);

            InsertDbT0nj0547 insT0nj0547 = new InsertDbT0nj0547(conn, t0nj0547);
            //insert master data
            insT0nj0547.InsertDbT0nj0547();
            List&lt;T0nj0547d&gt; t0nj0547d = t0nj0547.getBusinessItemOld();
            if (t0nj0547d.size() > 0) {
                InsertDbT0nj0547d insT0nj0547d = new InsertDbT0nj0547d(conn, t0nj0547d,t0nj0547.getBussrfno(),t0nj0547.getRegofc());
                //insert detail datas
                insT0nj0547d.InsertDbT0nj0547d();
            }
            resp = "{\"message\":\"資料已新增\",\"status\":\"" + Response.Status.CREATED + "\"}";
            return Response.status(Response.Status.CREATED)
                           .entity(resp).build();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            String resp = "{\"message\":\"" + ex.getMessage().replace("\"", "\'") + "\"}";
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(resp).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String resp = "{\"message\":\"" + e.getMessage().replace("\"", "\'") + "\"}";
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(resp).build();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("連線DB發生錯誤");
                }
            }
        }
    }
}
</pre>

## 增設兩個Insert Table的類別(InsertDbT0nj0547、InsertDbT0nj0547d)

<pre style="color:#000000;background:#ffffff;">
public class InsertDbT0nj0547 {
    private Connection conn = null;
    private T0nj0547 t47;

    public InsertDbT0nj0547() {
    }

    public InsertDbT0nj0547(Connection conn, T0nj0547 t47) {
        this.conn = conn;
        this.t47 = t47;
    }

    public void InsertDbT0nj0547() throws SQLException {
        Date date = new Date(System.currentTimeMillis());
        Timestamp today = new Timestamp(date.getTime());
        String sql =
            "INSERT INTO t0nj0547(bussrfno, bussnm, costsid, costsidcomt, regofc, regofccomt, busslocation, txdat) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, t47.getBussrfno());
        ps.setString(2, t47.getBussnm());
        ps.setString(3, t47.getCostsid());
        ps.setString(4, t47.getCostsidcomt());
        ps.setString(5, t47.getRegofc());
        ps.setString(6, t47.getRegofccomt());
        ps.setString(7, t47.getBusslocation());
        ps.setTimestamp(8, today);
        ps.execute();
        ps.close();
    }
}
</pre>

<pre style="color:#000000;background:#ffffff;">
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
</pre> 
## 將T0nj0547ServiceImp Create restful web service
![create Resuful Service](https://github.com/genovalee/PojoRestfulDemo/blob/master/Client/src/client/db/Image_010.png)
## 測試資料payload
<pre style="color:#000000;background:#ffffff;">
{
    "President_No": "01465625",
    "Business_Name": "XXXXX行",
    "Business_Current_Status": "01",
    "Business_Current_Status_Desc": "核准設立",
    "Business_Organization_Type_Desc": "獨資",
    "Agency": "123456789A",
    "Agency_Desc": "XX市政府經濟發展局",
    "Business_Address": "XXXXXX中港路XX巷15號",
    "Business_Item_Old": [
        {
            "Business_Seq_No": "1",
            "Business_Item": "F107200",
            "Business_Item_Desc": "XXXX原料批發業"
        },
        {
            "Business_Seq_No": "2",
            "Business_Item": "F113030",
            "Business_Item_Desc": "XXXX儀器批發業"
        }
    ]
}
</pre> 
![post test](https://github.com/genovalee/PojoRestfulDemo/blob/master/Client/src/client/db/Image_002.png)
![queryData](https://github.com/genovalee/PojoRestfulDemo/blob/master/Client/src/client/db/Image_003.png)
