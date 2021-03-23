package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.JOptionPane;


public class Setting {
	static Connection connection = null;
	static Statement statement = null;
	static int progress = 0;
	
	static {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			statement = connection.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new LoadingPanel().setVisible(true);

		execute("drop database if exists `2021지방_2`");
		
		execute("CREATE SCHEMA IF NOT EXISTS `2021지방_2` DEFAULT CHARACTER SET utf8 ;");
		
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`user` (\r\n"
				+ "  `u_No` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  `u_id` VARCHAR(20) NULL DEFAULT NULL,\r\n"
				+ "  `u_Pw` VARCHAR(20) NULL DEFAULT NULL,\r\n"
				+ "  `u_Name` VARCHAR(20) NULL DEFAULT NULL,\r\n"
				+ "  `u_Date` DATE NULL DEFAULT NULL,\r\n"
				+ "  `u_Phone` VARCHAR(50) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`u_No`))\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`weddinghall` (\r\n"
				+ "  `wh_No` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  `wh_Name` VARCHAR(20) NULL DEFAULT NULL,\r\n"
				+ "  `wh_Add` VARCHAR(50) NULL DEFAULT NULL,\r\n"
				+ "  `wh_People` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `wh_Price` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`wh_No`))\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`weddingtype` (\r\n"
				+ "  `wty_No` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  `wty_Name` VARCHAR(15) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`wty_No`))\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`mealtype` (\r\n"
				+ "  `m_No` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  `m_Name` VARCHAR(5) NULL DEFAULT NULL,\r\n"
				+ "  `m_Price` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`m_No`))\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`division` (\r\n"
				+ "  `wh_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `wty_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `m_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  INDEX `fk_division_mealtype_idx` (`m_No` ASC) VISIBLE,\r\n"
				+ "  INDEX `fk_division_weddingtype1_idx` (`wty_No` ASC) VISIBLE,\r\n"
				+ "  INDEX `fk_division_weddinghall1_idx` (`wh_No` ASC) VISIBLE,\r\n"
				+ "  CONSTRAINT `fk_division_mealtype`\r\n"
				+ "    FOREIGN KEY (`m_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`mealtype` (`m_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE,\r\n"
				+ "  CONSTRAINT `fk_division_weddingtype1`\r\n"
				+ "    FOREIGN KEY (`wty_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`weddingtype` (`wty_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE,\r\n"
				+ "  CONSTRAINT `fk_division_weddinghall1`\r\n"
				+ "    FOREIGN KEY (`wh_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`weddinghall` (`wh_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE)\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`payment` (\r\n"
				+ "  `p_No` VARCHAR(4) NOT NULL,\r\n"
				+ "  `wh_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `p_People` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `wty_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `m_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `i_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `p_Date` DATE NULL DEFAULT NULL,\r\n"
				+ "  `u_No` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`p_No`),\r\n"
				+ "  INDEX `fk_payment_mealtype1_idx` (`m_No` ASC) VISIBLE,\r\n"
				+ "  INDEX `fk_payment_weddingtype1_idx` (`wty_No` ASC) VISIBLE,\r\n"
				+ "  INDEX `fk_payment_user1_idx` (`u_No` ASC) VISIBLE,\r\n"
				+ "  INDEX `fk_payment_weddinghall1_idx` (`wh_No` ASC) VISIBLE,\r\n"
				+ "  CONSTRAINT `fk_payment_mealtype1`\r\n"
				+ "    FOREIGN KEY (`m_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`mealtype` (`m_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE,\r\n"
				+ "  CONSTRAINT `fk_payment_weddingtype1`\r\n"
				+ "    FOREIGN KEY (`wty_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`weddingtype` (`wty_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE,\r\n"
				+ "  CONSTRAINT `fk_payment_user1`\r\n"
				+ "    FOREIGN KEY (`u_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`user` (`u_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE,\r\n"
				+ "  CONSTRAINT `fk_payment_weddinghall1`\r\n"
				+ "    FOREIGN KEY (`wh_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`weddinghall` (`wh_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE)\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("CREATE TABLE IF NOT EXISTS `2021지방_2`.`invitation` (\r\n"
				+ "  `i_No` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  `p_No` VARCHAR(4) NULL DEFAULT NULL,\r\n"
				+ "  `i_From` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  `i_To` INT(11) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (`i_No`),\r\n"
				+ "  INDEX `fk_inivitation_payment1_idx` (`p_No` ASC) VISIBLE,\r\n"
				+ "  CONSTRAINT `fk_inivitation_payment1`\r\n"
				+ "    FOREIGN KEY (`p_No`)\r\n"
				+ "    REFERENCES `2021지방_2`.`payment` (`p_No`)\r\n"
				+ "    ON DELETE CASCADE\r\n"
				+ "    ON UPDATE CASCADE)\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "DEFAULT CHARACTER SET = utf8;");
		
		execute("set global local_infile=1");

		execute("use 2021지방_2");
		execute("drop user if exists 'user'@'%'");
		execute("create user 'user'@'%' identified by '1234'");
		execute("grant select, insert, delete, update on `2021지방_2`.* to 'user'@'%';");
		execute("flush privileges");

		for (String table : "user,weddinghall,weddingtype,mealtype,division,payment,invitation".split(",")) {
			execute("load data local infile './제3과제 datafile/" + table + ".txt' into table " + table
					+ " fields terminated by '\t' lines terminated by '\n' ignore 1 lines");
		}
		
	}

	static void execute(String sql) {
		try {
			statement.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
