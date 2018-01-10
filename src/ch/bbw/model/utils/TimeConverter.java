package ch.bbw.model.utils;

public class TimeConverter {

    public long getSecDif(long numb1,long numb2){
        return (numb1-numb2)/1000;
    }
    public String longToString(long time){
        String stringTime =  ""+time;
        return stringTime;
    }
}
