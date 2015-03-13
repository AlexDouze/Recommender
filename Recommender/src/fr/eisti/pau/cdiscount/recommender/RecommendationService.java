package fr.eisti.pau.cdiscount.recommender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.bson.types.ObjectId;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import fr.eisti.pau.cdiscount.recommender.core.BestWineModel;
import fr.eisti.pau.cdiscount.recommender.core.Preference;
import fr.eisti.pau.cdiscount.recommender.core.Recommendation;
import fr.eisti.pau.cdiscount.recommender.dto.WineDto;

@WebServlet("/recommandation")
public class RecommendationService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RecommendationService() {
		super();
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		BestWineModel wineModel = (BestWineModel) ctx.getBean("model");
		wineModel.resfresh();
		String line;
		StringBuffer jb=new StringBuffer();
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null)
			jb.append(line);
		System.out.println(jb.toString());
		Gson gson = new Gson();
		Recommendation reco = gson.fromJson(jb.toString(), Recommendation.class);
		
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		
		//writer.print(gson.toJson(wineModel.recommend(reco.getUserID(), reco.getNum())));
		MongoClient client = new MongoClient("localhost", 27017);
		DB recoDB = client.getDB("recommender");
		
		List<RecommendedItem> items = wineModel.recommend(reco.getUserID(), reco.getNum());
		DBCollection itemsColl = recoDB.getCollection("items");
		String res ="";
		long itemId;
		List<WineDto> resWine = new LinkedList<WineDto>();
		for (RecommendedItem recommendedItem : items) {
			System.out.println("toto");
			itemId = recommendedItem.getItemID();
			String mongoId = wineModel.getModel().fromLongToId(itemId);
			res=gson.toJson(itemsColl.findOne(new BasicDBObject("_id", new ObjectId(mongoId))));
			resWine.add(gson.fromJson(res, WineDto.class));
		}
		System.out.println(res);
		writer.print(gson.toJson(resWine));
		writer.close();
		client.close();
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		BestWineModel wineModel = (BestWineModel) ctx.getBean("model");
		String line; 
		StringBuffer jb=new StringBuffer();
		BufferedReader reader = req.getReader();
		while ((line = reader.readLine()) != null)
			jb.append(line); 

		Gson gson = new Gson();
		Preference pref = gson.fromJson(jb.toString(), Preference.class);
		wineModel.addPreference(pref.getUserId(), pref.getItemId(), pref.getRate());	
	}

}
