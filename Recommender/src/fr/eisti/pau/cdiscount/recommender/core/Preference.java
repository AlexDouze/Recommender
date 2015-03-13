package fr.eisti.pau.cdiscount.recommender.core;

import fr.eisti.pau.cdiscount.recommender.dto.WineDto;

public class Preference {

	private String userId;
	private WineDto wine;
	private float rate;
	
	public Preference() {
		super();
	}

	public Preference(String userId, WineDto itemId, float rate) {
		this.userId = userId;
		this.wine = itemId;
		this.rate = rate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public WineDto getItemId() {
		return wine;
	}

	public void setItemId(WineDto itemId) {
		this.wine = itemId;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}
	
}
