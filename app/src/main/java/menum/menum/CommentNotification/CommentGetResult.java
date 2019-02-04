package menum.menum.CommentNotification;

/**
 * Created by deniz on 7.2.2018.
 */

public class CommentGetResult {
    private static String oldCounter ;
    private static String newCounter ;

    public CommentGetResult(){

    }

    public CommentGetResult(String oldCounter, String newCounter) {
        this.oldCounter=oldCounter;
        this.newCounter=newCounter;
    }

    public int isEqual(){
        if(oldCounter.equalsIgnoreCase(newCounter)){
            return 1;
        }else{
            return 0;
        }
    }
}
