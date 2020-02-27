package com.internousdev.bianco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.bianco.dto.CartInfoDTO;
import com.internousdev.bianco.util.DBConnector;

public class CartInfoDAO {

	// カート情報を持ってくるメソッド
	public List<CartInfoDTO> getCartInfo(String userId) throws SQLException {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

		// 商品情報カート、カート情報（テーブル結合）
		String sql = "SELECT ci.id as id," + "ci.user_id as user_id," + "ci.product_id as product_id,"
				+ "ci.product_count as product_count," + "pi.price as price," + "pi.product_name as product_name,"
				+ "pi. product_name_kana as product_name_kana," + "pi.image_file_path as image_file_path,"
				+ "pi.image_file_name as image_file_name," + "pi.release_date as release_date,"
				+ "pi.release_company as release_company," + "pi.status as status," + "ci.regist_date as regist_date,"
				+ "ci.update_date as update_date," + "(ci.product_count * pi.price) as subprice "
				+ "FROM cart_info as ci " + "LEFT JOIN product_info as pi " + "ON ci.product_id = pi.product_id "
				+ "WHERE ci.user_id =? "
				+ "order by update_date desc,regist_date desc";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			// DBから取得した商品情報をDTOに格納
			while (rs.next()) {
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setId(rs.getInt("id"));
				cartInfoDTO.setUserId(rs.getString("user_id"));
				cartInfoDTO.setProductId(rs.getInt("product_id"));
				cartInfoDTO.setProductCount(rs.getInt("product_count"));
				cartInfoDTO.setPrice(rs.getInt("price"));
				cartInfoDTO.setProductName(rs.getString("product_name"));
				cartInfoDTO.setProductNameKana(rs.getString("product_name_kana"));
				cartInfoDTO.setImageFilePath(rs.getString("image_file_path"));
				cartInfoDTO.setImageFileName(rs.getString("image_file_name"));
				cartInfoDTO.setReleaseDate(rs.getDate("release_date"));
				cartInfoDTO.setReleaseCompany(rs.getString("release_company"));
				cartInfoDTO.setStatus(rs.getString("status"));
				cartInfoDTO.setSubPrice(rs.getInt("subprice"));
				cartInfoDTOList.add(cartInfoDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return cartInfoDTOList;
	}

	// カート合計金額
	public int getTotalPrice(String userId) throws SQLException {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int totalPrice = 0;

		String sql = "select sum(product_count * price) as total_price " + "FROM cart_info ci "
				+ "JOIN product_info pi " + "ON ci.product_id = pi.product_id " + "WHERE user_id=? "
				+ "group by user_id";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				totalPrice = rs.getInt("total_price");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return totalPrice;
	}

	// cart.jspの削除ボタンを押した時deletecartActionにて使用
	public int delete(String userId, String productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 1;
		String sql = "delete from cart_info where user_id=? and product_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, productId);

			count = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// ユーザーIDに紐付くカート情報を宛先情報テーブル全削除
	public int SettlementDeleteAll(String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql = "delete from cart_info where user_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// 追加する商品IDと一致するデータが存在チェック(AddCartaction)
	public static boolean isExistCart(String userId, int productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT COUNT(id) AS COUNT FROM cart_info WHERE user_id = ? and product_id=?";
		boolean result = false;

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				if (rs.getInt("COUNT") > 0) {
					result = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// 〇商品IDとカート情報テーブルから持ってきた商品IDが一値した場合(個数更新(存在する)カウントアップ)
	// すでに商品が入っている場合は、updateする
	public int productUpDate(String userId, int productId, int productCount) throws SQLException {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int result = 0;

		String sql = "UPDATE cart_info " + "SET product_count=(product_count + ?), update_date = now() "
				+ "WHERE user_id=? " + "AND product_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, productCount);
			ps.setString(2, userId);
			ps.setInt(3, productId);
			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	// 追加する商品IDと一致するデータが存在しない(カウントしない)
	public int regist(String userId, int productId, int productCount) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 1;
		String sql = "insert into cart_info(user_id, product_id, product_count, regist_date, update_date)"
				+ " values (?, ?, ?, now(), now())";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ps.setInt(3, productCount);

			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// 仮ユーザーIDと商品を繋ぐ
	public int linkToUserId(String userId, String tmpUserId, int prodctId) throws SQLException {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;

		String sql = "update cart_info set user_id=?, update_date = now() where user_id=? and product_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, tmpUserId);
			ps.setInt(3, prodctId);

			count = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return count;
	}

	// 仮ユーザーID用のカート削除機能
	public int tmpDelete(String tmpUserId, int productId) throws SQLException {

		DBConnector db = new DBConnector(); // データベース接続管理クラスの変数宣言
		Connection con = db.getConnection(); // データベース接続情報
		PreparedStatement ps; // sql管理情報
		int result = 0; // 検索結果

		String sql = "delete from cart_info where user_id =? and product_id=?"; // カート削除

		try {
			// DELETE文の登録と実行
			ps = con.prepareStatement(sql);
			ps.setString(1, tmpUserId);
			ps.setInt(2, productId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

}
