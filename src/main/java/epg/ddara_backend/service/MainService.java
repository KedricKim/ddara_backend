package epg.ddara_backend.service;

import epg.ddara_backend.dto.ChannelDto;
import epg.ddara_backend.dto.ChannelListDto;
import epg.ddara_backend.dto.ResponseDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public ResponseDto getApi(){

        ResponseDto responseDto = new ResponseDto();
        String targetUrl = "https://static.api.kbs.co.kr/mediafactory/v1/schedule/weekly?&rtype=jsonp&local_station_code=00&channel_code=21,22,23,24,25,26,I92&program_planned_date_from=20220524&program_planned_date_to=20220524&callback=ddara";
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

            responseDto.setToday("20220524");

            ChannelListDto channelListDto = new ChannelListDto();
            for(int i=0; i<array.length(); i++) {
                JSONObject dataObj = array.getJSONObject(i);
                JSONArray schedulesArray = new JSONArray(dataObj.get("schedules").toString());

                List<ChannelDto> list = new ArrayList<>();

                for (int j=0; j<schedulesArray.length(); j++){
                    ChannelDto channelDto = new ChannelDto();

                    JSONObject schedulesObj = schedulesArray.getJSONObject(j);
                    channelDto.setStartTime(String.valueOf(schedulesObj.get("program_planned_start_time")));
                    channelDto.setEndTime(String.valueOf(schedulesObj.get("program_planned_end_time")));
                    channelDto.setTitle(String.valueOf(schedulesObj.get("programming_table_title")));
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
            responseDto.setSchedule(channelListDto);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseDto;
    }
}
