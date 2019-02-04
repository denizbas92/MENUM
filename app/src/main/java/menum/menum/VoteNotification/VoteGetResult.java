package menum.menum.VoteNotification;

/**
 * Created by deniz on 7.2.2018.
 */

public class VoteGetResult {
    private static String oldCounter ;
    private static String newCounter ;

    public VoteGetResult(){

    }

    public VoteGetResult(String oldCounter, String newCounter) {
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
