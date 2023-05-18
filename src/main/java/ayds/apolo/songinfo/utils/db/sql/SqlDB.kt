package ayds.apolo.songinfo.utils.db.sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

abstract class SqlDB {
    private lateinit var connection: Connection
    protected abstract val dbUrl: String

    protected fun openConnection() {
        try {
            connection = DriverManager.getConnection(dbUrl)
        } catch (e: SQLException) {
            println("Could not create connection $dbUrl $e")
        }
    }

    protected fun tableCreated(table: String): Boolean {
        try {
            val tables = connection.metaData?.getTables(null, null, table, null)
            return tables?.next() ?: false
        } catch (e: SQLException) {
        }
        return false
    }

    protected val statement: Statement?
        get() {
            var statement: Statement? = null
            try {
                statement = connection.createStatement()
                statement.queryTimeout = 30
            } catch (e: SQLException) {
                println("Create statement error " + e.message)
            }
            return statement
        }

    protected fun closeConnection() {
        try {
            connection.close()
        } catch (e: SQLException) {
            System.err.println(e)
        }
    }
}