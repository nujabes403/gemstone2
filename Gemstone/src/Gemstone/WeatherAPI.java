/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.StringTokenizer;
import sage.google.weather.GoogleWeather;
import tv.sage.weather.WeatherDotCom;
/**
 *
 * @author jusjoken
 */
public class WeatherAPI {
    private static enum APITypes{GOOGLE,WEATHERCOM};
    private APITypes APIType = APITypes.GOOGLE;
    private GoogleWeather gWeather = null;
    private WeatherDotCom wWeather = null;
    
    public WeatherAPI(String APIType) {
        //used for temporarily getting a WeatherAPI object without saving the type
        this.APIType = getAPIType(APIType);
    }
    public WeatherAPI() {
        this.APIType = getAPIType();
        //InitAPI();
    }

    private APITypes getAPIType() {
        String tAPIType = util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            return APITypes.GOOGLE;
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            return APITypes.WEATHERCOM;
        }else{
            return APITypes.GOOGLE;
        }
    }
    private APITypes getAPIType(String tAPIType) {
        APITypes tempAPIType = APITypes.GOOGLE;
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            tempAPIType = APITypes.GOOGLE;
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            tempAPIType = APITypes.WEATHERCOM;
        }
        return tempAPIType;
    }

    private void setAPIType(APITypes APIType) {
        APITypes currAPIType = getAPIType();
        if (!APIType.equals(currAPIType)){
            //change the type
            util.SetOption(Const.WeatherProp, "APIType", APIType.toString());
        }
    }
    
    public void Init(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (wWeather==null){
                wWeather = WeatherDotCom.getInstance();
                gWeather = null;
            }
        }else{
            //default to GOOGLE
            if (gWeather==null){
                gWeather = GoogleWeather.getInstance();
                wWeather = null;
            }
        }
    }
    public void Update(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.updateNow();
        }else{
            //Google
            //get the language code
            String LangCode = util.GetProperty("ui/translation_language_code", "en");
            gWeather.updateAllNow(LangCode);
        }
    }
    public Boolean IsConfigured(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (wWeather==null){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }else{
            if (gWeather==null){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }
    }
    public Boolean IsWeatherDotCom(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean IsGoogleWeather(){
        if (APIType.equals(APITypes.GOOGLE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public String GetType() {
        return util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
    }
    //get a name for menu items/settings
    public String GetTypeName() {
        String tAPIType = util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            return "Google Weather";
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            return "Weather.com";
        }else{
            return "Google Weather";
        }
    }
    //change to the next valid WeatherAPI type
    public void NextType() {
        APITypes tempAPIType = getAPIType();
        if (tempAPIType.equals(APITypes.GOOGLE)){
            setAPIType(APITypes.WEATHERCOM);
        }else{
            setAPIType(APITypes.GOOGLE);
        }
    }

    public String GetTemp(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            //need to strip off the degree and unit from the temp
            String tTemp = wWeather.getCurrentCondition("curr_temp");
            return tTemp.substring(0, tTemp.length()-2);
        }else{
            return gWeather.getGWCurrentCondition("Temp");
        }
    }
    //return the temp with the degrees and units as part of the display
    public String GetTempFull(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            //this already has the units so just return the results
            return wWeather.getCurrentCondition("curr_temp");
        }else{
            return gWeather.getGWCurrentCondition("Temp") + GetUnitsDisplay();
        }
    }
    public void SetUnits(String Value){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.setUnits(Value);
        }else{
            gWeather.setUnits(Value);
        }
    }
    public String GetUnits(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getUnits();
        }else{
            return gWeather.getUnits();
        }
    }
    public String GetUnitsDisplay(){
        if (GetUnits().equals("s")){
            return "\u00B0" + "F";
        }else{
            return "\u00B0" + "C";
        }
    }
    public String GetIcon(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return WIcons.GetWeatherIconByNumber(wWeather.getCurrentCondition("curr_icon"));
        }else{
            return WIcons.GetWeatherIconURL(gWeather.getGWCurrentCondition("iconURL"));
        }
    }
    public String GetHumidity(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_humidity");
        }else{
            return gWeather.getGWCurrentCondition("HumidText").replaceAll("Humidity:", "").trim();
        }
    }
    public String GetCondition(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_conditions");
        }else{
            return gWeather.getGWCurrentCondition("CondText");
        }
    }
    public String GetWind(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tWind = wWeather.getCurrentCondition("curr_wind");
            if (tWind.startsWith("CALM")){
                return "CALM";
            }
            String WindDir = tWind.substring(0, tWind.indexOf(" "));
            String WindSpeed = tWind.substring(tWind.indexOf(" ")+1);
            return WindDir + "/" + WindSpeed;
        }else{
            return gWeather.getGWCurrentCondition("WindText").replaceAll("Wind:", "").trim().replaceFirst(" at ", "/");
        }
    }
    
}