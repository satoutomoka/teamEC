<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="./css/button.css">
<link rel="stylesheet" href="./css/detail.css">
<link rel="stylesheet" href="./css/error.css">
<link rel="stylesheet" href="./css/header.css">
<link rel="stylesheet" href="./css/list.css">
<link rel="stylesheet" href="./css/table1.css">
<link rel="stylesheet" href="./css/table2.css">
<link rel="stylesheet" href="./css/title.css">
<title>カート画面</title>
<script type="text/javascript">
	function checkValue() {

		var checkList = document.getElementsByClassName("checkList");
		//初期化
		var checkFlg = 0;

		//条件分岐(繰り返し処理)
		for (var i = 0; i < checkList.length; i++) {

			if (checkList[i].checked) {

				checkFlg = 1;
				break;
			}
		}
		if (checkFlg == 1) {

			document.getElementById('delete').disabled = "";

		} else {

			document.getElementById('delete').disabled = "true";
		}
	}
</script>
</head>
<body>
	<jsp:include page="header.jsp" />
	<h1>カート画面</h1>
	<div id="main">
		<s:if test="cartInfoDTOList != null && cartInfoDTOList.size()>0">
			<form action="DeleteCartAction">
				<table class=cart_table>
					<tr>
						<th><s:label value="#" /></th>
						<th><s:label value="商品名" /></th>
						<th><s:label value="商品名ふりがな" /></th>
						<th><s:label value="商品画像" /></th>
						<th><s:label value="値段" /></th>
						<th><s:label value="発売会社名" /></th>
						<th><s:label value="発売年月日" /></th>
						<th><s:label value="購入個数" /></th>
						<th><s:label value="合計金額" /></th>
					</tr>

					<s:iterator value="cartInfoDTOList">
						<tr>
							<td><input type="checkbox" name="checkList"
								class="checkList" value='<s:property value="productId"/>'
								onchange="checkValue()" /></td>
							<td><s:property value="productName" /></td>
							<td><s:property value="productNameKana" /></td>
							<td><img
								src='<s:property value="imageFilePath"/>/<s:property value="imageFileName"/>'
								width="50px" height="50px" /></td>
							<td><s:property value="price" />円</td>
							<td><s:property value="releaseCompany" /></td>
							<td><s:property value="releaseDate" /></td>
							<td><s:property value="productCount" />個</td>
							<td><s:property value="subPrice" />円</td>
						</tr>
					</s:iterator>
				</table>
				<div class="total-box">
					<h2>
						<s:label value="カート合計金額 :" />
						<s:property value="totalPrice" />
						円
					</h2>
				</div>
				<s:submit value="削除" class="submit_btn1" id="delete" disabled="true" />
			</form>

			<s:if test="#session.logined == 1">
				<s:form action="SettlementConfirmAction">
					<s:submit value="決済" class="submit_btn2" />
				</s:form>
			</s:if>
			<s:else>
				<s:form action="GoLoginAction">
					<s:submit value="決済" class="submit_btn2" />
					<s:hidden name="cartFlg" value="1" />
				</s:form>
			</s:else>
		</s:if>
		<s:else>
			<div class="info">カート情報がありません。</div>
		</s:else>
	</div>
</body>
</html>