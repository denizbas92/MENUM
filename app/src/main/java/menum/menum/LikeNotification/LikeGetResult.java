package menum.menum.LikeNotification;

/**
 * Created by deniz on 7.2.2018.
 */

public class LikeGetResult {
    private static String oldCounter ;
    private static String newCounter ;

    public LikeGetResult(){

    }

    public LikeGetResult(String oldCounter, String newCounter) {
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
