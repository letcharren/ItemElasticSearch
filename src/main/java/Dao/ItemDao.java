package Dao;

import Domain.Item;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;
import java.util.*;

public class ItemDao {

    //The config parameters for the connection
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";
    private static final String INDEX = "itemdata";
    private static final String TYPE = "item";
    private static RestHighLevelClient restHighLevelClient;

    /**
     * Implemented Singleton pattern here
     * so that there is just one connection at a time.
     * @return RestHighLevelClient
     */
    private static synchronized RestHighLevelClient makeConnection() {
        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }
        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    public static Item insertItem(Item item)  throws IOException {
        makeConnection();
        Map<String, Object> dataMap = toDataMap(item);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, item.getId())
                .source(dataMap);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
            return null;
        } catch (IOException ex){
            ex.getLocalizedMessage();
            return null;
        }finally {
           closeConnection();
        }
        return item;
    }

    public static Collection<Item> getItems() throws IOException {
        makeConnection();
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        try {
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        List<Item> list = new ArrayList<Item>();
        for (SearchHit hit : hits) {
            // hit.sourceAsMap()
            list.add(new Gson().fromJson(hit.getSourceAsString(),Item.class));
        }
        return list;

        }catch(IOException e){
            e.printStackTrace();
            return null;
        }finally {
            closeConnection();
        }
    }

    public static Item getItemById(String id) throws IOException{
        makeConnection();
        GetRequest getItemRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse;
        try {
            getResponse = restHighLevelClient.get(getItemRequest);
        } catch (IOException e){
            e.getLocalizedMessage();
            return null;
        }finally {
            closeConnection();
        }
        return !getResponse.isSourceEmpty() ?
                new Gson().fromJson(getResponse.getSourceAsString(), Item.class) : null;
    }

    public static Item updateItemById(String id, Item item)throws IOException{
        makeConnection();
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        try {
            item.setId(null);
            String personJson = new Gson().toJson(item,Item.class);
            updateRequest.doc(personJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            return !updateResponse.getGetResult().isSourceEmpty() ?
                    new Gson().fromJson(updateResponse.getGetResult().sourceAsString(), Item.class) : null;
        } catch (IOException e){
            e.getLocalizedMessage();
            return null;

        }finally {
            closeConnection();
        }
    }

    public static boolean deleteItemById(String id)throws IOException{
        makeConnection();

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
            return deleteResponse.status().getStatus() == 200;
        } catch (IOException e){
            e.getLocalizedMessage();
            return false;
        }finally {
            closeConnection();
        }
    }

    private static Map<String, Object> toDataMap(Item item){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("id", item.getId());
        dataMap.put("siteId", item.getId());
        dataMap.put("title", item.getTitle());
        dataMap.put("subtitle", item.getSubtitle());
        dataMap.put("sellerId", item.getSellerId());
        dataMap.put("categoryId", item.getCategoryId());
        dataMap.put("price", item.getPrice());
        dataMap.put("currencyId", item.getCurrencyId());
        dataMap.put("availableQuantity", item.getAvailableQuantity());
        dataMap.put("condition", item.getCondition());
        dataMap.put("pictures", item.getPictures());
        dataMap.put("acceptsMercadopago", item.getAcceptsMercadopago());
        dataMap.put("status", item.getStatus());
        dataMap.put("dateCreated", item.getDateCreated());
        dataMap.put("lastUpdated", item.getLastUpdated());
        return dataMap;
    }

}
