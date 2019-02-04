package menum.menum.AdviceNotification;

/**
 * Created by deniz on 7.2.2018.
 */

public class AdviceGetResult {

    private static String oldCounter ;
    private static String newCounter ;

    public AdviceGetResult(){

    }

    public AdviceGetResult(String oldCounter, String newCounter) {
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
