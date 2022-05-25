package epg.ddara_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import epg.ddara_backend.dto.ChannelDto;
import epg.ddara_backend.dto.ChannelListDto;
import epg.ddara_backend.dto.ResponseDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainService {

    private static String KBS1 = "21";
    private static String KBS2 = "22";
    private static String KBS3 = "23";
    private static String KBSCLASSIC = "24";
    private static String KBSCOOL = "25";
    private static String KBSHAN = "26";
    private static String KBSWORLD = "I92";

    private static String MBCFM = "FM";
    private static String MBCFM4U = "FM4U";

    /**
     * today 인자가 없는 경우, 배치
     * @return
     */
    public ResponseDto getSchedules(){
        LocalDate date = LocalDate.now();
        String today = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        ResponseDto responseDto = this.getSchedules(today); // 전체 스케쥴
        this.putSchedules(responseDto); // 배치인 경우

        return responseDto;
    }

    /**
     * 전체 schedules
     * @param today 요청 날짜 yyyyMMdd
     * @return
     */
    public ResponseDto getSchedules(String today){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setToday(today);

        ChannelListDto channelListDto = new ChannelListDto();
        channelListDto = this.getKbs(channelListDto, today); //kbs
        channelListDto = this.getMbc(channelListDto, today); // mbc

        responseDto.setStations(channelListDto);

        return responseDto;
    }

    /**
     * 타겟 날짜로 배치 및 응답
     * @param targetDay
     * @return
     */
    public ResponseDto putSchedules(String targetDay){

        ResponseDto responseDto = this.getSchedules(targetDay); // 전체 스케쥴
        this.putSchedules(responseDto); // 타겟 날짜로 배치

        return responseDto;
    }

    /**
     * firebase schedules put
     * @param responseDto
     */
    private void putSchedules(ResponseDto responseDto){

        try{
            URL url = new URL("https://ddara-70c65-default-rtdb.firebaseio.com/schedule.json");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("PUT"); // http 메서드
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            // jackson 을 이용한 직렬화
            ObjectMapper mapper = new ObjectMapper();
            String jsonResult = mapper.writeValueAsString(responseDto);

            bw.write(jsonResult);
            bw.flush();
            bw.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * kbs schedules
     * @param today
     * @return
     */
    private ChannelListDto getKbs(ChannelListDto channelListDto, String today){

        String defaultUrl = "https://static.api.kbs.co.kr/mediafactory/v1/schedule/weekly?&rtype=jsonp&local_station_code=00&channel_code=21,22,23,24,25,26,I92";
        defaultUrl += "&program_planned_date_from=" + today + "&program_planned_date_to=" + today;
        String targetUrl = defaultUrl + "&callback=ddara";

        try {

            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("GET"); // http 메서드
            conn.setRequestProperty("Content-Type", "application/json"); // header Content-Type 정보
            conn.setRequestProperty("auth", "myAuth"); // header의 auth 정보
            conn.setDoOutput(true); // 서버로부터 받는 값이 있다면 true

            // 서버로부터 데이터 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                sb.append(line);
            }

            String bodyData = sb.toString();

            int idx = bodyData.lastIndexOf("ddara");
            bodyData = bodyData.substring(idx+6, bodyData.length()-2);

            JSONArray array = new JSONArray(bodyData); // json으로 변경 (역직렬화)

            for(int i=0; i<array.length(); i++) {
                JSONObject dataObj = array.getJSONObject(i);
                JSONArray schedulesArray = new JSONArray(dataObj.get("schedules").toString());

                List<ChannelDto> list = new ArrayList<>();

                for (int j=0; j<schedulesArray.length(); j++){
                    ChannelDto channelDto = new ChannelDto();

                    JSONObject schedulesObj = schedulesArray.getJSONObject(j);
                    if(schedulesObj.has("program_planned_start_time")) channelDto.setStartTime(String.valueOf(schedulesObj.get("program_planned_start_time")).substring(0,4) );
                    if(schedulesObj.has("program_planned_end_time")) channelDto.setEndTime(String.valueOf(schedulesObj.get("program_planned_end_time")).substring(0,4) );
                    if(schedulesObj.has("programming_table_title")) channelDto.setTitle(String.valueOf(schedulesObj.get("programming_table_title")));
                    if(schedulesObj.has("homepage_url")) channelDto.setHomepageUrl(String.valueOf(schedulesObj.get("homepage_url")));
                    if(schedulesObj.has("image_w")) channelDto.setImage(String.valueOf(schedulesObj.get("image_w")));
                    if(schedulesObj.has("program_staff")) channelDto.setStaff(String.valueOf(schedulesObj.get("program_staff")));
                    if(schedulesObj.has("program_actor")) channelDto.setActor(String.valueOf(schedulesObj.get("program_actor")));
                    if(schedulesObj.has("program_intention")) channelDto.setDetail(String.valueOf(schedulesObj.get("program_intention")));
                    list.add(channelDto);
                }

                if(KBS1.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbs1(list);
                }else if(KBS2.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbs2(list);
                }else if(KBS3.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbs3(list);
                }else if(KBSCLASSIC.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbsClassic(list);
                }else if(KBSCOOL.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbsCool(list);
                }else if(KBSHAN.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbsHan(list);
                }else if(KBSWORLD.equals(dataObj.getString("channel_code")) ){
                    channelListDto.setKbsWorld(list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return channelListDto;
    }

    private ChannelListDto getMbc(ChannelListDto channelListDto, String today){

        String defaultUrl = "https://control.imbc.com/Schedule/Radio?callback=ddara";
        defaultUrl += "&sDate=" + today;
        String[] targetStation = new String[2];

        targetStation[0] =  MBCFM;
        targetStation[1] =  MBCFM4U;

        try {

            for(int k=0; k<targetStation.length; k++){

                String targetUrl = defaultUrl + "&sType=" + targetStation[k];
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestMethod("GET"); // http 메서드
                conn.setRequestProperty("Content-Type", "application/json"); // header Content-Type 정보
                conn.setRequestProperty("auth", "myAuth"); // header의 auth 정보
                conn.setDoOutput(true); // 서버로부터 받는 값이 있다면 true

                // 서버로부터 데이터 읽어오기
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                    sb.append(line);
                }

                String bodyData = sb.toString();

                int idx = bodyData.lastIndexOf("ddara");
                bodyData = bodyData.substring(idx+6, bodyData.length()-1);

                JSONArray array = new JSONArray(bodyData); // json으로 변경 (역직렬화)

                List<ChannelDto> list = new ArrayList<>();
                for(int i=0; i<array.length(); i++) {
                    JSONObject dataObj = array.getJSONObject(i);

                    ChannelDto channelDto = new ChannelDto();
                    if(dataObj.has("StartTime")) channelDto.setStartTime(String.valueOf(dataObj.get("StartTime")));
                    if(dataObj.has("EndTime")) channelDto.setEndTime(String.valueOf(dataObj.get("EndTime")));
                    if(dataObj.has("SubTitle")) channelDto.setTitle(String.valueOf(dataObj.get("SubTitle")));
                    if(dataObj.has("HomepageURL")) channelDto.setHomepageUrl(String.valueOf(dataObj.get("HomepageURL")));
                    if(dataObj.has("Photo")) channelDto.setImage(String.valueOf(dataObj.get("Photo")));
                    channelDto.setStaff("null");
                    if(dataObj.has("Players")) channelDto.setActor(String.valueOf(dataObj.get("Players")));
                    channelDto.setDetail("null");

                    list.add(channelDto);
                }

                if(MBCFM.equals(targetStation[k])){
                    channelListDto.setMbcFm(list);
                }else if(MBCFM4U.equals(targetStation[k])){
                    channelListDto.setMbcFm4u(list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return channelListDto;
    }
}
