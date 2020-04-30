package util;

import java.sql.SQLException;

public class TestMain {
	public void test(BaseDao baseDao){
		String sql = "select * from zcf_user.user_table as tut INNER JOIN haoyi_mahjong.login_log_table as hgu ON tut.id=hgu.userid";
		baseDao.executeAll(sql, null);
		try {
			System.out.println(baseDao.resultSet.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new TestMain().test(new BaseDao());
	}
}
