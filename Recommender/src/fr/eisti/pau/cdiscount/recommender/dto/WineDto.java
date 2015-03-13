package fr.eisti.pau.cdiscount.recommender.dto;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


public class WineDto {
	private String title;
	private String url_icon;
	private Double priceTop;
	private Double price;
	private int rating;
	private String description;
	private String url;
	
	public WineDto() {
		super();
	}

	public WineDto(String title, String url_icon, String priceTop, String price, int rating, String description, String url) {
		super();
		this.title = title;
		this.url_icon = url_icon;
		this.priceTop = Double.valueOf(priceTop);
		this.price = Double.valueOf(price);
		this.rating = rating;
		this.description = description;
		this.url = url;
	}
	
	public WineDto(Builder b){
		this.title = b.title;
		this.url_icon = b.url_icon;
		this.priceTop = b.priceTop;
		this.price = b.price;
		this.rating = b.rating;
		this.description = b.description;
		this.url = b.url;		
	}

	public static JSONObject setStringToJSON(String str){
		if(str != ""){
			try {
				return new JSONObject(str);
			} catch (JSONException e) {e.printStackTrace();}
		}
		return null;
	}
	
	public String getTitle() {return title;}

	public String getUrl_icon() {return url_icon;}

	public Double getPriceTop() {return priceTop;}

	public Double getPrice() {return price;}

	public int getRating() {return rating;}

	public String getDescription() {return description;}

	public String getUrl() {return url;}
	
	
	// ************** Class Builder de transformation d'un Wine en WineDto  ************
	
	public static class Builder{
		private String title;
		private String url_icon;
		private Double priceTop;
		private Double price;
		private int rating;
		private String description;
		private String url;
		
		public Builder(){}
		
		public Builder title(String title){
			this.title = title;
			return this;
		}
		
		public Builder url_icon(String url_icon){
			this.url_icon = url_icon;
			return this;
			
		}
		public Builder priceTop(Double priceTop){
			this.priceTop = priceTop;
			return this;
			
		}
		public Builder price(Double price){
			this.price = price;
			return this;
			
		}
		public Builder rating(int rating){
			this.rating = rating;
			return this;
			
		}
		public Builder description(String description){
			this.description = description;
			return this;
			
		}
		public Builder url(String url){
			this.url = url;
			return this;
			
		}
		
		
		
		public WineDto build(){
			return new WineDto(this);
		}
	}
}
