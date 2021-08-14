package OracleDBConnection

import java.sql.DriverManager

object oracleDBConnection extends App {
      // connect to the database named "mysql" on the localhost
      val driver = "oracle.jdbc.OracleDriver"
      val url = "jdbc:oracle:thin:@192.168.122.1:1521:orclcdb"
      val username = "testDB"
      val password = "Ma1234"
      // there's probably a better way to do this
      var connection = DriverManager.getConnection(url, username, password)
      try {
        // make the connection
        Class.forName(driver)
        // create the statement, and run the select query
        val statement = connection.createStatement()
        val terminalkey = "000096411017"
        val pspkey = "1005"
        val query = s"select * from terminals_tbl where terminalkey= '$terminalkey'"
        val resultSet = statement.executeQuery(query)
        while ( resultSet.next() )
        {
          val guild = resultSet.getString("trmrchtyp")
          val pspkey = resultSet.getString("pspkey")
          println("(guild,pspkey) = (" + guild + ", " + pspkey + ")")
        }
      } catch {
        case e => e.printStackTrace
      }
      connection.close()
}
