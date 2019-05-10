package Service;

import Controller.ExceptionItem;
import Dao.ItemDao;
import Domain.Item;
import Domain.externalDomain.Category;
import Domain.externalDomain.Site;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class ItemService {

    private static final String urlCategory = "https://api.mercadolibre.com/categories/";
    private static final String urlSite = "https://api.mercadolibre.com/sites/";

    public Item getItem(String id) throws ExceptionItem,IOException {

        if (id ==null || id.equals("")){
            throw new ExceptionItem("Id invalido");
        }
        return ItemDao.getItemById(id);
    }

    public Collection<Item> getItems() {
        try{
        return ItemDao.getItems();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public Item setItem(Item item) throws ExceptionItem, IOException {
        if (item==null){
            throw new ExceptionItem("Item no puede ser nulo");
        }
        if (item.getId()==null || item.getId().equals("")){
            throw new ExceptionItem("Item a modificar no puede tener id nulo o vacio");
        }
        if (item.getSiteId()!=null){
            throw new ExceptionItem("No se puede modificar el Site");
        }
        if (item.getCategoryId()!=null){
            throw new ExceptionItem("No se puede modificar la categoria");
        }
        return ItemDao.updateItemById(item.getId(),item);
    }

    public Item addItem(Item item) throws ExceptionItem {
        if (item==null){
            throw new ExceptionItem("Item no puede ser nulo");
        }
        if (item.getId()==null || item.getId().equals("")){
            throw new ExceptionItem("Item no puede ser vacio");
        }
        if (item.getSiteId()==null || item.getSiteId().equals("")){
            throw new ExceptionItem("Site Id no puede ser vacio");
        }
        if (item.getCategoryId()==null || item.getCategoryId().equals("")){
            throw new ExceptionItem("Site Id no puede ser vacio");
        }
        try {
            String data = readUrl(urlCategory+item.getCategoryId());
            Category category = new Gson().fromJson(data, Category.class);
            if (category.getId()==null){
                throw new ExceptionItem("Category Id invalido");
            }
            data = readUrl(urlSite+item.getSiteId());
            Site site = new Gson().fromJson(data, Site.class);
            if (site.getId()==null){
                throw new ExceptionItem("Site Id invalido");
            }
            Item itemResult = ItemDao.insertItem(item);
            if (itemResult==null){
                throw new ExceptionItem("Error guardando Item");
            }
            return item;
        }catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new ExceptionItem ("No existe Id Site o Categoria");
            }

            throw new ExceptionItem("Error conectandose API Mercado Libre");
        }
    }

    public boolean deleteItem(String id) throws ExceptionItem,IOException {

        if (id ==null || id.equals("")){
            throw new ExceptionItem("Id invalido");
        }
        return ItemDao.deleteItemById(id);
    }


    /**
     * @param urlStr String de la Url de la cual se quiere hace la solicitud
     * @return Retorna un String con con la respuesta de la URL
     * @throws IOException si hubo un error cuando se solicita el recurso a la URL
     */
    private static String readUrl(String urlStr) throws IOException {

        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();
        System.out.flush();
        int read;
        char[] chars = new char[1024];
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8));
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}