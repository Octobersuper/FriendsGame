package com.zcf.framework.gameCenter.mahjong.backservlet;

import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import com.zcf.framework.gameCenter.mahjong.service.Mahjong_Socket;
import org.apache.commons.lang3.StringUtils;
import util.BaseDao;
import util.Mahjong_Util;
import util.UtilClass;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * @author zhanghu
 * @createtime 2019年5月23日 上午9:40:33
 */
public class BackUserDao {
	private BaseDao baseDao = null;

	private String sql = "SELECT * from game_system where backuserid=?";

	public BackUserDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 查询是否拥有查看后台管理员列表的权限
	 * 
	 * @return
	 */
	private int getRole(int backuserid) {
		String sql = "select * from game_system where backuserid=?";
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启超管权限
	 * 
	 * @param backuserids
	 * @return
	 */
	public int onrole(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		String sql = "update game_system SET role=1,menuone=1,menutwo=1,menuthree=1,menufour=1,menufive=1,menusix=1,menuseven=1,menueight=1,menunine=1,menuten=1,eleven=1,twelve=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭超管权限
	 * 
	 * @param backuserids
	 * @return
	 */
	public Object offrole(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		String sql = "update game_system SET role=0,menuone=0,menutwo=0,menuthree=0,menufour=0,menufive=0,menusix=0,menuseven=0,menueight=0,menunine=0,menuten=0,eleven=0,twelve=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 开启游戏统计权限
	 * 
	 *            目标用户
	 * @param backuserids
	 *            操作的用户
	 * @return
	 */
	public Object onmenuone(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menuone=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭游戏统计权限
	 * 
	 * @param backuserids
	 * @return
	 */
	public Object offmenuone(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menuone=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	// 获取游戏统计权限
	private int selectMenuOne(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuone");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启金币统计权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenutwo(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menutwo=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭金币统计权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenutwo(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menutwo=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	// 获取金钱统计权限
	private int selectMenuTwo(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menutwo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启用户列表权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuthree(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menuthree=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭用户列表权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuthree(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menuthree=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	// 获取用户列表权限
	private int selectMenuThree(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuthree");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启管理房间权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuFour(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menufour=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭管理房间权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuFour(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menufour=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	// 获取管理房间权限
	private int selectMenuFour(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menufour");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启俱乐部管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuFive(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menufive=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭俱乐部管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuFive(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menufive=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	// 获取俱乐部管理权限
	private int selectMenuFive(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menufive");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启商品配置权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuSix(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menusix=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭商品配置权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuSix(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menusix=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取商品配置权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectMenuSix(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menusix");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启创建房间配置权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuSeven(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menuseven=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭创建房间配置权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuSeven(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menuseven=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取创建房间配置权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectMenuSeven(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuseven");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启公告管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuEight(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menueight=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭公告管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuEight(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menueight=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取公告管理权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectMenuEight(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menueight");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启管理员列表权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuNine(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menunine=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭管理员列表权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuNine(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menunine=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取管理员列表权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectMenuNine(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menunine");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启权限管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onmenuTen(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET menuten=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭权限管理权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offmenuTen(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,menuten=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取权限管理列表
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectMenuTen(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuten");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启登陆日志权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onEleven(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET eleven=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭登陆日志权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offEleven(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,eleven=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取登录日志权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectEleven(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("eleven");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开启添加后台权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object onTwelve(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET twelve=1 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 关闭添加后台权限
	 * 
	 * @param backuserid
	 * @param backuserids
	 * @return
	 */
	public Object offTwelve(int backuserid, int backuserids) {
		// 先判断是否拥有超管权限---有超管才可以赋权超管
		int i = getRole(backuserid);
		if (i != 1) {
			return 110;// 无超管权限
		}
		if (backuserid == backuserids) {
			return 111;// 请勿操作自己
		}
		if (backuserids == 1) {
			return 101;// 请勿对系统管理员进行操作
		}
		String sql = "update game_system SET role=0,twelve=0 WHERE backuserid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { backuserids });
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取添加后台权限
	 * 
	 * @param backuserid
	 * @return
	 */
	private int selectTwelve(String backuserid) {
		baseDao.executeAll(sql, new Object[] { backuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("twelve");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取玩家列表
	 * 
	 * @param pagenum
	 * @return
	 */
	public Map<String, Object> getuser(int pagenum, String backuserid, Map<String, Object> rmap) {
		// 查看用户菜单是否有权限
		int backuserids = selectMenuThree(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 没有权限访问
			return rmap;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Object[] object = new Object[rmap.size() + 1];
		StringBuffer stringb = new StringBuffer("SELECT * FROM user_table AS gu where 1=1 ");
		int index = 0;
		if (rmap.get("userid") != null) {
			stringb.append("and gu.userid=? ");
			object[index] = Integer.parseInt(rmap.get("userid").toString());
			index++;
		}
		if (rmap.get("nickname") != null) {
			stringb.append("AND gu.nickname like ? ");
			object[index] = "%" + rmap.get("nickname").toString() + "%";
			index++;
		}
		stringb.append(" ORDER BY date DESC  LIMIT ?,10 ");
		object[index] = (pagenum - 1) * 10;
		String executeAll = baseDao.executeAll(stringb.toString(), object);
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getUserList");
				int userid = Integer.parseInt(map.get("userid").toString());
				map.put("zuserid", userid);
				if (Public_State.clients.get(map.get("openid").toString()) != null) {
					map.put("online", 1);
				} else {
					map.put("online", 0);
				}
				list.add(map);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", getCount());
			map.put("list", list);
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询用户总条数
	 */
	public int getCount() {
		String sql = "SELECT count(1) as counts FROM user_table ";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
				int count = baseDao.resultSet.getInt("counts");
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 保存充值记录 流水账
	 */
	public void insertgamerecord(int userid, int currentbrand, int cheatbrand, Double blackbets, Double redbets,
			Double flowerbets, Double diamondsbets, Double kingbets, Double totalbets, Double score, Double date) {
		String sql = "insert into game_record(userid,currentbrand,cheatbrand,blackbets,redbets,flowerbets,diamondsbets,kingbets,totalbets,score,date) values(?,?,?,?,?,?,?,?,?,?,NOW());";
		baseDao.executeUpdate(sql, new Object[] { userid, currentbrand, cheatbrand, blackbets, redbets, flowerbets,
				diamondsbets, kingbets, totalbets, score, date });
	}

	/**
	 * 查看充值记录
	 * 
	 * @param pagenum
	 * @param zuserid
	 * @return
	 */
	public Object getrechargerecord(Integer pagenum, String backuserid, String zuserid, String startTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断充值记录的权限
		int backuserids = selectMenuFour(backuserid);
		if (backuserids == 0) {
			map.put("state", 101);
			return map;
		}
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(startTime)) {
			String sql = "select * from game_recharge where userid=? and (type = 1 or type = 2) ORDER BY date desc LIMIT ?,10";
			String executeAll = baseDao.executeAll(sql, new Object[] { zuserid, (pagenum - 1) * 10 });
			try {
				while (baseDao.resultSet.next() && "success".equals(executeAll)) {
					Map<String, Object> rmap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getrecord");
					list.add(rmap);
				}
				map.put("total", getrechargenum(zuserid));
				map.put("list", list);
				return map;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String startTimes = startTime + " 00:00:00";
			String endTimes = startTime + " 23:59:59";
			String sql = "select * from game_recharge where userid=? and (type = 1 or type = 2) AND date > ? AND date < ? ORDER BY date desc LIMIT ?,10";
			String executeAll = baseDao.executeAll(sql,
					new Object[] { zuserid, startTimes, endTimes, (pagenum - 1) * 10 });
			try {
				while (baseDao.resultSet.next() && "success".equals(executeAll)) {
					Map<String, Object> rmap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getrecord");
					list.add(rmap);
				}
				map.put("total", getrechargenum(zuserid));
				map.put("list", list);
				return map;
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	/**
	 * 查看积分记录
	 *
	 * @param pagenum
	 * @param zuserid
	 * @return
	 */
	public Object getmoneyrecord(Integer pagenum, String backuserid, String zuserid, String startTime) {
		Map<String, Object> map = new HashMap<String, Object>();/*
		// 判断充值记录的权限
		int backuserids = selectMenuFour(backuserid);
		if (backuserids == 0) {
			map.put("state", 101);
			return map;
		}*/
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(startTime)) {
			String sql = "select * from game_recharge_money where userid=? and (type = 3 or type = 4) ORDER BY date desc LIMIT ?,10";
			String executeAll = baseDao.executeAll(sql, new Object[] { zuserid, (pagenum - 1) * 10 });
			try {
				while (baseDao.resultSet.next() && "success".equals(executeAll)) {
					Map<String, Object> rmap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getrecord");
					list.add(rmap);
				}
				map.put("total", getrechargenum(zuserid));
				map.put("list", list);
				return map;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String startTimes = startTime + " 00:00:00";
			String endTimes = startTime + " 23:59:59";
			String sql = "select * from game_recharge_money where userid=? and (type = 3 or type = 4) AND date > ? AND date < ? ORDER BY date desc LIMIT ?,10";
			String executeAll = baseDao.executeAll(sql,
					new Object[] { zuserid, startTimes, endTimes, (pagenum - 1) * 10 });
			try {
				while (baseDao.resultSet.next() && "success".equals(executeAll)) {
					Map<String, Object> rmap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getrecord");
					list.add(rmap);
				}
				map.put("total", getrechargenum(zuserid));
				map.put("list", list);
				return map;
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return null;
	}


	/**
	 * 授权
	 *
	 * @param userid
	 * @return
	 */
	public Object author(String userid) {
		String sql = "UPDATE user_table SET role=1 where userid=?";
		return baseDao.executeUpdate(sql, new Object[]{userid});
	}

	/**
	 * 取消授权
	 *
	 * @param userid
	 * @return
	 */
	public Object delauthor(String userid) {
		String sql = "UPDATE user_table SET role=0 where userid=?";
		return baseDao.executeUpdate(sql, new Object[]{userid});
	}


	/**
	 * 充值提现记录总数
	 * 
	 * @param zuserid
	 * 
	 * @return
	 */
	private int getrechargenum(String zuserid) {
		String sql = "select count(1) as count from game_recharge where userid=?";
		baseDao.executeAll(sql, new Object[] { zuserid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 记录总数
	 * 
	 * @return
	 */
	private int getsystemCount() {
		String sql = "select count(*) as counts from game_system";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 对战房间列表
	 * 
	 * @param roomnum
	 * @return
	 */
	public Map<String, Object> getroom(int pagenum, String backuserid, String roomnum) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 查看房间菜单
		int backuserids = selectMenuFour(backuserid);
		if (backuserids == 0) {
			map.put("state", 101);// 权限不足
			return map;
		}
		int count = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (roomnum != null && Public_State.PKMap.get(roomnum) != null) {
			list.add(getroomone(roomnum));
			map.put("list", list);
			map.put("total", Public_State.PKMap.size());
			return map;
		}
		for (String key : Public_State.PKMap.keySet()) {
			count++;
			if (count < pagenum) {
				continue;
			}
			if ((count - pagenum) > 1) {
				break;
			}
			list.add(getroomone(key));
		}
		map.put("list", list);
		map.put("total", Public_State.PKMap.size());
		return map;
	}

	/**
	 * 获取具体的房间信息
	 * 
	 * @param roomnum
	 * @return
	 */
	private Map<String, Object> getroomone(String roomnum) {
		RoomBean roombean = Public_State.PKMap.get(roomnum);
		Map<String, Object> pkmap = new HashMap<String, Object>();
		pkmap.put("roomno", roombean.getRoomno());// 房间号
		pkmap.put("money", roombean.getRoom_card());// 房卡消耗
		pkmap.put("max_number", roombean.getMax_number());// 最大局数
		pkmap.put("max_person", roombean.getMax_person());// 最大人数
		pkmap.put("downscore", roombean.getFen());// 底分
		return pkmap;
	}

	/**
	 * 玩家id 玩家手里的牌 牌大玩家 当前局数
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getrecorduser(String room_number) {
		RoomBean roomBean = Public_State.PKMap.get(room_number);
		List<Map<String, Object>> userlist = new ArrayList<Map<String, Object>>();
		List<UserBean> game_userList = roomBean.getGame_userlist();
		for (UserBean user : game_userList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", user.getUserid());// 玩家id
			map.put("nickname", user.getNickname());// 玩家名称
			map.put("game_number", roomBean.getGame_number());// 当前局数
			// 玩家手里的牌
			List<Integer> brands = user.getBrands();
			// 排序
			Mahjong_Util.mahjong_Util.Order_Brands(brands);
			map.put("brand", transinfo(brands));
			userlist.add(map);
		}
		return userlist;
	}

	/**
	 * @param list
	 * @return
	 */
	private List<String> transinfo(List<Integer> list) {
		ArrayList<String> list2 = new ArrayList<String>();
		for (Integer integer : list) {
			list2.add(getbrand(integer));
		}
		Collections.sort(list2);
		return list2;
	}

	private String getbrand(int value) {
		String brand = "";
		if (value != -1) {
			brand = "<img src='http://210.16.188.100:8098/Gz_Majhong/img/" + value + ".png' width='40px' height='50px'/>";
			return brand;
		} else {
			return brand;
		}
	}

	/**************************** 麻将 **************************/
	/**
	 * 后台登录
	 * 
	 * @param pwd
	 * @return
	 */
	public BackUserBean login(String account, String pwd) {
		String sql = "select * from game_system where account=? and  password=?";
		baseDao.executeAll(sql, new Object[] { account, pwd });
		try {
			if (baseDao.resultSet.next()) {
				BackUserBean backUserBean = new BackUserBean();
				backUserBean.setBackuserid(baseDao.resultSet.getInt("backuserid"));
				backUserBean.setBackname(baseDao.resultSet.getString("backname"));
				backUserBean.setAccount(baseDao.resultSet.getString("account"));
				backUserBean.setPassword(baseDao.resultSet.getString("password"));
				backUserBean.setRole(baseDao.resultSet.getInt("role"));
				backUserBean.setMenuone(baseDao.resultSet.getInt("menuone"));
				backUserBean.setMenutwo(baseDao.resultSet.getInt("menutwo"));
				backUserBean.setMenuthree(baseDao.resultSet.getInt("menuthree"));
				backUserBean.setMenufour(baseDao.resultSet.getInt("menufour"));
				backUserBean.setMenufive(baseDao.resultSet.getInt("menufive"));
				backUserBean.setMenusix(baseDao.resultSet.getInt("menusix"));
				backUserBean.setMenuseven(baseDao.resultSet.getInt("menuseven"));
				backUserBean.setMenueight(baseDao.resultSet.getInt("menueight"));
				backUserBean.setMenunine(baseDao.resultSet.getInt("menunine"));
				backUserBean.setMenuten(baseDao.resultSet.getInt("menuten"));
				backUserBean.setEleven(baseDao.resultSet.getInt("eleven"));
				backUserBean.setTwelve(baseDao.resultSet.getInt("twelve"));
				return backUserBean;
			}
		} catch (SQLException e) {
			BackUserBean backUserBean = new BackUserBean();
			backUserBean.setBackuserid(-1);
			return backUserBean;
		}
		return null;
	}

	/**
	 * 后台修改用户昵称
	 * 
	 * @return
	 */
	public Object upuser(int backuserid, String backname) {
		String sql = "update game_backtable set backname=? where backuserid =?";
		return baseDao.executeUpdate(sql, new Object[] { backname, backuserid });
	}

	/**
	 * 后台管理修改密码
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public Object uppwd(int backuserid, String oldPwd, String newPwd) {
		String sql = "update game_backtable set password=? where backuserid =?";
		return baseDao.executeUpdate(sql, new Object[] { newPwd, backuserid });
	}

	/**
	 * 查看后台管理列表
	 * 
	 *
	 * @return
	 */
	public Object getbacklist(String backuserid, int pagenum) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 查询用户权限
		int i = selectMenuNine(backuserid);
		Map<String, Object> map = new HashMap<String, Object>();
		// 等于0 无权限
		if (i == 0) {
			map.put("state", 110);// 无访问权限
			return map;
		}
		String sql = "select * from game_system LIMIT ?,10";
		String executeAll = baseDao.executeAll(sql, new Object[] { (pagenum - 1) * 10 });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getBackUser");
				list.add(map);
			}
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("total", getsystemCount());
			map1.put("list", list);
			return map1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 查看用户当前在线人数
	 * 
	 * @param backuserid
	 * 
	 * @return
	 */
	public Object selectperson(String backuserid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		int onlineperson = 0;
		String sql = "SELECT count(openid) as sumperson from user_table";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
				int sumperson = baseDao.resultSet.getInt("sumperson");
				map.put("sumperson", sumperson);
				List<String> openList = UserOpenidList();
				for (String openid : openList) {
					Mahjong_Socket socket = Public_State.clients.get(openid);
					if (socket != null) {
						onlineperson++;
					}
				}
				map.put("onlineperson", onlineperson);
				list.add(map);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取所有用户
	 */
	private List<String> UserOpenidList() {
		List<String> list = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select * from user_table";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getUserList");
				String openid = map.get("openid").toString();
				list.add(openid);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看用户信息
	 * 
	 * @param pagenum
	 * @param rmap
	 * @return
	 */
	public Map<String, Object> selectuser(int pagenum, String backuserid, Map<String, Object> rmap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Object[] object = new Object[rmap.size() + 1];
		StringBuffer stringb = new StringBuffer("SELECT * FROM user_table AS gu where 1=1 ");
		int index = 0;
		if (rmap.get("userid") != null) {
			stringb.append("and gu.userid=? ");
			String[] ss = new String[] { "10000", "1000", "100", "10" };
			for (int i = 0; i < ss.length; i++) {
				int indexs = rmap.get("userid").toString().indexOf(ss[i]);
				if (indexs != -1) {
					String newuser = rmap.get("userid").toString().substring(indexs + ss[i].length(),
							rmap.get("userid").toString().length());
					rmap.put("userid", newuser);
					break;
				}
			}
			object[index] = Integer.parseInt(rmap.get("userid").toString());
			index++;
		}
		if (rmap.get("nickname") != null) {
			stringb.append("AND gu.nickname like ? ");
			object[index] = "%" + rmap.get("nickname").toString() + "%";
			index++;
		}
		stringb.append(" LIMIT ?,10 ");
		object[index] = (pagenum - 1) * 10;
		 baseDao.executeAll(stringb.toString(), object);
		try {
			while (baseDao.resultSet.next()) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getUserList");
				int userid = Integer.parseInt(map.get("userid").toString());
				if (userid >= 10000 && userid < 100000)
					map.put("zuserid", 1 + "" + userid);
				if (userid >= 1000 && userid < 10000)
					map.put("zuserid", 10 + "" + userid);
				if (userid >= 100 && userid < 1000)
					map.put("zuserid", 100 + "" + userid);
				if (userid >= 10 && userid < 100)
					map.put("zuserid", 1000 + "" + userid);
				if (userid < 10)
					map.put("zuserid", 10000 + "" + userid);
				if (Public_State.clients.get(map.get("openid").toString()) != null) {
					map.put("online", 1);
				} else {
					map.put("online", 0);
				}
				list.add(map);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", getCount_user());
			map.put("list", list);
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获得所有的用户数量
	public int getCount_user() {
		String sql = "SELECT count(1) as counts FROM user_table";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
                return  baseDao.resultSet.getInt("counts");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 查看指定的牌友圈
	 * 
	 * @return
	 */
	public int getCount_lookcircle(String circlenumber) {
		String sql = "SELECT count(1) AS counts FROM game_card_circle WHERE circlenumber=?";
		baseDao.executeAll(sql, new Object[] { circlenumber });
		try {
			if (baseDao.resultSet.next()) {
				int count = baseDao.resultSet.getInt("counts");
				return count;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 牌友圈列表
	 * 
	 * @param pagenum
	 * @param rmap
	 * @return
	 */
	public Map<String, Object> getcard(int pagenum, Map<String, Object> rmap, String backuserid) {
		int personnum = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Object[] object;
		if(!backuserid.equals("1")){
			object = new Object[rmap.size() + 2];
		}else{
			object = new Object[rmap.size() + 1];
		}
		StringBuffer stringb = new StringBuffer(
				"select *,(select count(1) from game_card_user where cc.circlenumber=circlenumber) as count,(SELECT nickname FROM user_table WHERE userid=cc.userid) AS nickname  from game_card_circle as cc where 1=1 ");
		int index = 0;
		if(!backuserid.equals("1")){
			stringb.append("and userid = ? ");
			object[index] = backuserid;
			index++;
		}
		if (rmap.get("circlenumber") != null) {
			stringb.append("and cc.circlenumber=? ");
			object[index] = Integer.parseInt(rmap.get("circlenumber").toString());
			index++;
		}
		stringb.append(" LIMIT ?,10 ");
		object[index] = (pagenum - 1) * 10;
		String executeAll = baseDao.executeAll(stringb.toString(), object);
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getcard");//

				int userid = Integer.parseInt(map.get("userid").toString());
				if (userid >= 10000 && userid < 100000)
					map.put("zuserid", 1 + "" + userid);
				if (userid >= 1000 && userid < 10000)
					map.put("zuserid", 10 + "" + userid);
				if (userid >= 100 && userid < 1000)
					map.put("zuserid", 100 + "" + userid);
				if (userid >= 10 && userid < 100)
					map.put("zuserid", 1000 + "" + userid);
				if (userid < 10)
					map.put("zuserid", 10000 + "" + userid);
				list.add(map);
			}
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("personnum", Public_State.getAlinenum2(Integer.parseInt(list.get(i).get("circlenumber").toString())));
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", getCount_circle());
			map.put("list", list);
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获得所有的牌友圈数量
	public int getCount_circle() {
		String sql = "SELECT count(1) AS counts FROM game_card_circle";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 在线人数
	 * 
	 * @param id
	 * @return
	 */
	public List<String> onlineperson(String id) {
		List<String> list = new ArrayList<String>();
		String sql = "SELECT gu.openid from game_card_user AS gc JOIN  user_table as gu ON gc.userid=gu.userid WHERE circlenumber =?";
		String executeAll = baseDao.executeAll(sql, new Object[] { id });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				list.add(baseDao.resultSet.getString("openid"));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 封禁
	 * 
	 * @param userid
	 * @param banned
	 * @return
	 */
	public Object banned(String userid, String banned) {
		String sql = "UPDATE user_table SET statetext=?,state = 1  where userid=?";
		String str = baseDao.executeUpdate(sql, new Object[] { banned, userid });
		if ("success".equals(str)) {
			return "success";
		}
		return -1;
	}

	/**
	 * 解封
	 * 
	 * @param userid
	 * @return
	 */
	public Object deblocking(String userid) {
		String sql = "UPDATE user_table SET statetext = '' , state = 0 where userid =?";
		String string = baseDao.executeUpdate(sql, new Object[] { userid });
		if ("success".equals(string)) {
			return "success";
		}
		return -1;
	}

	/**
	 * 开启
	 *
	 * @param userid
	 * @param
	 * @return
	 */
	public Object open(String userid) {
		String sql = "UPDATE user_table SET isPay=1  where userid=?";
		String str = baseDao.executeUpdate(sql, new Object[] {userid });
		if ("success".equals(str)) {
			return "success";
		}
		return -1;
	}
	/**
	 * 关闭
	 *
	 * @param userid
	 * @param
	 * @return
	 */
	public Object onclose(String userid) {
		String sql = "UPDATE user_table SET isPay=0  where userid=?";
		String str = baseDao.executeUpdate(sql, new Object[] {userid });
		if ("success".equals(str)) {
			return "success";
		}
		return -1;
	}

	/**
	 * 钻石充值
	 * 
	 * @param userid
	 * @param zuserid
	 * @return
	 */
	public Object updiamond(String userid, String diamond, String zuserid) {
		String sql = "UPDATE user_table SET diamond=diamond+?  where userid=?";
		String str = baseDao.executeUpdate(sql, new Object[] { diamond, userid });
		if ("success".equals(str)) {
			insertdiamond(diamond, userid);
			downdiamond2(zuserid,diamond,"0");
			return "success";
		}
		return -1;
	}
	/**
	 * 钻石充值
	 *
	 * @param userid
	 * @param zuserid
	 * @return
	 */
	public Object updiamond2(String userid, String diamond, String zuserid) {

		if(!zuserid.equals("1")){
			int d = getbackUser(zuserid);
			if(d<Integer.valueOf(diamond)){
				return "error";
			}
		}

		String sql = "UPDATE game_system SET menuone=menuone+?  where backuserid=?";
		String str = baseDao.executeUpdate(sql, new Object[] { diamond, userid });
		if ("success".equals(str)) {
			insertdiamond(diamond, userid);
			downdiamond2(zuserid,diamond,"0");
			return "success";
		}
		return -1;
	}

	private int getbackUser(String zuserid) {
		String sql = "SELECT menuone from game_system where backuserid=?";
		baseDao.executeAll(sql, new Object[]{zuserid});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuone");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 钻石充值
	 *
	 * @param userid
	 * @param zuserid
	 * @return
	 */
	public Object updiamond3(String userid, String diamond, String zuserid) {
		String sql = "UPDATE game_system SET menuone=menuone+?  where backuserid=?";
		String str = baseDao.executeUpdate(sql, new Object[] { diamond, userid });
		if ("success".equals(str)) {
			insertdiamond(diamond, userid);
			return "success";
		}
		return -1;
	}

	/**
	 * 插入钻石充值记录
	 * 
	 */
	private void insertdiamond(String diamond, String zuserid) {
		String sql = "insert into game_recharge(userid,money,date,type) values(?,?,NOW(),1) ";
		baseDao.executeUpdate(sql, new Object[] { zuserid, diamond });
	}
	/**
	 * 插入积分变更记录
	 *
	 */
	private void insertmoney(String money, String userid,int type) {
		String sql = "insert into game_recharge_money(userid,money,date,type) values(?,?,NOW(),?) ";
		baseDao.executeUpdate(sql, new Object[] {userid, money,type });
	}

	/**
	 * 钻石提现
	 * 
	 * @return
	 */
	public Object downdiamond(String userid, String diamond, String zuserid) {
		String sql = "UPDATE user_table SET diamond = diamond-? where userid=? and diamond - ? >0";
		String str = baseDao.executeUpdate(sql, new Object[] { diamond, userid, diamond });
		if ("success".equals(str)) {
			insertddiamond(diamond, userid);
			return "success";
		}
		return -1;
	}

	/**
	 * 钻石提现
	 *
	 * @return
	 */
	public Object downdiamond2(String userid, String diamond, String zuserid) {
		String sql = "UPDATE game_system SET menuone=menuone-?  where backuserid=? and menuone - ? >=0";
		String str = baseDao.executeUpdate(sql, new Object[] { diamond, userid, diamond });
		if ("success".equals(str)) {
			insertddiamond(diamond, userid);
			return "success";
		}
		return -1;
	}

	// 插入记录
	private void insertddiamond(String diamond, String zuserid) {
		String sql = "insert into game_recharge(userid,money,date,type) values(?,?,NOW(),2) ";
		baseDao.executeUpdate(sql, new Object[] { zuserid, diamond });
	}

	/**
	 * 加入牌友圈
	 * 
	 * @param userid
	 * @param card_id
	 * @return
	 */
	public String joincard(String userid, String card_id) {
		String sql = "call joincard(?,?)";
		baseDao.executeAll(sql, new Object[] { userid, card_id });
		try {
			if (baseDao.resultSet.next()) {
				int errno = baseDao.resultSet.getInt("errno");
				int state = baseDao.resultSet.getInt("state");
				if (errno == 0) {
					return state + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	/************************************* 游戏管理 **************************************************/

	/**
	 * 公告管理
	 * 
	 * @return
	 */
	public Object getnotice(String backuserid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		// 查看消耗兑换权限
		int backuserids = selectMenuEight(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT * FROM game_notice";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getnotice");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改公告管理
	 * 
	 * @param noticeid
	 * @param value
	 * @return
	 */
	public Object updnotice(int noticeid, String value) {
		String sql = "update game_notice SET `value`=? WHERE noticeid=?";
		return baseDao.executeUpdate(sql, new Object[] { value, noticeid });
	}

	/**
	 * 插入登录日志
	 * 
	 * @param userbean
	 * @param request
	 */
	public void insertUser(BackUserBean userbean, HttpServletRequest request) {
		String IP = UtilClass.utilClass.getIpAddr(request);
		System.out.println(">>>>>>>>>>>>>>>>>>ip" + IP);
		String sql = "insert into login_log_table(backuserid,date,IP) values(?,NOW(),?)";
		baseDao.executeUpdate(sql, new Object[] { userbean.getBackuserid(), IP });
	}

	/**
	 * 查询登录日志
	 * 
	 * @param request
	 * @param backuserid
	 * @param pagenum
	 * 
	 * @return
	 */
	public Object getlogin(HttpServletRequest request, String backuserid, Integer pagenum) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		// 查询登陆日志权限
		int backuserids = selectEleven(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT gs.backuserid,gs.backname,ll.date,ll.IP FROM login_log_table AS ll LEFT JOIN game_system AS gs ON gs.backuserid = ll.backuserid order by date desc  LIMIT ?,10 ";
		String executeAll = baseDao.executeAll(sql, new Object[] { (pagenum - 1) * 10 });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getlogin");
				list.add(map);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", getLoginCount());
			map.put("list", list);
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取登录日志数量
	 * 
	 * @return
	 */
	private Object getLoginCount() {
		String sql = "SELECT count(1) as counts FROM login_log_table ";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
				int count = baseDao.resultSet.getInt("counts");
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/***
	 * 查看用户权限
	 * 
	 * @param backuserid
	 * 
	 * @return
	 */
	public Object getbacksystem(String backuserid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		int backuserids = selectMenuTen(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT * FROM game_system";
		String executeAlll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getsystem");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取后台用户列表
	 * 
	 * @param backuserid
	 * @return
	 */
	public Object selectBackUser(String backuserid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		int backuserids = selectMenuNine(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT * FROM game_system";
		String executeAlll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getsystemuser");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取后台用户列表
	 *
	 * @param backuserid
	 * @return
	 */
	public Object selectBackUser2(String backuserid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		String sql = "SELECT * FROM game_system where menutwo = ?";
		String executeAlll = baseDao.executeAll(sql, new Object[] {backuserid});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getsystemuser");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除后台用户
	 * 
	 * @param sysuserid
	 * @return
	 */
	public Object delsystemuser(int sysuserid, int userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (sysuserid == userid) {
			map.put("state", 101);
			return map;
		} else if (userid != 1) {
			map.put("state", 100);
			return map;
		}
		String sql = "DELETE FROM game_system WHERE backuserid = ?";
		return baseDao.executeUpdate(sql, new Object[] { sysuserid });
	}

	/**
	 * 添加后台管理
	 * 
	 * @param name
	 * @param account
	 * @param pwd
	 * @return
	 */
	public Object addsysuser(String name, String account, String pwd, String backuserid) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 任务权限
		int backuid = Integer.parseInt(backuserid);
		if (backuid != 1) {
			map.put("state", 101);// 权限不足
			return map;
		}
		boolean b = getSysName(account);
		if (b) {
			// 系统管理员账号重复
			map.put("state", 102);
			return map;
		}
		String sql = "INSERT INTO game_system (backname,account,password,date,role,menuone,menutwo,menuthree,menufour,menufive,menusix,menuseven,menueight,menunine,menuten,eleven,twelve,thirteen,fourteen,fifteen,sixteen,seventeen,eighteen,nineteen,twenty,twentyone)VALUES(?,?,?,NOW(),2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)";
		return baseDao.executeUpdate(sql, new Object[] { name, account, pwd });
	}
	/**
	 * 添加后台管理
	 *
	 * @param name
	 * @param account
	 * @param pwd
	 * @return
	 */
	public Object addsysuser2(String name, String account, String pwd, String backuserid) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 任务权限
		int backuid = Integer.parseInt(backuserid);
		boolean b = getSysName(account);
		if (b) {
			// 系统管理员账号重复
			map.put("state", 102);
			return map;
		}
		String sql = "INSERT INTO game_system (backname,account,password,date,role,menuone,menutwo,menuthree,menufour,menufive,menusix,menuseven,menueight,menunine,menuten,eleven,twelve,thirteen,fourteen,fifteen,sixteen,seventeen,eighteen,nineteen,twenty,twentyone)VALUES(?,?,?,NOW(),3,0,?,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)";
		return baseDao.executeUpdate(sql, new Object[] { name, account, pwd ,backuid});
	}

	/**
	 * 查看系统管理员是否重名
	 * 
	 */
	private boolean getSysName(String account) {
		String sql = "select * from game_system where account=?";
		baseDao.executeAll(sql, new Object[] { account });
		try {
			return baseDao.resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 查看钻石商店信息
	 * 
	 * @param backuserid
	 * @return
	 */
	public Object getdiamondshop(String backuserid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		int backuserids = selectMenuSix(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT * FROM game_diamond_shop";
		String executeAlll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getdiamondshop");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 查看钻石商店信息
	 *
	 * @param backuserid
	 * @return
	 */
	public Object getmoneyshop(String backuserid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();
		int backuserids = selectMenuSix(backuserid);
		if (backuserids == 0) {
			rmap.put("state", 101);// 权限不足
			return rmap;
		}
		String sql = "SELECT * FROM game_money_shop";
		String executeAlll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getmoneyshop");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 修改金币商品
	 *
	 * @param id
	 * @param price
	 * @param realcircle
	 * @param
	 * @return
	 */
	public Object delmoney(int id, String moneyname, String realcircle, int price) {
		String sql = "update game_money_shop SET moneyname=?,realvalue=?,price=? where moneyid=?";
		return baseDao.executeUpdate(sql, new Object[] { moneyname, realcircle, price, id });
	}

	/**
	 * 修改钻石商品
	 * 
	 * @param id
	 * @param price
	 * @param realcircle
	 * @param diamondname
	 * @return
	 */
	public Object deldiamond(int id, String diamondname, String realcircle, int price) {
		String sql = "update game_diamond_shop SET diamondname=?,realvalue=?,price=? where diamondid=?";
		return baseDao.executeUpdate(sql, new Object[] { diamondname, realcircle, price, id });
	}

	/**
	 * 消耗钻石数量
	 * 
	 * @param backuserid
	 * @return
	 */
	public Object getfreediamond(String backuserid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rmap = new HashMap<String, Object>();

		String sql = "SELECT * FROM config_table";
		String executeAlll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAlll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getdiomond");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改钻石房消耗钻石
	 *
	 * @return
	 */
	public Object updfreediamond(String establish_two, String establish_four,String configid) {
		String sql = "UPDATE config_table SET establish_two=?,establish_four=? where configid = ?";
		return baseDao.executeUpdate(sql, new Object[]{establish_two, establish_four,configid});
	}

	/**
	 * 修改钻石房消耗钻石
	 *
	 * @return
	 */
	public Object updfreediamond2(String establish_zjh,String configid) {
		String sql = "UPDATE config_table SET establish_zjh=? where configid = ?";
		return baseDao.executeUpdate(sql, new Object[]{establish_zjh,configid});
	}

	/**
	 * 查看用户流水
	 * 
	 * @return
	 */
	public Object getgamerecord(String backuserid, String startTime) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapp = new HashMap<String, Object>();
		// 任务权限
		int backuserids = selectMenuTwo(backuserid);
		if (backuserids == 0) {
			mapp.put("state", 101);// 权限不足
			return mapp;
		}
		if (StringUtils.isEmpty(startTime)) {
			String sql = "SELECT SUM(diamond) AS diamond ,(select SUM(money) from game_recharge where type=1) AS money from game_recharge where type=1";
			baseDao.executeAll(sql, new Object[] {});
			try {
				while (baseDao.resultSet.next()) {
					Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getgamerecord");
					list.add(map);
				}
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String startTimes = startTime + " 00:00:00";
			String endTimes = startTime + " 23:59:59";
			String sql = "SELECT SUM(diamond) AS diamond ,(select SUM(money) from game_recharge where type=1 AND date > ? AND date < ? ) AS money from game_recharge where type=1 AND date > ? AND date < ?";
			baseDao.executeAll(sql, new Object[] { startTimes, endTimes, startTimes, endTimes });
			try {
				if (baseDao.resultSet.next()) {
					Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
							"sql_getgamerecord");
					list.add(map);
				}
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 查看牌友圈的用户信息
	 * 
	 * @param circlenumber
	 * @return
	 */
	public Object lookCircle(String circlenumber) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT gu.userid,gu.nickname,gu.diamond,gcu.date,gu.number_1,gu.number_4,gu.number_5 FROM user_table AS gu LEFT JOIN game_card_user AS gcu ON gcu.userid = gu.userid LEFT JOIN game_card_circle AS gcc ON gcc.circlenumber = gcu.circlenumber WHERE gcu.circlenumber = ?";
		String executeAll = baseDao.executeAll(sql, new Object[] { circlenumber });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_cardinfo");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 改牌
	 *     list集合删除操作  可直接按索引删除  也遍历集合删除元素
	 * @param roomnum
	 * @param pindex
	 * @return
	 */
	public int updatepai(String roomnum, int pindex) {
		if (roomnum != null && !"".equals(roomnum)) {
			RoomBean roomBean = Public_State.PKMap.get(roomnum);
			List<Integer> brands = roomBean.getBrands();
			if (brands.indexOf(pindex) != -1) {
				roomBean.setOnkey(pindex);
				for (int i = 0; i <brands.size() ; i++) {
					Integer brand = brands.get(i);
					if (brand==pindex){
						brands.remove(brand);
						return 0;
					}
				}
			} else {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * 查看提现申请列表
	 *
	 * param pagenum
	 */
	public Object getwithdraw(Integer pagenum, String backuserid) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 任务权限;l.
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select * from game_card_apply where backuserid = ? ORDER BY date desc LIMIT ?,10";
		String executeAll = baseDao.executeAll(sql, new Object[] { backuserid, (pagenum - 1) * 10 });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> rmap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_game_card_apply");
				list.add(rmap);
			}
			map.put("total", getWithdrawCount());
			map.put("list", list);
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 提现申请总数
	 *
	 * @return
	 */
	private Object getWithdrawCount() {
		String sql = "select count(*) as counts from game_card_apply";
		baseDao.executeAll(sql, new Object[] {});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 拒绝提现申请
	 *
	 * param withdrawid
	 * param userid
	 * param money
	 * param state
	 * @return
	 */
	public Object downwithdraw(int withdrawid, int state) {
		// 操作过拒绝操作
		if (state != 0) {
			return "error";
		}
		String sql = "UPDATE game_card_apply SET state=2 where applyid=?";
		return baseDao.executeUpdate(sql, new Object[] { withdrawid });
	}

	/**
	 * 更改用户余额
	 *
	 * param userid
	 * param money
	 * @return
	 */
	private String updateusermoney(int userid, int money) {
		String sql = "UPDATE zcf_user.user_table SET money=money+? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { money, userid });
	}

	/**
	 * 更改用户余额
	 *
	 * param userid
	 * param money
	 * @return
	 */
	private String updateuserDiamond(int userid, int diamond) {
		String sql = "UPDATE zcf_user.user_table SET diamond=diamond+? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { diamond, userid });
	}

	/**
	 * 同意提现申请
	 *
	 * param withdrawid
	 * @return
	 */
	public Object passwithdraw(int withdrawid, int state,String number_3,int userid) {
		if (state != 0) {
			return "error";
		}

		String sql1 = "UPDATE user_table SET number_3=? where userid=?";
		baseDao.executeUpdate(sql1, new Object[] {number_3,userid});
		String sql2 = "insert into game_card_user values(null,?,?,NOW())";
		baseDao.executeUpdate(sql2, new Object[] {userid,number_3});
		String sql = "UPDATE game_card_apply SET state=1 where applyid=?";
		return baseDao.executeUpdate(sql, new Object[] { withdrawid });
	}

	public Object refash(String backuserid) {
		String sql = "SELECT menuone from game_system where backuserid=?";
		baseDao.executeAll(sql, new Object[]{backuserid});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("menuone");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 金币充值
	 *
	 * param userid
	 * param money
	 * param nickname
	 * param zuserid
	 * @return
	 */
	public Object upmoney(String userid, String money, String zuserid, String nickname) {
		String sql = "UPDATE user_table SET money=money+?  where userid=?";
		String str = baseDao.executeUpdate(sql, new Object[] { money, userid });
		if ("success".equals(str)) {
			insertmoney(money, zuserid, 4);
			return "success";
		}
		return -1;
	}

	/**
	 * 金币提现
	 *
	 * param userid
	 * param money
	 * param nickname
	 * param zuserid
	 * @return
	 */
	public Object downmoney(String userid, String money, String zuserid, String nickname) {
		String sql = "UPDATE user_table SET money=money-?  where userid=? and money - ? >0";
		String str = baseDao.executeUpdate(sql, new Object[] { money, userid, money });
		if ("success".equals(str)) {
			insertmoney(money, zuserid, 4);
			return "success";
		}
		return -1;
	}

	/**
	 * 修改金币商品
	 *
	 * param id
	 * param i
	 * param string2
	 * param moneyname
	 * @return
	 */
	public Object updmoney(int id, String moneyname, String realvalue, int price) {
		String sql = "update game_money_shop SET moneyname=?,realvalue=?,price=? where moneyid=?";
		return baseDao.executeUpdate(sql, new Object[] { moneyname, realvalue, price, id });
	}
}