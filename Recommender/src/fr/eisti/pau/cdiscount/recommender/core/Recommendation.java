package fr.eisti.pau.cdiscount.recommender.core;

public class Recommendation {

	private String userId;
	private int num;
	
	public Recommendation() {
		super();
	}

	public Recommendation(String userID, int num) {
		this.userId = userID;
		this.num = num;
	}

	public String getUserID() {
		return userId;
	}

	public void setUserID(String userID) {
		this.userId = userID;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "Recommendation [userID=" + userId + ", num=" + num + "]";
	};
	
	

}
