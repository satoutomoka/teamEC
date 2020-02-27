package com.internousdev.bianco.action;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.bianco.dao.CartInfoDAO;
import com.internousdev.bianco.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware {

	private int totalPrice;
	private List<CartInfoDTO> cartInfoDTOList;
	private String[] checkList;
	private Map<String, Object> session;

	public String execute() {

		//loginIdかtmpUserIdがあればuserIdに変換してユーザー確認
		if (!session.containsKey("tmpUserId") && !session.containsKey("userId")) {
			return "sessionTimeout";
		}

		String result = ERROR;

		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		int count = 0;
		String userId = null;
		String tmpLogined = String.valueOf(session.get("logined"));
		int logined = "null".equals(tmpLogined) ? 0 : Integer.parseInt(tmpLogined);


		//ログイン済みだったらuserId
		//ログイン済みじゃなかったらtmpUserId
		if (logined == 1) {
			userId = session.get("userId").toString();
		} else {
			userId = String.valueOf(session.get("tmpUserId"));
		}

		//iDにチェックされた時の処理
		for (String productId : checkList) {
			count += cartInfoDAO.delete(userId,productId);
		}

		if (count == checkList.length) {
			try {
				cartInfoDTOList = cartInfoDAO.getCartInfo(userId);
				totalPrice = cartInfoDAO.getTotalPrice(userId);
			} catch (SQLException e) {

				e.printStackTrace();
			}

			result = SUCCESS;
		}
		return result;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<CartInfoDTO> getCartInfoDTOList() {
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(List<CartInfoDTO> cartInfoDTOList) {
		this.cartInfoDTOList = cartInfoDTOList;
	}

	public String[] getCheckList() {
		return checkList;
	}

	public void setCheckList(String[] checkList) {
		this.checkList = checkList;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
