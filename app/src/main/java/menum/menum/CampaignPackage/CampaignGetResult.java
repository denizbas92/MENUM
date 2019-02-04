package menum.menum.CampaignPackage;

/**
 * Created by deniz on 8.2.2018.
 */

public class CampaignGetResult {
    private static String oldCounter ;
    private static String newCounter ;

    public CampaignGetResult(){

    }

    public CampaignGetResult(String oldCounter, String newCounter) {
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
