package cz.osu.economyosu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String ico;

    private String dic;

    private String address;

    private String email;

    private String phone;
}