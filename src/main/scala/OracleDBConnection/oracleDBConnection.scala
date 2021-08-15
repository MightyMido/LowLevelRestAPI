package OracleDBConnection


import java.sql.{CallableStatement, Connection, DriverManager, ResultSet, Statement, Types}


object oracleDBConnection {
  final val driver = "oracle.jdbc.OracleDriver"
  final val url = "jdbc:oracle:thin:@192.168.122.1:1521:orclcdb"
  final val username = "testDB"
  final val password = "Ma1234"
  case class connection(url:String,username:String,password:String)
  {def create_connection:Connection = DriverManager.getConnection(url, username, password)}
  val cxnn = connection(url,username,password)
  case class total_Terminal (terminal_key:String , guild_code:String , guild_desc:String , mega_guild_desc:String)
  case class terminal(terminalKey:String , guild_Code:String)
}

class oracleDBConnection
{
  import OracleDBConnection.oracleDBConnection._
  def getResults(resultSet: ResultSet , resultList: List[terminal] = List()):
      List[terminal] =
      {
        if (resultSet.next() == true)
        {
          val guildCode = resultSet.getString("trmrchtyp")
          val TerminalKey = resultSet.getString("terminalkey")
          val rltLst = resultList :+ terminal(terminalKey = TerminalKey , guild_Code = guildCode)
          getResults(resultSet, rltLst)
        }
        else {
          resultSet.close()
          resultList
        }
      }

  def execute_sql_query(terminalKeyList:List[String]): List[terminal] =
    {
      val terminal_string = terminalKeyList.mkString("','")
      val query = s"select trmrchtyp, terminalkey from terminals_tbl where terminalkey in ('$terminal_string')"
      val connection = cxnn.create_connection
      val resultSet = connection.createStatement().executeQuery(query)
      val results = getResults(resultSet)
      connection.close()
      results
    }

  def createCallableStatement(ProcName:String , number_of_elements:Int , InputList : List[String]) : CallableStatement =
    {
      val l = InputList.length
      val q = List.fill(number_of_elements)("?")
      val sql = s"{call $ProcName(${q.mkString(",")})}"
      val connection = cxnn.create_connection
      val callableStatement = connection.prepareCall(sql)
      for (i <- 1 to l)
      {
        callableStatement.setString(i, InputList(i-1))
      }
      for (j <- l + 1 to number_of_elements)
      {
        callableStatement.registerOutParameter(j , Types.VARCHAR)
      }
      callableStatement.executeUpdate()
      callableStatement
    }

  def get_terminal_info(terminalID:String,PSPKey:String):total_Terminal = {
    val qu = createCallableStatement("getTerminalGuild", 6 , List(terminalID,PSPKey))
    val guild_code = qu.getString(3)
    val guild_desc = qu.getString(4)
    val mega_guild_desc = qu.getString(5)
    val TerminalKey = qu.getString(6)
    total_Terminal(TerminalKey, guild_code, guild_desc, mega_guild_desc)
  }

  //try {
  //      // make the connection
  //      Class.forName(driver)
  //      // create the statement, and run the select query
  //    } catch {
  //      case e => e.printStackTrace
  //    }
  //    connection.close()

}
