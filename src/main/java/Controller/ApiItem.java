package Controller;
import static Utils.JsonUtils.json;
import static spark.Spark.*;

import Domain.*;
import Utils.StandardResponse;
import Utils.StatusResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collection;

public class ApiItem{

    public static void main(String[] args) throws IOException {
        ItemController itemController = new ItemController();
        port(8080);
        path("/item", () -> {

            get("", (req, res) -> {

                Collection<Item> items = itemController.getItems(req, res);
                StandardResponse body = new StandardResponse();
                if (res.status() == 200) {
                    body.setStatus(StatusResponse.SUCCESS);
                    body.setData(new Gson().toJsonTree(items));
                } else {
                    body.setStatus(StatusResponse.ERROR);
                    body.setMessage("Error buscando Items");
                }
                return body;
            }, json());
            get("/:id", (req, res) -> {

                Item item = itemController.getItem(req, res);
                res.type("application/json");
                StandardResponse body = new StandardResponse(StatusResponse.SUCCESS);
                if (res.status() == 200) {
                    body.setData(new Gson().toJsonTree(item));
                } else {
                    body.setMessage("Usuario no encontrado");
                }
                return body;
            }, json());

            post("", (req, res) -> {
                StandardResponse body = new StandardResponse(StatusResponse.SUCCESS);
                try {
                    Item item = itemController.createItem(req, res);
                    res.type("application/json");
                    new StandardResponse(StatusResponse.SUCCESS);
                    body.setData(new Gson().toJsonTree(item));
                } catch (ExceptionItem e) {
                    body.setStatus(StatusResponse.ERROR);
                    body.setMessage(e.getMessage());
                }
                return body;
            }, json());

            put("", (req, res) -> {
                StandardResponse body = new StandardResponse();
                try {
                    Item item = itemController.setItem(req, res);
                    res.type("application/json");
                    if (res.status() == 200) {
                        body.setData(new Gson().toJsonTree(item));
                    } else {
                        body.setMessage("Usuario no actualizado");
                    }
                } catch (ExceptionItem e) {
                    body.setStatus(StatusResponse.ERROR);
                    body.setMessage(e.getMessage());
                }
                return body;
            }, json());
            delete("/:id", (req, res) -> {

                StandardResponse body = new StandardResponse(StatusResponse.SUCCESS);
                try {
                    itemController.deleteItem(req, res);
                    res.type("application/json");
                    body.setStatus(StatusResponse.SUCCESS);
                    if (res.status() == 200) {
                        body.setMessage("Usuario eliminado correctamente");
                    } else {
                        body.setMessage("Id usuario no encontrado, no se pudo eliminar el mismo");
                    }
                } catch (ExceptionItem e) {
                    body.setStatus(StatusResponse.ERROR);
                    body.setMessage(e.getMessage());
                }
                return body;
            }, json());
        });

    }
}