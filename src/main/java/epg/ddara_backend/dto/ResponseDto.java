package epg.ddara_backend.dto;

import lombok.Data;

@Data
public class ResponseDto {

    String today;
    ChannelListDto stations;

}
