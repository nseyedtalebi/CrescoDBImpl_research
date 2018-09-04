public class CrescoException extends Exception {

    public CrescoException(String message){
        super(message);
    }


    public class CrescoDBException extends CrescoException {

        public CrescoDBException(String message){
            super(message);
        }


        public class PersistenceProviderClassNotFound extends CrescoDBException {

            public PersistenceProviderClassNotFound(String message) {
                super(message);
            }


        }
    }
}
