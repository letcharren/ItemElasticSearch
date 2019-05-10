package Controller;

/**
 * Excepcion que maneja las excepciones de AgencyController para poder setear un mensaje mas amigable
 */
public class ExceptionItem extends Exception{

    public ExceptionItem(){
        super();
    }

    public ExceptionItem(String message){
        super(message);
    }
}
