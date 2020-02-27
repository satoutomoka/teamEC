package com.internousdev.bianco.action;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.bianco.dao.CartInfoDAO;
import com.internousdev.bianco.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware {
	private int totalPrice;
	private List<CartInfoDTO> cartInfoDTOList;
	private Map<String, Object> session;

	public String execute() throws SQLException {

		//loginIdかtmpUserIdがあればuserIdに変換してユーザー確認
		if (!session.containsKey("tmpUserId") && !session.containsKey("userId")) {
			return "sessionTimeout";
		}

		String userId = null;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();

		String tmpLogined = String.valueOf(session.get("logined"));
		int logined = "null".equals(tmpLogined) ? 0 : Integer.parseInt(tmpLogined);
		if (logined == 1) {
			userId = session.get("userId").toString();
		} else {
			userId = String.valueOf(session.get("tmpUserId"));
		}
		cartInfoDTOList = cartInfoDAO.getCartInfo(userId);
		totalPrice = cartInfoDAO.getTotalPrice(userId);

		return SUCCESS;
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

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
