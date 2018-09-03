package hu.tegzes.tamas.notifier

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


fun main(args: Array<String>) {
    Database.connect("jdbc:sqlite:/media/LinuxData/sqlite_db/test.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Cities, Residents)

        val stPeteId = Cities.insert {
            it[name] = "St. Petersburg"
        } get Cities.id

        val budapestId = Cities.insert {
            it[name] = "Budapest"
        } get Cities.id

        if (stPeteId != null) {

            Residents.insert {
                it[name] = "Vladimir"
                it[city] = stPeteId
            }

            Residents.insert {
                it[name] = "Vladimirovic"
                it[city] = stPeteId
            }

            Residents.insert {
                it[name] = "Vladimirovicovic"
                it[city] = stPeteId
            }
        }
        if (budapestId != null) {
            Residents.insert {
                it[name] = "Kovács"
                it[city] = budapestId
            }

            Residents.insert {
                it[name] = "Kovácsné"
                it[city] = budapestId
            }

            Residents.insert {
                it[name] = "ifj. Kovács"
                it[city] = budapestId
            }
        }

        println("Id of St. Petersburg: $stPeteId")

        for (row in (Cities innerJoin Residents).selectAll()) {
            println(row)
        }

    }
}

object Cities : IntIdTable() {
    val name = varchar("name", 50)
}

object Residents : IntIdTable() {
    val city = entityId("city", Cities) references Cities.id
    val name = text("name")
}