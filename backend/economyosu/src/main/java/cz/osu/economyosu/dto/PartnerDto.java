package cz.osu.economyosu.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerDto {

    private Long id;
    private String companyName;
    private String ico;
    private String dic;
    private String address;
    private String email;
    private String phone;
}
