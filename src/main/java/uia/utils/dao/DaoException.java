package uia.utils.dao;

/**
*
* @author Kyle K. Lin
*
*/
public class DaoException extends Exception {

    private static final long serialVersionUID = -5509816590777819211L;

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable th) {
        super(th);
    }
}
