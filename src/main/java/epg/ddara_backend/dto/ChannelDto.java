package epg.ddara_backend.dto;

import lombok.Data;

@Data
public class ChannelDto {

    String startTime;
    String endTime;
    String title;
    String homepageUrl;
    String image;
    String staff;
    String actor;
    String detail;
}
