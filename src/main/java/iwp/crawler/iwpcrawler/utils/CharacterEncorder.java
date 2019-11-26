package iwp.crawler.iwpcrawler.utils;

public class CharacterEncorder {

    private CharacterEncorder(){}


    public static String encodeUTF8(String str){
        String res = str;
        res = res.replace("&","+%26+");
        res = res.replace("-","+%2D+");
        res = res.replace(".","%2E+");
        res = res.replace(",","%2C+");
        res = res.replace("'","%27+");
        res = res.replace(" ","%20");
        return res;
    }
}
