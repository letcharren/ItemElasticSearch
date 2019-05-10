package Controller;

import Domain.Item;

import java.io.IOException;
import java.util.Collection;

import Service.ItemService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;

public class ItemController {
    private ItemService service = new ItemService();

    public Collection<Item> getItems(Request req, Response res) {

        Collection<Item> items = service.getItems();
        if (items!=null) {
            res.status(200);
        }else{
            res.status(500);
        }
        return items;
    }

    public Item getItem(Request req, Response res) throws ExceptionItem, IOException {
        try {
            Item item = service.getItem((req.params(":id")));
            if (item!=null) {
                res.status(200);
            }else{
                res.status(404);
            }
            return item;
        }catch (ExceptionItem e){
            res.status(400);
            throw  new ExceptionItem("id invalido");
        }
    }

    public Item createItem(Request req, Response res) throws ExceptionItem {
        try {
            Item item = new Gson().fromJson(req.body(), Item.class);
            res.status(201);
            return service.addItem(item);
        } catch (JsonSyntaxException e) {
            res.status(400);
            e.printStackTrace();
            throw new ExceptionItem("Error,Formato Item invalido");
        } catch (ExceptionItem e) {
            if (e.getMessage().equals("Error conectandose API Mercado Libre")){
                res.status(500);
            }else{
                res.status(400);
            }
            e.printStackTrace();
            throw e;
        }
    }

    public Item setItem(Request req, Response res) throws ExceptionItem,IOException {
        try {
            Item item = new Gson().fromJson(req.body(), Item.class);
            Item itemResult = service.setItem(item);
            if (itemResult!=null) {
                res.status(200);
            }else{
                res.status(500);
            }
            return itemResult;
        }catch(JsonSyntaxException e) {
            res.status(404);
            e.printStackTrace();
            throw new ExceptionItem("Error,Formato Item invalido");
        }catch (ExceptionItem exceptionItem) {
            exceptionItem.printStackTrace();
            res.status(404);
            throw exceptionItem;
        }
    }

    public void deleteItem(Request req, Response res) throws ExceptionItem,IOException {
        try {
            boolean itemResult = service.deleteItem((req.params(":id")));
            if (itemResult) {
                res.status(200);
            }else{
                res.status(400);
            }
        }catch (ExceptionItem e){
            res.status(404);
            throw new ExceptionItem("Id invalido");
        }
    }
}