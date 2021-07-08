import java.sql.DriverManager
import java.sql.Connection
import oracle.jdbc.pool.OracleDataSource

class oracle_connection {
  def connOracle : Unit = {
    val query = """SELECT * FROM TABLE1"""
    val connection : Connection = null
    val oracleUser = "ORACLE USER"
    val oraclePassword = "ORACLE USER PASSWORD"
    val oracleURL = "jdbc:oracle:thin:@//$HOST:$PORT/$SID"
    val ods = new OracleDataSource()
    ods.setUser(oracleUser)
    ods.setURL(oracleURL)
    ods.setPassword(oraclePassword)
    val con = ods.getConnection()
    val statement = con.createStatement()
    statement.setFetchSize(1000)      // important
    val  resultSet : java.sql.ResultSet = statement.executeQuery(query)
  }
}
