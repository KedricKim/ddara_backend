package epg.ddara_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChannelListDto{

    List<ChannelDto> kbs1;
    List<ChannelDto> kbs2;
    List<ChannelDto> kbs3;
    List<ChannelDto> kbsClassic;
    List<ChannelDto> kbsCool;
    List<ChannelDto> kbsHan;
    List<ChannelDto> kbsWorld;

    List<ChannelDto> mbcFm;
    List<ChannelDto> mbcFm4u;

    List<ChannelDto> sbsLove;
    List<ChannelDto> sbsPower;

    List<ChannelDto> ebsFm;
    List<ChannelDto> ebs;
}
