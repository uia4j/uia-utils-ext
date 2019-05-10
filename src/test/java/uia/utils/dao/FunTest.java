package uia.utils.dao;

import org.junit.Test;

import uia.utils.dao.hana.Hana;
import uia.utils.dao.pg.PostgreSQL;

public class FunTest {

	@Test
	public void test() throws Exception {
        Database db1 = new PostgreSQL("localhost", "5432", "tmddb", "tmd", "tmd");
        Database db2 = new Hana();
        System.out.println(db1.generateCreateTableSQL(db1.selectTable("zzt_exec_job", false)));
        System.out.println(db1.generateCreateTableSQL(db1.selectTable("zzt_exec_task", false)));
        System.out.println(db1.generateCreateTableSQL(db1.selectTable("zzt_qtz_clock", false)));
        System.out.println(db1.generateCreateTableSQL(db1.selectTable("zzt_tx_key", false)));
        System.out.println(db1.generateCreateTableSQL(db1.selectTable("zzt_tx_table", false)));
        db1.close();
        db2.close();
        

	}
}
