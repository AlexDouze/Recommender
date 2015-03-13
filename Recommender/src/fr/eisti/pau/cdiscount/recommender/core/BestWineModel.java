package fr.eisti.pau.cdiscount.recommender.core;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import fr.eisti.pau.cdiscount.recommender.dto.WineDto;

public class BestWineModel{

	private UserSimilarity similarity;
	private MongoDBDataModel model;
	private UserNeighborhood neighborhood;
	private UserBasedRecommender recommender;
	private MemoryIDMigrator idMigrator;

	public BestWineModel() {
		try { 
			idMigrator = new MemoryIDMigrator(); 
			model = new MongoDBDataModel("localhost", 27017, "recommender", "ratings", false, false, null);
			System.out.println(model.getNumItems());
			similarity = new PearsonCorrelationSimilarity(model);
			neighborhood = new NearestNUserNeighborhood(1, similarity, model);
			recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		} catch (UnknownHostException | TasteException e) {  
			e.printStackTrace();
		}
	}

	public void resfresh(){
		try { 
			model = new MongoDBDataModel("localhost", 27017, "recommender", "ratings", true, true, new SimpleDateFormat());
			System.out.println(model.getNumItems());
			similarity = new PearsonCorrelationSimilarity(model);
			neighborhood = new NearestNUserNeighborhood(3, similarity, model);
			recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		} catch (UnknownHostException | TasteException e) {
			e.printStackTrace();
		}
	}



	public List<RecommendedItem> recommend(String userId, int num){
		try{
			MongoDBDataModel model = new MongoDBDataModel("localhost", 27017, "recommender", "ratings", false, false, null);

			System.out.println(model.getNumUsers() +" users");
			System.out.println(model.getNumItems() +" items");

			SVDRecommender svd = 
					new SVDRecommender(model, new ALSWRFactorizer(model, 3, 0.05f, 50));


			MongoClient client = new MongoClient("localhost", 27017);
			DB reco = client.getDB("recommender");
			DBCollection users = reco.getCollection("users");
			DBObject u = users.findOne(new BasicDBObject("userId", userId));
			Long userLongId = Long.parseLong(model.fromIdToLong(u.get("_id").toString(), true));

			return svd.recommend(userLongId, num);
		} catch (UnknownHostException | TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addPreference(String userId, WineDto itemId, float rate){

		try {
			MongoClient client = new MongoClient("localhost", 27017);
			DB reco = client.getDB("recommender");
			String userLongId ="";
			String itemLongId = "";

			DBCollection users = reco.getCollection("users");
			if(users.count(new BasicDBObject("userId", userId))==0){
				users.insert(new BasicDBObject("userId", userId));
				DBObject u = users.findOne(new BasicDBObject("userId", userId));
				userLongId = u.get("_id").toString();
				System.out.println("new user: "+u.get("_id"));

			}else{
				DBObject u = users.findOne(new BasicDBObject("userId", userId));
				userLongId = u.get("_id").toString();
				System.out.println("old user: "+u.get("_id")); 
			}



			DBCollection items = reco.getCollection("items");
			if(items.count(new BasicDBObject("title", itemId.getTitle()))==0){
				items.insert(new 
						BasicDBObject("title", itemId.getTitle())
							.append("url_icon", itemId.getUrl_icon())
							.append("priceTop", itemId.getPriceTop())
							.append("price", itemId.getPrice())
							.append("rating", itemId.getRating())
							.append("descrition", itemId.getDescription())
							.append("url", itemId.getUrl()));
				DBObject i = items.findOne(new BasicDBObject("title", itemId.getTitle()));
				itemLongId = i.get("_id").toString();
				System.out.println("new item:"+i.get("_id"));
			}else{
				DBObject i = items.findOne(new BasicDBObject("title", itemId.getTitle()));
				itemLongId = i.get("_id").toString();
				System.out.println("old item:"+i.get("_id"));
			}

			DBCollection ratings = reco.getCollection("ratings");
			BasicDBObject rating = new BasicDBObject();
			rating.append("user_id", new ObjectId(userLongId));
			rating.append("item_id", new ObjectId(itemLongId));
			rating.append("preference", rate);
			//rating.append("created_at", 1339436655);
			ratings.insert(rating);

			System.out.println();

			client.close(); 

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}


	}
	
	public MongoDBDataModel getModel(){
		return this.model;
	}


}
